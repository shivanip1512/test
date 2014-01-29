package com.cannontech.development.service.impl;

import com.cannontech.common.pao.PaoType;

public enum DRReport {
    
    DR_REPORT_6200_V1_0_4(PaoType.LCR6200_RFN, "0.0.2", "DRReport_6200_v1_0_4.drr", "CPS", "1077", false),
    DR_REPORT_6200_V1_1_1(PaoType.LCR6200_RFN, "0.0.3", "DRReport_6200_v1_1_1.drr", "CPS", "1077", false),
    // has verification messages
    DR_REPORT_6200_V1_1_1M(PaoType.LCR6200_RFN, "0.0.3", "DRReport_6200_v1_1_1(ex2).drr", "CPS", "1077", true),
    DR_REPORT_6600_V1_0_3(PaoType.LCR6600_RFN, "0.0.2", "DRReport_6600_v1_0_3.drr", "CPS", "1082", false),
    DR_REPORT_6600_V1_1_1(PaoType.LCR6600_RFN, "0.0.3", "DRReport_6600_v1_1_1.drr", "CPS", "1082", false),
    // has verification messages
    DR_REPORT_6600_V1_1_1M(PaoType.LCR6600_RFN, "0.0.3", "DRReport_6600_v1_1_1(ex2).drr", "CPS", "1082", true);

    DRReport(PaoType type, String schema, String report, String manufacturer, String model, boolean hasVerificationMessages) {
        this.schema = schema;
        this.report = report;
        this.manufacturer = manufacturer;
        this.model = model;
        this.hasVerificationMessages = hasVerificationMessages;
        this.type = type;
    }
    
    private String schema;
    private String report;
    private String manufacturer;
    private String model;
    private boolean hasVerificationMessages;
    private PaoType type;
    private static final String classpath = "classpath:com/cannontech/dr/rfn/service/";
    
    public String getDescription(){
        String description =
            "[" + type + "(" + manufacturer + "," + model + ")]  schema:" + schema + "  report:"
                    + report;
        if ("0.0.3".equals(schema) && hasVerificationMessages) {
            description += "  (Broadcast Verification Messages)";
        }
        return description;
    }
    
    public String getClasspath(){
        return classpath + report;
    }
    
    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }
}
