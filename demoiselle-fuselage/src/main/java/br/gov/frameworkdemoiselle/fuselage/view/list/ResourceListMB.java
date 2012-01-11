package br.gov.frameworkdemoiselle.fuselage.view.list;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.ResourceBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractListPageBean;
import br.gov.frameworkdemoiselle.transaction.Transactional;

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
		return bc.findAll();
	}

	@Transactional
	public String deleteSelection() {
		bc.delete(getSelectedList());
		clearSelection();
		return null;
	}

}
