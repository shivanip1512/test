package com.cannontech.stars.xml.util;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class XMLUtil {

    private static String XML_DECL = "<?xml version=\"1.0\"?>";

    public static String removeXMLDecl(String xmlStr) {
        int pos = xmlStr.indexOf( "?>" );
        if (pos >= 0) {
            int pos2 = xmlStr.indexOf("<", pos + 2);
            if (pos2 > 0) {
                //XML_DECL = xmlStr.substring(0, pos2);
                return xmlStr.substring(pos2);
            }
        }
        return xmlStr;
    }

    public static String addXMLDecl(String xmlFrag) {
        return (XML_DECL + xmlFrag);
    }
}