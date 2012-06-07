package com.cannontech.stars.dr.account.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.easymock.EasyMockSupport;
import org.easymock.IMockBuilder;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.authentication.service.AuthType;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.dao.impl.LoginStatusEnum;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteSiteInformation;
import com.cannontech.stars.database.data.lite.LiteStarsCustAccountInformation;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.YukonEnergyCompanyMockFactory;
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
import com.cannontech.stars.dr.adapters.YukonEnergyCompanyServiceAdapter;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.event.dao.EventAccountDao;
import com.cannontech.stars.dr.event.dao.LMProgramEventDao;
import com.cannontech.stars.dr.general.service.ContactNotificationService;
import com.cannontech.stars.dr.general.service.ContactService;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class AccountServiceTest extends EasyMockSupport {
    
    // Class under test
    private AccountServiceImpl accountService;
    
    // collobarators to be mocked...and ridiculed
    private YukonUserDao yukonUserDaoMock;
    private RolePropertyDao rolePropertyDaoMock;
    private AuthDao authDaoMock;
    private YukonGroupDao yukonGroupDaoMock;
    private AddressDao addressDaoMock;
    private ContactNotificationService contactNotificationServiceMock;
    private CustomerDao customerDaoMock;
    private SiteInformationDao siteInformationDaoMock;
    private AccountSiteDao accountSiteDaoMock;
    private CustomerAccountDao customerAccountDaoMock;
    private ECMappingDao ecMappingDaoMock;
    private InventoryDao inventoryDaoMock;
    private LMHardwareBaseDao hardwareBaseDaoMock;
    private LMProgramEventDao lmProgramEventDaoMock;
    private ApplianceDao applianceDaoMock;
    private StarsWorkOrderBaseDao workOrderDaoMock;
    private CallReportDao callReportDaoMock;
    private AccountThermostatScheduleDao accountThermostatScheduleDaoMock;
    private EventAccountDao eventAccountDaoMock;
    private StarsCustAccountInformationDao starsCustAccountInformationDaoMock;
    private DBPersistentDao dbPersistantDaoMock;
    private AccountEventLogService accountEventLogServiceMock;
    
    private YukonEnergyCompanyService yukonEnergyCompanyServiceMock;
    private ContactService contactServiceMock;
    private StarsDatabaseCache starsDatabaseCacheMock;
    
    @Before
    public void setUp() throws SecurityException, NoSuchMethodException {
        yukonUserDaoMock = createNiceMock(YukonUserDao.class);
        rolePropertyDaoMock = createMock(RolePropertyDao.class);
        authDaoMock = createMock(AuthDao.class);
        yukonGroupDaoMock = createMock(YukonGroupDao.class);
        addressDaoMock = createMock(AddressDao.class);
        
        contactNotificationServiceMock = createNiceMock(ContactNotificationService.class);
        customerDaoMock = createMock(CustomerDao.class);
        siteInformationDaoMock = createMock(SiteInformationDao.class);
        accountSiteDaoMock = createMock(AccountSiteDao.class);
        customerAccountDaoMock = createMock(CustomerAccountDao.class);
        ecMappingDaoMock = createMock(ECMappingDao.class);
        inventoryDaoMock = createMock(InventoryDao.class);
        hardwareBaseDaoMock = createMock(LMHardwareBaseDao.class);
        lmProgramEventDaoMock = createMock(LMProgramEventDao.class);
        applianceDaoMock = createMock(ApplianceDao.class);
        workOrderDaoMock = createMock(StarsWorkOrderBaseDao.class);
        callReportDaoMock = createMock(CallReportDao.class);
        accountThermostatScheduleDaoMock = createMock(AccountThermostatScheduleDao.class);
        eventAccountDaoMock = createMock(EventAccountDao.class);
        starsCustAccountInformationDaoMock = createMock(StarsCustAccountInformationDao.class);
        dbPersistantDaoMock = createNiceMock(DBPersistentDao.class);
        accountEventLogServiceMock = createMock(AccountEventLogService.class);

        
        
        YukonEnergyCompanyService yecServiceMock = new YukonEnergyCompanyServiceAdapter() {
            @Override
            public YukonEnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator) {
                return YukonEnergyCompanyMockFactory.getYukonEC1();
            }
            
            @Override
            public YukonEnergyCompany getEnergyCompanyByAccountId(int accountId) {
                return YukonEnergyCompanyMockFactory.getYukonEC1();
            }
        };
        List<String> yukonEnergyCompanyServiceMockableMethods = 
            getMethodNamesToMock(yecServiceMock.getClass(), "getEnergyCompanyByOperator", "getEnergyCompanyByAccountId");
        yukonEnergyCompanyServiceMock = createPartialMock(yecServiceMock.getClass(), yukonEnergyCompanyServiceMockableMethods).createMock();        
        
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
                LiteStarsEnergyCompany ec = new LiteStarsEnergyCompany() {
                    public LiteStarsCustAccountInformation searchAccountByAccountNo(String accountNumber) {
                        return null;
                    }
                };
                return ec;
            }
        };

        accountService = new AccountServiceImpl();
        accountService.setYukonUserDao(yukonUserDaoMock);
        accountService.setRolePropertyDao(rolePropertyDaoMock);
        accountService.setAuthDao(authDaoMock);
        accountService.setYukonGroupDao(yukonGroupDaoMock);
        accountService.setAddressDao(addressDaoMock);
        accountService.setContactService(contactServiceMock);
        accountService.setContactNotificationService(contactNotificationServiceMock);
        accountService.setCustomerDao(customerDaoMock);
        accountService.setSiteInformationDao(siteInformationDaoMock);
        accountService.setAccountSiteDao(accountSiteDaoMock);
        accountService.setCustomerAccountDao(customerAccountDaoMock);
        accountService.setECMappingDao(ecMappingDaoMock);
        accountService.setInventoryDao(inventoryDaoMock);
        accountService.setHardwareBaseDao(hardwareBaseDaoMock);
        accountService.setLMProgramEventDao(lmProgramEventDaoMock);
        accountService.setApplianceDao(applianceDaoMock);
        accountService.setWorkOrderDao(workOrderDaoMock);
        accountService.setCallReportDao(callReportDaoMock);
        accountService.setAccountThermostatScheduleDao(accountThermostatScheduleDaoMock);
        accountService.setEventAccountDao(eventAccountDaoMock);
        accountService.setStarsCustAccountInformationDao(starsCustAccountInformationDaoMock);
        accountService.setDBPersistentDao(dbPersistantDaoMock);
        accountService.setYukonEnergyCompanyService(yukonEnergyCompanyServiceMock);
        accountService.setAccountEventLogService(accountEventLogServiceMock);
        accountService.setStarsDatabaseCache(starsDatabaseCacheMock);
    }
    
    private <T> IMockBuilder<T> createPartialMock(final Class<T> toMock, List<String> yukonEnergyCompanyServiceMockableMethods) {
        
        IMockBuilder<T> mockBuilder = createMockBuilder(toMock);
        for (String methodName : yukonEnergyCompanyServiceMockableMethods) {
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
        LiteYukonUser user = new LiteYukonUser();
        
        try {
            accountService.addAccount(updatableAccount, user);
        }catch(InvalidAccountNumberException e) {}
        catch(AccountNumberUnavailableException e) {}
        catch(UserNameUnavailableException e) {}
    }
    
    @Test
    public void testFullAddAccount() {
        
        /*
         * Setup the account info
         */
        UpdatableAccount updatableAccount = new UpdatableAccount();
        AccountDto accountDto1 = AccountDtoMockFactory.getAccountDto1();
        updatableAccount.setAccountNumber(accountDto1.getAccountNumber());
        updatableAccount.setAccountDto(accountDto1);

        LiteYukonUser user = new LiteYukonUser();

        LiteYukonUser newuser = new LiteYukonUser(); 
        newuser.setUsername(updatableAccount.getAccountDto().getUserName());
        newuser.setLoginStatus(LoginStatusEnum.ENABLED);
        newuser.setAuthType(AuthType.NONE);

        LiteCustomer liteCustomer = new LiteCustomer();
        liteCustomer.setCustomerNumber(CtiUtilities.STRING_NONE);
        liteCustomer.setAltTrackingNumber(updatableAccount.getAccountDto().getAltTrackingNumber());
        liteCustomer.setRateScheduleID(CtiUtilities.NONE_ZERO_ID);

        /*
         * Record what should happen
         */
        expect(customerAccountDaoMock.getByAccountNumber(updatableAccount.getAccountDto().getAccountNumber(), 1)).andReturn(null);
        expect(yukonUserDaoMock.findUserByUsername(updatableAccount.getAccountDto().getUserName())).andReturn(null);
        expect(rolePropertyDaoMock.getPropertyEnumValue(YukonRoleProperty.DEFAULT_AUTH_TYPE, AuthType.class, null)).andReturn(AuthType.NONE);
        expect(yukonGroupDaoMock.getLiteYukonGroup(YukonGroup.YUKON_GROUP_ID)).andReturn(new LiteYukonGroup());
        expect(yukonGroupDaoMock.getLiteYukonGroupByName(updatableAccount.getAccountDto().getLoginGroup())).andReturn(new LiteYukonGroup());
        List<LiteYukonGroup> list = new ArrayList<LiteYukonGroup>();
        list.add(null);
        list.add(null);
        yukonUserDaoMock.addLiteYukonUserWithPassword(newuser, updatableAccount.getAccountDto().getPassword(), list );
        dbPersistantDaoMock.processDBChange(new DBChangeMsg(user.getLiteID(),
            DBChangeMsg.CHANGE_YUKON_USER_DB,
            DBChangeMsg.CAT_YUKON_USER,
            DBChangeMsg.CAT_YUKON_USER,
            DbChangeType.ADD));
        expect(addressDaoMock.add(new LiteAddress())).andReturn(true);
        expect(addressDaoMock.add(new LiteAddress())).andReturn(true);
        expect(contactServiceMock.createContact(updatableAccount.getAccountDto().getFirstName(), 
                                                updatableAccount.getAccountDto().getLastName(), user));
        dbPersistantDaoMock.processDBChange(new DBChangeMsg(1,
                               DBChangeMsg.CHANGE_CONTACT_DB,
                               DBChangeMsg.CAT_CUSTOMERCONTACT,
                               DBChangeMsg.CAT_CUSTOMERCONTACT,
                               DbChangeType.ADD));
        expect(contactNotificationServiceMock.createNotification(new LiteContact(1), ContactNotificationType.HOME_PHONE, 
                                                                 updatableAccount.getAccountDto().getHomePhone())).andReturn(null);
        expect(contactNotificationServiceMock.createNotification(new LiteContact(1), ContactNotificationType.WORK_PHONE, 
                                                                 updatableAccount.getAccountDto().getWorkPhone())).andReturn(null);
        expect(contactNotificationServiceMock.createNotification(new LiteContact(1), ContactNotificationType.EMAIL, 
                                                                 updatableAccount.getAccountDto().getEmailAddress())).andReturn(null);
        expectLastCall().times(3);
        expect(authDaoMock.getUserTimeZone(user)).andReturn(TimeZone.getDefault());
        expect(authDaoMock.getRolePropertyValue(-9999, -1110)).andReturn(null);
        expect(rolePropertyDaoMock.getPropertyStringValue(YukonRoleProperty.DEFAULT_TEMPERATURE_UNIT, YukonEnergyCompanyMockFactory.getYukonEC1().getEnergyCompanyUser())).andReturn(null);
        customerDaoMock.addCustomer(liteCustomer);
        dbPersistantDaoMock.processDBChange(new DBChangeMsg(1,
                               DBChangeMsg.CHANGE_CUSTOMER_DB,
                               DBChangeMsg.CAT_CUSTOMER,
                               DBChangeMsg.CAT_CUSTOMER,
                               DbChangeType.ADD));
        expect(siteInformationDaoMock.getSubstationIdByName("SuperStation")).andReturn(-1);
        siteInformationDaoMock.add(new LiteSiteInformation());
        expect(accountSiteDaoMock.add(new AccountSite())).andReturn(true);
        CustomerAccount customerAccount = new CustomerAccount();
        customerAccount.setAccountNumber(updatableAccount.getAccountNumber());
        customerAccount.setAccountNotes(updatableAccount.getAccountDto().getAccountNotes());
        customerAccount.setCustomerId(liteCustomer.getCustomerID());
        customerAccountDaoMock.add(customerAccount);
        dbPersistantDaoMock.processDBChange(new DBChangeMsg(1,
                                                            DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                                                            DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                                            DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                                                            DbChangeType.ADD));

        ECToAccountMapping ecToAccountMapping = new ECToAccountMapping();
        ecToAccountMapping.setEnergyCompanyId(YukonEnergyCompanyMockFactory.getYukonEC1().getEnergyCompanyId());
        ecToAccountMapping.setAccountId(customerAccount.getAccountId());
        ecMappingDaoMock.addECToAccountMapping(ecToAccountMapping);
        accountEventLogServiceMock.accountAdded(user, updatableAccount.getAccountDto().getAccountNumber());
        
        replayAll();
        
        /*
         * run test
         */
        accountService.addAccount(updatableAccount, user);
    }

}
