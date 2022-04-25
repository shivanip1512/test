package com.cannontech.common.device.terminal.model;

import com.cannontech.common.i18n.DisplayableEnum;
import com.google.common.collect.ImmutableMap;

public enum Protocol implements DisplayableEnum {
    FLEX('F'),
    GOLAY_SEQUENTIAL_CODE('G'),
    POCSAG_512_BAUD_CCIR_1('P'),
    POCSAG_1200_BAUD('p'),
    POCSAG_2400_BAUD('Q');

    private char dbChar;
    private final static ImmutableMap<Character, Protocol> protocolMap;
    private final String formatKey = "yukon.web.modules.operator.pagingTerminal.protocol.";

    static {
        ImmutableMap.Builder<Character, Protocol> mapBuilder = ImmutableMap.builder();
        for (Protocol protocol : Protocol.values()) {
            mapBuilder.put(protocol.getDbChar(), protocol);
        }
        protocolMap = mapBuilder.build();
    }

    Protocol(char dbChar) {
        this.dbChar = dbChar;
    }

    @Override
    public String getFormatKey() {
        return formatKey + name();
    }

    public char getDbChar() {
        return dbChar;
    }

    public static Protocol getProtocolMapForDbChar(Character dbCharacter) {
        return protocolMap.get(dbCharacter);
    }
}