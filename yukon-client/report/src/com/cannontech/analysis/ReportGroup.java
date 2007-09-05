package com.cannontech.analysis;

public enum ReportGroup {
	ADMINISTRATIVE("Administrative"),
	DATABASE("Database"),
	METERING("Metering"),
	LOAD_MANAGEMENT("Load Management"),
	CAP_CONTROL("Cap Control"),
	STATISTICAL("Statistical"),
	STARS("STARS"),
	CCURT("C&I"),
	SETTLEMENT("Settlement"),
	OTHER("Other");
	
	private ReportGroup (String title) {
		this.title = title;
	}
	
	private String title;
	
	public String getTitle() {
		return title;
	}
}
