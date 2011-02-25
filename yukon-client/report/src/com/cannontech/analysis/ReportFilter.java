package com.cannontech.analysis;

public enum ReportFilter {
	NONE("", ReportFilterType.NONE),
	METER("Meter Number", ReportFilterType.METERNUMBER),
	DEVICE("Device", ReportFilterType.DEVICENAME),
	GROUPS("Groups", ReportFilterType.DEVICEGROUP),
	ROUTE("Route", ReportFilterType.PAOBJECTID),
	RECEIVER("Receiver", ReportFilterType.PAOBJECTID),
	LMGROUP("LM Group", ReportFilterType.PAOBJECTID),
	LMCONTROLAREA("LM Control Area", ReportFilterType.PAOBJECTID),
	LMSCENARIO("LM Scenario", ReportFilterType.PAOBJECTID),
	TRANSMITTER("Transmitter", ReportFilterType.PAOBJECTID),
	RTU("RTU", ReportFilterType.PAOBJECTID),
	CAPCONTROLSUBBUS("Substation Bus", ReportFilterType.PAOBJECTID),
	CAPCONTROLSUBSTATION("Substation", ReportFilterType.PAOBJECTID),
	CAPCONTROLFEEDER("Feeder", ReportFilterType.PAOBJECTID),
	CAPBANK("Cap Bank", ReportFilterType.PAOBJECTID),
	SCHEDULE("Schedule (Script)", ReportFilterType.PAOBJECTID),
	AREA("Area", ReportFilterType.PAOBJECTID),
	PORT("Port", ReportFilterType.PAOBJECTID),
	PROGRAM("Program", ReportFilterType.PAOBJECTID),
	PROGRAM_SINGLE_SELECT("Program", ReportFilterType.PAOBJECTID, false),
	STRATEGY("Strategy", ReportFilterType.PAOBJECTID),
	ACCOUNT_NUMBER("Account Number", ReportFilterType.ACCOUNTNUMBER),
	SERIAL_NUMBER("Serial Number", ReportFilterType.SERIALNUMBER),
	USER("User", ReportFilterType.USER),
	;

	private String filterTitle;
	private ReportFilterType reportFilterType;
	private boolean multiSelect = true;

	private ReportFilter(String filterTitle, ReportFilterType reportFilterType) {
		this.filterTitle = filterTitle;
		this.reportFilterType = reportFilterType;
	}

	private ReportFilter(String filterTitle, ReportFilterType reportFilterType, boolean multiSelect) {
		this.filterTitle = filterTitle;
		this.reportFilterType = reportFilterType;
		this.multiSelect = multiSelect;
	}

	public String getFilterTitle() {
		return filterTitle;
	}

	public boolean isMultiSelect() {
		return multiSelect;
	}

	public ReportFilterType getReportFilterType() {
		return reportFilterType;
	}
}