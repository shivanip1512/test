package com.cannontech.multispeak.emulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cannontech.msp.beans.v3.ApplicationPointList;
import com.cannontech.msp.beans.v3.ControlEventType;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetAllSubstationLoadControlStatuses;
import com.cannontech.msp.beans.v3.GetAllSubstationLoadControlStatusesResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEvent;
import com.cannontech.msp.beans.v3.InitiateLoadManagementEventResponse;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.ObjectRef;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.msp.beans.v3.Strategy;
import com.cannontech.msp.beans.v3.SubstationLoadControlStatus;
import com.cannontech.msp.beans.v3.Uom;
import com.cannontech.multispeak.client.MspAttribute;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.LMClient;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

public class LM_ServerTest {

    private String endpointURL = "http://127.0.0.1:8088/mockLM_ServerSoap";
    private static LMClient port;
    private static ObjectFactory objectFactory;
    List<MspAttribute> attributes = Arrays.asList(MspAttribute.KVAR_KVARH, MspAttribute.PEAKDEMAND_USAGE);
    private MultispeakVendor mspVendor = new MultispeakVendor(23213, "Cannon", "Yukon", "pwd", "sadsad", "", "", 100, 120, 12,
        null, false, attributes);

    public static void main(String[] args) {
        LM_ServerTest test = new LM_ServerTest();
		ApplicationContext context = new ClassPathXmlApplicationContext("com/cannontech/multispeak/emulator/testEmulatorContext.xml");
		port = context.getBean(LMClient.class);
		objectFactory = context.getBean(ObjectFactory.class);
        try {
            if (args != null && args.length > 0) {
                test.endpointURL = args[0];
            }
            if (args.length > 1) {
                String method = args[1];
                if (method.toLowerCase().startsWith("m"))
                    test.getMethodsTest();
                else
                    test.pingURLTest();
            } else {
                test.getMethodsTest();
            }
            // test.initiateLoadManagementEvent();
            test.getAll();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getMethodsTest() throws MultispeakWebServiceClientException {
        GetMethods getMethods = objectFactory.createGetMethods();
        GetMethodsResponse strings = port.getMethods(mspVendor, endpointURL, getMethods);
        print_String(strings.getGetMethodsResult().getString());
    }

    public void pingURLTest() throws MultispeakWebServiceClientException {
        PingURL pingURL = objectFactory.createPingURL();
        PingURLResponse pingURLResponse = port.pingURL(mspVendor, endpointURL, pingURL);
        print_ErrorObjects(pingURLResponse.getPingURLResult().getErrorObject());
    }

    
    public void initiateLoadManagementEvent() throws MultispeakWebServiceClientException {
        LoadManagementEvent event = new LoadManagementEvent();
        event.setObjectID("All devices+SUL SCU 03 MILL CREEK");
        event.setControlEventType(ControlEventType.INITIATE);
        event.setDuration(new Float(14));
        event.setTarget(new Float(1));
        event.setUom(Uom.VALUE_4);
        Strategy strategy = new Strategy();
        strategy.setStrategyName("All devices");
        ObjectRef point = new ObjectRef();
        point.setName("SUL SCU 03 MILL CREEK");
        ApplicationPointList applicationPointList = objectFactory.createApplicationPointList();
        applicationPointList.getApplicationPoint().add(point);
        strategy.setApplicationPointList(applicationPointList);
        event.setStrategy(strategy);
        Calendar scheduleDateTime = new GregorianCalendar();
        scheduleDateTime.set(2010, 10, 15, 14, 46, 0);
        event.setScheduleDateTime(MultispeakFuncs.toXMLGregorianCalendar(scheduleDateTime));
        InitiateLoadManagementEvent initiateLoadManagementEvent = objectFactory.createInitiateLoadManagementEvent();
        initiateLoadManagementEvent.setTheLMEvent(event);
        InitiateLoadManagementEventResponse object =
            port.initiateLoadManagementEvent(mspVendor, endpointURL, initiateLoadManagementEvent);
        ErrorObject errorObject = object.getInitiateLoadManagementEventResult();
        List<ErrorObject> objects = new ArrayList<ErrorObject>();
        objects.add(errorObject);
        print_ErrorObjects(objects);
    }

    public void getAll() throws MultispeakWebServiceClientException {
        GetAllSubstationLoadControlStatuses getAllSubstationLoadControlStatuses =
                objectFactory.createGetAllSubstationLoadControlStatuses();
        GetAllSubstationLoadControlStatusesResponse status =
            port.getAllSubstationLoadControlStatuses(mspVendor, endpointURL, getAllSubstationLoadControlStatuses);

        if (status.getGetAllSubstationLoadControlStatusesResult() != null) {
            for (SubstationLoadControlStatus substationLoadControlStatus : status.getGetAllSubstationLoadControlStatusesResult().getSubstationLoadControlStatus()) {
                System.out.println(substationLoadControlStatus.getStatus());
            }
        }
    }

    public void print_String(List<String> strings) {
        if (strings != null) {
            int index = 0;
            for (String obj : strings) {
                System.out.println("Method" + index++ + ": " + obj);
            }
        }
    }

    public void print_ErrorObjects(List<ErrorObject> list) {
        if (list != null) {
            int index = 0;
            for (ErrorObject obj : list) {
                System.out.println(index++ + ": " + obj == null ? "Null" : obj.getErrorString());
            }
        }
    }

}