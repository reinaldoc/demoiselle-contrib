package br.gov.frameworkdemoiselle.ldap.core;

import java.io.Serializable;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

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
	 */
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	/**
	 * Make a LDAP Connection with user values; Avoid use this method unless
	 * necessary, make resource configuration;
	 * 
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
	 * @param bindpw
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
	 * @param bindpw
	 */
	public boolean bind(String binddn, byte[] bindpw) {
		return connectionManager.bind(binddn, bindpw, protocol);
	}

	/**
	 * This is a isolated method that use a alternative connection to validate a
	 * dn or user and a password. This method don't touch current connection.
	 * 
	 * @param binddn
	 * @param bindpw
	 * @return
	 */
	public boolean authenticate(String binddn, String bindpw) {
		return connectionManager.authenticate(binddn, bindpw, protocol);
	}

	/**
	 * Persist a LDAP Entry. Use LDAP Add Operation
	 */
	public void persist(Map<String, String[]> entry, String dn) {
		coreMap.persist(entry, dn);
	}

	/**
	 * Update LDAP Entry from declared attributes only (ignore others) Use LDAP
	 * Modify Operation
	 */
	public void merge(Map<String, String[]> entry, String dn) {
		coreMap.merge(entry, dn);
	}

	/**
	 * Update LDAP Entry to declared attributes only (remove others). Use LDAP
	 * Modify Operation
	 */
	public void update(Map<String, String[]> entry, String dn) {
		coreMap.update(entry, dn);
	}

	/**
	 * Remove LDAP Entry
	 */
	public void remove(String dn) {
		coreMap.remove(dn);
	}

	/**
	 * Find a LDAP Entry by LDAP Search Filter (RFC 4515)
	 */
	public Map<String, String[]> find(String searchFilter) {
		return coreMap.find(searchFilter);
	}

	/**
	 * Find a LDAP Entry DN by DN (RFC 1485)
	 */
	public Map<String, String[]> getReference(String dn) {
		return coreMap.getReference(dn);
	}

	/**
	 * Find a LDAP Entry DN by LDAP Search Filter (RFC 4515)
	 */
	public String findReference(String searchFilter) {
		return coreMap.findReference(searchFilter);
	}

	/**
	 * Future support for POJO. Not implemented.
	 */
	public void persist(Object entry) {
		core.persist(entry);
	}

	/**
	 * Future support for POJO. Not implemented.
	 */
	public void merge(Object entry) {
		core.merge(entry);
	}

	/**
	 * Future support for POJO. Not implemented.
	 */
	public void remove(Object entry) {
		core.remove(entry);
	}

	/**
	 * Future support for POJO. Not implemented.
	 */
	public <T> T find(Class<T> entryClass, Object dn) {
		return core.find(entryClass, dn);
	}

	/**
	 * Future support for POJO. Not implemented.
	 */
	public <T> T getReference(Class<T> entryClass, Object dn) {
		return core.getReference(entryClass, dn);
	}

	/**
	 * Future support for POJO. Not implemented.
	 */
	public String findReference(Object entry) {
		return core.findReference(entry);
	}

	/**
	 * 
	 * @return
	 */
	public EntryQuery createQuery(String ldapSearchFilter) {
		query.setFilter(ldapSearchFilter);
		return query;
	}

}
