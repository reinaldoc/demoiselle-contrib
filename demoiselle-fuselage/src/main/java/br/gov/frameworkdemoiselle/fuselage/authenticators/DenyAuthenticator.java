package br.gov.frameworkdemoiselle.fuselage.authenticators;

public class DenyAuthenticator implements AuthenticatorModule {

	private AuthenticatorResults results = new AuthenticatorResults();

	public boolean authenticate(String username, String password) {
		results = new AuthenticatorResults();
		results.setAuthenticatorModuleName(getClass().getSimpleName());
		return false;
	}

	@Override
	public AuthenticatorResults getResults() {
		return results;
	}

}
