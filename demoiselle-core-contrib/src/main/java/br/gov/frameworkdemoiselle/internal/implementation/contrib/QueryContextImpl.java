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

import javax.enterprise.context.SessionScoped;

import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.query.contrib.QueryContext;

/**
 * Context implementation reserved for pagination purposes. Internally a hash
 * map is used to store pagination data for each class type.
 * 
 * @author SERPRO
 * @see QueryContext
 */
@SessionScoped
public class QueryContextImpl implements Serializable, QueryContext {

	private static final long serialVersionUID = 1L;

	private final Map<Class<?>, QueryConfig<?>> cache = new HashMap<Class<?>, QueryConfig<?>>();

	/**
	 * Method called by persistence layer (DAO), if queryConfig isn't create
	 * from view layer must return null;
	 */
	public <T> QueryConfig<T> getQueryConfig(final Class<T> clazz) {
		return (QueryConfig<T>) this.getQueryConfig(clazz, false);
	}

	/**
	 * Method called by view layer (Manage Bean), should create a queryConfig
	 * instance or reset values (filters, sort, case, ...);
	 */
	public <T> QueryConfig<T> getQueryConfig(final Class<T> clazz, final boolean create) {
		@SuppressWarnings("unchecked")
		QueryConfig<T> queryConfig = (QueryConfig<T>) cache.get(clazz);

		if (create) {
			if (queryConfig == null) {
				queryConfig = new QueryConfigImpl<T>();
				cache.put(clazz, queryConfig);
			}
			queryConfig.init();
		}

		return queryConfig;
	}

}
