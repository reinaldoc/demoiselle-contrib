package br.gov.frameworkdemoiselle.ldap.configuration;

import java.io.Serializable;

import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(resource = "EntryManager", prefix = "EntryManager")
public class EntryManagerConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("server")
	private String server = "ldap://127.0.0.1:389";

	@Name("starttls")
	private boolean starttls = false;

	@Name("protocol")
	private int protocol = 3;

	@Name("binddn")
	private String binddn = "";

	@Name("bindpw")
	private String bindpw = "";

	@Name("authenticate.filter")
	private String authenticate_filter = "(uid=%u)";

	@Name("search.basedn")
	private String basedn = "";

	@Name("search.sizelimit")
	private Integer sizelimit = 0;

	@Name("search.referrals")
	private boolean referrals = false;

	@Name("logger")
	private boolean logger = true;

	/**
	 * Get EntryManager.server property;
	 * 
	 * @return
	 */
	public String getServer() {
		return server;
	}

	/**
	 * Get EntryManager.starttls property;
	 * 
	 * @return
	 */
	public boolean isStarttls() {
		return starttls;
	}

	/**
	 * Get EntryManager.protocol property;
	 * 
	 * @return
	 */
	public int getProtocol() {
		return protocol;
	}

	/**
	 * Get EntryManager.binddn property;
	 * 
	 * @return
	 */
	public String getBinddn() {
		return binddn;
	}

	/**
	 * Get EntryManager.bindpw property;
	 * 
	 * @return
	 */
	public String getBindpw() {
		return bindpw;
	}

	/**
	 * Get EntryManager.bindpw property in byte[];
	 * 
	 * @return
	 */
	public byte[] getBindpwInBytes() {
		try {
			return getBindpw().getBytes("UTF8");
		} catch (Exception e) {
			return new byte[0];
		}
	}

	/**
	 * Get EntryManager.authenticate.filter property;
	 * 
	 * @return
	 */
	public String getAuthenticateFilter() {
		return authenticate_filter;
	}

	/**
	 * Get EntryManager.search.basedn property;
	 * 
	 * @return
	 */
	public String getBasedn() {
		return basedn;
	}

	/**
	 * Get EntryManager.search.sizelimit property;
	 * 
	 * @return
	 */
	public Integer getSizelimit() {
		return sizelimit;
	}

	/**
	 * Get EntryManager.search.referrals property;
	 * 
	 * @return
	 */
	public boolean isReferrals() {
		return referrals;
	}

	/**
	 * Get EntryManager.logger property;
	 * 
	 * @return
	 */
	public boolean isLogger() {
		return logger;
	}

}