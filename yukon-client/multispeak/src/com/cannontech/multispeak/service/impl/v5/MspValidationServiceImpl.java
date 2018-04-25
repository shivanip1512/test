package com.cannontech.multispeak.service.impl.v5;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.commontypes.ObjectRef;
import com.cannontech.msp.beans.v5.enumerations.ControlEventKind;
import com.cannontech.msp.beans.v5.enumerations.ReplyCodeCategoryKind;
import com.cannontech.msp.beans.v5.multispeak.LoadManagementEvent;
import com.cannontech.msp.beans.v5.multispeak.SCADAAnalog;
import com.cannontech.msp.beans.v5.multispeak.Strategy;
import com.cannontech.multispeak.block.v5.Block;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.dao.v5.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v5.MspValidationService;

public class MspValidationServiceImpl implements MspValidationService {

    @Autowired @Qualifier("mspMeterDaoV5") private MspMeterDao mspMeterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakFuncs multispeakFuncs;
    
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
    public ErrorObject isValidScadaAnalog(SCADAAnalog scadaAnalog) {
        ErrorObject errorObject = null;

        if (StringUtils.isBlank(scadaAnalog.getSCADAPointID().getObjectGUID())) {
            errorObject =
                mspObjectDao.getErrorObject(scadaAnalog.getObjectGUID(), "ScadaAnalog SCADAPointID was not populated.",
                    "ScadaAnalog", "isValidScadaAnalog", null);
        } else if (scadaAnalog.getValue() == null) {
            errorObject =
                mspObjectDao.getErrorObject(scadaAnalog.getObjectGUID(), "ScadaAnalog Value was not populated.",
                    "ScadaAnalog", "isValidScadaAnalog", null);
        } else if (scadaAnalog.getQuality() == null) {
            errorObject =
                mspObjectDao.getErrorObject(scadaAnalog.getObjectGUID(), "ScadaAnalog Quality was not populated.",
                    "ScadaAnalog", "isValidScadaAnalog", null);
        } else if (scadaAnalog.getValue().getUnits() == null) {
            errorObject =
                mspObjectDao.getErrorObject(scadaAnalog.getObjectGUID(), "ScadaAnalog Unit was not populated.",
                    "ScadaAnalog", "isValidScadaAnalog", null);
        }
        return errorObject;
    }

    @Override
    public ErrorObject isValidLoadManagementEvent(LoadManagementEvent loadManagementEvent) {
        ErrorObject errorObject = null;

        ControlEventKind controlEventKind = loadManagementEvent.getControlEventType();
        // is control type is defined?
        if (controlEventKind == null || StringUtils.isBlank(controlEventKind.value())) {
            errorObject =
                mspObjectDao.getErrorObject(loadManagementEvent.getReferableID(),
                    "ControlEventType not specified, event not processed.", "LoadManagementEvent",
                    "isValidLoadManagementEvent", null);
        } else {
            // is strategy name defined?
            Strategy strategy =
                loadManagementEvent.getStrategyChoices() != null
                    ? loadManagementEvent.getStrategyChoices().getStrategy() : null;
            if (strategy == null) {
                errorObject =
                    mspObjectDao.getErrorObject(loadManagementEvent.getReferableID(),
                        "Strategy is null, event not processed.", "LoadManagementEvent", "isValidLoadManagementEvent",
                        null);
            } else {
                // are substation values for the strategy defined?
                List<ObjectRef> substations =
                    strategy.getApplicationPoints() != null ? strategy.getApplicationPoints().getApplicationPoint()
                        : null;
                if (substations == null) {
                    errorObject =
                        mspObjectDao.getErrorObject(loadManagementEvent.getReferableID(),
                            "ApplicationPointList is null (invalid Substation value), event not processed.",
                            "LoadManagementEvent", "isValidLoadManagementEvent", null);
                } else {
                    // are the substation name values for the strategy defined?
                    for (ObjectRef substationRef : substations) {
                        if (StringUtils.isBlank(substationRef.getPrimaryIdentifierValue())) {
                            errorObject =
                                mspObjectDao.getErrorObject(loadManagementEvent.getReferableID(),
                                    "ApplicationPointList (" + substationRef.toString()
                                        + ") .Name is null, event not processed.", "LoadManagementEvent",
                                    "isValidLoadManagementEvent", null);
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
            errorObjectRetVal = mspObjectDao.getErrorObject("n/a", "responseURL is blank", nounType, method, null);
        }

        return errorObjectRetVal;
    }

    @Override
    public boolean isValidMeterCount(List<MeterID> meterIDs, int maxRecords) throws MultispeakWebServiceException {

        if (CollectionUtils.isNotEmpty(meterIDs)) {
            if (meterIDs.size() > maxRecords) {
                multispeakFuncs.updateResultIdentifierInResponseHeader(ReplyCodeCategoryKind.VALUE_4.value());
                return false;
            }
            return true;
        } else {
            String errorMessage = "MeterID Object is not present in request";
            log.error(errorMessage);
            throw new MultispeakWebServiceException(errorMessage);
        }
    }
    
}
