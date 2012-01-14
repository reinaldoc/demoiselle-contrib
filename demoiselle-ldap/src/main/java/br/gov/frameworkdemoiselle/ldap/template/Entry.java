package br.gov.frameworkdemoiselle.ldap.template;

import br.gov.frameworkdemoiselle.ldap.annotation.DistinguishedName;
import br.gov.frameworkdemoiselle.ldap.annotation.LDAPEntry;

@LDAPEntry
public abstract class Entry {

	@DistinguishedName
	private String dn;

	private String[] objectClass;

	protected abstract String[] objectClass();

	public Entry() {
		setObjectClass(objectClass());
	}

	public Entry(boolean setObjectClass) {
		if (setObjectClass)
			setObjectClass(objectClass());
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String[] getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String[] objectClass) {
		this.objectClass = objectClass;
	}

}
