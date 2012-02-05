package br.gov.frameworkdemoiselle.fuselage.configuration;

import java.util.ArrayList;
import java.util.List;

import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(resource = "demoiselle", prefix = "fuselage.authenticators.module.LocalAuthenticator")
public class LocalAuthenticatorConfig {

	@Name("admins")
	private List<String> admins = new ArrayList<String>();

	public List<String> getAdmins() {
		return admins;
	}

	public void setAdmins(List<String> admins) {
		this.admins = admins;
	}

}
