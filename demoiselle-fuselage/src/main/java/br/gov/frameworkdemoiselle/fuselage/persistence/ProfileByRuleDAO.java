package br.gov.frameworkdemoiselle.fuselage.persistence;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfileByRule;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.gov.frameworkdemoiselle.template.JPACrud;


@PersistenceController
public class ProfileByRuleDAO extends JPACrud<SecurityProfileByRule, Long> {
	private static final long serialVersionUID = 1L;
	
}