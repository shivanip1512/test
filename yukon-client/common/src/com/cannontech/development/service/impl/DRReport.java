package com.cannontech.development.service.impl;

import com.cannontech.common.pao.PaoType;

public enum DRReport {
    
    DR_REPORT_6200_V1_0_4(PaoType.LCR6200_RFN,			"0.0.2", "DRReport_6200_v1_0_4.drr",         "CPS", "1077", false, false),
    DR_REPORT_6200_V1_1_1(PaoType.LCR6200_RFN,			"0.0.3", "DRReport_6200_v1_1_1.drr",         "CPS", "1077", false, false),
    // has verification messages
    DR_REPORT_6200_V1_1_1M(PaoType.LCR6200_RFN,			"0.0.3", "DRReport_6200_v1_1_1(ex2).drr",    "CPS", "1077", true,  false),
    // has verification messages and expresscom header
    DR_REPORT_6200_V1_1_1MXCOM(PaoType.LCR6200_RFN,		"0.0.3", "DRReport_6200_v1_1_1(ex3).drr",    "CPS", "1077", true,  true),
    DR_REPORT_6600_V1_0_3(PaoType.LCR6600_RFN,			"0.0.2", "DRReport_6600_v1_0_3.drr",         "CPS", "1082", false, false),
    DR_REPORT_6600_V1_1_1(PaoType.LCR6600_RFN,			"0.0.3", "DRReport_6600_v1_1_1.drr",         "CPS", "1082", false, false),
    // has verification messages
    DR_REPORT_6600_V1_1_1M(PaoType.LCR6600_RFN,			"0.0.3", "DRReport_6600_v1_1_1(ex2).drr",    "CPS", "1082", true,  false),
    // has verification messages and expresscom header
    DR_REPORT_6600_V1_1_1MXCOM(PaoType.LCR6600_RFN,		"0.0.3", "DRReport_6600_v1_1_1(ex3).drr",    "CPS", "1082", true,  true);

	DRReport(PaoType type, String schema, String report, String manufacturer,
			String model, boolean hasVerificationMessages,
			boolean hasExpresscomHeader) {
        this.schema = schema;
        this.report = report;
        this.manufacturer = manufacturer;
        this.model = model;
        this.hasVerificationMessages = hasVerificationMessages;
        this.type = type;
        this.hasExpresscomHeader = hasExpresscomHeader;
    }
    
    private final String schema;
    private final String report;
    private final String manufacturer;
    private final String model;
    private final boolean hasVerificationMessages;
    private final PaoType type;
    private final boolean hasExpresscomHeader;
    private static final String classpath = "classpath:com/cannontech/dr/rfn/service/";
    
    public String getDescription(){
    	String xcomHeader = "";
    	String msgs = "";
        if ("0.0.3".equals(schema) && hasVerificationMessages) {
        	msgs = " [Broadcast Verification Messages]";
        }
        if(hasExpresscomHeader){
        	xcomHeader = " XCOM Header ";
        }
        String description = "[" + type + "(" + manufacturer + "," + model + ")]  [schema:" + schema +xcomHeader+ "]  [report:"
                        + report+"]"+msgs;
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
