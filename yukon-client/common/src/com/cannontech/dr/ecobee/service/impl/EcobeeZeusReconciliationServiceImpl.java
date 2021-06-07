package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.message.ZeusGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostat;
import com.cannontech.dr.ecobee.model.EcobeeZeusGroupDeviceMapping;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;

public class EcobeeZeusReconciliationServiceImpl implements EcobeeZeusReconciliationService {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusReconciliationServiceImpl.class);
    
    @Autowired private EcobeeZeusCommunicationService communicationService;
    @Override
    public int runReconciliationReport() throws EcobeeCommunicationException {
        List<EcobeeZeusGroupDeviceMapping> groupDeviceMapping = getZeusGroupDeviceMapping();

        /* TODO : updated in YUK-23931
        // get structure from Yukon
        Multimap<Integer, String> groupToDevicesMap = ecobeeGroupDeviceMappingDao.getSerialNumbersByGroupId();
        List<String> allSerialNumbers = ecobeeGroupDeviceMappingDao.getAllEcobeeSerialNumbers();

        // compare structures and build list of errors
        EcobeeZeusReconciliationReport report = generateReport(groupToDevicesMap, allSerialNumbers, groupDeviceMapping);

        // save errors to database and return report ID
        return reconciliationReportDao.insertReport(report);
        */
        return 0;
    }

    private List<EcobeeZeusGroupDeviceMapping> getZeusGroupDeviceMapping() {
        log.debug("Retrieving ecobee groups and its corresponding thermostats.");

        List<ZeusGroup> zeusGroups = communicationService.getAllGroups();
        List<EcobeeZeusGroupDeviceMapping> zeusGroupDeviceMaps = new ArrayList<EcobeeZeusGroupDeviceMapping>();

        zeusGroups.forEach(group -> {
            EcobeeZeusGroupDeviceMapping deviceGroupMap = new EcobeeZeusGroupDeviceMapping();
            deviceGroupMap.setGroupId(group.getGroupId());
            if (group.getParentGroupId() != null)
                deviceGroupMap.setParentGroupId(group.getParentGroupId());

            List<ZeusThermostat> thermostatsInGroup = communicationService.getThermostatsInGroup(group.getGroupId());
            List<String> serialNumbers = thermostatsInGroup.stream()
                    .map(thermostat -> thermostat.getSerialNumber())
                    .collect(Collectors.toList());

            deviceGroupMap.setThermostatsSerialNumber(serialNumbers);
            zeusGroupDeviceMaps.add(deviceGroupMap);
        });

        return zeusGroupDeviceMaps;
    }
}