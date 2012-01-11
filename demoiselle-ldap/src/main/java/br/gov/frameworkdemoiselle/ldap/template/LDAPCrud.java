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
package br.gov.frameworkdemoiselle.ldap.template;

import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.ldap.core.EntryManager;
import br.gov.frameworkdemoiselle.ldap.core.EntryQuery;
import br.gov.frameworkdemoiselle.pagination.Pagination;
import br.gov.frameworkdemoiselle.pagination.PaginationContext;
import br.gov.frameworkdemoiselle.template.Crud;
import br.gov.frameworkdemoiselle.util.Reflections;

/**
 * JPA specific implementation for Crud interface.
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
	private Instance<PaginationContext> paginationContext;

	private Pagination pagination;

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

	protected Pagination getPagination() {
		if (pagination == null) {
			PaginationContext context = paginationContext.get();
			pagination = context.getPagination(getBeanClass());
		}
		return pagination;
	}

	protected EntryQuery createQuery(final String ql) {
		return getEntryManager().createQuery(ql);
	}

	public void insert(final T entity) {
		getEntryManager().persist(entity);
	}

	public void delete(final I id) {
		T entity = getEntryManager().getReference(getBeanClass(), id);
		getEntryManager().remove(entity);
	}

	public void update(final T entity) {
		getEntryManager().merge(entity);
	}

	public T load(final I id) {
		return getEntryManager().find(getBeanClass(), id);
	}

	public List<T> findAll() {
		final String jpql = "select this from " + getBeanClass().getSimpleName() + " this";
		final EntryQuery query = getEntryManager().createQuery(jpql);

		final Pagination pagination = getPagination();
		if (pagination != null) {
			pagination.setTotalResults(this.countAll().intValue());
			// query.setFirstResult(pagination.getFirstResult());
			query.setMaxResults(pagination.getPageSize());
		}

		List<T> lista = null; // query.getResultList();
		return lista;
	}

	/**
	 * Retrieves the number of persisted objects for the current class type.
	 * 
	 * @return the row count
	 */
	@SuppressWarnings("unused")
	private Long countAll() {
		final EntryQuery query = getEntryManager().createQuery("select count(this) from " + beanClass.getSimpleName() + " this");
		return new Long(0); // (Long) query.getSingleResult();
	}

	@Override
	public List<T> findByExample(T example) {
		// TODO Auto-generated method stub
		return null;
	}

}
