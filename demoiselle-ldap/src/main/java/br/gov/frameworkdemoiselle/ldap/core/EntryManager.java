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

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.ldap.exception.EntryException;
import br.gov.frameworkdemoiselle.ldap.internal.ConnectionManager;
import br.gov.frameworkdemoiselle.ldap.internal.EntryCore;
import br.gov.frameworkdemoiselle.ldap.internal.EntryCoreMap;

import com.novell.ldap.LDAPException;

@SessionScoped
public class EntryManager implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ConnectionManager connectionManager;

	@Inject
	private EntryCore core;

	@Inject
	private EntryCoreMap coreMap;

	@Inject
	private EntryQuery query;

	@Inject
	private EntryQueryMap queryMap;

	private int protocol = 3;

	/**
	 * Set LDAP Protocol for LDAP BIND operation when not using resource
	 * configuration;
	 * 
	 * @param protocol
	 *            LDAP BIND Protocol commonly is 3
	 */
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	/**
	 * Make a LDAP Connection with user values; Avoid use this method unless
	 * necessary, make resource configuration;
	 * 
	 * @param serverURI
	 *            A DSA URI e.g. ldaps://ldap.example.com:636
	 * @param useTLS
	 *            Use STARTTLS, ignored if serverURI is ldaps
	 * @return true if connections is succesfully, otherwise false
	 * @throws LDAPException
	 */
	public boolean connect(String serverURI, boolean useTLS) {
		return connectionManager.connect(serverURI, useTLS);
	}

	/**
	 * Authenticate by user information; if already authenticated then reconnect
	 * and authenticate; Don't use this method unless necessary, make resource
	 * configuration;
	 * 
	 * @param binddn
	 *            A binddn to pass to bind operation e.g. cn=admin,o=University
	 * @param bindpw
	 *            A binddn password
	 * @return true if bind is succesfully, otherwise false
	 */
	public boolean bind(String binddn, String bindpw) {
		return connectionManager.bind(binddn, bindpw, protocol);
	}

	/**
	 * Authenticate by user information; if already authenticated then reconnect
	 * and authenticate; Don't use this method unless necessary, make resource
	 * configuration;
	 * 
	 * @param binddn
	 *            A binddn to pass to bind operation e.g. cn=admin,o=University
	 * @param bindpw
	 *            A binddn password
	 * @return true if bind is succesfully, otherwise false
	 */
	public boolean bind(String binddn, byte[] bindpw) {
		return connectionManager.bind(binddn, bindpw, protocol);
	}

	/**
	 * Get DN used on bind operation or null
	 * 
	 * @return A DN used on bind method or null
	 */
	public String getBindDn() {
		return connectionManager.getBindDn();
	}

	/**
	 * This is a isolated method that use a alternative connection to validate a
	 * dn or user and a password. This method don't touch current connection
	 * authentication.
	 * 
	 * @param binddn
	 *            A binddn to pass to bind operation in a new connection e.g.
	 *            cn=admin,o=University
	 * @param bindpw
	 *            A binddn password
	 * @return true if bind is succesfully, otherwise false
	 */
	public boolean authenticate(String binddn, String bindpw) {
		return connectionManager.authenticate(binddn, bindpw, protocol);
	}

	/**
	 * Get DN used on authenticate method or null (isn't current connection
	 * authentication)
	 * 
	 * @return The last DN used on authenticate method or null
	 */
	public String getAuthenticateDn() {
		return connectionManager.getAuthenticateDn();
	}

	/**
	 * Persist a LDAP Entry. Use LDAP Add Operation
	 * 
	 * @param entry
	 *            a MAP with key as attributes and values as attribute values.
	 *            The valid types for Object value is String, String[] or
	 *            byte[], a wrong type throw EntryException
	 * 
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @throws EntryException
	 */
	public void persist(Map<String, Object> entry, String dn) {
		coreMap.persist(entry, dn);
	}

	/**
	 * Merge LDAP Entry from declared attributes only. Undeclared attributes
	 * will remain. Declared attributes will be replaced. Use LDAP Modify
	 * Operation.
	 * 
	 * @param entry
	 *            a MAP with key as attributes and values as attribute values
	 *            The valid types for Object value is String, String[] or
	 *            byte[], a wrong type throw EntryException
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @throws EntryException
	 */
	public void merge(Map<String, Object> entry, String dn) {
		coreMap.merge(entry, dn);
	}

	/**
	 * Merge LDAP Entry to declared attributes only. Undeclared attributes will
	 * be removed from DSA. Declared attributes will be replaced. You must
	 * declare all required attributes. Use LDAP Modify Operation.
	 * 
	 * @param oldEntry
	 *            a MAP with key as attributes and values as attribute values
	 *            The valid types for Object value is String, String[] or
	 *            byte[], a wrong type throw EntryException
	 * @param entry
	 *            a MAP with key as attributes and values as attribute values
	 *            The valid types for Object value is String, String[] or
	 *            byte[], a wrong type throw EntryException
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @throws EntryException
	 */
	public void merge(Map<String, Object> oldEntry, Map<String, Object> entry, String dn) {
		coreMap.merge(oldEntry, entry, dn);
	}

	/**
	 * Remove a LDAP Entry. Use LDAP Del Operation
	 * 
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @throws EntryException
	 */
	public void remove(String dn) {
		coreMap.remove(dn);
	}

	/**
	 * Find a Entry by String Representation of Search Filters (RFC 4515)
	 * 
	 * @param searchFilter
	 *            String Representation of Search Filters (RFC 4515)
	 * @return a MAP with key as attributes and values as attribute values
	 */
	public Map<String, Object> find(String searchFilter) {
		return coreMap.find(searchFilter);
	}

	/**
	 * Find a Entry by String Representation of Distinguished Names (RFC 1485)
	 * 
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @return a MAP with key as attributes and values as attribute values
	 */
	public Map<String, Object> getReference(String dn) {
		return coreMap.getReference(dn);
	}

	/**
	 * Find a DN by String Representation of Search Filters (RFC 4515)
	 * 
	 * @param searchFilter
	 *            String Representation of Search Filters (RFC 4515)
	 * @return String Representation of Distinguished Name (RFC 1485)
	 */
	public String findReference(String searchFilter) {
		return coreMap.findReference(searchFilter);
	}

	/**
	 * Persist a @LDAPEntry annotated object. Use LDAP Add Operation
	 * 
	 * @param a
	 * @LDAPEntry annotated object
	 * @throws EntryException
	 */
	public void persist(Object entry) {
		core.persist(entry);
	}

	/**
	 * Merge LDAP Entry from not null attributes only. Null attributes will
	 * remain in DSA. Not null attributes will be replaced. Use LDAP Modify
	 * Operation.
	 * 
	 * @param a
	 * @LDAPEntry annotated object
	 * @throws EntryException
	 */
	public void merge(Object entry) {
		core.merge(entry);
	}

	/**
	 * Update LDAP Entry to not null attributes only. Null attributes will be
	 * removed from DSA. Not null attributes will be replaced. You must declare
	 * all required attributes. Use LDAP Modify Operation
	 * 
	 * @param a
	 * @LDAPEntry annotated object
	 * @throws EntryException
	 */
	public void update(Object oldEntry, Object entry) {
		core.merge(oldEntry, entry);
	}

	/**
	 * Remove a @LDAPEntry annotated object. Use LDAP Del Operation
	 * 
	 * @param a
	 * @LDAPEntry annotated object
	 * @throws EntryException
	 */
	public void remove(Object entry) {
		core.remove(entry);
	}

	/**
	 * Find a Entry by String Representation of Search Filters (RFC 4515)
	 * 
	 * @param entryClass
	 *            a entry class
	 * @param searchFilter
	 *            String Representation of Search Filters (RFC 4515)
	 * @return a entry object
	 */
	public <T> T find(Class<T> entryClass, Object id) {
		return core.find(entryClass, id);
	}

	/**
	 * Find a Entry with only DistinguishedName value by @id annotated value
	 * 
	 * @param entryClass
	 *            a entry class
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @return a entry object
	 */
	public <T> T getReference(Class<T> entryClass, Object id) {
		return core.getReference(entryClass, id);
	}

	/**
	 * Create a query based on not null attributes values of a entry.
	 * 
	 * @param entry
	 * @param isDisjunction
	 * @return
	 */
	public <T> List<T> findByExample(T entry, boolean isConjunction, int maxResult) {
		return query.findByExample(entry, isConjunction, maxResult);
	}

	/**
	 * Create a query based on not null attributes values of a entry.
	 * 
	 * @param entry
	 * @param isDisjunction
	 * @return
	 */
	public <T> List<T> findByExample(T entry, boolean isConjunction) {
		return query.findByExample(entry, isConjunction);
	}

	/**
	 * Create a query based on not null attributes values of a entry.
	 * 
	 * @param entry
	 * @param isDisjunction
	 * @return
	 */
	public <T> List<T> findByExample(T entry) {
		return query.findByExample(entry, true);
	}

	/**
	 * Create an instance of EntryQuery for executing a String Representation of
	 * Search Filters (RFC 4515) and returns Objects
	 * 
	 * @param ldapSearchFilter
	 *            LDAP Search Filter String (RFC 4515)
	 * @return the new entry query instance
	 */
	public EntryQuery createQuery(String searchFilter) {
		query.setSearchFilter(searchFilter);
		return query;
	}

	/**
	 * Create an instance of EntryQueryMap for executing a String Representation
	 * of Search Filters (RFC 4515) and returns HashMaps
	 * 
	 * @param ldapSearchFilter
	 *            LDAP Search Filter String (RFC 4515)
	 * @return the new entry query instance
	 */
	public EntryQueryMap createQueryMap(String searchFilter) {
		queryMap.init();
		queryMap.setSearchFilter(searchFilter);
		return queryMap;
	}

}
