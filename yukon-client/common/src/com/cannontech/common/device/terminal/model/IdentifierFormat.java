package com.cannontech.common.device.terminal.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;

public enum IdentifierFormat implements DisplayableEnum {
    CAP_PAGE('A'),
    ID_PAGE('B');

    private char dbChar;
    private final String formatKey = "yukon.web.modules.operator.pagingTerminal.identifierFormat.";
    private final static ImmutableMap<Character, IdentifierFormat> identifierFormatMap;

    static {
        ImmutableMap.Builder<Character, IdentifierFormat> mapBuilder = ImmutableMap.builder();
        for (IdentifierFormat identifierFormat : IdentifierFormat.values()) {
            mapBuilder.put(identifierFormat.getDbChar(), identifierFormat);
        }
        identifierFormatMap = mapBuilder.build();
    }

    public char getDbChar() {
        return dbChar;
    }

    private IdentifierFormat(char dbChar) {
        this.dbChar = dbChar;
    }

    public static IdentifierFormat getIdentifierFormatForDbChar(Character dbCharacter) {
        return identifierFormatMap.get(dbCharacter);
    }

    @Override
    public String getFormatKey() {
        return formatKey + name();
    }
}