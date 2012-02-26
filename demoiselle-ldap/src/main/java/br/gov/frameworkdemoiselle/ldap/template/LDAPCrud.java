/*
 * Demoiselle Framework
 * Copyright (C) 2010 SERPRO
 * Copyright (c) 2012 - Reinaldo de Carvalho <reinaldoc@gmail.com>
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
package br.gov.frameworkdemoiselle.ldap.template;

import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.enumeration.contrib.Logic;
import br.gov.frameworkdemoiselle.ldap.core.EntryManager;
import br.gov.frameworkdemoiselle.ldap.core.EntryQuery;
import br.gov.frameworkdemoiselle.ldap.exception.EntryException;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.query.contrib.QueryContext;
import br.gov.frameworkdemoiselle.template.Crud;
import br.gov.frameworkdemoiselle.util.Reflections;

/**
 * LDAP specific implementation for Crud interface.
 * 
 * @param <T>
 *            bean object type
 * @param <I>
 *            bean id type
 * @author SERPRO
 * @see Crud
 */
public class LDAPCrud<T, I> implements Crud<T, I> {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntryManager entryManager;

	@Inject
	private Instance<QueryContext> queryContext;

	private QueryConfig<T> queryConfig;

	private Class<T> beanClass;

	protected Class<T> getBeanClass() {
		if (this.beanClass == null) {
			this.beanClass = Reflections.getGenericTypeArgument(this.getClass(), 0);
		}
		return this.beanClass;
	}

	protected EntryManager getEntryManager() {
		return this.entryManager;
	}

	protected EntryQuery createQuery(final String ql) {
		return getEntryManager().createQuery(ql);
	}

	protected QueryConfig<T> getQueryConfig() {
		if (queryConfig == null) {
			QueryContext context = queryContext.get();
			queryConfig = context.getQueryConfig(getBeanClass());
		}
		return queryConfig;
	}

	/**
	 * Persist a @LDAPEntry annotated object. Use LDAP Add Operation
	 * 
	 * @param entry
	 *            a entry annotated with LDAPEntry
	 * @throws EntryException
	 */
	public void insert(final T entity) {
		getEntryManager().persist(entity);
	}

	/**
	 * Remove a @LDAPEntry annotated object. Use LDAP Del Operation
	 * 
	 * @param entry
	 *            a entry annotated with LDAPEntry
	 * @throws EntryException
	 */
	public void delete(final I id) {
		T entry = getEntryManager().find(getBeanClass(), id);
		getEntryManager().remove(entry);
	}

	/**
	 * Remove a LDAP Entry. Use LDAP Del Operation
	 * 
	 * @param dn
	 *            String Representation of Distinguished Name (RFC 1485)
	 * @throws EntryException
	 */
	public void remove(final String dn) {
		getEntryManager().remove(dn);
	}

	/**
	 * Merge LDAP Entry from not null attributes only. Null attributes will
	 * remain in DSA. Not null attributes will be replaced. Use LDAP Modify
	 * Operation.
	 * 
	 * @param entry
	 *            a entry annotated with LDAPEntry
	 * @throws EntryException
	 */
	public void update(final T entity) {
		getEntryManager().merge(entity);
	}

	/**
	 * Update LDAP Entry to not null attributes only. Null attributes will be
	 * removed from DSA. Not null attributes will be replaced. You must declare
	 * all required attributes. Use LDAP Modify Operation
	 * 
	 * @param oldEntry
	 *            the current entry annotated with LDAPEntry
	 * @param entry
	 *            entry annotated with LDAPEntry with values to update
	 * @throws EntryException
	 */
	public void update(final T entry, final T newentry) {
		getEntryManager().update(entry, newentry);
	}

	/**
	 * Find a entry with objectClass equals entry class name and @id attribute
	 * equals to id value, like &(objectClass=entryClass)(idattr=idvalue)
	 * 
	 * @param id
	 *            find a entry with @id attribute equals to id value
	 * @return a entry object
	 */
	public T load(final I id) {
		return getEntryManager().find(getBeanClass(), id);
	}

	/**
	 * Find a Entry by DN
	 * 
	 * @param entryClass
	 *            a entry class
	 * @param id
	 *            find a entry with @id attribute equals to id value
	 * @return String Representation of Distinguished Name (RFC 1485)
	 */
	public T getReference(String dn) {
		return getEntryManager().getReference(getBeanClass(), dn);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		String filter = "objectClass=" + getBeanClass().getSimpleName();

		final QueryConfig<T> queryConfig = this.getQueryConfig();
		if (queryConfig != null)
			if (queryConfig.getFilter() != null && !queryConfig.getFilter().isEmpty())
				filter = getFilter(getBeanClass(), queryConfig.getFilter(), queryConfig.getFilterLogic(), queryConfig.getFilterComparison());

		EntryQuery query = getEntryManager().createQuery(filter);

		if (queryConfig != null) {
			// TODO: implement pagination with LDAP Asynchronous Query
			// queryConfig.setTotalResults(countAll(queryConfig));
			if (queryConfig.getMaxResults() > 0) {
				// query.setFirstResult(queryConfig.getFirstResult());
				query.setMaxResults(queryConfig.getMaxResults());
			}
			query.setBaseDN((String) queryConfig.getGeneric());
		}

		return query.getResultList();
	}

	/**
	 * Create a filter like
	 * "(&(objectClass=className)(attribute1=value1)(attribute2=value2))" or
	 * "(&(objectClass=className)(|(attribute1=value1)(attribute2=value2)))"
	 * 
	 * @param clazz
	 *            a class with objectClass as name
	 * @param map
	 *            a map with attributes as key and attributes values as Object.
	 *            Object can be null for "(attribute=*)", or a .toString capable
	 *            object like Integer or Long for "(attribute=value)", or can be
	 *            a array, for "(attribute=value1)(attribute=value2)"
	 * @param logic
	 *            if AND or OR result "(attribute=value)", otherwise is NAND or
	 *            NOR that means "(!(attribute=value))"
	 * @param notation
	 *            if INFIX result "(attribute=*value*)", else if PREFIX
	 *            "(attribute=value*)", else if POSTFIX "(attribute=value*)",
	 *            otherwise is EXACT "(attribute=value)"
	 * @return a filter like
	 *         "(&(objectClass=className)(attribute1=value1)(attribute2=value2))"
	 *         or
	 *         "(&(objectClass=className)(|(attribute1=value1)(attribute2=value2)))"
	 */
	public static String getFilter(Class<?> clazz, Map<String, Object> map, Logic logic, Comparison notation) {
		if (logic == Logic.AND || logic == Logic.NAND)
			return "(&(objectClass=" + clazz.getSimpleName() + ")" + getPartialFilter(map, logic, notation) + ")";
		else
			return "(&(objectClass=" + clazz.getSimpleName() + ")(|" + getPartialFilter(map, logic, notation) + "))";
	}

	/**
	 * Create a partial filter like "(attribute1=value1)(attribute2=value2)" or
	 * "(!(attribute1=value1))(!(attribute2=value2))".
	 * 
	 * @param map
	 *            a map with attributes as key and attributes values as Object.
	 *            Object can be null for "(attribute=*)", or a .toString capable
	 *            object like Integer or Long for "(attribute=value)", or can be
	 *            a array, for "(attribute=value1)(attribute=value2)"
	 * @param logic
	 *            if AND or OR result "(attribute=value)", otherwise is NAND or
	 *            NOR that means "(!(attribute=value))"
	 * @param notation
	 *            if INFIX result "(attribute=*value*)", else if PREFIX
	 *            "(attribute=value*)", else if POSTFIX "(attribute=value*)",
	 *            otherwise is EXACT "(attribute=value)"
	 * @return a partial filter like "(attribute1=value1)(attribute2=value2)" or
	 *         "(!(attribute1=value1))(!(attribute2=value2))".
	 */
	public static String getPartialFilter(Map<String, Object> map, Logic logic, Comparison notation) {
		String partialFilter = "";
		for (Map.Entry<String, Object> mapEntry : map.entrySet())
			if (mapEntry.getValue() == null || !mapEntry.getValue().getClass().isArray())
				partialFilter = partialFilter + getPartialFilterElement(mapEntry.getKey(), mapEntry.getValue(), logic, notation);
			else
				for (Object value : (Object[]) mapEntry.getValue())
					partialFilter = partialFilter + getPartialFilterElement(mapEntry.getKey(), value, logic, notation);
		return partialFilter;
	}

	/**
	 * Create a partial filter element like "(attribute=value)" or
	 * "(!(attribute=value))"
	 * 
	 * @param attr
	 *            attribute name
	 * @param value
	 *            attribute value
	 * @param logic
	 *            if AND or OR result "(attribute=value)", otherwise is NAND or
	 *            NOR that means "(!(attribute=value))"
	 * @param notation
	 *            if INFIX result "(attribute=*value*)", else if PREFIX
	 *            "(attribute=value*)", else if POSTFIX "(attribute=value*)",
	 *            otherwise is EXACT "(attribute=value)"
	 * @return a partial filter element like "(attribute=*value*)" or
	 *         "(!(attribute=value))"
	 */
	public static String getPartialFilterElement(String attr, Object value, Logic logic, Comparison notation) {
		String partialFilter;

		if (value == null)
			partialFilter = "(" + attr + "=*)";
		else if (notation == Comparison.EQUALS)
			partialFilter = "(" + attr + "=" + value + ")";
		else if (notation == Comparison.CONTAINS)
			partialFilter = "(" + attr + "=*" + value + "*)";
		else if (notation == Comparison.STARTSWITH)
			partialFilter = "(" + attr + "=" + value + "*)";
		else
			partialFilter = "(" + attr + "=*" + value + ")";

		if (logic == Logic.NAND || logic == Logic.NOR)
			partialFilter = "(!" + partialFilter + ")";

		return partialFilter;
	}

}
