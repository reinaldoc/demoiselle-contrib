package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.enumeration.contrib.Logic;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.fuselage.persistence.ProfileDAO;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.stereotype.BusinessController;
import br.gov.frameworkdemoiselle.template.contrib.DelegateCrud;

@BusinessController
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

	/**
	 * Get all roles except listed in @param securityProfiles
	 */
	public List<SecurityProfile> findProfilesExceptList(List<SecurityProfile> securityProfiles) {
		QueryConfig<SecurityProfile> queryConfig = getQueryConfig();
		queryConfig.setSorting("name");
		if (securityProfiles != null && securityProfiles.size() > 0) {
			Long[] ids = new Long[securityProfiles.size()];
			for (int i = 0; i != securityProfiles.size(); i++)
				ids[i] = securityProfiles.get(i).getId();
			queryConfig.getFilter().put("id", ids);
			queryConfig.setFilterLogic(Logic.NAND);
		}
		return findAll();
	}

	public List<SecurityRole> getRolesExceptList(List<SecurityRole> securityRoles) {
		return roleBC.findRolesExceptList(securityRoles);
	}

	public List<SecurityResource> getResources() {
		getQueryConfig(SecurityResource.class).setSorting("name");
		return resourceBC.findAll();
	}

}
