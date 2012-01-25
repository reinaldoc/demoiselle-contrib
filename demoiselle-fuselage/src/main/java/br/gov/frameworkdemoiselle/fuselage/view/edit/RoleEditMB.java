package br.gov.frameworkdemoiselle.fuselage.view.edit;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.RoleBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractEditPageBean;
import br.gov.frameworkdemoiselle.util.Faces;

@ViewController
public class RoleEditMB extends AbstractEditPageBean<SecurityRole, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private RoleBC bc;

	@Override
	public String insert() {
		try {
			bc.insert(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.role.insert.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.role.insert.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public String update() {
		try {
			bc.update(getBean());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.role.update.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.role.update.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public String delete() {
		try {
			bc.delete(getBean().getId());
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.role.delete.success", getBean().getName()));
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.role.delete.failed", SeverityType.ERROR));
		}
		return null;
	}

	@Override
	public SecurityRole load(Long id) {
		try {
			return bc.load(id);
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.role.load.failed", SeverityType.ERROR));
		}
		return new SecurityRole();
	}

	/**
	 * Get all SecurityResources for datatable
	 * 
	 * @return list of all SecurityResources
	 */
	public List<SecurityResource> getResourceList() {
		return bc.getResources();
	}

	/**
	 * Get SecurityResources from current bean as array for datatable selection
	 * 
	 * @return array of bean SecurityResources
	 */
	public SecurityResource[] getResourceArray() {
		if (getBean().getResources() == null)
			return null;
		return getBean().getResources().toArray(new SecurityResource[0]);
	}

	/**
	 * Set SecurityResources on current bean from datatable selection array
	 * 
	 * @param resources
	 *            array of SecurityResources to set current bean
	 */
	public void setResourceArray(SecurityResource[] resources) {
		getBean().setResources(Arrays.asList(resources));
	}

}