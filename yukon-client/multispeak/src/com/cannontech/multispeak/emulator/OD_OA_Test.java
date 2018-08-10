/*
 * Created on Jun 13, 2005 To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code
 * Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cannontech.msp.beans.v3.ArrayOfString;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequest;
import com.cannontech.msp.beans.v3.InitiateOutageDetectionEventRequestResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.ODClient;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

/**
 * @author stacey To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code
 *         Generation&gt;Code and Comments
 */
public class OD_OA_Test {
    private static String endpointURL = "http://127.0.0.1:8088/mockOD_ServerSoap";
    // private String endpointURL = "http://pspl-sw-demo62.eatoneaseng.net:8080/soap/OD_ServerSoap";
    private static ODClient port;
    private static ObjectFactory objectFactory;
    private static MultispeakVendor mspVendor = new MultispeakVendor(23213, "Cannon", "Yukon", "pwd", "sadsad", "", "", 100,
        120, 12, null, false);

    public static void main(String[] args) {
        OD_OA_Test test = new OD_OA_Test();
		ApplicationContext context = new ClassPathXmlApplicationContext("com/cannontech/multispeak/emulator/testEmulatorContext.xml");
		port = context.getBean(ODClient.class);
		objectFactory = context.getBean(ObjectFactory.class);
        try {

            String[] mn = new String[4];
            mn[0] = "1010070";
            mn[1] = "1010073";
            mn[2] = "1010074";
            mn[3] = "109129453";

            // mn[0] = "101001";
            // mn[1] = "101002";
            // mn[2] = "101003";
            // mn[3] = "101004";

            test.initiateOutageDetectionTest(mn);
           // test.getMethodsTest();
           // test.pingURLTest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMethodsTest() throws MultispeakWebServiceClientException {
        GetMethods getMethods = objectFactory.createGetMethods();
        GetMethodsResponse response = port.getMethods(mspVendor, endpointURL, getMethods);
        if (response.getGetMethodsResult() != null) {
            print_String(response.getGetMethodsResult().getString());
        }
    }

    public void pingURLTest() throws MultispeakWebServiceClientException {
        PingURL pingURL = objectFactory.createPingURL();
        PingURLResponse response = port.pingURL(mspVendor, endpointURL, pingURL);
        if (response.getPingURLResult() != null) {
            print_ErrorObjects(response.getPingURLResult().getErrorObject());
        }
    }

    public void initiateOutageDetectionTest(String[] meters) throws MultispeakWebServiceClientException {
        InitiateOutageDetectionEventRequest initiateOutageDetectionEventRequest =
                objectFactory.createInitiateOutageDetectionEventRequest();
        initiateOutageDetectionEventRequest.setExpirationTime(null);
        ArrayOfString arrayOfMeters = objectFactory.createArrayOfString();
        for (String meterNum : meters) {
            arrayOfMeters.getString().add(meterNum);
        }

        initiateOutageDetectionEventRequest.setMeterNos(arrayOfMeters);
        initiateOutageDetectionEventRequest.setRequestDate(MultispeakFuncs.toXMLGregorianCalendar(new GregorianCalendar()));
        initiateOutageDetectionEventRequest.setTransactionID("1");
        initiateOutageDetectionEventRequest.setResponseURL("http://msp1.cannontech.com:8002/OA_ODSoap");
        InitiateOutageDetectionEventRequestResponse response =
            port.initiateOutageDetectionEventRequest(mspVendor, endpointURL, initiateOutageDetectionEventRequest);

        print_ErrorObjects(response.getInitiateOutageDetectionEventRequestResult().getErrorObject());

    }

    public void print_String(List<String> list) {
        if (list != null && list != null) {
            int i = 0;
            for (String obj : list) {
                System.out.println("Method" + i++ + ": " + obj);
            }
        }
    }

    public void print_ErrorObjects(List<ErrorObject> list) {
        if (list != null && list != null) {
            int i = 0;
            for (ErrorObject obj : list) {
                System.out.println("ErrorString" + i++ + ": " + obj.getErrorString());
            }
        }
    }

}
