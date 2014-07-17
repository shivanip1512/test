package com.cannontech.sensus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.sms.messages.rx.AndorianMessage;
import com.sms.messages.rx.AppMessageType1;
import com.sms.messages.rx.AppMessageType22;
import com.sms.messages.rx.AppMessageType5;

public class SensusCvsFileProcessor extends SensusMessageHandlerBase {
    private Logger log = Logger.getLogger(SensusCvsFileProcessor.class);
    
    @Override
    protected void processStatusMessage(AppMessageType22 message) {
        String csvStr = dataMessageToCSVString(message) + ", " + messageToCsv(message);
        log.info(csvStr);
        writeCvsStringToFile("0x22", csvStr, getFileHeaders(message.getAppCode()));
    }

    @Override
    protected void processBindingMessage(AppMessageType5 message) {
        String iconSerialNumber = message.getCustomerMeterNumber();
        String csvStr = dataMessageToCSVString(message);
        csvStr += ", " + iconSerialNumber + 
        ", " + message.getLatitude() + 
        ", " + message.getLongitude() +
    	", " + AndorianMessage.cleanHex(message.getRawMessage());
        
        log.info(csvStr);
        writeCvsStringToFile("0x05", csvStr, getFileHeaders(message.getAppCode()));
    }

    @Override
    protected void processSetupMessage(AppMessageType1 message) {
        // int deviceType = message.getDeviceType();	// deviceType == 12 is the FCI!  Using only SN ranges to do this though.

        int txOpMode = message.getTransmitOperationalMode();
        int supTxMult = message.getSupervisoryTransmitMultiple();

        String csvStr = dataMessageToCSVString(message) + 
        ", \"" + getTxMode(txOpMode) + "\"" + 
      	", \"" + getSuprRate(supTxMult) + "\"" +
    	", " + AndorianMessage.cleanHex(message.getRawMessage());
  
        log.info(csvStr);
        writeCvsStringToFile("0x01", csvStr, getFileHeaders(message.getAppCode()));        
    }

	private void writeCvsStringToFile(String file, String text, String header) {
        String fileName = getLogFilePath() + getNameOfAppInstance() + "_AppCode_" + file + ".log";
        File tempFile = new File(fileName);
        
        if(header != null && !tempFile.exists()) {
            try {
                FileWriter fw = new FileWriter(tempFile, true);
                fw.write(header);
                fw.write("\r\n");
                fw.close();
            } catch (IOException e) {
            	log.info("Unable to write.",e);
            }        
        }
        
        try {
            FileWriter fw = new FileWriter(tempFile, true);
            fw.write(text);
            fw.write("\r\n");
            fw.close();
        } catch (IOException e) {
        	log.info("Unable to write.",e);
        }        
	}
	
	private String messageToCsv(AppMessageType22 message) {
	        String csvStr = message.isStatusNo60HzOrUnderLineCurrent() + 
	        ", " + message.isStatusLatchedFault() +
	        ", " + message.isStatusEventTransBit() +
	        ", " + message.getCurrentDeviceTemperature() + 
	        ", " + message.getCurrentBatteryVoltage() + 
	        ", " + message.getLastTxTemperature() + 
	        ", " + message.getLastTxBatteryVoltage() +
	        ", " + eventToCSV(message.getLastEvent()) +
	        ", " + eventToCSV(message.getFirstHistoricalEvent()) +
	        ", " + eventToCSV(message.getSecondHistoricalEvent()) +
	        ", " + eventToCSV(message.getThirdHistoricalEvent()) +
	        ", " + AndorianMessage.cleanHex(message.getRawMessage());
	        
	        return csvStr;
	}
	
    public String eventToCSV(AppMessageType22.Event evt) {
        String cvsStr = evt.isPopulated() + 
        ", " + evt.isRestoreAfterFault() +
        ", " + evt.isNo60HzDetectedFollowingFault() +
        ", " + evt.isFaultDetected() + 
        ", " + evt.getSecondsSinceEvent(); 
        return cvsStr;
    }	
}
