/*
 * Created on Jun 13, 2005
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.msp.beans.v3.DomainMember;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.GetCustomerByMeterNo;
import com.cannontech.msp.beans.v3.GetCustomerByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetDomainMembers;
import com.cannontech.msp.beans.v3.GetDomainMembersResponse;
import com.cannontech.msp.beans.v3.GetMeterByServLoc;
import com.cannontech.msp.beans.v3.GetMeterByServLocResponse;
import com.cannontech.msp.beans.v3.GetMethods;
import com.cannontech.msp.beans.v3.GetMethodsResponse;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.PingURL;
import com.cannontech.msp.beans.v3.PingURLResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.CBClient;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

/**
 * @author stacey
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class CB_MR_Test {

    public static void main(String[] args) {
        try {
            //private String endpointURL = "http://localhost:8088/mockCB_MRSoap";
             String endpointURL = "http://localhost:8088/mockCB_ServerSoap";
            // endpointURL = "http://209.101.158.56/mspamrintegration/CB_MR.asmx"; //SEDC Test Server
           // endpointURL = "http://209.101.158.56:8080/mspamrintegration/CB_MR.asmx"; // SEDC Test Server and
                                                                                     // TCPTrace
           // endpointURL = "http://10.106.36.146:8081";
           // endpointURL = "http://127.0.0.1:8002/soap/CB_ServerSoap";
           // endpointURL = "http://moproxy.nisc.coop/cisMultispeak1/CB_MRSoap";
			ApplicationContext context = new ClassPathXmlApplicationContext("com/cannontech/multispeak/emulator/testEmulatorContext.xml");
			CBClient instance = context.getBean(CBClient.class);
			ObjectFactory objectFactory = context.getBean(ObjectFactory.class);
            MultispeakVendor mspVendor =
                new MultispeakVendor(23213, "Cannon", "Yukon", "pwd", "sadsad", "", "", 100, 120, 12, null, true);
            int todo = 4; // 0=meterByServLoc, 1=getMethods, 2=pingURL

            if (todo == 0) {
                GetMeterByServLoc getMeterByServLoc = objectFactory.createGetMeterByServLoc();
                getMeterByServLoc.setServLoc("1233");

                // inactive location 901003000
                // non existent location 1223
                GetMeterByServLocResponse response =
                    instance.getMeterByServLoc(mspVendor, endpointURL, getMeterByServLoc); // 1068048 whe,
                                                                                           // 1010156108
                                                                                           // sn_head/amr_demo

                if (response.getGetMeterByServLocResult() != null
                    && response.getGetMeterByServLocResult().getMeter() != null) {
                    for (Meter meter : response.getGetMeterByServLocResult().getMeter()) {
                        CTILogger.info("Meter received: " + (meter.getMeterNo() != null ? meter.getMeterNo() : "NULL"));
                        CTILogger.info("Meter Error String: " + meter.getErrorString());
                    }
                } else {
                    CTILogger.info("******   NULL METER  **********");
                }
            } else if (todo == 1) {
                GetMethods getMethods = objectFactory.createGetMethods();
                GetMethodsResponse response = instance.getMethods(mspVendor, endpointURL, getMethods);
                if (response.getGetMethodsResult() != null && response.getGetMethodsResult().getString() != null) {
                    int index = 0;
                    for (String obj : response.getGetMethodsResult().getString()) {
                        System.out.println("Method " + index + ": " + obj);
                    }
                }
            } else if (todo == 2) {
                PingURL pingurl = objectFactory.createPingURL();
                PingURLResponse response = instance.pingURL(mspVendor, endpointURL, pingurl);
                if (response.getPingURLResult() != null && response.getPingURLResult().getErrorObject() != null) {
                    int index = 0;
                    for (ErrorObject obj : response.getPingURLResult().getErrorObject()) {
                        System.out.println("Ping" + index + ": " + obj.getErrorString());
                    }
                }
            } else if (todo == 3) {
                GetCustomerByMeterNo getCustomerByMeterNo = objectFactory.createGetCustomerByMeterNo();
                getCustomerByMeterNo.setMeterNo("123");
                GetCustomerByMeterNoResponse response =
                    instance.getCustomerByMeterNo(mspVendor, endpointURL, getCustomerByMeterNo); // 1068048
                                                                                                 // whe,
                                                                                                 // 1010156108
                                                                                                 // sn_head/amr_demo

                if (response != null) {
                    Customer customer = response.getGetCustomerByMeterNoResult();
                    CTILogger.info("Customer received: " + customer.getFirstName() + " " + customer.getLastName());
                    CTILogger.info("Customer Error String: " + customer.getErrorString());
                } else {
                    CTILogger.info("******   NULL CUSTOMER  **********");
                }
            } else if (todo == 4) {
                GetDomainMembers getDomainMembers = objectFactory.createGetDomainMembers();
                getDomainMembers.setDomainName("substationCode");
                GetDomainMembersResponse response = instance.getDomainMembers(mspVendor, endpointURL, getDomainMembers);

                List<String> substationNames = new ArrayList<String>();
                if (response.getGetDomainMembersResult() != null
                    && response.getGetDomainMembersResult().getDomainMember() != null) {
                    for (DomainMember domainMember : response.getGetDomainMembersResult().getDomainMember()) {
                        substationNames.add(domainMember.getDescription());
                    }
                }
                System.out.println(substationNames.toString());
            }
        } catch (MultispeakWebServiceClientException e) {
            e.printStackTrace();
        }
    }
}
