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
 * "LICENCA.txt", junto com esse programa. Se não, acesse < http://www.gnu.org/licenses/ >
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.ldap.sample;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Descreva aqui seu caso de teste.
 */
public class AppTest{
	
	private App app;
	
	/**
	 * Qualquer método anotado com <code>@Before</code> será executado antes
	 * da execução de cada teste;
	 * 
	 * Propício para o carregamento de dados independentes de outros testes.
	 */
	@Before
	public void setUp() {
		app = new App();
	}
	
	/**
	 * Qualquer método anotado com <code>@After</code> será executado após
	 * da execução de cada teste;
	 */
	@After
	public void tearDown() {
		app = null;
	}
	
	
	/**
	 * O nome dos métodos devem ser auto-explicativos, indicando exatamente o que será testado. Isto
	 * faz com que a documentação aqui se torne opcional ou redundante.
	 */
	@Test
	public void test1Mais1() {
		Assert.assertEquals(2,app.soma(1,1));
	}
	
	@Test
	public void test1Mais2() {
		Assert.assertEquals(3,app.soma(1,2));
	}
	
	@Test
	public void test2Mais2() {
		Assert.assertEquals(4,app.soma(2,2));
	}

}
