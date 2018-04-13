package dataStorComponents;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileMan {
	
	File folder;
	
	public FileMan(String path) {
		
		this.folder = new File(path);
		
		
	}
	
	public String listFiles() {
		File[] listOfFiles = folder.listFiles();
		
		String response = "";
		
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	    	  response += "\n" + listOfFiles[i].getName()+"\t\t"+sizeHumanReadableStr(listOfFiles[i].length());
	    	  
	      }
	    }
	    response +="\n\n";
	    response +="\nFREE SPACE: "+sizeHumanReadableStr(folder.getFreeSpace());
		return response;
	}

	public String sizeHumanReadableStr(long sizeBytes) {
		String sizeDescr = "Bytes";
		  
		  if (sizeBytes > 1024) {
			  sizeBytes = sizeBytes/1024;
			  sizeDescr = "kB";
			  
			  if (sizeBytes > 1024) {
				  sizeBytes = sizeBytes/1024;
				  sizeDescr = "MB";
				  
				  if (sizeBytes > 1024) {
					  sizeBytes = sizeBytes/1024;
					  sizeDescr = "GB";
				  }
			  }
		  }
		  
		  return sizeBytes+" "+sizeDescr;
	}
	
	public String getHash(File f) {
		String response = "";
		try {
			response = getFileChecksum(MessageDigest.getInstance("SHA-1"), f);
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}
	
	// https://howtodoinjava.com/core-java/io/how-to-generate-sha-or-md5-file-checksum-hash-in-java/
	private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	{
	    //Get file input stream for reading the file content
	    FileInputStream fis = new FileInputStream(file);
	     
	    //Create byte array to read data in chunks
	    byte[] byteArray = new byte[1024];
	    int bytesCount = 0;
	      
	    //Read file data and update in message digest
	    while ((bytesCount = fis.read(byteArray)) != -1) {
	        digest.update(byteArray, 0, bytesCount);
	    };
	     
	    //close the stream; We don't need it now.
	    fis.close();
	     
	    //Get the hash's bytes
	    byte[] bytes = digest.digest();
	     
	    //This bytes[] has bytes in decimal format;
	    //Convert it to hexadecimal format
	    StringBuilder sb = new StringBuilder();
	    for(int i=0; i< bytes.length ;i++)
	    {
	        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	     
	    //return complete hash
	   return sb.toString();
	}

}
