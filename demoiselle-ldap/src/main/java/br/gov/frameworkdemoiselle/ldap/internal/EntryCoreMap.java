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

package br.gov.frameworkdemoiselle.ldap.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.ldap.configuration.EntryManagerConfig;
import br.gov.frameworkdemoiselle.ldap.core.EntryQueryMap;
import br.gov.frameworkdemoiselle.ldap.exception.EntryException;

import com.novell.ldap.LDAPAttribute;
import com.novell.ldap.LDAPAttributeSet;
import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPModification;

@RequestScoped
public class EntryCoreMap implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerProducer.create(EntryCoreMap.class);

	@Inject
	private EntryManagerConfig entryManagerConfig;

	@Inject
	private ConnectionManager conn;

	@Inject
	private EntryQueryMap queryMap;

	private boolean verbose;

	@PostConstruct
	public void init() {
		verbose = entryManagerConfig.isVerbose();
	}

	private LDAPConnection getConnection() {
		return this.conn.initialized();
	}

	public void persist(Map<String, Object> entry, String dn) {
		loggerArgs(entry, dn);
		try {
			LDAPAttributeSet attributeSet = new LDAPAttributeSet();

			for (Map.Entry<String, Object> attrMap : entry.entrySet()) {
				loggerEntry(attrMap.getKey(), attrMap.getValue());

				if (attrMap.getValue() instanceof String)
					attributeSet.add(new LDAPAttribute(attrMap.getKey(), (String) attrMap.getValue()));

				else if (attrMap.getValue() instanceof String[])
					attributeSet.add(new LDAPAttribute(attrMap.getKey(), (String[]) attrMap.getValue()));

				else if (attrMap.getValue() instanceof byte[])
					attributeSet.add(new LDAPAttribute(attrMap.getKey(), (byte[]) attrMap.getValue()));

				else
					throw new EntryException("Error persisting entry " + dn
							+ ". Attribute value should be String.class, String[].class or byte[].class");

			}

			LDAPEntry newEntry = new LDAPEntry(dn, attributeSet);
			getConnection().add(newEntry);

		} catch (LDAPException e) {
			throw new EntryException("Error persisting entry " + dn, e);
		}
	}

	public void merge(Map<String, Object> entry, String dn) {
		loggerArgs(entry, dn);
		try {
			List<LDAPModification> modList = new ArrayList<LDAPModification>();
			for (Map.Entry<String, Object> attrMap : entry.entrySet()) {
				loggerEntry(attrMap.getKey(), attrMap.getValue());

				LDAPAttribute attribute;
				if (attrMap.getValue() instanceof String)
					attribute = new LDAPAttribute(attrMap.getKey(), (String) attrMap.getValue());

				else if (attrMap.getValue() instanceof String[])
					attribute = new LDAPAttribute(attrMap.getKey(), (String[]) attrMap.getValue());

				else if (attrMap.getValue() instanceof byte[])
					attribute = new LDAPAttribute(attrMap.getKey(), (byte[]) attrMap.getValue());

				else
					throw new EntryException("Error merging entry " + dn + ". Attribute value should be String.class, String[].class or byte[].class");

				modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));
			}

			LDAPModification[] modsList = modList.toArray(new LDAPModification[0]);
			getConnection().modify(dn, modsList);

		} catch (LDAPException e) {
			throw new EntryException("Error merging entry " + dn + " - " + entry, e);
		}
	}

	public void merge(Map<String, Object> oldEntry, Map<String, Object> newEntry, String dn) {
		loggerArgs(newEntry, dn);
		try {
			List<LDAPModification> modList = new ArrayList<LDAPModification>();
			for (String attr : oldEntry.keySet()) {
				LDAPAttribute attribute;

				if (newEntry.containsKey(attr)) {
					loggerEntry(attr, newEntry.get(attr));

					if (newEntry.get(attr) instanceof String)
						attribute = new LDAPAttribute(attr, (String) newEntry.get(attr));

					else if (newEntry.get(attr) instanceof String[])
						attribute = new LDAPAttribute(attr, (String[]) newEntry.get(attr));

					else if (newEntry.get(attr) instanceof byte[])
						attribute = new LDAPAttribute(attr, (byte[]) newEntry.get(attr));

					else
						throw new EntryException("Error merging entry " + dn
								+ ". Attribute value should be String.class, String[].class or byte[].class");

					modList.add(new LDAPModification(LDAPModification.REPLACE, attribute));

				} else {
					loggerEntry(attr, null);

					attribute = new LDAPAttribute(attr);
					modList.add(new LDAPModification(LDAPModification.DELETE, attribute));
				}
			}

			LDAPModification[] modsList = modList.toArray(new LDAPModification[0]);
			getConnection().modify(dn, modsList);

		} catch (LDAPException e) {
			throw new EntryException("Error merging entry " + dn + " - " + newEntry);
		}
	}

	public void remove(String dn) {
		loggerArgs(dn);
		try {
			getConnection().delete(dn);
		} catch (LDAPException e) {
			throw new EntryException("Error deleting entry " + dn);
		}
	}

	public Map<String, Object> find(String searchFilter) {
		loggerArgs(searchFilter);
		queryMap.init();
		queryMap.setSearchFilter(searchFilter);
		return queryMap.getSingleResult();
	}

	public Map<String, Object> getReference(String dn) {
		loggerArgs(dn);
		queryMap.init();
		queryMap.setBaseDn(dn);
		queryMap.setScope(LDAPConnection.SCOPE_BASE);
		queryMap.setSearchFilter("objectClass=*");
		return queryMap.getSingleResult();
	}

	public String findReference(String searchFilter) {
		loggerArgs(searchFilter);
		queryMap.init();
		queryMap.setSearchFilter(searchFilter);
		return queryMap.getSingleDn();
	}

	private void loggerArgs(Object... arg) {
		if (verbose) {
			logger.info("@" + Thread.currentThread().getStackTrace()[2].getMethodName() + "(" + StringUtils.join(arg, ", ") + ")");
		}
	}

	private void loggerEntry(Object attr, Object value) {
		if (verbose) {
			if (value instanceof String[]) {
				String[] values = (String[]) value;
				for (String valueElement : values)
					logger.info(attr + ": " + valueElement);
			} else
				logger.info(attr + ": " + value);
		}
	}

}
