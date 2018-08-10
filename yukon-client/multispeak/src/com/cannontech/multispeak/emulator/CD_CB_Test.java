package com.cannontech.multispeak.emulator;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.msp.beans.v3.ArrayOfCDDevice;
import com.cannontech.msp.beans.v3.ArrayOfConnectDisconnectEvent;
import com.cannontech.msp.beans.v3.ArrayOfErrorObject;
import com.cannontech.msp.beans.v3.CDDevice;
import com.cannontech.msp.beans.v3.CDDeviceAddNotification;
import com.cannontech.msp.beans.v3.CDDeviceAddNotificationResponse;
import com.cannontech.msp.beans.v3.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetCDMeterState;
import com.cannontech.msp.beans.v3.GetCDMeterStateResponse;
import com.cannontech.msp.beans.v3.GetCDSupportedMeters;
import com.cannontech.msp.beans.v3.GetCDSupportedMetersResponse;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnect;
import com.cannontech.msp.beans.v3.InitiateConnectDisconnectResponse;
import com.cannontech.msp.beans.v3.LoadActionCode;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.Module;
import com.cannontech.msp.beans.v3.ModuleList;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.CDClient;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

/**
 * This class is used for 'interactive testing'. 
 * Un/comment methods as needed for testing purposes.
 * This is the "poor man's MultiSpeak testing harness" 
 */
public class CD_CB_Test {
    private static String endpointURL = "http://localhost:8088/mockCD_ServerSoap";
    
    private static CDClient instance;
    private static ObjectFactory objectFactory;
    private MultispeakVendor mspVendor = new MultispeakVendor(23213, "Cannon", "Yukon", "pwd", "sadsad", "", "", 100, 120, 12,
        null, true);

    public static void main(String[] args) {
        try {
            CD_CB_Test t = new CD_CB_Test();
			ApplicationContext context = new ClassPathXmlApplicationContext("com/cannontech/multispeak/emulator/testEmulatorContext.xml");
			instance = context.getBean(CDClient.class);
			objectFactory = context.getBean(ObjectFactory.class);
            // endpointURL = "http://demo.cannontech.com/soap/CD_CBSoap";
            // endpointURL = "http://10.100.10.25:80/soap/CD_ServerSoap";
            // endpointURL = "http://10.106.36.79:8080/soap/CD_ServerSoap"; //Mike's computer

            // System.out.println(meterNumber + "- IS CD METER? " + t.isCDMeter(meterNumber));
            t.initiateConnectDisconnect();
            // t.cdAddNotification();
            // t.getCDMeterState();
            /*
             * GetCDSupportedMetersResponse response = t.getCDSupportedMeters();
             * ArrayOfMeter arrayOfMeter = response.getGetCDSupportedMetersResult();
             * printMeters(arrayOfMeter.getMeter());
             */

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private GetCDSupportedMetersResponse getCDSupportedMeters() throws MultispeakWebServiceClientException {
        GetCDSupportedMeters getCDSupportedMeters = objectFactory.createGetCDSupportedMeters();
        getCDSupportedMeters.setLastReceived("23453425");
        GetCDSupportedMetersResponse response =
            instance.getCDSupportedMeters(mspVendor, endpointURL, getCDSupportedMeters);
        return response;
    }

    private void initiateConnectDisconnect() throws MultispeakWebServiceClientException {
        ConnectDisconnectEvent cdEvent = new ConnectDisconnectEvent();
        // cdEvent.setObjectID("50000012");
        cdEvent.setObjectID("1016655");
        cdEvent.setLoadActionCode(LoadActionCode.CONNECT);
        InitiateConnectDisconnect initiateConnectDisconnect = objectFactory.createInitiateConnectDisconnect();
        ArrayOfConnectDisconnectEvent arrayOfConnectDisconnectEvent =
            objectFactory.createArrayOfConnectDisconnectEvent();
        arrayOfConnectDisconnectEvent.getConnectDisconnectEvent().add(cdEvent);
        initiateConnectDisconnect.setCdEvents(arrayOfConnectDisconnectEvent);
        InitiateConnectDisconnectResponse response =
            instance.initiateConnectDisconnect(mspVendor, endpointURL, initiateConnectDisconnect);
        if (response.getInitiateConnectDisconnectResult() != null
            && response.getInitiateConnectDisconnectResult().getErrorObject() != null) {
            int index = 0;
            for (ErrorObject obj : response.getInitiateConnectDisconnectResult().getErrorObject()) {
                CTILogger.info("Ping" + index++ + ": " + obj.getErrorString());
            }
        } else {
            CTILogger.info("initiate Successful");
        }
    }

    private void cdAddNotification() throws MultispeakWebServiceClientException {
        CDDevice cdDevice = new CDDevice();
        cdDevice.setObjectID("meterNumber");
        cdDevice.setMeterBaseID("meterNumber");
        Module discModule = new Module();
        discModule.setObjectID("disconnectCollarAddress");
        ModuleList moduleList = objectFactory.createModuleList();
        moduleList.getModule().add(discModule);
        cdDevice.setModuleList(moduleList);
        ArrayOfCDDevice arrayOfCDDevice = objectFactory.createArrayOfCDDevice();
        List<CDDevice> cdDeviceList = arrayOfCDDevice.getCDDevice();
        cdDeviceList.add(cdDevice);
        CDDeviceAddNotification cdDeviceAddNotification = objectFactory.createCDDeviceAddNotification();
        cdDeviceAddNotification.setAddedCDDs(arrayOfCDDevice);
        CDDeviceAddNotificationResponse response =
            instance.cdDeviceAddNotification(mspVendor, endpointURL, cdDeviceAddNotification);
        ArrayOfErrorObject arrOfErrorObj = response.getCDDeviceAddNotificationResult();
        List<ErrorObject> errorobjects = arrOfErrorObj.getErrorObject();
        if (errorobjects != null) {
            for (ErrorObject errorObject : errorobjects) {
                CTILogger.info("CDDeviceAddNotification" + ": " + errorObject.getErrorString());
            }
        } else {
            CTILogger.info("CDDeviceAddNotification Successful");
        }
    }

    private void getCDMeterState() throws MultispeakWebServiceClientException {
        GetCDMeterState getCDMeterState = objectFactory.createGetCDMeterState();
        getCDMeterState.setMeterNo("3243324");
        GetCDMeterStateResponse response = instance.getCDMeterState(mspVendor, endpointURL, getCDMeterState);
        LoadActionCode loadActionCode = response.getGetCDMeterStateResult();
        CTILogger.info("loadActionCode = " + loadActionCode.value());
    }

    /*
     * private boolean isCDMeter(String meterNumber) throws MultispeakWebServiceClientException {
     * return isCDSupportedMeter(meterNumber);
     * }
     */
    private static void printMeters(List<Meter> meters) {
        if (meters != null) {
            CTILogger.info("METERS RETURNED: " + meters.size());
            for (Meter meter : meters) {
                CTILogger.info(meter.getMeterNo());
            }
        }
    }
}