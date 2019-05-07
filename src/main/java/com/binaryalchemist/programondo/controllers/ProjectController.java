package com.binaryalchemist.programondo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.binaryalchemist.programondo.models.CurrentProject;
import com.binaryalchemist.programondo.models.Project;
import com.binaryalchemist.programondo.models.ProjectTask;
import com.binaryalchemist.programondo.models.User;
import com.binaryalchemist.programondo.repositories.ProjectRepository;
import com.binaryalchemist.programondo.repositories.ProjectTaskRepository;
import com.binaryalchemist.programondo.repositories.UserRepository;
import com.binaryalchemist.programondo.services.UserServiceImpl;

@RestController
@RequestMapping("/api/project")
@CrossOrigin
public class ProjectController {
	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserServiceImpl userServiceImpl;

	@Autowired
	private ProjectTaskRepository projectTaskRepository;

	@GetMapping("{user}/all")
	public Iterable<Project> getAllProjects(@PathVariable String user) {
		
		return projectRepository.findByUserNameOrderByNameAsc(user);
	}

	@PostMapping("{user}/add")
	public ResponseEntity<?> addProject(@PathVariable String user, @RequestBody Project project,
			BindingResult results) {

		User foundUser = userServiceImpl.findByUsername(user);
		project.setUser(foundUser);

		Project savedProject = projectRepository.save(project);

		return new ResponseEntity<Project>(savedProject, HttpStatus.OK);

	}

	@PostMapping("{user}/update/{projectid}/selected/{selected}")
	public ResponseEntity<?> updateSelected(@PathVariable String user, @PathVariable long projectid,
			@PathVariable boolean selected) {

		Project foundProject = projectRepository.findById(projectid);

		foundProject.setSelected(selected);

		Project savedProject = projectRepository.save(foundProject);

		return new ResponseEntity<Project>(savedProject, HttpStatus.OK);

	}

	@PostMapping("{user}/update/{projectid}/compleated/{compleated}")
	public ResponseEntity<?> updateCompleated(@PathVariable String user, @PathVariable long projectid,
			@PathVariable boolean compleated) {

		Project foundProject = projectRepository.findById(projectid);

		foundProject.setCompleated(compleated);

		Project savedProject = projectRepository.save(foundProject);

		return new ResponseEntity<Project>(savedProject, HttpStatus.OK);

	}

	@PostMapping("{user}/update/{projectid}/name/{name}")
	public ResponseEntity<?> updateCompleated(@PathVariable String user, @PathVariable long projectid,
			@PathVariable String name) {

		Project foundProject = projectRepository.findById(projectid);

		foundProject.setName(name);

		Project savedProject = projectRepository.save(foundProject);

		return new ResponseEntity<Project>(savedProject, HttpStatus.OK);

	}

	@PostMapping("{user}/delete/{projectid}")
	public ResponseEntity<?> deleteCompleated(@PathVariable String user, @PathVariable long projectid) {

		projectRepository.deleteById(projectid);

		return new ResponseEntity<String>("Delete sucesfull", HttpStatus.OK);

	}

	@GetMapping("{user}/task/all")
	public Iterable<ProjectTask> getAllProjectTask(@PathVariable String user) {
		User foundUser = userServiceImpl.findByUsername(user);
		return projectTaskRepository.findByUserIdOrderByNameAsc(foundUser.getId());
		// return projectTaskRepository.findAll();

	}

	@PostMapping("{user}/add/task/{projectid}")
	public ResponseEntity<?> addTask(@PathVariable String user, @PathVariable long projectid,
			@RequestBody ProjectTask task, BindingResult results) {

		Project project = projectRepository.findById(projectid);
		task.setProject(project);

		ProjectTask savedTask = projectTaskRepository.save(task);

		return new ResponseEntity<ProjectTask>(savedTask, HttpStatus.OK);

	}

	@PostMapping("{user}/update/task/{taskid}/selected/{selected}")
	public ResponseEntity<?> updateSelectedTask(@PathVariable String user, @PathVariable long taskid,
			@PathVariable boolean selected) {

		ProjectTask task = projectTaskRepository.findById(taskid);
		task.setSelected(selected);

		ProjectTask savedTask = projectTaskRepository.save(task);

		return new ResponseEntity<ProjectTask>(savedTask, HttpStatus.OK);

	}

	@PostMapping("{user}/update/task/{taskid}/compleated/{selected}")
	public ResponseEntity<?> updateCompleatedTask(@PathVariable String user, @PathVariable long taskid,
			@PathVariable boolean selected) {

		ProjectTask task = projectTaskRepository.findById(taskid);
		task.setCompleated(selected);

		ProjectTask savedTask = projectTaskRepository.save(task);

		return new ResponseEntity<ProjectTask>(savedTask, HttpStatus.OK);

	}

	@PostMapping("{user}/update/task/{taskid}/name/{name}")
	public ResponseEntity<?> updateTaskName(@PathVariable String user, @PathVariable long taskid,
			@PathVariable String name) {

		System.out.println(name);
		
		ProjectTask task = projectTaskRepository.findById(taskid);
		task.setName(name);

		ProjectTask savedTask = projectTaskRepository.save(task);

		return new ResponseEntity<ProjectTask>(savedTask, HttpStatus.OK);

	}

	@PostMapping("{user}/delete/task/{taskid}")
	public ResponseEntity<?> deleteTask(@PathVariable String user, @PathVariable long taskid) {

		projectTaskRepository.deleteById(taskid);

		return new ResponseEntity<String>("Delete sucesfull", HttpStatus.OK);

	}

	@GetMapping("{user}/current")
	public ResponseEntity<?> getCurentProject(@PathVariable String user) {

		CurrentProject cp = new CurrentProject();

		User foundUser = userServiceImpl.findByUsername(user);

		Project project = projectRepository.findActiveProject(foundUser.getId());

		if (project == null) {
			cp.setName("None");
			cp.setTask("None");
			cp.setAllTasks(0);
			cp.setCompleated(0);
		} else {
			int compleated = 0;
			String selectedTask = "None";
			for (ProjectTask ts : project.getTasks()) {
				if (ts.isCompleated()) {
					compleated++;
				}
				if (ts.isSelected()) {
					selectedTask = ts.getName();
				}
			}

			cp.setName(project.getName());
			cp.setTask(selectedTask);
			cp.setCompleated(compleated);
			cp.setAllTasks(project.getTasks().size());
		}
		return new ResponseEntity<CurrentProject>(cp, HttpStatus.OK);
	}
	
	@PostMapping("{user}/task/clear")
	public ResponseEntity<?> clearActiveTask(@PathVariable String user) {

		User foundUser = userServiceImpl.findByUsername(user);
		ProjectTask pt = projectTaskRepository.findUserActiveTask(foundUser.getId());
		pt.setSelected(false);

		projectTaskRepository.save(pt);
		
		return new ResponseEntity<ProjectTask>(pt, HttpStatus.OK);
	}

}
