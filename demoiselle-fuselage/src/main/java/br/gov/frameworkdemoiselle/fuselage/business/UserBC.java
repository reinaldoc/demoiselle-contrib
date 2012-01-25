package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.fuselage.persistence.UserDAO;
import br.gov.frameworkdemoiselle.template.DelegateCrud;

public class UserBC extends DelegateCrud<SecurityUser, Long, UserDAO> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ProfileBC profileBC;

	public List<SecurityProfile> getProfiles() {
		return profileBC.findAll();
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
		if (userList.size() != 1) {
			return new SecurityUser();
		} else {
			return userList.get(0);
		}
	}

	public List<SecurityUser> findByLogin(String login) {
		SecurityUser userLoad = new SecurityUser();
		userLoad.setLogin(login);
		return getDelegate().findByExample(userLoad, true, 0);
	}

	public List<SecurityUser> findUsers(String query) {
		SecurityUser userLoad = new SecurityUser();
		userLoad.setLogin(query);
		userLoad.setName(query);
		userLoad.setDescription(query);
		return getDelegate().findByExample(userLoad, false, 0);
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
