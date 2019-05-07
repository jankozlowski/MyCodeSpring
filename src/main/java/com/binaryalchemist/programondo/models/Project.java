package com.binaryalchemist.programondo.models;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Project {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "project_id", unique = true, nullable = false)
	private long projectId;
	private String name;
	private boolean selected;
	private boolean compleated;
	
	@JsonIgnore
	@OneToMany(mappedBy="project")
	private Set<CodeLog> codeLogs = new HashSet<>();
	
	@OneToMany(mappedBy="project")
	@JsonIgnoreProperties("project")
	private Set<ProjectTask> tasks = new HashSet<>();
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

}
