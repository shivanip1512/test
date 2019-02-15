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

    private static Set<String> excludedJars = ImmutableSet.of("ahessian.jar", "annotation-detector-3.0.4.jar",
        "annotations-api.jar", "ant-junit.jar", "ant-junit4.jar", "ant-launcher.jar", "ant.jar",
        "apache-log4j-extras-1.0.jar", "applicationinsights-core-1.0.2.jar", "applicationinsights-web-1.0.2.jar",
        "applicationisights-web-1.0.2.jar", "azure-core-0.9.7.jar", "azure-media-0.9.0.jar",
        "azure-servicebus-0.9.7.jar", "azure-serviceruntime-0.9.0.jar", "azure-storage-4.0.0.jar",
        "azure-svc-mgmt-0.9.0.jar", "azure-svc-mgmt-compute-0.9.0.jar", "azure-svc-mgmt-media-0.9.0.jar",
        "azure-svc-mgmt-network-0.9.0.jar", "azure-svc-mgmt-scheduler-0.9.0.jar", "azure-svc-mgmt-sql-0.9.0.jar",
        "azure-svc-mgmt-storage-0.9.0.jar", "azure-svc-mgmt-websites-0.9.0.jar", "azure-tracing-util-0.9.0.jar",
        "bsf-2.3.0.jar", "bsh.jar", "catalina-ant.jar", "catalina-ha.jar", "catalina-storeconfig.jar",
        "catalina-tribes.jar", "catalina.jar", "commons-1.18.jar", "commons-beanutils-1.9.3.jar",
        "commons-cli-1.3.1.jar", "commons-codec-1.9.jar", "commons-collections-3.2.2.jar", "commons-discovery-0.5.jar",
        "commons-lang-2.6.jar", "commons-validator-1.4.0.jar", "commons-vfs2-2.0.jar",
        "concurrentlinkedhashmap-lru-1.4.jar", "easymock-3.0.jar", "ecj-4.7.3a.jar", "el-api.jar",
        "geojson-jackson-1.1.jar", "groovy-all-2.4.11.jar", "groovy-patch.jar", "gson-1.7.2.jar", "guava-13.0.1.jar",
        "h2-1.4.178.jar", "hamcrest-core-1.3.jar", "httpclient-4.3.4.jar", "httpcore-4.3.2.jar", "itext.jar",
        "jackson-core-asl-1.9.2.jar", "jackson-dataformat-csv-2.9.6.jarsnakeyaml-1.18.2.jar", "jackson-jaxrs-1.9.2.jar",
        "jackson-mapper-asl-1.9.2.jar", "jackson-xc-1.9.2.jar", "jasper-el.jar", "jasper.jar", "jaspic-api.jar",
        "javax.inject-1.jar", "jaxb-impl-2.2.3-1.jar", "jaxb-xjc.jar", "jaxb2-basics-ant-0.6.3.jar",
        "jaxb2-default-value-1.1.jar", "jaxm-runtime.jar", "jaxrpc.jar", "jersey-client-1.13.jar",
        "jersey-core-1.13.jar", "jersey-json-1.13.jar", "jettison-1.1.jar", "jfreereport.jar", "jjwt-0.5.1.jar",
        "jna-4.5.0.jar", "jna-platform-4.5.0.jar", "js.jar", "jsch-0.1.54.jar", "jsoup-1.10.3.jar", "jsp-api.jar",
        "jsr305-1.3.9.jar", "junit-4.12.jar", "lesscss-engine-1.4.2.jar", "libfonts-0.1.0.jar",
        "mail-1.4.5-sources.jar", "mail-1.4.5.jar", "mx4j-impl.jar", "mx4j-jmx.jar", "mx4j-remote.jar",
        "mx4j-rimpl.jar", "mx4j-rjmx.jar", "netty-all-4.0.47.Final.jar", "objenesis-1.2.jar", "poi.jar",
        "SchemaCompare.jar", "SchemaDumpToXml.jar", "servlet-api.jar", "spring-aop-5.0.2.RELEASE.jar",
        "spring-aspects-5.0.2.RELEASE.jar", "spring-beans-5.0.2.RELEASE.jar", "spring-context-5.0.2.RELEASE.jar",
        "spring-context-indexer-5.0.2.RELEASE.jar", "spring-context-support-5.0.2.RELEASE.jar",
        "spring-core-5.0.2.RELEASE.jar", "spring-expression-5.0.2.RELEASE.jar", "spring-instrument-5.0.2.RELEASE.jar",
        "spring-integration-core-5.0.2.RELEASE.jar", "spring-integration-jms-5.0.2.RELEASE.jar",
        "spring-jcl-5.0.2.RELEASE.jar", "spring-jdbc-5.0.2.RELEASE.jar", "spring-jms-5.0.2.RELEASE.jar",
        "spring-messaging-5.0.2.RELEASE.jar", "spring-orm-5.0.2.RELEASE.jar", "spring-oxm-5.0.2.RELEASE.jar",
        "spring-test-5.0.2.RELEASE.jar", "spring-tx-5.0.2.RELEASE.jar", "spring-web-5.0.2.RELEASE.jar",
        "spring-webflux-5.0.2.RELEASE.jar", "spring-webmvc-5.0.2.RELEASE.jar", "spring-websocket-5.0.2.RELEASE.jar",
        "stax-ex-1.8.jar", "tomcat-api.jar", "tomcat-coyote.jar", "tomcat-dbcp.jar", "tomcat-i18n-es.jar",
        "tomcat-i18n-fr.jar", "tomcat-i18n-ja.jar", "tomcat-i18n-ru.jar", "tomcat-jdbc.jar", "tomcat-jni.jar",
        "tomcat-juli-9.0.12.jar", "tomcat-util-scan.jar", "tomcat-util.jar", "tomcat-websocket.jar",
        "websocket-api.jar", "wrapper.jar", "wrapperApp.jar", "wro4j-core-1.6.3.jar", "wro4j-extensions-1.6.3.jar",
        "xbean-spring-4.5.jar", "yuicompressor-2.4.8.jar", "yukon-help.jar");

    @Test
    public void test_newlyAddedJars() throws Exception {

        Set<String> eimJars = new HashSet<>();
        File resourceFile = new File(System.getProperty("user.dir")+"\\build.xml");
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
