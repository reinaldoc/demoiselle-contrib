package br.gov.frameworkdemoiselle.ldap.core;

import java.util.HashMap;
import java.util.Map;

import org.ietf.ldap.LDAPConnection;

public class EntryQuery {

	private LDAPConnection conn;

	private String[] resultAttributes;

	public Map<String, Map<String, String[]>> getResult() {
		return new HashMap<String, Map<String, String[]>>();
	}

	public Map<String, String[]> getSingleResult() {
		return new HashMap<String, String[]>();
	}

	public Map<String, String> getSingleResultSingleAttributes() {
		return new HashMap<String, String>();
	}

	public String getSingleResultSingleAttribute() {
		return new String();
	}

	public LDAPConnection getConn() {
		return conn;
	}

	public void setConn(LDAPConnection conn) {
		this.conn = conn;
	}

	public String[] getResultAttributes() {
		return resultAttributes;
	}

	public void setResultAttributes(String[] resultAttributes) {
		this.resultAttributes = resultAttributes;
	}

}
