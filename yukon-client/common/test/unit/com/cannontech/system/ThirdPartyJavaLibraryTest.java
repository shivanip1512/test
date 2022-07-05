package com.cannontech.system;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.YamlParserUtils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class ThirdPartyJavaLibraryTest {

    private static Set<String> tomcatJars = ImmutableSet.of(
            //  Tomcat libraries stored in yukon-install that are not available to check during build, but are present when this test is run from Eclipse
            "annotations-api.jar",
            "catalina-ant.jar",
            "catalina-ha.jar",
            "catalina-ssi.jar",
            "catalina-storeconfig.jar",
            "catalina-tribes.jar",
            "ecj-4.20.jar",
            "el-api.jar",
            "jasper-el.jar",
            "jasper.jar",
            "jaspic-api.jar",
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
            "tomcat-websocket.jar",
            "websocket-api.jar",
            "catalina.jar",
            "tomcat-juli-9.0.56.jar");

    private static Stream<File> recurse(File f) {
        if (f.isDirectory()) {
            return Arrays.stream(f.listFiles()).flatMap(ThirdPartyJavaLibraryTest::recurse);
        }
        return Stream.of(f);
    }
    
    @Test
    public void test_thirdPartyLicenses() throws IOException, NoSuchAlgorithmException {

        ClassPathResource libraryYaml = new ClassPathResource("thirdPartyLibraries.yaml");
        
        ThirdPartyLibraries documentedLibraries = YamlParserUtils.parseToObject(libraryYaml.getInputStream(),
                ThirdPartyLibraries.class);
        
        Map<String, ThirdPartyJavaLibrary> documentedLibrariesByFilename = Maps.uniqueIndex(documentedLibraries.javaLibraries, l -> l.filename); 
        
        String javaClassPath = System.getProperty("java.class.path");
        Multimap<String, File> classpathJars = Arrays.stream(javaClassPath.split(File.pathSeparator))
                .map(File::new)
                .flatMap(ThirdPartyJavaLibraryTest::recurse)
                .filter(f -> f.getName().endsWith(".jar"))
                .collect(StreamUtils.toMultimap(File::getName, Function.identity()));
        
        Set<String> thirdPartyFilenames = Sets.difference(classpathJars.keySet(), IgnoredThirdPartyJavaLibraries.getFilenames());
        
        Set<String> unknownFiles = Sets.difference(thirdPartyFilenames, documentedLibrariesByFilename.keySet());
        //assertTrue(unknownFiles.isEmpty(), "Unknown JAR files found.  These must be added to thirdPartyLibraries.yaml or IgnoredThirdPartyJavaLibraries.java: " + unknownFiles);

        Set<String> missingFiles = Sets.difference(documentedLibrariesByFilename.keySet(), Sets.union(thirdPartyFilenames, tomcatJars));
        assertTrue(missingFiles.isEmpty(), "JAR files listed in thirdPartyLibraries.yaml, but missing from classpath: " + missingFiles);
        
        MessageDigest md_md5 = MessageDigest.getInstance("MD5");
        MessageDigest md_sha1 = MessageDigest.getInstance("SHA1");

        documentedLibrariesByFilename.entrySet().stream().forEach(e -> {
            assertNotNull(e.getValue().group, e.getKey() + " must have a Yukon library group");
            assertFalse(StringUtils.isEmpty(e.getValue().project), e.getKey() + " must have a project name");
            assertFalse(StringUtils.isEmpty(e.getValue().version), e.getKey() + " must have a project version");
            assertFalse(StringUtils.isEmpty(e.getValue().projectUrl), e.getKey() + " must have a project URL");
            assertFalse(StringUtils.isEmpty(e.getValue().mavenUrl), e.getKey() + " must have a Maven URL");
            assertThat(e.getKey() + " must have a valid Maven URL", e.getValue().mavenUrl, anyOf(startsWith("https://mvnrepository.com/artifact/"), equalTo("n/a")));
            assertFalse(CollectionUtils.isEmpty(e.getValue().licenses), e.getKey() + " must have a license type");
            assertFalse(CollectionUtils.isEmpty(e.getValue().licenseUrls), e.getKey() + " must have a license URL");
            assertFalse(StringUtils.isEmpty(e.getValue().jira), e.getKey() + " must have a JIRA entry");
            assertNotNull(e.getValue().updated, e.getKey() + " must have an updated date");
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
                assertEquals(md5, e.getValue().md5, "MD5 mismatch for " + e.getKey() + " " + p);
                assertEquals(sha1, e.getValue().sha1, "SHA1 mismatch for " + e.getKey() + " " + p);
            }
        });
    }
    
    @Test
    public void test_junitJarsExcluded()
            throws IOException, NoSuchAlgorithmException, ParserConfigurationException, SAXException {
        Set<String> unitTestJars = new HashSet<>();
        String buildDirectory = System.getProperty("user.dir");
        if (!buildDirectory.contains("yukon-build")) {
            String yukonDirectory = buildDirectory.substring(0, buildDirectory.indexOf("yukon-client"));
            buildDirectory = yukonDirectory + "yukon-build";
        }
        File resourceFile = new File(buildDirectory + File.separatorChar + "build.xml");
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser saxParser = factory.newSAXParser();
        DefaultHandler handler = new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes)
                    throws SAXException {
                if (qName.equals("exclude") && attributes.getValue("name").endsWith(".jar")) {
                    unitTestJars.add(attributes.getValue("name"));
                }
            }
        };
        saxParser.parse(resourceFile, handler);
        ClassPathResource libraryYaml = new ClassPathResource("thirdPartyLibraries.yaml");

        ThirdPartyLibraries documentedLibraries = YamlParserUtils.parseToObject(libraryYaml.getInputStream(),
                ThirdPartyLibraries.class);
        Map<String, ThirdPartyJavaLibrary> documentedLibrariesByFilename = Maps.uniqueIndex(documentedLibraries.javaLibraries,
                l -> l.filename);

        Set<String> unitTestjarsFromYaml = documentedLibrariesByFilename.entrySet().stream()
                .filter(e -> e.getValue().group == LibraryGroup.UNIT_TESTS).map(e -> e.getKey()).collect(Collectors.toSet());
        Set<String> missingJars = Sets.difference(unitTestjarsFromYaml, unitTestJars);
        assertTrue(missingJars.isEmpty(),
                "New Unit Tests related JAR found. These must be excluded in build.xml file of yukon-build " + missingJars);
    }
}
