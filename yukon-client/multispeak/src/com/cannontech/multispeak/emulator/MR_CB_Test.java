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
import com.cannontech.multispeak.ErrorObject;
import com.cannontech.multispeak.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.Meter;
import com.cannontech.multispeak.MeterRead;
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
			String endpointURL = "http://localhost:8080/3_1/soap/MR_CBSoap";
			endpointURL = "http://10.100.10.25:80/soap/MR_CBSoap";
		  	MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(new URL(endpointURL), new Service());
			
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
			instance.setHeader(header);

			int todo = 0;	//0=meterRead, 1=getAMRSupportedMeters, 2=pingURL
			
			if (todo==0)
			{
			    MeterRead mr = instance.getLatestReadingByMeterNo("1010156108");	//1068048 whe
				if( mr != null)
				{
				    CTILogger.info("MeterRead received: " + ( mr.getReadingDate() != null?mr.getReadingDate().getTime():null) + " : " +mr.getPosKWh());
				    CTILogger.info("MeterRead Error String: " + mr.getErrorString());
				}
				else
				{
				    CTILogger.info("******   NULL METER READING  **********");
				}
			}
			else if( todo == 1)
			{
				ArrayOfMeter meters = new ArrayOfMeter();
				meters = instance.getAMRSupportedMeters("10224712");//new String ("MCT - Annandale Broadcast"));
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
			}
			else if (todo == 2)
			{
			    ArrayOfErrorObject objects = instance.pingURL();
				if (objects != null && objects.getErrorObject() != null)
				{
					for (int i = 0; i < objects.getErrorObject().length; i++)
					{
						ErrorObject obj = objects.getErrorObject(i);
						System.out.println("Ping" + i + ": " + obj.getErrorString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
