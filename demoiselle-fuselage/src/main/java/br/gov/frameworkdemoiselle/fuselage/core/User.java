package br.gov.frameworkdemoiselle.fuselage.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class User implements br.gov.frameworkdemoiselle.security.User {

	private static final long serialVersionUID = 1L;

	private String id = "";

	private Map<Object, Object> attributes = new HashMap<Object, Object>();

	@Override
	public void setAttribute(Object key, Object value) {
		attributes.put(key, value);
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public Object getAttribute(Object key) {
		return attributes.get(key);
	}

	/**
	 * Set the HashSet<String> if current value is null, otherwise append (addALL)
	 * 
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void addAllAttribute(Object key, Set<String> value) {
		if (value != null) {
			Set<String> currentSetObj = (Set<String>) getAttribute(key);
			if (currentSetObj != null)
				value.addAll(currentSetObj);
			setAttribute(key, value);
		}
	}

	/**
	 * Create the HashSet<String> if current value is null, otherwise append (add)
	 * 
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void addAttribute(Object key, String value) {
		if (value != null) {
			Set<String> currentSetObj = (Set<String>) getAttribute(key);
			Set<String> valueSetObj = new HashSet<String>();
			valueSetObj.add(value);
			if (currentSetObj != null)
				valueSetObj.addAll(currentSetObj);
			setAttribute(key, valueSetObj);
		}
	}

	/**
	 * Set the Map<String,String> if current value is null, otherwise append (putALL)
	 * 
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void putAllAttribute(Object key, Map<String, String> value) {
		if (value != null) {
			Map<String, String> cuurentMapObj = (Map<String, String>) getAttribute(key);
			if (cuurentMapObj != null)
				value.putAll(cuurentMapObj);
			setAttribute(key, value);
		}
	}

};
