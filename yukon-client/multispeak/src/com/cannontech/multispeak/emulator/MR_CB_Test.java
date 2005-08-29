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

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.ArrayOfErrorObject;
import com.cannontech.multispeak.ArrayOfMeter;
import com.cannontech.multispeak.ArrayOfString;
import com.cannontech.multispeak.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.MessageHeaderCSUnits;
import com.cannontech.multispeak.Meter;
import com.cannontech.multispeak.MultiSpeakMsgHeader;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MR_CB_Test {

	public static void main(String [] args)
	{
		try {
			String endpointURL = "http://localhost:8080/head/services/MR_CBSoap";
		  	MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(new URL(endpointURL), new Service());
			
//			ArrayOfErrorObject objects = new ArrayOfErrorObject();
            
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
			instance.setHeader(header);
			
			String x = new String("TEst");
			ArrayOfMeter meters = new ArrayOfMeter();
			meters = instance.getAMRSupportedMeters(new String ("MCT - Annandale Broadcast"));
			if (meters != null && meters.getMeter().length > 0)
			{
				CTILogger.info("METERS RETURNED: " + meters.getMeter().length);
				for (int i = 0; i < meters.getMeter().length; i++)
				{
					Meter m = meters.getMeter(i);
					CTILogger.info(m.getMeterNo());
//					String obj = strings.getString(i);
//					System.out.println("Method" + i + ": " + obj);
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
			e.printStackTrace();
		}
	}
}
