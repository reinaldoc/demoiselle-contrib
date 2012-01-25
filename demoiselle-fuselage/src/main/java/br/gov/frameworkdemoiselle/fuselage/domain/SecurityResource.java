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
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
public class SecurityResource implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "system-uuid")
	@SequenceGenerator(name = "system-uuid", sequenceName = "guid")
	private Long id;

	@Column
	@NotBlank(message = "Melhore o nome deste recurso")
	@Size(min = 3, max = 255, message = "Melhore o nome deste recurso")
	private String name;

	@Column(unique = true)
	@NotBlank(message = "Especifique melhor o valor do recurso")
	@Size(min = 1, max = 255, message = "Especifique melhor o valor do recurso")
	private String value;

	@Column
	@NotBlank(message = "Melhore a descrição deste recurso")
	@Size(min = 10, max = 255, message = "Melhore a descrição deste recurso")
	private String description;

	@ManyToMany
	@JoinTable(name = "SECURITYROLE_RESOURCE", joinColumns = { @JoinColumn(name = "RESOURCE_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
	private List<SecurityRole> roles;

	public SecurityResource() {

	}

	public SecurityResource(String name) {
		this.name = name;
	}

	public SecurityResource(String name, String value, String desc) {
		this.name = name;
		this.value = value;
		this.description = desc;
	}

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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<SecurityRole> getRoles() {
		return roles;
	}

	public void setRoles(List<SecurityRole> roles) {
		this.roles = roles;
	}

	public String getLabel() {
		return value + " (" + name + ")";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
