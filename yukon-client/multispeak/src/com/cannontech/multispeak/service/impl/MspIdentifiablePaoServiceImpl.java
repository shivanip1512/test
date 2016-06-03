package com.cannontech.multispeak.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.multispeak.service.MspIdentifiablePaoService;
import com.cannontech.yukon.IDatabaseCache;

public class MspIdentifiablePaoServiceImpl implements MspIdentifiablePaoService {

    @Autowired private IDatabaseCache databaseCache;
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    
    @Override
    public String getObjectId(YukonPao paoIdentifier) {
        
        String returnString;
        
        if (paoIdentifier == null) {
            returnString = null;
        } else if (paoDefinitionDao.isTagSupported(paoIdentifier.getPaoIdentifier().getPaoType(), PaoTag.USES_METER_NUMBER_FOR_MSP)) {
            SimpleMeter meter = meterDao.getSimpleMeterForId(paoIdentifier.getPaoIdentifier().getPaoId());
            returnString = meter.getMeterNumber();
        } else {
            returnString = databaseCache.getAllPaosMap().get(paoIdentifier.getPaoIdentifier().getPaoId()).getPaoName();
        }
        
        return returnString;
    }
}