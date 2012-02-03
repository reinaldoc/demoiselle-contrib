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

	private int maxResults = 0;

	private String baseDN = null;

	public void init() {
		queryMap.init();
		queryMap.setDnAsAttibute(false);
		queryMap.setSearchFilter(searchFilter);
		if (maxResults != 0)
			queryMap.setMaxResults(maxResults);
		if (baseDN != null)
			queryMap.setBaseDn(baseDN);
	}

	@SuppressWarnings("rawtypes")
	public List getResultList() {
		init();
		return ClazzUtils.getEntryObjectList(queryMap.getResult(),
				ClazzUtils.getRequiredClassForSearchFilter(searchFilter, entryManagerConfig.getLdapentryPackages()));
	}

	public Object getSingleResult() {
		init();
		queryMap.setMaxResults(2);
		Map<String, Map<String, Object>> map = queryMap.getResult();
		if (map.size() == 1)
			return ClazzUtils.getEntryObjectList(map,
					ClazzUtils.getRequiredClassForSearchFilter(searchFilter, entryManagerConfig.getLdapentryPackages())).get(0);
		return null;
	}

	public void setMaxResults(int maxResult) {
		this.maxResults = maxResult;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

}
