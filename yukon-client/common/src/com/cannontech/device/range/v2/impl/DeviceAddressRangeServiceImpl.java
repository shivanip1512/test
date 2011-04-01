package com.cannontech.device.range.v2.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.device.range.v2.DeviceAddressRangeService;
import com.cannontech.device.range.v2.LongRange;

public class DeviceAddressRangeServiceImpl implements DeviceAddressRangeService {

    private static final LongRange DEFAULT_RANGE = new LongRange(Long.MIN_VALUE, Long.MAX_VALUE);
    
    private PaoDefinitionDao paoDefinitionDao;

    
    @Override
    public LongRange getAddressRangeForDevice(PaoType paoType) {
       
       if(!paoDefinitionDao.isTagSupported(paoType, PaoTag.PLC_ADDRESS_RANGE)) {
           CTILogger.debug("No Range found for " + paoType + ". Using Default Range");
           return DEFAULT_RANGE;
       }
       
       String rangeString = paoDefinitionDao.getValueForTagString(paoType, PaoTag.PLC_ADDRESS_RANGE);
       LongRange range = new LongRange(rangeString);
       return range;
    }

    @Override
    public boolean isValidAddress(PaoType paoType, long address) {
        LongRange range = getAddressRangeForDevice(paoType);
        return range.isWithinRange(address);
    }

    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    
}
