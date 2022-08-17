package com.cannontech.dr.ecobee.service.impl;

import static com.cannontech.dr.ecobee.model.EcobeeReconciliationResult.ErrorType.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.EcobeeDeviceDoesNotExistException;
import com.cannontech.dr.ecobee.EcobeeSetDoesNotExistException;
import com.cannontech.dr.ecobee.dao.EcobeeGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.dao.EcobeeReconciliationReportDao;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationReport;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationResult;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeExtraneousDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeExtraneousSetDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMislocatedDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMislocatedSetDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMissingDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMissingSetDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeReconciliationService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class EcobeeReconciliationServiceImpl implements EcobeeReconciliationService {
    
    private static final Logger log = YukonLogManager.getLogger(EcobeeReconciliationServiceImpl.class);
    
    @Autowired private EcobeeReconciliationReportDao reconciliationReportDao;
    @Autowired private EcobeeCommunicationService communicationService;
    @Autowired private EcobeeGroupDeviceMappingDao ecobeeGroupDeviceMappingDao;
    
    //Fix issues in this order to avoid e.g. deleting an extraneous set containing a mislocated set.
    //(This should not be rearranged without some thought)
    private static final ImmutableList<EcobeeDiscrepancyType> errorTypes = ImmutableList.of(
        EcobeeDiscrepancyType.MISSING_MANAGEMENT_SET,
        EcobeeDiscrepancyType.MISLOCATED_MANAGEMENT_SET,
        EcobeeDiscrepancyType.EXTRANEOUS_MANAGEMENT_SET,
        EcobeeDiscrepancyType.MISSING_DEVICE,
        EcobeeDiscrepancyType.MISLOCATED_DEVICE
        //EcobeeDiscrepancyType.EXTRANEOUS_DEVICE //No good way to fix this
    );
    
    @Override
    public int runReconciliationReport() throws EcobeeCommunicationException {
        //get structure from ecobee
        List<SetNode> ecobeeHierarchy = communicationService.getHierarchy();
        
        //get structure from Yukon
        Multimap<Integer, String> groupToDevicesMap = 
                ecobeeGroupDeviceMappingDao.getSerialNumbersByGroupId();
        List<String> allSerialNumbers = ecobeeGroupDeviceMappingDao.getAllEcobeeSerialNumbers();
        
        //compare structures and build list of errors
        EcobeeReconciliationReport report = generateReport(groupToDevicesMap, allSerialNumbers, ecobeeHierarchy);
        
        //save errors to database and return report ID
        return reconciliationReportDao.insertReport(report);
    }
    
    @Override
    public EcobeeReconciliationReport findReconciliationReport() {
        return reconciliationReportDao.findReport();
    }
    
    @Override
    public EcobeeReconciliationResult fixDiscrepancy(int reportId, int errorId) throws IllegalArgumentException {
        log.debug("Fixing ecobee discrepancy. ReportId: " + reportId + " ErrorId: " + errorId);
        
        //get discrepancy
        EcobeeReconciliationReport report = reconciliationReportDao.findReport();
        if (report.getReportId() != reportId) {
            throw new IllegalArgumentException("Report id is outdated.");
        }
        
        EcobeeDiscrepancy error = report.getError(errorId);
        if(error == null) {
            throw new IllegalArgumentException("Invalid error id.");
        }
        log.debug("Discrepancy type: " + error.getErrorType());
        
        //fix discrepancy
        EcobeeReconciliationResult result = fixDiscrepancy(error);
        
        //remove discrepancy from report
        if (result.isSuccess()) {
            reconciliationReportDao.removeError(reportId, errorId);
        }
        
        return result;
    }
    
    @Override
    public List<EcobeeReconciliationResult> fixAllDiscrepancies(int reportId) throws IllegalArgumentException {
        log.debug("Fixing all ecobee discrepancies. ReportId: " + reportId);
        
        //get discrepancies
        EcobeeReconciliationReport report = reconciliationReportDao.findReport();
        if (report.getReportId() != reportId) {
            throw new IllegalArgumentException("Report id is outdated.");
        }
        log.debug("Total number of discrepancies: " + report.getErrors().size());
        
        List<EcobeeReconciliationResult> results = new ArrayList<>();
        
        for (EcobeeDiscrepancyType errorType : errorTypes) {
            Collection<EcobeeDiscrepancy> errors = report.getErrors(errorType);
            for (EcobeeDiscrepancy error : errors) {
                //Attempt to fix
                EcobeeReconciliationResult result = fixDiscrepancy(error);
                //Save the result
                results.add(result);
                //Remove discrepancy from report
                if (result.isSuccess()) {
                    reconciliationReportDao.removeError(reportId, error.getErrorId());
                }
            }
        }
        
        return results;
    }
    
    private EcobeeReconciliationResult fixDiscrepancy(EcobeeDiscrepancy error) {
        try {
            switch (error.getErrorType()) {
                //Set is in ecobee, doesn't correspond to a Yukon group
                case EXTRANEOUS_MANAGEMENT_SET:
                    communicationService.deleteManagementSet(error.getCurrentPath());
                    return EcobeeReconciliationResult.newSuccess(error);
                    
                //ecobee device corresponds to a Yukon device, but is in the wrong management set
                case MISLOCATED_DEVICE:
                    communicationService.moveDeviceToSet(error.getSerialNumber(), error.getCorrectPath());
                    return EcobeeReconciliationResult.newSuccess(error);
                
                //ecobee set corresponds to Yukon group, but is in the wrong location in the hierarchy
                case MISLOCATED_MANAGEMENT_SET:
                    String currentPath = error.getCurrentPath();
                    
                    //Get the full path of the correct location, then remove the set name to get the parent path
                    String correctParentPath = getParentPath(error.getCorrectPath());
                    
                    communicationService.moveManagementSet(currentPath, correctParentPath);
                    return EcobeeReconciliationResult.newSuccess(error);
                
                //Device in Yukon, not in ecobee
                case MISSING_DEVICE:
                    communicationService.registerDevice(error.getSerialNumber());
                    communicationService.moveDeviceToSet(error.getSerialNumber(), error.getCorrectPath());
                    return EcobeeReconciliationResult.newSuccess(error);
                
                //Yukon group has no corresponding ecobee set
                case MISSING_MANAGEMENT_SET:
                    //This assumes that the correct path starts with / and is one level deep (as all sets corresponding
                    //to Yukon should be).
                    String setName = error.getCorrectPath().substring(1);
                    communicationService.createManagementSet(setName);
                    return EcobeeReconciliationResult.newSuccess(error);
                
                //Device in ecobee, not in Yukon
                case EXTRANEOUS_DEVICE:
                //Unknown discrepancy type, shouldn't happen
                default:
                    return EcobeeReconciliationResult.newFailure(error, NOT_FIXABLE);
            }
        } catch (EcobeeSetDoesNotExistException e) {
            return EcobeeReconciliationResult.newFailure(error, NO_SET);
        } catch (EcobeeDeviceDoesNotExistException e) {
            return EcobeeReconciliationResult.newFailure(error, NO_DEVICE);
        } catch (EcobeeCommunicationException e) {
            return EcobeeReconciliationResult.newFailure(error, COMMUNICATION);
        }
    }
    
    /**
     * Takes a full set path (including the set name) and parses out its parent set's name.
     */
    private String getParentPath(String fullSetPath) {
        int indexOfLastSlash = fullSetPath.lastIndexOf("/");
        return fullSetPath.substring(0, indexOfLastSlash + 1);
    }
    
    /**
     * Compares the Yukon groups and devices to the ecobee hierarchy and builds a report of discrepancies.
     */
    private EcobeeReconciliationReport generateReport(Multimap<Integer, String> groupToDevicesMap, 
                                                      List<String> allYukonSerialNumbers, List<SetNode> ecobeeHierarchy) {
        
        //Wrangle some data into useful structures
        EcobeeHierarchyInfo hierarchyInfo = new EcobeeHierarchyInfo(ecobeeHierarchy);
        Collection<String> allEcobeeSerialNumbers = hierarchyInfo.getSetNameToSerialNumbers().values();
        Set<Integer> yukonGroupIds = groupToDevicesMap.keySet();
        
        //Check for different types of discrepancies and add them to the errors list
        List<EcobeeDiscrepancy> errorsList = new ArrayList<>();
        checkForMissingAndMislocated(hierarchyInfo, yukonGroupIds, groupToDevicesMap, allEcobeeSerialNumbers, errorsList);
        checkForExtraneousSets(hierarchyInfo, yukonGroupIds, errorsList);
        checkForExtraneousDevices(hierarchyInfo, allYukonSerialNumbers, allEcobeeSerialNumbers, errorsList);
        
        //Build the actual report
        EcobeeReconciliationReport report = new EcobeeReconciliationReport(errorsList);
        return report;
    }
    
    /**
     * Checks for groups that are present in Yukon, but do not have a matching ecobee management set, or whose set is
     * in the wrong position in the hierarchy.
     * Also checks for devices that are present in Yukon, but are missing from ecobee, or in an incorrect management
     * set.
     */
    private void checkForMissingAndMislocated(EcobeeHierarchyInfo hierarchyInfo, Set<Integer> yukonGroupIds, 
                                              Multimap<Integer, String> groupToDevicesMap, 
                                              Collection<String> allEcobeeSerialNumbers,
                                              List<EcobeeDiscrepancy> errorsList) {
        
        for (Integer groupId : yukonGroupIds) {
            String setPath = hierarchyInfo.getSetNameToSetPath().get(Integer.toString(groupId));
            String correctPath = "/" + groupId;
            
            if (setPath == null) {
                //Set doesn't exist in ecobee
                errorsList.add(new EcobeeMissingSetDiscrepancy(correctPath));
            } else if (!setPath.equals(correctPath)) {
                //Set exists, but not under the root where it's supposed to be
                errorsList.add(new EcobeeMislocatedSetDiscrepancy(setPath, correctPath));
            } else {
                //Set exists, in the correct location
                Collection<String> yukonSerialNumbersForGroup = groupToDevicesMap.get(groupId);
                Collection<String> ecobeeSerialNumbersForGroup = hierarchyInfo.getSetNameToSerialNumbers().get(Integer.toString(groupId));
                Collection<String> optedOutSerialNumbers = 
                        hierarchyInfo.getSetNameToSerialNumbers().get(EcobeeCommunicationService.OPT_OUT_SET);
                
                for (String yukonSerialNumber : yukonSerialNumbersForGroup) {
                    if (!allEcobeeSerialNumbers.contains(yukonSerialNumber)) {
                        //Device doesn't exist in ecobee
                        errorsList.add(new EcobeeMissingDeviceDiscrepancy(yukonSerialNumber, setPath)); 
                    } else if (!ecobeeSerialNumbersForGroup.contains(yukonSerialNumber) 
                            && !optedOutSerialNumbers.contains(yukonSerialNumber)) {
                        //Device exists, but not in the correct set
                        String currentPath = hierarchyInfo.getSetPathForSerialNumber(yukonSerialNumber);
                        errorsList.add(new EcobeeMislocatedDeviceDiscrepancy(yukonSerialNumber, currentPath, setPath));
                    }
                }
            }
        }
        
        String optOutSetPath = hierarchyInfo.getSetNameToSetPath().get(EcobeeCommunicationService.OPT_OUT_SET);
        String correctOptOutSetPath = "/" + EcobeeCommunicationService.OPT_OUT_SET;
        if (optOutSetPath == null) {
            //Set doesn't exist in ecobee
            errorsList.add(new EcobeeMissingSetDiscrepancy(correctOptOutSetPath));
        } else if (!optOutSetPath.equals(correctOptOutSetPath)) {
            //Set exists, but not under the root where it's supposed to be
            errorsList.add(new EcobeeMislocatedSetDiscrepancy(optOutSetPath, correctOptOutSetPath));
        }
        //Not practical to check the devices in this group - it will change much more frequently than the report is run.
        
        String unenrolledSetPath = hierarchyInfo.getSetNameToSetPath().get(EcobeeCommunicationService.UNENROLLED_SET);
        String correctUnenrolledSetPath = "/" + EcobeeCommunicationService.UNENROLLED_SET;
        if (unenrolledSetPath == null) {
            //Set doesn't exist in ecobee
            errorsList.add(new EcobeeMissingSetDiscrepancy(correctUnenrolledSetPath));
        } else if (!unenrolledSetPath.equals(correctUnenrolledSetPath)) {
            //Set exists, but not under the root where it's supposed to be
            errorsList.add(new EcobeeMislocatedSetDiscrepancy(unenrolledSetPath, correctUnenrolledSetPath));
        }
        //Can't check devices in this group, they may be here, or in "Unassigned"
    }
    
    /**
     * Checks for ecobee sets that do not match a Yukon group.
     */
    private void checkForExtraneousSets(EcobeeHierarchyInfo hierarchyInfo, Set<Integer> yukonGroupIds, 
                                          List<EcobeeDiscrepancy> errorsList) {
        
        Set<String> ecobeeSetNames = hierarchyInfo.getSetNameToSetPath().keySet();
        for (String ecobeeSetName : ecobeeSetNames) {
            try {
                Integer ecobeeSetNameAsInteger = Integer.parseInt(ecobeeSetName);
                if (!yukonGroupIds.contains(ecobeeSetNameAsInteger)) {
                    //ecobee set name does not match any Yukon group id
                    String setPath = hierarchyInfo.getSetNameToSetPath().get(ecobeeSetName);
                    errorsList.add(new EcobeeExtraneousSetDiscrepancy(setPath));
                }
            } catch (NumberFormatException e) {
                //ecobee set is not an integer, so it can't match a Yukon group id
                if (!ecobeeSetName.equals(EcobeeCommunicationService.OPT_OUT_SET)
                        && !ecobeeSetName.equals(EcobeeCommunicationService.UNENROLLED_SET)) {
                    String setPath = hierarchyInfo.getSetNameToSetPath().get(ecobeeSetName);
                    errorsList.add(new EcobeeExtraneousSetDiscrepancy(setPath));
                }
            }
        }
    }
    
    /**
     * Checks for ecobee devices that do not match a Yukon device.
     */
    private void checkForExtraneousDevices(EcobeeHierarchyInfo hierarchyInfo,
                                           Collection<String> yukonSerialNumbers, 
                                           Collection<String> ecobeeSerialNumbers,
                                           List<EcobeeDiscrepancy> errorsList) {
        
        for (String ecobeeSerialNumber : ecobeeSerialNumbers) {
            if (!yukonSerialNumbers.contains(ecobeeSerialNumber)) {
                //ecobee device does not match any yukon device
                String setPath = hierarchyInfo.getSetPathForSerialNumber(ecobeeSerialNumber);
                errorsList.add(new EcobeeExtraneousDeviceDiscrepancy(ecobeeSerialNumber, setPath));
            }
        }
    }
    
    /**
     * A little helper class that parses an ecobee hierarchy into several useful maps, so it can be more easily
     * compared with Yukon.
     */
    private final class EcobeeHierarchyInfo {
        private final Map<String, String> setNameToSetPath = new HashMap<>();
        private final Multimap<String, String> setNameToSerialNumbers = ArrayListMultimap.create();
        private final Multimap<String, String> serialNumberToSetName = ArrayListMultimap.create();
        
        public EcobeeHierarchyInfo(List<SetNode> ecobeeHierarchy) {
            //the root, "/", should be the only top-level element 
            SetNode root = Iterables.getOnlyElement(ecobeeHierarchy); 
            for (SetNode childNode : root.getChildren()) {
                initialize(childNode);
            }
            Multimaps.invertFrom(setNameToSerialNumbers, serialNumberToSetName);
        }
        
        public void initialize(SetNode node) {
            for (SetNode childNode : node.getChildren()) {
                initialize(childNode);
            }
            setNameToSetPath.put(node.getSetName(), node.getSetPath());
            setNameToSerialNumbers.putAll(node.getSetName(), node.getThermostats());
        }

        public Map<String, String> getSetNameToSetPath() {
            return setNameToSetPath;
        }

        public Multimap<String, String> getSetNameToSerialNumbers() {
            return setNameToSerialNumbers;
        }
        
        public String getSetPathForSerialNumber(String serialNumber) {
            //Multimaps.invertFrom gives us a multimap, but each serial number should only be in one set
            String setName = Iterables.getOnlyElement(serialNumberToSetName.get(serialNumber));
            return setNameToSetPath.get(setName);
        }
    }
}
