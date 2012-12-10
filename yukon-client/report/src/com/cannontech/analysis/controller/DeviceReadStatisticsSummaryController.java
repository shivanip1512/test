package com.cannontech.analysis.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.DeviceReadStatisticsSummaryReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.DeviceReadStatisticsSummaryModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.util.ServletRequestEnumUtils;
import com.google.common.collect.Sets;

public class DeviceReadStatisticsSummaryController extends ReportControllerBase{

    private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS};
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private AttributeService attributeService = YukonSpringHook.getBean(AttributeService.class);

    public DeviceReadStatisticsSummaryController() {
        super();
        model = YukonSpringHook.getBean("deviceReadStatisticsSummaryModel", DeviceReadStatisticsSummaryModel.class);
        attributeService = YukonSpringHook.getBean(AttributeService.class);
        report = new DeviceReadStatisticsSummaryReport(model);
    }

    @Override
    public YukonReportBase getReport() {
        return report;
    }

    @SuppressWarnings("unchecked")
    public ReportModelBase<DeviceReadStatisticsSummaryModel> getModel() {
        return report.getModel();
    }

    @Override
    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }

    @Override
    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
        DeviceReadStatisticsSummaryModel deviceReadSummaryModel = (DeviceReadStatisticsSummaryModel)model;
        String attributeString = ServletUtil.getParameter(request, "dataAttribute");
        
        if(StringUtils.isNotBlank(attributeString)){
            Attribute attribute = BuiltInAttribute.valueOf(attributeString);
            deviceReadSummaryModel.setAttribute(attribute);
            deviceReadSummaryModel.setTitle("Device Read Statistics Summary: " + attribute.getMessage().getDefaultMessage());  
        }
        
        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(request, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);

        if (filter == ReportFilter.GROUPS) {
            String names[] = ServletRequestUtils.getStringParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
            List<String> namesList = Arrays.asList(names); 
            deviceReadSummaryModel.setGroupsFilter(namesList);
        } 
    }
    
    @Override
    public String getHTMLOptionsTable() {

        Set<BuiltInAttribute> measuredAttributeSet= Sets.difference(Sets.newHashSet(BuiltInAttribute.values()), BuiltInAttribute.getRfnEventTypes());
        
        Map<BuiltInAttribute, String> measuredAttributes =  attributeService.resolveAllToString(measuredAttributeSet, super.getUserContext());
        Map<BuiltInAttribute, String> eventAttributes =  attributeService.resolveAllToString(BuiltInAttribute.getRfnEventTypes(), super.getUserContext());

        final StringBuilder sb = new StringBuilder();
        sb.append("<table style='padding: 5px;' class='TableCell'>" + LINE_SEPARATOR);
        
        sb.append("    <tr>" + LINE_SEPARATOR);
        
        sb.append("        <td class='TitleHeader' style='padding-right: 5px;'>Data Attribute: </td>");
        sb.append("        <td class='main' style='padding-right: 5px;'>" + LINE_SEPARATOR);
        sb.append("            <select id=\"dataAttribute\" name=\"dataAttribute\">" + LINE_SEPARATOR);
        sb.append("                <optgroup label=\"Measured Attributes\">" + LINE_SEPARATOR);
        for (BuiltInAttribute attribute : measuredAttributes.keySet()) {
            sb.append("                <option value=\"" + attribute + "\">" + measuredAttributes.get(attribute) + "</option>" + LINE_SEPARATOR);
        }
        sb.append("                </optgroup>" + LINE_SEPARATOR);
        sb.append("                <optgroup label=\"Event Attributes\">" + LINE_SEPARATOR);
        for (BuiltInAttribute attribute : eventAttributes.keySet()) {
            sb.append("                <option value=\"" + attribute + "\">" + eventAttributes.get(attribute) + "</option>" + LINE_SEPARATOR);
        }
        sb.append("                </optgroup>" + LINE_SEPARATOR);
        sb.append("            </select>" + LINE_SEPARATOR);
        sb.append("        </td>" + LINE_SEPARATOR);
        
        sb.append("    </tr>" + LINE_SEPARATOR);
        sb.append("</table>" + LINE_SEPARATOR);
        
        return sb.toString();
    }
}