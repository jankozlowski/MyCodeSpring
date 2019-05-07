package com.binaryalchemist.programondo.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.binaryalchemist.programondo.models.UploadFileResponse;
import com.binaryalchemist.programondo.services.FileStorageServiceImpl;

@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController {
	
	 @Autowired
	 private FileStorageServiceImpl fileStorageService;
	 
	 @PostMapping("/uploadFile/{username}")
	    public UploadFileResponse uploadFile(@PathVariable String username, @RequestParam("file") MultipartFile file) {
	        String fileName = fileStorageService.storeFile(file, username);

	        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/downloadFile/")
	                .path(fileName)
	                .toUriString();

	        return new UploadFileResponse(fileName, fileDownloadUri,
	                file.getContentType(), file.getSize());
	    }

	 
	 @GetMapping("/downloadFile/{username}/{fileName:.+}")
	    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName,@PathVariable String username, HttpServletRequest request) {
	        // Load file as Resource
		 
	        Resource resource = fileStorageService.loadFileAsResource(username+"/"+fileName);

	        // Try to determine file's content type
	        String contentType = null;
	        try {
	            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
	        } catch (IOException ex) {
	          
	        }

	        // Fallback to the default content type if type could not be determined
	        if(contentType == null) {
	            contentType = "application/octet-stream";
	        }

	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(contentType))
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
	                .body(resource);
	    }

}
