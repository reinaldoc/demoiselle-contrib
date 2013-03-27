/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.util.contrib;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

import br.gov.frameworkdemoiselle.message.DefaultMessage;
import br.gov.frameworkdemoiselle.message.Message;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.util.Beans;
import br.gov.frameworkdemoiselle.util.ResourceBundle;

public class Faces extends br.gov.frameworkdemoiselle.util.Faces {

	private static final String REPORT_SUFFIX_PATH = "WEB-INF/classes/reports/";

	public static FacesContext getFacesContext() {
		return Beans.getReference(FacesContext.class);
	}

	public static void validationFailed() {
		getFacesContext().validationFailed();
	}

	public static boolean isValidationFailed() {
		return getFacesContext().isValidationFailed();
	}

	public static void validationFailedMessage(Message... messageList) {
		for (Message message : messageList)
			addMessage(message);
		getFacesContext().validationFailed();
	}

	public static void resetValidation() {
		resetInputFields(getFacesContext().getViewRoot().getChildren());
	}

	public static void resetParentFormValidation(UIComponent uiComponent) {
		if (uiComponent instanceof UIForm)
			resetInputFields(uiComponent.getChildren());
		else if (uiComponent.getParent() != null)
			resetParentFormValidation(uiComponent.getParent());
	}

	private static void resetInputFields(List<UIComponent> componentList) {
		for (UIComponent component : componentList) {
			if (component instanceof UIInput) {
				UIInput input = (UIInput) component;
				input.resetValue();
			}
			resetInputFields(component.getChildren());
		}
	}

	public static void addI18nMessage(String bundleKey) {
		addMessage(new DefaultMessage(Beans.getReference(ResourceBundle.class).getString(bundleKey)));
	}

	public static void addI18nMessage(String bundleKey, Object... params) {
		addMessage(new DefaultMessage(Beans.getReference(ResourceBundle.class).getString(bundleKey, params)));
	}

	public static void addI18nMessage(String bundleKey, SeverityType type) {
		addMessage(new DefaultMessage(Beans.getReference(ResourceBundle.class).getString(bundleKey), type));
	}

	public static void addI18nMessage(String bundleKey, SeverityType type, Object... params) {
		addMessage(new DefaultMessage(Beans.getReference(ResourceBundle.class).getString(bundleKey, params), type));
	}

	public static String getReportPath(String relativePath) {
		return getFacesContext().getExternalContext().getRealPath(REPORT_SUFFIX_PATH + relativePath);
	}

}
