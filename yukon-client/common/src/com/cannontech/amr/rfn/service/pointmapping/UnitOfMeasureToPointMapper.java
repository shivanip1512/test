package com.cannontech.amr.rfn.service.pointmapping;

import com.cannontech.amr.rfn.message.read.ChannelData;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.google.common.collect.Multimap;

public interface UnitOfMeasureToPointMapper {
    
    public <T extends ChannelData> PointValueHandler findMatch(YukonPao pao, T channelData);

    Multimap<PaoType, PointMapper> getPointMapper();
}