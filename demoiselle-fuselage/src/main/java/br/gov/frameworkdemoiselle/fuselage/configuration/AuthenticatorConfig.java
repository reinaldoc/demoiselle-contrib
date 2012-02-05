package br.gov.frameworkdemoiselle.fuselage.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(resource = "demoiselle", prefix = "fuselage.authenticators")
public class AuthenticatorConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("modules")
	private List<String> authenticators = new ArrayList<String>();

	public List<String> getAuthenticators() {
		return authenticators;
	}

	public void setAuthenticators(List<String> authenticators) {
		this.authenticators = authenticators;
	}

}
