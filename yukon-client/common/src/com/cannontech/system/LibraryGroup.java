package com.cannontech.system;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

public enum LibraryGroup {
    
    ACTIVEMQ("ActiveMQ"),
    ANT("Ant"),
    APACHE_COMMONS("Apache Commons"),
    AXIS("AXIS"),
    AZURE("Azure"),
    BOUNCY_CASTLE("Bouncy Castle"),
    COMMON("Common"),
    DATABASE__MICROSOFT_SQL_SERVER("Database - Microsoft SQL Server"),
    DATABASE__ORACLE("Database - Oracle"),
    DATABASE_SCHEMA("Database Schema"),
    DBUNIT("DBUnit"),
    DNS_JAVA("DNS Java"),
    GOOGLE_GUAVA("Google Guava"),
    GWT("Google Web Toolkit"),
    HIBERNATE("Hibernate"),
    IZPACK("IzPack"),
    JACKSON("Jackson"),
    JASPERSOFT("Jaspersoft"),
    JAVA_API_FOR_XML_MESSAGING("Java API for XML Messaging"),
    JAVAMAIL("JavaMail"),
    JAVASCRIPT("JavaScript"),
    JAVASSIST("Javassist"),
    JAXB("JAXB"),
    JFREEREPORT("jfreereport"),
    JMX("JMX"),
    LAUNCH4J("Launch4j"),
    LOG4J("Log4j"),
    LUCENE("Lucene"),
    OPC("OPC"),
    OPENEXI("OpenEXI"),
    PASSWORDS("Passwords"),
    POWERMOCK("PowerMock"),
    RADIUS("Radius"),
    SCHEDULING("Scheduling"),
    SPRING("Spring"),
    SPRING_WEB_SERVICES("Spring Web Services"),
    SPRING_REST_SERVICES("Spring Rest Services"),
    SQL("SQL"),
    STRUTS("Struts"),
    SUPPORT_BUNDLE("Support bundle"),
    SWING_EVENTS("Swing events"),
    THRIFT("Thrift"),
    TOMCAT("Tomcat"),
    UI("UI"),
    UNIT_TESTS("Unit Tests"),
    WRAPPER("Wrapper"),
    XML_PARSING("XML parsing"),
    YET_ANOTHER_JAVA_SERVICE_WRAPPER("Yet Another Java Service Wrapper"),
    ZZZ_RETIRED("ZZZ Retired");
    
    private String groupName;

    private static Map<String, LibraryGroup> groupLookup;
    
    static {
        groupLookup = Maps.uniqueIndex(Arrays.asList(values()), LibraryGroup::getGroupName);
    }
    
    @JsonCreator
    public static LibraryGroup getByJarFile(String groupName) {
        return Optional.ofNullable(groupLookup.getOrDefault(groupName, null))
                .orElseThrow(() -> new IllegalArgumentException("Unknown library group: " + groupName));
    }

    private LibraryGroup(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }
}
