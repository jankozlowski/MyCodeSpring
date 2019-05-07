package com.binaryalchemist.programondo.controllers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binaryalchemist.programondo.models.CodeLog;
import com.binaryalchemist.programondo.models.Language;
import com.binaryalchemist.programondo.models.StatisticValue;
import com.binaryalchemist.programondo.repositories.LanguageRepository;
import com.binaryalchemist.programondo.repositories.LogRepository;
import com.binaryalchemist.programondo.util.TimeFunctions;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin
public class StatisticsController {

	@Autowired
	private LogRepository logRepository;
	
	@Autowired
	private LanguageRepository languageRepository;
	
	@GetMapping("{user}/all")
	public ArrayList<StatisticValue> getTimeStatistics(@PathVariable String user) {
		
		ArrayList<StatisticValue> returnValues = new ArrayList<StatisticValue>(); 
		
		Iterable<Language> allLanguages = languageRepository.findByUserName(user); 
		
		Iterable<CodeLog> allLogs = logRepository.findByUserName(user);
		
		returnValues = calculateStatisticTimes(allLanguages, allLogs);
		
		return returnValues;

	}
	
	@GetMapping("{user}/year")
	public ArrayList<StatisticValue> getTimeStatisticsFromYear(@PathVariable String user) {
		
		ArrayList<StatisticValue> returnValues = new ArrayList<StatisticValue>(); 
		
		Instant currentDate = Instant.now();
		
		Calendar startYearCalendar = Calendar.getInstance();
		startYearCalendar.set(Calendar.MONTH, 1);
		startYearCalendar.set(Calendar.DAY_OF_MONTH, 1);
		startYearCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startYearCalendar.set(Calendar.MINUTE, 0);
		startYearCalendar.set(Calendar.SECOND, 1);
		Date startYearDate = startYearCalendar.getTime();
		
		Instant startDate =  Instant.ofEpochMilli(startYearDate.getTime());
		
		Iterable<Language> allLanguages = languageRepository.findByUserNameAndDate(user, startDate, currentDate); 
		
		Iterable<CodeLog> allLogs = logRepository.findByUserNameAndDate(user, startDate, currentDate); 
		
		returnValues = calculateStatisticTimes(allLanguages, allLogs);
		
		return returnValues;
		
	}
	
	@GetMapping("{user}/month")
	public ArrayList<StatisticValue> getTimeStatisticsFromMonth(@PathVariable String user) {
		
		ArrayList<StatisticValue> returnValues = new ArrayList<StatisticValue>(); 
	
		Instant currentDate = Instant.now();

		Calendar startYearCalendar = Calendar.getInstance();
		startYearCalendar.set(Calendar.DAY_OF_MONTH, 1);
		startYearCalendar.set(Calendar.HOUR_OF_DAY, 0);
		startYearCalendar.set(Calendar.MINUTE, 0);
		startYearCalendar.set(Calendar.SECOND, 1);
		Date startYearDate = startYearCalendar.getTime();	
		
		Instant startDate =  Instant.ofEpochMilli(startYearDate.getTime());
		
		Iterable<Language> allLanguages = languageRepository.findByUserNameAndDate(user, startDate, currentDate); 
		
		Iterable<CodeLog> allLogs = logRepository.findByUserNameAndDate(user, startDate, currentDate); 
		
		returnValues = calculateStatisticTimes(allLanguages, allLogs);
		
		return returnValues;
		
	}
	
	public ArrayList<StatisticValue> calculateStatisticTimes(Iterable<Language> allLanguages, Iterable<CodeLog> allLogs) {
		ArrayList<StatisticValue> returnValues = new ArrayList<StatisticValue>(); 
		ArrayList<String> durationList = new ArrayList<>();
		
		HashSet<String> allLanguagesNames = new HashSet<>();
		for(Language selectedLanguage: allLanguages) {
			allLanguagesNames.add(selectedLanguage.getName());
		}
		
		for(String selectedLanguage: allLanguagesNames) {
			
			StatisticValue sv = new StatisticValue();
			sv.setName(selectedLanguage);
			
			for(CodeLog log :allLogs) {
				for(Language lan : log.getLanguage()) {
					
					if(lan.getName().equals(selectedLanguage)){
						durationList.add(log.getDuration());
						
						break;
					}
				}
			}
			String lastDuration ="00:00:00:000";
			for(String duration : durationList) {
				lastDuration = TimeFunctions.addTimeString(lastDuration,duration);
			}
			
			long seconds = TimeFunctions.timeToSeconds(lastDuration);
			sv.setSeconds(seconds);
			sv.setDuration(TimeFunctions.secondsToTime(seconds));
			returnValues.add(sv);
			
			durationList.clear();
			
		}
		return returnValues;
	}
	
	
	
	
}
