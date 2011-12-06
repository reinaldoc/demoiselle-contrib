package br.gov.frameworkdemoiselle.ldap.internal;

import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.ietf.ldap.LDAPConnection;
import org.ietf.ldap.LDAPConstraints;
import org.ietf.ldap.LDAPException;

import br.gov.frameworkdemoiselle.ldap.configuration.EntryManagerConfig;
import br.gov.frameworkdemoiselle.ldap.core.EntryManager;
import br.gov.frameworkdemoiselle.ldap.core.EntryQuery;

public class ConnectionManager {

	@Inject
	private EntryManagerConfig entryManagerConfig;

	private LDAPConnection lc;
	private String host;
	private int port;
	private String binddn;
	private byte[] bindpw;
	private boolean referrals = false;
	private int protocol = 3;
	private String authenticateSearchFilter;

	@PostConstruct
	public void init() {
		host = entryManagerConfig.getHost();
		port = entryManagerConfig.getPort();
		binddn = entryManagerConfig.getBinddn();
		bindpw = entryManagerConfig.getBindpw();
		authenticateSearchFilter = entryManagerConfig.getAuthenticateSearchFilter();
	}

	/**
	 * Make a LDAP Connection with configuration values;
	 * 
	 * @throws LDAPException
	 */
	private void connect() throws LDAPException {
		lc = new LDAPConnection();
		lc.connect(host, port);
		LDAPConstraints ldapConstraints = new LDAPConstraints();
		ldapConstraints.setReferralFollowing(referrals);
		lc.setConstraints(ldapConstraints);
	}

	/**
	 * Make a LDAP Connection with user values; Don't use this method unless
	 * necessary, make resource configuration;
	 * 
	 * @throws LDAPException
	 */
	public boolean connect(String host, int port) {
		this.host = host;
		this.port = port;
		try {
			connect();
			return true;
		} catch (LDAPException e) {
			return false;
		}
	}

	/**
	 * Disconnect from server;
	 * 
	 * @throws LDAPException
	 */
	private void disconnect() throws LDAPException {
		lc.disconnect();
	}

	/**
	 * Make and Return a LDAP Connection if not connected;
	 * 
	 * @return LDAPConnection
	 * @throws LDAPException
	 */
	private LDAPConnection getConnection() throws LDAPException {
		if (lc == null || lc.isConnected() == false) {
			connect();
		}
		return lc;
	}

	/**
	 * Authenticate by user information; if already connected then reconnect and
	 * authenticate; Don't use this method unless necessary, make resource
	 * configuration;
	 * 
	 * @param binddn
	 * @param bindpw
	 */
	public boolean bind(String binddn, String bindpw) {
		this.binddn = binddn;
		try {
			this.bindpw = bindpw.getBytes("UTF8");
		} catch (Exception e) {
			this.bindpw = new byte[0];
		}
		try {
			bind();
			return true;
		} catch (LDAPException e) {
			return false;
		}
	}

	/**
	 * Get a Connection and do Authentication; if already connected then
	 * reconnect and authenticate;
	 * 
	 * @throws LDAPException
	 * @throws UnsupportedEncodingException
	 */
	private void bind() throws LDAPException {
		if (getConnection().isBound()) {
			if (!binddn.equals(getConnection().getAuthenticationDN())) {
				disconnect();
				getConnection().bind(protocol, binddn, bindpw);
			}
		} else {
			getConnection().bind(protocol, binddn, bindpw);
		}
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
		String searchFilter = null;
		EntryManager entryManager = new EntryManager();
		EntryQuery query = entryManager.createQuery();

		if (binddn != null && !binddn.contains("=")) {
			searchFilter = authenticateSearchFilter.replaceAll("%u", binddn);
			binddn = query.searchOneDN(searchFilter);
		}

		if (binddn != null) {
			try {
				LDAPConnection lc2bind = new LDAPConnection();
				lc2bind.connect(host, port);
				lc2bind.bind(3, binddn, bindpw.getBytes("UTF8"));
				lc2bind.disconnect();
				return true;
			} catch (LDAPException e) {
				if (e.getResultCode() == LDAPException.INVALID_CREDENTIALS) {
					System.out.println("[LDAP FACILITY] Wrong user or password [Info: filter=" + searchFilter + ", dn=" + binddn + "]");
				} else {
					e.printStackTrace();
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

}
