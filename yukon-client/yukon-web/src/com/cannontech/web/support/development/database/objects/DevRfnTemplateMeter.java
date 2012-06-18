package com.cannontech.web.support.development.database.objects;

import com.cannontech.common.pao.PaoType;

public enum DevRfnTemplateMeter {

    RFN_TEMPLATE_EE_A3R(PaoType.RFN430A3, "*RfnTemplate_EE_A3R"),
    RFN_TEMPLATE_EE_A3D(PaoType.RFN430A3, "*RfnTemplate_EE_A3D"),
    RFN_TEMPLATE_EE_A3T(PaoType.RFN430A3, "*RfnTemplate_EE_A3T"),
    RFN_TEMPLATE_EKA_WATER_SENSOR(PaoType.RFWMETER, "*RfnTemplate_Eka_water_sensor"),
    RFN_TEMPLATE_EKA_WATER_NODE(PaoType.RFWMETER, "*RfnTemplate_Eka_water_node"),
    RFN_TEMPLATE_LGYR_FOCUS_KWH(PaoType.RFN420FL, "*RfnTemplate_LGYR_FocuskWh"),
    RFN_TEMPLATE_LGYR_FOCUS_AXD(PaoType.RFN420FX, "*RfnTemplate_LGYR_FocusAXD"),
    RFN_TEMPLATE_LGYR_FOCUS_AXR_SD(PaoType.RFN420FD, "*RfnTemplate_LGYR_FocusAXR-SD"),
    RFN_TEMPLATE_ITRN_C2SX(PaoType.RFN420CL, "*RfnTemplate_ITRN_C2SX"),
    RFN_TEMPLATE_ITRN_C2SX_SD(PaoType.RFN420CD, "*RfnTemplate_ITRN_C2SX-SD"),
    RFN_TEMPLATE_CPS_LCR_6200(PaoType.LCR6200_RFN, "*RfnTemplate_CPS_RFN-LCR6200"),
    RFN_TEMPLATE_CPS_LCR_6600(PaoType.LCR6600_RFN, "*RfnTemplate_CPS_RFN-LCR6600"),
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
