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
import java.util.Iterator;
import java.util.List;

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

	private CriteriaBuilder cBuilder;

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

	private List<Order> createSort(Root<T> criteriaEntity, QueryConfig<T> queryConfig) {
		List<Order> orderList = new ArrayList<Order>();
		for (String sortAtrr : queryConfig.getSorting())
			if (sortAtrr != null)
				if (queryConfig.isSortOrder())
					orderList.add(this.cBuilder.asc(criteriaEntity.get(sortAtrr)));
				else
					orderList.add(this.cBuilder.desc(criteriaEntity.get(sortAtrr)));
		return orderList;
	}

	private String createNotationString(String entityAttrValue, NotationEnum notationEnum) {
		if (entityAttrValue == null) {
			return "";
		} else if (notationEnum == NotationEnum.INFIX) {
			return "%" + entityAttrValue + "%";
		} else if (notationEnum == NotationEnum.PREFIX) {
			return entityAttrValue + "%";
		} else if (notationEnum == NotationEnum.POSTFIX) {
			return "%" + entityAttrValue;
		}
		return entityAttrValue;
	}

	private Predicate createWhere(Root<T> criteriaEntity, QueryConfig<T> queryConfig) {
		Iterator<String> filtersIter = queryConfig.getFilter().keySet().iterator();
		List<Predicate> predicates = new ArrayList<Predicate>();

		while (filtersIter.hasNext()) {
			String entityAttr = filtersIter.next();
			if (entityAttr == null) {
				continue;
			}

			// GambiModeOn to support entity navigation
			String[] pathList = entityAttr.split("\\.");
			Expression<String> expEntityAttr = null;
			if (pathList.length == 4) {
				expEntityAttr = criteriaEntity.get(pathList[0]).get(pathList[1]).get(pathList[2]).get(pathList[3]);
			} else if (pathList.length == 3) {
				expEntityAttr = criteriaEntity.get(pathList[0]).get(pathList[1]).get(pathList[2]);
			} else if (pathList.length == 2) {
				expEntityAttr = criteriaEntity.get(pathList[0]).get(pathList[1]);
			} else {
				expEntityAttr = criteriaEntity.get(entityAttr);
			}
			// GambiModeOff
			// Expression<String> expEntityAttr =
			// criteriaEntity.get(entityAttr);
			String entityAttrValue = (String) queryConfig.getFilter().get(entityAttr);
			if (entityAttrValue == null) {
				entityAttrValue = "";
			}
			if (queryConfig.isFilterCaseInsensitive()) {
				expEntityAttr = this.cBuilder.lower(expEntityAttr);
				entityAttrValue = entityAttrValue.toLowerCase();
			}
			predicates.add(this.cBuilder.like(expEntityAttr, createNotationString(entityAttrValue, queryConfig.getFilterNotation())));
		}
		if (queryConfig.getFilterLogic() == LogicEnum.OR) {
			return this.cBuilder.or(predicates.toArray(new Predicate[] {}));
		} else {
			return this.cBuilder.and(predicates.toArray(new Predicate[] {}));
		}
	}

	@Override
	public List<T> findAll() {
		this.cBuilder = getCriteriaBuilder();
		CriteriaQuery<T> criteria = this.cBuilder.createQuery(getBeanClass());
		Root<T> criteriaEntity = criteria.from(getBeanClass());

		final QueryConfig<T> queryConfig = this.getQueryConfig();
		if (queryConfig != null) {
			if (queryConfig.getFilter() != null && !queryConfig.getFilter().isEmpty()) {
				criteria.where(createWhere(criteriaEntity, queryConfig));
			}
			if (queryConfig.getSorting() != null && queryConfig.getSorting().length != 0) {
				List<Order> orderList = createSort(criteriaEntity, queryConfig);
				criteria.orderBy(orderList.toArray(new Order[] {}));
			}
		}

		TypedQuery<T> query = getEntityManager().createQuery(criteria);

		if (queryConfig != null) {
			queryConfig.setTotalResults(countAll(queryConfig));
			if (queryConfig.getPageSize() > 0) {
				query.setFirstResult(queryConfig.getFirstResult());
				query.setMaxResults(queryConfig.getPageSize());
			}
		}

		List<T> resultList = query.getResultList();
		return resultList;
	}

	/**
	 * Retrieves the number of persisted objects for the current class type.
	 * 
	 * @return the row count
	 */
	private int countAll(QueryConfig<T> queryConfig) {
		CriteriaQuery<Long> criteria = this.cBuilder.createQuery(Long.class);
		Root<T> criteriaEntity = criteria.from(getBeanClass());
		criteria.select(this.cBuilder.count(criteriaEntity));

		if (queryConfig.getFilter() != null && !queryConfig.getFilter().isEmpty()) {
			criteria.where(createWhere(criteriaEntity, queryConfig));
		}

		Long count = getEntityManager().createQuery(criteria).getSingleResult();
		return count.intValue();
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
