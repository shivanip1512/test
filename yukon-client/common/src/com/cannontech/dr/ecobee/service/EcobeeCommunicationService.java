package com.cannontech.dr.ecobee.service;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;

import com.cannontech.common.util.Range;
import com.cannontech.dr.ecobee.EcobeeCommunicationException;
import com.cannontech.dr.ecobee.EcobeeDeviceDoesNotExistException;
import com.cannontech.dr.ecobee.EcobeeSetDoesNotExistException;
import com.cannontech.dr.ecobee.message.partial.Selection.SelectionType;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.google.common.collect.ImmutableList;

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
    String YUKON_CYCLE_EVENT_NAME = "yukonCycle";
    String YUKON_OVERRIDE_EVENT_NAME = "override";
    
    
    static final List<String> deviceReadColumns = ImmutableList.of(
        "zoneCalendarEvent", //currently running event
        "zoneAveTemp", // indoor temp
        "outdoorTemp", // outdoor temp
        "zoneCoolTemp", // cool set point
        "zoneHeatTemp", // heat set point
        "compCool1", // cool runtime
        "compHeat1"); // heat runtime
    
    /**
     * Registers the specified device with Ecobee.
     * @throws EcobeeCommunicationException if Yukon cannot log in or connect to Ecobee API, or if there is an error
     * registering the device.
     */
    void registerDevice(String serialNumber);
    
    /**
     * Deletes the specified device from Ecobee
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API
     */
    void deleteDevice(String serialNumber);
    
    /**
     * Removes the specified device from its management set, if it is in one, then moves the device into the specified
     * management set.
     * Management sets that correspond to load groups are named by the load group id.
     * The management set for opted out devices is EcobeeCommunicationService.OPT_OUT_SET.
     * The management set for unenrolled devices is EcobeeCommunicationService.UNASSIGNED_SET.
     * @throws EcobeeDeviceDoesNotExistException 
     * @throws EcobeeSetDoesNotExistException 
     */
    boolean moveDeviceToSet(String serialNumber, String setPath) throws EcobeeDeviceDoesNotExistException, 
            EcobeeSetDoesNotExistException;

    /**
     * Creates a new manaagement hierarchy set with teh specified name, directly beneath the root set "/"
     *
     * Returns true if the management set was created (or already exists)
     *
     * @param managementSetName
     * @throws EcobeeCommunicationException if Yukon cannot log in or connect to Ecobee API
     */
    boolean createManagementSet(String managementSetName);

    /**
     * Deletes the specified management hierarchy set, assuming it is located directly beneath the root set ("/").
     * @throws EcobeeCommunicationException if Yukon cannot log in or connect to Ecobee API
     * @return True if the management set was deleted.
     * @throws EcobeeSetDoesNotExistException
     */
    boolean deleteManagementSet(String managementSetName) throws EcobeeSetDoesNotExistException;

    /**
     * Moves the specified managementHierarchySet from one location to another.
     * @param currentPath The full path to the set, including the root "/" and the set name.
     * @param newPath The full path of the new parent set, including the root "/".
     * @throws EcobeeCommunicationException if Yukon cannot log in or connect to Ecobee API
     * @return True if the management set was successfully moved to the new path.
     */
    boolean moveManagementSet(String currentPath, String newParentPath);

    /**
     * Initiates a duty cycle demand response event in Ecobee.
     * @return the Ecobee identifier for this DR event. This identifier can be used to cancel an event in progress.
     * @throws EcobeeCommunicationException if Yukon cannot log in or connect to Ecobee API
     */
    String sendDutyCycleDR(EcobeeDutyCycleDrParameters parameters);
    
    /**
     * Initiates a 5-minute 0% duty cycle demand response event in ecobee.
     * This control will do no actual cycling. It's purpose is to override any events that are currently running.
     */
    void sendOverrideControl(String serialNumber);
    
    /**
     * Sends a message to cancel a DR event based on ecobee's event identifier.
     * @throws EcobeeCommunicationException if Yukon cannot log in or connect to Ecobee API
     */
    boolean sendRestore(String drIdentifier);

    /**
     * Gets the full hierarchy of sets and thermostats from Ecobee.
     * @throws EcobeeCommunicationException if Yukon cannot log in or connect to Ecobee API
     */
    List<SetNode> getHierarchy();

    /**
     * Requests device data for the specified devices or group (Management set)
     * @throws EcobeeCommunicationException if Yukon cannot connect to the Ecobee API.
     */

    List<EcobeeDeviceReadings> readDeviceData(SelectionType selectionType, Collection<String> selectionMatch,
            Range<LocalDate> dateRange);

}