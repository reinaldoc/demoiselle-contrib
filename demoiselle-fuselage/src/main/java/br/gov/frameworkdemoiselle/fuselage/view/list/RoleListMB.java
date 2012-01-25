package br.gov.frameworkdemoiselle.fuselage.view.list;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.RoleBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractListPageBean;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.Faces;

@ViewController
public class RoleListMB extends AbstractListPageBean<SecurityRole, Long> {
	private static final long serialVersionUID = 1L;

	@Inject
	private RoleBC bc;

	@PostConstruct
	public void init() {
		setLazyDataModelInitialSortAttribute("name");
	}

	@Override
	protected List<SecurityRole> handleResultList() {
		try {
			return bc.findAll();
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		return new ArrayList<SecurityRole>();
	}

	@Transactional
	public String deleteSelection() {
		try {
			bc.delete(getSelectedList());
			clearSelection();
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		return null;
	}

}
