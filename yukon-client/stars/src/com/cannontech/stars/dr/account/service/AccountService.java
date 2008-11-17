package com.cannontech.stars.dr.account.service;

import com.cannontech.common.bulk.field.impl.AccountDto;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.UserNameUnavailableException;

public interface AccountService {

    /**
     * Mehtod to add an account. Throws AccountNumberUnavailableException if the account
     * number is alread used for that energy company.
     * @param accountDto
     * @param liteEnergyCompany
     * @throws AccountNumberUnavailableException
     */
    public void addAccount(AccountDto accountDto, LiteStarsEnergyCompany liteEnergyCompany, String accountNumber) throws AccountNumberUnavailableException, UserNameUnavailableException;
    
    /**
     * Method to update an account
     * @param accountDto
     */
    public void updateAccount(AccountDto accountDto, LiteStarsEnergyCompany liteEnergyCompany, String accountNumber) throws NotFoundException;

    /**
     * Method to delete an account
     * @param accountNumber
     * @param liteEnergyCompany
     */
    public void deleteAccount(String accountNumber, LiteStarsEnergyCompany liteEnergyCompany);

    /**
     * Method that will update an account if it exists, update otherwise.
     * @param accountDto
     * @param liteEnergyCompany
     */
    public void updateOrAddAccount(AccountDto accountDto, LiteStarsEnergyCompany liteEnergyCompany, String accountNumber);

    /**
     * Method to return an account dto for an account number and energy company
     * @param accountNumber
     * @param energyCompanyId
     * @return
     */
    public AccountDto getAccountDto(String accountNumber, int energyCompanyId);
}
