/*
 * Created on Aug 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.multispeak.client;

import java.util.GregorianCalendar;
import java.util.Iterator;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeader;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.multispeak.ArrayOfErrorObject;
import com.cannontech.multispeak.ArrayOfString;
import com.cannontech.multispeak.ErrorObject;
import com.cannontech.roles.yukon.MultispeakRole;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MultispeakFuncs
{
	public static final String AMR_TYPE = "cannon";
	
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
	
//	public static String getUniqueKey()
//	{
//		String key = RoleFuncs.getGlobalPropertyValue(MultispeakRole.OMS_UNIQUE_KEY);
//		if( key == null )
//		{
//			CTILogger.info("Unkown OMS unique key value.");
//			key = "meterNumber";
//		}
//		return key;
//	}
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
