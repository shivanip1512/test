 /*
 * Created on Jul 11, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.client;

import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.DeviceFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.PointData;
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
import com.cannontech.multispeak.MeterRead;
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
public class Multispeak implements MessageListener, DBChangeListener {
	
	/** An instance of this */
	private static Multispeak _mspInstance = null;

	/** Singleton incrementor for messageIDs to send to porter connection */
	private static long messageID = 1;

	/** A map of Long(userMessageID) to ODEvent values */
	private static Map ODEventsMap = new HashMap();
	
	/** A map of Long(userMessageID) to MeterReadEvent value */
	private static Map meterReadEventsMap = new HashMap();

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
			DefaultDatabaseCache.getInstance().addDBChangeListener(_mspInstance);
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
				    boolean done = false;
					CTILogger.info("Received Message From ID:" + returnMsg.getDeviceID() + " - " + returnMsg.getResultString());
					ODEvent event = (ODEvent)getODEventsMap().get(new Long (returnMsg.getUserMessageID()) );
					if( event != null)
					{
					    messageReceived_ODEvent(event, returnMsg);
					    done = true;
					}

					if( !done )
					{
						MeterReadEvent mrEvent = (MeterReadEvent)getMeterReadEventsMap().get(new Long (returnMsg.getUserMessageID()) );
						if( mrEvent != null)
						{
						    messageReceived_MeterReadEvent(mrEvent, returnMsg);
						    done = true;
						}
					}
				}
			}
		}
	}
	/**
     * @param mrEvent
     * @param returnMsg
     */
    private void messageReceived_MeterReadEvent(MeterReadEvent mrEvent, Return returnMsg)
    {
        MultispeakVendor vendor = getMultispeakVendor(mrEvent.getVendorName());
		String key = (vendor != null ? vendor.getUniqueKey(): "meternumber");
		String keyValue = MultispeakFuncs.getKeyValue(key, returnMsg.getDeviceID());						
		
		MeterRead meterRead = new MeterRead();
		meterRead.setDeviceID(keyValue);
		meterRead.setMeterNo(keyValue);
		meterRead.setObjectID(keyValue);
		meterRead.setUtility(MultispeakFuncs.AMR_TYPE);

	    if( returnMsg.getStatus() != 0)
		{
	        String result = "MeterReadEvent: Reading Failed (" + keyValue + ") " + returnMsg.getResultString();
	        CTILogger.info(result);
	        meterRead.setErrorString(result);
		}
	    else
	    {
	        if(returnMsg.getVector().size() > 0 )
			{
				for (int i = 0; i < returnMsg.getVector().size(); i++)
				{
					Object o = returnMsg.getVector().elementAt(i);
					//TODO SN - Hoping at this point that only one value comes back in the point data vector 
					if (o instanceof PointData)
					{
						PointData point = (PointData) o;
						Double val = new Double(point.getValue());
						meterRead.setPosKWh(new BigInteger(String.valueOf(val.intValue())));
						GregorianCalendar cal = new GregorianCalendar();
						cal.setTimeInMillis(point.getPointDataTimeStamp().getTime());
						meterRead.setReadingDate(cal);
						CTILogger.info("MeterReadEvent:  Reading Successful (" + keyValue + ") - " );
					}
				}
			}
	    }
	    mrEvent.setMeterRead(meterRead);
	}

    /**
     * 
     */
    private void messageReceived_ODEvent(ODEvent event, Return returnMsg)
    {
		MultispeakVendor vendor = getMultispeakVendor(event.getVendorName());
		String key = (vendor != null ? vendor.getUniqueKey(): "meternumber");
		String keyValue = MultispeakFuncs.getKeyValue(key, returnMsg.getDeviceID());						
			
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
		*/
		if( returnMsg.getStatus() == 20 || returnMsg.getStatus() == 57 || returnMsg.getStatus() == 72)
		{	//Meter did not respond - outage assumed
	        CTILogger.info("OutageDetectionEvent: Ping Failed (" + keyValue + ") " + returnMsg.getResultString());
			ode.setOutageEventType(OutageEventType.Outage);
			ode.setErrorString("Ping failed: " + returnMsg.getResultString());
		}
	    else if( returnMsg.getStatus() == 31 || returnMsg.getStatus() == 32 || returnMsg.getStatus() == 33 || returnMsg.getStatus() == 65)
		{	//Unknown, but may not be an outage
	        CTILogger.info("OutageDetectionEvent: Communication Failure (" + keyValue + ") " + returnMsg.getResultString());
			ode.setOutageEventType(OutageEventType.NoResponse);
			ode.setErrorString("Communication failure: " + returnMsg.getResultString());
		}
	    else if( returnMsg.getStatus() == 1 || returnMsg.getStatus() == 17 || returnMsg.getStatus() == 74 || returnMsg.getStatus() == 0)
	    {	//Meter responsed in some way or another, 0 status was perfect
	        CTILogger.info("OutageDetectionEvent: Ping Successful (" + keyValue + ")");
	    	ode.setOutageEventType(OutageEventType.Restoration);
	    }
		else 
		{	//No idea what code this is
			CTILogger.info("Meter (" + keyValue + ") - Unknown return status from ping: " +returnMsg.getStatus());
			CTILogger.info("OutageDetectionEvent: Ping Status Unknown(" + keyValue + ")");
			ode.setOutageEventType(OutageEventType.NoResponse);
			ode.setErrorString("Unknown return status: " + returnMsg.getResultString());
		}
	    
	    event.setOutageDetectionEvent(ode);
		ODEventNotification(event);

		getODEventsMap().remove(new Long(event.getPilMessageID()));        
    }

    /**
	 * Send a ping command to pil connection for each meter in meterNumbers.
	 * @param meterNumbers
	 * @return ErrorObject [] Array of errorObjects for meters that cannot be found, etc.
	 */
	public synchronized ErrorObject[] ODEvent(String companyName, String[] meterNumbers)
	{
		Vector errorObjects = new Vector();
		MultispeakVendor vendor = getMultispeakVendor(companyName);
		
		String endpoint = (String)vendor.getServiceToEndpointMap().get(OA_ODSoap_BindingImpl.INTERFACE_NAME);
		String endpointURL = "";			
		if( endpoint != null)
			endpointURL = vendor.getUrl() + endpoint;
        try
        {
	 		OA_OD service = new OA_ODLocator();
			((OA_ODLocator)service).setOA_ODSoapEndpointAddress(endpointURL);
			OA_ODSoap_PortType port;
            port = service.getOA_ODSoap();
	        SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
			((OA_ODSoap_BindingStub)port).setHeader(header);
			((OA_ODSoap_BindingStub)port).setTimeout(10000);	//should respond within 10 seconds, right?
			//Ping the URL make sure it exists and we aren't reading meters for the fun of it..
			ArrayOfErrorObject errObjects = port.pingURL();
	
			Request pilRequest = null;
			CTILogger.info("Received " + meterNumbers.length + " Meter(s) for Outage Verification Testing from " + companyName);
			
			String key = (vendor != null ? vendor.getUniqueKey(): "meternumber");
	
			for (int i = 0; i < meterNumbers.length; i++)
			{
				LiteYukonPAObject lPao = null;
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
				}
				else
				{
					long id = generateMessageID();		
					ODEvent event = new ODEvent(vendor.getCompanyName(), id);
					getODEventsMap().put(new Long(id), event);
				    
					pilRequest = new Request(lPao.getYukonID(), "ping noqueue", id);
					pilRequest.setPriority(13);	//just below Client applications
					getPilConn().write(pilRequest);
				}
				
			}
        } catch (ServiceException e)
		{
			ErrorObject err = new ErrorObject();
			err.setEventTime(new GregorianCalendar());
			err.setErrorString("OA_OD service is not defined for company name: " + vendor.getCompanyName()+ ".  initiateOutageDetection cancelled.");
			errorObjects.add(err);
			CTILogger.info(err.getErrorString());
		} catch (AxisFault e) {
			ErrorObject err = new ErrorObject();
			err.setEventTime(new GregorianCalendar());
			err.setErrorString(e.getFaultReason() + " :  targetService: " + endpointURL + " companyName: " + vendor.getCompanyName() + ".  initiateOutageDetection cancelled.");
			errorObjects.add(err);
		    CTILogger.info(err.getErrorString());
		}catch (RemoteException e) {
			ErrorObject err = new ErrorObject();
			err.setEventTime(new GregorianCalendar());
			err.setErrorString("Could not find a target service to invoke!  targetService: " + endpointURL + " companyName: " + vendor.getCompanyName() + ".  initiateOutageDetection cancelled.");
			errorObjects.add(err);
		    CTILogger.info(err.getErrorString());
		}
    		
		if( !errorObjects.isEmpty())
		{
			ErrorObject[] errors = new ErrorObject[errorObjects.size()];
			errorObjects.toArray(errors);
			return errors;
		}
		return new ErrorObject[0];
	}
	
	public MeterRead MeterReadEvent(String companyName, String meterNumber, String command)
	{
		long id = generateMessageID();

		MeterReadEvent event = new MeterReadEvent(companyName, id);
		getMeterReadEventsMap().put(new Long(id), event);
		
		Request pilRequest = null;
		CTILogger.info("Received " + meterNumber + " for Meter Reading from " + companyName);
		MultispeakVendor vendor = getMultispeakVendor(event.getVendorName());
		
		String key = (vendor != null ? vendor.getUniqueKey(): "meternumber");

		LiteYukonPAObject lPao = null;
		if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
			lPao = DeviceFuncs.getLiteYukonPaobjectByDeviceName(meterNumber);
		else if(key.toLowerCase().startsWith("meternum"))
			lPao = DeviceFuncs.getLiteYukonPaobjectByMeterNumber(meterNumber);
		
		if (lPao != null)
		{
			pilRequest = new Request(lPao.getYukonID(), command, id);
			pilRequest.setPriority(15);
			//TODO Do we need to check if pilConn valid?
			getPilConn().write(pilRequest);

		    synchronized (event)
            {
		        long millisTimeOut = 0;	//
		        while (event.getMeterRead() == null && millisTimeOut < 120000)	//quit after 8 seconds
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

            }
		}

		return event.getMeterRead();
	}	
	
	/**
	 * Send an ODEventNotification to the OMS webservice containing odEvents 
	 * @param odEvents
	 */
	public void ODEventNotification(ODEvent odEvent)
	{
    	MultispeakVendor vendor = getMultispeakVendor(odEvent.getVendorName());
		String endpoint = (String)vendor.getServiceToEndpointMap().get(OA_ODSoap_BindingImpl.INTERFACE_NAME);
		String endpointURL = "";			
		if( endpoint != null)
			endpointURL = vendor.getUrl() + endpoint;

	    CTILogger.info("Responding to OMS ODEventNotification WebService ("+ endpointURL+ "): Meter Number " + odEvent.getOutageDetectionEvent().getObjectID() +" event."); 
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
	 * @return
	 */
	public static Map getODEventsMap() {
		return ODEventsMap;
	}
	/**
	 * @return
	 */
	public static Map getMeterReadEventsMap() {
		return meterReadEventsMap;
	}
	/**
	 * @param map
	 */
	public static void setODEventsMap(Map map) {
		ODEventsMap = map;
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

