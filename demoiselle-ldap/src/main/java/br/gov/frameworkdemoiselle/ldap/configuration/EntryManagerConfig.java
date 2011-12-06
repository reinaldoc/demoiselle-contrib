package br.gov.frameworkdemoiselle.ldap.configuration;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(resource = "EntryManager", prefix = "EntryManager")
public class EntryManagerConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("host")
	private String host = "127.0.0.1";

	@Name("port")
	private Integer port = 389;

	@Name("basedn")
	private String basedn;

	@Name("binddn")
	private String binddn;

	@Name("bindpw")
	private String bindpwStr;

	@Name("authenticate.searchFilter")
	private String authenticateSearchFilter = "(samAccountName=%u)";

	@Name("search.sizeLimit")
	private Integer searchSizelimit;

	@Name("searchCn.searchFilter")
	private String searchcnSearchfilter;

	@Name("searchCn.resultAttributes")
	private String searchcnResultattributesStr;

	@Name("searchOneEntry.searchFilter")
	private String searchOneEntrySearchFilter;


	public String getHost() {
		return host;
	}

	public Integer getPort() {
		return port;
	}

	public String getBasedn() {
		return basedn;
	}

	public String getBinddn() {
		return binddn;
	}

	public String getBindpwStr() {
		return bindpwStr;
	}

	public byte[] getBindpw() {
		try {
			return getBindpwStr().getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			return new byte[0];
		}
	}

	public Integer getSearchSizelimit() {
		return searchSizelimit;
	}

	public String getSearchcnSearchfilter() {
		return searchcnSearchfilter;
	}

	public String getSearchcnResultattributesStr() {
		return searchcnResultattributesStr;
	}

	public String[] getSearchcnResultattributes() {
		String[] searchCNresultAttributeArray = this.searchcnResultattributesStr.split(",");
		String[] searchCNresultAttributeArrayNew = new String[searchCNresultAttributeArray.length];
		for (int i = 0; i != searchCNresultAttributeArray.length; i++) {
			searchCNresultAttributeArrayNew[i] = searchCNresultAttributeArray[i].trim();
		}
		return searchCNresultAttributeArrayNew;
	}

	public String getSearchOneEntrySearchFilter() {
		return searchOneEntrySearchFilter;
	}

	public String getAuthenticateSearchFilter() {
		return authenticateSearchFilter;
	}

	public void setAuthenticateSearchFilter(String authenticateSearchFilter) {
		this.authenticateSearchFilter = authenticateSearchFilter;
	}

}