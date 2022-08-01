package com.cannontech.eim;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.jupiter.api.Test;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class EimJavaLibraryTest {

    private static Set<String> excludedJars = ImmutableSet.of(
        "activemq-openwire-legacy-5.13.4.jar",
        "ahessian.jar",
        "annotations-api.jar",
        "apache-log4j-extras-1.0.jar",
        "apiguardian-api-1.1.0.jar",
        "aspectjweaver-1.9.7.jar",
        "azure-core-1.23.1.jar",
        "azure-core-amqp-2.3.5.jar",
        "azure-core-http-netty-1.11.4.jar",
        "azure-messaging-servicebus-7.5.1.jar",
        "bsf-2.3.0.jar",
        "bsh.jar",
        "byte-buddy-1.12.10.jar",
        "byte-buddy-agent-1.12.10.jar",
        "catalina-ant.jar",
        "catalina-ha.jar",
        "catalina-ssi.jar",
        "catalina-storeconfig.jar",
        "catalina-tribes.jar",
        "catalina.jar",
        "commons-beanutils-1.9.4.jar",
        "commons-cli-1.4.jar",
        "commons-collections-3.2.2.jar",
        "commons-discovery-0.5.jar",
        "commons-lang-2.6.jar",
        "commons-validator-1.4.0.jar",
        "commons-vfs2-2.2.jar",
        "easymock-4.3.jar",
        "ecj-4.20.jar",
        "el-api.jar",
        "geojson-jackson-1.1.jar",
        "groovy-4.0.0.jar",
        "groovy-all-2.4.11.jar",
        "groovy-patch.jar",
        "gson-2.9.0.jar",
        "h2-1.4.178.jar",
        "hamcrest-2.2.jar",
        "hamcrest-core-2.2.jar",
        "hamcrest-library-2.2.jar",
        "itext.jar",
        "jasper-el.jar",
        "jasper.jar",
        "jaspic-api.jar",
        "javassist-3.26.0-GA.jar",
        "jakarta.mail-2.0.1.jar",
        "jaxb-xjc.jar",
        "jaxb2-basics-ant-0.6.3.jar",
        "jaxb2-default-value-1.1.jar",
        "jaxm-runtime.jar",
        "jjwt-impl-0.11.2.jar", 
        "jjwt-jackson-0.11.2.jar", 
        "jjwt-api-0.11.2.jar",
        "jna-4.5.0.jar",
        "jna-platform-4.5.0.jar",
        "js.jar",
        "jsch-0.1.54.jar",
        "jsoup-1.14.3.jar",
        "jsp-api.jar",
        "junit-jupiter-api-5.7.1.jar",
        "junit-jupiter-engine-5.7.1.jar",
        "junit-jupiter-params-5.7.1.jar",
        "junit-platform-commons-1.7.1.jar",
        "junit-platform-console-standalone-1.7.1.jar",
        "junit-platform-engine-1.7.1.jar",
        "lesscss-engine-1.4.2.jar",
        "libfonts-0.1.0.jar",
        "mockito-core-4.5.1.jar",
        "msp-beans-v3.jar",
        "msp-beans-v5.jar",
        "netty-all-4.0.47.Final.jar",
        "netty-buffer-4.1.70.Final.jar",
        "netty-codec-4.1.70.Final.jar", 
        "netty-codec-dns-4.1.70.Final.jar", 
        "netty-codec-http-4.1.70.Final.jar", 
        "netty-codec-http2-4.1.70.Final.jar", 
        "netty-codec-socks-4.1.70.Final.jar", 
        "netty-common-4.1.70.Final.jar", 
        "netty-handler-4.1.70.Final.jar", 
        "netty-handler-proxy-4.1.70.Final.jar", 
        "netty-reactive-streams-2.0.4.jar", 
        "netty-resolver-4.1.70.Final.jar", 
        "netty-resolver-dns-4.1.70.Final.jar",
        "netty-resolver-dns-classes-macos-4.1.70.Final.jar",
        "netty-resolver-dns-native-macos-4.1.70.Final-osx-x86_64.jar", 
        "netty-tcnative-boringssl-static-2.0.46.Final.jar", 
        "netty-tcnative-classes-2.0.46.Final.jar",
        "netty-transport-4.1.70.Final.jar", 
        "netty-transport-classes-epoll-4.1.70.Final.jar",
        "netty-transport-classes-kqueue-4.1.70.Final.jar",
        "netty-transport-native-epoll-4.1.70.Final-linux-x86_64.jar", 
        "netty-transport-native-kqueue-4.1.70.Final-osx-x86_64.jar", 
        "netty-transport-native-unix-common-4.1.70.Final.jar", 
        "objenesis-3.2.jar",
        "opentest4j-1.2.0.jar",
        "permit-reflect-0.4.jar",
        "poi.jar",
        "proton-j-0.33.8.jar",
        "protobuf-java-3.21.2.jar",
        "protobuf-java-format-1.4.jar",
        "protobuf-java-util-3.21.2.jar",
        "qpid-proton-j-extensions-1.2.4.jar",
        "reflections-0.9.12.jar",
        "servlet-api.jar",
        "slf4j-api-1.8.0-alpha2.jar",
        "spring-aop-5.3.22.jar",
        "spring-aspects-5.3.22.jar",
        "spring-beans-5.3.22.jar",
        "spring-context-5.3.22.jar",
        "spring-context-indexer-5.3.22.jar",
        "spring-context-support-5.3.22.jar",
        "spring-core-5.3.22.jar",
        "spring-expression-5.3.22.jar",
        "spring-instrument-5.3.22.jar",
        "spring-integration-core-5.5.14.jar",
        "spring-integration-jms-5.5.14.jar",
        "spring-jcl-5.3.22.jar",
        "spring-jdbc-5.3.22.jar",
        "spring-jms-5.3.22.jar",
        "spring-messaging-5.3.22.jar",
        "spring-orm-5.3.22.jar",
        "spring-oxm-5.3.22.jar",
        "spring-retry-1.3.3.jar",
        "spring-test-5.3.22.jar",
        "spring-tx-5.3.22.jar",
        "spring-web-5.3.22.jar",
        "spring-webflux-5.3.22.jar",
        "spring-webmvc-5.3.22.jar",
        "spring-websocket-5.3.22.jar",
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
        "tomcat-juli-9.0.65.jar",
        "tomcat-util-scan.jar",
        "tomcat-util.jar",
        "tomcat-websocket.jar",
        "validation-api-2.0.1.Final.jar",
        "websocket-api.jar",
        "wrapper.jar",
        "wrapperApp.jar",
        "wrapperApp9.jar",
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
        assertTrue(missingJars.isEmpty(), "Unknown JAR files found. These must be added either in build.xml file of api-web "
            + "or in excludedJars of this class: " + missingJars);
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

    ANT_JACOCO("jacocoant.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JACOCO_AGENT("jacocoagent.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_LAUNCHER("ant-launcher.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT("ant.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JUNIT4("ant-junit4.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JUNIT("ant-junit.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_APACHE("ant-apache-bsf.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_CONTRIN("ant-contrib-1.0b3.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_APACHE_ORO("ant-apache-oro.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_APACHE_RESOLVER("ant-apache-resolver.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JAVAMAIL("ant-javamail.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_NETREXX("ant-netrexx.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_ECJ("ecj-3.21.0.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JDEPEND("ant-jdepend.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JSCH("ant-jsch.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JUNITLAUNCHER("ant-junitlauncher.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_SWING("ant-swing.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_TESTUTIL("ant-testutil.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_LR("ant-antlr.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_XZ("ant-xz.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JMF("ant-jmf.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_XALAN("ant-apache-xalan2.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_LOG4J("ant-apache-log4j.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_IMAGEIO("ant-imageio.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_JAI("ant-jai.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_BCEL("ant-apache-bcel.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_NET("ant-commons-net.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_PLATFORM_LAUNCHER("junit-platform-launcher-1.7.1.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_REGXP("ant-apache-regexp.jar", IgnoreReason.ANT_BUILD_HELPERS),
    ANT_LOGGING("ant-commons-logging.jar", IgnoreReason.ANT_BUILD_HELPERS),

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
