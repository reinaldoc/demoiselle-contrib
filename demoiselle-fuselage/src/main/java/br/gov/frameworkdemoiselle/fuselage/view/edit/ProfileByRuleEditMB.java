package br.gov.frameworkdemoiselle.fuselage.view.edit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.ProfileByRuleBC;
import br.gov.frameworkdemoiselle.fuselage.configuration.ViewConfig;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfileByRule;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.contrib.AbstractEditPageBean;
import br.gov.frameworkdemoiselle.util.contrib.Faces;

@ViewController
public class ProfileByRuleEditMB extends AbstractEditPageBean<SecurityProfileByRule, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProfileByRuleBC bc;

	private List<SecurityProfile> profiles;

	private List<SecurityProfile> selectedProfiles = new ArrayList<SecurityProfile>();

	@Inject
	private ViewConfig viewConfig;

	@Override
	public String insert() {
		try {
			getBean().setAvailable(1);
			bc.insert(getBean());
			Faces.addI18nMessage("fuselage.profilerule.insert.success", getBean().getName());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.profilerule.insert.failed", SeverityType.ERROR);
		}
		return null;
	}

	@Override
	public String update() {
		try {
			bc.update(getBean());
			Faces.addI18nMessage("fuselage.profilerule.update.success", getBean().getName());
		} catch (RuntimeException e) {
			e.printStackTrace();
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.profilerule.update.failed", SeverityType.ERROR);
		}
		return null;
	}

	@Override
	public String delete() {
		try {
			bc.delete(getBean().getId());
			Faces.addI18nMessage("fuselage.profilerule.delete.success", getBean().getName());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.profilerule.delete.failed", SeverityType.ERROR);
		}
		return null;
	}

	@Override
	public SecurityProfileByRule load(Long id) {
		try {
			return bc.load(id);
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.profilerule.load.failed", SeverityType.ERROR);
		}
		return new SecurityProfileByRule();
	}

	public String disable() {
		try {
			bc.disable(getBean());
			Faces.addI18nMessage("fuselage.profilerule.disable.success", getBean().getName());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.profilerule.disable.failed", SeverityType.ERROR);
		}
		return null;
	}

	public String enable() {
		try {
			bc.enable(getBean());
			Faces.addI18nMessage("fuselage.profilerule.enable.success", getBean().getName());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.profilerule.enable.failed", SeverityType.ERROR);
		}
		return null;
	}

	public List<String> getImplementationList() {
		List<String> implList = new ArrayList<String>(viewConfig.getImplementations().keySet());
		Collections.sort(implList);
		return implList;
	}

	/**
	 * Get all profiles except already in bean
	 * 
	 * @return list of all SecurityProfiles
	 */
	public List<SecurityProfile> getProfileList() {
		try {
			if (profiles == null)
				profiles = bc.getProfilesExceptList(getBean().getProfiles());
			return profiles;
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.generic.business.error", SeverityType.ERROR);
		}
		return new ArrayList<SecurityProfile>();
	}

	public void clearProfileList() {
		profiles = null;
		selectedProfiles.clear();
	}

	public void unselectProfile(SecurityProfile securityProfile) {
		getBean().getProfiles().remove(securityProfile);
	}

	public void selectProfiles() {
		if (getBean().getProfiles() == null)
			getBean().setProfiles(new ArrayList<SecurityProfile>(selectedProfiles));
		else
			getBean().getProfiles().addAll(selectedProfiles);
	}

	/**
	 * Get SecurityProfiles from current bean as array for datatable selection
	 * 
	 * @return array of bean SecurityProfiles
	 */
	public SecurityProfile[] getProfileArray() {
		return selectedProfiles.toArray(new SecurityProfile[0]);
	}

	/**
	 * Set SecurityProfiles on current bean from datatable selection array
	 * 
	 * @param selectedProfilesArray
	 *            array of SecurityProfiles to set current bean
	 */
	public void setProfileArray(SecurityProfile[] selectedProfilesArray) {
		for (SecurityProfile profile : selectedProfilesArray)
			if (!selectedProfiles.contains(profile))
				selectedProfiles.add(profile);
	}

}