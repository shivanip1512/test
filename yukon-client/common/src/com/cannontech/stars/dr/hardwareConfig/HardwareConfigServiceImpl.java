package com.cannontech.stars.dr.hardwareConfig;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.EnergyCompanyRolePropertyDao;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.stars.core.dao.StarsInventoryBaseDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMHardware;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.action.YukonSwitchCommandAction;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class HardwareConfigServiceImpl implements HardwareConfigService {

    private HardwareEventLogService hardwareEventLogService;
    private CustomerAccountDao customerAccountDao;
    private StarsInventoryBaseDao starsInventoryBaseDao;
    private EnrollmentDao enrollmentDao;
    private EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao;

    private static Logger log = YukonLogManager.getLogger(HardwareConfigServiceImpl.class);
    
    @Override
    public void disable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws WebClientException {
        LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyId);

        if (VersionTools.staticLoadGroupMappingExists()) {
            addSwitchCommand(energyCompanyId, accountId, inventoryId,
                             SwitchCommandQueue.SWITCH_COMMAND_DISABLE);
        } else {
            YukonSwitchCommandAction.sendDisableCommand(energyCompany, liteHw, null);
        }
        logEvent(accountId, energyCompanyId,
                 liteHw.getManufacturerSerialNumber(),
                 ActivityLogActions.HARDWARE_DISABLE_ACTION, userContext);
    }

    @Override
    public void enable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws WebClientException {
        LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyId);

        if (VersionTools.staticLoadGroupMappingExists()) {
            addSwitchCommand(energyCompanyId, accountId, inventoryId,
                             SwitchCommandQueue.SWITCH_COMMAND_ENABLE);
        } else {
            YukonSwitchCommandAction.sendEnableCommand(energyCompany, liteHw, null);
        }
        logEvent(accountId, energyCompanyId,
                 liteHw.getManufacturerSerialNumber(),
                 ActivityLogActions.HARDWARE_ENABLE_ACTION, userContext);
    }

    @Override
    public List<String> getConfigCommands(int inventoryId, int energyCompanyId,
            boolean includeInService) throws WebClientException {
        LiteStarsLMHardware liteHw = (LiteStarsLMHardware) starsInventoryBaseDao.getByInventoryId(inventoryId);
        LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany(energyCompanyId);
        boolean trackHardwareAddressing =
            energyCompanyRolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.TRACK_HARDWARE_ADDRESSING,
                                                                 energyCompany);
        List<String> retVal = Lists.newArrayList();

        if (enrollmentDao.isEnrolled(inventoryId)) {
            if (includeInService) {
                retVal.addAll(YukonSwitchCommandAction.getEnableCommands(liteHw, energyCompany, true));
            }
            String[] commands = YukonSwitchCommandAction.getConfigCommands(liteHw, energyCompany,
                                                                           trackHardwareAddressing, null);
            retVal.addAll(Lists.newArrayList(commands));
        } else {
            retVal.addAll(YukonSwitchCommandAction.getDisableCommands(liteHw));
        }
        return retVal;
    }

    private void addSwitchCommand(int energyCompanyId, int accountId,
            int inventoryId, String commandType) {
        SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
        cmd.setEnergyCompanyID(energyCompanyId);
        cmd.setAccountID(accountId);
        cmd.setInventoryID(inventoryId);
        cmd.setCommandType(commandType);
        SwitchCommandQueue.getInstance().addCommand(cmd, true);
    }

    private void logEvent(int accountId, int energyCompanyId, String serialNumber, String action,
            YukonUserContext userContext) {
        LogHelper.debug(log, 
                        "%s was done to %s serial number on %d accountId in %d energyCompanyId", 
                        action, serialNumber, accountId, energyCompanyId);

        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        ActivityLogger.logEvent(userContext.getYukonUser().getUserID(),
                                accountId, energyCompanyId,
                                customerAccount.getCustomerId(),
                                action,
                                "Serial #:" + serialNumber);
                                
        if (ActivityLogActions.HARDWARE_DISABLE_ACTION.equals(action)) {
            hardwareEventLogService.hardwareDisabled(userContext.getYukonUser(),
                                                     serialNumber,
                                                     customerAccount.getAccountNumber());
        } else if (ActivityLogActions.HARDWARE_ENABLE_ACTION.equals(action)) {
            hardwareEventLogService.hardwareEnabled(userContext.getYukonUser(),
                                                    serialNumber,
                                                    customerAccount.getAccountNumber());
        }
        
        
    }

    @Autowired
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    @Autowired
    public void setHardwareEventLogService(HardwareEventLogService hardwareEventLogService) {
        this.hardwareEventLogService = hardwareEventLogService;
    }
    
    @Autowired
    public void setStarsInventoryBaseDao(StarsInventoryBaseDao starsInventoryBaseDao) {
        this.starsInventoryBaseDao = starsInventoryBaseDao;
    }

    @Autowired
    public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
        this.enrollmentDao = enrollmentDao;
    }

    @Autowired
    public void setEnergyCompanyRolePropertyDao(
            EnergyCompanyRolePropertyDao energyCompanyRolePropertyDao) {
        this.energyCompanyRolePropertyDao = energyCompanyRolePropertyDao;
    }
}
