package br.gov.frameworkdemoiselle.fuselage.authenticators;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.UserBC;
import br.gov.frameworkdemoiselle.fuselage.configuration.LdapAuthenticatorConfig;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.ldap.core.EntryManager;
import br.gov.frameworkdemoiselle.util.Strings;

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

		if (entryManager.authenticate(username, password)) {
			results.getGenericResults().put("dn", entryManager.getAuthenticateDn());

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
		Map<String, Object> attMap;
		attMap = entryManager.createQueryMap(ldapAuthConfig.getUserSearchFilter().replaceAll("%u", securityUser.getLogin()))
				.getSingleAttributesResult();

		securityUser.setName((String) attMap.get(ldapAuthConfig.getCnAttr()));
		securityUser.setOrgunit((String) attMap.get(ldapAuthConfig.getOuAttr()));
		securityUser.setDescription((String) attMap.get(ldapAuthConfig.getDescriptionAttr()));
		results.setSecurityUser(securityUser);

		userBC.insertOrUpdate(securityUser);

		Iterator<Map.Entry<String, Object>> entryIter = attMap.entrySet().iterator();
		while (entryIter.hasNext()) {
			Map.Entry<String, Object> entry = entryIter.next();
			if (entry.getValue() != null)
				results.getGenericResults().put(entry.getKey().toLowerCase(), (String) entry.getValue());
		}
	}

}
