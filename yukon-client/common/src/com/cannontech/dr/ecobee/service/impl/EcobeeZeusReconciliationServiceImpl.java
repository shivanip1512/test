package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationResult.ErrorType.COMMUNICATION;
import static com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationResult.ErrorType.NOT_FIXABLE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.EcobeeEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.dao.EcobeeGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;
import com.cannontech.dr.ecobee.dao.EcobeeZeusReconciliationReportDao;
import com.cannontech.dr.ecobee.message.ZeusGroup;
import com.cannontech.dr.ecobee.message.ZeusThermostat;
import com.cannontech.dr.ecobee.message.ZeusThermostatState;
import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeZeusGroupDeviceMapping;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationReport;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationResult;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusExtraneousDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusExtraneousGroupDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusMislocatedDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusMissingDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusMissingGroupDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusGroupService;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.google.common.base.Functions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class EcobeeZeusReconciliationServiceImpl implements EcobeeZeusReconciliationService {

    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusReconciliationServiceImpl.class);

    @Autowired private EcobeeZeusReconciliationReportDao reconciliationReportDao;
    @Autowired private EcobeeZeusCommunicationService communicationService;
    @Autowired private EcobeeGroupDeviceMappingDao ecobeeGroupDeviceMappingDao;
    @Autowired private EcobeeEventLogService ecobeeEventLogService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private EcobeeZeusGroupDao ecobeeZeusGroupDao;
    @Autowired private EcobeeZeusGroupService ecobeeZeusGroupService;

    // Fix issues in this order to avoid e.g. deleting an extraneous group containing a mislocated group.
    // (This should not be rearranged without some thought)
    private static final ImmutableList<EcobeeZeusDiscrepancyType> errorTypes = ImmutableList.of(
            EcobeeZeusDiscrepancyType.MISSING_GROUP,
            EcobeeZeusDiscrepancyType.EXTRANEOUS_GROUP,
            EcobeeZeusDiscrepancyType.MISSING_DEVICE,
            EcobeeZeusDiscrepancyType.MISLOCATED_DEVICE,
            EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE);

    @Override
    public int runReconciliationReport() throws EcobeeCommunicationException {
        // get structure from ecobee
        List<EcobeeZeusGroupDeviceMapping> ecobeeGroupDeviceMapping = getZeusGroupDeviceMapping();

        // get structure from Yukon
        Multimap<Integer, String> yukonGroupToDevicesMap = ecobeeGroupDeviceMappingDao.getSerialNumbersByGroupId();
        List<String> allYukonSerialNumbers = ecobeeGroupDeviceMappingDao.getAllEcobeeSerialNumbers();

        // compare structures and build list of errors
        EcobeeZeusReconciliationReport report = generateReport(yukonGroupToDevicesMap, allYukonSerialNumbers,
                ecobeeGroupDeviceMapping);

        // save errors to database and return report ID
        return reconciliationReportDao.insertReport(report);

    }

    @Override
    public EcobeeZeusReconciliationReport findReconciliationReport() {
        return reconciliationReportDao.findReport();
    }

    @Override
    public EcobeeZeusReconciliationResult fixDiscrepancy(int reportId, int errorId, LiteYukonUser liteYukonUser)
            throws IllegalArgumentException {
        log.debug("Fixing ecobee discrepancy. ReportId: " + reportId + " ErrorId: " + errorId);

        // get discrepancy
        EcobeeZeusReconciliationReport report = reconciliationReportDao.findReport();
        if (report == null || report.getReportId() != reportId) {
            throw new IllegalArgumentException("Report id is outdated.");
        }

        EcobeeZeusDiscrepancy error = report.getError(errorId);
        if (error == null) {
            throw new IllegalArgumentException("Invalid error id.");
        }
        log.debug("Discrepancy type: " + error.getErrorType());
        ecobeeEventLogService.reconciliationStarted(1, liteYukonUser);

        // fix discrepancy
        EcobeeZeusReconciliationResult result = fixDiscrepancy(error);
        doEventLog(liteYukonUser, error, result);

        // remove discrepancy from report
        if (result.isSuccess()) {
            reconciliationReportDao.removeError(reportId, errorId);
        }

        return result;
    }

    /**
     * Log events for Ecobee Reconciliation.
     */
    private void doEventLog(LiteYukonUser liteYukonUser, EcobeeZeusDiscrepancy error, EcobeeZeusReconciliationResult result) {
        String groups = getSyncObjectForError(error);
        int intValue = BooleanUtils.toInteger(result.isSuccess());
        if (error.getErrorType() == EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE
                || error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_DEVICE) {
            intValue = 2; // represents unmodified
        }
        ecobeeEventLogService.reconciliationCompleted(intValue, groups,
                result.getOriginalDiscrepancy().getErrorType().toString(), liteYukonUser, intValue);
    }

    /**
     * Helper method to retrieve Sync Object for the specified EcobeeDiscrepancyType.
     */
    private String getSyncObjectForError(EcobeeZeusDiscrepancy error) {
        switch (error.getErrorType()) {
        case EXTRANEOUS_GROUP:
            return error.getCurrentPath();
        case MISSING_GROUP:
            return error.getCorrectPath();
        case MISLOCATED_DEVICE:
        case MISSING_DEVICE:
        case EXTRANEOUS_DEVICE:
            return error.getSerialNumber();
        default:
            return StringUtils.EMPTY;
        }
    }

    @Override
    public List<EcobeeZeusReconciliationResult> fixAllDiscrepancies(int reportId, LiteYukonUser liteYukonUser)
            throws IllegalArgumentException {
        int successCount = 0;
        int unsupportedCount = 0;
        log.debug("Fixing all ecobee discrepancies. ReportId: " + reportId);

        // get discrepancies
        EcobeeZeusReconciliationReport report = reconciliationReportDao.findReport();
        if (report == null || report.getReportId() != reportId) {
            throw new IllegalArgumentException("Report id is outdated.");
        }
        log.debug("Total number of discrepancies: " + report.getErrors().size());
        ecobeeEventLogService.reconciliationStarted(report.getErrors().size(), liteYukonUser);
        List<EcobeeZeusReconciliationResult> results = new ArrayList<>();

        for (EcobeeZeusDiscrepancyType errorType : errorTypes) {
            Collection<EcobeeZeusDiscrepancy> errors = report.getErrors(errorType);
            for (EcobeeZeusDiscrepancy error : errors) {
                // Attempt to fix
                EcobeeZeusReconciliationResult result = fixDiscrepancy(error);
                // Save the result
                results.add(result);
                doEventLog(liteYukonUser, error, result);
                if (error.getErrorType() == EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE ||
                        error.getErrorType() == EcobeeZeusDiscrepancyType.MISSING_DEVICE) {
                    unsupportedCount++;
                }
                // Remove discrepancy from report
                if (result.isSuccess()) {
                    reconciliationReportDao.removeError(reportId, error.getErrorId());
                    successCount++;
                }
            }
        }
        ecobeeEventLogService.reconciliationResults(report.getErrors().size(), successCount,
                report.getErrors().size() - successCount - unsupportedCount, unsupportedCount);
        return results;
    }

    private EcobeeZeusReconciliationResult fixDiscrepancy(EcobeeZeusDiscrepancy error) {
        try {
            switch (error.getErrorType()) {
            // Group in ecobee, doesn't correspond to a Yukon group
            case EXTRANEOUS_GROUP:
                // Delete group from ecobee.
                communicationService.deleteGroup(error.getCurrentPath());
                return EcobeeZeusReconciliationResult.newSuccess(error);

            // ecobee device corresponds to a Yukon device, but is in the wrong group
            case MISLOCATED_DEVICE:
                // Unenroll device from incorrect group and Enroll device in correct group
                int inventoryId = lmHardwareBaseDao.getBySerialNumber(error.getSerialNumber()).getInventoryId();
                Set<Integer> groups = new HashSet<>();
                groups.add(Integer.parseInt(error.getCurrentPath()));
                communicationService.unEnroll(groups, error.getSerialNumber(), inventoryId, true);
                int lmGroupId = Integer.parseInt(error.getCorrectPath());
                int programId = ecobeeZeusGroupService.getProgramIdToEnroll(inventoryId, lmGroupId);
                communicationService.enroll(lmGroupId, error.getSerialNumber(), inventoryId, programId, true);
                return EcobeeZeusReconciliationResult.newSuccess(error);

            // Device in Yukon, not in ecobee
            case MISSING_DEVICE:
                // Cant fix this at Ecobee Zeus, as this require creation of thermostat at Zeus. Which is not supported
                return EcobeeZeusReconciliationResult.newFailure(error, NOT_FIXABLE);

            // Yukon group has no corresponding ecobee group
            case MISSING_GROUP:
                // Create thermostat group in ecobee with all its enrollments.
                List<Integer> ids = ecobeeZeusGroupDao.getInventoryIdsForYukonGroupID(error.getCorrectPath());
                List<String> inventoryIds = Lists.transform(ids, Functions.toStringFunction());
                communicationService.createThermostatGroup(error.getCorrectPath(), inventoryIds);
                return EcobeeZeusReconciliationResult.newSuccess(error);

            // Device in ecobee, not in Yukon
            case EXTRANEOUS_DEVICE:
                // Unknown discrepancy type, shouldn't happen
                return EcobeeZeusReconciliationResult.newFailure(error, NOT_FIXABLE);

            default:
                return EcobeeZeusReconciliationResult.newFailure(error, NOT_FIXABLE);
            }
        } catch (EcobeeCommunicationException e) {
            return EcobeeZeusReconciliationResult.newFailure(error, COMMUNICATION);
        }
    }

    /**
     * Compares the Yukon groups and devices to the ecobee groups and devices and builds a report of discrepancies.
     */
    private EcobeeZeusReconciliationReport generateReport(Multimap<Integer, String> yukonGroupToDevicesMap,
            List<String> allYukonSerialNumbers, List<EcobeeZeusGroupDeviceMapping> ecobeeGroupDeviceMapping) {

        List<EcobeeZeusDiscrepancy> errorsList = new ArrayList<>();

        List<String> ecobeeGroupIdsInYukon = ecobeeZeusGroupDao.getGroupMapping(yukonGroupToDevicesMap.keySet());
        Multimap<String, String> ecobeeSerialNumberToGroupMapping = ArrayListMultimap.create();
        
        ecobeeGroupDeviceMapping.forEach(mapping -> {
            List<String> thermostatsSerialNumber = mapping.getThermostatsSerialNumber();
            String zeusGroupId = mapping.getGroupId();
            thermostatsSerialNumber.forEach(serialNumber -> {
                ecobeeSerialNumberToGroupMapping.put(serialNumber, zeusGroupId);
            });
        });

        Multimap<String, String> ecobeeSerialNumberInYukonToGroupMapping = ecobeeZeusGroupDao
                .getAllEcobeeGroupToSerialNumberMapping();

        checkForMissingAndExtraneousGroup(ecobeeGroupDeviceMapping, ecobeeGroupIdsInYukon, errorsList);
        checkForMissingAndExtraneousDevices(allYukonSerialNumbers, ecobeeSerialNumberToGroupMapping.keySet(), errorsList);
        /**
         * For MISLOCATED_DEVICE we have to consider the below things:
         * 1> We should only consider the child groups. The root group will have all the thermostats and EXTRANEOUS_DEVICE is
         * computed based on this group.
         * 2> If there are any EXTRANEOUS_GROUP, we should not include the devices from that group. to fix EXTRANEOUS_GROUP, Yukon
         * will make delete API call to Ecobee.
         */
        ecobeeSerialNumberToGroupMapping.clear();
        List<String> extraneousGroupIds = new ArrayList<String>();
        for (EcobeeZeusDiscrepancy discrepancy : errorsList) {
            if (discrepancy.getErrorType() == EcobeeZeusDiscrepancyType.EXTRANEOUS_GROUP) {
                extraneousGroupIds.add(discrepancy.getCurrentPath());
            }
        }
        ecobeeGroupDeviceMapping.forEach(mapping -> {
            // Add check for root group and EXTRANEOUS_GROUP.
            if (StringUtils.isNotEmpty(mapping.getParentGroupId()) && !extraneousGroupIds.contains(mapping.getGroupId())) {
                List<String> thermostatsSerialNumber = mapping.getThermostatsSerialNumber();
                String zeusGroupId = mapping.getGroupId();
                thermostatsSerialNumber.forEach(serialNumber -> {
                    ecobeeSerialNumberToGroupMapping.put(serialNumber, zeusGroupId);
                });
            }
        });
        checkForMisLocatedDevices(ecobeeSerialNumberToGroupMapping, ecobeeSerialNumberInYukonToGroupMapping, errorsList);

        // Build the actual report
        EcobeeZeusReconciliationReport report = new EcobeeZeusReconciliationReport(errorsList);
        return report;
    }

    /**
     * 1. Check that only one root group is present.
     * 2. Check all groups have parent group as root group.
     * 3. Checks for groups that are present in Yukon but do not have matching ecobee group.
     * 4. Checks for groups is present in Yukon but do not have matching Yukon group.
     *
     */
    private void checkForMissingAndExtraneousGroup(List<EcobeeZeusGroupDeviceMapping> ecobeeGroupDeviceMapping,
            List<String> yukonGroupIds, List<EcobeeZeusDiscrepancy> errorsList) {

        List<String> parentGroupIds = ecobeeGroupDeviceMapping.stream()
                .filter(e -> e.getParentGroupId() == null)
                .map(e -> e.getGroupId())
                .collect(Collectors.toList());

        if (parentGroupIds.size() != 1) {
            log.error("There should be only 1 root Zeus group. This is incorrect, but Yukon cannot fix it");
            return;
        }

        // Get all the groupIds from Ecobee mapping. If we have multiple parent groups, Yukon can't fix it. Added return for this
        // scenario already.
        List<String> ecobeeGroupIds = ecobeeGroupDeviceMapping.stream()
                                                              .filter(e -> StringUtils.isNotEmpty(e.getParentGroupId()))
                                                              .map(e -> e.getGroupId())
                                                              .collect(Collectors.toList());

        // Iterate over Ecobee groups and find the Ecobee groups which are not here in Yukon. These groups will be in
        // EXTRANEOUS_GROUP.
        ecobeeGroupIds.forEach(ecobeeGroupId -> {
            // If Yukon does not contain the Ecobee group, add the group to EXTRANEOUS_GROUP.
            if (!yukonGroupIds.contains(ecobeeGroupId)) {
                errorsList.add(new EcobeeZeusExtraneousGroupDiscrepancy(ecobeeGroupId));

            }
        });

        // Iterate over Yukon groups and find the yukon groups which are not here in Ecobee. These groups will be in
        // MISSING_GROUP.
        yukonGroupIds.forEach(eyukonGroupId -> {
            // If Ecobee does not contain the Yukon group, add the group to MISSING_GROUP.
            if (!ecobeeGroupIds.contains(eyukonGroupId)) {
                errorsList.add(new EcobeeZeusMissingGroupDiscrepancy(eyukonGroupId));

            }
        });
    }

    /**
     * Checks for mismatch in devices at ecobee and yukon side.
     */
    private void checkForMissingAndExtraneousDevices(List<String> yukonSerialNumbers,
            Set<String> ecobeeSerialNumbers,
            List<EcobeeZeusDiscrepancy> errorsList) {

        for (String ecobeeSerialNumber : ecobeeSerialNumbers) {
            if (!yukonSerialNumbers.contains(ecobeeSerialNumber)) {
                // Device is in ecobee but not in Yukon
                errorsList.add(new EcobeeZeusExtraneousDeviceDiscrepancy(ecobeeSerialNumber, StringUtils.EMPTY));
            }
        }

        for (String yukonSerialNumber : yukonSerialNumbers) {
            if (!ecobeeSerialNumbers.contains(yukonSerialNumber)) {
                // Device is in Yukon and not in ecobee.
                errorsList.add(new EcobeeZeusMissingDeviceDiscrepancy(yukonSerialNumber, StringUtils.EMPTY));
            }
        }
    }

    // Check for devices are in correct group or not
    private void checkForMisLocatedDevices(Multimap<String, String> ecobeeSerialNumberToGroupMapping,
            Multimap<String, String> ecobeeSerialNumberInYukonToGroupMapping, List<EcobeeZeusDiscrepancy> errorList) {

        Multimap<String, String> mislocatedDevicesAtEcobee = Multimaps.filterEntries(ecobeeSerialNumberToGroupMapping,
                e -> !ecobeeSerialNumberInYukonToGroupMapping.containsEntry(e.getKey(), e.getValue()));

        mislocatedDevicesAtEcobee.entries().forEach(e -> {
            errorList.add(new EcobeeZeusMislocatedDeviceDiscrepancy(e.getKey(), e.getValue(), StringUtils.EMPTY));
        });
    }

    /**
     * Retrieve list of groups and thermostats.
     * Convert it into list of EcobeeZeusGroupDeviceMapping mapping object.
     */
    private List<EcobeeZeusGroupDeviceMapping> getZeusGroupDeviceMapping() {
        log.debug("Retrieving ecobee groups and its corresponding thermostats.");

        List<ZeusGroup> zeusGroups = communicationService.getAllGroups();
        List<EcobeeZeusGroupDeviceMapping> zeusGroupDeviceMaps = new ArrayList<EcobeeZeusGroupDeviceMapping>();

        zeusGroups.forEach(group -> {
            List<ZeusThermostat> thermostatsInGroup = communicationService.getThermostatsInGroup(group.getGroupId());
            // Don't include thermostats which are removed state. On Delete Thermostat API, Ecobee update the status to REMOVED.
            List<String> serialNumbers = thermostatsInGroup.stream()
                    .filter(thermostat -> thermostat.getState() != ZeusThermostatState.REMOVED)
                    .map(thermostat -> thermostat.getSerialNumber())
                    .collect(Collectors.toList());

            EcobeeZeusGroupDeviceMapping deviceGroupMap = new EcobeeZeusGroupDeviceMapping(group.getGroupId(), serialNumbers);
            if (group.getParentGroupId() != null)
                deviceGroupMap.setParentGroupId(group.getParentGroupId());
            zeusGroupDeviceMaps.add(deviceGroupMap);
        });

        return zeusGroupDeviceMaps;
    }
}
