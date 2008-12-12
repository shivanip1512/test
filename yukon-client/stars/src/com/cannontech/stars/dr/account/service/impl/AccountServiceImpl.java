package com.cannontech.stars.dr.account.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.field.impl.AccountDto;
import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.model.Address;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.yukon.AuthenticationRole;
import com.cannontech.roles.yukon.EnergyCompanyRole;
import com.cannontech.stars.core.dao.CallReportDao;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.exception.UserNameUnavailableException;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ECToAccountMapping;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.event.dao.EventAccountDao;
import com.cannontech.stars.dr.event.dao.LMProgramEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.user.UserUtils;

public class AccountServiceImpl implements AccountService {
    
    private Logger log = YukonLogManager.getLogger(AccountServiceImpl.class);
    
    private YukonUserDao yukonUserDao;
    private RoleDao roleDao;
    private AuthDao authDao;
    private YukonGroupDao yukonGroupDao;
    private AddressDao addressDao;
    private ContactDao contactDao;
    private ContactNotificationDao contactNotificationDao;
    private CustomerDao customerDao;
    private SiteInformationDao siteInformationDao;
    private AccountSiteDao accountSiteDao;
    private CustomerAccountDao customerAccountDao;
    private ECMappingDao ecMappingDao;
    private InventoryDao inventoryDao;
    private LMHardwareBaseDao hardwareBaseDao;
    private LMProgramEventDao lmProgramEventDao;
    private ApplianceDao applianceDao;
    private StarsWorkOrderBaseDao workOrderDao;
    private CallReportDao callReportDao;
    private ThermostatScheduleDao thermostatScheduleDao;
    private EventAccountDao eventAccountDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private DBPersistentDao dbPersistantDao;

    @Override
    @Transactional
    public void addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator) throws AccountNumberUnavailableException, UserNameUnavailableException {
        AccountDto accountDto = updatableAccount.getAccountDto();
        String accountNumber = updatableAccount.getAccountNumber();
        
        if(StringUtils.isBlank(accountNumber)) {
            log.error("Account " + accountNumber + " could not be added: The provided account number cannot be empty.");
            throw new InvalidAccountNumberException("The provided account number cannot be empty.");
        }
        
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompanyByUser(operator);
        LiteStarsEnergyCompany liteStarsEnergyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompany.getEnergyCompanyID());
        LiteStarsCustAccountInformation accountInformation = liteStarsEnergyCompany.searchAccountByAccountNo(accountNumber);
        
        if(accountInformation != null) {
            log.error("Account " + accountNumber + " could not be added: The provided account number already exists.");
            throw new AccountNumberUnavailableException("The provided account number already exists");
        }
        
        if(yukonUserDao.getLiteYukonUser( accountDto.getUserName() ) != null) {
            log.error("Account " + accountNumber + " could not be added: The provided username already exists.");
            throw new UserNameUnavailableException("The provided username already exists.");
        }
        
        Address streetAddress = accountDto.getStreetAddress();
        Address billingAddress = accountDto.getBillingAddress();
        
        /*
         * Create the user login
         */
        LiteYukonUser user = null;
        if(!StringUtils.isBlank(accountDto.getUserName())) {
            user = new LiteYukonUser(); 
            user.setUsername(accountDto.getUserName());
            user.setStatus(UserUtils.STATUS_ENABLED);
            AuthType defaultAuthType = roleDao.getGlobalRolePropertyValue(AuthType.class, AuthenticationRole.DEFAULT_AUTH_TYPE);
            user.setAuthType(defaultAuthType);
            List<LiteYukonGroup> groups = new ArrayList<LiteYukonGroup>();
            LiteYukonGroup defaultYukonGroup = yukonGroupDao.getLiteYukonGroup(YukonGroup.YUKON_GROUP_ID);
            LiteYukonGroup operatorGroup = yukonGroupDao.getLiteYukonGroupByName(accountDto.getLoginGroup());
            groups.add(defaultYukonGroup);
            groups.add(operatorGroup);
            yukonUserDao.addLiteYukonUserWithPassword(user, accountDto.getPassword(), liteStarsEnergyCompany.getEnergyCompanyID(), groups);
            dbPersistantDao.processDBChange(new DBChangeMsg(user.getLiteID(),
                DBChangeMsg.CHANGE_YUKON_USER_DB,
                DBChangeMsg.CAT_YUKON_USER,
                DBChangeMsg.CAT_YUKON_USER,
                DBChangeMsg.CHANGE_TYPE_ADD));
        }
            
        /*
         * Create the address if supplied
         */
        LiteAddress liteAddress = null;
        if(streetAddress != null && StringUtils.isNotBlank(streetAddress.getLocationAddress1())) {
        liteAddress = new LiteAddress();
            setAddressFieldsFromDTO(liteAddress, streetAddress);
            addressDao.add(liteAddress);
        }
            
        /*
         * Create billing address if supplied
         */
        LiteAddress liteBillingAddress = null;
        if(billingAddress != null && StringUtils.isNotBlank(billingAddress.getLocationAddress1())) {
            liteBillingAddress = new LiteAddress();
            setAddressFieldsFromDTO(liteBillingAddress, billingAddress);
            addressDao.add(liteBillingAddress);
        }
            
        /*
         * Create the contact
         */
        LiteContact liteContact = new LiteContact(-1); //  contactDao.saveContact will insert for -1, otherwise update
        liteContact.setContFirstName(accountDto.getFirstName());
        liteContact.setContLastName(accountDto.getLastName());
        liteContact.setLoginID(user == null ? UserUtils.USER_DEFAULT_ID : user.getUserID());
        contactDao.saveContact(liteContact);
        dbPersistantDao.processDBChange(new DBChangeMsg(liteContact.getLiteID(),
                               DBChangeMsg.CHANGE_CONTACT_DB,
                               DBChangeMsg.CAT_CUSTOMERCONTACT,
                               DBChangeMsg.CAT_CUSTOMERCONTACT,
                               DBChangeMsg.CHANGE_TYPE_ADD));
            
        /*
         * Create the notifications
         */
        if(StringUtils.isNotBlank(accountDto.getHomePhone())) {
            LiteContactNotification homePhoneLiteContactNotification = createNotification(liteContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, accountDto.getHomePhone());
            contactNotificationDao.saveNotification(homePhoneLiteContactNotification);
        }
        
        if(StringUtils.isNotBlank(accountDto.getWorkPhone())) {
            LiteContactNotification workPhoneLiteContactNotification = createNotification(liteContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE, accountDto.getWorkPhone());
            contactNotificationDao.saveNotification(workPhoneLiteContactNotification);
        }
        
        if(StringUtils.isNotBlank(accountDto.getEmailAddress())) {
            LiteContactNotification emailLiteContactNotification = createNotification(liteContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL, accountDto.getEmailAddress());
            contactNotificationDao.saveNotification(emailLiteContactNotification);
        }
            
        /*
         * Create the customer
         */
        LiteCustomer liteCustomer = new LiteCustomer();
        liteCustomer.setPrimaryContactID(liteContact.getContactID());
        if(accountDto.getIsCommercial()) {
            liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_CI);
        }else {
            liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_RESIDENTIAL);
        }
        liteCustomer.setTimeZone(authDao.getUserTimeZone(user).getID());
        liteCustomer.setCustomerNumber(CtiUtilities.STRING_NONE);
        liteCustomer.setRateScheduleID(CtiUtilities.NONE_ZERO_ID);
        liteCustomer.setAltTrackingNumber(accountDto.getAltTrackingNumber());
        liteCustomer.setTemperatureUnit(authDao.getRolePropertyValue(liteStarsEnergyCompany.getUserID(), EnergyCompanyRole.DEFAULT_TEMPERATURE_UNIT));
        customerDao.addCustomer(liteCustomer);
        dbPersistantDao.processDBChange(new DBChangeMsg(liteCustomer.getLiteID(),
                               DBChangeMsg.CHANGE_CUSTOMER_DB,
                               DBChangeMsg.CAT_CUSTOMER,
                               DBChangeMsg.CAT_CUSTOMER,
                               DBChangeMsg.CHANGE_TYPE_ADD));
            
        /*
         * Create comercial/industrial customer if company was passed
         */
        if(liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
            LiteCICustomer liteCICustomer = new LiteCICustomer();
            liteCICustomer.setMainAddressID(liteAddress.getAddressID());
            liteCICustomer.setDemandLevel(CtiUtilities.NONE_ZERO_ID);
            liteCICustomer.setCurtailAmount(CtiUtilities.NONE_ZERO_ID);
            liteCICustomer.setCompanyName(accountDto.getCompanyName());
            liteCICustomer.setCICustType(YukonListEntryTypes.CUSTOMER_TYPE_COMMERCIAL);
            customerDao.addCICustomer(liteCICustomer);
            dbPersistantDao.processDBChange(new DBChangeMsg(liteCustomer.getLiteID(),
                                   DBChangeMsg.CHANGE_CUSTOMER_DB,
                                   DBChangeMsg.CAT_CI_CUSTOMER,
                                   DBChangeMsg.CAT_CI_CUSTOMER,
                                   DBChangeMsg.CHANGE_TYPE_ADD));
        }
            
        /*
         * Create service info
         */
        LiteSiteInformation liteSiteInformation = new LiteSiteInformation();
        try {
            int subId = siteInformationDao.getSubstationIdByName(accountDto.getSiteInfo().getSubstationName());
            liteSiteInformation.setSubstationID(subId);
        }catch(NotFoundException e) {
            log.error("Unable to find substation by name: " + accountDto.getSiteInfo().getSubstationName() , e);
        }
        liteSiteInformation.setFeeder(accountDto.getSiteInfo().getFeeder());
        liteSiteInformation.setPole(accountDto.getSiteInfo().getPole());
        liteSiteInformation.setTransformerSize(accountDto.getSiteInfo().getTransformerSize());
        liteSiteInformation.setServiceVoltage(accountDto.getSiteInfo().getServiceVoltage());
        siteInformationDao.add(liteSiteInformation);
        
        AccountSite accountSite = new AccountSite();
        accountSite.setSiteInformationId(liteSiteInformation.getSiteID());
        accountSite.setStreetAddressId(liteAddress.getAddressID());
        accountSite.setCustAtHome("N");
        accountSite.setCustomerStatus("A");
        accountSite.setSiteNumber(accountDto.getMapNumber());
        accountSiteDao.add(accountSite);
        
        /*
         * Create customer account
         */
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setAccountSiteId(accountSite.getAccountSiteId());
        customerAccount.setAccountNumber(accountNumber);
        customerAccount.setCustomerId(liteCustomer.getCustomerID());
        if(liteBillingAddress != null) {
            customerAccount.setBillingAddressId(liteBillingAddress.getAddressID());
        }
        customerAccountDao.add(customerAccount);
        dbPersistantDao.processDBChange(new DBChangeMsg(customerAccount.getAccountId(),
                                                        DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                                                        DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                                        DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                                        DBChangeMsg.CHANGE_TYPE_ADD));
            
        /*
         * Add mapping
         */
        ECToAccountMapping ecToAccountMapping = new ECToAccountMapping();
        ecToAccountMapping.setEnergyCompanyId(liteStarsEnergyCompany.getEnergyCompanyID());
        ecToAccountMapping.setAccountId(customerAccount.getAccountId());
        ecMappingDao.addECToAccountMapping(ecToAccountMapping);
        
        log.info("Account: " + accountNumber + " added successfully.");
    }

    @Override
    @Transactional
    public void deleteAccount(String accountNumber, LiteYukonUser user) {
        
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompanyByUser(user);
        CustomerAccount  account = null;
        try {
            account = customerAccountDao.getByAccountNumber(accountNumber, energyCompany.getEnergyCompanyID());
        }catch(EmptyResultDataAccessException e ) {
            log.error("Account " + accountNumber + " could not be deleted: Unable to find account for account#: " + accountNumber);
            throw new InvalidAccountNumberException("Unable to find account for account#: " + accountNumber);
        }
        
        LiteStarsCustAccountInformation customerInfo = starsCustAccountInformationDao.getById(account.getAccountId(), energyCompany.getEnergyCompanyID());
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(account.getAccountSiteId());
        LiteSiteInformation siteInfo = siteInformationDao.getSiteInfoById(accountSite.getSiteInformationId());
        LiteCustomer liteCustomer = customerDao.getLiteCustomer(account.getCustomerId());
        customerInfo.setCustomer(liteCustomer);
        LiteAddress billingAddress = addressDao.getByAddressId(account.getBillingAddressId());
        LiteContact primaryContact = customerDao.getPrimaryContact(account.getCustomerId());
        LiteStarsEnergyCompany starsEnergyCompany = ecMappingDao.getContactEC(primaryContact.getContactID());
        Integer userId = primaryContact.getLoginID();
        
        /*
         * Delete Hardware info
         */
        List<Integer> inventoryIds = inventoryDao.getInventoryIdsByAccount(account.getAccountId());
        for(Integer inventoryId : inventoryIds) {
            hardwareBaseDao.clearLMHardwareInfo(inventoryId);
        }
        
        /*
         * Delete Program info
         */
        lmProgramEventDao.deleteProgramEventsForAccount(account.getAccountId());
        
        /*
         * Delete Appliance info
         */
        applianceDao.deleteAppliancesByAccountId(account.getAccountId());
        
        /*
         * Delete WorkOrders
         */
        workOrderDao.deleteByAccount(account.getAccountId());
        
        /*
         * Delete CallReports 
         */
        callReportDao.deleteByAccount(account.getAccountId());
        
        /*
         * Delete thermostat schedules for account
         */
        thermostatScheduleDao.deleteSchedulesForAccount(account.getAccountId());
        
        /*
         * Delete account mappings
         */
        ecMappingDao.deleteECToAccountMapping(account.getAccountId());
        
        /*
         * Delete account events
         */
        eventAccountDao.deleteAllEventsForAccount(account.getAccountId());
        
        /*
         * Delete customer account
         */
        customerAccountDao.remove(account);
        
        /*
         * Delete account site
         */
        accountSiteDao.remove(accountSite);
        
        /*
         * Delete site information
         */
        siteInformationDao.delete(siteInfo);
        
        /*
         * Delete billing address
         */
        addressDao.remove(billingAddress);
        
        /*
         * Delete customer
         */
        customerDao.deleteCustomer(account.getCustomerId());
        dbPersistantDao.processDBChange(new DBChangeMsg(liteCustomer.getLiteID(),
                                           DBChangeMsg.CHANGE_CUSTOMER_DB,
                                           DBChangeMsg.CAT_CUSTOMER,
                                           DBChangeMsg.CAT_CUSTOMER,
                                           DBChangeMsg.CHANGE_TYPE_DELETE));
        
        /*
         * Delete primary contact
         */
        contactDao.deleteContact(primaryContact.getContactID());
        dbPersistantDao.processDBChange(new DBChangeMsg(primaryContact.getLiteID(),
                               DBChangeMsg.CHANGE_CONTACT_DB,
                               DBChangeMsg.CAT_CUSTOMERCONTACT,
                               DBChangeMsg.CAT_CUSTOMERCONTACT,
                               DBChangeMsg.CHANGE_TYPE_DELETE));
        
        /*
         * Delete additional contacts
         */
        List<Integer> additionalContacts = contactDao.getAdditionalContactIdsForCustomer(account.getCustomerId());
        contactDao.deleteAllAdditionalContactsForCustomer(account.getCustomerId());
        processContactDeletes(additionalContacts);
        
        /*
         * Delete login
         */
        if (userId != UserUtils.USER_DEFAULT_ID &&
                userId != UserUtils.USER_ADMIN_ID &&
                userId != UserUtils.USER_YUKON_ID) {
            yukonUserDao.deleteUser(userId);
            StarsDatabaseCache.getInstance().deleteStarsYukonUser( userId );
            dbPersistantDao.processDBChange(new DBChangeMsg(user.getLiteID(),
                                   DBChangeMsg.CHANGE_YUKON_USER_DB,
                                   DBChangeMsg.CAT_YUKON_USER,
                                   DBChangeMsg.CAT_YUKON_USER,
                                   DBChangeMsg.CHANGE_TYPE_DELETE)); 
        }
        
        /*
         * Delete customer info
         */
        starsEnergyCompany.deleteCustAccountInformation(customerInfo);
        dbPersistantDao.processDBChange(new DBChangeMsg(customerInfo.getLiteID(),
                               DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                               DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                               DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                               DBChangeMsg.CHANGE_TYPE_DELETE));
        
        log.info("Account: " +accountNumber + " deleted successfully.");
    }
    
    @Override
    @Transactional
    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user) {
        AccountDto accountDto = updatableAccount.getAccountDto();
        String accountNumber = updatableAccount.getAccountNumber();
        
        if(StringUtils.isBlank(accountNumber)) {
            log.error("Account " + accountNumber + " could not be added: The provided account number cannot be empty.");
            throw new InvalidAccountNumberException("The provided account number cannot be empty.");
        }
        
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompanyByUser(user);
        LiteStarsEnergyCompany liteStarsEnergyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompany.getEnergyCompanyID());
        LiteStarsCustAccountInformation accountInformation = liteStarsEnergyCompany.searchAccountByAccountNo(accountNumber);
        
        if(accountInformation == null) {
            log.error("Account " + accountNumber + " could not be updated: Unable to find account for account#: " + accountNumber);
            throw new InvalidAccountNumberException("Unable to find account for account#: " + accountNumber);
        }
        
        CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber, liteStarsEnergyCompany.getEnergyCompanyID());
        LiteCustomer liteCustomer = customerDao.getLiteCustomer(account.getCustomerId());
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(account.getAccountSiteId());
        LiteSiteInformation liteSiteInformation = siteInformationDao.getSiteInfoById(accountSite.getSiteInformationId());
        LiteAddress liteStreetAddress = addressDao.getByAddressId(accountSite.getStreetAddressId()); 
        LiteAddress liteBillingAddress = addressDao.getByAddressId(account.getBillingAddressId());
        LiteContact primaryContact = customerDao.getPrimaryContact(account.getCustomerId());
        Address streetAddress = accountDto.getStreetAddress();
        Address billingAddress = accountDto.getBillingAddress();
        
        /*
         * Update the address
         */
        setAddressFieldsFromDTO(liteStreetAddress, streetAddress);
        addressDao.update(liteStreetAddress);
        
        /*
         * Update the billing address if supplied
         */
        if(billingAddress != null && StringUtils.isNotBlank(billingAddress.getLocationAddress1())) {
            setAddressFieldsFromDTO(liteBillingAddress, billingAddress);
            addressDao.update(liteBillingAddress);
        }
        
        /*
         * Update the customer account
         */
        
        account.setAccountNumber(accountNumber);
        customerAccountDao.update(account);
        dbPersistantDao.processDBChange(new DBChangeMsg(account.getAccountId(),
                                                        DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                                                        DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                                        DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                                        DBChangeMsg.CHANGE_TYPE_UPDATE));
        
        /*
         * Update the primary contact
         */
        primaryContact.setContFirstName(accountDto.getFirstName());
        primaryContact.setContLastName(accountDto.getLastName());
        contactDao.saveContact(primaryContact);
        dbPersistantDao.processDBChange(new DBChangeMsg(primaryContact.getLiteID(),
            DBChangeMsg.CHANGE_CONTACT_DB,
            DBChangeMsg.CAT_CUSTOMERCONTACT,
            DBChangeMsg.CAT_CUSTOMERCONTACT,
            DBChangeMsg.CHANGE_TYPE_UPDATE));
        
        /*
         * Update the notifications
         */
        LiteContactNotification homePhoneNotif = contactNotificationDao.getNotificationForContactByType(primaryContact.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
        LiteContactNotification workPhoneNotif = contactNotificationDao.getNotificationForContactByType(primaryContact.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
        LiteContactNotification emailNotif = contactNotificationDao.getNotificationForContactByType(primaryContact.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
        
        if(StringUtils.isNotBlank(accountDto.getHomePhone())) {
            if(homePhoneNotif == null) {
                LiteContactNotification homePhoneLiteContactNotification = createNotification(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE, accountDto.getHomePhone());
                contactNotificationDao.saveNotification(homePhoneLiteContactNotification);
            }else {
                homePhoneNotif.setNotification(accountDto.getHomePhone());
                contactNotificationDao.saveNotification(homePhoneNotif);
            }
        }else {
            if(homePhoneNotif != null) {
                contactNotificationDao.removeNotification(homePhoneNotif.getContactNotifID());
            }
        }
        
        if(StringUtils.isNotBlank(accountDto.getWorkPhone())) {
            if(workPhoneNotif == null) {
                LiteContactNotification workPhoneLiteContactNotification = createNotification(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE, accountDto.getWorkPhone());
                contactNotificationDao.saveNotification(workPhoneLiteContactNotification);
            }else {
                workPhoneNotif.setNotification(accountDto.getWorkPhone());
                contactNotificationDao.saveNotification(workPhoneNotif);
            }
        }else {
            if(workPhoneNotif != null) {
                contactNotificationDao.removeNotification(workPhoneNotif.getContactNotifID());
            }
        }
        
        if(StringUtils.isNotBlank(accountDto.getEmailAddress())) {
            if(emailNotif == null) {
                LiteContactNotification emailLiteContactNotification = createNotification(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL, accountDto.getEmailAddress());
                contactNotificationDao.saveNotification(emailLiteContactNotification);
            }else {
                emailNotif.setNotification(accountDto.getEmailAddress());
                contactNotificationDao.saveNotification(emailNotif);
            }
        }else {
            if(emailNotif != null) {
                contactNotificationDao.removeNotification(emailNotif.getContactNotifID());
            }
        }
        
        if(customerDao.isCICustomer(liteCustomer.getCustomerID())){
            liteCustomer.setAltTrackingNumber(accountDto.getAltTrackingNumber());
            if(!accountDto.getIsCommercial()) {
                // was commercial, not anymore
                LiteCICustomer liteCICustomer = customerDao.getLiteCICustomer(liteCustomer.getCustomerID());
                customerDao.deleteCICustomer(liteCustomer.getCustomerID());
                if(liteCICustomer.getMainAddressID() != CtiUtilities.NONE_ZERO_ID) {
                    LiteAddress mainAddress = addressDao.getByAddressId(liteCICustomer.getMainAddressID());
                    addressDao.remove(mainAddress);
                }
                liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_RESIDENTIAL);
                customerDao.updateCustomer(liteCustomer);
            }else {
                // was commercial and still is
                LiteCICustomer liteCICustomer = customerDao.getLiteCICustomer(liteCustomer.getCustomerID());
                liteCICustomer.setAltTrackingNumber(accountDto.getAltTrackingNumber());
                liteCICustomer.setCompanyName(accountDto.getCompanyName());
                customerDao.updateCICustomer(liteCICustomer);
            }
        }else {
            if(accountDto.getIsCommercial()) {
                // was residential, now commercial
                LiteCICustomer liteCICustomer = new LiteCICustomer();
                liteCICustomer.setMainAddressID(liteStreetAddress.getAddressID());
                liteCICustomer.setDemandLevel(CtiUtilities.NONE_ZERO_ID);
                liteCICustomer.setCurtailAmount(CtiUtilities.NONE_ZERO_ID);
                liteCICustomer.setCompanyName(accountDto.getCompanyName());
                liteCICustomer.setCICustType(YukonListEntryTypes.CUSTOMER_TYPE_COMMERCIAL);
                liteCICustomer.setAltTrackingNumber(accountDto.getAltTrackingNumber());
                customerDao.addCICustomer(liteCICustomer);
            }else {
                customerDao.updateCustomer(liteCustomer);
            }
        }
        
        /*
         * Update account site
         */
        accountSite.setSiteNumber(accountDto.getMapNumber());
        accountSiteDao.update(accountSite);
        
        /*
         * Update site information
         */
        try {
            int subId = siteInformationDao.getSubstationIdByName(accountDto.getSiteInfo().getSubstationName());
            liteSiteInformation.setSubstationID(subId);
        }catch(NotFoundException e) {
            log.error("Unable to find substation by name: " + accountDto.getSiteInfo().getSubstationName() , e);
        }
        liteSiteInformation.setFeeder(accountDto.getSiteInfo().getFeeder());
        liteSiteInformation.setPole(accountDto.getSiteInfo().getPole());
        liteSiteInformation.setTransformerSize(accountDto.getSiteInfo().getTransformerSize());
        liteSiteInformation.setServiceVoltage(accountDto.getSiteInfo().getServiceVoltage());
        siteInformationDao.update(liteSiteInformation);
        
        /*
         * Update mapping
         */
        ecMappingDao.updateECToAccountMapping(account.getAccountId(), liteStarsEnergyCompany.getEnergyCompanyID());
        
        log.info("Account: " +accountNumber + " updated successfully.");
    }
    
    @Override
    public AccountDto getAccountDto(String accountNumber, LiteYukonUser yukonUser) {
        AccountDto retrievedDto = new AccountDto();
        LiteStarsEnergyCompany ec = StarsDatabaseCache.getInstance().getEnergyCompanyByUser(yukonUser);
        CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(accountNumber, ec.getEnergyCompanyID());
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(customerAccount.getAccountSiteId());
        LiteSiteInformation siteInfo = siteInformationDao.getSiteInfoById(accountSite.getSiteInformationId());
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        LiteContact primaryContact = contactDao.getContact(customer.getPrimaryContactID());
        
        retrievedDto.setFirstName(primaryContact.getContFirstName());
        retrievedDto.setLastName(primaryContact.getContLastName());
        
        if(customer instanceof LiteCICustomer) {
            retrievedDto.setCompanyName(((LiteCICustomer) customer).getCompanyName());
            retrievedDto.setIsCommercial(true);
        }else {
            retrievedDto.setCompanyName("");
            retrievedDto.setIsCommercial(false);
        }
        LiteContactNotification homePhoneNotif = contactNotificationDao.getNotificationForContactByType(primaryContact.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
        LiteContactNotification workPhoneNotif = contactNotificationDao.getNotificationForContactByType(primaryContact.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
        LiteContactNotification emailNotif = contactNotificationDao.getNotificationForContactByType(primaryContact.getContactID(), YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
        if(homePhoneNotif != null) {
            retrievedDto.setHomePhone(homePhoneNotif.getNotification());
        }else {
            retrievedDto.setHomePhone("");
        }
        if(workPhoneNotif != null) {
            retrievedDto.setWorkPhone(workPhoneNotif.getNotification());
        }else {
            retrievedDto.setWorkPhone("");
        }
        if(emailNotif != null) {
            retrievedDto.setEmailAddress(emailNotif.getNotification());
        }else {
            retrievedDto.setEmailAddress("");
        }
        
        /*
         * Apperently a dummy address row gets set even when one wasn't provided. 
         */
        LiteAddress address = addressDao.getByAddressId(primaryContact.getAddressID());
        giveAddressFieldsToDTO(address, retrievedDto.getStreetAddress());
        LiteAddress billingAdress = addressDao.getByAddressId(customerAccount.getBillingAddressId());
        giveAddressFieldsToDTO(billingAdress, retrievedDto.getBillingAddress());
        
        /*
         * Don't work with passwords here because they are a special case.
         */
        int loginId = primaryContact.getLoginID();
        if(loginId > UserUtils.USER_DEFAULT_ID) {
            LiteYukonUser login = yukonUserDao.getLiteYukonUser(loginId);
            retrievedDto.setUserName(login.getUsername());
            retrievedDto.setPassword("");
            /*
             * Currently these accounts only support users with one login group.
             * This may need to change in the future.
             */
            List<LiteYukonGroup> groups = yukonGroupDao.getGroupsForUser(login);
            retrievedDto.setLoginGroup(groups.get(0).getGroupName());
        }else {
            retrievedDto.setUserName("");
            retrievedDto.setPassword("");
            retrievedDto.setLoginGroup("");
        }
        
        retrievedDto.setAltTrackingNumber(customer.getAltTrackingNumber());
        retrievedDto.setMapNumber(accountSite.getSiteNumber());
        String subName = null;
        try {
            subName = siteInformationDao.getSubstationNameById(siteInfo.getSubstationID());
        } catch(NotFoundException nfe) {
            subName = "";
        }
        retrievedDto.getSiteInfo().setSubstationName(subName);
        retrievedDto.getSiteInfo().setFeeder(siteInfo.getFeeder());
        retrievedDto.getSiteInfo().setPole(siteInfo.getPole());
        retrievedDto.getSiteInfo().setTransformerSize(siteInfo.getTransformerSize());
        retrievedDto.getSiteInfo().setServiceVoltage(siteInfo.getServiceVoltage());
        
        return retrievedDto;
    }
    
    //==============================================================================================
    // HELPER METHODS
    //==============================================================================================
    
    private void processContactDeletes(List<Integer> contactIds) {
        for(Integer contactId : contactIds) {
            DBChangeMsg changeMsg = new DBChangeMsg(contactId,
                DBChangeMsg.CHANGE_CONTACT_DB,
                DBChangeMsg.CAT_CUSTOMERCONTACT,
                DBChangeMsg.CAT_CUSTOMERCONTACT,
                DBChangeMsg.CHANGE_TYPE_DELETE);
            
            dbPersistantDao.processDBChange(changeMsg);
        }
    }
    
    private void setAddressFieldsFromDTO(LiteAddress lite, Address address) {
        lite.setLocationAddress1(address.getLocationAddress1());
        lite.setLocationAddress2(address.getLocationAddress2());
        lite.setCityName(address.getCityName());
        lite.setStateCode(address.getStateCode());
        lite.setZipCode(address.getZipCode());
        lite.setCounty(address.getCounty());
    }
    
    private void giveAddressFieldsToDTO(LiteAddress lite, Address address) {
        address.setLocationAddress1(lite.getLocationAddress1());
        address.setLocationAddress2(lite.getLocationAddress2());
        address.setCityName(lite.getCityName());
        address.setStateCode(lite.getStateCode());
        address.setZipCode(lite.getZipCode());
        address.setCounty(lite.getCounty());
    }
    
    private LiteContactNotification createNotification(LiteContact contact, int entyType, String notification) {
        LiteContactNotification notif = new LiteContactNotification(-1);
        notif.setContactID(contact.getContactID());
        notif.setNotificationCategoryID(entyType);
        notif.setDisableFlag("N");
        notif.setNotification(notification);
        notif.setOrder(CtiUtilities.NONE_ZERO_ID);
        return notif;
    }
    
    //==============================================================================================
    // INJECTED DEPENDANCIES
    //==============================================================================================
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
    
    @Autowired
    public void setRoleDao(RoleDao roleDao) {
        this.roleDao = roleDao;
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    @Autowired
    public void setYukonGroupDao(YukonGroupDao yukonGroupDao) {
        this.yukonGroupDao = yukonGroupDao;
    }
    
    @Autowired
    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
    @Autowired
    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }
    
    @Autowired
    public void setContactNotificationDao(ContactNotificationDao contactNotificationDao) {
        this.contactNotificationDao = contactNotificationDao;
    }
    
    @Autowired
    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }
    
    @Autowired
    public void setSiteInformationDao(SiteInformationDao siteInformationDao) {
        this.siteInformationDao = siteInformationDao;
    }
    
    @Autowired
    public void setAccountSiteDao(AccountSiteDao accountSiteDao) {
        this.accountSiteDao = accountSiteDao;
    }
    
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    
    @Autowired
    public void setECMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
    
    @Autowired
    public void setHardwareBaseDao(LMHardwareBaseDao hardwareBaseDao) {
        this.hardwareBaseDao = hardwareBaseDao;
    }

    @Autowired
    public void setLMProgramEventDao(LMProgramEventDao lmProgramEventDao) {
        this.lmProgramEventDao = lmProgramEventDao;
    }

    @Autowired
    public void setApplianceDao(ApplianceDao applianceDao) {
        this.applianceDao = applianceDao;
    }

    @Autowired
    public void setWorkOrderDao(StarsWorkOrderBaseDao workOrderDao) {
        this.workOrderDao = workOrderDao;
    }

    @Autowired
    public void setCallReportDao(CallReportDao callReportDao) {
        this.callReportDao = callReportDao;
    }

    @Autowired
    public void setThermostatScheduleDao(ThermostatScheduleDao thermostatScheduleDao) {
        this.thermostatScheduleDao = thermostatScheduleDao;
    }

    @Autowired
    public void setEventAccountDao(EventAccountDao eventAccountDao) {
        this.eventAccountDao = eventAccountDao;
    }

    @Autowired
    public void setStarsCustAccountInformationDao(StarsCustAccountInformationDao starsCustAccountInformationDao) {
        this.starsCustAccountInformationDao = starsCustAccountInformationDao;
    }
    
    @Autowired
    public void setDBPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistantDao = dbPersistentDao;
    }
    
}
