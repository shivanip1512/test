/*
 * Created on Jun 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.net.URL;

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.multispeak.ArrayOfErrorObject;
import com.cannontech.multispeak.ArrayOfString;
import com.cannontech.multispeak.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.MessageHeaderCSUnits;
import com.cannontech.multispeak.MultiSpeakMsgHeader;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CB_MR_Test {

	public static void main(String [] args)
	{
		try {
			String endpointURL = "http://localhost:8080/head/services/CB_MRSoap";
//			  String endpointURL = "http://www.turtletech.com/MultiSpeakWS/5_OA_OD.asmx";
//			  MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(new URL(endpointURL), new Service());
			  CB_MRSoap_BindingStub instance = new CB_MRSoap_BindingStub(new URL(endpointURL), new Service());
//			OA_ODSoap_BindingStub instance = new OA_ODSoap_BindingStub(new URL(endpointURL), new Service());
//			OD_OASoap_BindingStub instance = new OD_OASoap_BindingStub(new URL(endpointURL), new Service());
  
			MultiSpeakMsgHeader msHeader = new MultiSpeakMsgHeader();
			msHeader.setCSUnits(MessageHeaderCSUnits.feet);
			msHeader.setUserID(instance.getUsername());
			msHeader.setPwd(instance.getPassword());
			msHeader.setAppName("HEY");
			msHeader.setCompany("MULTISPEAK TEST");
            
			ArrayOfErrorObject objects = new ArrayOfErrorObject();
            
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", msHeader);
			instance.setHeader(header);
//			String[] mn = new String[2];
//			mn[0] = "12345";
//			mn[1] = "98765";
//			ArrayOfString meterNums = new ArrayOfString();
//			meterNums.setString(mn);
//			objects = instance.initiateOutageDetectionEventRequest(meterNums, new GregorianCalendar());
			
			ArrayOfString strings = new ArrayOfString();
			strings = instance.getMethods();
			if (strings != null && strings.getString().length > 0)
			{
				for (int i = 0; i < strings.getString().length; i++)
				{
					String obj = strings.getString(i);
					System.out.println("Method" + i + ": " + obj);
				}
			}
//			objects = instance.pingURL();
			/*if (objects != null && objects.getErrorObject().length > 0)
			{
				for (int i = 0; i < objects.getErrorObject().length; i++)
				{
					ErrorObject obj = objects.getErrorObject(i);
					System.out.println("Ping" + i + ": " + obj.getErrorString());
				}
			}*/
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
}
