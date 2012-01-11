package br.gov.frameworkdemoiselle.fuselage.view.list;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.ProfileBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractListPageBean;
import br.gov.frameworkdemoiselle.transaction.Transactional;

@ViewController
public class ProfileListMB extends AbstractListPageBean<SecurityProfile, Long> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ProfileBC bc;

	@PostConstruct
	public void init() {
		setLazyDataModelInitialSortAttribute("name");
	}

	@Override
	protected List<SecurityProfile> handleResultList() {
		return bc.findAll();
	}

	@Transactional
	public String deleteSelection() {
		bc.delete(getSelectedList());
		clearSelection();
		return null;
	}

}
