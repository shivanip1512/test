package com.cannontech.macs.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.panel.ManualChangeJPanel;
import com.cannontech.common.gui.util.MessagePanel;
import com.cannontech.common.gui.util.SortTableModelWrapper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.wizard.WizardPanelEvent;
import com.cannontech.macs.debug.ScheduleDebugViewer;
import com.cannontech.macs.gui.popup.SchedulerPopUpMenu;
import com.cannontech.macs.schedule.editor.ScheduleEditorPanel;
import com.cannontech.message.macs.message.OverrideRequest;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.concrete.ResourceFactory;

public class SchedulerMainPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.ListSelectionListener, javax.swing.event.TableModelListener, java.util.Observer, com.cannontech.common.wizard.WizardPanelListener, com.cannontech.common.editor.PropertyPanelListener, javax.swing.event.PopupMenuListener, com.cannontech.clientutils.popup.PopUpEventListener
{
	private static final int PRE_CREATED_FRAMES = 3;
	private com.cannontech.macs.gui.popup.SchedulerPopUpMenu schedulePopupMenu = null;
	private ScheduleTableModel scheduleTableModel = null;
	private java.util.ArrayList frames = null;
	
	private javax.swing.JSplitPane jSplitPane = null;
//	private com.cannontech.macs.MACSClientConnection connection = null;
	private static final String title = "MACS Scheduler";
	
	private JTable scheduleTable;
	private JButton startStopButton;
	private JButton enableDisableButton;
	private JButton editButton;
	private JButton createScheduleButton;
	private JButton deleteScheduleButton;
	
	//public SchedulerFileMenu schedulerFileMenu;
	//public SchedulerEditMenu schedulerEditMenu;
	//public SchedulerCreateMenu schedulerCreateMenu;
	private MessagePanel messagePanel;

	// crutch to remember the last schedule that was selected
	//so that we can reselect it if the table has to be updated
	private Schedule lastSelected = null;

	// Flag for connection to macs server
	private boolean lastConnectionStatus = false;
	//private boolean startingUp = true;

/**
 * SchedulerMainPanel constructor comment.
 */
public SchedulerMainPanel( boolean initPanelOnly ) 
{
	super();
	
	initialize( initPanelOnly );
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(ActionEvent event) 
{
	java.awt.Frame parent = null;
	java.awt.Cursor savedCursor = null;
	
	try
	{	
		parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
		savedCursor = parent.getCursor();
		parent.setCursor( new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR) );
			
		if( event.getSource() == getStartStopButton() )
		{
			executeStartStopButton_ActionPerformed( event );
		}
		else if( event.getSource() == getEnableDisableButton() )
		{
			executeEnableDisableButton_ActionPerformed( event );
		}
		else if( event.getSource() == getCreateScheduleButton() )
		{
			executeCreateButton_ActionPerformed( event );
		}
		else if( event.getSource() == getEditViewButton() )
		{
			executeEditButton_ActionPerformed( event );
		}
		/*else if( event.getSource() == schedulerFileMenu.connectMenuItem )
		{
			try
			{
				getConnection().reconnect();
			}
			catch(Exception e )
			{
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}*/
		else if( event.getSource() == getDeleteScheduleButton() )
		{
			executeDeleteButton_ActionPerformed( event );		
		}
		
	}
	finally
	{
		if( parent != null && savedCursor != null )
		{
			parent.setCursor( savedCursor );
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/23/00 2:08:41 PM)
 * @return boolean
 * @param actionString java.lang.String
 */
private boolean confirmAction(String actionString) {
	int response = javax.swing.JOptionPane.showConfirmDialog( this, actionString, "MACS Action", javax.swing.JOptionPane.YES_NO_OPTION );

	return ( response == javax.swing.JOptionPane.YES_OPTION );
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 4:21:41 PM)
 * @return java.lang.String[]
 */
public String[] createPrintableText() 
{
	int columnCount = this.getScheduleTable().getColumnCount();
	int rowCount = this.getScheduleTable().getRowCount();
																
	int cellCount = ( rowCount * columnCount + columnCount);
	
	String[] tableData = new String[ cellCount ];

	int j = 0;
	for( j = 0; j < columnCount; j++ )
	{
		tableData[ j ] = getScheduleTableModel().getColumnName( j );
	}
	
	for( int i = 0; i < rowCount; i++ )
	{
		for( int k = 0; k < columnCount; k++ )
		{
			if( getScheduleTableModel().getValueAt( i, k ).equals("") )
				break;  // blank row
			else
				tableData[ j++ ] = getScheduleTableModel().getValueAt( i, k ).toString();
		}
	}
	
	return tableData;
}
/**
 * Insert the method's description here.
 * Creation date: (3/23/00 2:08:41 PM)
 */
private void executeCreateButton_ActionPerformed( ActionEvent event )
{
	showWizardPanel( new com.cannontech.macs.schedule.wizard.ScheduleWizardPanel() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/23/00 2:08:41 PM)
 */
private void executeDeleteButton_ActionPerformed( ActionEvent event )
{
	Schedule selected = getSelectedSchedule();
	
	//Nothing we can do if there isn't something selected
	//in fact we should never have gotten here in that case
	if( selected == null || selected.getCurrentState().equals(Schedule.STATE_PENDING) )
		return;

	if( javax.swing.JOptionPane.showConfirmDialog( com.cannontech.common.util.CtiUtilities.getParentFrame(this), "Do you really want to delete '" + selected.getScheduleName() + "'?", "Schedule Deletion", javax.swing.JOptionPane.YES_NO_OPTION ) == javax.swing.JOptionPane.NO_OPTION )
		return;

	try
	{
		getIMACSConnection().sendDeleteSchedule( selected.getId() );
		getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, "Deleted schedule '" + selected.getScheduleName() + "' successfully sent", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE));		
	}
	catch( java.io.IOException e )
	{
		handleException( e );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (3/23/00 2:08:41 PM)
 */
private void executeEditButton_ActionPerformed( ActionEvent event )
{
   if( !getSelectedSchedule().getCurrentState().equals(Schedule.STATE_PENDING) &&
    	!getSelectedSchedule().getCurrentState().equals(Schedule.STATE_RUNNING) )
		showEditorPanel( getSelectedSchedule() );
}
/**
 * Insert the method's description here.
 * Creation date: (3/23/00 2:08:41 PM)
 */
private void executeEnableDisableButton_ActionPerformed( ActionEvent event )
{
	try
	{
		Schedule selected = getSelectedSchedule();
		//Nothing we can do if there isn't something selected
		//in fact we should never have gotten here in that case
		if( selected == null || selected.getCurrentState().equals(Schedule.STATE_PENDING) )
			return;
		
		String actionString = "Are you sure you want to " + enableDisableButton.getText().toLowerCase() +
							  " schedule '" + selected.getScheduleName() + "'?";

		if( !confirmAction(actionString) )
			return;

			
		// send out the message
		getIMACSConnection().sendEnableDisableSchedule(selected);
		getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, 
			enableDisableButton.getText().toLowerCase() + " schedule command successfully sent for '" +
			selected.getScheduleName() + "'", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE));
	}
	catch( java.io.IOException e )
	{
		handleException( e );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/23/00 2:08:41 PM)
 */
private void executeStartStopButton_ActionPerformed( ActionEvent event )
{
	try
	{
		Schedule selected = getSelectedSchedule();
		//Nothing we can do if there isn't something selected
		//in fact we should never have gotten here in that case
		if( selected == null || selected.getCurrentState().equalsIgnoreCase(Schedule.STATE_DISABLED) )
			return;


		final javax.swing.JDialog d = new javax.swing.JDialog( com.cannontech.common.util.CtiUtilities.getParentFrame(this) );
		ManualChangeJPanel panel = null;
		
		if( selected.getCurrentState().equalsIgnoreCase(Schedule.STATE_WAITING) )
		{
			d.setTitle("Start schedule " + selected.getScheduleName() );
			panel = new ManualChangeJPanel()
			{
				public void exit()
				{
					d.dispose();
				}			
			};
		}
		else
		{
			d.setTitle("Stop schedule " + selected.getScheduleName() );
			panel = new ManualChangeJPanel( ManualChangeJPanel.MODE_STOP )
			{
				public void exit()
				{
					d.dispose();
				}			
			};
		}
					

		d.setModal(true);
		d.setContentPane(panel);
		//d.setSize(280,280);
		d.pack();
		d.setLocationRelativeTo(this);
		d.show();

		if( panel.getChoice() == ManualChangeJPanel.OK_CHOICE )
		{
			// send out the message
			if( panel.getMode() == ManualChangeJPanel.MODE_STOP )
				getIMACSConnection().sendStartStopSchedule( 
									selected, 
									null, 
									panel.getStopTime(), 
									(panel.isStopStartNowSelected() ? OverrideRequest.OVERRIDE_STOP_NOW : OverrideRequest.OVERRIDE_STOP) );
			else
				getIMACSConnection().sendStartStopSchedule( 
									selected, 
									panel.getStartTime(), 
									panel.getStopTime(), 
									(panel.isStopStartNowSelected() ? OverrideRequest.OVERRIDE_START_NOW : OverrideRequest.OVERRIDE_START) );
		}

		//destroy the JDialog
		d.dispose();
	}
	catch( java.io.IOException e )
	{
		handleException( e );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 3:41:09 PM)
 * @return javax.swing.JFrame
 */
private javax.swing.JFrame getAvailableFrame() 
{
	for( int i = 0; i < getFrames().size(); i++ )
	{
		if( !((javax.swing.JFrame)getFrames().get(i)).isVisible() )
			return (javax.swing.JFrame)getFrames().get(i);
	}
	
	javax.swing.JFrame frame = new javax.swing.JFrame();
	getFrames().add(frame);
	return frame;
}
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 1:54:34 PM)
 * @return com.cannontech.macs.ClientConnection
 */
private IMACSConnection getIMACSConnection() 
{
	return ResourceFactory.getIYukon().getMACSConnection();
}

/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 9:55:18 AM)
 */
public String getConnectionState() 
{
	StringBuffer title = new StringBuffer("MACS Scheduler");
	boolean validConn = getIMACSConnection().getMACSConnBase().isValid();

	if( validConn && !lastConnectionStatus )
	{
		// connected and change
		title.append("   [Connected to MacsServer@" + 
				getIMACSConnection().getMACSConnBase().getHost() + 
				":" + getIMACSConnection().getMACSConnBase().getPort() + "]");

		getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, "Connection to MACS server established", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE));
		
		try
		{
			com.cannontech.clientutils.CTILogger.info("...Retrieving MACS schedules...");
			getIMACSConnection().sendRetrieveAllSchedules();
		}
		catch( java.io.IOException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
	else if( !validConn && lastConnectionStatus )
	{
		// not connected and change
		title.append("   [Not Connected to MacsServer]");
		getScheduleTableModel().clear();
		getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, "No connection to MACS server", com.cannontech.common.util.MessageEvent.ERROR_MESSAGE));
	}
	else if( lastConnectionStatus )  // still connected
		title.append("   [Connected to MacsServer@" + 
					getIMACSConnection().getMACSConnBase().getHost() + 
					":" + getIMACSConnection().getMACSConnBase().getPort() + "]");
					
	else // still disconnected
	{		
		title.append("   [Not Connected to MacsServer]");
		getScheduleTableModel().clear();
	}
			
	lastConnectionStatus = validConn;

	return title.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 9:56:38 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getCreateScheduleButton() 
{
	if( createScheduleButton == null )
	{
		createScheduleButton = new JButton("Create...");
		createScheduleButton.setPreferredSize( new java.awt.Dimension( 80, 23 ) );
		createScheduleButton.addActionListener(this);
		createScheduleButton.setVisible( com.cannontech.macs.gui.Scheduler.isCreateable() );
	}

	return createScheduleButton;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:01:39 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getDeleteScheduleButton() 
{
	if( deleteScheduleButton == null )
	{
		deleteScheduleButton = new JButton("Delete");
		deleteScheduleButton.setPreferredSize( new java.awt.Dimension( 80, 23 ) );
		deleteScheduleButton.setEnabled(false);
		deleteScheduleButton.addActionListener( this );
		deleteScheduleButton.setVisible( com.cannontech.macs.gui.Scheduler.isCreateable() );
	}

	return deleteScheduleButton;
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getEditViewButton() 
{
	if( editButton == null )
	{
		editButton = new JButton("Edit...");
		editButton.setPreferredSize( new java.awt.Dimension( 80, 23 ) );
		editButton.addActionListener(this);
		editButton.setEnabled(false);
		editButton.setVisible( com.cannontech.macs.gui.Scheduler.isCreateable() );
	}
	
	return editButton;
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getEnableDisableButton() 
{
	if( enableDisableButton == null )
	{
		enableDisableButton = new JButton("Enable");
		enableDisableButton.setPreferredSize( new java.awt.Dimension(80,23) );
		enableDisableButton.setEnabled(false);
		enableDisableButton.addActionListener(this);
		enableDisableButton.setVisible( com.cannontech.macs.gui.Scheduler.isEnableable() );
	}

	return enableDisableButton;
}
/**
 * Insert the method's description here.
 * Creation date: (2/15/2001 10:55:27 AM)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getFrames() 
{
	if( frames == null )
	{
		frames = new java.util.ArrayList(10);

		for( int i = 0; i < PRE_CREATED_FRAMES; i++ )
			getFrames().add( new javax.swing.JFrame() );
	}
		
	return frames;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 9:29:43 AM)
 * @return javax.swing.JSplitPane
 */
private javax.swing.JSplitPane getJSplitPane() 
{
	if( jSplitPane == null )
	{
		jSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
		jSplitPane.setName("JSplitPane");
		jSplitPane.setDividerSize(8);
		jSplitPane.setLastDividerLocation(1);
		jSplitPane.setDividerLocation(185);
		jSplitPane.setOneTouchExpandable(true);
		jSplitPane.setBounds(23, 236, 582, 307);
	}
	
	return jSplitPane;
}
/**
 * Insert the method's description here.
 * Creation date: (2/23/2001 5:35:55 PM)
 * @return com.cannontech.common.gui.util.MessagePanel
 */
private com.cannontech.common.gui.util.MessagePanel getMessagePanel() 
{
	if( messagePanel == null )
		messagePanel = new MessagePanel();

	return messagePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 10:14:31 AM)
 */
private com.cannontech.macs.gui.popup.SchedulerPopUpMenu getSchedulePopupMenu() 
{
	if( schedulePopupMenu == null )
	{
		schedulePopupMenu = new com.cannontech.macs.gui.popup.SchedulerPopUpMenu();
		schedulePopupMenu.setName("ScheduleTablePopupMenu");
	}
	
	return schedulePopupMenu;
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/00 3:56:44 PM)
 * @return javax.swing.JTable
 */
public javax.swing.JTable getScheduleTable() 
{
	if( scheduleTable == null )
	{
		scheduleTable = new JTable( getScheduleTableModel() );
		scheduleTable.setName("ScheduleTable");
		scheduleTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
		scheduleTable.setGridColor( java.awt.Color.black );
		scheduleTable.setDefaultRenderer( Object.class, new ScheduleCellRenderer() );
		scheduleTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

		scheduleTable.setGridColor( getScheduleTable().getTableHeader().getBackground() );
		
		
		//add all the needed code for sorting a table
		final SortTableModelWrapper s = new SortTableModelWrapper(getScheduleTableModel());
		java.awt.event.MouseAdapter m = new java.awt.event.MouseAdapter() 
		{
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				if( e.getClickCount() == 2 )
				{
					int vc = getScheduleTable().getColumnModel().getColumnIndexAtX( e.getX() );
					int mc = getScheduleTable().convertColumnIndexToModel( vc );

					java.awt.Frame owner = CtiUtilities.getParentFrame( getScheduleTable() );
					java.awt.Cursor original = null;

					if( owner != null )
					{	
						original = owner.getCursor();	
						owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
					}
		
					try
					{						
//						Schedule sched = 
//								getScheduleTableModel().getSchedule(getScheduleTable().getSelectedRow());

						s.sort( mc );

						//Set the last selected row selected
//						int i = 0;
//						for( i = 0 ; i < getScheduleTableModel().getRowCount(); i++ )
//							if( getScheduleTableModel().getSchedule(i).equals(sched) )
//							{
//								getScheduleTable().getSelectionModel().setSelectionInterval( i, i );
//								break;
//							}
							
//						i = (i == getScheduleTableModel().getRowCount()-1 ? i+1: i);
//						getScheduleTable().scrollRectToVisible( 
//								new Rectangle( 
//									0, 
//									(getScheduleTable().getRowHeight() * i),
//									0, 0 ) );
					}
					finally
					{
						if( owner != null )
							owner.setCursor( original );
					}
					
				}
				
			}};
	
		s.setTableHeaderListener( m, scheduleTable.getTableHeader() );
		scheduleTable.setModel( s );
		
		
	}
	
	return scheduleTable;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 9:34:26 AM)
 * @return com.cannontech.macs.gui.ScheduleTableModel
 */
public ScheduleTableModel getScheduleTableModel() 
{
	if( scheduleTableModel == null )
	{
		// Set up the schedule table
		scheduleTableModel = new ScheduleTableModel();
		//scheduleTableModel.setConnection( getConnection() );
		
		getIMACSConnection().getMACSConnBase().addObserver(scheduleTableModel);
		
		
		//Listen for TableModelEvent
		getScheduleTableModel().addTableModelListener( this );
	}
	
	return scheduleTableModel;
}
/**
 * This method was created in VisualAge.
 * @return Schedule
 */
protected Schedule getSelectedSchedule() 
{
	ListSelectionModel lsm = getScheduleTable().getSelectionModel();
	
	//only one should be selected
	int selectedRow = lsm.getMinSelectionIndex();


	if( selectedRow < 0 )
		return null;
		
	Schedule selected = getScheduleTableModel().getSchedule( selectedRow );

	return selected;
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getStartStopButton() 
{
	if( startStopButton == null )
	{
		startStopButton = new JButton("Start");
		startStopButton.setPreferredSize( new java.awt.Dimension( 80, 23 ) );
		startStopButton.setEnabled(false);
		startStopButton.addActionListener( this );
		startStopButton.setVisible( com.cannontech.macs.gui.Scheduler.isStartable() );
	}
	
	return startStopButton;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/* This method was created in VisualAge.
 * @param event com.cannontech.common.util.MessageEvent
 */
public void handlePopUpEvent(com.cannontech.clientutils.commonutils.GenericEvent event)
{

	try
	{
		switch( event.getEventId() )
		{
			case SchedulerPopUpMenu.DELETE_SCHEDULE:
			executeDeleteButton_ActionPerformed( new ActionEvent(this, SchedulerPopUpMenu.DELETE_SCHEDULE, "delete") );
			break;

			case SchedulerPopUpMenu.EDIT_SCHEDULE:
			executeEditButton_ActionPerformed( new ActionEvent(this, SchedulerPopUpMenu.EDIT_SCHEDULE, "edit") );
			break;

			case SchedulerPopUpMenu.ENABLEDISABLE_SCHEDULE:
			executeEnableDisableButton_ActionPerformed( new ActionEvent(this, SchedulerPopUpMenu.ENABLEDISABLE_SCHEDULE, "enableDisable") );
			break;

			case SchedulerPopUpMenu.STARTSTOP_SCHEDULE:
			executeStartStopButton_ActionPerformed( new ActionEvent(this, SchedulerPopUpMenu.STARTSTOP_SCHEDULE, "startStop") );
			break;

			case SchedulerPopUpMenu.UPDATE_SCHEDULE:
			getIMACSConnection().sendRetrieveOneSchedule( getSelectedSchedule().getId() );
			break;

			default:
			throw new RuntimeException("Unknown eventId received from " + event.getSource().getClass().getName() + ", id = " + event.getEventId() );
		}
	}
	catch( java.io.IOException e )
	{
		handleException( e );
	}


}
/**
 * Insert the method's description here.
 * Creation date: (2/21/2001 5:07:20 PM)
 */
private void initConnections() 
{
	//Listen for list selection events from the table so we can maintain the last selected
	//schedule between updates
	ListSelectionModel lsModel = getScheduleTable().getSelectionModel();
	lsModel.addListSelectionListener( this );

	//Listen for mouse click events from the table
	getScheduleTable().addMouseListener( this );	

	// init the popup box for the ScheduleTable
	java.awt.event.MouseListener schedListener = new com.cannontech.clientutils.popup.PopUpMenuShower( getSchedulePopupMenu() );
	getScheduleTable().addMouseListener( schedListener );
	getSchedulePopupMenu().addPopupMenuListener( this );

	getSchedulePopupMenu().addPopUpEventListener( this );

	getScheduleTableModel().addTableModelListener( this );

	//Observer connection state changes
	getIMACSConnection().getMACSConnBase().addObserver(this);
	//update( getConnection(), getConnection() );
}
/**
 * This method was created in VisualAge.
 */
private void initialize( boolean initPanelOnly ) 
{
	setLayout( new java.awt.BorderLayout() );
	
	// Create the JTable with its model and scrollpane
	javax.swing.JScrollPane scrollPaneSchedule = new javax.swing.JScrollPane(getScheduleTable());

	initConnections();
	
	JPanel toolBarPanel = null;
	
	if( !initPanelOnly )
	{
		// 'Tool bar'
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout( new java.awt.FlowLayout() );
		buttonPanel.add( getStartStopButton() );
		buttonPanel.add( getEnableDisableButton() );
		buttonPanel.add( getEditViewButton() );
		buttonPanel.add( getCreateScheduleButton() );
		buttonPanel.add( getDeleteScheduleButton() );

		toolBarPanel = new JPanel();
		toolBarPanel.setLayout( new java.awt.BorderLayout() );
		toolBarPanel.add( buttonPanel, "West" );
	}

	add( scrollPaneSchedule, "Center" );

	if( toolBarPanel != null )
		add( toolBarPanel, "North" );
		
	add( getMessagePanel(), "South" );	
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseClicked(MouseEvent event)
{

	//If there was a double click open a new edit window
	if (event.getClickCount() == 2  )//&& !(Scheduler.isReadOnly()) )
	{
		if( event.isShiftDown() )
			showDebugInfo();
		else		
			executeEditButton_ActionPerformed( new ActionEvent(event.getSource(), event.getID(), "Mouse Clicked") );
	}
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseEntered(MouseEvent event) {
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseExited(MouseEvent event) {
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mousePressed(MouseEvent event) 
{
	if( event.getSource() == SchedulerMainPanel.this.getScheduleTable() )
	{
		int rowLocation = getScheduleTable().rowAtPoint( event.getPoint() );
		
		getScheduleTable().getSelectionModel().setSelectionInterval(
				 		rowLocation, rowLocation );
	}

}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseReleased(MouseEvent event) {
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 9:45:22 AM)
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) 
{		
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 9:45:22 AM)
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) 
{
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 9:45:22 AM)
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
{
	if( e.getSource() == SchedulerMainPanel.this.getSchedulePopupMenu() )
	{
		getSchedulePopupMenu().setSchedule( getSelectedSchedule() );
	}
}
/** Removes an ActionListener 
 *
 * @param l  the com.cannontech.clientutils.popup.PopUpEventListener to remove
 */
public void removePopUpEventListener(com.cannontech.clientutils.popup.PopUpEventListener l)
{
   listenerList.remove(com.cannontech.clientutils.popup.PopUpEventListener.class, l);
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 4:07:38 PM)
 */
private void removeUnneededFrames() 
{
	for( int i = 0; i < getFrames().size(); i++ )
	{
		if( getFrames().size() > PRE_CREATED_FRAMES )
		{
			if( ((javax.swing.JFrame)getFrames().get(i)).isVisible() )
				continue;
			else
				getFrames().remove(i);
		}
		else
			return;
	}

}
/**
 * This method was created in VisualAge.
 * @param event com.cannontech.common.editor.PropertyPanelEvent
 */
public void selectionPerformed( PropertyPanelEvent event) 
{
	if( !( event.getSource() instanceof PropertyPanel) )
		return;

	PropertyPanel panel = (PropertyPanel) event.getSource();

	// if the input entered is not legit and the user wants to commit the changes
	if( (event.getID() == PropertyPanelEvent.APPLY_SELECTION ||
		  event.getID() == PropertyPanelEvent.OK_SELECTION) && !panel.isInputValid() )
	{
		javax.swing.JOptionPane.showMessageDialog( panel, panel.getErrorString(), "Input Error", javax.swing.JOptionPane.WARNING_MESSAGE );
		return;
	}

	
	//Update the object on an apply or ok
	if( event.getID() == PropertyPanelEvent.APPLY_SELECTION ||
		 event.getID() == PropertyPanelEvent.OK_SELECTION		)
	{
		try
		{
			// use a clone of the Schedule passed into the panel.setValue(Object) call
			// since we do not want our client to change its meaning of the Schedule
			Schedule object = (Schedule) panel.getValue( null );

			getIMACSConnection().sendUpdateSchedule( object );
		}
		catch( java.io.IOException e )
		{
			handleException( e );
		}
		
		panel.setChanged(false);
	}

	//Destroy the frame on an ok or a cancel
	if( event.getID() == PropertyPanelEvent.OK_SELECTION ||
		 event.getID() == PropertyPanelEvent.CANCEL_SELECTION )
	{
		//Loop through all the frames on the desktopPane
		//and find out which one contains the PropertyPanel
		//responsible for this event
		PropertyPanel p = (PropertyPanel)event.getSource();
		
		for( int i = 0; i < getFrames().size(); i++ )
		{
			if( ((javax.swing.JFrame)getFrames().get(i)).getContentPane() == p )
			{
				((javax.swing.JFrame)getFrames().get(i)).setVisible(false);

				if( i >= PRE_CREATED_FRAMES )
					removeUnneededFrames();

				repaint();
				break;
			}
		}
	}
	
}
/**
 * This method was created in VisualAge.
 * @param event com.cannontech.common.wizard.WizardPanelEvent
 */
public void selectionPerformed(WizardPanelEvent event) 
{
	if( event.getID() == WizardPanelEvent.FINISH_SELECTION )
	{
		WizardPanel p = (WizardPanel) event.getSource();

		Schedule newItem = (Schedule)p.getValue(null);

		try
		{
			// send the new schedule
			getIMACSConnection().sendCreateSchedule( newItem );				
			getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, "Created new schedule '" + ((Schedule)newItem).getScheduleName() + "' successfully", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE));
		}
		catch( java.io.IOException e )
		{
			getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, "Unable to create schedule '" + ((Schedule)newItem).getScheduleName() + "'", com.cannontech.common.util.MessageEvent.ERROR_MESSAGE));
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		
	}

	if( event.getID() == WizardPanelEvent.CANCEL_SELECTION ||
		event.getID() == WizardPanelEvent.FINISH_SELECTION )
	{
		//Loop through all the frames on the desktopPane
		//and find out which one contains the WizardPanel
		//responsible for this event
		WizardPanel p = (WizardPanel) event.getSource();
		
		for( int i = 0; i < getFrames().size(); i++ )
		{
			if( ((javax.swing.JFrame)getFrames().get(i)).getContentPane() == p )
			{
				((javax.swing.JFrame)getFrames().get(i)).setVisible(false);
				
				if( i >= PRE_CREATED_FRAMES )
					removeUnneededFrames();
					
				repaint();
				break;
			}
		}
		
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/00 4:29:59 PM)
 * Version: <version>
 */
private void showDebugInfo( ) 
{
	
	ScheduleDebugViewer d = new ScheduleDebugViewer( 
		com.cannontech.common.util.CtiUtilities.getParentFrame(this) ); 
	
	d.setValue( getSelectedSchedule() );
		
	d.setLocation( this.getLocationOnScreen() );		
	d.setModal( true );		
	d.show();
	
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void showEditorPanel( final Schedule selectedSchedule ) 
{
	if( selectedSchedule == null )
		return;
	
	java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	java.awt.Cursor savedCursor = owner.getCursor();
	
	try
	{
		owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

		//create a new editor panel for this Schedule
		ScheduleEditorPanel panel = new com.cannontech.macs.schedule.editor.ScheduleEditorPanel();
				//(ScheduleEditorPanel)selectedSchedule.getEditorPanel();
				
		javax.swing.JFrame frame = getAvailableFrame();
		frame.setContentPane( panel );
		
		// sets the size of EVERY editor frame!!!!!!!
		frame.setSize(450,570);
		panel.addPropertyPanelListener(this);
	
		frame.setTitle( panel.toString() );
		frame.setLocation( getEditViewButton().getLocation() );
		frame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("tdcIcon.gif"));
		frame.show();


		// use a clone of the desired Schedule since we do not want our client
		// to change its meaning of the Schedule
		Schedule tempSched = (Schedule)com.cannontech.common.util.CtiUtilities.copyObject(selectedSchedule);
		tempSched.getNonPersistantData().setCategories( getIMACSConnection().getCategoryNames().keys() );
		panel.setValue( tempSched );

		frame.validate();
	

		// IF ITS A SCRIPT SCHEDULE, WE MUST GET THE SCRIPT TEXT HERE
		if( Schedule.SCRIPT_TYPE.equalsIgnoreCase(selectedSchedule.getType()) )
			getIMACSConnection().sendRetrieveScriptText( selectedSchedule.getScriptFileName() );		
	}
	catch( java.io.IOException e )
	{
		handleException( e );
	}
	finally
	{
		owner.setCursor( savedCursor );
	}
}
/**
 * This method was created in VisualAge.
 * @param wizard com.cannontech.common.wizard.WizardPanel
 */
private void showWizardPanel(WizardPanel wizard) 
{
	//Set the cursor to wait
	java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame( this );
	java.awt.Cursor savedCursor = owner.getCursor();
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	wizard.addWizardPanelListener(this);	
	javax.swing.JFrame frame = getAvailableFrame();
	frame.setContentPane( wizard );
	frame.setSize(450,570);

	//frame.setLocationRelativeTo( getCreateScheduleButton() );
	frame.setLocation( getCreateScheduleButton().getLocation() );

	// create a new schedule with the current categories available and other data
	Schedule tempSched = new Schedule();
	tempSched.getNonPersistantData().setCategories( getIMACSConnection().getCategoryNames().keys() );
	wizard.setValue( tempSched );
	
	frame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("tdcIcon.gif"));
	frame.setTitle("Create Schedule");
	frame.show();
}
/**
 * This method was created in VisualAge.
 * @param selected Schedule
 */
protected void synchTableAndButtons(Schedule selected)
{
   if (startStopButton == null || editButton == null || enableDisableButton == null)
	  return;
	
	getDeleteScheduleButton().setEnabled(true);
	getEnableDisableButton().setEnabled(true);

   if (selected.getCurrentState().equals(Schedule.STATE_WAITING))
   {
   	  getEditViewButton().setEnabled(true);
	  getStartStopButton().setText("Start");
	  getStartStopButton().setEnabled(true);
	  getEnableDisableButton().setText("Disable");
   }
   else if (selected.getCurrentState().equals(Schedule.STATE_RUNNING))
   {
   	  getEditViewButton().setEnabled(false);
	  getStartStopButton().setText("Stop");
	  getStartStopButton().setEnabled(true);
	  getDeleteScheduleButton().setEnabled(false);
	  getEnableDisableButton().setText("Disable");
   }
   else if (selected.getCurrentState().equals(Schedule.STATE_DISABLED))
   {
   	  getEditViewButton().setEnabled(true);
	  getStartStopButton().setText("Start");
	  getStartStopButton().setEnabled(false);
	  getEnableDisableButton().setText("Enable");
   }
   else if (selected.getCurrentState().equals(Schedule.STATE_PENDING))
   {
   	 
	   //disable all buttons!!
	  getStartStopButton().setText("Stop");
	  getEnableDisableButton().setEnabled(false);
	  getEditViewButton().setEnabled(false);
	  getDeleteScheduleButton().setEnabled(false);
   }
   //revalidate();
   //repaint();
}
/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(TableModelEvent event ) 
{
	if( event instanceof com.cannontech.macs.events.MACSGenericTableModelEvent )
	{
		getScheduleTable().getSelectionModel().setSelectionInterval( -1, -1 );
		lastSelected = null;
	}
	else if( lastSelected != null )
	{
		//Attempt to re-selected the last thing that was selected
		for( int i = 0; i < getScheduleTableModel().getRowCount(); i++ )
		{
			if( lastSelected.equals( getScheduleTableModel().getSchedule(i) ) )
			{
				getScheduleTable().getSelectionModel().setSelectionInterval( i, i );
				break;
			}
		}
	}

	revalidate();
	
	Schedule selected = getSelectedSchedule();

	if( selected != null )
		synchTableAndButtons( selected );

	getScheduleTable().repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:36:12 AM)
 * @param o java.util.Observable
 * @param val java.lang.Object
 */
public void update(java.util.Observable obs, Object val)
{

	//Could be an instance of com.cannontech.macs.MACSClientConnection
	//notifying us of a change in the connections state
	if( val != null && val instanceof IMACSConnection )
	{
		// set the frames Title to a connected/not connected text
		final String connectedString = getConnectionState();
				
		javax.swing.SwingUtilities.invokeLater( new Runnable()
		{
			public void run()
			{
				java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(SchedulerMainPanel.this);
				if( f != null )
					f.setTitle(connectedString);
			}
				
		});			
					
	}
	else if( val instanceof com.cannontech.message.macs.message.Info )
	{
		getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, ((com.cannontech.message.macs.message.Info)val).getInfo(), com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE));
	}
	else if( val instanceof com.cannontech.message.macs.message.ScriptFile )
	{
		for( int i = 0; i < getFrames().size(); i++ )
		{
			javax.swing.JFrame f = (javax.swing.JFrame)getFrames().get(i);

			if( f.isVisible() 
			    && f.getContentPane() instanceof ScheduleEditorPanel )
			{
				ScheduleEditorPanel pane = (ScheduleEditorPanel)f.getContentPane();

				pane.updateScriptText( (com.cannontech.message.macs.message.ScriptFile)val );
			}

		}
		
	}
	
}
/**
 * This method handles ListSelectionEvents generated by the schedule
 * JTable.  
 * @param event ListSelectionEvent
 */
public void valueChanged(ListSelectionEvent event) {

	ListSelectionModel lsm = (ListSelectionModel) event.getSource();

	if( lsm.isSelectionEmpty() )
		return;
	else
	{		
		Schedule selected = getSelectedSchedule();

		lastSelected = selected;

		if( selected != null )
			synchTableAndButtons( selected );
	}
	
}
}
