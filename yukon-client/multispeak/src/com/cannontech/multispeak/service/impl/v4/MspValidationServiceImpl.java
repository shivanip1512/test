package com.cannontech.multispeak.service.impl.v4;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.v4.MspValidationService;

public class MspValidationServiceImpl implements MspValidationService {
    @Autowired private MspMeterDao mspMeterDao;
    
    private final Logger log = YukonLogManager.getLogger(MspValidationService.class);

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

}
