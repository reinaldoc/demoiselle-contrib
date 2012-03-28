package br.gov.frameworkdemoiselle.fuselage.configuration;

import java.io.Serializable;

import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(prefix = "frameworkdemoiselle.security")
public class WebfilterConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("enabled")
	private boolean webfilter = true;

	@Name("login.page")
	private String loginPage = "/login";

	public String getLoginPage() {
		return loginPage;
	}

	public boolean isWebfilterEnabled() {
		return webfilter;
	}

}