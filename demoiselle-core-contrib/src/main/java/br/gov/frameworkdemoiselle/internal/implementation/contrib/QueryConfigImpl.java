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
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Alternative;

import br.gov.frameworkdemoiselle.enumeration.contrib.LogicEnum;
import br.gov.frameworkdemoiselle.enumeration.contrib.NotationEnum;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
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

	private int pageSize;

	private int totalResults;

	private int totalPages;

	private String[] sorting;

	private boolean sortOrder;

	private Map<String, Object> filter;

	private NotationEnum filterNotation;

	private LogicEnum filterLogic;

	private boolean filterCaseInsensitive;
	
	private Object generic;

	public QueryConfigImpl() {
		pageSize = 0;
		totalResults = 0;
		reset();
	}

	private void reset() {
		currentPage = 0;
		totalPages = 0;
		sorting = new String[0];
		sortOrder = true;
		filter = new HashMap<String, Object>();
		filterNotation = NotationEnum.INFIX;
		filterLogic = LogicEnum.AND;
		filterCaseInsensitive = true;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	private void setTotalPages(int totalPages) {
		validateNegativeValue(totalPages);
		this.totalPages = totalPages;

		if (totalPages == 0) {
			reset();
		} else if (getCurrentPage() >= totalPages) {
			setCurrentPage(totalPages - 1);
		}
	}

	private void validateNegativeValue(int input) throws IndexOutOfBoundsException {
		if (input < 0) {
			throw new IndexOutOfBoundsException("colocar mensagem");
		}
	}

	private void validateCurrentPage(int currentPage) throws IndexOutOfBoundsException {
		if (currentPage >= this.totalPages) {
			if (this.totalPages > 0) {
				throw new IndexOutOfBoundsException("colocar mensagem");
			}
		}
	}

	public void setCurrentPage(int currentPage) {
		validateNegativeValue(currentPage);
		validateCurrentPage(currentPage);
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getTotalResults() {
		return totalResults;
	}

	public void setTotalResults(int totalResults) {
		validateNegativeValue(totalResults);
		this.totalResults = totalResults;

		if (totalResults > 0) {
			setTotalPages();
		} else {
			reset();
		}
	}

	private void setTotalPages() {
		if (totalResults > 0) {
			setTotalPages((int) Math.ceil(totalResults * 1d / getPageSize()));
		} else {
			setTotalPages(0);
		}
	}

	public int getTotalPages() {
		return totalPages;
	}

	public int getFirstResult() {
		return getCurrentPage() * getPageSize();
	}

	public void setPageSize(int pageSize) {
		validateNegativeValue(pageSize);
		this.pageSize = pageSize;

		if (pageSize > 0) {
			setTotalPages();
		} else {
			reset();
		}
	}

	private void validateFirstResult(int firstResult) throws IndexOutOfBoundsException {
		if (firstResult >= this.totalResults) {
			if (this.totalResults > 0) {
				throw new IndexOutOfBoundsException("colocar mensagem");
			}
		}
	}

	public void setFirstResult(int firstResult) {
		validateNegativeValue(firstResult);
		validateFirstResult(firstResult);

		if (firstResult > 0) {
			setCurrentPage(firstResult / pageSize);
		} else {
			setCurrentPage(0);
		}
	}

	@Override
	public String toString() {
		return Strings.toString(this);
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

	}

	public NotationEnum getFilterNotation() {
		return filterNotation;
	}

	public void setFilterNotation(NotationEnum filtersNotation) {
		this.filterNotation = filtersNotation;
	}

	public LogicEnum getFilterLogic() {
		return filterLogic;
	}

	public void setFilterLogic(LogicEnum filtersLogic) {
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
