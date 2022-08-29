package com.cannontech.multispeak.emulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.msp.beans.v3.ArrayOfMeter;
import com.cannontech.msp.beans.v3.ArrayOfServiceLocation;
import com.cannontech.msp.beans.v3.DeleteMeterGroup;
import com.cannontech.msp.beans.v3.EaLoc;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.EstablishMeterGroup;
import com.cannontech.msp.beans.v3.EstablishMeterGroupResponse;
import com.cannontech.msp.beans.v3.FormattedBlock;
import com.cannontech.msp.beans.v3.GetAMRSupportedMeters;
import com.cannontech.msp.beans.v3.GetAMRSupportedMetersResponse;
import com.cannontech.msp.beans.v3.GetLatestReadingByMeterNo;
import com.cannontech.msp.beans.v3.GetLatestReadingByMeterNoAndType;
import com.cannontech.msp.beans.v3.GetLatestReadingByMeterNoAndTypeResponse;
import com.cannontech.msp.beans.v3.GetLatestReadingByType;
import com.cannontech.msp.beans.v3.GetLatestReadingByTypeResponse;
import com.cannontech.msp.beans.v3.GetReadingsByDate;
import com.cannontech.msp.beans.v3.GetReadingsByDateAndType;
import com.cannontech.msp.beans.v3.GetReadingsByDateAndTypeResponse;
import com.cannontech.msp.beans.v3.GetReadingsByDateResponse;
import com.cannontech.msp.beans.v3.GetReadingsByMeterNo;
import com.cannontech.msp.beans.v3.GetReadingsByMeterNoAndType;
import com.cannontech.msp.beans.v3.GetReadingsByMeterNoAndTypeResponse;
import com.cannontech.msp.beans.v3.GetReadingsByMeterNoResponse;
import com.cannontech.msp.beans.v3.GetSupportedReadingTypes;
import com.cannontech.msp.beans.v3.GetSupportedReadingTypesResponse;
import com.cannontech.msp.beans.v3.InitiateMeterReadByMeterNoAndType;
import com.cannontech.msp.beans.v3.InitiateMeterReadByMeterNoAndTypeResponse;
import com.cannontech.msp.beans.v3.InsertMeterInMeterGroup;
import com.cannontech.msp.beans.v3.InsertMeterInMeterGroupResponse;
import com.cannontech.msp.beans.v3.IsAMRMeter;
import com.cannontech.msp.beans.v3.IsAMRMeterResponse;
import com.cannontech.msp.beans.v3.MRArrayOfString2;
import com.cannontech.msp.beans.v3.Meter;
import com.cannontech.msp.beans.v3.MeterAddNotification;
import com.cannontech.msp.beans.v3.MeterAddNotificationResponse;
import com.cannontech.msp.beans.v3.MeterGroup;
import com.cannontech.msp.beans.v3.MeterList;
import com.cannontech.msp.beans.v3.MeterRead;
import com.cannontech.msp.beans.v3.MeterRemoveNotification;
import com.cannontech.msp.beans.v3.Nameplate;
import com.cannontech.msp.beans.v3.ObjectFactory;
import com.cannontech.msp.beans.v3.RemoveMetersFromMeterGroup;
import com.cannontech.msp.beans.v3.RemoveMetersFromMeterGroupResponse;
import com.cannontech.msp.beans.v3.ServiceLocation;
import com.cannontech.msp.beans.v3.ServiceLocationChangedNotification;
import com.cannontech.msp.beans.v3.UtilityInfo;
import com.cannontech.multispeak.client.MspAttribute;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.MRClient;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;

/**
 * This class is used for 'interactive testing'.
 * Un/comment methods as needed for testing purposes.
 * This is the "poor man's MultiSpeak testing harness"
 */
public class MR_CB_Test {

    private String endpointURL = "http://localhost:8088/mockMR_ServerSoap";
    private static MRClient instance;
    private static ObjectFactory objectFactory;
    List<MspAttribute> attributes = Arrays.asList(MspAttribute.KVAR_KVARH, MspAttribute.PEAKDEMAND_USAGE);
    private MultispeakVendor mspVendor = new MultispeakVendor(23213, "Cannon", "Yukon", "pwd", "sadsad", "", "", 100, 120, 12,
        null, false, attributes);

    public static void main(String[] args) {
        MR_CB_Test test = new MR_CB_Test();
		ApplicationContext context = new ClassPathXmlApplicationContext("com/cannontech/multispeak/emulator/testEmulatorContext.xml");
		instance = context.getBean(MRClient.class);
		objectFactory = context.getBean(ObjectFactory.class);
        try {
            // endpointURL = "http://pspl-qa008.eatoneaseng.net:8080/soap/MR_ServerSoap";
            // endpointURL = "http://demo.cannontech.com/soap/MR_CBSoap";
            // endpointURL = "http://10.100.10.25:80/soap/MR_CBSoap";
            // endpointURL = "http://10.106.36.79:8080/soap/MR_CBSoap"; //Mike's computer

            List<ErrorObject> objects = null;
            test.isAMRMeter("109129080");
            test.updateServiceLocation();
            List<Meter> meters = test.getAMRSupportedMeters();
            printMeters(meters);

            objects = test.initiateMeterReadByMeterNoAndType("109129080", "Load");
            printErrorObjects(objects);

            MeterRead meterRead = test.getLatestReadingByMeterNo("109129080");
            printMeterRead(meterRead);
            List<MeterRead> meterReads = test.getReadingsByMeterNo("109129080");
            printMeterReads(meterReads);

            meterReads = test.getReadingsByDate(null);
            printMeterReads(meterReads);

            // Formatted Block reading tests

            String readingType = "Outage"; // "Load"
            FormattedBlock formattedBlock = test.getLatestReadingByMeterNoAndType("109129080", readingType);
            printFormattedBlock(formattedBlock);
            List<FormattedBlock> formattedBlocks = test.getReadingsByDateAndType("109129080", readingType);
            printFormattedBlocks(formattedBlocks);
            formattedBlocks = test.getLatestReadingByType(readingType);
            printFormattedBlocks(formattedBlocks);
            formattedBlocks = test.getReadingsByMeterNoAndType("109129080", readingType);
            printFormattedBlocks(formattedBlocks);

            // Meter change process tests
            objects = test.meterAddNotification();
            test.largeTest();

            // Meter Group tests

            String groupName = "/Meters/Test/Stacey";
            objects = test.removeMetersFromGroup(groupName);
            printErrorObjects(objects);
            objects = test.establishMeterGroup(groupName);
            printErrorObjects(objects);
            objects = test.insertMeterInMeterGroup(groupName);
            printErrorObjects(objects);
            test.deleteMeterGroup(groupName);

            printMeterRead(meterRead);
            printMeterReads(meterReads);
            test.initiateMeterReadByMeterNoAndType_RFN();
            printErrorObjects(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<ErrorObject> removeMetersFromGroup(String groupName) throws MultispeakWebServiceClientException {
        RemoveMetersFromMeterGroupResponse objects;
        List<ErrorObject> errorObjects = null;
        MeterList meterList = buildMeterList();
        RemoveMetersFromMeterGroup removeMetersFromMeterGroup = objectFactory.createRemoveMetersFromMeterGroup();
        removeMetersFromMeterGroup.setMeterGroupID(groupName);
        MRArrayOfString2 arrayOfString = objectFactory.createMRArrayOfString2();
        buildArrayOfStringWithMeterList(arrayOfString, meterList.getMeterID());
        removeMetersFromMeterGroup.setMeterNumbers(arrayOfString);
        objects = instance.removeMetersFromMeterGroup(mspVendor, endpointURL, removeMetersFromMeterGroup);
        if (objects.getRemoveMetersFromMeterGroupResult() != null) {
            errorObjects = objects.getRemoveMetersFromMeterGroupResult().getErrorObject();
        }
        return errorObjects;
    }

    private void buildArrayOfStringWithMeterList(MRArrayOfString2 arrayOfString, List<String> list) {
        for (String meterId : list) {
            arrayOfString.getString().add(meterId);
        }
    }

    private List<ErrorObject> establishMeterGroup(String groupName) throws MultispeakWebServiceClientException {
        List<ErrorObject> errorObjects = null;
        EstablishMeterGroupResponse objects;
        MeterGroup meterGroup = buildMeterGroup(groupName);

        EstablishMeterGroup establishMeterGroup = objectFactory.createEstablishMeterGroup();
        establishMeterGroup.setMeterGroup(meterGroup);
        objects = instance.establishMeterGroup(mspVendor, endpointURL, establishMeterGroup);
        if (objects.getEstablishMeterGroupResult() != null) {
            errorObjects = objects.getEstablishMeterGroupResult().getErrorObject();
        }
        return errorObjects;
    }

    private List<ErrorObject> insertMeterInMeterGroup(String groupName) throws MultispeakWebServiceClientException {
        InsertMeterInMeterGroupResponse objects;
        MeterList meterList = buildMeterList();
        InsertMeterInMeterGroup insertMeterInMeterGroup = objectFactory.createInsertMeterInMeterGroup();
        insertMeterInMeterGroup.setMeterGroupID(groupName);
        MRArrayOfString2 arrayOfString = objectFactory.createMRArrayOfString2();
        buildArrayOfStringWithMeterList(arrayOfString, meterList.getMeterID());
        insertMeterInMeterGroup.setMeterNumbers(arrayOfString);
        objects = instance.insertMeterInMeterGroup(mspVendor, endpointURL, insertMeterInMeterGroup);
        List<ErrorObject> errorObjects = null;
        if (objects.getInsertMeterInMeterGroupResult() != null) {
            errorObjects = objects.getInsertMeterInMeterGroupResult().getErrorObject();
        }
        return errorObjects;
    }

    private void deleteMeterGroup(String groupName) throws MultispeakWebServiceClientException {
        DeleteMeterGroup deleteMeterGroup = objectFactory.createDeleteMeterGroup();
        deleteMeterGroup.setMeterGroupID(groupName);
        instance.deleteMeterGroup(mspVendor, endpointURL, deleteMeterGroup);
    }

    private List<ErrorObject> meterAddNotification() throws MultispeakWebServiceClientException {
        MeterAddNotificationResponse objects;

        String meterNo = "556689";
        String paoNameId = "556689";

        Meter meter = buildMeter("MSP_RFN_420fD", meterNo, paoNameId, paoNameId, paoNameId, "ADAMS", "10556689");
        MeterAddNotification meterAddNotification = objectFactory.createMeterAddNotification();
        ArrayOfMeter arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter);
        meterAddNotification.setAddedMeters(arrayOfMeter);
        objects = instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);
        List<ErrorObject> errorObjects = null;
        if (objects.getMeterAddNotificationResult() != null) {
            errorObjects = objects.getMeterAddNotificationResult().getErrorObject();
        }
        return errorObjects;
    }

    private void largeTest() throws MultispeakWebServiceClientException {
        // Using Account Number as primary, auto meter_number lookup
        // Add plc new meter. MeterNumber:901000 DeviceName:901000an Address:911000
        Meter meter1 =
            buildMeter("!MCT-410fL Template", "901000", "901000an", "901000ci", "901000ea", "ADAMS", "911000");
        ArrayOfMeter arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter1);
        MeterAddNotification meterAddNotification = objectFactory.createMeterAddNotification();
        meterAddNotification.setAddedMeters(arrayOfMeter);
        MeterAddNotificationResponse objects =
            instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);
        List<ErrorObject> errorObjects = null;
        if (objects.getMeterAddNotificationResult() != null) {
            errorObjects = objects.getMeterAddNotificationResult().getErrorObject();
        }
        printErrorObjects(errorObjects);

        // Add plc new meter, meter number already exists. Treat as "update". MeterNumber:901000
        // DeviceName:901001an Address:911001
        Meter meter2 =
            buildMeter("!MCT-410fL Template", "901000", "901001an", "901001ci", "901001ea", "ADAMS", "911001");
        arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter2);
        errorObjects = null;
        objects = instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);
        if (objects.getMeterAddNotificationResult() != null) {
            errorObjects = objects.getMeterAddNotificationResult().getErrorObject();
        }
        printErrorObjects(errorObjects);

        // Add plc new meter, device name already exists. Treat as "update". Meter Number not found, but
        // device name will find.
        // MeterNumber:901001 DeviceName:901001an Address:911000

        Meter meter3 =
            buildMeter("!MCT-410fL Template", "901001", "901001an", "901000ci", "901000ea", "ADAMS", "911000");
        arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter3);
        errorObjects = null;
        objects = instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);
        if (objects.getMeterAddNotificationResult() != null) {
            errorObjects = objects.getMeterAddNotificationResult().getErrorObject();
        }
        printErrorObjects(errorObjects);

        // Add plc new meter, address already exists
        // MeterNumber:901002 DeviceName:901002an Address:911000
        Meter meter4 =
            buildMeter("!MCT-410fL Template", "901002", "901002an", "901000ci", "901000ea", "ADAMS", "911000");
        arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter4);
        errorObjects = null;
        objects = instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);
        if (objects.getMeterAddNotificationResult() != null) {
            errorObjects = objects.getMeterAddNotificationResult().getErrorObject();
        }
        printErrorObjects(errorObjects);

        // Add rfn new meter with RfnId in template name
        // MeterNumber:902000 DeviceName:901002an RFNId:ITRN, C2SX-SD, 912000
        Meter meter5 =
            buildMeter("*RfnTemplate_ITRN_C2SX-SD", "902000", "902000an", "902000ci", "902000ea", "", "912000");
        arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter5);
        errorObjects = null;
        objects = instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);
        if (objects.getMeterAddNotificationResult() != null) {
            errorObjects = objects.getMeterAddNotificationResult().getErrorObject();
        }
        printErrorObjects(errorObjects);

        // Add rfn new meter with RfnId in template name
        // MeterNumber:902001 902001an:901002an RFNId: LGYR, FocuskWh, 912001
        Meter meter6 = buildMeter("109129076", "902001", "902001an", "902001ci", "902001ea", "", "912001");
        arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter6);
        errorObjects = null;
        objects = instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);
        if (objects.getMeterAddNotificationResult() != null) {
            errorObjects = objects.getMeterAddNotificationResult().getErrorObject();
        }
        printErrorObjects(errorObjects);

        // Change rfn meter to PLC meter, should fail
        Meter meter7 = buildMeter("901002an", "902001", "902001an", "902001ci", "902001ea", "DODAH", "912001");
        arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter7);
        errorObjects = null;
        objects = instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);
        if (objects.getMeterAddNotificationResult() != null) {
            errorObjects = objects.getMeterAddNotificationResult().getErrorObject();
        }
        printErrorObjects(errorObjects);
        // Change rfn-420fL meter to rfn-420cL meter, should pass
        Meter meter8 = buildMeter("88638107", "902001", "902001an", "902001ci", "902001ea", "BLING", "912001");
        arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter8);
        errorObjects = null;
        objects = instance.meterAddNotification(mspVendor, endpointURL, meterAddNotification);
        if (objects.getMeterAddNotificationResult() != null) {
            errorObjects = objects.getMeterAddNotificationResult().getErrorObject();
        }
        printErrorObjects(errorObjects);

        MeterRemoveNotification meterRemoveNotification = objectFactory.createMeterRemoveNotification();
        arrayOfMeter = objectFactory.createArrayOfMeter();
        arrayOfMeter.getMeter().add(meter1);
        arrayOfMeter.getMeter().add(meter2);
        arrayOfMeter.getMeter().add(meter3);
        arrayOfMeter.getMeter().add(meter4);
        arrayOfMeter.getMeter().add(meter5);
        arrayOfMeter.getMeter().add(meter6);
        arrayOfMeter.getMeter().add(meter7);
        arrayOfMeter.getMeter().add(meter8);
        meterRemoveNotification.setRemovedMeters(arrayOfMeter);
        instance.meterRemoveNotification(mspVendor, endpointURL, meterRemoveNotification);
    }

    private MeterRead getLatestReadingByMeterNo(String meterNo) throws MultispeakWebServiceClientException {
        GetLatestReadingByMeterNo getLatestReadingByMeterNo = objectFactory.createGetLatestReadingByMeterNo();
        getLatestReadingByMeterNo.setMeterNo(meterNo);
        MeterRead meterRead =
            (instance.getLatestReadingByMeterNo(mspVendor, endpointURL,
                getLatestReadingByMeterNo)).getGetLatestReadingByMeterNoResult();
        return meterRead;
    }

    private List<Meter> getAMRSupportedMeters() throws MultispeakWebServiceClientException {
        GetAMRSupportedMeters getAMRSupportedMeters = objectFactory.createGetAMRSupportedMeters();
        getAMRSupportedMeters.setLastReceived("0");
        GetAMRSupportedMetersResponse response =
            instance.getAMRSupportedMeters(mspVendor, endpointURL, getAMRSupportedMeters);
        return response.getGetAMRSupportedMetersResult().getMeter();
    }

    private List<MeterRead> getReadingsByMeterNo(String meterNumber) throws MultispeakWebServiceClientException {
        GetReadingsByMeterNo getReadingsByMeterNo = objectFactory.createGetReadingsByMeterNo();
        getReadingsByMeterNo.setStartDate(MultispeakFuncs.toXMLGregorianCalendar(getStartDate()));
        getReadingsByMeterNo.setMeterNo(meterNumber);
        getReadingsByMeterNo.setEndDate(MultispeakFuncs.toXMLGregorianCalendar(getEndDate()));
        GetReadingsByMeterNoResponse response =
            instance.getReadingsByMeterNo(mspVendor, endpointURL, getReadingsByMeterNo); // 1068048
        List<MeterRead> meterReads = null;
        if (response.getGetReadingsByMeterNoResult() != null) {
            meterReads = response.getGetReadingsByMeterNoResult().getMeterRead();
        }
        return meterReads;
    }

    private List<MeterRead> getReadingsByDate(String lastReceived) throws MultispeakWebServiceClientException {
        GetReadingsByDate getReadingsByDate = objectFactory.createGetReadingsByDate();
        getReadingsByDate.setEndDate(MultispeakFuncs.toXMLGregorianCalendar(getEndDate()));
        getReadingsByDate.setLastReceived(lastReceived);
        getReadingsByDate.setStartDate(MultispeakFuncs.toXMLGregorianCalendar(getStartDate()));
        GetReadingsByDateResponse response = instance.getReadingsByDate(mspVendor, endpointURL, getReadingsByDate);
        List<MeterRead> meterReads = null;
        if (response.getGetReadingsByDateResult() != null) {
            meterReads = response.getGetReadingsByDateResult().getMeterRead();
        }
        return meterReads;

    }

    private List<ErrorObject> initiateMeterReadByMeterNoAndType(String meterNumber, String readingType)
            throws MultispeakWebServiceClientException {
        InitiateMeterReadByMeterNoAndType initiateMeterReadByMeterNoAndType =
            objectFactory.createInitiateMeterReadByMeterNoAndType();
        initiateMeterReadByMeterNoAndType.setExpirationTime(Float.MIN_NORMAL);
        initiateMeterReadByMeterNoAndType.setMeterNo(meterNumber);
        initiateMeterReadByMeterNoAndType.setReadingType(readingType);
        initiateMeterReadByMeterNoAndType.setResponseURL(endpointURL);
        initiateMeterReadByMeterNoAndType.setTransactionID("999");
        InitiateMeterReadByMeterNoAndTypeResponse response =
            instance.initiateMeterReadByMeterNoAndType(mspVendor, endpointURL, initiateMeterReadByMeterNoAndType);
        List<ErrorObject> errorObjects = null;
        if (response.getInitiateMeterReadByMeterNoAndTypeResult() != null) {
            if (response.getInitiateMeterReadByMeterNoAndTypeResult().getErrorObject() != null) {
                errorObjects = response.getInitiateMeterReadByMeterNoAndTypeResult().getErrorObject();
            }
        }
        return errorObjects;
    }

    private FormattedBlock getLatestReadingByMeterNoAndType(String meterNumber, String readingType)
            throws MultispeakWebServiceClientException {

        GetLatestReadingByMeterNoAndType getLatestReadingByMeterNoAndType =
            objectFactory.createGetLatestReadingByMeterNoAndType();
        getLatestReadingByMeterNoAndType.setFieldName(null);
        getLatestReadingByMeterNoAndType.setFormattedBlockTemplateName(null);
        getLatestReadingByMeterNoAndType.setMeterNo(meterNumber);
        getLatestReadingByMeterNoAndType.setReadingType(readingType);
        GetLatestReadingByMeterNoAndTypeResponse response =
            instance.getLatestReadingByMeterNoAndType(mspVendor, endpointURL, getLatestReadingByMeterNoAndType);
        FormattedBlock blocks = null;
        if (response.getGetLatestReadingByMeterNoAndTypeResult() != null) {
            blocks = response.getGetLatestReadingByMeterNoAndTypeResult();
        }
        return blocks;
    }

    private List<FormattedBlock> getLatestReadingByType(String readingType) throws MultispeakWebServiceClientException {
        GetLatestReadingByType getLatestReadingByType = objectFactory.createGetLatestReadingByType();
        getLatestReadingByType.setReadingType(readingType);
        GetLatestReadingByTypeResponse response =
            instance.getLatestReadingByType(mspVendor, endpointURL, getLatestReadingByType);
        List<FormattedBlock> blocks = null;
        if (response.getGetLatestReadingByTypeResult() != null) {
            blocks = response.getGetLatestReadingByTypeResult().getFormattedBlock();
        }
        return blocks;
    }

    private List<FormattedBlock> getReadingsByDateAndType(String meterNumber, String readingType)
            throws MultispeakWebServiceClientException {
        GetReadingsByDateAndType getReadingsByDateAndType = objectFactory.createGetReadingsByDateAndType();
        getReadingsByDateAndType.setEndDate(MultispeakFuncs.toXMLGregorianCalendar(getEndDate()));
        getReadingsByDateAndType.setStartDate(MultispeakFuncs.toXMLGregorianCalendar(getStartDate()));
        getReadingsByDateAndType.setReadingType(readingType);
        GetReadingsByDateAndTypeResponse response =
            instance.getReadingsByDateAndType(mspVendor, endpointURL, getReadingsByDateAndType);
        List<FormattedBlock> blocks = null;
        if (response.getGetReadingsByDateAndTypeResult() != null) {
            blocks = response.getGetReadingsByDateAndTypeResult().getFormattedBlock();
        }
        return blocks;
    }

    private List<FormattedBlock> getReadingsByMeterNoAndType(String meterNumber, String readingType)
            throws MultispeakWebServiceClientException {
        GetReadingsByMeterNoAndType getReadingsByMeterNoAndType = objectFactory.createGetReadingsByMeterNoAndType();

        getReadingsByMeterNoAndType.setEndDate(MultispeakFuncs.toXMLGregorianCalendar(getEndDate()));
        getReadingsByMeterNoAndType.setStartDate(MultispeakFuncs.toXMLGregorianCalendar(getStartDate()));
        getReadingsByMeterNoAndType.setReadingType(readingType);
        getReadingsByMeterNoAndType.setMeterNo(meterNumber);

        GetReadingsByMeterNoAndTypeResponse response =
            instance.getReadingsByMeterNoAndType(mspVendor, endpointURL, getReadingsByMeterNoAndType);
        List<FormattedBlock> blocks = null;
        if (response.getGetReadingsByMeterNoAndTypeResult() != null) {
            blocks = response.getGetReadingsByMeterNoAndTypeResult().getFormattedBlock();
        }
        return blocks;
    }

    private List<String> getSupportedReadingTypes() throws MultispeakWebServiceClientException {
        GetSupportedReadingTypes getSupportedReadingTypes = objectFactory.createGetSupportedReadingTypes();
        GetSupportedReadingTypesResponse response =
            instance.getSupportedReadingTypes(mspVendor, endpointURL, getSupportedReadingTypes);
        List<String> readingTypes = null;
        if (response.getGetSupportedReadingTypesResult() != null) {
            readingTypes = response.getGetSupportedReadingTypesResult().getString();
        }
        return readingTypes;
    }

    private boolean isAMRMeter(String meterNumber) throws MultispeakWebServiceClientException {
        IsAMRMeter isAMRMeter = objectFactory.createIsAMRMeter();
        isAMRMeter.setMeterNo(meterNumber);
        IsAMRMeterResponse response = instance.isAMRMeter(mspVendor, endpointURL, isAMRMeter);
        return response.isIsAMRMeterResult();
    }

    private static Meter buildMeter(String templateName, String meterNumber, String accountNumber, String customerId,
            String eaLoc, String substationName, String transponderId) {
        Meter meter = new Meter();
        meter.setAMRDeviceType(templateName);
        meter.setMeterNo(meterNumber);

        UtilityInfo utilityInfo = new UtilityInfo();
        utilityInfo.setAccountNumber(accountNumber);
        utilityInfo.setCustID(customerId);
        utilityInfo.setServLoc(accountNumber);

        EaLoc eaLocObject = new EaLoc();
        eaLocObject.setName(eaLoc);
        utilityInfo.setEaLoc(eaLocObject);
        utilityInfo.setSubstationName(substationName);
        meter.setUtilityInfo(utilityInfo);

        Nameplate nameplate = new Nameplate();
        nameplate.setTransponderID(transponderId);
        meter.setNameplate(nameplate);

        return meter;
    }

    private static MeterList buildMeterList() {
        MeterList meterList = objectFactory.createMeterList();
        meterList.getMeterID().add("50000011");
        meterList.getMeterID().add("50000012");
        meterList.getMeterID().add("7888");
        meterList.getMeterID().add("787");
        return meterList;
    }

    private static MeterGroup buildMeterGroup(String groupName) {
        MeterGroup meterGroup = new MeterGroup();
        MeterList meterList = buildMeterList();
        meterGroup.setMeterList(meterList);
        meterGroup.setGroupName(groupName);
        return meterGroup;
    }

    private static Calendar getStartDate() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.set(Calendar.MONTH, Calendar.JULY);
        cal.set(Calendar.YEAR, 2006);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    private static Calendar getEndDate() {
        return new GregorianCalendar();
    }

    private static void printMeterRead(MeterRead meterRead) {
        if (meterRead != null) {
            List<MeterRead> meterReads = new ArrayList<MeterRead>();
            meterReads.add(meterRead);
            printMeterReads(meterReads);
        }
    }

    private static void printMeterReads(List<MeterRead> meterReads) {
        if (meterReads != null) {
            CTILogger.info("MeterRead received: " + meterReads.size());

            for (MeterRead meterRead : meterReads) {
                CTILogger.info(meterRead.getMeterNo() + " - " + meterRead.getPosKWh() + " "
                    + (meterRead.getReadingDate() != null ? meterRead.getReadingDate() : "*") + " --- "
                    + meterRead.getKW() + " " + (meterRead.getKWDateTime() != null ? meterRead.getKWDateTime() : "*"));
            }
        }
    }

    private static void printFormattedBlock(FormattedBlock formattedBlock) {
        if (formattedBlock != null) {
            List<FormattedBlock> formattedBlocks = new ArrayList<FormattedBlock>();
            formattedBlocks.add(formattedBlock);
            printFormattedBlocks(formattedBlocks);
        }
    }

    private static void printFormattedBlocks(List<FormattedBlock> formattedBlocks) {
        if (formattedBlocks != null) {
            CTILogger.info("MeterRead received: " + formattedBlocks.size() + " : ");

            for (FormattedBlock formattedBlock : formattedBlocks) {
                if (formattedBlock.getValueList() != null) {
                    for (String value : formattedBlock.getValueList().getVal()) {
                        CTILogger.info(value);
                    }
                }
            }
        }
    }

    private static void printErrorObjects(List<ErrorObject> errorObjects) {
        if (errorObjects != null) {
            CTILogger.info("ErrorObjects received: " + errorObjects.size() + " : ");
            for (ErrorObject errorObject : errorObjects) {
                CTILogger.info("Error-" + errorObject.getErrorString());
            }
        } else {
            CTILogger.info("Successful - No errors returned");
        }
    }

    private static void printMeters(List<Meter> meters) {
        if (meters != null) {
            CTILogger.info("METERS RETURNED: " + meters.size());
            for (Meter meter : meters) {
                CTILogger.info(meter.getMeterNo());
            }
        }
    }

    private void updateServiceLocation() throws MultispeakWebServiceClientException {
        ServiceLocation serviceLocation = new ServiceLocation();
        serviceLocation.setObjectID("JESSMETER 46"); // JESSMETER 4609
        ServiceLocationChangedNotification serviceLocationChangedNotification =
            objectFactory.createServiceLocationChangedNotification();
        ArrayOfServiceLocation arrayOfServLoc = objectFactory.createArrayOfServiceLocation();
        arrayOfServLoc.getServiceLocation().add(serviceLocation);
        serviceLocationChangedNotification.setChangedServiceLocations(arrayOfServLoc);
        instance.serviceLocationChangedNotification(mspVendor, endpointURL, serviceLocationChangedNotification);
    }

    private List<ErrorObject> initiateMeterReadByMeterNoAndType(String meterNo, String responseURL, String readingType,
            String transactionID, Float expirationTime) throws MultispeakWebServiceClientException {
        InitiateMeterReadByMeterNoAndType initiateMeterReadByMeterNoAndType =
            objectFactory.createInitiateMeterReadByMeterNoAndType();
        initiateMeterReadByMeterNoAndType.setExpirationTime(expirationTime);
        initiateMeterReadByMeterNoAndType.setMeterNo(meterNo);
        initiateMeterReadByMeterNoAndType.setReadingType(readingType);
        initiateMeterReadByMeterNoAndType.setResponseURL(responseURL);
        initiateMeterReadByMeterNoAndType.setTransactionID(transactionID);
        InitiateMeterReadByMeterNoAndTypeResponse response =
            instance.initiateMeterReadByMeterNoAndType(mspVendor, endpointURL, initiateMeterReadByMeterNoAndType);
        List<ErrorObject> errorObjects = null;
        if (response.getInitiateMeterReadByMeterNoAndTypeResult() != null) {
            if (response.getInitiateMeterReadByMeterNoAndTypeResult().getErrorObject() != null) {
                errorObjects = response.getInitiateMeterReadByMeterNoAndTypeResult().getErrorObject();
            }
        }
        return errorObjects;
    }

    private void initiateMeterReadByMeterNoAndType_RFN() throws MultispeakWebServiceClientException {

        // local RFN

        printErrorObjects(initiateMeterReadByMeterNoAndType("109129076", null, "Load", "999", Float.MIN_NORMAL));
        printErrorObjects(initiateMeterReadByMeterNoAndType("109129080", null, "Load", "999", Float.MIN_NORMAL));
        printErrorObjects(initiateMeterReadByMeterNoAndType("109129118", null, "Load", "999", Float.MIN_NORMAL));
        printErrorObjects(initiateMeterReadByMeterNoAndType("109129452", null, "Load", "999", Float.MIN_NORMAL));
        printErrorObjects(initiateMeterReadByMeterNoAndType("109129453", null, "Load", "999", Float.MIN_NORMAL));
        // local PLC
        printErrorObjects(initiateMeterReadByMeterNoAndType("01102073", null, "Load", "999", Float.MIN_NORMAL));
        printErrorObjects(initiateMeterReadByMeterNoAndType("01102074", null, "Load", "999", Float.MIN_NORMAL));
        printErrorObjects(initiateMeterReadByMeterNoAndType("01102078", null, "Load", "999", Float.MIN_NORMAL));
        printErrorObjects(initiateMeterReadByMeterNoAndType("01102080", null, "Load", "999", Float.MIN_NORMAL));
        printErrorObjects(initiateMeterReadByMeterNoAndType("01102081", null, "Load", "999", Float.MIN_NORMAL));
        printErrorObjects(initiateMeterReadByMeterNoAndType("01102083", null, "Load", "999", Float.MIN_NORMAL));
        // QA008 meters
        // printErrorObjects(initiateMeterReadByMeterNoAndType("068498683", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("068498684", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("068498685", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("068498686", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("103399179", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("103399180", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("103399181", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("103399211", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("103399212", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("103399213", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("103399214", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("108951738", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("108951739", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(initiateMeterReadByMeterNoAndType("108951740", null, "Load", "999",
        // Float.MIN_NORMAL));
        // printErrorObjects(instance.initiateMeterReadByMeterNoAndType("108951741", null, "Load", "999",
        // Float.MIN_NORMAL));
    }

}