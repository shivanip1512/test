package com.cannontech.dr.ecobee.service.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.ecobee.message.ZeusGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostat;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public class EcobeeZeusSyncService {

    @Autowired private ConfigurationSource configurationSource;
    @Autowired private EcobeeZeusGroupService ecobeeZeusGroupService;
    @Autowired private EcobeeZeusCommunicationService ecobeeZeusCommunicationService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> scheduledFuture;

    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusSyncService.class);

    @PostConstruct
    public void init() {
        log.debug("Starting scheduler to populate mapping table for Zeus");

        scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(() -> {
            syncZeusGroup();
        }, 1, 1, TimeUnit.MINUTES);

    }

    private void syncZeusGroup() {
        log.info("Populating table to Zeus mapping");
        boolean groupMappingExists = ecobeeZeusGroupService.getGroupCount() == 0 ? false : true;
        boolean thermostatMappingExists = ecobeeZeusGroupService.getAllThermostatCount() == 0 ? false : true;
        if (!groupMappingExists || !thermostatMappingExists) {
            log.debug("Tables are not empty starting process to populate data");
            try {
                List<ZeusGroup> zeusGroups = ecobeeZeusCommunicationService.getAllGroups();
                log.debug("No of ZeusGroups found " + zeusGroups.size());
                zeusGroups.forEach(zeusGroup -> {
                    int yukonGroupId;
                    String zeusGroupName = zeusGroup.getName();
                    if (StringUtils.isNumeric(zeusGroupName)) {
                        if (zeusGroupName.contains("/")) {
                            yukonGroupId = Integer.parseInt(zeusGroupName.replace("/", ""));
                        } else {
                            yukonGroupId = Integer.parseInt(zeusGroupName);
                        }
                        String zeusGroupId = zeusGroup.getGroupId();
                        if (!groupMappingExists) {
                            ecobeeZeusGroupService.mapGroupIdToZeusGroup(yukonGroupId, zeusGroupId, zeusGroupName);
                        }
                        if (!thermostatMappingExists) {
                            log.debug("Getting thermostats for group " + zeusGroupId);
                            List<ZeusThermostat> zeusThermostats = ecobeeZeusCommunicationService
                                    .getThermostatsInGroup(zeusGroupId);
                            mapInventoryToZeusGroup(zeusThermostats, zeusGroupId);
                            
                        }
                    } else {
                        log.info("Skipping Zeus group " + zeusGroupName);
                    }
                });
            } catch (Exception e) {
                log.error("Error communicating with Ecobee" + e);
            }
        } else {
            // stop the scheduler, table have values.
            log.debug("Stopping Zeus mapping schedular as tables are not empty");
            scheduledFuture.cancel(true);
            scheduledExecutorService.shutdown();
        }

    }

    private void mapInventoryToZeusGroup(List<ZeusThermostat> zeusThermostats, String zeusGroupId) {
        zeusThermostats.forEach(zeusThermostat -> {
            try {
                LMHardwareBase lMHardwareBase = lmHardwareBaseDao.getBySerialNumber(zeusThermostat.getSerialNumber());
                ecobeeZeusGroupService.mapInventoryToZeusGroupId(lMHardwareBase.getInventoryId(), zeusGroupId);
                log.debug("Mapped total thermostats " + zeusThermostats.size() + " to group", zeusGroupId);
            } catch (NotFoundException e) {
                log.error("Hardware not found with serialNumber. Skipping serialNumber " + zeusThermostat.getSerialNumber());
            }
        });

    }
}
