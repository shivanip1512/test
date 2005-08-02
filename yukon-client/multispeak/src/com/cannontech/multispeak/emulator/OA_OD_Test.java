/*
 * Created on Jun 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.multispeak.ArrayOfErrorObject;
import com.cannontech.multispeak.ArrayOfString;
import com.cannontech.multispeak.OA_OD;
import com.cannontech.multispeak.OA_ODLocator;
import com.cannontech.multispeak.OA_ODSoap_BindingStub;
import com.cannontech.multispeak.OA_ODSoap_PortType;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OA_OD_Test {

	public static void main(String [] args)
	{
		try {

			String endpointURL = "http://65.201.119.107:80/soap/OA_ODSoap";
			
			if( args != null && args.length > 0)
			{
				endpointURL = args[0]; 
			}
//			String endpointURL = "http://localhost:8080/head/services/OD_OASoap";

//			OA_OD service = new OA_ODLocator();
//			((OA_ODLocator)service).setOA_ODSoapEndpointAddress(endpointURL);
//			OA_ODSoap_PortType port = service.getOA_ODSoap();

			OA_OD service = new OA_ODLocator();
			((OA_ODLocator)service).setOA_ODSoapEndpointAddress(endpointURL);
			OA_ODSoap_PortType port = service.getOA_ODSoap();
          
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
			((OA_ODSoap_BindingStub)port).setHeader(header);

			ArrayOfErrorObject objects = new ArrayOfErrorObject();
//			objects = port.getminitiateOutageDetectionEventRequest(meterNums, new GregorianCalendar());
			
//			OutageDetectionEvent odEvent = new OutageDetectionEvent();
//			ArrayOfOutageDetectionEvent events = new ArrayOfOutageDetectionEvent(new OutageDetectionEvent[]{odEvent});
//			System.out.println("1 " + events.getOutageDetectionEvent(0).getUtility());
//			port.ODEventNotification(events);
//			System.out.println("2 " + events.getOutageDetectionEvent(0).getUtility());
			
			ArrayOfString strings = new ArrayOfString();
			
			strings = port.getMethods();
			if (strings != null && strings.getString().length > 0)
			{
				for (int i = 0; i < strings.getString().length; i++)
				{
					String obj = strings.getString(i);
					System.out.println("Method" + i + ": " + obj);
				}
			}
//			objects = 
//port.pingURL();
			/* (objects != null && objects.getErrorObject().length > 0)
			{
				for (int i = 0; i < objects.getErrorObject().length; i++)
				{
					ErrorObject obj = objects.getErrorObject(i);
					System.out.println(i + ": " + obj.getErrorString());
				}
			}*/
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
