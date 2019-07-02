package com.binaryalchemist.programondo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import com.binaryalchemist.programondo.models.Book;
import com.binaryalchemist.programondo.util.ReadWriteFunctions;

class BookControllerTest {

	@Test
	void downloadFeaturedBooksDocumentFromUrlTest() {

		Document doc = mock(Document.class);
		ReadWriteFunctions rwf = new ReadWriteFunctions();
		try {
			String docHtmlMock = rwf
					.readFile(ResourceUtils.getFile(this.getClass().getResource("/bookHtmlTestString.txt")));
			doc.append(docHtmlMock);
		} catch (FileNotFoundException e) {
			fail("File Not Found Exception");
		}

		BookController bookController = mock(BookController.class);
		when(bookController
				.downloadFeaturedBooksDocumentFromUrl("https://www.goodreads.com/genres/most_read/computer-science"))
						.thenReturn(doc);

		assertNotNull(doc);

	}

	@Test
	void getFeaturedBooksCoversFromFeaturedBooksDocumentTest() {

		Document doc = null;
		ReadWriteFunctions rwf = new ReadWriteFunctions();
		try {
			String docHtmlMock = rwf
					.readFile(ResourceUtils.getFile(this.getClass().getResource("/bookHtmlTestString.txt")));
			doc = Jsoup.parse(docHtmlMock);
		} catch (FileNotFoundException e) {
			fail("File Not Found Exception");
		}

		BookController bookController = new BookController();
		Elements elements = bookController.getFeaturedBooksCoversFromFeaturedBooksDocument(doc);
		
		for (int counter = 0; counter < elements.size(); counter++) {
			assertEquals(elements.get(counter).select(".coverWrapper").size(), 1);
			assertEquals(elements.get(counter).select(".coverWrapper > a").size(), 1);
		}

	}
	
	@Test
	void getBooksTitleImageIdFromRowElementsTest(){
		
		Elements inputElements = new Elements();
		String elementString = "<div class=\"coverWrapper\" id=\"bookCover963154_38212157\"> \r\n" + 
							" <a href=\"/book/show/38212157-hello-world\"><img alt=\"Hello World: Being Human in the Age of Algorithms\" title=\"\" width=\"115\" class=\"bookImage\" src=\"https://images.gr-assets.com/books/1525784303l/38212157.jpg\"></a> \r\n" + 
							"</div>";
		Element testElement = Jsoup.parse(elementString, "", Parser.xmlParser());
		
		inputElements.add(testElement);
		
		Book expectedBook = new Book();
		expectedBook.setGoodreadsId(38212157);
		expectedBook.setImageUrl("https://images.gr-assets.com/books/1525784303l/38212157.jpg");
		expectedBook.setTitle("Hello World: Being Human in the Age of Algorithms");
		expectedBook.setLink("https://www.goodreads.com/book/show/38212157-hello-world");
		
		BookController bookController = new BookController();
		ArrayList<Book> parsedBooksFromElements = bookController.getBooksTitleImageLinkIdFromRowElements(inputElements);
		
		assertEquals(expectedBook.getGoodreadsId(), parsedBooksFromElements.get(0).getGoodreadsId());
		assertEquals(expectedBook.getImageUrl(), parsedBooksFromElements.get(0).getImageUrl());
		assertEquals(expectedBook.getTitle(), parsedBooksFromElements.get(0).getTitle());
		assertEquals(expectedBook.getLink(), parsedBooksFromElements.get(0).getLink());
		
	}
	
	
	
	

}
