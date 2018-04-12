package shared;

import java.io.File;

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

}
