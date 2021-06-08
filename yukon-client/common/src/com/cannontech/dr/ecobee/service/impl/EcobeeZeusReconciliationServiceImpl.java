package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.events.loggers.EcobeeEventLogService;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.dao.EcobeeGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.dao.EcobeeZeusReconciliationReportDao;
import com.cannontech.dr.ecobee.model.EcobeeZeusDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeZeusGroupDeviceMapping;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationReport;
import com.cannontech.dr.ecobee.model.EcobeeZeusReconciliationResult;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeZeusDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeZeusCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeZeusReconciliationService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class EcobeeZeusReconciliationServiceImpl implements EcobeeZeusReconciliationService {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeZeusReconciliationServiceImpl.class);
    
    @Autowired private EcobeeZeusReconciliationReportDao reconciliationReportDao;
    @Autowired private EcobeeZeusCommunicationService communicationService;
    @Autowired private EcobeeGroupDeviceMappingDao ecobeeGroupDeviceMappingDao;
    @Autowired private EcobeeEventLogService ecobeeEventLogService;
    
  //Fix issues in this order to avoid e.g. deleting an extraneous set containing a mislocated set.
    //(This should not be rearranged without some thought)
    private static final ImmutableList<EcobeeZeusDiscrepancyType> errorTypes = ImmutableList.of(
        EcobeeZeusDiscrepancyType.MISSING_GROUP,
        EcobeeZeusDiscrepancyType.MISLOCATED_GROUP,
        EcobeeZeusDiscrepancyType.EXTRANEOUS_GROUP,
        EcobeeZeusDiscrepancyType.MISSING_DEVICE,
        EcobeeZeusDiscrepancyType.MISLOCATED_DEVICE,
        EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE
    );
    
    @Override
    public int runReconciliationReport() throws EcobeeCommunicationException {
        //get structure from ecobee
        List<EcobeeZeusGroupDeviceMapping> groupDeviceMapping = getZeusGroupDeviceMapping();
        
        //get structure from Yukon
        Multimap<Integer, String> groupToDevicesMap = 
                ecobeeGroupDeviceMappingDao.getSerialNumbersByGroupId();
        List<String> allSerialNumbers = ecobeeGroupDeviceMappingDao.getAllEcobeeSerialNumbers();
        
        //compare structures and build list of errors
        EcobeeZeusReconciliationReport report = generateReport(groupToDevicesMap, allSerialNumbers, groupDeviceMapping);
        
        //save errors to database and return report ID
        return reconciliationReportDao.insertReport(report);
    }

    @Override
    public EcobeeZeusReconciliationReport findReconciliationReport() {
        return reconciliationReportDao.findReport();
    }
    
    @Override
    public EcobeeZeusReconciliationResult fixDiscrepancy(int reportId, int errorId, LiteYukonUser liteYukonUser) throws IllegalArgumentException {
        return null;
    }
    
    /**
     * Log events for Ecobee Reconciliation.
     */
    private void doEventLog(LiteYukonUser liteYukonUser, EcobeeZeusDiscrepancy error, EcobeeZeusReconciliationResult result) {
        String managementSet = getSyncObjectForError(error);
        int intValue = BooleanUtils.toInteger(result.isSuccess());
        if (error.getErrorType() == EcobeeZeusDiscrepancyType.EXTRANEOUS_DEVICE) {
            intValue = 2; // represents unmodified
        }
        ecobeeEventLogService.reconciliationCompleted(intValue, managementSet,
                result.getOriginalDiscrepancy().getErrorType().toString(), liteYukonUser, intValue);
    }
    
    /**
     * Helper method to retrieve Sync Object for the specified EcobeeDiscrepancyType.
     */
    private String getSyncObjectForError(EcobeeZeusDiscrepancy error) {
        switch (error.getErrorType()) {
        case EXTRANEOUS_GROUP:
        case MISLOCATED_GROUP:
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
        // TODO: YUK-24505
        // get discrepancies
        EcobeeZeusReconciliationReport report = reconciliationReportDao.findReport();
        if (report.getReportId() != reportId) {
            throw new IllegalArgumentException("Report id is outdated.");
        }
        return null;
    }
    
    private EcobeeZeusReconciliationResult fixDiscrepancy(EcobeeZeusDiscrepancy error) {
        return null;
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
    
    // TODO: Add method level comments
    private List<EcobeeZeusGroupDeviceMapping> getZeusGroupDeviceMapping() {
        // TODO: YUK-23930
        // Call communication service to get group and thermostats.
        // Convert it into list of EcobeeZeusGroupDeviceMapping mapping object.
        return new ArrayList<>();
    }
}
