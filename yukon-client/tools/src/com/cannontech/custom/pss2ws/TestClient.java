/*
 * Created on Jun 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.custom.pss2ws;
import java.util.Date;

import org.apache.axis.MessageContext;


/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class TestClient {

	public static void main(String [] args)
	{
		try {
			String endpointURL = "http://www.electricprice.net/PSS2WS/PSS2WS";
			PSS2WS service = new PSS2WSLocator();
			((PSS2WSLocator)service).setPSS2WSSEIPortEndpointAddress(endpointURL);
			
			PSS2WSSEI port = service.getPSS2WSSEIPort();
//			((PSS2WSSEIBindingStub)port).setUsername("lbl");
//			((PSS2WSSEIBindingStub)port).setPassword("lbl");
			((PSS2WSSEIBindingStub)port)._setProperty(javax.xml.rpc.Stub.USERNAME_PROPERTY, "target");
			((PSS2WSSEIBindingStub)port)._setProperty(javax.xml.rpc.Stub.PASSWORD_PROPERTY, "SW1623a");
			
			PriceSchedule ps = null;
			while (true) {
				ps = port.getPriceSchedule(ps);
				ps.getCurrentPriceDPKWH();
				System.out.println((new Date()) + " - " + ps.getCurrentPriceDPKWH());
				Thread.sleep(60000);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
