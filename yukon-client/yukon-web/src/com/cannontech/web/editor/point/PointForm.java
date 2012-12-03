package com.cannontech.web.editor.point;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.pao.DBEditorTypes;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointOffsetUtils;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.database.db.point.PointLimit;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.web.editor.CapControlForm;
import com.cannontech.web.editor.DBEditorForm;
import com.cannontech.web.exceptions.InvalidPointLimits;
import com.cannontech.web.exceptions.InvalidPointOffsetException;
import com.cannontech.web.util.CBCSelectionLists;
import com.cannontech.web.util.JSFParamUtil;
import com.cannontech.web.wizard.PointWizardModel;
import com.cannontech.yukon.IDatabaseCache;

public class PointForm extends DBEditorForm {
    private static final Logger log = YukonLogManager.getLogger(PointForm.class);
    
    private SelectItem[] stateGroups = null;
    private SelectItem[] initialStates = null;
    private SelectItem[] decimalDigits = null;
    private SelectItem[] notifGroups = null;
    private SelectItem[] alarmCategories = null;
    
    private boolean isArchiveInterEnabled = false;
    private boolean isCalcRateEnabled = false;
    private List<AlarmTableEntry> alarmTableEntries = null;
        
    private static SelectItem[] logicalGroups = null;
    private static SelectItem[] uofms = null;


    //sub editors used to divide up functionality into different classes

    //limit point data for analog, calc & demand types
    private PointLimitEntry pointLimitEntry = null;
    
    //fdr point data
    private PointFDREntry pointFDREntry = null;
    
    //control point specific editor
    private PointAnalogControlEntry analogControlEntry = null;
    private PointStatusControlEntry statusControlEntry = null;
    
    private StaleData staleData = null;

    private PointWizardModel wizData = null;


    //init our static data with real values
    static {
        logicalGroups = new SelectItem[PointLogicalGroups.LGRP_STRS.length];
        for( int i = 0; i < PointLogicalGroups.LGRP_STRS.length; i++ ) {
            logicalGroups[i] = new SelectItem( PointLogicalGroups.LGRP_STRS[i], PointLogicalGroups.LGRP_STRS[i] );         
        }
    }

    public PointForm()
    {
        super();
      
    }
    


    public SelectItem[] getInitialStates() {
        
        if( initialStates == null ) {
            initialStates = new SelectItem[] { new SelectItem(new Integer(0), CtiUtilities.STRING_NONE) };
        }

        return initialStates;
    }
    
    public SelectItem[] getDecimalDigits() {
        if( decimalDigits == null ) {
            decimalDigits = new SelectItem[] { 
                    new SelectItem(new Integer(0), "0"),
                    new SelectItem(new Integer(1), "1"),
                    new SelectItem(new Integer(2), "2"),
                    new SelectItem(new Integer(3), "3"),
                    new SelectItem(new Integer(4), "4"),
                    new SelectItem(new Integer(5), "5"),
                    new SelectItem(new Integer(6), "6"),
                    new SelectItem(new Integer(7), "7"),
                    new SelectItem(new Integer(8), "8")};
        }

        return decimalDigits;
    }

    public SelectItem[] getLogicalGroups() {        
        return logicalGroups;
    }

    public SelectItem[] getTimeInterval() {
        return CBCSelectionLists.TIME_INTERVAL;
    }

    /**
     * Have the lowest value for archiving set to 60 seconds
     */
    public SelectItem[] getArchiveInterval() {
        return CBCSelectionLists.getTimeSubList(60);
    }

    public List<AlarmTableEntry> getAlarmTableEntries() {      
        return alarmTableEntries;
    }

    /**
     * Returns all the UofM as strings in the system
     *
     */
    public SelectItem[] getUofMs() {
        if (uofms == null) {
            List<LiteUnitMeasure> allUnitMeasures = DaoFactory.getUnitMeasureDao().getLiteUnitMeasures();
            uofms = new SelectItem[allUnitMeasures.size()];
            for (int i = 0; i < allUnitMeasures.size(); i++) {
                LiteUnitMeasure lu = allUnitMeasures.get(i);
                uofms[i] = new SelectItem(new Integer(lu.getUomID()),lu.getUnitMeasureName());
            }
        }
        return uofms;
    }

    /**
     * Returns all the AlarmCategoris as strings in the system
     *
     */
    public SelectItem[] getAlarmCategories() {      

        if( alarmCategories == null ) {

            List<LiteAlarmCategory> allAlarmStates = DaoFactory.getAlarmCatDao().getAlarmCategories();                
            alarmCategories = new SelectItem[ allAlarmStates.size() ];
            for( int i=0; i < allAlarmStates.size(); i++ ) {

                LiteAlarmCategory lac = allAlarmStates.get(i);
                alarmCategories[i] = new SelectItem(  //value, label
                        lac.getCategoryName(),
                        lac.getCategoryName() );
            }
        }
        
        return alarmCategories;
    }
    public SelectItem[] getNotifcationGrps() {
        
        if( notifGroups == null ) {         
            
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();
            
            synchronized( cache ) {
                
                List<LiteNotificationGroup> liteNotifGrps = cache.getAllContactNotificationGroupsWithNone();

                notifGroups = new SelectItem[ liteNotifGrps.size() ];
                for( int i = 0; i < liteNotifGrps.size(); i++ ) {
                    LiteNotificationGroup liteGroup = liteNotifGrps.get(i);
                    notifGroups[i] = 
                        new SelectItem(
                            new Integer(liteGroup.getNotificationGroupID()),
                            liteGroup.toString());
                }
            }
        }

        return notifGroups;
    }

    /**
     * Return all the contacts the have at least 1 email.
     * @return
     */ 
    public SelectItem[] getStateGroups() {
        
        if( stateGroups == null ) {         
            
            LiteStateGroup[] allStateGroups = DaoFactory.getStateDao().getAllStateGroups();
            
            stateGroups = new SelectItem[ allStateGroups.length ];
            for( int i = 0; i < allStateGroups.length; i++) {
                LiteStateGroup grp = allStateGroups[i];

                stateGroups[i] = new SelectItem(
                        new Integer(grp.getStateGroupID()),
                        grp.getStateGroupName() );
            }
        }
        
        return stateGroups;
    }

    /**
     * initializes model data. sets parent id of the point
     * @param parentId
     */
    public void initWizard(Integer parentId) {
        
        if (wizData == null) {
            wizData = new PointWizardModel();
        }
        wizData.setParentId(parentId);
        
    }
    
    /**
     * Restores the object from the database
     *
     */
    public void initItem( int id ) {

        PointBase pointDB = (PointBase)LiteFactory.createDBPersistent( DaoFactory.getPointDao().getLitePoint(id) );
        setDbPersistent( pointDB );
        resetForm();
        initItem();
        getStaleData();
    }

    protected void initItem() {

        if( retrieveDBPersistent() == null )
            return;
        
        initPanels();
    }

    /**
     * Reset any data structures and allow the parent to do its thing
     * 
     */
    @Override
    public void resetForm() {
        stateGroups = null;
        initialStates = null;
        notifGroups = null;
        alarmCategories = null;
        isArchiveInterEnabled = false;
        isCalcRateEnabled = false;
        alarmTableEntries = null;
        pointLimitEntry = null;
        pointFDREntry = null;
        analogControlEntry = null;
        statusControlEntry = null;
        staleData = null;
    }
    
    
    public String create() {
        if(!isEditingAuthorized()) {
            throw new NotAuthorizedException("The user is not authorized to perform this action.");
        }
        String edType = "pointEditor";
        FacesMessage fcsMessage = new FacesMessage();
        int pointType = getWizData().getPointType().intValue();
        String name = getWizData().getName();
        Integer paoId = getWizData().getParentId();
        boolean disabled = getWizData().getDisabled();
        
        try {
            fcsMessage.setDetail("Database add was SUCCESSFUL");
            PointBase point = PointUtil.createPoint(pointType, name, paoId, disabled);    
            initItem( point.getPoint().getPointID().intValue() );
            CapControlForm capControlForm = (CapControlForm)JSFParamUtil.getJSFVar( "capControlForm" );
            capControlForm.getPointTreeForm().resetPointList();
            getWizData().reset();
        }catch (TransactionException e){
            fcsMessage.setDetail("ERROR creating point -- PointForm.create" + e.getMessage());
            edType =  "";
        }
        finally{
            //set the Context
            FacesContext.getCurrentInstance().addMessage("cti_db_add", fcsMessage);
        }
        return edType;
    }



    /**
     * All possible panels for this editor go here. Set visible panels
     * based on type of object.
     *
     */
    private void initPanels() {

        //all panels that are always displayed
        getVisibleTabs().put( "General", new Boolean(true) );
        getVisibleTabs().put( "Alarming", new Boolean(true) );
        getVisibleTabs().put( "FDR", new Boolean(true) );
        
        //all type specifc panels
        getVisibleTabs().put( "PointAnalog", new Boolean(false) );
        getVisibleTabs().put( "PointStatus", new Boolean(false) );
        getVisibleTabs().put( "PointCalcStatus", new Boolean(false) );
        getVisibleTabs().put( "PointAccum", new Boolean(false) );
        getVisibleTabs().put( "PointCalc", new Boolean(false) );


        int ptType = PointTypes.getType( getPointBase().getPoint().getPointType() );
        switch( ptType ) {

            case PointTypes.ANALOG_POINT:
                getVisibleTabs().put( "PointAnalog", new Boolean(true) );
                break;
            
            case PointTypes.STATUS_POINT:
                getVisibleTabs().put( "PointStatus", new Boolean(true) );
                break;
            
            case PointTypes.CALCULATED_STATUS_POINT:
                getVisibleTabs().put( "PointCalcStatus", new Boolean(true) );
                updateTypeChanged(
                    new ValueChangeEvent(
                        DUMMY_UI, null,
                        ((CalcStatusPoint)getPointBase()).getCalcBase().getUpdateType() ) );
    
                break;
            
            case PointTypes.DEMAND_ACCUMULATOR_POINT:           
                getVisibleTabs().put( "PointAccum", new Boolean(true) );
                break;
                
            case PointTypes.PULSE_ACCUMULATOR_POINT:           
                getVisibleTabs().put( "PointAccum", new Boolean(true) );
                break;
             
            case PointTypes.CALCULATED_POINT:
                getVisibleTabs().put( "PointCalc", new Boolean(true) );
                updateTypeChanged(
                    new ValueChangeEvent(
                        DUMMY_UI, null,
                        ((CalculatedPoint)getPointBase()).getCalcBase().getUpdateType() ) );
                break;
            
            default:
                throw new IllegalArgumentException("Unknown point type given, point type = " + ptType );
        }           


        stateGroupChanged(
            new ValueChangeEvent(
                DUMMY_UI, null,
                new Integer(getPointBase().getPoint().getStateGroupID().intValue())) );

        archiveTypeChanged(
            new ValueChangeEvent( //src, oldValue, newValue
                DUMMY_UI, null,
                getPointBase().getPoint().getArchiveType()) );


        initAlarmTable();
    }
    
    /**
     * The instance of the underlying base object
     *
     */
    public PointBase getPointBase() {
        return (PointBase)getDbPersistent();
    }

    /**
     * Fired when the StateGroup component is changed
     * @param ev
     */
    public void stateGroupChanged( ValueChangeEvent ev ) {
        
        if(ev == null || ev.getNewValue() == null) return;

        LiteState[] lStates = DaoFactory.getStateDao().getLiteStates( ((Integer)ev.getNewValue()).intValue() );
        initialStates = new SelectItem[ lStates.length ];
        for( int i = 0; i < lStates.length; i++ )
            initialStates[i] =
                new SelectItem( new Integer(lStates[i].getLiteID()),
                                lStates[i].getStateText() );        
    
        //out state group may have changes
        //initAlarmTable( ptType );
    }

    /**
     * Fired when the ArchiveType component is changed
     * @param ev
     */
    public void archiveTypeChanged( ValueChangeEvent ev ) {
        
        if( ev == null || ev.getNewValue() == null ) return;

        String newVal = ev.getNewValue().toString();
        isArchiveInterEnabled =  ( newVal.equalsIgnoreCase(PointArchiveType.ON_TIMER.getPointArchiveTypeName()) 
                                    ||
                                    newVal.equalsIgnoreCase(PointArchiveType.ON_TIMER_OR_UPDATE.getPointArchiveTypeName()) 
                                  );
    }   

    /**
     * Fired when the UpdateType component is changed
     * @param ev
     */
    public void updateTypeChanged( ValueChangeEvent ev ) {
        
        if( ev == null || ev.getNewValue() == null ) return;
        
        String newVal = ev.getNewValue().toString();
        isCalcRateEnabled = newVal.equalsIgnoreCase("On Timer") || newVal.equalsIgnoreCase("On Timer+Change");
    }


    /**
     * Initializes our alarm table
     * 
     */
    private void initAlarmTable() {

        int ptType = PointTypes.getType( getPointBase().getPoint().getPointType() );
        
        ArrayList<AlarmTableEntry> notifEntries = new ArrayList<AlarmTableEntry>(32);
        List<LiteAlarmCategory> allAlarmStates = DefaultDatabaseCache.getInstance().getAllAlarmCategories();
        //be sure we have a 32 character string
        String alarmStates =
           ( getPointBase().getPointAlarming().getAlarmStates().length() != PointAlarming.ALARM_STATE_COUNT
             ? PointAlarming.DEFAULT_ALARM_STATES
             : getPointBase().getPointAlarming().getAlarmStates() );

        String excludeNotifyStates = getPointBase().getPointAlarming().getExcludeNotifyStates();

        //this drives what list of strings we will put into our table
        String[] alarm_cats = IAlarmDefs.OTHER_ALARM_STATES;
        if( ptType == PointTypes.STATUS_POINT || ptType == PointTypes.CALCULATED_STATUS_POINT ) {
            alarm_cats = IAlarmDefs.STATUS_ALARM_STATES;
        }
        LiteStateGroup stateGroup = DaoFactory.getStateDao().getLiteStateGroup( getPointBase().getPoint().getStateGroupID().intValue() );

        String[] stateNames = new String[stateGroup.getStatesList().size()];
        for( int j = 0; j < stateGroup.getStatesList().size(); j++ ) {
            stateNames[j] = stateGroup.getStatesList().get(j).toString();
        }
        // insert all the predefined states into the table
        int i = 0;
        for( i = 0; i < alarm_cats.length; i++ ) {
            AlarmTableEntry entry = new AlarmTableEntry();
            setAlarmGenNotif( entry, alarmStates.charAt(i), allAlarmStates, excludeNotifyStates.toUpperCase().charAt(i) );
                
            entry.setCondition( alarm_cats[i] );
            notifEntries.add( entry );
        }
            
        if( ptType == PointTypes.STATUS_POINT || ptType == PointTypes.CALCULATED_STATUS_POINT ) {

            for( int j = 0; j < stateNames.length; j++, i++ ) {
                if( i >= alarmStates.length() ) {
                    throw new ArrayIndexOutOfBoundsException("Trying to get alarmStates["+i+"] while alarmStates.length()==" + alarmStates.length() +
                    ", to many states for Status point " + getPointBase().getPoint().getPointName() + " defined.");
                }
                AlarmTableEntry entry = new AlarmTableEntry();
                setAlarmGenNotif( entry, alarmStates.charAt(i), allAlarmStates, excludeNotifyStates.toUpperCase().charAt(i) );
                    
                entry.setCondition( stateNames[j] );
                notifEntries.add( entry );
            }       
        }
                    
        alarmTableEntries = notifEntries;
    }
    
    private void setAlarmGenNotif( AlarmTableEntry entry, int alarmStateId, List<LiteAlarmCategory> allAlarmStates, char gen ) {

        if( (alarmStateId-1) < allAlarmStates.size() ) {
            entry.setGenerate( allAlarmStates.get( (alarmStateId-1) ).getCategoryName() );
        }else {
            entry.setGenerate( allAlarmStates.get(0).getCategoryName() );
        }
        entry.setExcludeNotify( PointAlarming.getExcludeNotifyString(gen) );
    }

    /**
     * @return
     */
    public boolean isArchiveInterEnabled()
    {
        return isArchiveInterEnabled;
    }

    /**
     * @return
     */
    public boolean isCalcRateEnabled()
    {
        return isCalcRateEnabled;
    }
    
    
    /**
     * Executes any last minute object updates before writting
     * the data to the databse. The return value is where the requested
     * value is redirected as defined in our faces-config.xml
     * 
     */
    public void update() {
        if(!isEditingAuthorized()) {
            throw new NotAuthorizedException("The user is not authorized to perform this action.");
        }
        
        String alarmStates = "";
        String exclNotify = "";

        int i = 0;
        for( i = 0; i < getAlarmTableEntries().size(); i++ ) {
            
            AlarmTableEntry entry = getAlarmTableEntries().get(i);
            
            alarmStates += (char)DaoFactory.getAlarmCatDao().getAlarmCategoryId( entry.getGenerate() );           
            exclNotify += PointAlarming.getExcludeNotifyChar( entry.getExcludeNotify() );
        }

        // fill in the rest of the alarmStates and excludeNotifyState so we have 32 chars
        alarmStates += PointAlarming.DEFAULT_ALARM_STATES.substring(i);
        exclNotify += PointAlarming.DEFAULT_EXCLUDE_NOTIFY.substring(i);

        getPointBase().getPointAlarming().setAlarmStates( alarmStates );
        getPointBase().getPointAlarming().setExcludeNotifyStates( exclNotify );
        if( getPointBase() instanceof ScalarPoint && pointLimitEntry != null) {
            ((ScalarPoint) getPointBase()).setPointLimitsMap(pointLimitEntry.getScalarPoint().getPointLimitsMap());
        }
        //special case for archiving the status point 
        handleArchiveStatus();
        
        //this message will be filled in by the super class
        FacesMessage facesMsg = new FacesMessage();

        try {
            checkForErrors();
            updateDBObject( getDbPersistent(), facesMsg );
            updateStaleData();
            facesMsg.setDetail( "Database update was SUCCESSFUL" );
        }
        catch( TransactionException te ) {
            String errorString = te.getMessage();
            facesMsg.setDetail(errorString);
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
        } catch (InvalidPointLimits iple) {
            String errorString = iple.getMessage();
            facesMsg.setDetail(errorString);
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
        }
        finally {
            FacesContext.getCurrentInstance().addMessage("cti_db_update", facesMsg);        
        }

    }

    private void updateStaleData() {
        getStaleData().update();
    }

    /**
     * method that will set archiving for the status point to 'none' or 'on_change'
     * depending on the value of the boolean
     */
    private void handleArchiveStatus() {
        String pointType = PointTypes.getType( PointTypes.STATUS_POINT );
        Point point = getPointBase().getPoint();
        if (point.getPointType().equalsIgnoreCase( pointType)) {
            if (point.isArchiveStatusData()) 
        		point.setArchiveType(PointArchiveType.ON_CHANGE.getPointArchiveTypeName());
            else
                point.setArchiveType(PointArchiveType.NONE.getPointArchiveTypeName());
                
        }
    }

    /**
     * @return
     */
    public PointLimitEntry getPointLimitEntry() {
        
        if( pointLimitEntry == null ) {
        
            if( !(getPointBase() instanceof ScalarPoint) )
                CTILogger.warn("Attempting to create a PointLimit editor for a non Scalar point");
            else
                pointLimitEntry = new PointLimitEntry( (ScalarPoint)getPointBase() );
        }

        return pointLimitEntry;
    }
    
    /**
     * @return
     */
    public StaleData getStaleData() {
        
        if( staleData == null ) {
            staleData = new StaleData( getPointBase() );
        }

        return staleData;
    }
    
    public PointAnalogControlEntry getPointAnalogControlEntry() {
        if (analogControlEntry == null) {
            if (getPointBase() instanceof AnalogPoint) {
                AnalogPoint analogPoint = (AnalogPoint)getPointBase();
                analogControlEntry = new PointAnalogControlEntry(analogPoint.getPointAnalogControl());
            } else {
                log.warn("Attempting to create an AnalogPointControl editor for a non-controllable point or for an invalid point type");
            }
        }
        
        return analogControlEntry;
    }
    
    public PointStatusControlEntry getPointStatusControlEntry() {
        if (statusControlEntry == null) {
            if (getPointBase() instanceof StatusPoint) {
                StatusPoint statusPoint = (StatusPoint)getPointBase();
                statusControlEntry = new PointStatusControlEntry(statusPoint.getPointStatusControl());
            } else {
                log.warn("Attempting to create a StatusPointControl editor for a non-controllable point or for an invalid point type");
            }
        }
        
        return statusControlEntry;
    }

    /**
     * @return
     */
    public PointFDREntry getPointFDREntry() {
        if(pointFDREntry == null) {
			pointFDREntry = new PointFDREntry( getPointBase() );
	    }      	

        return pointFDREntry;
    }
    
    public void paoClick(ActionEvent ae){
        FacesMessage fm = new FacesMessage();
        try {
            //go to the next page
            String path = "/editor/cbcBase.jsf";
            String itemId = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
            .get("paoID");
            String type = "" + DBEditorTypes.EDITOR_CAPCONTROL;
            String query = "?type=" + type + "&" + "itemid=" + itemId;
            String location = path + query;                                       
            //bookmark the current page
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            CBCNavigationUtil.bookmarkLocationAndRedirect(location,session);
            FacesContext.getCurrentInstance().getExternalContext().redirect(location);            
            FacesContext.getCurrentInstance().responseComplete();            
        } 

        catch (IOException e) {
            fm.setDetail("ERROR - Couldn't redirect. PointForm:paoClick. " + e.getMessage());
        } catch (Exception e) {
            //code to handle null session
        }
        finally{
            if(fm.getDetail() != null) {
                FacesContext.getCurrentInstance().addMessage("pao_click", fm);
            }
        }
    }
    
    public PointWizardModel getWizData() {
        if (wizData == null)
            return new PointWizardModel();
        else
            return wizData;
    }


    public void setWizData(PointWizardModel wizData) {
        this.wizData = wizData;
    }

    @Override
    protected void checkForErrors() throws InvalidPointOffsetException, InvalidPointLimits {
        int offset = getPointBase().getPoint().getPointOffset().intValue();
        int type = PointTypes.getType (getPointBase().getPoint().getPointType());
        Integer paoId = getWizData().getParentId();
        //make sure we are not erroring out because of the same offset
        LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(getPointBase().getPoint().getPointID().intValue());
        
        if (!checkPointLimits())   
            throw new InvalidPointLimits ("High point limit can't be lower than Low point Limit");
        if (litePoint.getPointOffset() == offset)
            return;
        if (!PointOffsetUtils.isValidPointOffset(offset, paoId, type)) {
            throw new InvalidPointOffsetException("The point offset " + offset + " is already in use by another " + getPointBase().getPoint().getPointType() +  " point on this device.");
        }
     }

    /**
     * Returns true if the user has the CapControl Settings > Database Editing role property.
     * @return
     */
    public boolean isEditingAuthorized() {
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        return rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, JSFParamUtil.getYukonUser());
    }

    private boolean checkPointLimits() {
        if (getPointBase() instanceof AnalogPoint) {
            AnalogPoint point = (AnalogPoint) getPointBase();
            PointLimit limitOne = point.getLimitOne();
            PointLimit limitTwo = point.getLimitTwo();
            
            if (limitOne != null && limitTwo != null) {
                if ( (limitOne.getHighLimit().doubleValue() < limitOne.getLowLimit().doubleValue()) ||
                        (limitTwo.getHighLimit().doubleValue() < limitTwo.getLowLimit().doubleValue()) )
                    return false;
            }
        }               
        return true;
    }

}