package com.cannontech.device.range.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.device.range.IntegerRange;
import com.cannontech.device.range.PlcAddressRangeService;

public class PlcAddressRangeServiceImpl implements PlcAddressRangeService {

    private static final IntegerRange DEFAULT_RANGE = new IntegerRange(0, Integer.MAX_VALUE);
    
    private PaoDefinitionDao paoDefinitionDao;

    private static final Logger logger = YukonLogManager.getLogger(PlcAddressRangeServiceImpl.class);
    
    @Override
    public IntegerRange getAddressRangeForDevice(PaoType paoType) {
       
       if(!paoDefinitionDao.isTagSupported(paoType, PaoTag.PLC_ADDRESS_RANGE)) {
           logger.debug("No Range found for " + paoType + ". Using Default Range");
           return DEFAULT_RANGE;
       }
       
       String rangeString = paoDefinitionDao.getValueForTagString(paoType, PaoTag.PLC_ADDRESS_RANGE);
       IntegerRange range = new IntegerRange(rangeString);
       return range;
    }

    @Override
    public boolean isValidAddress(PaoType paoType, int address) {
        IntegerRange range = getAddressRangeForDevice(paoType);
        return range.isWithinRange(address);
    }

    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    
}
