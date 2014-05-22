package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.dao.EcobeeGroupDeviceMappingDao;
import com.cannontech.dr.ecobee.dao.EcobeeReconciliationReportDao;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeReconciliationReport;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeDiscrepancy;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeReconciliationService;
import com.google.common.collect.Multimap;

public class EcobeeReconciliationServiceImpl implements EcobeeReconciliationService {
    @Autowired EcobeeReconciliationReportDao reconciliationReportDao; //TODO bean def
    @Autowired EcobeeCommunicationService communicationService;
    @Autowired EcobeeGroupDeviceMappingDao ecobeeGroupDeviceMappingDao;
    
    @Override
    public int runReconciliationReport() throws EcobeeCommunicationException {
        //get structure from ecobee
        List<SetNode> ecobeeHierarchy = communicationService.getHierarchy();
        
        //get structure from Yukon
        Multimap<Integer, String> groupToDevicesMap = 
                ecobeeGroupDeviceMappingDao.getGroupIdToSerialNumberMultimap();
        
        //compare structures and build list of errors
        EcobeeReconciliationReport report = generateReport(groupToDevicesMap, ecobeeHierarchy);
        
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
    public List<String> fixDiscrepancies(int reportId, EcobeeDiscrepancyType type) throws EcobeeCommunicationException {
        //TODO
        //get discrepancies
        //fix all with ecobeecommunicationservice
        //remove discrepancies from report
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
    private EcobeeReconciliationReport generateReport(Multimap<Integer, String> groupToDevicesMap, List<SetNode> ecobeeHierarchy) {
        //TODO
        
        List<EcobeeDiscrepancy> errorsList = new ArrayList<>();
        
        for (Integer groupId : groupToDevicesMap.keySet()) {
            //if groupId not in top level of ecobeeHierarchy
              //add EcobeeMislocatedSetDiscrepancy
            //if groupId not in ecobeeHierarchy at all
              //add EcobeeSetDiscrepancy - missing
            
            for(String deviceSerial : groupToDevicesMap.get(groupId)) {
                //if serial not in group set
                  //add EcobeeMislocatedDeviceDiscrepancy
                //if serial not in hierarchy at all
                  //add EcobeeDeviceDiscrepancy - missing
            }
        }
        
        //check for extraneous groups
        //check for extraneous devices
        return null;
    }
}
