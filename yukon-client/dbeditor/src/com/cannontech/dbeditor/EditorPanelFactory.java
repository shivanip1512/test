package com.cannontech.dbeditor;

/**
 * Creates an appropriate PropertyPanel given a DBPersistent
 *
 * Creation date: (1/7/2002 1:38:27 PM)
 * @author: Aaron Lauinger aka The MAN
 */

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.database.data.baseline.Baseline;
import com.cannontech.database.data.config.ConfigTwoWay;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.customer.Customer;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.lm.LMControlArea;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.holiday.HolidaySchedule;
import com.cannontech.database.data.notification.NotificationGroup;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.TcpPort;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.season.SeasonSchedule;
import com.cannontech.database.data.state.GroupState;
import com.cannontech.database.data.tou.TOUSchedule;
import com.cannontech.database.data.user.UserGroup;
import com.cannontech.database.data.user.YukonGroup;
import com.cannontech.database.data.user.YukonUser;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.lm.LMProgramConstraint;
import com.cannontech.database.db.notification.AlarmCategory;
import com.cannontech.database.db.tags.Tag;
import com.cannontech.dbeditor.editor.alarmcategories.AlarmCategoriesEditorPanel;
import com.cannontech.dbeditor.editor.baseline.BaselineEditorPanel;
import com.cannontech.dbeditor.editor.config.ConfigEditorPanel;
import com.cannontech.dbeditor.editor.contact.ContactEditor;
import com.cannontech.dbeditor.editor.contact.CustomerEditor;
import com.cannontech.dbeditor.editor.device.DeviceEditorPanel;
import com.cannontech.dbeditor.editor.device.lmconstraint.LMConstraintEditorPanel;
import com.cannontech.dbeditor.editor.device.lmcontrolarea.LMControlAreaEditor;
import com.cannontech.dbeditor.editor.device.lmgroup.LMGroupEditor;
import com.cannontech.dbeditor.editor.device.lmprogram.LMProgramEditor;
import com.cannontech.dbeditor.editor.device.lmscenario.LMScenarioEditorPanel;
import com.cannontech.dbeditor.editor.holidayschedule.HolidayScheduleEditor;
import com.cannontech.dbeditor.editor.notification.group.NotificationGroupEditorPanel;
import com.cannontech.dbeditor.editor.point.PointEditorPanel;
import com.cannontech.dbeditor.editor.port.PortEditorPanel;
import com.cannontech.dbeditor.editor.route.RouteEditorPanel;
import com.cannontech.dbeditor.editor.season.SeasonEditorPanel;
import com.cannontech.dbeditor.editor.state.StateEditorPanel;
import com.cannontech.dbeditor.editor.tags.TagEditorPanel;
import com.cannontech.dbeditor.editor.tou.TOUEditorPanel;
import com.cannontech.dbeditor.editor.user.LoginEditorPanel;
import com.cannontech.dbeditor.editor.user.RoleGroupEditorPanel;
import com.cannontech.dbeditor.editor.user.UserGroupEditorPanel;

public final class EditorPanelFactory
{

	//the below 2D array will map Editors to the classes they edit
	// the format is:
	//	   EDITOR_CLASS[X][0] is the class to be edited
	//	   EDITOR_CLASS[X][1] is the class that is the Panel to use
	public static final Class[][] EDITOR_CLASSES =
	{
		//{Object.class,Object.class}
   	{AlarmCategory.class, AlarmCategoriesEditorPanel.class},
	{Customer.class, CustomerEditor.class},
   	{Contact.class, ContactEditor.class},
   	{com.cannontech.database.db.contact.Contact.class, ContactEditor.class},
   	{YukonUser.class, LoginEditorPanel.class},
	{YukonGroup.class, RoleGroupEditorPanel.class},
	{UserGroup.class, UserGroupEditorPanel.class}, 
   	{DeviceBase.class, DeviceEditorPanel.class},
   	{DirectPort.class, PortEditorPanel.class},
   	{NotificationGroup.class, NotificationGroupEditorPanel.class},
	//{com.cannontech.database.data.notification.ContactNotification.class, com.cannontech.dbeditor.editor.notification.recipient.NotificationRecipientEditorPanel.class},

   	{GroupState.class, StateEditorPanel.class},
	{LMControlArea.class, LMControlAreaEditor.class},
   	{LMProgramBase.class, LMProgramEditor.class},
   	{LMGroup.class, LMGroupEditor.class},
   	{PointBase.class, PointEditorPanel.class},
   	{RouteBase.class, RouteEditorPanel.class},
   	{HolidaySchedule.class, HolidayScheduleEditor.class},
	{Baseline.class, BaselineEditorPanel.class},
	{ConfigTwoWay.class, ConfigEditorPanel.class},
		
	{Tag.class,TagEditorPanel.class},
	{LMProgramConstraint.class, LMConstraintEditorPanel.class},
	{LMScenario.class, LMScenarioEditorPanel.class},
	{SeasonSchedule.class, SeasonEditorPanel.class},
	{TOUSchedule.class,TOUEditorPanel.class},
	{TcpPort.class, com.cannontech.dbeditor.editor.port.PortEditorPanel.class},
        
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
	
    for( int i = 0; i < EDITOR_CLASSES.length; i++ ) {
        if( o.equals(EDITOR_CLASSES[i][0]) ) {
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
public static PropertyPanel createEditorPanel(DBPersistent o) {
	if( o instanceof EditorPanel ) {
		return createEditorPanel( (EditorPanel)o );
	} else {
	   throw new IllegalArgumentException("The object of type: " + o.getClass().getName() + " is not a " + EditorPanel.class.getName() );
	}
}
}