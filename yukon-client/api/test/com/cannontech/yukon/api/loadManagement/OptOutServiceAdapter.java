package com.cannontech.yukon.api.loadManagement;

import java.util.Date;
import java.util.List;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;

public class OptOutServiceAdapter implements OptOutService {

	@Override
	public void allowAdditionalOptOuts(String accountNumber,
			String serialNumber, int additionalOptOuts, LiteYukonUser user)
			throws InventoryNotFoundException, AccountNotFoundException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void cancelAllOptOuts(LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public void cancelAllOptOutsByProgramId(int programId, LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void cancelOptOut(List<Integer> eventIdList,
			LiteYukonUser user) throws CommandCompletionException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void changeOptOutCountStateForToday(LiteYukonUser user,
			boolean optOutCounts) {
		throw new UnsupportedOperationException("not implemented");	
	}
	
	@Override
	public void changeOptOutCountStateForTodayByProgramId(LiteYukonUser user,
			boolean optOutCounts, int webpublishingProgramId) {
		throw new UnsupportedOperationException("not implemented");	
	}

	@Override
	public void changeOptOutEnabledStateForToday(LiteYukonUser user,
			boolean optOutsEnabled) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<Integer> getAvailableOptOutPeriods(LiteYukonUser user) {
	    throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public OptOutCountHolder getCurrentOptOutCount(int inventoryId,
			int customerAccountId) {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getOptOutDeviceCountForAccount(String accountNumber,
			Date startTime, Date stopTime, LiteYukonUser user,
			String programName) throws AccountNotFoundException,
			ProgramNotFoundException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public int getOptOutDeviceCountForProgram(String programName,
			Date startTime, Date stopTime, LiteYukonUser user)
			throws NotFoundException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<OverrideHistory> getOptOutHistoryByProgram(String programName,
			Date startTime, Date stopTime, LiteYukonUser user)
			throws NotFoundException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<OverrideHistory> getOptOutHistoryForAccount(
			String accountNumber, Date startTime, Date stopTime,
			LiteYukonUser user, String programName) throws NotFoundException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void optOut(CustomerAccount customerAccount, OptOutRequest request,
			LiteYukonUser user) throws CommandCompletionException {
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

	@Override
	public void cleanUpCancelledOptOut(LiteStarsLMHardware inventory,
			LiteStarsEnergyCompany energyCompany, OptOutEvent event,
			CustomerAccount customerAccount, LiteYukonUser user)
			throws CommandCompletionException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public OptOutLimit getCurrentOptOutLimit(int customerAccountId) {
		throw new UnsupportedOperationException("not implemented");
	}

}
