package com.binaryalchemist.programondo.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProjectTask {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "task_id", unique = true, nullable = false)
	private long taskId;
	private String name;
	private boolean compleated;
	private boolean selected;
	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;
	
	
}
