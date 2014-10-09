package com.cannontech.multispeak.emulator;

import java.rmi.RemoteException;

import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.SCADA_Server;
import com.cannontech.multispeak.deploy.service.SCADA_ServerLocator;
import com.cannontech.multispeak.deploy.service.SCADA_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.SCADA_ServerSoap_PortType;
import com.cannontech.multispeak.deploy.service.ScadaAnalog;

public class SCADA_ServerTest {

    private SCADA_ServerSoap_PortType port = null;
    private String endpointURL = "http://127.0.0.1:8080/yukon/soap/SCADA_ServerSoap";
    private SOAPHeaderElement header;

    public static void main(String[] args) {
        SCADA_ServerTest test = new SCADA_ServerTest();
        YukonMultispeakMsgHeader testHeader = new YukonMultispeakMsgHeader();
        testHeader.setCompany("Cannon");
        testHeader.setUserID("yukon");
        testHeader.setPwd("yukon");
        test.header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", testHeader);

        try {
            if (args != null && args.length > 0) {
                test.endpointURL = args[0];
            }
            SCADA_Server service = new SCADA_ServerLocator();
            ((SCADA_ServerLocator) service).setSCADA_ServerSoapEndpointAddress(test.endpointURL);

            test.port = service.getSCADA_ServerSoap();
            ((SCADA_ServerSoap_BindingStub) test.port).setHeader(test.header);

            test.getAll();

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

    public void getAll() throws RemoteException {
        ScadaAnalog[] scadaAnalogs = port.getAllSCADAAnalogs("0");

        for (ScadaAnalog scadaAnalog : scadaAnalogs) {
            System.out.println(scadaAnalog.getTimeStamp().getTime() + " " + scadaAnalog.getValue());
        }
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