package com.cannontech.amr.archivedValueExporter.service.impl;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.PadSide;
import com.cannontech.amr.archivedValueExporter.model.Preview;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoData;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PaoSelectionService;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.ReadableRange;
import com.cannontech.common.util.TimeZoneFormat;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public class ExportReportGeneratorServiceImpl implements ExportReportGeneratorService {

    @Autowired private PaoSelectionService paoSelectionService;
    @Autowired private AttributeService attributeService;
    @Autowired private PointDao pointDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private ConfigurationSource configSource;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private StateGroupDao stateGroupDao;

    public static String baseKey = "yukon.web.modules.tools.bulk.archivedValueExporter.";

    private static String previewUOMValueKey = baseKey + "previewUOMValue";
    private static String previewPointStateKey = baseKey + "previewPointState";
    private static String previewMeterNumberKey = baseKey + "previewMeterNumber";
    private static String previewMeterNameKey = baseKey + "previewMeterName";
    private static String previewMeterAddressKey = baseKey + "previewMeterAddress";
    private static String previewMeterRouteKey = baseKey + "previewMeterRoute";

    private static final Logger log = YukonLogManager.getLogger(ExportReportGeneratorServiceImpl.class);

    /*The value to be returned in case the meter information we
    were looking for was not found, and the user elected to skip the record.
    This string is defined as SKIP_RECORD_SKIP_RECORD because the value SKIP_RECORD might be selected
    as a Fixed Value, and SKIP_RECORD_SKIP_RECORD is longer than the 20 characters allowed for MissingAttributeValue, thus
    making it impossible to be used as Fixed Value. 
     */
    private static String SKIP_RECORD = "SKIP_RECORD_SKIP_RECORD";

    private static double previewValue = 1234546.012;
    private static PointQuality previewQuality = PointQuality.Normal;
    private static Set<PointQuality> excludedQualities = ImmutableSet.of(PointQuality.Abnormal, 
            PointQuality.Invalid, 
            PointQuality.Questionable, 
            PointQuality.Uninitialized, 
            PointQuality.Unreasonable);
    @PostConstruct
    private void pointQualities() {
        String qualityList = configSource.getString(MasterConfigString.EXCLUDED_POINT_QUALITIES);
        if (StringUtils.isNotEmpty(qualityList)) {
            String[] strings = qualityList.split("\\s*,\\s*");
            Builder<PointQuality> b = ImmutableSet.builder();
            for (String string : strings) {
                b.add(PointQuality.valueOf(string));
            }
            excludedQualities = b.build();
        }
    }
    private static int fakePaoId = -1;

    @Override
    public Preview generatePreview(ExportFormat format, YukonUserContext userContext) {
        Preview preview = new Preview();
        preview.setHeader(format.getHeader());
        preview.setFooter(format.getFooter());

        try (
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
                ) {
            Map<? extends YukonPao, PaoData> previewData = getPreviewData(userContext);
            if (format.getFormatType() == ArchivedValuesExportFormatType.FIXED_ATTRIBUTE) {
                Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> fieldIdToAttributeData = getFixedPreviewAttributeData(format, previewData);
                generateFixedBody(previewData.keySet(), previewData, format, fieldIdToAttributeData, userContext, null, writer);
            } else {
                ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData = getDynamicPreviewAttributeData(format, previewData);
                generateDynamicBody(previewData.keySet(), previewData, format, userContext, BuiltInAttribute.USAGE, attributeData, null, writer);
            }
            writer.flush();
            preview.setBody(Lists.newArrayList(output.toString().split(System.lineSeparator())));
        } catch (IOException e) {
            throw new IllegalStateException("ByteArrayOutputStream shouldn't ever throw IOException", e);
        }

        return preview;
    }

    private boolean checkStatic(ExportFormat format) {
        for (ExportField ex: format.getFields()) {
            if(!ex.getField().isPlainTextType() && 
                    (ex.getMaxLength() == 0 || ex.getPadSide().equals(PadSide.NONE))){
                return false;
            }
        }
        return true;
    }

    @Override
    public void generateReport(List<? extends YukonPao> allPaos, 
            ExportFormat format, 
            DataRange range,
            YukonUserContext userContext, 
            Attribute[] attributes, BufferedWriter writer) throws IOException {

        if (log.isDebugEnabled()) {
            log.info("Generating report for " + allPaos.size() + " devices");
        }
        Set<OptionalField> requestedFields = new HashSet<>();
        boolean needsName = false;
        boolean needsUoM = false;
        for (ExportField field : format.getFields()) {
            if (field.getField().getType() == FieldType.DEVICE_NAME) {
                needsName = true;
            }
            if (field.getField().getType() == FieldType.UNIT_OF_MEASURE
                    || field.getField().getType() == FieldType.ATTRIBUTE
                    && field.getAttributeField() == AttributeField.UNIT_OF_MEASURE) {
                needsUoM = true;
            }
            OptionalField optionalField = field.getField().getType().getPaoDataOptionalField();
            if (optionalField != null) {
                requestedFields.add(optionalField);
            }
        }
        if (StringUtils.isNotEmpty(format.getHeader())) {
            writer.write(format.getHeader());
            writer.newLine();
        }

        for (List<? extends YukonPao> paosSublist : Lists.partition(allPaos, 250)) {
            Map<? extends YukonPao, PaoData> paoDataByPao = null;
            if (needsName || requestedFields.size() > 0) {
                paoDataByPao = paoSelectionService.lookupPaoData(paosSublist, requestedFields);
            }
            Table<Integer, PointIdentifier, LiteUnitMeasure> unitMeasureLookupTable = null;
            if (needsUoM) {
                unitMeasureLookupTable = unitMeasureDao.getLiteUnitMeasureByPaoIdAndPoint(paosSublist);
            }

            DateTimeZone reportTZ = getReportTZ(format.getDateTimeZoneFormat(), userContext);
            switch (range.getDataRangeType()) {
            //fixed formats
            case END_DATE:
                DateTime endOfDay = range.getEndDate().plusDays(1).toDateTimeAtStartOfDay(reportTZ);
                Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> data = getEndDateAttributeData(format, paosSublist, endOfDay);
                generateFixedBody(paosSublist, paoDataByPao, format, data, userContext, unitMeasureLookupTable, writer);
                break;
            case DAYS_OFFSET:
                endOfDay = range.getEndDate().plusDays(1).minusDays(range.getDaysOffset()).toDateTimeAtStartOfDay(reportTZ);
                data = getEndDateAttributeData(format, paosSublist, endOfDay);
                generateFixedBody(paosSublist, paoDataByPao, format, data, userContext, unitMeasureLookupTable, writer);
                break;
                //dynamic formats
            case DATE_RANGE:
                Range<Instant> dateRange = range.getLocalDateRange().getInstantDateRange(userContext);
                for (Attribute attribute : attributes) {
                    ListMultimap<PaoIdentifier, PointValueQualityHolder> dateRangeAttributeData =
                            getDynamicAttributeData(paosSublist, attribute, dateRange, range, null, format);
                    generateDynamicBody(paosSublist, paoDataByPao, format, userContext, attribute,
                            dateRangeAttributeData, unitMeasureLookupTable, writer);
                }

                break;
            case DAYS_PREVIOUS:
                Instant now = Instant.now();
                Instant start = now.minus(Duration.standardDays(range.getDaysPrevious()));
                Range<Instant> previousDaysDateRange = Range.exclusiveInclusive(start, now);

                for (Attribute attribute : attributes) {
                    ListMultimap<PaoIdentifier, PointValueQualityHolder> previousDaysAttributeData = 
                            getDynamicAttributeData(paosSublist, attribute, previousDaysDateRange, range, null, format);
                    generateDynamicBody(paosSublist, paoDataByPao, format, userContext, attribute,
                            previousDaysAttributeData, unitMeasureLookupTable, writer);
                }

                break;
            case SINCE_LAST_CHANGE_ID:
                long firstChangeId = range.getChangeIdRange().getFirstChangeId();
                long lastChangeId = range.getChangeIdRange().getLastChangeId();
                Range<Long> changeIdRange = Range.exclusiveInclusive(firstChangeId, lastChangeId);
                if (log.isDebugEnabled()) {
                    log.info("Generating by last change id for a range " + changeIdRange);
                }

                for (Attribute attribute : attributes) {
                    if (log.isDebugEnabled()) {
                        log.info("Getting data for attribute=" + attribute + " devices=" + paosSublist.size());
                    }
                    ListMultimap<PaoIdentifier, PointValueQualityHolder> sinceLastChangeIdAttributeData =
                            getDynamicAttributeData(paosSublist, attribute, null, range, changeIdRange, format);
                    if (log.isDebugEnabled()) {
                        log.info("Found values since the last change id=" + sinceLastChangeIdAttributeData.size());
                    }
                    generateDynamicBody(paosSublist, paoDataByPao, format, userContext, attribute,
                            sinceLastChangeIdAttributeData, unitMeasureLookupTable, writer);
                    log.info("Finished writing to file");
                }
                break;
            default:
                throw new IllegalArgumentException(range.getDataRangeType()+" is not currently supported by the export report generator.");
            }
        }
        if (StringUtils.isNotEmpty(format.getFooter())) {
            writer.write(format.getFooter());
            writer.newLine();
        }
        writer.flush();
        log.info("Generating report is done");
    }


    private Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> getEndDateAttributeData(
            ExportFormat format, List<? extends YukonPao> paosSublist, DateTime endOfDay) {
        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> endDateAttributeData = new HashMap<>();
        for (ExportField field : format.getFields()) {
            if (field.getField().getType() == FieldType.ATTRIBUTE) {
                DateTime startDate = getStartDate(field.getField().getAttribute(), endOfDay);
                Range<Instant> offsetRange = Range.exclusiveInclusive(startDate.toInstant(), endOfDay.toInstant());
                ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeData =
                        getFixedAttributeData(paosSublist, field.getField().getAttribute(), offsetRange, format);

                endDateAttributeData.put(field.getFieldId(), attributeData);
            }
        }
        return endDateAttributeData;
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
     * Builds and returns a list of strings each representing one row of data for the report.
     * @throws IOException 
     */
    private void generateFixedBody( 
            Iterable<? extends YukonPao> paos,
            Map<? extends YukonPao, PaoData> paoDataByPao, 
            ExportFormat format, 
            Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData, 
            YukonUserContext userContext, Table<Integer, PointIdentifier, LiteUnitMeasure> unitMeasureLookupTable,
            BufferedWriter writer) throws IOException {

        for (YukonPao pao : paos) {
            PaoData data = null;
            if(paoDataByPao != null){
                data = paoDataByPao.get(pao);
            }
            String dataRow = getDataRow(format, pao, data, userContext, attributeData, unitMeasureLookupTable);
            if (!dataRow.equals(SKIP_RECORD)) {
                writer.write(dataRow);
                writer.newLine();
            }
        }
    }

    /**
     * Builds and returns a list of strings each representing one row of data for the report.
     * @throws IOException 
     */
    private void generateDynamicBody(
            Iterable<? extends YukonPao> paos,
            Map<? extends YukonPao, PaoData> paoDataByPao, 
            ExportFormat format, 
            YukonUserContext userContext,
            Attribute attribute, 
            ListMultimap<PaoIdentifier, 
            PointValueQualityHolder> attributeData, Table<Integer, PointIdentifier, LiteUnitMeasure> unitMeasureLookupTable,
            BufferedWriter writer) throws IOException {

        for (YukonPao pao : paos) {
            List<PointValueQualityHolder> pointData = attributeData.get(pao.getPaoIdentifier());
            for (PointValueQualityHolder pointValueQualityHolder : pointData) {

                PaoData data = null;
                if (paoDataByPao != null) {
                    data = paoDataByPao.get(pao);
                }
                String reportRow = generateReportRow(format, pao, attribute, data,
                        pointValueQualityHolder, userContext, unitMeasureLookupTable);
                if (!reportRow.equals(SKIP_RECORD)) {
                    writer.write(reportRow);
                    writer.newLine();
                }
            }
        }
    }

    /**
     * This method generates one row to the given report.
     */
    private String generateReportRow(ExportFormat format, 
            YukonPao pao, 
            Attribute attribute, 
            PaoData paoData,
            PointValueQualityHolder pointValueQualityHolder, 
            YukonUserContext userContext, Table<Integer, PointIdentifier, LiteUnitMeasure> unitMeasureLookupTable) {

        StringBuilder reportRow = new StringBuilder();
        Instant now = Instant.now();  // time all rows/report were generated
        for (int i = 0; i < format.getFields().size(); i++) {
            ExportField field = format.getFields().get(i);
            String value = getValue(field, pao, paoData, attribute, pointValueQualityHolder, userContext,
                    format.getDateTimeZoneFormat(), unitMeasureLookupTable, now);

            if (StringUtils.isEmpty(value) && field.getField().getType() != FieldType.PLAIN_TEXT) {
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
    public String getValue(ExportField exportField, 
            YukonPao pao, 
            PaoData paoData, 
            Attribute attribute,
            PointValueQualityHolder pointValueQualityHolder, 
            YukonUserContext userContext, 
            TimeZoneFormat tzFormat,
            Table<Integer, PointIdentifier, LiteUnitMeasure> unitMeasureLookupTable,
            Instant reportRunTime) {

        switch (exportField.getField().getType()) {
        case METER_NUMBER:
            return StringUtils.isEmpty(paoData.getMeterNumber()) ? "" : paoData.getMeterNumber();
        case DEVICE_NAME:
            return StringUtils.isEmpty(paoData.getName()) ? "" : paoData.getName();
        case ADDRESS:
            return StringUtils.isEmpty(paoData.getAddressOrSerialNumber()) ? "" : paoData.getAddressOrSerialNumber();
        case PLAIN_TEXT:
            return exportField.getPattern();
        case ROUTE:
            return StringUtils.isEmpty(paoData.getRouteName()) ? "" : paoData.getRouteName();
        case ATTRIBUTE_NAME:
            return getAttributeName(attribute, userContext);
        case UNIT_OF_MEASURE:
            return getUOMValue(pao, attribute, userContext, unitMeasureLookupTable);
        case POINT_NAME:
            return pointValueQualityHolder == null ? "" : getPointName(attribute, pao);
        case POINT_VALUE:
            return getPointValue(exportField, pointValueQualityHolder);
        case POINT_TIMESTAMP:
            return getTimestamp(exportField, pointValueQualityHolder, userContext, tzFormat);
        case POINT_QUALITY:
            return getQuality(exportField, pointValueQualityHolder);
        case DEVICE_TYPE:
            return StringUtils.isEmpty(paoData.getPaoIdentifier().getPaoType().getPaoTypeName()) ? ""
                    : paoData.getPaoIdentifier().getPaoType().getPaoTypeName();
        case POINT_STATE:
            return getPointState(userContext, pao, pointValueQualityHolder);
        case ATTRIBUTE:
            switch (exportField.getAttributeField()) {
            case UNIT_OF_MEASURE:
                return getUOMValue(pao, exportField.getField().getAttribute().getAttribute(), userContext, unitMeasureLookupTable);
            case VALUE:
                return getPointValue(exportField, pointValueQualityHolder);
            case TIMESTAMP:
                return getTimestamp(exportField, pointValueQualityHolder, userContext, tzFormat);
            case QUALITY:
                return getQuality(exportField, pointValueQualityHolder);
            case POINT_STATE:
                return getPointState(userContext, pao, pointValueQualityHolder);
            }
        case RUNTIME:
            return getTimestamp(exportField, reportRunTime.toDate(), userContext, tzFormat);
        default:
            throw new IllegalArgumentException(exportField.getField().getType() +" is not currently supported in the export report process");
        }
    }

    private String getPointName(Attribute attribute, YukonPao pao) {
        Map<Attribute, AttributeDefinition> attrDefMap = 
                paoDefinitionDao.getPaoAttributeAttrDefinitionMap().get(pao.getPaoIdentifier().getPaoType());
        return attrDefMap.get(attribute).getPointTemplate().getName();
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
            if (field.getField().getType() == FieldType.ATTRIBUTE) {
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
            ExportAttribute attribute, 
            ReadableRange<Instant> dateRange,
            ExportFormat format) {

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

        Set<PointQuality> excludeQualities = format.isExcludeAbnormal() ? excludedQualities : null;
        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeDataValues =
                rawPointHistoryDao.getLimitedAttributeData(paos, attribute.getAttribute(), dateRange, null, 1, false, order, orderBy, excludeQualities);
        return attributeDataValues;
    }

    /**
     * This is method is a helper method for retrieving the raw point history data for the supplied dateRange or
     * changeIdRange.
     * 
     */
    private ListMultimap<PaoIdentifier, PointValueQualityHolder> getDynamicAttributeData(List<? extends YukonPao> paos,
            Attribute attribute, ReadableRange<Instant> dateRange, DataRange range, ReadableRange<Long> changeIdRange,
            ExportFormat format) {
        Set<PointQuality> excludeQualities = format.isExcludeAbnormal() ? excludedQualities : null;
        ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeDataValues = null;
        if (range.isTimeSelected()) {
            //if time is specified the data returned will be for the time within the range.
            attributeDataValues =
                    rawPointHistoryDao.getAttributeData(paos, attribute, dateRange, range.getTime(), changeIdRange, false,
                            Order.FORWARD, null, excludeQualities);
        } else {
            attributeDataValues =
                    rawPointHistoryDao.getAttributeData(paos, attribute, dateRange, changeIdRange, false, Order.FORWARD,
                            null, excludeQualities);
        }
        return attributeDataValues;
    }

    /**
     * Builds the data row for the report. If the value was not found, and a user
     * selected to skip a record, it returns "SKIP_RECORD_SKIP_RECORD".
     */
    private String getDataRow(ExportFormat format, 
            YukonPao pao, 
            PaoData paoData, 
            YukonUserContext userContext,
            Map<Integer, 
            ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData,
            Table<Integer, PointIdentifier, LiteUnitMeasure> unitMeasureLookupTable) {

        StringBuilder dataRow = new StringBuilder();
        Instant now = Instant.now();  // time all rows/report were generated
        
        for (int i = 0; i < format.getFields().size(); i++) {
            ExportField field = format.getFields().get(i);

            PointValueQualityHolder pointValueQualityHolder = null;
            if (field.getField().getType() == FieldType.ATTRIBUTE) {
                pointValueQualityHolder = findPointValueQualityHolder(pao, field, attributeData);
            }

            String value = getValue(field, pao, paoData, null, pointValueQualityHolder, userContext,
                    format.getDateTimeZoneFormat(), unitMeasureLookupTable, now);

            if (StringUtils.isEmpty(value) && field.getField().getType() != FieldType.PLAIN_TEXT) {
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
    private PointValueQualityHolder findPointValueQualityHolder(YukonPao pao, 
            ExportField field,
            Map<Integer, 
            ListMultimap<PaoIdentifier, 
            PointValueQualityHolder>> attributeData) {

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
        String attributeName = null;
        if (null != attribute) {
            attributeName = messageSourceAccessor.getMessage(attribute.getMessage());
        }

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
     * Gets the state associated with the point's value.
     * Returns raw value if the point is not a status point, the state group is the System state group, OR if
     * no state is found for the point's value.
     */
    private String getPointState(YukonUserContext userContext, YukonPao pao,
            PointValueQualityHolder pointValueQualityHolder) {
        if (pointValueQualityHolder == null) {
            return StringUtils.EMPTY;
        }

        if (pao.getPaoIdentifier().getPaoId() == fakePaoId) {
            return getPreviewPointState(userContext);
        }

        if (pointValueQualityHolder.getPointType().isStatus()) {
            LitePoint litePoint = pointDao.getLitePoint(pointValueQualityHolder.getId());
            if (litePoint.getStateGroupID() != StateGroupUtils.SYSTEM_STATEGROUPID) {
                LiteState liteState =
                        stateGroupDao.findLiteState(litePoint.getStateGroupID(), (int) pointValueQualityHolder.getValue());
                if (liteState != null) {
                    return liteState.getStateText();
                }
            }
        }
        return String.valueOf(pointValueQualityHolder.getValue());
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
        return getTimestamp(field, pointValueQualityHolder.getPointDataTimeStamp(), userContext, tzFormat);
    }
    
    private String getTimestamp(ExportField field, Date date, YukonUserContext userContext, TimeZoneFormat tzFormat) {
        DateTimeZone timeZone = getReportTZ(tzFormat, userContext);
        DateTime dateTime = new DateTime(date).withZone(timeZone);
        if (tzFormat == TimeZoneFormat.LOCAL_NO_DST) {
            DateTimeFormatter dateTimeFormat= ISODateTimeFormat.dateTime().withZone(DateTimeZone.UTC);
            dateTime = dateTimeFormat.parseDateTime(dateTime.toString());
            if (!timeZone.isStandardOffset(dateTime.getMillis())) {
                long stdOffset = timeZone.getStandardOffset(dateTime.getMillis());
                DateTimeZone timeZoneWithoutDST = DateTimeZone.forOffsetMillis((int) stdOffset);
                dateTime = dateTime.withZone(timeZoneWithoutDST);
            }
        }
        return field.formatTimestamp(dateTime);
    }

    /**
     * Gets the UOM value. Returns "" if the value was not found. If the meter.getDeviceId() is -1, returns the preview value.
     */
    private String getUOMValue(YukonPao pao, Attribute attribute, YukonUserContext userContext,
            Table<Integer, PointIdentifier, LiteUnitMeasure> unitMeasureLookupTable) {
        try {
            if (pao.getPaoIdentifier().getPaoId() == fakePaoId) {
                return getDefaultUOMValue(userContext);
            }
            PaoPointIdentifier paoPointIdent = attributeService.getPaoPointIdentifierForAttribute(pao, attribute);
            int paoId = pao.getPaoIdentifier().getPaoId();
            if (unitMeasureLookupTable.get(paoId, paoPointIdent.getPointIdentifier()) != null) {
                return unitMeasureLookupTable.get(paoId, paoPointIdent.getPointIdentifier()).getUnitMeasureName();
            }
        } catch (IllegalUseOfAttribute | IllegalArgumentException e) {
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

    /**
     * Gets default (preview) State Text
     */

    private String getPreviewPointState(YukonUserContext userContext) {
        MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        return messageSourceAccessor.getMessage(previewPointStateKey);
    }
}