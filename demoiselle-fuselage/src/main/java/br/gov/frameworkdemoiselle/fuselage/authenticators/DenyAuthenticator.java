package br.gov.frameworkdemoiselle.fuselage.authenticators;

public class DenyAuthenticator extends AbstractAuthenticatorModule<DenyAuthenticator> {

	private AuthenticatorResults results = new AuthenticatorResults();

	public AuthenticatorResults getResults() {
		return results;
	}

	public boolean authenticate(String username, String password) {
		results = new AuthenticatorResults();
		results.setAuthenticatorModuleName(getModuleName());
		results.setUserUnavailable(true);
		getLogger().info(getBundle().getString("fuselage.authenticators.login.failed", username, getModuleName()));
		return false;
	}

}
