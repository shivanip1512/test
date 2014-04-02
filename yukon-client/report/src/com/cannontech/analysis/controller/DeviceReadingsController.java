package com.cannontech.analysis.controller;

import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.DeviceReadingsReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.DeviceReadingsModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.Sets;

public class DeviceReadingsController extends ReportControllerBase {
    
    private YukonUserContextMessageSourceResolver resolver;

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
	private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS, ReportFilter.DEVICE};

	private AttributeService attributeService;
	
    public DeviceReadingsController() {
        super();
        model = YukonSpringHook.getBean("deviceReadingsModel", DeviceReadingsModel.class);
        report = new DeviceReadingsReport(model);
        resolver = YukonSpringHook.getBean("yukonUserContextMessageSourceResolver", YukonUserContextMessageSourceResolver.class);
        attributeService = YukonSpringHook.getBean(AttributeService.class);
    }

    @Override
    public YukonReportBase getReport() {
        return report;
    }

    @Override
    public ReportFilter[] getFilterModelTypes() {
    	return filterModelTypes;
    }
    
    @SuppressWarnings("unchecked")
    public ReportModelBase<DeviceReadingsModel> getModel() {
        return report.getModel();
    }

    @Override
    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
        
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        DeviceReadingsModel deviceReadingsModel = (DeviceReadingsModel)model;
        String attributeString = ServletUtil.getParameter(request, "dataAttribute");
        String startHour = ServletUtil.getParameter(request, "startHour");
        String startMinute = ServletUtil.getParameter(request, "startMinute");
        String stopHour = ServletUtil.getParameter(request, "stopHour");
        String stopMinute = ServletUtil.getParameter(request, "stopMinute");
        String resultType = ServletUtil.getParameter(request, "resultType");
        boolean excludeDisabledDevices = ServletRequestUtils.getBooleanParameter(request, "excludeDisabledDevices", false);
        deviceReadingsModel.setExcludeDisabledDevices(excludeDisabledDevices);
        
        if(StringUtils.isNotBlank(attributeString)){
            Attribute attribute = BuiltInAttribute.valueOf(attributeString);
            deviceReadingsModel.setAttribute(attribute);
        }
        if(StringUtils.isNotBlank(startHour)){
            /* Joda treats hours as 1-24, Reports.jsp uses 0-23, halarity insues. */
            if(startHour.equalsIgnoreCase("0")) startHour = "24";
            if(stopHour.equalsIgnoreCase("0")) stopHour = "24";
            TimeZone timeZone = userContext.getTimeZone();
            DateTimeFormatter pattern = DateTimeFormat.forPattern("kk:mm");
            DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
            /* Construct new start date/time */
            DateTime startDateTime = pattern.parseDateTime(startHour + ":" + startMinute);
            LocalTime startLocalTime = startDateTime.toLocalTime();
            DateTime newStartDateTime = new DateTime(deviceReadingsModel.getStartDate(), dateTimeZone);
            DateTime startTime = startLocalTime.toDateTime(newStartDateTime);
            deviceReadingsModel.setStartDate(startTime.toDate());
            /* Construct new stop date/time */
            DateTime stopDateTime = pattern.parseDateTime(stopHour + ":" + stopMinute);
            LocalTime stopLocalTime = stopDateTime.toLocalTime();
            DateTime newStopDateTime = new DateTime(deviceReadingsModel.getStopDate(), dateTimeZone);
            DateTime stopTime = stopLocalTime.toDateTime(newStopDateTime);
            deviceReadingsModel.setStopDate(stopTime.toDate());
        }

        if(StringUtils.isNotBlank(resultType)){
            boolean all = resultType.equalsIgnoreCase("all");
            deviceReadingsModel.setRetrieveAll(all);
        }        
    }
    
    @Override
    public boolean useStartStopTimes() {
        return true;
    }

    @Override
    public String getHTMLOptionsTable() {
        
        Set<BuiltInAttribute> measuredAttributeSet= Sets.difference(Sets.newHashSet(BuiltInAttribute.values()), BuiltInAttribute.getRfnEventTypes());
        
        Map<BuiltInAttribute, String> measuredAttributes =  attributeService.resolveAllToString(measuredAttributeSet, super.getUserContext());
        Map<BuiltInAttribute, String> eventAttributes =  attributeService.resolveAllToString(BuiltInAttribute.getRfnEventTypes(), super.getUserContext());
        
        final StringBuilder sb = new StringBuilder();
        sb.append("<table style='padding: 10px;' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("  <tr>" + LINE_SEPARATOR);
        
        sb.append("    <td class='title-header' style='padding-right: 5px;'>Data Attribute: </td>");
        sb.append("    <td class='main'>" + LINE_SEPARATOR);
        sb.append("      <select id=\"dataAttribute\" name=\"dataAttribute\">" + LINE_SEPARATOR);
        sb.append("        <optgroup label=\"Measured Attributes\">" + LINE_SEPARATOR);
        for (BuiltInAttribute attribute : measuredAttributes.keySet()) {
            sb.append("        <option value=\"" + attribute + "\">" + measuredAttributes.get(attribute) + "</option>" + LINE_SEPARATOR);
        }
        sb.append("        </optgroup>" + LINE_SEPARATOR);
        sb.append("        <optgroup label=\"Event Attributes\">" + LINE_SEPARATOR);
        for (BuiltInAttribute attribute : eventAttributes.keySet()) {
            sb.append("        <option value=\"" + attribute + "\">" + eventAttributes.get(attribute) + "</option>" + LINE_SEPARATOR);
        }
        sb.append("        </optgroup>" + LINE_SEPARATOR);
        sb.append("      </select>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);

        sb.append("    <td style='padding-left: 30px;vertical-align: middle;'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='radio' name='resultType' value='all' title='Get All the readings for each device within the date/time range.' checked='checked' onclick='enableDates(true)'>"); 
        sb.append(" Get All Results</td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='radio' name='resultType' value='last' title='Get only the most recent reading for each device.' onclick='enableDates(false)'>"); 
        sb.append(" Get Most Recent Results <span style='font-weight: bold;''>(Ignores date and time range.)</span></td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>");
        
        sb.append("    <td valign='top' style='padding-left: 30px;'>" + LINE_SEPARATOR);        
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td valign='top' class='title-header'>Disabled Devices</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='Checkbox' name='excludeDisabledDevices' value='true'> Exclude Disabled Devices" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);
        
        sb.append("  </tr>" + LINE_SEPARATOR);
        sb.append("</table>" + LINE_SEPARATOR);
        
        return sb.toString();
    }

}