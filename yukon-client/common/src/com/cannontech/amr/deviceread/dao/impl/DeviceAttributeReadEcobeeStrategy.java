package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
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
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.GroupCommandCompletionCallback;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.impl.EcobeePointUpdateServiceImpl;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Iterables;

public class DeviceAttributeReadEcobeeStrategy implements DeviceAttributeReadStrategy {
    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadEcobeeStrategy.class);

    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private EcobeePointUpdateServiceImpl ecobeePointUpdateServiceImpl;

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
            final DeviceAttributeReadStrategyCallback delegateCallback,
            DeviceRequestType type, LiteYukonUser user) {
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
                        YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.attributeRead.general.readError", e.getMessage());
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
        // TODO: support group reads
    }
}