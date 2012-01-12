package br.gov.frameworkdemoiselle.template;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Require Parameter.class from demoiselle-jsf;
 */
@SessionScoped
@Named
public class MenuContextFaces extends MenuContext {

	private static final long serialVersionUID = 1L;

	@RequestScoped
	@Inject
	private Parameter<String> menuName;

	@RequestScoped
	@Inject
	private Parameter<String> itemName;

	public MenuContextFaces() {
		select("MenuX", "ItemY");
	}

	public String selectByParameters() {
		if (menuName.getValue() != null && itemName.getValue() != null)
			select(menuName.getValue(), itemName.getValue());
		return "index.jsf";
	}

}
