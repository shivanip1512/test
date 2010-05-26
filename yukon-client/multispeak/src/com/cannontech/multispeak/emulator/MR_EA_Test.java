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
import java.util.GregorianCalendar;

import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.MR_EASoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.SyntaxItem;

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
			String endpointURL = "http://localhost:8080/soap/MR_EASoap";
//			endpointURL = "http://10.100.10.25:80/soap/MR_EASoap";
		  	MR_EASoap_BindingStub instance = new MR_EASoap_BindingStub(new URL(endpointURL), new Service());
            YukonMultispeakMsgHeader mspHeader =  new YukonMultispeakMsgHeader();
            mspHeader.setCompany("SEDC");
            
			SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", mspHeader);
			instance.setHeader(header);
			String meterNumber = "1100100";
			int todo = 6;	//0=meterRead, 1=getAMRSupportedMeters, 2=pingURL, 3=getReadingsByMeterNo, 4=getLatestReadings
			
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
				Meter[] meters = new Meter[]{};
				meters = instance.getAMRSupportedMeters(meterNumber);//"10224712");//new String ("MCT - Annandale Broadcast"));
				if (meters != null && meters.length > 0)
				{
					CTILogger.info("METERS RETURNED: " + meters.length);
					for (int i = 0; i < meters.length; i++)
					{
						Meter m = meters[i];
						CTILogger.info(m.getMeterNo());
	//					String obj = strings.getString(i);
	//					System.out.println("Method" + i + ": " + obj);
					}
				}
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
				cal.set(Calendar.YEAR, 2005);
				cal.set(Calendar.DAY_OF_MONTH, 1);
				cal.set(Calendar.HOUR_OF_DAY, 1);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);

				GregorianCalendar endCal = (GregorianCalendar)cal.clone();
				endCal.add(Calendar.YEAR, 15);
				MeterRead[] amr = instance.getReadingsByMeterNo(meterNumber, cal, endCal);	//1068048 whe, 1010156108 sn_head/amr_demo
				if( amr != null)
				{
					CTILogger.info("MeterRead received: " + amr.length + " : " );
//									CTILogger.info("MeterRead Error String: " + mr.getErrorString());
				}
			}
			else if( todo == 4)
			{
				MeterRead[] amr = instance.getLatestReadings(null);	//1068048 whe, 1010156108 sn_head/amr_demo
				if( amr != null)
				{
					CTILogger.info("MeterRead received: " + amr.length + " : " );
//												CTILogger.info("MeterRead Error String: " + mr.getErrorString());
				}
			}			
            else if( todo == 5){
                FormattedBlock fb = instance.getLatestReadingByMeterNoAndType(meterNumber, "Load");
                System.out.println(fb.getSeparator());
                    for (SyntaxItem item : fb.getValSyntax()) {
                        System.out.println(item.getFieldName());
                        System.out.println(item.getPosition());
                        if (item.getUom()!=null)
                            System.out.println(item.getUom().getValue());
                    }
                    for (String item : fb.getValueList()) {
                        System.out.println(item);
                    }
            }
            
            else if( todo == 6){
                ErrorObject[] objects = instance.initiateMeterReadByMeterNoAndType(meterNumber, null, "Outage", "1");
                
                if (objects != null && objects != null)
                {
                    for (int i = 0; i < objects.length; i++)
                    {
                        ErrorObject obj = objects[i];
                        System.out.println("Ping" + i + ": " + obj.getErrorString());
                    }
                }
            }
                
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}