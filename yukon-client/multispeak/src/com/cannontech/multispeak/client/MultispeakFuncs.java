/*
 * Created on Aug 17, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.multispeak.client;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.functions.RoleFuncs;
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
				CTILogger.info("Error Return from " + intfaceName + "(" + methodName + "): " + (objects[i] == null? "Null" : objects[i].getErrorString()));
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
	
	public static String getUniqueKey()
	{
		String key = RoleFuncs.getGlobalPropertyValue(MultispeakRole.OMS_UNIQUE_KEY);
		if( key == null )
		{
			CTILogger.info("Unkown OMS unique key value.");
			key = "meterNumber";
		}
		return key;
	}
}
