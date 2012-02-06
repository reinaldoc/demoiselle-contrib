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
package br.gov.frameworkdemoiselle.query.contrib;

import java.util.Map;

import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.enumeration.contrib.Logic;

/**
 * Structure used to handle query configuration of data results on both
 * <i>backend</i> (i.e., persistence) and <i>frontend</i> (i.e., presentation)
 * layers in the application.
 * 
 * @author SERPRO
 */
public interface QueryConfig<T> {

	/**
	 * Returns the current page.
	 */
	int getCurrentPage();

	/**
	 * Sets the current page.
	 */
	void setCurrentPage(int currentPage);

	/**
	 * Returns the max results that means the page size for paginated query.
	 */
	int getMaxResults();

	/**
	 * Sets the max results that means the page size for paginated query.
	 */
	void setMaxResults(int maxResult);

	/**
	 * Returns true if pagination is enabled.
	 */
	boolean isPaginated();

	/**
	 * To enable pagination, you must inform the first item (aka firstResult)
	 * and number of result items (aka page size). getTotalResults() and
	 * getTotalPages() can be read after query.
	 */
	void setPagination(int firstResult, int maxResult);

	/**
	 * Returns the total number of results.
	 */
	int getTotalResults();

	/**
	 * Sets the total number of results and calculates the number of pages.
	 */
	void setTotalResults(int totalResults);

	/**
	 * Returns the total number of pages.
	 */
	int getTotalPages();

	/**
	 * Returns the position for the first record according to current page and
	 * page size.
	 */
	int getFirstResult();

	/**
	 * Returns the attributes names for sorting query, means
	 * "sort by attr1, attr2, ..."
	 */
	String[] getSorting();

	/**
	 * Set the attributes names for sorting query, means
	 * "sort by attr1, attr2, ..."
	 */
	void setSorting(String... attributesName);

	/**
	 * Returns the sort order, means 'asc' for true, and 'desc' for false.
	 * Default is true (asc).
	 */
	boolean isSortOrder();

	/**
	 * Set the sort order, means 'asc' for true, and 'desc' for false
	 */
	void setSortOrder(boolean ordering);

	/**
	 * Returns the Map to create filters, means 'where attr1=value1'
	 */
	Map<String, Object> getFilter();

	/**
	 * Set filters, means 'where attr1=value1'
	 */
	void setFilterStr(Map<String, String> filters);

	/**
	 * Set filters, means 'where attr1=value1'. Object can be a array that means
	 * 'where attr1=value1 and attr1=value2'. Default logic is AND but can be
	 * change to OR.
	 */
	void setFilter(Map<String, Object> filters);

	/**
	 * Set filters reading by Reflection not null attributes. See below a sample
	 * of its usage:
	 * 
	 * Attention for default attributes values! Will be included on query.
	 * 
	 * Employee example = new Employee();
	 * example.setName("John Doe");
	 * example.setMail("john@example.com");
	 * setFilter(exmaple);
	 * 
	 * This means 'where name = "John Doe" and mail = "john@example.com"'
	 * 
	 * Default logic is AND but can be change to OR. Default comparison is
	 * EQUALS, but can be changed to CONTAINS, STARTSWITH or ENDSWITH.
	 */
	void setFilter(T filter);

	/**
	 * Returns the comparison mode between filters for String attributes. Others
	 * types is always EQUALS.
	 * 
	 * Comparison.EQUALS means 'where name = "John Doe"'
	 * Comparison.CONTAINS means 'where name like "%John Doe%"'
	 * Comparison.STARTSWITH means 'where name like "John Doe%"'
	 * Comparison.ENDSWITH means 'where name like "%John Doe"'
	 * 
	 */
	Comparison getFilterComparison();

	/**
	 * Set the comparison mode between filters for String attributes. Others
	 * types is always EQUALS.
	 * 
	 * Comparison.EQUALS means 'where name = "John Doe"'
	 * Comparison.CONTAINS means 'where name like "%John Doe%"'
	 * Comparison.STARTSWITH means 'where name like "John Doe%"'
	 * Comparison.ENDSWITH means 'where name like "%John Doe"'
	 * 
	 */
	void setFilterComparison(Comparison comparisonEnum);

	/**
	 * Returns true if logic mode between filters is Logic.NAND or Logic.NOR
	 * otherwise return false
	 * 
	 */
	boolean isFilterLogicNegation();

	/**
	 * Returns true if logic mode between filters is Logic.AND or Logic.NAND
	 * otherwise return false
	 * 
	 */
	boolean isFilterLogicConjunction();

	/**
	 * Returns the logic mode between filters.
	 * 
	 * Logic.AND means 'where name like "John Doe" and age = 10'
	 * Logic.OR means 'where name like "John Doe" or age = 10'
	 * Logic.NAND means 'where name not like "John Doe" and not age = 10'
	 * Logic.NOR means 'where name not like "John Doe" or not age = 10'
	 * 
	 */
	Logic getFilterLogic();

	/**
	 * Set the logic mode between filters.
	 * 
	 * Logic.AND means 'where name like "John Doe" and age = 10'
	 * Logic.OR means 'where name like "John Doe" or age = 10'
	 * Logic.NAND means 'where name not like "John Doe" and not age = 10'
	 * Logic.NOR means 'where name not like "John Doe" or not age = 10'
	 * 
	 */
	void setFilterLogic(Logic logicEnum);

	/**
	 * Returns the case mode for String attributes. Others types is ignored.
	 * 
	 * true means 'where lower(name) like "John Doe".toLower();
	 * 
	 */
	boolean isFilterCaseInsensitive();

	/**
	 * Set the case mode for String attributes. Others types is ignored.
	 * 
	 * true means 'where lower(name) like "John Doe".toLower();
	 * 
	 */
	void setFilterCaseInsensitive(boolean insensitive);

	/**
	 * Returns the a generic information for query implementation. Used for
	 * options not especified on this interface.
	 * 
	 */
	Object getGeneric();

	/**
	 * Set the a generic information for query implementation. Used for
	 * options not especified on this interface.
	 * 
	 */
	void setGeneric(Object object);

}
