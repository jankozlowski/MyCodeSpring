package com.binaryalchemist.programondo.controllers;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.InvalidArgumentException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.binaryalchemist.programondo.constants.Constants;
import com.binaryalchemist.programondo.models.Book;
import com.binaryalchemist.programondo.models.Material;
import com.binaryalchemist.programondo.models.MaterialGroup;
import com.binaryalchemist.programondo.models.ScreenShot;
import com.binaryalchemist.programondo.models.User;
import com.binaryalchemist.programondo.repositories.MaterialGroupRepository;
import com.binaryalchemist.programondo.repositories.MaterialRepository;
import com.binaryalchemist.programondo.services.UserServiceImpl;
import com.binaryalchemist.programondo.util.PCloudUtils;
import com.binaryalchemist.programondo.util.ReadWriteFunctions;
import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.Authenticators;
import com.pcloud.sdk.PCloudSdk;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/materials")

public class MaterialController {

	@Autowired
	private MaterialRepository materialRepository;
	@Autowired
	private MaterialGroupRepository materialGroupRepository;
	@Autowired
	private UserServiceImpl userServiceImpl;
	@Autowired 
	private PCloudUtils pCloudUtils;
	@Autowired
	private ResourceLoader resourceLoader;
	
	@GetMapping("/{username}/all")
	public Iterable<MaterialGroup> downloadAllMaterials(@PathVariable String username) throws IOException, ApiError {
		
		ApiClient apiClient = PCloudSdk.newClientBuilder()
	                .authenticator(Authenticators.newOAuthAuthenticator(Constants.pCloudToken))
	                .create();
		RemoteFolder screensFolder = pCloudUtils.findFolderInUserFolder(apiClient, username, "screens");
		ArrayList<ScreenShot> screens = pCloudUtils.getScreensFromCloud(apiClient, screensFolder);
		apiClient.shutdown();
		
		Iterable<MaterialGroup> materialGroups = materialGroupRepository.findAllUserMaterialsGroupsWithMaterials(username);
		materialGroups = setScreenShotsForAllMaterialsInMaterialGroups(materialGroups, screens);

		
		return materialGroups;
		
	}
	
	@PostMapping("/{username}/add/{groupId}")
	public ResponseEntity<Material> addMaterial(@PathVariable String username,  @PathVariable long groupId, @RequestParam("url") String url,  @RequestParam("title") String title) throws IOException, ApiError{
		
		Optional<MaterialGroup> materialGroup = materialGroupRepository.findById(groupId);
		
		Material material = new Material(title, url, materialGroup.get());
		materialGroup.get().getMaterials().add(material);
		 
		Material savedMaterial = materialRepository.save(material);
		materialGroupRepository.save(materialGroup.get()); 
		
		File screenShot = makePageScreenShot(url, String.valueOf(savedMaterial.getMaterialId()));
		if(screenShot == null) {
			savedMaterial.setImage(Constants.backEndUrl+"img/broken.png");
		}
		else {
		ApiClient apiClient = PCloudSdk.newClientBuilder()
                .authenticator(Authenticators.newOAuthAuthenticator(Constants.pCloudToken))
                .create();
		
		RemoteFile remoteFile = pCloudUtils.uploadFileWithChecks(apiClient, username, "screens", Files.readAllBytes(screenShot.toPath()), screenShot.getName());
		savedMaterial.setImage(String.valueOf(remoteFile.asFile().createFileLink().bestUrl()));
		apiClient.shutdown();
		}
		materialRepository.save(savedMaterial);
		
	
		
		return new ResponseEntity<Material>(savedMaterial, HttpStatus.OK);
		
	}
	@PostMapping("/{username}/add")
	public ResponseEntity<MaterialGroup> addMaterialGroup(@PathVariable String username){
		
		User user = userServiceImpl.findByUsername(username);
		MaterialGroup mg = new MaterialGroup();
		mg.setTitle("Title");
		mg.setUser(user);
		
		MaterialGroup mgs = materialGroupRepository.save(mg);
		
		return new ResponseEntity<MaterialGroup>(mgs, HttpStatus.OK);
				
	}
	@PostMapping("/{username}/update/material/{materialId}")
	public ResponseEntity<Material> updateMaterial(@PathVariable long materialId, @PathVariable String username, @RequestParam("url") String url,  @RequestParam("title") String title) throws IOException, ApiError{
		
		Optional<Material> material = materialRepository.findById(materialId);
		material.get().setTitle(title);
		if(!material.get().getUrl().equals(url)) {
			
			ApiClient apiClient = PCloudSdk.newClientBuilder()
	                .authenticator(Authenticators.newOAuthAuthenticator(Constants.pCloudToken))
	                .create();
			File screenShot = makePageScreenShot(url, String.valueOf(material.get().getMaterialId()));
			RemoteFile remoteFile = pCloudUtils.uploadFileWithChecks(apiClient, username, "screens", Files.readAllBytes(screenShot.toPath()), screenShot.getName());
			material.get().setImage(String.valueOf(remoteFile.asFile().createFileLink().bestUrl()));
			material.get().setUrl(url);
			apiClient.shutdown();
		}
		
		materialRepository.save(material.get());
		
		return new ResponseEntity<Material>(material.get(), HttpStatus.OK);
				
	}
	@PostMapping("/{username}/update/materialgroup/{materialGroupId}")
	public ResponseEntity<MaterialGroup> updateMaterialGroup(@PathVariable long materialGroupId, @RequestParam("title") String title){
		
		Optional<MaterialGroup> materialGroup = materialGroupRepository.findById(materialGroupId);
		materialGroup.get().setTitle(title);
		materialGroupRepository.save(materialGroup.get());
		
		return new ResponseEntity<MaterialGroup>(materialGroup.get(), HttpStatus.OK);
				
	}
	
	@DeleteMapping("/{username}/delete/material/{materialId}")
	public ResponseEntity<String> deleteMaterial(@PathVariable long materialId){
		
		materialRepository.deleteById(materialId);
		
		return new ResponseEntity<String>("OK", HttpStatus.OK);
		
	}
	
	@DeleteMapping("/{username}/delete/materialgroup/{materialGroupId}")
	public ResponseEntity<String> deleteMaterialGroup(@PathVariable long materialGroupId){
		
		materialGroupRepository.deleteById(materialGroupId);
		
		return new ResponseEntity<String>("OK", HttpStatus.OK);
		
	}
	
	public Iterable<MaterialGroup> setScreenShotsForAllMaterialsInMaterialGroups(Iterable<MaterialGroup> materialGroups, ArrayList<ScreenShot> screens){
		
		
		
		for(MaterialGroup mg : materialGroups) {
			for(Material material: mg.getMaterials()) {
				boolean exists = false;
				for(ScreenShot shot: screens) {
					if(shot.getId()==material.getMaterialId()) {
						material.setImage(shot.getUrl());
						exists = true;
						break;
					}
				}
				if(!exists) {
					material.setImage(Constants.backEndUrl+"img/broken.png");
				}
			}
		}
		
		return materialGroups;
		
	}
	

	public File makePageScreenShot(String url, String name) throws IOException{
		
		Resource resource = resourceLoader.getResource("classpath:\\static\\geckodriver.exe");
        InputStream dbAsStream = resource.getInputStream();
        
        Path path = Files.createTempFile("geckodrive", ".exe");
        File file = path.toFile();
        Files.write(path, StreamUtils.copyToByteArray(dbAsStream));
             
		System.setProperty("webdriver.gecko.driver",file.getAbsolutePath());
		
		File screenShot = new File(name);
		FirefoxOptions options = new FirefoxOptions();
		options.addArguments("--headless");
		final WebDriver webDriver = new FirefoxDriver(options);
		
		try {
			webDriver.get(url);
		}
		catch(InvalidArgumentException e) {
			return null;
		}
		screenShot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
		String newPath = screenShot.getAbsolutePath().substring(0,screenShot.getAbsolutePath().lastIndexOf("\\"));
		File screenShotWithIdName =  new File(newPath+"\\"+name+".png");
		FileUtils.copyFile(screenShot, screenShotWithIdName);
        
		webDriver.close();
		file.delete();
		
		return screenShotWithIdName;
	}
	
	
	
}
