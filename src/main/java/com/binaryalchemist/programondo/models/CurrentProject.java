package com.binaryalchemist.programondo.models;

import java.util.Set;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentProject {

	private String name;
	private String task;
	private int compleated;
	private int allTasks;
	
	
}
