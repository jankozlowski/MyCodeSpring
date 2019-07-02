package com.binaryalchemist.programondo.models;

import lombok.Data;

@Data
public class UploadFileResponse {
	private long id;
    private String title;
    private String link;
    private String fileType;
    private long size;

    public UploadFileResponse(long id, String title, String link, String fileType, long size) {
        this.id = id;
    	this.title = title;
        this.link = link;
        this.fileType = fileType;
        this.size = size;
    }
}