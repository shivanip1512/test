package com.cannontech.stars.core.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.customer.Customer;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsSearchDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.core.service.StarsSearchService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;
import com.cannontech.stars.database.db.customer.CustomerAccount;
import com.cannontech.stars.database.db.report.WorkOrderBase;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.Lists;

public class StarsSearchServiceImpl implements StarsSearchService {
    @Autowired private StarsWorkOrderBaseDao starsWorkOrderBaseDao;
    @Autowired private EnergyCompanySettingDao energyCompanySettingDao;
    @Autowired private StarsSearchDao starsSearchDao;
    @Autowired private StarsCustAccountInformationDao starsCustAccountInformationDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private ContactDao contactDao;
    
    @Override
    public List<LiteWorkOrderBase> searchWorkOrderByOrderNo(LiteStarsEnergyCompany energyCompany,
            String orderNo, boolean searchMembers) {
        List<LiteWorkOrderBase> orderList = new ArrayList<>();
        
        int[] orderIDs = WorkOrderBase.searchByOrderNumber(orderNo, energyCompany.getEnergyCompanyId());
        if (orderIDs == null) {
            return null;
        }

        List<Integer> workOrderIds = Arrays.asList(ArrayUtils.toObject(orderIDs));
        List<LiteWorkOrderBase> workOrderList = starsWorkOrderBaseDao.getByIds(workOrderIds);
        
        for (final LiteWorkOrderBase workOrderBase : workOrderList) {
            orderList.add(workOrderBase);
        }
        
        if (searchMembers) {
            for (LiteStarsEnergyCompany childEnergyCompany : energyCompany.getChildren()) {
                List<LiteWorkOrderBase> memberList = searchWorkOrderByOrderNo(childEnergyCompany, orderNo, searchMembers);
                orderList.addAll(memberList);
            }
        }

        return orderList;
    }

    @Override
    @Deprecated
    public LiteAccountInfo searchAccountByAccountNo(YukonEnergyCompany energyCompany, String accountNo) {
        List<Integer> accountIds = searchAccountByAccountNumber(energyCompany, accountNo, false, true);
        int accountNumberLength = 
                energyCompanySettingDao.getInteger(EnergyCompanySettingType.ACCOUNT_NUMBER_LENGTH, energyCompany.getEnergyCompanyId());
        int rotationDigitLength = 
                energyCompanySettingDao.getInteger(EnergyCompanySettingType.ROTATION_DIGIT_LENGTH, energyCompany.getEnergyCompanyId());
        int accountNumSansRotationDigitsIndex = accountNo.length();
        boolean adjustForRotationDigits = false;
        boolean adjustForAccountLength = false;
        if (rotationDigitLength > 0 && rotationDigitLength < accountNo.length()) {
            accountNumSansRotationDigitsIndex = accountNo.length() - rotationDigitLength;
            accountNo = accountNo.substring(0, accountNumSansRotationDigitsIndex);
            adjustForRotationDigits = true;
        }
        
        int comparableDigitEndIndex = 0;
        if (accountNumberLength > 0) {
            comparableDigitEndIndex = accountNumberLength;
            if (accountNo.length() >= comparableDigitEndIndex) {
                accountNo = accountNo.substring(0, comparableDigitEndIndex);
            }
            adjustForAccountLength = true;
        }
        
        for (Integer accountId : accountIds) {
                LiteAccountInfo liteAcctInfo = starsCustAccountInformationDao.getById(accountId, energyCompany.getEnergyCompanyId());

                String comparableAcctNum = liteAcctInfo.getCustomerAccount().getAccountNumber();
                if(accountNumSansRotationDigitsIndex > 0 && comparableAcctNum.length() >= accountNumSansRotationDigitsIndex && adjustForRotationDigits) {
                    comparableAcctNum = comparableAcctNum.substring(0, accountNumSansRotationDigitsIndex);
                }
                if(comparableDigitEndIndex > 0 && comparableAcctNum.length() >= comparableDigitEndIndex && adjustForAccountLength) {
                    comparableAcctNum = comparableAcctNum.substring(0, comparableDigitEndIndex);
                }
                if (comparableAcctNum.equalsIgnoreCase(accountNo)) {
                    return liteAcctInfo;
                }
        }
        
        return null;
    }

    @Override
    public List<Integer> searchAccountByAccountNumber(YukonEnergyCompany energyCompany, String accountNumber,
            boolean searchMembers, boolean partialMatch) {

        List<Integer> allEnergyCompanyIDs = new ArrayList<>();
        if(searchMembers) {
            List<EnergyCompany> descendantEcs = 
                    ecDao.getEnergyCompany(energyCompany.getEnergyCompanyId()).getDescendants(true);
            allEnergyCompanyIDs = Lists.transform(descendantEcs, EnergyCompanyDao.TO_ID_FUNCTION);
        } else {
            allEnergyCompanyIDs.add(energyCompany.getEnergyCompanyId());
        }

        List<Integer> accountList = searchByAccountNumber(accountNumber, partialMatch, allEnergyCompanyIDs);

        return accountList;
    }

    @Override
    public List<Integer> searchAccountBySerialNo(LiteStarsEnergyCompany energyCompany, String serialNo, boolean searchMembers) {
        List<LiteInventoryBase> invList = 
            starsSearchDao.searchLMHardwareBySerialNumber(serialNo, Collections.singletonList(energyCompany));
        List<Integer> accountList = new ArrayList<>();
       
        for (int i = 0; i < invList.size(); i++) {
            LiteInventoryBase inv = invList.get(i);
            Integer accountId = inv.getAccountID();
            if (accountId > 0) {
                accountList.add(accountId);
            }
        }
        
        if (searchMembers) {
            for (LiteStarsEnergyCompany childEnergyCompany : energyCompany.getChildren()) {
                List<Integer> memberList = searchAccountBySerialNo(childEnergyCompany, serialNo, searchMembers);
                accountList.addAll(memberList);
            }
        }
        
        return accountList;
    }

    @Override
    public List<Integer> searchAccountByAddress(LiteStarsEnergyCompany energyCompany, String address, boolean searchMembers, boolean partialMatch) {
        List<Integer> allEnergyCompanyIDs = new ArrayList<>();
        if( searchMembers) {
            List<EnergyCompany> descendantEcs = 
                    ecDao.getEnergyCompany(energyCompany.getEnergyCompanyId()).getDescendants(true);
            allEnergyCompanyIDs = Lists.transform(descendantEcs, EnergyCompanyDao.TO_ID_FUNCTION);
        } else {
            allEnergyCompanyIDs.add(energyCompany.getEnergyCompanyId());
        }

        List<Integer> accountList = searchByStreetAddress(address, partialMatch, allEnergyCompanyIDs);

        return accountList;
    }

    @Override
    public List<Integer> searchAccountByOrderNo(LiteStarsEnergyCompany energyCompany, String orderNo, boolean searchMembers) {
        List<LiteWorkOrderBase> orderList = searchWorkOrderByOrderNo(energyCompany, orderNo, false);
        List<Integer> accountList = new ArrayList<>();

        for (final LiteWorkOrderBase liteOrder : orderList) {
            if (liteOrder != null) {
                int accountId = liteOrder.getAccountID();
                if (accountId > 0){
                    accountList.add(accountId);
                }
            }
        }
        
        if (searchMembers) {
            for (LiteStarsEnergyCompany childEnergyCompany : energyCompany.getChildren()) {
                List<Integer> memberList = searchAccountByOrderNo(childEnergyCompany, orderNo, searchMembers);
                accountList.addAll(memberList);
            }
        }

        return accountList;
    }

    @Override
    public List<Integer> searchAccountByPhoneNo(LiteStarsEnergyCompany energyCompany, String phoneNo, boolean searchMembers) {
        List<LiteContact> contacts = YukonSpringHook.getBean(ContactDao.class).findContactsByPhoneNo(phoneNo, true);
        
        int[] contactIDs = new int[ contacts.size()];
        for (int i = 0; i < contacts.size(); i++) {
            contactIDs[i] = contacts.get(i).getContactID();
        }
        
        return searchAccountByContactIDs(energyCompany, contactIDs, searchMembers);
    }

    @Override
    public List<Integer> searchAccountByLastName(LiteStarsEnergyCompany energyCompany, String lastName, 
                                                 boolean searchMembers, boolean partialMatch) {
        List<Integer> allEnergyCompanyIDs = new ArrayList<>();
        if(searchMembers) {
            List<EnergyCompany> descendantEcs = 
                    ecDao.getEnergyCompany(energyCompany.getEnergyCompanyId()).getDescendants(true);
            allEnergyCompanyIDs = Lists.transform(descendantEcs, EnergyCompanyDao.TO_ID_FUNCTION);
        } else {
            allEnergyCompanyIDs.add(energyCompany.getEnergyCompanyId());
        }

        List<Integer> accountList = 
                searchByPrimaryContactLastName(lastName, partialMatch, allEnergyCompanyIDs);

        return accountList;
    }

    private List<Integer> searchByPrimaryContactLastName(String lastName_, boolean partialMatch, 
                                                         List<Integer> energyCompanyIDList) {
        if (lastName_ == null || lastName_.length() == 0 
                || energyCompanyIDList == null || energyCompanyIDList.size() == 0) {
            return null;
        }
        
        Date timerStart = new Date();
        String lastName = lastName_.trim();
        String firstName = null;
        int commaIndex = lastName_.indexOf(",");
        if( commaIndex > 0 ) {
            firstName = lastName_.substring(commaIndex+1).trim();
            lastName = lastName_.substring(0, commaIndex).trim();
        }
        String sql = "SELECT map.energycompanyID, acct.AccountID " + 
                    " FROM ECToAccountMapping map, " + CustomerAccount.TABLE_NAME + " acct, " + 
                    Customer.TABLE_NAME + " cust, " +  Contact.TABLE_NAME + " cont " +
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND acct.CustomerID = cust.CustomerID " + 
                    " AND cust.primarycontactid = cont.contactid " +
                    " AND UPPER(cont.contlastname) like ? " ;
                    if (firstName != null && firstName.length() > 0) {
                        sql += " AND UPPER(CONTFIRSTNAME) LIKE ? ";
                    }

                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size())
                    {
                        sql += " AND (map.EnergyCompanyID = " + energyCompanyIDList.get(0).toString(); 
                        for (int i = 1; i < energyCompanyIDList.size(); i++) {
                            sql += " OR map.EnergyCompanyID = " + energyCompanyIDList.get(i).toString();
                        }
                        sql += " ) ";
                    }
                    sql += " order by contlastname, contfirstname";                    
        
        List<Integer> accountList = new ArrayList<>();
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString( 1, lastName.toUpperCase()+(partialMatch? "%":""));
            if (firstName != null && firstName.length() > 0) {
                pstmt.setString(2, firstName.toUpperCase() + "%");
            }
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
  
            while(rset.next()) {
                Integer accountID = new Integer(rset.getInt(2));

                accountList.add(accountID);

                count++;
            }
        }
        catch( Exception e ){
            CTILogger.error( "Error retrieving contacts with last name " + lastName_+ ": " + e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for '" + lastName_ + "' Search (" + count + " AccountIDS loaded; EC=" + (energyCompanyIDList.size() == StarsDatabaseCache.getInstance().getAllEnergyCompanies().size()? "ALL" : energyCompanyIDList.toString()) + ")" );
        return accountList;
    }

    private static List<Integer> searchByStreetAddress(String address, boolean partialMatch, 
                                                       List<Integer> energyCompanyIDList) {
        if (address == null || address.length() == 0 
                ||energyCompanyIDList == null || energyCompanyIDList.size() == 0) {
            return null;
        }
        
        Date timerStart = new Date();
        String addressTrimmed = address.trim();
        
        String sql = "SELECT map.energycompanyID, acct.AccountID " + 
                    " FROM ECToAccountMapping map, " + CustomerAccount.TABLE_NAME + " acct, " + 
                    " AccountSite acs, Address adr " +  
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND acct.AccountSiteID = acs.AccountSiteID " +
                    " AND acs.streetaddressID = adr.addressID " +
                    " AND UPPER(adr.LocationAddress1) LIKE ? ";
                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size())
                    {
                        sql += " AND (map.EnergyCompanyID = " + energyCompanyIDList.get(0).toString(); 
                        for (int i = 1; i < energyCompanyIDList.size(); i++) {
                            sql += " OR map.EnergyCompanyID = " + energyCompanyIDList.get(i).toString();
                        }
                        sql += " ) ";
                    }
                    sql += " order by adr.LocationAddress1, adr.LocationAddress2, CityName, StateCode";                          
        
        List<Integer> accountIdList = new ArrayList<>();
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString( 1, addressTrimmed.toUpperCase()+(partialMatch? "%":""));
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
  
            while(rset.next()) {
                Integer accountID = new Integer(rset.getInt(2));

                accountIdList.add(accountID);

                count++;
            }
        }
        catch( Exception e ){
            CTILogger.error( "Error retrieving contacts with last name " + address + ": " + e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for '" + address 
                        + "' Search (" + count + " AccountIDS loaded; EC=" + 
                (energyCompanyIDList.size() == StarsDatabaseCache.getInstance().getAllEnergyCompanies().size()? "ALL" : energyCompanyIDList.toString()) + ")" );
        return accountIdList;
    }

    private static List<Integer> searchByAccountNumber(String accountNumber_, 
                   boolean partialMatch, List<Integer> energyCompanyIDList) {
        if (accountNumber_ == null || accountNumber_.length() == 0 
                ||energyCompanyIDList == null || energyCompanyIDList.size() == 0) {
            return null;
        }
        
        Date timerStart = new Date();
        String accountNumber = accountNumber_.trim();
        
        String sql = "SELECT map.energycompanyID, acct.AccountID " + //1-2" +
                    " FROM ECToAccountMapping map, " + CustomerAccount.TABLE_NAME + " acct " + 
                    " WHERE map.AccountID = acct.AccountID " +
                    " AND UPPER(acct.AccountNumber) LIKE ? ";
                    // Hey, if we have all the ECIDs available, don't bother adding this criteria!
                    if( energyCompanyIDList.size() != StarsDatabaseCache.getInstance().getAllEnergyCompanies().size())
                    {
                        sql += " AND (map.EnergyCompanyID = " + energyCompanyIDList.get(0).toString(); 
                        for (int i = 1; i < energyCompanyIDList.size(); i++) {
                            sql += " OR map.EnergyCompanyID = " + energyCompanyIDList.get(i).toString();
                        }
                        sql += " ) ";
                    }
                    sql += " order by accountNumber";                          
        
        List<Integer> accountList = new ArrayList<>();
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        int count = 0; 
        try {
            conn = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString( 1, accountNumber.toUpperCase()+(partialMatch? "%":""));
            rset = pstmt.executeQuery();

            CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " After execute" );
  
            Integer accountId = null;
            while(rset.next())
            {
                accountId = new Integer(rset.getInt(2));

                accountList.add(accountId);

                count++;
            }
        }
        catch( Exception e ){
            CTILogger.error( "Error retrieving contacts with last name " + accountNumber_ + ": " + e.getMessage(), e );
        }
        finally {
            try {
                if (rset != null) {
                    rset.close();
                }
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            }
            catch (java.sql.SQLException e) {}
        }

        CTILogger.debug((new Date().getTime() - timerStart.getTime())*.001 + " Secs for '" + accountNumber_  + "' Search (" + count + " AccountIDS loaded; EC=" + (energyCompanyIDList.size() == StarsDatabaseCache.getInstance().getAllEnergyCompanies().size()? "ALL" : energyCompanyIDList.toString()) + ")" );
        return accountList;
    }
    
    private List<Integer> searchAccountByContactIDs(LiteStarsEnergyCompany energyCompany, int[] contactIDs, 
            boolean searchMembers) {
        List<Integer> accountList = new ArrayList<>();
        
        int[] accountIDs = CustomerAccount.searchByPrimaryContactIDs(contactIDs, energyCompany.getEnergyCompanyId());
        if (accountIDs != null) {
            for (final int accountId : accountIDs) {
                accountList.add(accountId);
            }
        }
        
        if (searchMembers) {
            for (LiteStarsEnergyCompany childEnergyCompany : energyCompany.getChildren()) {
                List<Integer> memberList = searchAccountByContactIDs(childEnergyCompany, contactIDs, searchMembers);
                accountList.addAll(memberList);
            }
        }
        
        return accountList;
    }
}
