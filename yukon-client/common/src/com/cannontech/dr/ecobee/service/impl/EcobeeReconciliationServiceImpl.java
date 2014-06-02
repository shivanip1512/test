package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.dao.EcobeeGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.dao.EcobeeReconciliationReportDao;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationReport;
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
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class EcobeeReconciliationServiceImpl implements EcobeeReconciliationService {
    @Autowired private EcobeeReconciliationReportDao reconciliationReportDao;
    @Autowired private EcobeeCommunicationService communicationService;
    @Autowired private EcobeeGroupDeviceMappingDao ecobeeGroupDeviceMappingDao;
    
    @Override
    public int runReconciliationReport() throws EcobeeCommunicationException {
        //get structure from ecobee
        List<SetNode> ecobeeHierarchy = communicationService.getHierarchy();
        
        //get structure from Yukon
        Multimap<String, String> groupToDevicesMap = 
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
    @Transactional
    public List<String> fixDiscrepancy(int reportId, int errorId) throws EcobeeCommunicationException {
        //TODO
        //get discrepancy
        //fix with ecobeecommunicationservice
        //remove discrepancy from report
        return null;
    }
    
    @Override
    @Transactional
    public List<String> fixAllDiscrepancies(int reportId) throws EcobeeCommunicationException {
        //TODO
        //get discrepancies
        //fix all with ecobeecommunicationservice
        //remove discrepancies from report
        return null;
    }
    
    /**
     * Compares the Yukon groups and devices to the ecobee hierarchy and builds a report of discrepancies.
     */
    private EcobeeReconciliationReport generateReport(Multimap<String, String> groupToDevicesMap, 
                                                      List<String> allYukonSerialNumbers, List<SetNode> ecobeeHierarchy) {
        
        //Wrangle some data into useful structures
        EcobeeHierarchyInfo hierarchyInfo = new EcobeeHierarchyInfo(ecobeeHierarchy);
        Collection<String> allEcobeeSerialNumbers = hierarchyInfo.getSetsAndSerialNumbers().values();
        Set<String> yukonGroupNames = groupToDevicesMap.keySet();
        
        //Check for different types of discrepancies and add them to the errors list
        List<EcobeeDiscrepancy> errorsList = new ArrayList<>();
        checkForMissingAndMislocated(hierarchyInfo, yukonGroupNames, groupToDevicesMap, allEcobeeSerialNumbers, errorsList);
        checkForExtraneousSets(hierarchyInfo, yukonGroupNames, errorsList);
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
    private void checkForMissingAndMislocated(EcobeeHierarchyInfo hierarchyInfo, Set<String> yukonGroupNames, 
                                              Multimap<String, String> groupToDevicesMap, 
                                              Collection<String> allEcobeeSerialNumbers,
                                              List<EcobeeDiscrepancy> errorsList) {
        
        for (String groupId : yukonGroupNames) {
            String setPath = hierarchyInfo.getSetNamesAndPaths().get(groupId);
            String correctPath = "/" + groupId;
            
            if (setPath == null) {
                //Set doesn't exist in ecobee
                errorsList.add(new EcobeeMissingSetDiscrepancy(correctPath));
            } else if (setPath != correctPath) {
                //Set exists, but not under the root where it's supposed to be
                errorsList.add(new EcobeeMislocatedSetDiscrepancy(setPath, correctPath));
            } else {
                //Set exists, in the correct location
                Collection<String> yukonSerialNumbersForGroup = groupToDevicesMap.get(groupId);
                Collection<String> ecobeeSerialNumbersForGroup = hierarchyInfo.getSetsAndSerialNumbers().get(groupId);
                
                for (String yukonSerialNumber : yukonSerialNumbersForGroup) {
                    if (!allEcobeeSerialNumbers.contains(yukonSerialNumber)) {
                        //Device doesn't exist in ecobee
                        errorsList.add(new EcobeeMissingDeviceDiscrepancy(yukonSerialNumber, setPath)); 
                    } else if (!ecobeeSerialNumbersForGroup.contains(yukonSerialNumber)) {
                        //Device exists, but not in the correct set
                        String currentPath = hierarchyInfo.getSetPathForSerialNumber(yukonSerialNumber);
                        errorsList.add(new EcobeeMislocatedDeviceDiscrepancy(yukonSerialNumber, currentPath, setPath));
                    }
                }
            }
        }
    }
    
    /**
     * Checks for ecobee sets that do not match a Yukon group.
     */
    private void checkForExtraneousSets(EcobeeHierarchyInfo hierarchyInfo, Set<String> yukonGroupNames, 
                                          List<EcobeeDiscrepancy> errorsList) {
        
        Set<String> ecobeeSetNames = hierarchyInfo.getSetNamesAndPaths().keySet();
        for (String ecobeeSetName : ecobeeSetNames) {
            if (!yukonGroupNames.contains(ecobeeSetName)
                    && !ecobeeSetName.equals(EcobeeCommunicationService.OPT_OUT_SET)
                    && !ecobeeSetName.equals(EcobeeCommunicationService.UNENROLLED_SET)) {
                //ecobee set does not match any yukon group
                String setPath = hierarchyInfo.getSetNamesAndPaths().get(ecobeeSetName);
                errorsList.add(new EcobeeExtraneousSetDiscrepancy(setPath));
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
        private final Map<String, String> setNamesAndPaths = new HashMap<>();
        private final Multimap<String, String> setsAndSerialNumbers = ArrayListMultimap.create();
        private final Multimap<String, String> serialNumbersAndSets = ArrayListMultimap.create();
        
        public EcobeeHierarchyInfo(List<SetNode> ecobeeHierarchy) {
            SetNode root = Iterables.getOnlyElement(ecobeeHierarchy); //only top-level element should be the root, "/"
            for (SetNode childNode : root.getChildren()) {
                initialize(childNode);
            }
            Multimaps.invertFrom(setsAndSerialNumbers, serialNumbersAndSets);
        }
        
        public void initialize(SetNode node) {
            for (SetNode childNode : node.getChildren()) {
                initialize(childNode);
            }
            setNamesAndPaths.put(node.getSetName(), node.getSetPath());
            setsAndSerialNumbers.putAll(node.getSetName(), node.getThermostats());
        }

        public Map<String, String> getSetNamesAndPaths() {
            return setNamesAndPaths;
        }

        public Multimap<String, String> getSetsAndSerialNumbers() {
            return setsAndSerialNumbers;
        }
        
        public String getSetPathForSerialNumber(String serialNumber) {
            //Multimaps.invertFrom gives us a multimap, but each serial number should only be in one set
            String setName = Iterables.getOnlyElement(serialNumbersAndSets.get(serialNumber));
            return setNamesAndPaths.get(setName);
        }
    }
}
