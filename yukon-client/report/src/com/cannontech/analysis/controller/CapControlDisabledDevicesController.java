package com.cannontech.analysis.controller;

import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.CapControlDisabledDevicesReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.CapControlDisabledDevicesModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.spring.YukonSpringHook;

public class CapControlDisabledDevicesController extends CapControlReportControllerBase {
    
    private ReportFilter[] filterModelTypes = new ReportFilter[]{
            ReportFilter.AREA,
            ReportFilter.CAPCONTROLSUBSTATION,
            ReportFilter.CAPCONTROLSUBBUS,
            ReportFilter.CAPCONTROLFEEDER,
            ReportFilter.CAPBANK
            };
    
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    protected static final String ATT_DEVICE_TYPES = "deviceTypes";
    
    public CapControlDisabledDevicesController() {
        super();
        model = (CapControlDisabledDevicesModel)YukonSpringHook.getBean("capControlDisabledDevicesModel");
        report = new CapControlDisabledDevicesReport(model);
    }

    public YukonReportBase getReport() {
        return report;
    }

    @SuppressWarnings("unchecked")
    public ReportModelBase getModel() {
        return report.getModel();
    }
    
    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }
    
    public void setRequestParameters(HttpServletRequest request) {
        CapControlDisabledDevicesModel ddModel = (CapControlDisabledDevicesModel) model;
        super.setRequestParameters(request);
        String[] deviceTypes = request.getParameterValues(ATT_DEVICE_TYPES);
        ddModel.setDeviceTypes(deviceTypes);
    }
    
    public String getHTMLOptionsTable() {
        String html = "";
        html += "<table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell' checked>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td class='TitleHeader'>&nbsp;Device Types</td>" +LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_DEVICE_TYPES+"' value='Area' checked>Area"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_DEVICE_TYPES+"' value='Substation' checked>Substation"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_DEVICE_TYPES+"' value='Sub Bus' checked>Substation Bus"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_DEVICE_TYPES+"' value='Feeder' checked>Feeder"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_DEVICE_TYPES+"' value='Cap Bank' checked>Cap Bank"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "<tr>" + LINE_SEPARATOR;
        html += "<td><input type='checkbox' name='"+ATT_DEVICE_TYPES+"' value='CBC' checked>CBC"+ LINE_SEPARATOR;
        html += "</td>" + LINE_SEPARATOR;
        html += "</tr>" + LINE_SEPARATOR;
        html += "</table>" + LINE_SEPARATOR;
        return html;
    }
    
    public LinkedHashMap<ReportFilter,List<? extends Object>> getFilterObjectsMap() {
        LinkedHashMap<ReportFilter, List<? extends Object>> result = new LinkedHashMap<ReportFilter, List<? extends Object>>();
        return result;
    }
}