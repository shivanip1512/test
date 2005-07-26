package com.cannontech.web.editor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.StateFuncs;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.web.editor.point.AlarmTableEntry;

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


	private static SelectItem[] logicalGroups = null;
	private static SelectItem[] uofms = null;
	private static SelectItem[] archiveIntervals = null;
	
	private boolean isArchiveInterEnabled = false;
	private boolean isCalcRateEnabled = false;
	private List alarmTableEntries = null;
	
	
	//dummy UI comp to be used for internal event firing only
	private static final UIComponent DUMMY_UI = new UIData();


	//init our static data with real values
	static {
		logicalGroups = new SelectItem[PointLogicalGroups.LGRP_STRS.length];
		for( int i = 0; i < PointLogicalGroups.LGRP_STRS.length; i++ )
			logicalGroups[i] =
				new SelectItem( PointLogicalGroups.LGRP_STRS[i], PointLogicalGroups.LGRP_STRS[i] );			


		//value, label
		archiveIntervals = new SelectItem[16];				
		archiveIntervals[0] = new SelectItem(new Integer(5), "5 seconds");
		archiveIntervals[1] = new SelectItem(new Integer(10), "10 seconds");
		archiveIntervals[2] = new SelectItem(new Integer(15), "15 seconds");
		archiveIntervals[3] = new SelectItem(new Integer(30), "30 seconds");
		archiveIntervals[4] = new SelectItem(new Integer(60), "1 minute");
		archiveIntervals[5] = new SelectItem(new Integer(120), "2 minutes");
		archiveIntervals[6] = new SelectItem(new Integer(180), "3 minutes");
		archiveIntervals[7] = new SelectItem(new Integer(300), "5 minutes");
		archiveIntervals[8] = new SelectItem(new Integer(600), "10 minutes");
		archiveIntervals[9] = new SelectItem(new Integer(900), "15 minutes");
		archiveIntervals[10] = new SelectItem(new Integer(1800), "30 minutes");
		archiveIntervals[11] = new SelectItem(new Integer(3600), "1 hour");
		archiveIntervals[12] = new SelectItem(new Integer(7200), "2 hours");
		archiveIntervals[13] = new SelectItem(new Integer(21600), "6 hours");
		archiveIntervals[14] = new SelectItem(new Integer(43200), "12 hours");
		archiveIntervals[15] = new SelectItem(new Integer(86400), "1 day");
		
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

	public SelectItem[] getArchiveIntervals() {		
			return archiveIntervals;
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

			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
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

			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
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
			
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			
			synchronized( cache ) {
				
				List contacts = cache.getAllContacts();

				emailNotifcations = new SelectItem[ contacts.size() + 1 ];
				emailNotifcations[0] = 
					new SelectItem(
						new Integer(LiteContact.NONE_LITE_CONTACT.getContactID()),
						LiteContact.NONE_LITE_CONTACT.toString());
				
				for( int i = 0; i < contacts.size(); i++ ) {

					LiteContact contact = (LiteContact)contacts.get(i);
				
					if( findEmailContact(contact) != CtiUtilities.NONE_ZERO_ID )
						emailNotifcations[i+1] = 
							new SelectItem(
								new Integer(findEmailContact(contact)),
								contact.toString());
				}
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
			
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			
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
			
			LiteStateGroup[] allStateGroups = StateFuncs.getAllStateGroups();
			
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

	public void initPoint( int ptId, int ptType ) {

System.out.println("-------  Initing point");
		try {		
			PointBase pointDB = PointFactory.retrievePoint( new Integer(ptId) );
	
			setDbPersistent( pointDB );
			
			setVisiblePanels( ptType );

			initAllPanels( ptType );
		}
		catch( SQLException sx ) {
			CTILogger.error( sx.getMessage(), sx  );
		}
	}
	
	/**
	 * Inits all Point panels regardless of point type
	 *
	 */
	private void initAllPanels( int ptType ) {

		stateGroupChanged(
			new ValueChangeEvent(
				DUMMY_UI, null,
				new Integer(getPointBase().getPoint().getStateGroupID().intValue())) );

		archiveTypeChanged(
			new ValueChangeEvent( //src, oldValue, newValue
				DUMMY_UI, null,
				getPointBase().getPoint().getArchiveType()) );


		initAlarmTable( ptType );
	}

	/**
	 * All possible panels for this editor go here. Set visible panels
	 * based on type of object.
	 *
	 */
	private void setVisiblePanels( int ptType ) {

		//all panels that are always displayed
		getVisibleTabs().put( "General", new Boolean(true) );
		getVisibleTabs().put( "Alarming", new Boolean(true) );
		
		//all type specifc panels
		getVisibleTabs().put( "PointAnalog", new Boolean(false) );
		getVisibleTabs().put( "PointStatus", new Boolean(false) );
		getVisibleTabs().put( "PointCalcStatus", new Boolean(false) );
		getVisibleTabs().put( "PointAccum", new Boolean(false) );
		getVisibleTabs().put( "PointCalc", new Boolean(false) );


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
			
			case PointTypes.ACCUMULATOR_DEMAND:			
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

	}
	
	public PointBase getPointBase() {
		return (PointBase)getDbPersistent();
	}

	/**
	 * Fired when the StateGroup component is changed
	 * @param ev
	 */
	public void stateGroupChanged( ValueChangeEvent ev ) {
		
		LiteState[] lStates = StateFuncs.getLiteStates( ((Integer)ev.getNewValue()).intValue() );
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
		
		if( ev.getNewValue() == null ) return;
		
		String newVal = ev.getNewValue().toString();
		isArchiveInterEnabled =  newVal.equalsIgnoreCase("On Timer");
	}	

	/**
	 * Fired when the UpdateType component is changed
	 * @param ev
	 */
	public void updateTypeChanged( ValueChangeEvent ev ) {
		
		if( ev.getNewValue() == null ) return;
		
		String newVal = ev.getNewValue().toString();
		isCalcRateEnabled =
			newVal.equalsIgnoreCase("On Timer") || newVal.equalsIgnoreCase("On Timer+Change");
	}


	/**
	 * Initializes our alarm table
	 * 
	 */
	private void initAlarmTable( int ptType ) {

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
			StateFuncs.getLiteStateGroup( getPointBase().getPoint().getStateGroupID().intValue() );

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
					
		//alarmTableEntries = new AlarmTableEntry[notifEntries.size()];
		//alarmTableEntries = (AlarmTableEntry[])notifEntries.toArray( alarmTableEntries );
		alarmTableEntries = notifEntries;
	}
	
	private void setAlarmGenNotif( AlarmTableEntry entry, int alarmStateId, List allAlarmStates, char gen ) {

		if( (alarmStateId-1) < allAlarmStates.size() )
			entry.setGenerate( ((LiteAlarmCategory)allAlarmStates.get( (int)(alarmStateId-1) )).getCategoryName() );
		else
			entry.setGenerate( ((LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName() );
		
		entry.setExcludeNotify( String.valueOf(gen) );
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

}