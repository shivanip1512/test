package com.cannontech.yukon.api.util;

import java.util.Properties;

import org.jdom.Namespace;

public class YukonXml {
    private static final Namespace yukonNamespace = Namespace.getNamespace("y", "http://yukon.cannontech.com/api");
    private static Properties properties = new Properties();
    static {
        properties.put(yukonNamespace.getPrefix(), yukonNamespace.getURI());
    }
    
    public static Namespace getYukonNamespace() {
        return yukonNamespace;
    }
    
    public static Properties getYukonNamespaceAsProperties() {
        return properties;
    }
}
