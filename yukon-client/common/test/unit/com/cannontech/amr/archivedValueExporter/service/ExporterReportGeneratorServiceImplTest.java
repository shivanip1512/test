package com.cannontech.amr.archivedValueExporter.service;

import static com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType.DYNAMIC_ATTRIBUTE;
import static com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType.FIXED_ATTRIBUTE;
import static com.cannontech.amr.archivedValueExporter.model.DataSelection.EARLIEST;
import static com.cannontech.amr.archivedValueExporter.model.DataSelection.LATEST;
import static com.cannontech.amr.archivedValueExporter.model.DataSelection.MAX;
import static com.cannontech.amr.archivedValueExporter.model.DataSelection.MIN;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.ADDRESS;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.ATTRIBUTE;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.ATTRIBUTE_NAME;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.DEVICE_NAME;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.METER_NUMBER;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.PLAIN_TEXT;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.POINT_TIMESTAMP;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.POINT_VALUE;
import static com.cannontech.amr.archivedValueExporter.model.FieldType.ROUTE;
import static com.cannontech.amr.archivedValueExporter.model.PadSide.NONE;
import static com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode.HALF_UP;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.DEMAND;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.PEAK_DEMAND;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.PEAK_KVAR;
import static com.cannontech.common.pao.attribute.model.BuiltInAttribute.USAGE;
import static com.cannontech.common.point.PointQuality.Manual;
import static com.cannontech.database.data.point.PointType.PulseAccumulator;

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
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.ChangeIdRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.LocalDateRange;
import com.cannontech.amr.archivedValueExporter.service.impl.ExportReportGeneratorServiceImpl;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.dao.MockMeterDaoImpl;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.amr.rfn.dao.MockRfnDeviceDaoImpl;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.MockAttributeServiceImpl;
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
    private AttributeService attributeService = new MockAttributeServiceImpl();
    private MeterDao meterDao = new MockMeterDaoImpl();
    private RfnDeviceDao rfnDeviceDao = new MockRfnDeviceDaoImpl();
    private RawPointHistoryDao rawPointHistoryDao = new MockRawPointHistoryDaoImpl();
    private UnitMeasureDao unitMeasureDao = new MockUnitMeasureDaoImpl();
    
    StaticMessageSource messageSource = new StaticMessageSource();
    {
        messageSource.addMessage("yukon.web.modules.amr.archivedValueExporter.previewUOMValue", Locale.US, "UnitOfMeasure");
        messageSource.addMessage("yukon.web.modules.amr.archivedValueExporter.previewMeterNumber", Locale.US, "Meter Number");
        messageSource.addMessage("yukon.web.modules.amr.archivedValueExporter.previewMeterName", Locale.US, "Meter Name");
        messageSource.addMessage("yukon.web.modules.amr.archivedValueExporter.previewMeterAddress", Locale.US, "Meter Address");
        messageSource.addMessage("yukon.web.modules.amr.archivedValueExporter.previewMeterRoute", Locale.US, "Meter Route");
        messageSource.addMessage("yukon.common.attribute.builtInAttribute.USAGE", Locale.US, "Usage");
    }
    YukonUserContextMessageSourceResolverMock messageSourceResolver = new YukonUserContextMessageSourceResolverMock();
    {
        messageSourceResolver.setMessageSource(messageSource);
    }
    
    ExportReportGeneratorServiceImpl exporterReportGeneratorService = new ExportReportGeneratorServiceImpl(); 
    {
        ReflectionTestUtils.setField(exporterReportGeneratorService, "attributeService", attributeService);
        ReflectionTestUtils.setField(exporterReportGeneratorService, "messageSourceResolver", messageSourceResolver);
        ReflectionTestUtils.setField(exporterReportGeneratorService, "rawPointHistoryDao", rawPointHistoryDao);
        ReflectionTestUtils.setField(exporterReportGeneratorService, "rfnDeviceDao", rfnDeviceDao);
        ReflectionTestUtils.setField(exporterReportGeneratorService, "unitMeasureDao", unitMeasureDao);
    }

    private static final DateTimeZone centralTimeZone = DateTimeZone.forOffsetHoursMinutes(5, 0);
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("MM/dd/yyyy").withZone(centralTimeZone);
    private static final YukonUserContext userContextOne = new SimpleYukonUserContext(null, null, centralTimeZone.toTimeZone(), null);
    
    DateTime julyDateTime = dateTimeFormatter.parseDateTime("07/12/2012");
    PointValueQualityHolder pointValueQualityHolder = 
            PointValueBuilder.create().withPointId(200).withType(PulseAccumulator).withPointQuality(Manual).withValue(600).withTimeStamp(julyDateTime.toDate()).build();
    
    private static final ExportAttribute earliestUsageAttribute = new ExportAttribute(USAGE, EARLIEST, 7);
    private static final ExportAttribute latestUsageAttribute = new ExportAttribute(USAGE, LATEST, 7);
    private static final ExportAttribute maxPeakDemandAttribute = new ExportAttribute(PEAK_DEMAND, MAX, 4);
    private static final ExportAttribute minPeakKVarAttribute = new ExportAttribute(PEAK_KVAR, MIN, 4);
    
    
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
    }

    @Test
    public void getValue_minUsageAttribute_Test() {

        // Point Value Test
        ExportField exportFieldPointValue = getExportField(0, FieldType.POINT_VALUE, earliestUsageAttribute, AttributeField.VALUE, "#####");
        String pointValue = exporterReportGeneratorService.getValue(exportFieldPointValue, null, null, pointValueQualityHolder, userContextOne);
        
        Assert.assertEquals(pointValue, "600");

        // Timestamp Test
        ExportField exportFieldTimestamp = getExportField(0, FieldType.POINT_TIMESTAMP, earliestUsageAttribute, AttributeField.TIMESTAMP, "MM/dd/yyyy");
        String timestampValue = exporterReportGeneratorService.getValue(exportFieldTimestamp, null, null, pointValueQualityHolder, userContextOne);
        
        Assert.assertEquals(timestampValue, "07/12/2012");

        // Unit of Measure Test 
        Meter meter = meterDao.getForMeterNumber("Meter Number 1");
        ExportField exportFieldUnitOfMeasure = getExportField(0, FieldType.UNIT_OF_MEASURE, earliestUsageAttribute, AttributeField.UNIT_OF_MEASURE, null);
        String unitOfMeasureValue = exporterReportGeneratorService.getValue(exportFieldUnitOfMeasure, meter, USAGE, pointValueQualityHolder, userContextOne);
        
        Assert.assertEquals(unitOfMeasureValue, "kWH");
        
        // Quality Test 
        ExportField exportFieldQuality= getExportField(0, FieldType.POINT_QUALITY, earliestUsageAttribute, AttributeField.QUALITY, null);
        String qualityValue = exporterReportGeneratorService.getValue(exportFieldQuality, null, null, pointValueQualityHolder, userContextOne);
        
        Assert.assertEquals(qualityValue, "Manual");
    }

    @Test
    public void getValue_Meter_Test() {
        Meter meter = meterDao.getForMeterNumber("Meter Number 1");
        
        // Point Value Test
        ExportField meterExportFieldMeterNumber = getExportField(0, METER_NUMBER);
        String meterNumberValue = exporterReportGeneratorService.getValue(meterExportFieldMeterNumber, meter, null, pointValueQualityHolder, userContextOne);

        Assert.assertEquals(meterNumberValue, "Meter Number 1");
        
        ExportField meterExportFieldDeviceName = getExportField(0, DEVICE_NAME);
        String deviceNameValue = exporterReportGeneratorService.getValue(meterExportFieldDeviceName, meter, null, pointValueQualityHolder, userContextOne);

        Assert.assertEquals(deviceNameValue, "MCT410FL 1");
        
        ExportField meterExportFieldAddress = getExportField(0, ADDRESS);
        String addressValue = exporterReportGeneratorService.getValue(meterExportFieldAddress, meter, null, pointValueQualityHolder, userContextOne);

        Assert.assertEquals(addressValue, "Address A");

        Meter rfnMeter = meterDao.getForMeterNumber("Meter Number 3");
        String rfnSerialNumberValue = exporterReportGeneratorService.getValue(meterExportFieldAddress, rfnMeter, null, pointValueQualityHolder, userContextOne);

        Assert.assertEquals("410987654", rfnSerialNumberValue);
        
        ExportField meterExportFieldRoute = getExportField(0, ROUTE);
        String routeValue = exporterReportGeneratorService.getValue(meterExportFieldRoute, meter, null, pointValueQualityHolder, userContextOne);

        Assert.assertEquals(routeValue, "Route A");
    }

    @Test
    public void getValue_Null_Meter_Test() {
        Meter meter = meterDao.getForMeterNumber("Null Valued Meter");
        
        ExportField meterExportFieldAddress = getExportField(0, ADDRESS);
        String addressValue = exporterReportGeneratorService.getValue(meterExportFieldAddress, meter, null, pointValueQualityHolder, userContextOne);

        Assert.assertEquals("", addressValue);
        
        ExportField meterExportFieldRoute = getExportField(0, ROUTE);
        String routeValue = exporterReportGeneratorService.getValue(meterExportFieldRoute, meter, null, pointValueQualityHolder, userContextOne);

        Assert.assertEquals( "", routeValue);
    }

    @Test
    public void getValue_Plain_Test() {
        ExportField exportField = getExportField(0, PLAIN_TEXT, "This is plain text");
        String plainTextValue = exporterReportGeneratorService.getValue(exportField, null, null, pointValueQualityHolder, userContextOne);
        
        Assert.assertEquals(plainTextValue, "This is plain text");
    }
    
    @Test
    public void generateFixedFormatPreview_Test() {
        List<String> previewReportRows = exporterReportGeneratorService.generatePreview(basicFixedFormatExport, userContextOne);

        Assert.assertEquals("Device Name, Meter Number, Earliest Usage Value, Earliest Timestamp, Max Peak Demand Value, Plain Text", previewReportRows.get(0));
        Assert.assertEquals("Meter Name,Meter Number,1234546,"+dateTimeFormatter.print(Instant.now())+",1234546,Plain Text", previewReportRows.get(1));
        Assert.assertEquals("End File", previewReportRows.get(2));
    }

    @Test
    public void generateDyanamicFormatPreview_Test() {
        List<String> previewReportRows = exporterReportGeneratorService.generatePreview(basicDyanamicFormatExport, userContextOne);

        Assert.assertEquals("Device Name, Meter Route, Attribute Name, Point Value, Point Timestamp, Plain Text", previewReportRows.get(0));
        Assert.assertEquals("Meter Name,Meter Route,Usage,1234546,"+dateTimeFormatter.print(Instant.now())+",Plain Text", previewReportRows.get(1));
        Assert.assertEquals("End File", previewReportRows.get(2));
    }

    @Test
    public void generateFixedFormat_Test() {
        List<Meter> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        meters.add(meterDao.getForMeterNumber("Meter Number 2"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.END_DATE);
        dataRange.setEndDate(dateTimeFormatter.parseLocalDate("07/16/2012"));
        
        List<String> previewReportRows = exporterReportGeneratorService.generateReport(meters, basicFixedFormatExport, dataRange, userContextOne);

        Assert.assertEquals("Device Name, Meter Number, Earliest Usage Value, Earliest Timestamp, Max Peak Demand Value, Plain Text", previewReportRows.get(0));
        Assert.assertEquals("MCT410FL 1,Meter Number 1,600,07/12/2012,25,Plain Text", previewReportRows.get(1));
        Assert.assertEquals("MCT410IL 2,Meter Number 2,600,07/12/2012,25,Plain Text", previewReportRows.get(2));
        Assert.assertEquals("End File", previewReportRows.get(3));
    }

    @Test
    public void generateDyanamicFormat_noAttributes_Test() {
        List<Meter> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        meters.add(meterDao.getForMeterNumber("Meter Number 2"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.DATE_RANGE);
        LocalDateRange localDateRange = new LocalDateRange();
        localDateRange.setStartDate(dateTimeFormatter.parseLocalDate("07/13/2012"));
        localDateRange.setEndDate(dateTimeFormatter.parseLocalDate("07/18/2012"));
        dataRange.setLocalDateRange(localDateRange);
        
        List<String> previewReportRows = exporterReportGeneratorService.generateReport(meters, basicDyanamicFormatExport, dataRange, userContextOne);

        Assert.assertEquals("Device Name, Meter Route, Attribute Name, Point Value, Point Timestamp, Plain Text", previewReportRows.get(0));
        Assert.assertEquals("End File", previewReportRows.get(1));
    }
    
    @Test
    public void generateDyanamicFormatByDateRange_Test() {
        List<Meter> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        meters.add(meterDao.getForMeterNumber("Meter Number 2"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.DATE_RANGE);
        LocalDateRange localDateRange = new LocalDateRange();
        localDateRange.setStartDate(dateTimeFormatter.parseLocalDate("07/15/2012"));
        localDateRange.setEndDate(dateTimeFormatter.parseLocalDate("07/18/2012"));
        dataRange.setLocalDateRange(localDateRange);
        
        List<String> previewReportRows = exporterReportGeneratorService.generateReport(meters, basicDyanamicFormatExport, dataRange, userContextOne, USAGE, DEMAND);

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
        List<Meter> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        meters.add(meterDao.getForMeterNumber("Meter Number 2"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.SINCE_LAST_CHANGE_ID);
        ChangeIdRange changeIdRange = new ChangeIdRange();
        changeIdRange.setFirstChangeId(5);
        changeIdRange.setLastChangeId(12);
        dataRange.setChangeIdRange(changeIdRange);
        
        List<String> previewReportRows = exporterReportGeneratorService.generateReport(meters, basicDyanamicFormatExport, dataRange, userContextOne, USAGE, DEMAND);

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
        List<Meter> meters = new ArrayList<>();
        meters.add(meterDao.getForMeterNumber("Meter Number 1"));
        
        DataRange dataRange = new DataRange();
        dataRange.setDataRangeType(DataRangeType.DAYS_PREVIOUS);
        dataRange.setDaysPrevious(36500);  // This isn't the greatest way to test it, but it should at least do some rough testing for 100 years.
        
        List<String> previewReportRows = exporterReportGeneratorService.generateReport(meters, basicDyanamicFormatExport, dataRange, userContextOne, USAGE, DEMAND);

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

        exportField.setFieldType(fieldType);
        
        return exportField;
    }
    
    /**
     * This method returns an ExportField with the supplied fieldType and plainText and then uses
     * the defaults from the getExportFieldBase for the rest of the fields.
     */
    private ExportField getExportField(int fieldId, FieldType fieldType, String pattern) {
        ExportField exportField = getExportFieldBase(fieldId);

        exportField.setFieldType(fieldType);
        exportField.setPattern(pattern);

        return exportField;
    }

    /**
     * This method returns an ExportField with the supplied fieldType and plainText and then uses
     * the defaults from the getExportFieldBase for the rest of the fields.
     */
    private ExportField getExportField(int fieldId, FieldType fieldType, ExportAttribute exportAttribute) {
        ExportField exportField = getExportFieldBase(fieldId);

        exportField.setFieldType(fieldType);
        exportField.setAttribute(exportAttribute);

        return exportField;
    }
    
    /**
     * This method returns an ExportField with the supplied exportAttribute, pattern, and fieldType and then uses
     * the defaults from the getExportFieldBase for the rest of the fields.
     */
    private ExportField getExportField(int fieldId, FieldType fieldType, ExportAttribute exportAttribute, AttributeField attributeField, String pattern) {
        ExportField exportField = getExportFieldBase(fieldId);
        
        exportField.setFieldType(fieldType);
        exportField.setAttributeField(attributeField);
        exportField.setAttribute(exportAttribute);
        exportField.setPattern(pattern);
        
        return exportField;
    }
    
    /**
     * This method returns a default base of an exportField that can be used an overwritten to make a usable ExportField.
     */
    private ExportField getExportFieldBase(int fieldId) {
        ExportField exportField = new ExportField();
        
        exportField.setFieldId(fieldId);
        exportField.setMaxLength(0);
        exportField.setPadSide(NONE);
        exportField.setRoundingMode(HALF_UP);
        
        return exportField;
    }
}