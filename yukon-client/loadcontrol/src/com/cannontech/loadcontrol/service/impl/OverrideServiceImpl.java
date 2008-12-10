package com.cannontech.loadcontrol.service.impl;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.OverrideService;
import com.cannontech.loadcontrol.service.data.OverrideHistory;

public class OverrideServiceImpl implements OverrideService {

	@Override
	public void cancelAllCurrentOverrides(LiteYukonUser user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void countOverridesTowardsLimit(LiteYukonUser user) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<OverrideHistory> overrideHistoryByAccountNumber(
			String accountNumber, String programName, Date startTime, Date stopTime, LiteYukonUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<OverrideHistory> overrideHistoryByProgramName(
			String programName, Date startTime, Date stopTime, LiteYukonUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prohibitConsumerOverrides(LiteYukonUser user) {
		// TODO Auto-generated method stub

	}

	@Override
	public long totalOverridenDevicesByAccountNumber(String accountNumber,
			String programName, Date startDate, Date startTime, LiteYukonUser user) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long totalOverridenDevicesByProgramName(String programName, Date startTime, Date stopTime, 
			LiteYukonUser user) {
		// TODO Auto-generated method stub
		return 0;
	}

}
