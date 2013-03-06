package com.cannontech.web.scheduledFileExport;

import java.util.Date;

public interface ScheduledBillingJobData extends Comparable<ScheduledBillingJobData>{
	public int getId();
	public String getName();
	public String getCronString();
	public Date getNextRun();
}
