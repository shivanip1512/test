/*
 * Created on Feb 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.gui;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.jfree.report.JFreeReport;
import org.jfree.report.function.FunctionInitializeException;
import org.jfree.report.util.IntList;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.controller.ReportController;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.ReportModelBase.ReportFilter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.user.UserUtils;
import com.cannontech.util.ServletUtil;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ReportBean
{
	/**
	 * @deprecated see getter method
	 */
	private ReportModelBase model = null;
	private int type = -1;
	private int[] availReportTypes = new int[0];
	private int groupType = -1;
	private int userID = UserUtils.USER_YUKON_ID;
	private int energyCompanyID = EnergyCompany.DEFAULT_ENERGY_COMPANY_ID;
	
	private String start = "";
    private Date startDate = null;
	private String stop = "";
    private Date stopDate = null;
	
	private boolean isChanged = false;
    private ReportController reportController;
	
	/**
	 * 
	 */
	public ReportBean()
	{
		super();
		CTILogger.info("Report Bean Initialized");
	}

	public static void main(final String[] args) throws Exception
	{
		ReportBean bean = new ReportBean();
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
        if (StringUtils.isBlank(startDateString)) {
            return;
        }
		start = startDateString;
		startDate = ServletUtil.parseDateStringLiberally(start);
	}

	public Date getStartDate()
	{
	    if (startDate == null) {
            return ServletUtil.getYesterday();
        }
	    
		return startDate;
	}
    
	public Date getStopDate()
	{
	    if (stopDate == null) {
            return ServletUtil.getTomorrow();
        }
		return stopDate;
	}
    
	/**
	 * @param string
	 */
	public void setStop(String stopDateString)
	{
        if (StringUtils.isBlank(stopDateString)) {
            return;
        }
		stop = stopDateString;
		stopDate = ServletUtil.parseDateStringLiberally(stop);
		
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
			loadReportTypes();
			int [] reportTypes = getReportTypes();
			if( reportTypes.length > 0)
				setType(reportTypes[0]);	//default to the first one
			setChanged(true);
		}
	}

	/**
	 * Returns the ReportModelBase, creates a new ReportModelBase if isChanged flag is true.
	 * Resets the isChanged flag on new model creation.
	 * @return
     * @ deprecated This class should only use the reportController where possible.
	 */
	public ReportModelBase getModel()
	{
		if( getReportController() == null)
			return null;
		return getReportController().getReport().getModel();
	}
    
    public boolean hasFilter() {
        //getModel();
        if(reportController == null) {
            return false;
        }else {
            return !reportController.getFilterObjectsMap().isEmpty();
        }
    }
    
    public Map<ReportFilter,List<? extends Object>> getFilterObjectsMap() {
       // getModel();
        return reportController.getFilterObjectsMap();
    }
    
	/**
	 * Returns a JFreeReport instance using a YukonReportBase parameter.
	 * Uses the getModel() field value to create the YukonReportBase parameter.
	 * Collects the model data and sets the JFreeReports data field using the getModel() field.  
	 * @return
	 * @throws FunctionInitializeException
	 */
	public JFreeReport createReport() {
	    try {
            //Create an instance of JFreeReport from the YukonReportBase
            YukonReportBase report = reportController.getReport();
            JFreeReport jfreeReport = report.createReport();
            
            //Collecto the data for the model and set the freeReports data
            getModel().collectData();
            jfreeReport.setData(getModel());
            
            return jfreeReport;
        } catch (FunctionInitializeException e) {
            throw new RuntimeException("Unable to create report for " + reportController, e);
        }
	}

	/**
	 * Returns true if some other parameter has changed.
	 * @return
	 */
	public boolean isChanged()
	{
		return isChanged;
	}

	/**
	 * Set isChanged value
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

		return reportController.getHTMLOptionsTable();
	}

    /**
     * @param model The model to set.
     */
    public void setModel(ReportModelBase model)
    {
        this.model = model;
        if( model != null)
        {
        	model.setEnergyCompanyID(new Integer(getEnergyCompanyID()));
        	model.setUserID(new Integer(getUserID()));
        }
    }
	/**
	 * Returns the EnergyCompanyID for the reportBean's user
	 * @return
	 */
	public int getEnergyCompanyID()
	{
		return energyCompanyID;
	}

	/**
	 * Set the EnergyCompanyID for the reportBean's user
	 * @param i
	 */
	public void setEnergyCompanyID(int ecID)
	{
		energyCompanyID = ecID;
	}

	/**
	 * Returns the userID
	 * @return
	 */
	public int getUserID()
	{
		return userID;
	}

	/**
	 * Set userID
	 * @param i
	 */
	public void setUserID(int i)
	{
		userID = i;
	}
	
	/**
	 * Returns an array of reportType ints that are valid for grpType
	 * Settlement groupType reportTypes will be loaded based on getEnergyCompanyID()
	 * @param groupType
	 * @return
	 */
	public int[] getReportTypes()
	{
		return availReportTypes;
	}
	
	/**
	 * Load the availReportTypes for getGroupType().
	 */
	private void loadReportTypes()
	{
		if (getGroupType() == ReportTypes.SETTLEMENT_REPORTS_GROUP)
		{
			//Need to replace types with the settlement report types based on the energyCompany's Settlement list and yukonListEntries.
			LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( getEnergyCompanyID() );
			YukonSelectionList list = liteEC.getYukonSelectionList(YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE);
			ArrayList yukListEntries = list.getYukonListEntries();
			IntList intList = new IntList(3);
			//Loop through all list entries, there may be more than one settlement type per energycompany.
			for (int i = 0; i < yukListEntries.size(); i ++)
			{
				YukonListEntry entry = (YukonListEntry)yukListEntries.get(i);
				int[] addTypes = ReportTypes.getSettlementReportTypes(entry.getYukonDefID());
				//Loop through all reportTypes per yukDefID and add them to intList.
				for (int j = 0; j < addTypes.length; j++)
				{
					intList.add(addTypes[j]);
				}
			}
			availReportTypes = intList.toArray();
		}
		else
			availReportTypes = ReportTypes.getGroupReportTypes(getGroupType());
	}
    
    public ReportController getReportController() {
    	if (reportController == null || isChanged())
    		createController();
        return reportController;
    }

    public void createController() {
        reportController = ReportTypes.create(getType());
        if (reportController == null) {
            setModel(null);
        } else {
            setModel(reportController.getReport().getModel());
        }
        setChanged(false);
    }
}
