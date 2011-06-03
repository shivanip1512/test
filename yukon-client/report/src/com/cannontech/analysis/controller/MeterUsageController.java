package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.MeterUsageReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.MeterUsageModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.util.ServletRequestEnumUtils;

public class MeterUsageController extends ReportControllerBase {

	private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS, ReportFilter.DEVICE, ReportFilter.METER};
	
	private static final String ATT_EXCLUDE_DISABLED_DEVICES = "excludeDisabledDevices";
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	public MeterUsageController() {
		super();
		model = YukonSpringHook.getBean("meterUsageModel", MeterUsageModel.class);
		report = new MeterUsageReport(model);
	}

	@Override
	public YukonReportBase getReport() {
		return report;
	}

    @SuppressWarnings("unchecked")
    public ReportModelBase<MeterUsageModel> getModel() {
        return report.getModel();
    }
    
	@Override
	public void setRequestParameters(HttpServletRequest req) {
		super.setRequestParameters(req);

		MeterUsageModel meterUsageModel = (MeterUsageModel)model;
        String param = req.getParameter(ATT_EXCLUDE_DISABLED_DEVICES);
        if( param != null) {
            meterUsageModel.setExcludeDisabledDevices(CtiUtilities.isTrue(param));
        }

        Attribute attribute = ServletRequestEnumUtils.getEnumParameter(req, BuiltInAttribute.class, "dataAttribute");
        if (attribute != null){
            meterUsageModel.setAttribute(attribute);
        }
	}

	@Override
    public boolean useStartStopTimes() {
        return true;
    }

	@Override
	public ReportFilter[] getFilterModelTypes() {
		return filterModelTypes;
	}
	
    @Override
    public String getHTMLOptionsTable() {
	    final StringBuilder sb = new StringBuilder();
        
	    sb.append("<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("  <tr>" + LINE_SEPARATOR);
        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("    		<td class='TitleHeader' style='padding-right: 5px;'>Data Attribute: </td>");
        sb.append("        </tr>" + LINE_SEPARATOR);

        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("    		 <td class='main'>" + LINE_SEPARATOR);
        sb.append("      	   <select id=\"dataAttribute\" name=\"dataAttribute\">" + LINE_SEPARATOR);
        for (BuiltInAttribute attribute : BuiltInAttribute.values()) {
        	if (attribute.isAccumulator()) {
		        sb.append("        	     <option value=\"" + attribute + "\">" + attribute.getDescription() + "</option>" + LINE_SEPARATOR);
        	}
		}
        sb.append("      	   </select>" + LINE_SEPARATOR);
        sb.append("    		 </td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        	
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);
        
        sb.append("    <td valign='top'>" + LINE_SEPARATOR);        
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td valign='top' class='TitleHeader'>Disabled Devices</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='checkbox' name='" + ATT_EXCLUDE_DISABLED_DEVICES +"' value='true'> Exclude Disabled Devices" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);
        
        sb.append("  </tr>" + LINE_SEPARATOR);
        sb.append("</table>" + LINE_SEPARATOR);
        
	    return sb.toString();

    }
}
