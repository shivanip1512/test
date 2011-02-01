package com.cannontech.multispeak.emulator;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.deploy.service.CB_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.Meter;

/**
 * This class is used for 'interactive testing'. 
 * Un/comment methods as needed for testing purposes.
 * This is the "poor man's MultiSpeak testing harness" 
 */
public class CB_CD_Test {

	public static void main(String [] args)
	{
		try {
			String endpointURL = "http://localhost:8002/soap/CB_ServerSoap";
//			endpointURL = "http://209.101.158.56/mspamrintegration/CB_Server.asmx";  //SEDC Test Server
//			endpointURL = "http://209.101.158.56:8080/mspamrintegration/CB_Server.asmx";  //SEDC Test Server and TCPTrace
//		  	endpointURL = "http://10.106.36.146:8081";
			CB_ServerSoap_BindingStub instance = new CB_ServerSoap_BindingStub(new URL(endpointURL), new Service());
			
            YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
            msgHeader.setPwd("cannon");
            msgHeader.setUserID("cannon");
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", msgHeader);
			instance.setHeader(header);

			int todo = 2;	//0=meterByServLoc, 1=getMethods, 2=pingURL
			
			if (todo==0)
			{
			    //inactive location 901003000
			    //non existent location 1223
			    Meter[] meters = instance.getMeterByServLoc("1233");	//1068048 whe, 1010156108 sn_head/amr_demo
			    
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
			else if( todo == 3)
			{
                Customer customer = instance.getCustomerByMeterNo("123");    //1068048 whe, 1010156108 sn_head/amr_demo
                
                if( customer != null)
                {
                        CTILogger.info("Customer received: " +  customer.getFirstName() + " " + customer.getLastName());
                        CTILogger.info("Customer Error String: " + customer.getErrorString());
                }
                else
                {
                    CTILogger.info("******   NULL CUSTOMER  **********");
                }
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
		    e.printStackTrace();
		}
	}
}
