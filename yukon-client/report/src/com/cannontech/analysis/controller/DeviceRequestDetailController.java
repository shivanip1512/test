package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.report.DeviceRequestDetailReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.DeviceRequestDetailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.spring.YukonSpringHook;

public class DeviceRequestDetailController extends DeviceReportControllerBase{

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    
    public DeviceRequestDetailController() {
        super();
        model = YukonSpringHook.getBean("deviceRequestDetailModel", DeviceRequestDetailModel.class);
        report = new DeviceRequestDetailReport(model);
    }

    @Override
    public YukonReportBase getReport() {
        return report;
    }

    @SuppressWarnings("unchecked")
    public ReportModelBase<DeviceRequestDetailModel> getModel() {
        return report.getModel();
    }

    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
        DeviceRequestDetailModel deviceRequestDetailModel = (DeviceRequestDetailModel)model;

        String resultType = ServletRequestUtils.getStringParameter(request, "resultType", "daterange");
        if(resultType.equalsIgnoreCase("lifetime")){
            deviceRequestDetailModel.setLifetime(true);
        }
    }
    
    public String getHTMLOptionsTable() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<table style='padding: 10px;' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("    <tr>" + LINE_SEPARATOR);
        
        sb.append("        <td style='padding-left: 30px;vertical-align: middle;'>");
        sb.append("            <input type='radio' name='resultType' value='daterange' ");
        sb.append("                title='Get the request statistics within the date range. Only recorded up to the past 120 days.' ");
        sb.append("                checked='checked' onclick='enableDates(true)'>");
        
        sb.append(" Get Statistics For Date Range <span style='font-weight: bold;''> (Only the last 120 days are recorded.)</span>");
        sb.append("            <span style='padding-left: 10px;'><input type='radio' name='resultType' value='lifetime' ");
        sb.append("                title='Get the lifetime request statistics.' onclick='enableDates(false)'></span> ");
        sb.append(" Get Lifetime Statistics ");
        sb.append("        </td>");
        
        sb.append("    </tr>" + LINE_SEPARATOR);
        sb.append("</table>" + LINE_SEPARATOR);
        
        return sb.toString();
    }
}