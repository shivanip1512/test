package com.cannontech.system;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.cannontech.system.IgnoredThirdPartyJavaLibraries.IgnoreReason.*;

public enum IgnoredThirdPartyJavaLibraries {

    API("api.jar", YUKON_PROJECT),
    BILLING("billing.jar", YUKON_PROJECT),
    CBC("cbc.jar", YUKON_PROJECT),
    COMMON("common.jar", YUKON_PROJECT),
    DBEDITOR("dbeditor.jar", YUKON_PROJECT),
    EXPORT("export.jar", YUKON_PROJECT),
    GRAPH("graph.jar", YUKON_PROJECT),
    I18N_EN_US("i18n-en_US.jar", YUKON_PROJECT),
    MACS("macs.jar", YUKON_PROJECT),
    MULTISPEAK("multispeak.jar", YUKON_PROJECT),
    NOTIFICATION("notification.jar", YUKON_PROJECT),
    REPORT("report.jar", YUKON_PROJECT),
    SCHEMA_COMPARE("SchemaCompare.jar", YUKON_PROJECT),
    SCHEMA_DUMP_TO_XML("SchemaDumpToXml.jar", YUKON_PROJECT),
    SERVICES("services.jar", YUKON_PROJECT),
    SIMULATORS("simulators.jar", YUKON_PROJECT),
    TDC("tdc.jar", YUKON_PROJECT),
    TOOLS("tools.jar", YUKON_PROJECT),
    WEB_COMMON("web-common.jar", YUKON_PROJECT),
    YC("yc.jar", YUKON_PROJECT),
    YUKON_APP_SERVER("yukonappserver.jar", YUKON_PROJECT),
    YUKON_HELP("yukon-help.jar", YUKON_PROJECT),
    YUKON_MESSAGE_BROKER("yukon-message-broker.jar", YUKON_PROJECT),
    YUKON_SHARED("yukon-shared.jar", YUKON_PROJECT),
    YUKON_WATCHDOG_SERVICE("yukon-watchdog-service.jar", YUKON_PROJECT),
    YUKON_WEB("yukon-web.jar", YUKON_PROJECT),
    YUKON_WEB_JSP("yukon-web-jsp.jar", YUKON_PROJECT),

    ANT_LAUNCHER("ant-launcher.jar", ANT_BUILD_HELPERS),
    ANT("ant.jar", ANT_BUILD_HELPERS), 
    ANT_JUNIT4("ant-junit4.jar", ANT_BUILD_HELPERS), 
    ANT_JUNIT("ant-junit.jar", ANT_BUILD_HELPERS),
    
    ITRON_DEVICE_MANAGER_TYPES("itronDeviceManagerTypes_v1_8.jar", COMPILED_ITRON_WSDL), 
    ITRON_SERVICE_POINT_MANAGER_TYPES("itronServicePointManagerTypes_v1_3.jar", COMPILED_ITRON_WSDL);

    enum IgnoreReason {
        YUKON_PROJECT("Yukon project JARs owned by Eaton, third-party license not applicable"),
        ANT_BUILD_HELPERS("Ant helpers on the classpath while the test is executed, but not redistributed"),
        COMPILED_ITRON_WSDL("Itron WSDL jars, license indeterminate");
        
        private String description;
        
        IgnoreReason(String description) {
            this.description = description;
        }

        @SuppressWarnings("unused")
        public String getDescription() {
            return description;
        }
    }

    private static Set<String> filenames = Arrays.stream(values())
            .map(IgnoredThirdPartyJavaLibraries::getFilename)
            .collect(Collectors.toSet());

    private String filename;
    @SuppressWarnings("unused")
    private IgnoreReason reason;
            
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
