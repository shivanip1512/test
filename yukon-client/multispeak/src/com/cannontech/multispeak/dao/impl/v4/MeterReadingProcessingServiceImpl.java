package com.cannontech.multispeak.dao.impl.v4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v4.ArrayOfReadingValue;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.ReadingValue;
import com.cannontech.msp.beans.v4.ServiceType;
import com.cannontech.multispeak.block.syntax.v4.SyntaxItem;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.v4.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MeterReadUpdater;
import com.cannontech.multispeak.dao.v4.MeterReadingProcessingService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableMap;

public class MeterReadingProcessingServiceImpl implements MeterReadingProcessingService {
    @Autowired private GlobalSettingDao globalSettingDao;

    private ImmutableMap<BuiltInAttribute, ReadingProcessor> attributesToLoad;

    public interface ReadingProcessor {
        public void apply(PointValueHolder value, MeterReading reading, PaoType type);
    }
    
    class DefaultReadingProcessor implements ReadingProcessor {

        private SyntaxItem syntaxItem;
        private RoundingMode roundingMode;

        public DefaultReadingProcessor(SyntaxItem syntaxItem, RoundingMode roundingMode) {
            this.syntaxItem = syntaxItem;
            this.roundingMode = roundingMode;
        }

        @Override
        public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(value.getPointDataTimeStamp());

            ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
            ReadingValue readingValue = new ReadingValue();
            readingValue.setFieldName(syntaxItem.getMspFieldName());
            readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

            BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
            readingValue.setValue(valueWithPrecision.toString());
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
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                
                
                if(type.isGasMeter()) {
                    readingValue.setFieldName(SyntaxItem.GAS_VOLUME.getMspFieldName());
                } else if (type.isWaterMeter()){
                    readingValue.setFieldName(SyntaxItem.WATER_VOLUME.getMspFieldName());
                } else {
                    readingValue.setFieldName(SyntaxItem.KWH.getMspFieldName());
                }
               
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor peakDemandConverter = new DefaultReadingProcessor(SyntaxItem.PEAK_DEMAND, roundingMode);
        ReadingProcessor blinkConverter = new DefaultReadingProcessor(SyntaxItem.BLINK_COUNT, roundingMode);
        ReadingProcessor powerFactorConverter = new DefaultReadingProcessor(SyntaxItem.POWER_FACTOR, roundingMode);
        ReadingProcessor kvaConverter = new DefaultReadingProcessor(SyntaxItem.KVA, roundingMode);
        ReadingProcessor kvarConverter = new DefaultReadingProcessor(SyntaxItem.KVAR, roundingMode);
        ReadingProcessor kvarhConverter = new DefaultReadingProcessor(SyntaxItem.KVARH, roundingMode);
        ReadingProcessor deliveredKwhRateAConverter = new DefaultReadingProcessor(SyntaxItem.DELIVERED_KWH_RATE_A, roundingMode);
        ReadingProcessor deliveredKwhRateBConverter = new DefaultReadingProcessor(SyntaxItem.DELIVERED_KWH_RATE_B, roundingMode);
        ReadingProcessor deliveredKwhRateCConverter = new DefaultReadingProcessor(SyntaxItem.DELIVERED_KWH_RATE_C, roundingMode);
        ReadingProcessor deliveredKwhRateDConverter = new DefaultReadingProcessor(SyntaxItem.DELIVERED_KWH_RATE_D, roundingMode);
        ReadingProcessor receivedKwhConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH, roundingMode);
        ReadingProcessor receivedKwhRateAConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH_RATE_A, roundingMode);
        ReadingProcessor receivedKwhRateBConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH_RATE_B, roundingMode);
        ReadingProcessor receivedKwhRateCConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH_RATE_C, roundingMode);
        ReadingProcessor receivedKwhRateDConverter = new DefaultReadingProcessor(SyntaxItem.RECEIVED_KWH_RATE_D, roundingMode);
        ReadingProcessor peakDemandRateAConverter = new DefaultReadingProcessor(SyntaxItem.PEAK_DEMAND_RATE_A, roundingMode);
        ReadingProcessor peakDemandRateBConverter = new DefaultReadingProcessor(SyntaxItem.PEAK_DEMAND_RATE_B, roundingMode);
        ReadingProcessor peakDemandRateCConverter = new DefaultReadingProcessor(SyntaxItem.PEAK_DEMAND_RATE_C, roundingMode);
        ReadingProcessor peakDemandRateDConverter = new DefaultReadingProcessor(SyntaxItem.PEAK_DEMAND_RATE_D, roundingMode);

        attributesToLoad = ImmutableMap.<BuiltInAttribute, ReadingProcessor>builder()
                                                                            .put(BuiltInAttribute.USAGE, usageConverter)
                                                                            .put(BuiltInAttribute.PEAK_DEMAND, peakDemandConverter)
                                                                            .put(BuiltInAttribute.BLINK_COUNT, blinkConverter)
                                                                            .put(BuiltInAttribute.POWER_FACTOR, powerFactorConverter)
                                                                            .put(BuiltInAttribute.KVA, kvaConverter)
                                                                            .put(BuiltInAttribute.KVAR, kvarConverter)
                                                                            .put(BuiltInAttribute.KVARH, kvarhConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH, receivedKwhConverter)
                                                                            .put(BuiltInAttribute.DELIVERED_KWH_RATE_A, deliveredKwhRateAConverter)
                                                                            .put(BuiltInAttribute.DELIVERED_KWH_RATE_B, deliveredKwhRateBConverter)
                                                                            .put(BuiltInAttribute.DELIVERED_KWH_RATE_C, deliveredKwhRateCConverter)
                                                                            .put(BuiltInAttribute.DELIVERED_KWH_RATE_D, deliveredKwhRateDConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH_RATE_A, receivedKwhRateAConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH_RATE_B, receivedKwhRateBConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH_RATE_C, receivedKwhRateCConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH_RATE_D, receivedKwhRateDConverter)
                                                                            .put(BuiltInAttribute.PEAK_DEMAND_RATE_A, peakDemandRateAConverter)
                                                                            .put(BuiltInAttribute.PEAK_DEMAND_RATE_B, peakDemandRateBConverter)
                                                                            .put(BuiltInAttribute.PEAK_DEMAND_RATE_C, peakDemandRateCConverter)
                                                                            .put(BuiltInAttribute.PEAK_DEMAND_RATE_D, peakDemandRateDConverter)
                                                                            .build();

    }

    @Override
    public void updateMeterReading(MeterReading reading, BuiltInAttribute attribute, PointValueHolder pointValueHolder, PaoType type) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        processor.apply(pointValueHolder, reading, type);

    }

    @Override
    public MeterReading createMeterReading(YukonMeter meter) {
        MeterReading reading = new MeterReading();
        reading.setObjectID(meter.getMeterNumber());
        reading.setDeviceID(meter.getMeterNumber());
        reading.setUtility(MultispeakDefines.AMR_VENDOR);

        MeterID meterId = new MeterID();
        meterId.setMeterNo(meter.getMeterNumber());
        if (meter.getPaoIdentifier().getPaoType().isGasMeter()) {
            meterId.setServiceType(ServiceType.GAS);
        } else if (meter.getPaoIdentifier().getPaoType().isWaterMeter()) {
            meterId.setServiceType(ServiceType.WATER);
        } else {
            meterId.setServiceType(ServiceType.ELECTRIC);
        }
        meterId.setObjectID(meter.getMeterNumber());
        meterId.setUtility(MultispeakDefines.AMR_VENDOR);
        
        reading.setMeterID(meterId);
        return reading;
    }
    
    private ArrayOfReadingValue createArrayOfReadingValue(MeterReading reading) {
        ArrayOfReadingValue readingValues = reading.getReadingValues();
        if(readingValues ==  null) {
            readingValues = new ArrayOfReadingValue();
        }
        return readingValues;
    }

    @Override
    public Set<BuiltInAttribute> getAttributes() {
        return attributesToLoad.keySet();
    }

    @Override
    public MeterReadUpdater buildMeterReadUpdater(BuiltInAttribute attribute, PointValueHolder pointValueHolder, PaoType type) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        return new MeterReadUpdater() {
            @Override
            public void update(MeterReading reading) {
                processor.apply(pointValueHolder, reading, type);
            }
        };
    }
    
    

}
