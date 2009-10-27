package com.cannontech.analysis.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.DeviceReadStatisticsSummaryReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.DeviceReadStatisticsSummaryModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;

public class DeviceReadStatisticsSummaryController extends ReportControllerBase{

    private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS};
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public DeviceReadStatisticsSummaryController() {
        super();
        model = YukonSpringHook.getBean("deviceReadStatisticsSummaryModel", DeviceReadStatisticsSummaryModel.class);
        report = new DeviceReadStatisticsSummaryReport(model);
    }

    public YukonReportBase getReport() {
        return report;
    }

    @SuppressWarnings("unchecked")
    public ReportModelBase<DeviceReadStatisticsSummaryModel> getModel() {
        return report.getModel();
    }

    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }

    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
        DeviceReadStatisticsSummaryModel deviceReadSummaryModel = (DeviceReadStatisticsSummaryModel)model;
        String attributeString = ServletUtil.getParameter(request, "dataAttribute");
        
        if(StringUtils.isNotBlank(attributeString)){
            Attribute attribute = BuiltInAttribute.valueOf(attributeString);
            deviceReadSummaryModel.setAttribute(attribute);
            deviceReadSummaryModel.setTitle("Device Read Statistics Summary: " + attribute.getDescription());  
        }
        
        int filterModelType = ServletRequestUtils.getIntParameter(request, ReportModelBase.ATT_FILTER_MODEL_TYPE, -1);

        if (filterModelType == ReportFilter.GROUPS.ordinal()) {
            String names[] = ServletRequestUtils.getStringParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
            List<String> namesList = Arrays.asList(names); 
            deviceReadSummaryModel.setGroupsFilter(namesList);
        } 
        
        YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(request);
        TimeZone timeZone = yukonUserContext.getTimeZone();
        DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
        DateTime now = new DateTime(new Date(), dateTimeZone);
        DateTime lastMonth = now.minusDays(31);
        deviceReadSummaryModel.setLastMonthDate(lastMonth);
    }
    
    public String getHTMLOptionsTable() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<table style='padding: 10px;' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("    <tr>" + LINE_SEPARATOR);
        
        sb.append("        <td class='TitleHeader' style='padding-right: 5px;'>Data Attribute: </td>");
        sb.append("        <td class='main'>" + LINE_SEPARATOR);
        sb.append("            <select id=\"dataAttribute\" name=\"dataAttribute\">" + LINE_SEPARATOR);
        int i = 0;
        for(BuiltInAttribute attribute : BuiltInAttribute.values()){
            sb.append("              <option value=\"" + attribute + "\""); 
            if(i == 0) sb.append(" selected "); 
            sb.append(">" + attribute.getDescription() + "</option>" + LINE_SEPARATOR);
            i++;
        }
        sb.append("            </select>" + LINE_SEPARATOR);
        sb.append("        </td>" + LINE_SEPARATOR);

        sb.append("    </tr>" + LINE_SEPARATOR);
        sb.append("</table>" + LINE_SEPARATOR);
        
        return sb.toString();
    }
}