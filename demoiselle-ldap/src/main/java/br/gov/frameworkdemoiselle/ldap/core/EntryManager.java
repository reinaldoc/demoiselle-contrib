package br.gov.frameworkdemoiselle.ldap.core;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.ietf.ldap.LDAPException;

import br.gov.frameworkdemoiselle.ldap.internal.ConnectionManager;

@SessionScoped
public class EntryManager implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ConnectionManager connectionManager;

	/**
	 * Make a LDAP Connection with user values; Don't use this method unless
	 * necessary, make resource configuration;
	 * 
	 * @throws LDAPException
	 */
	public boolean connect(String host, int port) {
		return connectionManager.connect(host, port);
	}

	/**
	 * Authenticate by user information; if already authenticated then
	 * reconnect and authenticate; Don't use this method unless necessary,
	 * make resource configuration;
	 * 
	 * @param binddn
	 * @param bindpw
	 */
	public boolean bind(String binddn, String bindpw) {
		return connectionManager.bind(binddn, bindpw);
	}

	/**
	 * Authenticate by user information; if already authenticated then
	 * reconnect and authenticate; Don't use this method unless necessary,
	 * make resource configuration;
	 * 
	 * @param binddn
	 * @param bindpw
	 */
	public boolean bind(String binddn, byte[] bindpw) {
		return connectionManager.bind(binddn, bindpw);
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
		return new EntryQuery(connectionManager, ldapSearchFilter);
	}

}
