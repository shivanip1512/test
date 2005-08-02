/*
 * Created on Jun 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.multispeak.ArrayOfErrorObject;
import com.cannontech.multispeak.ArrayOfString;
import com.cannontech.multispeak.ErrorObject;
import com.cannontech.multispeak.OD_OA;
import com.cannontech.multispeak.OD_OALocator;
import com.cannontech.multispeak.OD_OASoap_BindingStub;
import com.cannontech.multispeak.OD_OASoap_PortType;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OD_OA_Test {

	private OD_OASoap_PortType port = null;
	private String endpointURL = "http://localhost:8080/head/services/OD_OASoap";
	private SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
	
	public static void main(String [] args)
	{
		OD_OA_Test test = new OD_OA_Test();

		try {
//			test.endpointURL = "http://localhost:8080/head/services/OD_OASoap";
//			test.endpointURL = "http://localhost:90/head/services/OD_OASoap";
//			test.endpointURL = "http://65.201.119.107:80/soap/OA_ODSoap";
			
			OD_OA service = new OD_OALocator();
			((OD_OALocator)service).setOD_OASoapEndpointAddress(test.endpointURL);

			test.port = service.getOD_OASoap();
			((OD_OASoap_BindingStub)test.port).setHeader(test.header);

//			test.getMethodsTest();
//			test.pingURLTest();
			test.initiateOutageDetectionTest();			
						
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	public void getMethodsTest() throws RemoteException
	{
		ArrayOfString strings = port.getMethods();
		print_ArrayOfString(strings);
	}
	
	public void pingURLTest() throws RemoteException
	{
		ArrayOfErrorObject objects = port.pingURL();
		print_ArrayOfErrorObjects(objects);
	}
	
	public void initiateOutageDetectionTest() throws RemoteException
	{
		String[] mn = new String[3];
		mn[0] = "101015611";
		mn[1] = "101015610";
		mn[2] = "1010156107";
		ArrayOfString meterNums = new ArrayOfString();
		meterNums.setString(mn);
		
		ArrayOfErrorObject objects = port.initiateOutageDetectionEventRequest(meterNums, new GregorianCalendar());
		print_ArrayOfErrorObjects(objects);

	}
	
	public void print_ArrayOfString(ArrayOfString strings)
	{
		if (strings != null && strings.getString() != null)
		{
			for (int i = 0; i < strings.getString().length; i++)
			{
				String obj = strings.getString(i);
				System.out.println("Method" + i + ": " + obj);
			}
		}
	}
	
	public void print_ArrayOfErrorObjects( ArrayOfErrorObject objects )
	{
		if (objects != null && objects.getErrorObject() != null)
		{
			for (int i = 0; i < objects.getErrorObject().length; i++)
			{
				ErrorObject obj = objects.getErrorObject(i);
				System.out.println(i + ": " + obj == null? "Null" : obj.getErrorString());
			}
		}
	}
	
}
