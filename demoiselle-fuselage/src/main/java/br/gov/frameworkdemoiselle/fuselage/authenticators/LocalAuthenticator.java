package br.gov.frameworkdemoiselle.fuselage.authenticators;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;

import br.gov.frameworkdemoiselle.fuselage.business.UserBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.util.Strings;

public class LocalAuthenticator extends AbstractAuthenticatorModule<LocalAuthenticator> {

	@Inject
	private UserBC userBC;

	private AuthenticatorResults results = new AuthenticatorResults();

	public AuthenticatorResults getResults() {
		return results;
	}

	public boolean authenticate(String username, String password) {
		results = new AuthenticatorResults();
		results.setAuthenticatorModuleName(getModuleName());
		results.setLoggedIn(login(username, password));
		if (results.isLoggedIn())
			getLogger().info(getBundle().getString("fuselage.authenticators.login.success", username, getModuleName()));
		else
			getLogger().info(getBundle().getString("fuselage.authenticators.login.failed", username, getModuleName()));
		if (results.isUserUnavailable())
			getLogger().info(getBundle().getString("fuselage.authenticators.login.unavailable", username, getModuleName()));
		return results.isLoggedIn();
	}

	private boolean login(String username, String password) {
		if (Strings.isBlank(username) || Strings.isBlank(password))
			return false;

		SecurityUser securityUser = userBC.loadByLogin(username);
		results.setSecurityUser(securityUser);

		if (securityUser.getId() != null && !securityUser.isEnabled()) {
			results.setUserUnavailable(true);
			return false;
		}

		if (DigestUtils.sha512Hex(password).equals(securityUser.getPassword())) {
			return true;
		}
		return false;
	}

}
