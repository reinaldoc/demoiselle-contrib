package br.gov.frameworkdemoiselle.fuselage.persistence;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.gov.frameworkdemoiselle.template.JPACrud;

@PersistenceController
public class UserDAO extends JPACrud<SecurityUser, Long> {
	private static final long serialVersionUID = 1L;
	
}
