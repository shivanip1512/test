package com.cannontech.web.dr.ecobee;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.impl.EcobeePointUpdateServiceImpl;
import com.cannontech.jobs.support.YukonTaskBase;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.google.common.collect.ImmutableSet;
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

        List<LiteYukonPAObject> ecobeeDevices = getAllEcobeeDevices();

        Map<String, PaoIdentifier> ecobeeDevicesBySerialNumber = Maps.newHashMapWithExpectedSize(ecobeeDevices.size());

        for (LiteYukonPAObject ecobeePao : ecobeeDevices) {
            PaoIdentifier pao = ecobeePao.getPaoIdentifier();
            String ecobeeSerialNumber = lmHardwareBaseDao.getSerialNumberForDevice(pao.getPaoId());
            ecobeeDevicesBySerialNumber.put(ecobeeSerialNumber, pao);
        }

        LocalDate startDate = new LocalDate().minusDays(1);
        LocalDate endDate = new LocalDate();
        Range<LocalDate> dateRange = Range.inclusive(startDate, endDate);

        if (log.isInfoEnabled()) {
            log.info("Reading device data for " + ecobeeDevices.size() + " ecobee devices. Time range: "
                + dateRange);
        }

        List<EcobeeDeviceReadings> allDeviceReadings =
            ecobeeCommunicationService.readDeviceData(SelectionType.MANAGEMENT_SET, ImmutableSet.of("/"), dateRange);
        for (EcobeeDeviceReadings deviceReadings : allDeviceReadings) {
            if (ecobeeDevicesBySerialNumber.containsKey(deviceReadings.getSerialNumber())) {
                ecobeePointUpdateServiceImpl.updatePointData(ecobeeDevicesBySerialNumber.get(deviceReadings.getSerialNumber()), deviceReadings);
            } else {
                log.info("Ecobee Device may be missing in Yukon System. SerialNumber : " + deviceReadings.getSerialNumber());
            }
        }

        if (log.isInfoEnabled()) {
            log.info("Ecobee daily reads complete for all types.");
        }
    }

    private List<LiteYukonPAObject> getAllEcobeeDevices() {

        List<LiteYukonPAObject> ecobeeDevices = new ArrayList<>();
        PaoType.getEcobeeTypes().forEach(type -> ecobeeDevices.addAll(paoDao.getLiteYukonPAObjectByType(type)));
        return ecobeeDevices;
    }
}
