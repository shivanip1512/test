 /*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.service;

import java.rmi.RemoteException;

import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.ServiceLocation;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MultispeakMeterService {
	
    /** 
     * Returns the LoadActionCode (or connected status) for meter
     * @param mspVendor
     * @param meter
     * @param transactionID
     * @return
     * @throws RemoteException
     */
    public LoadActionCode CDMeterState(MultispeakVendor mspVendor, 
            com.cannontech.amr.meter.model.Meter meter,
            String transactionID) throws RemoteException;

    /**
     * This is a workaround method for SEDC.  This method is used to perform an actual meter interrogation and then return
     * the collected reading if message recieved within 2 minutes.
     * @param mspVendor
     * @param meterNumber
     * @return
     * @throws RemoteException
     */
    public MeterRead getLatestReadingInterrogate(MultispeakVendor mspVendor, 
            com.cannontech.amr.meter.model.Meter meter,
            String transactionID);

	/**
	 * Send a ping command to pil connection for each meter in meterNumbers.
	 * @param meterNumbers
	 * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
	 */
	public ErrorObject[] odEvent(MultispeakVendor vendor, 
	        String[] meterNumbers,
	        String transactionID) throws RemoteException;
	
    /**
     * Send meter read commands to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public ErrorObject[] meterReadEvent(MultispeakVendor vendor, 
            String[] meterNumbers,
            String transactionID);

    /**
     * Send meter read commands to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public ErrorObject[] blockMeterReadEvent(MultispeakVendor vendor, 
            String meterNumber, FormattedBlockService block, String transactionID);

    /**
     * Send a ping command to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public ErrorObject[] cdEvent(MultispeakVendor vendor, 
            ConnectDisconnectEvent [] cdEvents,
            String transactionID) throws RemoteException;

    /**
     * Add MeterNos to SystemGroupEnum.DISCONNECTSTATUS Device Group. 
     * @param mspVendor
     * @param meterNos
     * @return
     */
    public ErrorObject[] initiateDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos);

    /**
     * Add MeterNos to SystemGroupEnum.USAGEMONITORING Device Group.
     * @param mspVendor
     * @param meterNos
     * @return
     */
    public ErrorObject[] initiateUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos);

    /**
     * Remove MeterNos from SystemGroupEnum.DISCONNECTSTATUS Device Group. 
     * @param mspVendor
     * @param meterNos
     * @return
     */
    public ErrorObject[] cancelDisconnectedStatus(MultispeakVendor mspVendor, String[] meterNos);

    /**
     * Remove MeterNos from SystemGroupEnum.USAGEMONITORING Device Group.
     * @param mspVendor
     * @param meterNos
     * @return
     */
    public ErrorObject[] cancelUsageMonitoringStatus(MultispeakVendor mspVendor, String[] meterNos);
    
    /**
     * Add addMeters to Yukon database.
     * If the meter already exists in Yukon (looked up by MeterNumber, then Address, then DeviceName), 
     *  and the meter is disabled, then the meter will be updated with the new information.
     *  Else, an ErrorObject will be returned.
     * If the meter does not already exist in Yukon (looked up by MeterNumber, then Address, then DeviceName),
     *  then a new Meter object will be added to Yukon.
     * @param mspVendor
     * @param addMeters
     * @return
     * @throws RemoteException
     */
    public ErrorObject[] addMeterObject(final MultispeakVendor mspVendor, Meter[] addMeters) throws RemoteException;

    /**
     * Removes (disables) a list of meters in Yukon.
     * @param mspVendor
     * @param removeMeters
     * @return
     */
    public ErrorObject[] removeMeterObject(MultispeakVendor mspVendor, Meter[] removeMeters);

    /**
     * Updates the "meter" object, based on the PaoName Alias lookup value. 
     * @param mspVendor
     * @param serviceLocations
     * @return
     */
    public ErrorObject[] updateServiceLocation(final MultispeakVendor mspVendor, ServiceLocation[] serviceLocations);

    /**
     * Changes the meter information.  Meter is looked up by the Physical Addres (TransponderId). 
     * @param mspVendor
     * @param changedMeters
     * @return
     * @throws RemoteException
     */
    public ErrorObject[] changeMeterObject(final MultispeakVendor mspVendor, Meter[] changedMeters) throws RemoteException;

}
