package com.cannontech.common.rfn.message.metadatamulti.neighbor;

import java.util.Arrays;

public enum LinkRate {
    LINK_RATE_HALF_X((short) 0), // 1/2x
    LINK_RATE_ONE_X((short) 1), // 1x
    LINK_RATE_TWO_X((short) 2), // 2x
    LINK_RATE_EIGHTH_X((short) 3), // 1/8x
    LINK_RATE_FOURTH_X((short) 4), // 1/4x
    LINK_RATE_FOUR_X((short) 5), // 4x
    ;

    private final short linkRateID;

    private LinkRate(short linkRateID) {
        this.linkRateID = linkRateID;
    }

    public short getLinkRateID() {
        return linkRateID;
    }

    /**
     * @return the Enum representation for the given LinkRateID.
     *         null if unknown LinkRateID.
     */
    public static LinkRate fromLinkRateID(short linkRateID) {
        return Arrays.stream(LinkRate.values())
            .filter(v -> v.getLinkRateID() == linkRateID)
            .findFirst()
            .orElse(null);
    }
}