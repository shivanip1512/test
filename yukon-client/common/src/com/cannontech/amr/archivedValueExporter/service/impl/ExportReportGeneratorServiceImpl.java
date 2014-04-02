package com.cannontech.amr.archivedValueExporter.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.common.util.TimeZoneFormat;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
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
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;

public class ExportReportGeneratorServiceImpl implements ExportReportGeneratorService {
    @Autowired private PaoSelectionService paoSelectionService;
    @Autowired private AttributeService attributeService;
    @Autowired private PointDao pointDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;

    public static String baseKey = "yukon.web.modules.tools.bulk.archivedValueExporter.";

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
    private static int fakePaoId = -1;
    
    @Override
    public List<String> generatePreview(ExportFormat format, YukonUserContext userContext) {
        List<String> preview = new ArrayList<>();

        Map<? extends YukonPao, PaoData> previewData = getPreviewData(userContext);
        addHeader(format, preview);

        if (format.getFormatType() == ArchivedValuesExportFormatType.FIXED_ATTRIBUTE) {
            Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> fieldIdToAttributeData =
                    getFixedPreviewAttributeData(format, previewData);
            generateFixedBody(preview, previewData.keySet(), previewData, format, fieldIdToAttributeData,
                userContext);
        } else {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
                    getDynamicPreviewAttributeData(format, previewData);
            generateDynamicBody(preview, previewData.keySet(), previewData, format, userContext,
                BuiltInAttribute.USAGE, attributeData);
        }
        
        addFooter(format, preview);
        return preview;
    }

    @Override
    public List<String> generateReport(List<? extends YukonPao> paos, ExportFormat format, DataRange dataRange,
            YukonUserContext userContext, Attribute[] attributes) {
        Set<OptionalField> requestedFields = new HashSet<>();
        boolean needsName = false;
        for (ExportField field : format.getFields()) {
            if (field.getFieldType() == FieldType.DEVICE_NAME) {
                needsName = true;
            }
            OptionalField optionalField = field.getFieldType().getPaoDataOptionalField();
            if (optionalField != null) {
                requestedFields.add(optionalField);
            }
        }
        Map<? extends YukonPao, PaoData> paoDataByPao = null;
        if (needsName || requestedFields.size() > 0) {
            paoDataByPao = paoSelectionService.lookupPaoData(paos, requestedFields);
        }

        List<String> reportResults = new ArrayList<>();

        addHeader(format, reportResults);
        TimeZoneFormat tzFormat = format.getDateTimeZoneFormat();
        DateTimeZone reportTZ = getReportTZ(tzFormat, userContext);

        switch (dataRange.getDataRangeType()) {
            case END_DATE:
                Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> endDateAttributeData = new HashMap<>();
                DateTime endOfDay = dataRange.getEndDate().plusDays(1).toDateTimeAtStartOfDay(reportTZ);
                
                for (ExportField field : format.getFields()) {
                    if (field.getFieldType() == FieldType.ATTRIBUTE) {
                        DateTime startDate = getStartDate(field.getAttribute(), endOfDay);
                        Range<Instant> dateRange = Range.exclusiveInclusive(startDate.toInstant(), endOfDay.toInstant());
                        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
                                getFixedAttributeData(paos, field.getAttribute(), dateRange);
                        
                        endDateAttributeData.put(field.getFieldId(), attributeData);
                    }
                }

                generateFixedBody(reportResults, paos, paoDataByPao, format, endDateAttributeData, userContext);
                break;

            case DATE_RANGE:
                Range<Instant> dateRange = dataRange.getLocalDateRange().getInstantDateRange(userContext);
                for (Attribute attribute : attributes) {
                    ListMultimap<PaoIdentifier, PointValueQualityHolder> dateRangeAttributeData =
                            getDynamicAttributeData(paos, attribute, dateRange, null);
                    generateDynamicBody(reportResults, paos, paoDataByPao, format, userContext, attribute,
                        dateRangeAttributeData);
                }

                break;
            case DAYS_PREVIOUS:
                Instant now = Instant.now();
                Instant start = now.minus(Duration.standardDays(dataRange.getDaysPrevious()));
                Range<Instant> previousDaysDateRange = Range.exclusiveInclusive(start, now);

                for (Attribute attribute : attributes) {
                    ListMultimap<PaoIdentifier, PointValueQualityHolder> previousDaysAttributeData = 
                            getDynamicAttributeData(paos, attribute, previousDaysDateRange, null);
                    generateDynamicBody(reportResults, paos, paoDataByPao, format, userContext, attribute,
                        previousDaysAttributeData);
                }

                break;
            case SINCE_LAST_CHANGE_ID:
                long firstChangeId = dataRange.getChangeIdRange().getFirstChangeId();
                long lastChangeId = dataRange.getChangeIdRange().getLastChangeId();
                Range<Long> changeIdRange = Range.exclusiveInclusive(firstChangeId, lastChangeId);

                for (Attribute attribute : attributes) {
                    ListMultimap<PaoIdentifier, PointValueQualityHolder> sinceLastChangeIdAttributeData =
                            getDynamicAttributeData(paos, attribute, null, changeIdRange);
                    generateDynamicBody(reportResults, paos, paoDataByPao, format, userContext, attribute,
                        sinceLastChangeIdAttributeData);
                }
                
                break;
            default:
                throw new IllegalArgumentException(dataRange.getDataRangeType()+" is not currently supported by the export report generator.");
        }
        
        // Add Footer
        addFooter(format, reportResults);

        return reportResults;
    }

    /**
     * Determines the time zone to use for the report.
     */
    private DateTimeZone getReportTZ(TimeZoneFormat tzFormat, YukonUserContext userContext) {
    
        DateTimeZone reportTZ = null;
        switch (tzFormat) { 
            case LOCAL:
                reportTZ = userContext.getJodaTimeZone();
                break;
            case LOCAL_NO_DST:
                reportTZ = userContext.getJodaTimeZone();
                break;
            case UTC:
                reportTZ = DateTimeZone.UTC;
                break;
        }
        return reportTZ;
    }
    
    
    /**
     * Adds the header portion of the report
     */
    private void addHeader(ExportFormat format, List<String> reportResults) {
        if (StringUtils.isNotEmpty(format.getHeader())) {
            reportResults.add(format.getHeader());
        }
    }
    
    /**
     * Adds the footer portion of the report
     */
    private void addFooter(ExportFormat format, List<String> reportResults) {
        if (StringUtils.isNotEmpty(format.getFooter())) {
            reportResults.add(format.getFooter());
        }
    }

    /**
     * Builds and returns a list of strings each representing one row of data for the report.
     */
    private void generateFixedBody(List<String> reportRows, Iterable<? extends YukonPao> paos,
            Map<? extends YukonPao, PaoData> paoDataByPao, ExportFormat format, 
            Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData, 
            YukonUserContext userContext) {

        for (YukonPao pao : paos) {
            PaoData data = null;
            if(paoDataByPao != null){
                data = paoDataByPao.get(pao);
            }
            String dataRow = getDataRow(format, pao, data, userContext, attributeData);
            if (!dataRow.equals(SKIP_RECORD)) {
                reportRows.add(dataRow);
            }
        }
    }

    /**
     * Builds and returns a list of strings each representing one row of data for the report.
     */
    private void generateDynamicBody(List<String> reportRows, Iterable<? extends YukonPao> paos,
            Map<? extends YukonPao, PaoData> paoDataByPao, ExportFormat format, YukonUserContext userContext,
            Attribute attribute, ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData) {
        
        for (YukonPao pao : paos) {
            List<PointValueQualityHolder> pointData = attributeData.get(pao.getPaoIdentifier());
            for (PointValueQualityHolder pointValueQualityHolder : pointData) {
                PaoData data = null;
                if(paoDataByPao != null){
                    data = paoDataByPao.get(pao);
                }
                String reportRow = generateReportRow(format, pao, attribute, data,
                    pointValueQualityHolder, userContext);
                if (!reportRow.equals(SKIP_RECORD)) {
                    reportRows.add(reportRow);
                }
            }
        }
    }
    
    /**
     * This method generates one row to the given report.
     */
    private String generateReportRow(ExportFormat format, YukonPao pao, Attribute attribute, PaoData paoData,
                                     PointValueQualityHolder pointValueQualityHolder, 
                                     YukonUserContext userContext) {

        StringBuilder reportRow = new StringBuilder();
        
        for (int i = 0; i < format.getFields().size(); i++) {
            ExportField field = format.getFields().get(i);
            String value = getValue(field, pao, paoData, attribute, pointValueQualityHolder, userContext,
                format.getDateTimeZoneFormat());

            if (StringUtils.isEmpty(value) && field.getFieldType() != FieldType.PLAIN_TEXT) {
                switch (field.getMissingAttribute()) {
                    case LEAVE_BLANK:
                        break;
                    case FIXED_VALUE:
                        value = field.getMissingAttributeValue();
                        break;
                    case SKIP_RECORD:
                        return SKIP_RECORD;
                }
            }

            reportRow.append(field.padValue(value));
            if (i != format.getFields().size() - 1) {
                reportRow.append(format.getDelimiter());
            }
        }
        
        return reportRow.toString();
    }

    /**
     * This method translates the information supplied along with the export field to get the desired data from the report row.
     */
    public String getValue(ExportField field, YukonPao pao, PaoData paoData, Attribute attribute,
                           PointValueQualityHolder pointValueQualityHolder, 
                           YukonUserContext userContext, TimeZoneFormat tzFormat) {
        switch (field.getFieldType()) {
            case METER_NUMBER:
                return StringUtils.isEmpty(paoData.getMeterNumber()) ? "" : paoData.getMeterNumber();
            case DEVICE_NAME:
                return StringUtils.isEmpty(paoData.getName()) ? "" : paoData.getName();
            case ADDRESS:
                return StringUtils.isEmpty(paoData.getAddressOrSerialNumber()) ? "" : paoData.getAddressOrSerialNumber();
            case PLAIN_TEXT:
                return field.getPattern();
            case ROUTE:
                return StringUtils.isEmpty(paoData.getRouteName()) ? "" : paoData.getRouteName();
            case ATTRIBUTE_NAME:
                return getAttributeName(attribute, userContext);
            case UNIT_OF_MEASURE:
                return getUOMValue(pao, attribute, userContext);
            case POINT_NAME:
                return getPointName(field, pointValueQualityHolder);
            case POINT_VALUE:
                return getPointValue(field, pointValueQualityHolder);
            case POINT_TIMESTAMP:
                return getTimestamp(field, pointValueQualityHolder, userContext, tzFormat);
            case POINT_QUALITY:
                return getQuality(field, pointValueQualityHolder);
                
            case ATTRIBUTE:
                switch (field.getAttributeField()) {
                    case UNIT_OF_MEASURE:
                        return getUOMValue(pao, field.getAttribute().getAttribute(), userContext);
                    case VALUE:
                        return getPointValue(field, pointValueQualityHolder);
                    case TIMESTAMP:
                        return getTimestamp(field, pointValueQualityHolder, userContext, tzFormat);
                    case QUALITY:
                        return getQuality(field, pointValueQualityHolder);
                }
            default:
                throw new IllegalArgumentException(field.getFieldType() +" is not currently supported in the export report process");
        }
    }

    /**
     * This method builds preview attribute data from localized preview values. 
     */
    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getDynamicPreviewAttributeData(ExportFormat format,
            Map<? extends YukonPao, PaoData> previewData) {
        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeDataValues = ArrayListMultimap.create();

        PointData pointData = new PointData();
        pointData.setPointQuality(previewQuality);
        pointData.setTime(new Date());
        pointData.setValue(previewValue);
        attributeDataValues.put(previewData.keySet().iterator().next().getPaoIdentifier(), pointData);
        
        return attributeDataValues;
    }
    
    /**
     * This method builds preview attribute data from localized preview values. 
     */
    private Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> getFixedPreviewAttributeData(ExportFormat format,
            Map<? extends YukonPao, PaoData> previewData) {
        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData = new HashMap<>();

        for (ExportField field : format.getFields()) {
            if (field.getFieldType() == FieldType.ATTRIBUTE) {
                ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeDataValues =
                        getDynamicPreviewAttributeData(format, previewData);
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
     */
    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getFixedAttributeData(List<? extends YukonPao> paos,
            ExportAttribute attribute, ReadableRange<Instant> dateRange) {
        Order order = null;
        OrderBy orderBy = null;

        switch (attribute.getDataSelection()) {
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

        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeDataValues =
            rawPointHistoryDao.getLimitedAttributeData(paos, attribute.getAttribute(), dateRange, null, 1, false, order, orderBy);
        return attributeDataValues;
    }

    /**
     * This is method is a helper method for retrieving the raw point history data for the supplied dateRange or changeIdRange.
     */
    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getDynamicAttributeData(List<? extends YukonPao> paos,
            Attribute attribute, ReadableRange<Instant> dateRange, ReadableRange<Long> changeIdRange) {
        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeDataValues = 
                rawPointHistoryDao.getAttributeData(paos, attribute, dateRange, changeIdRange, false, Order.FORWARD, null);
        return attributeDataValues;
    }

    /**
     * Builds the data row for the report. If the the value was not found, and a user
     * selected to skip a record, it returns "SKIP_RECORD_SKIP_RECORD".
     */
    private String getDataRow(ExportFormat format, YukonPao pao, PaoData paoData, YukonUserContext userContext,
                              Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData) {

        StringBuilder dataRow = new StringBuilder();
        
        for (int i = 0; i < format.getFields().size(); i++) {
            ExportField field = format.getFields().get(i);

            PointValueQualityHolder pointValueQualityHolder = null;
            if (field.getFieldType() == FieldType.ATTRIBUTE) {
                pointValueQualityHolder = findPointValueQualityHolder(pao, field, attributeData);
            }

            String value = getValue(field, pao, paoData, null, pointValueQualityHolder, userContext,
                format.getDateTimeZoneFormat());

            if (StringUtils.isEmpty(value) && field.getFieldType() != FieldType.PLAIN_TEXT) {
                switch (field.getMissingAttribute()) {
                    case LEAVE_BLANK:
                        value = "";
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
     * Finds point value quality holder for a meter. This method looks at the attribute map. First, it finds the collection
     * of meter data by the fieldId, then it finds point value quality holder using the paoIdentifier from the meter. 
     * If Point value quality holder was not found, null is returned.
     *
     */
    private PointValueQualityHolder findPointValueQualityHolder(YukonPao pao, ExportField field,
                                                                Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData) {

        PointValueQualityHolder pointValueQualityHolder = null;
        if (!attributeData.isEmpty()) {
            ListMultimap<PaoIdentifier, PointValueQualityHolder> data = attributeData.get(field.getFieldId());

            if (!data.isEmpty()) {
                List<PointValueQualityHolder> pointValueQualityHolders = data.get(pao.getPaoIdentifier());

                if (pointValueQualityHolders != null && !pointValueQualityHolders.isEmpty()) {
                    pointValueQualityHolder = data.get(pao.getPaoIdentifier()).iterator().next();
                }
            }
        }
        return pointValueQualityHolder;
    }

    /**
     * Gets the attribute name.
     */
    private String getAttributeName(Attribute attribute, YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String attributeName = messageSourceAccessor.getMessage(attribute.getMessage());

        return attributeName;
    }
    
    /**
     * Gets the quality. Returns "" if the quality was not found.
     */
    private String getQuality(ExportField field, PointValueQualityHolder pointValueQualityHolder) {
        if (pointValueQualityHolder == null) {
            return "";
        }

        return pointValueQualityHolder.getPointQuality().getDescription();
    }

    /**
     * Gets the value. Returns "" if the value was not found.
     */
    private String getPointName(ExportField field, PointValueQualityHolder pointValueQualityHolder) {
        if (pointValueQualityHolder == null) {
            return "";
        }

        return pointDao.getPointName(pointValueQualityHolder.getId());
    }

    /**
     * Gets the value. Returns "" if the value was not found.
     */
    private String getPointValue(ExportField field, PointValueQualityHolder pointValueQualityHolder) {
        if (pointValueQualityHolder == null) {
            return "";
        }

        return field.formatValue(pointValueQualityHolder.getValue());
    }

    /**
     * Gets the timestamp. Returns "" if the timestamp was not found.
     */
    private String getTimestamp(ExportField field, PointValueQualityHolder pointValueQualityHolder, 
                                YukonUserContext userContext, TimeZoneFormat tzFormat) {
        if (pointValueQualityHolder == null) {
            return "";
        }

        DateTimeZone timeZone = getReportTZ(tzFormat, userContext);
        DateTime dateTime = new DateTime(pointValueQualityHolder.getPointDataTimeStamp()).withZone(timeZone);
        if (tzFormat == TimeZoneFormat.LOCAL_NO_DST && !timeZone.isStandardOffset(dateTime.getMillis())) {
            long stdOffset = timeZone.getStandardOffset(dateTime.getMillis());
            DateTimeZone timeZoneWithoutDST = DateTimeZone.forOffsetMillis((int) stdOffset);
            dateTime = dateTime.withZone(timeZoneWithoutDST);
        }
        return field.formatTimestamp(dateTime);
    }

    /**
     * Gets the UOM value. Returns "" if the value was not found. If the meter.getDeviceId() is -1, returns the preview value.
     */
    private String getUOMValue(YukonPao pao, Attribute attribute, YukonUserContext userContext) {
        try {
            if (pao.getPaoIdentifier().getPaoId() == fakePaoId) {
                return getDefaultUOMValue(userContext);
            } else {
                LitePoint point = attributeService.getPointForAttribute(pao, attribute);
                LiteUnitMeasure unitMeasure = unitMeasureDao.getLiteUnitMeasure(point.getUofmID());
                return unitMeasure.getUnitMeasureName();
            }
        } catch (IllegalUseOfAttribute e) {
            // missing value
        }

        return "";
    }

    /**
     * Gets the start date.
     */
    private DateTime getStartDate(ExportAttribute attribute, DateTime stopDate) {
        return stopDate.minus(Period.days(attribute.getDaysPrevious()));
    }
    
    /**
     * Gets preview data.
     */
    private Map<? extends YukonPao, PaoData> getPreviewData(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);

        String paoName = messageSourceAccessor.getMessage(previewMeterNameKey);
        String meterNumber = messageSourceAccessor.getMessage(previewMeterNumberKey);
        int carrierAddress = 5;
        String routeName = messageSourceAccessor.getMessage(previewMeterRouteKey);
        String address = messageSourceAccessor.getMessage(previewMeterAddressKey);

        PaoIdentifier paoIdentifier = new PaoIdentifier(fakePaoId, PaoType.MCT420CL);
        PaoData paoData = new PaoData(OptionalField.SET_OF_ALL, paoIdentifier);
        paoData.setName(paoName);
        paoData.setEnabled(true);
        paoData.setMeterNumber(meterNumber);
        paoData.setCarrierAddress(carrierAddress);
        paoData.setRouteName(routeName);
        paoData.setAddressOrSerialNumber(address);

        return ImmutableMap.of(paoIdentifier, paoData);
    }
    
    /**
     * Gets default (preview) UOM value
     */
    
    private String getDefaultUOMValue(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return messageSourceAccessor.getMessage(previewUOMValueKey);
    }
}