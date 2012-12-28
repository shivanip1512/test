package com.cannontech.web.support;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.common.collect.Lists;

public class LocalizationPair implements Comparable<LocalizationPair> {
    String key;
    String value;
    String secondaryValue;
    
    public LocalizationPair(String key, String value) {
        this.key = key;
        this.value = value;
    }
    
    public LocalizationPair(String key, String value, String secondaryValue) {
        this.key = key;
        this.value = value;
        this.secondaryValue = secondaryValue;
    }
    
    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getSecondaryValue() {
        return secondaryValue;
    }
    
    public String getXml() {       
        return "<entry key=\"" + key + "\">" + sanitizeXml(value) + "</entry>";
    }
    
    public static String sanitizeXml(String unclean){
        if(unclean == null){
            return null;
        }
        
        String clean = unclean;
        if(unclean.contains("<") || unclean.contains(">")){
            clean = unclean.replaceAll("]]>", "]]><![CDATA[");
            clean = "<![CDATA[" + clean + "]]>";
        }
        else {
            clean = StringEscapeUtils.escapeXml(unclean);
            //We can have quotes and apostrophes in our properties xml file
            clean = clean.replaceAll("&quot;", "\"");
            clean = clean.replaceAll("&apos;", "'");
        }
        return clean;
    }

    public static List<LocalizationPair> sanitizeHtmlPairs(List<LocalizationPair> input){
        List<LocalizationPair> results = Lists.newArrayList();
        for(LocalizationPair unclean : input){
            results.add(new LocalizationPair(
                    StringEscapeUtils.escapeHtml(unclean.getKey()),
                    StringEscapeUtils.escapeHtml(LocalizationPair.sanitizeXml(unclean.getValue())),
                    StringEscapeUtils.escapeHtml(LocalizationPair.sanitizeXml(unclean.getSecondaryValue()))
                    ));
        }
        return results;
    }
    
    @Override
    public int compareTo(LocalizationPair lp) {
        return key.compareTo(lp.key);
    }
}
