package com.binaryalchemist.programondo.models;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchBookResponse {

	int page;
	int maxPage;
	int foundResults;
	ArrayList<Book> foundBooks;
	
}
