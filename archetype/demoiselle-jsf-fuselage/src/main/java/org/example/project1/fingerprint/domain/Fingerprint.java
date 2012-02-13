package org.example.project1.fingerprint.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Fingerprint implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column
	@NotEmpty(message = "Selecione a categoria")
	private String category;

	@Column
	@Size(min = 5, max = 255, message = "Especifique melhor servidor")
	private String serverName;

	@Column
	@Size(min = 5, max = 255, message = "Especifique melhor fignerprint")
	private String fingerprint;

	public Fingerprint() {
		super();
	}

	public Fingerprint(String category, String description, String link) {
		this.category = category;
		this.serverName = description;
		this.fingerprint = link;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

}
