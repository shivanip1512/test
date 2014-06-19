package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;

public enum ReadingPattern implements DisplayableEnum{
    
    FIVE_ZERO("#####"),
    THREE_THREE("###.###"),
    FOUR_TWO("####.##"),
    CUSTOM("CUSTOM");
    
    private final String pattern;
    
    private ReadingPattern(String pattern) {
        this.pattern = pattern;
    }
    
    public String getPattern() {
        return pattern;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.archivedValueExporter.readingPattern." + name();
    }
    
    public boolean isCustom() {
        return this == CUSTOM;
    }
}