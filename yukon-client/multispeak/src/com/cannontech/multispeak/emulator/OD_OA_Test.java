/*
 * Created on Jun 13, 2005 To change the template for this generated file go to Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.rmi.RemoteException;
import java.util.GregorianCalendar;

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.OD_OA;
import com.cannontech.multispeak.deploy.service.OD_OALocator;
import com.cannontech.multispeak.deploy.service.OD_OASoap_BindingStub;
import com.cannontech.multispeak.deploy.service.OD_OASoap_PortType;

/**
 * @author stacey To change the template for this generated type comment go to Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class OD_OA_Test {

    private OD_OASoap_PortType port = null;
    private String endpointURL = "http://127.0.0.1:8080/yukon/soap/OD_ServerSoap";
//    private String endpointURL = "http://pspl-sw-demo62.eatoneaseng.net:8080/soap/OD_ServerSoap";
    private SOAPHeaderElement header;

    public static void main(String[] args) {
        OD_OA_Test test = new OD_OA_Test();
        YukonMultispeakMsgHeader testHeader = new YukonMultispeakMsgHeader();
        testHeader.setCompany("Cannon MSP1");
//        testHeader.setCompany("MSP1");
        test.header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", testHeader);
        try {

            String[] mn = new String[4];
            mn[0] = "1010070";
            mn[1] = "1010073";
            mn[2] = "1010074";
            mn[3] = "109129453";
            
//            mn[0] = "101001";
//            mn[1] = "101002";
//            mn[2] = "101003";
//            mn[3] = "101004";

            OD_OA service = new OD_OALocator();
            ((OD_OALocator) service).setOD_OASoapEndpointAddress(test.endpointURL);

            test.port = service.getOD_OASoap();
            ((OD_OASoap_BindingStub) test.port).setHeader(test.header);

            test.initiateOutageDetectionTest(mn);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public void getMethodsTest() throws RemoteException {
        String[] strings = port.getMethods();
        print_String(strings);
    }

    public void pingURLTest() throws RemoteException {
        ErrorObject[] objects = port.pingURL();
        print_ErrorObjects(objects);
    }

    public void initiateOutageDetectionTest(String[] meters) throws RemoteException {
        ErrorObject[] objects = port.initiateOutageDetectionEventRequest(meters,
                                                                         new GregorianCalendar(),
                                                                         "http://msp1.cannontech.com:8002/OA_ODSoap",
                                                                         "1");
        print_ErrorObjects(objects);

    }

    public void print_String(String[] strings) {
        if (strings != null && strings != null) {
            for (int i = 0; i < strings.length; i++) {
                String obj = strings[i];
                System.out.println("Method" + i + ": " + obj);
            }
        }
    }

    public void print_ErrorObjects(ErrorObject[] objects) {
        if (objects != null && objects != null) {
            for (int i = 0; i < objects.length; i++) {
                ErrorObject obj = objects[i];
                System.out.println(i + ": " + obj == null ? "Null" : obj.getErrorString());
            }
        }
    }

}
