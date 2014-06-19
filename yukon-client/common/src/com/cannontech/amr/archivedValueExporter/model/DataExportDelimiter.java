package com.cannontech.amr.archivedValueExporter.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public enum DataExportDelimiter implements DisplayableEnum {
    
    COMMA(","),
    SEMI_COLON(";"),
    COLON(":"),
    SPACE(" "),
    NONE(""),
    CUSTOM(null);
    
    private final String value;
    private static final ImmutableSet<DataExportDelimiter> nonCustom = ImmutableSet.of(COMMA, SEMI_COLON, COLON, SPACE, NONE);
    private static final ImmutableMap<String, DataExportDelimiter> valueMap;
    static {
        Builder<String, DataExportDelimiter> b = ImmutableMap.builder();
        for (DataExportDelimiter delimiter : nonCustom) {
            b.put(delimiter.getValue(), delimiter);
        }
        valueMap = b.build();
    }
    
    private DataExportDelimiter(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * Returns the enum entry for the given value or CUSTOM if there is no match
     */
    public static DataExportDelimiter getForValue(String value) {
        
        DataExportDelimiter type = valueMap.get(value);
        if (type == null) {
            return DataExportDelimiter.CUSTOM;
        }
        
        return type;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.tools.bulk.archivedValueExporter.delimiter." + name();
    }
}