package com.cannontech.stars.dr.account.service;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.YukonUserContext;

public interface AccountService {

    /**
     * Method to add an account. Throws AccountNumberUnavailableException if the
     * account number is already used for that energy company. The energy
     * company is for the user is retrieved, then addAccount(UpdatableAccount
     * updatableAccount, LiteStarsEnergyCompany ec) is called.
     * @throws AccountNumberUnavailableException
     * @throws UserNameUnavailableException
     */
    public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator)
            throws AccountNumberUnavailableException, UserNameUnavailableException;

    public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator, YukonEnergyCompany energyCompany)
            throws AccountNumberUnavailableException, UserNameUnavailableException;

    /**
     * Method to update an account. The energy company for the user is
     * retrieved, then updateAccount(UpdatableAccount updatableAccount, int
     * accountId, LiteYukonUser user) is called. This means that this method
     * cannot be used to update accounts on member energy companies since the
     * account to update is looked up using the account number in the user's
     * energy company. Account numbers can be reused between different energy
     * companies.
     * @throws NotFoundException
     */
    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user)
            throws InvalidAccountNumberException;

    /**
     * Method to update an account. The energy company for the user is
     * retrieved, then updateAccount(UpdatableAccount updatableAccount, int
     * accountId, LiteYukonUser user) is called. This means that this method
     * cannot be used to update accounts on member energy companies since the
     * account to update is looked up using the account number in the user's
     * energy company. Account numbers can be reused between different energy
     * companies. Use this method when you already have Energy Company
     * information Or in case of Large Loops This reduces the number of Database
     * hits considerably
     * @throws InvalidAccountNumberException
     */

    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user, YukonEnergyCompany energyCompany)
            throws InvalidAccountNumberException;

    /**
     * Method to update an account. Use this method when possible. This method
     * can update accounts on member energy companies since the account is
     * lookup by the accountid, not the account number on the user's energy
     * company.
     * @param user user that initiated the action, only used for logging
     * @throws InvalidAccountNumberException
     */
    public void updateAccount(UpdatableAccount updatableAccount, int accountId, LiteYukonUser user)
            throws InvalidAccountNumberException;

    /**
     * Method to delete an account The energy company is for the user is
     * retrieved, then deleteAccount(String accountNumber,
     * LiteStarsEnergyCompany ec) is called.
     */
    public void deleteAccount(String accountNumber, LiteYukonUser user);

    public void deleteAccount(int accountId, LiteYukonUser user);

    /**
     * Method to delete an account The energy company is for the user is
     * retrieved, then deleteAccount(String accountNumber,
     * LiteStarsEnergyCompany ec) is called. Use this method when you already
     * have the EnergyCompany Information.Or in Large Loops This reduces the
     * number of Database hits considerably
     */

    public void deleteAccount(String accountNumber, LiteYukonUser user, YukonEnergyCompany energyCompany);

    /**
     * Method to return an account dto for an account number and energy company.
     * The energy company is for the user is retrieved, then
     * getAccountDto(String accountNumber, LiteStarsEnergyCompany ec) is called.
     */
    public AccountDto getAccountDto(String accountNumber, LiteYukonUser yukonUser);

    public AccountDto getAccountDto(String accountNumber, YukonEnergyCompany ec);

    public AccountDto getAccountDto(int accountId, int energyCompanyId, YukonUserContext userContext);

    AccountDto getAccountDto(int accountId, int energyCompanyId);
}
