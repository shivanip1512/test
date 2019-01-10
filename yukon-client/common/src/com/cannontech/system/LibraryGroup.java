package com.cannontech.system;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.collect.Maps;

public enum LibraryGroup {
    
    ACTIVEMQ("ActiveMQ"),
    APACHE_COMMONS("Apache Commons"),
    AXIS("AXIS"),
    AZURE("Azure"),
    BOUNCY_CASTLE("Bouncy Castle"),
    COMMON("Common"),
    DATABASE__MICROSOFT_SQL_SERVER("Database - Microsoft SQL Server"),
    DATABASE__ORACLE("Database - Oracle"),
    DATABASE_SCHEMA("Database Schema"),
    GOOGLE_GUAVA("Google Guava"),
    HIBERNATE("Hibernate"),
    JACKSON("Jackson"),
    JAVA_API_FOR_XML_MESSAGING("Java API for XML Messaging"),
    JAVAMAIL("JavaMail"),
    JAVASCRIPT("JavaScript"),
    JAXB("JAXB"),
    JFREEREPORT("jfreereport"),
    JMX("JMX"),
    LOG4J("Log4j"),
    LUCENE("Lucene"),
    OPC("OPC"),
    OPENEXI("OpenEXI"),
    PASSWORDS("Passwords"),
    RADIUS("Radius"),
    SCHEDULING("Scheduling"),
    SPRING("Spring"),
    SPRING_WEB_SERVICES("Spring Web Services"),
    SQL("SQL"),
    SUPPORT_BUNDLE("Support bundle"),
    SWING_EVENTS("Swing events"),
    THRIFT("Thrift"),
    TOMCAT("Tomcat"),
    UI("UI"),
    UNIT_TESTS("Unit Tests"),
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
