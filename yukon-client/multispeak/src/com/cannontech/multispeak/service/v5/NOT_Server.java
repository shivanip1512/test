package com.cannontech.multispeak.service.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeterExchange;
import com.cannontech.msp.beans.v5.multispeak.ObjectDeletion;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;


public interface NOT_Server {

    /**
     * Requester Pings URL of NOT to see if it is alive.
     * 
     * @throws MultispeakWebServiceException
     */
    public void pingURL() throws MultispeakWebServiceException;

    
    /**
     * Requester requests list of methods supported by NOT.
     */
    List<String> getMethods() throws MultispeakWebServiceException;
   
    /**
     * SCADA Analog Changed Notification.
     * 
     * @param scadaAnalogs the scada analogs
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> scadaAnalogsChangedNotification(List<SCADAAnalog> scadaAnalogs)
            throws MultispeakWebServiceException;
    
    /**
     * Meters Changed Notification.
     * 
     * @param ElectricMeter
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    
    List<ErrorObject> metersChangedNotification(List<ElectricMeter> electricMeters)
            throws MultispeakWebServiceException;
    
    /**
     * service Locations Changed Notification.
     * 
     * @param changedServiceLocations the changed service locations
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
    */
   public List<ErrorObject> serviceLocationsChangedNotification(List<ServiceLocation> changedServiceLocations)
            throws MultispeakWebServiceException;
    /**
     * Meters Creation Notification.
     * 
     * @param ElectricMeter
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    List<ErrorObject> metersCreatedNotification(List<ElectricMeter> addedMeters) throws MultispeakWebServiceException;

    /**
     * Meters Uninstall Notification.
     * 
     * @param ElectricMeter
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> metersUninstalledNotification(List<ElectricMeter> electricMeters) throws MultispeakWebServiceException;

    /**
     * Meters Install Notification.
     * 
     * @param ElectricMeter
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> metersInstalledNotification(List<ElectricMeter> electricMeters) throws MultispeakWebServiceException;

    /**
     * Meters Exchange Notification.
     * 
     * @param ElectricMeter
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> metersExchangedNotification(List<ElectricMeterExchange> exchangeMeters) throws MultispeakWebServiceException;

    /**
     * Meters Delete Notification.
     * 
     * @param ElectricMeter
     * @return the error object[]
     * @throws MultispeakWebServiceException the multispeak web service exception
     */
    public List<ErrorObject> metersDeletedNotification(List<ObjectDeletion> electricMeters) throws MultispeakWebServiceException;


}
