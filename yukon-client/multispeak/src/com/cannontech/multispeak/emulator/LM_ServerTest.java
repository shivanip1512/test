package com.cannontech.multispeak.emulator;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.deploy.service.ControlEventType;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LM_Server;
import com.cannontech.multispeak.deploy.service.LM_ServerLocator;
import com.cannontech.multispeak.deploy.service.LM_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.LM_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.LoadManagementEvent;
import com.cannontech.multispeak.deploy.service.ObjectRef;
import com.cannontech.multispeak.deploy.service.Strategy;
import com.cannontech.multispeak.deploy.service.SubstationLoadControlStatus;
import com.cannontech.multispeak.deploy.service.Uom;

public class LM_ServerTest {

	private LM_ServerSoap_PortType port = null;
	private String endpointURL = "http://127.0.0.1:8080/soap/LM_ServerSoap";
	private SOAPHeaderElement header;
	
	public static void main(String [] args)
	{
		LM_ServerTest test = new LM_ServerTest();
		YukonMultispeakMsgHeader testHeader = new YukonMultispeakMsgHeader();
		testHeader.setCompany("Cannon");
		testHeader.setUserID("yukon");
		testHeader.setPwd("yukon");
		test.header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", testHeader);
		
		try {
			if( args != null && args.length > 0) {
				test.endpointURL = args[0]; 
			}
			LM_Server service = new LM_ServerLocator();
			((LM_ServerLocator)service).setLM_ServerSoapEndpointAddress(test.endpointURL);

			test.port = service.getLM_ServerSoap();
			((LM_ServerSoap_BindingStub)test.port).setHeader(test.header);

//			if( args.length > 1) {
//				String method = args[1];
//				if( method.toLowerCase().startsWith("m"))
//					test.getMethodsTest();
//				else
//					test.pingURLTest();
//			} else {
//			    test.getMethodsTest();
//			}
//			test.initiateLoadManagementEvent();
			test.getAll();

						
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}
	public void getMethodsTest() throws RemoteException
	{
		String[] strings = port.getMethods();
		print_String(strings);
	}
	
	public void pingURLTest() throws RemoteException
	{
		ErrorObject[] objects = port.pingURL();
		print_ErrorObjects(objects);
	}
	
	public void initiateLoadManagementEvent() throws RemoteException
    {
	    LoadManagementEvent event = new LoadManagementEvent();
	    event.setObjectID("All devices+SUL SCU 03 MILL CREEK");
	    event.setControlEventType(ControlEventType.Initiate);
	    event.setDuration(new Float(14));
	    event.setTarget(new Float(1));
	    event.setUom(Uom.value4);
	    Strategy strategy = new Strategy();
	    strategy.setStrategyName("All devices");
	    ObjectRef[] pointList = new ObjectRef[1];
	    ObjectRef point = new ObjectRef();
	    point.setName("SUL SCU 03 MILL CREEK");
	    pointList[0] = point;
	    strategy.setApplicationPointList(pointList);
	    event.setStrategy(strategy);
	    Calendar scheduleDateTime = new GregorianCalendar();
	    scheduleDateTime.set(2010, 10, 15, 14, 46, 0);
	    event.setScheduleDateTime(scheduleDateTime);
	                                     
	    ErrorObject object = port.initiateLoadManagementEvent(event);
	    ErrorObject[] objects = new ErrorObject[1];
	    objects[0] = object;
        print_ErrorObjects(objects);
    }
	
	   public void getAll() throws RemoteException
	    {
	       SubstationLoadControlStatus[] status = port.getAllSubstationLoadControlStatuses();
	       
	       for (SubstationLoadControlStatus substationLoadControlStatus : status) {
            System.out.println(substationLoadControlStatus.getStatus());
        }
	    }
	public void print_String(String[] strings)
	{
		if (strings != null && strings != null)
		{
			for (int i = 0; i < strings.length; i++)
			{
				String obj = strings[i];
				System.out.println("Method" + i + ": " + obj);
			}
		}
	}
	
	public void print_ErrorObjects( ErrorObject[] objects )
	{
		if (objects != null && objects != null)
		{
			for (int i = 0; i < objects.length; i++)
			{
				ErrorObject obj = objects[i];
				System.out.println(i + ": " + obj == null? "Null" : obj.getErrorString());
			}
		}
	}
	
}