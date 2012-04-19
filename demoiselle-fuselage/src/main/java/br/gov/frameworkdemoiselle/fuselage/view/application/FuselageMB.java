package br.gov.frameworkdemoiselle.fuselage.view.application;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.internal.configuration.PaginationConfig;
import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@Named
public class FuselageMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private PaginationConfig paginationConfig;

	@Inject
	private SecurityContext securityContext;

	public PaginationConfig getPaginationConfig() {
		return paginationConfig;
	}

	public void setPaginationConfig(PaginationConfig paginationConfig) {
		this.paginationConfig = paginationConfig;
	}

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

}
