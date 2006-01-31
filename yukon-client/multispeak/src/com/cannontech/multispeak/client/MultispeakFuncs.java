/*
 * Created on Aug 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.multispeak.client;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.message.SOAPHeaderElement;
import java.net.URL;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.DeviceFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.multispeak.ArrayOfErrorObject;
import com.cannontech.multispeak.ArrayOfString;
import com.cannontech.multispeak.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.EA_MRSoap_BindingStub;
import com.cannontech.multispeak.ErrorObject;
import com.cannontech.multispeak.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.MR_EASoap_BindingStub;
import com.cannontech.multispeak.MultiSpeakMsgHeader;
import com.cannontech.multispeak.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.OD_OASoap_BindingStub;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultispeakFuncs
{
	public static final String AMR_TYPE = "Cannon";
	
	public static void logArrayOfString(String intfaceName, String methodName, String[] strings)
	{
		if (strings != null)
		{
			for (int i = 0; i < strings.length; i++)
			{
				CTILogger.info("Return from " + intfaceName + " (" + methodName + "): " + strings[i]);
			}
		}
	}
	
	public static void logArrayOfErrorObjects(String intfaceName, String methodName, ErrorObject[] objects )
	{
		if (objects != null)
		{
			for (int i = 0; i < objects.length; i++)
			{
				CTILogger.info("Error Return from " + intfaceName + "(" + methodName + "): " + (objects[i] == null? "Null" : objects[i].getObjectID() +" - " + objects[i].getErrorString()));
			}
		}
	}
	public static void loadResponseHeader() 
	{
		try {
			//Get current message context
			MessageContext ctx = MessageContext.getCurrentContext();

			// Get SOAP envelope of response
			SOAPEnvelope env = ctx.getResponseMessage().getSOAPEnvelope();
			
			//Create SOAP header object } } } 
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());

			// Set Header
			env.addHeader(header);
		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String getKeyValue(String key, int deviceID)
	{
	    if( key.toLowerCase().startsWith("device") || key.toLowerCase().startsWith("pao"))
		{
			LiteYukonPAObject lPao = PAOFuncs.getLiteYukonPAO(deviceID);
			return (lPao == null ? null : lPao.getPaoName());
		}
		else //if(key.toLowerCase().startsWith("meternum"))	// default value
		{
			LiteDeviceMeterNumber ldmn = DeviceFuncs.getLiteDeviceMeterNumber(deviceID);
			return (ldmn == null ? null : ldmn.getMeterNumber());
		}
		
	}
	/**
	 * This method should be called by every multispeak function!!!
	 *
	 */
	public static void init()
	{
		try
		{
			CTILogger.info("MSP MESSAGE RECEIVED: " + MessageContext.getCurrentContext().getCurrentMessage().getSOAPPartAsString().toString());
		} catch (AxisFault e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MultispeakFuncs.loadResponseHeader();
	}
	/**
	 * A common declaration of the pingURL method for all services to use.
	 * @param interfaceName
	 * @return
	 */
	public static com.cannontech.multispeak.ArrayOfErrorObject pingURL(String interfaceName)
	{
		if (Multispeak.getInstance() != null)
			return new ArrayOfErrorObject(new ErrorObject[0]);
		ErrorObject err = new ErrorObject();
		err.setErrorString("Yukon Multispeak WebServices are down.");
		err.setEventTime(new GregorianCalendar());
		ErrorObject[] errorObject = new ErrorObject[]{err};
		MultispeakFuncs.logArrayOfErrorObjects(interfaceName, "pingURL", errorObject);
		return new ArrayOfErrorObject(errorObject);
	}
	
	/**
	 * A common declaration of the pingURL method for all services to use.
	 * @param interfaceName
	 * @return
	 */
	public static ArrayOfErrorObject pingURL(String url, String service, String endpoint)
	{
		ArrayOfErrorObject objects = new ArrayOfErrorObject();
		try
		{
			String endpointURL = url + endpoint;			
			MultiSpeakMsgHeader msHeader = new YukonMultispeakMsgHeader();
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", msHeader);
			if( service.equalsIgnoreCase("OD_OA"))
			{
				OD_OASoap_BindingStub instance = new OD_OASoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.pingURL();
			}
			else if( service.equalsIgnoreCase("OA_OD"))
			{
				OA_ODSoap_BindingStub instance = new OA_ODSoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.pingURL();
			}
			else if( service.equalsIgnoreCase("EA_MR"))
			{
				EA_MRSoap_BindingStub instance = new EA_MRSoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.pingURL();
			}
			else if( service.equalsIgnoreCase("MR_EA"))
			{
				MR_EASoap_BindingStub instance = new MR_EASoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.pingURL();
			}
			else if( service.equalsIgnoreCase("MR_CB"))
			{
				MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.pingURL();
			}
			else if( service.equalsIgnoreCase("CB_MR"))
			{
				CB_MRSoap_BindingStub instance = new CB_MRSoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.pingURL();
			}
			else
			{
				ErrorObject errorObj = new ErrorObject();
				errorObj.setErrorString("Unknown service: " + service + ".  Do not know what endpoint to call.");
				errorObj.setEventTime(new GregorianCalendar());
				ErrorObject[] errorObjs = new ErrorObject[]{errorObj};
				objects.setErrorObject(errorObjs);
			}
		}catch (AxisFault af)
		{
			ErrorObject errorObj = new ErrorObject();
			errorObj.setErrorString(af.getMessage());
			errorObj.setEventTime(new GregorianCalendar());
			ErrorObject[] errorObjs = new ErrorObject[]{errorObj};
			objects.setErrorObject(errorObjs);
		} 
		catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		finally{
			return objects;
		}	
	}
	/**
	 * A common declaration of the pingURL method for all services to use.
	 * @param interfaceName
	 * @return
	 */
	public static ArrayOfString getMethods(String url, String service, String endpoint)
	{
		ArrayOfString objects = new ArrayOfString();
		try
		{
			String endpointURL = url + endpoint;			
			MultiSpeakMsgHeader msHeader = new YukonMultispeakMsgHeader();
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", msHeader);
			if( service.equalsIgnoreCase("OD_OA"))
			{
				OD_OASoap_BindingStub instance = new OD_OASoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.getMethods();
			}
			else if( service.equalsIgnoreCase("OA_OD"))
			{
				OA_ODSoap_BindingStub instance = new OA_ODSoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.getMethods();
			}
			else if( service.equalsIgnoreCase("EA_MR"))
			{
				EA_MRSoap_BindingStub instance = new EA_MRSoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.getMethods();
			}
			else if( service.equalsIgnoreCase("MR_EA"))
			{
				MR_EASoap_BindingStub instance = new MR_EASoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.getMethods();
			}
			else if( service.equalsIgnoreCase("MR_CB"))
			{
				MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.getMethods();
			}
			else if( service.equalsIgnoreCase("CB_MR"))
			{
				CB_MRSoap_BindingStub instance = new CB_MRSoap_BindingStub(new URL(endpointURL), new Service());
				instance.setHeader(header);
				objects = instance.getMethods();
			}
		}catch (AxisFault af)
		{
			CTILogger.error(af.getMessage());
		} 
		catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
		finally{
			return objects;
		}	
	}	
	
	/**
	 * A common declaration of the getMethods method for all services to use.
	 * @param interfaceName
	 * @param methods
	 * @return
	 * @throws java.rmi.RemoteException
	 */
	public static com.cannontech.multispeak.ArrayOfString getMethods(String interfaceName, String[] methods) throws java.rmi.RemoteException {
		MultispeakFuncs.logArrayOfString(interfaceName, "getMethods", methods);
		return new ArrayOfString(methods);
	}
	
	public static String getCompanyNameFromSOAPHeader() throws java.rmi.RemoteException
	{
		String companyName = null;
		try
		{
			// Gets the SOAPHeader for the Request Message
			SOAPHeader soapHead = (SOAPHeader)MessageContext.getCurrentContext().getRequestMessage().getSOAPEnvelope().getHeader();
			// Gets all the SOAPHeaderElements into an Iterator
			Iterator itrElements= soapHead.getChildElements();
			while(itrElements.hasNext())
			{
				org.apache.axis.message.SOAPHeaderElement ele = (org.apache.axis.message.SOAPHeaderElement)itrElements.next();
				companyName = ele.getAttribute("Company");
				if( companyName == null )	//try a different string case
					companyName = ele.getAttribute("company");
			}
		}
		catch (SOAPException e)
		{
			e.printStackTrace();
		}
		return companyName;
	}	
}
