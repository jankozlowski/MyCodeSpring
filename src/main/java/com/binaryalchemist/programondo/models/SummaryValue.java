package com.binaryalchemist.programondo.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryValue {

	private int sessionCount;
	private String sumTime;
	private long charSum;
	private int compleatedProjects;
	private int allProjects;
	private long compleatedTasks;
	private long allTasks;
	private Float averageCharTime;
	private String averageTimeInDay;
	
	
	
}
