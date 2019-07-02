package com.binaryalchemist.programondo.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ReadWriteFunctions {

	public String readFile(File file) {
		
		String content = "";
		try {
			content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	public void renameFile(File toBeRenamed, String new_name)
		    throws IOException {
		    File fileWithNewName = new File(toBeRenamed.getParent(), new_name);
		    if (fileWithNewName.exists()) {
		        throw new IOException("file exists");
		    }

		    boolean success = toBeRenamed.renameTo(fileWithNewName);
		    if (!success) {

		    }
		}
	
}
