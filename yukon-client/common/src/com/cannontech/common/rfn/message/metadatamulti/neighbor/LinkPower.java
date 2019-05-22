package com.cannontech.common.rfn.message.metadatamulti.neighbor;

import java.util.Arrays;

public enum LinkPower {
    LINK_POWER_125_mW((short) 0), // 125 mWatt
    LINK_POWER_250_mW((short) 1), // 250 mWatt
    LINK_POWER_HALF_WATT((short) 2), // 0.5 Watt
    LINK_POWER_ONE_WATT((short) 3), // 1 Watt
    ;

    private final short linkPowerID;

    private LinkPower(short linkPowerID) {
        this.linkPowerID = linkPowerID;
    }

    public short getLinkPowerID() {
        return linkPowerID;
    }

    /**
     * @return the Enum representation for the given linkPowerID.
     *         null if unknown linkPowerID.
     */
    public static LinkPower fromLinkPowerID(short linkPowerID) {
        return Arrays.stream(LinkPower.values())
            .filter(v -> v.getLinkPowerID() == linkPowerID)
            .findFirst()
            .orElse(null);
    }
}