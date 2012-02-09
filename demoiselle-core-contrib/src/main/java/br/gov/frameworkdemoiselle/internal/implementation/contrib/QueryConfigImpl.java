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
package br.gov.frameworkdemoiselle.internal.implementation.contrib;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Alternative;

import br.gov.frameworkdemoiselle.annotation.Ignore;
import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.enumeration.contrib.Logic;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.util.contrib.Reflections;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

/**
 * Structure used to handle pagination of data results on both <i>backend</i>
 * (i.e., persistence) and <i>frontend</i> (i.e., presentation) layers in the
 * application.
 * <p>
 * Internally, it stores the current page index on {@code currentPage} variable,
 * the amount of records in a single page on {@code pageSize}, and the total
 * number of pages in {@code totalPages}.
 * 
 * @author SERPRO
 * @see QueryConfig
 */
@Alternative
public class QueryConfigImpl<T> implements Serializable, QueryConfig<T> {

	private static final long serialVersionUID = 1L;

	private int currentPage;

	private int maxResult;

	private boolean pagination;

	private int totalResults;

	private int totalPages;

	private String[] sorting;

	private boolean sortOrder;

	private Map<String, Object> filter;

	private Comparison filterComparison;

	private Logic filterLogic;

	private boolean filterCaseInsensitive;

	private Object generic;

	@Override
	public String toString() {
		return Strings.toString(this);
	}

	public QueryConfigImpl() {
		totalResults = 0;
		resetPagination();
		init();
	}

	public void init() {
		pagination = false;
		maxResult = 0;
		sorting = new String[0];
		sortOrder = true;
		filter = new HashMap<String, Object>();
		filterComparison = Comparison.EQUALS;
		filterLogic = Logic.AND;
		filterCaseInsensitive = true;
		generic = null;
	}

	private void resetPagination() {
		pagination = false;
		currentPage = 0;
		totalPages = 0;
	}

	private void requireNonNegativeValue(int value) throws IndexOutOfBoundsException {
		if (value < 0)
			throw new IndexOutOfBoundsException("value must be non negative");
	}


	public boolean isPaginated() {
		return pagination;
	}

	/**
	 * Pagination is based on slice of a table. Then is required a initial
	 * (first result) and a final values (max result that means page size) for
	 * retrieve the slice. To enable pagination maxResult must be greater then
	 * zero
	 */
	public void setPagination(int firstResult, int maxResult) {
		this.pagination = true;
		setMaxResults(maxResult);
		setFirstResult(firstResult);
	}

	public int getFirstResult() {
		return currentPage * maxResult;
	}

	private void setFirstResult(int firstResult) {
		requireNonNegativeValue(firstResult);
		if (firstResult >= totalResults && totalResults > 0)
			throw new IndexOutOfBoundsException("colocar mensagem");

		if (firstResult > 0)
			setCurrentPage(firstResult / maxResult);
		else
			setCurrentPage(0);
	}

	public int getMaxResults() {
		return maxResult;
	}

	/**
	 * Max results means page size for pagination concept. In general is the
	 * query limit;
	 */
	public void setMaxResults(int maxResult) {
		requireNonNegativeValue(maxResult);
		this.maxResult = maxResult;
		if (maxResult == 0)
			resetPagination();
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		requireNonNegativeValue(currentPage);
		if (currentPage >= totalPages && totalPages > 0)
			throw new IndexOutOfBoundsException("current page should be lower then total papes");
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	private void setTotalPages() {
		requireNonNegativeValue(totalPages);
		if (totalResults > 0 && maxResult > 0)
			this.totalPages = (int) Math.ceil(totalResults * 1d / maxResult);
		else
			this.totalPages = 0;
		if (totalPages == 0)
			resetPagination();
		else if (currentPage >= totalPages)
			setCurrentPage(totalPages - 1);
	}
	
	/**
	 * Method called by manage bean to retrieve the count all value defined by
	 * persistence layer;
	 */
	public int getTotalResults() {
		return totalResults;
	}

	/**
	 * Method called by persistence to inform value from count all operation;
	 * And calculate the total pages (totalResults / pageSize);
	 */
	public void setTotalResults(int countall) {
		requireNonNegativeValue(countall);
		totalResults = countall;
		setTotalPages();
	}

	public String[] getSorting() {
		return sorting;
	}

	public void setSorting(String... sorting) {
		this.sorting = sorting;
	}

	public boolean isSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(boolean sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Map<String, Object> getFilter() {
		return filter;
	}

	public void setFilter(Map<String, Object> filters) {
		this.filter = filters;
	}

	public void setFilterStr(Map<String, String> filters) {
		Map<String, Object> filterMap = new HashMap<String, Object>();
		if (filters != null)
			for (Map.Entry<String, String> entry : filters.entrySet())
				filterMap.put(entry.getKey(), entry.getValue());
		this.filter = filterMap;
	}

	public void setFilter(T domain) {
		Field[] fields = Reflections.getSuperClassesFields(domain.getClass());
		for (Field field : fields) {
			if (field.isAnnotationPresent(Ignore.class) || filter.containsKey(field.getName()))
				continue;
			Object value = Reflections.getFieldValue(field, domain);
			if (value != null)
				filter.put(field.getName(), value);
		}
	}

	public Comparison getFilterComparison() {
		return filterComparison;
	}

	public void setFilterComparison(Comparison filtersComparison) {
		this.filterComparison = filtersComparison;
	}

	public boolean isFilterLogicNegation() {
		if (filterLogic == Logic.NAND || filterLogic == Logic.NOR)
			return true;
		return false;
	}

	public boolean isFilterLogicConjunction() {
		if (filterLogic == Logic.AND || filterLogic == Logic.NAND)
			return true;
		return false;
	}

	public Logic getFilterLogic() {
		return filterLogic;
	}

	public void setFilterLogic(Logic filtersLogic) {
		this.filterLogic = filtersLogic;
	}

	public boolean isFilterCaseInsensitive() {
		return filterCaseInsensitive;
	}

	public void setFilterCaseInsensitive(boolean filtersCaseInsensitive) {
		this.filterCaseInsensitive = filtersCaseInsensitive;
	}

	public Object getGeneric() {
		return generic;
	}

	public void setGeneric(Object generic) {
		this.generic = generic;
	}

}
