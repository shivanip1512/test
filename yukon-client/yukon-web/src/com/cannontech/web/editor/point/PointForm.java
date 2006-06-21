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

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointOffsetUtils;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.data.point.ScalarPoint;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.servlet.nav.CBCNavigationUtil;
import com.cannontech.servlet.nav.DBEditorTypes;
import com.cannontech.web.editor.DBEditorForm;
import com.cannontech.web.exceptions.InvalidPointOffsetException;
import com.cannontech.web.util.CBCSelectionLists;
import com.cannontech.web.wizard.PointWizardModel;
import com.cannontech.yukon.IDatabaseCache;

/**
 * @author ryan
 *
 */
public class PointForm extends DBEditorForm
{
	private SelectItem[] emailNotifcations = null;
	private SelectItem[] stateGroups = null;
	private SelectItem[] initialStates = null;
	private SelectItem[] notifGroups = null;
	private SelectItem[] alarmCategories = null;
	
	private boolean isArchiveInterEnabled = false;
	private boolean isCalcRateEnabled = false;
	private List alarmTableEntries = null;
	
	private static SelectItem[] logicalGroups = null;
	private static SelectItem[] uofms = null;


	//sub editors used to divide up functionality into different classes

	//limit point data for analog, calc & demand types
	private PointLimitEntry pointLimitEntry = null;
	
	//fdr point data
	private PointFDREntry pointFDREntry = null;
	
	//status point specific editor
	private PointStatusEntry pointStatusEntry = null;

	private PointWizardModel wizData = null;


	//init our static data with real values
	static {
		logicalGroups = new SelectItem[PointLogicalGroups.LGRP_STRS.length];
		for( int i = 0; i < PointLogicalGroups.LGRP_STRS.length; i++ )
			logicalGroups[i] =  //value, label
				new SelectItem( PointLogicalGroups.LGRP_STRS[i], PointLogicalGroups.LGRP_STRS[i] );			


		//getArchiveIntervalComboBox().setSelectedItem("5 minute");
	}

	public PointForm()
	{
		super();
      
	}
	


	public SelectItem[] getInitialStates() {
		
		if( initialStates == null ) {
			initialStates = new SelectItem[]
				{ new SelectItem(new Integer(0), CtiUtilities.STRING_NONE) };
		}

		return initialStates;
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

	public List getAlarmTableEntries() {		
		return alarmTableEntries;
	}

	/**
	 * Returns all the UofM as strings in the system
	 *
	 */
	public SelectItem[] getUofMs() {		

		if( uofms == null ) {

			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized(cache) {
				List allUnitMeasures = cache.getAllUnitMeasures();
				
				uofms = new SelectItem[ allUnitMeasures.size() ];
				for( int i=0; i<allUnitMeasures.size(); i++ ) {
					
					LiteUnitMeasure lu = (LiteUnitMeasure)allUnitMeasures.get(i);
					uofms[i] = new SelectItem(  //value, label
							new Integer(lu.getUomID()),
							lu.getUnitMeasureName() );
				}
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

			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized(cache) {
				List allAlarmStates = cache.getAllAlarmCategories();				
				alarmCategories = new SelectItem[ allAlarmStates.size() ];
				for( int i=0; i < allAlarmStates.size(); i++ ) {

					LiteAlarmCategory lac = (LiteAlarmCategory)allAlarmStates.get(i);
					alarmCategories[i] = new SelectItem(  //value, label
							lac.getCategoryName(),
							lac.getCategoryName() );
				}
			}
		}
		
		return alarmCategories;
	}
	
	/**
	 * Return all the contacts the have at least 1 email.
	 * @return
	 */	
	public SelectItem[] getEmailNotifcations() {
		
		if( emailNotifcations == null ) {			
			
			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			
			synchronized( cache ) {
				
				List contacts = cache.getAllContacts();

				ArrayList emailList = new ArrayList();
				emailList.add( 
					new SelectItem(
						new Integer(LiteContact.NONE_LITE_CONTACT.getContactID()),
						LiteContact.NONE_LITE_CONTACT.toString()) );
				
				for( int i = 0; i < contacts.size(); i++ ) {

					LiteContact contact = (LiteContact)contacts.get(i);
					int cntNotifID = findEmailContact(contact);

					if( cntNotifID != CtiUtilities.NONE_ZERO_ID )
						emailList.add( 
							new SelectItem(
								new Integer(cntNotifID),
								contact.toString()) );
				}

				emailNotifcations = new SelectItem[ emailList.size() ];
				emailNotifcations =
					(SelectItem[])emailList.toArray( emailNotifcations );
			}			
		}

		return emailNotifcations;
	}
	
	/**
	 * Return all the contacts the have at least 1 email.
	 * @return
	 */	
	public SelectItem[] getNotifcationGrps() {
		
		if( notifGroups == null ) {			
			
			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			
			synchronized( cache ) {
				
				List liteNotifGrps = cache.getAllContactNotificationGroups();
				notifGroups = new SelectItem[ liteNotifGrps.size() ];
				
				for( int i = 0; i < liteNotifGrps.size(); i++ ) {
					LiteNotificationGroup liteGroup = (LiteNotificationGroup)liteNotifGrps.get(i);
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
				LiteStateGroup grp = (LiteStateGroup)allStateGroups[i];

				stateGroups[i] = new SelectItem(
						new Integer(grp.getStateGroupID()),
						grp.getStateGroupName() );
			}
		}
		
		return stateGroups;
	}


	private int findEmailContact( LiteContact contact ) {

		if( contact != null )
		{
			//find the first email address in the list ContactNotifications...then use it
			for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
			{	
				LiteContactNotification ltCntNotif = 
						(LiteContactNotification)contact.getLiteContactNotifications().get(j);
						
				if( ltCntNotif.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL )
				{
					return ltCntNotif.getContactNotifID();
				}
			}
		}

		//no e-mail notif found
		return CtiUtilities.NONE_ZERO_ID;
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

		PointBase pointDB =
			(PointBase)LiteFactory.createDBPersistent( DaoFactory.getPointDao().getLitePoint(id) );
		setDbPersistent( pointDB );
		
//		try {
//			PointBase pointDB = PointFactory.retrievePoint(
//					new Integer(DaoFactory.getPointDao().getLitePoint(id).getPointID()) );
//
//			setDbPersistent( pointDB );
//		}
//		catch( SQLException sql ) {
//			CTILogger.error("Unable to retrieve YukonPAObject", sql );
//		}

		initItem();
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
	public void resetForm() {
		
		emailNotifcations = null;
		stateGroups = null;
		initialStates = null;
		notifGroups = null;
		alarmCategories = null;
	
		isArchiveInterEnabled = false;
		isCalcRateEnabled = false;
		alarmTableEntries = null;

		pointLimitEntry = null;
		pointFDREntry = null;
		pointStatusEntry = null;

		initItem();
	}
    
    
    public String create() {
        String edType = "pointEditor";
        FacesMessage fcsMessage = new FacesMessage();
        int pointType = getWizData().getPointType().intValue();
        String name = getWizData().getName();
        Integer paoId = getWizData().getParentId();
        
        try {
            fcsMessage.setDetail("Database add was SUCCESSFUL");
            PointBase point = PointUtil.createPoint(pointType, name, paoId);    
            initItem( point.getPoint().getPointID().intValue() );
             
        }catch (TransactionException e){
            fcsMessage.setDetail("ERROR creating point -- PointForm.create" + e.getMessage());
            edType =  "";
        }
        finally{
            //set the Context
            FacesContext.getCurrentInstance()
            .addMessage("cti_db_add", fcsMessage);
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
		isArchiveInterEnabled =  newVal.equalsIgnoreCase(PointTypes.ARCHIVE_ON_TIMER);
	}	

	/**
	 * Fired when the UpdateType component is changed
	 * @param ev
	 */
	public void updateTypeChanged( ValueChangeEvent ev ) {
		
		if( ev == null || ev.getNewValue() == null ) return;
		
		String newVal = ev.getNewValue().toString();
		isCalcRateEnabled =
			newVal.equalsIgnoreCase("On Timer") || newVal.equalsIgnoreCase("On Timer+Change");
	}


	/**
	 * Initializes our alarm table
	 * 
	 */
	private void initAlarmTable() {

		int ptType = PointTypes.getType( getPointBase().getPoint().getPointType() );
		
		ArrayList notifEntries = new ArrayList(32);
		java.util.List allAlarmStates = DefaultDatabaseCache.getInstance().getAllAlarmCategories();
		//be sure we have a 32 character string
		String alarmStates =
		   ( getPointBase().getPointAlarming().getAlarmStates().length() != PointAlarming.ALARM_STATE_COUNT
			 ? PointAlarming.DEFAULT_ALARM_STATES
			 : getPointBase().getPointAlarming().getAlarmStates() );

		String excludeNotifyStates =
			getPointBase().getPointAlarming().getExcludeNotifyStates();

		//this drives what list of strings we will put into our table
		String[] alarm_cats = IAlarmDefs.OTHER_ALARM_STATES;
		if( ptType == PointTypes.STATUS_POINT || ptType == PointTypes.CALCULATED_STATUS_POINT )
			alarm_cats = IAlarmDefs.STATUS_ALARM_STATES;
				
		LiteStateGroup stateGroup = (LiteStateGroup)
			DaoFactory.getStateDao().getLiteStateGroup( getPointBase().getPoint().getStateGroupID().intValue() );

		String[] stateNames = new String[stateGroup.getStatesList().size()];
		for( int j = 0; j < stateGroup.getStatesList().size(); j++ )
			stateNames[j] = stateGroup.getStatesList().get(j).toString();
		
		// insert all the predefined states into the table
		int i = 0;
		for( i = 0; i < alarm_cats.length; i++ )
		{
			AlarmTableEntry entry = new AlarmTableEntry();
			setAlarmGenNotif( entry, 
					(int)alarmStates.charAt(i), allAlarmStates,
					excludeNotifyStates.toUpperCase().charAt(i) );
				
			entry.setCondition( alarm_cats[i] );
			notifEntries.add( entry );
		}
			
		if( ptType == PointTypes.STATUS_POINT
			|| ptType == PointTypes.CALCULATED_STATUS_POINT ) {

			for( int j = 0; j < stateNames.length; j++, i++ ) {
				if( i >= alarmStates.length() )
					throw new ArrayIndexOutOfBoundsException("Trying to get alarmStates["+i+"] while alarmStates.length()==" + alarmStates.length() +
					", to many states for Status point " + getPointBase().getPoint().getPointName() + " defined.");
							
				AlarmTableEntry entry = new AlarmTableEntry();
				setAlarmGenNotif( entry,
						(int)alarmStates.charAt(i), allAlarmStates,
						excludeNotifyStates.toUpperCase().charAt(i) );
					
				entry.setCondition( stateNames[j] );
				notifEntries.add( entry );
			}		
		}
					
		alarmTableEntries = notifEntries;
	}
	
	private void setAlarmGenNotif( AlarmTableEntry entry, int alarmStateId, List allAlarmStates, char gen ) {

		if( (alarmStateId-1) < allAlarmStates.size() )
			entry.setGenerate( ((LiteAlarmCategory)allAlarmStates.get( (int)(alarmStateId-1) )).getCategoryName() );
		else
			entry.setGenerate( ((LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName() );
		
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
		
		String alarmStates = "";
		String exclNotify = "";

		int i = 0;
		for( i = 0; i < getAlarmTableEntries().size(); i++ ) {
			
			AlarmTableEntry entry =
				(AlarmTableEntry)getAlarmTableEntries().get(i);
			
			alarmStates += (char)DaoFactory.getAlarmCatDao().getAlarmCategoryId( entry.getGenerate() );			
			exclNotify += PointAlarming.getExcludeNotifyChar( entry.getExcludeNotify() );
		}

		// fill in the rest of the alarmStates and excludeNotifyState so we have 32 chars
		alarmStates += PointAlarming.DEFAULT_ALARM_STATES.substring(i);
		exclNotify += PointAlarming.DEFAULT_EXCLUDE_NOTIFY.substring(i);

		getPointBase().getPointAlarming().setAlarmStates( alarmStates );
		getPointBase().getPointAlarming().setExcludeNotifyStates( exclNotify );
		
		
		
		
		//this message will be filled in by the super class
		FacesMessage facesMsg = new FacesMessage();

		try {
			checkForErrors();
            updateDBObject( getDbPersistent(), facesMsg );
			facesMsg.setDetail( "Database update was SUCCESSFULL" );
		}
		catch( TransactionException te ) {
            String errorString = te.getMessage();
            facesMsg.setDetail(errorString);
            facesMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
		}
		finally {
			FacesContext.getCurrentInstance().addMessage("cti_db_update", facesMsg);		
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
	public PointStatusEntry getPointStatusEntry() {
		
		if( pointStatusEntry == null ) {
		
			if( !(getPointBase() instanceof StatusPoint) )
				CTILogger.warn("Attempting to create an StatuPoint editor for a non status point");
			else
				pointStatusEntry = new PointStatusEntry( (StatusPoint)getPointBase() );
		}

		return pointStatusEntry;
	}

	/**
	 * @return
	 */
	public PointFDREntry getPointFDREntry() {
		
		if( pointFDREntry == null )
			pointFDREntry = new PointFDREntry( getPointBase() );

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
            CBCNavigationUtil.bookmarkLocation(location,session);
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

    protected void checkForErrors() throws InvalidPointOffsetException {
        int offset = getPointBase().getPoint().getPointOffset().intValue();
        int type = PointTypes.getType (getPointBase().getPoint().getPointType());
        Integer paoId = getWizData().getParentId();
        //make sure we are not erroring out because of the same offset
        LitePoint litePoint = DaoFactory.getPointDao().getLitePoint(getPointBase().getPoint().getPointID().intValue());
        if (litePoint.getPointOffset() == offset)
        	return;
        if (!PointOffsetUtils.isValidPointOffset(offset,paoId ,type)) {
            throw new InvalidPointOffsetException("The point offset if invalid for " + getPointBase().getPoint().getPointType() +  " point type. Consider increasing offset");
        }
     }
}