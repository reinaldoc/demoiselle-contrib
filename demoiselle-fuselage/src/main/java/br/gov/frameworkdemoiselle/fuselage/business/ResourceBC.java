package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.List;

import br.gov.frameworkdemoiselle.annotation.Startup;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.persistence.ResourceDAO;
import br.gov.frameworkdemoiselle.template.contrib.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;

public class ResourceBC extends DelegateCrud<SecurityResource, Long, ResourceDAO> {

	private static final long serialVersionUID = 1L;

	@Transactional
	@Startup
	public void startup() {
		if (findAll().isEmpty()) {
			insert(new SecurityResource("public_url", "/index.jsf", "Url inicial"));
			insert(new SecurityResource("public_url", "/security/login.jsf", "Url de Login"));
			insert(new SecurityResource("public_url_startswith", "/javax.faces.resource/", "Url de recursos JSF"));
		}
	}

	public List<SecurityResource> findResourceByName(String resourceName) {
		getQueryConfig().getFilter().put("name", resourceName);
		getQueryConfig().setFilterCaseInsensitive(false);
		return findAll();
	}

}
