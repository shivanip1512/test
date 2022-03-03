package com.cannontech.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.MatcherAssert.assertThat;

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
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.YamlParserUtils;
import com.cannontech.system.ThirdPartyJavaScriptLibrary;
import com.cannontech.system.ThirdPartyLibraries;
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
        
        ThirdPartyLibraries documentedLibraries = YamlParserUtils.parseToObject(libraryYaml.getInputStream(), ThirdPartyLibraries.class);
        
        Map<String, ThirdPartyJavaScriptLibrary> documentedLibrariesByProject = Maps.uniqueIndex(documentedLibraries.jsLibraries, l -> l.project); 
        
        File baseDir = new File(jsPath);
        
        Map<String, File> thirdPartyProjects = Arrays.stream(baseDir.listFiles())
                .collect(StreamUtils.mapToSelf(File::getName));
        
        Set<String> unknownFiles = Sets.difference(thirdPartyProjects.keySet(), documentedLibrariesByProject.keySet());
        assertTrue(unknownFiles.isEmpty(), "Unknown JavaScript projects found: " + unknownFiles);

        Set<String> missingFiles = Sets.difference(documentedLibrariesByProject.keySet(), thirdPartyProjects.keySet());
        assertTrue(missingFiles.isEmpty(), "JavaScript projects listed in thirdPartyLibraries.yaml, but missing from WebContent/resources/js/lib: " + missingFiles);
        
        MessageDigest md_md5 = MessageDigest.getInstance("MD5");
        MessageDigest md_sha1 = MessageDigest.getInstance("SHA1");

        documentedLibrariesByProject.entrySet().stream().forEach(e -> {
            assertFalse(StringUtils.isEmpty(e.getValue().project), e.getKey() + " must have a project name");
            assertFalse(StringUtils.isEmpty(e.getValue().version), e.getKey() + " must have a project version");
            assertFalse(StringUtils.isEmpty(e.getValue().projectUrl), e.getKey() + " must have a project URL");
            assertFalse(StringUtils.isEmpty(e.getValue().npmUrl), e.getKey() + " must have an NPM URL");
            assertThat(e.getKey() + " must have a valid NPM URL", e.getValue().npmUrl, anyOf(startsWith("https://www.npmjs.com/package/"), equalTo("n/a")));
            assertFalse(CollectionUtils.isEmpty(e.getValue().licenses), e.getKey() + " must have a license type");
            assertFalse(CollectionUtils.isEmpty(e.getValue().licenseUrls), e.getKey() + " must have a license URL");
            assertFalse(StringUtils.isEmpty(e.getValue().jira), e.getKey() + " must have a JIRA entry");
            assertNotNull(e.getValue().updated, e.getKey() + " must have an updated date");
            
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

            assertEquals(e.getValue().md5, md5, "MD5 mismatch for " + e.getKey());
            assertEquals(e.getValue().sha1, sha1, "SHA1 mismatch for " + e.getKey());
        });
    }
}
