package br.gov.frameworkdemoiselle.ldap.core;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.ietf.ldap.LDAPConnection;
import org.ietf.ldap.LDAPException;

import br.gov.frameworkdemoiselle.ldap.configuration.EntryManagerConfig;
import br.gov.frameworkdemoiselle.ldap.internal.ConnectionManager;

public class EntryManager {

	@Inject
	private EntryManagerConfig entryManagerConfig;

	private LDAPConnection lc;

	private String host = "127.0.0.1";

	private Integer port = 389;

	private String binddn = "cn=admin,dc=nodomain";

	private byte[] bindpw = "mypassword".getBytes();

	private String authenticateSearchFilter = "(samAccountName=%u)";

	private String searchcnSearchFilter = "(&(cn=*%s*)(objectClass=inetOrgPerson))";

	private String[] searchcnResultattributes = new String[] { "cn" };

	private Integer searchSizeLimit = new Integer(10);

	private String searchBaseDn = "dc=nodomain";

	private String searchOneEntrySearchFilter = "(cn=%s)";

	private ConnectionManager connectionManager;

	@PostConstruct
	public void init() {
		connectionManager = new ConnectionManager();
		authenticateSearchFilter = entryManagerConfig.getAuthenticateSearchFilter();
		searchBaseDn = entryManagerConfig.getBasedn();
		searchSizeLimit = entryManagerConfig.getSearchSizelimit();
		searchcnSearchFilter = entryManagerConfig.getSearchcnSearchfilter();
		searchcnResultattributes = entryManagerConfig.getSearchcnResultattributes();
		searchOneEntrySearchFilter = entryManagerConfig.getSearchOneEntrySearchFilter();
	}

	/**
	 * New implementation methods: EntryManager
	 * 
	 * @throws LDAPException
	 */

	public EntryQuery createQuery() {
		EntryQuery query = new EntryQuery();
		query.setConn(connectionManager);
		return query;
	}

}
