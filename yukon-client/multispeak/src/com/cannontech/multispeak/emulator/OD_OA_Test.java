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

import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfString;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.OD_OA;
import com.cannontech.multispeak.service.OD_OALocator;
import com.cannontech.multispeak.service.OD_OASoap_BindingStub;
import com.cannontech.multispeak.service.OD_OASoap_PortType;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class OD_OA_Test {

	private OD_OASoap_PortType port = null;
	private String endpointURL = "http://10.100.10.25/soap/OD_OASoap";
	private SOAPHeaderElement header;
	
	public static void main(String [] args)
	{
		OD_OA_Test test = new OD_OA_Test();
		YukonMultispeakMsgHeader testHeader = new YukonMultispeakMsgHeader();
		testHeader.setCompany("milsoft");
		test.header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", testHeader);
		try {
			if( args != null && args.length > 0)
			{
				test.endpointURL = args[0]; 
			}
			String[] mn = new String[3];
			mn[0] = "86200262";
			mn[1] = "86200263";
			mn[2] = "86200264";
			if( args.length > 2)	// comma separated list of meter numbers
			{
				String m = args[2];
				mn  = m.split(",");
			}
			
			OD_OA service = new OD_OALocator();
			((OD_OALocator)service).setOD_OASoapEndpointAddress(test.endpointURL);

			test.port = service.getOD_OASoap();
			((OD_OASoap_BindingStub)test.port).setHeader(test.header);

			if( args.length > 1)
			{
				String method = args[1];
				if( method.toLowerCase().startsWith("i"))
					test.initiateOutageDetectionTest(mn);
				else if( method.toLowerCase().startsWith("m"))
					test.getMethodsTest();
				else
					test.pingURLTest();
			}
			else	//if all else fails, ping!
				test.initiateOutageDetectionTest(mn);

						
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
	
	public void initiateOutageDetectionTest(String [] meters) throws RemoteException
	{
		ArrayOfString meterNums = new ArrayOfString();
		meterNums.setString(meters);
		
		ArrayOfErrorObject objects = port.initiateOutageDetectionEventRequest(meterNums, new GregorianCalendar(), null);
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
