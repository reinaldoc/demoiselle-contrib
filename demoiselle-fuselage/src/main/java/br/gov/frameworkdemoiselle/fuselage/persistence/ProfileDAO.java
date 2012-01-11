package br.gov.frameworkdemoiselle.fuselage.persistence;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.gov.frameworkdemoiselle.template.JPACrud;

@PersistenceController
public class ProfileDAO extends JPACrud<SecurityProfile, Long> {
	private static final long serialVersionUID = 1L;
	
}