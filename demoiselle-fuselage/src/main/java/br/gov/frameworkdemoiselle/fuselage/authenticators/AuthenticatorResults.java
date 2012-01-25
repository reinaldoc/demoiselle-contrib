package br.gov.frameworkdemoiselle.fuselage.authenticators;

import java.util.HashMap;
import java.util.Map;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;

public class AuthenticatorResults {

	public String authenticatorModuleName;

	public SecurityUser securityUser;

	public boolean loggedIn = false;

	public boolean userUnavailable = false;

	public Map<String, String> genericResults = new HashMap<String, String>();

	public String getAuthenticatorModuleName() {
		return authenticatorModuleName;
	}

	public void setAuthenticatorModuleName(String authenticatorModuleName) {
		this.authenticatorModuleName = authenticatorModuleName;
	}

	public SecurityUser getSecurityUser() {
		return securityUser;
	}

	public void setSecurityUser(SecurityUser securityUser) {
		this.securityUser = securityUser;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public boolean isUserUnavailable() {
		return userUnavailable;
	}

	public void setUserUnavailable(boolean userUnavailable) {
		this.userUnavailable = userUnavailable;
	}

	public Map<String, String> getGenericResults() {
		return genericResults;
	}

	public void setGenericResults(Map<String, String> genericResults) {
		this.genericResults = genericResults;
	}

}
