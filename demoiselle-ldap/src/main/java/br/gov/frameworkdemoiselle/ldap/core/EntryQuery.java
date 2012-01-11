package br.gov.frameworkdemoiselle.ldap.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.ldap.configuration.EntryManagerConfig;
import br.gov.frameworkdemoiselle.ldap.internal.ConnectionManager;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPReferralException;
import com.novell.ldap.LDAPSearchConstraints;
import com.novell.ldap.LDAPSearchResults;
import com.novell.ldap.controls.LDAPSortControl;
import com.novell.ldap.controls.LDAPSortKey;

@RequestScoped
public class EntryQuery implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerProducer.create(EntryQuery.class);

	@Inject
	private EntryManagerConfig entryManagerConfig;

	@Inject
	private ConnectionManager conn;

	private String ldapFilter;

	private String[] resultAttributes;

	private String basedn;

	private int scope;

	private LDAPSearchConstraints ldapConstraints;

	private boolean dnAsAttribute;

	private boolean enforceSingleResult;

	@PostConstruct
	public void init() {
		ldapFilter = "(objectClass=*)";
		scope = LDAPConnection.SCOPE_SUB;
		ldapConstraints = new LDAPSearchConstraints();
		ldapConstraints.setReferralFollowing(entryManagerConfig.isReferrals());
		ldapConstraints.setMaxResults(entryManagerConfig.getSizelimit());
		basedn = entryManagerConfig.getBasedn();
		dnAsAttribute = true;
		enforceSingleResult = true;
	}

	public void setMaxResults(int maxResult) {
		this.ldapConstraints.setMaxResults(maxResult);
	}

	public void setBaseDn(String basedn) {
		this.basedn = basedn;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public void setDnAsAttibute(boolean dnAsAttibute) {
		this.dnAsAttribute = dnAsAttibute;
	}

	public void setEnforceSingleResult(boolean enforceSingleResult) {
		this.enforceSingleResult = enforceSingleResult;
	}

	public void setFilter(String ldapFilter) {
		this.ldapFilter = ldapFilter;
	}

	public void setAttrSorting(Map<String, Boolean> sorting) {
		LDAPSortKey[] key = new LDAPSortKey[sorting.size()];
		int i = 0;
		for (Map.Entry<String, Boolean> entry : sorting.entrySet()) {
			key[i] = new LDAPSortKey(entry.getKey(), !entry.getValue().booleanValue());
			i++;
		}
		LDAPSortControl sortControl = new LDAPSortControl(key, false);
		ldapConstraints.setControls(sortControl);
	}

	public void setAttrSortingAsc(String... sorting) {
		LDAPSortKey[] key = new LDAPSortKey[sorting.length];
		for (int i = 0; i < sorting.length; i++)
			key[i] = new LDAPSortKey(sorting[i]);
		LDAPSortControl sortControl = new LDAPSortControl(key, false);
		ldapConstraints.setControls(sortControl);
	}

	public void setAttrSortingDesc(String... sorting) {
		LDAPSortKey[] key = new LDAPSortKey[sorting.length];
		for (int i = 0; i < sorting.length; i++)
			key[i] = new LDAPSortKey(sorting[i], true);
		LDAPSortControl sortControl = new LDAPSortControl(key, false);
		ldapConstraints.setControls(sortControl);
	}

	public void setResultAttributes(String... resultAttributes) {
		this.resultAttributes = resultAttributes;
	}

	private LDAPConnection getConnection() {
		return this.conn.initialized();
	}

	/**
	 * 
	 * @param searchFilter
	 * @param resultAttributes
	 * @return
	 */
	private List<LDAPEntry> find() {
		List<LDAPEntry> resultList = new ArrayList<LDAPEntry>();
		try {
			LDAPSearchResults searchResults = getConnection().search(basedn, scope, ldapFilter, resultAttributes, false, ldapConstraints);
			while (searchResults != null && searchResults.hasMore()) {
				try {
					LDAPEntry entry = searchResults.next();
					resultList.add(entry);
				} catch (LDAPReferralException e) {
					// Ignore referrals;
				} catch (LDAPException e) {
					if (e.getResultCode() == LDAPException.SIZE_LIMIT_EXCEEDED)
						logger.warn("Size limit exceeded for query " + ldapFilter);
					else
						throw e;
				}
			}
		} catch (LDAPException e) {
			logger.warn("Server returned error on query " + ldapFilter);
		}
		return resultList;
	}

	/**
	 * 
	 * @param resultAttributes
	 * @return
	 */
	private List<LDAPEntry> find(String[] resultAttributes) {
		String[] resultAttributesSaved = null;
		if (this.resultAttributes != null)
			resultAttributesSaved = this.resultAttributes.clone();
		this.resultAttributes = resultAttributes;
		List<LDAPEntry> findResult = find();
		this.resultAttributes = resultAttributesSaved;
		return findResult;
	}

	/**
	 * 
	 * @return
	 */
	private Map<String, Map<String, String[]>> getResult(boolean singleResult) {
		List<LDAPEntry> searchResult = new ArrayList<LDAPEntry>();
		Map<String, Map<String, String[]>> resultMap = new HashMap<String, Map<String, String[]>>();

		searchResult = find();
		if (singleResult && enforceSingleResult && searchResult.size() > 1)
			return resultMap;

		Iterator<LDAPEntry> itrEntry = searchResult.iterator();
		while (itrEntry.hasNext()) {
			LDAPEntry entry = itrEntry.next();
			Map<String, String[]> entryMap = new HashMap<String, String[]>();
			@SuppressWarnings("unchecked")
			Iterator<LDAPAttribute> itrAttr = entry.getAttributeSet().iterator();
			while (itrAttr.hasNext()) {
				LDAPAttribute attr = itrAttr.next();
				entryMap.put(attr.getName(), attr.getStringValueArray());
			}
			if (dnAsAttribute)
				entryMap.put("dn", new String[] { entry.getDN() });
			resultMap.put(entry.getDN(), entryMap);
			if (singleResult)
				break;
		}
		return resultMap;
	}

	public Collection<Map<String, String[]>> getResultCollection() {
		return getResult().values();
	}

	public Map<String, Map<String, String[]>> getResult() {
		return getResult(false);
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, String[]> getSingleResult() {
		Map<String, String[]> resultMap = new HashMap<String, String[]>();

		int maxResults = this.ldapConstraints.getMaxResults();
		this.ldapConstraints.setMaxResults(2);
		Map<String, Map<String, String[]>> searchResult = getResult(true);
		this.ldapConstraints.setMaxResults(maxResults);

		if (!searchResult.values().isEmpty())
			resultMap = searchResult.values().iterator().next();
		return resultMap;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, String> getSingleAttributesResult() {
		Map<String, String[]> searchResult = getSingleResult();
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

	/**
	 * Set result attributes is required.
	 * 
	 * @return
	 */
	public String getSingleAttributeResult() {
		String result = new String();
		if (resultAttributes == null || resultAttributes.length == 0)
			return result;

		Map<String, String> resultMap = getSingleAttributesResult();
		for (String attr : resultAttributes) {
			if (resultMap.containsKey(attr)) {
				return resultMap.get(attr);
			}
		}
		return result;
	}

	/**
	 * Execute a LDAP SEARCH query and return a single Distinguished Name or
	 * null none or multiples results.
	 * 
	 * @return the result
	 */
	public String getSingleDn() {
		List<String> dnList = getDnList();
		if (dnList.size() == 1) {
			return dnList.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Execute a LDAP SEARCH query and return a List of Distinguished Names.
	 * 
	 * @return a list of the Distinguished Names
	 */
	public List<String> getDnList() {
		List<String> dnList = new ArrayList<String>();
		ListIterator<LDAPEntry> itr = find(new String[] { "-" }).listIterator();
		while (itr.hasNext()) {
			dnList.add(itr.next().getDN());
		}
		return dnList;
	}

	/**
	 * Execute a LDAP SEARCH query and return a List of Attributes values. Set
	 * result attributes is required.
	 * 
	 * @return a list of the Attributes values
	 */
	public List<String> getAttributeList() {
		if (resultAttributes == null || resultAttributes.length == 0)
			return new ArrayList<String>();

		List<String> resultList = new ArrayList<String>();
		ListIterator<LDAPEntry> itr = find().listIterator();
		while (itr.hasNext()) {
			LDAPEntry entry = itr.next();
			for (int i = 0; i != resultAttributes.length; i++) {
				LDAPAttribute attr = entry.getAttribute(resultAttributes[i]);
				String[] attrArray = attr.getStringValueArray();
				for (int y = 0; y != attrArray.length; y++) {
					resultList.add(attrArray[y]);
				}
			}
		}
		return resultList;
	}

	/**
	 * if isn't a search filter, convert to a valid search filter by template.
	 * 
	 * @param searchFilterTemplate
	 * @param maybeSearchFilter
	 * @return
	 */
	@SuppressWarnings("unused")
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

}
