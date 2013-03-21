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
    public static final Meter METER_MCT410FL = new Meter();
    static {
        METER_MCT410FL.setAddress("Address A");
        METER_MCT410FL.setDisabled(false);
        METER_MCT410FL.setMeterNumber("Meter Number 1");
        METER_MCT410FL.setName("MCT410FL 1");
        METER_MCT410FL.setPaoIdentifier(new PaoIdentifier(11, MCT410FL));
        METER_MCT410FL.setRoute("Route A");
        METER_MCT410FL.setRouteId(1);
    };
    public static final Meter METER_MCT410IL = new Meter();
    static {
        METER_MCT410IL.setAddress("Address B");
        METER_MCT410IL.setDisabled(false);
        METER_MCT410IL.setMeterNumber("Meter Number 2");
        METER_MCT410IL.setName("MCT410IL 2");
        METER_MCT410IL.setPaoIdentifier(new PaoIdentifier(12, MCT410IL));
        METER_MCT410IL.setRoute("Route A");
        METER_MCT410IL.setRouteId(1);
    };

    public static final Meter METER_RFN410FL = new Meter();
    static {
        METER_RFN410FL.setDisabled(false);
        METER_RFN410FL.setMeterNumber("Meter Number 3");
        METER_RFN410FL.setName("RFN410FL 3");
        METER_RFN410FL.setPaoIdentifier(new PaoIdentifier(13, RFN410FL));
    };

    public static final Meter NULL_VALUED_METER = new Meter();
    static {
        NULL_VALUED_METER.setDisabled(false);
        NULL_VALUED_METER.setMeterNumber("Null Valued Meter");
        NULL_VALUED_METER.setName("MCT410IL 3");
        NULL_VALUED_METER.setPaoIdentifier(new PaoIdentifier(13, MCT410IL));
    };
    
    private List<Meter> meters = Lists.newArrayList(METER_MCT410FL, METER_MCT410IL, METER_RFN410FL, NULL_VALUED_METER);
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