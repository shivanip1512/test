package com.cannontech.stars.dr.account.service;

import org.junit.Test;

import com.cannontech.common.bulk.field.impl.UpdatableAccount;
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
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.CallReportDao;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.SiteInformationDao;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.core.dao.StarsWorkOrderBaseDao;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.service.impl.AccountServiceImpl;
import com.cannontech.stars.dr.appliance.dao.ApplianceDao;
import com.cannontech.stars.dr.event.dao.EventAccountDao;
import com.cannontech.stars.dr.event.dao.LMProgramEventDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;

import junit.framework.TestCase;

import static org.easymock.EasyMock.*;

public class AccounServiceTest extends TestCase {
    
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
    public void testAddAccount() {
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
    }

}
