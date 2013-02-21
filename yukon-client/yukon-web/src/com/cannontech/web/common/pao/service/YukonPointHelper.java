package com.cannontech.web.common.pao.service;

import java.util.List;

import com.cannontech.common.pao.YukonPao;

public interface YukonPointHelper {

    /**
     * Builds a list of {@link LiteYukonPoint} objects for a device and sorts them.
     * 
     * @param orderBy An optional parameter, will default to the point name.
     * @param descending An optional parameter, will default to the descending.
     */
    public List<LiteYukonPoint> getYukonPoints(YukonPao pao, String orderBy, Boolean descending);
    
    public List<LiteYukonPoint> getYukonPoints(YukonPao pao);
}