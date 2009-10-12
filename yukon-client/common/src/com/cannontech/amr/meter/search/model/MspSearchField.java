package com.cannontech.amr.meter.search.model;

public enum MspSearchField {

	SERVICE_LOCATION("Service Location", "GetMeterByServLoc"),
	EA_LOCATION("EA Location", "GetMetersByEALocation"),
	FACILITY_ID("Facility ID", "GetMetersByFacilityId"),
	ACCOUNT_NUMBER("Account Number", "GetMeterByAccountNumber"),
	CUSTOMER_ID("Customer ID", "GetMeterByCustId"),
	;
	
	private String name;
	private String requiredMspMethodName;
	
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
