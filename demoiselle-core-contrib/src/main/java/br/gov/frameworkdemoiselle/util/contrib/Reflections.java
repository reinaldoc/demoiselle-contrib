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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;

import org.apache.commons.lang.ArrayUtils;

public class Reflections extends br.gov.frameworkdemoiselle.util.Reflections {

	/**
	 * Build a array of super classes fields
	 * 
	 * @return Array of Super Classes Fields
	 */
	public static Field[] getSuperClassesFields(Class<?> entryClass) {
		Field[] fieldArray = null;
		fieldArray = (Field[]) ArrayUtils.addAll(fieldArray, entryClass.getDeclaredFields());
		Class<?> superClazz = entryClass.getSuperclass();
		while (superClazz != null && !"java.lang.Object".equals(superClazz.getName())) {
			fieldArray = (Field[]) ArrayUtils.addAll(fieldArray, superClazz.getDeclaredFields());
			superClazz = superClazz.getSuperclass();
		}
		return fieldArray;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] getAnnotatedValues(Collection<?> collection, Class<? extends Annotation> aclazz, Class<T> T) {
		T[] values = null;
		if (collection != null)
			for (Object item : collection)
				try {
					values = (T[]) ArrayUtils.add(values, (T) getAnnotatedValue(item, aclazz, true));
				} catch (Exception e) {
				}
		return values;
	}

	public static Object getAnnotatedValue(Object entry, Class<? extends Annotation> aclazz, boolean required) throws Exception {
		Field field = getAnnotatedField(entry.getClass(), aclazz, required);
		if (field != null)
			return getFieldValue(field, entry);
		return null;
	}

	public static Field getAnnotatedField(Class<?> clazz, Class<? extends Annotation> aclazz, boolean required) throws Exception {
		for (Field field : getSuperClassesFields(clazz))
			if (field.isAnnotationPresent(aclazz))
				return field;
		if (required)
			throw new Exception("Field with @" + aclazz.getSimpleName() + " not found on class " + clazz.getSimpleName());
		else
			return null;
	}

}
