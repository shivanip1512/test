package com.cannontech.common.pao.attribute.service;

import static com.cannontech.core.dao.MockUnitMeasureDaoImpl.*;
import static com.cannontech.amr.meter.dao.MockMeterDaoImpl.*;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.*;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.database.data.lite.LitePoint;

public class MockAttributeServiceImpl extends AttributeServiceImpl {
    public static final LitePoint USAGE_POINT_ONE = new LitePoint(0, "kWh", 0, 0, 0, 0, 0, kWH.getUomID());
    public static final LitePoint PEAK_DEMAND_POINT_ONE = new LitePoint(0, "Peak kW", 0, 0, 0, 0, 0, kW.getUomID());
    public static final LitePoint PEAK_KVAR_POINT_ONE = new LitePoint(0, "Peak kVAr", 0, 0, 0, 0, 0, kVAr.getUomID());
    
    private Map<YukonPao, Map<Attribute, LitePoint>> paoToAttributeToPoints = new HashMap<>();
    {
        Map<Attribute, LitePoint> meterOneAttributeToPoints = new HashMap<>();
        paoToAttributeToPoints.put(METER_ONE, meterOneAttributeToPoints);
        meterOneAttributeToPoints.put(USAGE, USAGE_POINT_ONE);
        meterOneAttributeToPoints.put(PEAK_DEMAND, PEAK_DEMAND_POINT_ONE);
        meterOneAttributeToPoints.put(PEAK_KVAR, PEAK_KVAR_POINT_ONE);
    }
    
    @Override
    public LitePoint getPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        Map<Attribute, LitePoint> attributeToPoints = paoToAttributeToPoints.get(pao);
        return attributeToPoints != null ? attributeToPoints.get(attribute) : null; 
    }
}