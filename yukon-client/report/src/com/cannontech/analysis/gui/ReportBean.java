/*
 * Created on Feb 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.gui;

import java.util.Date;

import org.jfree.report.JFreeReport;
import org.jfree.report.function.FunctionInitializeException;

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
	private ReportModelBase model = null;
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
	    if (getModel() == null)
	        return ServletUtil.getYesterday();
	    
		return getModel().getStartDate();
	}
	public Date getStopDate()
	{
	    if( getModel() == null)
	        return ServletUtil.getTomorrow();
		return getModel().getStopDate();
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
	 * Returns the ReportModelBase, creates a new ReportModelBase if isChanged flag is true.
	 * Resets the isChanged flag on new model creation.
	 * @return
	 */
	public ReportModelBase getModel()
	{
	    if (model == null || isChanged())
	    {
	        model = ReportTypes.create(getType());
	        setChanged(false);
	    }
		return model;
	}
	/**
	 * Returns a JFreeReport instance using a YukonReportBase parameter.
	 * Uses the getModel() field value to create the YukonReportBase parameter.
	 * Collects the model data and sets the JFreeReports data field using the getModel() field.  
	 * @return
	 * @throws FunctionInitializeException
	 */
	public JFreeReport createReport() throws FunctionInitializeException
	{
	    //Create an instance of JFreeReport from the YukonReportBase
	    YukonReportBase report = ReportFuncs.createYukonReport(getModel());
	    JFreeReport jfreeReport = report.createReport();
	    
	    //Collecto the data for the model and set the freeReports data
	    getModel().collectData();
	    jfreeReport.setData(getModel());
	    
	    return jfreeReport;
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
    /**
     * @param model The model to set.
     */
    public void setModel(ReportModelBase model)
    {
        this.model = model;
    }
}
