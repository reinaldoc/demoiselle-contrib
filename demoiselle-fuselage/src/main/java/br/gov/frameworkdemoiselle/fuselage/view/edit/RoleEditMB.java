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

	private List<SecurityResource> resources;

	private List<SecurityResource> selectedResources = new ArrayList<SecurityResource>();

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
		try {
			if (resources == null)
				resources = bc.getResourcesExceptList(getBean().getResources());
			return resources;
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.generic.business.error", SeverityType.ERROR);
		}
		return new ArrayList<SecurityResource>();
	}

	public void clearResourceList() {
		resources = null;
		selectedResources.clear();
	}

	public void unselectResource(SecurityResource securityResource) {
		getBean().getResources().remove(securityResource);
	}

	public void selectResources() {
		if (getBean().getResources() == null)
			getBean().setResources(new ArrayList<SecurityResource>(selectedResources));
		else
			getBean().getResources().addAll(selectedResources);
	}

	/**
	 * Get SecurityResources from current bean as array for datatable selection
	 */
	public SecurityResource[] getResourceArray() {
		return selectedResources.toArray(new SecurityResource[0]);
	}

	/**
	 * Set SecurityResources on current bean from datatable selection array
	 * 
	 * @param selectedResourcesArray
	 *            array of SecurityResources to set current bean
	 */
	public void setResourceArray(SecurityResource[] selectedResourcesArray) {
		for (SecurityResource resource : selectedResourcesArray)
			if (!selectedResources.contains(resource))
				selectedResources.add(resource);
	}

}