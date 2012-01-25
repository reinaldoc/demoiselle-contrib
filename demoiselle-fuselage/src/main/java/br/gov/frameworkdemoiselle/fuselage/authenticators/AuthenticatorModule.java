package br.gov.frameworkdemoiselle.fuselage.authenticators;

public interface AuthenticatorModule {

	public String getModuleName();

	public boolean authenticate(String username, String password);

	public AuthenticatorResults getResults();

}
