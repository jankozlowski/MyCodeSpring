package com.binaryalchemist.programondo.models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User extends DateAudit {

	User(){
		
	}
	
	public User(String name, String password, String email) {
		this.name = name;
		this.password = password;
		this.email = email;
		this.active = false;
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id", unique = true, nullable = false)
	private long id;

	
	private boolean active;
	@NotBlank
	@Email
	@Column(unique = true)
	private String email;
	@NotBlank
	private String password;
	@NotBlank
	@Column(unique = true)
	private String name;
	
  /*  @OneToOne(targetEntity = PasswordResetToken.class, fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(nullable = false, name = "user_id")
    private PasswordResetToken passwordToken;
	*/
	@JsonIgnore
	@ManyToMany
    @JoinTable(name = "user_role", joinColumns = {@JoinColumn(name = "user_id")}, inverseJoinColumns = {@JoinColumn(name = "role_id")})
	private Set<Role> roles;
	
	@OneToMany(mappedBy="user")
	@JsonIgnoreProperties("user")
	private Set<Project> projects = new HashSet<>();
	
	@OneToMany(mappedBy="user")
	@JsonIgnoreProperties("user")
	private Set<CodeLog> codeLogs;
	
	@ManyToOne
	@JoinColumn(name = "group_id")
	private MaterialGroup materialGroup;
	
	@OneToOne(mappedBy="user")
	@JsonIgnoreProperties("user")
	private UserDetails userDetails;
	
}