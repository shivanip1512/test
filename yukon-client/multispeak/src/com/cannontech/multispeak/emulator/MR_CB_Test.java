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
			String endpointURL = "http://localhost:8080/soap/MR_ServerSoap";
//			endpointURL = "http://demo.cannontech.com/soap/MR_CBSoap";
//			endpointURL = "http://10.100.10.25:80/soap/MR_CBSoap";
//			endpointURL = "http://10.106.36.79:8080/soap/MR_CBSoap";  //Mike's computer
			test.loadInstance(endpointURL);

			//Results container objects
			Meter[] meters;
			MeterRead meterRead;
			MeterRead [] meterReads;
			FormattedBlock formattedBlock;
			FormattedBlock[] formattedBlocks;
			ErrorObject[] objects = null;

			meters = test.getAMRSupportedMeters();
			printMeters(meters);
			
			//Readings tests
			String meterNumber = "1000119";		
			System.out.println(meterNumber + "- IS AMR METER? " + test.isAMRMeter(meterNumber));
			
/*			objects = test.initiateMeterReadByMeterNo();
			printErrorObjects(objects);
			
			meterRead = test.getLatestReadingByMeterNo(meterNumber);
			printMeterRead(meterRead);
			meterReads = test.getReadingsByMeterNo(meterNumber);
			printMeterReads(meterReads);
			*/
//			meterReads = test.getReadingsByDate(null);
//			printMeterReads(meterReads);

			
			//Formatted Block reading tests
			String readingType = "Outage";	//"Load"
			formattedBlock = test.getLatestReadingByMeterNoAndType(meterNumber, readingType);
			printFormattedBlock(formattedBlock);
			formattedBlocks = test.getReadingsByDateAndType(meterNumber, readingType);
			printFormattedBlocks(formattedBlocks);
			formattedBlocks = test.getLatestReadingByType(readingType);
			printFormattedBlocks(formattedBlocks);
			formattedBlocks = test.getReadingsByMeterNoAndType(meterNumber, readingType);
			printFormattedBlocks(formattedBlocks);
			
			//Meter change process tests
//			objects = test.meterAddNotification();
			
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
//			printErrorObjects(objects);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	private ErrorObject[] initiateMeterReadByMeterNo() throws RemoteException {
		String[] meterNumbers = new String[]{"0300031", "10620108"};
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
		
		Meter meter = buildMeter("Cart #1 MCT-410cL (0300031)", "Cart #1 MCT-410cL (0320819)", "1234567", "1234567", 
				"1234567", "SUBSTATION 1", "320819");
		
		objects = instance.meterAddNotification(new Meter[]{meter});
		return objects;
	}

	private void loadInstance(String endpointURL) throws AxisFault, MalformedURLException {
		instance = new MR_ServerSoap_BindingStub(new URL(endpointURL), new Service());
		
		YukonMultispeakMsgHeader msgHeader =new YukonMultispeakMsgHeader();
//		msgHeader.setCompany("Cannon MSP1");
		msgHeader.setCompany("Cannon");
		
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
}