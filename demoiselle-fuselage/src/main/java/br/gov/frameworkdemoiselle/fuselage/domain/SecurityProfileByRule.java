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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
public class SecurityProfileByRule implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "system-uuid")
	@SequenceGenerator(name = "system-uuid", sequenceName = "guid")
	private Long id;

	@Column
	@NotBlank(message = "Especifique melhor o nome da regra")
	@Size(min = 3, max = 255, message = "Especifique melhor o nome da regra")
	private String name;

	@Column
	@NotBlank(message = "Descreva melhor a regra")
	@Size(min = 10, max = 255, message = "Descreva melhor a regra")
	private String description;

	@Column
	@NotBlank(message = "Selecione a implementação")
	@Size(min = 3, max = 255, message = "Selecione a implementação")
	private String implementation;

	@Column
	@Size(max = 255, message = "Identifique melhor o nome da chave")
	private String keyname;

	@Column
	@Size(max = 255, message = "Identifique melhor o valor da chave")
	private String value;

	@Column
	@Size(max = 255, message = "Identifique melhor a notação da chave")
	private String valuenotation;

	@Column
	@NotNull
	private Integer available;

	@ManyToMany
	@JoinTable(name = "SECURITYPROFILE_BYRULE", joinColumns = { @JoinColumn(name = "RULE_ID") }, inverseJoinColumns = { @JoinColumn(name = "PROFILE_ID") })
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

	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
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

	public Integer getAvailable() {
		return available;
	}

	public void setAvailable(Integer available) {
		this.available = available;
	}

	public boolean isEnabled() {
		if (this.available != null && this.available.intValue() == 1)
			return true;
		return false;
	}

}
