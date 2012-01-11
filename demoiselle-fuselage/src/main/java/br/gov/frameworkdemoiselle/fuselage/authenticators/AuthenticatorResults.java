package br.gov.frameworkdemoiselle.fuselage.authenticators;

import java.util.HashMap;
import java.util.Map;

public class AuthenticatorResults {

	public String authenticatorModuleName;

	public String uid;

	public String commonName;

	public String organizationalUnit;

	public String description;

	public Map<String, String> genericResults = new HashMap<String, String>();

	public Map<String, String> getGenericResults() {
		return genericResults;
	}

	public void setGenericResults(Map<String, String> genericResults) {
		this.genericResults = genericResults;
	}

	public void putGenericResults(String key, String value) {
		this.genericResults.put(key, value);
	}

	public Map<String, String> getUserResults() {
		Map<String, String> allResults = new HashMap<String, String>();
		if (uid != null)
			allResults.put("uid", uid);
		if (commonName != null)
			allResults.put("commonName", commonName);
		if (organizationalUnit != null)
			allResults.put("organizationalUnit", commonName);
		if (description != null)
			allResults.put("description", description);
		return allResults;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getOrganizationalUnit() {
		return organizationalUnit;
	}

	public void setOrganizationalUnit(String organizationalUnit) {
		this.organizationalUnit = organizationalUnit;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAuthenticatorModuleName() {
		return authenticatorModuleName;
	}

	public void setAuthenticatorModuleName(String authenticatorModuleName) {
		this.authenticatorModuleName = authenticatorModuleName;
	}

}
