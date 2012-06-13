package com.cannontech.core.authentication.model;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.Lists;


public class PolicyRuleRegexSupport {
    public static String[] LATIN = {
        "Basic Latin",
        "Latin-1 Supplement",
        "Latin Extended-A",
        "Latin Extended-B",
        "Latin Extended Additional"
    };
    public static String[] GREEK = {
        "Greek and Coptic",
        "Greek Extended"
    };
    public static String[] CYRILLIC = {
        "Cyrillic"
    };
    
    public static String EUROPEAN_LANG_CHAR_REGEX = buildCharacterRegex(LATIN, GREEK, CYRILLIC);
    
    /**
     * This method returns a regex expression of the languages passed in.
     */
    private static String buildCharacterRegex(String[] firstLanguageSet, String[]... extraLanguageSets) {
        List<String> europeanLanguages = addAll(firstLanguageSet, extraLanguageSets);
        
        StringBuilder results = new StringBuilder(200);
        for (String europeanLanguage : europeanLanguages) {
            results.append("\\p{In");
            results.append(europeanLanguage);
            results.append("}");
        }
        
        return results.toString();
    }
    
    /**
     * This method collapses several arrays into one list.
     */
    private static List<String> addAll(String[] first, String[]... arrays){
        String[] results = first;
        for (String[] array :  arrays) {
            results = (String[]) ArrayUtils.addAll(results, array);
        }
        
        return Lists.newArrayList(results); 
    }
}