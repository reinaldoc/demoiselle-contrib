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

package br.gov.frameworkdemoiselle.ldap.internal;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.ldap.configuration.EntryManagerConfig;

public class ConnectionURI implements Serializable {

	private static final long serialVersionUID = 1L;

	public static enum TlsEnum {
		SSL, TLS, NONE
	};

	@Inject
	private EntryManagerConfig entryManagerConfig;

	private URI serverURI;

	private boolean starttls;

	private TlsEnum useTLS;

	public ConnectionURI(String serverURI, boolean starttls)
			throws URISyntaxException {
		this.serverURI = new URI(serverURI);
		this.starttls = starttls;
		setUseTLS();
	}

	@PostConstruct
	private void init() throws URISyntaxException {
		this.serverURI = new URI(entryManagerConfig.getServer());
		this.starttls = entryManagerConfig.isStarttls();
		setUseTLS();
	}

	/**
	 * Set tls value by URI scheme and starttls option. TlsEnum.SSL for ldaps
	 * URI scheme, or TlsEnum.TLS if starttls is true, otherwise TlsEnum.NONE;
	 */
	private void setUseTLS() {
		if ("ldaps".equals(serverURI.getScheme()))
			useTLS = TlsEnum.SSL;
		else if (starttls)
			useTLS = TlsEnum.TLS;
		else
			useTLS = TlsEnum.NONE;
	}

	/**
	 * Returns the security layer at enumeration element: TlsEnum.SSL or
	 * TlsEnum.TLS or TlsEnum.NONE;
	 * 
	 * @return the security layer at TlsEnum enumeration element
	 */
	public TlsEnum getTls() {
		return useTLS;
	}

	/**
	 * @return the host component of this URI
	 */
	public String getHost() {
		return serverURI.getHost();
	}

	/**
	 * @return the port number of this URI
	 */
	public Integer getPort() {
		return serverURI.getPort();
	}

	/**
	 * @return the content of this URI as a String. The original input
	 *         constructor string
	 */
	public String getServerURI() {
		return serverURI.toString();
	}

	/**
	 * @return starttls parameter used on constructor
	 */
	public boolean isStarttls() {
		return starttls;
	}
}
