package br.gov.frameworkdemoiselle.fuselage.persistence;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.gov.frameworkdemoiselle.template.JPACrud;

@PersistenceController
public class RoleDAO extends JPACrud<SecurityRole, Long> {
	private static final long serialVersionUID = 1L;

}