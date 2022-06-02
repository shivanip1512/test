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
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.KVAR.getMspFieldName());
                readingValue.setDateTime(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);            
//                reading.setTOUReadings();
            }
        };
        
        ReadingProcessor sumKwhConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading, PaoType paoType) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                
                ReadingValues readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.SUM_KWH.getMspFieldName());
                readingValue.setDateTime(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);            
            }
        };

        attributesToLoad =
            ImmutableMap.of(BuiltInAttribute.USAGE, usageConverter,
                            BuiltInAttribute.PEAK_DEMAND, peakDemandConverter,
                            BuiltInAttribute.BLINK_COUNT, blinkConverter,
                            BuiltInAttribute.KVAR, KVArConverter,
                            BuiltInAttribute.SUM_KWH, sumKwhConverter);
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
