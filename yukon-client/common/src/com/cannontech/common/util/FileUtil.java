package com.cannontech.common.util;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.cannontech.clientutils.YukonLogManager;

public final class FileUtil {
	private static Logger log = YukonLogManager.getLogger(FileUtil.class);
	
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
    
    /**
     * Like FileCopyUtils.copy(Reader,Writer), but without flushing or closing the out.
     * @param in will be closed
     * @param out will not be closed
     * @return bytes copied
     * @throws IOException
     */
    public static int copyNoFlush(Reader in, Writer out) throws IOException {
        Assert.notNull(in, "No Reader specified");
        Assert.notNull(out, "No Writer specified");
        try {
            int byteCount = 0;
            char[] buffer = new char[4096];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            return byteCount;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {
                log.warn("Could not close Reader", ex);
            }
        }
    }

    /**
     * Like FileCopyUtils.copy(Reader,Writer), but without flushing or closing the out.
     * Uses InputStream, and OutputStream objects
     * @param in will be closed
     * @param out will not be closed
     * @return bytes copied
     * @throws IOException
     */
    public static int copyNoFlush(InputStream in, OutputStream out) throws IOException {
        Assert.notNull(in, "No InputStream specified");
        Assert.notNull(out, "No OutputStream specified");
        try {
            int byteCount = 0;
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                byteCount += bytesRead;
            }
            return byteCount;
        }
        finally {
            try {
                in.close();
            }
            catch (IOException ex) {
                log.warn("Could not close OutputStream", ex);
            }
        }
    }
    
    /**
     * Checks two files for equality, ignoring any trailing
     * separator characters in the file path.  (The File.equals
     * method displays incorrect behavior regarding this.  See
     * Java bug #4730835 for more info).
     * @param file1
     * @param file2
     * @return true if the files have equivalent paths, otherwise
     * false
     */
    public static boolean areFilesEqual(File file1, File file2){
        try{
            file1 = file1.getCanonicalFile();
            file2 = file2.getCanonicalFile();
            if(file1.equals(file2)) return true;
            File fileWithTrailingSeparator = new File(file1, "/");
            if(fileWithTrailingSeparator.equals(file2)) return true;
            fileWithTrailingSeparator = new File(file2, "/");
            if(fileWithTrailingSeparator.equals(file1)) return true;
        } catch (IOException e){
            log.warn("Exception occurred testing file equality.", e);
        }
        return false;
    }
}