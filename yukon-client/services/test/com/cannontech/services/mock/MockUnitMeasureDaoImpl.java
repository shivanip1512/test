package com.cannontech.services.mock;

import java.util.List;
import java.util.Map;

import com.cannontech.core.dao.impl.UnitMeasureDaoImpl;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MockUnitMeasureDaoImpl extends UnitMeasureDaoImpl {
    
    public static final LiteUnitMeasure kW = new LiteUnitMeasure(0, "kW", 0, "kW");
    public static final LiteUnitMeasure kWH = new LiteUnitMeasure(1, "kWH", 0, "kWH");
    public static final LiteUnitMeasure kVAr = new LiteUnitMeasure(3, "kVAr", 0, "kVAr");
    
    private static final List<LiteUnitMeasure>unitOfMeasures = Lists.newArrayList(kW, kWH, kVAr);
    
    private Map<Integer, LiteUnitMeasure> unitOfMeasureIdToUnitOfMeasure;
    {
        unitOfMeasureIdToUnitOfMeasure = 
            Maps.uniqueIndex(unitOfMeasures, new Function<LiteUnitMeasure, Integer>() {
                @Override
                public Integer apply(LiteUnitMeasure unitMeasure) {
                    return unitMeasure.getUomID();
                }
            });
    }
    
    @Override
    public LiteUnitMeasure getLiteUnitMeasure(int unitOfMeasureId) {
        return unitOfMeasureIdToUnitOfMeasure.get(unitOfMeasureId);
    }
}