package com.cannontech.common.util;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.google.common.collect.Lists;


public class RegexUtil {
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
    
    public static String getEuropeanLanguageCharactersRegex() {
        List<String> europeanLanguages = addAll(LATIN, GREEK, CYRILLIC);
        
        StringBuilder results = new StringBuilder(200);
        for (String europeanLanguage : europeanLanguages) {
            results.append("\\p{In");
            results.append(europeanLanguage);
            results.append("}");
        }
        
        return results.toString();
    }
    
    private static List<String> addAll(String[] first, String[]... arrays){
        String[] results = first;
        for (String[] array :  arrays) {
            results = (String[]) ArrayUtils.addAll(results, array);
        }
        
        return Lists.newArrayList(results); 
    }
}