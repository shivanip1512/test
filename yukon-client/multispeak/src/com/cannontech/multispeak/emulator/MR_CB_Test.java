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
import com.cannontech.multispeak.dao.impl.MspMeterDaoImpl;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.MR_CBSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterRead;
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
			endpointURL = "http://demo.cannontech.com/soap/MR_CBSoap";
//			endpointURL = "http://10.100.10.25:80/soap/MR_CBSoap";
		  	MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(new URL(endpointURL), new Service());
			
            YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
            msgHeader.setCompany("milsoft");
            
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", msgHeader);
			instance.setHeader(header);

			int todo = 2;	//0=meterRead, 1=getAMRSupportedMeters, 2=pingURL, 3=getReadingsByMeterNo
			
			if (todo==0)
			{
			    MeterRead mr = instance.getLatestReadingByMeterNo("10620108");	//1068048 whe, 1010156108 sn_head/amr_demo
				if( mr != null)
				{
				    CTILogger.info("MeterRead received: " + ( mr.getReadingDate() != null?mr.getReadingDate().getTime():null) + " : " +mr.getPosKWh());
				    CTILogger.info("MeterRead Error String: " + mr.getErrorString());
				}
				else
				{
				    CTILogger.info("******   NULL METER READING  **********");
				}
			}
			else if( todo == 1)
			{
                List<Meter>meters = ((MspMeterDaoImpl)YukonSpringHook.getBean("mspMeterDao")).getAMRSupportedMeters("0", 10000);
                System.out.println(meters.size());
/*				ArrayOfMeter meters = new ArrayOfMeter();
				meters = instance.getAMRSupportedMeters("10224712");//new String ("MCT - Annandale Broadcast"));
				if (meters != null && meters.getMeter().length > 0)
				{
					CTILogger.info("METERS RETURNED: " + meters.getMeter().length);
					for (int i = 0; i < meters.getMeter().length; i++)
					{
						Meter m = meters.getMeter(i);
						CTILogger.info(m.getMeterNo());
	//					String obj = strings.getString(i);
	//					System.out.println("Method" + i + ": " + obj);
					}
				}*/
			}
			else if (todo == 2)
			{
			    ErrorObject[] objects = instance.pingURL();
				if (objects != null && objects != null)
				{
					for (int i = 0; i < objects.length; i++)
					{
						ErrorObject obj = objects[i];
						System.out.println("Ping" + i + ": " + obj.getErrorString());
					}
				}
			}
			else if( todo == 3)
			{
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
				if( amr != null)
				{
					CTILogger.info("MeterRead received: " + amr.length + " : " );
//									CTILogger.info("MeterRead Error String: " + mr.getErrorString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
