package br.gov.frameworkdemoiselle.fuselage.view.edit;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.RoleBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityRole;
import br.gov.frameworkdemoiselle.template.AbstractEditPageBean;

public class RoleEditMB extends AbstractEditPageBean<SecurityRole, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private RoleBC bc;

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
	public SecurityRole load(Long id) {
		return null;
	}

}