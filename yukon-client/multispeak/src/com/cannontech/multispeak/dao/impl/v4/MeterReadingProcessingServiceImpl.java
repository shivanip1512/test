package com.cannontech.multispeak.dao.impl.v4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Map;
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
import com.cannontech.multispeak.dao.v4.MeterReadingProcessingService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableMap;

public class MeterReadingProcessingServiceImpl implements MeterReadingProcessingService {
    @Autowired private GlobalSettingDao globalSettingDao;

    private Map<BuiltInAttribute, ReadingProcessor> attributesToLoad;

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

        attributesToLoad = ImmutableMap.of(BuiltInAttribute.USAGE, usageConverter,
                                           BuiltInAttribute.PEAK_DEMAND, peakDemandConverter,
                                           BuiltInAttribute.BLINK_COUNT, blinkConverter);
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

}
