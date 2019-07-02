package com.binaryalchemist.programondo.models;

import java.util.Set;

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
public class Material {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long materialId;
	
	private String title;
	private String url;
	private String image;
	
	@ManyToOne
	@JoinColumn(name = "group_id")
	private MaterialGroup materialGroup;

	public Material() {
		super();
	}
	
	public Material(String title, String url, MaterialGroup materialGroup) {
		super();
		this.title = title;
		this.url = url;
		this.materialGroup = materialGroup;
	}

	
}
