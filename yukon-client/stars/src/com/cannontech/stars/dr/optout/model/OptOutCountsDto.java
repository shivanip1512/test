package com.cannontech.stars.dr.optout.model;

import java.util.Date;

public class OptOutCountsDto {

	private OptOutCounts optOutCounts;
	private Integer programId;
	private Date startDate;
	
	public OptOutCountsDto(OptOutCounts optOutCounts, Integer programId, Date startDate) {
		
		this.programId = programId;
		this.optOutCounts = optOutCounts;
		this.startDate = startDate;
	}
	
	public OptOutCounts getOptOutCounts() {
		return optOutCounts;
	}
	public Integer getProgramId() {
		return programId;
	}
	public Date getStartDate() {
		return startDate;
	}
	
}
