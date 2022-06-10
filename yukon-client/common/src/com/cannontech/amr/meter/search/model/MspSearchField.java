package com.cannontech.amr.meter.search.model;

public enum MspSearchField {

    SERVICE_LOCATION("Service Location", "GetMeterByServLoc", "GetMeterByServiceLocationID", "GetMetersByServiceLocationIDs"),
    EA_LOCATION("EA Location", "GetMetersByEALocation", "GetMetersByEALocation", "GetMetersByNetworkModelRef"),
    FACILITY_ID("Facility ID", "GetMetersByFacilityId", "GetMetersByFacilityID", "GetMetersByContactInfo"),
    ACCOUNT_NUMBER("Account Number", "GetMeterByAccountNumber", "GetMetersByAccountNumberAndServiceType", "GetMetersByAccountIDs"),
    CUSTOMER_ID("Customer ID", "GetMeterByCustId", "GetMeterByCustomerID", "GetMetersByCustomerIDs"),
    CIS_SEARCH("CIS Search", "GetMetersBySearchString", "GetMetersBySearchString", "GetMetersBySearchString"),
	;
	
	private final String name;
	private final String requiredMspMethodName;
	private final String requiredMspMethodNameV4;
	private final String requiredMspMethodNameV5;
	
	MspSearchField(String name, String requiredMspMethodName, String requiredMspMethodNameV4, String requiredMspMethodNameV5) {
		this.name = name;
		this.requiredMspMethodName = requiredMspMethodName;
		this.requiredMspMethodNameV4 = requiredMspMethodNameV4;
                this.requiredMspMethodNameV5 = requiredMspMethodNameV5;
	}
	
	public String getName() {
		return name;
	}
	public String getRequiredMspMethodName() {
		return requiredMspMethodName;
	}
	
        public String getRequiredMspMethodNameV4() {
            return requiredMspMethodNameV4;
        }

        public String getRequiredMspMethodNameV5() {
            return requiredMspMethodNameV5;
    }
}
