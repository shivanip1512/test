package com.cannontech.common;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.google.common.collect.Sets;

public class YukonColorPaletteTest {

    @Test
    public void testYukonColorPaletteColors() throws IOException {
        Set<String> yukonColorPaletteEnumValues = Stream.of(YukonColorPalette.values()).map(YukonColorPalette::name).collect(Collectors.toSet());
        
        Resource resource = new ClassPathResource("com/cannontech/colors.less");
        String colors = IOUtils.toString(resource.getInputStream(), "UTF-8");
        Pattern variableNamePattern = Pattern.compile("@(.*?):", Pattern.DOTALL);
        Matcher variableNameMatcher = variableNamePattern.matcher(colors);
        Set<String> colorsDefinedInFile = new HashSet<>();
        while(variableNameMatcher.find()) {
            colorsDefinedInFile.add(variableNameMatcher.group(1).toUpperCase());
        }
        Set<String> colorsDifference = Sets.difference(yukonColorPaletteEnumValues, colorsDefinedInFile);
        assertTrue("Found colors in YukonColorPalette.java that are not defined in colors.less file. These colors and their values must be defined in colors.less file.: " + colorsDifference, colorsDifference.isEmpty());
    }
}