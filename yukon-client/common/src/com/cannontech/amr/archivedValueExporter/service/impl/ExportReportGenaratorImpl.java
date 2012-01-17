package com.cannontech.amr.archivedValueExporter.service.impl;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.service.ExportReportGeneratorService;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Clusivity;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.RawPointHistoryDao.OrderBy;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class ExportReportGenaratorImpl implements ExportReportGeneratorService {

    @Autowired private AttributeService attributeService;
    @Autowired private UnitMeasureDao unitMeasureDao;

    @Autowired private RawPointHistoryDao rawPointHistoryDao;

    static double previewValue = 1234546.00;
    static String previewQuality = "Normal";
    static Date previewDate = new Timestamp(System.currentTimeMillis());
    static String previewUOMValue = "kW";

    public List<String> generateReport(List<Meter> meters, ExportFormat format, Date stopDate,
                                       YukonUserContext userContext) {

        boolean isPreview = (stopDate == null);
        List<String> report = Lists.newArrayList();
        //<field id, device data>
        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData = new HashMap<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>>();
        if(!isPreview){
            //For each Export Field that has an Field Type of Attribute, get the Device Data and store it an a map
            attributeData = getAttributeData(meters,format,stopDate);
        }
        //Build report rows
        //add header
        if (StringUtils.isNotEmpty(format.getHeader())) {
            report.add(format.getHeader());
        }
        for (Meter meter : meters) {
            //for each meter create a row
            String dataRow = getDataRow(format, meter, isPreview, userContext, stopDate, attributeData);
            report.add(dataRow);
        }
        //add footer
        if (StringUtils.isNotEmpty(format.getFooter())) {
            report.add(format.getFooter());
        }
        return report;
    }
    
    private Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> getAttributeData(List<Meter> meters,ExportFormat format, Date stopDate){
        Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData = new HashMap<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>>();
        for (ExportField field: format.getFields()) {
            if(field.getFieldType() == FieldType.ATTRIBUTE ){
                
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

                Date startDate = getStartDate(field.getAttribute(), stopDate);
                ListMultimap<PaoIdentifier, PointValueQualityHolder> attributeDataValues =
                    rawPointHistoryDao.getLimitedAttributeData(meters,
                                                               field.getAttribute().getAttribute(),
                                                               startDate,
                                                               stopDate,
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
    
    private String getDataRow(ExportFormat format, Meter meter, boolean isPreview,
                              YukonUserContext userContext, Date stopDate, Map<Integer, ListMultimap<PaoIdentifier, PointValueQualityHolder>> attributeData) {
        StringBuilder dataRow = new StringBuilder();
        boolean skipRecord = false;
        for (int i = 0; i < format.getFields().size(); i++) {
            ExportField field = format.getFields().get(i);
            String value = getValue(meter, field, isPreview, stopDate, userContext, attributeData);
            if (StringUtils.isEmpty(value)) {
                // missing value
                switch (field.getMissingAttribute()) {
                case LEAVE_BLANK:
                    break;
                case FIXED_VALUE:
                    value = field.getMissingAttributeValue();
                    break;
                case SKIP_RECORD:
                    skipRecord = true;
                    break;
                }
            }
            if (!skipRecord) {
                String formattedValue = ExportValueFormatter.padValue(field, value);
                dataRow.append(formattedValue);
            }
            if (i != format.getFields().size() - 1) {
                dataRow.append(format.getDelimiter());
            }
        }
        return dataRow.toString();
    }

    private String getValue(Meter meter,
                            ExportField field,
                            boolean isPreview,
                            Date stopDate,
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
        case DLC_ADDRESS:
            value = meter.getAddress();
            break;
        case PLAIN_TEXT:
            value = field.getPattern();
            break;
        case ROUTE:
            value = meter.getRoute();
            break;
     /*  case RF_MANUFACTURER:
        case RF_MODEL:
        case RF_SERIAL_NUMBER:
            break;*/
        case ATTRIBUTE:
            PointValueQualityHolder pointValueQualityHolder =
                findPointValueQualityHolder(meter, field, attributeData);
            switch (field.getAttributeField()) {
            case UNIT_OF_MEASURE:
                value = getUOMValue(meter, field, isPreview);
                break;
            case VALUE:
                value = getValue(field, isPreview, userContext, pointValueQualityHolder);
                break;
            case TIMESTAMP:
                value = getTimestamp(field, isPreview, userContext, pointValueQualityHolder);
                break;
            case QUALITY:
                value = getQuality(field, isPreview, pointValueQualityHolder);
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

    private String getQuality(ExportField field, boolean isPreview,PointValueQualityHolder pointValueQualityHolder) {
        String value = "";
        if (isPreview) {
            value = previewQuality;
        } else if (pointValueQualityHolder != null) {
            value = pointValueQualityHolder.getPointQuality().getDescription();
        }
        return value;
    }

    private String getValue(ExportField field, boolean isPreview,
                            YukonUserContext userContext,
                            PointValueQualityHolder pointValueQualityHolder) {
        String formattedValue = "";
        if (isPreview) {
            formattedValue = ExportValueFormatter.formatValue(previewValue, field);
        } else if (pointValueQualityHolder != null) {
            formattedValue = ExportValueFormatter.formatValue(pointValueQualityHolder.getValue(), field);
        }
        return formattedValue;

    }

    private String getTimestamp(ExportField field, boolean isPreview,
                                YukonUserContext userContext,
                                PointValueQualityHolder pointValueQualityHolder) {
        String formattedValue = "";
        if (isPreview) {
            formattedValue =
                ExportValueFormatter.formatTimestamp(new DateTime(previewDate, userContext
                    .getJodaTimeZone()), field);
        } else if (pointValueQualityHolder != null) {
            DateTime date = new DateTime(pointValueQualityHolder.getPointDataTimeStamp(),
                                         userContext.getJodaTimeZone());
            formattedValue = ExportValueFormatter.formatTimestamp(date, field);
        }
        return formattedValue;

    }

    private String getUOMValue(Meter meter, ExportField field, boolean isPreview) {
        String valueString = "";
        if (isPreview) {
            valueString = previewUOMValue;
        } else {
            try {
                LitePoint point =
                    attributeService.getPointForAttribute(meter, field.getAttribute()
                        .getAttribute());
                LiteUnitMeasure unitMeasure =
                    unitMeasureDao.getLiteUnitMeasure(point.getUofmID());
                valueString = unitMeasure.getUnitMeasureName();

            } catch (IllegalUseOfAttribute e) {
                // missing value
            }
        }
        return valueString;

    }

    private Date getStartDate(ExportAttribute attribute, Date stopDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(stopDate);
        cal.add(Calendar.DATE, -1 * attribute.getDaysPrevious());
        return cal.getTime();
    }

}
