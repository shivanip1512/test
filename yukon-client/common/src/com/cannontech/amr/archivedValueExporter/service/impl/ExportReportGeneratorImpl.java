package com.cannontech.amr.archivedValueExporter.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.point.PointQuality;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

// TODO: Auto-generated Javadoc
/**
 * The Class ExportReportGeneratorImpl.
 */
public class ExportReportGeneratorImpl implements ExportReportGeneratorService {

    @Autowired private AttributeService attributeService;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    public static String baseKey = "yukon.web.modules.amr.archivedValueExporter.";

    private static String previewUOMValueKey = baseKey + "previewUOMValue";
    private static String previewMeterNumberKey = baseKey + "previewMeterNumber";
    private static String previewMeterNameKey = baseKey + "previewMeterName";
    private static String previewMeterAddressKey = baseKey + "previewMeterAddress";
    private static String previewMeterRouteKey = baseKey + "previewMeterRoute";


    /*The value to be returned in case the meter information we
    were looking for was not found, and the user elected to skip the record.
    This string is defined as SKIP_RECORD_SKIP_RECORD because the value SKIP_RECORD might be selected
    as a Fixed Value, and SKIP_RECORD_SKIP_RECORD is longer than the 20 characters allowed for MissingAttributeValue, thus
    making it impossible to be used as Fixed Value. 
    */
    private static String SKIP_RECORD = "SKIP_RECORD_SKIP_RECORD";

    private static double previewValue = 1234546.012;
    private static PointQuality previewQuality = PointQuality.Normal;
    private static int fakeDeviceId = -1;
    
    public List<String> generatePreview(ExportFormat format, YukonUserContext userContext) {
        Meter previewMeter = getDefaultMeter(userContext);

        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData =
            getPreviewAttributeData(format, previewMeter);

        List<String> report = Lists.newArrayList();
        report = generate(Collections.singletonList(previewMeter), format, attributeData, userContext);
        
        return report;
    }


    public List<String> generateReport(List<Meter> meters, ExportFormat format, Date stopDate,
                                       YukonUserContext userContext) {

        DateTime stopDateTime = new DateTime(stopDate, userContext.getJodaTimeZone());

        // <field id, device data>
        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData =
            getAttributeData(meters, format, stopDateTime);
        
        List<String> report = generate(meters, format, attributeData, userContext);
        return report;
    }
 
    
    /**
     * Builds and returns a list of strings each representing one row of data for the report.
     * Adds the header (opt.), meter read data, and footer (opt.).
     * @param meters
     * @param format
     * @param attributeData
     * @param userContext
     * @return
     */
    private List<String> generate(List<Meter> meters, ExportFormat format, 
            Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData, 
            YukonUserContext userContext) {

        List<String> report = Lists.newArrayList();
        // Build report rows
        // add header
        if (StringUtils.isNotEmpty(format.getHeader())) {
            report.add(format.getHeader());
        }

        for (Meter meter : meters) {
            // for each meter create a row
            String dataRow = getDataRow(format, meter, userContext, attributeData);
            if (!dataRow.equals(SKIP_RECORD)) {
                report.add(dataRow);
            }
        }
        
        // add footer
        if (StringUtils.isNotEmpty(format.getFooter())) {
            report.add(format.getFooter());
        }
        return report;
    }

    /**
     * This method builds preview attribute data from localized preview values. 
     *
     * @param format
     * @return 
     */
    private Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> getPreviewAttributeData(ExportFormat format, Meter previewMeter) {
        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData =
            new HashMap<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>>();
        for (ExportField field : format.getFields()) {
            if (field.getFieldType() == FieldType.ATTRIBUTE) {
                ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeDataValues =
                    ArrayListMultimap.create();
                PointData value = new PointData();
                value.setPointQuality(previewQuality);
                value.setTime(new Date());
                value.setValue(previewValue);
                attributeDataValues.put(previewMeter.getPaoIdentifier(), value);
                attributeData.put(field.getFieldId(), attributeDataValues);
            }
        }
        return attributeData;
    }

    /**
     * This method builds attribute data. For each field with a field type of an attribute, the meter data is gathered.
     * Format defines the rules on how the data should be selected. After the data is selected, it is stored in map with the key 
     * - fieldId and the values - the data for all the devices (meters) selected by the user.
     *
     * @param meters
     * @param format
     * @param stopDate
     * @return 
     */
    private Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> getAttributeData(List<Meter> meters,
                                                                                                ExportFormat format,
                                                                                                DateTime stopDate) {
    	
        /*Adding one day to the stop date so the end date is “inclusive” of the entire end date selected.
        (including the “midnight” value for the day selected)*/
       stopDate = stopDate.plusDays(1);
       
        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData =
            new HashMap<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>>();
        for (ExportField field : format.getFields()) {
            if (field.getFieldType() == FieldType.ATTRIBUTE) {

                Order order = null;
                OrderBy orderBy = null;

                switch (field.getAttribute().getDataSelection()) {
                case EARLIEST:
                    order = Order.FORWARD;
                    orderBy = OrderBy.TIMESTAMP;
                    break;
                case MIN:
                    order = Order.FORWARD;
                    orderBy = OrderBy.VALUE;
                    break;
                case MAX:
                    order = Order.REVERSE;
                    orderBy = OrderBy.VALUE;
                    break;
                case LATEST:
                    order = Order.REVERSE;
                    orderBy = OrderBy.TIMESTAMP;
                    break;
                }
                DateTime startDate = getStartDate(field.getAttribute(), stopDate);
                
                ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeDataValues =
                    rawPointHistoryDao.getLimitedAttributeData(meters,
                                                               field.getAttribute().getAttribute(),
                                                               startDate.toDate(),
                                                               stopDate.toDate(),
                                                               1,
                                                               false,
                                                               Clusivity.EXCLUSIVE_INCLUSIVE,
                                                               order,
                                                               orderBy);
                attributeData.put(field.getFieldId(), attributeDataValues);
            }
        }
        return attributeData;
    }

    /**
     * Builds the data row for the report. If the the value was not found, and a user
     * selected to skip a record, it returns "SKIP_RECORD_SKIP_RECORD".
     *
     * @param format
     * @param meter
     * @param timeZone
     * @param attributeData
     * @return 
     */
    private String getDataRow(ExportFormat format,
                              Meter meter,
                              YukonUserContext userContext,
                              Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData) {
        StringBuilder dataRow = new StringBuilder();
        for (int i = 0; i < format.getFields().size(); i++) {
            ExportField field = format.getFields().get(i);
            String value = getValue(meter, field, userContext, attributeData);
            if (StringUtils.isEmpty(value)) {
                // missing value
                switch (field.getMissingAttribute()) {
                case LEAVE_BLANK:
                    break;
                case FIXED_VALUE:
                    // User have selected a missing attribute value to display in case value was not found
                    value = field.getMissingAttributeValue();
                    break;
                    //Skipping the record
                case SKIP_RECORD:
                    return SKIP_RECORD;
                }
            }
            dataRow.append(field.padValue(value));
            if (i != format.getFields().size() - 1) {
                dataRow.append(format.getDelimiter());
            }
        }
        return dataRow.toString();
    }

    /**
     * Gets the value to display in the report. Returns "" if the value was not found.
     *
     * @param meter
     * @param field
     * @param timeZone
     * @param attributeData
     * @return
     */
    private String getValue(Meter meter,
                            ExportField field,
                            YukonUserContext userContext,
                            Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData) {
        String value = "";
        switch (field.getFieldType()) {
        case METER_NUMBER:
            value = meter.getMeterNumber();
            break;
        case DEVICE_NAME:
            value = meter.getName();
            break;
        case ADDRESS:
            value = meter.getAddress();
            break;
        case PLAIN_TEXT:
            value = field.getPattern();
            break;
        case ROUTE:
            value = meter.getRoute();
            break;
        /*
         * case RF_MANUFACTURER:
         * case RF_MODEL:
         * case RF_SERIAL_NUMBER:
         * break;
         */
        case ATTRIBUTE:
            PointValueQualityHolder pointValueQualityHolder =
                findPointValueQualityHolder(meter, field, attributeData);
            switch (field.getAttributeField()) {
            case UNIT_OF_MEASURE:
                value = getUOMValue(meter, field, userContext);
                break;
            case VALUE:
                value = getValue(field, pointValueQualityHolder);
                break;
            case TIMESTAMP:
                value = getTimestamp(field, userContext.getJodaTimeZone(), pointValueQualityHolder);
                break;
            case QUALITY:
                value = getQuality(field, pointValueQualityHolder);
                break;
            }
        }
        return value;
    }

    /**
     * Finds point value quality holder for a meter. This method looks at the attribute map. First, it finds the collection
     * of meter data by the fieldId, then it finds point value quality holder using the paoIdentifier from the meter. 
     * If Point value quality holder was not found, null is returned.
     *
     * @param meter
     * @param field
     * @param attributeData
     * @return
     */
    private PointValueQualityHolder findPointValueQualityHolder(Meter meter,
                                                                ExportField field,
                                                                Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData) {
        PointValueQualityHolder pointValueQualityHolder = null;
        if (!attributeData.isEmpty()) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> data =
                attributeData.get(field.getFieldId());
            if (!data.isEmpty()) {
                List<PointValueQualityHolder> pointValueQualityHolders =
                    data.get(meter.getPaoIdentifier());
                if (pointValueQualityHolders != null && !pointValueQualityHolders.isEmpty()) {
                    pointValueQualityHolder = data.get(meter.getPaoIdentifier()).iterator().next();
                }
            }
        }
        return pointValueQualityHolder;
    }

    /**
     * Gets the quality. Returns "" if the quality was not found.
     *
     * @param field
     * @param pointValueQualityHolder
     * @return 
     */
    private String getQuality(ExportField field, PointValueQualityHolder pointValueQualityHolder) {
        String value = "";
        if (pointValueQualityHolder != null) {
            value = pointValueQualityHolder.getPointQuality().getDescription();
        }
        return value;
    }

    /**
     * Gets the value. Returns "" if the value was not found.
     *
     * @param field the field
     * @param pointValueQualityHolder
     * @return the value
     */
    private String getValue(ExportField field,
                            PointValueQualityHolder pointValueQualityHolder) {
        String formattedValue = "";
        if (pointValueQualityHolder != null) {
            formattedValue = field.formatValue(pointValueQualityHolder.getValue());
        }
        return formattedValue;

    }

    /**
     * Gets the timestamp. Returns "" if the timestamp was not found.
     *
     * @param field
     * @param timeZone
     * @param pointValueQualityHolder
     * @return
     */
    private String getTimestamp(ExportField field,
                                DateTimeZone timeZone,
                                PointValueQualityHolder pointValueQualityHolder) {
        String formattedValue = "";
        if (pointValueQualityHolder != null) {
            DateTime dateTime = new DateTime(pointValueQualityHolder.getPointDataTimeStamp()).withZone(timeZone);
            formattedValue = field.formatTimestamp(dateTime);
        }
        return formattedValue;

    }

    /**
     * Gets the UOM value. Returns "" if the value was not found. If the meter.getDeviceId() is -1, returns the preview value.
     *
     * @param meter
     * @param field
     * @param userContext
     * @return
     */
    private String getUOMValue(Meter meter, ExportField field, YukonUserContext userContext) {
        String valueString = "";
        try {
            if (meter.getDeviceId() == fakeDeviceId) {
                valueString = getDefaultUOMValue( userContext);
            } else {
                LitePoint point =
                    attributeService.getPointForAttribute(meter, field.getAttribute()
                        .getAttribute());
                LiteUnitMeasure unitMeasure =
                    unitMeasureDao.getLiteUnitMeasure(point.getUofmID());
                valueString = unitMeasure.getUnitMeasureName();
            }

        } catch (IllegalUseOfAttribute e) {
            // missing value
        }
        return valueString;

    }

    /**
     * Gets the start date.
     *
     * @param attribute
     * @param stopDate
     * @return
     */
    private DateTime getStartDate(ExportAttribute attribute, DateTime stopDate) {
        return stopDate.minus(Period.days(attribute.getDaysPrevious()));
    }
    
    /**
     * Gets default (preview) meter
     *
     * @param userContext
     * @return
     */
    
    public Meter getDefaultMeter(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        Meter previewMeter = new Meter();
        previewMeter.setMeterNumber(messageSourceAccessor.getMessage(previewMeterNumberKey));
        previewMeter.setName(messageSourceAccessor.getMessage(previewMeterNameKey));
        previewMeter.setAddress(messageSourceAccessor.getMessage(previewMeterAddressKey));
        previewMeter.setRoute(messageSourceAccessor.getMessage(previewMeterRouteKey));
        PaoIdentifier paoIdentifier = new PaoIdentifier(fakeDeviceId, PaoType.MCT420CL);
        previewMeter.setPaoIdentifier(paoIdentifier);
        return previewMeter;
    }
    
    /**
     * Gets default (preview) UOM value
     *
     * @param userContext
     * @return
     */
    
    public String getDefaultUOMValue(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return messageSourceAccessor.getMessage(previewUOMValueKey);
    }

}
