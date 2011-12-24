package br.gov.frameworkdemoiselle.ldap.core;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.ldap.internal.ConnectionManager;

import com.novell.ldap.LDAPException;

@SessionScoped
public class EntryManager implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ConnectionManager connectionManager;

	@Inject
	private EntryQuery query;

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
	 * Update not implemented
	 */
	@SuppressWarnings("unused")
	private void update() {

	}

	/**
	 * Insert not implemented
	 */
	@SuppressWarnings("unused")
	private void insert() {

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
