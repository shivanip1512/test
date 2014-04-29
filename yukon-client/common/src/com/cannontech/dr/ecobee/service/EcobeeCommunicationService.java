package com.cannontech.dr.ecobee.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.EcobeeDeviceDoesNotExistException;
import com.cannontech.dr.ecobee.EcobeeSetDoesNotExistException;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;

/**
 * This service provides the communications layer with the Ecobee portal. It is responsible for converting requests into
 * the appropriate JSON messages, sending them, and parsing the responses back into Java objects. It handles 
 * communications errors that occur and may throw various subclasses of EcobeeException if an operation fails.
 * 
 * Authentication with the Ecobee portal (and re-authentication whenever the session times out) is handled automatically 
 * by EcobeeCommunicationAopAuthenticator.
 * 
 * This service is solely responsible for updating the Ecobee query counts via the EcobeeQueryCountDao.
 */
public interface EcobeeCommunicationService {
    public static final String OPT_OUT_SET = "/optout";
    public static final String UNENROLLED_SET = "/unenrolled";
    
    /**
     * Registers the specified device with Ecobee.
     */
    public boolean registerDevice(String serialNumber, int energyCompanyId) throws EcobeeAuthenticationException, 
            EcobeeCommunicationException;
    
    /**
     * Removes the specified device from its management set, if it is in one, then moves the device into the specified 
     * management set.
     * Management sets that correspond to load groups are named by the load group id.
     * The management set for opted out devices is EcobeeCommunicationService.OPT_OUT_SET.
     * The management set for unenrolled devices is EcobeeCommunicationService.UNASSIGNED_SET.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    public boolean moveDeviceToSet(String serialNumber, String setPath, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException, EcobeeSetDoesNotExistException,
            EcobeeDeviceDoesNotExistException;

    /**
     * Requests device data for the specified devices over a limited date range.
     * @param serialNumbers Must contain no more than 25 serial numbers. Any devices specified that do not exist in the
     * Ecobee API will be ignored.
     * @param dateRange Must span no more than 7 days.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    public List<EcobeeDeviceReadings> readDeviceData(Iterable<String> serialNumbers, Range<Instant> dateRange, 
            int energyCompanyId) throws EcobeeAuthenticationException, EcobeeCommunicationException;
    
    /**
     * Creates a new management hierarchy set with the specified name, directly beneath the root set ("/").
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     * @return True if the management set was created.
     */
    public boolean createManagementSet(String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException;
    
    /**
     * Deletes the specified management hierarchy set, assuming it is located directly beneath the root set ("/").
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     * @return True if the management set was deleted.
     */
    public boolean deleteManagementSet(String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException;
    
    /**
     * Moves the specified managementHierarchySet from one location to another.
     * @param currentPath The full path to the set, including the root "/" and the set name.
     * @param newPath The full path of the new parent set, including the root "/".
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     * @return True if the management set was successfully moved to the new path.
     */
    public boolean moveManagementSet(String currentPath, String newPath, int energyCompanyId)
            throws EcobeeAuthenticationException, EcobeeCommunicationException;
}