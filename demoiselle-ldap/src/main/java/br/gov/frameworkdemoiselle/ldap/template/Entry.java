package br.gov.frameworkdemoiselle.ldap.template;

public abstract class Entry {

	private String dn;

	private String[] objectClass;

	protected abstract String[] objectClass();

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	public String[] getObjectClass() {
		if (objectClass == null)
			objectClass = objectClass();
		return objectClass;
	}

	public void setObjectClass(String[] objectClass) {
		this.objectClass = objectClass;
	}

}
