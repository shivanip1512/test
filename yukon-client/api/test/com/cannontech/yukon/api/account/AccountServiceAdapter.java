package com.cannontech.yukon.api.account;

import com.cannontech.common.bulk.field.impl.AccountDto;
import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.exception.UserNameUnavailableException;
import com.cannontech.stars.dr.account.service.AccountService;

public class AccountServiceAdapter implements AccountService {

	@Override
	public void addAccount(UpdatableAccount updatableAccount,
			LiteYukonUser operator) throws AccountNumberUnavailableException,
			UserNameUnavailableException {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public void addAccount(UpdatableAccount updatableAccount,
			LiteStarsEnergyCompany ec)
			throws AccountNumberUnavailableException,
			UserNameUnavailableException {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public void deleteAccount(String accountNumber, LiteYukonUser user) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public void deleteAccount(String accountNumber, LiteStarsEnergyCompany ec) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public AccountDto getAccountDto(String accountNumber,
			LiteYukonUser yukonUser) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public AccountDto getAccountDto(String accountNumber,
			LiteStarsEnergyCompany ec) {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public void updateAccount(UpdatableAccount updatableAccount,
			LiteYukonUser user) throws InvalidAccountNumberException {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public void updateAccount(UpdatableAccount updatableAccount,
			LiteStarsEnergyCompany ec) throws InvalidAccountNumberException {
		throw new UnsupportedOperationException("Not Implemented");
	}

}
