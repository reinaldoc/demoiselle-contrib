package br.gov.frameworkdemoiselle.ldap.internal;

import java.io.Serializable;

public class EntryCore implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Insert not implemented
	 */
	public void persist(Object entry) {

	}

	/**
	 * Update not implemented
	 */
	public void merge(Object entry) {

	}

	/**
	 * Update not implemented
	 */
	public void update(Object entry) {

	}

	/**
	 * Remove not implemented
	 */
	public void remove(Object entry) {

	}

	/**
	 * Find not implemented
	 */
	public <T> T find(Class<T> entryClass, Object dn) {
		T entry = null;
		try {
			entry = entryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return entry;
	}

	/**
	 * getReference not implemented
	 */
	public <T> T getReference(Class<T> entryClass, Object dn) {
		T entry = null;
		try {
			entry = entryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return entry;
	}

	/**
	 * findReference not implemented
	 */
	public String findReference(Object entry) {
		return "";
	}

}