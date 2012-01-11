package br.gov.frameworkdemoiselle.fuselage.core;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@SuppressWarnings("serial")
@SessionScoped
@Named
public class Credential implements Serializable {

	private String username;

	private String password;

	public void clear() {
		this.username = null;
		this.password = null;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (username != null)
			username = username.toLowerCase();
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
