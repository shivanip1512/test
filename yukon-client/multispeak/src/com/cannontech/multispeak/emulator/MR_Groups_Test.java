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
import com.cannontech.msp.beans.v3.DeleteMeterGroup;
import com.cannontech.msp.beans.v3.DeleteMeterGroupResponse;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.EstablishMeterGroup;
import com.cannontech.msp.beans.v3.EstablishMeterGroupResponse;
import com.cannontech.msp.beans.v3.InsertMeterInMeterGroup;
import com.cannontech.msp.beans.v3.InsertMeterInMeterGroupResponse;
import com.cannontech.msp.beans.v3.MRArrayOfString2;
import com.cannontech.msp.beans.v3.MeterGroup;
import com.cannontech.msp.beans.v3.MeterList;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.RemoveMetersFromMeterGroup;
import com.cannontech.msp.beans.v3.RemoveMetersFromMeterGroupResponse;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.MRClient;

/**
 * @author stacey
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MR_Groups_Test {
    //static String endpointURL = "http://10.106.33.25:8080/soap/mockMR_CBSoap";
    private static String endpointURL = "http://localhost:8088/mockMR_ServerSoap";

    private static MRClient instance;
    private static ObjectFactory objectFactory;

    private static MultispeakVendor mspVendor = new MultispeakVendor(23213, "Cannon", "Yukon", "pwd", "sadsad", "", "", 100,
        120, 12, null, false);

    public static void main(String[] args) {
        List<ErrorObject> objects = new ArrayList<ErrorObject>();
		ApplicationContext context = new ClassPathXmlApplicationContext("com/cannontech/multispeak/emulator/testEmulatorContext.xml");
		instance = context.getBean(MRClient.class);
		objectFactory = context.getBean(ObjectFactory.class);
        try {
            String METER_NO_1 = "10523687";
            String METER_NO_2 = "10153216";
            String METER_NO_3 = "1010156610";
            String METER_NO_4 = "10523145";
            // String METER_NO_5 = "1015321";
            // String METER_NO_6 = "10531225";
            // String METER_NO_7 = "1015321";
            // String METER_NO_8 = "10532165";
            // String METER_NO_INVALID = "4833124";

            MeterGroup meterGroup = new MeterGroup();
            MeterList meterList = null;
            int todo = 1; // 0=meterRead, 1=getAMRSupportedMeters, 2=pingURL, 3=getReadingsByMeterNo,
                          // 4=meterAddNotification

            if (todo == 1) {
                // establish meter group, no meter numbers
                meterList = objectFactory.createMeterList();
                meterGroup.setMeterList(meterList);
                String groupName = "/Meters/MultiSpeak/Establish3";
                meterGroup.setGroupName(groupName);

                EstablishMeterGroup establishMeterGroup = objectFactory.createEstablishMeterGroup();
                establishMeterGroup.setMeterGroup(meterGroup);
                EstablishMeterGroupResponse response  =instance.establishMeterGroup(mspVendor, endpointURL, establishMeterGroup);
                objects = response.getEstablishMeterGroupResult().getErrorObject();
            } else if (todo == 2) {
                // establish meter group, with 4 meters
                meterList = objectFactory.createMeterList();
                meterGroup.setMeterList(meterList);
                String groupName = "/Meters/MultiSpeak/Establish2/";
                meterGroup.setGroupName(groupName);
                meterList.getMeterID().add(METER_NO_1);
                meterList.getMeterID().add(METER_NO_2);
                meterList.getMeterID().add(METER_NO_3);
                meterList.getMeterID().add(METER_NO_4);
                meterGroup.setMeterList(meterList);
                EstablishMeterGroup establishMeterGroup = objectFactory.createEstablishMeterGroup();
                establishMeterGroup.setMeterGroup(meterGroup);
                EstablishMeterGroupResponse response =
                    instance.establishMeterGroup(mspVendor, endpointURL, establishMeterGroup);
                objects = response.getEstablishMeterGroupResult().getErrorObject();
            } else if (todo == 3) {
                // insert 4 meters into existing group
                String groupName = "/Meters/MultiSpeak/Establish/";
                meterGroup.setGroupName(groupName);
                InsertMeterInMeterGroup insertMeterInMeterGroup = objectFactory.createInsertMeterInMeterGroup();
                insertMeterInMeterGroup.setMeterGroupID(groupName);
                MRArrayOfString2 arrayOfMeters = objectFactory.createMRArrayOfString2();
                arrayOfMeters.getString().add(METER_NO_1);
                arrayOfMeters.getString().add(METER_NO_2);
                arrayOfMeters.getString().add(METER_NO_3);
                arrayOfMeters.getString().add(METER_NO_4);
                insertMeterInMeterGroup.setMeterNumbers(arrayOfMeters);
                insertMeterInMeterGroup.setMeterGroupID(groupName);
                InsertMeterInMeterGroupResponse response =
                    instance.insertMeterInMeterGroup(mspVendor, endpointURL, insertMeterInMeterGroup);
                objects = response.getInsertMeterInMeterGroupResult().getErrorObject();
            } else if (todo == 4) {
                String groupName = "/Meters/MultiSpeak/Insert/";
                InsertMeterInMeterGroup insertMeterInMeterGroup = objectFactory.createInsertMeterInMeterGroup();
                insertMeterInMeterGroup.setMeterGroupID(groupName);
                MRArrayOfString2 arrayOfMeters = objectFactory.createMRArrayOfString2();
                arrayOfMeters.getString().add(METER_NO_1);
                arrayOfMeters.getString().add(METER_NO_2);
                arrayOfMeters.getString().add(METER_NO_3);
                arrayOfMeters.getString().add(METER_NO_4);
                insertMeterInMeterGroup.setMeterNumbers(arrayOfMeters);
                InsertMeterInMeterGroupResponse response =
                    instance.insertMeterInMeterGroup(mspVendor, endpointURL, insertMeterInMeterGroup);
                objects = response.getInsertMeterInMeterGroupResult().getErrorObject();
            } else if (todo == 5) {
                // remove 4 meters from non-existing group
                String groupName = "/Meters/MultiSpeak/Remove/";
                MRArrayOfString2 arrayOfMeters = objectFactory.createMRArrayOfString2();
                arrayOfMeters.getString().add(METER_NO_1);
                arrayOfMeters.getString().add(METER_NO_2);
                arrayOfMeters.getString().add(METER_NO_3);
                arrayOfMeters.getString().add(METER_NO_4);
                RemoveMetersFromMeterGroup removeMetersFromMeterGroup =
                    objectFactory.createRemoveMetersFromMeterGroup();
                removeMetersFromMeterGroup.setMeterGroupID(groupName);
                removeMetersFromMeterGroup.setMeterNumbers(arrayOfMeters);
                RemoveMetersFromMeterGroupResponse response =
                    instance.removeMetersFromMeterGroup(mspVendor, endpointURL, removeMetersFromMeterGroup);
                objects = response.getRemoveMetersFromMeterGroupResult().getErrorObject();
            } else if (todo == 6) {
                // remove 4 meters from existing group
                String groupName = "/Meters/MultiSpeak/Establish/";
                MRArrayOfString2 arrayOfMeters = objectFactory.createMRArrayOfString2();
                arrayOfMeters.getString().add(METER_NO_1);
                arrayOfMeters.getString().add(METER_NO_2);
                arrayOfMeters.getString().add(METER_NO_3);
                arrayOfMeters.getString().add(METER_NO_4);
                RemoveMetersFromMeterGroup removeMetersFromMeterGroup =
                    objectFactory.createRemoveMetersFromMeterGroup();
                removeMetersFromMeterGroup.setMeterGroupID(groupName);
                removeMetersFromMeterGroup.setMeterNumbers(arrayOfMeters);
                RemoveMetersFromMeterGroupResponse response =
                    instance.removeMetersFromMeterGroup(mspVendor, endpointURL, removeMetersFromMeterGroup);
                objects = response.getRemoveMetersFromMeterGroupResult().getErrorObject();
            } else if (todo == 7) {
                // delete group non-exists
                String groupName = "/Meters/MultiSpeak/Remove2/";
                DeleteMeterGroup deleteMeterGroup = objectFactory.createDeleteMeterGroup();
                deleteMeterGroup.setMeterGroupID(groupName);
                DeleteMeterGroupResponse response = instance.deleteMeterGroup(mspVendor, endpointURL, deleteMeterGroup);
                objects.add(response.getDeleteMeterGroupResult());
            } else if (todo == 8) {
                // delete group exists
                String groupName = "/Meters/MultiSpeak/";
                DeleteMeterGroup deleteMeterGroup = objectFactory.createDeleteMeterGroup();
                deleteMeterGroup.setMeterGroupID(groupName);
                DeleteMeterGroupResponse response = instance.deleteMeterGroup(mspVendor, endpointURL, deleteMeterGroup);
                objects.add(response.getDeleteMeterGroupResult());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (objects != null) {
            int i = 0;
            for (ErrorObject obj : objects) {
                CTILogger.info("Error-" + i++ + ": " + obj.getErrorString());
            }
        } else {
            CTILogger.info("Successful");
        }

    }
}
