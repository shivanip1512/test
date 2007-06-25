/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.event;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfOutageDetectionEvent;
import com.cannontech.multispeak.service.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.service.OutageDetectDeviceType;
import com.cannontech.multispeak.service.OutageDetectionEvent;
import com.cannontech.multispeak.service.OutageEventType;
import com.cannontech.multispeak.service.OutageLocation;
import com.cannontech.multispeak.service.impl.MultispeakPortFactory;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ODEvent extends MultispeakEvent{

   private OutageDetectionEvent outageDetectionEvent = null;

	/**
	 * @param vendorName_
	 * @param pilMessageID_
	 */
	public ODEvent(MultispeakVendor mspVendor_, long pilMessageID_)//, int meterCount_)
	{
		super(mspVendor_, pilMessageID_);
	}

    /**
     * @return
     */
    public OutageDetectionEvent getOutageDetectionEvent()
    {
        return outageDetectionEvent;
    }
    /**
     * @param outageDetectionEvent_
     */
    public void setOutageDetectionEvent(OutageDetectionEvent outageDetectionEvent_)
    {
        this.outageDetectionEvent = outageDetectionEvent_;
    }

    /** ERRORCODE                               Description
     * The following codes are indeterminate, there may not be an outage
     * 31 (Timeout reading from port)           This is a communications error between Yukon and the CCU.  Not a carrier error.
     * 32 (Sequence Reject Frame Received)      This is a communications error between Yukon and the CCU.  Not a carrier error.
     * 33 (Framing error)                       This is a communications error between Yukon and the CCU.  Not a carrier error.
     * 65 (No DCD on return message)            This is a communications error between Yukon and the CCU.  Not a carrier error.
     *
     * The following codes constitute NO Outage, the meter responded in some fashion at least.
     * 1 (Bad BCH)                              Powerline carrier checksum failed.  Powerline is noisy, and the return message is incomplete when received by Yukon.
     * 17 (Word 1 Nack)                         CCU receives a partial return message from any 2-way device.
     * 74 (Route Failed on CCU Queue Entry)     Essentially the same as an error 17.  CCU receives a partial return message from any 2-way device.  The only difference being that a 74 will be the                             error generated for a queued message and an error 17 will be generated for a non-queued message.

     * The following codes constitute and OUTAGE 
     * 20 (Word 1 Nack Padded)                  No return message received at the CCU.  Failed 2-way device.
     * 57 (E-Word Received in Return Message)   This message only occurs in systems containing repeaters.  The repeater did not receive an expected return message from a 2-way device.                         Failed 2-way device.
     * 72 (DLC Read Timeout On CCU Queue Entry) Essentially the same as an error 20.  No return message received.  The only difference is that a 72 will be the error code during a queued message and                      an error 20 will be the error for a non-queued message.
     * 
     * Handle the pil Return messageReceived for ODEvents
     * @param event The OD event for the Return message
     * @param returnMsg The Pil return message
     */
    public boolean messageReceived(Return returnMsg) {

        String objectID = getObjectID(returnMsg.getDeviceID());
        OutageDetectionEvent ode = new OutageDetectionEvent();
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(returnMsg.getTimeStamp());
        ode.setEventTime(cal);
        ode.setObjectID(objectID);
        ode.setOutageDetectDeviceID(objectID);
        ode.setOutageDetectDeviceType(OutageDetectDeviceType.Meter);
        OutageLocation loc = new OutageLocation();
        loc.setObjectID(objectID);
        loc.setMeterNo(objectID);
        ode.setOutageLocation(loc);
        ode.setComments(returnMsg.getResultString());
        
        if( returnMsg.getStatus() == 20 || returnMsg.getStatus() == 57 || returnMsg.getStatus() == 72)
        {   //Meter did not respond - outage assumed
            CTILogger.info("OutageDetectionEvent(" + objectID + ") - Ping Failed:" + returnMsg.getResultString());
            ode.setOutageEventType(OutageEventType.Outage);
            ode.setErrorString("Ping failed: " + returnMsg.getResultString());
        }
        else if( returnMsg.getStatus() == 31 || returnMsg.getStatus() == 32 || returnMsg.getStatus() == 33 || returnMsg.getStatus() == 65)
        {   //Unknown, but may not be an outage
            CTILogger.info("OutageDetectionEvent(" + objectID + ") - Communication Failure:" + returnMsg.getResultString());
            ode.setOutageEventType(OutageEventType.NoResponse);
            ode.setErrorString("Communication failure: " + returnMsg.getResultString());
        }
        else if( returnMsg.getStatus() == 1 || returnMsg.getStatus() == 17 || returnMsg.getStatus() == 74 || returnMsg.getStatus() == 0)
        {   //Meter responsed in some way or another, 0 status was perfect
            CTILogger.info("OutageDetectionEvent(" + objectID + ") - Ping Successful");
            ode.setOutageEventType(OutageEventType.Restoration);
        }
        else 
        {   //No idea what code this is
            CTILogger.info("Meter (" + objectID + ") - Unknown return status from ping: " +returnMsg.getStatus());
            CTILogger.info("OutageDetectionEvent(" + objectID + ") - Ping Status Unknown");
            ode.setOutageEventType(OutageEventType.NoResponse);
            ode.setErrorString("Unknown return status: " + returnMsg.getResultString());
        }
        
        setOutageDetectionEvent(ode);
        
        eventNotification();
        return true;
    }

    /**
     * Send an ODEventNotification to the OMS webservice containing odEvents 
     * @param odEvents
     */
    public void eventNotification() {
        
        String endpointURL = getMspVendor().getEndpointURL(MultispeakDefines.OA_OD_STR);
        CTILogger.info("Sending ODEventNotification ("+ endpointURL+ "): Meter Number " + getOutageDetectionEvent().getObjectID());
        
        try {
            OutageDetectionEvent[] odEventArray = new OutageDetectionEvent[1];
            odEventArray[0] = getOutageDetectionEvent();
            ArrayOfOutageDetectionEvent arrayODEvents = new ArrayOfOutageDetectionEvent(odEventArray);
            
            OA_ODSoap_BindingStub port = MultispeakPortFactory.getOA_ODPort(getMspVendor());
            ArrayOfErrorObject errObjects = port.ODEventNotification(arrayODEvents);
            if( errObjects != null)
                ((MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs")).logArrayOfErrorObjects(endpointURL, "ODEventNotification", errObjects.getErrorObject());
            
        } catch (RemoteException e) {
            CTILogger.error("TargetService: " + endpointURL + " - initiateOutageDetection (" + getMspVendor().getCompanyName() + ")");
            CTILogger.error("RemoteExceptionDetail: " + e.getMessage());
        }
    }
    
    public boolean isPopulated() {
        //Technically this object is always/never populated because it is asynchronous. 
        return true;
    }
}
