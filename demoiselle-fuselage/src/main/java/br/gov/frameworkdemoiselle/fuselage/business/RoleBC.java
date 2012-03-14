package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.enumeration.contrib.Logic;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.fuselage.persistence.RoleDAO;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.template.contrib.DelegateCrud;

public class RoleBC extends DelegateCrud<SecurityRole, Long, RoleDAO> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ResourceBC resourceBC;

	/**
	 * Get all roles except listed in @param securityRoles
	 */
	public List<SecurityRole> findRolesExceptList(List<SecurityRole> securityRoles) {
		QueryConfig<SecurityRole> queryConfig = getQueryConfig();
		queryConfig.setSorting("name");
		if (securityRoles != null  && securityRoles.size() > 0) {
			Long[] ids = new Long[securityRoles.size()];
			for (int i = 0; i != securityRoles.size(); i++)
				ids[i] = securityRoles.get(i).getId();
			queryConfig.getFilter().put("id", ids);
			queryConfig.setFilterLogic(Logic.NAND);
		}
		return findAll();
	}

	/**
	 * Get all resources except listed in @param securityResources
	 */
	public List<SecurityResource> getResourcesExceptList(List<SecurityResource> securityResources) {
		return resourceBC.findResourceExceptList(securityResources);
	}

}
