package com.cannontech.stars.dr.account.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.database.SqlUtils;
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
import com.cannontech.stars.core.dao.CallReportDao;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.exception.InvalidLoginGroupException;
import com.cannontech.stars.dr.account.exception.InvalidSubstationNameException;
import com.cannontech.stars.dr.account.exception.UserNameUnavailableException;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ECToAccountMapping;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.event.dao.EventAccountDao;
import com.cannontech.stars.dr.event.dao.LMProgramEventDao;
import com.cannontech.stars.dr.general.service.ContactNotificationService;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.user.UserUtils;

public class AccountServiceImpl implements AccountService {
    
    private Logger log = YukonLogManager.getLogger(AccountServiceImpl.class);
    
    private YukonUserDao yukonUserDao;
    private RolePropertyDao rolePropertyDao;
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
    private StarsDatabaseCache starsDatabaseCache;
    private SystemDateFormattingService systemDateFormattingService;
    private AuthenticationService authenticationService;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private ContactNotificationService contactNotificationService;
    private ContactService contactService;
    
    // ADD ACCOUNT
    @Override
    @Transactional
    public void addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator) throws AccountNumberUnavailableException, UserNameUnavailableException {
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(operator);
    	addAccount(updatableAccount, energyCompany);
    }
    
    @Override
    @Transactional
    public void addAccount(UpdatableAccount updatableAccount, LiteStarsEnergyCompany energyCompany) throws AccountNumberUnavailableException, UserNameUnavailableException {
        AccountDto accountDto = updatableAccount.getAccountDto();
        String accountNumber = updatableAccount.getAccountNumber();
        
        if(StringUtils.isBlank(accountNumber)) {
            log.error("Account " + accountNumber + " could not be added: The provided account number cannot be empty.");
            throw new InvalidAccountNumberException("The provided account number cannot be empty.");
        }
        
        // Checks to see if the account number is already being used.
        try {
            CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(accountNumber, energyCompany.getEnergyCompanyID());
            if (customerAccount != null){
            	log.error("Account " + accountNumber + " could not be added: The provided account number already exists.");
                throw new AccountNumberUnavailableException("The provided account number already exists.");
            }
        } catch (NotFoundException e ) {
        	// Account doesn't exist
        }
        
        if(yukonUserDao.getLiteYukonUser( accountDto.getUserName() ) != null) {
            log.error("Account " + accountNumber + " could not be added: The provided username already exists.");
            throw new UserNameUnavailableException("The provided username already exists.");
        }
        
        Address streetAddress = accountDto.getStreetAddress();
        Address billingAddress = accountDto.getBillingAddress();
        
        /*
         * Create the user login. If no password is specified set the AuthType
         * to NONE(No Login) and set the password to a space(done by dao).
         * If any password is specified set the AuthType to the default
         * AuthType. If it's empty set the password to a space(done by dao);
         */
        LiteYukonUser user = null;
        AuthType authType = null; // To be used later when we add AuthType to the xml messaging.
        String emptyPassword = "";
        AuthType defaultAuthType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, user);
        if(!StringUtils.isBlank(accountDto.getUserName())) {
            user = new LiteYukonUser(); 
            user.setUsername(accountDto.getUserName());
            user.setStatus(UserUtils.STATUS_ENABLED);
            List<LiteYukonGroup> groups = new ArrayList<LiteYukonGroup>();
            LiteYukonGroup defaultYukonGroup = yukonGroupDao.getLiteYukonGroup(YukonGroup.YUKON_GROUP_ID);
            groups.add(defaultYukonGroup);
            LiteYukonGroup operatorGroup = null;
            try {
                operatorGroup = yukonGroupDao.getLiteYukonGroupByName(accountDto.getLoginGroup());
                groups.add(operatorGroup);
            }catch (NotFoundException e) {
                log.error("Account " + accountNumber + " could not be added: The provided login group '"+ accountDto.getLoginGroup() + "' doesn't exist.");
                throw new InvalidLoginGroupException("The provided login group '"+ accountDto.getLoginGroup() + "' doesn't exist.");
            }
            String password = accountDto.getPassword();
            if(password != null) {
                if(authType == null) {
                    user.setAuthType(defaultAuthType);
                }else {
                    user.setAuthType(authType);
                }
            }else {
                user.setAuthType(AuthType.NONE);
                password = emptyPassword;
            }
            yukonUserDao.addLiteYukonUserWithPassword(user, password, energyCompany.getEnergyCompanyID(), groups);
            dbPersistantDao.processDBChange(new DBChangeMsg(user.getLiteID(),
                DBChangeMsg.CHANGE_YUKON_USER_DB,
                DBChangeMsg.CAT_YUKON_USER,
                DBChangeMsg.CAT_YUKON_USER,
                DBChangeMsg.CHANGE_TYPE_ADD));
        }
            
        /*
         * Create the address
         */
        LiteAddress liteAddress = new LiteAddress();
        if(streetAddress != null && StringUtils.isNotBlank(streetAddress.getLocationAddress1())) {
            setAddressFieldsFromDTO(liteAddress, streetAddress);
        }else {
            setAddressDefaults(liteAddress);
        }
        addressDao.add(liteAddress);
            
        /*
         * Create billing address
         */
        LiteAddress liteBillingAddress = new LiteAddress();
        if(billingAddress != null && StringUtils.isNotBlank(billingAddress.getLocationAddress1())) {
            setAddressFieldsFromDTO(liteBillingAddress, billingAddress);
        }else {
            setAddressDefaults(liteBillingAddress);
        }
        addressDao.add(liteBillingAddress);
            
        /*
         * Create the contact
         */
        LiteContact liteContact = contactService.createContact(accountDto.getFirstName(), accountDto.getLastName(), user);
        
        /*
         * Create the notifications
         */
        if(StringUtils.isNotBlank(accountDto.getHomePhone())) {
        	LiteContactNotification homePhoneLiteContactNotification = contactNotificationService.createNotification(liteContact, ContactNotificationType.HOME_PHONE, accountDto.getHomePhone());
            contactNotificationDao.saveNotification(homePhoneLiteContactNotification);
        }
        
        if(StringUtils.isNotBlank(accountDto.getWorkPhone())) {
        	LiteContactNotification workPhoneLiteContactNotification = contactNotificationService.createNotification(liteContact, ContactNotificationType.WORK_PHONE, accountDto.getWorkPhone());
            contactNotificationDao.saveNotification(workPhoneLiteContactNotification);
        }
        
        if(StringUtils.isNotBlank(accountDto.getEmailAddress())) {
        	LiteContactNotification emailLiteContactNotification = contactNotificationService.createNotification(liteContact, ContactNotificationType.EMAIL, accountDto.getEmailAddress());
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
        if (accountDto.getAltTrackingNumber() == null) {
        	accountDto.setAltTrackingNumber(CtiUtilities.STRING_NONE);
        }
        if (user != null) {
        	liteCustomer.setTimeZone(authDao.getUserTimeZone(user).getID());
        } else {
        	liteCustomer.setTimeZone(systemDateFormattingService.getSystemTimeZone().getID());
        }
        if (accountDto.getCustomerNumber() != null) {
        	liteCustomer.setCustomerNumber(accountDto.getCustomerNumber());
        } else {
        	liteCustomer.setCustomerNumber(CtiUtilities.STRING_NONE);
        }
        if (accountDto.getRateScheduleEntryId() != null) {
        	liteCustomer.setRateScheduleID(accountDto.getRateScheduleEntryId());
        } else {
        	liteCustomer.setRateScheduleID(CtiUtilities.NONE_ZERO_ID);
        }
        liteCustomer.setAltTrackingNumber(accountDto.getAltTrackingNumber());
        liteCustomer.setTemperatureUnit(rolePropertyDao.getPropertyStringValue(YukonRoleProperty.DEFAULT_TEMPERATURE_UNIT, energyCompany.getUser()));
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
            LiteAddress companyAddress = new LiteAddress();
            if(streetAddress != null && StringUtils.isNotBlank(streetAddress.getLocationAddress1())) {
                setAddressFieldsFromDTO(companyAddress, streetAddress);
            }else {
                setAddressDefaults(companyAddress);
            }
            addressDao.add(companyAddress);
            
            LiteCICustomer liteCICustomer = new LiteCICustomer();
            liteCICustomer.setCustomerID(liteCustomer.getCustomerID());
            liteCICustomer.setMainAddressID(companyAddress.getAddressID());
            liteCICustomer.setDemandLevel(CtiUtilities.NONE_ZERO_ID);
            liteCICustomer.setCurtailAmount(CtiUtilities.NONE_ZERO_ID);
            liteCICustomer.setCompanyName(accountDto.getCompanyName());
            if (accountDto.getCommercialTypeEntryId() != null) {
            	liteCICustomer.setCICustType(accountDto.getCommercialTypeEntryId());
            } else {
            	liteCICustomer.setCICustType(YukonListEntryTypes.CUSTOMER_TYPE_COMMERCIAL);
            }
            liteCICustomer.setEnergyCompanyID(energyCompany.getEnergyCompanyID());
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
        if(StringUtils.isNotEmpty(accountDto.getSiteInfo().getSubstationName())) {
            try {
                int subId = siteInformationDao.getSubstationIdByName(accountDto.getSiteInfo().getSubstationName());
                liteSiteInformation.setSubstationID(subId);
            }catch(NotFoundException e) {
                log.error("Unable to find substation by name: " + accountDto.getSiteInfo().getSubstationName());
                throw new InvalidSubstationNameException("Unable to find substation by name: "+ accountDto.getSiteInfo().getSubstationName());
            }
        }
        liteSiteInformation.setFeeder(accountDto.getSiteInfo().getFeeder());
        liteSiteInformation.setPole(accountDto.getSiteInfo().getPole());
        liteSiteInformation.setTransformerSize(accountDto.getSiteInfo().getTransformerSize());
        liteSiteInformation.setServiceVoltage(accountDto.getSiteInfo().getServiceVoltage());
        siteInformationDao.add(liteSiteInformation);
        
        /*
         * Create account site
         */
        AccountSite accountSite = new AccountSite();
        accountSite.setSiteInformationId(liteSiteInformation.getSiteID());
        accountSite.setStreetAddressId(liteAddress.getAddressID());
        if (accountDto.getIsCustAtHome() != null) {
        	accountSite.setCustAtHome(BooleanUtils.toString(accountDto.getIsCustAtHome(), "Y", "N"));
        } else {
        	accountSite.setCustAtHome("N");
        }
        if (accountDto.getCustomerStatus() != null) {
        	accountSite.setCustomerStatus(accountDto.getCustomerStatus());
        } else {
        	accountSite.setCustomerStatus("A");
        }
        accountSite.setSiteNumber(StringUtils.isBlank(accountDto.getMapNumber()) ? " " : accountDto.getMapNumber());
        accountSite.setPropertyNotes(CtiUtilities.STRING_NONE);
        accountSiteDao.add(accountSite);
        
        /*
         * Create customer account
         */
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setAccountSiteId(accountSite.getAccountSiteId());
        customerAccount.setAccountNumber(accountNumber);
        customerAccount.setCustomerId(liteCustomer.getCustomerID());
        customerAccount.setAccountNotes(CtiUtilities.STRING_NONE);
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
        ecToAccountMapping.setEnergyCompanyId(energyCompany.getEnergyCompanyID());
        ecToAccountMapping.setAccountId(customerAccount.getAccountId());
        ecMappingDao.addECToAccountMapping(ecToAccountMapping);
        log.info("Account: " + accountNumber + " added successfully.");
    }

    // DELETE ACCOUNT
    @Override
    @Transactional
    public void deleteAccount(String accountNumber, LiteYukonUser user) {
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
    	deleteAccount(accountNumber, energyCompany);
    }
    
    @Override
    @Transactional
    public void deleteAccount(int accountId, int energyCompanyId) {
    	
    	CustomerAccount customerAccount = getCustomerAccountForAccountId(accountId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
    	
        deleteAccount(customerAccount.getAccountNumber(), energyCompany);
    }
    
    @Override
    @Transactional
    public void deleteAccount(String accountNumber, LiteStarsEnergyCompany energyCompany) {
        
        CustomerAccount  account = null;
        try {
            account = customerAccountDao.getByAccountNumber(accountNumber, energyCompany.getEnergyCompanyID());
        }catch (NotFoundException e ) {
            log.error("Account " + accountNumber + " could not be deleted: Unable to find account for account#: " + accountNumber);
            throw new InvalidAccountNumberException("Unable to find account for account#: " + accountNumber, e);
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
         * This also handles unenrolling inventory as well.
         * clearLMHardwareInfo clears the operator side enrollment and the unenrollHardware method
         * cleans up the consumer side enrollment
         */
        List<Integer> inventoryIds = inventoryDao.getInventoryIdsByAccount(account.getAccountId());
        for(Integer inventoryId : inventoryIds) {
            hardwareBaseDao.clearLMHardwareInfo(inventoryId);
            lmHardwareControlGroupDao.unenrollHardware(inventoryId);
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
        if(billingAddress.getAddressID() != CtiUtilities.NONE_ZERO_ID) {
            addressDao.remove(billingAddress);
        }
        
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
            starsDatabaseCache.deleteStarsYukonUser( userId );
            dbPersistantDao.processDBChange(new DBChangeMsg(energyCompany.getUser().getUserID(),
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
    
    
    // UPDATE ACCOUNT
    @Override
    @Transactional
    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user) {
    
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
    	updateAccount(updatableAccount, energyCompany);
    }
    
    @Override
    @Transactional
    public void updateAccount(UpdatableAccount updatableAccount, LiteStarsEnergyCompany energyCompany) {
        AccountDto accountDto = updatableAccount.getAccountDto();
        String accountNumber = updatableAccount.getAccountNumber();
        String username = accountDto.getUserName(); 
        
        /*
         * Check for account info errors.
         */
        if(StringUtils.isBlank(accountNumber)) {
            log.error("Account " + accountNumber + " could not be updated: The provided account number cannot be empty.");
            throw new InvalidAccountNumberException("The provided account number cannot be empty.");
        }
        
        CustomerAccount  account = null;
        try {
            account = customerAccountDao.getByAccountNumber(accountNumber, energyCompany.getEnergyCompanyID());
        } catch (NotFoundException e ) {
            log.error("Account " + accountNumber + " could not be updated: Unable to find account for account#: " + accountNumber);
            throw new InvalidAccountNumberException("Unable to find account for account#: " + accountNumber, e);
        }
        
        boolean accountNumberUpdate = false;
        String updatedAccountNumber = accountDto.getAccountNumber();
        if (StringUtils.isNotBlank(updatedAccountNumber) && !updatedAccountNumber.equals(accountNumber)) {
        		
    		try {
                CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(updatedAccountNumber, energyCompany.getEnergyCompanyID());
                if (customerAccount != null){
                	log.error("Account " + accountNumber + " could not be updated: The provided new account number already exists (" + updatedAccountNumber + ").");
                    throw new AccountNumberUnavailableException("The provided new account number already exists (" + updatedAccountNumber + ").");
                }
            } catch (NotFoundException e ) {
            	// if an account is not found, then there is no risk of it already existing
            }

            accountNumberUpdate = true;
        }
        
        /*
         * Check for login info errors.
         */
        LiteYukonUser tempUser = yukonUserDao.getLiteYukonUser( username ); 
        if(tempUser != null) {
            List<LiteContact> tempContacts = contactDao.getContactsByLoginId(tempUser.getUserID());
            boolean ourUsername = false;
            for(LiteContact tempContact : tempContacts) {
                CustomerAccount tempAccount = customerAccountDao.getAccountByContactId(tempContact.getContactID());
                if(tempAccount.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                    ourUsername = true;
                    break;
                }
            }
            if(!ourUsername) {
                log.error("Account " + accountNumber + " could not be updated: The provided username already in use.");
                throw new UserNameUnavailableException("The provided username already in use.");
            }
        }
        
        LiteCustomer liteCustomer = customerDao.getLiteCustomer(account.getCustomerId());
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(account.getAccountSiteId());
        LiteSiteInformation liteSiteInformation = siteInformationDao.getSiteInfoById(accountSite.getSiteInformationId());
        LiteAddress liteStreetAddress = addressDao.getByAddressId(accountSite.getStreetAddressId()); 
        LiteAddress liteBillingAddress = addressDao.getByAddressId(account.getBillingAddressId());
        LiteContact primaryContact = customerDao.getPrimaryContact(account.getCustomerId());
        Address streetAddress = accountDto.getStreetAddress();
        Address billingAddress = accountDto.getBillingAddress();
        
        /*RULES FOR LOGIN UPDATE/CREATION
         * LOGIN EXISTS
         * Update the login, if no password is specified don't change the Authtype or password.
         * If any password is specified, set the AuthType to the default AuthType. If it was empty
         * set the password to a space(done by doa). 
         * 
         * LOGIN DOESN'T EXIST
         * If the account has no login, create one. If no password is specified set the 
         * AuthType to NONE(No Login) and set the password to a space(done by dao). 
         * If any password is specified set AuthType to default AuthType.  If it was empty set it
         *  to a space(done by dao).
         */
        int newLoginId = UserUtils.USER_DEFAULT_ID;
        AuthType authType = null; // To be used later when we add AuthType to the xml messaging.
        AuthType defaultAuthType = rolePropertyDao.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, null);
        String emptyPassword = ""; // Dao will end up setting this to a space for oracle reasons.
        if(StringUtils.isNotBlank(username)) {
            LiteYukonUser login = yukonUserDao.getLiteYukonUser(primaryContact.getLoginID());
            if(login != null && login.getUserID() != UserUtils.USER_DEFAULT_ID) {
                /*
                 * Update their login info.
                 */
                login.setUsername(username);
                // passwords should be handled better than plain text
                String password = accountDto.getPassword();
                if(password != null) {
                    if(authType == null) {
                        login.setAuthType(defaultAuthType);
                    }else {
                        login.setAuthType(authType);
                    }
                    if(authenticationService.supportsPasswordSet(login.getAuthType())) {
                        authenticationService.setPassword(login, SqlUtils.convertStringToDbValue(password));
                    }
                }
                yukonUserDao.update(login);
            } else {
                /*
                 * Create a new login.
                 */
                LiteYukonUser user = new LiteYukonUser(); 
                user.setUsername(accountDto.getUserName());
                user.setStatus(UserUtils.STATUS_ENABLED);
                List<LiteYukonGroup> groups = new ArrayList<LiteYukonGroup>();
                LiteYukonGroup defaultYukonGroup = yukonGroupDao.getLiteYukonGroup(YukonGroup.YUKON_GROUP_ID);
                groups.add(defaultYukonGroup);
                LiteYukonGroup loginGroup = null;
                try {
                    loginGroup = yukonGroupDao.getLiteYukonGroupByName(accountDto.getLoginGroup());
                    groups.add(loginGroup);
                }catch (NotFoundException e) {
                    log.error("Account " + accountNumber + " could not be updated: The provided login group '"+ accountDto.getLoginGroup() + "' doesn't exist.");
                    throw new InvalidLoginGroupException("The provided login group '"+ accountDto.getLoginGroup() + "' doesn't exist.");
                }
                String password = accountDto.getPassword();
                if(password != null) {
                    if(authType == null) {
                        user.setAuthType(defaultAuthType);
                    }else {
                        user.setAuthType(authType);
                    }
                }else {
                    user.setAuthType(AuthType.NONE);
                    password = emptyPassword;
                }
                yukonUserDao.addLiteYukonUserWithPassword(user, password, energyCompany.getEnergyCompanyID(), groups);
                newLoginId = user.getUserID();
                dbPersistantDao.processDBChange(new DBChangeMsg(user.getLiteID(),
                    DBChangeMsg.CHANGE_YUKON_USER_DB,
                    DBChangeMsg.CAT_YUKON_USER,
                    DBChangeMsg.CAT_YUKON_USER,
                    DBChangeMsg.CHANGE_TYPE_ADD));
            }
        }
        
        /*
         * Update the address
         */
        if(streetAddress != null && StringUtils.isNotBlank(streetAddress.getLocationAddress1())) {
            setAddressFieldsFromDTO(liteStreetAddress, streetAddress);
            addressDao.update(liteStreetAddress);
        }
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
        if (accountNumberUpdate) {
        	account.setAccountNumber(updatedAccountNumber);
        } else {
        	account.setAccountNumber(accountNumber);
        }
        customerAccountDao.update(account);
        dbPersistantDao.processDBChange(new DBChangeMsg(account.getAccountId(),
                                                        DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                                                        DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                                        DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                                        DBChangeMsg.CHANGE_TYPE_UPDATE));
        
        /*
         * Update the primary contact
         */
        contactService.updateContact(primaryContact, accountDto.getFirstName(), accountDto.getLastName(), newLoginId != UserUtils.USER_DEFAULT_ID ? newLoginId : null);
        
        primaryContact.setContFirstName(accountDto.getFirstName());
        primaryContact.setContLastName(accountDto.getLastName());
        if(newLoginId != UserUtils.USER_DEFAULT_ID) {
            primaryContact.setLoginID(newLoginId);
        }
        contactDao.saveContact(primaryContact);
        dbPersistantDao.processDBChange(new DBChangeMsg(primaryContact.getLiteID(),
            DBChangeMsg.CHANGE_CONTACT_DB,
            DBChangeMsg.CAT_CUSTOMERCONTACT,
            DBChangeMsg.CAT_CUSTOMERCONTACT,
            DBChangeMsg.CHANGE_TYPE_UPDATE));
        
        /*
         * Update the notifications
         */
        LiteContactNotification homePhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
        LiteContactNotification workPhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
        LiteContactNotification emailNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
        
        if(StringUtils.isNotBlank(accountDto.getHomePhone())) {
            if(homePhoneNotif == null) {
            	LiteContactNotification homePhoneLiteContactNotification = contactNotificationService.createNotification(primaryContact, ContactNotificationType.HOME_PHONE, accountDto.getHomePhone());
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
            	LiteContactNotification workPhoneLiteContactNotification = contactNotificationService.createNotification(primaryContact, ContactNotificationType.WORK_PHONE, accountDto.getWorkPhone());
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
            	LiteContactNotification emailLiteContactNotification = contactNotificationService.createNotification(primaryContact, ContactNotificationType.EMAIL, accountDto.getEmailAddress());
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
        
        /*
         * Update the customer
         */
        liteCustomer.setAltTrackingNumber(accountDto.getAltTrackingNumber());
        if (accountDto.getCustomerNumber() != null) {
    		liteCustomer.setCustomerNumber(accountDto.getCustomerNumber());
        }
        if (accountDto.getRateScheduleEntryId() != null) {
        	liteCustomer.setRateScheduleID(accountDto.getRateScheduleEntryId());
        }
        
        if(customerDao.isCICustomer(liteCustomer.getCustomerID())){
            if(!accountDto.getIsCommercial()) {
                // was commercial, not anymore
                customerDao.deleteCICustomer(liteCustomer.getCustomerID());
                liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_RESIDENTIAL);
                customerDao.updateCustomer(liteCustomer);
            }else {
                // was commercial and still is
                LiteCICustomer liteCICustomer = customerDao.getLiteCICustomer(liteCustomer.getCustomerID());
                liteCICustomer.setCompanyName(accountDto.getCompanyName());
                if (accountDto.getCommercialTypeEntryId() != null) {
                	liteCICustomer.setCICustType(accountDto.getCommercialTypeEntryId());
                }
                customerDao.updateCICustomer(liteCICustomer);
                customerDao.updateCustomer(liteCustomer);
            }
        }else {
            if(accountDto.getIsCommercial()) {
                // was residential, now commercial
                LiteAddress companyAddress = new LiteAddress();
                if(streetAddress != null && StringUtils.isNotBlank(streetAddress.getLocationAddress1())) {
                    setAddressFieldsFromDTO(companyAddress, streetAddress);
                }
                addressDao.add(companyAddress);
                
                liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_CI);
                customerDao.updateCustomer(liteCustomer);
                
                LiteCICustomer liteCICustomer = new LiteCICustomer();
                liteCICustomer.setMainAddressID(companyAddress.getAddressID());
                liteCICustomer.setDemandLevel(CtiUtilities.NONE_ZERO_ID);
                liteCICustomer.setCurtailAmount(CtiUtilities.NONE_ZERO_ID);
                liteCICustomer.setCompanyName(accountDto.getCompanyName());
                if (accountDto.getCommercialTypeEntryId() != null) {
                	liteCICustomer.setCICustType(accountDto.getCommercialTypeEntryId());
                } else {
                	liteCICustomer.setCICustType(YukonListEntryTypes.CUSTOMER_TYPE_COMMERCIAL);
                }
                liteCICustomer.setAltTrackingNumber(accountDto.getAltTrackingNumber());
                liteCICustomer.setCustomerID(liteCustomer.getCustomerID());
                customerDao.addCICustomer(liteCICustomer);
            }else {
                customerDao.updateCustomer(liteCustomer);
            }
        }
        dbPersistantDao.processDBChange(new DBChangeMsg(liteCustomer.getLiteID(),
                                                        DBChangeMsg.CHANGE_CUSTOMER_DB,
                                                        DBChangeMsg.CAT_CUSTOMER,
                                                        DBChangeMsg.CAT_CUSTOMER,
                                                        DBChangeMsg.CHANGE_TYPE_UPDATE));
        
        /*
         * Update account site
         */
        accountSite.setSiteNumber(StringUtils.isBlank(accountDto.getMapNumber()) ? CtiUtilities.STRING_NONE : accountDto.getMapNumber());
        if (accountDto.getCustomerStatus() != null) {
        	accountSite.setCustomerStatus(accountDto.getCustomerStatus());
        }
        if (accountDto.getIsCustAtHome() != null) {
        	accountSite.setCustAtHome(BooleanUtils.toString(accountDto.getIsCustAtHome(), "Y", "N"));
        }
        accountSiteDao.update(accountSite);
        
        /*
         * Update site information
         */
        if(StringUtils.isNotEmpty(accountDto.getSiteInfo().getSubstationName())) {
            try {
                int subId = siteInformationDao.getSubstationIdByName(accountDto.getSiteInfo().getSubstationName());
                liteSiteInformation.setSubstationID(subId);
            }catch(NotFoundException e) {
                log.error("Unable to find substation by name: " + accountDto.getSiteInfo().getSubstationName() , e);
                throw new InvalidSubstationNameException("Unable to find substation by name: "+ accountDto.getSiteInfo().getSubstationName());
            }
        }
        liteSiteInformation.setFeeder(accountDto.getSiteInfo().getFeeder());
        liteSiteInformation.setPole(accountDto.getSiteInfo().getPole());
        liteSiteInformation.setTransformerSize(accountDto.getSiteInfo().getTransformerSize());
        liteSiteInformation.setServiceVoltage(accountDto.getSiteInfo().getServiceVoltage());
        siteInformationDao.update(liteSiteInformation);
        
        /*
         * Update mapping
         */
        ecMappingDao.updateECToAccountMapping(account.getAccountId(), energyCompany.getEnergyCompanyID());
        
        /*
         * Update Login
         */
        
        
        log.info("Account: " +accountNumber + " updated successfully.");
    }
    
    @Override
    public AccountDto getAccountDto(String accountNumber, LiteYukonUser yukonUser) {
    	
    	LiteStarsEnergyCompany ec = starsDatabaseCache.getEnergyCompanyByUser(yukonUser);
    	CustomerAccount customerAccount = getCustomerAccountForAccountNumberAndEnergyCompany(accountNumber, ec);
        
    	return getAccountDto(customerAccount, ec);
    }
    
    @Override
    public AccountDto getAccountDto(String accountNumber, LiteStarsEnergyCompany ec) {
    
    	CustomerAccount customerAccount = getCustomerAccountForAccountNumberAndEnergyCompany(accountNumber, ec);
    	return getAccountDto(customerAccount, ec);
    }
    
    @Override
    public AccountDto getAccountDto(int accountId, int energyCompanyId) {
    	
    	CustomerAccount customerAccount = getCustomerAccountForAccountId(accountId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
    	
        return getAccountDto(customerAccount, energyCompany);
    }
    
    private AccountDto getAccountDto(CustomerAccount customerAccount, LiteStarsEnergyCompany ec) {
        AccountDto retrievedDto = new AccountDto();
        
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(customerAccount.getAccountSiteId());
        LiteSiteInformation siteInfo = siteInformationDao.getSiteInfoById(accountSite.getSiteInformationId());
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        LiteContact primaryContact = contactDao.getContact(customer.getPrimaryContactID());
        
        retrievedDto.setAccountNumber(customerAccount.getAccountNumber());
        retrievedDto.setFirstName(primaryContact.getContFirstName());
        retrievedDto.setLastName(primaryContact.getContLastName());
        
        /*
         * Customer
         */
        retrievedDto.setAltTrackingNumber(customer.getAltTrackingNumber());
        retrievedDto.setCustomerNumber(customer.getCustomerNumber());
        retrievedDto.setRateScheduleEntryId(customer.getRateScheduleID());
        if(customer instanceof LiteCICustomer) {
            retrievedDto.setCompanyName(((LiteCICustomer) customer).getCompanyName());
            retrievedDto.setIsCommercial(true);
            retrievedDto.setCommercialTypeEntryId(((LiteCICustomer) customer).getCICustType());
        }else {
            retrievedDto.setCompanyName("");
            retrievedDto.setIsCommercial(false);
            retrievedDto.setCommercialTypeEntryId(CustomerTypes.CUSTOMER_RESIDENTIAL);
        }
        
        /*
         * Notifications
         */
        LiteContactNotification homePhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
        LiteContactNotification workPhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE);
        LiteContactNotification emailNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, YukonListEntryTypes.YUK_ENTRY_ID_EMAIL);
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
         * Addresses
         */
        LiteAddress address = addressDao.getByAddressId(accountSite.getStreetAddressId());
        giveAddressFieldsToDTO(address, retrievedDto.getStreetAddress());
        LiteAddress billingAdress = addressDao.getByAddressId(customerAccount.getBillingAddressId());
        giveAddressFieldsToDTO(billingAdress, retrievedDto.getBillingAddress());
        
        int loginId = primaryContact.getLoginID();
        if(loginId > UserUtils.USER_DEFAULT_ID) {
            LiteYukonUser login = yukonUserDao.getLiteYukonUser(loginId);
            retrievedDto.setUserName(login.getUsername());
            /*
             * Currently these accounts only support users with one login group.
             * This may need to change in the future.
             */
            List<LiteYukonGroup> groups = yukonGroupDao.getGroupsForUser(login);
            retrievedDto.setLoginGroup(groups.get(0).getGroupName());
        }else {
            retrievedDto.setUserName("");
            retrievedDto.setLoginGroup("");
        }
        
        /*
         * Account site
         */
        retrievedDto.setMapNumber(accountSite.getSiteNumber());
        retrievedDto.setCustomerStatus(accountSite.getCustomerStatus());
        retrievedDto.setIsCustAtHome(accountSite.getCustAtHome().equals("N") ? false : true);
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
    
    private CustomerAccount getCustomerAccountForAccountNumberAndEnergyCompany(String accountNumber, LiteStarsEnergyCompany ec) {
    	
    	CustomerAccount customerAccount = null;
        try {
            customerAccount = customerAccountDao.getByAccountNumber(accountNumber, ec.getEnergyCompanyID());
        } catch (NotFoundException e) {
            log.error("Unable to find account for account#: " + accountNumber);
            throw new InvalidAccountNumberException("Unable to find account for account#: " + accountNumber, e);
        }
        
        return customerAccount;
    }
    
    private CustomerAccount getCustomerAccountForAccountId(int accountId) {
    	
    	CustomerAccount customerAccount = null;
        try {
            customerAccount = customerAccountDao.getById(accountId);
        } catch (EmptyResultDataAccessException e) {
            log.error("Unable to find account for accountId: " + accountId);
            throw new InvalidAccountNumberException("Unable to find account for accountId: " + accountId, e);
        }
        
        return customerAccount;
    }
    
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
    
    private void setAddressDefaults(LiteAddress liteAddress) {
        liteAddress.setLocationAddress1(CtiUtilities.STRING_NONE);
        liteAddress.setLocationAddress2(CtiUtilities.STRING_NONE);
        liteAddress.setCityName(CtiUtilities.STRING_NONE);
        liteAddress.setCounty(CtiUtilities.STRING_NONE);
        liteAddress.setStateCode("  ");
        liteAddress.setZipCode(CtiUtilities.STRING_NONE);
    }
    
    private void setAddressFieldsFromDTO(LiteAddress lite, Address address) {
        lite.setLocationAddress1(address.getLocationAddress1());
        if (StringUtils.isBlank(address.getLocationAddress2())) {
        	lite.setLocationAddress2(CtiUtilities.STRING_NONE);
        } else {
        	lite.setLocationAddress2(address.getLocationAddress2());
        }
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
    
    //==============================================================================================
    // INJECTED DEPENDANCIES
    //==============================================================================================
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
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
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }
    
    @Autowired
    public void setSystemDateFormattingService(SystemDateFormattingService systemDateFormattingService) {
		this.systemDateFormattingService = systemDateFormattingService;
	}
    
    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setLmHardwareControlGroupDao(LMHardwareControlGroupDao lmHardwareControlGroupDao) {
        this.lmHardwareControlGroupDao = lmHardwareControlGroupDao;
    }
    
    @Autowired
    public void setContactNotificationService(ContactNotificationService contactNotificationService) {
		this.contactNotificationService = contactNotificationService;
	}
    
    @Autowired
    public void setContactService(ContactService contactService) {
		this.contactService = contactService;
	}
}
