/*
 * Created on Aug 19, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis;

import java.util.Date;

import org.jfree.report.JFreeReport;
import org.jfree.report.modules.output.pageable.base.ReportStateList;

import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.util.ServletUtil;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ReportBean
{
	private boolean isChanged = true;
	
	private JFreeReport fReport = null;
	private YukonReportBase reportBase = null;
	private ReportStateList stateList = null;
	
	private ReportModelBase modelBase = null;
	private Date startDate = ServletUtil.getToday();
	private Date endDate = ServletUtil.getTomorrow();
	
	private int type = ReportTypes.SYSTEM_LOG_DATA;
	private String ext = "png";

	/**
	 * @return
	 */
	public ReportModelBase getModelBase()
	{
		return modelBase;
	}

	/**
	 * @return
	 */
	public Date getEndDate()
	{
		return endDate;
	}

	/**
	 * @return
	 */
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @return
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * @param base
	 */
	public void setModelBase(ReportModelBase modelBase)
	{
		this.modelBase = modelBase;
	}

	/**
	 * @param date
	 */
	public void setEndDate(Date date)
	{
		endDate = date;
	}

	/**
	 * @param date
	 */
	public void setStartDate(Date date)
	{
		startDate = date;
	}

	/**
	 * @param i
	 */
	public void setType(int i)
	{
		type = i;
	}

	/**
	 * @return
	 */
	public String getExt()
	{
		return ext;
	}

	/**
	 * @return
	 */
	public JFreeReport getFReport()
	{
		return fReport;
	}

	/**
	 * @return
	 */
	public YukonReportBase getReportBase()
	{
		return reportBase;
	}

	/**
	 * @return
	 */
	public ReportStateList getStateList()
	{
		return stateList;
	}

	/**
	 * @param string
	 */
	public void setExt(String string)
	{
		ext = string;
	}

	/**
	 * @param report
	 */
	public void setFReport(JFreeReport report)
	{
		fReport = report;
	}

	/**
	 * @param base
	 */
	public void setReportBase(YukonReportBase base)
	{
		reportBase = base;
	}

	/**
	 * @param list
	 */
	public void setStateList(ReportStateList list)
	{
		stateList = list;
	}

	/**
	 * @return
	 */
	public boolean isChanged()
	{
		return isChanged;
	}

	/**
	 * @param b
	 */
	public void setChanged(boolean b)
	{
		isChanged = b;
	}

}
