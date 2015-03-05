package com.cannontech.amr.meter.search.model;

public enum MspSearchField {

	SERVICE_LOCATION("Service Location", "GetMeterByServLoc"),
	EA_LOCATION("EA Location", "GetMetersByEALocation"),
	FACILITY_ID("Facility ID", "GetMetersByFacilityId"),
	ACCOUNT_NUMBER("Account Number", "GetMeterByAccountNumber"),
	CUSTOMER_ID("Customer ID", "GetMeterByCustId"),
	CIS_SEARCH("CIS Search", "GetMetersBySearchString"),
	;
	
	private final String name;
	private final String requiredMspMethodName;
	
	MspSearchField(String name, String requiredMspMethodName) {
		this.name = name;
		this.requiredMspMethodName = requiredMspMethodName;
	}
	
	public String getName() {
		return name;
	}
	public String getRequiredMspMethodName() {
		return requiredMspMethodName;
	}
}
