package com.cannontech.web.support;

import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

import com.google.common.collect.Lists;

/**
 * This class stores a key-value pair for localization i18n keys
 *  it also has a secondary value for comparisons
 *
 */
public class LocalizationPair implements Comparable<LocalizationPair> {
    private final String key;
    private final String value;
    private final String secondaryValue;

    /**
     * Creates a LocalizationPair with a key and a value
     */
    public LocalizationPair(String key, String value) {
        this(key,value,null);
    }

    /**
     * Creates a LocalizationPair with a key, a value, and a secondary value (for comparisons)
     */
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

    /**
     * @return null if unused
     */
    public String getSecondaryValue() {
        return secondaryValue;
    }

    /**
     * @return i18n xml entry for the key and value
     */
    public String getXml() {
        return "<entry key=\"" + key + "\">" + sanitizeXml(value) + "</entry>";
    }

    /**
     * @param unclean
     * @return input string, with all characters properly escaped for an xml properties file
     * anything within a separate tag is put in a CDATA section
     */
    private static String sanitizeXml(String unclean){
        if(unclean == null){
            return null;
        }

        String clean = unclean;
        if(unclean.contains("<") || unclean.contains(">") || unclean.contains("\n")){
            clean = unclean.replaceAll("]]>", "]]><![CDATA[");
            clean = "<![CDATA[" + clean + "]]>";
        }
        else {
            clean = StringEscapeUtils.escapeXml11(unclean);
            //We can have quotes and apostrophes in our properties xml file
            clean = clean.replaceAll("&quot;", "\"");
            clean = clean.replaceAll("&apos;", "'");
        }
        return clean;
    }

    /**
     * @param input
     * @return escapes html in a list of LocalizationPairs.
     * This allows the values to be displayed for comparison
     */
    public static List<LocalizationPair> sanitizeHtmlPairs(List<LocalizationPair> input){
        List<LocalizationPair> results = Lists.newArrayList();
        for(LocalizationPair unclean : input){
            results.add(new LocalizationPair(StringEscapeUtils.escapeHtml4(unclean.getKey()),
                StringEscapeUtils.escapeHtml4(LocalizationPair.sanitizeXml(unclean.getValue())),
                StringEscapeUtils.escapeHtml4(LocalizationPair.sanitizeXml(unclean.getSecondaryValue()))));
        }
        return results;
    }

    @Override
    public int compareTo(LocalizationPair lp) {
        return key.compareTo(lp.key);
    }
}
