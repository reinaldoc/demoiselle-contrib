package org.example.project1.bookmark.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

@Entity
public class Bookmark implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Column
	@NotEmpty(message = "Selecione a categoria")
	private String category;

	@Column
	@Size(min = 5, max = 255, message = "Especifique melhor a descrição")
	private String description;

	@Column
	@Size(min = 5, max = 255, message = "Especifique melhor o link")
	private String link;

	public Bookmark() {
		super();
	}

	public Bookmark(String category, String description, String link) {
		this.category = category;
		this.description = description;
		this.link = link;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

}
