package com.cannontech.stars.dr.account.service.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.inventory.InventoryIdentifier;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.validator.AddressValidator;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.core.service.SystemDateFormattingService;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.data.customer.CustomerTypes;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.nest.service.NestService;
import com.cannontech.i18n.service.YukonUserContextService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteSiteInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CallReportDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.exception.InvalidAddressException;
import com.cannontech.stars.dr.account.exception.InvalidLoginGroupException;
import com.cannontech.stars.dr.account.exception.InvalidSubstationNameException;
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
import com.cannontech.stars.dr.hardware.dao.LMHardwareControlGroupDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.service.HardwareService;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;

public class AccountServiceImpl implements AccountService {
    private final static Logger log = YukonLogManager.getLogger(AccountServiceImpl.class);

    @Autowired private AccountEventLogService accountEventLogService;
    @Autowired private AccountSiteDao accountSiteDao;
    @Autowired private AccountThermostatScheduleDao accountThermostatScheduleDao;
    @Autowired private AddressDao addressDao;
    @Autowired private ApplianceDao applianceDao;
    @Autowired private AuthDao authDao;
    @Autowired private AuthenticationService authenticationService;
    @Autowired private CallReportDao callReportDao;
    @Autowired private ContactDao contactDao;
    @Autowired private ContactNotificationDao contactNotificationDao;
    @Autowired private ContactNotificationService contactNotificationService;
    @Autowired private ContactService contactService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private CustomerDao customerDao;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private EventAccountDao eventAccountDao;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private LmHardwareBaseDao hardwareBaseDao;
    @Autowired private LMProgramEventDao lmProgramEventDao;
    @Autowired private LMHardwareControlGroupDao lmHardwareControlGroupDao;
    @Autowired private PhoneNumberFormattingService phoneNumberFormattingService;
    @Autowired private StarsCustAccountInformationDao starsCustAccountInformationDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private StarsWorkOrderBaseDao workOrderDao;
    @Autowired private SiteInformationDao siteInformationDao;
    @Autowired private SystemDateFormattingService systemDateFormattingService;
    @Autowired private UserGroupDao userGroupDao;
    @Autowired private YukonUserContextService userContextService;
    @Autowired private YukonUserDao userDao;
    @Autowired private YukonUserPasswordDao yukonUserPasswordDao;
    @Autowired private NestService nestService;
    @Autowired private HardwareService hardwareService;
    @Autowired private UsersEventLogService usersEventLogService;
    
    @Override
    @Transactional
    public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator)
            throws AccountNumberUnavailableException, UserNameUnavailableException {
        EnergyCompany ec = ecDao.getEnergyCompanyByOperator(operator);
        return addAccount(updatableAccount, operator, ec);
    }
    
    @Override
    @Transactional
    public int addAccount(UpdatableAccount updatableAccount, LiteYukonUser operator, YukonEnergyCompany operatorEnergyCompany)
            throws AccountNumberUnavailableException, UserNameUnavailableException {
        // Add the account to the user's energy company, we do not have a mechanism to allow
        // an operator user of a parent energy company to add accounts to member energy companies.
        EnergyCompany ec = (EnergyCompany) operatorEnergyCompany;
        AccountDto accountDto = updatableAccount.getAccountDto();
        String accountNumber = updatableAccount.getAccountNumber();

        if (StringUtils.isBlank(accountNumber)) {
            log.error("Account " + accountNumber + " could not be added: The provided account number cannot be empty.");
            throw new InvalidAccountNumberException("The provided account number cannot be empty.");
        } else if (accountNumber.length() > 40) { // if not blank, check it is within 40 character db size limit
            log.error("Account " + accountNumber
                + " could not be added: The provided account number exceeds maximum length of 40.");
            throw new InvalidAccountNumberException("The provided account number exceeds maximum length of 40.");
        }

        // Checks to see if the account number is already being used in primary energy company and its descendants
        try {
            CustomerAccount customerAccount =
                customerAccountDao.getByAccountNumber(accountNumber, ec.getDescendants(true));
            if (customerAccount != null) {
                log.error("Account " + accountNumber
                    + " could not be added: The provided account number already exists.");
                throw new AccountNumberUnavailableException("The provided account number already exists.");
            }
        } catch (NotFoundException e) {
            // Account doesn't exist
        }

        if (userDao.findUserByUsername(accountDto.getUserName()) != null) {
            log.error("Account " + accountNumber + " could not be added: The provided username already exists.");
            throw new UserNameUnavailableException("The provided username already exists.");
        }

        Address streetAddress = accountDto.getStreetAddress();
        Address billingAddress = accountDto.getBillingAddress();

        // Create the user login. If no password is specified set the AuthType
        // to NONE(No Login) and set the password to a space(done by dao).
        // If any password is specified set the AuthType to the default
        // AuthType. If it's empty set the password to a space(done by dao);
        LiteYukonUser user = null;
        if (!StringUtils.isBlank(accountDto.getUserName())) {
            // This is not preferred, but until we correctly rewrite a way to "create" a YukonUser this is how
            // it will be.
            user = new LiteYukonUser(LiteYukonUser.CREATE_NEW_USER_ID, accountDto.getUserName(), LoginStatusEnum.ENABLED);
            try {
                LiteUserGroup operatorUserGroup =
                    userGroupDao.getLiteUserGroupByUserGroupName(accountDto.getUserGroup());
                user.setUserGroupId(operatorUserGroup.getUserGroupId());
            } catch (NotFoundException e) {
                log.error("Account " + accountNumber + " could not be added: The provided user group '"
                    + accountDto.getUserGroup() + "' doesn't exist.");
                throw new InvalidLoginGroupException("The provided role group '" + accountDto.getUserGroup()
                    + "' doesn't exist.");
            }

            userDao.save(user);
            usersEventLogService.userCreated(user.getUsername(), accountDto.getUserGroup(), ec.getName(), user.getLoginStatus() , operator);
            String password = accountDto.getPassword();
            if (!StringUtils.isBlank(password)) {
                authenticationService.setPassword(user, authenticationService.getDefaultAuthenticationCategory(),
                    password, operator);
                /*
                 * This is to force password reset if the user is created during the account creation from
                 * Account Importer in Bulk Operations
                 */
                if (accountDto.getForcePasswordReset().equals(YNBoolean.YES)) {
                    yukonUserPasswordDao.setForceResetForUser(user, YNBoolean.YES);
                }
            }

            dbChangeManager.processDbChange(user.getLiteID(), DBChangeMsg.CHANGE_YUKON_USER_DB,
                DBChangeMsg.CAT_YUKON_USER, DBChangeMsg.CAT_YUKON_USER, DbChangeType.ADD);
        }

        // Create the address
        LiteAddress liteAddress = new LiteAddress();
        if (streetAddress != null) {
            validateAddress(accountNumber, streetAddress, "streetAddress");
            setAddressFieldsFromDTO(liteAddress, streetAddress);
        } else {
            setAddressDefaults(liteAddress);
        }
        addressDao.add(liteAddress);

        // Create billing address
        LiteAddress liteBillingAddress = new LiteAddress();
        if (billingAddress != null) {
            validateAddress(accountNumber, billingAddress, "billingAddress");
            setAddressFieldsFromDTO(liteBillingAddress, billingAddress);
        } else {
            setAddressDefaults(liteBillingAddress);
        }
        addressDao.add(liteBillingAddress);

        // Create the contact
        LiteContact liteContact =
            contactService.createContact(accountDto.getFirstName(), accountDto.getLastName(), user);

        // Create the notifications
        if (StringUtils.isNotBlank(accountDto.getHomePhone())) {
            contactNotificationService.createNotification(liteContact, ContactNotificationType.HOME_PHONE,
                accountDto.getHomePhone());
        }

        if (StringUtils.isNotBlank(accountDto.getWorkPhone())) {
            contactNotificationService.createNotification(liteContact, ContactNotificationType.WORK_PHONE,
                accountDto.getWorkPhone());
        }

        if (StringUtils.isNotBlank(accountDto.getEmailAddress())) {
            contactNotificationService.createNotification(liteContact, ContactNotificationType.EMAIL,
                accountDto.getEmailAddress());
        }

        if (StringUtils.isNotBlank(accountDto.getIvrLogin())) {
            contactNotificationService.createNotification(liteContact, ContactNotificationType.IVR_LOGIN,
                accountDto.getIvrLogin());
        }

        if (StringUtils.isNotBlank(accountDto.getVoicePIN())) {
            contactNotificationService.createNotification(liteContact, ContactNotificationType.VOICE_PIN,
                accountDto.getVoicePIN());
        }

        // Create the customer
        LiteCustomer liteCustomer = new LiteCustomer();
        liteCustomer.setPrimaryContactID(liteContact.getContactID());
        if (accountDto.getIsCommercial()) {
            liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_CI);
        } else {
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
        String tempUnit = ecSettingDao.getEnum(EnergyCompanySettingType.DEFAULT_TEMPERATURE_UNIT,
            TemperatureUnit.class, ec.getId()).getLetter();
        liteCustomer.setTemperatureUnit(tempUnit);
        liteCustomer.setEnergyCompanyID(ec.getId());
        customerDao.addCustomer(liteCustomer);
        dbChangeManager.processDbChange(liteCustomer.getLiteID(), DBChangeMsg.CHANGE_CUSTOMER_DB,
            DBChangeMsg.CAT_CUSTOMER, DBChangeMsg.CAT_CUSTOMER, DbChangeType.ADD);

        // Create commercial/industrial customer if company was passed
        if (liteCustomer.getCustomerTypeID() == CustomerTypes.CUSTOMER_CI) {
            LiteAddress companyAddress = new LiteAddress();
            if (streetAddress != null) {
                validateAddress(accountNumber, streetAddress, "streetAddress");
                setAddressFieldsFromDTO(companyAddress, streetAddress);
            } else {
                setAddressDefaults(companyAddress);
            }
            addressDao.add(companyAddress);

            LiteCICustomer liteCICustomer = new LiteCICustomer(liteCustomer.getCustomerID());
            liteCICustomer.setMainAddressID(companyAddress.getAddressID());
            liteCICustomer.setDemandLevel(CtiUtilities.NONE_ZERO_ID);
            liteCICustomer.setCurtailAmount(CtiUtilities.NONE_ZERO_ID);
            liteCICustomer.setCompanyName(accountDto.getCompanyName());
            if (accountDto.getCommercialTypeEntryId() != null) {
                liteCICustomer.setCICustType(accountDto.getCommercialTypeEntryId());
            } else {
                liteCICustomer.setCICustType(YukonListEntryTypes.CUSTOMER_TYPE_COMMERCIAL);
            }
            liteCICustomer.setEnergyCompanyID(ec.getId());
            customerDao.addCICustomer(liteCICustomer);
            dbChangeManager.processDbChange(liteCustomer.getLiteID(), DBChangeMsg.CHANGE_CUSTOMER_DB,
                DBChangeMsg.CAT_CI_CUSTOMER, DBChangeMsg.CAT_CI_CUSTOMER, DbChangeType.ADD);
        }

        // Create service info
        LiteSiteInformation liteSiteInformation = new LiteSiteInformation();
        if (StringUtils.isNotEmpty(accountDto.getSiteInfo().getSubstationName())) {
            try {
                int subId = siteInformationDao.getSubstationIdByName(accountDto.getSiteInfo().getSubstationName());
                liteSiteInformation.setSubstationID(subId);
            } catch (NotFoundException e) {
                log.error("Unable to find substation by name: " + accountDto.getSiteInfo().getSubstationName());
                throw new InvalidSubstationNameException("Unable to find substation by name: "
                    + accountDto.getSiteInfo().getSubstationName());
            }
        }
        liteSiteInformation.setFeeder(accountDto.getSiteInfo().getFeeder());
        liteSiteInformation.setPole(accountDto.getSiteInfo().getPole());
        liteSiteInformation.setTransformerSize(accountDto.getSiteInfo().getTransformerSize());
        liteSiteInformation.setServiceVoltage(accountDto.getSiteInfo().getServiceVoltage());
        siteInformationDao.add(liteSiteInformation);

        // Create account site
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
        accountSite.setPropertyNotes(updatableAccount.getAccountDto().getPropertyNotes() == null
            ? CtiUtilities.STRING_NONE : updatableAccount.getAccountDto().getPropertyNotes());
        accountSiteDao.add(accountSite);

        // Create customer account
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setAccountSiteId(accountSite.getAccountSiteId());
        customerAccount.setAccountNumber(accountNumber);
        customerAccount.setCustomerId(liteCustomer.getCustomerID());
        customerAccount.setAccountNotes(updatableAccount.getAccountDto().getAccountNotes() == null
            ? CtiUtilities.STRING_NONE : updatableAccount.getAccountDto().getAccountNotes());
        if (liteBillingAddress != null) {
            customerAccount.setBillingAddressId(liteBillingAddress.getAddressID());
        }
        customerAccountDao.add(customerAccount);
        dbChangeManager.processDbChange(customerAccount.getAccountId(), DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
            DBChangeMsg.CAT_CUSTOMER_ACCOUNT, DBChangeMsg.CAT_CUSTOMER_ACCOUNT, DbChangeType.ADD);

        // Add mapping
        ECToAccountMapping ecToAccountMapping = new ECToAccountMapping();
        ecToAccountMapping.setEnergyCompanyId(ec.getId());
        ecToAccountMapping.setAccountId(customerAccount.getAccountId());
        ecMappingDao.addECToAccountMapping(ecToAccountMapping);
        log.info("Account: " + accountNumber + " added successfully.");

        accountEventLogService.accountAdded(operator, accountNumber);

        return customerAccount.getAccountId();
    }

    @Override
    @Transactional
    public void deleteAccount(int accountId, LiteYukonUser user) {
        CustomerAccount account = customerAccountDao.getById(accountId);
        deleteAccount(account, user);
    }

    @Override
    @Transactional
    public void deleteAccount(String accountNumber, LiteYukonUser user) {
        YukonEnergyCompany yukonEnergyCompany = ecDao.getEnergyCompanyByOperator(user);
        deleteAccount(accountNumber, user, yukonEnergyCompany);
    }
    
    @Override
    @Transactional
    public void deleteAccount(String accountNumber, LiteYukonUser user,YukonEnergyCompany energyCompany){
        try {
            CustomerAccount account =
                customerAccountDao.getByAccountNumber(accountNumber, energyCompany.getEnergyCompanyId());
            deleteAccount(account, user);
        } catch (NotFoundException e) {
            log.error("Account " + accountNumber + " could not be deleted: Unable to find account for account#: "
                + accountNumber);
            throw new InvalidAccountNumberException("Unable to find account for account#: " + accountNumber, e);
        }
    }

    private void deleteAccount(CustomerAccount account, LiteYukonUser user) {
        log.info("Deleting account id# " + account.getAccountId());
        LiteAccountInfo customerInfo = starsCustAccountInformationDao.getByAccountId(account.getAccountId());
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(account.getAccountSiteId());
        LiteSiteInformation siteInfo = siteInformationDao.getSiteInfoById(accountSite.getSiteInformationId());
        LiteCustomer liteCustomer = customerDao.getLiteCustomer(account.getCustomerId());
        customerInfo.setCustomer(liteCustomer);
        LiteAddress billingAddress = addressDao.getByAddressId(account.getBillingAddressId());
        LiteContact primaryContact = customerDao.getPrimaryContact(account.getCustomerId());
        LiteStarsEnergyCompany starsEnergyCompany = ecMappingDao.getCustomerAccountEC(account.getAccountId());
        Integer userId = primaryContact.getLoginID();

        // Delete Hardware info
        // This also handles unenrolling inventory as well.
        // clearLMHardwareInfo clears the operator side enrollment and the unenrollHardware method
        // cleans up the consumer side enrollment
        List<Integer> inventoryIds = inventoryDao.getInventoryIdsByAccount(account.getAccountId());
        Set<InventoryIdentifier> inventory = inventoryDao.getYukonInventory(inventoryIds);
        boolean deleteFromNest = inventory.stream().anyMatch(identifier -> identifier.getHardwareType().isNest());
        
        for (Integer inventoryId : inventoryIds) {
            log.info("Clearing LMHardwareInfo and unenrolling hardware for inventory id# " + inventoryId);
            hardwareBaseDao.clearLMHardwareInfo(inventoryId);
            lmHardwareControlGroupDao.unenrollHardware(inventoryId);
            InventoryIdentifier identifier = inventoryDao.getYukonInventory(inventoryId);
            if (identifier.getHardwareType().isNest()) {
                try {
                    hardwareService.deleteHardware(user, true, inventoryId);
                } catch (NotFoundException | PersistenceException | CommandCompletionException | SQLException e) {
                    log.error("Error deleting nest device, when deleting account " + e);
                }
            }
        }

        // Delete Program info
        log.info("Deleting Program info for account id# " + account.getAccountId());
        lmProgramEventDao.deleteProgramEventsForAccount(account.getAccountId());

        // Delete Appliance info
        log.info("Deleting Appliances for account id# " + account.getAccountId());
        applianceDao.deleteAppliancesByAccountId(account.getAccountId());

        // Delete WorkOrders
        log.info("Deleting Work Orders for account id# " + account.getAccountId());
        workOrderDao.deleteByAccount(account.getAccountId());

        // Delete CallReports
        log.info("Deleting call reports for account id# " + account.getAccountId());
        callReportDao.deleteAllCallsByAccount(account.getAccountId());

        // Delete thermostat schedules for account
        log.info("Deleting thermostat schedules for account id# " + account.getAccountId());
        accountThermostatScheduleDao.deleteAllByAccountId(account.getAccountId());

        // Delete account mappings
        log.info("Deleting EC to account mappings for account id# " + account.getAccountId());
        ecMappingDao.deleteECToAccountMapping(account.getAccountId());

        // Delete account events
        log.info("Deleting events for account id# " + account.getAccountId());
        eventAccountDao.deleteAllEventsForAccount(account.getAccountId());

        // Delete customer account
        log.info("Deleting customer account " + account.getAccountId());
        customerAccountDao.remove(account);

        // Delete account site
        log.info("Deleting account site id# " + accountSite.getAccountSiteId());
        accountSiteDao.remove(accountSite);

        // Delete site information
        log.info("Deleting site info id# " + siteInfo.getSiteID());
        siteInformationDao.delete(siteInfo);

        // Delete billing address
        if (billingAddress.getAddressID() != CtiUtilities.NONE_ZERO_ID) {
            log.info("Deleting billing address id# " + billingAddress.getAddressID());
            addressDao.remove(billingAddress);
        }

        // Delete customer
        // - handles contact deletion
        customerDao.deleteCustomer(account.getCustomerId());
        dbChangeManager.processDbChange(liteCustomer.getLiteID(), DBChangeMsg.CHANGE_CUSTOMER_DB,
            DBChangeMsg.CAT_CUSTOMER, DBChangeMsg.CAT_CUSTOMER, DbChangeType.DELETE);

        // Delete login
        if (!UserUtils.isReservedUserId(userId)) {
            log.info("Deleting login and removing from starsDatabaseCache id# " + userId);
            LiteYukonUser liteUser = userDao.getLiteYukonUser(userId);
            userDao.deleteUser(userId);
            usersEventLogService.userDeleted(liteUser.getUsername(), user);
            starsDatabaseCache.deleteStarsYukonUser(userId);
        }

        // Delete customer info
        starsEnergyCompany.deleteCustAccountInformation(customerInfo);
        dbChangeManager.processDbChange(customerInfo.getLiteID(), DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
            DBChangeMsg.CAT_CUSTOMER_ACCOUNT, DBChangeMsg.CAT_CUSTOMER_ACCOUNT, DbChangeType.DELETE);

        log.info("Account: " + account.getAccountNumber() + " deleted successfully.");

        accountEventLogService.accountDeleted(user, account.getAccountNumber());
        
        if (deleteFromNest) {
            nestService.dissolveAccountWithNest(liteCustomer, account.getAccountNumber());
        }
    }
    
    @Override
    @Transactional
    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user)
            throws InvalidAccountNumberException {
        YukonEnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(user);
        updateAccount(updatableAccount, user, energyCompany);
    }

    @Override
    @Transactional
    public void updateAccount(UpdatableAccount updatableAccount, LiteYukonUser user, YukonEnergyCompany energyCompany) {

        int energyCompanyId = energyCompany.getEnergyCompanyId();
        String accountNumber = updatableAccount.getAccountNumber();

        CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber, energyCompanyId);
        int accountId = account.getAccountId();

        updateAccount(updatableAccount, accountId, user);
    }

    // UPDATE ACCOUNT
    @Transactional
    @Override
    public void updateAccount(UpdatableAccount updatableAccount, int accountId, LiteYukonUser user) {

        YukonEnergyCompany energyCompanyOfAccount = ecDao.getEnergyCompanyByAccountId(accountId);
        AccountDto accountDto = updatableAccount.getAccountDto();
        String accountNumber = updatableAccount.getAccountNumber();
        String username = accountDto.getUserName();

        // Check for account info errors.
        if (StringUtils.isBlank(accountNumber)) {
            log.error("Account " + accountNumber
                + " could not be updated: The provided account number cannot be empty.");
            throw new InvalidAccountNumberException("The provided account number cannot be empty.");

        }

        CustomerAccount account = customerAccountDao.getById(accountId);

        // If updating the account number, verify the new one is available
        boolean accountNumberUpdate = false;
        String updatedAccountNumber = accountDto.getAccountNumber();
        if (StringUtils.isNotBlank(updatedAccountNumber) && !updatedAccountNumber.equals(accountNumber)) {

            try {
                CustomerAccount customerAccount =
                    customerAccountDao.getByAccountNumber(updatedAccountNumber,
                        energyCompanyOfAccount.getEnergyCompanyId());
                if (customerAccount != null) {
                    log.error("Account " + accountNumber
                        + " could not be updated: The provided new account number already exists ("
                        + updatedAccountNumber + ").");
                    throw new AccountNumberUnavailableException("The provided new account number already exists ("
                        + updatedAccountNumber + ").");
                }
            } catch (NotFoundException e) {
                // Ignore; new account number is available.
            }

            accountNumberUpdate = true;
        }

        // Check for login info errors.
        LiteYukonUser tempUser = userDao.findUserByUsername(username);
        if (tempUser != null) {
            List<LiteContact> tempContacts = contactDao.getContactsByLoginId(tempUser.getUserID());
            boolean ourUsername = false;
            for (LiteContact tempContact : tempContacts) {
                CustomerAccount tempAccount = customerAccountDao.getAccountByContactId(tempContact.getContactID());
                if (tempAccount.getAccountNumber().equalsIgnoreCase(accountNumber)) {
                    ourUsername = true;
                    break;
                }
            }
            if (!ourUsername) {
                log.error("Account " + accountNumber + " could not be updated: The provided username already in use.");
                throw new UserNameUnavailableException("The provided username already in use.");
            }
        }

        LiteCustomer liteCustomer = customerDao.getLiteCustomer(account.getCustomerId());
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(account.getAccountSiteId());
        LiteSiteInformation liteSiteInformation =
            siteInformationDao.getSiteInfoById(accountSite.getSiteInformationId());
        LiteAddress liteStreetAddress = addressDao.getByAddressId(accountSite.getStreetAddressId());
        LiteAddress liteBillingAddress = addressDao.getByAddressId(account.getBillingAddressId());
        LiteContact primaryContact = customerDao.getPrimaryContact(account.getCustomerId());
        Address streetAddress = accountDto.getStreetAddress();
        Address billingAddress = accountDto.getBillingAddress();

        // RULES FOR LOGIN UPDATE/CREATION
        // LOGIN EXISTS
        // Update the login, if no password is specified don't change the Authtype or password.
        // If any password is specified, set the AuthType to the default AuthType. If it was empty
        // set the password to a space(done by doa).
        //
        // LOGIN DOESN'T EXIST
        // If the account has no login, create one. If no password is specified set the
        // AuthType to NONE(No Login) and set the password to a space(done by dao).
        // If any password is specified set AuthType to default AuthType. If it was empty set it
        // to a space(done by dao).
        int newLoginId = UserUtils.USER_NONE_ID;
        AuthenticationCategory defaultAuthenticationCategory = authenticationService.getDefaultAuthenticationCategory();
        if (StringUtils.isNotBlank(username)) {
            LiteYukonUser login = userDao.getLiteYukonUser(primaryContact.getLoginID());
            if (login != null && login.getUserID() != UserUtils.USER_NONE_ID) {
                // Update their login info.
                int userGroupId = login.getUserGroupId();
                if (accountDto.getUserGroup() != null) {
                    updateUserGroup(login, accountDto.getUserGroup(), accountNumber);
                }
                
                    login.setUsername(username);
                String userGroupName = userGroupDao.getLiteUserGroup(userGroupId).getUserGroupName();
                userDao.update(login);
                usersEventLogService.userUpdated(username, userGroupName, energyCompanyOfAccount.getName(), login.getLoginStatus(), user);
                if (userGroupId != login.getUserGroupId()) {
                    usersEventLogService.userRemoved(user.getUsername(), userGroupName, user);
                    if (login.getUserGroupId() != null) {
                        LiteUserGroup addedToUserGroup = userGroupDao.getLiteUserGroup(user.getUserGroupId());
                        usersEventLogService.userAdded(user.getUsername(),
                            addedToUserGroup.getUserGroupName(), user);
                    }
                }

                String password = accountDto.getPassword();
                if (password != null) {
                    if (authenticationService.supportsPasswordSet(defaultAuthenticationCategory)) {
                        authenticationService.setPassword(login, defaultAuthenticationCategory,
                            SqlUtils.convertStringToDbValue(password), user);
                        /*
                         * This is to force password reset if the user is updated during the account update from
                         * Account Importer in Bulk Operations
                         */
                        if (accountDto.getForcePasswordReset().equals(YNBoolean.YES)) {
                            yukonUserPasswordDao.setForceResetForUser(login, YNBoolean.YES);
                        }
                    } else {
                        authenticationService.setAuthenticationCategory(login, defaultAuthenticationCategory);
                    }
                }
            } else {
                // Create a new login.
                // This is not preferred, but until we correctly rewrite a way to "create" a YukonUser this is
                // how it will be.
                LiteYukonUser newUser = new LiteYukonUser(LiteYukonUser.CREATE_NEW_USER_ID, accountDto.getUserName(),
                        LoginStatusEnum.ENABLED);

                if (accountDto.getUserGroup() != null) {
                    updateUserGroup(newUser, accountDto.getUserGroup(), accountNumber);
                }

                userDao.save(newUser);
                usersEventLogService.userCreated(user.getUsername(), accountDto.getUserGroup(), energyCompanyOfAccount.getName(), newUser.getLoginStatus() , user);
                String password = accountDto.getPassword();
                if (!StringUtils.isBlank(password)) {
                    authenticationService.setPassword(newUser, defaultAuthenticationCategory, password, user);
                }

                newLoginId = newUser.getUserID();
                dbChangeManager.processDbChange(newUser.getLiteID(), DBChangeMsg.CHANGE_YUKON_USER_DB,
                    DBChangeMsg.CAT_YUKON_USER, DBChangeMsg.CAT_YUKON_USER, DbChangeType.ADD);
            }
        }

        // Update the address
        if (streetAddress != null) {
            validateAddress(accountNumber, streetAddress, "streetAddress");
            setAddressFieldsFromDTO(liteStreetAddress, streetAddress);
            addressDao.update(liteStreetAddress);
        }
        // Update the billing address if supplied
        if (billingAddress != null) {
            validateAddress(accountNumber, billingAddress, "billingAddress");
            setAddressFieldsFromDTO(liteBillingAddress, billingAddress);
            addressDao.update(liteBillingAddress);
        }

        // Update the customer account
        if (accountNumberUpdate) {
            account.setAccountNumber(updatedAccountNumber);
        } else {
            account.setAccountNumber(accountNumber);
        }
        account.setAccountNotes(accountDto.getAccountNotes());

        customerAccountDao.update(account);
        dbChangeManager.processDbChange(account.getAccountId(), DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
            DBChangeMsg.CAT_CUSTOMER_ACCOUNT, DBChangeMsg.CAT_CUSTOMER_ACCOUNT, DbChangeType.UPDATE);

        // Update the primary contact
        contactService.updateContact(primaryContact, accountDto.getFirstName(), accountDto.getLastName(),
            newLoginId != UserUtils.USER_NONE_ID ? newLoginId : null);

        primaryContact.setContFirstName(accountDto.getFirstName());
        primaryContact.setContLastName(accountDto.getLastName());
        if (newLoginId != UserUtils.USER_NONE_ID) {
            primaryContact.setLoginID(newLoginId);
        }
        contactDao.saveContact(primaryContact);
        dbChangeManager.processDbChange(primaryContact.getLiteID(), DBChangeMsg.CHANGE_CONTACT_DB,
            DBChangeMsg.CAT_CUSTOMERCONTACT, DBChangeMsg.CAT_CUSTOMERCONTACT, DbChangeType.UPDATE);

        // Update the notifications
        LiteContactNotification homePhoneNotif =
            contactNotificationDao.getFirstNotificationForContactByType(primaryContact,
                ContactNotificationType.HOME_PHONE);
        LiteContactNotification workPhoneNotif =
            contactNotificationDao.getFirstNotificationForContactByType(primaryContact,
                ContactNotificationType.WORK_PHONE);
        LiteContactNotification emailNotif =
            contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.EMAIL);
        LiteContactNotification ivrLoginNotif =
            contactNotificationDao.getFirstNotificationForContactByType(primaryContact,
                ContactNotificationType.IVR_LOGIN);
        LiteContactNotification voicePINNotif =
            contactNotificationDao.getFirstNotificationForContactByType(primaryContact,
                ContactNotificationType.VOICE_PIN);

        if (StringUtils.isNotBlank(accountDto.getHomePhone())) {
            String homePhone = accountDto.getHomePhone();
            if (homePhoneNotif == null) {
                contactNotificationService.createNotification(primaryContact, ContactNotificationType.HOME_PHONE,
                    homePhone);
            } else {
                homePhoneNotif.setNotification(homePhone);
                contactNotificationDao.saveNotification(homePhoneNotif);
            }
        } else {
            if (homePhoneNotif != null) {
                contactNotificationDao.removeNotification(homePhoneNotif.getContactNotifID());
            }
        }

        if (StringUtils.isNotBlank(accountDto.getWorkPhone())) {
            String workPhone = accountDto.getWorkPhone();
            if (workPhoneNotif == null) {
                contactNotificationService.createNotification(primaryContact, ContactNotificationType.WORK_PHONE,
                    workPhone);
            } else {
                workPhoneNotif.setNotification(workPhone);
                contactNotificationDao.saveNotification(workPhoneNotif);
            }
        } else {
            if (workPhoneNotif != null) {
                contactNotificationDao.removeNotification(workPhoneNotif.getContactNotifID());
            }
        }

        if (StringUtils.isNotBlank(accountDto.getEmailAddress())) {
            String email = accountDto.getEmailAddress();
            if (emailNotif == null) {
                contactNotificationService.createNotification(primaryContact, ContactNotificationType.EMAIL, email);
            } else {
                emailNotif.setNotification(email);
                contactNotificationDao.saveNotification(emailNotif);
            }
        } else {
            if (emailNotif != null) {
                contactNotificationDao.removeNotification(emailNotif.getContactNotifID());
            }
        }

        if (StringUtils.isNotBlank(accountDto.getIvrLogin())) {
            String ivrLogin = accountDto.getIvrLogin();
            if (ivrLoginNotif == null) {
                contactNotificationService.createNotification(primaryContact, ContactNotificationType.IVR_LOGIN,
                    ivrLogin);
            } else {
                ivrLoginNotif.setNotification(ivrLogin);
                contactNotificationDao.saveNotification(ivrLoginNotif);
            }
        } else {
            if (ivrLoginNotif != null) {
                contactNotificationDao.removeNotification(ivrLoginNotif.getContactNotifID());
            }
        }

        if (StringUtils.isNotBlank(accountDto.getVoicePIN())) {
            String voicePin = accountDto.getVoicePIN();
            if (voicePINNotif == null) {
                contactNotificationService.createNotification(primaryContact, ContactNotificationType.VOICE_PIN,
                    voicePin);
            } else {
                voicePINNotif.setNotification(voicePin);
                contactNotificationDao.saveNotification(voicePINNotif);
            }
        } else {
            if (voicePINNotif != null) {
                contactNotificationDao.removeNotification(voicePINNotif.getContactNotifID());
            }
        }

        // Update the customer
        liteCustomer.setAltTrackingNumber(accountDto.getAltTrackingNumber());
        if (accountDto.getCustomerNumber() != null) {
            liteCustomer.setCustomerNumber(accountDto.getCustomerNumber());
        }
        if (accountDto.getRateScheduleEntryId() != null) {
            liteCustomer.setRateScheduleID(accountDto.getRateScheduleEntryId());
        }

        if (customerDao.isCICustomer(liteCustomer.getCustomerID())) {
            if (!accountDto.getIsCommercial()) {
                // was commercial, not anymore
                customerDao.deleteCICustomer(liteCustomer.getCustomerID());
                liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_RESIDENTIAL);
                customerDao.updateCustomer(liteCustomer);

                // Log customer type change
                accountEventLogService.customerTypeChanged(user, accountNumber, CustomerTypes.STRING_CI_CUSTOMER,
                    CustomerTypes.STRING_RES_CUSTOMER);
            } else {
                // was commercial and still is
                LiteCICustomer liteCICustomer = customerDao.getLiteCICustomer(liteCustomer.getCustomerID());
                String oldCompanyName = liteCICustomer.getCompanyName();
                liteCICustomer.setCompanyName(accountDto.getCompanyName());
                if (accountDto.getCommercialTypeEntryId() != null) {
                    liteCICustomer.setCICustType(accountDto.getCommercialTypeEntryId());
                }
                customerDao.updateCICustomer(liteCICustomer);
                customerDao.updateCustomer(liteCustomer);

                if (!oldCompanyName.equalsIgnoreCase(accountDto.getCompanyName())) {
                    accountEventLogService.companyNameChanged(user, accountNumber, oldCompanyName,
                        accountDto.getCompanyName());
                }
            }
        } else {
            if (accountDto.getIsCommercial()) {
                // was residential, now commercial
                LiteAddress companyAddress = new LiteAddress();
                if (streetAddress != null) {
                    validateAddress(accountNumber, streetAddress, "streetAddress");
                    setAddressFieldsFromDTO(companyAddress, streetAddress);
                } else {
                    setAddressDefaults(companyAddress);
                }
                addressDao.add(companyAddress);

                liteCustomer.setCustomerTypeID(CustomerTypes.CUSTOMER_CI);
                customerDao.updateCustomer(liteCustomer);

                LiteCICustomer liteCICustomer = new LiteCICustomer(liteCustomer.getCustomerID());
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
                liteCICustomer.setEnergyCompanyID(energyCompanyOfAccount.getEnergyCompanyId());
                customerDao.addCICustomer(liteCICustomer);

                // Log customer type change
                accountEventLogService.customerTypeChanged(user, accountNumber, CustomerTypes.STRING_RES_CUSTOMER,
                    CustomerTypes.STRING_CI_CUSTOMER);
            } else {
                customerDao.updateCustomer(liteCustomer);
            }
        }

        dbChangeManager.processDbChange(liteCustomer.getLiteID(), DBChangeMsg.CHANGE_CUSTOMER_DB,
            DBChangeMsg.CAT_CUSTOMER, DBChangeMsg.CAT_CUSTOMER, DbChangeType.UPDATE);

        // Update account site
        accountSite.setSiteNumber(accountDto.getMapNumber());
        if (accountDto.getCustomerStatus() != null) {
            accountSite.setCustomerStatus(accountDto.getCustomerStatus());
        }
        if (accountDto.getIsCustAtHome() != null) {
            accountSite.setCustAtHome(BooleanUtils.toString(accountDto.getIsCustAtHome(), "Y", "N"));
        }
        accountSite.setPropertyNotes(accountDto.getPropertyNotes());
        accountSiteDao.update(accountSite);

        // Update site information
        if (StringUtils.isNotEmpty(accountDto.getSiteInfo().getSubstationName())) {
            try {
                int subId = siteInformationDao.getSubstationIdByName(accountDto.getSiteInfo().getSubstationName());
                liteSiteInformation.setSubstationID(subId);
            } catch (NotFoundException e) {
                log.error("Unable to find substation by name: " + accountDto.getSiteInfo().getSubstationName(), e);
                throw new InvalidSubstationNameException("Unable to find substation by name: "
                    + accountDto.getSiteInfo().getSubstationName());
            }
        }
        liteSiteInformation.setFeeder(accountDto.getSiteInfo().getFeeder());
        liteSiteInformation.setPole(accountDto.getSiteInfo().getPole());
        liteSiteInformation.setTransformerSize(accountDto.getSiteInfo().getTransformerSize());
        liteSiteInformation.setServiceVoltage(accountDto.getSiteInfo().getServiceVoltage());
        siteInformationDao.update(liteSiteInformation);

        // Update mapping
        ecMappingDao.updateECToAccountMapping(account.getAccountId(), energyCompanyOfAccount.getEnergyCompanyId());

        // Update Login
        log.info("Account: " + accountNumber + " updated successfully.");

        if (!updatableAccount.getAccountNumber().equalsIgnoreCase(account.getAccountNumber())) {
            accountEventLogService.accountNumberChanged(user, account.getAccountNumber(),
                updatableAccount.getAccountNumber());
        }

        accountEventLogService.accountUpdated(user, accountNumber);
    }

    @Override
    public AccountDto getAccountDto(String accountNumber, LiteYukonUser user) {
        YukonEnergyCompany ec = ecDao.getEnergyCompanyByOperator(user);
        CustomerAccount customerAccount = getCustomerAccountForAccountNumberAndEnergyCompany(accountNumber, ec);

        YukonUserContext userContext = userContextService.getEnergyCompanyDefaultUserContext(ec.getEnergyCompanyUser());

        return getAccountDto(customerAccount, userContext);
    }

    @Override
    public AccountDto getAccountDto(String accountNumber, YukonEnergyCompany ec) {
        YukonUserContext userContext = userContextService.getEnergyCompanyDefaultUserContext(ec.getEnergyCompanyUser());

        CustomerAccount customerAccount = getCustomerAccountForAccountNumberAndEnergyCompany(accountNumber, ec);
        return getAccountDto(customerAccount, userContext);
    }
    
    @Override
    public AccountDto getAccountDto(int accountId, int energyCompanyId) {
        EnergyCompany ec = ecDao.getEnergyCompany(energyCompanyId);
        YukonUserContext userContext = userContextService.getEnergyCompanyDefaultUserContext(ec.getUser());

        CustomerAccount customerAccount = getCustomerAccountForAccountId(accountId);
        return getAccountDto(customerAccount, userContext);
    }

    @Override
    public AccountDto getAccountDto(int accountId, int energyCompanyId, YukonUserContext userContext) {
        CustomerAccount customerAccount = getCustomerAccountForAccountId(accountId);
        return getAccountDto(customerAccount, userContext);
    }

    private AccountDto getAccountDto(CustomerAccount customerAccount, YukonUserContext userContext) {
        AccountDto retrievedDto = new AccountDto();

        AccountSite accountSite = accountSiteDao.getByAccountSiteId(customerAccount.getAccountSiteId());
        LiteSiteInformation siteInfo = siteInformationDao.getSiteInfoById(accountSite.getSiteInformationId());
        LiteCustomer customer = customerDao.getLiteCustomer(customerAccount.getCustomerId());
        LiteContact primaryContact = contactDao.getContact(customer.getPrimaryContactID());

        retrievedDto.setAccountNumber(customerAccount.getAccountNumber());
        retrievedDto.setFirstName(primaryContact != null ? primaryContact.getContFirstName() : "");
        retrievedDto.setLastName(primaryContact != null ? primaryContact.getContLastName() : "");

        // Customer
        retrievedDto.setAltTrackingNumber(stripNone(customer.getAltTrackingNumber()));
        retrievedDto.setCustomerNumber(stripNone(customer.getCustomerNumber()));
        retrievedDto.setRateScheduleEntryId(customer.getRateScheduleID());
        if (customer instanceof LiteCICustomer) {
            retrievedDto.setCompanyName(((LiteCICustomer) customer).getCompanyName());
            retrievedDto.setIsCommercial(true);
            retrievedDto.setCommercialTypeEntryId(((LiteCICustomer) customer).getCICustType());
        } else {
            retrievedDto.setCompanyName("");
            retrievedDto.setIsCommercial(false);
            retrievedDto.setCommercialTypeEntryId(null);
        }
        retrievedDto.setAccountNotes(customerAccount.getAccountNotes());

        // Notifications
        LiteContactNotification homePhoneNotif =
            contactNotificationDao.getFirstNotificationForContactByType(primaryContact,
                ContactNotificationType.HOME_PHONE);
        LiteContactNotification workPhoneNotif =
            contactNotificationDao.getFirstNotificationForContactByType(primaryContact,
                ContactNotificationType.WORK_PHONE);
        LiteContactNotification emailNotif =
            contactNotificationDao.getFirstNotificationForContactByType(primaryContact, ContactNotificationType.EMAIL);
        if (homePhoneNotif != null) {
            String homePhone = homePhoneNotif.getNotification();
            homePhone = phoneNumberFormattingService.formatPhoneNumber(homePhone, userContext);
            retrievedDto.setHomePhone(homePhone);
        } else {
            retrievedDto.setHomePhone("");
        }
        if (workPhoneNotif != null) {
            String workPhone = workPhoneNotif.getNotification();
            workPhone = phoneNumberFormattingService.formatPhoneNumber(workPhone, userContext);
            retrievedDto.setWorkPhone(workPhone);
        } else {
            retrievedDto.setWorkPhone("");
        }
        if (emailNotif != null) {
            retrievedDto.setEmailAddress(emailNotif.getNotification());
        } else {
            retrievedDto.setEmailAddress("");
        }

        // Addresses
        LiteAddress address = addressDao.getByAddressId(accountSite.getStreetAddressId());
        giveAddressFieldsToDTO(address, retrievedDto.getStreetAddress());
        LiteAddress billingAdress = addressDao.getByAddressId(customerAccount.getBillingAddressId());
        giveAddressFieldsToDTO(billingAdress, retrievedDto.getBillingAddress());
        retrievedDto.setPropertyNotes(accountSite.getPropertyNotes());

        if (primaryContact != null && primaryContact.getLoginID() > UserUtils.USER_NONE_ID) {
            LiteYukonUser user = userDao.getLiteYukonUser(primaryContact.getLoginID());
            retrievedDto.setUserName(user.getUsername());

            if (user.getUserGroupId() != null) {
                LiteUserGroup userGroup = userGroupDao.getLiteUserGroup(user.getUserGroupId());
                retrievedDto.setUserGroup(userGroup.getUserGroupName());
            }
        } else {
            retrievedDto.setUserName("");
            retrievedDto.setUserGroup(null);
        }

        // Account site
        retrievedDto.setMapNumber(accountSite.getSiteNumber());
        retrievedDto.setCustomerStatus(accountSite.getCustomerStatus());
        retrievedDto.setIsCustAtHome(accountSite.getCustAtHome().equals("N") ? false : true);
        String subName = null;
        try {
            subName = siteInformationDao.getSubstationNameById(siteInfo.getSubstationID());
        } catch (NotFoundException nfe) {
            subName = "";
        }
        retrievedDto.getSiteInfo().setSubstationName(subName);
        retrievedDto.getSiteInfo().setFeeder(siteInfo.getFeeder());
        retrievedDto.getSiteInfo().setPole(siteInfo.getPole());
        retrievedDto.getSiteInfo().setTransformerSize(siteInfo.getTransformerSize());
        retrievedDto.getSiteInfo().setServiceVoltage(siteInfo.getServiceVoltage());

        return retrievedDto;
    }

    private CustomerAccount getCustomerAccountForAccountNumberAndEnergyCompany(String accountNumber,
        YukonEnergyCompany ec) {

        CustomerAccount customerAccount = null;
        try {
            customerAccount = customerAccountDao.getByAccountNumber(accountNumber, ec.getEnergyCompanyId());
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
        liteAddress.setLocationAddress1("");
        liteAddress.setLocationAddress2("");
        liteAddress.setCityName("");
        liteAddress.setCounty("");
        liteAddress.setStateCode("  ");
        liteAddress.setZipCode("");
    }

    public static void setAddressFieldsFromDTO(LiteAddress lite, Address address) {
        lite.setLocationAddress1(StringUtils.defaultString(address.getLocationAddress1()));
        lite.setLocationAddress2(StringUtils.defaultString(address.getLocationAddress2()));
        lite.setCityName(StringUtils.defaultString(address.getCityName()));
        lite.setStateCode(StringUtils.defaultString(address.getStateCode()));
        lite.setZipCode(StringUtils.defaultString(address.getZipCode()));
        lite.setCounty(StringUtils.defaultString(address.getCounty()));
    }

    // when extracting lets turn "(none)"s into blank strings even though they may turn back into "(none)"s if
    // they are re-saved without being set
    private void giveAddressFieldsToDTO(LiteAddress lite, Address address) {
        address.setLocationAddress1(stripNone(lite.getLocationAddress1()));
        address.setLocationAddress2(stripNone(lite.getLocationAddress2()));
        address.setCityName(stripNone(lite.getCityName()));
        address.setStateCode(stripNone(lite.getStateCode()));
        address.setZipCode(stripNone(lite.getZipCode()));
        address.setCounty(stripNone(lite.getCounty()));
    }

    private String stripNone(String value) {
        return CtiUtilities.STRING_NONE.equals(value) ? "" : value;
    }

    /**
     * This method tries to update the user's user group to the user group name supplied. This method will
     * throw a
     * InvalidLoginGroupException if the user group does not exist.
     * 
     * @throws InvalidLoginGroupException - The user group name supplied does not exist.
     */
    private void updateUserGroup(LiteYukonUser user, String userGroupName, String accountNumber)
            throws InvalidLoginGroupException {
        try {
            LiteUserGroup userGroup = userGroupDao.getLiteUserGroupByUserGroupName(userGroupName);
            user.setUserGroupId(userGroup.getUserGroupId());
        } catch (NotFoundException e) {
            log.error("Account " + accountNumber + " could not be updated: The provided user group '" + userGroupName
                + "' doesn't exist.");
            throw new InvalidLoginGroupException("The provided user group '" + userGroupName + "' doesn't exist.");
        }
    }

    /**
     * This method validates the given address
     * 
     * @throws InvalidAddressException - The address is not valid.
     */
    private void validateAddress(String accountNumber, Address address, String addressType) {
        AddressValidator addressValidator = new AddressValidator();
        Errors errors = new BeanPropertyBindingResult(address, addressType);
        addressValidator.validate(address, errors);
        if (errors.getErrorCount() != 0) {
            log.error("Address for account " + accountNumber
                + " could not be updated: The address provided is invalid.");
            throw new InvalidAddressException("The address provided is invalid.");
        }
    }
}
