package br.gov.frameworkdemoiselle.fuselage.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.fuselage.authenticators.AuthenticatorModule;
import br.gov.frameworkdemoiselle.fuselage.authenticators.AuthenticatorResults;
import br.gov.frameworkdemoiselle.fuselage.business.ProfileByRuleBC;
import br.gov.frameworkdemoiselle.fuselage.configuration.AuthenticatorConfig;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfileByRule;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.util.Beans;
import br.gov.frameworkdemoiselle.util.ResourceBundle;
import br.gov.frameworkdemoiselle.util.contrib.Faces;

@SessionScoped
public class Authenticator implements br.gov.frameworkdemoiselle.security.Authenticator {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerProducer.create(Authenticator.class);

	@Inject
	private ResourceBundle bundle;

	private User user;

	@Inject
	private Credential credential;

	@Inject
	private AuthenticatorConfig authenticatorConfig;

	private AuthenticatorResults authResults;

	@Inject
	private ProfileByRuleBC profileByRuleBC;

	@Override
	public void unAuthenticate() {
		user = null;
		credential.clear();
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public boolean authenticate() {
		user = null;
		authResults = callAuthenticationModules();
		if (authResults.isLoggedIn()) {
			user = new User();
			user.setAttribute("user", authResults.getSecurityUser());
			user.setAttribute("user_detail", authResults.getAttribute());
			user.setId(authResults.getSecurityUser().getName());
			setUserPermissions(authResults.getSecurityUser());
			return true;
		}
		Faces.validationFailed();
		return false;
	}

	private AuthenticatorResults callAuthenticationModules() {

		AuthenticatorResults authenticatorResults = new AuthenticatorResults();
		if (authenticatorConfig.getAuthenticators() == null || authenticatorConfig.getAuthenticators().size() == 0) {
			logger.info(bundle.getString("fuselage.authenticators.unavailable"));
			return authenticatorResults;
		}

		for (String module : authenticatorConfig.getAuthenticators()) {
			try {
				AuthenticatorModule authenticatorModule = (AuthenticatorModule) Beans.getReference(Class
						.forName("br.gov.frameworkdemoiselle.fuselage.authenticators." + module));
				if (authenticatorModule.authenticate(credential.getUsername(), credential.getPassword()))
					return authenticatorModule.getResults();

				if (authenticatorModule.getResults().isUserUnavailable())
					return authenticatorResults;
			} catch (RuntimeException e) {
				logger.info(bundle.getString("fuselage.authenticators.moduleunavailable", module));
				e.printStackTrace();
			} catch (Exception e) {
				logger.info(bundle.getString("fuselage.authenticators.moduleunavailable", module));
				e.printStackTrace();
			}
		}

		return authenticatorResults;
	}

	private void setUserPermissions(SecurityUser userLoad) {
		/*
		 * Set all profiles to admin
		 */
		if (authResults.getAttribute().containsKey("admin")) {
			setPermissionsByProfiles(profileByRuleBC.getProfiles(), false);
			return;
		}

		/*
		 * SecurityUser.Profiles
		 */
		setPermissionsByProfiles(userLoad.getProfiles(), true);

		/*
		 * SecurityProfileDetect.implementation.ALL-LOGGED-IN
		 */
		List<SecurityProfileByRule> allLoggedIn = profileByRuleBC.findByImplementation("ALL-LOGGED-IN");
		if (allLoggedIn != null)
			for (SecurityProfileByRule profileDetect : allLoggedIn)
				setPermissionsByProfiles(profileDetect.getProfiles(), true);

		/*
		 * LdapAuthenticator
		 */
		if ("LdapAuthenticator".equals(authResults.getAuthenticatorModuleName())) {
			/*
			 * SecurityProfileDetect.implementation.LDAP-USER-ATTR
			 */
			List<SecurityProfileByRule> ldapUserAttr = profileByRuleBC.findByImplementation("LDAP-USER-ATTR");
			if (ldapUserAttr != null) {
				for (SecurityProfileByRule profileDetect : ldapUserAttr) {
					if (profileDetect.getKeyname() != null && profileDetect.getValue() != null && profileDetect.getValuenotation() != null) {
						if (profileDetect.getValuenotation().equalsIgnoreCase("EXACT")) {
							if (profileDetect.getValue().equalsIgnoreCase(
									((String[]) authResults.getAttribute().get(profileDetect.getKeyname().toLowerCase()))[0]))
								setPermissionsByProfiles(profileDetect.getProfiles(), true);
						} else if (profileDetect.getValuenotation().equalsIgnoreCase("CONTAINS")) {
							String value = ((String[]) authResults.getAttribute().get(profileDetect.getKeyname().toLowerCase()))[0];
							if (value != null && value.toLowerCase().contains(profileDetect.getValue().toLowerCase()))
								setPermissionsByProfiles(profileDetect.getProfiles(), true);
						} else
							logger.info("setUserPermissions()->Notation not implemented: " + profileDetect.getValuenotation());
					}

				}
			}

			/*
			 * SecurityProfileDetect.implementation.LDAP-USER-DN
			 */
			List<SecurityProfileByRule> ldapUserDn = profileByRuleBC.findByImplementation("LDAP-USER-DN");
			String dn = ((String[]) authResults.getAttribute().get("dn"))[0];
			if (ldapUserDn != null && dn != null)
				for (SecurityProfileByRule profileDetect : ldapUserDn)
					if (profileDetect.getValue() != null && profileDetect.getValuenotation() != null)
						if (profileDetect.getValuenotation().equalsIgnoreCase("EXACT")) {
							if (dn.equals(profileDetect.getValue()))
								setPermissionsByProfiles(profileDetect.getProfiles(), true);
						} else if (profileDetect.getValuenotation().equalsIgnoreCase("CONTAINS")) {
							if (dn.contains(profileDetect.getValue()))
								setPermissionsByProfiles(profileDetect.getProfiles(), true);
						}

			/*
			 * Add LDAP User groups as Role: LDAP-GROUP:GROUPNAME
			 */
			Set<String> roles = new HashSet<String>();
			String[] member = (String[]) authResults.getAttribute().get("memberOf");
			if (member != null) {
				for (String group : member) {
					roles.add("LDAPGROUP:"+group.split(",")[0].split("=")[1]);
					System.out.println("===> " + group.split(",")[0].split("=")[1]);
				}
				user.addAllAttribute("roles", roles);
			}

		}
	}

	private void setPermissionsByProfiles(List<SecurityProfile> profiles, boolean includeRestrictiveRole) {
		if (profiles != null)
			for (SecurityProfile profile : profiles)
				setPermissionsByProfile(profile, includeRestrictiveRole);
	}

	private void setPermissionsByProfile(SecurityProfile profile, boolean includeRestrictiveRole) {
		setUserWelcomePage(profile);

		Set<String> roles = new HashSet<String>();
		Set<String> rolesNames = new HashSet<String>();
		Map<String, String> resourceMap = new HashMap<String, String>();

		List<SecurityRole> roleList = profile.getRoles();
		if (roleList != null) {
			for (SecurityRole role : roleList) {
				if (!includeRestrictiveRole && role.getRestriction().booleanValue())
					continue;
				roles.add(role.getName());
				rolesNames.add(role.getShortDescription());
				List<SecurityResource> resourceList = role.getResources();
				if (resourceList != null)
					for (SecurityResource resource : resourceList)
						resourceMap.put(resource.getValue(), resource.getName());
			}
		}

		if (profile.getName() != null)
			user.addAttribute("profile_names", profile.getName());
		user.addAllAttribute("role_names", rolesNames);
		user.addAllAttribute("roles", roles);
		user.putAllAttribute("resources", resourceMap);
	}

	private void setUserWelcomePage(SecurityProfile profile) {
		if (profile != null) {
			Long welcomePriority = profile.getWelcomePagePriority();
			Long lastWelcomePriotiry = (Long) user.getAttribute("welcome_page_priority");

			if (welcomePriority != null && welcomePriority.intValue() > 0)
				if (lastWelcomePriotiry == null || lastWelcomePriotiry.intValue() > welcomePriority.intValue())
					if (profile.getWelcomePage() != null && profile.getWelcomePage().getValue() != null)
						if (profile.getWelcomePage().getName() != null && profile.getWelcomePage().getName().contains("url")) {
							user.setAttribute("welcome_page_priority", welcomePriority);
							user.setAttribute("welcome_page", profile.getWelcomePage().getValue());
						}
		}
	}
}
