package br.gov.frameworkdemoiselle.fuselage.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.SequenceGenerator;

@Entity
public class SecurityProfileDetect implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "system-uuid")
	@SequenceGenerator(name = "system-uuid", sequenceName = "guid")
	private Long id;

	@Column
	private String name;

	@Column
	private String description;

	@Column
	private String implementation;

	@Column
	private String keyname;

	@Column
	private String value;

	@Column
	private String valuenotation;

	@ManyToMany
	@JoinTable(name = "SECURITYPROFILE_PROFILEDETECT", joinColumns = { @JoinColumn(name = "DETECT_ID") }, inverseJoinColumns = { @JoinColumn(name = "PROFILE_ID") })
	private List<SecurityProfile> profiles;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImplementation() {
		return implementation;
	}

	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}

	public String getKeyName() {
		return keyname;
	}

	public void setKeyName(String keyname) {
		this.keyname = keyname;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValuenotation() {
		return valuenotation;
	}

	public void setValuenotation(String valuenotation) {
		this.valuenotation = valuenotation;
	}

	public List<SecurityProfile> getProfiles() {
		return profiles;
	}

	public void setProfiles(List<SecurityProfile> profiles) {
		this.profiles = profiles;
	}

}
