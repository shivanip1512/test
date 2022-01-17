package com.cannontech.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import com.cannontech.common.util.YamlParserUtils;
import com.cannontech.system.ThirdPartyIconLibrary;
import com.cannontech.system.ThirdPartyLibraries;
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
        
        ThirdPartyLibraries documentedLibraries = YamlParserUtils.parseToObject(libraryYaml.getInputStream(),
                ThirdPartyLibraries.class);
        
        Map<Path, ThirdPartyIconLibrary> documentedLibrariesByProject = 
                Maps.uniqueIndex(documentedLibraries.iconLibraries, 
                                 l -> Paths.get(iconBasePath, l.path)); 
        
        Set<Path> allIcons = Files.list(Paths.get(iconBasePath))
                .flatMap(ThirdPartyIconLibraryTest::recurse)
                .collect(Collectors.toSet());

        Set<Path> thirdPartyIcons = Sets.filter(allIcons, p -> !ignoredIcons.contains(p));
        
        Set<Path> unknownFiles = Sets.difference(thirdPartyIcons, documentedLibrariesByProject.keySet());
        assertTrue(unknownFiles.isEmpty(), "Unknown icons found: " + unknownFiles);

        Set<Path> missingFiles = Sets.difference(documentedLibrariesByProject.keySet(), thirdPartyIcons);
        assertTrue(missingFiles.isEmpty(), "Icons listed in thirdPartyLibraries.yaml, but missing from WebContent/WebConfig/yukon/Icons: " + missingFiles);
        
        MessageDigest md_md5 = MessageDigest.getInstance("MD5");
        MessageDigest md_sha1 = MessageDigest.getInstance("SHA1");

        documentedLibrariesByProject.entrySet().stream().forEach(e -> {
            assertFalse(StringUtils.isEmpty(e.getValue().project), e.getKey() + " must have a project name");
            assertFalse(StringUtils.isEmpty(e.getValue().version), e.getKey() + " must have a project version");
            assertFalse(StringUtils.isEmpty(e.getValue().projectUrl), e.getKey() + " must have a project URL");
            assertFalse(CollectionUtils.isEmpty(e.getValue().licenses), e.getKey() + " must have a license type");
            assertFalse(CollectionUtils.isEmpty(e.getValue().licenseUrls), e.getKey() + " must have a license URL");
            assertFalse(StringUtils.isEmpty(e.getValue().jira), e.getKey() + " must have a JIRA entry");
            assertNotNull(e.getValue().updated, e.getKey() + " must have an updated date");
            
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

            assertEquals(e.getValue().md5, md5, "MD5 mismatch for " + e.getKey());
            assertEquals(e.getValue().sha1, sha1, "SHA1 mismatch for " + e.getKey());
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
                    "marker-generic.svg", 
                    "marker-lcr-grey.svg", 
                    "marker-meter-elec-grey.svg", 
                    "marker-meter-gas-grey.svg", 
                    "marker-meter-plc-elec-grey.svg", 
                    "marker-meter-water-grey.svg", 
                    "marker-meter-wifi-grey.svg", 
                    "marker-plc-lcr-grey.svg", 
                    "marker-relay-grey.svg", 
                    "marker-relay-cell-grey.svg",
                    "marker-thermostat-grey.svg", 
                    "marker-transmitter-grey.svg", 
                    "pencil.png",
                    "plus-minus.png", 
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
