package br.gov.frameworkdemoiselle.fuselage.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.gov.frameworkdemoiselle.fuselage.business.ResourceBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.util.Beans;

@FacesConverter("securityResource")
public class SecurityResourceConverter implements Converter {

	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
		if (value.trim().isEmpty()) {
			return null;
		} else {
			return resourceBC().load(new Long(value));
		}
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object securityResource) {
		if (securityResource instanceof SecurityResource)
			return ((SecurityResource) securityResource).getId().toString();
		else
			return "";
	}

	private ResourceBC resourceBC() {
		return Beans.getReference(ResourceBC.class);
	}

}
