package br.gov.frameworkdemoiselle.fuselage.view.app;

import java.io.Serializable;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.util.Redirector;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@Named
@SessionScoped
public class FuselageMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private SecurityContext securityContext;

	public String getUserNameProperCase() {
		try {
			return Strings.capitalize(securityContext.getUser().getId().toLowerCase());
		} catch (Exception e) {
			return "null";
		}
	}

	public String getUsername() {
		try {
			if (securityContext.isLoggedIn())
				return ((SecurityUser) securityContext.getUser().getAttribute("user")).getLogin();
		} catch (Exception e) {
			return "null";
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public String getDn() {
		try {
			return ((Map<String, String>) securityContext.getUser().getAttribute("user_detail")).get("dn");
		} catch (Exception e) {
			return null;
		}
	}

	public String redirectToWelcomePage() {
		try {
			Redirector.redirect((String) securityContext.getUser().getAttribute("welcome_page"));
		} catch (Exception e) {
		}
		return null;
	}

}
