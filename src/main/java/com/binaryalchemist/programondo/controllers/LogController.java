package com.binaryalchemist.programondo.controllers;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binaryalchemist.programondo.models.CodeLog;
import com.binaryalchemist.programondo.models.Language;
import com.binaryalchemist.programondo.models.Project;
import com.binaryalchemist.programondo.models.User;
import com.binaryalchemist.programondo.repositories.LanguageRepository;
import com.binaryalchemist.programondo.repositories.LogRepository;
import com.binaryalchemist.programondo.repositories.ProjectRepository;
import com.binaryalchemist.programondo.services.UserServiceImpl;

@RestController
@RequestMapping("/api/log")
@CrossOrigin
public class LogController {

	@Autowired
	private LogRepository logRepository;

	@Autowired
	private LanguageRepository languageRepository;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@GetMapping("all")
	public Iterable<CodeLog> getAllLogs() {
		return logRepository.findAll();

	}

	@GetMapping("{user}/page/{page}/{num}")
	public Iterable<CodeLog> getUserLogs(@PathVariable int page, @PathVariable int num, @PathVariable String user) {
		// return logRepository.findAll();

		return logRepository.findByUserName(user, PageRequest.of(page, num, Sort.Direction.DESC, "logId"));

	}

	@GetMapping("{user}/last/{num}")
	public Iterable<CodeLog> getUserXLastLogs(@PathVariable int num, @PathVariable String user) {

		return logRepository.findByUserName(user, PageRequest.of(0, num, Sort.Direction.DESC, "logId"));

	}

	@GetMapping("{user}/size")
	public int getUserLogSize(@PathVariable String user) {

		return logRepository.countByUserName(user);

	}

	@GetMapping("{id}")
	public Optional<CodeLog> getLogById(@PathVariable long id) {

		return logRepository.findById(id);

	}

	@PostMapping("add/{username}/{projectid}")
	public ResponseEntity<?> addLogToDb(@PathVariable String username, @PathVariable long projectid,
			@RequestBody CodeLog codeLog, BindingResult results) {

		Set<Language> lan = codeLog.getLanguage();

		User user = userServiceImpl.findByUsername(username);
		codeLog.setUser(user);
		codeLog.setCreatedAt(Instant.now());
		codeLog.setUpdatedAt(Instant.now());

		System.out.println(projectid);
		
		if (projectid == -1) {
			codeLog.setProject(null);
		} else {
			Project pr = projectRepository.findById(projectid);
			codeLog.setProject(pr);
		}
		CodeLog savedLog = logRepository.save(codeLog);

		for (Language l : lan) {
			l.setCodeLog(savedLog);
			languageRepository.save(l);
		}

		return new ResponseEntity<CodeLog>(codeLog, HttpStatus.OK);

	}

	@DeleteMapping("delete/{logId}")
	public ResponseEntity<?> deleteLogFromDb(@PathVariable long logId){
		
		logRepository.deleteById(logId);
		return new ResponseEntity<String>("Log Deleted", HttpStatus.OK);
		
	}
	
}
