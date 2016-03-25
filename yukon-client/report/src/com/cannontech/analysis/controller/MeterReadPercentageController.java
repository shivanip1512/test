package com.cannontech.analysis.controller;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.MeterReadPercentageReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.MeterReadPercentageModel;
import com.cannontech.analysis.tablemodel.MeterReadPercentageModel.MeterReadPercentagePeriod;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.util.ServletRequestEnumUtils;

public class MeterReadPercentageController extends ReportControllerBase{
    
    private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS};
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String PERIOD = "period";
   
    public MeterReadPercentageController() {
        super();
        model = YukonSpringHook.getBean("meterReadPercentageModel", MeterReadPercentageModel.class);
        report = new MeterReadPercentageReport(model);
    }

    @Override
    public YukonReportBase getReport() {
        return report;
    }

    @SuppressWarnings("unchecked")
    public ReportModelBase<MeterReadPercentageModel> getModel() {
        return report.getModel();
    }

    @Override
    public ReportFilter[] getFilterModelTypes() {
        return filterModelTypes;
    }

    @Override
    public void setRequestParameters(HttpServletRequest request) {
        super.setRequestParameters(request);
        MeterReadPercentageModel meterReadPercentageModel = (MeterReadPercentageModel)model;
   
        ReportFilter filter = ServletRequestEnumUtils.getEnumParameter(request, ReportFilter.class, ReportModelBase.ATT_FILTER_MODEL_TYPE, ReportFilter.NONE);

        if (filter == ReportFilter.GROUPS) {
            String names[] = ServletRequestUtils.getStringParameters(request, ReportModelBase.ATT_FILTER_MODEL_VALUES);
            List<String> namesList = Arrays.asList(names); 
            meterReadPercentageModel.setGroupsFilter(namesList);
        } 
        
        String period = request.getParameter(PERIOD);
        if(StringUtils.isNotEmpty(period)){
            meterReadPercentageModel.setPeriod(MeterReadPercentagePeriod.valueOf(request.getParameter(PERIOD)));
        }
    }
    
    @Override
    public String getHTMLOptionsTable() {
        
        MeterReadPercentageModel meterReadPercentageModel = (MeterReadPercentageModel)model;
       
        final StringBuilder sb = new StringBuilder();
        sb.append("<table style='padding: 5px;' class='TableCell' align='center'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td class='title-header' align='center'>&nbsp;Period</td>");
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("<td><select  id=\"daysId\" name='period' onchange='change()' >" + LINE_SEPARATOR);
        for (MeterReadPercentagePeriod period : MeterReadPercentagePeriod.values()) {
                 sb.append("<option name='"+ PERIOD +"' value='" + period + "' " +  
                        (meterReadPercentageModel.getPeriod() == period ? "selected" : "") + ">" + period.getDisplayName() + LINE_SEPARATOR);
            }
        sb.append("</select>" + LINE_SEPARATOR );
        sb.append("          </td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);       
        sb.append("</table>" + LINE_SEPARATOR);
        return sb.toString();
    }
}
