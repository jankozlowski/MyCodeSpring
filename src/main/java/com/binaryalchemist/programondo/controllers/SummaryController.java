package com.binaryalchemist.programondo.controllers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binaryalchemist.programondo.models.CodeLog;
import com.binaryalchemist.programondo.models.Project;
import com.binaryalchemist.programondo.models.ProjectTask;
import com.binaryalchemist.programondo.models.SummaryValue;
import com.binaryalchemist.programondo.models.User;
import com.binaryalchemist.programondo.repositories.LanguageRepository;
import com.binaryalchemist.programondo.repositories.LogRepository;
import com.binaryalchemist.programondo.repositories.ProjectRepository;
import com.binaryalchemist.programondo.services.UserServiceImpl;
import com.binaryalchemist.programondo.util.TimeFunctions;

@RestController
@RequestMapping("/api/summary")
@CrossOrigin
public class SummaryController {

	
	@Autowired
	private LogRepository logRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserServiceImpl userServiceImpl;
	
	@GetMapping("{user}")
	public SummaryValue getUserSummary(@PathVariable String user) {
		
		SummaryValue sv = new SummaryValue();
		
		Iterable<CodeLog> allLogs = logRepository.findByUserName(user);
		
		long allChars=0;
		String lastDuration ="00:00:00:000";
		
		for(CodeLog code : allLogs) {
			allChars += code.getCharSum();
			lastDuration = TimeFunctions.addTimeString(lastDuration,code.getDuration());
		}
		long seconds = TimeFunctions.timeToSeconds(lastDuration);
		String durationSum = TimeFunctions.secondsToTime(seconds);
		
		Iterable<Project> allProjects = projectRepository.findByUserName(user);
		
		int projectsCount = 0;
		int compleatedProjects = 0;
		
		long allTasks = 0;
		long compleatedTasks = 0;
		
		for(Project project: allProjects) {
			projectsCount++;
			if(project.isCompleated()) {
				compleatedProjects++;
			}
			for(ProjectTask task: project.getTasks()) {
				allTasks++;
				if(task.isCompleated()) {
					compleatedTasks++;
				}
				
			}
		}
		
		
		User foundUser = userServiceImpl.findByUsername(user);
		Instant now = Instant.now();
		Instant accountCreated = foundUser.getCreatedAt();
		
		long daysFromAccountCreation = accountCreated.until(now, ChronoUnit.DAYS);
		if(daysFromAccountCreation == 0) {
			daysFromAccountCreation = 1;
		}
		
		long minutes = (Math.round(seconds/60));
		if(minutes == 0) {
			minutes =1;
		}
		
		sv.setSessionCount(logRepository.countByUserName(user));
		sv.setCharSum(allChars);
		sv.setAllProjects(projectsCount);
		sv.setCompleatedProjects(compleatedProjects);
		sv.setAllTasks(allTasks);
		sv.setCompleatedTasks(compleatedTasks);
		sv.setSumTime(durationSum);
		sv.setAverageCharTime(Float.valueOf(allChars/minutes));
		sv.setAverageTimeInDay(TimeFunctions.secondsToTime(Math.round(seconds/daysFromAccountCreation)));		
		
		return sv;
		
	}
	
}
