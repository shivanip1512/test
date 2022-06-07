package com.cannontech.multispeak.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v3.MeterRead;
import com.cannontech.msp.beans.v3.ReadingValue;
import com.cannontech.msp.beans.v3.ReadingValues;
import com.cannontech.multispeak.block.syntax.SyntaxItem;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.MeterReadProcessingService;
import com.cannontech.multispeak.dao.MeterReadUpdater;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableMap;

public class MeterReadProcessingServiceImpl implements MeterReadProcessingService {
    @Autowired private GlobalSettingDao globalSettingDao;

    private Map<BuiltInAttribute, ReadingProcessor> attributesToLoad;

    public interface ReadingProcessor {
        public void apply(PointValueHolder value, MeterRead reading, PaoType paoType);
    }

    @PostConstruct
    public void setup() {
        final RoundingMode roundingMode = globalSettingDao
            .getEnum(GlobalSettingType.DEFAULT_ROUNDING_MODE, YukonRoundingMode.class)
            .getRoundingMode();

        ReadingProcessor usageConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                reading.setReadingDate(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                // create a nice BigDecimal with unlimited precision
                BigDecimal exactValue = new BigDecimal(value.getValue());
                BigDecimal noFractionValue = exactValue.setScale(0, roundingMode);
                reading.setPosKWh(noFractionValue.toBigIntegerExact());
            }
        };

        ReadingProcessor peakDemandConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                reading.setKWDateTime(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                reading.setKW((float) value.getValue());
            }
        };

        ReadingProcessor blinkConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                // TBD
            }
        };
        
        
        ReadingProcessor KVArConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.KVAR.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);            
            }

        };
        
        ReadingProcessor sumKwhConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.SUM_KWH.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);           
            }
        };
        
        ReadingProcessor netKwhConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.NET_KWH.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);      
            }
        };
        
        ReadingProcessor kvaConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.KVA.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);             
            }
        };
        
        ReadingProcessor kvarhConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.KVARH.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);             
            }
        };
        
        ReadingProcessor powerFactorConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.POWER_FACTOR.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);             
            }
        };
        
        ReadingProcessor receivedKwhConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        ReadingProcessor peakDemandRateAConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.PEAK_DEMAND_RATE_A.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        ReadingProcessor peakDemandRateBConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.PEAK_DEMAND_RATE_B.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        ReadingProcessor peakDemandRateCConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.PEAK_DEMAND_RATE_C.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        ReadingProcessor peakDemandRateDConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.PEAK_DEMAND_RATE_D.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        ReadingProcessor receivedKwhRateAConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH_RATE_A.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        ReadingProcessor receivedKwhRateBConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH_RATE_B.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        ReadingProcessor receivedKwhRateCConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH_RATE_C.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        ReadingProcessor receivedKwhRateDConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH_RATE_D.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        ReadingProcessor deliveredKwhRateAConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.DELIVERED_KWH_RATE_A.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        ReadingProcessor deliveredKwhRateBConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.DELIVERED_KWH_RATE_B.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);             
            }
        };
        
        ReadingProcessor deliveredKwhRateCConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.DELIVERED_KWH_RATE_C.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);           
            }
        };
        
        ReadingProcessor deliveredKwhRateDConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.DELIVERED_KWH_RATE_D.getMspFieldName());
                setTimestampAndValuesForMeterRead(value, reading, readingValues, readingValue);              
            }
        };
        
        attributesToLoad = ImmutableMap.<BuiltInAttribute, ReadingProcessor>builder()
                                       .put(BuiltInAttribute.USAGE, usageConverter)
                                       .put(BuiltInAttribute.PEAK_DEMAND, peakDemandConverter)
                                       .put(BuiltInAttribute.BLINK_COUNT, blinkConverter)
                                       .put(BuiltInAttribute.KVAR, KVArConverter)
                                       .put(BuiltInAttribute.SUM_KWH, sumKwhConverter)
                                       .put(BuiltInAttribute.NET_KWH, netKwhConverter)
                                       .put(BuiltInAttribute.KVA, kvaConverter)
                                       .put(BuiltInAttribute.KVARH, kvarhConverter)
                                       .put(BuiltInAttribute.POWER_FACTOR, powerFactorConverter)
                                       .put(BuiltInAttribute.RECEIVED_KWH, receivedKwhConverter)
                                       .put(BuiltInAttribute.PEAK_DEMAND_RATE_A, peakDemandRateAConverter)
                                       .put(BuiltInAttribute.PEAK_DEMAND_RATE_B, peakDemandRateBConverter)
                                       .put(BuiltInAttribute.PEAK_DEMAND_RATE_C, peakDemandRateCConverter)
                                       .put(BuiltInAttribute.PEAK_DEMAND_RATE_D, peakDemandRateDConverter)
                                       .put(BuiltInAttribute.RECEIVED_KWH_RATE_A, receivedKwhRateAConverter)
                                       .put(BuiltInAttribute.RECEIVED_KWH_RATE_B, receivedKwhRateBConverter)
                                       .put(BuiltInAttribute.RECEIVED_KWH_RATE_C, receivedKwhRateCConverter)
                                       .put(BuiltInAttribute.RECEIVED_KWH_RATE_D, receivedKwhRateDConverter)
                                       .put(BuiltInAttribute.DELIVERED_KWH_RATE_A, deliveredKwhRateAConverter)
                                       .put(BuiltInAttribute.DELIVERED_KWH_RATE_B, deliveredKwhRateBConverter)
                                       .put(BuiltInAttribute.DELIVERED_KWH_RATE_C, deliveredKwhRateCConverter)
                                       .put(BuiltInAttribute.DELIVERED_KWH_RATE_D, deliveredKwhRateDConverter)
                                       .build();
    }
    
    private void setTimestampAndValuesForMeterRead(PointValueHolder value, MeterRead reading,
            ReadingValues readingValues, ReadingValue readingValue) {
        final RoundingMode roundingMode = globalSettingDao.getEnum(GlobalSettingType.DEFAULT_ROUNDING_MODE, YukonRoundingMode.class)
                                                          .getRoundingMode();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(value.getPointDataTimeStamp());
        readingValue.setDateTime(MultispeakFuncs.toXMLGregorianCalendar(calendar));
        BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
        readingValue.setValue(valueWithPrecision.toString());
        readingValues.getReadingValue().add(readingValue);
        reading.setReadingValues(readingValues);
    }
    
    private ReadingValues createArrayOfReadingValue(MeterRead reading) {
        ReadingValues readingValues = reading.getReadingValues();
        if(readingValues ==  null) {
            readingValues = new ReadingValues();
        }
        return readingValues;
    }

    @Override
    public MeterReadUpdater buildMeterReadUpdater(BuiltInAttribute attribute,
                                           final PointValueHolder pointValueHolder, PaoType paoType) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        return new MeterReadUpdater() {
            @Override
            public void update(MeterRead reading) {
                processor.apply(pointValueHolder, reading,paoType);
            }
        };
    }

    @Override
    public void updateMeterRead(MeterRead reading, BuiltInAttribute attribute, PointValueHolder pointValueHolder, PaoType paoType) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        processor.apply(pointValueHolder, reading, paoType);

    }

    @Override
    public MeterRead createMeterRead(YukonMeter meter) {
        MeterRead reading = new MeterRead();
        reading.setMeterNo(meter.getMeterNumber());
        reading.setObjectID(meter.getMeterNumber());
        reading.setDeviceID(meter.getMeterNumber());
        reading.setUtility(MultispeakDefines.AMR_VENDOR);
        return reading;
    }

}
