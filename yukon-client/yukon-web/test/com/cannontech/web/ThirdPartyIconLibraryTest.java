package com.cannontech.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.system.ThirdPartyIconLibrary;
import com.cannontech.system.ThirdPartyLibraries;
import com.cannontech.system.ThirdPartyLibraryParser;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ThirdPartyIconLibraryTest {

    private static String iconBasePath = "../yukon-web/WebContent/WebConfig/yukon/Icons";
    private static Set<Path> ignoredIcons = buildIgnoredIconSet();
    
    private static Stream<Path> recurse(Path p) {
        if (Files.isDirectory(p)) {
            try {
                return Files.list(p)
                        .flatMap(ThirdPartyIconLibraryTest::recurse);
            } catch (IOException e) {
                throw new RuntimeException("Could not list files in " + p, e);
            }
        }
        return Stream.of(p);
    }
    
    @Test
    public void test_thirdPartyLicenses() throws IOException, NoSuchAlgorithmException {

        ClassPathResource libraryYaml = new ClassPathResource("thirdPartyLibraries.yaml");
        
        ThirdPartyLibraries documentedLibraries = ThirdPartyLibraryParser.parse(libraryYaml.getInputStream());
        
        Map<Path, ThirdPartyIconLibrary> documentedLibrariesByProject = 
                Maps.uniqueIndex(documentedLibraries.iconLibraries, 
                                 l -> Paths.get(iconBasePath, l.path)); 
        
        Set<Path> allIcons = Files.list(Paths.get(iconBasePath))
                .flatMap(ThirdPartyIconLibraryTest::recurse)
                .collect(Collectors.toSet());

        Set<Path> thirdPartyIcons = Sets.filter(allIcons, p -> !ignoredIcons.contains(p));
        
        Set<Path> unknownFiles = Sets.difference(thirdPartyIcons, documentedLibrariesByProject.keySet());
        assertTrue("Unknown icons found: " + unknownFiles, unknownFiles.isEmpty());

        Set<Path> missingFiles = Sets.difference(documentedLibrariesByProject.keySet(), thirdPartyIcons);
        assertTrue("Icons listed in thirdPartyLibraries.yaml, but missing from WebContent/WebConfig/yukon/Icons: " + missingFiles, missingFiles.isEmpty());
        
        MessageDigest md_md5 = MessageDigest.getInstance("MD5");
        MessageDigest md_sha1 = MessageDigest.getInstance("SHA1");

        documentedLibrariesByProject.entrySet().stream().forEach(e -> {
            assertFalse(e.getKey() + " must have a project name", StringUtils.isEmpty(e.getValue().project));
            assertFalse(e.getKey() + " must have a project version", StringUtils.isEmpty(e.getValue().version));
            assertFalse(e.getKey() + " must have a project URL", StringUtils.isEmpty(e.getValue().projectUrl));
            assertFalse(e.getKey() + " must have a license type", CollectionUtils.isEmpty(e.getValue().licenses));
            assertFalse(e.getKey() + " must have a license URL", CollectionUtils.isEmpty(e.getValue().licenseUrls));
            assertFalse(e.getKey() + " must have a JIRA entry", StringUtils.isEmpty(e.getValue().jira));
            assertNotNull(e.getKey() + " must have an updated date", e.getValue().updated);
            
            md_md5.reset();
            md_sha1.reset();
            
            byte[] contents;
            try {
                contents = Files.readAllBytes(e.getKey());
            } catch (IOException e1) {
                throw new RuntimeException("Could not read " + e.getKey(), e1);
            }
            String md5 = Hex.encodeHexString(md_md5.digest(contents));
            String sha1 = Hex.encodeHexString(md_sha1.digest(contents));

            assertEquals("MD5 mismatch for " + e.getKey(), e.getValue().md5, md5);
            assertEquals("SHA1 mismatch for " + e.getKey(), e.getValue().sha1, sha1);
        });
    }
    
    private static Set<Path> buildIgnoredIconSet() {
        return Lists.newArrayList(
        			"$$$Sm.gif", 
                    "$$Sm.gif",
                    "$Sm.gif",
                    "AC.png", 
                    "DualFuel.png", 
                    "Electric.png", 
                    "Generation.png", 
                    "GrainDryer.png", 
                    "HalfSm.gif", 
                    "HeatPump.png", 
                    "HotTub.png", 
                    "Irrigation.png", 
                    "Load.png", 
                    "Pool.png", 
                    "QuarterSm.gif", 
                    "Setback.png", 
                    "SixthSm.gif", 
                    "StartCalendar.png", 
                    "StorageHeat.png", 
                    "ThirdSm.gif", 
                    "Tree1Sm.gif", 
                    "Tree2Sm.gif", 
                    "Tree3Sm.gif", 
                    "VerticalRule.gif", 
                    "WaterHeater.png", 
                    "accept.png", 
                    "arrow_down_green_anim.gif", 
                    "arrow_down_orange_anim.gif", 
                    "arrow_up_green_anim.gif", 
                    "arrow_up_orange_anim.gif", 
                    "bullet_arrow_down.png", 
                    "busy_rotation.gif", 
                    "dashboardImages.png", 
                    "error.gif", 
                    "green_local.png", 
                    "icon_blockcollapsed.png", 
                    "icon_blockexpanded.png", 
                    "icons-32-disabled.png", 
                    "icons-32.png", 
                    "information.gif", 
                    "marker-generic.png", 
                    "marker-lcr-grey.png", 
                    "marker-meter-elec-grey.png", 
                    "marker-meter-gas-grey.png", 
                    "marker-meter-plc-elec-grey.png", 
                    "marker-meter-water-grey.png", 
                    "marker-plc-lcr-grey.png", 
                    "marker-relay-grey.png", 
                    "marker-thermostat-grey.png", 
                    "marker-transmitter-grey.png", 
                    "pencil.png",
                    "plus-minus.png", 
                    "relay.png", 
                    "spinner-white.gif", 
                    "spinner.gif", 
                    "time.gif", 
                    "triangle-down_white.gif", 
                    "triangle-right.gif", 
                    "triangle-right_white.gif", 
                    "warning.gif")
                .stream()
                .map(i -> Paths.get(iconBasePath, i))
                .collect(Collectors.toSet());
    }
}
