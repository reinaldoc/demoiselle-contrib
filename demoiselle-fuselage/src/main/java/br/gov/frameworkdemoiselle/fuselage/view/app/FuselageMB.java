package br.gov.frameworkdemoiselle.fuselage.view.app;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.gov.frameworkdemoiselle.fuselage.configuration.WebfilterConfig;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.menu.core.MenuContext;
import br.gov.frameworkdemoiselle.security.NotLoggedInException;
import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.util.Redirector;
import br.gov.frameworkdemoiselle.util.contrib.Faces;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@Named
@SessionScoped
public class FuselageMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private WebfilterConfig config;

	@Inject
	private MenuContext menuContext;

	@Inject
	private SecurityContext securityContext;

	public boolean hasRole(String role) {
		try {
			return securityContext.hasRole(role);
		} catch (NotLoggedInException e) {
			return false;
		}
	}

	public boolean hasOneOfRoles(Collection<String> roles) {
		if (roles != null)
			for (String role : roles)
				if (hasRole(role))
					return true;
		return false;
	}

	public boolean hasResource(String operation, String resource) {
		try {
			return securityContext.hasPermission(operation, resource);
		} catch (NotLoggedInException e) {
			return false;
		}
	}

	public SecurityUser getSecurityUser() {
		try {
			if (securityContext.isLoggedIn())
				return (SecurityUser) securityContext.getUser().getAttribute("user");
		} catch (Exception e) {
		}
		return null;
	}

	public String getUsername() {
		try {
			return getSecurityUser().getLogin();
		} catch (Exception e) {
			return null;
		}
	}

	public String getUserNameProperCase() {
		try {
			return Strings.capitalize(securityContext.getUser().getId().toLowerCase());
		} catch (Exception e) {
			return "null";
		}
	}

	@SuppressWarnings("unchecked")
	public String getDn() {
		try {
			return ((String[]) ((Map<String, Object>) securityContext.getUser().getAttribute("user_detail")).get("dn"))[0];
		} catch (Exception e) {
			return null;
		}
	}

	public String redirectToWelcomePage() {
		try {
			String welcome = (String) securityContext.getUser().getAttribute("welcome_page");
			if (welcome == null)
				welcome = Faces.getFacesContext().getExternalContext().getRequestContextPath() + config.getLoginPage();
			else
				menuContext.select("URL", welcome);
			Redirector.redirect(welcome);
		} catch (Exception e) {
		}
		return null;
	}

}
