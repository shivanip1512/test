package com.cannontech.services.mock;

import static com.cannontech.common.pao.PaoType.*;

import java.util.List;
import java.util.Map;

import com.cannontech.amr.meter.dao.impl.MeterDaoImpl;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MockMeterDaoImpl extends MeterDaoImpl {
    public static final PlcMeter METER_MCT410FL = new PlcMeter(new PaoIdentifier(11, MCT410FL),
                                                         "Meter Number 1",
                                                         "MCT410FL 1",
                                                         false,
                                                         "Route A", 
                                                         1,
                                                         "Address A");
    
    public static final PlcMeter METER_MCT410IL = new PlcMeter(new PaoIdentifier(12, MCT410FL),
                                                         "Meter Number 2",
                                                         "MCT410IL 2",
                                                         false,
                                                         "Route A", 
                                                         1,
                                                         "Address B");

    public static final RfnMeter METER_RFN410FL = new RfnMeter(new PaoIdentifier(13, RFN410FL),
                                                               new RfnIdentifier("410987654", "LGYR", "FocuskWh"),
                                                               "Meter Number 3",
                                                               "RFN410FL 3",
                                                               false);
                                                         
    public static final PlcMeter NULL_VALUED_METER = new PlcMeter(new PaoIdentifier(14, MCT410IL), 
                                                            "Null Valued Meter",
                                                            "MCT410IL 3",
                                                            false,
                                                            "",
                                                            1,
                                                            "");
    
    private static List<YukonMeter> meters = Lists.newArrayList(METER_MCT410FL, METER_MCT410IL, METER_RFN410FL, NULL_VALUED_METER);
    private Map<String, YukonMeter> meterNumberToMeter;
    {
        meterNumberToMeter =
            Maps.uniqueIndex(meters, new Function<YukonMeter, String>() {
                @Override
                public String apply(YukonMeter meter) {
                    return meter.getMeterNumber();
                }
            });
    }
    
    @Override
    public YukonMeter getForMeterNumber(String meterNumber) {
        return meterNumberToMeter.get(meterNumber);
    }
    
    public static List<YukonMeter> getMeters() {
        return meters;
    }
}