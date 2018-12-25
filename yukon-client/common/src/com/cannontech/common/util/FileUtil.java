package com.cannontech.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.springframework.util.Assert;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.exception.FileCreationException;

public final class FileUtil {
    private static Logger log = YukonLogManager.getLogger(FileUtil.class);
    private final static String fileNameDateFormat = "yyyyMMdd";

    /**
     * Patterns (NAME_DATE_LOG, NAME_DATE_ZIP, NAME_DATE_LOG_ZIP) look for a base file name (group 1) 
     * that is the leading part of the file minus an optional underscore, a 1-8 digit number which is 
     * the log file date (group 2), and the extension (group 3).
     * Pattern NAME_LOG looks for a base file name (group 1) that is the leading part of the file and 
     * extension (group 2). 
     */
    public static enum LogFilePattern {
        // Accept file name like abc_20180101.log
        NAME_DATE_LOG(Pattern.compile("(.+?)(_?\\d{1,8})(\\.[^.]+$)"), "log") {
            @Override
            protected Date getFileDate(File file, Matcher matcher) throws ParseException {
                return new SimpleDateFormat(fileNameDateFormat).parse(matcher.group(2).replaceFirst("_", ""));
            }
        },
        // Accept file name like abc_20180101.zip
        NAME_DATE_ZIP(Pattern.compile("(.+?)(_?\\d{1,8})(\\.[^.]+$)"), "zip") {
            @Override
            protected Date getFileDate(File file, Matcher matcher) throws ParseException {
                return new SimpleDateFormat(fileNameDateFormat).parse(matcher.group(2).replaceFirst("_", ""));
            }
        },
        // Accept file name like abc_20180101.log.zip
        NAME_DATE_LOG_ZIP(Pattern.compile("(.+?)(_?\\d{1,8})(\\.[^.]+)(\\.[^.]+$)"), "zip") {
            @Override
            protected Date getFileDate(File file, Matcher matcher) throws ParseException {
                return new SimpleDateFormat(fileNameDateFormat).parse(matcher.group(2).replaceFirst("_", ""));
            }
        },
        // Accept file name like abc.log
        NAME_LOG(Pattern.compile("(.+?)(\\.[^.]+$)"), "log"){
            @Override
            protected Date getFileDate(File file, Matcher matcher) throws ParseException {
                // Read header line from current log file and parse log creation date
                try (BufferedReader fileHeader = new BufferedReader(new FileReader(file))) {
                    String[] headerArray = fileHeader.readLine().split("\\:");
                    SimpleDateFormat dateFormat = new SimpleDateFormat(fileNameDateFormat);
                    String logDateStr= dateFormat.format((new SimpleDateFormat("yyyy-MM-dd").parse(headerArray[1].trim()))); 
                    return dateFormat.parse(logDateStr);
                } catch (Exception e) {
                    log.debug("Unable to parse log creation date for " + file.getName());
                    return LocalDate.now(DateTimeZone.getDefault()).toDate();
                }
            }
        };

        private LogFilePattern(Pattern pattern, String ext) {
            this.pattern = pattern;
            this.ext = ext;
        }

        private Pattern pattern;
        private String ext;

        protected Matcher getMatcher(String fileNameWithExt) {
            return this.pattern.matcher(fileNameWithExt);
        }

        /**
         * Returns date from fileName or file header.
         * Override per LogFilePattern for specific pattern parsing. 
         */
        protected Date getFileDate(File file, Matcher matcher) throws ParseException {
            return LocalDate.now(DateTimeZone.getDefault()).toDate();
        };

        /**
         * Returns applicationName from fileNameWithExt 
         */
        protected String getApplicationName(String fileNameWithExt, Matcher matcher) {
            return StringUtils.capitalize(matcher.group(1));
        }
    }

    /**
     * FileUtil constructor comment.
     */
    private FileUtil() {
        super();
    }

    public static List<String> readLines(File fileObj, int linesWanted, long offSet) throws IOException{
    	List<String> results = new ArrayList<String>();
        int avgLineSize = 75;
            	
    	/* Makes sure that an adequate number of characters are read in 
    	 * to obtain a full line
    	 */
    	int characterBufferSize = avgLineSize*linesWanted;
    	if(characterBufferSize < 550) {
            characterBufferSize = 550;
        }
    	if(linesWanted < 0) {
            linesWanted = 0;
        }

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
    			if (fileCharDisplacement != offSet) {
                    raf.readLine();
                }	
    		
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
                log.warn("Could not close InputStream", ex);
            }
        }
    }
    
    public static Date getCreationDate(File file) throws IOException {
    	BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
		return new Date(attr.creationTime().toMillis());
    }

    /**
     * Returns a FileFilter which will filter files based on the date range supplied. 
     */
    public static FileFilter creationDateFilter(final Instant from, final Instant to, final boolean filterDirectories) {
        return new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (!filterDirectories && file.isDirectory()) {
                    return true;
                }
                try {
                    Instant creation = new Instant(getFileDate(file));
                    return !(from.isAfter(creation) || to.isBefore(creation));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    log.debug("Parse Exception occurred for log file " + file.getName(), e);
                    return false;
                }
            }
        };
    }

    /**
     * Returns the application name based on the pattern of the filename.
     * Most commonly used for parsing applicationname from log file name.
     */
    public static String getApplicationName(File file) throws IOException {
        String fileNameWithExt = file.getName();
        for (LogFilePattern pattern : LogFilePattern.values()) {
            Matcher matcher = pattern.getMatcher(fileNameWithExt);
            if (matcher.matches() && fileNameWithExt.endsWith(pattern.ext)) {
                return pattern.getApplicationName(fileNameWithExt, matcher);
            }
        }
        return fileNameWithExt;
    }

    /**
     * Returns a date based upon the pattern of the filename.
     * Most commonly used for parsing date from log file name.
     */
    public static Date getFileDate(File file) throws IOException, ParseException {
        String fileNameWithExt = file.getName();
        for (LogFilePattern pattern : LogFilePattern.values()) {
            Matcher matcher = pattern.getMatcher(fileNameWithExt);
            if (matcher.matches() && fileNameWithExt.endsWith(pattern.ext)) {
                return pattern.getFileDate(file, matcher);
            }
        }
        return getCreationDate(file);
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
            if(file1.equals(file2)) {
                return true;
            }
            File fileWithTrailingSeparator = new File(file1, "/");
            if(fileWithTrailingSeparator.equals(file2)) {
                return true;
            }
            fileWithTrailingSeparator = new File(file2, "/");
            if(fileWithTrailingSeparator.equals(file1)) {
                return true;
            }
        } catch (IOException e){
            log.warn("Exception occurred testing file equality.", e);
        }
        return false;
    }

    /**
     * 
     * @return Date , if fileName matches with pattern (accepts file like DBEditor[10.24.26.137]20180531.log format)
     *         otherwise returns null
     * @param fileName
     * @throws ParseException
     */
    public static Date getOldClientLogDate(String fileName) throws ParseException {
        Pattern pattern = Pattern.compile("(.+?)(\\[)(\\d+\\.{1}\\d+\\.{1}\\d+\\.{1}\\d+)(\\])(\\d{1,8})(\\.[^.]+$)");
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.matches()) {
                return new SimpleDateFormat(fileNameDateFormat).parse(matcher.group(5));
        } else {
            return null;
        }
    }

    /**
     * @return true if file name matches with either of one format fileName_YYYYMMDD.log or fileName_YYYYMMDD.log.zip or
     *         fileName_YYYYMMDD.zip
     *         otherwise false
     */
    public static boolean isJavaLogFile(String fileName, String regex) {
        return fileName.matches(regex + ".log") || fileName.matches(regex + ".log.zip")
               || fileName.matches(regex + ".zip");
    }

    /**
     * @return true if file name matches with pattern RfnCommsLog_YYYYMMDD.log
     *         otherwise false
     * */
    public static boolean isOldRfnCommLogFile(String fileName) {
        return fileName.matches("^" + "RfnCommsLog_" + "([0-9]{2}|[0-9]{8})" + ".log");
    }
    
    /**
     * @return file creation date after parsing header string.
     * @throws ParseException 
     */
    public static DateTime parseLogCreationDate(String header) {
        String[] output = header.split("\\:");
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(output[1].trim());
            return new DateTime(date);
        } catch (ParseException | ArrayIndexOutOfBoundsException e) {
            log.error("Unable to parse " + header);
            return null;
        }
    }

    /**
     * @return Log creation date after parsing it from fileName.
     *         If fails returns null
     *         
     */
    public static Date getLogCreationDate(String fileName, String prefix) {
        try { /* lets try to parse the filename for the date */
            DateFormat dateFormat = new SimpleDateFormat(fileNameDateFormat);
            return dateFormat.parse(fileName.replace(prefix, ""));
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Verify the directory exist or not with specified path. If directory does not exist create a new
     * directory.
     * 
     * @param path - Directory path.
     * @throws FileCreationException : If unable to create a directory with specified path.
     */

    public final static void verifyDirectory(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean success = dir.mkdirs();
            if (!success) {
                throw new FileCreationException("Error occurred while creating directory " + path);
            }
        }
    }
}