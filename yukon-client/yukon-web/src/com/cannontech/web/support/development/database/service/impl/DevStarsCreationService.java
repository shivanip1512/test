package com.cannontech.web.support.development.database.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.inventory.HardwareHistory;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.Address;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.YukonGroupDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.stars.model.EnergyCompanyDto;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.user.UserUtils;
import com.cannontech.web.support.development.database.objects.DevCCU;
import com.cannontech.web.support.development.database.objects.DevHardwareType;
import com.cannontech.web.support.development.database.objects.DevStars;
import com.cannontech.web.support.development.database.objects.DevStarsAccounts;
import com.google.common.collect.Lists;

public class DevStarsCreationService extends DevObjectCreationBase {
    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private YukonUserDao yukonUserDao;
    @Autowired private YukonGroupDao yukonGroupDao;
    @Autowired private ECMappingDao ecMappingDao;
    @Autowired private AccountService accountService;
    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private HardwareUiService hardwareUiService;
    @Autowired private StarsInventoryBaseDao starsInventoryBaseDao;
    
    @Override
    protected void createAll() {
        if (devDbSetupTask.getDevStars().getEnergyCompany() == null) {
            createOperatorsGroup(devDbSetupTask.getDevStars());
            createEnergyCompany(devDbSetupTask.getDevStars());
        }
        createResidentialGroup(devDbSetupTask.getDevStars());
        setupStarsAccounts(devDbSetupTask.getDevStars());
        createStars(devDbSetupTask.getDevStars());
    }
    
    @Override
    protected void logFinalExecutionDetails() {
        log.info("Stars:");
    }
    
    private void createOperatorsGroup(DevStars devStars) {
        LiteYukonGroup group = new LiteYukonGroup();
        group.setGroupDescription(devStars.getNewEnergyCompanyName() + " Operators Grp");
        group.setGroupName(devStars.getNewEnergyCompanyName() + " Operators Grp");
        yukonGroupDao.save(group);
        
        setRoleProperty(group, YukonRoleProperty.POINT_ID_EDIT, " ");
        setRoleProperty(group, YukonRoleProperty.DBEDITOR_LM, " ");
        setRoleProperty(group, YukonRoleProperty.DBEDITOR_SYSTEM, " ");
        setRoleProperty(group, YukonRoleProperty.UTILITY_ID_RANGE, " ");
        setRoleProperty(group, YukonRoleProperty.PERMIT_LOGIN_EDIT, " ");
        setRoleProperty(group, YukonRoleProperty.ALLOW_MEMBER_PROGRAMS, " ");
        
        setRoleProperty(group, YukonRoleProperty.LOADCONTROL_EDIT, " ");
        setRoleProperty(group, YukonRoleProperty.MACS_EDIT, " ");
        setRoleProperty(group, YukonRoleProperty.TDC_EXPRESS, " ");
        setRoleProperty(group, YukonRoleProperty.TDC_MAX_ROWS, " ");
        setRoleProperty(group, YukonRoleProperty.TDC_RIGHTS, " ");
        setRoleProperty(group, YukonRoleProperty.TDC_ALARM_COUNT, " ");
        setRoleProperty(group, YukonRoleProperty.DECIMAL_PLACES, " ");
        setRoleProperty(group, YukonRoleProperty.LC_REDUCTION_COL, " ");
        
        setRoleProperty(group, YukonRoleProperty.GRAPH_EDIT_GRAPHDEFINITION, " ");
        setRoleProperty(group, YukonRoleProperty.TRENDING_DISCLAIMER, " ");
        setRoleProperty(group, YukonRoleProperty.SCAN_NOW_ENABLED, " ");
        setRoleProperty(group, YukonRoleProperty.MINIMUM_SCAN_FREQUENCY, " ");
        setRoleProperty(group, YukonRoleProperty.MAXIMUM_DAILY_SCANS, " ");

        setRoleTrue(group, YukonRoleProperty.VERSACOM_SERIAL_MODEL);
        setRoleTrue(group, YukonRoleProperty.EXPRESSCOM_SERIAL_MODEL);
        setRoleProperty(group, YukonRoleProperty.DCU_SA205_SERIAL_MODEL,"false");
        setRoleProperty(group, YukonRoleProperty.DCU_SA305_SERIAL_MODEL,"false");
        setRoleProperty(group, YukonRoleProperty.LC_REDUCTION_COL, " ");
        setRoleProperty(group, YukonRoleProperty.COMMANDS_GROUP, " ");
        
        setRoleProperty(group, YukonRoleProperty.DYNAMIC_BILLING_FILE_SETUP, " ");
        
        devStars.setLiteYukonGroupOperator(group);
    }
    
    private void createEnergyCompany(DevStars devStars) {
        
        EnergyCompanyDto energyCompanyDto = new EnergyCompanyDto();
        energyCompanyDto.setName(devStars.getNewEnergyCompanyName());
        energyCompanyDto.setEmail("info@cannontech.com");
        energyCompanyDto.setPrimaryOperatorGroupId(devStars.getLiteYukonGroupOperator().getGroupID());
        String username = StringUtils.deleteWhitespace(devStars.getNewEnergyCompanyName()).toLowerCase() + "_op";
        energyCompanyDto.setAdminUsername(username);
        energyCompanyDto.setAdminPassword1(username);
        energyCompanyDto.setAdminPassword2(username);
        
        LiteYukonUser yukonUser = yukonUserDao.getLiteYukonUser(UserUtils.USER_YUKON_ID);
        
        try {
            LiteStarsEnergyCompany ec = energyCompanyService.createEnergyCompany(energyCompanyDto,  yukonUser, null);
            devStars.setEnergyCompany(ec);
        } catch (Exception e) {
            log.error("Unable to create new energycompany " + energyCompanyDto.getName());
            throw new RuntimeException(e);
        }
        
        try {
            if (!energyCompanyService.isOperator(yukonUser)) {
                ecMappingDao.addEnergyCompanyOperatorLoginListMapping(yukonUser, devStars.getEnergyCompany());
                log.info("Set user yukon as operator login for " + devStars.getNewEnergyCompanyName());
            } else {
                YukonEnergyCompany yukonOperator = yukonEnergyCompanyService.getEnergyCompanyByOperator(yukonUser);
                log.warn("Yukon user already set as operator login for " + yukonOperator.getName());
            }
        } catch (Exception e) {
            log.warn("Unable to link new energy company to yukon/yukon",e);
        }
    }
    
    private void createResidentialGroup(DevStars devStars) {
        int energyCompanyId = devStars.getEnergyCompany().getEnergyCompanyId();
        List<LiteYukonGroup> residentialGroups = ecMappingDao.getResidentialGroups(energyCompanyId);
        if (residentialGroups.isEmpty()) {
            LiteYukonGroup group = null;

            try {        
                group = yukonGroupDao.getLiteYukonGroupByName(devStars.getNewEnergyCompanyName() + " Residential Grp");
            } catch(Exception e) {
                group = new LiteYukonGroup();
                group.setGroupDescription(devStars.getNewEnergyCompanyName() + " Residential Grp");
                group.setGroupName(devStars.getNewEnergyCompanyName() + " Residential Grp");
                yukonGroupDao.save(group);
            }

            setRoleProperty(group, YukonRoleProperty.HOME_URL, "/spring/stars/consumer/general");
            setRoleProperty(group, YukonRoleProperty.STYLE_SHEET, " ");
            setRoleProperty(group, YukonRoleProperty.NAV_BULLET_EXPAND, " ");
            setRoleProperty(group, YukonRoleProperty.NAV_BULLET_SELECTED, " ");
            setRoleProperty(group, YukonRoleProperty.HEADER_LOGO, "yukon/DemoHeaderCES.gif");
            setRoleProperty(group, YukonRoleProperty.LOG_IN_URL, " ");
            setRoleProperty(group, YukonRoleProperty.NAV_CONNECTOR_BOTTOM, " ");
            setRoleProperty(group, YukonRoleProperty.NAV_CONNECTOR_MIDDLE, " ");
            setRoleProperty(group, YukonRoleProperty.INBOUND_VOICE_HOME_URL, " ");
            
            setRoleTrue(group, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_ACCOUNT_GENERAL);
            setRoleTrue(group, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_CONTROL_HISTORY);
            setRoleTrue(group, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_ENROLLMENT);
            setRoleTrue(group, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_PROGRAMS_OPT_OUT);
            setRoleTrue(group, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_HARDWARES_THERMOSTAT);
            setRoleTrue(group, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_QUESTIONS_FAQ);
            setRoleTrue(group, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_QUESTIONS_UTIL);
            setRoleTrue(group, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_CHANGE_LOGIN_USERNAME);
            setRoleTrue(group, YukonRoleProperty.RESIDENTIAL_CONSUMER_INFO_THERMOSTATS_ALL);
            
            setRoleProperty(group, YukonRoleProperty.RESIDENTIAL_HIDE_OPT_OUT_BOX, "false");
            setRoleProperty(group, YukonRoleProperty.RESIDENTIAL_OPT_OUT_PERIOD, " ");
            setRoleProperty(group, YukonRoleProperty.RESIDENTIAL_WEB_LINK_FAQ, " ");
            setRoleProperty(group, YukonRoleProperty.RESIDENTIAL_WEB_LINK_THERM_INSTRUCTIONS, " ");

            setRoleProperty(group, YukonRoleProperty.CSRF_TOKEN_MODE, " ");
            
            ecMappingDao.addECToResidentialGroupMapping(energyCompanyId, Lists.newArrayList(group.getGroupID()));
            devStars.setLiteYukonGroupResidential(group);
        } else {
            devStars.setLiteYukonGroupResidential(residentialGroups.get(0));
        }
    }

    private void setupStarsAccounts(DevStars devStars) {
        DevStarsAccounts devStarsAccounts = devStars.getDevStarsAccounts();
        int accountNumIterator = devStarsAccounts.getAccountNumMin();
        for (int i = 0; i < devStarsAccounts.getNumAccounts(); i++) {
            UpdatableAccount account = new UpdatableAccount();
            String accountNumString = String.valueOf(accountNumIterator);
            AccountDto accountDto = new AccountDto();
            accountDto.setAccountNumber(accountNumString);
            accountDto.setLastName("last" + accountNumString);
            accountDto.setFirstName("first" + accountNumString);
            accountDto.setCompanyName("company" + accountNumString);
            accountDto.setHomePhone("555-555-5555");
            accountDto.setWorkPhone("555-555-5555");
            accountDto.setEmailAddress(accountNumString + "@test.com");
            accountDto.setStreetAddress(new Address("1234 Fake Street", "5678 Really Fake Street", "Fakeland", "MN", "55555", "Fake County"));
            accountDto.setBillingAddress(new Address("1234 Fake Street", "5678 Really Fake Street", "Fakeland", "MN", "55555", "Fake County"));
            accountDto.setUserName(accountNumString);
            accountDto.setPassword(accountNumString);
            accountDto.setLoginGroup(devStars.getLiteYukonGroupResidential().getGroupName());
            accountDto.setMapNumber(accountNumString);
            accountDto.setAltTrackingNumber(accountNumString);
            accountDto.setIsCommercial(false);
            accountDto.setCustomerNumber(accountNumString);
            accountDto.setIsCustAtHome(true);
            accountDto.setPropertyNotes("Property Notes for account number: " + accountNumString);
            accountDto.setAccountNotes("Account Notes for account number: " + accountNumString);
            account.setAccountDto(accountDto);
            account.setAccountNumber(accountNumString);
            devStarsAccounts.getAccounts().add(account);
            accountNumIterator++;
        }
    }
    
    private void createStars(DevStars devStars) {
        log.info("Creating Stars Accounts (and Hardware) ...");
        int inventoryIdIterator = devStars.getDevStarsHardware().getSerialNumMin();
        // Account Hardware
        for (UpdatableAccount updatableAccount: devStars.getDevStarsAccounts().getAccounts()) {
            if (createAccount(devStars, updatableAccount)) {
                for (int i = 0; i < devStars.getDevStarsHardware().getNumPerAccount(); i++) {
                    for (DevHardwareType devHardwareType: devStars.getDevStarsHardware().getHardwareTypes()) {
                        if (devHardwareType.isCreate()) {
                            CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(updatableAccount.getAccountNumber(), devStars.getEnergyCompany().getEnergyCompanyId());
                            createHardware(devStars, devHardwareType, customerAccount.getAccountId(), inventoryIdIterator);
                            inventoryIdIterator++;
                        }
                    }
                }
            }
        }
        log.info("Creating Extra Stars Hardware ...");
        // Warehouse (extra) Hardware
        for (int i = 0; i < devStars.getDevStarsHardware().getNumExtra(); i++) {
            for (DevHardwareType devHardwareType: devStars.getDevStarsHardware().getHardwareTypes()) {
                if (devHardwareType.isCreate()) {
                    createHardware(devStars, devHardwareType, 0, inventoryIdIterator);
                    inventoryIdIterator++;
                }
            }
        }
    }

    private boolean createAccount(DevStars devStars, UpdatableAccount updatableAccount) {
        checkIsCancelled();
        LiteStarsEnergyCompany energyCompany = devStars.getEnergyCompany();
        if (!canAddStarsAccount(devStars, updatableAccount, energyCompany.getEnergyCompanyId())) {
            devStars.addToFailureCount(devStars.getDevStarsHardware().getNumHardwarePerAccount() + 1);
            return false;
        }
        accountService.addAccount(updatableAccount, energyCompany.getUser());
        log.info("STARS Account added: " + updatableAccount.getAccountNumber() + " to EC: " + energyCompany.getName());
        devStars.incrementSuccessCount();
        return true;
    }
    
    private void createHardware(DevStars devStars, DevHardwareType devHardwareType, int accountId, int inventoryIdIterator) {
        checkIsCancelled();
        LiteStarsEnergyCompany energyCompany = devStars.getEnergyCompany();
        Hardware hardware = getHardwareDto(devHardwareType, energyCompany, inventoryIdIterator);
        if (!canAddStarsHardware(devStars, hardware)) {
            devStars.incrementFailureCount();
            return;
        }
        try {
            hardwareUiService.createHardware(hardware, accountId, energyCompany.getUser());
            devStars.incrementSuccessCount();
            if (accountId == 0) {
                log.info("STARS Hardware added: " + hardware.getDisplayName() + " to warehouse");
            } else {
                log.info("STARS Hardware added: " + hardware.getDisplayName() + " to accountId: " + accountId);
            }
        } catch (ObjectInOtherEnergyCompanyException e) {
            // We should have caught this already in canAddStarsHardware, but createHardware requires it
            log.info("Hardware Object " + hardware.getDisplayName() + " is in another energy company.");
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            devStars.incrementFailureCount();
        }
    }
    
    private Hardware getHardwareDto(DevHardwareType devHardwareType, LiteStarsEnergyCompany energyCompany, int inventoryId) {
        String inventoryIdIteratorString = String.valueOf(inventoryId);
        HardwareType hardwareType = devHardwareType.getHardwareType();
        Hardware hardware = new Hardware();
        hardware.setInventoryId(inventoryId);
        hardware.setDeviceId(0);
        String hardwareName = devHardwareType.getHardwareType().name() + " " + inventoryIdIteratorString;
        hardware.setDisplayName(hardwareName);
        hardware.setDisplayLabel(hardwareName);
        hardware.setDisplayType(hardwareName);
        hardware.setSerialNumber(inventoryIdIteratorString);
        hardware.setDeviceNotes("Device Notes for inventoryId: " + inventoryIdIteratorString);
        /* Non-ZigBee two way LCR fields */
        if (hardwareType.isTwoWay() && !hardwareType.isZigbee()) {
            hardware.setTwoWayDeviceName(hardwareType + " " + inventoryIdIteratorString);
            hardware.setCreatingNewTwoWayDevice(true);
        }
        String ccuName = DevCCU.SIM_711.getName();
        Integer routeId = paoDao.getRouteIdForRouteName(ccuName);
        if (routeId == null) {
            throw new RuntimeException("Couldn't find route with name " + ccuName);
        }
        hardware.setRouteId(routeId);
        hardware.setEnergyCompanyId(energyCompany.getEnergyCompanyId());
        hardware.setHardwareType(hardwareType);
        YukonListEntry typeEntry = energyCompany.getYukonListEntry(hardwareType.getDefinitionId());
        hardware.setHardwareTypeEntryId(typeEntry.getEntryID());
        hardware.setDisplayType(hardwareType.name());
        hardware.setCategoryName("Category");
        hardware.setAltTrackingNumber("4");
        hardware.setFieldInstallDate(new Date());
        hardware.setInstallNotes("Install notes: This hardware went in super easy.");
        HardwareHistory hardwareHistory = new HardwareHistory();
        hardwareHistory.setAction("Install performed.");
        hardwareHistory.setDate(new Date());
        hardware.setHardwareHistory(Lists.newArrayList(hardwareHistory));
        
        /* Non-ZigBee two way LCR fields */
        if (hardwareType.isTwoWay() && !hardwareType.isZigbee()) {
            hardware.setCreatingNewTwoWayDevice(true);
        }
        return hardware;
    }
    
    private boolean canAddStarsHardware(DevStars devStars, Hardware hardware) {
        int serialNum = Integer.valueOf(hardware.getSerialNumber());
        if (serialNum >= devStars.getDevStarsHardware().getSerialNumMax()) {
            log.info("Hardware Object " + hardware.getDisplayName() + " cannot be added. Max of "
                     + devStars.getDevStarsHardware().getSerialNumMax() + " reached.");
            return false;
        }
        // check energy company
        boolean isSerialNumInUse = hardwareUiService.isSerialNumberInEC(hardware);
        if (isSerialNumInUse) {
            log.info("Hardware Object " + hardware.getDisplayName() + " already exists in the database assigned to ec: "
                     + hardware.getEnergyCompanyId());
            return false;
        }
        // check warehouse
        try {
            starsInventoryBaseDao.getByInventoryId(hardware.getInventoryId());
        } catch (NotFoundException e) {
            return true;
        }
        
        log.info("Hardware Object " + hardware.getDisplayName() + " already exists in the database (warehouse).");
        return false;
    }
    
    private boolean canAddStarsAccount(DevStars devStars, UpdatableAccount updatableAccount, int energyCompanyId) {
        String accountNumber = updatableAccount.getAccountNumber();
        Integer acctNumInt = Integer.valueOf(accountNumber);
        if (acctNumInt >= devStars.getDevStarsAccounts().getAccountNumMax()) {
            log.info("Account " + accountNumber + " could not be added: Reached max account number of ." + devStars.getDevStarsAccounts().getAccountNumMax());
            return false;
        }
        // Checks to see if the account number is already being used.
        try {
            CustomerAccount customerAccount = customerAccountDao.getByAccountNumber(accountNumber, energyCompanyId);
            if (customerAccount != null){
                log.info("Account " + accountNumber + " could not be added: The provided account number already exists.");
                return false;
            }
        } catch (NotFoundException e ) {
            // Account doesn't exist
        }
        if(yukonUserDao.findUserByUsername( updatableAccount.getAccountDto().getUserName() ) != null) {
            log.info("Account " + accountNumber + " could not be added: The provided username already exists.");
            return false;
        }
        return true;
    }
}
