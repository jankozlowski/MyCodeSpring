package com.binaryalchemist.programondo.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.binaryalchemist.programondo.models.Book;
import com.binaryalchemist.programondo.models.CodeLog;
import com.binaryalchemist.programondo.models.UploadFileResponse;
import com.binaryalchemist.programondo.services.FileStorageServiceImpl;
import com.binaryalchemist.programondo.util.PCloudUtils;
import com.binaryalchemist.programondo.constants.Constants;
import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.Authenticators;
import com.pcloud.sdk.Call;
import com.pcloud.sdk.DataSource;
import com.pcloud.sdk.DownloadOptions;
import com.pcloud.sdk.FileLink;
import com.pcloud.sdk.PCloudSdk;
import com.pcloud.sdk.ProgressListener;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

@RestController
@RequestMapping("/api/file")
@CrossOrigin
public class FileController {
	
	 @Autowired
	 private FileStorageServiceImpl fileStorageService;
	 @Autowired 
	 private PCloudUtils pCloudUtils;
	 
	 @PostMapping("/uploadFile/{username}")
	    public UploadFileResponse saveFile(@PathVariable String username, @RequestParam("file") MultipartFile file) {
	        String fileName = fileStorageService.storeFile(file, username);

	        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
	                .path("/downloadFile/")
	                .path(fileName)
	                .toUriString();

	        return new UploadFileResponse(0,fileName, fileDownloadUri,
	                file.getContentType(), file.getSize());
	    }
	 
	 @PostMapping("/uploadFile/{username}/pdf")
	    public UploadFileResponse uploadPDFFile(@PathVariable String username, @RequestParam("file") MultipartFile file) throws IOException, ApiError {
	        
		 ApiClient apiClient = PCloudSdk.newClientBuilder()
	                .authenticator(Authenticators.newOAuthAuthenticator(Constants.pCloudToken))
	                .create();
		
		 
		 RemoteFile uplodedFile = pCloudUtils.uploadFileWithChecks(apiClient, username, "books", file.getBytes(), file.getOriginalFilename());
		 
		
		 apiClient.shutdown();
	
	        return new UploadFileResponse(uplodedFile.fileId(),uplodedFile.name(), String.valueOf(uplodedFile.createFileLink().bestUrl()),
	                file.getContentType(), file.getSize());
	    }
	 
	 @DeleteMapping("/deleteFile/{username}/pdf/{fileId}")
	    public ResponseEntity<String> deletePDFFile(@PathVariable String username, @PathVariable long fileId) throws IOException, ApiError  {
		 
		 ApiClient apiClient = PCloudSdk.newClientBuilder()
	                .authenticator(Authenticators.newOAuthAuthenticator(Constants.pCloudToken))
	                .create();
		 
		 RemoteFolder userBooksFolder = pCloudUtils.findFolderInUserFolder(apiClient, username, "books");
		 for(int index=0; index<userBooksFolder.children().size(); index++) {
			 if(userBooksFolder.children().get(index).asFile().fileId()==fileId) {
				 apiClient.deleteFile(userBooksFolder.children().get(index).asFile().fileId()).execute();
			 }
		 }
		 
		 
		 return new ResponseEntity<String>("OK", HttpStatus.OK);
	 }
	 
	 @GetMapping("/downloadBook/{username}/all")
	 public ResponseEntity<ArrayList<Book>> downloadBooks(@PathVariable String username) throws IOException, ApiError {
	 
		 ApiClient apiClient = PCloudSdk.newClientBuilder()
	                .authenticator(Authenticators.newOAuthAuthenticator(Constants.pCloudToken))
	                .create();
		 
		 RemoteFolder userBooksFolder = pCloudUtils.findFolderInUserFolder(apiClient, username, "books");
		 
		 ArrayList<Book> books = new ArrayList<>();
		 books = pCloudUtils.getBooksFrompCloud(apiClient,userBooksFolder);
		 
		 apiClient.shutdown();
		 
		 return new ResponseEntity<ArrayList<Book>>(books, HttpStatus.OK);
		 
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
