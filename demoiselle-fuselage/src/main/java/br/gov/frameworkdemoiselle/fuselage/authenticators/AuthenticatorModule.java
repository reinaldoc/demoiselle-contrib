package br.gov.frameworkdemoiselle.fuselage.authenticators;

public interface AuthenticatorModule {
	public boolean authenticate(String username, String password);
	
	public AuthenticatorResults getResults();

}
