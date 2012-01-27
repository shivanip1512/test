package com.cannontech.amr.archivedValueExporter.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.model.Meter;
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
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class ExportReportGeneratorImpl implements ExportReportGeneratorService {

    @Autowired private AttributeService attributeService;
    @Autowired private UnitMeasureDao unitMeasureDao;
    @Autowired private RawPointHistoryDao rawPointHistoryDao;

    private static String SKIP_RECORD = "SKIP_RECORD_SKIP_RECORD";

    private static double previewValue = 1234546.00;
    private static PointQuality previewQuality = PointQuality.Normal;
    private static int fakeDeviceId = -1;
    
    private Meter previewMeter;
    private String previewUOMValue;

   

    public List<String> generatePreview(Meter meter, String previewUOMValue, ExportFormat format, YukonUserContext userContext) {
        previewMeter = meter;
        previewMeter.setDeviceId(fakeDeviceId);
        this.previewUOMValue = previewUOMValue;
        List<String> report = Lists.newArrayList();
        // Build report rows
        // add header
        if (StringUtils.isNotEmpty(format.getHeader())) {
            report.add(format.getHeader());
        }
        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData =
            getPreviewAttributeData(format);
        String dataRow =
            getDataRow(format, meter, userContext.getJodaTimeZone(), attributeData);
        report.add(dataRow);
        // add footer
        if (StringUtils.isNotEmpty(format.getFooter())) {
            report.add(format.getFooter());
        }
        return report;
    }

    public List<String> generateReport(List<Meter> meters, ExportFormat format, Date stopDate,
                                       YukonUserContext userContext) {

        DateTime stopDateTime = new DateTime(stopDate, userContext.getJodaTimeZone());
        List<String> report = Lists.newArrayList();
        // <field id, device data>
        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData =
            getAttributeData(meters, format, stopDateTime);
        // Build report rows
        // add header
        if (StringUtils.isNotEmpty(format.getHeader())) {
            report.add(format.getHeader());
        }
        for (Meter meter : meters) {
            // for each meter create a row
            String dataRow =
                getDataRow(format, meter, userContext.getJodaTimeZone(), attributeData);
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

    private Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> getPreviewAttributeData(ExportFormat format) {
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

    private Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> getAttributeData(List<Meter> meters,
                                                                                                ExportFormat format,
                                                                                                DateTime stopDate) {
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

    private String getDataRow(ExportFormat format,
                              Meter meter,
                              DateTimeZone timeZone,
                              Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData) {
        StringBuilder dataRow = new StringBuilder();
        for (int i = 0; i < format.getFields().size(); i++) {
            ExportField field = format.getFields().get(i);
            String value = getValue(meter, field, timeZone, attributeData);
            if (StringUtils.isEmpty(value)) {
                // missing value
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
            dataRow.append(field.padValue(value));
            if (i != format.getFields().size() - 1) {
                dataRow.append(format.getDelimiter());
            }
        }
        return dataRow.toString();
    }

    private String getValue(Meter meter,
                            ExportField field,
                            DateTimeZone timeZone,
                            Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData) {
        String value = "";
        switch (field.getFieldType()) {
        case METER_NUMBER:
            value = meter.getMeterNumber();
            break;
        case DEVICE_NAME:
            value = meter.getName();
            break;
        case DLC_ADDRESS:
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
                value = getUOMValue(meter, field);
                break;
            case VALUE:
                value = getValue(field, pointValueQualityHolder);
                break;
            case TIMESTAMP:
                value = getTimestamp(field, timeZone, pointValueQualityHolder);
                break;
            case QUALITY:
                value = getQuality(field, pointValueQualityHolder);
                break;
            }
        }
        return value;
    }

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

    private String getQuality(ExportField field, PointValueQualityHolder pointValueQualityHolder) {
        String value = "";
        if (pointValueQualityHolder != null) {
            value = pointValueQualityHolder.getPointQuality().getDescription();
        }
        return value;
    }

    private String getValue(ExportField field,
                            PointValueQualityHolder pointValueQualityHolder) {
        String formattedValue = "";
        if (pointValueQualityHolder != null) {
            formattedValue = field.formatValue(pointValueQualityHolder.getValue());
        }
        return formattedValue;

    }

    private String getTimestamp(ExportField field,
                                DateTimeZone timeZone,
                                PointValueQualityHolder pointValueQualityHolder) {
        String formattedValue = "";
        if (pointValueQualityHolder != null) {
            DateTime date = new DateTime(pointValueQualityHolder.getPointDataTimeStamp(),
                                         timeZone);
            formattedValue = field.formatTimestamp(date);
        }
        return formattedValue;

    }

    private String getUOMValue(Meter meter, ExportField field) {
        String valueString = "";
        try {
            if (meter.getDeviceId() == fakeDeviceId) {
                valueString = previewUOMValue;
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

    private DateTime getStartDate(ExportAttribute attribute, DateTime stopDate) {
        return stopDate.minus(Period.days(attribute.getDaysPrevious()));
    }

}
