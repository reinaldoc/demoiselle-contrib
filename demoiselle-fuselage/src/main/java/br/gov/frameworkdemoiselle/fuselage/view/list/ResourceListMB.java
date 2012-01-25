package br.gov.frameworkdemoiselle.fuselage.view.list;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.ResourceBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractListPageBean;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.Faces;

@ViewController
public class ResourceListMB extends AbstractListPageBean<SecurityResource, Long> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ResourceBC bc;

	@PostConstruct
	public void init() {
		setLazyDataModelInitialSortAttribute("name");
	}

	@Override
	protected List<SecurityResource> handleResultList() {
		try {
			return bc.findAll();
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addMessage(bc.getBundle().getI18nMessage("fuselage.generic.business.error", SeverityType.ERROR));
		}
		return new ArrayList<SecurityResource>();
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
