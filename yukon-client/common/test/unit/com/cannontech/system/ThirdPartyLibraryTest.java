package com.cannontech.system;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.CollectionUtils;
import org.h2.util.StringUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.common.stream.StreamUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class ThirdPartyLibraryTest {

    private static Set<String> excludedJars = ImmutableSet.of(
            //  Yukon project JARs owned by Eaton, third-party license not applicable
            "api.jar",
            "billing.jar",
            "cbc.jar",
            "common.jar",
            "dbeditor.jar",
            "export.jar",
            "graph.jar",
            "i18n-en_US.jar",
            "macs.jar",
            "multispeak.jar",
            "notification.jar",
            "report.jar",
            "SchemaCompare.jar",
            "SchemaDumpToXml.jar",
            "services.jar",
            "simulators.jar",
            "tdc.jar",
            "tools.jar",
            "web-common.jar",
            "yc.jar",
            "yukonappserver.jar",
            "yukon-help.jar",
            "yukon-message-broker.jar",
            "yukon-shared.jar",
            "yukon-watchdog-service.jar",
            "yukon-web.jar",
            "yukon-web-jsp.jar",
            //  Ant helpers on the classpath while the test is executed, but not redistributed
            "ant-launcher.jar",
            "ant.jar", 
            "ant-junit4.jar", 
            "ant-junit.jar",
            "itronDeviceManagerTypes_v1_8.jar",
            "itronServicePointManagerTypes_v1_3.jar");

    private static Set<String> tomcatJars = ImmutableSet.of(
            //  Tomcat libraries stored in yukon-install that are not available to check during build, but are present when this test is run from Eclipse
            "annotations-api.jar",
            "catalina-ant.jar",
            "catalina-ha.jar",
            "catalina-storeconfig.jar",
            "catalina-tribes.jar",
            "ecj-4.7.3a.jar",
            "el-api.jar",
            "jasper-el.jar",
            "jasper.jar",
            "jaspic-api.jar",
            "tomcat-coyote.jar",
            "tomcat-dbcp.jar",
            "tomcat-i18n-es.jar",
            "tomcat-i18n-fr.jar",
            "tomcat-i18n-ja.jar",
            "tomcat-i18n-ru.jar",
            "tomcat-jdbc.jar",
            "tomcat-jni.jar",
            "tomcat-websocket.jar",
            "websocket-api.jar",
            "catalina.jar",
            "tomcat-juli-9.0.12.jar");

    private static Stream<File> recurse(File f) {
        if (f.isDirectory()) {
            return Arrays.stream(f.listFiles()).flatMap(ThirdPartyLibraryTest::recurse);
        }
        return Stream.of(f);
    }
    
    @Test
    public void test_thirdPartyLicenses() throws IOException, NoSuchAlgorithmException {

        ClassPathResource libraryYaml = new ClassPathResource("thirdPartyLibraries.yaml");
        
        ThirdPartyLibrary[] documentedLibraries = ThirdPartyLibraryParser.parse(libraryYaml.getInputStream());
        
        Map<String, ThirdPartyLibrary> documentedLibrariesByFilename = 
                Arrays.stream(documentedLibraries)
                    .collect(StreamUtils.mapToSelf(l -> l.filename));
        
        String javaClassPath = System.getProperty("java.class.path");
        Multimap<String, File> classpathJars = Arrays.stream(javaClassPath.split(File.pathSeparator))
                .map(File::new)
                .flatMap(ThirdPartyLibraryTest::recurse)
                .filter(f -> f.getName().endsWith(".jar"))
                .collect(StreamUtils.toMultimap(File::getName, Function.identity()));
        
        Set<String> thirdPartyFilenames = Sets.difference(classpathJars.keySet(), excludedJars);
        
        Set<String> unknownFiles = Sets.difference(thirdPartyFilenames, documentedLibrariesByFilename.keySet());
        assertTrue("Unknown JAR files found: " + unknownFiles, unknownFiles.isEmpty());

        Set<String> missingFiles = Sets.difference(documentedLibrariesByFilename.keySet(), Sets.union(thirdPartyFilenames, tomcatJars));
        assertTrue("JAR files listed in thirdPartyLibraries.yaml, but missing from classpath: " + missingFiles, missingFiles.isEmpty());
        
        MessageDigest md_md5 = MessageDigest.getInstance("MD5");
        MessageDigest md_sha1 = MessageDigest.getInstance("SHA1");

        documentedLibrariesByFilename.entrySet().stream().forEach(e -> {
            assertFalse(e.getKey() + " must have a project name", StringUtils.isNullOrEmpty(e.getValue().project));
            assertFalse(e.getKey() + " must have a project version", StringUtils.isNullOrEmpty(e.getValue().version));
            assertFalse(e.getKey() + " must have a project URL", StringUtils.isNullOrEmpty(e.getValue().projectUrl));
            assertFalse(e.getKey() + " must have a Maven URL", StringUtils.isNullOrEmpty(e.getValue().mavenUrl));
            assertThat(e.getKey() + " must have a valid Maven URL", e.getValue().mavenUrl, anyOf(startsWith("https://mvnrepository.com/artifact/"), equalTo("n/a")));
            assertFalse(e.getKey() + " must have a license type", CollectionUtils.isEmpty(e.getValue().licenses));
            assertFalse(e.getKey() + " must have a license URL", CollectionUtils.isEmpty(e.getValue().licenseUrls));
            assertFalse(e.getKey() + " must have a JIRA entry", StringUtils.isNullOrEmpty(e.getValue().jira));
            assertNotNull(e.getKey() + " must have an updated date", e.getValue().updated);
            for (File f : classpathJars.get(e.getKey())) {
                Path p = f.toPath();
                byte[] contents;
                try {
                    contents = Files.readAllBytes(p);
                } catch (IOException e1) {
                    throw new RuntimeException("Could not read " + p, e1);
                }
                String md5 = Hex.encodeHexString(md_md5.digest(contents));
                String sha1 = Hex.encodeHexString(md_sha1.digest(contents));
                assertEquals("MD5 mismatch for " + e.getKey() + " " + p + "\n", e.getValue().md5, md5);
                assertEquals("SHA1 mismatch for " + e.getKey() + " " + p + "\n", e.getValue().sha1, sha1);
            }
        });
    }
}
