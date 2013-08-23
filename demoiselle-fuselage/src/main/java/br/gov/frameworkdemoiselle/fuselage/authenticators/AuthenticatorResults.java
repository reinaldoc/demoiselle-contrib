package br.gov.frameworkdemoiselle.fuselage.authenticators;

import java.util.HashMap;
import java.util.Map;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;

public class AuthenticatorResults {

	public String authenticatorModuleName;

	public SecurityUser securityUser;

	public boolean loggedIn = false;

	public boolean userUnavailable = false;

	public Map<String, Object> attribute = new HashMap<String, Object>();

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

	public Map<String, Object> getAttribute() {
		return attribute;
	}

	public void setAttribute(Map<String, Object> attribute) {
		this.attribute = attribute;
	}

}
