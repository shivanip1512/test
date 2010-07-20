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
import com.cannontech.common.events.loggers.AccountEventLogService;
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
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.service.PhoneNumberFormattingService;
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
import com.cannontech.i18n.service.YukonUserContextService;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CallReportDao;
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
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;

public class AccountServiceImpl implements AccountService {
    
    private AccountEventLogService accountEventLogService;
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
    private EventAccountDao eventAccountDao;
    private StarsCustAccountInformationDao starsCustAccountInformationDao;
    private DBPersistentDao dbPersistantDao;
    private StarsDatabaseCache starsDatabaseCache;
    private SystemDateFormattingService systemDateFormattingService;
    private AuthenticationService authenticationService;
    private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    private ContactNotificationService contactNotificationService;
    private ContactService contactService;
    private PhoneNumberFormattingService phoneNumberFormattingService;
    private YukonUserContextService yukonUserContextService;
    private AccountThermostatScheduleDao accountThermostatScheduleDao;
    
    // ADD ACCOUNT
    @Override
    @Transactional
    public void addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator) throws AccountNumberUnavailableException, UserNameUnavailableException {
    	
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(operator);
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
            user.setLoginStatus(LoginStatusEnum.ENABLED);
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
        	contactNotificationService.createNotification(liteContact, ContactNotificationType.HOME_PHONE, accountDto.getHomePhone());
        }
        
        if(StringUtils.isNotBlank(accountDto.getWorkPhone())) {
        	contactNotificationService.createNotification(liteContact, ContactNotificationType.WORK_PHONE, accountDto.getWorkPhone());
        }
        
        if(StringUtils.isNotBlank(accountDto.getEmailAddress())) {
        	contactNotificationService.createNotification(liteContact, ContactNotificationType.EMAIL, accountDto.getEmailAddress());
        }

        if(StringUtils.isNotBlank(accountDto.getIvrLogin())) {
            contactNotificationService.createNotification(liteContact, ContactNotificationType.IVR_LOGIN, accountDto.getIvrLogin());
        }

        if(StringUtils.isNotBlank(accountDto.getVoicePIN())) {
            contactNotificationService.createNotification(liteContact, ContactNotificationType.VOICE_PIN, accountDto.getVoicePIN());
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
        
        accountSite.setSiteNumber(accountDto.getMapNumber());
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
        
        accountEventLogService.accountAdded(operator, accountNumber);
    }

    // DELETE ACCOUNT
    @Override
    @Transactional
    public void deleteAccount(int accountId, LiteYukonUser user) {
        CustomerAccount account = customerAccountDao.getById(accountId);
        deleteAccount(account, user);
    }
    
    @Override
    @Transactional
    public void deleteAccount(String accountNumber, LiteYukonUser user) {
    	
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
    	try {
    	    CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber, energyCompany.getEnergyCompanyID());
            deleteAccount(account, user);

    	}catch (NotFoundException e ) {
            log.error("Account " + accountNumber + " could not be deleted: Unable to find account for account#: " + accountNumber);
            throw new InvalidAccountNumberException("Unable to find account for account#: " + accountNumber, e);
        }
    }

    private void deleteAccount(CustomerAccount account, LiteYukonUser user) {

        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);

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
        callReportDao.deleteAllCallsByAccount(account.getAccountId());
        
        /*
         * Delete thermostat schedules for account
         */
        accountThermostatScheduleDao.deleteAllByAccountId(account.getAccountId());
        
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
         * - handles contact deletion
         */
        customerDao.deleteCustomer(account.getCustomerId());
        dbPersistantDao.processDBChange(new DBChangeMsg(liteCustomer.getLiteID(),
                                           DBChangeMsg.CHANGE_CUSTOMER_DB,
                                           DBChangeMsg.CAT_CUSTOMER,
                                           DBChangeMsg.CAT_CUSTOMER,
                                           DBChangeMsg.CHANGE_TYPE_DELETE));
        
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
        
        log.info("Account: " +account.getAccountNumber()+ " deleted successfully.");
        
        accountEventLogService.accountDeleted(user, account.getAccountNumber());
    }
    
    
    // UPDATE ACCOUNT
    @Override
    @Transactional
    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user) {
    
    	LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompanyByUser(user);
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
                LiteYukonUser newUser = new LiteYukonUser(); 
                newUser.setUsername(accountDto.getUserName());
                newUser.setLoginStatus(LoginStatusEnum.ENABLED);
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
                        newUser.setAuthType(defaultAuthType);
                    }else {
                        newUser.setAuthType(authType);
                    }
                }else {
                    newUser.setAuthType(AuthType.NONE);
                    password = emptyPassword;
                }
                yukonUserDao.addLiteYukonUserWithPassword(newUser, password, energyCompany.getEnergyCompanyID(), groups);
                newLoginId = newUser.getUserID();
                dbPersistantDao.processDBChange(new DBChangeMsg(newUser.getLiteID(),
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
        LiteContactNotification homePhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.HOME_PHONE);
        LiteContactNotification workPhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.WORK_PHONE);
        LiteContactNotification emailNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.EMAIL);
        LiteContactNotification ivrLoginNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.IVR_LOGIN);
        LiteContactNotification voicePINNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.VOICE_PIN);
        
        if(StringUtils.isNotBlank(accountDto.getHomePhone())) {
        	String homePhone = accountDto.getHomePhone();
            if(homePhoneNotif == null) {
            	contactNotificationService.createNotification(primaryContact, ContactNotificationType.HOME_PHONE, homePhone);
            }else {
                homePhoneNotif.setNotification(homePhone);
                contactNotificationDao.saveNotification(homePhoneNotif);
            }
        }else {
            if(homePhoneNotif != null) {
                contactNotificationDao.removeNotification(homePhoneNotif.getContactNotifID());
            }
        }
        
        if(StringUtils.isNotBlank(accountDto.getWorkPhone())) {
        	String workPhone = accountDto.getWorkPhone();
            if(workPhoneNotif == null) {
            	contactNotificationService.createNotification(primaryContact, ContactNotificationType.WORK_PHONE, workPhone);
            }else {
                workPhoneNotif.setNotification(workPhone);
                contactNotificationDao.saveNotification(workPhoneNotif);
            }
        }else {
            if(workPhoneNotif != null) {
                contactNotificationDao.removeNotification(workPhoneNotif.getContactNotifID());
            }
        }
        
        if(StringUtils.isNotBlank(accountDto.getEmailAddress())) {
        	String email = accountDto.getEmailAddress();
            if(emailNotif == null) {
            	contactNotificationService.createNotification(primaryContact, ContactNotificationType.EMAIL, email);
            }else {
                emailNotif.setNotification(email);
                contactNotificationDao.saveNotification(emailNotif);
            }
        }else {
            if(emailNotif != null) {
                contactNotificationDao.removeNotification(emailNotif.getContactNotifID());
            }
        }
        
        if(StringUtils.isNotBlank(accountDto.getIvrLogin())) {
            String ivrLogin = accountDto.getIvrLogin();
            if(ivrLoginNotif == null) {
                contactNotificationService.createNotification(primaryContact, ContactNotificationType.IVR_LOGIN, ivrLogin);
            }else {
                ivrLoginNotif.setNotification(ivrLogin);
                contactNotificationDao.saveNotification(ivrLoginNotif);
            }
        }else {
            if(ivrLoginNotif != null) {
                contactNotificationDao.removeNotification(ivrLoginNotif.getContactNotifID());
            }
        }
        
        if(StringUtils.isNotBlank(accountDto.getVoicePIN())) {
            String voicePin = accountDto.getVoicePIN();
            if(voicePINNotif == null) {
                contactNotificationService.createNotification(primaryContact, ContactNotificationType.VOICE_PIN, voicePin);
            }else {
                voicePINNotif.setNotification(voicePin);
                contactNotificationDao.saveNotification(voicePINNotif);
            }
        }else {
            if(voicePINNotif != null) {
                contactNotificationDao.removeNotification(voicePINNotif.getContactNotifID());
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
        accountSite.setSiteNumber(accountDto.getMapNumber());
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
        
        accountEventLogService.accountUpdated(user, accountNumber);
    }
    
    @Override
    public AccountDto getAccountDto(String accountNumber, LiteYukonUser yukonUser) {
    	
    	LiteStarsEnergyCompany ec = starsDatabaseCache.getEnergyCompanyByUser(yukonUser);
    	CustomerAccount customerAccount = getCustomerAccountForAccountNumberAndEnergyCompany(accountNumber, ec);
        
    	YukonUserContext userContext = yukonUserContextService.getEnergyCompanyDefaultUserContext(ec.getUser());
    	
    	return getAccountDto(customerAccount, ec, userContext);
    }
    
    @Override
    public AccountDto getAccountDto(String accountNumber, LiteStarsEnergyCompany ec) {
    
    	YukonUserContext userContext = yukonUserContextService.getEnergyCompanyDefaultUserContext(ec.getUser());
    	
    	CustomerAccount customerAccount = getCustomerAccountForAccountNumberAndEnergyCompany(accountNumber, ec);
    	return getAccountDto(customerAccount, ec, userContext);
    }
    
    @Override
    public AccountDto getAccountDto(int accountId, int energyCompanyId, YukonUserContext userContext) {
    	
    	CustomerAccount customerAccount = getCustomerAccountForAccountId(accountId);
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
    	
        return getAccountDto(customerAccount, energyCompany, userContext);
    }
    
    private AccountDto getAccountDto(CustomerAccount customerAccount, LiteStarsEnergyCompany ec, YukonUserContext userContext) {
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
        retrievedDto.setAltTrackingNumber(stripNone(customer.getAltTrackingNumber()));
        retrievedDto.setCustomerNumber(stripNone(customer.getCustomerNumber()));
        retrievedDto.setRateScheduleEntryId(customer.getRateScheduleID());
        if(customer instanceof LiteCICustomer) {
            retrievedDto.setCompanyName(((LiteCICustomer) customer).getCompanyName());
            retrievedDto.setIsCommercial(true);
            retrievedDto.setCommercialTypeEntryId(((LiteCICustomer) customer).getCICustType());
        }else {
            retrievedDto.setCompanyName("");
            retrievedDto.setIsCommercial(false);
            retrievedDto.setCommercialTypeEntryId(null);
        }
        
        /*
         * Notifications
         */
        LiteContactNotification homePhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.HOME_PHONE);
        LiteContactNotification workPhoneNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.WORK_PHONE);
        LiteContactNotification emailNotif = contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.EMAIL);
        if(homePhoneNotif != null) {
        	String homePhone = homePhoneNotif.getNotification();
        	homePhone = phoneNumberFormattingService.formatPhoneNumber(homePhone, userContext);
            retrievedDto.setHomePhone(homePhone);
        }else {
            retrievedDto.setHomePhone("");
        }
        if(workPhoneNotif != null) {
        	String workPhone = workPhoneNotif.getNotification();
        	workPhone = phoneNumberFormattingService.formatPhoneNumber(workPhone, userContext);
            retrievedDto.setWorkPhone(workPhone);
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
    
    // we store "(none)" in the database for some reason
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
    
    // when extracting lets turn "(none)"s into blank strings even though they may turn back into "(none)"s if they are re-saved without being set
    private void giveAddressFieldsToDTO(LiteAddress lite, Address address) {
        address.setLocationAddress1(stripNone(lite.getLocationAddress1()));
        address.setLocationAddress2(stripNone(lite.getLocationAddress2()));
        address.setCityName(stripNone(lite.getCityName()));
        address.setStateCode(stripNone(lite.getStateCode()));
        address.setZipCode(stripNone(lite.getZipCode()));
        address.setCounty(stripNone(lite.getCounty()));
    }
    
    private String stripNone (String value) {
    	return CtiUtilities.STRING_NONE.equals(value) ? "" : value;
    }
    
    //==============================================================================================
    // INJECTED DEPENDANCIES
    //==============================================================================================
    
    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
    
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
    
    @Autowired
    public void setPhoneNumberFormattingService(PhoneNumberFormattingService phoneNumberFormattingService) {
		this.phoneNumberFormattingService = phoneNumberFormattingService;
	}
    
    @Autowired
    public void setYukonUserContextService(YukonUserContextService yukonUserContextService) {
        this.yukonUserContextService = yukonUserContextService;
    }
    
    @Autowired
    public void setAccountThermostatScheduleDao(AccountThermostatScheduleDao accountThermostatScheduleDao) {
		this.accountThermostatScheduleDao = accountThermostatScheduleDao;
	}
}
