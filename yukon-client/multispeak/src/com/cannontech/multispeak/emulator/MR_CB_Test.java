/*
 * Created on Jun 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.deploy.service.EaLoc;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.Nameplate;
import com.cannontech.multispeak.deploy.service.UtilityInfo;
import com.cannontech.spring.YukonSpringHook;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MR_CB_Test {

	public static void main(String [] args)
	{
		try {
			String endpointURL = "http://localhost:8080/soap/MR_CBSoap";
//			endpointURL = "http://demo.cannontech.com/soap/MR_CBSoap";
//			endpointURL = "http://10.100.10.25:80/soap/MR_CBSoap";
			endpointURL = "http://10.106.36.79:8080/soap/MR_CBSoap";  //Mike's computer
		  	MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(new URL(endpointURL), new Service());
			
            YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
            msgHeader.setCompany("milsoft");
            
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", msgHeader);
			instance.setHeader(header);

			int todo = 4;	//0=meterRead, 1=getAMRSupportedMeters, 2=pingURL, 3=getReadingsByMeterNo, 4=meterAddNotification
			
			if (todo==0) {
			    MeterRead mr = instance.getLatestReadingByMeterNo("10620108");	//1068048 whe, 1010156108 sn_head/amr_demo
				if( mr != null) {
				    CTILogger.info("MeterRead received: " + ( mr.getReadingDate() != null?mr.getReadingDate().getTime():null) + " : " +mr.getPosKWh());
				    CTILogger.info("MeterRead Error String: " + mr.getErrorString());
				} else {
				    CTILogger.info("******   NULL METER READING  **********");
				}
			}
			else if( todo == 1) {
                List<Meter>meters = (YukonSpringHook.getBean("mspMeterDao", MspMeterDao.class)).getAMRSupportedMeters("0", 10000);
				if (meters != null && meters.size() > 0) {
				    
					CTILogger.info("METERS RETURNED: " + meters.size());
					for (Meter meter : meters) {
						CTILogger.info(meter.getMeterNo());
					}
				}
			}
			else if (todo == 2) {
			    ErrorObject[] objects = instance.pingURL();
				if (objects != null && objects != null) {
					for (int i = 0; i < objects.length; i++) {
						ErrorObject obj = objects[i];
						CTILogger.info("Ping" + i + ": " + obj.getErrorString());
					}
				} else {
				    CTILogger.info("PingURL Successful");
	            }
			}
			else if( todo == 3) {
				GregorianCalendar cal = new GregorianCalendar();
				cal.set(Calendar.MONTH, Calendar.JULY);
				cal.set(Calendar.YEAR, 2006);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR_OF_DAY, 1);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);

				GregorianCalendar endCal = (GregorianCalendar)cal.clone();
				endCal.add(Calendar.MONTH, 2);
				MeterRead[] amr = instance.getReadingsByMeterNo("01071861", cal, endCal);	//1068048 whe, 1010156108 sn_head/amr_demo
				if( amr != null) {
					CTILogger.info("MeterRead received: " + amr.length + " : " );
				}
			} else if( todo == 4) {
			    Meter meter = new Meter();
			    meter.setAMRDeviceType("Cart #1 MCT-410cL (0300031)"); // this is the "Template" name
			    meter.setMeterNo("Cart #1 MCT-410cL (0320819)"); //MeterNumber, and value used for PaoName when PaoNameAlias is DeviceName (Default)
			    UtilityInfo utilityInfo = new UtilityInfo();
			    utilityInfo.setAccountNumber("1234567"); //Value used for PaoName when PaoNameAlias = AccountNumber
			    utilityInfo.setCustID("1234567"); //Value used for PaoName when PaoNameAlias = CustomerNumber
			    EaLoc eaLoc = new EaLoc();
			    eaLoc.setName("1234567");    //Value used for PaoName when PaoNameAlias = EALocation
			    utilityInfo.setEaLoc(eaLoc);
			    utilityInfo.setSubstationName("SUBSTATION 1");   //SubstationName that maps to Route in SubstationToRouteMapping
			    meter.setUtilityInfo(utilityInfo);
			    Nameplate nameplate = new Nameplate();
			    nameplate.setTransponderID("320819");   //This is the Physical Address
			    meter.setNameplate(nameplate);
			    
			    ErrorObject[] objects = instance.meterAddNotification(new Meter[]{meter});
                if (objects != null && objects != null) {
                    for (int i = 0; i < objects.length; i++) {
                        ErrorObject obj = objects[i];
                        CTILogger.info("Add Meter Error-" + i + ": " + obj.getErrorString());
                    }
                } else {
                    CTILogger.info("MeterAddNotification Successful");
                }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}