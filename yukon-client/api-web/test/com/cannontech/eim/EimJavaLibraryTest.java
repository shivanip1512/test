package com.cannontech.eim;

import static org.junit.Assert.*;

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

    private static Set<String> excludedJars = ImmutableSet.of(
        "activemq-openwire-legacy-5.13.4.jar",
        "accessors-smart-1.2.jar",
        "activation-1.1.jar", 
        "adal4j-1.6.2.jar",
        "adapter-rxjava-2.4.0.jar",
        "ahessian.jar",
        "annotation-detector-3.0.4.jar",
        "annotations-api.jar",
        "apache-log4j-extras-1.0.jar",
        "applicationinsights-core-1.0.2.jar",
        "applicationinsights-web-1.0.2.jar",
        "asm-5.0.4.jar",
        "async-http-client-2.5.2.jar",
        "async-http-client-netty-utils-2.5.2.jar",
        "azure-annotations-1.7.0.jar",
        "azure-client-authentication-1.6.7.jar", 
        "azure-client-runtime-1.6.7.jar",
        "azure-servicebus-3.1.3.jar",
        "bsf-2.3.0.jar",
        "bsh.jar",
        "catalina-ant.jar",
        "catalina-ha.jar",
        "catalina-storeconfig.jar",
        "catalina-tribes.jar",
        "catalina.jar",
        "client-runtime-1.6.7.jar",
        "converter-jackson-2.5.0.jar",
        "commons-beanutils-1.9.4.jar",
        "commons-cli-1.4.jar",
        "commons-collections-3.2.2.jar",
        "commons-discovery-0.5.jar",
        "commons-lang-2.6.jar",
        "commons-validator-1.4.0.jar",
        "commons-vfs2-2.2.jar",
        "easymock-3.0.jar",
        "ecj-4.10.jar",
        "el-api.jar",
        "geojson-jackson-1.1.jar",
        "groovy-2.5.7.jar",
        "groovy-all-2.4.11.jar",
        "groovy-patch.jar",
        "gson-1.7.2.jar",
        "h2-1.4.178.jar",
        "hamcrest-core-1.3.jar",
        "itext.jar",
        "jackson-datatype-joda-2.9.8.jar",
        "jasper-el.jar",
        "jasper.jar",
        "jaspic-api.jar",
        "javax.activation-1.2.0.jar", 
        "javax.mail-1.6.1.jar",
        "jcip-annotations-1.0-1.jar",
        "json-smart-2.3.jar",
        "jaxb-xjc.jar",
        "jaxb2-basics-ant-0.6.3.jar",
        "jaxb2-default-value-1.1.jar",
        "jaxm-runtime.jar",
        "jfreereport.jar",
        "jjwt-impl-0.10.6.jar", 
        "jjwt-jackson-0.10.6.jar", 
        "jjwt-api-0.10.6.jar",
        "jna-4.5.0.jar",
        "jna-platform-4.5.0.jar",
        "js.jar",
        "jsch-0.1.54.jar",
        "jsoup-1.10.3.jar",
        "jsp-api.jar",
        "junit-4.12.jar",
        "lang-tag-1.4.4.jar",
        "logging-interceptor-3.11.0.jar",
        "lesscss-engine-1.4.2.jar",
        "libfonts-0.1.0.jar",
        "mail-1.4.5-sources.jar",
        "mail-1.4.5.jar",
        "msp-beans-v3.jar",
        "msp-beans-v5.jar",
        "netty-all-4.0.47.Final.jar",
        "netty-buffer-4.1.27.Final.jar",
        "netty-codec-4.1.27.Final.jar", 
        "netty-codec-dns-4.1.27.Final.jar", 
        "netty-codec-http-4.1.27.Final.jar", 
        "netty-codec-socks-4.1.27.Final.jar", 
        "netty-common-4.1.27.Final.jar", 
        "netty-handler-4.1.27.Final.jar", 
        "netty-handler-proxy-4.1.27.Final.jar", 
        "netty-reactive-streams-2.0.0.jar", 
        "netty-resolver-4.1.27.Final.jar", 
        "netty-resolver-dns-4.1.27.Final.jar", 
        "netty-transport-4.1.27.Final.jar", 
        "netty-transport-native-epoll-4.1.27.Final-linux-x86_64.jar", 
        "netty-transport-native-unix-common-4.1.27.Final.jar", 
        "nimbus-jose-jwt-8.2.1.jar",
        "oauth2-oidc-sdk-5.64.4.jar", 
        "okhttp-3.12.0.jar", 
        "okhttp-urlconnection-3.11.0.jar",
        "okio-1.15.0.jar", 
        "objenesis-1.2.jar",
        "poi.jar",
        "proton-j-0.31.0.jar",
        "qpid-proton-j-extensions-1.2.1.jar",
        "retrofit-2.5.0.jar",
        "rxjava-1.3.8.jar",
        "servlet-api.jar",
        "slf4j-api-1.8.0-alpha2.jar",
        "spring-aop-5.1.5.RELEASE.jar",
        "spring-aspects-5.1.5.RELEASE.jar",
        "spring-beans-5.1.5.RELEASE.jar",
        "spring-context-5.1.5.RELEASE.jar",
        "spring-context-indexer-5.1.5.RELEASE.jar",
        "spring-context-support-5.1.5.RELEASE.jar",
        "spring-core-5.1.5.RELEASE.jar",
        "spring-expression-5.1.5.RELEASE.jar",
        "spring-instrument-5.1.5.RELEASE.jar",
        "spring-integration-core-5.1.3.RELEASE.jar",
        "spring-integration-jms-5.1.3.RELEASE.jar",
        "spring-jcl-5.1.5.RELEASE.jar",
        "spring-jdbc-5.1.5.RELEASE.jar",
        "spring-jms-5.1.5.RELEASE.jar",
        "spring-messaging-5.1.5.RELEASE.jar",
        "spring-orm-5.1.5.RELEASE.jar",
        "spring-oxm-5.1.5.RELEASE.jar",
        "spring-test-5.1.5.RELEASE.jar",
        "spring-tx-5.1.5.RELEASE.jar",
        "spring-web-5.1.5.RELEASE.jar",
        "spring-webflux-5.1.5.RELEASE.jar",
        "spring-webmvc-5.1.5.RELEASE.jar",
        "spring-websocket-5.1.5.RELEASE.jar",
        "stax-ex-1.8.jar",
        "tomcat-api.jar",
        "tomcat-coyote.jar",
        "tomcat-dbcp.jar",
        "tomcat-i18n-cs.jar",
        "tomcat-i18n-de.jar",
        "tomcat-i18n-es.jar",
        "tomcat-i18n-fr.jar",
        "tomcat-i18n-ja.jar",
        "tomcat-i18n-ko.jar",
        "tomcat-i18n-pt-BR.jar",
        "tomcat-i18n-ru.jar",
        "tomcat-i18n-zh-CN.jar",
        "tomcat-jdbc.jar",
        "tomcat-jni.jar",
        "tomcat-juli-9.0.20.jar",
        "tomcat-util-scan.jar",
        "tomcat-util.jar",
        "tomcat-websocket.jar",
        "validation-api-2.0.1.Final.jar",
        "websocket-api.jar",
        "wrapper.jar",
        "wrapperApp.jar",
        "wro4j-core-1.6.3.jar",
        "wro4j-extensions-1.6.3.jar",
        "xbean-spring-4.5.jar",
        "yuicompressor-2.4.8.jar");

    @Test
    public void test_newlyAddedJars() throws Exception {

        Set<String> eimJars = new HashSet<>();
        String userDirectory = System.getProperty("user.dir");
        if (!userDirectory.contains("api-web")) {
            String clientDir = userDirectory.substring(0, userDirectory.lastIndexOf("\\") + 1);
            userDirectory = clientDir + "api-web";
        }
        File resourceFile = new File(userDirectory + File.separatorChar + "build.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DefaultHandler handler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                if (qName.equals("include") && attributes.getValue("name").endsWith(".jar")) {
                    eimJars.add(attributes.getValue("name"));
                }
            }
        };
        saxParser.parse(resourceFile, handler);
        Set<String> classpathJars =
            Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator)).filter(
                element -> element.endsWith(".jar")).map(
                    element -> element.substring(element.lastIndexOf("\\") + 1, element.length())).collect(
                        Collectors.toSet());

        Set<String> thirdPartyFilenames = Sets.difference(classpathJars, IgnoredThirdPartyJavaLibraries.getFilenames());
        Set<String> newCommonJars = Sets.difference(thirdPartyFilenames, eimJars);
        Set<String> missingJars = Sets.difference(newCommonJars, excludedJars);
        assertTrue("Unknown JAR files found. These must be added either in build.xml file of api-web "
            + "or in excludedJars of this class: " + missingJars, missingJars.isEmpty());
    }
}

enum IgnoreReason {
    YUKON_PROJECT("Yukon project JARs owned by Eaton, third-party license not applicable"),
    ANT_BUILD_HELPERS("Ant helpers on the classpath while the test is executed, but not redistributed"),
    COMPILED_ITRON_WSDL("Itron WSDL jars, license indeterminate");

    private String description;

    IgnoreReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}

enum IgnoredThirdPartyJavaLibraries {

    API("api.jar", IgnoreReason.YUKON_PROJECT),
    BILLING("billing.jar", IgnoreReason.YUKON_PROJECT),
    CBC("cbc.jar", IgnoreReason.YUKON_PROJECT),
    COMMON("common.jar", IgnoreReason.YUKON_PROJECT),
    DBEDITOR("dbeditor.jar", IgnoreReason.YUKON_PROJECT),
    EXPORT("export.jar", IgnoreReason.YUKON_PROJECT),
    GRAPH("graph.jar", IgnoreReason.YUKON_PROJECT),
    I18N_EN_US("i18n-en_US.jar", IgnoreReason.YUKON_PROJECT),
    MACS("macs.jar", IgnoreReason.YUKON_PROJECT),
    MULTISPEAK("multispeak.jar", IgnoreReason.YUKON_PROJECT),
    NOTIFICATION("notification.jar", IgnoreReason.YUKON_PROJECT),
    REPORT("report.jar", IgnoreReason.YUKON_PROJECT),
    SCHEMA_COMPARE("SchemaCompare.jar", IgnoreReason.YUKON_PROJECT),
    SCHEMA_DUMP_TO_XML("SchemaDumpToXml.jar", IgnoreReason.YUKON_PROJECT),
    SERVICES("services.jar", IgnoreReason.YUKON_PROJECT),
    SIMULATORS("simulators.jar", IgnoreReason.YUKON_PROJECT),
    TDC("tdc.jar", IgnoreReason.YUKON_PROJECT),
    TOOLS("tools.jar", IgnoreReason.YUKON_PROJECT),
    WEB_COMMON("web-common.jar", IgnoreReason.YUKON_PROJECT),
    YC("yc.jar", IgnoreReason.YUKON_PROJECT),
    YUKON_APP_SERVER("yukonappserver.jar", IgnoreReason.YUKON_PROJECT),
    YUKON_CLOUD_SERVICE("yukon-cloud-service.jar", IgnoreReason.YUKON_PROJECT),
    YUKON_HELP("yukon-help.jar", IgnoreReason.YUKON_PROJECT),
    YUKON_MESSAGE_BROKER("yukon-message-broker.jar", IgnoreReason.YUKON_PROJECT),
    YUKON_SHARED("yukon-shared.jar", IgnoreReason.YUKON_PROJECT),
    YUKON_WATCHDOG_SERVICE("yukon-watchdog-service.jar", IgnoreReason.YUKON_PROJECT),
    YUKON_WEB("yukon-web.jar", IgnoreReason.YUKON_PROJECT),
    YUKON_WEB_JSP("yukon-web-jsp.jar", IgnoreReason.YUKON_PROJECT),

    ANT_LAUNCHER("ant-launcher.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT("ant.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JUNIT4("ant-junit4.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JUNIT("ant-junit.jar", IgnoreReason.ANT_BUILD_HELPERS),

    ITRON_DEVICE_MANAGER_TYPES("itronDeviceManagerTypes_v1_8.jar", IgnoreReason.COMPILED_ITRON_WSDL),
    ITRON_SERVICE_POINT_MANAGER_TYPES("itronServicePointManagerTypes_v1_3.jar", IgnoreReason.COMPILED_ITRON_WSDL),
    ITRON_PROGRAM_EVENT_MANAGER_TYPES("itronProgramEventManagerTypes_v1_6.jar", IgnoreReason.COMPILED_ITRON_WSDL),
    ITRON_PROGRAM_MANAGER_TYPES("itronProgramManagerTypes_v1_1.jar", IgnoreReason.COMPILED_ITRON_WSDL),
    ITRON_REPORT_MANAGER_TYPES("itronReportManagerTypes_v1_2.jar", IgnoreReason.COMPILED_ITRON_WSDL);
    private static Set<String> filenames =
        Arrays.stream(values()).map(IgnoredThirdPartyJavaLibraries::getFilename).collect(Collectors.toSet());

    private String filename;
    @SuppressWarnings("unused") private IgnoreReason reason;

    private IgnoredThirdPartyJavaLibraries(String filename, IgnoreReason reason) {
        this.filename = filename;
        this.reason = reason;
    }

    String getFilename() {
        return filename;
    }

    public static Set<String> getFilenames() {
        return filenames;
    }
}
