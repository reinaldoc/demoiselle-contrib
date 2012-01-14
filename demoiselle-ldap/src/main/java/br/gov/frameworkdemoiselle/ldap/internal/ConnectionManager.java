package br.gov.frameworkdemoiselle.ldap.internal;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.ldap.configuration.EntryManagerConfig;
import br.gov.frameworkdemoiselle.ldap.core.EntryQuery;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPConstraints;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPJSSESecureSocketFactory;
import com.novell.ldap.LDAPJSSEStartTLSFactory;

@SessionScoped
public class ConnectionManager implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerProducer.create(ConnectionManager.class);

	@Inject
	private EntryManagerConfig entryManagerConfig;

	@Inject
	private EntryQuery query;

	private LDAPConnection lc;

	private ConnectionURI connURI;

	private String binddn;

	private byte[] bindpw;

	private boolean referrals;

	private int protocol;

	private String authenticateFilter;

	private boolean verbose = false;

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() throws URISyntaxException {
		if (entryManagerConfig.isLogger())
			verbose = true;
		connURI = new ConnectionURI(entryManagerConfig.getServer(), entryManagerConfig.isStarttls());
		binddn = entryManagerConfig.getBinddn();
		bindpw = entryManagerConfig.getBindpwInBytes();
		referrals = entryManagerConfig.isReferrals();
		protocol = entryManagerConfig.getProtocol();
		authenticateFilter = entryManagerConfig.getAuthenticateFilter();
	}

	/**
	 * Make a LDAP Connection with configuration values;
	 * 
	 * @throws LDAPException
	 * @throws URISyntaxException
	 */
	private void connect() throws LDAPException, URISyntaxException {
		if (connURI.getTls() == ConnectionURI.TlsEnum.SSL) {
			LDAPJSSESecureSocketFactory connFactory = new LDAPJSSESecureSocketFactory();
			lc = new LDAPConnection(connFactory);
		} else if (connURI.getTls() == ConnectionURI.TlsEnum.TLS) {
			LDAPJSSEStartTLSFactory connFactory = new LDAPJSSEStartTLSFactory();
			lc = new LDAPConnection(connFactory);
		} else {
			lc = new LDAPConnection();
		}
		lc.connect(connURI.getHost(), connURI.getPort());
		if (connURI.getTls() == ConnectionURI.TlsEnum.TLS) {
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
	public boolean connect(String serverURI, boolean useTLS) {
		try {
			connURI = new ConnectionURI(serverURI, useTLS);
			connect();
			return true;
		} catch (LDAPException e) {
			loggerInfo("LDAPException raised at connect()");
			return false;
		} catch (URISyntaxException e) {
			loggerInfo("URISyntaxException raised at connect()");
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
	 * @throws URISyntaxException
	 */
	private LDAPConnection getConnection() throws LDAPException, URISyntaxException {
		if (lc == null || !lc.isConnected()) {
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
		bind(binddn, bindpw, protocol);
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
	public boolean bind(String binddn, String bindpw, int protocol) {
		byte[] bindpwutf;
		try {
			bindpwutf = bindpw.getBytes("UTF8");
		} catch (Exception e) {
			loggerInfo("Exception raised at bind()");
			return false;
		}
		return bind(binddn, bindpwutf, protocol);
	}

	/**
	 * Authenticate by user information; if already connected then reconnect and
	 * authenticate; Don't use this method unless necessary, make resource
	 * configuration;
	 * 
	 * @param binddn
	 * @param bindpw
	 */
	public boolean bind(String binddn, byte[] bindpw, int protocol) {
		this.binddn = binddn;
		this.bindpw = bindpw;
		this.protocol = protocol;
		try {
			bind();
			return true;
		} catch (LDAPException e) {
			loggerInfo("LDAPException raised at bind()");
			return false;
		} catch (URISyntaxException e) {
			loggerInfo("URISyntaxException raised at bind()");
			return false;
		}
	}

	/**
	 * Get a Connection and do Authentication; if already connected then
	 * reconnect and authenticate;
	 * 
	 * @throws LDAPException
	 * @throws URISyntaxException
	 * @throws UnsupportedEncodingException
	 */
	private void bind() throws LDAPException, URISyntaxException {
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
	public boolean authenticate(String binddn, String bindpw, int protocol) {
		if (binddn != null && !binddn.contains("=")) {
			query.setFilter(authenticateFilter.replaceAll("%u", binddn));
			binddn = query.getSingleDn();
		}

		if (binddn != null) {
			ConnectionManager cm = new ConnectionManager();
			cm.connect(connURI.getServerURI(), connURI.isStarttls());
			return cm.bind(binddn, bindpw, protocol);
		}
		return false;
	}

	private void loggerInfo(String msg) {
		if (verbose) {
			this.logger.info(msg);
		}
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

}
