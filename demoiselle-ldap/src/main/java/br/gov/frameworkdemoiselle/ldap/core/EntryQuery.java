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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.ldap.configuration.EntryManagerConfig;
import br.gov.frameworkdemoiselle.ldap.internal.ClazzUtils;

@RequestScoped
public class EntryQuery implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntryManagerConfig entryManagerConfig;

	@Inject
	private EntryQueryMap queryMap;

	private String searchFilter;

	private int maxResults;

	@PostConstruct
	public void init() {
		queryMap.init();
		queryMap.setSearchFilter(searchFilter);
		queryMap.setMaxResults(maxResults);
	}

	@SuppressWarnings("rawtypes")
	public List getResultList() {
		init();
		queryMap.setDnAsAttibute(false);
		return ClazzUtils.getEntryObjectList(queryMap.getResult(),
				ClazzUtils.getRequiredClassForSearchFilter(searchFilter, entryManagerConfig.getLdapentryPackages()));
	}

	public Object getSingleResult() {
		init();
		queryMap.setDnAsAttibute(false);
		queryMap.setMaxResults(2);
		Map<String, Map<String, Object>> map = queryMap.getResult();
		if (map.size() == 1)
			return ClazzUtils.getEntryObjectList(map,
					ClazzUtils.getRequiredClassForSearchFilter(searchFilter, entryManagerConfig.getLdapentryPackages())).get(0);
		return null;
	}

	public String getPartialFilter(String attr, String value, boolean isConjunction) {
		if (isConjunction)
			return "(" + attr + "=" + value + ")";
		else
			return "(" + attr + "=*" + value + "*)";
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findByExample(T entry, boolean isConjunction, int maxResult) {
		try {
			Map<String, Object> map = ClazzUtils.getObjectMap(entry);

			String filter = "";
			for (Map.Entry<String, Object> mapEntry : map.entrySet())
				if (mapEntry.getValue() instanceof String)
					filter = filter + getPartialFilter(mapEntry.getKey(), (String) mapEntry.getValue(), isConjunction);
				else if (mapEntry.getValue() instanceof String[])
					for (String value : (String[]) mapEntry.getValue())
						filter = filter + getPartialFilter(mapEntry.getKey(), value, isConjunction);

			if (filter.isEmpty())
				return null;

			if (isConjunction)
				setSearchFilter("(&(objectClass=" + entry.getClass().getSimpleName() + ")(&" + filter + "))");
			else
				setSearchFilter("(&(objectClass=" + entry.getClass().getSimpleName() + ")(|" + filter + "))");

			setMaxResults(maxResult);
			return getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public <T> List<T> findByExample(T entry, boolean isConjunction) {
		return findByExample(entry, isConjunction, entryManagerConfig.getFindByExampleMaxresult());
	}

	public void setMaxResults(int maxResult) {
		this.maxResults = maxResult;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

}
