package com.cannontech.sensus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.amdswireless.messages.rx.AppMessageType1;
import com.amdswireless.messages.rx.AppMessageType22;
import com.amdswireless.messages.rx.AppMessageType5;
import com.amdswireless.messages.rx.DataMessage;

public class CvsFileProcessor extends SensusMessageHandlerBase {
    private Logger log = Logger.getLogger(CvsFileProcessor.class);
    
    protected void processStatusMessage(AppMessageType22 message) {
        String csvStr = dataMessageToCSVString(message) + ", " + message.toCSV();
        log.info(csvStr);
        writeCvsStringToFile("0x22", csvStr, getFileHeaders(message.getAppCode()));
    }

    protected void processBindingMessage(AppMessageType5 message) {
        String iconSerialNumber = message.getIconSerialNumber();
        String csvStr = dataMessageToCSVString(message);
        csvStr += ", " + iconSerialNumber + 
        ", " + message.getLatitude() + 
        ", " + message.getLongitude() +
    	", " + DataMessage.cleanHex(message.getRawMessage());
        
        log.info(csvStr);
        writeCvsStringToFile("0x05", csvStr, getFileHeaders(message.getAppCode()));
    }

    protected void processSetupMessage(AppMessageType1 message) {
        // int deviceType = message.getDeviceType();	// deviceType == 12 is the FCI!  Using only SN ranges to do this though.

        int txOpMode = message.getTransmitOperationalMode();
        int supTxMult = message.getSupervisoryTransmitMultiple();

        String csvStr = dataMessageToCSVString(message) + 
        ", \"" + getTxMode(txOpMode) + "\"" + 
      	", \"" + getSuprRate(supTxMult) + "\"" +
    	", " + DataMessage.cleanHex(message.getRawMessage());
  
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
}
