package com.binaryalchemist.programondo.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MaterialGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long groupId;
	
	private String title;
	
	@OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true, mappedBy="materialGroup")
	@JsonIgnoreProperties("materialGroup")
	private List<Material> materials = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
}
