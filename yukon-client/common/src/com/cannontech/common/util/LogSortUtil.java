package com.cannontech.common.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public final class LogSortUtil {
    /**
     * ListUtil constructor comment.
     */
    private LogSortUtil() {
        super();
    }
    
    /**
     * @param stringList
     * @param regexPattern
     * @throws Exception 
     * @return
     * 
     * This is a function that will return a map with the regexExpression portion of
     * the pattern being pulled out of the String and setting up a map with the String
     * as the key and the snipit acting as the value.
     */
    public static Map<String, String> returnSearchMap(List<File> fileList, Pattern regexPattern) throws Exception{
    	Map<String, String> mapResults = new HashMap<String, String>();
    	for (File file : fileList) {

            /* Checks to see if the File obj is a directory and if so adds the obj to
             * a special key that will be used in the results
             */
            if(file.isDirectory()){
                mapResults.put(file.getName(), "Directories");
            }else{
            
                /* Uses a Pattern object to extract the wanted piece of the File Object's Name
                 * that will be used later on in 
                 */
                String logFile = file.getName();
                Matcher patternMatches = regexPattern.matcher(logFile);
                if(patternMatches.matches() && patternMatches.groupCount() > 0){
                
                    // Sets up the values for the map
                    String original = StringUtils.capitalize(logFile);
                    String stringSnippet = StringUtils.capitalize((patternMatches.group(1)).toLowerCase());
                    mapResults.put(original, stringSnippet);
                }
            }
    	}
    	if(mapResults.isEmpty()){
    		throw new Exception("Invalid Pattern Used for returnSearchMap");
    	}
    	
    	return mapResults;
    }

    /**
     * 
     * @param fileList
     * @return
     * @throws Exception
     * 
     * Uses the lastModified as the parameter for the search map to search by.
     */
    public static Map<String, String> returnSearchMap(List<File> fileList) throws Exception {
        Map<String, String> mapResults = new HashMap<String, String>();
        for (File file : fileList) {

            if(file.isDirectory()){
                mapResults.put(file.getName(), "Directories");
            }else{

            
                long lastModL = file.lastModified();
                Date lastModDate = new Date(lastModL);
            
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                String lastMod = null;
                if( lastModDate != null) {
                    lastMod = df.format(lastModDate);
                    mapResults.put(StringUtils.capitalize(file.getName()), lastMod);
                }
            }
        }
        if(mapResults.isEmpty()){
            throw new Exception("Invalid Pattern Used for returnSearchMap");
        }
        return mapResults;
    }
    
    /**
     * Overloaded function of SortedMap
     * 
     * @param searchMap
     * @return
     * 
     * This function takes in a searchMap and returns a sorted list 
     */
    public static SortedMap<String, List<String>> sortSearchMap(Map<String, String> searchMap, File localDir){
       return sortSearchMap(searchMap, localDir, 0);
    }
    
    /**
     * This function is used to take a Map<nameOfString, sortKey> 
     * and return the strings in sorted order with a sortKey as a header
     * 
     * @param searchMap
     * @param charTruncate
     * @return
     */
    public static SortedMap<String, List<String>> sortSearchMap(Map<String, String> searchMap, File localDir, int charTruncate){
        SortedMap<String, List<String>> sortResults = new TreeMap<String, List<String>>();
        List<Map.Entry<String, String>> sortedList = new ArrayList<Map.Entry<String, String>>(searchMap.entrySet());

        for(Map.Entry<String, String> entry : sortedList){
           
            String key = entry.getValue();
            String logName = entry.getKey();
            if(!(key.equalsIgnoreCase("Directories"))){
                // Sets up the key that is used for adding and retrieving information from the SortedMap
                key = entry.getValue().substring(0, entry.getValue().length() - charTruncate);
                if((String.valueOf(key.charAt(key.length()-1))).equals("_")){
                    key = key.substring(0, key.length()-1);
                }
            }else{
                logName += '/';
            }
                
            
            if(sortResults.get(key) == null){
                List<String> tempArray = new ArrayList<String>();
                sortResults.put(key, tempArray);
            }
            
            // Grabs the list from the sortedMap and adds the new value to the list and sorts the list
            List<String> tempArr = new ArrayList<String>();
            tempArr = sortResults.get(key);
            tempArr.add(logName);
            Collections.sort(tempArr);
        }
        
        return sortResults;
    }
}