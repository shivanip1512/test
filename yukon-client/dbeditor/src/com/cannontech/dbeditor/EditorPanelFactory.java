package com.cannontech.dbeditor;

/**
 * Creates an appropriate PropertyPanel given a DBPersistent
 *
 * Creation date: (1/7/2002 1:38:27 PM)
 * @author: Aaron Lauinger aka The MAN
 */

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.data.season.Season;
import com.cannontech.dbeditor.editor.season.SeasonEditorPanel;

public final class EditorPanelFactory
{

	//the below 2D array will map Editors to the classes they edit
	// the format is:
	//	   EDITOR_CLASS[X][0] is the class to be edited
	//	   EDITOR_CLASS[X][1] is the class that is the Panel to use
	public static final Class[][] EDITOR_CLASSES =
	{
		//{Object.class,Object.class}
   	{com.cannontech.database.db.notification.AlarmCategory.class,
		 com.cannontech.dbeditor.editor.alarmcategories.AlarmCategoriesEditorPanel.class},
   	
   	{com.cannontech.database.data.capcontrol.CapControlFeeder.class,
	   com.cannontech.dbeditor.editor.capfeeder.CCFeederEditor.class},

		{com.cannontech.database.data.capcontrol.CapControlSubBus.class,
	  	com.cannontech.dbeditor.editor.capsubbus.CCSubstationBusEditorPanel.class},
		
		{com.cannontech.database.data.customer.Customer.class,
		 com.cannontech.dbeditor.editor.contact.CustomerEditor.class},

   	{com.cannontech.database.data.customer.Contact.class,
		 com.cannontech.dbeditor.editor.contact.ContactEditor.class},

   	{com.cannontech.database.db.contact.Contact.class,
		 com.cannontech.dbeditor.editor.contact.ContactEditor.class},

   	{com.cannontech.database.data.user.YukonUser.class,
		 com.cannontech.dbeditor.editor.user.LoginEditorPanel.class},

		{com.cannontech.database.data.user.YukonGroup.class,
		 com.cannontech.dbeditor.editor.user.RoleGroupEditorPanel.class},

   	{com.cannontech.database.data.device.DeviceBase.class,
	  	com.cannontech.dbeditor.editor.device.DeviceEditorPanel.class},
   	
   	{com.cannontech.database.data.port.DirectPort.class,
	  	com.cannontech.dbeditor.editor.port.PortEditorPanel.class},

   	{com.cannontech.database.data.notification.GroupNotification.class,
		 com.cannontech.dbeditor.editor.notification.group.NotificationGroupEditorPanel.class},

//   	{com.cannontech.database.data.notification.ContactNotification.class,
//		 com.cannontech.dbeditor.editor.notification.recipient.NotificationRecipientEditorPanel.class},

   	{com.cannontech.database.data.state.GroupState.class,
	  	com.cannontech.dbeditor.editor.state.StateEditorPanel.class},

		{com.cannontech.database.data.device.lm.LMControlArea.class,
		 com.cannontech.dbeditor.editor.device.lmcontrolarea.LMControlAreaEditor.class},

   	{com.cannontech.database.data.device.lm.LMProgramBase.class,
	  	com.cannontech.dbeditor.editor.device.lmprogram.LMProgramEditor.class},

   	{com.cannontech.database.data.device.lm.LMGroup.class,
	  	com.cannontech.dbeditor.editor.device.lmgroup.LMGroupEditor.class},
   	
   	{com.cannontech.database.data.point.PointBase.class,
	  	com.cannontech.dbeditor.editor.point.PointEditorPanel.class},

   	{com.cannontech.database.data.route.RouteBase.class,
	  	com.cannontech.dbeditor.editor.route.RouteEditorPanel.class},

   	{com.cannontech.database.data.holiday.HolidaySchedule.class,
	   com.cannontech.dbeditor.editor.holidayschedule.HolidayScheduleEditor.class},
	   
	{com.cannontech.database.data.baseline.Baseline.class,
		com.cannontech.dbeditor.editor.baseline.BaselineEditorPanel.class},
		
	{com.cannontech.database.data.config.ConfigTwoWay.class,
		com.cannontech.dbeditor.editor.config.ConfigEditorPanel.class},
		
	{com.cannontech.database.db.tags.Tag.class,
		com.cannontech.dbeditor.editor.tags.TagEditorPanel.class},
		
	{com.cannontech.database.db.device.lm.LMProgramConstraint.class,
		com.cannontech.dbeditor.editor.device.lmconstraint.LMConstraintEditorPanel.class},
	
	{LMScenario.class,
		com.cannontech.dbeditor.editor.device.lmscenario.LMScenarioEditorPanel.class},
		
	{Season.class,
		SeasonEditorPanel.class}
	
   	//Remove the Schedule editor because of dependency issues
   	//{com.cannontech.message.macs.message.Schedule.class,
		//com.cannontech.macs.schedule.editor.ScheduleEditorPanel.class}

	};


/**
 * EditorPanelFactory constructor comment.
 */
private EditorPanelFactory() {
	 super();
}
/**
 * Creation date: (1/7/2002 1:40:08 PM)
 * @return com.cannontech.common.editor.PropertyPanel
 * @param o com.cannontech.database.db.DBPersistent
 */
/* The 2 for loops in this method at first look may seem to be a
   performance hinderance, but testing shows that the time of execution
   for most lookups is between 20 and 40 millis
  */
public static PropertyPanel createEditorPanel(EditorPanel o)
{
   Class panelClass = null;
	
   for( int i = 0; i < EDITOR_CLASSES.length; i++ )
   {
   	if( o.equals(EDITOR_CLASSES[i][0]) )
   	{
			panelClass = EDITOR_CLASSES[i][1];
			break;
   	}			
   }

   //if we still havent found our class,
   //  find the first super class and use that
   if( panelClass == null )
   {	   
	   for( int i = 0; i < EDITOR_CLASSES.length; i++ )
	   {
		   Class tClass = o.getClass();
		   
			for( ; tClass != Object.class; tClass = tClass.getSuperclass() )
			{
		   	if( tClass.equals(EDITOR_CLASSES[i][0]) )
		   	{
					panelClass = EDITOR_CLASSES[i][1];
					break;
		   	}
			}
		}
	}


   //create a new instance of our panel if one was found
   if (panelClass != null)
   {
	  try
	  {
		 return (PropertyPanel)panelClass.newInstance();
	  }
	  catch (InstantiationException ie)
	  {
		 com.cannontech.clientutils.CTILogger.error( ie.getMessage(), ie );
	  }
	  catch (IllegalAccessException iae)
	  {
		 com.cannontech.clientutils.CTILogger.error( iae.getMessage(), iae );
	  }
   }
   
	// Didn't find a match, chuck some cheese   
   throw new IllegalArgumentException("Unable to create an editor panel for the object of type: " + o.getClass().getName() );
}
/**
 * Creation date: (1/7/2002 1:40:08 PM)
 * @return com.cannontech.common.editor.PropertyPanel
 * @param o com.cannontech.database.db.DBPersistent
 */
public static PropertyPanel createEditorPanel(DBPersistent o)
{
	if( o instanceof EditorPanel )
		return createEditorPanel( (EditorPanel)o );
	else
	   throw new IllegalArgumentException("The object of type: " + o.getClass().getName() + " is not a " + EditorPanel.class.getName() );
}
}
