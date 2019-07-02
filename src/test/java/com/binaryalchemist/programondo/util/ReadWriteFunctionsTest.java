package com.binaryalchemist.programondo.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

class ReadWriteFunctionsTest {

	@Test
	void testReadFileExists() throws FileNotFoundException {
		
		File file = ResourceUtils.getFile(this.getClass().getResource("/bookHtmlTestString.txt"));
		assertTrue(file.exists());
		
	}
	
	@Test
	void testReadFile() {
		String content = null;
		ReadWriteFunctions rwf = new ReadWriteFunctions(); 
		try {
			content = rwf
					.readFile(ResourceUtils.getFile(this.getClass().getResource("/bookHtmlTestString.txt")));
		} catch (FileNotFoundException e) {
			fail("File Not Found Exception");
		}
		
		assertNotNull(content);
	}
	

}
