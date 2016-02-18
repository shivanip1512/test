package com.cannontech.development.model;

import com.cannontech.common.pao.PaoType;

public enum DevMeter {
    MCT_410_IL_CART_1(PaoType.MCT410IL, "* Cart MCT-410iL (1016655)", 1016655, DevCCU.CART_711),
    MCT_410_IL_CART_2(PaoType.MCT410IL, "* Cart MCT-410iL (1068063)", 1068063, DevCCU.CART_711),
    MCT_410_IL_CART_3(PaoType.MCT410IL, "* Cart MCT-410iL (1016651)", 1016651, DevCCU.CART_711),
    MCT_430_S4_CART_1(PaoType.MCT430S4, "* Cart MCT-430S4 (632198)", 632198, DevCCU.CART_711),
    ;
    
    private final PaoType paoType;
    private final String name;
    private final int address;
    private final DevCCU ccu;
    
    private DevMeter(PaoType paoType, String name, int address, DevCCU ccu) {
        this.paoType = paoType;
        this.name = name;
        this.address = address;
        this.ccu = ccu;
    }

    public PaoType getPaoType() {
        return paoType;
    }

    public String getName() {
        return name;
    }

    public int getAddress() {
        return address;
    }

    public DevCCU getCcu() {
        return ccu;
    }

}
