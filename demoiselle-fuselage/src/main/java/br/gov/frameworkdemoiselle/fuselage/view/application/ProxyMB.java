package br.gov.frameworkdemoiselle.fuselage.view.application;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import br.gov.frameworkdemoiselle.internal.configuration.PaginationConfig;
import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.util.Strings;

@Named
public class ProxyMB implements Serializable {

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
			return Strings.capitalizeBr(this.securityContext.getUser().getId().toLowerCase());
		} catch (Exception e) {
			return "null";
		}
	}

}
