package br.gov.frameworkdemoiselle.fuselage.view.edit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.ProfileBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractEditPageBean;
import br.gov.frameworkdemoiselle.util.Faces;

@ViewController
public class ProfileEditMB extends AbstractEditPageBean<SecurityProfile, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProfileBC bc;

	@Override
	public String insert() {
		try {
			bc.insert(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profile.insert.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profile.insert.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public String update() {
		try {
			bc.update(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profile.update.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profile.update.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public String delete() {
		try {
			bc.delete(getBean().getId());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profile.delete.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profile.delete.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public SecurityProfile load(Long id) {
		try {
			return bc.load(id);
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.profile.load.failed", SeverityType.ERROR));
		}
		return new SecurityProfile();
	}

	public List<Long> getResourcePriorities() {
		List<Long> priorities = new ArrayList<Long>();
		try {
			List<Long> usedPriorities = bc.getUsedPrioritiesExceptMyself(getBean());
			for (int i = 1; i < 100; i++)
				if (!usedPriorities.contains(new Long(i)))
					priorities.add(new Long(i));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		return priorities;
	}

	/**
	 * Get all SecurityResources for selectOneMenu to select welcome page
	 * 
	 * @return list of all SecurityResources
	 */
	public List<SecurityResource> getResourceList() {
		try {
			return bc.getResources();
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		return new ArrayList<SecurityResource>();
	}

	/**
	 * Get all SecurityRoles for datatable
	 * 
	 * @return list of all SecurityRoles
	 */
	public List<SecurityRole> getRoleList() {
		try {
			return bc.getRoles();
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		return new ArrayList<SecurityRole>();
	}

	/**
	 * Get SecurityRoles from current bean as array for datatable selection
	 * 
	 * @return array of bean SecurityRoles
	 */
	public SecurityRole[] getRoleArray() {
		if (getBean().getRoles() == null)
			return null;
		return getBean().getRoles().toArray(new SecurityRole[0]);
	}

	/**
	 * Set SecurityRoles on current bean from datatable selection array
	 * 
	 * @param roles
	 *            array of SecurityRoles to set current bean
	 */
	public void setRoleArray(SecurityRole[] roles) {
		getBean().setRoles(Arrays.asList(roles));
	}

}