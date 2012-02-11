package br.gov.frameworkdemoiselle.fuselage.view.edit;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.UserBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.contrib.AbstractEditPageBean;
import br.gov.frameworkdemoiselle.util.contrib.Faces;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@ViewController
public class UserEditMB extends AbstractEditPageBean<SecurityUser, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserBC bc;

	private List<SecurityProfile> profiles;

	private List<SecurityProfile> selectedProfiles = new ArrayList<SecurityProfile>();

	@Override
	public String insert() {
		try {
			if (getBean().getPassword().length() < 8) {
				Faces.validationFailed();
				Faces.addI18nMessage("fuselage.user.password.notstrong");
				return null;
			}
			if (!bc.userAvailable(getBean().getLogin())) {
				Faces.validationFailed();
				Faces.addI18nMessage("fuselage.user.available.unavailable", getBean().getLogin());
				return null;
			}
			if (!getBean().getPassword().equals(getBean().getPasswordrepeat())) {
				Faces.validationFailed();
				Faces.addI18nMessage("fuselage.user.password.notmatch");
				return null;
			}
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.user.available.failed", SeverityType.ERROR);
			return null;
		}

		getBean().setPasswordhash();
		getBean().setAvailable(1);

		try {
			bc.insert(getBean());
			Faces.addI18nMessage("fuselage.user.insert.success", getBean().getLogin());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.user.insert.failed", SeverityType.ERROR);
		}

		return null;
	}

	@Override
	public String update() {
		try {
			if (Strings.isNotBlank(getBean().getPassword()) || Strings.isNotBlank(getBean().getPasswordrepeat())) {
				if (Strings.isNotBlank(getBean().getPassword()) && getBean().getPassword().length() < 8) {
					Faces.validationFailed();
					Faces.addI18nMessage("fuselage.user.password.notstrong");
					return null;
				} else if (Strings.isNotBlank(getBean().getPassword()) && !getBean().getPassword().equals(getBean().getPasswordrepeat())) {
					Faces.validationFailed();
					Faces.addI18nMessage("fuselage.user.password.notmatch");
					return null;
				}
				getBean().setPasswordhash();
			}
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.user.update.failed", SeverityType.ERROR);
			return null;
		}

		try {
			bc.update(getBean());
			Faces.addI18nMessage("fuselage.user.update.success", getBean().getLogin());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.user.update.failed", SeverityType.ERROR);
		}
		return null;
	}

	@Override
	public String delete() {
		try {
			bc.delete(getBean().getId());
			Faces.addI18nMessage("fuselage.user.delete.success", getBean().getLogin());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.user.delete.failed", SeverityType.ERROR);
		}
		return null;
	}

	public String disable() {
		try {
			bc.disable(getBean());
			Faces.addI18nMessage("fuselage.user.disable.success", getBean().getLogin());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.user.disable.failed", SeverityType.ERROR);
		}
		return null;
	}

	public String enable() {
		try {
			bc.enable(getBean());
			Faces.addI18nMessage("fuselage.user.enable.success", getBean().getLogin());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.user.enable.failed", SeverityType.ERROR);
		}
		return null;
	}

	public String userAvailable() {
		try {
			if (bc.userAvailable(getBean().getLogin()))
				Faces.addI18nMessage("fuselage.user.available.success", getBean().getLogin());
			else {
				Faces.validationFailed();
				Faces.addI18nMessage("fuselage.user.available.unavailable", getBean().getLogin());
			}
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.user.available.failed", SeverityType.ERROR);
		}

		return null;
	}

	@Override
	public SecurityUser load(Long id) {
		try {
			return bc.load(id);
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.user.load.failed", SeverityType.ERROR);
		}
		return new SecurityUser();
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
			getBean().setProfiles(selectedProfiles);
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
	 * @param profiles
	 *            array of SecurityProfiles to set current bean
	 */
	public void setProfileArray(SecurityProfile[] selectedProfilesArray) {
		for (SecurityProfile profile : selectedProfilesArray)
			if (!selectedProfiles.contains(profile))
				selectedProfiles.add(profile);
	}

}
