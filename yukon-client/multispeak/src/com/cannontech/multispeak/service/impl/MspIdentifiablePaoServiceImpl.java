package com.cannontech.multispeak.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.multispeak.service.MspIdentifiablePaoService;

public class MspIdentifiablePaoServiceImpl implements MspIdentifiablePaoService {

    private MeterDao meterDao;
    private PaoDefinitionDao paoDefinitionDao;
    private PaoDao paoDao;
    
    @Override
    public String getObjectId(YukonPao paoIdentifier) {
        
        String returnString;
        
        if (paoIdentifier == null) {
            returnString = null;
        } else if (paoDefinitionDao.isTagSupported(paoIdentifier.getPaoIdentifier().getPaoType(), PaoTag.USES_METER_NUMBER_FOR_MSP)) {
            SimpleMeter meter = meterDao.getSimpleMeterForId(paoIdentifier.getPaoIdentifier().getPaoId());
            returnString = meter.getMeterNumber();
        } else {
            returnString = paoDao.getYukonPAOName(paoIdentifier.getPaoIdentifier().getPaoId());
        }
        
        return returnString;
    }
    
    @Autowired
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
}