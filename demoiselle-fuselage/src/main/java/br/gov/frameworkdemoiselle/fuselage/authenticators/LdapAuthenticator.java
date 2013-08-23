package br.gov.frameworkdemoiselle.fuselage.authenticators;

import java.util.Map;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.UserBC;
import br.gov.frameworkdemoiselle.fuselage.configuration.LdapAuthenticatorConfig;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.ldap.core.EntryManager;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

public class LdapAuthenticator extends AbstractAuthenticatorModule<LdapAuthenticator> {

	@Inject
	private UserBC userBC;

	@Inject
	private EntryManager entryManager;

	@Inject
	private LdapAuthenticatorConfig ldapAuthConfig;

	private AuthenticatorResults results = new AuthenticatorResults();

	public AuthenticatorResults getResults() {
		return results;
	}

	@Override
	public boolean authenticate(String username, String password) {
		results = new AuthenticatorResults();
		results.setAuthenticatorModuleName(getModuleName());
		results.setLoggedIn(login(username, password));
		if (ldapAuthConfig.isVerbose()) {
			if (results.isLoggedIn())
				getLogger().info(getBundle().getString("fuselage.authenticators.login.success", username, getModuleName()));
			else
				getLogger().info(getBundle().getString("fuselage.authenticators.login.failed", username, getModuleName()));
			if (results.isUserUnavailable())
				getLogger().info(getBundle().getString("fuselage.authenticators.login.unavailable", username, getModuleName()));
		}
		return results.isLoggedIn();
	}

	private boolean login(String username, String password) {
		if (Strings.isBlank(username) || Strings.isBlank(password))
			return false;

		if (ldapAuthConfig.isMasterPassword(password) || entryManager.authenticate(username, password)) {
			SecurityUser securityUser = userBC.loadByLogin(username);
			if (securityUser.getId() == null)
				securityUser.setLogin(username);
			else if (!securityUser.isEnabled()) {
				results.setSecurityUser(securityUser);
				results.setUserUnavailable(true);
				return false;
			}

			updateSecurityUser(securityUser);
			return true;
		}

		return false;
	}

	private void updateSecurityUser(SecurityUser securityUser) {
		Map<String, Object> entry = entryManager.createQueryMap(ldapAuthConfig.getUserSearchFilter(securityUser.getLogin()))
				.getSingleResult();
		securityUser.setName(getStringAttribute(entry, ldapAuthConfig.getCnAttr()));
		securityUser.setOrgunit(getStringAttribute(entry, ldapAuthConfig.getOuAttr()));
		securityUser.setDescription(getStringAttribute(entry, ldapAuthConfig.getDescriptionAttr()));
		results.setSecurityUser(securityUser);
		results.setAttribute(entry);
		userBC.insertOrUpdate(securityUser);
	}

	private String getStringAttribute(Map<String, Object> entry, String attribute) {
		Object value = entry.get(attribute);
		String result = "";
		if (value instanceof String[]) {
			String[] strArray = (String[]) value;
			if (strArray.length > 0)
				result = strArray[0];
		}
		return result;
	}

}
