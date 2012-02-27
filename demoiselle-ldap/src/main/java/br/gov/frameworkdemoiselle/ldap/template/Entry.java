/*
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

package br.gov.frameworkdemoiselle.ldap.template;

import br.gov.frameworkdemoiselle.ldap.annotation.DistinguishedName;
import br.gov.frameworkdemoiselle.ldap.annotation.LDAPEntry;
import br.gov.frameworkdemoiselle.ldap.annotation.ParentDN;

@LDAPEntry
public abstract class Entry {

	@DistinguishedName
	private String dn;

	@ParentDN
	private String parentDN;

	private String[] objectClass;

	protected abstract String[] objectClass();

	public Entry() {
		setObjectClass(objectClass());
	}

	public Entry(boolean skipObjectClass) {
		if (!skipObjectClass)
			setObjectClass(objectClass());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || dn == null || getClass() != obj.getClass())
			return false;
		if (dn.equals(((Entry) obj).dn))
			return true;
		return false;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String getParentDN() {
		return parentDN;
	}

	public void setParentDN(String parentDN) {
		this.parentDN = parentDN;
	}

	public String[] getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String[] objectClass) {
		this.objectClass = objectClass;
	}

}
