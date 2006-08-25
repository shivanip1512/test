 /*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.multispeak.data.MeterReadFactory;
import com.cannontech.multispeak.data.ReadableDevice;
import com.cannontech.multispeak.db.MultispeakInterface;
import com.cannontech.multispeak.event.MeterReadEvent;
import com.cannontech.multispeak.event.MultispeakEvent;
import com.cannontech.multispeak.event.ODEvent;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfMeterRead;
import com.cannontech.multispeak.service.ArrayOfOutageDetectionEvent;
import com.cannontech.multispeak.service.CB_MR;
import com.cannontech.multispeak.service.CB_MRLocator;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.CB_MRSoap_PortType;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.MeterRead;
import com.cannontech.multispeak.service.OA_OD;
import com.cannontech.multispeak.service.OA_ODLocator;
import com.cannontech.multispeak.service.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.service.OA_ODSoap_PortType;
import com.cannontech.multispeak.service.OutageDetectDeviceType;
import com.cannontech.multispeak.service.OutageDetectionEvent;
import com.cannontech.multispeak.service.OutageEventType;
import com.cannontech.multispeak.service.OutageLocation;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Multispeak implements MessageListener {
	
	/** An instance of this */
	private static Multispeak _mspInstance = null;

	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to MultispeakEvent values */
	private static Map<Long,MultispeakEvent> eventsMap = new HashMap<Long,MultispeakEvent>();
	 
	/**
	 * Get the static instance of Multispeak (this) object.
	 * Adds a message listener to the pil connection instance. 
	 * @return
	 */
   
	public static synchronized Multispeak getInstance()
	{
		if( _mspInstance == null ) 
		{
			CTILogger.info("New MSP instance created");
			_mspInstance = new Multispeak();
			_mspInstance.getPilConn().addMessageListener(_mspInstance);
			CTILogger.info("Porter Connection Valid: " + Multispeak.getInstance().getPilConn().isValid());
		}

		return _mspInstance;
	}

	/**
	 * A static instance of connection to Pil
	 * @return
	 */
	public IServerConnection getPilConn()
	{
		return ConnPool.getInstance().getDefPorterConn();        
	}
	
	/**
	 * generate a unique mesageid, don't let it be negative
	 * @return
	 */
	private synchronized long generateMessageID() {
		if(++messageID == Long.MAX_VALUE) {
			messageID = 1;
		}
		return messageID;
	}

	/* (non-Javadoc)
	 * @see com.cannontech.message.util.MessageListener#messageReceived(com.cannontech.message.util.MessageEvent)
	 */
	public void messageReceived(MessageEvent e)
	{
		Message in = e.getMessage();		
		if(in instanceof Return)
		{
			Return returnMsg = (Return) in;
			synchronized(this)
			{
//				if( !getRequestMessageIDs().contains( new Long(returnMsg.getUserMessageID())))
//				{
//					CTILogger.info("Unknown Message: "+ returnMsg.getUserMessageID() +" Command [" + returnMsg.getCommandString()+"]");
//					CTILogger.info("Unknown Message: "+ returnMsg.getUserMessageID() +" Result [" + returnMsg.getResultString()+"]");
//					return;
//				}
//				else
//				{
//					//TODO Should the ids be removed after being processed ? 
//					//Remove the messageID from the set of this ids.
//					if(sendMore == 0 && returnMsg.getExpectMore() == 0)	//nothing more is coming, remove from list.
//					{
//						getRequestMessageIDs().remove( new Long(returnMsg.getUserMessageID()));
//					}
//				}
				CTILogger.debug("A MESSAGE: (ExpectMore=" + returnMsg.getExpectMore() + ") " + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());
				if( returnMsg.getExpectMore() == 0)
				{
					CTILogger.info("Received Message From ID:" + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());
					MultispeakEvent event = (MultispeakEvent)getEventsMap().get(new Long (returnMsg.getUserMessageID()) );
					
					if( event instanceof ODEvent)
						messageReceived_ODEvent((ODEvent)event, returnMsg);

					else if (event instanceof MeterReadEvent)
						messageReceived_MeterReadEvent((MeterReadEvent)event, returnMsg);

				}
			}
		}
	}
	/**
     * Handle the pil Return messageReceived for MeterReadEvents
	 * @param mrEvent The meterRead event for the Return message
	 * @param returnMsg The Pil return message
	 */
	private void messageReceived_MeterReadEvent(MeterReadEvent mrEvent, Return returnMsg)
	{
		String key = mrEvent.getMspVendor().getUniqueKey();
		String objectID = MultispeakFuncs.getObjectID(key, returnMsg.getDeviceID());						
	    if( returnMsg.getStatus() != 0)
		{
	        String result = "MeterReadEvent(" + objectID + ") - Reading Failed (ERROR:" + returnMsg.getStatus() + ") " + returnMsg.getResultString();
	        CTILogger.info(result);
	        //TODO Should we send old data if a new reading fails?
            mrEvent.getDevice().populateWithPointData(returnMsg.getDeviceID());
	        mrEvent.getDevice().getMeterRead().setErrorString(result);
		}
	    else
	    {
            CTILogger.info("MeterReadEvent(" + objectID + ") - Reading Successful" );
	        if(returnMsg.getVector().size() > 0 )
			{
				for (int i = 0; i < returnMsg.getVector().size(); i++)
				{
					Object o = returnMsg.getVector().elementAt(i);
					//TODO SN - Hoping at this point that only one value comes back in the point data vector 
					if (o instanceof PointData)
					{
						PointData pointData = (PointData) o;
                        LitePoint lPoint = DaoFactory.getPointDao().getLitePoint(pointData.getId());
                        mrEvent.getDevice().populate(lPoint.getPointType(), lPoint.getPointOffset(), lPoint.getUofmID(), pointData.getPointDataTimeStamp(), pointData.getValue());
					}
				}
			}
	    }
        if(returnMsg.getExpectMore() == 0){
            mrEvent.messageReceived();
            if( mrEvent.getReturnMessages() == 0) {
                notify();
                ReadingChangedNotification(mrEvent);
                getEventsMap().remove(new Long(mrEvent.getPilMessageID()));
            }
        }
	}

	/** ERRORCODE								Description
	 * The following codes are indeterminate, there may not be an outage
	 * 31 (Timeout reading from port)			This is a communications error between Yukon and the CCU.  Not a carrier error.
	 * 32 (Sequence Reject Frame Received)		This is a communications error between Yukon and the CCU.  Not a carrier error.
	 * 33 (Framing error)						This is a communications error between Yukon and the CCU.  Not a carrier error.
	 * 65 (No DCD on return message)			This is a communications error between Yukon and the CCU.  Not a carrier error.
	 *
	 * The following codes constitute NO Outage, the meter responded in some fashion at least.
	 * 1 (Bad BCH)								Powerline carrier checksum failed.  Powerline is noisy, and the return message is incomplete when received by Yukon.
	 * 17 (Word 1 Nack)							CCU receives a partial return message from any 2-way device.
	 * 74 (Route Failed on CCU Queue Entry)		Essentially the same as an error 17.  CCU receives a partial return message from any 2-way device.  The only difference being that a 74 will be the 							error generated for a queued message and an error 17 will be generated for a non-queued message.

	 * The following codes constitute and OUTAGE 
	 * 20 (Word 1 Nack Padded)					No return message received at the CCU.  Failed 2-way device.
	 * 57 (E-Word Received in Return Message)	This message only occurs in systems containing repeaters.  The repeater did not receive an expected return message from a 2-way device.							Failed 2-way device.
	 * 72 (DLC Read Timeout On CCU Queue Entry)	Essentially the same as an error 20.  No return message received.  The only difference is that a 72 will be the error code during a queued message and 						an error 20 will be the error for a non-queued message.
     * 
     * Handle the pil Return messageReceived for ODEvents
     * @param event The OD event for the Return message
     * @param returnMsg The Pil return message
	 */
	private void messageReceived_ODEvent(ODEvent event, Return returnMsg)
	{
		String key = event.getMspVendor().getUniqueKey();
		String objectID = MultispeakFuncs.getObjectID(key, returnMsg.getDeviceID());						
			
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
	    
		if( returnMsg.getStatus() == 20 || returnMsg.getStatus() == 57 || returnMsg.getStatus() == 72)
		{	//Meter did not respond - outage assumed
			CTILogger.info("OutageDetectionEvent(" + objectID + ") - Ping Failed:" + returnMsg.getResultString());
			ode.setOutageEventType(OutageEventType.Outage);
			ode.setErrorString("Ping failed: " + returnMsg.getResultString());
		}
		else if( returnMsg.getStatus() == 31 || returnMsg.getStatus() == 32 || returnMsg.getStatus() == 33 || returnMsg.getStatus() == 65)
		{	//Unknown, but may not be an outage
			CTILogger.info("OutageDetectionEvent(" + objectID + ") - Communication Failure:" + returnMsg.getResultString());
			ode.setOutageEventType(OutageEventType.NoResponse);
			ode.setErrorString("Communication failure: " + returnMsg.getResultString());
		}
		else if( returnMsg.getStatus() == 1 || returnMsg.getStatus() == 17 || returnMsg.getStatus() == 74 || returnMsg.getStatus() == 0)
		{	//Meter responsed in some way or another, 0 status was perfect
			CTILogger.info("OutageDetectionEvent(" + objectID + ") - Ping Successful");
			ode.setOutageEventType(OutageEventType.Restoration);
		}
		else 
		{	//No idea what code this is
			CTILogger.info("Meter (" + objectID + ") - Unknown return status from ping: " +returnMsg.getStatus());
			CTILogger.info("OutageDetectionEvent(" + objectID + ") - Ping Status Unknown");
			ode.setOutageEventType(OutageEventType.NoResponse);
			ode.setErrorString("Unknown return status: " + returnMsg.getResultString());
		}
	    
		event.setOutageDetectionEvent(ode);
		ODEventNotification(event);

		getEventsMap().remove(new Long(event.getPilMessageID()));        
	}

	/**
	 * Send a ping command to pil connection for each meter in meterNumbers.
	 * @param meterNumbers
	 * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
	 */
	public synchronized ErrorObject[] ODEvent(MultispeakVendor vendor, String[] meterNumbers) throws RemoteException
	{
		Vector errorObjects = new Vector();
		Request pilRequest = null;
		CTILogger.info("Received " + meterNumbers.length + " Meter(s) for Outage Verification Testing from " + vendor.getCompanyName());
		
        for (String meterNumber : meterNumbers) {
			LiteYukonPAObject lPao = MultispeakFuncs.getLiteYukonPaobject(vendor.getUniqueKey(), meterNumber);
			if (lPao == null)
			{
                ErrorObject err = MultispeakFuncs.getErrorOjbect(meterNumber, "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.");
				errorObjects.add(err);
			}
			else
			{
				long id = generateMessageID();		
				ODEvent event = new ODEvent(vendor, id);
				getEventsMap().put(new Long(id), event);
			    
				pilRequest = new Request(lPao.getYukonID(), "ping noqueue", id);
				pilRequest.setPriority(13);	//just below Client applications
				getPilConn().write(pilRequest);
			}
			
		}
    		
		if( !errorObjects.isEmpty())
		{
			ErrorObject[] errors = new ErrorObject[errorObjects.size()];
			errorObjects.toArray(errors);
			return errors;
		}
		return new ErrorObject[0];
	}
	
    /**
     * Send meter read commands to pil connection for each meter in meterNumbers.
     * @param meterNumbers
     * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
     */
    public synchronized ErrorObject[] MeterReadEvent(MultispeakVendor vendor, String[] meterNumbers) throws RemoteException
    {
        Vector errorObjects = new Vector();
        Request pilRequest = null;
        CTILogger.info("Received " + meterNumbers.length + " Meter(s) for Outage Verification Testing from " + vendor.getCompanyName());
        
        for (String meterNumber : meterNumbers) {
            LiteYukonPAObject lPao = MultispeakFuncs.getLiteYukonPaobject(vendor.getUniqueKey(), meterNumber);
            if (lPao == null)
            {
                ErrorObject err = MultispeakFuncs.getErrorOjbect(meterNumber, "MeterNumber: " + meterNumber + " - Was NOT found in Yukon.");
                errorObjects.add(err);
            }
            else
            {
                long id = generateMessageID();      
                MeterReadEvent event = new MeterReadEvent(vendor, id, 2);
                ReadableDevice device = MeterReadFactory.createMeterReadObject(lPao.getCategory(), lPao.getType(), meterNumber);
                event.setDevice(device);
                getEventsMap().put(new Long(id), event);
                
                pilRequest = new Request(lPao.getYukonID(), "getvalue kwh update", id);
                pilRequest.setPriority(13); //just below Client applications
                getPilConn().write(pilRequest);
                
                pilRequest.setCommandString("getvalue demand update");
                getPilConn().write(pilRequest);
            }
            
        }
        
        if( !errorObjects.isEmpty())
        {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }
    
	/*public MeterRead MeterReadEvent(MultispeakVendor mspVendor, String meterNumber, String command) throws RemoteException
	{
        LiteYukonPAObject lPao = MultispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNumber);
        long id = generateMessageID();
        MeterReadEvent event = new MeterReadEvent(mspVendor, id);
        
        if (lPao != null)
        {		
            ReadableDevice device = MeterReadFactory.createMeterReadObject(lPao.getCategory(), lPao.getType(), meterNumber);
            event.setDevice(device);
    		getEventsMap().put(new Long(id), event);
    		
    		Request pilRequest = null;
    		CTILogger.info("Received " + meterNumber + " for Meter Reading from " + mspVendor.getCompanyName());

			pilRequest = new Request(lPao.getYukonID(), command, id);
			pilRequest.setPriority(15);
			getPilConn().write(pilRequest);

            CTILogger.info(System.currentTimeMillis() + " HI");
            try
            {
                wait(120000l);
            } catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            CTILogger.info(System.currentTimeMillis() + " HI2");
/*			synchronized (event)
			{
				long millisTimeOut = 0;	//
				while (event.getMeterRead() == null && millisTimeOut < 120000)	//quit after 2 minutes
				{
					try
					{
						Thread.sleep(1000);
						millisTimeOut += 1000;
					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
				if( millisTimeOut >= 120000)// this broke the loop, more than likely, have to kill it sometime
					event.getMeterRead().setErrorString("Reading Timed out after 2 minutes.");

			}*/
	/*	}

		return event.getDevice().getMeterRead();
	}*/
	
	/**
	 * Send an ODEventNotification to the OMS webservice containing odEvents 
	 * @param odEvents
	 */
	public void ODEventNotification(ODEvent odEvent)
	{
        MultispeakVendor vendor = odEvent.getMspVendor();
        //TODO need to change using the getServiceToEndpointMap
		MultispeakInterface mspInterface = odEvent.getMspVendor().getMspInterfaceMap().get(MultispeakDefines.OA_OD_STR);
		String endpointURL = "";			
		if( mspInterface != null)
			endpointURL = vendor.getUrl() + mspInterface.getMspEndpoint();

        CTILogger.info("Sending ODEventNotification ("+ endpointURL+ "): Meter Number " + odEvent.getOutageDetectionEvent().getObjectID());
		try
		{
			
			OA_OD service = new OA_ODLocator();
			((OA_ODLocator)service).setOA_ODSoapEndpointAddress(endpointURL);
			
			OA_ODSoap_PortType port = service.getOA_ODSoap();
			
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
			((OA_ODSoap_BindingStub)port).setHeader(header);
			((OA_ODSoap_BindingStub)port).setTimeout(10000);	//should respond within 10 seconds, right?
	
			OutageDetectionEvent[] odEventArray = new OutageDetectionEvent[1];
			odEventArray[0] = odEvent.getOutageDetectionEvent();
			ArrayOfOutageDetectionEvent arrayODEvents = new ArrayOfOutageDetectionEvent(odEventArray);
			
			ArrayOfErrorObject errObjects = port.ODEventNotification(arrayODEvents);
			if( errObjects != null)
				MultispeakFuncs.logArrayOfErrorObjects(endpointURL, "ODEventNotification", errObjects.getErrorObject());
		} catch (ServiceException e)
		{	
			CTILogger.info("OA_OD service is not defined for company name: " + vendor.getCompanyName()+ ".  initiateOutageDetection cancelled.");
			e.printStackTrace();
		} catch (RemoteException e) {
			
			CTILogger.info("Could not find a target service to invoke!  targetService: " + endpointURL + " companyName: " + vendor.getCompanyName() + ".  initiateOutageDetection cancelled.");
			e.printStackTrace();
		}	
	}
    
    /**
     * Send an ReadingChangedNotification to the CB webservice containing meterReadEvents 
     * @param odEvents
     */
    public void ReadingChangedNotification(MeterReadEvent meterReadEvent)
    {
        MultispeakVendor vendor = meterReadEvent.getMspVendor();
        MultispeakInterface mspInterface = meterReadEvent.getMspVendor().getMspInterfaceMap().get(MultispeakDefines.CB_MR_STR);

        String endpointURL = "";            
        if( mspInterface != null)
            endpointURL = vendor.getUrl() + mspInterface.getMspEndpoint();

        CTILogger.info("Sending ReadingChangedNotification ("+ endpointURL+ "): Meter Number " + meterReadEvent.getDevice().getMeterRead().getObjectID()); 
        try
        {            
            CB_MR service = new CB_MRLocator();
            ((CB_MRLocator)service).setCB_MRSoapEndpointAddress(endpointURL);
            
            CB_MRSoap_PortType port = service.getCB_MRSoap();
            
            SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
            ((CB_MRSoap_BindingStub)port).setHeader(header);
            ((CB_MRSoap_BindingStub)port).setTimeout(10000);    //should respond within 10 seconds, right?
    
            MeterRead [] meterReadArray = new MeterRead[1];
            meterReadArray[0] = meterReadEvent.getDevice().getMeterRead();
            ArrayOfMeterRead arrayMeterRead = new ArrayOfMeterRead(meterReadArray);
            
            ArrayOfErrorObject errObjects = port.readingChangedNotification(arrayMeterRead);
            if( errObjects != null)
                MultispeakFuncs.logArrayOfErrorObjects(endpointURL, "ReadingChangedNotification", errObjects.getErrorObject());
        } catch (ServiceException e)
        {   
            CTILogger.info("CB_MR service is not defined for company name: " + vendor.getCompanyName()+ ".  Method cancelled.");
            e.printStackTrace();
        } catch (RemoteException e) {
            
            CTILogger.info("Could not find a target service to invoke!  targetService: " + endpointURL + " companyName: " + vendor.getCompanyName() + ".  Method cancelled.");
            e.printStackTrace();
        }   
    }
	/**
	 * @return
	 */
	public static Map<Long,MultispeakEvent> getEventsMap() {
		return eventsMap;
	}
    
    public ErrorObject[] initiateStatusChange(MultispeakVendor mspVendor, String[] meterNos, String statusPrefix) {
        Vector errorObjects = new Vector();
        for  (int i = 0; i < meterNos.length; i++){
            String meterNo = meterNos[i];
            LiteYukonPAObject liteYukonPaobject = MultispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNo);
            if( liteYukonPaobject != null){
                YukonPAObject yukonPaobject = (YukonPAObject)MultispeakFuncs.getDbPersistentDao().retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase){
                    String origCollGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup().getCollectionGroup();
                    if( origCollGroup.indexOf(statusPrefix) < 0) {
                        ((MCTBase)yukonPaobject).getDeviceMeterGroup().setCollectionGroup(statusPrefix + origCollGroup);
                        MultispeakFuncs.getDbPersistentDao().performDBChange(yukonPaobject, Transaction.UPDATE);
                    } else {
                        ErrorObject err = MultispeakFuncs.getErrorOjbect(meterNo, "MeterNumber: " + meterNo + " - Is already in State (" + origCollGroup +").  Cannot initiate change.");
                        errorObjects.add(err);
                    }
                }
            }else {
                ErrorObject err = MultispeakFuncs.getErrorOjbect(meterNo, "MeterNumber: " + meterNo + " - Was NOT found in Yukon.");
                errorObjects.add(err);              
            }
        }
        if( !errorObjects.isEmpty())
        {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }

    public ErrorObject[] cancelStatusChange(MultispeakVendor mspVendor, String[] meterNos, String statusPrefix) {
        Vector errorObjects = new Vector();
        for  (int i = 0; i < meterNos.length; i++){
            String meterNo = meterNos[i];
            LiteYukonPAObject liteYukonPaobject = MultispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNo);
            if( liteYukonPaobject != null) {
                YukonPAObject yukonPaobject = (YukonPAObject)MultispeakFuncs.getDbPersistentDao().retrieveDBPersistent(liteYukonPaobject);
                if (yukonPaobject instanceof MCTBase){
                    String origCollGroup = ((MCTBase)yukonPaobject).getDeviceMeterGroup().getCollectionGroup();
                    if( origCollGroup.indexOf(statusPrefix) > -1) {
                        ((MCTBase)yukonPaobject).getDeviceMeterGroup().setCollectionGroup(origCollGroup.replaceAll(statusPrefix, ""));
                        MultispeakFuncs.getDbPersistentDao().performDBChange(yukonPaobject, Transaction.UPDATE);
                    }
                    else {
                        ErrorObject err = MultispeakFuncs.getErrorOjbect(meterNo, "MeterNumber: " + meterNo + " - Is not in State (" + statusPrefix+").  Cannot cancel.");
                        errorObjects.add(err);
                    }
                } else {
                    ErrorObject err = MultispeakFuncs.getErrorOjbect(meterNo, "MeterNumber: " + meterNo + " - Was NOT found in Yukon.");
                    errorObjects.add(err);
                }
            }
        }
        if( !errorObjects.isEmpty())
        {
            ErrorObject[] errors = new ErrorObject[errorObjects.size()];
            errorObjects.toArray(errors);
            return errors;
        }
        return new ErrorObject[0];
    }
    
    public ErrorObject[] disableMeter(MultispeakVendor mspVendor, String[] meterNos, String statusPrefix) {
        Vector errorObjects = new Vector();
        for  (int i = 0; i < meterNos.length; i++){
            String meterNo = meterNos[i];
            LiteYukonPAObject liteYukonPaobject = MultispeakFuncs.getLiteYukonPaobject(mspVendor.getUniqueKey(), meterNo);
            YukonPAObject yukonPaobject = (YukonPAObject)MultispeakFuncs.getDbPersistentDao().retrieveDBPersistent(liteYukonPaobject);
            if( !yukonPaobject.isDisabled()) {
                yukonPaobject.setDisabled(true);
                MultispeakFuncs.getDbPersistentDao().performDBChange(yukonPaobject, Transaction.UPDATE);
            }
        }
        return new ErrorObject[0];
    }

}

