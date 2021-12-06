package com.cannontech.stars.dr.hardware.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.events.loggers.EatonCloudEventLogService;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommandRequestV1;
import com.cannontech.dr.eatonCloud.service.EatonCloudSendControlService.CommandParam;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudCommunicationServiceV1;
import com.cannontech.stars.core.dao.InventoryBaseDao;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.LmCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandParam;
import com.cannontech.stars.dr.hardware.model.Thermostat;
import com.cannontech.stars.dr.hardware.service.HardwareStrategyType;
import com.cannontech.stars.dr.hardware.service.LmHardwareCommandStrategy;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatManualEvent;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleUpdateResult;

public class EatonCloudCommandStrategy implements LmHardwareCommandStrategy {
    private static final Logger log = YukonLogManager.getLogger(EatonCloudCommandStrategy.class);
    @Autowired private DeviceDao deviceDao;
    @Autowired private EatonCloudEventLogService eatonCloudEventLogService;
    @Autowired private EatonCloudCommunicationServiceV1 eatonCloudCommunicationService;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;

    @Override
    public void sendCommand(LmHardwareCommand command) throws CommandCompletionException {
        int deviceId = command.getDevice().getDeviceID();
        String deviceName = command.getDevice().getDeviceLabel();
        String deviceGuid = deviceDao.getGuid(deviceId);
        switch (command.getType()) {
        case SHED:
            Map<String, Object> shedParams = getShedParams(command);
            checkOptout(command);
            sendRequest(command, shedParams);
            eatonCloudEventLogService.sendShed(deviceName,
                    deviceGuid,
                    (String) shedParams.get(CommandParam.EVENT_ID.getParamName()),
                    "1",
                    (Integer) shedParams.get(CommandParam.CYCLE_PERCENT.getParamName()),
                    (Integer) shedParams.get(CommandParam.CYCLE_PERIOD.getParamName()),
                    (Integer) shedParams.get(CommandParam.CRITICALITY.getParamName()),
                    (Integer) command.getParams().get(LmHardwareCommandParam.RELAY));
            break;
        case RESTORE:
            checkOptout(command);
            Map<String, Object> restoreParams =  getRestoreParams(command);
            sendRequest(command, getRestoreParams(command));
            eatonCloudEventLogService.sendRestore(deviceName, deviceGuid,
                    (String) restoreParams.get(CommandParam.EVENT_ID.getParamName()),
                    (Integer) command.getParams().get(LmHardwareCommandParam.RELAY));
            break;
        case TEMP_OUT_OF_SERVICE:
            sendRequest(command, null);
            break;
        default:
            log.info("Sending {} is not supported for device:{}", command.getType(), command.getDevice());
            break;
        }
    }

    private void sendRequest(LmHardwareCommand command, Map<String, Object> params) throws CommandCompletionException {
        String deviceGuid = deviceDao.getGuid(command.getDevice().getDeviceID());
        try {
            eatonCloudCommunicationService.sendCommand(deviceGuid, new EatonCloudCommandRequestV1("LCR_Control", params));
        } catch (Exception e) {
            log.error("Error Sending Command : {} ", command, e);
            throw new CommandCompletionException("Error sending Command to device. See logs for details.", e);
        }
    }

    /**
     * If user opted out device this method will throw exception
     * @throws CommandCompletionException
     */
    private void checkOptout(LmHardwareCommand command) throws CommandCompletionException {
        LiteLmHardwareBase hardware = inventoryBaseDao.getHardwareByDeviceId(command.getDevice().getDeviceID());
        int accountId = inventoryDao.getAccountIdForInventory(hardware.getInventoryID());
        if (optOutEventDao.isOptedOut(hardware.getInventoryID(), accountId)) {
            log.info("Unable to send {} to device:{} user oped out", command.getType(), command.getDevice());
            throw new CommandCompletionException("User opted out");
        }
    }


    private Map<String, Object> getShedParams(LmHardwareCommand command) {
        Map<String, Object> params = new LinkedHashMap<>();
        Integer eventId = nextValueHelper.getNextValue("EatonCloudEventIdIncrementor");
        Integer relay = (Integer) command.getParams().get(LmHardwareCommandParam.RELAY);
        Duration duration = (Duration) command.getParams().get(LmHardwareCommandParam.DURATION);

        // See LCR Control Command Payloads reference:
        // https://confluence-prod.tcc.etn.com/pages/viewpage.action?pageId=137056391

        params.put(CommandParam.VRELAY.getParamName(), relay - 1);
        params.put(CommandParam.CYCLE_PERCENT.getParamName(), 100);
        params.put(CommandParam.CYCLE_PERIOD.getParamName(), Math.toIntExact(duration.getStandardMinutes()));
        params.put(CommandParam.CYCLE_COUNT.getParamName(), 1);
        params.put(CommandParam.START_TIME.getParamName(), System.currentTimeMillis() / 1000);
        params.put(CommandParam.EVENT_ID.getParamName(), eventId);
        params.put(CommandParam.CRITICALITY.getParamName(), 255);
        params.put(CommandParam.RANDOMIZATION.getParamName(), 0);
        params.put(CommandParam.CONTROL_FLAGS.getParamName(), 0);
        params.put(CommandParam.STOP_TIME.getParamName(), (System.currentTimeMillis() + duration.getMillis()) / 1000);
        params.put(CommandParam.STOP_FLAGS.getParamName(), 0);

        return params;
    }
    
    private Map<String, Object> getRestoreParams(LmHardwareCommand command) {
        Map<String, Object> params = new LinkedHashMap<>();
        Integer relay = (Integer) command.getParams().get(LmHardwareCommandParam.RELAY);
        params.put(CommandParam.VRELAY.getParamName(), relay);
        params.put(CommandParam.FLAGS.getParamName(), 0);
        return params;
    }

    @Override
    public HardwareStrategyType getType() {
        return HardwareStrategyType.EATON_CLOUD;
    }

    @Override
    public boolean canHandle(HardwareType type) {
        return type.isEatonCloud();
    }

    @Override
    public void doManualAdjustment(ThermostatManualEvent event, Thermostat thermostat, LiteYukonUser user)
            throws CommandCompletionException {
        throw new UnsupportedOperationException();

    }

    @Override
    public ThermostatScheduleUpdateResult doScheduleUpdate(CustomerAccount account, AccountThermostatSchedule ats,
            ThermostatScheduleMode mode, Thermostat stat, LiteYukonUser user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendTextMessage(YukonTextMessage message) {
        throw new UnsupportedOperationException();

    }

    @Override
    public void cancelTextMessage(YukonCancelTextMessage message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canBroadcast(LmCommand command) {
        return false;
    }

    @Override
    public void sendBroadcastCommand(LmCommand command) throws CommandCompletionException {
        throw new UnsupportedOperationException();

    }

    @Override
    public void verifyCanSendConfig(LmHardwareCommand command) throws BadConfigurationException {
        throw new UnsupportedOperationException();
    }
}
