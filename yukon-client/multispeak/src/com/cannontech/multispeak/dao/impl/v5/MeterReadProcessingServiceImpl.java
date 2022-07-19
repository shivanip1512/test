package com.cannontech.multispeak.dao.impl.v5;

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
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.enumerations.FieldNameKind;
import com.cannontech.msp.beans.v5.enumerations.ServiceKind;
import com.cannontech.msp.beans.v5.multispeak.MeterReading;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCode;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCodeItem;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCodeItems;
import com.cannontech.msp.beans.v5.multispeak.ReadingTypeCodeOption;
import com.cannontech.msp.beans.v5.multispeak.ReadingValue;
import com.cannontech.msp.beans.v5.multispeak.ReadingValues;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.v5.MeterReadProcessingService;
import com.cannontech.multispeak.dao.v5.MeterReadUpdater;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableMap;

public class MeterReadProcessingServiceImpl implements MeterReadProcessingService {
    @Autowired private GlobalSettingDao globalSettingDao;

    private Map<BuiltInAttribute, ReadingProcessor> attributesToLoad;

    public interface ReadingProcessor {
        public void apply(PointValueHolder value, MeterReading reading, PaoType type);
    }
    
    class DefaultReadingProcessor implements ReadingProcessor{
        
        private FieldNameKind fieldNameKind;
        private RoundingMode roundingMode;
        
        public DefaultReadingProcessor(FieldNameKind fieldNameKind, RoundingMode roundingMode) {
            super();
            this.roundingMode = roundingMode;
            this.fieldNameKind = fieldNameKind;
        }

        @Override
        public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
            // Reading Timestamp
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(value.getPointDataTimeStamp());
            ReadingValues readingValues = getReadingValues(reading);
            ReadingValue readingValue = new ReadingValue();
            readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

            BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, this.roundingMode).stripTrailingZeros();
            readingValue.setValue(valueWithPrecision.toString());
            readingValues.getReadingValue().add(readingValue);
            reading.setReadingValues(readingValues);

            // Reading Type Code
            ReadingTypeCodeItems readingTypeCodeItems = getReadingTypeCodeItems(reading);
            ReadingTypeCodeItem readingTypeCodeItem = new ReadingTypeCodeItem();
            ReadingTypeCode readingTypeCode = new ReadingTypeCode();
            readingTypeCode.setFieldName(this.fieldNameKind);
            readingTypeCodeItem.setReadingTypeCode(readingTypeCode);
            readingTypeCodeItems.getReadingTypeCodeItem().add(readingTypeCodeItem);
            reading.setReadingTypeCodeItems(readingTypeCodeItems);

            ReadingTypeCodeOption option = new ReadingTypeCodeOption();
            option.setReadingTypeCode(readingTypeCode);
            readingValue.setReadingTypeCodeOption(option);
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
                // Reading Timestamp
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                ReadingValues readingValues = getReadingValues(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);

                // Reading Type Code
                ReadingTypeCodeItems readingTypeCodeItems = getReadingTypeCodeItems(reading);
                ReadingTypeCodeItem readingTypeCodeItem = new ReadingTypeCodeItem();
                ReadingTypeCode readingTypeCode = new ReadingTypeCode();
                
                if(type.isWaterMeter()) {
                    readingTypeCode.setFieldName(FieldNameKind.WATER_VOLUME);
                } else if (type.isGasMeter()) {
                    readingTypeCode.setFieldName(FieldNameKind.CORRECTED_GAS_VOLUME);
                } else {
                    readingTypeCode.setFieldName(FieldNameKind.POS_K_WH);
                }

                readingTypeCodeItem.setReadingTypeCode(readingTypeCode);
                readingTypeCodeItems.getReadingTypeCodeItem().add(readingTypeCodeItem);
                reading.setReadingTypeCodeItems(readingTypeCodeItems);
                
                ReadingTypeCodeOption option = new ReadingTypeCodeOption();
                option.setReadingTypeCode(readingTypeCode);
                readingValue.setReadingTypeCodeOption(option);
            }
        };

        ReadingProcessor peakDemandConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                // Reading Timestamp
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                ReadingValues readingValues = getReadingValues(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);

                // Reading Type Code
                ReadingTypeCodeItems readingTypeCodeItems = getReadingTypeCodeItems(reading);
                ReadingTypeCodeItem readingTypeCodeItem = new ReadingTypeCodeItem();
                ReadingTypeCode readingTypeCode = new ReadingTypeCode();
                readingTypeCode.setFieldName(FieldNameKind.MAX_DEMAND);
                readingTypeCodeItem.setReadingTypeCode(readingTypeCode);
                readingTypeCodeItems.getReadingTypeCodeItem().add(readingTypeCodeItem);
                reading.setReadingTypeCodeItems(readingTypeCodeItems);

                ReadingTypeCodeOption option = new ReadingTypeCodeOption();
                option.setReadingTypeCode(readingTypeCode);
                readingValue.setReadingTypeCodeOption(option);
            }
        };

        ReadingProcessor blinkConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading, PaoType type) {
                // Reading Timestamp
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                ReadingValues readingValues = getReadingValues(reading);
                ReadingValue readingValue = new ReadingValue();
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                // Reading Value - create a nice BigDecimal with unlimited precision
                BigDecimal exactValue = new BigDecimal(value.getValue());
                BigDecimal noFractionValue = exactValue.setScale(0, roundingMode);
                readingValue.setValue(noFractionValue.toBigIntegerExact().toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);

                // Reading Type Code
                ReadingTypeCodeItems readingTypeCodeItems = getReadingTypeCodeItems(reading);
                ReadingTypeCodeItem readingTypeCodeItem = new ReadingTypeCodeItem();
                ReadingTypeCode readingTypeCode = new ReadingTypeCode();
                readingTypeCode.setFieldName(FieldNameKind.SUSTAINED_OUTAGE);
                readingTypeCodeItem.setReadingTypeCode(readingTypeCode);
                readingTypeCodeItems.getReadingTypeCodeItem().add(readingTypeCodeItem);
                reading.setReadingTypeCodeItems(readingTypeCodeItems);

                ReadingTypeCodeOption option = new ReadingTypeCodeOption();
                option.setReadingTypeCode(readingTypeCode);
                readingValue.setReadingTypeCodeOption(option);
            }
        };
        
        ReadingProcessor kvaConverter = new DefaultReadingProcessor(FieldNameKind.PREVIOUS_COINCIDENT_KVA, roundingMode);
        ReadingProcessor kvarConverter = new DefaultReadingProcessor(FieldNameKind.PREVIOUS_COINCIDENT_KVAR, roundingMode);
        ReadingProcessor kvarhConverter = new DefaultReadingProcessor(FieldNameKind.POS_KVA_RH, roundingMode);
        ReadingProcessor powerFactorConverter = new DefaultReadingProcessor(FieldNameKind.PF_AVG, roundingMode);
        ReadingProcessor deliveredKwhRateAConverter = new DefaultReadingProcessor(FieldNameKind.TOU_1_K_WH, roundingMode);
        ReadingProcessor deliveredKwhRateBConverter = new DefaultReadingProcessor(FieldNameKind.TOU_2_K_WH, roundingMode);
        ReadingProcessor deliveredKwhRateCConverter = new DefaultReadingProcessor(FieldNameKind.TOU_3_K_WH, roundingMode);
        ReadingProcessor deliveredKwhRateDConverter = new DefaultReadingProcessor(FieldNameKind.TOU_4_K_WH, roundingMode);
        ReadingProcessor receivedKwhConverter = new DefaultReadingProcessor(FieldNameKind.PREVIOUS_POS_K_WH, roundingMode);
        ReadingProcessor receivedKwhRateAConverter = new DefaultReadingProcessor(FieldNameKind.PREVIOUS_TOU_1_K_WH, roundingMode);
        ReadingProcessor receivedKwhRateBConverter = new DefaultReadingProcessor(FieldNameKind.PREVIOUS_TOU_2_K_WH, roundingMode);
        ReadingProcessor receivedKwhRateCConverter = new DefaultReadingProcessor(FieldNameKind.PREVIOUS_TOU_3_K_WH, roundingMode);
        ReadingProcessor receivedKwhRateDConverter = new DefaultReadingProcessor(FieldNameKind.PREVIOUS_TOU_4_K_WH, roundingMode);
        ReadingProcessor peakDemandRateAConverter = new DefaultReadingProcessor(FieldNameKind.TOU_1_MAX_DEMAND, roundingMode);
        ReadingProcessor peakDemandRateBConverter = new DefaultReadingProcessor(FieldNameKind.TOU_2_MAX_DEMAND, roundingMode);
        ReadingProcessor peakDemandRateCConverter = new DefaultReadingProcessor(FieldNameKind.TOU_3_MAX_DEMAND, roundingMode);
        ReadingProcessor peakDemandRateDConverter = new DefaultReadingProcessor(FieldNameKind.TOU_4_MAX_DEMAND, roundingMode);

        attributesToLoad = ImmutableMap.<BuiltInAttribute, ReadingProcessor>builder()
                                                                            .put(BuiltInAttribute.USAGE, usageConverter)
                                                                            .put(BuiltInAttribute.PEAK_DEMAND, peakDemandConverter)
                                                                            .put(BuiltInAttribute.BLINK_COUNT, blinkConverter)
                                                                            .put(BuiltInAttribute.POWER_FACTOR, powerFactorConverter)
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
    public MeterReadUpdater buildMeterReadUpdater(BuiltInAttribute attribute, final PointValueHolder pointValueHolder, PaoType type) {
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

    @Override
    public void updateMeterRead(MeterReading reading, BuiltInAttribute attribute, PointValueHolder pointValueHolder, PaoType type) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        
        processor.apply(pointValueHolder, reading, type);

    }

    @Override
    public MeterReading createMeterRead(YukonMeter meter) {
        MeterReading reading = new MeterReading();
        MeterID meterId = new MeterID();

        if (meter.getPaoType().isWaterMeter()) {
            meterId.setServiceType(ServiceKind.WATER);
        } else if (meter.getPaoType().isGasMeter()) {
            meterId.setServiceType(ServiceKind.GAS);
        } else {
            meterId.setServiceType(ServiceKind.ELECTRIC);
        }

        meterId.setRegisteredName(MultispeakDefines.REGISTERED_NAME);
        meterId.setSystemName(MultispeakDefines.MSP_APPNAME_YUKON);
        meterId.setUtility(MultispeakDefines.AMR_VENDOR);
        // TODO How to get communication port in yukon application
        // TODO meterId.setValue(uuid); 
        meterId.setCommunicationAddress(meter.getSerialOrAddress());
        meterId.setMeterName(meter.getMeterNumber());
        reading.setMeterID(meterId);
        reading.setReferableID(meter.getMeterNumber());
        reading.setDeviceID(meter.getMeterNumber());
        return reading;
    }
    
    private ReadingValues getReadingValues(MeterReading reading) {
        ReadingValues readingValues = reading.getReadingValues();
        if (readingValues == null) {
            readingValues = new ReadingValues();
        }
        return readingValues;
    }

    private ReadingTypeCodeItems getReadingTypeCodeItems(MeterReading reading) {
        ReadingTypeCodeItems readingTypeCodeItems = reading.getReadingTypeCodeItems();
        if (readingTypeCodeItems == null) {
            readingTypeCodeItems = new ReadingTypeCodeItems();
        }
        return readingTypeCodeItems;
    }

}
