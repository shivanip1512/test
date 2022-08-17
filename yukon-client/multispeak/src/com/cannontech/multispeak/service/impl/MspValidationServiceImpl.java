package com.cannontech.multispeak.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.msp.beans.v3.ControlEventType;
import com.cannontech.msp.beans.v3.ErrorObject;
import com.cannontech.msp.beans.v3.LoadManagementEvent;
import com.cannontech.msp.beans.v3.ObjectRef;
import com.cannontech.msp.beans.v3.ScadaAnalog;
import com.cannontech.msp.beans.v3.Strategy;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.dao.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v3.MspValidationService;

public class MspValidationServiceImpl implements MspValidationService {
    @Autowired private MspMeterDao mspMeterDao;
    @Autowired private MspObjectDao mspObjectDao;
    
    private final Logger log = YukonLogManager.getLogger(MspValidationService.class);

    @Override
    public FormattedBlockProcessingService<Block> getProcessingServiceByReadingType(Map<String, FormattedBlockProcessingService<Block>> readingTypesMap,
            String readingType) throws MultispeakWebServiceException {
        FormattedBlockProcessingService<Block> formattedBlock = readingTypesMap.get(readingType);
        if( formattedBlock == null) {
            String message = readingType + " is NOT a supported ReadingType.";
            log.error(message);
            throw new MultispeakWebServiceException(message);
        }
        return formattedBlock;
    }

    @Override
    public YukonMeter isYukonMeterNumber(String meterNumber) throws MultispeakWebServiceException {
        YukonMeter yukonMeter;
        if( StringUtils.isBlank(meterNumber)) {
            String errorMessage = "Meter Number is invalid.  Meter number is blank or null";
            log.error(errorMessage);
            throw new MultispeakWebServiceException(errorMessage);
        }
        
        try {
            yukonMeter = mspMeterDao.getMeterForMeterNumber(meterNumber);
        }catch (NotFoundException e){
            String errorMessage = "Meter Number: (" + meterNumber + ") - Was NOT found in Yukon.";
            log.error(errorMessage);
            throw new MultispeakWebServiceException(errorMessage);
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
		if( controlEventType == null || StringUtils.isBlank(controlEventType.value())) {
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
                List<ObjectRef> substations = strategy.getApplicationPointList().getApplicationPoint();
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
