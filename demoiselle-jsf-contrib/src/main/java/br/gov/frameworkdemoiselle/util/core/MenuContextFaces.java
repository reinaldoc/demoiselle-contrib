package br.gov.frameworkdemoiselle.util.core;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.util.Parameter;
import br.gov.frameworkdemoiselle.util.core.MenuContext;

@SessionScoped
public class MenuContextFaces extends MenuContext {

	private static final long serialVersionUID = 1L;

	@RequestScoped
	@Inject
	private Parameter<String> menuName;

	@RequestScoped
	@Inject
	private Parameter<String> itemName;

	public void selectByParameters() {
		if (menuName.getValue() != null && itemName.getValue() != null)
			select(menuName.getValue(), itemName.getValue());
	}

}
