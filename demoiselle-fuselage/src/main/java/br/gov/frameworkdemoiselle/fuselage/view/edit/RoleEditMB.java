package br.gov.frameworkdemoiselle.fuselage.view.edit;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.RoleBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.contrib.AbstractEditPageBean;
import br.gov.frameworkdemoiselle.util.contrib.Faces;

@ViewController
public class RoleEditMB extends AbstractEditPageBean<SecurityRole, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private RoleBC bc;

	@Override
	public String insert() {
		try {
			bc.insert(getBean());
			Faces.addI18nMessage("fuselage.role.insert.success", getBean().getName());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.role.insert.failed", SeverityType.ERROR);
		}
		return null;
	}

	@Override
	public String update() {
		try {
			bc.update(getBean());
			Faces.addI18nMessage("fuselage.role.update.success", getBean().getName());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.role.update.failed", SeverityType.ERROR);
		}
		return null;
	}

	@Override
	public String delete() {
		try {
			bc.delete(getBean().getId());
			Faces.addI18nMessage("fuselage.role.delete.success", getBean().getName());
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.role.delete.failed", SeverityType.ERROR);
		}
		return null;
	}

	@Override
	public SecurityRole load(Long id) {
		try {
			return bc.load(id);
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.role.load.failed", SeverityType.ERROR);
		}
		return new SecurityRole();
	}

	/**
	 * Get all resources except already in bean
	 */
	public List<SecurityResource> getResourceList() {
		return bc.getResourcesExceptList(getBean().getResources());
	}

	public void unselectResource(SecurityResource securityResource) {
		getBean().getResources().remove(securityResource);
	}

	/**
	 * null array for datatable selection
	 */
	public SecurityResource[] getResourceArray() {
		return null;
	}

	/**
	 * Set SecurityResources on current bean from datatable selection array
	 * 
	 * @param selectedResources
	 *            array of SecurityResources to set current bean
	 */
	public void setResourceArray(SecurityResource[] selectedResources) {
		if (selectedResources == null || selectedResources.length == 0)
			getBean().setResources(null);
		List<SecurityResource> securityResources = new ArrayList<SecurityResource>();
		for (SecurityResource securityResource : selectedResources)
			securityResources.add(securityResource);
		if (getBean().getResources() == null)
			getBean().setResources(securityResources);
		else
			getBean().getResources().addAll(securityResources);
	}

}