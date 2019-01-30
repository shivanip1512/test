package com.cannontech.yukon.api.account;

import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;

public class AccountServiceAdapter implements AccountService {

	@Override
	public int addAccount(UpdatableAccount updatableAccount,
			LiteYukonUser operator) throws AccountNumberUnavailableException,
			UserNameUnavailableException {
		throw new UnsupportedOperationException("Not Implemented");
	}
	
	@Override
    public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator, YukonEnergyCompany energyCompany)
            throws AccountNumberUnavailableException, UserNameUnavailableException {
	    throw new UnsupportedOperationException("Not Implemented");
    }

	@Override
	public void deleteAccount(String accountNumber, LiteYukonUser user) {
		throw new UnsupportedOperationException("Not Implemented");
	}

    @Override
    public void deleteAccount(int accountId, LiteYukonUser user) {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void deleteAccount(String accountNumber, LiteYukonUser user, YukonEnergyCompany energyCompany) {
        throw new UnsupportedOperationException("Not Implemented");
    }
    
	@Override
	public AccountDto getAccountDto(String accountNumber,
			LiteYukonUser yukonUser) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public AccountDto getAccountDto(String accountNumber,
			YukonEnergyCompany ec) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public void updateAccount(UpdatableAccount updatableAccount,
			LiteYukonUser user) throws InvalidAccountNumberException {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public AccountDto getAccountDto(int accountId, int energyCompanyId, YukonUserContext userContext) {
		throw new UnsupportedOperationException("Not Implemented");
	}

    @Override
    public void updateAccount(UpdatableAccount updatableAccount, int accountId, LiteYukonUser user)
            throws InvalidAccountNumberException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user, YukonEnergyCompany energyCompany)
            throws InvalidAccountNumberException {
        throw new UnsupportedOperationException("Not Implemented");
    }

    @Override
    public AccountDto getAccountDto(int accountId, int energyCompanyId) {
        throw new UnsupportedOperationException("Not Implemented");
    }
    
}
