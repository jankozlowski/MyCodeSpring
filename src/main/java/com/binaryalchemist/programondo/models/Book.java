package com.binaryalchemist.programondo.models;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Book {

	private long id;
	private long goodreadsId;
	private String title;
	private String descripction;
	private String imageUrl;
	private String link;
	
}
