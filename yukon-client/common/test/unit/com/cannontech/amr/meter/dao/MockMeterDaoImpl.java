package com.cannontech.amr.meter.dao;

import static com.cannontech.common.pao.PaoType.*;

import java.util.List;
import java.util.Map;

import com.cannontech.amr.meter.dao.impl.MeterDaoImpl;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.PaoIdentifier;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MockMeterDaoImpl extends MeterDaoImpl {
    public static final Meter METER_ONE = new Meter();
    {
        METER_ONE.setAddress("Address A");
        METER_ONE.setDisabled(false);
        METER_ONE.setMeterNumber("Meter Number 1");
        METER_ONE.setName("MCT410FL 1");
        METER_ONE.setPaoIdentifier(new PaoIdentifier(11, MCT410FL));
        METER_ONE.setRoute("Route A");
        METER_ONE.setRouteId(1);
    };
    public static final Meter METER_TWO = new Meter();
    {
        METER_TWO.setAddress("Address B");
        METER_TWO.setDisabled(false);
        METER_TWO.setMeterNumber("Meter Number 2");
        METER_TWO.setName("MCT410IL 2");
        METER_TWO.setPaoIdentifier(new PaoIdentifier(12, MCT410IL));
        METER_TWO.setRoute("Route A");
        METER_TWO.setRouteId(1);
    };
    
    private List<Meter> meters = Lists.newArrayList(METER_ONE, METER_TWO);
    private Map<String, Meter> meterNumberToMeter;
    {
        meterNumberToMeter =
            Maps.uniqueIndex(meters, new Function<Meter, String>() {
                @Override
                public String apply(Meter meter) {
                    return meter.getMeterNumber();
                }
            });
    }
    
    @Override
    public Meter getForMeterNumber(String meterNumber) {
        return meterNumberToMeter.get(meterNumber);
    }
}