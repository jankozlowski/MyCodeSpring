package com.binaryalchemist.programondo.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.binaryalchemist.programondo.FileStorageProperties;

@Service
public class FileStorageServiceImpl {


    public String storeFile(MultipartFile file, String username) {
        
    	Path fileStorageLocation = Paths.get("uploads", username).toAbsolutePath().normalize();
    	try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception ex) {
            System.out.println("Could not create the directory where the uploaded files will be stored." + ex);
        }
    	
    	// Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                System.out.println("Sorry! Filename contains invalid path sequence " + fileName);
            }

            // Copy file to the target location (Replacing existing file with the same name)
            
            Path targetLocation = fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            System.out.println("Could not store file " + fileName + ". Please try again!" + ex);
        }
		return fileName;
    }

    public Resource loadFileAsResource(String fileName) {
    	Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();
        try {
            Path filePath = fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                System.out.println("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            System.out.println("File not found " + fileName +" " + ex);
        }
		return null;
    }
}