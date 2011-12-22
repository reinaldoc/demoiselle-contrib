package br.gov.frameworkdemoiselle.ldap.configuration;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import br.gov.frameworkdemoiselle.annotation.Ignore;
import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(resource = "EntryManager", prefix = "EntryManager")
public class EntryManagerConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("server")
	private String server;

	@Ignore
	private URI serverURI;

	@Name("starttls")
	private String starttls = "no";

	@Name("binddn")
	private String binddn;

	@Name("bindpw")
	private String bindpw;

	@Name("authenticate.filter")
	private String authenticate_filter = "(uid=%u)";

	@Name("search.basedn")
	private String basedn;

	@Name("search.sizelimit")
	private Integer sizelimit = 0;

	private boolean isValueTrue(String value) {
		if (value != null) {
			String lowerValue = value.toLowerCase();
			if ("yes".equals(lowerValue) || "true".equals(lowerValue) || "1".equals(lowerValue))
				return true;
		}
		return false;
	}

	public String getServer() {
		return server;
	}

	private URI getServerURI() throws URISyntaxException {
		if (serverURI == null)
			serverURI = new URI(server);
		return serverURI;
	}

	public String getTls() throws URISyntaxException {
		if ("ldaps".equals(getServerURI().getScheme()))
			return "ssl";
		if (isValueTrue(starttls))
			return "tls";
		return "none";
	}

	public String getHost() throws URISyntaxException {
		return getServerURI().getHost();
	}

	public Integer getPort() throws URISyntaxException {
		return getServerURI().getPort();
	}

	public String getBasedn() {
		return basedn;
	}

	public String getBinddn() {
		return binddn;
	}

	public String getBindpw() {
		return bindpw;
	}

	public byte[] getBindpwInBytes() {
		try {
			return getBindpw().getBytes("UTF8");
		} catch (Exception e) {
			return new byte[0];
		}
	}

	public Integer getSizelimit() {
		return sizelimit;
	}

	public String getAuthenticateFilter() {
		return authenticate_filter;
	}

}