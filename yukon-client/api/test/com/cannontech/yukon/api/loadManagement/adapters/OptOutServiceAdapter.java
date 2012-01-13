package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;

import com.cannontech.common.device.commands.impl.CommandCompletionException;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsLMHardware;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.optout.exception.OptOutException;
import com.cannontech.stars.dr.optout.model.OptOutCountHolder;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutLimit;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.service.OptOutRequest;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;

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
	public void cancelAllOptOutsByProgramName(String programName, LiteYukonUser user) {
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
	public void changeOptOutCountStateForTodayByProgramName(LiteYukonUser user,
			boolean optOutCounts, String programName) {
		throw new UnsupportedOperationException("not implemented");	
	}

	@Override
	public void changeOptOutEnabledStateForToday(LiteYukonUser user,
			boolean optOutsEnabled) {
		throw new UnsupportedOperationException("not implemented");
	}
	
    @Override
    public void changeOptOutEnabledStateForTodayByProgramName(LiteYukonUser user, boolean optOutsEnabled,
                                                              String programName) throws ProgramNotFoundException {
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
	public List<OverrideHistory> getOptOutHistoryByProgram(String programName, Date startTime, Date stopTime, LiteYukonUser user)
			throws NotFoundException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public List<OverrideHistory> getOptOutHistoryForAccount(String accountNumber, Date startTime, Date stopTime, LiteYukonUser user, String programName) 
	        throws NotFoundException {
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
	public void resetOptOutLimitForInventory(Integer inventoryId, int accountId, LiteYukonUser user) {
		throw new UnsupportedOperationException("not implemented");
	}
	
	@Override
	public void resetOptOutLimitForInventory(String accountNumber,
			String serialNumber, LiteYukonUser user)
			throws InventoryNotFoundException, AccountNotFoundException,
			IllegalArgumentException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public void cleanUpCancelledOptOut(LiteStarsLMHardware inventory, YukonEnergyCompany yukonEnergyCompany,
	                                   OptOutEvent event, CustomerAccount customerAccount, LiteYukonUser user)
	throws CommandCompletionException {
		throw new UnsupportedOperationException("not implemented");
	}

	@Override
	public OptOutLimit getCurrentOptOutLimit(int customerAccountId) {
		throw new UnsupportedOperationException("not implemented");
	}

    @Override
    public void allowAdditionalOptOuts(int accountId, int serialNumber,
                                       int additionalOptOuts, LiteYukonUser user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String checkOptOutStartDate(int accountId, LocalDate startDate,
                                       YukonUserContext userContext,
                                       boolean isOperator) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void optOut(CustomerAccount customerAccount, OptOutRequest request, LiteYukonUser user,
                       OptOutCounts optOutCounts) throws CommandCompletionException, OptOutException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void optOutWithValidation(CustomerAccount customerAccount, OptOutRequest request,
                                          LiteYukonUser user, OptOutCounts optOutCounts)
            throws CommandCompletionException, OptOutException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<OptOutLimit> findCurrentOptOutLimit(LiteYukonGroup residentialGroup) {
        throw new UnsupportedOperationException("not implemented");
    }
}