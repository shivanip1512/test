package com.cannontech.stars.dr.hardwareConfig;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.clientutils.LogHelper;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.HardwareEventLogService;
import com.cannontech.common.version.VersionTools;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandService;
import com.cannontech.stars.dr.hardware.service.impl.PorterExpressComCommandBuilder;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.util.SwitchCommandQueue;
import com.cannontech.user.YukonUserContext;

public class HardwareConfigServiceImpl implements HardwareConfigService {
    private final static Logger log = YukonLogManager.getLogger(HardwareConfigServiceImpl.class);

    @Autowired private CustomerAccountDao customerAccountDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private EnergyCompanySettingDao ecSettingDao;
    @Autowired private EnrollmentDao enrollmentDao;
    @Autowired private HardwareEventLogService hardwareEventLogService;
    @Autowired private InventoryBaseDao inventoryBaseDao;
    @Autowired private LmHardwareCommandService lmHardwareCommandService;
    @Autowired private PorterExpressComCommandBuilder xcomCommandBuilder;

    @Override
    public void disable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws CommandCompletionException {
        LiteLmHardwareBase liteHw = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);

        if (VersionTools.staticLoadGroupMappingExists()) {
            addSwitchCommand(energyCompanyId, accountId, inventoryId,
                             SwitchCommandQueue.SWITCH_COMMAND_DISABLE);
        } else {
            LiteYukonUser user = ecDao.getEnergyCompanyByInventoryId(inventoryId).getUser();
            LmHardwareCommand command = new LmHardwareCommand();
            command.setDevice(liteHw);
            command.setType(LmHardwareCommandType.OUT_OF_SERVICE);
            command.setUser(user);

            lmHardwareCommandService.sendOutOfServiceCommand(command);
        }
        logEvent(accountId, energyCompanyId,
                 liteHw.getManufacturerSerialNumber(),
                 ActivityLogActions.HARDWARE_DISABLE_ACTION, userContext);
    }

    @Override
    public void enable(int inventoryId, int accountId, int energyCompanyId,
            YukonUserContext userContext) throws CommandCompletionException {
        LiteLmHardwareBase liteHw = (LiteLmHardwareBase) inventoryBaseDao.getByInventoryId(inventoryId);

        if (VersionTools.staticLoadGroupMappingExists()) {
            addSwitchCommand(energyCompanyId, accountId, inventoryId,
                             SwitchCommandQueue.SWITCH_COMMAND_ENABLE);
        } else {
            LiteYukonUser user = ecDao.getEnergyCompanyByInventoryId(inventoryId).getUser();
            LmHardwareCommand command = new LmHardwareCommand();
            command.setDevice(liteHw);
            command.setType(LmHardwareCommandType.IN_SERVICE);
            command.setUser(user);

            lmHardwareCommandService.sendInServiceCommand(command);
        }
        logEvent(accountId, energyCompanyId,
                 liteHw.getManufacturerSerialNumber(),
                 ActivityLogActions.HARDWARE_ENABLE_ACTION, userContext);
    }

    private void addSwitchCommand(int energyCompanyId, int accountId, int inventoryId, String commandType) {
        SwitchCommandQueue.SwitchCommand cmd = new SwitchCommandQueue.SwitchCommand();
        cmd.setEnergyCompanyID(energyCompanyId);
        cmd.setAccountID(accountId);
        cmd.setInventoryID(inventoryId);
        cmd.setCommandType(commandType);
        SwitchCommandQueue.getInstance().addCommand(cmd, true);
    }

    private void logEvent(int accountId, int ecId, String serialNumber, String action, YukonUserContext context) {
        LogHelper.debug(log, 
                        "%s was done to %s serial number on %d accountId in %d energyCompanyId", 
                        action, serialNumber, accountId, ecId);

        CustomerAccount customerAccount = customerAccountDao.getById(accountId);
        ActivityLogger.logEvent(context.getYukonUser().getUserID(),
                                accountId, ecId,
                                customerAccount.getCustomerId(),
                                action,
                                "Serial #:" + serialNumber);
                                
        if (ActivityLogActions.HARDWARE_DISABLE_ACTION.equals(action)) {
            hardwareEventLogService.hardwareDisabled(context.getYukonUser(),
                                                     serialNumber,
                                                     customerAccount.getAccountNumber());
        } else if (ActivityLogActions.HARDWARE_ENABLE_ACTION.equals(action)) {
            hardwareEventLogService.hardwareEnabled(context.getYukonUser(),
                                                    serialNumber,
                                                    customerAccount.getAccountNumber());
        }
    }

}