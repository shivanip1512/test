package com.cannontech.multispeak.emulator;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.axis.AxisFault;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.YukonMultispeakMsgHeader;
import com.cannontech.multispeak.deploy.service.EaLoc;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.FormattedBlock;
import com.cannontech.multispeak.deploy.service.MR_ServerSoap_BindingStub;
import com.cannontech.multispeak.deploy.service.Meter;
import com.cannontech.multispeak.deploy.service.MeterGroup;
import com.cannontech.multispeak.deploy.service.MeterRead;
import com.cannontech.multispeak.deploy.service.Nameplate;
import com.cannontech.multispeak.deploy.service.ServiceLocation;
import com.cannontech.multispeak.deploy.service.UtilityInfo;

/**
 * This class is used for 'interactive testing'. 
 * Un/comment methods as needed for testing purposes.
 * This is the "poor man's MultiSpeak testing harness" 
 */
public class MR_CB_Test {

	private MR_ServerSoap_BindingStub instance;
	
	public static void main(String [] args)
	{
		MR_CB_Test test = new MR_CB_Test();
		try {
			String endpointURL = "http://localhost:8080/yukon/soap/MR_ServerSoap";
//			endpointURL = "http://pspl-qa008.eatoneaseng.net:8080/soap/MR_ServerSoap";
//	         endpointURL = "http://demo.cannontech.com/soap/MR_CBSoap";
//			endpointURL = "http://10.100.10.25:80/soap/MR_CBSoap";
//			endpointURL = "http://10.106.36.79:8080/soap/MR_CBSoap";  //Mike's computer
			test.loadInstance(endpointURL);

			ErrorObject[] objects = null;
test.isAMRMeter("109129080");
//			test.updateServiceLocation();
//			Meter[] meters = test.getAMRSupportedMeters();
//			printMeters(meters);
			
//			objects = test.initiateMeterReadByMeterNo();
//			printErrorObjects(objects);
/*			
			meterRead = test.getLatestReadingByMeterNo(meterNumber);
			printMeterRead(meterRead);
			meterReads = test.getReadingsByMeterNo(meterNumber);
			printMeterReads(meterReads);
			*/
//			meterReads = test.getReadingsByDate(null);
//			printMeterReads(meterReads);

			
			//Formatted Block reading tests
			/*String readingType = "Outage";	//"Load"
			formattedBlock = test.getLatestReadingByMeterNoAndType(meterNumber, readingType);
			printFormattedBlock(formattedBlock);
			formattedBlocks = test.getReadingsByDateAndType(meterNumber, readingType);
			printFormattedBlocks(formattedBlocks);
			formattedBlocks = test.getLatestReadingByType(readingType);
			printFormattedBlocks(formattedBlocks);
			formattedBlocks = test.getReadingsByMeterNoAndType(meterNumber, readingType);
			printFormattedBlocks(formattedBlocks);
			*/
			
			//Meter change process tests
//			objects = test.meterAddNotification();
//			test.largeTest();
			
			//Meter Group tests
/*			String groupName = "/Meters/Test/Stacey";
			objects = test.removeMetersFromGroup(groupName);
			printErrorObjects(objects);
			objects = test.establishMeterGroup(groupName);
			printErrorObjects(objects);
			objects = test.insertMeterInMeterGroup(groupName);
			printErrorObjects(objects);
			test.deleteMeterGroup(groupName);
*/

//			printMeterRead(meterRead);
//			printMeterReads(meterReads);
//			test.initiateMeterReadByMeterNoAndType_RFN();
			printErrorObjects(objects);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private ErrorObject[] initiateMeterReadByMeterNo() throws RemoteException {
		String[] meterNumbers = new String[]{"1010071"};
		ErrorObject[] errorObject = instance.initiateMeterReadByMeterNumber(meterNumbers, null, "999", Float.MIN_NORMAL);
		return errorObject;
	}
	
	private ErrorObject[] removeMetersFromGroup(String groupName) throws RemoteException {
		ErrorObject[] objects;
		String [] meterList = buildMeterList();
		objects = instance.removeMetersFromMeterGroup(meterList, groupName);
		return objects;
	}

	private ErrorObject[] establishMeterGroup(String groupName) throws RemoteException {
		ErrorObject[] objects;
		MeterGroup meterGroup = buildMeterGroup(groupName);
		
		objects = instance.establishMeterGroup(meterGroup);
		return objects;
	}

	private ErrorObject[] insertMeterInMeterGroup(String groupName) throws RemoteException {
		ErrorObject[] objects;
		String [] meterList = buildMeterList();
		objects = instance.insertMeterInMeterGroup(meterList, groupName);
		return objects;
	}

	private void deleteMeterGroup(String groupName) throws RemoteException {
		instance.deleteMeterGroup(groupName);
	}

	
	private ErrorObject[] meterAddNotification() throws RemoteException {
		ErrorObject[] objects;
		
		String meterNo = "556689";
		String paoNameId = "556689";
		        
		Meter meter = buildMeter("MSP_RFN_420fD", meterNo, paoNameId, paoNameId, paoNameId, "ADAMS", "10556689");
		
		objects = instance.meterAddNotification(new Meter[]{meter});
		return objects;
	}
	
    private void largeTest() throws RemoteException {
        // Using Account Number as primary, auto meter_number lookup
        // Add plc new meter. MeterNumber:901000 DeviceName:901000an Address:911000
        Meter meter1 = buildMeter("!MCT-410fL Template", "901000", "901000an", "901000ci", "901000ea", "ADAMS", "911000");
        printErrorObjects(instance.meterAddNotification(new Meter[] { meter1}));

        // Add plc new meter, meter number already exists. Treat as "update". MeterNumber:901000 DeviceName:901001an Address:911001
        Meter meter2 = buildMeter("!MCT-410fL Template", "901000", "901001an", "901001ci", "901001ea", "ADAMS", "911001");
        printErrorObjects(instance.meterAddNotification(new Meter[] { meter2 }));
        
        // Add plc new meter, device name already exists. Treat as "update". Meter Number not found, but device name will find.
        // MeterNumber:901001 DeviceName:901001an Address:911000
        Meter meter3 = buildMeter("!MCT-410fL Template", "901001", "901001an", "901000ci", "901000ea", "ADAMS", "911000");
        printErrorObjects(instance.meterAddNotification(new Meter[] { meter3 }));
        
        // Add plc new meter, address already exists
        // MeterNumber:901002 DeviceName:901002an Address:911000
        Meter meter4 = buildMeter("!MCT-410fL Template", "901002", "901002an", "901000ci", "901000ea", "ADAMS", "911000");
        printErrorObjects(instance.meterAddNotification(new Meter[] { meter4 }));

        // Add rfn new meter with RfnId in template name
        // MeterNumber:902000 DeviceName:901002an RFNId:ITRN, C2SX-SD, 912000
        Meter meter5 = buildMeter("*RfnTemplate_ITRN_C2SX-SD", "902000", "902000an", "902000ci", "902000ea", "", "912000");
        printErrorObjects(instance.meterAddNotification(new Meter[] { meter5 }));

        // Add rfn new meter with RfnId in template name
        // MeterNumber:902001 902001an:901002an RFNId: LGYR, FocuskWh, 912001
        Meter meter6 = buildMeter("109129076", "902001", "902001an", "902001ci", "902001ea", "", "912001");
        printErrorObjects(instance.meterAddNotification(new Meter[] { meter6 }));

        // Change rfn meter to PLC meter, should fail
        Meter meter7 = buildMeter("901002an", "902001", "902001an", "902001ci", "902001ea", "DODAH", "912001");
        printErrorObjects(instance.meterAddNotification(new Meter[] { meter7 }));
        
        // Change rfn-420fL meter to rfn-420cL meter, should pass
        Meter meter8 = buildMeter("88638107", "902001", "902001an", "902001ci", "902001ea", "BLING", "912001");
        printErrorObjects(instance.meterAddNotification(new Meter[] { meter8 }));

        instance.meterRemoveNotification(new Meter[]{meter1, meter2, meter3, meter4, meter5, meter6, meter7, meter8});
    }

	private void loadInstance(String endpointURL) throws AxisFault, MalformedURLException {
		instance = new MR_ServerSoap_BindingStub(new URL(endpointURL), new Service());
		
		YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
		msgHeader.setCompany("Cannon MSP1");
//		msgHeader.setCompany("MSP1");
		
		SOAPHeaderElement header = new SOAPHeaderElement("http://www.multispeak.org/Version_3.0", "MultiSpeakMsgHeader", msgHeader);
		instance.setHeader(header);
	}

	private MeterRead getLatestReadingByMeterNo(String meterNo) throws RemoteException {
		MeterRead meterRead = instance.getLatestReadingByMeterNo(meterNo);
		return meterRead;
	}
	
	private Meter[] getAMRSupportedMeters() throws RemoteException {
		Meter[] meters = instance.getAMRSupportedMeters("0");
		return meters;		
	}
	
	private MeterRead[] getReadingsByMeterNo(String meterNumber) throws RemoteException {
		MeterRead[] meterReads = instance.getReadingsByMeterNo(meterNumber, getStartDate(), getEndDate());	//1068048 whe, 1010156108 sn_head/amr_demo
		return meterReads;
	}

	private MeterRead[] getReadingsByDate(String lastReceived) throws RemoteException {
		MeterRead[] meterReads = instance.getReadingsByDate(getStartDate(), getEndDate(), lastReceived);
		return meterReads;

	}

	private ErrorObject[] initiateMeterReadByMeterNoAndType(String meterNumber, String readingType) throws RemoteException {
		return instance.initiateMeterReadByMeterNoAndType(meterNumber, null, readingType, "999", Float.MIN_NORMAL);
	}

	private FormattedBlock getLatestReadingByMeterNoAndType(String meterNumber, String readingType) throws RemoteException {
		return instance.getLatestReadingByMeterNoAndType(meterNumber, readingType, null, null);
	}

	private FormattedBlock[] getLatestReadingByType(String readingType) throws RemoteException {
		return instance.getLatestReadingByType(readingType, null, null, null);
	}

	private FormattedBlock[] getReadingsByDateAndType(String meterNumber, String readingType) throws RemoteException {
		return instance.getReadingsByDateAndType(getStartDate(), getEndDate(), readingType, null, null, null);
	}
	
	private FormattedBlock[] getReadingsByMeterNoAndType(String meterNumber, String readingType) throws RemoteException {
		return instance.getReadingsByMeterNoAndType(meterNumber, getStartDate(), getEndDate(), readingType, null, null, null);
	}
	
	private String[] getSupportedReadingTypes() throws RemoteException {
		return instance.getSupportedReadingTypes();
	}
	

	private boolean isAMRMeter(String meterNumber) throws RemoteException {
		return instance.isAMRMeter(meterNumber);		
	}
	
	private static Meter buildMeter(String templateName, String meterNumber, String accountNumber, String customerId, String eaLoc,
			String substationName, String transponderId) {
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

	
	private static String[] buildMeterList() {
		String[] meterList = new String[4];
		meterList[0] = "50000011";
		meterList[1] = "50000012";
		meterList[2] = "7888";
		meterList[3] = "787";
		
		return meterList;
	}
	
	private static MeterGroup buildMeterGroup(String groupName) {
		MeterGroup meterGroup = new MeterGroup();
		String [] meterList = buildMeterList();
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
			MeterRead[] meterReads = new MeterRead[]{meterRead};
			printMeterReads(meterReads);
		}
	}
	
	private static void printMeterReads(MeterRead[] meterReads) {
		if(meterReads != null) {
			CTILogger.info("MeterRead received: " + meterReads.length);
			
			for (MeterRead meterRead : meterReads ) {
				CTILogger.info(meterRead.getMeterNo() + " - " +
						meterRead.getPosKWh() + " " +
						(meterRead.getReadingDate() != null ? meterRead.getReadingDate().getTime():"*") + " --- " +  
						meterRead.getKW() + " " + 
						(meterRead.getKWDateTime() != null ? meterRead.getKWDateTime().getTime():"*"));
			}
		}
	}

	private static void printFormattedBlock(FormattedBlock formattedBlock) {
		if (formattedBlock != null) {
			FormattedBlock[] formattedBlocks = new FormattedBlock[]{formattedBlock};
			printFormattedBlocks(formattedBlocks);
		}
	}
	
	private static void printFormattedBlocks(FormattedBlock[] formattedBlocks) {
		if(formattedBlocks != null) {
			CTILogger.info("MeterRead received: " + formattedBlocks.length + " : " );
			
			for (FormattedBlock formattedBlock : formattedBlocks) {
				if (formattedBlock.getValueList() != null) {
					for (String value : formattedBlock.getValueList()) {
						CTILogger.info(value);
					}
				}
			}
		}
	}

	private static void printErrorObjects(ErrorObject[] errorObjects) {
		if (errorObjects != null) {
			CTILogger.info("ErrorObjects received: " + errorObjects.length + " : " );
			for (ErrorObject errorObject : errorObjects) {
			    CTILogger.info("Error-" + errorObject.getErrorString());
            }
        } else {
            CTILogger.info("Successful - No errors returned");
        }
	}

	private static void printMeters(Meter[] meters) {
		if (meters != null) {
			CTILogger.info("METERS RETURNED: " + meters.length);
			for (Meter meter : meters) {
				CTILogger.info(meter.getMeterNo());
			}
		}
	}
	
	private void updateServiceLocation() throws RemoteException {
	    ServiceLocation serviceLocation = new ServiceLocation();
	    serviceLocation.setObjectID("JESSMETER 46");   //JESSMETER 4609
	    ServiceLocation[]serviceLocations = new ServiceLocation[] {serviceLocation};
	    instance.serviceLocationChangedNotification(serviceLocations);
	}
	
   private void initiateMeterReadByMeterNoAndType_RFN() throws RemoteException {
       
       // local RFN

       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("109129076", null, "Load", "999", Float.MIN_NORMAL));
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("109129080", null, "Load", "999", Float.MIN_NORMAL));
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("109129118", null, "Load", "999", Float.MIN_NORMAL));
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("109129452", null, "Load", "999", Float.MIN_NORMAL));
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("109129453", null, "Load", "999", Float.MIN_NORMAL));
       // local PLC
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("01102073", null, "Load", "999", Float.MIN_NORMAL));
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("01102074", null, "Load", "999", Float.MIN_NORMAL));
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("01102078", null, "Load", "999", Float.MIN_NORMAL));
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("01102080", null, "Load", "999", Float.MIN_NORMAL));
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("01102081", null, "Load", "999", Float.MIN_NORMAL));
       printErrorObjects(instance.initiateMeterReadByMeterNoAndType("01102083", null, "Load", "999", Float.MIN_NORMAL));
       //QA008 meters
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("068498683", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("068498684", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("068498685", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("068498686", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("103399179", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("103399180", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("103399181", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("103399211", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("103399212", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("103399213", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("103399214", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("108951738", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("108951739", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("108951740", null, "Load", "999", Float.MIN_NORMAL));
//        printErrorObjects(instance.initiateMeterReadByMeterNoAndType("108951741", null, "Load", "999", Float.MIN_NORMAL));
    }

}