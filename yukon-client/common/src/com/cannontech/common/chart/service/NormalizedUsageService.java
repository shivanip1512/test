package com.cannontech.common.chart.service;

import java.util.List;

import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.core.dynamic.PointValueHolder;

public interface NormalizedUsageService {
    List<PointValueHolder> getNormalizedUsage(List<PointValueHolder> unNormalizedPoints,
                                              Attribute usageAttribute);
}
