package com.cannontech.common;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.collect.Sets;

public class YukonColorPaletteTest {
    
    private static Set<String> yukonColorPaletteEnumValues;
    private static Set<String> colorsDefinedInFile = new HashSet<>();
    
    @BeforeClass
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
        assertTrue("Found colors in YukonColorPalette.java that are not defined in colors.less file. These colors and their values must be defined in colors.less file.: " + colorsDifference, colorsDifference.isEmpty());
    }
    
    @Test
    public void testColorsFromFile() {
        Set<String> colorsDifference = Sets.difference(colorsDefinedInFile, yukonColorPaletteEnumValues);
        assertTrue("Found colors in colors.less file that are not defined in YukonColorPalette. Define an enum value in YukonColorPalette for these colors.: " + colorsDifference, colorsDifference.isEmpty());
    }
 
    @Test
    public void testMissingColor() throws InvalidPropertiesFormatException, IOException {
        String userDirectory = System.getProperty("user.dir");

        try {
            InputStream inputStream = new FileInputStream(userDirectory + "/i18n/en_US/com/cannontech/yukon/common/general.xml");
            Properties generalProperties = new Properties();
            generalProperties.loadFromXML(inputStream);
            for (YukonColorPalette  attr : YukonColorPalette.values()) {
                var colorEntry = generalProperties.get(attr.getFormatKey());
                assertNotNull("No key for " + attr + " in general.xml file", colorEntry);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
