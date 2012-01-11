package br.gov.frameworkdemoiselle.fuselage.persistence;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfileDetect;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.gov.frameworkdemoiselle.template.JPACrud;


@PersistenceController
public class ProfileDetectDAO extends JPACrud<SecurityProfileDetect, Long> {
	private static final long serialVersionUID = 1L;
	
}