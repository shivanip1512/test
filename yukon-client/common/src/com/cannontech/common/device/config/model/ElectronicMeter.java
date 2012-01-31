package com.cannontech.common.device.config.model;

import com.cannontech.web.input.type.InputOptionProvider;

public enum ElectronicMeter implements InputOptionProvider {
    NONE(0, "None"),
    S4(1, "S4"),
    ALPHA_A3(2, "Alpha A3"),
    ALPHA_PPLUS(3, "Alpha P+"),
    GEKV(4, "GEKV"),
    GEKV2(5, "GEKV2"),
    SENTINAL(6, "Sentinel"),
    DNP(7, "DNP"),
    GEKV2C(8, "GEKV2C"),
    ;

    private final String text;
    private final int electronicMeterId;
    private ElectronicMeter(int electronicMeterId, String text) {
        this.electronicMeterId = electronicMeterId;
        this.text = text;
    }

    public int getElectronicMeterId() {
        return electronicMeterId;
    }

    @Override
    public String getValue() {
        return String.valueOf(electronicMeterId);
    }

    @Override
    public String getText() {
        return text;
    }
}
