package com.binaryalchemist.programondo.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.binaryalchemist.programondo.models.Book;
import com.binaryalchemist.programondo.models.SearchBookResponse;

@RestController
@RequestMapping("/api/books")
@CrossOrigin
public class BookController {

	@GetMapping("/featured")
	public ResponseEntity<?> downloadFeaturedBooksFromGoodReads() {
		final String featuredBooksUrl = "https://www.goodreads.com/genres/most_read/computer-science";
		Document featuredBooksDocument = downloadFeaturedBooksDocumentFromUrl(featuredBooksUrl);
		Elements featuredBooksRowsElements = getFeaturedBooksCoversFromFeaturedBooksDocument(featuredBooksDocument);
		ArrayList<Book> featuredBooks = getBooksTitleImageLinkIdFromRowElements(featuredBooksRowsElements);
		
		return new ResponseEntity<ArrayList<Book>>(featuredBooks, HttpStatus.OK);
	}
	
	@GetMapping("/search/{keyword}/{page}")
	public ResponseEntity<?> searchBooksFromGoodReads(@PathVariable String keyword, @PathVariable int page) throws SAXException, IOException, ParserConfigurationException {
		final String key = "y7VzIvGwhqi3CSq7XJRTyg";
		final String searchedBooksUrl = "https://www.goodreads.com/search/index.xml?key="+key+"&q="+keyword+"&page="+page;
		final String xmlTagForOneBook = "best_book";
		
		org.w3c.dom.Document doc = getXmlDocumentFromUrl(searchedBooksUrl);
		NodeList nodeList = doc.getElementsByTagName(xmlTagForOneBook);
		ArrayList<Book> foundBooks = getBooksFromXMLNodeList(nodeList);
		
		SearchBookResponse searchBookResponse = new SearchBookResponse();
		searchBookResponse.setPage(Integer.valueOf(doc.getElementsByTagName("results-start").item(0).getTextContent()));
		searchBookResponse.setMaxPage(Integer.valueOf(doc.getElementsByTagName("results-end").item(0).getTextContent()));
		searchBookResponse.setFoundResults(Integer.valueOf(doc.getElementsByTagName("total-results").item(0).getTextContent()));
		searchBookResponse.setFoundBooks(foundBooks);
		

		
		return new ResponseEntity<SearchBookResponse>(searchBookResponse, HttpStatus.OK);
	}
	
	
	public Document downloadFeaturedBooksDocumentFromUrl(String featuredBooksUrl) {

		Document featuredBooksDocument = null;
		try {
			featuredBooksDocument = Jsoup.connect(featuredBooksUrl).get();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return featuredBooksDocument;
	}
	
	public Elements getFeaturedBooksCoversFromFeaturedBooksDocument(Document featuredBooksDocument){
		Elements featuredBooksRows = featuredBooksDocument.getElementsByClass("coverWrapper");
		return featuredBooksRows;
	}
	
	public ArrayList<Book> getBooksTitleImageLinkIdFromRowElements(Elements featuredBooksCovers) {
		
		ArrayList<Book> bookList = new ArrayList<>();
		
		for(Element featuredBookCover : featuredBooksCovers) {
			Book newFeaturedBook = new Book();
			
			Elements bookImagesAhref = featuredBookCover.getElementsByTag("a");
			String link = bookImagesAhref.get(0).attr("href");
			newFeaturedBook.setLink("https://www.goodreads.com"+link);
			
			Elements bookImagesRow = featuredBookCover.getElementsByTag("img");
			Element oneImage = bookImagesRow.get(0);
				
			newFeaturedBook.setTitle(oneImage.attr("alt"));
			newFeaturedBook.setGoodreadsId(Long.valueOf(getBookIdFromBookImageUrl(oneImage.attr("src"))));
			newFeaturedBook.setImageUrl(oneImage.attr("src"));
			bookList.add(newFeaturedBook);
			
		}
		
		return bookList;

	}
	
	public String getBookIdFromBookImageUrl(String url) {
		
		String id = url.substring(url.lastIndexOf("/")+1,url.length());
		id = id.substring(0,id.indexOf("."));
		
		return id;
		
	}
	
	public org.w3c.dom.Document getXmlDocumentFromUrl(String url) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();

		URL link = new URL(url);
		InputStream stream = link.openStream();
		org.w3c.dom.Document doc = docBuilder.parse(stream);
		
		return doc;
	}
	
	public ArrayList<Book> getBooksFromXMLNodeList(NodeList nodeList){
		
		ArrayList<Book> bookList = new ArrayList<>();
		
		for(int index=0; index<nodeList.getLength(); index++) {
			Book book = new Book();
			Node nNode = nodeList.item(index);			
			org.w3c.dom.Element eElement = (org.w3c.dom.Element) nNode;
			
			book.setGoodreadsId(Integer.valueOf((eElement.getElementsByTagName("id").item(0).getTextContent())));
			book.setTitle(eElement.getElementsByTagName("title").item(0).getTextContent());
			book.setImageUrl(eElement.getElementsByTagName("image_url").item(0).getTextContent());
			book.setLink("https://www.goodreads.com/book/show/"+book.getGoodreadsId());
			bookList.add(book);
		}
		
		return bookList;
	}
	
	
}
