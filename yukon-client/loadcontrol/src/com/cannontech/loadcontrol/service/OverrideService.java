package com.cannontech.loadcontrol.service;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.OverrideHistory;

public interface OverrideService {

	public void cancelAllCurrentOverrides(LiteYukonUser user);
	
	public void countOverridesTowardsLimit(LiteYukonUser user);
	
	public List<OverrideHistory> getOverrideHistoryByAccountNumber(String accountNumber, String programName, Date startTime, Date stopTime, LiteYukonUser user);
	
	public List<OverrideHistory> getOverrideHistoryByProgramName(String programName, Date startTime, Date stopTime, LiteYukonUser user);
	
	public void prohibitConsumerOverrides(LiteYukonUser user);
	
	public long getTotalOverridenDevicesByAccountNumber(String accountNumber, String programName, Date startTime, Date stopTime, LiteYukonUser user);
	
	public long getTotalOverridenDevicesByProgramName(String programName, Date startTime, Date stopTime, LiteYukonUser user);
}
