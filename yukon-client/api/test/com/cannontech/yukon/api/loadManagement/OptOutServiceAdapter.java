package com.cannontech.yukon.api.loadManagement;

import java.util.Date;
import java.util.List;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.YukonUserContext;

public class OptOutServiceAdapter implements OptOutService {

	@Override
	public void allowAdditionalOptOuts(String accountNumber,
			String serialNumber, int additionalOptOuts, LiteYukonUser user)
			throws ObjectInOtherEnergyCompanyException {
		throw new UnsupportedOperationException("not implemented");
		
	}

	@Override
	public void cancelAllOptOuts(YukonUserContext userContext)
			throws CommandCompletionException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void cancelOptOut(List<Integer> eventIdList,
			YukonUserContext userContext) throws CommandCompletionException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void changeOptOutCountStateForToday(LiteYukonUser user,
			boolean optOutCounts) {
		throw new UnsupportedOperationException("not implemented");	
	}

	@Override
	public void changeOptOutEnabledStateForToday(LiteYukonUser user,
			boolean optOutsEnabled) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<OptOutLimit> getAllOptOutLimits(LiteYukonGroup group) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public OptOutCountHolder getCurrentOptOutCount(int inventoryId,
			int customerAccountId) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getOptOutDeviceCountForAccount(String accountNumber,
			Date startTime, Date stopTime, LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getOptOutDeviceCountForProgram(String programName,
			Date startTime, Date stopTime, LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<OverrideHistory> getOptOutHistoryByProgram(String programName,
			Date startTime, Date stopTime, LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<OverrideHistory> getOptOutHistoryForAccount(
			String accountNumber, Date startTime, Date stopTime,
			LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void optOut(CustomerAccount customerAccount, OptOutRequest request,
			YukonUserContext userContext) throws CommandCompletionException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void resendOptOut(int inventoryId, int customerAccountId,
			LiteYukonUser user) throws CommandCompletionException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void resetOptOutLimitForInventory(Integer inventoryId, int accountId) {
		throw new UnsupportedOperationException("not implemented");
	}

}
