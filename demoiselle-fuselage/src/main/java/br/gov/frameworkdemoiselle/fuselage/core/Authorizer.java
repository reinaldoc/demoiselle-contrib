package br.gov.frameworkdemoiselle.fuselage.core;

import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.security.SecurityContext;

@Alternative
public class Authorizer implements br.gov.frameworkdemoiselle.security.Authorizer {

	private static final long serialVersionUID = 1L;

	@Inject
	private SecurityContext securityContext;

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasRole(String role) {
		try {
			Set<String> roleSet = ((Set<String>) securityContext.getUser().getAttribute("roles"));
			if (roleSet.contains(role))
				return true;
		} catch (Exception e) {
			// Ignore
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean hasPermission(String resource, String operation) {
		try {
			Map<String, String> resourceMap = ((Map<String, String>) securityContext.getUser().getAttribute("resources"));
			if (resource.equals(resourceMap.get(operation)))
				return true;
		} catch (Exception e) {
			// Ignore
		}
		return false;
	}
}
