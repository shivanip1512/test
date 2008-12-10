package com.cannontech.loadcontrol.service;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.OverrideHistory;

public interface OverrideService {

	public void cancelAllCurrentOverrides(LiteYukonUser user);
	
	public void countOverridesTowardsLimit(LiteYukonUser user);
	
	public List<OverrideHistory> overrideHistoryByAccountNumber(String accountNumber, String programName, Date startTime, Date stopTime, LiteYukonUser user);
	
	public List<OverrideHistory> overrideHistoryByProgramName(String programName, Date startTime, Date stopTime, LiteYukonUser user);
	
	public void prohibitConsumerOverrides(LiteYukonUser user);
	
	public long totalOverridenDevicesByAccountNumber(String accountNumber, String programName, Date startTime, Date stopTime, LiteYukonUser user);
	
	public long totalOverridenDevicesByProgramName(String programName, Date startTime, Date stopTime, LiteYukonUser user);
}
