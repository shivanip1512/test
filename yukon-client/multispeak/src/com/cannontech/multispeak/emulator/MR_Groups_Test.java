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
import com.cannontech.multispeak.deploy.service.MeterGroup;
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
public class MR_Groups_Test {

	public static void main(String [] args)
	{
		ErrorObject[] objects = null;
		try {
			String endpointURL = "http://10.106.33.25:8080/soap/MR_CBSoap";
		  	MR_CBSoap_BindingStub instance = new MR_CBSoap_BindingStub(new URL(endpointURL), new Service());
			
            YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
            SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", msgHeader);
			instance.setHeader(header);

			String METER_NO_1 = "10523687";
			String METER_NO_2 = "10153216";
			String METER_NO_3 = "1010156610";
			String METER_NO_4 = "10523145";
			String METER_NO_5 = "1015321";
			String METER_NO_6 = "10531225";
			String METER_NO_7 = "1015321";
			String METER_NO_8 = "10532165";
			String METER_NO_INVALID = "4833124";

			MeterGroup meterGroup = new MeterGroup();
			String[] meterList = new String[4];
			int todo = 1;	//0=meterRead, 1=getAMRSupportedMeters, 2=pingURL, 3=getReadingsByMeterNo, 4=meterAddNotification

			if (todo == 1 ) {
				//establish meter group, no meter numbers
				meterGroup.setMeterList(meterList);
				String groupName = "/Meters/MultiSpeak/Establish3";
				meterGroup.setGroupName(groupName);

				objects = instance.establishMeterGroup(meterGroup);
			} else if (todo == 2) {
				//establish meter group, with 4 meters
				meterGroup.setMeterList(meterList);
				String groupName = "/Meters/MultiSpeak/Establish2/";
				meterGroup.setGroupName(groupName);

				meterList = new String[4];
				meterList[0] = METER_NO_1;
				meterList[1] = METER_NO_2;
				meterList[2] = METER_NO_3;
				meterList[3] = METER_NO_4;
				meterGroup.setMeterList(meterList);
				
				objects = instance.establishMeterGroup(meterGroup);
			} else if (todo == 3) {
				//insert 4 meters into existing group
				meterGroup.setMeterList(meterList);
				String groupName = "/Meters/MultiSpeak/Establish/";
				meterGroup.setGroupName(groupName);

				meterList = new String[4];
				meterList[0] = METER_NO_1;
				meterList[1] = METER_NO_2;
				meterList[2] = METER_NO_3;
				meterList[3] = METER_NO_4;
				meterGroup.setMeterList(meterList);
				objects = instance.insertMeterInMeterGroup(meterList, groupName);
			} else if (todo == 4) {
				//insert 4 meters into non-existing group
				meterGroup.setMeterList(meterList);
				String groupName = "/Meters/MultiSpeak/Insert/";
				meterGroup.setGroupName(groupName);

				meterList = new String[4];
				meterList[0] = METER_NO_1;
				meterList[1] = METER_NO_2;
				meterList[2] = METER_NO_3;
				meterList[3] = METER_NO_4;
				meterGroup.setMeterList(meterList);
				
				objects = instance.insertMeterInMeterGroup(meterList, groupName);
			} else if (todo == 5) {
				//remove 4 meters from non-existing group
				meterGroup.setMeterList(meterList);
				String groupName = "/Meters/MultiSpeak/Remove/";
				meterGroup.setGroupName(groupName);

				meterList = new String[4];
				meterList[0] = METER_NO_1;
				meterList[1] = METER_NO_2;
				meterList[2] = METER_NO_3;
				meterList[3] = METER_NO_4;
				meterGroup.setMeterList(meterList);
				
				objects = instance.removeMetersFromMeterGroup(meterList, groupName);
			} else if (todo == 6) {
				//remove 4 meters from existing group
				meterGroup.setMeterList(meterList);
				String groupName = "/Meters/MultiSpeak/Establish/";
				meterGroup.setGroupName(groupName);
	
				meterList = new String[4];
				meterList[0] = METER_NO_1;
				meterList[1] = METER_NO_2;
				meterList[2] = METER_NO_3;
				meterList[3] = METER_NO_4;
				meterGroup.setMeterList(meterList);
				
				objects = instance.removeMetersFromMeterGroup(meterList, groupName);
			} else if (todo == 7) {
				//delete group non-exists
				String groupName = "/Meters/MultiSpeak/Remove2/";
				objects = new ErrorObject[1];
				objects[0] = instance.deleteMeterGroup(groupName);
			} else if (todo == 8) {
				//delete group exists
				String groupName = "/Meters/MultiSpeak/";
				objects = new ErrorObject[1];
				objects[0] = instance.deleteMeterGroup(groupName);
			}

			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
        if (objects != null && objects != null) {
            for (int i = 0; i < objects.length; i++) {
                ErrorObject obj = objects[i];
                CTILogger.info("Error-" + i + ": " + obj.getErrorString());
            }
        } else {
            CTILogger.info("Successful");
        }

	}
}
