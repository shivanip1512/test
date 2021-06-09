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
import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeZeusGroupDeviceMapping;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationReport;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationResult;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class EcobeeZeusReconciliationServiceImpl implements EcobeeZeusReconciliationService {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusReconciliationServiceImpl.class);
    
    @Autowired private EcobeeZeusReconciliationReportDao reconciliationReportDao;
    @Autowired private EcobeeZeusCommunicationService communicationService;
    @Autowired private EcobeeGroupDeviceMappingDao ecobeeGroupDeviceMappingDao;
    @Autowired private EcobeeEventLogService ecobeeEventLogService;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private EcobeeZeusGroupDao ecobeeZeusGroupDao;

    
  //Fix issues in this order to avoid e.g. deleting an extraneous group containing a mislocated group.
    //(This should not be rearranged without some thought)
    private static final ImmutableList<EcobeeZeusDiscrepancyType> errorTypes = ImmutableList.of(
        EcobeeZeusDiscrepancyType.MISSING_GROUP,
        EcobeeZeusDiscrepancyType.EXTRANEOUS_GROUP,
        EcobeeZeusDiscrepancyType.MISSING_DEVICE,
        EcobeeZeusDiscrepancyType.MISLOCATED_DEVICE,
        EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE
    );
    
    @Override
    public int runReconciliationReport() throws EcobeeCommunicationException {
        //get structure from ecobee
        List<EcobeeZeusGroupDeviceMapping> groupDeviceMapping = getZeusGroupDeviceMapping();
        
        /* TODO : updated in YUK-23931
        //get structure from Yukon
        Multimap<Integer, String> groupToDevicesMap = 
                ecobeeGroupDeviceMappingDao.getSerialNumbersByGroupId();
        List<String> allSerialNumbers = ecobeeGroupDeviceMappingDao.getAllEcobeeSerialNumbers();
        
        //compare structures and build list of errors
        EcobeeZeusReconciliationReport report = generateReport(groupToDevicesMap, allSerialNumbers, groupDeviceMapping);
        
        //save errors to database and return report ID
        return reconciliationReportDao.insertReport(report);
		        */
        return 0;
    }

    @Override
    public EcobeeZeusReconciliationReport findReconciliationReport() {
        return reconciliationReportDao.findReport();
    }
    
    @Override
    public EcobeeZeusReconciliationResult fixDiscrepancy(int reportId, int errorId, LiteYukonUser liteYukonUser) throws IllegalArgumentException {
        log.debug("Fixing ecobee discrepancy. ReportId: " + reportId + " ErrorId: " + errorId);
        
        //get discrepancy
        EcobeeZeusReconciliationReport report = reconciliationReportDao.findReport();
        if (report.getReportId() != reportId) {
            throw new IllegalArgumentException("Report id is outdated.");
        }
        
        EcobeeZeusDiscrepancy error = report.getError(errorId);
        if(error == null) {
            throw new IllegalArgumentException("Invalid error id.");
        }
        log.debug("Discrepancy type: " + error.getErrorType());
        ecobeeEventLogService.reconciliationStarted(1, liteYukonUser);
        
        //fix discrepancy
        EcobeeZeusReconciliationResult result = fixDiscrepancy(error);
        doEventLog(liteYukonUser, error, result);
        
        //remove discrepancy from report
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
        if (report.getReportId() != reportId) {
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
                communicationService.unEnroll(groups, error.getSerialNumber(), inventoryId);
                communicationService.enroll(Integer.parseInt(error.getCorrectPath()), error.getSerialNumber(), inventoryId);
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
     * Compares the Yukon groups and devices to the ecobee hierarchy and builds a report of discrepancies.
     */
    private EcobeeZeusReconciliationReport generateReport(Multimap<Integer, String> groupToDevicesMap, 
                                                      List<String> allYukonSerialNumbers, List<EcobeeZeusGroupDeviceMapping> groupDeviceMapping) {
        
     // TODO: YUK-23931
        return null;
    }
    
    /**
     * Checks for groups that are present in Yukon, but do not have a matching ecobee management set, or whose set is
     * in the wrong position in the hierarchy.
     * Also checks for devices that are present in Yukon, but are missing from ecobee, or in an incorrect management
     * set.
     */
    private void checkForMissingAndMislocated(EcobeeZeusHierarchyInfo hierarchyInfo, Set<Integer> yukonGroupIds, 
                                              Multimap<Integer, String> groupToDevicesMap, 
                                              Collection<String> allEcobeeSerialNumbers,
                                              List<EcobeeZeusDiscrepancy> errorsList) {
        
        // TODO: YUK-23931
    }
    
    /**
     * Checks for ecobee sets that do not match a Yukon group.
     */
    private void checkForExtraneousSets(EcobeeZeusHierarchyInfo hierarchyInfo, Set<Integer> yukonGroupIds, 
                                          List<EcobeeZeusDiscrepancy> errorsList) {
        
     // TODO: YUK-23931
    }
    
    /**
     * Checks for ecobee devices that do not match a Yukon device.
     */
    private void checkForExtraneousDevices(EcobeeZeusHierarchyInfo hierarchyInfo,
                                           Collection<String> yukonSerialNumbers, 
                                           Collection<String> ecobeeSerialNumbers,
                                           List<EcobeeZeusDiscrepancy> errorsList) {
        
     // TODO: YUK-23931
    }
    
    /**
     * A little helper class that parses an ecobee hierarchy into several useful maps, so it can be more easily
     * compared with Yukon.
     */
    private final class EcobeeZeusHierarchyInfo {
     // TODO: YUK-23931 - This class might not be required as we do not have complex hierarchy structure.
    }
    
    /**
     *  Retrieve list of groups and thermostats. 
     *  Convert it into list of EcobeeZeusGroupDeviceMapping mapping object.
     */
    private List<EcobeeZeusGroupDeviceMapping> getZeusGroupDeviceMapping() {
        log.debug("Retrieving ecobee groups and its corresponding thermostats.");

        List<ZeusGroup> zeusGroups = communicationService.getAllGroups();
        List<EcobeeZeusGroupDeviceMapping> zeusGroupDeviceMaps = new ArrayList<EcobeeZeusGroupDeviceMapping>();

        zeusGroups.forEach(group -> {
            List<ZeusThermostat> thermostatsInGroup = communicationService.getThermostatsInGroup(group.getGroupId());
            List<String> serialNumbers = thermostatsInGroup.stream()
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
