/*
 * Created on Jun 13, 2005
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cannontech.msp.beans.v3.ArrayOfMeter;
import com.cannontech.msp.beans.v3.ArrayOfServiceLocation;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.MeterAddNotification;
import com.cannontech.msp.beans.v3.MeterAddNotificationResponse;
import com.cannontech.msp.beans.v3.Nameplate;
import com.cannontech.msp.beans.v3.Network;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotification;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotificationResponse;
import com.cannontech.msp.beans.v3.UtilityInfo;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.MRClient;

/**
 * @author stacey
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SEDC_Test {

    // private static String endpointURL = "http://127.0.0.1:8088/MR_CBSoap";
    private static String endpointURL = "http://127.0.0.1:8088/mockMR_ServerSoap";
    private static MultispeakVendor mspVendor = new MultispeakVendor(23213, "Cannon", "Yukon", "pwd", "sadsad", "", "", 100, 120,
            12, null, false, "Peak Demand , Usage");
    private static MRClient instance;
    private static ObjectFactory objectFactory;

    public static void main(String[] args) {
        try {
			ApplicationContext context = new ClassPathXmlApplicationContext("com/cannontech/multispeak/emulator/testEmulatorContext.xml");
			instance = context.getBean(MRClient.class);
			objectFactory = context.getBean(ObjectFactory.class);
            // ENTER IP ADDRESS OF WEBSERVICES HERE for localhost
            // String endpointURL = "http://msp2.cannontech.com:8080/soap/MR_CBSoap";
            // String endpointURL = "http://10.106.36.202:8080/soap/MR_CBSoap";

            Meter meter = new Meter();
            meter.setMeterNo("1084229");
            Nameplate nameplate = new Nameplate();
            nameplate.setTransponderID("8111");
            meter.setNameplate(nameplate);
            UtilityInfo utilityInfo = new UtilityInfo();
            utilityInfo.setSubstationName("Mount Hood");
            utilityInfo.setServLoc("7777777");
            meter.setUtilityInfo(utilityInfo);
            meter.setAMRDeviceType("*Default Template");

            Meter[] meters = new Meter[] { meter };

            ServiceLocation servLoc = new ServiceLocation();
            // THIS NEEDS TO BE A VALID DEVICE NAME
            servLoc.setObjectID("01021743");
            servLoc.setObjectID("Cart #1 MCT");
            servLoc.setObjectID("128138");
            servLoc.setObjectID("920005233");
            servLoc.setServAddr1("42301 LA HWY 931");
            servLoc.setRevenueClass("00");

            servLoc.setBillingCycle(String.valueOf(new Long(System.nanoTime()).intValue()));
            Network network = new Network();
            network.setDistrict("15");
            servLoc.setNetwork(network);

            int test = 2;
            if (test == 1) {
                for (int x = 0; x < 5; x++) {
                    String bcycle = new Long(System.nanoTime()).toString().substring(0, 4);
                    servLoc.setBillingCycle(bcycle);
                    ServiceLocationChangedNotification serviceLocationChangedNotification =
                            objectFactory.createServiceLocationChangedNotification();
                    ArrayOfServiceLocation arrayOfASvcLoc = objectFactory.createArrayOfServiceLocation();
                    arrayOfASvcLoc.getServiceLocation().add(servLoc);
                    serviceLocationChangedNotification.setChangedServiceLocations(arrayOfASvcLoc);
                    ServiceLocationChangedNotificationResponse response =
                        instance.serviceLocationChangedNotification(mspVendor, endpointURL,
                            serviceLocationChangedNotification);
                    List<ErrorObject> objects = null;
                    if (response.getServiceLocationChangedNotificationResult() != null
                        && response.getServiceLocationChangedNotificationResult().getErrorObject() != null) {
                        objects = response.getServiceLocationChangedNotificationResult().getErrorObject();
                        int i = 0;
                        for (ErrorObject obj : objects) {
                            System.out.println("servLocError" + i++ + ": " + obj.getErrorString());
                        }
                    }

                    Meter meterx = meters[0];
                    // meterx.setMeterNo("1084229" + x);
                    meters = new Meter[] { meterx };
                    meterx.getNameplate().setTransponderID("8111" + x);
                    meterx.setNameplate(nameplate);

                    MeterAddNotification meterAddNotification = objectFactory.createMeterAddNotification();
                    ArrayOfMeter arrayOfMeters = objectFactory.createArrayOfMeter();
                    arrayOfMeters.getMeter().add(meterx);
                    meterAddNotification.setAddedMeters(arrayOfMeters);
                    MeterAddNotificationResponse response2 =
                        instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);

                    objects = null;
                    if (response2.getMeterAddNotificationResult() != null
                        && response2.getMeterAddNotificationResult().getErrorObject() != null) {
                        objects = response2.getMeterAddNotificationResult().getErrorObject();
                        int i = 0;
                        for (ErrorObject obj : objects) {
                            System.out.println("meterAddError" + i + ": " + obj.getErrorString());
                        }
                    }
                }
                System.out.println("OUT OF FOR LOOP");
            } else if (test == 2) {
                ServiceLocationChangedNotification serviceLocationChangedNotification =
                        objectFactory.createServiceLocationChangedNotification();
                ArrayOfServiceLocation arrayOfSvcLocs = objectFactory.createArrayOfServiceLocation();
                arrayOfSvcLocs.getServiceLocation().add(servLoc);
                serviceLocationChangedNotification.setChangedServiceLocations(arrayOfSvcLocs);
                ServiceLocationChangedNotificationResponse response3 =
                    instance.serviceLocationChangedNotification(mspVendor, endpointURL,
                        serviceLocationChangedNotification);
                List<ErrorObject> objects = null;
                if (response3.getServiceLocationChangedNotificationResult() != null
                    && response3.getServiceLocationChangedNotificationResult().getErrorObject() != null) {
                    objects = response3.getServiceLocationChangedNotificationResult().getErrorObject();
                    int i = 0;
                    for (ErrorObject obj : objects) {
                        System.out.println("servLocError" + i++ + ": " + obj.getErrorString());
                    }
                } else {
                    System.out.println("DONE");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Exiting");
    }
}
