package com.cannontech.macs.gui.popup;

/**
 * Insert the type's description here.
 * Creation date: (2/13/2001 2:49:45 PM)
 * @author: 
 */
import com.cannontech.message.macs.message.Schedule;

public class SchedulerPopUpMenu extends com.cannontech.clientutils.popup.JPopUpMenuEventBase implements java.awt.event.ActionListener
{
	private Schedule schedule = null;
	//private int commandToExecute = -1;
	private javax.swing.JMenuItem jMenuItemEnableDisable = null;
	private javax.swing.JMenuItem jMenuItemStartStop = null;
	private javax.swing.JMenuItem jMenuItemEdit = null;
	private javax.swing.JMenuItem jMenuItemDelete = null;
	private javax.swing.JMenuItem jMenuItemUpdate = null;

	public static final int DELETE_SCHEDULE = 0;
	public static final int STARTSTOP_SCHEDULE = 1;
	public static final int ENABLEDISABLE_SCHEDULE = 2;
	public static final int EDIT_SCHEDULE = 3;
	public static final int UPDATE_SCHEDULE = 4;
/**
 * SchedulerPopUpMenu constructor comment.
 */
public SchedulerPopUpMenu() {
	super();
	initialize();
}
/**
 * SchedulerPopUpMenu constructor comment.
 * @param label java.lang.String
 */
public SchedulerPopUpMenu(String label) {
	super(label);
	initialize();
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event) 
{
	if( event.getSource() == SchedulerPopUpMenu.this.getJMenuItemStartStop() )
		firePopUpEvent( new com.cannontech.clientutils.commonutils.GenericEvent( this, "startStopSchedule", STARTSTOP_SCHEDULE ) );
		
	if( event.getSource() == SchedulerPopUpMenu.this.getJMenuItemEdit() )
		firePopUpEvent( new com.cannontech.clientutils.commonutils.GenericEvent( this, "editSchedule", EDIT_SCHEDULE) );
		
	if( event.getSource() == SchedulerPopUpMenu.this.getJMenuItemEnableDisable() )
		firePopUpEvent( new com.cannontech.clientutils.commonutils.GenericEvent( this, "enableDisableSchedule", ENABLEDISABLE_SCHEDULE ) );
		
	if( event.getSource() == SchedulerPopUpMenu.this.getJMenuItemDelete() )
		firePopUpEvent( new com.cannontech.clientutils.commonutils.GenericEvent( this, "deleteSchedule", DELETE_SCHEDULE ) );

	if( event.getSource() == SchedulerPopUpMenu.this.getJMenuItemUpdate() )
		firePopUpEvent( new com.cannontech.clientutils.commonutils.GenericEvent( this, "updateSchedule", UPDATE_SCHEDULE ) );
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemDelete()
{
	if( jMenuItemDelete == null )
	{
		jMenuItemDelete = new javax.swing.JMenuItem();
		jMenuItemDelete.setName("JMenuItemDelete");
		jMenuItemDelete.setMnemonic('t');
		jMenuItemDelete.setText("Delete");
		jMenuItemDelete.setVisible( com.cannontech.macs.gui.Scheduler.isCreateable() );
	}
	
	return jMenuItemDelete;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemEdit() 
{
	if( jMenuItemEdit == null )
	{
		jMenuItemEdit = new javax.swing.JMenuItem();
		jMenuItemEdit.setName("JMenuItemEdit");
		jMenuItemEdit.setMnemonic('d');
		jMenuItemEdit.setText("Edit...");
		jMenuItemEdit.setVisible( com.cannontech.macs.gui.Scheduler.isCreateable() );
	}
	
	return jMenuItemEdit;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemEnableDisable() 
{
	if( jMenuItemEnableDisable == null )
	{
		jMenuItemEnableDisable = new javax.swing.JMenuItem();
		jMenuItemEnableDisable.setName("JMenuItemEnableDisable");
		jMenuItemEnableDisable.setMnemonic('b');
		jMenuItemEnableDisable.setText("Enable");
		jMenuItemEnableDisable.setVisible( com.cannontech.macs.gui.Scheduler.isEnableable() );
	}
	
	return jMenuItemEnableDisable;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemStartStop() 
{
	if( jMenuItemStartStop == null )
	{
		jMenuItemStartStop = new javax.swing.JMenuItem();
		jMenuItemStartStop.setName("JMenuItemStartStop");
		jMenuItemStartStop.setMnemonic('s');
		jMenuItemStartStop.setText("Start...");
		jMenuItemStartStop.setVisible( com.cannontech.macs.gui.Scheduler.isStartable() );
	}
	
	return jMenuItemStartStop;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:13:30 AM)
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemUpdate()
{
	if( jMenuItemUpdate == null )
	{
		jMenuItemUpdate = new javax.swing.JMenuItem();
		jMenuItemUpdate.setName("JMenuItemUpdate");
		jMenuItemUpdate.setMnemonic('u');
		jMenuItemUpdate.setText("Update");
	}
	
	return jMenuItemUpdate;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:20:16 AM)
 * @return Schedule
 */
public Schedule getSchedule() {
	return schedule;
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:09:35 AM)
 */
private void initConnections() 
{
	getJMenuItemDelete().addActionListener( this );
	getJMenuItemEdit().addActionListener( this );
	getJMenuItemEnableDisable().addActionListener( this );
	getJMenuItemStartStop().addActionListener( this );
	getJMenuItemUpdate().addActionListener( this );
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:08:06 AM)
 */
private void initialize() 
{
	setName("SchedulePopUp");
	
	if( getJMenuItemStartStop().isVisible() )
		add(getJMenuItemStartStop(), getJMenuItemStartStop().getName());

	if( getJMenuItemEnableDisable().isVisible() )
		add(getJMenuItemEnableDisable(), getJMenuItemEnableDisable().getName());
		
	if( getJMenuItemEdit().isVisible() )
		add(getJMenuItemEdit(), getJMenuItemEdit().getName());

	if( getJMenuItemUpdate().isVisible() )
		add(getJMenuItemUpdate(), getJMenuItemUpdate().getName());
		
	if( getJMenuItemDelete().isVisible() )
		add(getJMenuItemDelete(), getJMenuItemDelete().getName());
	
	initConnections();
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2001 10:20:16 AM)
 * @param newSchedule Schedule
 */
public void setSchedule(Schedule newSchedule) 
{
	schedule = newSchedule;

	if( getSchedule() != null )
	{
		if( getSchedule().getCurrentState().equalsIgnoreCase( Schedule.STATE_RUNNING ) )
		{
			getJMenuItemDelete().setEnabled(false);
			getJMenuItemEdit().setEnabled(false);
			getJMenuItemStartStop().setText("Stop...");
		}
		else
		{
			getJMenuItemDelete().setEnabled(true);
			getJMenuItemEdit().setEnabled(true);
			getJMenuItemStartStop().setText("Start...");
		}

		if( getSchedule().getCurrentState().equalsIgnoreCase( Schedule.STATE_DISABLED ) )
		{
			getJMenuItemStartStop().setEnabled(false);
			getJMenuItemEnableDisable().setText("Enable");
			getJMenuItemEnableDisable().setEnabled(true);
			getJMenuItemUpdate().setEnabled(true);
		}
		else
		{
			getJMenuItemEnableDisable().setText("Disable");
			getJMenuItemStartStop().setEnabled(true);
			getJMenuItemEnableDisable().setEnabled(true);
			getJMenuItemUpdate().setEnabled(true);
		}


		// should always be at the bottom of this method
		if( getSchedule().getCurrentState().equalsIgnoreCase( Schedule.STATE_PENDING ) )
		{
			getJMenuItemStartStop().setText("Stop...");
			getJMenuItemDelete().setEnabled(false);
			getJMenuItemEnableDisable().setEnabled(false);
			getJMenuItemUpdate().setEnabled(false);
			getJMenuItemEdit().setEnabled(false);
		}
	}
	
}
}
