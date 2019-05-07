package com.binaryalchemist.programondo.models;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
public class CodeLog extends DateAudit {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "log_id", unique = true, nullable = false)
	private long logId;
	private String duration;
	@ElementCollection
	@CollectionTable(
	        name="date_click", joinColumns=@javax.persistence.JoinColumn(name="log_id")
	      
	  )
	private List<Instant> clickDate;
	
	@ElementCollection
	@CollectionTable(
	        name="char_count", joinColumns=@javax.persistence.JoinColumn(name="log_id")
	      
	  )
	private Map<Integer,Integer> charCount;
	
	@OneToMany(mappedBy="codeLog")
	@JsonIgnoreProperties("codeLog")
	private Set<Language> language = new HashSet<>();
	
	@ManyToOne
	@JoinColumn(name = "user")
	private User user;
	
	
	@ManyToOne
	@JoinColumn(name="project_id")
	private Project project;
	
	private int charSum;
	private Instant recordStart;
}
