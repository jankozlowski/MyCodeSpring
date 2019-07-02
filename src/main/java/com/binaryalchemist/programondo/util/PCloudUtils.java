package com.binaryalchemist.programondo.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.binaryalchemist.programondo.models.Book;
import com.binaryalchemist.programondo.models.ScreenShot;
import com.pcloud.sdk.ApiClient;
import com.pcloud.sdk.ApiError;
import com.pcloud.sdk.DataSource;
import com.pcloud.sdk.DownloadOptions;
import com.pcloud.sdk.FileLink;
import com.pcloud.sdk.ProgressListener;
import com.pcloud.sdk.RemoteFile;
import com.pcloud.sdk.RemoteFolder;

@Component
public class PCloudUtils {

	public boolean checkIfFolderExists(String name, RemoteFolder folder) {

		 boolean exists = false;
		 for(int index=0; index<folder.children().size(); index++) {
			 if(folder.children().get(index).isFolder() && folder.children().get(index).name().equals(name)) {
				 exists = true;
			 }
		 }
		 
		 return exists;
	 }
	 
	 public RemoteFolder findFolderChild(ApiClient apiClient, String childName, RemoteFolder parentFolder) throws IOException, ApiError {
		
		RemoteFolder searchedFolder = null;
		for(int index=0; index<parentFolder.children().size(); index++) {
			
			 if(parentFolder.children().get(index).isFolder() && parentFolder.children().get(index).name().equals(childName)) {
				 searchedFolder = parentFolder.children().get(index).asFolder();
				 searchedFolder = apiClient.listFolder(searchedFolder.folderId()).execute(); 
				 break;
			 }
		 }
		
		 return searchedFolder;
	 }
	 
	 public void createNamedFolderAndUsernameFoldersIfNotExists(ApiClient apiClient, String userName, String folderName) throws IOException, ApiError {
		 
		 
		 RemoteFolder rootFolder = apiClient.listFolder(RemoteFolder.ROOT_FOLDER_ID).execute(); 
		 if(!checkIfFolderExists(userName, rootFolder)) {
			 apiClient.createFolder(rootFolder.folderId(), userName).execute();
			 rootFolder = apiClient.listFolder(RemoteFolder.ROOT_FOLDER_ID).execute();
		 }
		 
		 RemoteFolder userFolder = findFolderChild(apiClient, userName, rootFolder);
		 if(!checkIfFolderExists(folderName, userFolder)) {
			 apiClient.createFolder(userFolder.folderId(), folderName).execute();
		 }
	 }
	
	 public RemoteFile uploadFile(ApiClient apiClient, byte[] file, String fileName, long remoteFolderUploadId) throws IOException, ApiError {
		 
	        return apiClient.createFile(remoteFolderUploadId, fileName, DataSource.create(file), new Date(), new ProgressListener() {
	            public void onProgress(long done, long total) {
	                System.out.format("\rUploading... %.1f\n", ((double) done / (double) total) * 100d);
	                
	            }
	        }).execute();
	   }
	 
	 public RemoteFile uploadFileWithChecks(ApiClient apiClient, String username, String uploadFolderName, byte[] uploadFileBytes, String uploadFileName) throws IOException, ApiError {
		
		 createNamedFolderAndUsernameFoldersIfNotExists(apiClient,username, uploadFolderName);
		 RemoteFolder rootFolder = apiClient.listFolder(RemoteFolder.ROOT_FOLDER_ID).execute(); 
		 RemoteFolder userFolder = findFolderChild(apiClient,username, rootFolder);
		 RemoteFolder userBooksFolder = findFolderChild(apiClient,uploadFolderName, userFolder);
		 RemoteFile uplodedFile = uploadFile( apiClient, uploadFileBytes, uploadFileName, userBooksFolder.folderId());
		 
		 return uplodedFile;
	 
	 }
	 
	 public ArrayList<Book> getBooksFrompCloud( ApiClient apiClient, RemoteFolder folder) throws IOException, ApiError {
		 
		 ArrayList<Book> books = new ArrayList<>();
		 
		 for(int index=0; index<folder.children().size(); index++) {
			 Book book = new Book();
			 
			 if(folder.children().get(index).isFile()) {
				 FileLink downloadLink = apiClient.createFileLink((RemoteFile) folder.children().get(index), DownloadOptions.DEFAULT).execute();
				 book.setLink(String.valueOf(downloadLink.bestUrl()));
				 book.setTitle(folder.children().get(index).name());
				 book.setId(folder.children().get(index).asFile().fileId());
				 books.add(book);
			 }
		 }
		 
		 return books;
	 }
	 
	 public ArrayList<ScreenShot> getScreensFromCloud( ApiClient apiClient, RemoteFolder folder) throws IOException, ApiError {
		 ArrayList<ScreenShot> screens = new ArrayList<>();
		 
		 for(int index=0; index<folder.children().size(); index++) {
			 if(folder.children().get(index).isFile()) {
				 
				 ScreenShot screenShot = new ScreenShot();
				 FileLink downloadLink = apiClient.createFileLink((RemoteFile) folder.children().get(index), DownloadOptions.DEFAULT).execute();
				 screenShot.setUrl(String.valueOf(downloadLink));
				 screenShot.setId(Long.valueOf(folder.children().get(index).name().substring(0,folder.children().get(index).name().indexOf("."))));
				 screens.add(screenShot);
			 }
		 }
		 
		 return screens;
	 
	 }

	 public RemoteFolder findFolderInUserFolder(ApiClient apiClient, String username, String searchedFolder) throws IOException, ApiError {
		 RemoteFolder rootFolder = apiClient.listFolder(RemoteFolder.ROOT_FOLDER_ID).execute(); 
		 RemoteFolder userFolder = findFolderChild(apiClient,username, rootFolder);
		 RemoteFolder userBooksFolder = findFolderChild(apiClient, searchedFolder, userFolder);
		 userBooksFolder =  apiClient.listFolder(userBooksFolder.folderId()).execute();
		 
		 return userBooksFolder;
	 }
	
}
