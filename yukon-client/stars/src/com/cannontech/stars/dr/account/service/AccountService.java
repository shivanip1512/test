package com.cannontech.stars.dr.account.service;

import com.cannontech.common.bulk.field.impl.AccountDto;
import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.UserNameUnavailableException;

public interface AccountService {

    /**
     * Mehtod to add an account. Throws AccountNumberUnavailableException if the account
     * number is alread used for that energy company.
     * @param updatableAccount
     * @param operator
     * @throws AccountNumberUnavailableException
     * @throws UserNameUnavailableException
     */
    public void addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator) throws AccountNumberUnavailableException, UserNameUnavailableException;
    
    /**
     * Method to update an account
     * @param updatableAccount
     * @param user
     * @throws NotFoundException
     */
    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user) throws NotFoundException;

    /**
     * Method to delete an account
     * @param accountNumber
     * @param user
     */
    public void deleteAccount(String accountNumber, LiteYukonUser user);

    /**
     * Method to return an account dto for an account number and energy company
     * @param accountNumber
     * @param energyCompanyId
     * @return
     */
    public AccountDto getAccountDto(String accountNumber, int energyCompanyId);
}
