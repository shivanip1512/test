package com.cannontech.dr.ecobee.service.impl;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;
import com.cannontech.dr.ecobee.dao.EcobeeZeusReconciliationReportDao;
import com.cannontech.dr.ecobee.message.ZeusGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostat;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class EcobeeZeusSyncService {

    @Autowired private EcobeeZeusGroupService ecobeeZeusGroupService;
    @Autowired private EcobeeZeusReconciliationReportDao ecobeeZeusReconciliationReportDao;
    @Autowired private EcobeeZeusCommunicationService ecobeeZeusCommunicationService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private EcobeeZeusGroupDao ecobeeZeusGroupDao;
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
        String ecobeePassword = globalSettingDao.getString(GlobalSettingType.ECOBEE_PASSWORD);
        String ecobeeUsername = globalSettingDao.getString(GlobalSettingType.ECOBEE_USERNAME);
        String ecobeeServerURL = globalSettingDao.getString(GlobalSettingType.ECOBEE_SERVER_URL);

        if (StringUtils.isNotEmpty(ecobeeServerURL) && StringUtils.isNotEmpty(ecobeeUsername)
                && StringUtils.isNotEmpty(ecobeePassword)) {
            log.info("Populating table to Zeus mapping");
            boolean groupMappingExists = ecobeeZeusGroupService.getGroupCount() == 0 ? false : true;
            boolean thermostatMappingExists = ecobeeZeusGroupService.getAllThermostatCount() == 0 ? false : true;
            if (!groupMappingExists && !thermostatMappingExists) {
                log.debug("Tables are empty starting process to populate data");
                try {
                    List<ZeusGroup> zeusGroups = ecobeeZeusCommunicationService.getAllGroups();
                    log.debug("Number of ZeusGroups found " + zeusGroups.size());
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
                                ecobeeZeusGroupService.mapGroupIdToZeusGroup(yukonGroupId, zeusGroupId, zeusGroupName,
                                        EcobeeZeusGroupService.DEFAULT_PROGRAM_ID);
                            }
                            if (!thermostatMappingExists) {
                                log.debug("Getting thermostats for group " + zeusGroupId);
                                List<ZeusThermostat> zeusThermostats = ecobeeZeusCommunicationService
                                        .getThermostatsInGroup(zeusGroupId);
                                mapInventoryToZeusGroup(zeusThermostats, zeusGroupId);
                                // If thermostats are enrolled i.e Zeus group has thermostats, then only update the ProgramId.
                                if (CollectionUtils.isNotEmpty(zeusThermostats)) {
                                    // A thermostat can be mapped to an account only. So use a thermostat id to retrieve the
                                    // programId
                                    updateProgramId(yukonGroupId, zeusThermostats.get(0).getSerialNumber(), zeusGroupId);
                                }
                            }
                        } else {
                            log.info("Skipping Zeus group " + zeusGroupName);
                        }
                    });
                } catch (Exception e) {
                    log.error("Error communicating with Ecobee" + e);
                }
                // Cleanup table for Reconciliation and reporting
                ecobeeZeusReconciliationReportDao.cleanUpReconciliationTables();
            } else {
                // stop the scheduler, table have values.
                log.debug("Stopping Zeus mapping schedular as tables are not empty");
                scheduledFuture.cancel(true);
                scheduledExecutorService.shutdown();
            }
        }
    }

    private void updateProgramId(int yukonGroupId, String serialNumber, String zeusGroupId) {
        int inventoryId = lmHardwareBaseDao.getBySerialNumber(serialNumber).getInventoryId();
        List<Integer> programIds = ecobeeZeusGroupService.getActiveEnrolmentProgramIds(inventoryId, yukonGroupId);
        // Old system does not support multiple program enrollment for Ecobee. So controlGroupList must contain same programId for
        // all the control groups.
        if (CollectionUtils.isNotEmpty(programIds)) {
            log.info("Mapping programID : {} to Yukon group : {} with Zeus group {} as thermostats are enrolled.",
                    programIds.get(0), yukonGroupId, zeusGroupId);
            ecobeeZeusGroupDao.updateProgramId(zeusGroupId, programIds.get(0));
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
