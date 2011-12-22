package br.gov.frameworkdemoiselle.ldap.internal;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.ldap.configuration.EntryManagerConfig;
import br.gov.frameworkdemoiselle.ldap.core.EntryManager;
import br.gov.frameworkdemoiselle.ldap.core.EntryQuery;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPConstraints;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSESecureSocketFactory;
import com.novell.ldap.LDAPJSSEStartTLSFactory;

@SessionScoped
public class ConnectionManager implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntryManagerConfig entryManagerConfig;

	@Inject
	@RequestScoped
	private EntryQuery query;

	private LDAPConnection lc;
	private String host;
	private int port;
	private String tls;
	private String binddn;
	private byte[] bindpw;
	private boolean referrals = false;
	private int protocol = 3;
	private String authenticateSearchFilter;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() throws URISyntaxException {
		host = entryManagerConfig.getHost();
		port = entryManagerConfig.getPort();
		tls = entryManagerConfig.getTls();
		binddn = entryManagerConfig.getBinddn();
		bindpw = entryManagerConfig.getBindpwInBytes();
		authenticateSearchFilter = entryManagerConfig.getAuthenticateFilter();
	}

	/**
	 * Make a LDAP Connection with configuration values;
	 * 
	 * @throws LDAPException
	 */
	private void connect() throws LDAPException {
		if (tls.equals("ssl")) {
			LDAPJSSESecureSocketFactory sslFactory = new LDAPJSSESecureSocketFactory();
			lc = new LDAPConnection(sslFactory);
		} else if (tls.equals("tls")) {
			LDAPJSSEStartTLSFactory sslFactory = new LDAPJSSEStartTLSFactory();
			lc = new LDAPConnection(sslFactory);
		} else {
			lc = new LDAPConnection();
		}
		lc.connect(host, port);
		if (tls.equals("tls")) {
			lc.startTLS();
		}
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
	 * Try get a authenticated connection, bind fails silently;
	 * 
	 * @return
	 */
	public LDAPConnection initialized() {
		bind(this.binddn, this.bindpw);
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
		byte[] bindpwutf;
		try {
			bindpwutf = bindpw.getBytes("UTF8");
		} catch (Exception e) {
			return false;
		}
		return bind(binddn, bindpwutf);
	}

	/**
	 * Authenticate by user information; if already connected then reconnect and
	 * authenticate; Don't use this method unless necessary, make resource
	 * configuration;
	 * 
	 * @param binddn
	 * @param bindpw
	 */
	public boolean bind(String binddn, byte[] bindpw) {
		this.binddn = binddn;
		this.bindpw = bindpw;
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
		if (binddn != null && !binddn.contains("=")) {
			query.setSearchFilter(authenticateSearchFilter.replaceAll("%u", binddn));
			binddn = query.getSingleDn();
		}

		if (binddn != null) {
			EntryManager entryManager = new EntryManager();
			return entryManager.bind(this.binddn, this.bindpw);
		}
		return false;
	}

}
