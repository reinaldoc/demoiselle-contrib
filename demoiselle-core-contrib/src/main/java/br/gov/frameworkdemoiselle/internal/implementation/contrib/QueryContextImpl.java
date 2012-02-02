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
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.internal.configuration.PaginationConfig;
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

	@Inject
	private PaginationConfig config;

	private final Map<Class<?>, QueryConfig<?>> cache = new HashMap<Class<?>, QueryConfig<?>>();

	public <T> QueryConfig<T> getQueryConfig(final Class<T> clazz) {
		return (QueryConfig<T>) this.getQueryConfig(clazz, false);
	}

	public <T> QueryConfig<T> getQueryConfig(final Class<T> clazz, final boolean create) {
		@SuppressWarnings("unchecked")
		QueryConfig<T> queryConfig = (QueryConfig<T>) cache.get(clazz);

		if (queryConfig == null || create) {
			queryConfig = new QueryConfigImpl<T>();
			queryConfig.setPageSize(config.getPageSize());
			cache.put(clazz, queryConfig);
		}

		return queryConfig;
	}

}
