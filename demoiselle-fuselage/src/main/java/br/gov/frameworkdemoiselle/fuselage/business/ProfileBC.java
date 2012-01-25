package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.fuselage.persistence.ProfileDAO;
import br.gov.frameworkdemoiselle.template.DelegateCrud;

public class ProfileBC extends DelegateCrud<SecurityProfile, Long, ProfileDAO> {
	private static final long serialVersionUID = 1L;

	@Inject
	private RoleBC roleBC;

	@Inject
	private ResourceBC resourceBC;

	public List<Long> getUsedPrioritiesExceptMyself(SecurityProfile securityProfile) {
		List<Long> usedPriorities = new ArrayList<Long>();
		for (Long usedPriority : getDelegate().getUsedPrioritiesExceptMyself(securityProfile))
			usedPriorities.add(usedPriority);
		return usedPriorities;
	}

	public List<SecurityRole> getRoles() {
		return roleBC.findAll();
	}

	public List<SecurityResource> getResources() {
		return resourceBC.findAll();
	}

}
