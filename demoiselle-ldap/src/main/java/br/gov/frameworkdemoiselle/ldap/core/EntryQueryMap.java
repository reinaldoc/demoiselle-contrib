/**
 * Copyright (c) 2012 - Reinaldo de Carvalho <reinaldoc@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 */

package br.gov.frameworkdemoiselle.ldap.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.ldap.configuration.EntryManagerConfig;
import br.gov.frameworkdemoiselle.ldap.exception.EntryException;
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
public class EntryQueryMap implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerProducer.create(EntryQueryMap.class);

	@Inject
	private EntryManagerConfig entryManagerConfig;

	@Inject
	private ConnectionManager conn;

	private String searchFilter;

	private String[] resultAttributes;

	private String basedn;

	private int scope;

	private LDAPSearchConstraints ldapConstraints;

	private boolean dnAsAttribute;

	private boolean enforceSingleResult;

	private List<String> binaryAttributes;

	private boolean verbose;

	@PostConstruct
	public void init() {
		scope = LDAPConnection.SCOPE_SUB;
		setLdapConstraints(new LDAPSearchConstraints());
		getLdapConstraints().setReferralFollowing(entryManagerConfig.isReferrals());
		getLdapConstraints().setMaxResults(entryManagerConfig.getSizelimit());
		basedn = entryManagerConfig.getBasedn();
		dnAsAttribute = entryManagerConfig.isDnAsAttribute();
		enforceSingleResult = entryManagerConfig.isEnforceSingleResult();
		binaryAttributes = entryManagerConfig.getBinaryAttributes();
		verbose = entryManagerConfig.isVerbose();
	}

	public void setMaxResults(int maxResult) {
		getLdapConstraints().setMaxResults(maxResult);
	}

	public void setBaseDn(String basedn) {
		this.basedn = basedn;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	public void setDnAsAttibute(boolean dnAsAttibute) {
		this.dnAsAttribute = dnAsAttibute;
	}

	public void setEnforceSingleResult(boolean enforceSingleResult) {
		this.enforceSingleResult = enforceSingleResult;
	}

	public void setAttrSorting(Map<String, Boolean> sorting) {
		LDAPSortKey[] key = new LDAPSortKey[sorting.size()];
		int i = 0;
		for (Map.Entry<String, Boolean> entry : sorting.entrySet()) {
			key[i] = new LDAPSortKey(entry.getKey(), !entry.getValue().booleanValue());
			i++;
		}
		LDAPSortControl sortControl = new LDAPSortControl(key, false);
		getLdapConstraints().setControls(sortControl);
	}

	public void setAttrSortingAsc(String... sorting) {
		LDAPSortKey[] key = new LDAPSortKey[sorting.length];
		for (int i = 0; i < sorting.length; i++)
			key[i] = new LDAPSortKey(sorting[i]);
		LDAPSortControl sortControl = new LDAPSortControl(key, false);
		getLdapConstraints().setControls(sortControl);
	}

	public void setAttrSortingDesc(String... sorting) {
		LDAPSortKey[] key = new LDAPSortKey[sorting.length];
		for (int i = 0; i < sorting.length; i++)
			key[i] = new LDAPSortKey(sorting[i], true);
		LDAPSortControl sortControl = new LDAPSortControl(key, false);
		getLdapConstraints().setControls(sortControl);
	}

	public void setResultAttributes(String... resultAttributes) {
		this.resultAttributes = resultAttributes;
	}

	private LDAPConnection getConnection() {
		return this.conn.initialized();
	}

	private LDAPSearchResults search() {
		try {
			return getConnection().search(basedn, scope, searchFilter, resultAttributes, false, getLdapConstraints());
		} catch (LDAPException e) {
			throw new EntryException("Server returned error on query " + searchFilter, e);
		}
	}

	/**
	 * Object can be String[] or byte[][]
	 * 
	 * @param entry
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> getObjectMapForEntry(LDAPEntry entry) {
		Map<String, Object> entryMap = new HashMap<String, Object>();
		Iterator<LDAPAttribute> itrAttr = entry.getAttributeSet().iterator();

		while (itrAttr.hasNext()) {
			LDAPAttribute attr = itrAttr.next();
			if (binaryAttributes.contains(attr.getName()))
				entryMap.put(attr.getName(), attr.getByteValueArray());
			else
				entryMap.put(attr.getName(), attr.getStringValueArray());
		}

		if (dnAsAttribute)
			entryMap.put("dn", new String[] { entry.getDN() });

		return entryMap;
	}

	/**
	 * 
	 * @param searchFilter
	 * @param resultAttributes
	 * @return
	 */
	private Map<String, Map<String, Object>> find() {
		Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();
		LDAPSearchResults searchResults = search();

		while (searchResults != null && searchResults.hasMore()) {
			try {
				LDAPEntry entry = searchResults.next();
				resultMap.put(entry.getDN(), getObjectMapForEntry(entry));
			} catch (LDAPReferralException e) {
				// Ignore referrals;
			} catch (LDAPException e) {
				if (e.getResultCode() == LDAPException.SIZE_LIMIT_EXCEEDED && verbose)
					logger.warn("Size limit reached for query " + searchFilter);
				else {
					e.printStackTrace();
					logger.warn("Server returned error on query " + searchFilter);
				}
			}
		}

		if (resultMap.size() == 0)
			return null;
		return resultMap;
	}

	/**
	 * 
	 * @param resultAttributes
	 * @return
	 */
	private Map<String, Map<String, Object>> find(String[] resultAttributes) {
		String[] resultAttributesSaved = null;
		if (this.resultAttributes != null)
			resultAttributesSaved = this.resultAttributes.clone();
		this.resultAttributes = resultAttributes;
		Map<String, Map<String, Object>> findResult = find();
		this.resultAttributes = resultAttributesSaved;
		return findResult;
	}

	private Map<String, Map<String, Object>> find(int maxResult) {
		int lastMaxResults = getLdapConstraints().getMaxResults();
		getLdapConstraints().setMaxResults(maxResult);
		Map<String, Map<String, Object>> searchResult = find();
		getLdapConstraints().setMaxResults(lastMaxResults);
		return searchResult;
	}

	/**
	 * 
	 * @return
	 */
	private Map<String, Map<String, Object>> getResult(boolean singleResult) {
		Map<String, Map<String, Object>> resultMap = new HashMap<String, Map<String, Object>>();

		if (singleResult)
			resultMap = find(2);
		else
			resultMap = find();

		if (resultMap == null || resultMap.size() == 0 || (singleResult && enforceSingleResult && resultMap.size() > 1))
			return null;

		return resultMap;
	}

	public Map<String, Map<String, Object>> getResult() {
		return getResult(false);
	}

	public Collection<Map<String, Object>> getResultCollection() {
		return getResult().values();
	}

	public Map<String, Object> getSingleResult() {
		Map<String, Map<String, Object>> searchResult = getResult(true);
		if (searchResult != null && !searchResult.values().isEmpty())
			return searchResult.values().iterator().next();
		return null;
	}

	/**
	 * 
	 * @return
	 */
	public Map<String, Object> getSingleAttributesResult() {
		Map<String, Object> searchResult = getSingleResult();
		for (Map.Entry<String, Object> entry : searchResult.entrySet())
			if (entry.getValue() instanceof String[])
				entry.setValue(((String[]) entry.getValue())[0]);
			else if (entry.getValue() instanceof byte[][])
				entry.setValue(((byte[][]) entry.getValue())[0]);
		return searchResult;
	}

	/**
	 * Set result attributes is required.
	 * 
	 * @return should be casted for String or byte[] for binaries
	 */
	public Object getSingleAttributeResult() {
		if (resultAttributes == null || resultAttributes.length == 0)
			return null;

		Map<String, Object> resultMap = getSingleAttributesResult();
		for (String attr : resultAttributes)
			if (resultMap.containsKey(attr))
				return resultMap.get(attr);

		return null;
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
		Map<String, Map<String, Object>> result = find(new String[] { "-" });
		if (result == null)
			return null;
		for (String dn : result.keySet()) {
			dnList.add(dn);
		}
		return dnList;
	}

	/**
	 * Execute a LDAP SEARCH query and return a List of Attributes values. Set
	 * result attributes is required.
	 * 
	 * @return a list of the Attributes values
	 */
	public List<Object> getAttributeList() {
		if (resultAttributes == null || resultAttributes.length == 0)
			return null;

		Map<String, Map<String, Object>> result = find(new String[] { "-" });
		if (result == null)
			return null;

		List<Object> resultList = new ArrayList<Object>();
		for (Map<String, Object> entry : result.values())
			for (String resultAttribute : resultAttributes)
				if (entry.containsKey(resultAttribute)) {
					Object attrObj = entry.get(resultAttribute);
					if (attrObj instanceof String[]) {
						String[] attrArray = (String[]) entry.get(resultAttribute);
						for (String attr : attrArray)
							resultList.add(attr);
					} else if (attrObj instanceof byte[][]) {
						byte[][] attrArray = (byte[][]) entry.get(resultAttribute);
						for (byte[] attr : attrArray)
							resultList.add(attr);
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

	public LDAPSearchConstraints getLdapConstraints() {
		return ldapConstraints;
	}

	public void setLdapConstraints(LDAPSearchConstraints ldapConstraints) {
		this.ldapConstraints = ldapConstraints;
	}

}
