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

package br.gov.frameworkdemoiselle.ldap.configuration;

import java.io.Serializable;
import java.util.List;

import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(resource = "entrymanager", prefix = "EntryManager")
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

	@Name("ldapentry.packages")
	private List<String> ldapentry_packages;

	@Name("findByExample.maxresult")
	private int findByExampleMaxresult = 0;

	@Name("query.enforceSingleResult")
	private boolean enforceSingleResult = true;

	@Name("query.dnAsAttribute")
	private boolean dnAsAttribute = true;

	@Name("query.binaryAttributes")
	private List<String> binaryAttributes;

	@Name("verbose")
	private boolean verbose = true;

	/**
	 * @return EntryManager.server property;
	 */
	public String getServer() {
		return server;
	}

	/**
	 * @return EntryManager.starttls property;
	 */
	public boolean isStarttls() {
		return starttls;
	}

	/**
	 * @return EntryManager.protocol property;
	 */
	public int getProtocol() {
		return protocol;
	}

	/**
	 * @return EntryManager.binddn property;
	 */
	public String getBinddn() {
		return binddn;
	}

	/**
	 * @return EntryManager.bindpw property;
	 */
	public String getBindpw() {
		return bindpw;
	}

	/**
	 * @return EntryManager.bindpw property in byte[];
	 */
	public byte[] getBindpwInBytes() {
		try {
			return getBindpw().getBytes("UTF8");
		} catch (Exception e) {
			return new byte[0];
		}
	}

	/**
	 * @return EntryManager.authenticate.filter property;
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
	 * @return EntryManager.search.sizelimit property;
	 */
	public Integer getSizelimit() {
		return sizelimit;
	}

	/**
	 * @return EntryManager.search.referrals property;
	 */
	public boolean isReferrals() {
		return referrals;
	}

	/**
	 * @return EntryManager.ldapentry.packages property;
	 */
	public List<String> getLdapentryPackages() {
		return ldapentry_packages;
	}

	/**
	 * @return EntryManager.findByExample.maxresult property
	 */
	public int getFindByExampleMaxresult() {
		return findByExampleMaxresult;
	}

	/**
	 * @return EntryManager.query.enforceSingleResult property;
	 */
	public boolean isEnforceSingleResult() {
		return enforceSingleResult;
	}

	/**
	 * @return EntryManager.query.dnAsAttribute property;
	 */
	public boolean isDnAsAttribute() {
		return dnAsAttribute;
	}

	/**
	 * @return EntryManager.query.binaryAttributes property;
	 */
	public List<String> getBinaryAttributes() {
		return binaryAttributes;
	}

	/**
	 * @return EntryManager.logger property;
	 */
	public boolean isVerbose() {
		return verbose;
	}

}