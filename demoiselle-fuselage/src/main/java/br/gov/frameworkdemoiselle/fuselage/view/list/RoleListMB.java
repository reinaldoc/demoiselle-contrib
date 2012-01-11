package br.gov.frameworkdemoiselle.fuselage.view.list;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.RoleBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractListPageBean;
import br.gov.frameworkdemoiselle.transaction.Transactional;

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
		return bc.findAll();
	}

	@Transactional
	public String deleteSelection() {
		bc.delete(getSelectedList());
		clearSelection();
		return null;
	}

}
