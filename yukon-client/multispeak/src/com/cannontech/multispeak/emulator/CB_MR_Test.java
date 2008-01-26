/*
 * Created on Jun 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.dao.impl.MspMeterDaoImpl;
import com.cannontech.multispeak.deploy.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.spring.YukonSpringHook;

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
			String endpointURL = "http://localhost:8080/soap/CB_MRSoap";
			endpointURL = "http://209.101.158.56/mspamrintegration/CB_MR.asmx";  //SEDC Test Server
//			endpointURL = "http://209.101.158.56:8080/mspamrintegration/CB_MR.asmx";  //SEDC Test Server and TCPTrace
//			endpointURL = "http://10.100.10.25:80/soap/CB_MRSoap";
		  	CB_MRSoap_BindingStub instance = new CB_MRSoap_BindingStub(new URL(endpointURL), new Service());
			
            YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
            msgHeader.setPwd("cannon");
            msgHeader.setUserID("cannon");
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", msgHeader);
			instance.setHeader(header);

			int todo = 0;	//0=meterByServLoc, 1=getMethods, 2=pingURL
			
			if (todo==0)
			{
			    Meter[] meters = instance.getMeterByServLoc("1223");	//1068048 whe, 1010156108 sn_head/amr_demo
			    
				if( meters!= null)
				{
				    for (Meter meter : meters) {
    				    CTILogger.info("Meter received: " + ( meter.getMeterNo() != null?meter.getMeterNo():"NULL"));
    				    CTILogger.info("Meter Error String: " + meter.getErrorString());
				    }
				}
				else
				{
				    CTILogger.info("******   NULL METER  **********");
				}
			}
			else if (todo == 1)
            {
                String[] objects = instance.getMethods();
                if (objects != null && objects != null)
                {
                    for (int i = 0; i < objects.length; i++)
                    {
                        String obj = objects[i];
                        System.out.println("Method " + i + ": " + obj);
                    }
                }
            }
			else if (todo == 2)
			{
			    ErrorObject[] objects = instance.pingURL();
				if (objects != null && objects != null)
				{
					for (int i = 0; i < objects.length; i++)
					{
						ErrorObject obj = objects[i];
						System.out.println("Ping" + i + ": " + obj.getErrorString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
