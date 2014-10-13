package com.cannontech.web.dr.ecobee;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.impl.EcobeePointUpdateServiceImpl;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class EcobeePointUpdateTask extends YukonTaskBase {

    private final Logger log = YukonLogManager.getLogger(EcobeePointUpdateTask.class);

    @Autowired private PaoDao paoDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private EcobeeCommunicationService ecobeeCommunicationService;
    @Autowired private EcobeePointUpdateServiceImpl ecobeePointUpdateServiceImpl;

    @Override
    public void start() {
        log.info("Starting ecobee daily reads.");

        for (PaoType type : PaoType.getEcobeeTypes()) {
            List<LiteYukonPAObject> ecobeeDevices = paoDao.getLiteYukonPAObjectByType(type);

            Map<String, PaoIdentifier> ecobeeDevicesBySerialNumber =
                Maps.newHashMapWithExpectedSize(ecobeeDevices.size());

            for (LiteYukonPAObject ecobeePao : ecobeeDevices) {
                PaoIdentifier pao = ecobeePao.getPaoIdentifier();
                String ecobeeSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(pao.getPaoId());
                ecobeeDevicesBySerialNumber.put(ecobeeSerialNumber, pao);
            }

            MutableDateTime startDate = new MutableDateTime();
            startDate.addDays(-1);
            startDate.setMillisOfDay(0);
            Instant start = startDate.toInstant();
            Instant end = start.plus(Duration.standardDays(1));
            Range<Instant> dateRange = Range.inclusive(start, end);

            if (log.isInfoEnabled()) {
                log.info("Reading device data for " + type + " " + ecobeeDevices.size()
                    + " ecobee devices. Time range: " + dateRange);
            }

            for (List<String> serialNumbers : Iterables.partition(ecobeeDevicesBySerialNumber.keySet(), 25)) {
                List<EcobeeDeviceReadings> allDeviceReadings =
                    ecobeeCommunicationService.readDeviceData(serialNumbers, dateRange);
                for (EcobeeDeviceReadings deviceReadings : allDeviceReadings) {
                    ecobeePointUpdateServiceImpl.
                        updatePointData(ecobeeDevicesBySerialNumber.get(deviceReadings.getSerialNumber()), deviceReadings);
                }
            }

            if (log.isInfoEnabled()) {
                log.info(type + " daily reads complete.");
            }
        }
    }
}
