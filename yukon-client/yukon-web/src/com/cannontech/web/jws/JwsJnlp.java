package com.cannontech.web.jws;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public enum JwsJnlp {
    DBEDITOR("dbeditor.jnlp",
             "Database Editor",
             "Allows users to add, modify, or remove database items in the Yukon database.",
             "com.cannontech.dbeditor.DatabaseEditor",
             "dbeditor.jar",
             "/WebConfig/yukon/Icons/DatabaseEditor64.gif"),
    TDC("tdc.jnlp",
        "Tabular Data Console",
        "Allows users to view real-time data in tabular format.",
        "com.cannontech.tdc.TDCMainFrame",
        "tdc.jar",
        "/WebConfig/yukon/Icons/TDC64.gif",
        "graph.jar", "cbc.jar", "macs.jar"),
    TRENDING("trending.jnlp",
             "Trending",
             "Allows users to view real-time data in graphical format.",
             "com.cannontech.graph.GraphClient",
             "graph.jar",
             "/WebConfig/yukon/Icons/GraphTrending64.gif"),
    ESUB("esub.jnlp",
         "eSubstation Editor",
         "Allows users to create, modify, and save eSubstation drawings.",
         "com.cannontech.esub.editor.Editor",
         "esub-editor.jar",
         "/WebConfig/yukon/Icons/Esubstation64.gif",
         "esub.jar", "dbeditor.jar"),
    COMMANDER("commander.jnlp",
              "Commander",
              "Allows users to send commands to field devices.",
              "com.cannontech.clientutils.commander.YukonCommander",
              "yc.jar",
              "/WebConfig/yukon/Icons/Commander64.gif"),
    BOUNCY_CASTLE("bc.jnlp",
                  "Bouncy Castle libraries",
                  "Bouncy Castle libraries",
                  null, null, null,
                  "bcpkix-jdk15on-149.jar",
                  "bcprov-jdk15on-149.jar"),
    CLIENT_LIBS("client_libs.jnlp",
                "Client Libraries",
                "Libraries shared by all web start clients.",
                null, null, null,
                "activemq-broker-5.9.1-amq-4906.jar",
                "activemq-client-5.9.1.jar",
                "activemq-kahadb-store-5.9.1.jar",
                "activemq-openwire-legacy-5.9.1.jar",
                "activemq-protobuf-1.1.jar",
                "antlr-2.7.6rc1.jar",
                "aopalliance-1.0.jar",
                "asm.jar",
                "asm-attrs.jar",
                "axis.jar",
                "batik.jar",
                "billing.jar",
                "cglib-2.1.3.jar",
                "common.jar",
                "commons-codec-1.9.jar",
                "commons-collections-3.2.1.jar",
                "commons-collections4-4.0.jar",
                "commons-dbcp2-2.0.jar",
                "commons-discovery-0.5.jar",
                "commons-fileupload-1.3.1.jar",
                "commons-httpclient.jar",
                "commons-io-2.4.jar",
                "commons-lang3-3.3.1.jar",
                "commons-logging-1.1.3.jar",
                "commons-pool2-2.2.jar",
                "dom4j-1.6.1.jar",
                "ecs-1.4.2.jar",
                "geronimo-j2ee-management_1.1_spec-1.0.1.jar",
                "geronimo-jms_1.1_spec-1.1.1.jar",
                "guava-13.0.1.jar",
                "hawtbuf-1.9.jar",
                "i18n-en_US.jar",
                "jackson-annotations-2.2.3.jar",
                "jackson-core-2.2.3.jar",
                "jackson-databind-2.2.3.jar",
                "jasypt-1.9.0.jar",
                "jaxb-api.jar",
                "jaxb-impl.jar",
                "jaxm-api.jar",
                "jaxrpc.jar",
                "jcfield401K.jar",
                "jcommon.jar",
                "jcpagelayout450K.jar",
                "jdom-1.1.3.jar",
                "jdom-2.0.2.jar",
                "jfreechart.jar",
                "jloox30.jar",
                "joda-time-2.3.jar",
                "jradius-client.jar",
                "jta.jar",
                "jtds-1.3.0.jar",
                "libthrift-0.9.0.jar",
                "loadcontrol.jar",
                "log4j-1.2.17.jar",
                "lucene-core-3.4.0.jar",
                "mailapi-1.5.1.jar",
                "nagasena.jar",
                "ojdbc6.jar",
                "org.osgi.core-4.1.0.jar",
                "org.springframework.aop-3.1.3.RELEASE.jar",
                "org.springframework.asm-3.1.3.RELEASE.jar",
                "org.springframework.aspects-3.1.3.RELEASE.jar",
                "org.springframework.beans-3.1.3.RELEASE.jar",
                "org.springframework.context.support-3.1.3.RELEASE.jar",
                "org.springframework.context-3.1.3.RELEASE.jar",
                "org.springframework.core-3.1.3.RELEASE.jar",
                "org.springframework.expression-3.1.3.RELEASE.jar",
                "org.springframework.instrument.tomcat-3.1.3.RELEASE.jar",
                "org.springframework.instrument-3.1.3.RELEASE.jar",
                "org.springframework.jdbc-3.1.3.RELEASE.jar",
                "org.springframework.jms-3.1.3.RELEASE.jar",
                "org.springframework.orm-3.1.3.RELEASE.jar",
                "org.springframework.oxm-3.1.3.RELEASE.jar",
                "org.springframework.test-3.1.3.RELEASE.jar",
                "org.springframework.transaction-3.1.3.RELEASE.jar",
                "org.springframework.web.portlet-3.1.3.RELEASE.jar",
                "org.springframework.web.servlet-3.1.3.RELEASE.jar",
                "org.springframework.web-3.1.3.RELEASE.jar",
                "quartz-all-1.6.0.jar",
                "saaj.jar",
                "servlet-api.jar",
                "slf4j-api-1.7.5.jar",
                "slf4j-log4j12-1.7.2.jar",
                "smtp-1.5.1.jar",
                "spring-integration-core-2.1.4.RELEASE.jar",
                "spring-integration-jms-2.1.4.RELEASE.jar",
                "spring-xml-2.1.4.RELEASE.jar",
                "stax2-api-3.1.4.jar",
                "SqlServer.jar",
                "xbean-spring-3.4.jar",
                "xercesImpl-2.11.0.jar",
                "xml-apis-1.4.01.jar",
                "yukonappserver.jar",
                "yukon-help.jar"
                ),
    ;

    private final static Map<String, JwsJnlp> jnlps;
    static {
        Builder<String, JwsJnlp> builder = ImmutableMap.builder();
        for (JwsJnlp app : values()) {
            builder.put(app.getPath(), app);
        }
        jnlps = builder.build();
    }

    private final String path;
    private final String title;
    private final String description;
    private final String appMainClass;
    private final String appMainClassJar;
    private final String appIcon;
    private final String[] jars;

    private JwsJnlp(String path, String title, String description, String appMainClass, String appMainClassJar,
            String appIcon, String ...jars) {
        this.path = path;
        this.title = title;
        this.description = description;
        this.appMainClass = appMainClass;
        this.appMainClassJar = appMainClassJar;
        this.appIcon = appIcon;
        this.jars = jars;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAppMainClass() {
        return appMainClass;
    }

    public String getAppMainClassJar() {
        return appMainClassJar;
    }

    public String getAppIcon() {
        return appIcon;
    }
    
    public static JwsJnlp getFromPath(String path) {
        return jnlps.get(path);
    }

    public String[] getAppJars() {
        return jars;
    }
}
