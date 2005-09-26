/*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.DeviceFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.multispeak.ArrayOfErrorObject;
import com.cannontech.multispeak.ArrayOfOutageDetectionEvent;
import com.cannontech.multispeak.ErrorObject;
import com.cannontech.multispeak.OA_OD;
import com.cannontech.multispeak.OA_ODLocator;
import com.cannontech.multispeak.OA_ODSoap_BindingImpl;
import com.cannontech.multispeak.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.OA_ODSoap_PortType;
import com.cannontech.multispeak.OutageDetectDeviceType;
import com.cannontech.multispeak.OutageDetectionEvent;
import com.cannontech.multispeak.OutageEventType;
import com.cannontech.multispeak.OutageLocation;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.roles.yukon.MultispeakRole;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Multispeak implements MessageListener, Observer, DBChangeListener {
	
	/** An instance of this */
	private static Multispeak _mspInstance = null;

	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to ODEvent values */
	private static Map ODEventsMap = new HashMap();

	/** A map of String<companyName> (from the MultispeakRole) to MultispeakVendor objects*/
	private static Map mspRolesMap = null;
	 
	/** A connection to dispatch*/
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;		
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
			_mspInstance.getClientConnection();
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
					ODEvent event = (ODEvent)getODEventsMap().get(new Long (returnMsg.getUserMessageID()) );
					if( event != null)
					{
						MultispeakVendor vendor = getMultispeakVendor(event.getVendorName());
						String key = (vendor != null ? vendor.getUniqueKey(): "meternumber");
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

						OutageDetectionEvent ode = new OutageDetectionEvent();
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTime(returnMsg.getTimeStamp());
						ode.setEventTime(cal);
						ode.setObjectID(keyValue);
						ode.setOutageDetectDeviceID(keyValue);
						ode.setOutageDetectDeviceType(OutageDetectDeviceType.Meter);
						OutageLocation loc = new OutageLocation();
						loc.setMeterNo(keyValue);
						ode.setOutageLocation(loc);
					    
					    if( returnMsg.getStatus() != 0)
						{
					        CTILogger.info("OutageDetectionEvent: Ping Failed (" + keyValue + ")");
							ode.setOutageEventType(OutageEventType.Outage);
							//ode.setErrorString("Ping failed");	//Not Valid (info received from Luis @ Milsoft
						}
					    else
					    {
					        CTILogger.info("OutageDetectionEvent: Ping Successful (" + keyValue + ")");
					    	ode.setOutageEventType(OutageEventType.Restoration);
					    }
					    
					    event.getODEvents().add(ode);
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
	public synchronized ErrorObject[] ODEvent(String companyName, String[] meterNumbers)
	{
		long id = generateMessageID();

		Vector errorObjects = new Vector();
		ODEvent event = new ODEvent(companyName, id, meterNumbers.length);
		event.addObserver(this);
		getODEventsMap().put(new Long(id), event);
		
		Request pilRequest = null;
		CTILogger.info("Received " + meterNumbers.length + " Meter(s) for Outage Verification Testing from OMS vendor");
		
		MultispeakVendor vendor = getMultispeakVendor(event.getVendorName());
		
		String key = (vendor != null ? vendor.getUniqueKey(): "meternumber");

		for (int i = 0; i < meterNumbers.length; i++)
		{
			LiteYukonPAObject lPao = null;
			//This error will never happen.
			/*if( key == null )
			{
				ErrorObject err = new ErrorObject();
				err.setEventTime(new GregorianCalendar());
				err.setErrorString("Unknown OMS unique key value.");
				err.setObjectID(meterNumbers[i]);
				errorObjects.add(err);
			} else */
			if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
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
				//Remove unknown meters from expected return message
				event.setTotalMeterCount(event.getTotalMeterCount()-1);
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
		return new ErrorObject[0];
	}
	
	/**
	 * Send an ODEventNotification to the OMS webservice containing odEvents 
	 * @param odEvents
	 */
	public synchronized void ODEventNotification(ODEvent odEvent)//OutageDetectionEvent [] odEvents)
	{
		try
		{
			OutageDetectionEvent [] odEvents = new OutageDetectionEvent[odEvent.getODEvents().size()];
			odEvent.getODEvents().toArray(odEvents);
			
//			String endpointURL = "http://localhost:8080/head/services/OA_ODSoap";
			MultispeakVendor vendor = getMultispeakVendor(odEvent.getVendorName());
			String endpoint = (String)vendor.getServiceToEndpointMap().get(OA_ODSoap_BindingImpl.INTERFACE_NAME);
			String endpointURL = "";			
			if( endpoint != null)
				endpointURL = vendor.getUrl() + endpoint;

		    CTILogger.info("Responding to OMS ODEventNotification WebService ("+ endpointURL+ "): " + odEvents.length + " events."); 
	
			OA_OD service = new OA_ODLocator();
			((OA_ODLocator)service).setOA_ODSoapEndpointAddress(endpointURL);
			
			OA_ODSoap_PortType port = service.getOA_ODSoap();
			
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
			((OA_ODSoap_BindingStub)port).setHeader(header);
	
			ArrayOfOutageDetectionEvent arrayODEvents = new ArrayOfOutageDetectionEvent(odEvents);
			ArrayOfErrorObject errObjects = port.ODEventNotification(arrayODEvents);
			if( errObjects != null)
			    MultispeakFuncs.logArrayOfErrorObjects(endpointURL, "ODEventNotification", errObjects.getErrorObject());
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
			ODEventNotification(odEvent);

			((ODEvent)ODEventsMap.get(new Long(odEvent.getPilMessageID()))).deleteObservers();
			ODEventsMap.remove(new Long(odEvent.getPilMessageID()));
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.cache.DBChangeListener#getClientConnection()
	 */
	public ClientConnection getClientConnection()
	{
		if( connToDispatch == null )
		{
			String host = "127.0.0.1";
			int port = 1510;
			try
			{
				host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
				port = Integer.parseInt(RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) ); 
			}
			catch( Exception e)
			{
				CTILogger.error( e.getMessage(), e );
			}

			connToDispatch = new com.cannontech.message.dispatch.ClientConnection();
			Registration reg = new Registration();
			reg.setAppName("Multispeak Webservices @" + CtiUtilities.getUserName() );
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay( 300 );  // 5 minutes should be OK

			connToDispatch.addObserver(this);
			connToDispatch.setHost(host);
			connToDispatch.setPort(port);
			connToDispatch.setAutoReconnect(true);
			connToDispatch.setRegistrationMsg(reg);
			
			try
			{
				connToDispatch.connectWithoutWait();
			}
			catch( Exception e ) 
			{
				CTILogger.error( e.getMessage(), e );
			}
		}
		return connToDispatch;
	}
	/* (non-Javadoc)
	 * @see com.cannontech.database.cache.DBChangeListener#handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg, com.cannontech.database.data.lite.LiteBase)
	 */
	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase lBase)
	{
		if (msg.getDatabase() == DBChangeMsg.CHANGE_YUKON_USER_DB)
		{
			mspRolesMap = null;
			CTILogger.debug("DBChange received: Releasing multispeak Roles Map");
			//Release the stored multispeak role/properties
		}
	}

	public Map getMultispeakRolesMap()
	{
		if( mspRolesMap == null)
		{
			mspRolesMap = new HashMap(9);
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			Map groupRolePropMap = cache.getYukonGroupRolePropertyMap();
			
			//Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String(value)>>>			
			Map groupMapToRoleMap = (Map)groupRolePropMap.get(AuthFuncs.getGroup(YukonGroupRoleDefs.GRP_YUKON));
			Map roleMapToPropMap = (Map)groupMapToRoleMap.get(AuthFuncs.getRole(MultispeakRole.ROLEID));
			Collection propValues = roleMapToPropMap.values();
			String [] x = new String[propValues.size()];
			propValues.toArray(x);
			for (int i = 0; i < x.length; i++)
			{
				String [] fields = x[i].split(",");
				
				if(fields.length >= MultispeakVendor.URL_INDEX)
				{
					MultispeakVendor vendor = new MultispeakVendor(fields[MultispeakVendor.COMPANY_NAME_INDEX].trim(),
																fields[MultispeakVendor.USERNAME_INDEX].trim(),
																fields[MultispeakVendor.PASSWORD_INDEX].trim(),
																fields[MultispeakVendor.UNIQUE_KEY_INDEX].trim(),
																fields[MultispeakVendor.URL_INDEX].trim());
																
					for(int j = MultispeakVendor.FIRST_ENDPOINT_INDEX; j < fields.length; j++)
					{
						String[] services = fields[j].trim().split("=");
						if(services.length == 2)	//0=service, 1=endpointName
							vendor.getServiceToEndpointMap().put(services[0].trim(), services[1].trim());
					}
					//Put a lower case company name so we can be consistent and find the value in a get()
					mspRolesMap.put(vendor.getCompanyName().toLowerCase(), vendor);
				}
			}  
		}
		return mspRolesMap;
	}
	/**
	 * Returns the MultispeakVendor for the companyName (uses toLower() for the company name so we can ignore the case)
	 * @param companyName
	 * @return
	 */
	public MultispeakVendor getMultispeakVendor(String companyName)
	{
		return (MultispeakVendor)getMultispeakRolesMap().get(companyName.toLowerCase());
	}
			
}

