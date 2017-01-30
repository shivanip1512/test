package com.cannontech.amr.meter.search.model;

public enum MspSearchField {

    SERVICE_LOCATION("Service Location", "GetMeterByServLoc", "GetMetersByServiceLocationIDs"),
    EA_LOCATION("EA Location", "GetMetersByEALocation", "GetMetersByNetworkModelRef"),
    FACILITY_ID("Facility ID", "GetMetersByFacilityId", "GetMetersByContactInfo"),
    ACCOUNT_NUMBER("Account Number", "GetMeterByAccountNumber", "GetMetersByAccountIDs"),
    CUSTOMER_ID("Customer ID", "GetMeterByCustId", "GetMetersByCustomerIDs"),
    CIS_SEARCH("CIS Search", "GetMetersBySearchString", "GetMetersBySearchString"),
	;
	
	private final String name;
	private final String requiredMspMethodName;
    private final String requiredMspMethodNameV5;
	
	MspSearchField(String name, String requiredMspMethodName, String requiredMspMethodNameV5) {
		this.name = name;
		this.requiredMspMethodName = requiredMspMethodName;
        this.requiredMspMethodNameV5 = requiredMspMethodNameV5;
	}
	
	public String getName() {
		return name;
	}
	public String getRequiredMspMethodName() {
		return requiredMspMethodName;
	}

    public String getRequiredMspMethodNameV5() {
        return requiredMspMethodNameV5;
    }
}
