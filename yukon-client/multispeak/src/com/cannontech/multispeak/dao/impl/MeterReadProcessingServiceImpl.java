package com.cannontech.multispeak.dao.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.amr.meter.model.YukonMeter;
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
        public void apply(PointValueHolder value, MeterRead reading);
    }
    
    class DefaultReadingProcessor implements ReadingProcessor {
        private SyntaxItem syntaxItem;
        private RoundingMode roundingMode;
        
        public DefaultReadingProcessor(SyntaxItem syntaxItem, RoundingMode roundingMode) {
            this.syntaxItem = syntaxItem;
            this.roundingMode = roundingMode;
        }
        
        @Override
        public void apply(PointValueHolder value, MeterRead reading) {
            
            ReadingValues readingValues = createArrayOfReadingValue(reading);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(value.getPointDataTimeStamp());
           
            ReadingValue readingValue = new ReadingValue();
            readingValue.setDateTime(MultispeakFuncs.toXMLGregorianCalendar(calendar));
            BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
            readingValue.setValue(valueWithPrecision.toString());
            readingValue.setFieldName(syntaxItem.getMspFieldName());
            readingValues.getReadingValue().add(readingValue);
            reading.setReadingValues(readingValues);
        }
    }

    @PostConstruct
    public void setup() {
        final RoundingMode roundingMode = globalSettingDao
            .getEnum(GlobalSettingType.DEFAULT_ROUNDING_MODE, YukonRoundingMode.class)
            .getRoundingMode();

        ReadingProcessor usageConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading) {
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
            public void apply(PointValueHolder value, MeterRead reading) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                reading.setKWDateTime(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                reading.setKW((float) value.getValue());
            }
        };

        ReadingProcessor blinkConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterRead reading) {
                // TBD
            }
        };

        ReadingProcessor kvarConverter = new DefaultReadingProcessor(SyntaxItem.KVAR, roundingMode);

        ReadingProcessor kvaConverter = new DefaultReadingProcessor(SyntaxItem.KVA, roundingMode);

        ReadingProcessor kvarhConverter = new DefaultReadingProcessor(SyntaxItem.KVARH, roundingMode);

        ReadingProcessor powerFactorConverter = new DefaultReadingProcessor(SyntaxItem.POWER_FACTOR, roundingMode);

        ReadingProcessor receivedKwhConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH, roundingMode);

        ReadingProcessor peakDemandRateAConverter = new DefaultReadingProcessor(SyntaxItem.PEAK_DEMAND_RATE_A, roundingMode);

        ReadingProcessor peakDemandRateBConverter = new DefaultReadingProcessor(SyntaxItem.PEAK_DEMAND_RATE_B, roundingMode);

        ReadingProcessor peakDemandRateCConverter = new DefaultReadingProcessor(SyntaxItem.PEAK_DEMAND_RATE_C, roundingMode);

        ReadingProcessor peakDemandRateDConverter = new DefaultReadingProcessor(SyntaxItem.PEAK_DEMAND_RATE_D, roundingMode);

        ReadingProcessor receivedKwhRateAConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH_RATE_A, roundingMode);

        ReadingProcessor receivedKwhRateBConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH_RATE_B, roundingMode);

        ReadingProcessor receivedKwhRateCConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH_RATE_C, roundingMode);

        ReadingProcessor receivedKwhRateDConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH_RATE_D, roundingMode);

        ReadingProcessor deliveredKwhRateAConverter = new DefaultReadingProcessor(SyntaxItem.DELIVERED_KWH_RATE_A, roundingMode);

        ReadingProcessor deliveredKwhRateBConverter = new DefaultReadingProcessor(SyntaxItem.DELIVERED_KWH_RATE_B, roundingMode);

        ReadingProcessor deliveredKwhRateCConverter = new DefaultReadingProcessor(SyntaxItem.DELIVERED_KWH_RATE_C, roundingMode);

        ReadingProcessor deliveredKwhRateDConverter = new DefaultReadingProcessor(SyntaxItem.DELIVERED_KWH_RATE_D, roundingMode);

        attributesToLoad = ImmutableMap.<BuiltInAttribute, ReadingProcessor>builder()
                                       .put(BuiltInAttribute.USAGE, usageConverter)
                                       .put(BuiltInAttribute.PEAK_DEMAND, peakDemandConverter)
                                       .put(BuiltInAttribute.BLINK_COUNT, blinkConverter)
                                       .put(BuiltInAttribute.KVAR, kvarConverter)
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

    private ReadingValues createArrayOfReadingValue(MeterRead reading) {
        ReadingValues readingValues = reading.getReadingValues();
        if (readingValues == null) {
            readingValues = new ReadingValues();
        }
        return readingValues;
    }

    @Override
    public MeterReadUpdater buildMeterReadUpdater(BuiltInAttribute attribute, final PointValueHolder pointValueHolder) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        return new MeterReadUpdater() {
            @Override
            public void update(MeterRead reading) {
                processor.apply(pointValueHolder, reading);
            }
        };
    }

    @Override
    public void updateMeterRead(MeterRead reading, BuiltInAttribute attribute, PointValueHolder pointValueHolder) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        processor.apply(pointValueHolder, reading);
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
