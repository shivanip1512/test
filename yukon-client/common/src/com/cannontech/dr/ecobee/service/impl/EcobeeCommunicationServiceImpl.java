package com.cannontech.dr.ecobee.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestOperations;

import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCountDao;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeManagementSet;
import com.cannontech.dr.ecobee.service.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.service.EcobeeCommunicationService;
import com.cannontech.dr.ecobee.service.EcobeeDeviceDoesNotExistException;
import com.cannontech.dr.ecobee.service.EcobeeSetDoesNotExistException;

public class EcobeeCommunicationServiceImpl implements EcobeeCommunicationService {
    @Autowired private EcobeeQueryCountDao ecobeeQueryCountDao;
    @Autowired RestOperations restTemplate;
    private final Map<Integer, DatedObject<String>> ecAuthKeys = new HashMap<>();
    
    @Override
    public void moveDeviceToSet(long serialNumber, String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException, EcobeeDeviceDoesNotExistException, 
            EcobeeSetDoesNotExistException {
        
    }

    @Override
    public void removeDeviceFromSet(long serialNumber, int energyCompanyId) throws EcobeeAuthenticationException, 
            EcobeeCommunicationException, EcobeeDeviceDoesNotExistException {
        
    }
    
    @Override
    public List<EcobeeDeviceReadings> readDeviceData(Iterable<String> serialNumbers, Range<Instant> dateRange, 
            int energyCompanyId) throws EcobeeAuthenticationException, EcobeeCommunicationException {
//        List<EcobeeDeviceReadings> deviceReadings = new ArrayList<>();
//        for (String serialNumber : serialNumbers) {
//            List<EcobeeDeviceReading> readings = new ArrayList<>();
//            for (int i=0;i<100;i++) {
//                readings.add(new EcobeeDeviceReading(90.98465498798f, 60.298465498798f, 60.3459846534498798f, 
//                                                     50.465498798f, 600, "not sure", Instant.now().plus(Duration.standardHours((long) (Math.random()*100)-50))));
//            }
//            deviceReadings.add(new EcobeeDeviceReadings(serialNumber, dateRange, readings));
//        }
//        return deviceReadings;
        return new ArrayList<>();
    }

    @Override
    public boolean createManagementSet(String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException {
        return false;
    }

    @Override
    public boolean deleteManagementSet(String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException, EcobeeSetDoesNotExistException {
        return false;
    }

    @Override
    public boolean moveManagementSet(EcobeeManagementSet currentPath, EcobeeManagementSet newPath, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException, EcobeeSetDoesNotExistException {
        return false;
    }

    /* TODO: define what this returns
    @Override
    public EcobeeManagementHierarchy getHierarchy(int energyCompanyId) throws EcobeeAuthenticationException, 
            EcobeeCommunicationException {
        return null;
    }*/
    
    /**
     * Attempt to authenticate with the Ecobee API using the username and password from the specified energy company.
     * If the authentication is successful, the ecAuthKeys map will be updated with a new key for that energy company
     * id.
     * @throws EcobeeAuthenticationException if authentication fails.
     * @throws EcobeeCommunicationException if Yukon could not communicate with the Ecobee server.
     */
    private void authenticate(int energyCompanyId) throws EcobeeAuthenticationException, EcobeeCommunicationException {
        //TODO: switch between ecobee beta server and live server
    }
}
