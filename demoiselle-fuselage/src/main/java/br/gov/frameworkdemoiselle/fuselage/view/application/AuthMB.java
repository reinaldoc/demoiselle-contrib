package br.gov.frameworkdemoiselle.fuselage.view.application;

import java.io.Serializable;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.transaction.Transactional;

@ViewController
public class AuthMB implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private SecurityContext securityContext;

	@Transactional
	public String login() {
		securityContext.login();
		return null;
	}

	public String logout() {
		securityContext.logout();
		return null;
	}

}
