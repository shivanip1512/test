package com.cannontech.dr.ecobee.service;

import java.util.List;

import org.joda.time.Instant;

import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.EcobeeException;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;

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
    String OPT_OUT_SET = "optout";
    String UNENROLLED_SET = "unenrolled";

    /**
     * Registers the specified device with Ecobee.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    boolean registerDevice(String serialNumber, int ecId) throws EcobeeException;
    
    /**
     * Removes the specified device from its management set, if it is in one, then moves the device into the specified 
     * management set.
     * Management sets that correspond to load groups are named by the load group id.
     * The management set for opted out devices is EcobeeCommunicationService.OPT_OUT_SET.
     * The management set for unenrolled devices is EcobeeCommunicationService.UNASSIGNED_SET.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    boolean moveDeviceToSet(String serialNumber, String setPath, int ecId) throws EcobeeException;

    /**
     * Requests device data for the specified devices over a limited date range.
     * @param serialNumbers Must contain no more than 25 serial numbers. Any devices specified that do not exist in the
     * Ecobee API will be ignored.
     * @param dateRange Must span no more than 7 days.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    List<EcobeeDeviceReadings> readDeviceData(Iterable<String> serialNumbers, Range<Instant> dateRange, int ecId)
            throws EcobeeException;

    /**
     * Creates a new manaagement hierarchy set with teh specified name, directly beneath the root set "/"
     * 
     * Returns true if the management set was created (or already exists)
     * 
     * @param managementSetName
     * @throws EcobeeException if Yukon cannot log in or connect to Ecobee API
     */
    boolean createManagementSet(String managementSetName, int ecId) throws EcobeeException;

    /**
     * Deletes the specified management hierarchy set, assuming it is located directly beneath the root set ("/").
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     * @return True if the management set was deleted.
     */
    boolean deleteManagementSet(String managementSetName, int ecId) throws EcobeeException;
    
    /**
     * Moves the specified managementHierarchySet from one location to another.
     * @param currentPath The full path to the set, including the root "/" and the set name.
     * @param newPath The full path of the new parent set, including the root "/".
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     * @return True if the management set was successfully moved to the new path.
     */
    boolean moveManagementSet(String currentPath, String newPath, int ecId) throws EcobeeException;
    
    /**
     * Initiates a duty cycle demand response event in Ecobee.
     * @return the Ecobee identifier for this DR event. This identifier can be used to cancel an event in progress.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters, int ecId) throws EcobeeException;
    
    /**
     * Sends a message to cancel a DR event based on ecobee's event identifier.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    boolean sendRestore(String drIdentifier, int ecId) throws EcobeeException;
    
    /**
     * Gets the full hierarchy of sets and thermostats from Ecobee.
     * @throws EcobeeAuthenticationException if Yukon cannot log in to the Ecobee API.
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */
    List<SetNode> getHierarchy(int ecId) throws EcobeeException;

}