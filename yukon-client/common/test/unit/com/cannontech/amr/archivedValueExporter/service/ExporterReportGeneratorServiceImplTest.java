package com.cannontech.amr.archivedValueExporter.service;

import static com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType.*;
import static com.cannontech.amr.archivedValueExporter.model.DataSelection.*;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.*;
import static com.cannontech.amr.archivedValueExporter.model.PadSide.*;
import static com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode.*;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.*;
import static com.cannontech.common.point.PointQuality.*;
import static com.cannontech.database.data.point.PointType.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.Field;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.Preview;
import com.cannontech.amr.archivedValueExporter.model.dataRange.ChangeIdRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.LocalDateRange;
import com.cannontech.amr.archivedValueExporter.service.impl.ExportReportGeneratorServiceImpl;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.dao.MockMeterDaoImpl;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.MockRfnDeviceDaoImpl;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.MockAttributeServiceImpl;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.pao.service.impl.MockPaoSelectionServiceImpl;
import com.cannontech.common.util.TimeZoneFormat;
import com.cannontech.core.dao.MockRawPointHistoryDaoImpl;
import com.cannontech.core.dao.MockUnitMeasureDaoImpl;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointValueBuilder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.i18n.YukonUserContextMessageSourceResolverMock;
import com.cannontech.user.SimpleYukonUserContext;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ExporterReportGeneratorServiceImplTest {
    private PaoSelectionService paoSelectionService = new MockPaoSelectionServiceImpl();
    private AttributeService attributeService = new MockAttributeServiceImpl();
    private MeterDao meterDao = new MockMeterDaoImpl();
    private RfnDeviceDao rfnDeviceDao = new MockRfnDeviceDaoImpl();
    private RawPointHistoryDao rawPointHistoryDao = new MockRawPointHistoryDaoImpl();
    private UnitMeasureDao unitMeasureDao = new MockUnitMeasureDaoImpl();
    private TimeZoneFormat tzFormat = TimeZoneFormat.LOCAL;
    
    StaticMessageSource messageSource = new StaticMessageSource();
    {
        messageSource.addMessage("yukon.web.modules.tools.bulk.archivedValueExporter.previewUOMValue", Locale.US, "UnitOfMeasure");
        messageSource.addMessage("yukon.web.modules.tools.bulk.archivedValueExporter.previewMeterNumber", Locale.US, "Meter Number");
        messageSource.addMessage("yukon.web.modules.tools.bulk.archivedValueExporter.previewMeterName", Locale.US, "Meter Name");
        messageSource.addMessage("yukon.web.modules.tools.bulk.archivedValueExporter.previewMeterAddress", Locale.US, "Meter Address");
        messageSource.addMessage("yukon.web.modules.tools.bulk.archivedValueExporter.previewMeterRoute", Locale.US, "Meter Route");
        messageSource.addMessage("yukon.common.attribute.builtInAttribute.USAGE", Locale.US, "Usage");
    }
    YukonUserContextMessageSourceResolverMock messageSourceResolver = new YukonUserContextMessageSourceResolverMock();
    {
        messageSourceResolver.setMessageSource(messageSource);
    }
    
    ExportReportGeneratorServiceImpl exporterReportGeneratorService = new ExportReportGeneratorServiceImpl(); 
    {
        ReflectionTestUtils.setField(exporterReportGeneratorService, "attributeService", attributeService);
        ReflectionTestUtils.setField(exporterReportGeneratorService, "paoSelectionService", paoSelectionService);
        ReflectionTestUtils.setField(exporterReportGeneratorService, "messageSourceResolver", messageSourceResolver);
        ReflectionTestUtils.setField(exporterReportGeneratorService, "rawPointHistoryDao", rawPointHistoryDao);
        ReflectionTestUtils.setField(exporterReportGeneratorService, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(exporterReportGeneratorService, "unitMeasureDao", unitMeasureDao);
    }

    private final static DateTimeZone centralTimeZone = DateTimeZone.forOffsetHoursMinutes(5, 0);
    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy").withZone(centralTimeZone);
    private final static YukonUserContext userContextOne = new SimpleYukonUserContext(null, null, centralTimeZone.toTimeZone(), null);
    
    DateTime julyDateTime = dateTimeFormatter.parseDateTime("07/12/2012");
    PointValueQualityHolder pointValueQualityHolder = 
            PointValueBuilder.create().withPointId(200).withType(PulseAccumulator).withPointQuality(Manual).withValue(600).withTimeStamp(julyDateTime.toDate()).build();

    private final static ExportAttribute earliestUsageAttribute = new ExportAttribute();
    private final static ExportAttribute latestUsageAttribute = new ExportAttribute();
    private final static ExportAttribute maxPeakDemandAttribute = new ExportAttribute();
    private final static ExportAttribute minPeakKVarAttribute = new ExportAttribute();
    static {
        earliestUsageAttribute.setAttribute(USAGE);
        earliestUsageAttribute.setDataSelection(EARLIEST);
        earliestUsageAttribute.setDaysPrevious(7);
        latestUsageAttribute.setAttribute(USAGE);
        latestUsageAttribute.setDataSelection(LATEST);
        latestUsageAttribute.setDaysPrevious(7);
        maxPeakDemandAttribute.setAttribute(PEAK_DEMAND);
        maxPeakDemandAttribute.setDataSelection(MAX);
        maxPeakDemandAttribute.setDaysPrevious(4);
        minPeakKVarAttribute.setAttribute(PEAK_KVAR);
        minPeakKVarAttribute.setDataSelection(MIN);
        minPeakKVarAttribute.setDaysPrevious(4);
    }

    private final ExportFormat basicFixedFormatExport = new ExportFormat();
    {
        ExportField deviceNameExportField = getExportField(0, DEVICE_NAME);
        ExportField meterNumberField = getExportField(1, METER_NUMBER);
        ExportField earliestUsageAttriubteExportField_value = getExportField(2, ATTRIBUTE, earliestUsageAttribute, AttributeField.VALUE, "####");
        ExportField earliestUsageAttriubteExportField_ts = getExportField(3, ATTRIBUTE, earliestUsageAttribute, AttributeField.TIMESTAMP, "MM/dd/yyyy");
        ExportField maxPeakDemandAttributeExportField = getExportField(4, ATTRIBUTE, maxPeakDemandAttribute, AttributeField.VALUE, "####");
        ExportField plainTextField = getExportField(5, PLAIN_TEXT, "Plain Text");

        basicFixedFormatExport.setHeader("Device Name, Meter Number, Earliest Usage Value, Earliest Timestamp, Max Peak Demand Value, Plain Text");
        basicFixedFormatExport.setFormatType(FIXED_ATTRIBUTE);
        basicFixedFormatExport.setDelimiter(",");
        basicFixedFormatExport.setAttributes(Lists.newArrayList(earliestUsageAttribute, maxPeakDemandAttribute));
        basicFixedFormatExport.setFields(Lists.newArrayList(deviceNameExportField, meterNumberField, earliestUsageAttriubteExportField_value,
                                                       earliestUsageAttriubteExportField_ts, maxPeakDemandAttributeExportField, plainTextField));
        basicFixedFormatExport.setFooter("End File");
        basicFixedFormatExport.setDateTimeZoneFormat(TimeZoneFormat.LOCAL);
    }

    private final ExportFormat basicDyanamicFormatExport = new ExportFormat();
    {
        ExportField deviceNameExportField = getExportField(0, DEVICE_NAME);
        ExportField routeExportField = getExportField(1, ROUTE);
        ExportField attributeNameExportField = getExportField(2, ATTRIBUTE_NAME, earliestUsageAttribute);
        ExportField pointValueExportField = getExportField(3, POINT_VALUE, "####");
        ExportField pointTimestampExportField = getExportField(4, POINT_TIMESTAMP, "MM/dd/yyyy");
        ExportField plainTextExportField = getExportField(5, PLAIN_TEXT, "Plain Text");
        
        basicDyanamicFormatExport.setHeader("Device Name, Meter Route, Attribute Name, Point Value, Point Timestamp, Plain Text");
        basicDyanamicFormatExport.setFormatType(DYNAMIC_ATTRIBUTE);
        basicDyanamicFormatExport.setDelimiter(",");
        basicDyanamicFormatExport.setFields(Lists.newArrayList(deviceNameExportField, routeExportField, attributeNameExportField,
                                                               pointValueExportField, pointTimestampExportField, plainTextExportField));
        basicDyanamicFormatExport.setFooter("End File");
        basicDyanamicFormatExport.setDateTimeZoneFormat(TimeZoneFormat.LOCAL);
    }

    @Test
    public void getValue_minUsageAttribute_Test() {

        // Point Value Test
        ExportField exportFieldPointValue = getExportField(0, FieldType.POINT_VALUE, earliestUsageAttribute, AttributeField.VALUE, "#####");
        String pointValue = exporterReportGeneratorService.getValue(exportFieldPointValue, null, null, null, pointValueQualityHolder, userContextOne, tzFormat);
        
        Assert.assertEquals(pointValue, "600");

        // Timestamp Test
        ExportField exportFieldTimestamp = getExportField(0, FieldType.POINT_TIMESTAMP, earliestUsageAttribute, AttributeField.TIMESTAMP, "MM/dd/yyyy");
        String timestampValue = exporterReportGeneratorService.getValue(exportFieldTimestamp, null, null, null, pointValueQualityHolder, userContextOne, tzFormat);
        
        Assert.assertEquals(timestampValue, "07/12/2012");

        // Unit of Measure Test 
        YukonMeter meter = meterDao.getForMeterNumber("Meter Number 1");
        ExportField exportFieldUnitOfMeasure = getExportField(0, FieldType.UNIT_OF_MEASURE, earliestUsageAttribute, AttributeField.UNIT_OF_MEASURE, null);
        String unitOfMeasureValue = exporterReportGeneratorService.getValue(exportFieldUnitOfMeasure, meter, null, USAGE, pointValueQualityHolder, userContextOne, tzFormat);
        
        Assert.assertEquals(unitOfMeasureValue, "kWH");
        
        // Quality Test 
        ExportField exportFieldQuality= getExportField(0, FieldType.POINT_QUALITY, earliestUsageAttribute, AttributeField.QUALITY, null);
        String qualityValue = exporterReportGeneratorService.getValue(exportFieldQuality, null, null, null, pointValueQualityHolder, userContextOne, tzFormat);
        
        Assert.assertEquals(qualityValue, "Manual");
    }

    private PaoData makePaoDataFromMeter(YukonMeter meter) {
        PaoData paoData = new PaoData(OptionalField.SET_OF_ALL, meter.getPaoIdentifier());
        paoData.setName(meter.getName());
        paoData.setEnabled(!meter.isDisabled());
        paoData.setMeterNumber(meter.getMeterNumber());
        // paoData.setCarrierAddress(carrierAddress);
        paoData.setRouteName(meter.getRoute());
        paoData.setAddressOrSerialNumber(meter.getSerialOrAddress());
        return paoData;
    }

    @Test
    public void getValue_Meter_Test() {
        YukonMeter meter = meterDao.getForMeterNumber("Meter Number 1");
        PaoData paoData = makePaoDataFromMeter(meter);

        // Point Value Test
        ExportField meterExportFieldMeterNumber = getExportField(0, METER_NUMBER);
        String meterNumberValue = exporterReportGeneratorService.getValue(meterExportFieldMeterNumber, meter, paoData, null, pointValueQualityHolder, userContextOne, tzFormat);

        Assert.assertEquals(meterNumberValue, "Meter Number 1");
        
        ExportField meterExportFieldDeviceName = getExportField(0, DEVICE_NAME);
        String deviceNameValue = exporterReportGeneratorService.getValue(meterExportFieldDeviceName, meter, paoData, null, pointValueQualityHolder, userContextOne, tzFormat);

        Assert.assertEquals(deviceNameValue, "MCT410FL 1");
        
        ExportField meterExportFieldAddress = getExportField(0, ADDRESS);
        String addressValue = exporterReportGeneratorService.getValue(meterExportFieldAddress, meter, paoData, null, pointValueQualityHolder, userContextOne, tzFormat);

        Assert.assertEquals(addressValue, "Address A");

        YukonMeter rfnMeter = meterDao.getForMeterNumber("Meter Number 3");
        PaoData rfnPaoData = makePaoDataFromMeter(rfnMeter);
        String rfnSerialNumberValue = exporterReportGeneratorService.getValue(meterExportFieldAddress, rfnMeter, rfnPaoData, null, pointValueQualityHolder, userContextOne, tzFormat);

        Assert.assertEquals("410987654", rfnSerialNumberValue);
        
        ExportField meterExportFieldRoute = getExportField(0, ROUTE);
        String routeValue = exporterReportGeneratorService.getValue(meterExportFieldRoute, meter, paoData, null, pointValueQualityHolder, userContextOne, tzFormat);

        Assert.assertEquals(routeValue, "Route A");
    }

    @Test
    public void getValue_Null_Meter_Test() {
        YukonMeter meter = meterDao.getForMeterNumber("Null Valued Meter");
        PaoData paoData = makePaoDataFromMeter(meter);
        
        ExportField meterExportFieldAddress = getExportField(0, ADDRESS);
        String addressValue = exporterReportGeneratorService.getValue(meterExportFieldAddress, meter, paoData, null, pointValueQualityHolder, userContextOne, tzFormat);

        Assert.assertEquals("", addressValue);
        
        ExportField meterExportFieldRoute = getExportField(0, ROUTE);
        String routeValue = exporterReportGeneratorService.getValue(meterExportFieldRoute, meter, paoData, null, pointValueQualityHolder, userContextOne, tzFormat);

        Assert.assertEquals( "", routeValue);
    }

    @Test
    public void getValue_Plain_Test() {
        ExportField exportField = getExportField(0, PLAIN_TEXT, "This is plain text");
        String plainTextValue = exporterReportGeneratorService.getValue(exportField, null, null, null, pointValueQualityHolder, userContextOne, tzFormat);
        
        Assert.assertEquals(plainTextValue, "This is plain text");
    }
    
    @Test
    public void generateFixedFormatPreview_Test() {
        Preview preview = exporterReportGeneratorService.generatePreview(basicFixedFormatExport, userContextOne);

        Assert.assertEquals("Device Name, Meter Number, Earliest Usage Value, Earliest Timestamp, Max Peak Demand Value, Plain Text", preview.getHeader());
        Assert.assertEquals("Meter Name,Meter Number,1234546," + dateTimeFormatter.print(Instant.now()) + ",1234546,Plain Text", preview.getBody());
        Assert.assertEquals("End File", preview.getFooter());
    }

    @Test
    public void generateDyanamicFormatPreview_Test() {
        Preview preview = exporterReportGeneratorService.generatePreview(basicDyanamicFormatExport, userContextOne);

        Assert.assertEquals("Device Name, Meter Route, Attribute Name, Point Value, Point Timestamp, Plain Text", preview.getHeader());
        Assert.assertEquals("Meter Name,Meter Route,Usage,1234546," + dateTimeFormatter.print(Instant.now()) + ",Plain Text", preview.getBody());
        Assert.assertEquals("End File", preview.getFooter());
    }

    @Test
    public void generateFixedFormat_Test() {
        List<YukonMeter> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        meters.add(meterDao.getForMeterNumber("Meter Number 2"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.END_DATE);
        dataRange.setEndDate(dateTimeFormatter.parseLocalDate("07/16/2012"));
        
        List<String> previewReportRows = exporterReportGeneratorService.generateReport(meters, basicFixedFormatExport,
            dataRange, userContextOne, new Attribute[] {});

        Assert.assertEquals("Device Name, Meter Number, Earliest Usage Value, Earliest Timestamp, Max Peak Demand Value, Plain Text", previewReportRows.get(0));
        Assert.assertEquals("MCT410FL 1,Meter Number 1,600,07/12/2012,25,Plain Text", previewReportRows.get(1));
        Assert.assertEquals("MCT410IL 2,Meter Number 2,600,07/12/2012,25,Plain Text", previewReportRows.get(2));
        Assert.assertEquals("End File", previewReportRows.get(3));
    }

    @Test
    public void generateDyanamicFormat_noAttributes_Test() {
        List<YukonMeter> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        meters.add(meterDao.getForMeterNumber("Meter Number 2"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.DATE_RANGE);
        LocalDateRange localDateRange = new LocalDateRange();
        localDateRange.setStartDate(dateTimeFormatter.parseLocalDate("07/13/2012"));
        localDateRange.setEndDate(dateTimeFormatter.parseLocalDate("07/18/2012"));
        dataRange.setLocalDateRange(localDateRange);
        
        List<String> previewReportRows = exporterReportGeneratorService.generateReport(meters,
            basicDyanamicFormatExport, dataRange, userContextOne, new Attribute[] {});

        Assert.assertEquals("Device Name, Meter Route, Attribute Name, Point Value, Point Timestamp, Plain Text", previewReportRows.get(0));
        Assert.assertEquals("End File", previewReportRows.get(1));
    }
    
    @Test
    public void generateDyanamicFormatByDateRange_Test() {
        List<YukonPao> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        meters.add(meterDao.getForMeterNumber("Meter Number 2"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.DATE_RANGE);
        LocalDateRange localDateRange = new LocalDateRange();
        localDateRange.setStartDate(dateTimeFormatter.parseLocalDate("07/15/2012"));
        localDateRange.setEndDate(dateTimeFormatter.parseLocalDate("07/18/2012"));
        dataRange.setLocalDateRange(localDateRange);
        
        List<String> previewReportRows =
                exporterReportGeneratorService.generateReport(meters, basicDyanamicFormatExport, dataRange,
                    userContextOne, new Attribute[] { USAGE, DEMAND });

        Assert.assertEquals("Device Name, Meter Route, Attribute Name, Point Value, Point Timestamp, Plain Text", previewReportRows.get(0));
        Assert.assertEquals("MCT410FL 1,Route A,Usage,661,07/15/2012,Plain Text", previewReportRows.get(1));
        Assert.assertEquals("MCT410FL 1,Route A,Usage,670,07/16/2012,Plain Text", previewReportRows.get(2));
        Assert.assertEquals("MCT410FL 1,Route A,Usage,675,07/17/2012,Plain Text", previewReportRows.get(3));
        Assert.assertEquals("MCT410IL 2,Route A,Usage,661,07/15/2012,Plain Text", previewReportRows.get(4));
        Assert.assertEquals("MCT410IL 2,Route A,Usage,670,07/16/2012,Plain Text", previewReportRows.get(5));
        Assert.assertEquals("MCT410IL 2,Route A,Usage,675,07/17/2012,Plain Text" , previewReportRows.get(6));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,15,07/15/2012,Plain Text", previewReportRows.get(7));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,10,07/16/2012,Plain Text", previewReportRows.get(8));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,5,07/17/2012,Plain Text", previewReportRows.get(9));
        Assert.assertEquals("MCT410IL 2,Route A,Demand,15,07/15/2012,Plain Text", previewReportRows.get(10));
        Assert.assertEquals("MCT410IL 2,Route A,Demand,10,07/16/2012,Plain Text", previewReportRows.get(11));
        Assert.assertEquals("MCT410IL 2,Route A,Demand,5,07/17/2012,Plain Text", previewReportRows.get(12));
        Assert.assertEquals("End File", previewReportRows.get(13));
    }

    
    @Test
    public void generateDyanamicFormatByChangeId_Test() {
        List<YukonMeter> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        meters.add(meterDao.getForMeterNumber("Meter Number 2"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.SINCE_LAST_CHANGE_ID);
        ChangeIdRange changeIdRange = new ChangeIdRange();
        changeIdRange.setFirstChangeId(5);
        changeIdRange.setLastChangeId(12);
        dataRange.setChangeIdRange(changeIdRange);
        
        List<String> previewReportRows = exporterReportGeneratorService.generateReport(meters,
            basicDyanamicFormatExport, dataRange, userContextOne, new Attribute[] { USAGE, DEMAND });

        Assert.assertEquals("Device Name, Meter Route, Attribute Name, Point Value, Point Timestamp, Plain Text", previewReportRows.get(0));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,20,07/12/2012,Plain Text", previewReportRows.get(1));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,20,07/13/2012,Plain Text", previewReportRows.get(2));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,25,07/14/2012,Plain Text", previewReportRows.get(3));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,15,07/15/2012,Plain Text", previewReportRows.get(4));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,10,07/16/2012,Plain Text", previewReportRows.get(5));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,5,07/17/2012,Plain Text", previewReportRows.get(6));
        Assert.assertEquals("MCT410IL 2,Route A,Demand,20,07/12/2012,Plain Text", previewReportRows.get(7));
        Assert.assertEquals("MCT410IL 2,Route A,Demand,20,07/13/2012,Plain Text", previewReportRows.get(8));
        Assert.assertEquals("MCT410IL 2,Route A,Demand,25,07/14/2012,Plain Text" , previewReportRows.get(9));
        Assert.assertEquals("MCT410IL 2,Route A,Demand,15,07/15/2012,Plain Text", previewReportRows.get(10));
        Assert.assertEquals("MCT410IL 2,Route A,Demand,10,07/16/2012,Plain Text", previewReportRows.get(11));
        Assert.assertEquals("MCT410IL 2,Route A,Demand,5,07/17/2012,Plain Text", previewReportRows.get(12));
        Assert.assertEquals("End File", previewReportRows.get(13));
    }

    @Test
    public void generateDyanamicFormatByDaysPrevious_Test() {
        List<YukonMeter> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.DAYS_PREVIOUS);
        dataRange.setDaysPrevious(36500);  // This isn't the greatest way to test it, but it should at least do some rough testing for 100 years.
        
        List<String> previewReportRows = exporterReportGeneratorService.generateReport(meters,
            basicDyanamicFormatExport, dataRange, userContextOne, new Attribute[] { USAGE, DEMAND });

        Assert.assertEquals("Device Name, Meter Route, Attribute Name, Point Value, Point Timestamp, Plain Text", previewReportRows.get(0));
        Assert.assertEquals("MCT410FL 1,Route A,Usage,600,07/12/2012,Plain Text", previewReportRows.get(1));
        Assert.assertEquals("MCT410FL 1,Route A,Usage,620,07/13/2012,Plain Text", previewReportRows.get(2));
        Assert.assertEquals("MCT410FL 1,Route A,Usage,646,07/14/2012,Plain Text", previewReportRows.get(3));
        Assert.assertEquals("MCT410FL 1,Route A,Usage,661,07/15/2012,Plain Text", previewReportRows.get(4));
        Assert.assertEquals("MCT410FL 1,Route A,Usage,670,07/16/2012,Plain Text", previewReportRows.get(5));
        Assert.assertEquals("MCT410FL 1,Route A,Usage,675,07/17/2012,Plain Text" , previewReportRows.get(6));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,20,07/12/2012,Plain Text", previewReportRows.get(7));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,20,07/13/2012,Plain Text", previewReportRows.get(8));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,25,07/14/2012,Plain Text", previewReportRows.get(9));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,15,07/15/2012,Plain Text", previewReportRows.get(10));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,10,07/16/2012,Plain Text", previewReportRows.get(11));
        Assert.assertEquals("MCT410FL 1,Route A,Demand,5,07/17/2012,Plain Text", previewReportRows.get(12));
        Assert.assertEquals("End File", previewReportRows.get(13));
    }

    /**
     * This method returns an ExportField with the supplied fieldType and then uses the defaults from the getExportFieldBase for the rest of the fields.
     */
    private ExportField getExportField(int fieldId, FieldType fieldType) {
        ExportField exportField = getExportFieldBase(fieldId);

        exportField.getField().setType(fieldType);
        
        return exportField;
    }
    
    /**
     * This method returns an ExportField with the supplied fieldType and plainText and then uses
     * the defaults from the getExportFieldBase for the rest of the fields.
     */
    private ExportField getExportField(int fieldId, FieldType fieldType, String pattern) {
        ExportField exportField = getExportFieldBase(fieldId);

        exportField.getField().setType(fieldType);
        exportField.setPattern(pattern);

        return exportField;
    }

    /**
     * This method returns an ExportField with the supplied fieldType and plainText and then uses
     * the defaults from the getExportFieldBase for the rest of the fields.
     */
    private ExportField getExportField(int fieldId, FieldType fieldType, ExportAttribute exportAttribute) {
        ExportField exportField = getExportFieldBase(fieldId);

        exportField.getField().setType(fieldType);
        exportField.getField().setAttribute(exportAttribute);

        return exportField;
    }
    
    /**
     * This method returns an ExportField with the supplied exportAttribute, pattern, and fieldType and then uses
     * the defaults from the getExportFieldBase for the rest of the fields.
     */
    private ExportField getExportField(int fieldId, FieldType fieldType, ExportAttribute exportAttribute, AttributeField attributeField, String pattern) {
        ExportField exportField = getExportFieldBase(fieldId);
        
        exportField.getField().setType(fieldType);
        exportField.setAttributeField(attributeField);
        exportField.getField().setAttribute(exportAttribute);
        exportField.setPattern(pattern);
        
        return exportField;
    }
    
    /**
     * This method returns a default base of an exportField that can be used an overwritten to make a usable ExportField.
     */
    private ExportField getExportFieldBase(int fieldId) {
        
        ExportField exportField = new ExportField();
        exportField.setField(new Field());
        exportField.setFieldId(fieldId);
        exportField.setMaxLength(0);
        exportField.setPadSide(NONE);
        exportField.setRoundingMode(HALF_UP);
        
        return exportField;
    }
}