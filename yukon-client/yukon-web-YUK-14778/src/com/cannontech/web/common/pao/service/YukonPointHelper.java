package com.cannontech.web.common.pao.service;

import java.util.List;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.pao.YukonPao;

public interface YukonPointHelper {

    /**
     * Builds a list of {@link LiteYukonPoint} objects for a device and sorts them.
     * @param pao the yukon PAO whose points are to be sorted.
     * @param sorting {sort : field on which sorting is to be done,direction:direction for sorting}.
     * @param accessor the accessor is required to getMessage for internalization.
     * @return sorted list of yukon points based on field.
     */
    public List<LiteYukonPoint> getYukonPoints(YukonPao pao, SortingParameters sorting, MessageSourceAccessor accessor);

    /**
     * Builds a list of {@link LiteYukonPoint} objects for a device
     * @param pao the yukon PAO whose points are to be sorted.
     * @return the List of yukon points
     */
    public List<LiteYukonPoint> getYukonPoints(YukonPao pao);

}