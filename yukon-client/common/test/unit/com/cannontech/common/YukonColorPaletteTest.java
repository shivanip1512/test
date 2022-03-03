package com.cannontech.common;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.collect.Sets;

public class YukonColorPaletteTest {
    
    private static Set<String> yukonColorPaletteEnumValues;
    private static Set<String> colorsDefinedInFile = new HashSet<>();
    
    @BeforeAll
    public static void init() throws IOException {
        yukonColorPaletteEnumValues = Stream.of(YukonColorPalette.values()).map(YukonColorPalette::name).collect(Collectors.toSet());
        
        Resource resource = new ClassPathResource("com/cannontech/colors.less");
        String colors = IOUtils.toString(resource.getInputStream(), "UTF-8");
        Pattern variableNamePattern = Pattern.compile("@(.*?):", Pattern.DOTALL);
        Matcher variableNameMatcher = variableNamePattern.matcher(colors);
        
        while(variableNameMatcher.find()) {
            colorsDefinedInFile.add(variableNameMatcher.group(1).toUpperCase());
        }
    }
    
    @Test
    public void testYukonColorPaletteColors() {
        Set<String> colorsDifference = Sets.difference(yukonColorPaletteEnumValues, colorsDefinedInFile);
        assertTrue(colorsDifference.isEmpty(), "Found colors in YukonColorPalette.java that are not defined in colors.less file. These colors and their values must be defined in colors.less file.: " + colorsDifference);
    }
    
    @Test
    public void testColorsFromFile() {
        Set<String> colorsDifference = Sets.difference(colorsDefinedInFile, yukonColorPaletteEnumValues);
        assertTrue(colorsDifference.isEmpty(), "Found colors in colors.less file that are not defined in YukonColorPalette. Define an enum value in YukonColorPalette for these colors.: " + colorsDifference);
    }
 
    @Test
    public void testMissingColor() throws InvalidPropertiesFormatException, IOException {
        String userDirectory = System.getProperty("user.dir");

        try {
            if (!userDirectory.contains("common")) {
                String clientDir = userDirectory.substring(0, userDirectory.lastIndexOf("\\") + 1);
                userDirectory = clientDir + "common";
            }
            InputStream inputStream = new FileInputStream(userDirectory + "/i18n/en_US/com/cannontech/yukon/common/general.xml");
            Properties generalProperties = new Properties();
            generalProperties.loadFromXML(inputStream);
            for (YukonColorPalette  attr : YukonColorPalette.values()) {
                var colorEntry = generalProperties.get(attr.getFormatKey());
                assertNotNull(colorEntry, "No key for " + attr + " in general.xml file");
            }
        } catch (FileNotFoundException e) {
            fail("Caught exception in testMissingColor: " + e.getMessage());
        }
    }
}
