package br.gov.frameworkdemoiselle.fuselage.view.edit;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.ProfileBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractEditPageBean;

@ViewController
public class ProfileEditMB extends AbstractEditPageBean<SecurityProfile, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private ProfileBC bc;

	@Override
	public String insert() {
		bc.insert(getBean());
		return null;
	}

	@Override
	public String update() {
		bc.update(getBean());
		return null;
	}

	@Override
	public String delete() {
		bc.delete(getBean().getId());
		return null;
	}

	@Override
	public SecurityProfile load(Long id) {
		return null;
	}

}