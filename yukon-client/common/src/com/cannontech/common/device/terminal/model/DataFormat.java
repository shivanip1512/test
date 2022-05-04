package com.cannontech.common.device.terminal.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;

public enum DataFormat implements DisplayableEnum {

    ALPHANUMERIC_DISPLAY('A'),
    BEEP_ONLY('B'),
    NUMERIC_DISPLAY('N');

    private char dbChar;
    private final String formatKey = "yukon.web.modules.operator.pagingTerminal.dataFormat.";
    private final static ImmutableMap<Character, DataFormat> dataFormatMap;

    static {
        ImmutableMap.Builder<Character, DataFormat> mapBuilder = ImmutableMap.builder();
        for (DataFormat dataFormat : DataFormat.values()) {
            mapBuilder.put(dataFormat.getDbChar(), dataFormat);
        }
        dataFormatMap = mapBuilder.build();
    }

    public char getDbChar() {
        return dbChar;
    }

    private DataFormat(char dbChar) {
        this.dbChar = dbChar;
    }

    @Override
    public String getFormatKey() {
        return formatKey + name();
    }

    public static DataFormat getDataFormatForDbChar(Character dbCharacter) {
        return dataFormatMap.get(dbCharacter);
    }
}