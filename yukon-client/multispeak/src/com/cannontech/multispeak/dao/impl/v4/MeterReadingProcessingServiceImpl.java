package com.cannontech.multispeak.dao.impl.v4;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.msp.beans.v4.ArrayOfReadingValue;
import com.cannontech.msp.beans.v4.MeterID;
import com.cannontech.msp.beans.v4.MeterReading;
import com.cannontech.msp.beans.v4.ReadingValue;
import com.cannontech.multispeak.block.syntax.v4.SyntaxItem;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.dao.v4.MeterReadingProcessingService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.google.common.collect.ImmutableMap;

public class MeterReadingProcessingServiceImpl implements MeterReadingProcessingService {
    @Autowired private GlobalSettingDao globalSettingDao;

    private Map<BuiltInAttribute, ReadingProcessor> attributesToLoad;

    public interface ReadingProcessor {
        public void apply(PointValueHolder value, MeterReading reading);
    }

    @PostConstruct
    public void setup() {
        final RoundingMode roundingMode = globalSettingDao
                .getEnum(GlobalSettingType.DEFAULT_ROUNDING_MODE, YukonRoundingMode.class)
                .getRoundingMode();

        ReadingProcessor usageConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading) {
                /*Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                // reading.setReadingValues(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                // create a nice BigDecimal with unlimited precision
                BigDecimal exactValue = new BigDecimal(value.getValue());
                BigDecimal noFractionValue = exactValue.setScale(0, roundingMode);
                // reading.setPosKWh(noFractionValue.toBigIntegerExact());
                 */ 
                   Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                
                ArrayOfReadingValue readingValues = new ArrayOfReadingValue();
                ReadingValue readingValue = new ReadingValue();
                
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                readingValue.setFieldName(SyntaxItem.KWH.getMspFieldName());
                readingValues.getReadingValue().add(readingValue);
                
                
                reading.setReadingValues(readingValues);
               
                // Reading Type Code
                //ReadingTypeCodeItems readingTypeCodeItems = getReadingTypeCodeItems(reading);
                /*ReadingTypeCodeItem readingTypeCodeItem = new ReadingTypeCodeItem();
                ReadingTypeCode readingTypeCode = new ReadingTypeCode();
                
                if(type.isWaterMeter()) {
                    readingTypeCode.setFieldName(FieldNameKind.WATER_VOLUME);
                } else {
                    readingTypeCode.setFieldName(FieldNameKind.POS_K_WH);
                }

                readingTypeCodeItem.setReadingTypeCode(readingTypeCode);
                readingTypeCodeItems.getReadingTypeCodeItem().add(readingTypeCodeItem);
                reading.setReadingTypeCodeItems(readingTypeCodeItems);
                
                ReadingTypeCodeOption option = new ReadingTypeCodeOption();
                option.setReadingTypeCode(readingTypeCode);
                readingValue.setReadingTypeCodeOption(option);*/
           }
        };

        ReadingProcessor peakDemandConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                
                ArrayOfReadingValue readingValues = new ArrayOfReadingValue();
                ReadingValue readingValue = new ReadingValue();
                readingValue.setFieldName(SyntaxItem.LAST_INTERVAL_DEMAND_DATETIME.getMspFieldName()); 
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                 //reading.setKWDateTime(MultispeakFuncs.toXMLGregorianCalendar(calendar));
                BigDecimal valueWithPrecision = new BigDecimal(value.getValue()).setScale(3, roundingMode).stripTrailingZeros();
                readingValue.setValue(valueWithPrecision.toString());
                reading.setReadingValues(readingValues);
                // reading.setKW((float) value.getValue());
            }
        };

        ReadingProcessor blinkConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading) {
                // TBD
            }
        };

        attributesToLoad = ImmutableMap.of(BuiltInAttribute.USAGE, usageConverter,
                BuiltInAttribute.PEAK_DEMAND, peakDemandConverter,
                BuiltInAttribute.BLINK_COUNT, blinkConverter);
    }

    @Override
    public void updateMeterReading(MeterReading reading, BuiltInAttribute attribute, PointValueHolder pointValueHolder) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        processor.apply(pointValueHolder, reading);

    }

    @Override
    public MeterReading createMeterReading(YukonMeter meter) {
        MeterReading reading = new MeterReading();
        reading.setObjectID(meter.getMeterNumber());
        reading.setDeviceID(meter.getMeterNumber());
        reading.setUtility(MultispeakDefines.AMR_VENDOR);

        MeterID meterID = new MeterID();
        meterID.setMeterNo(meter.getMeterNumber());
        reading.setMeterID(meterID);
        return reading;
    }

}
