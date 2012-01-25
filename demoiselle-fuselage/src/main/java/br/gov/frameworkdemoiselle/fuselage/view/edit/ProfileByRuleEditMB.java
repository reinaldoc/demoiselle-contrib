package br.gov.frameworkdemoiselle.fuselage.view.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.ProfileByRuleBC;
import br.gov.frameworkdemoiselle.fuselage.configuration.ViewConfig;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfileByRule;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractEditPageBean;
import br.gov.frameworkdemoiselle.util.Faces;

@ViewController
public class ProfileByRuleEditMB extends AbstractEditPageBean<SecurityProfileByRule, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProfileByRuleBC bc;

	@Inject
	private ViewConfig viewConfig;

	@Override
	public String insert() {
		try {
			getBean().setAvailable(1);
			bc.insert(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.insert.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.insert.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public String update() {
		try {
			bc.update(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.update.success", getBean().getName()));
		} catch (RuntimeException e) {
			e.printStackTrace();
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.update.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public String delete() {
		try {
			bc.delete(getBean().getId());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.delete.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.delete.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public SecurityProfileByRule load(Long id) {
		try {
			return bc.load(id);
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.load.failed", SeverityType.ERROR));
		}
		return new SecurityProfileByRule();
	}

	public String disable() {
		try {
			bc.disable(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.disable.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.disable.failed", SeverityType.ERROR));
		}
		return null;
	}

	public String enable() {
		try {
			bc.enable(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.enable.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profilerule.enable.failed", SeverityType.ERROR));
		}
		return null;
	}

	public List<String> getImplementationList() {
		List<String> implList = new ArrayList<String>(viewConfig.getImplementations().keySet());
		Collections.sort(implList);
		return implList;
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