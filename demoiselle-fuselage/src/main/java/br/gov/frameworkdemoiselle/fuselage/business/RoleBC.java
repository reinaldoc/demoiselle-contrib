package br.gov.frameworkdemoiselle.fuselage.business;

import br.gov.frameworkdemoiselle.annotation.Startup;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.fuselage.persistence.RoleDAO;
import br.gov.frameworkdemoiselle.template.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;


public class RoleBC extends DelegateCrud<SecurityRole, Long, RoleDAO> {
	private static final long serialVersionUID = 1L;

	@Transactional
	@Startup
	public void startup() {
		if (findAll().isEmpty()) {
			//insert(new SecurityRole());
		}
	}
}
