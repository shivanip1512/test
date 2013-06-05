package com.cannontech.analysis.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.ServletRequestUtils;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.report.MeterOutageReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.MeterOutageModel;
import com.cannontech.analysis.tablemodel.MeterOutageModel.MeterOutageOrderByFilter;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.util.ServletRequestEnumUtils;

public class MeterOutageController extends ReportControllerBase {

	private ReportFilter[] filterModelTypes = new ReportFilter[] {ReportFilter.GROUPS, ReportFilter.DEVICE, ReportFilter.METER};
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
    private static final String ATT_ORDER_DIR = "orderDirection";
    private static final String ATT_ORDER_BY = "orderByOutages";
    private static final String ATT_MINIMUM_OUTAGE_SECS = "minOutageSeconds";

    public MeterOutageController() {
		super();
		model = YukonSpringHook.getBean("meterOutageModel", MeterOutageModel.class);
		report = new MeterOutageReport(model);
	}

	@Override
	public YukonReportBase getReport() {
		return report;
	}

    @SuppressWarnings("unchecked")
    public ReportModelBase<MeterOutageModel> getModel() {
        return report.getModel();
    }
    
	@Override
	public void setRequestParameters(HttpServletRequest req) {
		super.setRequestParameters(req);

		MeterOutageModel meterOutageModel = (MeterOutageModel)model;
        meterOutageModel.setMinOutageSecs(ServletRequestUtils.getIntParameter(req, ATT_MINIMUM_OUTAGE_SECS, 0));
        meterOutageModel.setOrderBy(ServletRequestEnumUtils.getEnumParameter(req,  MeterOutageOrderByFilter.class, ATT_ORDER_BY,  MeterOutageOrderByFilter.DEVICE_NAME));
        meterOutageModel.setOrderDirection(ServletRequestEnumUtils.getEnumParameter(req,  Order.class, ATT_ORDER_DIR,  Order.FORWARD));
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
        sb.append("          <td class='TitleHeader'>&nbsp;Minimum Outage Duration</td>");
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td class='main'>");
        sb.append("            <input type='text' name='"+ATT_MINIMUM_OUTAGE_SECS +"' value='0'>&nbsp;seconds");  
        sb.append("          </td>" + LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);       

        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td class='TitleHeader'>&nbsp;Order By</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);
        for (MeterOutageOrderByFilter orderBy : MeterOutageOrderByFilter.values()) {
            sb.append("        <tr>" + LINE_SEPARATOR);
            sb.append("          <td><input type='radio' name='"+ATT_ORDER_BY+"' value='" + orderBy + "' ");  
            sb.append((orderBy == MeterOutageOrderByFilter.DEVICE_NAME ? "checked" : "") + ">" + orderBy.getDisplayName()+ LINE_SEPARATOR);
            sb.append("          </td>" + LINE_SEPARATOR);
            sb.append("        </tr>" + LINE_SEPARATOR);
        }
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);

        sb.append("    <td valign='top'>" + LINE_SEPARATOR);
        sb.append("      <table width='100%' border='0' cellspacing='0' cellpadding='0' class='TableCell'>" + LINE_SEPARATOR);     
        sb.append("        <tr>" + LINE_SEPARATOR);
        sb.append("          <td class='TitleHeader'>&nbsp;Order Direction</td>" +LINE_SEPARATOR);
        sb.append("        </tr>" + LINE_SEPARATOR);

        sb.append("        <tr><td><input type='radio' name='" +ATT_ORDER_DIR + "' value='" + Order.FORWARD + "' checked>Ascending</td></tr>" + LINE_SEPARATOR);
        sb.append("        <tr><td><input type='radio' name='" +ATT_ORDER_DIR + "' value='" + Order.REVERSE + "'>Descending</td></tr>" + LINE_SEPARATOR);
        
        sb.append("      </table>" + LINE_SEPARATOR);
        sb.append("    </td>" + LINE_SEPARATOR);
        
        sb.append("  </tr>" + LINE_SEPARATOR);
        sb.append("</table>" + LINE_SEPARATOR);
        return sb.toString();
    }
}
