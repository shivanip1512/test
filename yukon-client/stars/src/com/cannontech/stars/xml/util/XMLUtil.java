package com.cannontech.stars.xml.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class XMLUtil {

    private static String LOG4J_CONFIG_FILE = "log4j.properties";

    private static String XML_DECL = "<?xml version=\"1.0\"?>";

    private static LogFactory logFac = null;

    static {
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.Log4jFactory");
        System.setProperty("log4j.configuration", LOG4J_CONFIG_FILE);
    }

    private static LogFactory getLogFactory() throws Exception {
        if (logFac == null)
            logFac = LogFactory.getFactory();
        return logFac;
    }

    public static Log getLogger(Class c) {
        return getLogger( c.getName() );
    }

    public static Log getLogger(String name) {
        try {
            Log logger = getLogFactory().getLog(name);
            return logger;
        }
        catch (Exception e) {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }
        return null;
    }

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