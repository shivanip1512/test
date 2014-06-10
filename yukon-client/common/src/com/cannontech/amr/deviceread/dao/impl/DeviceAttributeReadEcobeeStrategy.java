package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.device.StrategyType;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadError;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadErrorType;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecutionResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.impl.EcobeePointUpdateServiceImpl;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Iterables;

public class DeviceAttributeReadEcobeeStrategy implements DeviceAttributeReadStrategy {
    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadEcobeeStrategy.class);

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private EcobeePointUpdateServiceImpl ecobeePointUpdateServiceImpl;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;

    @Override
    public StrategyType getType() {
        return StrategyType.ECOBEE;
    }

    @Override
    public boolean canRead(PaoType paoType) {
        return paoType.isEcobee();
    }

    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> devices, LiteYukonUser user) {
        // TODO decide if anything more is required
        return true;
    }

    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> devices,
            final DeviceAttributeReadStrategyCallback delegateCallback, DeviceRequestType type, LiteYukonUser user) {
        Map<String, PaoIdentifier> ecobeeDevices = new HashMap<>();

        for (PaoMultiPointIdentifier paoMultiPointIdentifier : devices) {
            PaoIdentifier pao = paoMultiPointIdentifier.getPao();
            String ecobeeSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(pao.getPaoId());
            ecobeeDevices.put(ecobeeSerialNumber, pao);
        }

        Instant end = new Instant().minus(Duration.standardMinutes(15));
        MutableDateTime mutableStartTime = new MutableDateTime(end.minus(Duration.standardHours(24)));
        // Start request at top of hour requested. This makes some point archiving calculations easier
        mutableStartTime.setMinuteOfHour(0);
        Instant start = mutableStartTime.toInstant();
        Range<Instant> lastTwentyFourHours = Range.inclusive(start, end);
        try {
            for (List<String> serialNumbers : Iterables.partition(ecobeeDevices.keySet(), 25)) {
                List<EcobeeDeviceReadings> allDeviceReadings =
                    ecobeeCommunicationService.readDeviceData(serialNumbers, lastTwentyFourHours);
                for (EcobeeDeviceReadings deviceReadings : allDeviceReadings) {
                    ecobeePointUpdateServiceImpl.updatePointData(ecobeeDevices.get(deviceReadings.getSerialNumber()),
                        deviceReadings);
                }
            }
        } catch (EcobeeCommunicationException e) {
            MessageSourceResolvable summary =
                YukonMessageSourceResolvable.createSingleCodeWithArguments(
                    "yukon.common.device.attributeRead.general.readError", e.getMessage());
            DeviceAttributeReadError exceptionError =
                new DeviceAttributeReadError(DeviceAttributeReadErrorType.EXCEPTION, summary);
            log.error("Unable to read device.", e);
            delegateCallback.receivedException(exceptionError);
        }
        delegateCallback.complete();
    }

    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy) {
        return devicesForThisStrategy.size();
    }

    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> points,
            final GroupCommandCompletionCallback groupCallback, DeviceRequestType type,
            final YukonUserContext userContext) {
        initiateRead(points, new DeviceAttributeReadStrategyCallback() {
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                CommandRequestDevice command = new CommandRequestDevice();
                command.setDevice(new SimpleDevice(pao));
                groupCallback.receivedValue(command, value);
            }

            @Override
            public void receivedLastValue(PaoIdentifier pao) {
                //ignore the last value if this device had an error
                if (groupCallback.getErrors().get(new SimpleDevice(pao)) == null) {
                    CommandRequestDevice command = new CommandRequestDevice();
                    command.setDevice(new SimpleDevice(pao));
                    groupCallback.receivedLastResultString(command, "");
                    saveCommandRequestExecutionResult(groupCallback.getExecution(),  pao.getPaoId(),  0);
                }
            }

            @Override
            public void receivedError(PaoIdentifier pao, DeviceAttributeReadError error) {
                CommandRequestDevice command = new CommandRequestDevice();
                command.setDevice(new SimpleDevice(pao));
                SpecificDeviceErrorDescription errorDescription = getErrorDescription(error);
                groupCallback.receivedLastError(command, errorDescription);
                saveCommandRequestExecutionResult(groupCallback.getExecution(), pao.getPaoId(),
                    error.getType().getErrorCode());
            }

            @Override
            public void receivedException(DeviceAttributeReadError error) {
                SpecificDeviceErrorDescription errorDescription = getErrorDescription(error);
                CommandRequestDevice command = new CommandRequestDevice();
                groupCallback.receivedLastError(command, errorDescription);
            }

            @Override
            public void complete() {
                groupCallback.complete();
            }

            private SpecificDeviceErrorDescription getErrorDescription(DeviceAttributeReadError error) {
                String summary = "";
                String detail = "";
                if (error.getSummary() != null) {
                    summary = messageSourceAccessor.getMessage(error.getSummary());
                }
                if (error.getDetail() != null) {
                    detail = messageSourceAccessor.getMessage(error.getDetail());
                }
                DeviceErrorDescription desc =
                    new DeviceErrorDescription(error.getType().getErrorCode(), "", "", summary, detail);
                SpecificDeviceErrorDescription errorDescription = new SpecificDeviceErrorDescription(desc, detail);
                return errorDescription;
            }
        }, type, userContext.getYukonUser());
    }

    private void saveCommandRequestExecutionResult(CommandRequestExecution execution, int deviceId, int errorCode) {
        CommandRequestExecutionResult result = new CommandRequestExecutionResult();
        result.setCommandRequestExecutionId(execution.getId());
        result.setCommand(execution.getCommandRequestExecutionType().getShortName());
        result.setCompleteTime(new Date());
        result.setDeviceId(deviceId);
        result.setErrorCode(errorCode);
        commandRequestExecutionResultDao.saveOrUpdate(result);
    }
}