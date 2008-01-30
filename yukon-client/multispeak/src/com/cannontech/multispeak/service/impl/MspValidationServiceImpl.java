package com.cannontech.multispeak.service.impl;

import java.rmi.RemoteException;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.multispeak.block.Block;
import com.cannontech.multispeak.block.FormattedBlockService;
import com.cannontech.multispeak.deploy.service.MR_CBSoap_PortType;
import com.cannontech.multispeak.service.MspValidationService;

public class MspValidationServiceImpl implements MspValidationService {

    public MR_CBSoap_PortType mr_cb;
    public MeterDao meterDao;
    
    @Required
    public void setMr_cb(MR_CBSoap_PortType mr_cb) {
        this.mr_cb = mr_cb;
    }
    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Override
    public FormattedBlockService<Block> isValidBlockReadingType(Map<String, FormattedBlockService> readingTypesMap,
            String readingType) throws RemoteException {
        FormattedBlockService<Block> formattedBlock = readingTypesMap.get(readingType);
        if( formattedBlock == null) {
            String message = readingType + " is NOT a supported ReadingType.";
            CTILogger.error(message);
            throw new RemoteException(message);
        }
        return formattedBlock;
    }

    @Override
    public Meter isYukonMeterNumber(String meterNumber) throws RemoteException {
        Meter meter;
        if( StringUtils.isBlank(meterNumber))
            throw new RemoteException("Meter Number is invalid.  Meter number is blank or null");

        try {
            meter = meterDao.getForMeterNumber(meterNumber);
        }catch (NotFoundException e){
            CTILogger.info("MeterNumber (" + meterNumber + ") NOT found, returning false." );
            throw new RemoteException("MeterNumber (" + meterNumber + ") NOT found, returning false." );
        }
        return meter;
    }
}
