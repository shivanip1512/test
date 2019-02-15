package com.cannontech;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class EimJavaLibraryTest {

    private static Set<String> excludedJars = ImmutableSet.of("gson-1.7.2.jar", "azure-svc-mgmt-scheduler-0.9.0.jar",
        "jjwt-0.5.1.jar", "azure-svc-mgmt-websites-0.9.0.jar", "azure-media-0.9.0.jar", "xbean-spring-4.5.jar",
        "servlet-api.jar", "jackson-mapper-asl-1.9.2.jar", "jaxb-impl-2.2.3-1.jar",
        "spring-context-indexer-5.0.2.RELEASE.jar", "jackson-core-asl-1.9.2.jar", "spring-test-5.0.2.RELEASE.jar",
        "spring-aspects-5.0.2.RELEASE.jar", "jersey-core-1.13.jar", "spring-web-5.0.2.RELEASE.jar",
        "spring-context-support-5.0.2.RELEASE.jar", "spring-webmvc-5.0.2.RELEASE.jar", "spring-aop-5.0.2.RELEASE.jar",
        "jackson-xc-1.9.2.jar", "spring-jms-5.0.2.RELEASE.jar", "spring-jcl-5.0.2.RELEASE.jar",
        "applicationinsights-core-1.0.2.jar", "spring-oxm-5.0.2.RELEASE.jar", "azure-servicebus-0.9.7.jar",
        "guava-13.0.1.jar", "spring-core-5.0.2.RELEASE.jar", "azure-svc-mgmt-sql-0.9.0.jar",
        "spring-orm-5.0.2.RELEASE.jar", "stax-ex-1.8.jar", "annotation-detector-3.0.4.jar", "azure-core-0.9.7.jar",
        "azure-storage-4.0.0.jar", "jsr305-1.3.9.jar", "spring-jdbc-5.0.2.RELEASE.jar", "yukon-help.jar",
        "mail-1.4.5.jar", "spring-webflux-5.0.2.RELEASE.jar", "jettison-1.1.jar", "spring-instrument-5.0.2.RELEASE.jar",
        "jersey-client-1.13.jar", "spring-context-5.0.2.RELEASE.jar", "spring-websocket-5.0.2.RELEASE.jar",
        "httpcore-4.3.2.jar", "commons-codec-1.9.jar", "azure-serviceruntime-0.9.0.jar",
        "spring-integration-jms-5.0.2.RELEASE.jar", "httpclient-4.3.4.jar", "spring-integration-core-5.0.2.RELEASE.jar",
        "azure-svc-mgmt-media-0.9.0.jar", "apache-log4j-extras-1.0.jar", "azure-tracing-util-0.9.0.jar",
        "spring-messaging-5.0.2.RELEASE.jar", "commons-discovery-0.5.jar", "javax.inject-1.jar", "jersey-json-1.13.jar",
        "applicationisights-web-1.0.2.jar", "jackson-dataformat-csv-2.9.6.jarsnakeyaml-1.18.2.jar",
        "spring-beans-5.0.2.RELEASE.jar", "azure-svc-mgmt-network-0.9.0.jar", "jackson-jaxrs-1.9.2.jar",
        "commons-beanutils-1.9.3.jar", "azure-svc-mgmt-storage-0.9.0.jar", "spring-tx-5.0.2.RELEASE.jar",
        "spring-expression-5.0.2.RELEASE.jar", "concurrentlinkedhashmap-lru-1.4.jar", "tomcat-i18n-ru.jar",
        "azure-svc-mgmt-compute-0.9.0.jar", "h2-1.4.178.jar", "jsch-0.1.54.jar", "easymock-3.0.jar",
        "objenesis-1.2.jar", "applicationinsights-web-1.0.2.jar", "jaxb-xjc.jar", "jaxb2-default-value-1.1.jar",
        "websocket-api.jar", "tomcat-i18n-es.jar", "tomcat-juli-9.0.12.jar", "catalina-ant.jar", "commons-lang-2.6.jar",
        "tomcat-util-scan.jar", "catalina.jar", "catalina-storeconfig.jar", "junit-4.12.jar", "tomcat-jni.jar",
        "azure-svc-mgmt-0.9.0.jar", "el-api.jar", "mx4j-jmx.jar", "tomcat-websocket.jar", "tomcat-i18n-fr.jar",
        "jasper-el.jar", "jsp-api.jar", "tomcat-api.jar", "catalina-ha.jar", "jasper.jar", "catalina-tribes.jar",
        "hamcrest-core-1.3.jar", "jaspic-api.jar", "tomcat-dbcp.jar", "tomcat-i18n-ja.jar", "tomcat-util.jar",
        "tomcat-coyote.jar", "tomcat-jdbc.jar", "jaxb2-basics-ant-0.6.3.jar", "annotations-api.jar", "ecj-4.7.3a.jar",
        "mail-1.4.5-sources.jar", "commons-1.18.jar");

    @Test
    public void test_newlyAddedJars() throws Exception {

        Set<String> eimJars = new HashSet<>();
        File resourceFile = new File(".\\.\\.\\build.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DefaultHandler handler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                if (qName.equals("include")) {
                    eimJars.add(attributes.getValue("name"));
                }
            }
        };
        saxParser.parse(resourceFile, handler);
        Set<String> classpathJars =  Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator))
                .filter(element -> element.endsWith(".jar"))
                .map(element -> element.substring(element.lastIndexOf("\\") + 1, element.length()))
                .collect(Collectors.toSet());

        Set<String> newCommonJars = Sets.difference(classpathJars, eimJars);
        Set<String> missingJars = Sets.difference(newCommonJars, excludedJars);
        assertTrue("Unknown JAR files found. These must be added either in build.xml file of api-web "
            + "or in excludedJars of this class: " + missingJars, missingJars.isEmpty());
    }

}
