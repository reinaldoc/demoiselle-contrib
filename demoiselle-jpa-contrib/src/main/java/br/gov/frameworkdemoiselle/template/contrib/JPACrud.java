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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Enumerated;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.ArrayUtils;

import br.gov.frameworkdemoiselle.DemoiselleException;
import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;
import br.gov.frameworkdemoiselle.enumeration.contrib.LogicEnum;
import br.gov.frameworkdemoiselle.enumeration.contrib.NotationEnum;
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
public class JPACrud<T, I> implements Crud<T, I> {

	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager entityManager;

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

	protected CriteriaBuilder getCriteriaBuilder() {
		return getEntityManager().getCriteriaBuilder();
	}

	protected EntityManager getEntityManager() {
		return this.entityManager;
	}

	protected QueryConfig<T> getQueryConfig() {
		if (queryConfig == null) {
			QueryContext context = queryContext.get();
			queryConfig = context.getQueryConfig(getBeanClass());
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

	private Order[] getOrder() {
		List<Order> orderList = new ArrayList<Order>();
		for (String sortAtrr : queryConfig.getSorting())
			if (sortAtrr != null)
				if (queryConfig.isSortOrder())
					orderList.add(this.cBuilder.asc(this.cRoot.get(sortAtrr)));
				else
					orderList.add(this.cBuilder.desc(this.cRoot.get(sortAtrr)));
		return orderList.toArray(new Order[] {});
	}

	private String getNotationString(String entityAttrValue) {
		if (entityAttrValue == null) {
			return "";
		} else if (queryConfig.getFilterNotation() == NotationEnum.INFIX) {
			return "%" + entityAttrValue + "%";
		} else if (queryConfig.getFilterNotation() == NotationEnum.PREFIX) {
			return entityAttrValue + "%";
		} else if (queryConfig.getFilterNotation() == NotationEnum.POSTFIX) {
			return "%" + entityAttrValue;
		}
		return entityAttrValue;
	}

	protected Predicate getPredicateForString(Expression<String> attr, String value) {
		if (queryConfig.isFilterCaseInsensitive()) {
			attr = this.cBuilder.lower(attr);
			value = value.toLowerCase();
		}
		if (queryConfig.getFilterLogic() == LogicEnum.AND || queryConfig.getFilterLogic() == LogicEnum.OR)
			return this.cBuilder.like(attr, getNotationString(value));
		else
			return this.cBuilder.notLike(attr, getNotationString(value));
	}

	protected Predicate getPredicateAsString(Expression<String> attr, Object value) {
		if (queryConfig.getFilterLogic() == LogicEnum.AND || queryConfig.getFilterLogic() == LogicEnum.OR)
			return this.cBuilder.equal(attr, value);
		else
			return this.cBuilder.notEqual(attr, value);
	}

	protected Expression<String> getAttributeExpression(Root<T> criteriaEntity, String attributeName) {
		// TODO: made generic to support any navigation level
		// GambiModeOn to support 4 level entity navigation
		String[] attrNameList = attributeName.split("\\.");
		if (attrNameList.length == 4)
			return criteriaEntity.get(attrNameList[0]).get(attrNameList[1]).get(attrNameList[2]).get(attrNameList[3]);
		else if (attrNameList.length == 3)
			return criteriaEntity.get(attrNameList[0]).get(attrNameList[1]).get(attrNameList[2]);
		else if (attrNameList.length == 2)
			return criteriaEntity.get(attrNameList[0]).get(attrNameList[1]);
		else
			return criteriaEntity.get(attributeName);
		// GambiModeOff
	}

	private Predicate getWhere() {
		List<Predicate> predicates = new ArrayList<Predicate>();

		for (Map.Entry<String, Object> entry : queryConfig.getFilter().entrySet()) {
			String entityAttr = entry.getKey();
			if (entityAttr == null)
				continue;
			Object entityAttrValue = queryConfig.getFilter().get(entityAttr);
			if (entityAttrValue == null)
				continue;

			if (entityAttrValue instanceof String)
				predicates.add(getPredicateForString(getAttributeExpression(this.cRoot, entityAttr), (String) entityAttrValue));
			else if (entityAttrValue.getClass().isArray())
				for (Object value : (Object[]) entityAttrValue)
					if (value instanceof String)
						predicates.add(getPredicateForString(getAttributeExpression(this.cRoot, entityAttr), (String) value));
					else
						predicates.add(getPredicateAsString(getAttributeExpression(this.cRoot, entityAttr), value));
			else
				predicates.add(getPredicateAsString(getAttributeExpression(this.cRoot, entityAttr), entityAttrValue));

		}

		if (queryConfig.getFilterLogic() == LogicEnum.OR || queryConfig.getFilterLogic() == LogicEnum.NOR)
			return this.cBuilder.or(predicates.toArray(new Predicate[] {}));
		else
			return this.cBuilder.and(predicates.toArray(new Predicate[] {}));
	}

	@Override
	public List<T> findAll() {
		this.cBuilder = getCriteriaBuilder();
		CriteriaQuery<T> criteria = this.cBuilder.createQuery(getBeanClass());
		this.cRoot = criteria.from(getBeanClass());

		final QueryConfig<T> queryConfig = getQueryConfig();
		if (queryConfig != null) {
			if (queryConfig.getFilter() != null && !queryConfig.getFilter().isEmpty())
				criteria.where(getWhere());
			if (queryConfig.getSorting() != null && queryConfig.getSorting().length != 0)
				criteria.orderBy(getOrder());
		}

		TypedQuery<T> query = getEntityManager().createQuery(criteria);

		if (queryConfig != null) {
			queryConfig.setTotalResults(countAll(queryConfig));
			if (queryConfig.getPageSize() > 0) {
				query.setFirstResult(queryConfig.getFirstResult());
				query.setMaxResults(queryConfig.getPageSize());
			}
		}

		return query.getResultList();
	}

	/**
	 * Retrieves the number of persisted objects for the current class type.
	 * 
	 * @return the row count
	 */
	private int countAll(QueryConfig<T> queryConfig) {
		CriteriaQuery<Long> criteria = this.cBuilder.createQuery(Long.class);
		criteria.select(this.cBuilder.count(this.cRoot));

		if (queryConfig.getFilter() != null && !queryConfig.getFilter().isEmpty())
			criteria.where(getWhere());

		return getEntityManager().createQuery(criteria).getSingleResult().intValue();
	}

	/**
	 * Retrieves a list of entities based on a single example instance with
	 * conjunction (AND) or disjunction (OR) between not null attributes of it.
	 * Attention for default fields values.
	 * <p>
	 * See below a sample of its usage:
	 * 
	 * <pre>
	 * Employee example = new Employee();
	 * example.setId(12345);
	 * return (List&lt;Employee&gt;) findByExample(example);
	 * </pre>
	 * 
	 * @param example
	 *            an entity example
	 * @return a list of entities
	 */
	public List<T> find(final T example) {
		boolean isConjunction = true;
		int maxResult = 0;
		final CriteriaQuery<T> criteria = createCriteriaByExample(example, isConjunction);
		TypedQuery<T> query = getEntityManager().createQuery(criteria);
		if (maxResult > 0)
			query.setMaxResults(maxResult);
		return query.getResultList();
	}

	/**
	 * Support method which will be used for construction of criteria-based
	 * queries.
	 * 
	 * @param example
	 *            an example of the given entity
	 * @return an instance of {@code CriteriaQuery}
	 */
	private CriteriaQuery<T> createCriteriaByExample(final T example, boolean logic) {

		final CriteriaBuilder builder = getCriteriaBuilder();
		final CriteriaQuery<T> query = builder.createQuery(getBeanClass());
		final Root<T> entity = query.from(getBeanClass());

		final List<Predicate> predicates = new ArrayList<Predicate>();
		final Field[] fields = getSuperClassesFields(example.getClass());

		for (Field field : fields) {

			if (!field.isAnnotationPresent(Column.class) && !field.isAnnotationPresent(Basic.class) && !field.isAnnotationPresent(Enumerated.class)) {
				continue;
			}

			Object value = null;

			try {
				field.setAccessible(true);
				value = field.get(example);
			} catch (IllegalArgumentException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}

			if (value == null) {
				continue;
			}

			Predicate pred;
			if (logic)
				pred = builder.equal(entity.get(field.getName()), value);
			else {
				if (value instanceof String) {
					Expression<String> expField = entity.get(field.getName());
					pred = builder.like(builder.lower(expField), "%" + ((String) value).toLowerCase() + "%");
				} else
					pred = builder.equal(entity.get(field.getName()), value);
			}
			predicates.add(pred);
		}
		if (logic)
			return query.where(builder.and(predicates.toArray(new Predicate[0]))).select(entity);
		else
			return query.where(builder.or(predicates.toArray(new Predicate[0]))).select(entity);
	}

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

	/**
	 * Get annotated value
	 * 
	 * @param entry
	 */
	public static Object getAnnotatedValue(Object entry, Class<? extends Annotation> aclazz, boolean required) {
		Field field = getFieldAnnotatedAs(entry.getClass(), aclazz, required);
		if (field != null)
			return Reflections.getFieldValue(field, entry);
		return null;
	}

	public static Field getFieldAnnotatedAs(Class<?> clazz, Class<? extends Annotation> aclazz, boolean required) {
		for (Field field : getSuperClassesFields(clazz))
			if (field.isAnnotationPresent(aclazz))
				return field;
		if (required)
			throw new DemoiselleException("Field with @" + aclazz.getSimpleName() + " not found on class " + clazz.getSimpleName());
		else
			return null;
	}

}
