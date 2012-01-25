package br.gov.frameworkdemoiselle.fuselage.view.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.util.Faces;
import br.gov.frameworkdemoiselle.util.ResourceBundle;

@ViewController
public class PermissionsListMB {

	@Inject
	private ResourceBundle bundle;

	@Inject
	private SecurityContext securityContext;

	public SecurityUser getUser() {
		try {
			return ((SecurityUser) securityContext.getUser().getAttribute("user"));
		} catch (RuntimeException e) {
		} catch (Exception e) {
		}
		Faces.validationFailed();
		Faces.addMessage(bundle.getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		return new SecurityUser();
	}

	@SuppressWarnings("unchecked")
	public List<String> getProfileList() {
		List<String> listResult = new ArrayList<String>();
		try {
			Set<String> setResult = ((Set<String>) securityContext.getUser().getAttribute("profile_names"));
			listResult = Arrays.asList(setResult.toArray(new String[] {}));
		} catch (Exception e) {
			Faces.validationFailed();
			Faces.addMessage(bundle.getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		Collections.sort(listResult);
		return listResult;
	}

	@SuppressWarnings("unchecked")
	public List<String> getRolesList() {
		List<String> listResult = new ArrayList<String>();
		try {
			Set<String> setResult = ((Set<String>) securityContext.getUser().getAttribute("roles"));
			listResult = Arrays.asList(setResult.toArray(new String[] {}));
		} catch (Exception e) {
			Faces.validationFailed();
			Faces.addMessage(bundle.getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		Collections.sort(listResult);
		return listResult;
	}

	@SuppressWarnings("unchecked")
	public List<String> getRolesNamesList() {
		List<String> listResult = new ArrayList<String>();
		try {
			Set<String> setResult = ((Set<String>) securityContext.getUser().getAttribute("role_names"));
			listResult = Arrays.asList(setResult.toArray(new String[] {}));
		} catch (Exception e) {
			Faces.validationFailed();
			Faces.addMessage(bundle.getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		Collections.sort(listResult);
		return listResult;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getResources() {
		Map<String, String> resourceMap = new HashMap<String, String>();
		try {
			resourceMap = ((Map<String, String>) securityContext.getUser().getAttribute("resources"));
		} catch (Exception e) {
			Faces.validationFailed();
			Faces.addMessage(bundle.getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		return resourceMap;
	}

	@SuppressWarnings("unchecked")
	public List<String> getResourcesList() {
		List<String> listResult = new ArrayList<String>();
		try {
			listResult = Arrays.asList(((Map<String, String>) securityContext.getUser().getAttribute("resources")).keySet().toArray(new String[] {}));
		} catch (Exception e) {
			Faces.validationFailed();
			Faces.addMessage(bundle.getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		Collections.sort(listResult);
		return listResult;
	}
}
