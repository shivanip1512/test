package com.cannontech.common.device.groups.util;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.ObjectMapper;

public class YukonPaoToIdMapper implements ObjectMapper<YukonPao, Integer> {
    public YukonPaoToIdMapper() {
    }
    
    @Override
    public Integer map(YukonPao from) {
        return from.getPaoIdentifier().getPaoId();
    }
}