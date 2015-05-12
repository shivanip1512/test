package com.cannontech.stars.core.service;

import java.util.List;

import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface StarsSearchService {

    List<LiteWorkOrderBase> searchWorkOrderByOrderNo(LiteStarsEnergyCompany energyCompany, String orderNo,
        boolean searchMembers);

    /**
     * Search customer account by account # within the energy company.
     * int totalComparableDigits is for use with rotation digit billing systems: the field
     * represents the total length of the account number to be considered in the search (i.e. 
     * the account number sans the rotation digits, most commonly the last two digits).  If
     * normal length, then 0 or lower will result in the whole account number being used.  This would
     * be default.  
     * 
     * Use LiteStarsEnergyCompany.searchAccountByAccountNumber or CustomerAccountDao.getByAccountNumber
     */
    @Deprecated
    LiteAccountInfo searchAccountByAccountNo(YukonEnergyCompany energyCompany, String accountNo);
    
    /**
     * Search customer accounts by account #, search results based on partial match.
     * 
     * Returns a list of account ids
     */
    List<Integer> searchAccountByAccountNumber(YukonEnergyCompany energyCompany, String accountNumber, 
        boolean searchMembers, boolean partialMatch);
    
    /**
     * Search customer accounts by hardware serial #.
     * 
     * Returns a list of account ids
     */
    List<Integer> searchAccountBySerialNo(LiteStarsEnergyCompany energyCompany, String serialNo, boolean searchMembers);

    /**
     * Search customer accounts by service address. The search is based on partial match, and is case-insensitive.
     * Returns a list of account ids
     */
    List<Integer> searchAccountByAddress(LiteStarsEnergyCompany energyCompany, String address, boolean searchMembers,
        boolean partialMatch);

    
    List<Integer> searchAccountByOrderNo(LiteStarsEnergyCompany energyCompany, String orderNo, boolean searchMembers);

    /**
     * Search customer accounts by phone number.
     * Returns a list of account ids
     */
    List<Integer> searchAccountByPhoneNo(LiteStarsEnergyCompany energyCompany, String phoneNo, boolean searchMembers);

    /**
     * Search customer accounts by last name. The search is based on partial match, and is case-insensitive.
     * Returns a list of account ids
     */
    List<Integer> searchAccountByLastName(LiteStarsEnergyCompany energyCompany, String lastName, boolean searchMembers, 
        boolean partialMatch);
}
