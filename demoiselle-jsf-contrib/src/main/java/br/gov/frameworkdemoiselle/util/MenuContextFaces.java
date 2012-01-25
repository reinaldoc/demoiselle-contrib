package br.gov.frameworkdemoiselle.util;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.util.MenuContext;
import br.gov.frameworkdemoiselle.util.Parameter;

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
