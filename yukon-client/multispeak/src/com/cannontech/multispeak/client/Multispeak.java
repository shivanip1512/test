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
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.DeviceFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.multispeak.ArrayOfOutageDetectionEvent;
import com.cannontech.multispeak.ErrorObject;
import com.cannontech.multispeak.OA_OD;
import com.cannontech.multispeak.OA_ODLocator;
import com.cannontech.multispeak.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.OA_ODSoap_PortType;
import com.cannontech.multispeak.OutageDetectionEvent;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Multispeak implements MessageListener, Observer {
	
	/** An instance of this */
	private static Multispeak _mspInstance = null;

	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to ODEvent values */
	private static Map ODEventsMap = new HashMap();
		
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
				CTILogger.info("A MESSAGE: " + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());
				if( returnMsg.getExpectMore() == 0)
				{
					CTILogger.info("Received Message From ID:" + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());
					ODEvent event = (ODEvent)getODEventsMap().get(new Long (returnMsg.getUserMessageID()) );
					if( returnMsg.getStatus() != 0)
					{
						if( event != null)
						{
							String key = RoleFuncs.getGlobalPropertyValue(MultispeakRole.OMS_UNIQUE_KEY);
							String keyValue = null;
							if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
							{
								LiteYukonPAObject lPao = PAOFuncs.getLiteYukonPAO(returnMsg.getDeviceID());
								keyValue = (lPao == null ? null : lPao.getPaoName());
							}
							else if(key.toLowerCase().startsWith("meternum"))
							{
								LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(returnMsg.getDeviceID());
								keyValue = (ldmn == null ? null : ldmn.getMeterNumber());
							}

							CTILogger.info("OutageDetectionEvent: Ping Failed (" + keyValue + ")");
							OutageDetectionEvent ode = new OutageDetectionEvent();
							GregorianCalendar cal = new GregorianCalendar();
							cal.setTime(returnMsg.getTimeStamp());
							ode.setEventTime(cal);
							ode.setObjectID(keyValue);
							ode.setOutageDetectDeviceID(keyValue);
							ode.setErrorString("Ping failed");
							event.getODEvents().add(ode);
						}
					}
					event.setCompletedMeterCount(event.getCompletedMeterCount() + 1);
				}
			}
		}
	}
	/**
	 * Send a ping command to pil connection for each meter in meterNumbers.
	 * @param meterNumbers
	 * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
	 */
	public synchronized ErrorObject[] ODEvent(String[] meterNumbers)
	{
		long id = generateMessageID();

		Vector errorObjects = new Vector();
		ODEvent event = new ODEvent(id, meterNumbers.length);
		event.addObserver(this);
		getODEventsMap().put(new Long(id), event);
		
		Request pilRequest = null;
		CTILogger.info("Received " + meterNumbers.length + " Meter(s) for Outage Verification Testing from OMS vendor");
		for (int i = 0; i < meterNumbers.length; i++)
		{
			String key = RoleFuncs.getGlobalPropertyValue(MultispeakRole.OMS_UNIQUE_KEY);
			LiteYukonPAObject lPao = null;
			if( key == null )
			{
				ErrorObject err = new ErrorObject();
				err.setEventTime(new GregorianCalendar());
				err.setErrorString("Unknown OMS unique key value.");
				err.setObjectID(meterNumbers[i]);
				errorObjects.add(err);
			}
			else if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
				lPao = DeviceFuncs.getLiteYukonPaobjectByDeviceName(meterNumbers[i]);
			else if(key.toLowerCase().startsWith("meternum"))
				lPao = DeviceFuncs.getLiteYukonPaobjectByMeterNumber(meterNumbers[i]);
			
			
			if (lPao == null)
			{
				ErrorObject err = new ErrorObject();
				err.setEventTime(new GregorianCalendar());
				err.setErrorString("MeterNumber: " + meterNumbers[i] + " - Was NOT found in Yukon.");
				err.setObjectID(meterNumbers[i]);
				errorObjects.add(err);
			}
			else
			{
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
		System.out.println("HERE");
		return new ErrorObject[0];
	}
	
	/**
	 * Send an ODEventNotification to the OMS webservice containing odEvents 
	 * @param odEvents
	 */
	public static synchronized void ODEventNotification(OutageDetectionEvent [] odEvents)
	{
		try
		{
			CTILogger.info("Responding to OMS ODEventNotification WebService with " + odEvents.length + " unsuccesfully ping'd meters."); 
//			String endpointURL = "http://localhost:8080/head/services/OA_ODSoap";
			String endpointURL = RoleFuncs.getGlobalPropertyValue(MultispeakRole.OMS_WEBSERVICE_URL);
	
			OA_OD service = new OA_ODLocator();
			((OA_ODLocator)service).setOA_ODSoapEndpointAddress(endpointURL);
			
			OA_ODSoap_PortType port = service.getOA_ODSoap();
			
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
			((OA_ODSoap_BindingStub)port).setHeader(header);
	
			ArrayOfOutageDetectionEvent arrayODEvents = new ArrayOfOutageDetectionEvent(odEvents);
			port.ODEventNotification(arrayODEvents);	
		}
		catch (ServiceException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	/**
	 * @return
	 */
	public static Map getODEventsMap() {
		return ODEventsMap;
	}

	/**
	 * @param map
	 */
	public static void setODEventsMap(Map map) {
		ODEventsMap = map;
	}


	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	/** 
	 * Handle a notify message from any ODEvent being observed.
	 * For ODEvent object, ODEventNotification is called.
	 * Removes the ODEvent observer and removes ODEvent from the ODEventsMap
	 */
	public void update(Observable o, Object arg)
	{
		if( o instanceof ODEvent)
		{
			ODEvent odEvent = (ODEvent)o;

			OutageDetectionEvent [] odEvents = new OutageDetectionEvent[odEvent.getODEvents().size()];
			odEvent.getODEvents().toArray(odEvents);
			
			ODEventNotification(odEvents);

			((ODEvent)ODEventsMap.get(new Long(odEvent.getPilMessageID()))).deleteObservers();
			ODEventsMap.remove(new Long(odEvent.getPilMessageID()));
		}
	}

}

