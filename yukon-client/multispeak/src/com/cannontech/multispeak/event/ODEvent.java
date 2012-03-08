package com.cannontech.multispeak.event;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.message.porter.message.Return;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.OA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OutageDetectDeviceType;
import com.cannontech.multispeak.deploy.service.OutageDetectionEvent;
import com.cannontech.multispeak.deploy.service.OutageEventType;
import com.cannontech.multispeak.deploy.service.OutageLocation;
import com.cannontech.multispeak.deploy.service.impl.MultispeakPortFactory;
import com.cannontech.multispeak.service.MultispeakMeterService;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;

public class ODEvent extends MultispeakEvent{

    private final MeterDao meterDao = YukonSpringHook.getBean("meterDao", MeterDao.class);
    private final MultispeakMeterService multispeakMeterService = YukonSpringHook.getBean("multispeakMeterService", MultispeakMeterService.class);

   private OutageDetectionEvent outageDetectionEvent = null;

	/**
	 * @param vendorName_
	 * @param pilMessageID_
	 */
	public ODEvent(MultispeakVendor mspVendor_, long pilMessageID_, String transactionID_)
	{
		super(mspVendor_, pilMessageID_, transactionID_);
	}

    public OutageDetectionEvent getOutageDetectionEvent()
    {
        return outageDetectionEvent;
    }

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

        YukonMeter meter = meterDao.getYukonMeterForId(returnMsg.getDeviceID());
        OutageDetectionEvent ode = new OutageDetectionEvent();
        String meterNumber = meter.getMeterNumber();
        
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(returnMsg.getTimeStamp());
        ode.setEventTime(cal);
        ode.setObjectID(meterNumber);
        ode.setOutageDetectDeviceID(meterNumber);
        ode.setOutageDetectDeviceType(OutageDetectDeviceType.Meter);
        OutageLocation loc = new OutageLocation();
        loc.setObjectID(meterNumber);
        loc.setMeterNo(meterNumber);
        ode.setOutageLocation(loc);
        ode.setComments(returnMsg.getResultString());
        ode.setOutageEventType(OutageEventType.NoResponse);
        ode.setErrorString("Unknown return status: " + returnMsg.getResultString());
        
        ImmutableSetMultimap<OutageEventType, Integer> outageConfig = multispeakMeterService.getOutageConfig();
        ImmutableSet<OutageEventType> supportedEventTypes = multispeakMeterService.getSupportedEventTypes();
        for (OutageEventType eventType : supportedEventTypes) {
            if (outageConfig.get(eventType).contains(returnMsg.getStatus())) {
                ode.setOutageEventType(eventType);
                ode.setErrorString(eventType.getValue() + ": " + returnMsg.getResultString());
                break;
            }
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
        
        String endpointURL = getMspVendor().getEndpointURL(MultispeakDefines.OA_Server_STR);
        CTILogger.info("Sending ODEventNotification ("+ endpointURL+ "): Meter Number " + getOutageDetectionEvent().getObjectID());
        
        try {
            OutageDetectionEvent[] odEvents = new OutageDetectionEvent[1];
            odEvents [0] = getOutageDetectionEvent();
            
            OA_ServerSoap_BindingStub port = MultispeakPortFactory.getOA_ServerPort(getMspVendor());
            if (port != null) {
                ErrorObject[] errObjects = port.ODEventNotification(odEvents, getTransactionID());
                if( errObjects != null)
                    ((MultispeakFuncs)YukonSpringHook.getBean("multispeakFuncs")).logErrorObjects(endpointURL, "ODEventNotification", errObjects);
            } else {
                CTILogger.error("Port not found for OA_Server (" + getMspVendor().getCompanyName() + ")");
            }
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
