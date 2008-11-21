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
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.deploy.service.CD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadActionCode;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CD_CB_Test {

	public static void main(String [] args)
	{
		try {
			String endpointURL = "http://localhost:8080/soap/CD_ServerSoap";
//			endpointURL = "http://demo.cannontech.com/soap/CD_CBSoap";
//			endpointURL = "http://10.100.10.25:80/soap/CD_ServerSoap";
//			endpointURL = "http://10.106.36.79:8080/soap/CD_ServerSoap";  //Mike's computer
		  	CD_ServerSoap_BindingStub instance = new CD_ServerSoap_BindingStub(new URL(endpointURL), new Service());
			
            YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
            msgHeader.setCompany("milsoft");
            
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", msgHeader);
			instance.setHeader(header);

			int todo = 0;	//0=meterRead, 1=getAMRSupportedMeters, 2=pingURL, 3=getReadingsByMeterNo, 4=meterAddNotification
			
			if (todo==0) {
			    ConnectDisconnectEvent[] cdEvents = new ConnectDisconnectEvent[1];
			    ConnectDisconnectEvent cdEvent = new ConnectDisconnectEvent();
			    cdEvent.setObjectID("50000012");
			    cdEvent.setLoadActionCode(LoadActionCode.Connect);
			    cdEvents[0] = cdEvent;
			    ErrorObject[] objects = instance.initiateConnectDisconnect(cdEvents, null, null);
			    if (objects != null && objects != null) {
                    for (int i = 0; i < objects.length; i++) {
                        ErrorObject obj = objects[i];
                        CTILogger.info("Ping" + i + ": " + obj.getErrorString());
                    }
                } else {
                    CTILogger.info("initiate Successful");
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}