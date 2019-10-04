package com.cannontech.common.pao.attribute.service;

import static com.cannontech.amr.meter.dao.MockMeterDaoImpl.*;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.*;
import static com.cannontech.core.dao.MockUnitMeasureDaoImpl.*;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class MockAttributeServiceImpl extends AttributeServiceImpl {
    public static final LitePoint USAGE_POINT_ONE = new LitePoint(0, "kWh", 0, 0, 0, 0, 0, kWH.getUomID());
    public static final LitePoint PEAK_DEMAND_POINT_ONE = new LitePoint(0, "Peak kW", 0, 0, 0, 0, 0, kW.getUomID());
    public static final LitePoint PEAK_KVAR_POINT_ONE = new LitePoint(0, "Peak kVAr", 0, 0, 0, 0, 0, kVAr.getUomID());
    
    private static final Map<YukonPao, Map<Attribute, LitePoint>> paoToAttributeToPoints = new HashMap<>();
    static {
        Map<Attribute, LitePoint> meterOneAttributeToPoints = new HashMap<>();
        paoToAttributeToPoints.put(METER_MCT410FL, meterOneAttributeToPoints);
        meterOneAttributeToPoints.put(USAGE, USAGE_POINT_ONE);
        meterOneAttributeToPoints.put(PEAK_DEMAND, PEAK_DEMAND_POINT_ONE);
        meterOneAttributeToPoints.put(PEAK_KVAR, PEAK_KVAR_POINT_ONE);
    }
    
    @Override
    public LitePoint getPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        Map<Attribute, LitePoint> attributeToPoints = paoToAttributeToPoints.get(pao);
        return attributeToPoints != null ? attributeToPoints.get(attribute) : null; 
    }
    
    @Override
    public LitePoint createAndFindPointForAttribute(YukonPao pao, BuiltInAttribute attribute) {
        return getPointForAttribute(pao, attribute);
    }
    
    @Override
    public LitePoint findPointForAttribute(YukonPao pao, Attribute attribute) throws IllegalUseOfAttribute {
        return getPointForAttribute(pao, attribute);
    }

    @Override
    public BiMap<PaoIdentifier, LitePoint> getPoints(Iterable<? extends YukonPao> paos, BuiltInAttribute attribute) {
        final BiMap<PaoIdentifier, LitePoint> deviceToPoint = HashBiMap.create();

        paos.forEach(pao -> {
            deviceToPoint.put(pao.getPaoIdentifier(), new LitePoint(pao.getPaoIdentifier().getPaoId()));
        });
        return deviceToPoint;
    }
}