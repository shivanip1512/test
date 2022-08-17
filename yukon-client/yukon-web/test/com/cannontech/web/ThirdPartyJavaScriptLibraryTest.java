package com.cannontech.web;

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
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.common.stream.StreamUtils;
import com.cannontech.system.ThirdPartyJavaScriptLibrary;
import com.cannontech.system.ThirdPartyLibraries;
import com.cannontech.system.ThirdPartyLibraryParser;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ThirdPartyJavaScriptLibraryTest {

    private static String jsPath = "../yukon-web/WebContent/resources/js/lib";
    
    private static Stream<File> recurse(File f) {
        if (f.isDirectory()) {
            return Arrays.stream(f.listFiles()).flatMap(ThirdPartyJavaScriptLibraryTest::recurse);
        }
        return Stream.of(f);
    }
    
    @Test
    public void test_thirdPartyLicenses() throws IOException, NoSuchAlgorithmException {

        ClassPathResource libraryYaml = new ClassPathResource("thirdPartyLibraries.yaml");
        
        ThirdPartyLibraries documentedLibraries = ThirdPartyLibraryParser.parse(libraryYaml.getInputStream());
        
        Map<String, ThirdPartyJavaScriptLibrary> documentedLibrariesByProject = Maps.uniqueIndex(documentedLibraries.jsLibraries, l -> l.project); 
        
        File baseDir = new File(jsPath);
        
        Map<String, File> thirdPartyProjects = Arrays.stream(baseDir.listFiles())
                .collect(StreamUtils.mapToSelf(File::getName));
        
        Set<String> unknownFiles = Sets.difference(thirdPartyProjects.keySet(), documentedLibrariesByProject.keySet());
        assertTrue("Unknown JavaScript projects found: " + unknownFiles, unknownFiles.isEmpty());

        Set<String> missingFiles = Sets.difference(documentedLibrariesByProject.keySet(), thirdPartyProjects.keySet());
        assertTrue("JavaScript projects listed in thirdPartyLibraries.yaml, but missing from WebContent/resources/js/lib: " + missingFiles, missingFiles.isEmpty());
        
        MessageDigest md_md5 = MessageDigest.getInstance("MD5");
        MessageDigest md_sha1 = MessageDigest.getInstance("SHA1");

        documentedLibrariesByProject.entrySet().stream().forEach(e -> {
            assertFalse(e.getKey() + " must have a project name", StringUtils.isEmpty(e.getValue().project));
            assertFalse(e.getKey() + " must have a project version", StringUtils.isEmpty(e.getValue().version));
            assertFalse(e.getKey() + " must have a project URL", StringUtils.isEmpty(e.getValue().projectUrl));
            assertFalse(e.getKey() + " must have an NPM URL", StringUtils.isEmpty(e.getValue().npmUrl));
            assertThat(e.getKey() + " must have a valid NPM URL", e.getValue().npmUrl, anyOf(startsWith("https://www.npmjs.com/package/"), equalTo("n/a")));
            assertFalse(e.getKey() + " must have a license type", CollectionUtils.isEmpty(e.getValue().licenses));
            assertFalse(e.getKey() + " must have a license URL", CollectionUtils.isEmpty(e.getValue().licenseUrls));
            assertFalse(e.getKey() + " must have a JIRA entry", StringUtils.isEmpty(e.getValue().jira));
            assertNotNull(e.getKey() + " must have an updated date", e.getValue().updated);
            
            File baseFile = thirdPartyProjects.get(e.getKey());
            Stream<File> files = baseFile.isFile()
                    ? Stream.of(baseFile)
                    : recurse(baseFile);

            md_md5.reset();
            md_sha1.reset();
            
            files.forEach(f -> {
                Path p = f.toPath();
                byte[] contents;
                try {
                    contents = Files.readAllBytes(p);
                } catch (IOException e1) {
                    throw new RuntimeException("Could not read " + p, e1);
                }
                md_md5.update(contents);
                md_sha1.update(contents);
            });
            
            String md5 = Hex.encodeHexString(md_md5.digest());
            String sha1 = Hex.encodeHexString(md_sha1.digest());

            assertEquals("MD5 mismatch for " + e.getKey(), e.getValue().md5, md5);
            assertEquals("SHA1 mismatch for " + e.getKey(), e.getValue().sha1, sha1);
        });
    }
}
