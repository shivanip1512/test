
package com.cannontech.multispeak.emulator;

import java.net.URL;
import java.rmi.RemoteException;

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.deploy.service.CDDevice;
import com.cannontech.multispeak.deploy.service.CD_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.ConnectDisconnectEvent;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadActionCode;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.Module;

/**
 * This class is used for 'interactive testing'. 
 * Un/comment methods as needed for testing purposes.
 * This is the "poor man's MultiSpeak testing harness" 
 */
public class CD_CB_Test {
	private String endpointURL = "http://localhost:8080/soap/CD_ServerSoap";
	private CD_ServerSoap_BindingStub instance;
	
	public static void main(String [] args)
	{
		try {
			CD_CB_Test t = new CD_CB_Test();
//			endpointURL = "http://demo.cannontech.com/soap/CD_CBSoap";
//			endpointURL = "http://10.100.10.25:80/soap/CD_ServerSoap";
//			endpointURL = "http://10.106.36.79:8080/soap/CD_ServerSoap";  //Mike's computer
		  	t.instance = new CD_ServerSoap_BindingStub(new URL(t.endpointURL), new Service());
			
            YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
            //msgHeader.setCompany("Cannon");
            
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", msgHeader);
			t.instance.setHeader(header);
			
			String meterNumber = "0300031";
//			System.out.println(meterNumber + "- IS CD METER? " + t.isCDMeter(meterNumber));
//			t.initiateConnectDisconnect();
//			t.cdAddNotification();
//			t.getCDMeterState();
			Meter[] meters = t.getCDSupportedMeters();
			printMeters(meters);			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Meter[] getCDSupportedMeters() throws RemoteException {
		Meter[] meters = instance.getCDSupportedMeters("23453425");
		return meters;		
	}
	
	private void initiateConnectDisconnect() throws RemoteException {
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
	
	private void cdAddNotification() throws RemoteException {
		CDDevice cdDevice = new CDDevice();
		cdDevice.setObjectID("meterNumber");
		cdDevice.setMeterBaseID("meterNumber");
        Module discModule = new Module();
        discModule.setObjectID("disconnectCollarAddress");
        cdDevice.setModuleList(new Module[]{discModule});
        
        ErrorObject[] objects = instance.CDDeviceAddNotification(new CDDevice[]{cdDevice});
	    if (objects != null && objects != null) {
            for (int i = 0; i < objects.length; i++) {
                ErrorObject obj = objects[i];
                CTILogger.info("CDDeviceAddNotification" + i + ": " + obj.getErrorString());
            }
        } else {
            CTILogger.info("CDDeviceAddNotification Successful");
        }
	}
	
	private void getCDMeterState() throws RemoteException {
		LoadActionCode loadActionCode = instance.getCDMeterState("1100100");
		CTILogger.info("loadActionCode = " + loadActionCode.getValue());
	}

/*	private boolean isCDMeter(String meterNumber) throws RemoteException {
		return isCDSupportedMeter(meterNumber);		
	}
	*/
	private static void printMeters(Meter[] meters) {
		if (meters != null) {
			CTILogger.info("METERS RETURNED: " + meters.length);
			for (Meter meter : meters) {
				CTILogger.info(meter.getMeterNo());
			}
		}
	}
}