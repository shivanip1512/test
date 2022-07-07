package com.cannontech.multispeak.emulator;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogs;
import com.cannontech.msp.beans.v3.GetAllSCADAAnalogsResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.multispeak.client.Attributes;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.SCADAClient;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class SCADA_ServerTest {

    private static String endpointURL = "http://127.0.0.1:8088/mockSCADA_ServerSoap";
    private static SCADAClient port;
    private static ObjectFactory objectFactory;
    static List<Attributes> attributes = Arrays.asList(Attributes.KVAR_KVARH, Attributes.PEAKDEMAND_USAGE);
    private static MultispeakVendor mspVendor = new MultispeakVendor(23213, "Cannon", "Yukon", "yukon", "yukon", "", "", 100,
            120, 12, null, false, attributes);

    public static void main(String[] args) {
        SCADA_ServerTest test = new SCADA_ServerTest();
		ApplicationContext context = new ClassPathXmlApplicationContext("com/cannontech/multispeak/emulator/testEmulatorContext.xml");
		port = context.getBean(SCADAClient.class);
		objectFactory = context.getBean(ObjectFactory.class);
        try {
            if (args != null && args.length > 0) {
                endpointURL = args[0];
            }
           test.getAll();
          //test.getMethodsTest();
          //test.pingURLTest();

        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    public void getMethodsTest() throws MultispeakWebServiceClientException {
        GetMethods getMethods = objectFactory.createGetMethods();
        GetMethodsResponse response = port.getMethods(mspVendor, endpointURL, getMethods);
        if (response.getGetMethodsResult() != null && response.getGetMethodsResult().getString() != null) {
            print_String(response.getGetMethodsResult().getString());
        }
    }

    public void pingURLTest() throws MultispeakWebServiceClientException {
        PingURL pingURL = objectFactory.createPingURL();
        PingURLResponse response = port.pingURL(mspVendor, endpointURL, pingURL);
        if (response.getPingURLResult() != null && response.getPingURLResult().getErrorObject() != null)
            print_ErrorObjects(response.getPingURLResult().getErrorObject());
    }

    public void getAll() throws MultispeakWebServiceClientException {
        GetAllSCADAAnalogs getAllSCADAAnalogs = objectFactory.createGetAllSCADAAnalogs();
        getAllSCADAAnalogs.setLastReceived("0");
        GetAllSCADAAnalogsResponse response = port.getAllSCADAAnalogs(mspVendor, endpointURL, getAllSCADAAnalogs);
        if (response.getGetAllSCADAAnalogsResult() != null
            && response.getGetAllSCADAAnalogsResult().getScadaAnalog() != null) {
            for (ScadaAnalog scadaAnalog : response.getGetAllSCADAAnalogsResult().getScadaAnalog()) {
                System.out.println(scadaAnalog.getTimeStamp() + " " + scadaAnalog.getValue());
            }
        }
    }

    public void print_String(List<String> list) {
        if (list != null) {
            int i = 0;
            for (String obj : list) {
                System.out.println(i++ + ": " + obj == null ? "Null" : obj);
            }
        }
    }

    public void print_ErrorObjects(List<ErrorObject> list) {
        if (list != null) {
            int i = 0;
            for (ErrorObject obj : list) {
                System.out.println(i++ + ": " + obj == null ? "Null" : obj.getErrorString());
            }
        }
    }
   
}