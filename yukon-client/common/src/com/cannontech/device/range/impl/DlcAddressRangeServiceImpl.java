package com.cannontech.device.range.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.device.range.DlcAddressRangeService;
import com.google.common.collect.ImmutableList;

public class DlcAddressRangeServiceImpl implements DlcAddressRangeService {

    private static final Range<Integer> DEFAULT_RANGE = Range.between(0, Integer.MAX_VALUE);
    private static final Logger logger = YukonLogManager.getLogger(DlcAddressRangeServiceImpl.class);
    
    private @Autowired PaoDefinitionDao paoDefinitionDao;
    
    @Override
    public List<Range<Integer>> getAddressRangeForDevice(PaoType paoType) {
        return getAddressRange(paoType, PaoTag.DLC_ADDRESS_RANGE);
    }
    
    @Override
    public List<Range<Integer>> getEnforcedAddressRangeForDevice(PaoType paoType) {
        return getAddressRange(paoType, PaoTag.DLC_ADDRESS_RANGE_ENFORCE);
    }
    
    @Override
    public boolean isValidEnforcedAddress(PaoType paoType, int address) {
        
        List<Range<Integer>> ranges = getEnforcedAddressRangeForDevice(paoType);
        for (Range<Integer> range : ranges) {
            if (! range.contains(address)) return false;
        }
        
        return true;
    }
    
    @Override
    public boolean isValidNonEnforcedAddress(PaoType paoType, int address) {
        
        List<Range<Integer>> ranges = getAddressRangeForDevice(paoType);
        for (Range<Integer> range : ranges) {
            if (! range.contains(address)) return false;
        }
        
        return true;
    }
    
    /**
     * Helper method to get int ranges for PaoType and PaoTag.
     * Valid PaoTags are DLC_ADDRESS_RANGE and DLC_ADDRESS_RANGE_ENFORCE.
     */
    private List<Range<Integer>> getAddressRange(PaoType paoType, PaoTag paoTag) {
        
        if (!paoDefinitionDao.isTagSupported(paoType, paoTag)) {
            logger.debug("No Range found for " + paoType + ". Using Default Range");
            return ImmutableList.of(DEFAULT_RANGE);
        }
        
        String rangeString = paoDefinitionDao.getValueForTagString(paoType, paoTag);
        String[] intervals = rangeString.split(",");
        
        List<Range<Integer>> ranges = new ArrayList<>();
        
        for (String interval : intervals) {
            String[] bounds = interval.split("-");
            int lower = Integer.parseInt(bounds[0].trim());
            int upper = Integer.parseInt(bounds[1].trim());
            ranges.add(Range.between(lower, upper));
        }
        
        return ranges;
    }
    
    @Override
    public String rangeString(PaoType type) {
        return rangeString(getAddressRangeForDevice(type));
    }
    
    @Override
    public String rangeString(List<Range<Integer>> ranges) {
        
        List<String> rangeStrings = new ArrayList<>();
        for (Range<Integer> range : ranges) {
            String rangeString = "[" + range.getMinimum() + " - " + range.getMaximum() + "]";
            rangeStrings.add(rangeString);
        }
        
        return StringUtils.join(rangeStrings, ", ");
    }
    
}