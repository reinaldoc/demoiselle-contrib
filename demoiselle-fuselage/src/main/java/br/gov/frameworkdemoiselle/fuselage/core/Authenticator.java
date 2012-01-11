package br.gov.frameworkdemoiselle.fuselage.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.authenticators.AuthenticatorResults;
import br.gov.frameworkdemoiselle.fuselage.authenticators.LdapAuthenticator;
import br.gov.frameworkdemoiselle.fuselage.authenticators.LocalAuthenticator;
import br.gov.frameworkdemoiselle.fuselage.business.ProfileDetectBC;
import br.gov.frameworkdemoiselle.fuselage.business.UserBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfileDetect;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;

@Alternative
public class Authenticator implements br.gov.frameworkdemoiselle.security.Authenticator {

	private static final long serialVersionUID = 1L;

	private User user;

	@Inject
	private Credential credential;

	@Inject
	private LocalAuthenticator localAuthenticator;

	@Inject
	private LdapAuthenticator ldapAuthenticator;

	private AuthenticatorResults authResults = new AuthenticatorResults();

	@Inject
	private UserBC userBC;

	@Inject
	private ProfileDetectBC profileDetectBC;

	@Override
	public void unAuthenticate() {
		this.user = null;
		credential.clear();
	}

	@Override
	public User getUser() {
		return user;
	}

	@Override
	public boolean authenticate() {
		user = null;
		authResults = new AuthenticatorResults();
		SecurityUser authUser = callAuthenticationModules();
		if (authUser != null) {
			user = new User();
			user.setId(authUser.getName());
			user.setAttribute("user", authUser);
			setUserPermissions(authUser);
			return true;
		}
		return false;
	}

	private SecurityUser callAuthenticationModules() {
		SecurityUser authUser = null;
		boolean auth = localAuthenticator.authenticate(credential.getUsername(), credential.getPassword());
		authResults = localAuthenticator.getResults();
		if (!auth) {
			auth = ldapAuthenticator.authenticate(credential.getUsername(), credential.getPassword());
			if (auth) {
				authResults = ldapAuthenticator.getResults();
				authUser = new SecurityUser();
				authUser.setLogin(credential.getUsername());
				authUser.setName(authResults.getCommonName());
				authUser.setOrgunit(authResults.getOrganizationalUnit());
				authUser.setDescription(authResults.getDescription());
				authUser = userBC.loadAndUpdate(authUser);
			}
		}
		return authUser;
	}

	private void setUserPermissions(SecurityUser userLoad) {
		/*
		 * SecurityUser.Profiles
		 */
		setPermissionsByProfiles(userLoad.getProfiles());

		/*
		 * SecurityProfileDetect.implementation.ALL-LOGGED-IN
		 */
		List<SecurityProfileDetect> allLoggedIn = profileDetectBC.findByImplementation("ALL-LOGGED-IN");
		if (allLoggedIn != null) {
			for (SecurityProfileDetect profileDetect : allLoggedIn) {
				setPermissionsByProfiles(profileDetect.getProfiles());
				setUserWelcomePage(profileDetect);
			}
		}

		/*
		 * LdapAuthenticator
		 */
		if (ldapAuthenticator.getClass().getSimpleName().equals(authResults.getAuthenticatorModuleName())) {
			/*
			 * SecurityProfileDetect.implementation.LDAP-USER-ATTR
			 */
			List<SecurityProfileDetect> ldapUserAttr = profileDetectBC.findByImplementation("LDAP-USER-ATTR");
			if (ldapUserAttr != null) {
				for (SecurityProfileDetect profileDetect : ldapUserAttr) {
					if (profileDetect.getKeyName() != null && profileDetect.getValue() != null && profileDetect.getValuenotation() != null) {
						if (profileDetect.getValuenotation().equalsIgnoreCase("EXACT")) {
							if (profileDetect.getValue().equalsIgnoreCase(authResults.getGenericResults().get(profileDetect.getKeyName().toLowerCase()))) {
								setPermissionsByProfiles(profileDetect.getProfiles());
								setUserWelcomePage(profileDetect);
							}
						} else if (profileDetect.getValuenotation().equalsIgnoreCase("CONTAINS")) {
							String value = authResults.getGenericResults().get(profileDetect.getKeyName().toLowerCase());
							if (value != null && value.toLowerCase().contains(profileDetect.getValue().toLowerCase())) {
								setPermissionsByProfiles(profileDetect.getProfiles());
								setUserWelcomePage(profileDetect);
							}
						} else {
							System.out.println("Authenticator.setUserPermissions(), Notation not implemented: " + profileDetect.getValuenotation());
						}
					}

				}
			}
			
			/*
			 * SecurityProfileDetect.implementation.LDAP-USER-DN
			 */
			List<SecurityProfileDetect> ldapUserDn = profileDetectBC.findByImplementation("LDAP-USER-DN");
			if (ldapUserDn != null) {
				for (SecurityProfileDetect profileDetect : ldapUserDn) {
					if (profileDetect.getValue() != null && profileDetect.getValuenotation() != null) {
						if (profileDetect.getValuenotation().equalsIgnoreCase("EXACT-PARENT")) {
							String dn = authResults.getGenericResults().get("dn");
							if (dn != null && dn.contains(","+profileDetect.getValue()+",")) {
								setPermissionsByProfiles(profileDetect.getProfiles());
								setUserWelcomePage(profileDetect);
							}
						}
					}
				}
			}


		}
	}

	private void setPermissionsByProfiles(List<SecurityProfile> profiles) {
		if (profiles != null) {
			for (SecurityProfile profile : profiles) {
				setPermissionsByProfile(profile);
			}
		}
	}

	private void setPermissionsByProfile(SecurityProfile profile) {
		Set<String> roles = new HashSet<String>();
		Set<String> rolesNames = new HashSet<String>();
		Map<String, String> resourceMap = new HashMap<String, String>();

		List<SecurityRole> roleList = profile.getRoles();
		if (roleList != null) {
			for (SecurityRole role : roleList) {
				roles.add(role.getName());
				rolesNames.add(role.getHumanName());
				List<SecurityResource> resourceList = role.getResource();
				if (resourceList != null) {
					for (SecurityResource resource : resourceList)
						resourceMap.put(resource.getValue(), resource.getName());
				}
			}
		}

		if (profile.getName() != null) {
			user.addAttribute("profile_names", profile.getName());
		}
		user.addAllAttribute("role_names", rolesNames);
		user.addAllAttribute("roles", roles);
		user.putAllAttribute("resources", resourceMap);
	}

	private void setUserWelcomePage(SecurityProfileDetect detect) {
//		if (detect != null) {
//			if (user.getAttribute("welcome_page_priority") != null) {
//				//Object priority
//			}
//			
//			
//			Long resourcePriority = detect.getResourcePriority();
//		}
	}

}
