package com.cannontech.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.clientutils.YukonLogManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class ColorsFileReader {
    
    public final static ImmutableMap<YukonColorPalette, String> colorHexValueMap;
    
    private static final Logger log = YukonLogManager.getLogger(ColorsFileReader.class);
    
    static {
        Builder<YukonColorPalette, String> colorHexValueMapBuilder = ImmutableMap.builder();
        try {
            Resource resource = new ClassPathResource("com/cannontech/colors.less");
            String colors = IOUtils.toString(resource.getInputStream(), "UTF-8");
            Pattern variableNamePattern = Pattern.compile("@(.*?):", Pattern.DOTALL);
            Matcher variableNameMatcher = variableNamePattern.matcher(colors);
            Pattern valuePattern = Pattern.compile(":(.*?);", Pattern.DOTALL);
            Matcher valueMatcher = valuePattern.matcher(colors);
            
            while (variableNameMatcher.find() && valueMatcher.find()) {
                colorHexValueMapBuilder.put(YukonColorPalette.valueOf(variableNameMatcher.group(1).toUpperCase()), valueMatcher.group(1));
            }
        } catch (Exception e) {
            log.error("An Exception occured while reading colors.less file.", e);
            System.exit(1);
        }
        colorHexValueMap = colorHexValueMapBuilder.build();
    }
}