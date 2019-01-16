package com.cannontech.stars.dr.account.service;

import static org.easymock.EasyMock.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.IMockBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.loggers.SystemEventLogService;
import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.AuthenticationService;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.users.dao.UserGroupDao;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteSiteInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.AccountDtoMockFactory;
import com.cannontech.stars.dr.account.ContactMockFactory;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CallReportDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ECToAccountMapping;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.impl.AccountServiceImpl;
import com.cannontech.stars.dr.adapters.ContactServiceAdapter;
import com.cannontech.stars.dr.adapters.EnergyCompanyDaoAdapter;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.event.dao.EventAccountDao;
import com.cannontech.stars.dr.event.dao.LMProgramEventDao;
import com.cannontech.stars.dr.general.service.ContactNotificationService;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.stars.energyCompany.model.EnergyCompany.Builder;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class AccountServiceTest extends EasyMockSupport {
    
    // Class under test
    private AccountServiceImpl accountService;

    // collaborators to be mocked...and ridiculed
    private AuthenticationService authenticationServiceMock;
    private YukonUserDao userDaoMock;
    private UserGroupDao userGroupDaoMock;
    private EnergyCompanySettingDao ecSettingDaoMock;
    private AuthDao authDaoMock;
    private AddressDao addressDaoMock;
    private ContactNotificationService contactNotificationServiceMock;
    private CustomerDao customerDaoMock;
    private SiteInformationDao siteInformationDaoMock;
    private AccountSiteDao accountSiteDaoMock;
    private CustomerAccountDao customerAccountDaoMock;
    private ECMappingDao ecMappingDaoMock;
    private InventoryDao inventoryDaoMock;
    private LmHardwareBaseDao hardwareBaseDaoMock;
    private LMProgramEventDao lmProgramEventDaoMock;
    private ApplianceDao applianceDaoMock;
    private StarsWorkOrderBaseDao workOrderDaoMock;
    private CallReportDao callReportDaoMock;
    private AccountThermostatScheduleDao accountThermostatScheduleDaoMock;
    private EventAccountDao eventAccountDaoMock;
    private StarsCustAccountInformationDao starsCustAccountInformationDaoMock;
    private DbChangeManager dbChangeManager;
    private AccountEventLogService accountEventLogServiceMock;
    private EnergyCompanyDao ecServiceMock;
    private ContactService contactServiceMock;
    private StarsDatabaseCache starsDatabaseCacheMock;
    private UsersEventLogService usersEventLogServiceMock;

    @Before
    public void setUp() throws SecurityException {
        authenticationServiceMock = createNiceMock(AuthenticationService.class);
        userDaoMock = createNiceMock(YukonUserDao.class);
        userGroupDaoMock = createNiceMock(UserGroupDao.class);
        ecSettingDaoMock = createMock(EnergyCompanySettingDao.class);
        authDaoMock = createMock(AuthDao.class);
        addressDaoMock = createMock(AddressDao.class);
        
        contactNotificationServiceMock = createNiceMock(ContactNotificationService.class);
        customerDaoMock = createMock(CustomerDao.class);
        siteInformationDaoMock = createMock(SiteInformationDao.class);
        accountSiteDaoMock = createMock(AccountSiteDao.class);
        customerAccountDaoMock = createMock(CustomerAccountDao.class);
        ecMappingDaoMock = createMock(ECMappingDao.class);
        inventoryDaoMock = createMock(InventoryDao.class);
        hardwareBaseDaoMock = createMock(LmHardwareBaseDao.class);
        lmProgramEventDaoMock = createMock(LMProgramEventDao.class);
        applianceDaoMock = createMock(ApplianceDao.class);
        workOrderDaoMock = createMock(StarsWorkOrderBaseDao.class);
        callReportDaoMock = createMock(CallReportDao.class);
        accountThermostatScheduleDaoMock = createMock(AccountThermostatScheduleDao.class);
        eventAccountDaoMock = createMock(EventAccountDao.class);
        starsCustAccountInformationDaoMock = createMock(StarsCustAccountInformationDao.class);
        dbChangeManager = createNiceMock(DbChangeManager.class);
        accountEventLogServiceMock = createMock(AccountEventLogService.class);
        usersEventLogServiceMock = createMock(UsersEventLogService.class);
        
        EnergyCompanyDao yecServiceMock = new EnergyCompanyDaoAdapter() {
            @Override
            public EnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
                Builder ecBuilder = new EnergyCompany.Builder();
                ecBuilder.addEnergyCompany(1, "test energy company", null, 1, null);
                return ecBuilder.build().get(1);
            }
            
            @Override
            public EnergyCompany getEnergyCompanyByAccountId(int accountId) {
                Builder ecBuilder = new EnergyCompany.Builder();
                ecBuilder.addEnergyCompany(1, "test energy company", null, 1, null);
                return ecBuilder.build().get(1);
            }
        };
        List<String> ecDaoMockableMethods = 
            getMethodNamesToMock(yecServiceMock.getClass(), "getEnergyCompanyByOperator", "getEnergyCompanyByAccountId");
        ecServiceMock = createPartialMock(yecServiceMock.getClass(), ecDaoMockableMethods).createMock();        
        
        ContactService contServiceMock = new ContactServiceAdapter() {
            @Override
            public LiteContact createContact(String firstName, String lastName, LiteYukonUser contactUser) {
                return ContactMockFactory.getLiteContact1();
            }

            @Override
            public void updateContact(LiteContact liteContact, String firstName, String lastName, Integer loginId) {}
        };
        List<String> contactServiceMockableMethods = 
            getMethodNamesToMock(contServiceMock.getClass(), "createContact", "updateContact");
        contactServiceMock = createPartialMock(contServiceMock.getClass(), contactServiceMockableMethods).createMock();        

        starsDatabaseCacheMock = new StarsDatabaseCache() {
            @Override
            public LiteStarsEnergyCompany getEnergyCompanyByUser(LiteYukonUser user) {
                LiteStarsEnergyCompany ec = new LiteStarsEnergyCompany() { };
                return ec;
            }
        };

        accountService = new AccountServiceImpl();
        ReflectionTestUtils.setField(accountService, "authenticationService", authenticationServiceMock);
        ReflectionTestUtils.setField(accountService, "userDao", userDaoMock);
        ReflectionTestUtils.setField(accountService, "ecSettingDao", ecSettingDaoMock);
        ReflectionTestUtils.setField(accountService, "authDao", authDaoMock);
        ReflectionTestUtils.setField(accountService, "addressDao", addressDaoMock);
        ReflectionTestUtils.setField(accountService, "contactService", contactServiceMock);
        ReflectionTestUtils.setField(accountService, "contactNotificationService", contactNotificationServiceMock);
        ReflectionTestUtils.setField(accountService, "customerDao", customerDaoMock);
        ReflectionTestUtils.setField(accountService, "siteInformationDao", siteInformationDaoMock);
        ReflectionTestUtils.setField(accountService, "accountSiteDao", accountSiteDaoMock);
        ReflectionTestUtils.setField(accountService, "customerAccountDao", customerAccountDaoMock);
        ReflectionTestUtils.setField(accountService, "ecMappingDao", ecMappingDaoMock);
        ReflectionTestUtils.setField(accountService, "inventoryDao", inventoryDaoMock);
        ReflectionTestUtils.setField(accountService, "hardwareBaseDao", hardwareBaseDaoMock);
        ReflectionTestUtils.setField(accountService, "lmProgramEventDao", lmProgramEventDaoMock);
        ReflectionTestUtils.setField(accountService, "applianceDao", applianceDaoMock);
        ReflectionTestUtils.setField(accountService, "workOrderDao", workOrderDaoMock);
        ReflectionTestUtils.setField(accountService, "callReportDao", callReportDaoMock);
        ReflectionTestUtils.setField(accountService, "accountThermostatScheduleDao", accountThermostatScheduleDaoMock);
        ReflectionTestUtils.setField(accountService, "eventAccountDao", eventAccountDaoMock);
        ReflectionTestUtils.setField(accountService, "starsCustAccountInformationDao", starsCustAccountInformationDaoMock);
        ReflectionTestUtils.setField(accountService, "dbChangeManager", dbChangeManager);
        ReflectionTestUtils.setField(accountService, "ecDao", ecServiceMock);
        ReflectionTestUtils.setField(accountService, "accountEventLogService", accountEventLogServiceMock);
        ReflectionTestUtils.setField(accountService, "starsDatabaseCache", starsDatabaseCacheMock);
        ReflectionTestUtils.setField(accountService, "userGroupDao", userGroupDaoMock);
        ReflectionTestUtils.setField(accountService, "usersEventLogService", usersEventLogServiceMock);

    }
    
    private <T> IMockBuilder<T> createPartialMock(final Class<T> toMock, List<String> ecDaoMockableMethods) {
        
        IMockBuilder<T> mockBuilder = createMockBuilder(toMock);
        for (String methodName : ecDaoMockableMethods) {
            try {
                mockBuilder.addMockedMethod(methodName);
            }catch (IllegalArgumentException e) {
                // There are several methods that could show up in this list that aren't reflection methods.  Just ignore them.
            }
        }
        
        return mockBuilder;
    }

    private List<String> getMethodNamesToMock(Class<?> mockClass, String... nonMockedMethodNames) {

        List<Method> classDeclaredMethods = Lists.newArrayList(mockClass.getClass().getDeclaredMethods());
        List<String> methodNamesToMock = Lists.transform(classDeclaredMethods, new Function<Method, String>() {

            @Override
            public String apply(Method method) {
                return method.getName();
            }
        });
        
        methodNamesToMock.removeAll(Lists.newArrayList(nonMockedMethodNames));
        return methodNamesToMock;
    }
    
    @Test
    public void testEmptyAddAccount() {

        replayAll();
        
        UpdatableAccount updatableAccount = new UpdatableAccount();
        LiteYukonUser operator = new LiteYukonUser(4, "neverAuthenticatedOperator", LoginStatusEnum.ENABLED);
        
        try {
            accountService.addAccount(updatableAccount, operator);
        }catch(InvalidAccountNumberException e) {}
        catch(AccountNumberUnavailableException e) {}
        catch(UserNameUnavailableException e) {}
    }
    
    @Test
    public void testFullAddAccount() {
        Builder ecBuilder = new EnergyCompany.Builder();
        ecBuilder.addEnergyCompany(1, "test energy company", null, 1, null);
        EnergyCompany ecMock1 =  ecBuilder.build().get(1);
        /*
         * Setup the account info
         */
        UpdatableAccount updatableAccount = new UpdatableAccount();
        AccountDto accountDto1 = AccountDtoMockFactory.getAccountDto1();
        updatableAccount.setAccountNumber(accountDto1.getAccountNumber());
        updatableAccount.setAccountDto(accountDto1);

        LiteYukonUser operatorUser = new LiteYukonUser(4, "neverAuthenticatedOperator", LoginStatusEnum.ENABLED);

        // This is not preferred, but until we correctly rewrite a way to "create" a YukonUser this is how it will be.
        LiteYukonUser newuser = new LiteYukonUser(LiteYukonUser.CREATE_NEW_USER_ID, 
        											updatableAccount.getAccountDto().getUserName(),
        											LoginStatusEnum.ENABLED);

        LiteCustomer liteCustomer = new LiteCustomer();
        liteCustomer.setCustomerNumber(CtiUtilities.STRING_NONE);
        liteCustomer.setAltTrackingNumber(updatableAccount.getAccountDto().getAltTrackingNumber());
        liteCustomer.setRateScheduleID(CtiUtilities.NONE_ZERO_ID);

        /*
         * Record what should happen
         */
        List<EnergyCompany> energyCompanyList = new ArrayList<>();
        energyCompanyList.add(ecMock1);

        expect(customerAccountDaoMock.getByAccountNumber(updatableAccount.getAccountDto().getAccountNumber(), energyCompanyList)).andReturn(null);
        expect(userDaoMock.findUserByUsername(updatableAccount.getAccountDto().getUserName())).andReturn(null);
        expect(userGroupDaoMock.getLiteUserGroupByUserGroupName(updatableAccount.getAccountDto().getUserGroup())).andReturn(new LiteUserGroup());

        userDaoMock.save(newuser);
        authenticationServiceMock.setPassword(newuser, updatableAccount.getAccountDto().getPassword(), newuser);
        
        dbChangeManager.processDbChange(newuser.getLiteID(),
                                        DBChangeMsg.CHANGE_YUKON_USER_DB,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DBChangeMsg.CAT_YUKON_USER,
                                        DbChangeType.ADD);
        expect(addressDaoMock.add(new LiteAddress())).andReturn(true);
        expect(addressDaoMock.add(new LiteAddress())).andReturn(true);
        expect(contactServiceMock.createContact(updatableAccount.getAccountDto().getFirstName(), 
                                                updatableAccount.getAccountDto().getLastName(), newuser));
        expect(authenticationServiceMock.getDefaultAuthType()).andReturn(AuthType.NONE);
        dbChangeManager.processDbChange(1,
                                        DBChangeMsg.CHANGE_CONTACT_DB,
                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                        DBChangeMsg.CAT_CUSTOMERCONTACT,
                                        DbChangeType.ADD);
        expect(contactNotificationServiceMock.createNotification(new LiteContact(1), ContactNotificationType.HOME_PHONE, 
                                                                 updatableAccount.getAccountDto().getHomePhone())).andReturn(null);
        expect(contactNotificationServiceMock.createNotification(new LiteContact(1), ContactNotificationType.WORK_PHONE, 
                                                                 updatableAccount.getAccountDto().getWorkPhone())).andReturn(null);
        expect(contactNotificationServiceMock.createNotification(new LiteContact(1), ContactNotificationType.EMAIL, 
                                                                 updatableAccount.getAccountDto().getEmailAddress())).andReturn(null);
        expectLastCall().times(3);
        expect(authDaoMock.getUserTimeZone(newuser)).andReturn(TimeZone.getDefault());
        expect(ecSettingDaoMock.getEnum(EnergyCompanySettingType.DEFAULT_TEMPERATURE_UNIT, 
                                        TemperatureUnit.class, ecMock1.getId())).andReturn(TemperatureUnit.FAHRENHEIT);
        customerDaoMock.addCustomer(liteCustomer);
        dbChangeManager.processDbChange(1,
                                        DBChangeMsg.CHANGE_CUSTOMER_DB,
                                        DBChangeMsg.CAT_CUSTOMER,
                                        DBChangeMsg.CAT_CUSTOMER,
                                        DbChangeType.ADD);
        expect(siteInformationDaoMock.getSubstationIdByName("SuperStation")).andReturn(-1);
        siteInformationDaoMock.add(new LiteSiteInformation());
        expect(accountSiteDaoMock.add(new AccountSite())).andReturn(true);
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setAccountNumber(updatableAccount.getAccountNumber());
        customerAccount.setAccountNotes(updatableAccount.getAccountDto().getAccountNotes());
        customerAccount.setCustomerId(liteCustomer.getCustomerID());
        customerAccountDaoMock.add(customerAccount);
        dbChangeManager.processDbChange(1,
                                        DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                                        DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                        DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                        DbChangeType.ADD);

        ECToAccountMapping ecToAccountMapping = new ECToAccountMapping();
        ecToAccountMapping.setEnergyCompanyId(ecMock1.getId());
        ecToAccountMapping.setAccountId(customerAccount.getAccountId());
        ecMappingDaoMock.addECToAccountMapping(ecToAccountMapping);
        accountEventLogServiceMock.accountAdded(operatorUser, updatableAccount.getAccountDto().getAccountNumber());
        usersEventLogServiceMock.userCreated(updatableAccount.getAccountDto().getUserName(),
            updatableAccount.getAccountDto().getUserGroup(), ecMock1.getName(),
            LoginStatusEnum.ENABLED, operatorUser);
        
        replayAll();
        
        /*
         * run test
         */
        accountService.addAccount(updatableAccount, operatorUser);
    }

}
