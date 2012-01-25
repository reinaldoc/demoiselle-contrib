package br.gov.frameworkdemoiselle.fuselage.view.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.codec.digest.DigestUtils;

import br.gov.frameworkdemoiselle.fuselage.business.UserBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractEditPageBean;
import br.gov.frameworkdemoiselle.util.Faces;
import br.gov.frameworkdemoiselle.util.Strings;

@ViewController
public class UserEditMB extends AbstractEditPageBean<SecurityUser, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private UserBC bc;

	@Override
	public String insert() {
		try {
			if (Strings.isNotBlank(getBean().getPassword()) && getBean().getPassword().length() < 8) {
				Faces.validationFailed();
				Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.password.notstrong"));
				return null;
			}
			if (!bc.userAvailable(getBean().getLogin())) {
				Faces.validationFailed();
				Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.available.unavailable", getBean().getLogin()));
				return null;
			}
			if (Strings.isNotBlank(getBean().getPassword()) && getBean().getPassword().equals(getBean().getPasswordrepeat())) {
				getBean().setPassword(DigestUtils.sha512Hex(getBean().getPassword()));
			} else {
				Faces.validationFailed();
				Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.password.notmatch"));
				return null;
			}
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.available.failed", SeverityType.ERROR));
			return null;
		}

		getBean().setAvailable(1);

		try {
			bc.insert(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.insert.success", getBean().getLogin()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.insert.failed", SeverityType.ERROR));
		}

		return null;
	}

	@Override
	public String update() {
		try {
			if (Strings.isBlank(getBean().getPassword()) && Strings.isBlank(getBean().getPasswordrepeat())) {
				SecurityUser securityUser = bc.load(getBean().getId());
				getBean().setPassword(securityUser.getPassword());
			} else if (Strings.isNotBlank(getBean().getPassword()) && getBean().getPassword().length() < 8) {
				Faces.validationFailed();
				Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.password.notstrong"));
				return null;
			} else if (Strings.isNotBlank(getBean().getPassword()) && getBean().getPassword().equals(getBean().getPasswordrepeat())) {
				getBean().setPassword(DigestUtils.sha512Hex(getBean().getPassword()));
			} else {
				Faces.validationFailed();
				Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.password.notmatch"));
				return null;
			}
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.update.failed", SeverityType.ERROR));
			return null;
		}

		try {
			bc.update(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.update.success", getBean().getLogin()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.update.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public String delete() {
		try {
			bc.delete(getBean().getId());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.delete.success", getBean().getLogin()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.delete.failed", SeverityType.ERROR));
		}
		return null;
	}

	public String disable() {
		try {
			bc.disable(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.disable.success", getBean().getLogin()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.disable.failed", SeverityType.ERROR));
		}
		return null;
	}

	public String enable() {
		try {
			bc.enable(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.enable.success", getBean().getLogin()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.enable.failed", SeverityType.ERROR));
		}
		return null;
	}

	public String userAvailable() {
		try {
			if (bc.userAvailable(getBean().getLogin()))
				Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.available.success", getBean().getLogin()));
			else {
				Faces.validationFailed();
				Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.available.unavailable", getBean().getLogin()));
			}
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.available.failed", SeverityType.ERROR));
		}

		return null;
	}

	@Override
	public SecurityUser load(Long id) {
		try {
			return bc.load(id);
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.user.load.failed", SeverityType.ERROR));
		}
		return new SecurityUser();
	}

	/**
	 * Get all SecurityProfiles for datatable
	 * 
	 * @return list of all SecurityProfiles
	 */
	public List<SecurityProfile> getProfileList() {
		try {
			return bc.getProfiles();
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		return new ArrayList<SecurityProfile>();
	}

	/**
	 * Get SecurityProfiles from current bean as array for datatable selection
	 * 
	 * @return array of bean SecurityProfiles
	 */
	public SecurityProfile[] getProfileArray() {
		if (getBean().getProfiles() == null)
			return null;
		return getBean().getProfiles().toArray(new SecurityProfile[0]);
	}

	/**
	 * Set SecurityProfiles on current bean from datatable selection array
	 * 
	 * @param profiles
	 *            array of SecurityProfiles to set current bean
	 */
	public void setProfileArray(SecurityProfile[] profiles) {
		getBean().setProfiles(Arrays.asList(profiles));
	}

}
