package br.gov.frameworkdemoiselle.fuselage.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("longNullAsHyphen")
public class LongNullAsHyphenConverter implements Converter {

	public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
		try {
			return new Long(value);
		} catch (Exception e) {
			return null;
		}
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
		if (object == null)
			return "---";
		if (object instanceof Long && ((Long) object).intValue() < 1)
			return "---";
		return String.valueOf(object);
	}

}
