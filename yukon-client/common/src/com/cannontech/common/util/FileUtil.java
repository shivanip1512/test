package com.cannontech.common.util;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public final class FileUtil {
	
	
    /**
     * FileUtil constructor comment.
     */
    private FileUtil() {
        super();
    }
    
    public static List<String> readLines(File fileObj, int linesWanted) throws IOException{
    	return readLines(fileObj, linesWanted, 0);
    }
    
    public static List<String> readLines(File fileObj, int linesWanted, long offSet) throws IOException{
    	List<String> results = new ArrayList<String>();
        int avgLineSize = 75;
            	
    	/* Makes sure that an adequate number of characters are read in 
    	 * to obtain a full line
    	 */
    	int characterBufferSize = avgLineSize*linesWanted;
    	if(characterBufferSize < 550)
    		characterBufferSize = 550;
    	if(linesWanted < 0)
    		linesWanted = 0;

    	long fileLength = fileObj.length();
    	long fileCharDisplacement = (fileLength)-characterBufferSize;
   	
        RandomAccessFile raf = new RandomAccessFile(fileObj,"r");
    	if(raf != null){
    		
    		while(results.size() < linesWanted){        	
    			List<String> helper = null;
                Long oldCutOffLength = fileLength; 
    		
                /* Checks to see if this run is the second or more run through
    			 *	and if it is allocates a copy of the previous array to allow
    			 *	expansion to happen in the correct order
    			 */
    			if(results.size() > 0){
    				helper = new ArrayList<String>(results);
    				results.clear();
    				oldCutOffLength = fileCharDisplacement;
    				fileCharDisplacement = fileCharDisplacement-(long)(characterBufferSize*.5);
                }
    		
    			if(fileCharDisplacement < offSet){
    				fileCharDisplacement = offSet;
    			}
    			raf.seek(fileCharDisplacement);
    	
    			// burns the first line to reassure that there are no partial lines
    			if (fileCharDisplacement != offSet)
    				raf.readLine();	
    		
    			String tempString;
    			while((tempString = raf.readLine()) != null){
                    
    				if(tempString.length() <= 0){continue;}
                    if(results.size() < linesWanted){
                        results.add(tempString);
                    }else{
                        results.remove(0);
                        results.add(tempString);
                    }
                    
                    /* Breaks out of the for loop if filePoiter has
                     * passed its previous mark
                     */
                    if(oldCutOffLength < raf.getFilePointer()){
                        break;
                    }
    			}
    		
    			if((helper != null) && (helper.size() > 0)){
    				for(int i=0; i < helper.size(); i++){
                        results.add(helper.get(i));
    				}
    			}

    			/* This shows the complete file since the user asked for more
    			 * 	lines than there were in the log file.
    			 */
       			if(fileCharDisplacement == offSet){
    				break;
    			}
    		}
            
            // Cleans out the non-needed elements
            for(int i=0; i < results.size(); i++){
                if(results.size() > linesWanted){
                    results.remove(0);
                }
            }
            
    		raf.close();
       	}
    	return results;
    }

}