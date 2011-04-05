package com.cannontech.device.range.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.device.range.PlcAddressRangeService;
import com.cannontech.device.range.IntegerRange;

public class PlcAddressRangeServiceImpl implements PlcAddressRangeService {

    private static final IntegerRange DEFAULT_RANGE = new IntegerRange(0, Integer.MAX_VALUE);
    
    private PaoDefinitionDao paoDefinitionDao;

    
    @Override
    public IntegerRange getAddressRangeForDevice(PaoType paoType) {
       
       if(!paoDefinitionDao.isTagSupported(paoType, PaoTag.PLC_ADDRESS_RANGE)) {
           CTILogger.debug("No Range found for " + paoType + ". Using Default Range");
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
