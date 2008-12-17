package com.cannontech.yukon.api.loadManagement;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.OverrideService;
import com.cannontech.loadcontrol.service.data.OverrideHistory;

public class OverrideServiceAdapter implements OverrideService {

	@Override
	public void cancelAllCurrentOverrides(LiteYukonUser user) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public void countOverridesTowardsLimit(LiteYukonUser user) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public List<OverrideHistory> getOverrideHistoryByAccountNumber(
			String accountNumber, String programName, Date startTime, Date stopTime, LiteYukonUser user) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public List<OverrideHistory> getOverrideHistoryByProgramName(
			String programName, Date startTime, Date stopTime, LiteYukonUser user) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public void prohibitConsumerOverrides(LiteYukonUser user) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public long getTotalOverridenDevicesByAccountNumber(String accountNumber,
			String programName, Date startDate, Date stopDate,
			LiteYukonUser user) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public long getTotalOverridenDevicesByProgramName(String programName,
			Date startDate, Date stopDate, LiteYukonUser user) {
		throw new UnsupportedOperationException("Not Implemented");
	}

}
