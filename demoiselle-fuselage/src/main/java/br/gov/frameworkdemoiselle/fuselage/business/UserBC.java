package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.List;

import br.gov.frameworkdemoiselle.annotation.Startup;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.fuselage.persistence.UserDAO;
import br.gov.frameworkdemoiselle.template.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.Strings;

public class UserBC extends DelegateCrud<SecurityUser, Long, UserDAO> {
	private static final long serialVersionUID = 1L;

	@Transactional
	@Startup
	public void startup() {
		if (findAll().isEmpty()) {
			insert(new SecurityUser("faa-admin", "Usuário Administrador", "adminpass"));
		}
	}

	public SecurityUser loadAndUpdate(SecurityUser securityUser) {
		if (securityUser != null && Strings.isNotBlank(securityUser.getLogin())) {
			SecurityUser bean = loadByLogin(securityUser.getLogin());
			if (bean.getId() == null) {
				bean = securityUser;
				bean.setAvailable(1);
				insert(bean);
			} else if (bean.getAvailable() == null || bean.getAvailable() != 1)
				return null;
			else {
				bean.setName(securityUser.getName());
				bean.setOrgunit(securityUser.getOrgunit());
				bean.setDescription(securityUser.getDescription());
				update(bean);
			}
			return bean;
		}
		return null;
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
		return getDelegate().findByExample(userLoad);
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
