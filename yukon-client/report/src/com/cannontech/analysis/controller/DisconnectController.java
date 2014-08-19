package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.DisconnectReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.DisconnectModel;
import com.cannontech.analysis.tablemodel.DisconnectModel.DiscStatusOrderByFilter;
import com.cannontech.analysis.tablemodel.DisconnectModel.DiscStatusStateFilter;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.util.ServletRequestEnumUtils;

public class DisconnectController extends ReportControllerBase {

	private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS, ReportFilter.DEVICE, ReportFilter.METER};
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
    private static final String ATT_DISCONNECT_STATE = "disconnectState";
    private static final String ATT_SHOW_HISTORY = "history";
    private static final String ATT_ORDER_BY = "discOrderBy";

    public DisconnectController() {
		super();
		model = YukonSpringHook.getBean("disconnectModel", DisconnectModel.class);
		report = new DisconnectReport(model);
	}

	@Override
	public YukonReportBase getReport() {
		return report;
	}

    @SuppressWarnings("unchecked")
    public ReportModelBase<DisconnectModel> getModel() {
        return report.getModel();
    }
    
	@Override
	public void setRequestParameters(HttpServletRequest req) {
		super.setRequestParameters(req);

		DisconnectModel disconnectModel = (DisconnectModel)model;
		disconnectModel.setDiscStateFilter(ServletRequestEnumUtils.getEnumParameter(req,  DiscStatusStateFilter.class, ATT_DISCONNECT_STATE,  DiscStatusStateFilter.ALL_STATES));
		String showHistory = ServletRequestUtils.getStringParameter(req,  ATT_SHOW_HISTORY, null);
		disconnectModel.setShowHistory(StringUtils.isNotBlank(showHistory));
		disconnectModel.setOrderBy(ServletRequestEnumUtils.getEnumParameter(req,  DiscStatusOrderByFilter.class, ATT_ORDER_BY,  DiscStatusOrderByFilter.DEVICE_NAME));
		
		// this is kind of a hack, but didn't want to mess around with the jsp :(
		// showHistory uses a date range, otherwise it's just the latest available from "now"
		if(!disconnectModel.isShowHistory()){
		    disconnectModel.setStartDate(null);
		    disconnectModel.setStopDate(null);
		}
	}

	@Override
	public ReportFilter[] getFilterModelTypes() {
		return filterModelTypes;
	}
	
    @Override
    public String getHTMLOptionsTable() {
        final StringBuilder sb = new StringBuilder();
        sb.append("<script>" + LINE_SEPARATOR);
        sb.append("function enableCheckBox(value){" + LINE_SEPARATOR);
        sb.append("  if( value) {" + LINE_SEPARATOR);
        sb.append("    document.reportForm." + ATT_DISCONNECT_STATE + "[0].checked = !value;" + LINE_SEPARATOR);
        sb.append("    document.reportForm." + ATT_DISCONNECT_STATE + "[1].checked = !value;" + LINE_SEPARATOR);        
        sb.append("    document.reportForm." + ATT_DISCONNECT_STATE + "[2].checked = !value;" + LINE_SEPARATOR);
        sb.append("  } else {" + LINE_SEPARATOR);
        sb.append("    document.reportForm." + ATT_DISCONNECT_STATE + "[2].checked = !value;" + LINE_SEPARATOR);        
        sb.append("  } " + LINE_SEPARATOR);
        sb.append("  document.reportForm." + ATT_DISCONNECT_STATE + "[0].disabled = value;" + LINE_SEPARATOR);
        sb.append("  document.reportForm." + ATT_DISCONNECT_STATE + "[1].disabled = value;" + LINE_SEPARATOR);
        sb.append("  document.reportForm." + ATT_DISCONNECT_STATE + "[2].disabled = value;" + LINE_SEPARATOR);
        sb.append("}" + LINE_SEPARATOR);

        //Run this method on load, it's NOT in a function!
        sb.append("$(function () {enableDates(false)});" + LINE_SEPARATOR);
        sb.append("</script>" + LINE_SEPARATOR);

        sb.append("<table align='center' width='90%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("  <tr>" + LINE_SEPARATOR);
        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td valign='top' class='title-header'>Data Display</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td><input type='checkbox' name='" + ATT_SHOW_HISTORY +"' value='history' onclick='enableDates(this.checked);enableCheckBox(this.checked)'>Historical" + LINE_SEPARATOR);
        sb.append("          </td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);

        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);      
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td class='title-header'>&nbsp;Disconnect State</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        
        for (DiscStatusStateFilter discState : DiscStatusStateFilter.values()) {
            sb.append("        <tr>" + LINE_SEPARATOR);
            sb.append("          <td><input type='radio' name='"+ATT_DISCONNECT_STATE+"' value='" + discState + "' ");
            sb.append((discState == DiscStatusStateFilter.ALL_STATES ? "checked" : "") + ">" + discState.getDisplayName() + "</td>" + LINE_SEPARATOR);
            sb.append("        </tr>" + LINE_SEPARATOR);
        }
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);

        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td class='title-header'>&nbsp;Order By</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        for (DiscStatusOrderByFilter orderBy : DiscStatusOrderByFilter.values()) {
            sb.append("        <tr>" + LINE_SEPARATOR);
            sb.append("          <td><input type='radio' name='"+ATT_ORDER_BY+"' value='" + orderBy + "' ");  
            sb.append((orderBy == DiscStatusOrderByFilter.DEVICE_NAME ? "checked" : "") + ">" + orderBy.getDisplayName() + "</td>" + LINE_SEPARATOR);
            sb.append("        </tr>" + LINE_SEPARATOR);
        }
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);

        sb.append("  </tr>" + LINE_SEPARATOR);
        sb.append("</table>" + LINE_SEPARATOR);
        return sb.toString();
    }
}
