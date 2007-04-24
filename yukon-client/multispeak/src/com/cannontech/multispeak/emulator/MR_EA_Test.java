/*
/*
 * Created on Jun 13, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.multispeak.emulator;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.service.ArrayOfErrorObject;
import com.cannontech.multispeak.service.ArrayOfMeter;
import com.cannontech.multispeak.service.ArrayOfMeterRead;
import com.cannontech.multispeak.service.ErrorObject;
import com.cannontech.multispeak.service.MR_EASoap_BindingStub;
import com.cannontech.multispeak.service.Meter;
import com.cannontech.multispeak.service.MeterRead;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MR_EA_Test {

	public static void main(String [] args)
	{
		try {
			String endpointURL = "http://localhost:8080/3_2/soap/MR_EASoap";
			endpointURL = "http://10.100.10.25:80/soap/MR_EASoap";
		  	MR_EASoap_BindingStub instance = new MR_EASoap_BindingStub(new URL(endpointURL), new Service());
			
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", new YukonMultispeakMsgHeader());
			instance.setHeader(header);
			String meterNumber = "81307861";
			int todo = 0;	//0=meterRead, 1=getAMRSupportedMeters, 2=pingURL, 3=getReadingsByMeterNo, 4=getLatestReadings
			
			if (todo==0)
			{
			    MeterRead mr = instance.getLatestReadingByMeterNo(meterNumber);	//1068048 whe, 1010156108 sn_head/amr_demo
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
				ArrayOfMeter meters = new ArrayOfMeter();
				meters = instance.getAMRSupportedMeters(meterNumber);//"10224712");//new String ("MCT - Annandale Broadcast"));
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
				}
			}
			else if (todo == 2)
			{
			    ArrayOfErrorObject objects = instance.pingURL();
				if (objects != null && objects.getErrorObject() != null)
				{
					for (int i = 0; i < objects.getErrorObject().length; i++)
					{
						ErrorObject obj = objects.getErrorObject(i);
						System.out.println("Ping" + i + ": " + obj.getErrorString());
					}
				}
			}
			else if( todo == 3)
			{
				GregorianCalendar cal = new GregorianCalendar();
				cal.set(Calendar.MONTH, Calendar.JULY);
				cal.set(Calendar.YEAR, 2005);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR_OF_DAY, 1);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);

				GregorianCalendar endCal = (GregorianCalendar)cal.clone();
				endCal.add(Calendar.MONTH, 1);
				ArrayOfMeterRead amr = instance.getReadingsByMeterNo(meterNumber, cal, endCal);	//1068048 whe, 1010156108 sn_head/amr_demo
				if( amr != null)
				{
					CTILogger.info("MeterRead received: " + amr.getMeterRead().length + " : " );
//									CTILogger.info("MeterRead Error String: " + mr.getErrorString());
				}
			}
			else if( todo == 4)
			{
				ArrayOfMeterRead amr = instance.getLatestReadings(null);	//1068048 whe, 1010156108 sn_head/amr_demo
				if( amr != null)
				{
					CTILogger.info("MeterRead received: " + amr.getMeterRead().length + " : " );
//												CTILogger.info("MeterRead Error String: " + mr.getErrorString());
				}
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}