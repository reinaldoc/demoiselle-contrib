package br.gov.frameworkdemoiselle.ldap.core;

import java.io.Serializable;
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
	private EntryQuery query;

	@Inject
	private EntryCore core;

	@Inject
	private EntryCoreMap coreMap;

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
	 * Persist a LDAP Entry. Use LDAP Add Operation
	 * 
	 * @param entry
	 *            a MAP with key as attributes and values as attribute values
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @throws EntryException
	 */
	public void persist(Map<String, String[]> entry, String dn) {
		coreMap.persist(entry, dn);
	}

	/**
	 * Merge LDAP Entry from declared attributes only. Undeclared attributes
	 * will remain. Declared attributes will be replaced. Use LDAP Modify
	 * Operation.
	 * 
	 * @param entry
	 *            a MAP with key as attributes and values as attribute values
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @throws EntryException
	 */
	public void merge(Map<String, String[]> entry, String dn) {
		coreMap.merge(entry, dn);
	}

	/**
	 * Update LDAP Entry to declared attributes only. Undeclared attributes will
	 * be removed. Declared attributes will be replaced. You must declare all
	 * required attributes. Use LDAP Modify Operation
	 * 
	 * @param entry
	 *            a MAP with key as attributes and values as attribute values
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @throws EntryException
	 */
	public void update(Map<String, String[]> entry, String dn) {
		coreMap.update(entry, dn);
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
	public Map<String, String[]> find(String searchFilter) {
		return coreMap.find(searchFilter);
	}

	/**
	 * Find a Entry by String Representation of Distinguished Names (RFC 1485)
	 * 
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @return a MAP with key as attributes and values as attribute values
	 */
	public Map<String, String[]> getReference(String dn) {
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
	public void update(Object entry) {
		core.update(entry);
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
	 * @Method not implemented yet
	 * @param entryClass
	 *            a entry class
	 * @param searchFilter
	 *            String Representation of Search Filters (RFC 4515)
	 * @return a entry object
	 */
	public <T> T find(Class<T> entryClass, Object searchFilter) {
		return core.find(entryClass, searchFilter);
	}

	/**
	 * Find a Entry by String Representation of Distinguished Names (RFC 1485)
	 * 
	 * @Method not implemented yet
	 * @param entryClass
	 *            a entry class
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @return a entry object
	 */
	public <T> T getReference(Class<T> entryClass, Object dn) {
		return core.getReference(entryClass, dn);
	}

	/**
	 * Find a DN by String Representation of Search Filters (RFC 4515)
	 * 
	 * @param searchFilter
	 *            String Representation of Search Filters (RFC 4515)
	 * @return dn String Representation of Distinguished Name (RFC 1485)
	 */
	public String findReference(Object searchFilter) {
		return core.findReference(searchFilter);
	}

	/**
	 * Create an instance of EntryQuery for executing a String Representation of
	 * Search Filters (RFC 4515)
	 * 
	 * @param ldapSearchFilter
	 *            LDAP Search Filter String (RFC 4515)
	 * @return the new entry query instance
	 */
	public EntryQuery createQuery(String ldapSearchFilter) {
		query.setFilter(ldapSearchFilter);
		return query;
	}

	/**
	 * Get verbose status.
	 * 
	 * @return true if enabled
	 */
	public boolean isVerbose() {
		return coreMap.isVerbose();
	}

	/**
	 * Enable or disabled verbose. If enabled entry processing is logged.
	 * 
	 * @param verbose
	 */
	public void setVerbose(boolean verbose) {
		connectionManager.setVerbose(verbose);
		core.setVerbose(verbose);
		coreMap.setVerbose(verbose);
		query.setVerbose(verbose);
	}

}
