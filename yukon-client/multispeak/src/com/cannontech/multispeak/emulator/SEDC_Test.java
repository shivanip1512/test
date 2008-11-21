/*
 * Created on Jun 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.net.URL;

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.Nameplate;
import com.cannontech.multispeak.deploy.service.Network;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.UtilityInfo;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class SEDC_Test {

    public static void main(String [] args)
    {
        try {

            //ENTER IP ADDRESS OF WEBSERVICES HERE for localhost
            String endpointURL = "http://127.0.0.1:8080/soap/MR_CBSoap";
//            String endpointURL = "http://msp2.cannontech.com:8080/soap/MR_CBSoap";
//            String endpointURL = "http://10.106.36.202:8080/soap/MR_CBSoap";
            
            /*MultispeakVendor vendor = new MultispeakVendor();
            vendor.setVendorID(1);
            vendor.setCompanyName("SEDC");
            vendor.setOutPassword("cannon");
            vendor.setOutUserName("cannon");
            vendor.setUrl("http://127.0.0.1:8080/");
            List<MultispeakInterface> interfaces =  new ArrayList<MultispeakInterface>();
            interfaces.add(new MultispeakInterface(1, MultispeakDefines.CB_MR_STR, "CB_MRSoap"));
            vendor.setMspInterfaces(interfaces);
            CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(vendor);
*/
            MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(new URL(endpointURL), new Service());
            YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
            msgHeader.setCompany("SEDC");
            msgHeader.setAppName("Test");
            
            SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", msgHeader);
            instance.setHeader(header);

            Meter meter = new Meter();
//            servLoc.setObjectID("01021743");
//            servLoc.setObjectID("Cart #1 MCT");
            meter.setMeterNo("1084229");
            Nameplate nameplate = new Nameplate();
            nameplate.setTransponderID("8111");
            meter.setNameplate(nameplate);
            UtilityInfo utilityInfo = new UtilityInfo();
            utilityInfo.setSubstationName("Mount Hood");
            utilityInfo.setServLoc("7777777");
            meter.setUtilityInfo(utilityInfo);
            meter.setAMRDeviceType("*Default Template");
 
            Meter[] meters= new Meter[]{meter};
            
            ServiceLocation servLoc = new ServiceLocation();
            //THIS NEEDS TO BE A VALID DEVICE NAME
//            servLoc.setObjectID("01021743");
//            servLoc.setObjectID("Cart #1 MCT");
//            servLoc.setObjectID("128138");
            servLoc.setObjectID("920005233");
            servLoc.setServAddr1("42301 LA HWY 931");
            servLoc.setRevenueClass("00");
            
            servLoc.setBillingCycle(String.valueOf(new Long(System.nanoTime()).intValue()));
            Network network = new Network();
            network.setDistrict("15");
            servLoc.setNetwork(network);
            
            int test = 1; 
            ServiceLocation[] servLocs = new ServiceLocation[]{servLoc};
            if (test == 1) {
                for(int x= 0; x < 5; x++) {
                    String bcycle = new Long(System.nanoTime()).toString().substring(0,4);
                    servLoc.setBillingCycle(bcycle);
                    servLocs = new ServiceLocation[]{servLoc};
                    ErrorObject[] objects = instance.serviceLocationChangedNotification(servLocs);
                    if (objects != null) {
                        for (int i = 0; i < objects.length; i++) {
                            ErrorObject obj = objects[i];
                            System.out.println("servLocError" + i + ": " + obj.getErrorString());
                        }
                    }else {
//                        System.out.println("DONE WITH " + x );
                    }
                    
                    Meter meterx = meters[0];
//                    meterx.setMeterNo("1084229" + x);
                    meters = new Meter[]{meterx};
                    meterx.getNameplate().setTransponderID("8111" + x);
                    meterx.setNameplate(nameplate);

                    objects = instance.meterAddNotification(meters);
                    if (objects != null ) {
                        for (int i = 0; i < objects.length; i++) {
                            ErrorObject obj = objects[i];
                            System.out.println("meterAddError" + i + ": " + obj.getErrorString());
                        }
                    }else {
//                        System.out.println("DONE WITH " + x );
                    }
                }
                System.out.println("OUT OF FOR LOOP");
            }
            else if (test == 2) {
                ErrorObject [] objects = instance.serviceLocationChangedNotification(servLocs);
                if (objects != null ) {
                    for (int i = 0; i < objects.length; i++) {
                        ErrorObject obj = objects[i];
                        System.out.println("servLocError" + i + ": " + obj.getErrorString());
                    }
                }else {
                    System.out.println("DONE");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Exitting");
    }
}
