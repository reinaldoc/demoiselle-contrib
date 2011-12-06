package br.gov.frameworkdemoiselle.ldap.core;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.ietf.ldap.LDAPAttribute;
import org.ietf.ldap.LDAPConnection;
import org.ietf.ldap.LDAPEntry;
import org.ietf.ldap.LDAPException;
import org.ietf.ldap.LDAPReferralException;
import org.ietf.ldap.LDAPSearchConstraints;
import org.ietf.ldap.LDAPSearchResults;
import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.ldap.internal.ConnectionManager;

public class EntryQuery {

	@Inject
	private Logger logger;
	private ConnectionManager conn;
	private String[] resultAttributes;

	public Map<String, Map<String, String[]>> getResult() {
		return new HashMap<String, Map<String, String[]>>();
	}

	public Map<String, String[]> getSingleResult() {
		return new HashMap<String, String[]>();
	}

	public Map<String, String> getSingleResultSingleAttributes() {
		return new HashMap<String, String>();
	}

	public String getSingleResultSingleAttribute() {
		return new String();
	}

	public String searchOneDN(String searchFilter) {
		List<String> dns = searchDN(searchFilter);
		if (dns.size() == 1) {
			return dns.get(0);
		} else {
			return null;
		}
	}

	public List<String> searchDN(String searchFilter) {
		Map<String, LDAPEntry> searchResult = new HashMap<String, LDAPEntry>();
		List<String> dnList = new ArrayList<String>();

		searchResult = search_priv(searchFilter, new String[] { "objectClass" });

		Iterator<String> itr = searchResult.keySet().iterator();
		while (itr.hasNext() == true) {
			dnList.add(itr.next());
		}
		return dnList;
	}

	public List<String> searchCN(String queryCN) {
		Map<String, LDAPEntry> searchResult = new HashMap<String, LDAPEntry>();
		List<String> returnResult = new ArrayList<String>();

		searchResult = search_priv(searchcnSearchFilter.replaceAll("%s", queryCN), searchcnResultattributes);

		Collection<LDAPEntry> c = searchResult.values();
		Iterator<LDAPEntry> itr = c.iterator();
		while (itr.hasNext() == true) {
			LDAPEntry entry = itr.next();
			for (int i = 0; i != searchcnResultattributes.length; i++) {
				LDAPAttribute attr = entry.getAttribute(searchcnResultattributes[i]);
				String[] attrArray = attr.getStringValueArray();
				for (int y = 0; y != attrArray.length; y++) {
					returnResult.add(attrArray[y]);
				}
			}
		}
		return returnResult;
	}

	private Map<String, LDAPEntry> search_priv(String searchFilter, String[] resultAttributes) {
		Map<String, LDAPEntry> mapResult = new HashMap<String, LDAPEntry>();
		try {
			initialize();
			LDAPSearchConstraints ldapConstraints = new LDAPSearchConstraints();
			ldapConstraints.setMaxResults(searchSizeLimit);
			LDAPSearchResults searchResults = getConnection().search(searchBaseDn, LDAPConnection.SCOPE_SUB, searchFilter, resultAttributes, false, ldapConstraints);
			while (searchResults != null && searchResults.hasMore()) {
				try {
					LDAPEntry entry = searchResults.next();
					mapResult.put(entry.getDN(), entry);
				} catch (LDAPReferralException e) {
					// Ignore referral;
				} catch (LDAPException e) {
					// Catch size limit exceeded;
					if (e.getResultCode() != LDAPException.SIZE_LIMIT_EXCEEDED) {
						throw e;
					}
				}
			}
		} catch (LDAPException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return mapResult;
	}

	public Map<String, Map<String, String[]>> search(String searchFilter, String[] resultAttributes) {

		Map<String, LDAPEntry> searchResult = new HashMap<String, LDAPEntry>();
		Map<String, Map<String, String[]>> returnResult = new HashMap<String, Map<String, String[]>>();

		searchResult = search_priv(searchFilter, resultAttributes);

		Iterator<String> itrDn = searchResult.keySet().iterator();
		while (itrDn.hasNext()) {
			String dn = itrDn.next();
			Map<String, String[]> entryMap = new HashMap<String, String[]>();
			@SuppressWarnings("unchecked")
			Iterator<LDAPAttribute> itrAttr = searchResult.get(dn).getAttributeSet().iterator();
			while (itrAttr.hasNext()) {
				LDAPAttribute attr = itrAttr.next();
				entryMap.put(attr.getName(), attr.getStringValueArray());
			}
			returnResult.put(dn, entryMap);
		}
		return returnResult;
	}

	public Map<String, Map<String, String[]>> search(String searchFilter) {
		return search(searchFilter, null);
	}

	public Map<String, String[]> searchOneEntry(String searchFilter, String[] resultAttributes) {

		Map<String, LDAPEntry> searchResult = new HashMap<String, LDAPEntry>();
		Map<String, String[]> returnResult = new HashMap<String, String[]>();

		searchFilter = getSearchFilter(searchOneEntrySearchFilter, searchFilter);
		searchResult = search_priv(searchFilter, resultAttributes);

		if (searchResult.size() == 1) {
			Iterator<LDAPEntry> itr = searchResult.values().iterator();
			while (itr.hasNext() == true) {
				@SuppressWarnings("unchecked")
				Iterator<LDAPAttribute> itrAttr = itr.next().getAttributeSet().iterator();
				while (itrAttr.hasNext()) {
					LDAPAttribute attr = itrAttr.next();
					returnResult.put(attr.getName(), attr.getStringValueArray());
				}
			}
		}
		return returnResult;
	}

	public Map<String, String[]> searchOneEntry(String searchFilter) {
		return searchOneEntry(searchFilter, null);
	}

	public Map<String, String> searchOneEntrySingleAttributes(String searchFilter, String[] resultAttributes) {
		Map<String, String[]> searchResult = searchOneEntry(searchFilter, resultAttributes);
		Map<String, String> returnResult = new HashMap<String, String>();
		Iterator<String> iter = searchResult.keySet().iterator();
		while (iter.hasNext()) {
			String attr = iter.next();
			String[] valuesList = searchResult.get(attr);
			if (valuesList.length > 0)
				returnResult.put(attr, valuesList[0]);
		}
		return returnResult;
	}

	public String searchOneEntrySingleAttribute(String searchFilter, String resultAttribute) {
		String returnResult = "";
		Map<String, String[]> searchResult = searchOneEntry(searchFilter, new String[] { resultAttribute });

		if (searchResult.containsKey(resultAttribute)) {
			String[] valuesList = searchResult.get(resultAttribute);
			if (valuesList.length > 0)
				returnResult = valuesList[0];
		}
		return returnResult;
	}

	private String getSearchFilter(String searchFilterTemplate, String maybeSearchFilter) {
		String searchFilter = "(invalidFilter=*)";
		if (maybeSearchFilter == null || !maybeSearchFilter.contains("=")) {
			if (searchFilterTemplate == null || !searchFilterTemplate.contains("=")) {
				logger.warn("Search filter template must have RFC 2254 sintax.");
			} else {
				searchFilter = searchFilterTemplate.replaceAll("%s", maybeSearchFilter);
			}
		} else {
			searchFilter = maybeSearchFilter;
		}
		return searchFilter;
	}

	/*
	 * Getters and Setters
	 */

	public String[] getResultAttributes() {
		return resultAttributes;
	}

	public void setResultAttributes(String[] resultAttributes) {
		this.resultAttributes = resultAttributes;
	}

	public ConnectionManager getConn() {
		return conn;
	}

	public void setConn(ConnectionManager conn) {
		this.conn = conn;
	}

}
