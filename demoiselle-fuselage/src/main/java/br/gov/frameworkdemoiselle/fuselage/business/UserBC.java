package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.annotation.Startup;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.fuselage.persistence.UserDAO;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.stereotype.BusinessController;
import br.gov.frameworkdemoiselle.template.contrib.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;

@BusinessController
public class UserBC extends DelegateCrud<SecurityUser, Long, UserDAO> {
	private static final long serialVersionUID = 1L;

	@Transactional
	@Startup
	public void startup() {
		if (findAll().isEmpty()) {
			insert(new SecurityUser("faa-admin", "Administrador do Fuselage", "123456"));
		}
	}

	@Inject
	private ProfileBC profileBC;

	public List<SecurityProfile> getProfilesExceptList(List<SecurityProfile> securityProfiles) {
		return profileBC.findProfilesExceptList(securityProfiles);
	}

	public boolean userAvailable(String login) {
		if (login != null && login.length() > 2) {
			List<SecurityUser> userList = findByLogin(login);
			if (userList.size() == 0)
				return true;
		}
		return false;
	}

	public void insertOrUpdate(SecurityUser securityUser) {
		if (securityUser.getId() == null) {
			securityUser.setAvailable(1);
			getDelegate().insert(securityUser);
		} else
			getDelegate().update(securityUser);
	}

	public SecurityUser loadByLogin(String login) {
		List<SecurityUser> userList = findByLogin(login);
		if (userList.size() != 1)
			return new SecurityUser();
		else
			return userList.get(0);
	}

	public List<SecurityUser> findByLogin(String login) {
		QueryConfig<SecurityUser> queryConfig = getQueryConfig();
		queryConfig.getFilter().put("login", login);
		queryConfig.setFilterCaseInsensitive(false);
		return findAll();
	}

	public void disable(SecurityUser securityUser) {
		securityUser.setAvailable(0);
		update(securityUser);
	}

	public void enable(SecurityUser securityUser) {
		securityUser.setAvailable(1);
		update(securityUser);
	}

}
