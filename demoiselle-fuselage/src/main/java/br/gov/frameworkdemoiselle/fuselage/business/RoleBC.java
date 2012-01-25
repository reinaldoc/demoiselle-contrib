package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.fuselage.persistence.RoleDAO;
import br.gov.frameworkdemoiselle.template.DelegateCrud;

public class RoleBC extends DelegateCrud<SecurityRole, Long, RoleDAO> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ResourceBC resourceBC;

	public List<SecurityResource> getResources() {
		return resourceBC.findAll();
	}

}
