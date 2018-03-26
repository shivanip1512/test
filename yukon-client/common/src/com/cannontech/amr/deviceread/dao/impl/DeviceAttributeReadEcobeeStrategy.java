package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
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
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class DeviceAttributeReadEcobeeStrategy implements DeviceAttributeReadStrategy {
    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadEcobeeStrategy.class);

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private EcobeePointUpdateServiceImpl ecobeePointUpdateServiceImpl;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    @Override
    public StrategyType getStrategy() {
        return StrategyType.ECOBEE;
    }

    @Override
    public boolean canRead(PaoType paoType) {
        return paoType.isEcobee();
    }

    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> devices) {
        return !Iterables.isEmpty(devices);
    }

    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> devices, DeviceAttributeReadCallback callback,
            CommandRequestExecution execution, LiteYukonUser user) {
        try {
            // All devices succeeded.
            Multimap<PaoIdentifier, PointValueHolder> devicesToPointValues = initiateRead(devices);
            for (PaoIdentifier pao : devicesToPointValues.keySet()) {
                for (PointValueHolder pointValue : devicesToPointValues.values()) {
                    callback.receivedValue(pao, pointValue);
                }
                commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution, pao.getPaoId(), 0);
                callback.receivedLastValue(pao, "");
            }
        } catch (EcobeeCommunicationException error) {
            /*
             * Read for all devices failed. This error
             * is treated the same as "no porter connection" error. There is no
             * command request execution result created for the entries.
             */

            DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.TIMEOUT);
            MessageSourceResolvable detail = YukonMessageSourceResolvable.createSingleCodeWithArguments(
                "yukon.common.device.attributeRead.general.readError", error.getMessage());
            SpecificDeviceErrorDescription deviceError = new SpecificDeviceErrorDescription(errorDescription, detail);
            log.error(error);
            callback.receivedException(deviceError);
        }
        callback.complete(getStrategy());
    }

    private Multimap<PaoIdentifier, PointValueHolder> initiateRead(Iterable<PaoMultiPointIdentifier> devices)
            throws EcobeeCommunicationException {

        Multimap<PaoIdentifier, PointValueHolder> devicesToPointValues = ArrayListMultimap.create();
        Map<String, PaoIdentifier> ecobeeDevices = new HashMap<>();

        for (PaoMultiPointIdentifier paoMultiPointIdentifier : devices) {
            PaoIdentifier pao = paoMultiPointIdentifier.getPao();
            String ecobeeSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(pao.getPaoId());
            ecobeeDevices.put(ecobeeSerialNumber, pao);
        }

        // Set end of range at 1 hour before current time (which is about the most recent data ecobee will have)
        MutableDateTime mutableDateTime = new MutableDateTime();
        mutableDateTime.addHours(-1);;
        Instant end = mutableDateTime.toInstant();

        // Set start of range at the start of the current day.
        mutableDateTime.setMillisOfDay(0);
        Instant start = mutableDateTime.toInstant();

        Range<Instant> dateRange = Range.inclusive(start, end);

        for (List<String> serialNumbers : Iterables.partition(ecobeeDevices.keySet(), 25)) {
            List<EcobeeDeviceReadings> allDeviceReadings =
                ecobeeCommunicationService.readDeviceData(serialNumbers, dateRange);
            for (EcobeeDeviceReadings deviceReadings : allDeviceReadings) {
                PaoIdentifier paoIdentifier = ecobeeDevices.get(deviceReadings.getSerialNumber());
                Set<PointValueHolder> pointValues =
                    ecobeePointUpdateServiceImpl.updatePointData(paoIdentifier, deviceReadings);
                devicesToPointValues.putAll(paoIdentifier, pointValues);
            }
        }

        return devicesToPointValues;
    }

    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy) {
        return devicesForThisStrategy.size();
    }

    @Override
    public void cancel(CollectionActionResult result, LiteYukonUser user) {
        // not supported
        
    }
}