package com.cannontech.web.common.pao.service;

import java.util.List;

import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.YukonPao;

public interface YukonPointHelper {

    /** Builds a list of {@link LiteYukonPoint} objects for a device and sorts them. */
    public List<LiteYukonPoint> getYukonPoints(YukonPao pao, SortingParameters sorting);
    
    /** Builds a list of {@link LiteYukonPoint} objects for a device. */
    public List<LiteYukonPoint> getYukonPoints(YukonPao pao);
}