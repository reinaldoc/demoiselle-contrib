package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfileByRule;
import br.gov.frameworkdemoiselle.fuselage.persistence.ProfileByRuleDAO;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.template.contrib.DelegateCrud;

public class ProfileByRuleBC extends DelegateCrud<SecurityProfileByRule, Long, ProfileByRuleDAO> {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProfileBC profileBC;

	public List<SecurityProfile> getProfiles() {
		return profileBC.findAll();
	}

	public List<SecurityProfileByRule> findByImplementation(String implementation) {
		QueryConfig<SecurityProfileByRule> queryConfig = getQueryConfig();
		queryConfig.getFilter().put("implementation", implementation);
		queryConfig.getFilter().put("available", 1);
		return findAll();
	}

	public void disable(SecurityProfileByRule securityProfileByRule) {
		securityProfileByRule.setAvailable(0);
		update(securityProfileByRule);
	}

	public void enable(SecurityProfileByRule securityProfileByRule) {
		securityProfileByRule.setAvailable(1);
		update(securityProfileByRule);
	}

}
