/*
 * Created on Feb 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.gui;

import java.util.Date;

import com.cannontech.analysis.ReportFuncs;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.util.ServletUtil;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReportBean
{
	private YukonReportBase report = null;
	private int type = -1;
	private int groupType = -1;
	
	private String start = "";
	private String stop = "";
	
	private boolean isChanged = false;
	
	/**
	 * 
	 */
	public ReportBean()
	{
		super();
		CTILogger.info(" CREATING REPORT BEAN");
	}

	public static void main(final String[] args) throws Exception
	{
		ReportBean bean = new ReportBean();
		CTILogger.info("HERE");
		bean.setType(16);
		bean.buildOptionsHTML();
	}
	/**
	 * @return
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * @return
	 */
	public String getStart()
	{
		return start;
	}

	/**
	 * @return
	 */
	public String getStop()
	{
		return stop;
	}

	/**
	 * @param i
	 */
	public void setType(int i)
	{
		if( i != type)
		{
			type = i;
			setChanged(true);
		}
	}

	/**
	 * @param string
	 */
	public void setStart(String startDateString)
	{
		start = startDateString;
		setStartDate(ServletUtil.parseDateStringLiberally(start));
	}

	public void setStartDate(Date newStartDate)
	{
		if( getStartDate().compareTo((Object)newStartDate) != 0 )
		{
			getModel().setStartDate(newStartDate);
		}
	}
	public Date getStartDate()
	{
		return getModel().getStartDate();
	}
	public Date getStopDate()
	{
		return getModel().getStopDate()
		;
	}
	/**
	 * @param string
	 */
	public void setStop(String stopDateString)
	{
		stop = stopDateString;
		setStopDate(ServletUtil.parseDateStringLiberally(stop));
		
	}
	public void setStopDate(Date newStopDate)
	{
		if( getStopDate().compareTo((Object)newStopDate) != 0 )
		{
			getModel().setStopDate( newStopDate);
		}
	}

	/**
	 * Return the report group type
	 * @return
	 */
	public int getGroupType()
	{
		return groupType;
	}

	/**
	 * Sets the report group type
	 * @param i
	 */
	public void setGroupType(int i)
	{
		if( i != groupType)
		{
			groupType = i;
			setType(ReportTypes.getGroupTypes(groupType)[0]);	//default to the first one
			setChanged(true);
		}
	}

	/**
	 * @return
	 */
	public YukonReportBase getReport()
	{
		if( report == null || isChanged())
		{
			report = ReportFuncs.createYukonReport(getType());
			setChanged(false); //reset the chagned flag here (?)
		}
		return report;
	}
	
	public ReportModelBase getModel()
	{
		return getReport().getModel();
	}

	/**
	 * @param base
	 */
	public void setReport(YukonReportBase base)
	{
		report = base;
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

	public String buildOptionsHTML()
	{
		if( getModel() == null)
			return "";

		return getModel().getHTMLOptionsTable();
	}
	
	public String buildBaseOptionsHTML()
	{
		if (getModel() == null)
			return "";
		return getModel().getHTMLBaseOptionsTable();
	}
}
