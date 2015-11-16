package com.cannontech.web.dev.database.objects;

import com.cannontech.common.pao.PaoType;

public enum DevRfnTemplateMeter {

    RFN_TEMPLATE_EE_A3D(PaoType.RFN430A3D, "*RfnTemplate_EE_A3D"),
    RFN_TEMPLATE_EE_A3T(PaoType.RFN430A3T, "*RfnTemplate_EE_A3T"),
    RFN_TEMPLATE_EE_A3K(PaoType.RFN430A3K, "*RfnTemplate_EE_A3K"),
    RFN_TEMPLATE_EE_A3R(PaoType.RFN430A3R, "*RfnTemplate_EE_A3R"),
    RFN_TEMPLATE_EKA_WATER_SENSOR(PaoType.RFWMETER, "*RfnTemplate_Eka_water_sensor"),
    RFN_TEMPLATE_EKA_WATER_NODE(PaoType.RFWMETER, "*RfnTemplate_Eka_water_node"),
    RFN_TEMPLATE_LGYR_FOCUS_KWH(PaoType.RFN420FL, "*RfnTemplate_LGYR_FocuskWh"),
    RFN_TEMPLATE_LGYR_FOCUS_AXD(PaoType.RFN420FX, "*RfnTemplate_LGYR_FocusAXD"),
    RFN_TEMPLATE_LGYR_FOCUS_AXR_SD(PaoType.RFN420FD, "*RfnTemplate_LGYR_FocusAXR-SD"),
    RFN_TEMPLATE_LGYR_FOCUS_RXR(PaoType.RFN420FRX, "*RfnTemplate_LGYR_FocusRXR"),
    RFN_TEMPLATE_LGYR_FOCUS_RXR_SD(PaoType.RFN420FRD, "*RfnTemplate_LGYR_FocusRXR-SD"),
    RFN_TEMPLATE_ITRN_C1SX(PaoType.RFN410CL, "*RfnTemplate_ITRN_C1SX"),
    RFN_TEMPLATE_ITRN_C2SX(PaoType.RFN420CL, "*RfnTemplate_ITRN_C2SX"),
    RFN_TEMPLATE_ITRN_C2SX_SD(PaoType.RFN420CD, "*RfnTemplate_ITRN_C2SX-SD"),
    RFN_TEMPLATE_CPS_1077(PaoType.LCR6200_RFN, "*RfnTemplate_CPS_1077"),
    RFN_TEMPLATE_CPS_1082(PaoType.LCR6600_RFN, "*RfnTemplate_CPS_1082"),
    RFN_TEMPLATE_SCH_SL0(PaoType.RFN430SL0, "*RfnTemplate_SCH_SENTINEL-L0"),
    RFN_TEMPLATE_SCH_SL1(PaoType.RFN430SL1, "*RfnTemplate_SCH_SENTINEL-L1"),
    RFN_TEMPLATE_SCH_SL2(PaoType.RFN430SL2, "*RfnTemplate_SCH_SENTINEL-L2"),
    RFN_TEMPLATE_SCH_SL3(PaoType.RFN430SL3, "*RfnTemplate_SCH_SENTINEL-L3"),
    RFN_TEMPLATE_SCH_SL4(PaoType.RFN430SL4, "*RfnTemplate_SCH_SENTINEL-L4"),
    ;
    
    private final PaoType paoType;
    private final String name;
    
    private DevRfnTemplateMeter(PaoType paoType, String name) {
        this.paoType = paoType;
        this.name = name;
    }

    public PaoType getPaoType() {
        return paoType;
    }

    public String getName() {
        return name;
    }

}
