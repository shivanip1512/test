package com.cannontech.web.support.development.database.objects;

import com.cannontech.common.pao.PaoType;

public enum DevMeter {
    MCT_410_IL_CART_1(PaoType.MCT410IL, "MCT_410il_cart_1", 1016655, DevCCU.CCU_711_CART_1),
    MCT_410_IL_CART_2(PaoType.MCT410IL, "MCT_410il_cart_2", 1068063, DevCCU.CCU_711_CART_1),
    MCT_410_IL_CART_3(PaoType.MCT410IL, "MCT_410il_cart_3", 1016651, DevCCU.CCU_711_CART_1),
    MCT_430_S4_CART_1(PaoType.MCT430S4, "MCT_430s4_cart", 632198, DevCCU.CCU_711_CART_1),
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
