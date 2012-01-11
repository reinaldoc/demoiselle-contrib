package br.gov.frameworkdemoiselle.fuselage.business;

import br.gov.frameworkdemoiselle.annotation.Startup;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.persistence.ProfileDAO;
import br.gov.frameworkdemoiselle.template.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;

public class ProfileBC extends DelegateCrud<SecurityProfile, Long, ProfileDAO> {
	private static final long serialVersionUID = 1L;

	@Transactional
	@Startup
	public void startup() {
		if (findAll().isEmpty()) {
			// insert(new SecurityProfile());
		}
	}
}
