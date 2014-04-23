package com.cannontech.dr.ecobee.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeManagementSet;

/**
 * This service provides the communications layer with the Ecobee portal. It is responsible for converting requests into
 * the appropriate JSON messages, sending them, and parsing the responses back into Java objects. It handles 
 * communications errors that occur and may throw various subclasses of EcobeeException if an operation fails.
 * 
 * This service also transparently handles authentication with the Ecobee portal (and re-authentication whenever the
 * session times out).
 * 
 * Finally, it is solely responsible for updating the Ecobee query counts via the EcobeeQueryCountDao.
 */
public interface EcobeeCommunicationService {
    
    /**
     * Removes the specified device from its management set, if it is in one, then moves the device into the specified 
     * management set.
     * Management sets that correspond to load groups are named by the load group id.
     * The management set for opted out devices is named "optout".
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     * @throws EcobeeDeviceDoesNotExistException if the specified device does not exist in the Ecobee API.
     * @throws EcobeeSetDoesNotExistException if the specified management set does not exist in the Ecobee API.
     */
    public void moveDeviceToSet(long serialNumber, String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException, EcobeeDeviceDoesNotExistException, 
            EcobeeSetDoesNotExistException;
    
    /**
     * Removes the specified device from its management set, if it is in one, and places it in the "default set", which
     * does not correspond to any load group.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     * @throws EcobeeDeviceDoesNotExistException if the specified device does not exist in the Ecobee API.
     */
    public void removeDeviceFromSet(long serialNumber, int energyCompanyId) throws EcobeeAuthenticationException, 
            EcobeeCommunicationException, EcobeeDeviceDoesNotExistException;
    
    /**
     * Requests device data for the specified devices over a limited date range.
     * @param serialNumbers Must contain no more than 25 serial numbers. Any devices specified that do not exist in the
     * Ecobee API will be ignored.
     * @param dateRange Must span no more than 7 days.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    public List<EcobeeDeviceReadings> readDeviceData(Iterable<Long> serialNumbers, Range<Instant> dateRange, 
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
     * @throws EcobeeSetDoesNotExistException if the specified management set does not exist in the Ecobee API.
     * @return True if the management set was deleted.
     */
    public boolean deleteManagementSet(String managementSetName, int energyCompanyId) 
            throws EcobeeAuthenticationException, EcobeeCommunicationException, EcobeeSetDoesNotExistException;
    
    /**
     * Moves the specified managementHierarchySet from one location to another.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     * @return True if the management set was successfully moved to the new path.
     */
    public boolean moveManagementSet(EcobeeManagementSet currentPath, EcobeeManagementSet newPath, int energyCompanyId)
            throws EcobeeAuthenticationException, EcobeeCommunicationException, EcobeeSetDoesNotExistException;
    
    /**
     * Retrieves the current hierarchy of management sets.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    /*public EcobeeManagementHierarchy getHierarchy(int energyCompanyId) throws EcobeeAuthenticationException, 
            EcobeeCommunicationException;
    */
}