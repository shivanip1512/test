package com.cannontech.stars.dr.account.service;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.user.YukonUserContext;

public interface AccountService {

    /**
     * Method to add an account. Throws AccountNumberUnavailableException if the account
     * number is already used for that energy company.
     * The energy company is for the user is retrieved, then addAccount(UpdatableAccount updatableAccount, LiteStarsEnergyCompany ec) is called.
     * @param updatableAccount
     * @param operator
     * @throws AccountNumberUnavailableException
     * @throws UserNameUnavailableException
     * @return accountId
     */
    public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator) throws AccountNumberUnavailableException, UserNameUnavailableException;
    
    /**
     * Method to update an account
     * The energy company is for the user is retrieved, then updateAccount(UpdatableAccount updatableAccount, LiteStarsEnergyCompany ec) is called.
     * @param updatableAccount
     * @param user
     * @throws NotFoundException
     */
    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user) throws InvalidAccountNumberException;
    
    /**
     * Method to delete an account
     * The energy company is for the user is retrieved, then deleteAccount(String accountNumber, LiteStarsEnergyCompany ec) is called.
     * @param accountNumber
     * @param user
     */
    public void deleteAccount(String accountNumber, LiteYukonUser user);
    
    public void deleteAccount(int accountId, LiteYukonUser user);
    
    /**
     * Method to return an account dto for an account number and energy company.
     * The energy company is for the user is retrieved, then getAccountDto(String accountNumber, LiteStarsEnergyCompany ec) is called. 
     * @param accountNumber
     * @param yukonUser
     * @return
     */
    public AccountDto getAccountDto(String accountNumber, LiteYukonUser yukonUser);

    public AccountDto getAccountDto(String accountNumber, LiteStarsEnergyCompany ec);
    
    public AccountDto getAccountDto(int accountId, int energyCompanyId, YukonUserContext userContext);
}
