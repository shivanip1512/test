/*
 * Created on Feb 22, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.gui;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.jfree.report.JFreeReport;
import org.jfree.report.function.FunctionInitializeException;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.analysis.ReportGroup;
import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.controller.ReportController;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;
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
    @Deprecated
    private ReportModelBase model = null;
    private String type = "";
    private Vector<ReportTypes> availReportTypes = new Vector<ReportTypes>();
    private String groupType = "";
    private int userID = UserUtils.USER_YUKON_ID;
    private int energyCompanyID = EnergyCompanyDao.DEFAULT_ENERGY_COMPANY_ID;
    
    private String start = "";
    private Date startDate = null;
    private String stop = "";
    private Date stopDate = null;
    
    private boolean isChanged = false;
    private ReportController reportController;
    
    private String selectedReportFilter = null;
    private String selectedReportFilterValues = null;
    
    /**
     * 
     */
    public ReportBean()
    {
        super();
        CTILogger.info("Report Bean Initialized");
    }

    public void setType(String type) {
        
        if( !this.type.equals(type)) {
            this.type = type;
            setChanged(true);
        }
    }
    public void setType(ReportTypes reportType) {
        setType(reportType.toString());
    }

    public void setGroupType(String groupType)
    {
        if( !this.groupType.equals(groupType)) {
            this.groupType = groupType;
            loadReportTypes();
            final Vector<ReportTypes> reportTypes = getReportTypes();
            if(!reportTypes.isEmpty())
             {
                setType(reportTypes.get(0));    //default to the first one
            }
            setChanged(true);
        }
    }

    public void setStart(String startDateString) {
        if (StringUtils.isBlank(startDateString)) {
            return;
        }
        start = startDateString;
        startDate = ServletUtil.parseDateStringLiberally(start);
    }

    public void setStop(String stopDateString) {
        
        if (StringUtils.isBlank(stopDateString)) {
            return;
        }
        stop = stopDateString;
        stopDate = ServletUtil.parseDateStringLiberally(stop);
        
    }
    
    public String getSelectedReportFilter() {
        return selectedReportFilter;
    }
    public void setSelectedReportFilter(String selectedReportFilter) {
        this.selectedReportFilter = selectedReportFilter;
    }
    
    public String getSelectedReportFilterValues() {
        return selectedReportFilterValues;
    }
    public List<String> getSelectedReportFilterValuesList() {
        if (getSelectedReportFilterValues() == null) {
            return null;
        }
        return Arrays.asList(StringUtils.split(getSelectedReportFilterValues(), ","));
    }
    public void setSelectedReportFilterValues(String selectedReportFilterValues) {
        this.selectedReportFilterValues = selectedReportFilterValues;
    }
    
    /**
     * Returns the EnergyCompanyID for the reportBean's user
     * @return
     */
    public int getEnergyCompanyID() {
        return energyCompanyID;
    }

    /**
     * Set the EnergyCompanyID for the reportBean's user
     * @param i
     */
    public void setEnergyCompanyID(int ecID) {
        energyCompanyID = ecID;
    }

    /**
     * Returns the userID
     * @return
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Set userID
     * @param i
     */
    public void setUserID(int i) {
        userID = i;
    }

    public Date getStartDate() {
        
        if (startDate == null) {
            return ServletUtil.getYesterday();
        }
        
        return startDate;
    }

    public Date getStopDate() {
        if (stopDate == null) {
            return ServletUtil.getTomorrow();
        }
        return stopDate;
    }
    
    public ReportTypes getReportType() {
        if (type != "") {
            return ReportTypes.valueOf(type);
        }
        return null;
    }

    public ReportGroup getReportGroup() {
        if( groupType != "") {
            return ReportGroup.valueOf(groupType);
        }
        return null;
    }

    /**
     * Returns the ReportModelBase, creates a new ReportModelBase if isChanged flag is true.
     * Resets the isChanged flag on new model creation.
     * @return
     * @ deprecated This class should only use the reportController where possible.
     */
    public ReportModelBase getModel()
    {
        if( getReportController() == null) {
            return null;
        }
        return getReportController().getReport().getModel();
    }
    
    public boolean hasFilter() {
        if(reportController == null) {
            return false;
        }else {
            return reportController.reportHasFilter(userID);
        }
    }
    
    public LinkedHashMap<ReportFilter,List<? extends Object>> getFilterObjectsMap() {
        return new LinkedHashMap<ReportFilter, List<? extends Object>>(reportController.getFilterObjectsMap(userID));
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
            //Collect the data for the model
            //It is important to collect the data before the JFreeReport is created so that any data collected may 
            //  be already available during the ReportHeaders/Footers creation. 
            getModel().collectData();

            //Create an instance of JFreeReport from the YukonReportBase
            YukonReportBase report = reportController.getReport();
            JFreeReport jfreeReport = report.createReport();
            
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
    public boolean isChanged() {
        return isChanged;
    }

    /**
     * Set isChanged value
     * @param b
     */
    public void setChanged(boolean b) {
        isChanged = b;
    }

    public String buildOptionsHTML() {
        if( getModel() == null) {
            return "";
        }

        return reportController.getHTMLOptionsTable();
    }

    /**
     * @param model The model to set.
     */
    public void setModel(ReportModelBase model) {
        this.model = model;
        if( model != null)
        {
            model.setEnergyCompanyID(new Integer(getEnergyCompanyID()));
            model.setUserID(new Integer(getUserID()));
        }
    }

    
    /**
     * Returns an array of reportType ints that are valid for grpType
     * Settlement groupType reportTypes will be loaded based on getEnergyCompanyID()
     * @param groupType
     * @return
     */
    public Vector<ReportTypes> getReportTypes() {
        return availReportTypes;
    }
    
    /**
     * Load the availReportTypes for getReportGroup().
     */
    private void loadReportTypes() {
        
        if (getReportGroup() == ReportGroup.SETTLEMENT)
        {
            //Need to replace types with the settlement report types based on the energyCompany's Settlement list and yukonListEntries.
            LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance().getEnergyCompany( getEnergyCompanyID() );
            SelectionListService selectionListService = YukonSpringHook.getBean(SelectionListService.class);
            YukonSelectionList list = selectionListService.getSelectionList(liteEC,
                                        YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE);
            List<YukonListEntry> yukListEntries = list.getYukonListEntries();
            //Loop through all list entries, there may be more than one settlement type per energycompany.
            for (int i = 0; i < yukListEntries.size(); i ++)
            {
                YukonListEntry entry = yukListEntries.get(i);
                Vector<ReportTypes> settlementTypes = ReportTypes.getSettlementReportTypes(entry.getYukonDefID());
                //Loop through all reportTypes per yukDefID and add them to intList.
                availReportTypes = settlementTypes;
            }
        } else {
            availReportTypes = ReportTypes.getGroupReportTypes(getReportGroup());
        }
    }
    
    public ReportController getReportController() {
        if (reportController == null || isChanged()) {
            createController();
        }
        return reportController;
    }

    public void createController() {
        if( getReportType() != null) {
            reportController = ReportTypes.create(getReportType());
        }
        
        if (reportController == null) {
            setModel(null);
        } else {
            setModel(reportController.getReport().getModel());
        }
        setChanged(false);
    }
}
