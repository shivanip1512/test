package com.cannontech.web.support.development.database.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.hardware.model.HardwareDto;
import com.cannontech.stars.dr.hardware.model.HardwareHistory;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;
import com.cannontech.stars.util.ObjectInOtherEnergyCompanyException;
import com.cannontech.web.support.development.database.objects.DevCCU;
import com.cannontech.web.support.development.database.objects.DevHardwareType;
import com.cannontech.web.support.development.database.objects.DevStars;
import com.cannontech.web.support.development.database.service.DevStarsCreationService;
import com.google.common.collect.Lists;

public class DevStarsCreationServiceImpl extends DevObjectCreationBase implements DevStarsCreationService {
    private AccountService accountService;
    private HardwareUiService hardwareUiService;
    private CustomerAccountDao customerAccountDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    
    @Override
    public void createAll() {
        createStars(devDbSetupTask.getDevStars());
    }

    private void createStars(DevStars devStars) {
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
        HardwareDto hardwareDto = getHardwareDto(devHardwareType, energyCompany, inventoryIdIterator);
        if (!canAddStarsHardware(devStars, hardwareDto)) {
            devStars.incrementFailureCount();
            return;
        }
        try {
            hardwareUiService.createHardware(hardwareDto, accountId, energyCompany.getUser());
            devStars.incrementSuccessCount();
            if (accountId == 0) {
                log.info("STARS Hardware added: " + hardwareDto.getDisplayName() + " to warehouse");
            } else {
                log.info("STARS Hardware added: " + hardwareDto.getDisplayName() + " to accountId: " + accountId);
            }
        } catch (ObjectInOtherEnergyCompanyException e) {
            // We should have caught this already in canAddStarsHardware, but createHardware requires it
            log.info("Hardware Object " + hardwareDto.getDisplayName() + " is in another energy company.");
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            devStars.incrementFailureCount();
        }
    }
    
    private HardwareDto getHardwareDto(DevHardwareType devHardwareType, LiteStarsEnergyCompany energyCompany, int inventoryId) {
        String inventoryIdIteratorString = String.valueOf(inventoryId);
        HardwareType hardwareType = devHardwareType.getHardwareType();
        HardwareDto hardwareDto = new HardwareDto();
        hardwareDto.setInventoryId(inventoryId);
        hardwareDto.setDeviceId(0);
        String hardwareName = devHardwareType.getHardwareType().name() + " " + inventoryIdIteratorString;
        hardwareDto.setDisplayName(hardwareName);
        hardwareDto.setDisplayLabel(hardwareName);
        hardwareDto.setDisplayType(hardwareName);
        hardwareDto.setSerialNumber(inventoryIdIteratorString);
        hardwareDto.setDeviceNotes("Device Notes for inventoryId: " + inventoryIdIteratorString);
        /* Non-ZigBee two way LCR fields */
        if (hardwareType.isTwoWay() && !hardwareType.isZigbee()) {
            hardwareDto.setTwoWayDeviceName("twoWayDeviceName" + inventoryIdIteratorString);
            hardwareDto.setCreatingNewTwoWayDevice(true);
        }
        String ccuName = DevCCU.CCU_711_SIM.getName();
        Integer routeId = paoDao.getRouteIdForRouteName(ccuName);
        if (routeId == null) {
            throw new RuntimeException("Couldn't find route with name " + ccuName);
        }
        hardwareDto.setRouteId(routeId);
        hardwareDto.setEnergyCompanyId(energyCompany.getEnergyCompanyId());
        hardwareDto.setHardwareType(hardwareType);
        YukonListEntry typeEntry = energyCompany.getYukonListEntry(hardwareType.getDefinitionId());
        hardwareDto.setHardwareTypeEntryId(typeEntry.getEntryID());
        hardwareDto.setDisplayType(hardwareType.name());
        hardwareDto.setCategoryName("Category");
        hardwareDto.setAltTrackingNumber("4");
//            private Integer voltageEntryId;
        hardwareDto.setFieldInstallDate(new Date());
//            private Date fieldReceiveDate;
//            private Date fieldRemoveDate;
//            private Integer serviceCompanyId;
//            private Integer warehouseId;
        hardwareDto.setInstallNotes("Install notes: This hardware went in super easy.");
//            private Integer deviceStatusEntryId;
//            private Integer originalDeviceStatusEntryId;
        HardwareHistory hardwareHistory = new HardwareHistory();
        hardwareHistory.setAction("Install performed.");
        hardwareHistory.setDate(new Date());
        hardwareDto.setHardwareHistory(Lists.newArrayList(hardwareHistory));
        
        /* Non-ZigBee two way LCR fields */
        if (hardwareType.isTwoWay() && !hardwareType.isZigbee()) {
            hardwareDto.setCreatingNewTwoWayDevice(true);
        }
        return hardwareDto;
    }
    
    private boolean canAddStarsHardware(DevStars devStars, HardwareDto hardwareDto) {
        int serialNum = Integer.valueOf(hardwareDto.getSerialNumber());
        if (serialNum >= devStars.getDevStarsHardware().getSerialNumMax()) {
            log.info("Hardware Object " + hardwareDto.getDisplayName() + " cannot be added. Max of "
                     + devStars.getDevStarsHardware().getSerialNumMax() + " reached.");
            return false;
        }
        // check energy company
        boolean isSerialNumInUse = hardwareUiService.isSerialNumberInEC(hardwareDto);
        if (isSerialNumInUse) {
            log.info("Hardware Object " + hardwareDto.getDisplayName() + " already exists in the database assigned to ec: "
                     + hardwareDto.getEnergyCompanyId());
            return false;
        }
        // check warehouse
        try {
            starsInventoryBaseDao.getByInventoryId(hardwareDto.getInventoryId());
        } catch (NotFoundException e) {
            return true;
        }
        
        log.info("Hardware Object " + hardwareDto.getDisplayName() + " already exists in the database (warehouse).");
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
    
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
    @Autowired
    public void setHardwareUiService(HardwareUiService hardwareUiService) {
        this.hardwareUiService = hardwareUiService;
    }
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }
}
