package com.cannontech.device.range.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.device.range.IntegerRange;

public class DlcAddressRangeServiceImpl implements DlcAddressRangeService {

    private static final IntegerRange DEFAULT_RANGE = new IntegerRange(0, Integer.MAX_VALUE);
    
    private PaoDefinitionDao paoDefinitionDao;

    private static final Logger logger = YukonLogManager.getLogger(DlcAddressRangeServiceImpl.class);

    @Override
    public IntegerRange getAddressRangeForDevice(PaoType paoType) {
        return getAddressRange(paoType, PaoTag.DLC_ADDRESS_RANGE);
    }

    @Override
    public IntegerRange getEnforcedAddressRangeForDevice(PaoType paoType) {
        return getAddressRange(paoType, PaoTag.DLC_ADDRESS_RANGE_ENFORCE);
    }

    @Override
    public boolean isValidAddress(PaoType paoType, int address) {
        IntegerRange range = getEnforcedAddressRangeForDevice(paoType);
        return range.isWithinRange(address);
    }

    /**
     * Helper method to get IntegerRange for PaoType and PaoTag.
     * Valid PaoTags are DLC_ADDRESS_RANGE and DLC_ADDRESS_RANGE_ENFORCE.
     * @param paoType
     * @return
     */
	private IntegerRange getAddressRange(PaoType paoType, PaoTag paoTag) {
		if(!paoDefinitionDao.isTagSupported(paoType, paoTag)) {
            logger.debug("No Range found for " + paoType + ". Using Default Range");
            return DEFAULT_RANGE;
        }
        
        String rangeString = paoDefinitionDao.getValueForTagString(paoType, paoTag);
        IntegerRange range = new IntegerRange(rangeString);
        return range;
	}
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
}