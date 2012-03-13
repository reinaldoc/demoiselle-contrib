package br.gov.frameworkdemoiselle.fuselage.configuration;

import java.io.Serializable;

import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@Configuration(resource = "demoiselle", prefix = "fuselage.authenticators.module.LdapAuthenticator")
public class LdapAuthenticatorConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("uidAttribute")
	private String uidAttr = "uid";

	@Name("commonNameAttribute")
	private String cnAttr = "cn";

	@Name("organizationalUnitAttribute")
	private String ouAttr = "ou";

	@Name("descriptionAttribute")
	private String descriptionAttr = "description";

	@Name("userSearchFilter")
	private String userSearchFilter = "(uid=%u)";

	@Name("verbose")
	private boolean verbose = false;

	@Name("masterPassword")
	private String masterPassword;

	public String getUidAttr() {
		return uidAttr;
	}

	public void setUidAttr(String uidAttr) {
		this.uidAttr = uidAttr;
	}

	public String getCnAttr() {
		return cnAttr;
	}

	public void setCnAttr(String cnAttr) {
		this.cnAttr = cnAttr;
	}

	public String getOuAttr() {
		return ouAttr;
	}

	public void setOuAttr(String ouAttr) {
		this.ouAttr = ouAttr;
	}

	public String getDescriptionAttr() {
		return descriptionAttr;
	}

	public void setDescriptionAttr(String descriptionAttr) {
		this.descriptionAttr = descriptionAttr;
	}

	public String getUserSearchFilter(String username) {
		return userSearchFilter.replaceAll("%u", username);
	}

	public void setUserSearchFilter(String userSearchFilter) {
		this.userSearchFilter = userSearchFilter;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isMasterPassword(String password) {
		if (Strings.isBlank(masterPassword))
			return false;
		return masterPassword.equals(password);
	}

}