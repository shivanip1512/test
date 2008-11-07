package com.cannontech.stars.dr.account.service;

import com.cannontech.common.bulk.field.impl.AccountDto;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.stars.dr.account.service.result.AccountActionResult;

public interface AccountService {

    /**
     * Method to create an account
     * @param accountDto
     * @return AccountActionResult
     */
    public AccountActionResult addAccount(AccountDto accountDto);
    
    /**
     * Method to update an account
     * @param accountDto
     * @return AccountActionResult
     */
    public AccountActionResult updateAccount(AccountDto accountDto);

    /**
     * Method to delete an account
     * @param accountNumer
     * @param liteEnergyCompany
     * @return AccountActionResult
     */
    public AccountActionResult deleteAccount(String accountNumber, LiteEnergyCompany liteEnergyCompany);
}
