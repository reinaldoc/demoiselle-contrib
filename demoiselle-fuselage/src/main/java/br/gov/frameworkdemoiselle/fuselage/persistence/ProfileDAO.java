package br.gov.frameworkdemoiselle.fuselage.persistence;

import java.util.List;

import javax.persistence.Query;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.gov.frameworkdemoiselle.template.JPACrud;

@PersistenceController
public class ProfileDAO extends JPACrud<SecurityProfile, Long> {
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public List<Long> getUsedPrioritiesExceptMyself(SecurityProfile securityProfile) {
		String q = "select p.welcomePagePriority from SecurityProfile as p where p.welcomePagePriority <> :welcome order by p.welcomePagePriority";
		Query query = createQuery(q);
		query.setParameter("welcome", securityProfile.getWelcomePagePriority());
		return query.getResultList();
	}

}