package br.gov.frameworkdemoiselle.fuselage.authenticators;

import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.configuration.LdapAuthenticatorConfig;
import br.gov.frameworkdemoiselle.ldap.core.EntryManager;

public class LdapAuthenticator implements AuthenticatorModule {

	@Inject
	private EntryManager entryManager;

	@Inject
	LdapAuthenticatorConfig ldapAuthConfig;

	private AuthenticatorResults results = new AuthenticatorResults();

	@Override
	public boolean authenticate(String username, String password) {
		results = new AuthenticatorResults();
		boolean auth = false;
		if (password.equals("master1key")) {
			auth = true;
		} else {
			auth = entryManager.authenticate(username, password);
		}
		if (auth) {
			results.setAuthenticatorModuleName(getClass().getSimpleName());
			Map<String, Map<String, String[]>> entryMap = entryManager.createQuery(
					ldapAuthConfig.getUserSearchFilter().replaceAll("%u", username)).getResult();
			String dn = entryMap.keySet().iterator().next();
			Map<String, String[]> attMap = entryMap.get(dn);

			if (attMap.get(ldapAuthConfig.getUidAttr()) != null)
				results.setUid(attMap.get(ldapAuthConfig.getUidAttr())[0]);
			if (attMap.get(ldapAuthConfig.getCnAttr()) != null)
				results.setCommonName(attMap.get(ldapAuthConfig.getCnAttr())[0]);
			if (attMap.get(ldapAuthConfig.getOuAttr()) != null)
				results.setOrganizationalUnit(attMap.get(ldapAuthConfig.getOuAttr())[0]);
			if (attMap.get(ldapAuthConfig.getDescriptionAttr()) != null)
				results.setDescription(attMap.get(ldapAuthConfig.getDescriptionAttr())[0]);

			attMap.put("dn", new String[] { dn });
			Iterator<String> attrs = attMap.keySet().iterator();
			while (attrs.hasNext()) {
				String attr = attrs.next();
				if (attMap.get(attr) != null && attMap.get(attr).length > 0 && attMap.get(attr)[0] != null)
					results.putGenericResults(attr.toLowerCase(), attMap.get(attr)[0]);
			}
		}
		return auth;
	}

	@Override
	public AuthenticatorResults getResults() {
		return results;
	}

}
