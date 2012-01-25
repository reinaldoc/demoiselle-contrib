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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

@Entity
public class SecurityProfile implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "system-uuid")
	@SequenceGenerator(name = "system-uuid", sequenceName = "guid")
	private Long id;

	@Column
	@NotBlank(message = "Especifique melhor o nome do perfil")
	@Size(min = 3, max = 255, message = "Especifique melhor o nome do perfil")
	private String name;

	@Column
	@NotBlank(message = "Melhore a descrição curta deste perfil")
	@Size(min = 10, max = 255, message = "Melhore a descrição curta deste perfil")
	private String shortDescription;

	@Column
	@NotBlank(message = "Melhore a descrição deste perfil")
	@Size(min = 20, max = 255, message = "Melhore a descrição deste perfil")
	private String description;

	@Column
	private Long welcomePagePriority;

	@OneToOne
	private SecurityResource welcomePage;

	@ManyToMany
	@JoinTable(name = "SECURITYUSER_PROFILE", joinColumns = { @JoinColumn(name = "PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "USER_ID") })
	private List<SecurityUser> users;

	@ManyToMany
	@JoinTable(name = "SECURITYPROFILE_ROLE", joinColumns = { @JoinColumn(name = "PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
	private List<SecurityRole> roles;

	@ManyToMany
	@JoinTable(name = "SECURITYPROFILE_BYRULE", joinColumns = { @JoinColumn(name = "PROFILE_ID") }, inverseJoinColumns = { @JoinColumn(name = "RULE_ID") })
	private List<SecurityProfileByRule> rules;

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

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getWelcomePagePriority() {
		return welcomePagePriority;
	}

	public void setWelcomePagePriority(Long welcomePagePriority) {
		this.welcomePagePriority = welcomePagePriority;
	}

	public SecurityResource getWelcomePage() {
		return welcomePage;
	}

	public void setWelcomePage(SecurityResource welcomePage) {
		this.welcomePage = welcomePage;
	}

	public List<SecurityUser> getUsers() {
		return users;
	}

	public void setUsers(List<SecurityUser> users) {
		this.users = users;
	}

	public List<SecurityRole> getRoles() {
		return roles;
	}

	public void setRoles(List<SecurityRole> roles) {
		this.roles = roles;
	}

	public List<SecurityProfileByRule> getRules() {
		return rules;
	}

	public void setRules(List<SecurityProfileByRule> rules) {
		this.rules = rules;
	}

}
