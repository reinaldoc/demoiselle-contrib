package br.gov.frameworkdemoiselle.fuselage.business;

import br.gov.frameworkdemoiselle.annotation.Startup;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.persistence.ResourceDAO;
import br.gov.frameworkdemoiselle.template.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;

public class ResourceBC extends DelegateCrud<SecurityResource, Long, ResourceDAO> {
	private static final long serialVersionUID = 1L;

	@Transactional
	@Startup
	public void startup() {
		if (findAll().isEmpty()) {
			insert(new SecurityResource("public_url", "/", "Url inicial"));
			insert(new SecurityResource("public_url", "/login", "Url de Login"));
			insert(new SecurityResource("public_url_startswith", "/javax.faces.resource/", "Url de recursos JSF"));
		}
	}

}
