package com.cannontech.stars.dr.hardware.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.commands.exception.CommandCompletionException;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.eatonCloud.EatonCloudMessageListener.CommandParam;
import com.cannontech.dr.pxmw.model.v1.PxMWCommandRequestV1;
import com.cannontech.dr.pxmw.service.v1.PxMWCommunicationServiceV1;
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
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EatonCloudCommandStrategy implements LmHardwareCommandStrategy {
    private static final Logger log = YukonLogManager.getLogger(EatonCloudCommandStrategy.class);
    @Autowired private DeviceDao deviceDao;
    @Autowired private PxMWCommunicationServiceV1 pxMWCommunicationService;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private InventoryDao inventoryDao;
    @Autowired private OptOutEventDao optOutEventDao;
    @Autowired private InventoryBaseDao inventoryBaseDao;

    // device id, relay, event id
    private final Cache<Pair<Integer, Integer>, Integer> devicesToEvents = CacheBuilder.newBuilder().build();

    @Override
    public void sendCommand(LmHardwareCommand command) throws CommandCompletionException {
        switch (command.getType()) {
        case SHED:
        case RESTORE:
            LiteLmHardwareBase hardware = inventoryBaseDao.getHardwareByDeviceId(command.getDevice().getDeviceID());
            int accountId = inventoryDao.getAccountIdForInventory(hardware.getInventoryID());
            if (optOutEventDao.isOptedOut(hardware.getInventoryID(), accountId)) {
                log.info("Unable to send {} to device:{} user oped out", command.getType(), command.getDevice());
                throw new CommandCompletionException("User opted out");
            }
            String deviceGuid = deviceDao.getGuid(command.getDevice().getDeviceID());
            try {
                pxMWCommunicationService.sendCommand(deviceGuid, new PxMWCommandRequestV1("LCR_Control", getParams(command)));
            } catch (Exception e) {
                log.error("Error Sending Command : {} ", command, e);
                throw new CommandCompletionException("Error sending Command to device. See logs for details.", e);
            }
            break;
        default:
            log.info("Sending {} is not supported for device:{}", command.getType(), command.getDevice());
            break;
        }
    }

    private Map<String, Object> getParams(LmHardwareCommand command) {
        Map<String, Object> params = new LinkedHashMap<>();
        Integer relay = (Integer) command.getParams().get(LmHardwareCommandParam.RELAY);
        params.put(CommandParam.VRELAY.getParamName(), relay);
        Pair<Integer, Integer> cacheKey = Pair.of(command.getDevice().getDeviceID(), relay);
        switch (command.getType()) {
        case SHED:
            Integer eventId = nextValueHelper.getNextValue("PxMWEventIdIncrementor");
            Duration duration = (Duration) command.getParams().get(LmHardwareCommandParam.DURATION);
            params.put(CommandParam.CYCLE_PERCENT.getParamName(), 100);
            params.put(CommandParam.CYCLE_COUNT.getParamName(), 1);
            params.put(CommandParam.START_TIME.getParamName(), System.currentTimeMillis() / 1000);
            params.put(CommandParam.STOP_TIME.getParamName(), (System.currentTimeMillis() + duration.getMillis()) / 1000);
            params.put(CommandParam.CRITICALITY.getParamName(), 255);
            params.put(CommandParam.CONTROL_FLAGS.getParamName(), 0);
            params.put(CommandParam.EVENT_ID.getParamName(), eventId);
            devicesToEvents.put(cacheKey, eventId);
            break;
        case RESTORE:
            params.put(CommandParam.STOP_TIME.getParamName(), 0);
            params.put(CommandParam.FLAGS.getParamName(), 0);
            params.put(CommandParam.STOP_FLAGS.getParamName(), 0);
            eventId = devicesToEvents.getIfPresent(cacheKey);
            if(eventId != null) {
                params.put(CommandParam.EVENT_ID.getParamName(), eventId);
                devicesToEvents.invalidate(cacheKey);
            } else {
                throw new BadConfigurationException(
                        "Event Id is not in cache unable to send " + command.getType() + "  to device:"
                                + command.getDevice().getManufacturerSerialNumber() + " " + command.getDevice().getDeviceID());
            }
            break;
        default:
            break;
        }
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
