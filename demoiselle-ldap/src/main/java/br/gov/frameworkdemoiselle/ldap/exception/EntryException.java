/**
 * Copyright (c) 2012 - Reinaldo de Carvalho <reinaldoc@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 */

package br.gov.frameworkdemoiselle.ldap.exception;

import br.gov.frameworkdemoiselle.DemoiselleException;

public class EntryException extends DemoiselleException {

	private static final long serialVersionUID = 1L;

	public EntryException(String message) {
		super(message);
	}

	public EntryException(String message, Throwable cause) {
		super(message, cause);
	}

}
