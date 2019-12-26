package com.cannontech.multispeak.dao.impl.v5;

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
        public void apply(PointValueHolder value, MeterReading reading);
    }

    @PostConstruct
    public void setup() {
        final RoundingMode roundingMode =
            globalSettingDao.getEnum(GlobalSettingType.DEFAULT_ROUNDING_MODE, YukonRoundingMode.class).getRoundingMode();

        ReadingProcessor usageConverter = new ReadingProcessor() {
            @Override
            public void apply(PointValueHolder value, MeterReading reading) {
                // Reading Timestamp
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                ReadingValues readingValues = new ReadingValues();
                ReadingValue readingValue = new ReadingValue();
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                // Reading Value - create a nice BigDecimal with unlimited precision
                BigDecimal exactValue = new BigDecimal(value.getValue());
                BigDecimal noFractionValue = exactValue.setScale(0, roundingMode);
                readingValue.setValue(noFractionValue.toBigIntegerExact().toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);

                // Reading Type Code
                ReadingTypeCodeItems readingTypeCodeItems = new ReadingTypeCodeItems();
                ReadingTypeCodeItem readingTypeCodeItem = new ReadingTypeCodeItem();
                ReadingTypeCode readingTypeCode = new ReadingTypeCode();
                readingTypeCode.setFieldName(FieldNameKind.POS_K_WH);
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
            public void apply(PointValueHolder value, MeterReading reading) {
                // Reading Timestamp
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                ReadingValues readingValues = new ReadingValues();
                ReadingValue readingValue = new ReadingValue();
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                // Reading Value - create a nice BigDecimal with unlimited precision
                BigDecimal exactValue = new BigDecimal(value.getValue());
                BigDecimal noFractionValue = exactValue.setScale(0, roundingMode);
                readingValue.setValue(noFractionValue.toBigIntegerExact().toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);

                // Reading Type Code
                ReadingTypeCodeItems readingTypeCodeItems = new ReadingTypeCodeItems();
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
            public void apply(PointValueHolder value, MeterReading reading) {
                // Reading Timestamp
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(value.getPointDataTimeStamp());
                ReadingValues readingValues = new ReadingValues();
                ReadingValue readingValue = new ReadingValue();
                readingValue.setTimeStamp(MultispeakFuncs.toXMLGregorianCalendar(calendar));

                // Reading Value - create a nice BigDecimal with unlimited precision
                BigDecimal exactValue = new BigDecimal(value.getValue());
                BigDecimal noFractionValue = exactValue.setScale(0, roundingMode);
                readingValue.setValue(noFractionValue.toBigIntegerExact().toString());
                readingValues.getReadingValue().add(readingValue);
                reading.setReadingValues(readingValues);

                // Reading Type Code
                ReadingTypeCodeItems readingTypeCodeItems = new ReadingTypeCodeItems();
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

        attributesToLoad =
            ImmutableMap.of(BuiltInAttribute.USAGE, usageConverter, BuiltInAttribute.PEAK_DEMAND, peakDemandConverter,
                BuiltInAttribute.BLINK_COUNT, blinkConverter);
    }

    @Override
    public MeterReadUpdater buildMeterReadUpdater(BuiltInAttribute attribute, final PointValueHolder pointValueHolder) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        return new MeterReadUpdater() {
            @Override
            public void update(MeterReading reading) {
                processor.apply(pointValueHolder, reading);
            }
        };
    }

    @Override
    public void updateMeterRead(MeterReading reading, BuiltInAttribute attribute, PointValueHolder pointValueHolder) {
        final ReadingProcessor processor = attributesToLoad.get(attribute);
        if (processor == null) {
            throw new IllegalArgumentException("Attribute " + attribute + " is not supported");
        }
        processor.apply(pointValueHolder, reading);

    }

    @Override
    public MeterReading createMeterRead(YukonMeter meter) {
        MeterReading reading = new MeterReading();
        MeterID meterId = new MeterID();
        meterId.setServiceType(ServiceKind.ELECTRIC);
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

}
