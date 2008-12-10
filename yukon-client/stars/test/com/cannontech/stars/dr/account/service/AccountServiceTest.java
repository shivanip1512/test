package com.cannontech.stars.dr.account.service;

import java.util.ArrayList;

import org.junit.Test;

import com.cannontech.common.bulk.field.impl.AccountDto;
import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.common.model.Address;
import com.cannontech.common.model.SiteInformation;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.core.dao.RoleDao;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
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
import com.cannontech.stars.dr.account.exception.UserNameUnavailableException;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ECToAccountMapping;
import com.cannontech.stars.dr.account.service.impl.AccountServiceImpl;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.event.dao.EventAccountDao;
import com.cannontech.stars.dr.event.dao.LMProgramEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class AccountServiceTest extends TestCase {
    
    // Class under test
    private AccountServiceImpl accountService;
    
    // collobarators to be mocked...and ridiculed
    private YukonUserDao yukonUserDaoMock;
    private RoleDao roleDaoMock;
    private AuthDao authDaoMock;
    private YukonGroupDao yukonGroupDaoMock;
    private AddressDao addressDaoMock;
    private ContactDao contactDaoMock;
    private ContactNotificationDao contactNotificationDaoMock;
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
    private ThermostatScheduleDao thermostatScheduleDaoMock;
    private EventAccountDao eventAccountDaoMock;
    private StarsCustAccountInformationDao starsCustAccountInformationDaoMock;
    private DBPersistentDao dbPersistantDaoMock;
    private EnergyCompanyDao energyCompanyDaoMock;
    
    protected void setUp() {
        yukonUserDaoMock = createMock(YukonUserDao.class);
        roleDaoMock = createMock(RoleDao.class);
        authDaoMock = createMock(AuthDao.class);
        yukonGroupDaoMock = createMock(YukonGroupDao.class);
        addressDaoMock = createMock(AddressDao.class);
        contactDaoMock = createMock(ContactDao.class);
        contactNotificationDaoMock = createMock(ContactNotificationDao.class);
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
        thermostatScheduleDaoMock = createMock(ThermostatScheduleDao.class);
        eventAccountDaoMock = createMock(EventAccountDao.class);
        starsCustAccountInformationDaoMock = createMock(StarsCustAccountInformationDao.class);
        dbPersistantDaoMock = createMock(DBPersistentDao.class);
        energyCompanyDaoMock = createMock(EnergyCompanyDao.class);
        
        accountService = new AccountServiceImpl();
        
        accountService.setYukonUserDao(yukonUserDaoMock);
        accountService.setRoleDao(roleDaoMock);
        accountService.setAuthDao(authDaoMock);
        accountService.setYukonGroupDao(yukonGroupDaoMock);
        accountService.setAddressDao(addressDaoMock);
        accountService.setContactDao(contactDaoMock);
        accountService.setContactNotificationDao(contactNotificationDaoMock);
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
        accountService.setThermostatScheduleDao(thermostatScheduleDaoMock);
        accountService.setEventAccountDao(eventAccountDaoMock);
        accountService.setStarsCustAccountInformationDao(starsCustAccountInformationDaoMock);
        accountService.setDBPersistentDao(dbPersistantDaoMock);
        accountService.setEnergyCompanyDao(energyCompanyDaoMock);
    }
    
    @Test
    public void testEmptyAddAccount() {
        replay(yukonUserDaoMock);
        replay(roleDaoMock);
        replay(authDaoMock);
        replay(yukonGroupDaoMock);
        replay(addressDaoMock);
        replay(contactDaoMock);
        replay(contactNotificationDaoMock);
        replay(customerDaoMock);
        replay(siteInformationDaoMock);
        replay(accountSiteDaoMock);
        replay(customerAccountDaoMock);
        replay(ecMappingDaoMock);
        replay(inventoryDaoMock);
        replay(hardwareBaseDaoMock);
        replay(lmProgramEventDaoMock);
        replay(applianceDaoMock);
        replay(workOrderDaoMock);
        replay(callReportDaoMock);
        replay(thermostatScheduleDaoMock);
        replay(eventAccountDaoMock);
        replay(starsCustAccountInformationDaoMock);
        replay(dbPersistantDaoMock);
        replay(energyCompanyDaoMock);
        
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
        updatableAccount.setAccountNumber("123456");
        LiteYukonUser user = new LiteYukonUser();
        AccountDto dto = new AccountDto();
        
        dto.setFirstName("Bob");
        dto.setFirstName("Vila");
        dto.setEmailAddress("bob@aol.com");
        dto.setHomePhone("763 867 5309");
        dto.setWorkPhone("1 800 GHOSTBUSTERS");
        dto.setIsCommercial(false);
        dto.setLoginGroup("Residential Customer");
        dto.setMapNumber("123456");
        dto.setUserName("bob");
        dto.setPassword("itsasecrettoeveryone");
        dto.setAltTrackingNumber("123456");
        dto.setCompanyName("");
        dto.setStreetAddress(new Address());
        dto.getStreetAddress().setLocationAddress1("12345 Bob Street NE");
        dto.getStreetAddress().setLocationAddress2("");
        dto.getStreetAddress().setCityName("Minneapolis");
        dto.getStreetAddress().setStateCode("MN");
        dto.getStreetAddress().setZipCode("55374");
        dto.getStreetAddress().setCounty("Hennepin");
        dto.setBillingAddress(new Address());
        dto.getBillingAddress().setLocationAddress1("12345 Bob Street NE");
        dto.getBillingAddress().setLocationAddress2("");
        dto.getBillingAddress().setCityName("Minneapolis");
        dto.getBillingAddress().setStateCode("MN");
        dto.getBillingAddress().setZipCode("55374");
        dto.getBillingAddress().setCounty("Hennepin");
        dto.setSiteInfo(new SiteInformation());
        dto.getSiteInfo().setSubstationName("SuperStation");
        dto.getSiteInfo().setFeeder("SuperFeeder");
        dto.getSiteInfo().setPole("SouthPole");
        dto.getSiteInfo().setServiceVoltage("120");
        dto.getSiteInfo().setTransformerSize("2400");
        updatableAccount.setAccountDto(dto);
        
        /*
         * Record what should happen
         */
        yukonGroupDaoMock.getLiteYukonGroup(YukonGroup.YUKON_GROUP_ID);
        yukonGroupDaoMock.getLiteYukonGroupByName(dto.getLoginGroup());
        yukonUserDaoMock.addLiteYukonUserWithPassword(user, dto.getPassword(), 1, new ArrayList<LiteYukonGroup>());
        dbPersistantDaoMock.processDBChange(new DBChangeMsg(user.getLiteID(),
            DBChangeMsg.CHANGE_YUKON_USER_DB,
            DBChangeMsg.CAT_YUKON_USER,
            DBChangeMsg.CAT_YUKON_USER,
            DBChangeMsg.CHANGE_TYPE_ADD));
        addressDaoMock.add(new LiteAddress());
        addressDaoMock.add(new LiteAddress());
        contactDaoMock.saveContact(new LiteContact(1));
        dbPersistantDaoMock.processDBChange(new DBChangeMsg(1,
                               DBChangeMsg.CHANGE_CONTACT_DB,
                               DBChangeMsg.CAT_CUSTOMERCONTACT,
                               DBChangeMsg.CAT_CUSTOMERCONTACT,
                               DBChangeMsg.CHANGE_TYPE_ADD));
        contactNotificationDaoMock.saveNotification(new LiteContactNotification(1));
        expectLastCall().times(3);
        customerDaoMock.addCustomer(new LiteCustomer());
        dbPersistantDaoMock.processDBChange(new DBChangeMsg(1,
                               DBChangeMsg.CHANGE_CUSTOMER_DB,
                               DBChangeMsg.CAT_CUSTOMER,
                               DBChangeMsg.CAT_CUSTOMER,
                               DBChangeMsg.CHANGE_TYPE_ADD));
        siteInformationDaoMock.add(new LiteSiteInformation());
        dbPersistantDaoMock.processDBChange(new DBChangeMsg(1,
                               DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB,
                               DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                               DBChangeMsg.CAT_CUSTOMER_ACCOUNT,
                               DBChangeMsg.CHANGE_TYPE_ADD));
        accountSiteDaoMock.add(new AccountSite());
        customerAccountDaoMock.add(new CustomerAccount());
        ecMappingDaoMock.addECToAccountMapping(new ECToAccountMapping());
        
        /*
         * Set Mocks to replay mode
         */
        replay(yukonUserDaoMock);
        replay(roleDaoMock);
        replay(authDaoMock);
        replay(yukonGroupDaoMock);
        replay(addressDaoMock);
        replay(contactDaoMock);
        replay(contactNotificationDaoMock);
        replay(customerDaoMock);
        replay(siteInformationDaoMock);
        replay(accountSiteDaoMock);
        replay(customerAccountDaoMock);
        replay(ecMappingDaoMock);
        replay(inventoryDaoMock);
        replay(hardwareBaseDaoMock);
        replay(lmProgramEventDaoMock);
        replay(applianceDaoMock);
        replay(workOrderDaoMock);
        replay(callReportDaoMock);
        replay(thermostatScheduleDaoMock);
        replay(eventAccountDaoMock);
        replay(starsCustAccountInformationDaoMock);
        replay(dbPersistantDaoMock);
        replay(energyCompanyDaoMock);
        
        /*
         * run test
         */
//        try {
            accountService.addAccount(updatableAccount, user);
//        }catch(InvalidAccountNumberException e) {}
//        catch(AccountNumberUnavailableException e) {}
//        catch(UserNameUnavailableException e) {}
    }

}
