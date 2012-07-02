package com.cannontech.multispeak.service.impl;

import java.rmi.RemoteException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.deploy.service.ControlEventType;
import com.cannontech.multispeak.deploy.service.ErrorObject;
import com.cannontech.multispeak.deploy.service.LoadManagementEvent;
import com.cannontech.multispeak.deploy.service.ObjectRef;
import com.cannontech.multispeak.deploy.service.ScadaAnalog;
import com.cannontech.multispeak.deploy.service.Strategy;
import com.cannontech.multispeak.service.MspValidationService;

public class MspValidationServiceImpl implements MspValidationService {
    @Autowired private MeterDao meterDao;
    @Autowired private MspObjectDao mspObjectDao;

    @Override
    public FormattedBlockProcessingService<Block> getProcessingServiceByReadingType(Map<String, FormattedBlockProcessingService<Block>> readingTypesMap,
            String readingType) throws RemoteException {
        FormattedBlockProcessingService<Block> formattedBlock = readingTypesMap.get(readingType);
        if( formattedBlock == null) {
            String message = readingType + " is NOT a supported ReadingType.";
            CTILogger.error(message);
            throw new RemoteException(message);
        }
        return formattedBlock;
    }

    @Override
    public YukonMeter isYukonMeterNumber(String meterNumber) throws RemoteException {
        YukonMeter yukonMeter;
        if( StringUtils.isBlank(meterNumber))
            throw new RemoteException("Meter Number is invalid.  Meter number is blank or null");

        try {
            yukonMeter = meterDao.getYukonMeterForMeterNumber(meterNumber);
        }catch (NotFoundException e){
            CTILogger.info("Meter Number: (" + meterNumber + ") - Was NOT found in Yukon.");
            throw new RemoteException( "Meter Number: (" + meterNumber + ") - Was NOT found in Yukon.");
        }
        return yukonMeter;
    }
    
    @Override
    public ErrorObject isValidScadaAnalog(ScadaAnalog scadaAnalog) {
    	ErrorObject errorObject = null;
    	if (StringUtils.isBlank(scadaAnalog.getObjectID())) {
            errorObject = mspObjectDao.getErrorObject(null, 
                    "ScadaAnalog objectId was not populated.",
                    "ScadaAnalog", "isValidScadaAnalog", null);
    	} else if (scadaAnalog.getValue() == null) {
            errorObject = mspObjectDao.getErrorObject(scadaAnalog.getObjectID(), 
                    "ScadaAnalog Value was not populated.",
                    "ScadaAnalog", "isValidScadaAnalog", null);
    	} else if (scadaAnalog.getQuality() == null) {
            errorObject = mspObjectDao.getErrorObject(scadaAnalog.getObjectID(), 
                    "ScadaAnalog Quality was not populated.",
                    "ScadaAnalog", "isValidScadaAnalog", null);
    	} else if (scadaAnalog.getUnit() == null) {
            errorObject = mspObjectDao.getErrorObject(scadaAnalog.getObjectID(), 
                    "ScadaAnalog Unit was not populated.",
                    "ScadaAnalog", "isValidScadaAnalog", null);
    	}
    	return errorObject;
    }
    
    @Override
    public ErrorObject isValidLoadManagementEvent(LoadManagementEvent loadManagementEvent) {
    	ErrorObject errorObject = null;
    	
	    ControlEventType controlEventType = loadManagementEvent.getControlEventType();
	    //is control type is defined?
		if( controlEventType == null || StringUtils.isBlank(controlEventType.getValue())) {
			errorObject = mspObjectDao.getErrorObject(loadManagementEvent.getObjectID(), 
					"ControlEventType not specified, event not processed.",
					"LoadManagementEvent", "isValidLoadManagementEvent", null);
		} else {
			//is strategy name defined?
			Strategy strategy = loadManagementEvent.getStrategy();
			if (strategy == null) {
				errorObject = mspObjectDao.getErrorObject(loadManagementEvent.getObjectID(), 
						"Strategy is null, event not processed.",
						"LoadManagementEvent", "isValidLoadManagementEvent", null);
			} else {
				//are substation values for the strategy defined?
			    ObjectRef[] substations = strategy.getApplicationPointList();
			    if (substations == null) {
			    	errorObject = mspObjectDao.getErrorObject(loadManagementEvent.getObjectID(), 
							"ApplicationPointList is null (invalid Substation value), event not processed.",
							"LoadManagementEvent", "isValidLoadManagementEvent", null);
			    } else {
			    	//are the substation name values for the strategy defined? 
			    	for (ObjectRef substationRef : substations) {
						if(StringUtils.isBlank(substationRef.getName())) {
							errorObject = mspObjectDao.getErrorObject(loadManagementEvent.getObjectID(), 
									"ApplicationPointList (" + substationRef.toString() + ") .Name is null, event not processed.",
									"LoadManagementEvent", "isValidLoadManagementEvent", null);
						}
			    	}
			    }
			}
		}
		return errorObject;
    }

    @Override
    public ErrorObject validateResponseURL(String responseURL, String nounType, String method) {
        ErrorObject errorObjectRetVal = null;

        if (StringUtils.isBlank(responseURL)) {
            errorObjectRetVal = mspObjectDao.getErrorObject("n/a", "responseURL is blank", nounType, method,
                                                 null);
        }

        return errorObjectRetVal;
    }
}
