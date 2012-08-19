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
package br.gov.frameworkdemoiselle.template.contrib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.gov.frameworkdemoiselle.DemoiselleException;
import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;
import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.query.contrib.QueryContext;
import br.gov.frameworkdemoiselle.template.Crud;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.Reflections;
import br.gov.frameworkdemoiselle.util.ResourceBundle;

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
public class JPACrud<T, I> extends br.gov.frameworkdemoiselle.template.JPACrud<T, I> {

	private static final long serialVersionUID = 1L;

	@Inject
	private Instance<QueryContext> queryContext;

	private QueryConfig<T> queryConfig;

	@Inject
	@Name("demoiselle-jpa-bundle")
	private Instance<ResourceBundle> bundle;

	protected CriteriaBuilder cBuilder = null;

	protected Root<T> cRoot = null;

	private Class<T> beanClass;

	protected Class<T> getBeanClass() {
		if (this.beanClass == null) {
			this.beanClass = Reflections.getGenericTypeArgument(this.getClass(), 0);
		}
		return this.beanClass;
	}

	protected CriteriaQuery<T> getCriteria() {
		this.cBuilder = getCriteriaBuilder();
		CriteriaQuery<T> criteria = this.cBuilder.createQuery(getBeanClass());
		this.cRoot = criteria.from(getBeanClass());
		return criteria;
	}

	protected CriteriaBuilder getCriteriaBuilder() {
		return getEntityManager().getCriteriaBuilder();
	}

	protected QueryConfig<T> getQueryConfig() {
		if (queryConfig == null) {
			queryConfig = queryContext.get().getQueryConfig(getBeanClass());
		}
		return queryConfig;
	}

	protected CriteriaQuery<T> createCriteriaQuery() {
		return getCriteriaBuilder().createQuery(getBeanClass());
	}

	protected Query createQuery(final String ql) {
		return getEntityManager().createQuery(ql);
	}

	protected void handleException(Throwable cause) throws Throwable {
		if (cause instanceof TransactionRequiredException) {
			String message = bundle.get().getString("no-transaction-active", "frameworkdemoiselle.transaction.class", Configuration.DEFAULT_RESOURCE);
			throw new DemoiselleException(message, cause);

		} else {
			throw cause;
		}
	}

	@Override
	@Transactional
	public void insert(final T entity) {
		getEntityManager().persist(entity);
	}

	@Override
	@Transactional
	public void delete(final I id) {
		T entity = getEntityManager().getReference(getBeanClass(), id);
		getEntityManager().remove(entity);
	}

	@Override
	@Transactional
	public void update(final T entity) {
		getEntityManager().merge(entity);
	}

	@Override
	public T load(final I id) {
		return getEntityManager().find(getBeanClass(), id);
	}

	protected Order[] getOrder() {
		List<Order> orderList = new ArrayList<Order>();
		for (String sortAtrr : queryConfig.getSorting())
			if (sortAtrr != null)
				if (queryConfig.isSortOrder())
					orderList.add(this.cBuilder.asc(getAttributeExpression(sortAtrr)));
				else
					orderList.add(this.cBuilder.desc(getAttributeExpression(sortAtrr)));
		return orderList.toArray(new Order[] {});
	}

	protected String getComparison(String entityAttrValue) {
		if (entityAttrValue == null)
			return "";
		else if (queryConfig.getFilterComparison() == Comparison.EQUALS)
			return entityAttrValue;
		else if (queryConfig.getFilterComparison() == Comparison.CONTAINS)
			return "%" + entityAttrValue + "%";
		else if (queryConfig.getFilterComparison() == Comparison.STARTSWITH)
			return entityAttrValue + "%";
		else
			return "%" + entityAttrValue;
	}

	protected Predicate getPredicateForString(Expression<String> attr, String value) {
		if (queryConfig.isFilterCaseInsensitive()) {
			attr = this.cBuilder.lower(attr);
			value = value.toLowerCase();
		}
		if (queryConfig.isFilterLogicNegation())
			return this.cBuilder.notLike(attr, getComparison(value));
		else
			return this.cBuilder.like(attr, getComparison(value));
	}

	protected Predicate getPredicate(Expression<String> attr, Object value) {
		if (queryConfig.isFilterLogicNegation())
			return this.cBuilder.notEqual(attr, value);
		else
			return this.cBuilder.equal(attr, value);
	}

	protected Expression<String> getAttributeExpression(String attributeName) {
		// TODO: fix GabiMode making generic to support any navigation level
		// GambiModeOn to support 4 level entity navigation
		String[] attrNameList = attributeName.split("\\.");
		if (attrNameList.length == 4)
			return this.cRoot.get(attrNameList[0]).get(attrNameList[1]).get(attrNameList[2]).get(attrNameList[3]);
		else if (attrNameList.length == 3)
			return this.cRoot.get(attrNameList[0]).get(attrNameList[1]).get(attrNameList[2]);
		else if (attrNameList.length == 2)
			return this.cRoot.get(attrNameList[0]).get(attrNameList[1]);
		else
			return this.cRoot.get(attributeName);
		// GambiModeOff
	}

	protected Predicate getWhere() {
		if (queryConfig.getFilter() == null || queryConfig.getFilter().isEmpty())
			return null;

		List<Predicate> predicates = new ArrayList<Predicate>();
		for (Map.Entry<String, Object> entry : queryConfig.getFilter().entrySet()) {
			if (entry.getKey() == null || entry.getValue() == null)
				continue;

			Object[] values;
			if (entry.getValue().getClass().isArray()) {
				values = (Object[]) entry.getValue();
				if (values.length == 0)
					continue;
			} else
				values = new Object[] { entry.getValue() };

			for (Object value : values) {
				if (value == null)
					continue;
				if (value instanceof String && queryConfig.getFilterComparison() != Comparison.EQUALS)
					predicates.add(getPredicateForString(getAttributeExpression(entry.getKey()), (String) value));
				else
					predicates.add(getPredicate(getAttributeExpression(entry.getKey()), value));
			}
		}

		if (predicates.size() == 0)
			return null;
		if (queryConfig.isFilterLogicConjunction())
			return this.cBuilder.and(predicates.toArray(new Predicate[] {}));
		else
			return this.cBuilder.or(predicates.toArray(new Predicate[] {}));
	}

	@Override
	public List<T> findAll() {
		CriteriaQuery<T> criteria = getCriteria();

		getQueryConfig();
		if (queryConfig != null) {
			Predicate predicate = getWhere();
			if (predicate != null)
				criteria.where(predicate);
			if (queryConfig.getSorting() != null && queryConfig.getSorting().length != 0)
				criteria.orderBy(getOrder());
		}

		TypedQuery<T> query = getEntityManager().createQuery(criteria);

		if (queryConfig != null) {
			if (queryConfig.isPaginated()) {
				query.setFirstResult(queryConfig.getFirstResult());
				queryConfig.setTotalResults(countAll());
			}
			if (queryConfig.getMaxResults() > 0)
				query.setMaxResults(queryConfig.getMaxResults());
		}

		return query.getResultList();
	}

	/**
	 * Retrieves the number of persisted objects for the current class type.
	 * 
	 * @return the row count
	 */
	protected int countAll() {
		CriteriaQuery<Long> criteria = this.cBuilder.createQuery(Long.class);
		criteria.from(getBeanClass());
		criteria.select(this.cBuilder.count(this.cRoot));

		Predicate predicate = getWhere();
		if (predicate != null)
			criteria.where(predicate);

		return getEntityManager().createQuery(criteria).getSingleResult().intValue();
	}

	/**
	 * Retrieves the number of persisted objects type by hql and parameters
	 * (name and value). Examples:
	 * 
	 * countAll("from User u where u.city = :city order by u.name", "city",
	 * "New York")
	 * 
	 * Starts with "from Entity e" is required for create "select count(e)";
	 * 
	 * @return the row count
	 */
	protected int countAll(String hql, Object... parameters) {
		Query query = createQuery("select count(" + hql.split(" ")[2] + ") " + hql);
		setQueryParameters(query, parameters);
		return ((Long) query.getSingleResult()).intValue();
	}

	protected Query createQuery(String ql, Object... parameters) {
		Query query = createQuery(ql);
		setQueryParameters(query, parameters);
		setQueryPagination(query, ql, parameters);
		return query;
	}

	private void setQueryParameters(Query query, Object... parameters) {
		if (parameters.length % 2 != 0)
			throw new IllegalArgumentException();
		for (int i = 0; i < parameters.length; i++) {
			query.setParameter((String) parameters[i], parameters[i + 1]);
			i++;
		}
	}

	private void setQueryPagination(Query query, String ql, Object... parameters) {
		getQueryConfig();
		if (queryConfig != null) {
			queryConfig.setTotalResults(countAll(ql, parameters));
			if (queryConfig.getMaxResults() > 0) {
				query.setFirstResult(queryConfig.getFirstResult());
				query.setMaxResults(queryConfig.getMaxResults());
			}
		}
	}

}
