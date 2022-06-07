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

        ReadingProcessor peakDemandConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.PEAK_DEMAND.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor blinkConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.BLINK_COUNT.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };
        
        ReadingProcessor powerFactorConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.POWER_FACTOR.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor sumKwhConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.SUM_KWH.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor netKwhConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.NET_KWH.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor kvaConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.KVA.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor kvarConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.KVAR.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor kvarhConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.KVARH.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor deliveredKwhRateAConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.DELIVERED_KWH_RATE_A.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor deliveredKwhRateBConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.DELIVERED_KWH_RATE_B.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor deliveredKwhRateCConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.DELIVERED_KWH_RATE_C.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor deliveredKwhRateDConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.DELIVERED_KWH_RATE_D.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor receivedKwhConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor receivedKwhRateAConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH_RATE_A.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor receivedKwhRateBConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH_RATE_B.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor receivedKwhRateCConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH_RATE_C.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor receivedKwhRateDConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.RECEIVED_KWH_RATE_D.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor peakDemandRateAConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.PEAK_DEMAND_RATE_A.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor peakDemandRateBConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.PEAK_DEMAND_RATE_B.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor peakDemandRateCConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.PEAK_DEMAND_RATE_C.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };

        ReadingProcessor peakDemandRateDConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());

                ArrayOfReadingValue readingValues = createArrayOfReadingValue(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.PEAK_DEMAND_RATE_D.getMspFieldName());
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);
            }
        };
        
        attributesToLoad = ImmutableMap.<BuiltInAttribute, ReadingProcessor>builder()
                                                                            .put(BuiltInAttribute.USAGE, usageConverter)
                                                                            .put(BuiltInAttribute.PEAK_DEMAND, peakDemandConverter)
                                                                            .put(BuiltInAttribute.BLINK_COUNT, blinkConverter)
                                                                            .put(BuiltInAttribute.POWER_FACTOR, powerFactorConverter)
                                                                            .put(BuiltInAttribute.SUM_KWH, sumKwhConverter)
                                                                            .put(BuiltInAttribute.NET_KWH, netKwhConverter)
                                                                            .put(BuiltInAttribute.KVA, kvaConverter)
                                                                            .put(BuiltInAttribute.KVAR, kvarConverter)
                                                                            .put(BuiltInAttribute.KVARH, kvarhConverter)
                                                                            .put(BuiltInAttribute.DELIVERED_KWH_RATE_A, deliveredKwhRateAConverter)
                                                                            .put(BuiltInAttribute.DELIVERED_KWH_RATE_B, deliveredKwhRateBConverter)
                                                                            .put(BuiltInAttribute.DELIVERED_KWH_RATE_C, deliveredKwhRateCConverter)
                                                                            .put(BuiltInAttribute.DELIVERED_KWH_RATE_D, deliveredKwhRateDConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH_RATE_A, receivedKwhRateAConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH_RATE_B, receivedKwhRateBConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH_RATE_C, receivedKwhRateCConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH_RATE_D, receivedKwhRateDConverter)
                                                                            .put(BuiltInAttribute.RECEIVED_KWH, receivedKwhConverter)
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
