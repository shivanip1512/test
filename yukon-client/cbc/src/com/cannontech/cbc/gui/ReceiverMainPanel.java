package com.cannontech.cbc.gui;

/**
 * This type was created in VisualAge.
 */

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;

import com.cannontech.cbc.data.SubBus;
import com.cannontech.cbc.messages.CBCCommand;
import com.cannontech.cbc.popupmenu.CapBankDevicePopUp;
import com.cannontech.cbc.popupmenu.FeederPopUp;
import com.cannontech.cbc.popupmenu.SubBusPopUp;
import com.cannontech.cbc.tablemodelevents.CBCGenericTableModelEvent;
import com.cannontech.common.gui.panel.*;
import com.cannontech.common.gui.util.MessagePanel;
import com.cannontech.common.gui.util.SortTableModelWrapper;
import com.cannontech.message.dispatch.message.Multi;

public class ReceiverMainPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.ListSelectionListener, javax.swing.event.TableModelListener, java.util.Observer, javax.swing.event.PopupMenuListener 
{
	//private javax.swing.JSplitPane jSplitPaneOuter = null;
	//private javax.swing.JSplitPane jSplitPaneInner = null;
	private CompositeJSplitPane compSplitPane = null;
	private CompositeJDeskTopPanel compJDeskTopPanel = null;

	public static final int VIEW_SPLIT_PANE = 0;
	public static final int VIEW_INTERNAL_FRAMES = 1;
	private int viewMode = VIEW_SPLIT_PANE;

	private FeederPopUp feederPopupMenu = null;
	private SubBusPopUp subBusPopupMenu = null;
	private CapBankDevicePopUp capBankPopupMenu = null;
	private SubBusTableModel subBusTableModel = null;
	private CapBankTableModel cabBankTableModel = null;
	private FeederTableModel feederTableModel = null;
	private com.cannontech.cbc.data.CBCClientConnection connectionWrapper = null;
	private static final String TITLE = StrategyReceiver.CBC_NAME;
	
	private JTable subBusTable = null ;
	private JTable capBankTable = null;
	private JTable feederTable = null;

	private javax.swing.JScrollPane subBusScrollPane = null;
	private javax.swing.JScrollPane capBankScrollPane = null;
	private javax.swing.JScrollPane feederScrollPane = null;
	
	private JButton enableAreaButton = null;
	private JButton disableAreaButton = null;
	private JButton currentViewButton = null;
	private JButton confirmAreaButton = null;
	private JButton unwaiveAreaButton = null;
	private JButton waiveAreaButton = null;

	private MessagePanel messagePanel = null;

	public static final int NO_ROW_SELECTED = -1;
	
	// crutch to remember the last capbank or feeder that was selected
	//  so that we can reselect it if the table has to be updated
	private int prevSelectedCapBnkRow = NO_ROW_SELECTED;
	private int prevSelectedFeederRow = NO_ROW_SELECTED;
	private int prevSelectedSubRow = NO_ROW_SELECTED;

	// Flag for connection to CBC server
	private boolean lastConnectionStatus = true;
	private boolean startingUp = true;

/**
 * SchedulerMainPanel constructor comment.
 */
public ReceiverMainPanel( com.cannontech.cbc.data.CBCClientConnection conn ) 
{
	this( conn, VIEW_SPLIT_PANE );
}
/**
 * SchedulerMainPanel constructor comment.
 */
public ReceiverMainPanel( com.cannontech.cbc.data.CBCClientConnection conn, int displayMode ) 
{
	super();

	viewMode = displayMode;
	connectionWrapper = conn;
	
	initialize();
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
			
		if( event.getSource() == getEnableAreaButton() )
			execute_EnableAreaButton( event );

		if( event.getSource() == getDisableAreaButton() )
			execute_DisableAreaButton( event );

		if( event.getSource() == getCurrentViewButton() )
			execute_CurrentViewButton( event );
			
		if( event.getSource() == getConfirmAreaButton() )
			execute_ConfirmAreaButton( event );

		if( event.getSource() == getWaiveAreaButton() )
			execute_WaiveSubs( event );

		if( event.getSource() == getUnwaiveAreaButton() )
			execute_UnwaiveSubs( event );

	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
 * Creation date: (1/8/2001 12:44:52 PM)
 * @param e javax.swing.event.PopupMenuEvent
 */
private void capBankPopUpMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
{
	com.cannontech.cbc.data.CapBankDevice capBank = getCapBankTableModel().getRowAt(getCapBankTable().getSelectedRow());

	if( capBank != null )
	{				
		getCapBankPopupMenu().setCapBankDevice( 
				capBank,
				getFeederTableModel().getCapBankFeederOwner(capBank) );
	}

}


/**
 * Insert the method's description here.
 * Creation date: (3/23/00 2:08:41 PM)
 * @return boolean
 * @param actionString java.lang.String
 */
private boolean confirmAction( String actionString, String title )
{

	int response = javax.swing.JOptionPane.showConfirmDialog(
			this,
			actionString, 
			title, 
			javax.swing.JOptionPane.YES_NO_OPTION );

	return ( response == javax.swing.JOptionPane.YES_OPTION );
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 1:38:32 PM)
 */
private void createButtonToolBar() 
{
	JPanel toolBarPanel = null;

	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout( new java.awt.FlowLayout() );
	buttonPanel.add( getEnableAreaButton() );
	buttonPanel.add( getDisableAreaButton() );
	buttonPanel.add( getConfirmAreaButton() );
	buttonPanel.add( getWaiveAreaButton() );
	buttonPanel.add( getUnwaiveAreaButton() );
	buttonPanel.add( getCurrentViewButton() );

	toolBarPanel = new JPanel();
	toolBarPanel.setLayout( new java.awt.BorderLayout() );
	toolBarPanel.add( buttonPanel, "West" );

	if( toolBarPanel != null )
		add( toolBarPanel, "North" );

}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 3:16:38 PM)
 */
private void createCenterComponent() 
{
	// Now create and add the center component
	if( viewMode == VIEW_SPLIT_PANE )
	{
		remove( getCompJDeskTopPanel() );
		compJDeskTopPanel = null;
		
		add( getCompSplitPane(), "Center" );
		setBorder( null );
	}
	else if( viewMode == VIEW_INTERNAL_FRAMES )
	{
		remove( getCompSplitPane() );
		compSplitPane = null;

		add( getCompJDeskTopPanel(), "Center" );
		SubBus sub = getSubBusTableModel().getRowAt(getSubBusTable().getSelectedRow());
		
		//set a border for our main panel
		setBorder(
				new javax.swing.border.TitledBorder(
					javax.swing.BorderFactory.createEmptyBorder(),
					"Selected SubBus: " + (sub == null ? "" : sub.toString()),
					javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
					javax.swing.border.TitledBorder.ABOVE_TOP,
					new java.awt.Font("Courier", java.awt.Font.BOLD, 16 ),
					java.awt.Color.black ) );		
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 1:41:32 PM)
 */
private void createMessagePanel() 
{
	messagePanel = new MessagePanel();
	getConnectionWrapper().addMessageEventListener(messagePanel);
	add( messagePanel, "South" );
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 4:21:41 PM)
 * @return java.lang.String[]
 */
public String[] createPrintableText() 
{
	int columnCount = getSubBusTable().getColumnCount();
	int rowCount = getSubBusTable().getRowCount();
																
	int cellCount = ( rowCount * columnCount + columnCount);
	
	String[] tableData = new String[ cellCount ];

	int j = 0;
	for( j = 0; j < columnCount; j++ )
	{
		tableData[ j ] = getSubBusTableModel().getColumnName( j );
	}
	
	for( int i = 0; i < rowCount; i++ )
	{
		for( int k = 0; k < columnCount; k++ )
		{
			/*if( tableModel.getValueAt( i, k ).equals("") )
				break;  // blank row
			else*/
				tableData[ j++ ] = getSubBusTableModel().getValueAt( i, k ).toString();
		}
	}
	
	return tableData;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:51:22 PM)
 */
public void destroy() 
{
	getCapBankTableModel().clear();
	getConnectionWrapper().deleteObserver( this );
}

/**
 * Insert the method's description here.
 * Creation date: (12/3/2001 11:47:38 AM)
 * @param event java.awt.event.ActionEvent
 */
private void execute_ConfirmAreaButton(ActionEvent event) 
{
   String filter = getSubBusTableModel().getFilter();
   
	String actionString = "Are you sure you want to confirm " +
						  		 "the area '" + getSubBusTableModel().getFilter() + "'?";

	if( !confirmAction(actionString, "Confirm Area") )
		return;


	//lock down our table model
	synchronized( getSubBusTableModel() )
	{
		for( int i = 0; i < getSubBusTableModel().getRowCount(); i++ )
		{
			SubBus sub = getSubBusTableModel().getRowAt(i);
			
			getSubBusPopUp().setSubBus(sub);
			getSubBusPopUp().jMenuItemConfirm_ActionPerformed(null);
		}

	}

	
}
/**
 * Insert the method's description here.
 * Creation date: (12/3/2001 11:47:38 AM)
 * @param event java.awt.event.ActionEvent
 */
private void execute_CurrentViewButton( ActionEvent event )
{

   if( viewMode == VIEW_SPLIT_PANE )
	{
		viewMode = VIEW_INTERNAL_FRAMES ;
		getCurrentViewButton().setText("Multiple Frame View");
	}
	else
	{
		viewMode = VIEW_SPLIT_PANE;
		getCurrentViewButton().setText("Split Pane View");
	}

	createCenterComponent();
	getCapBankTable().invalidate();
	getCapBankTable().repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (12/3/2001 11:47:38 AM)
 * @param event java.awt.event.ActionEvent
 */
private void execute_DisableAreaButton(ActionEvent event) 
{
   String filter = getSubBusTableModel().getFilter();
   
	if( getSubBusTableModel().getRowCount() <= 0 )
		return;

	String actionString = "Are you sure you want to " + getDisableAreaButton().getActionCommand() +
						  " the area '" + getSubBusTableModel().getFilter() + "'?";

	if( !confirmAction(actionString, "Disable Area") )
		return;
		

	//lock down our table model
	synchronized( getSubBusTableModel() )
	{
		Multi multi =
				new Multi();
		
		for( int i = 0; i < getSubBusTableModel().getRowCount(); i++ )
		{
			SubBus sub = getSubBusTableModel().getRowAt(i);
			
			//only send the command if the state of the sub != disabled
			if( !sub.getCcDisableFlag().booleanValue() )
				multi.getVector().add( new CBCCommand(CBCCommand.DISABLE_SUBBUS, sub.getCcId().intValue()) );
		}

		if( multi.getVector().size() > 0 )
			getConnectionWrapper().write( multi );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (12/3/2001 11:47:38 AM)
 * @param event java.awt.event.ActionEvent
 */
private void execute_EnableAreaButton(ActionEvent event) 
{
   String filter = getSubBusTableModel().getFilter();
   
	if( getSubBusTableModel().getRowCount() <= 0 )
		return;

	String actionString = "Are you sure you want to " + getEnableAreaButton().getActionCommand() +
						  " the area '" + getSubBusTableModel().getFilter() + "'?";

	if( !confirmAction(actionString, "Enable Area") )
		return;
		
	//lock down our table model
	synchronized( getSubBusTableModel() )
	{
		Multi multi = new Multi();
		
		for( int i = 0; i < getSubBusTableModel().getRowCount(); i++ )
		{
			SubBus sub = getSubBusTableModel().getRowAt(i);
			
			//only send the command if the state of the sub != our wanted command
			if( sub.getCcDisableFlag().booleanValue() )
				multi.getVector().add( new CBCCommand(CBCCommand.ENABLE_SUBBUS, sub.getCcId().intValue()) );
		}

		if( multi.getVector().size() > 0 )
			getConnectionWrapper().write( multi );
	}	

}

/**
 * Comment
 */
public void execute_WaiveSubs(java.awt.event.ActionEvent actionEvent) 
{
	String filter = getSubBusTableModel().getFilter();
   
	if( getSubBusTableModel().getRowCount() <= 0 )
		return;

	int confirm = javax.swing.JOptionPane.showConfirmDialog( this, 
			"Are you sure you want to Waive control " +
			"for '" + getSubBusTableModel().getFilter() +"' ?",
			"Confirm Waive", 
			javax.swing.JOptionPane.YES_OPTION);
   
	if( confirm != javax.swing.JOptionPane.YES_OPTION )
		return;
		
	//lock down our table model
	synchronized( getSubBusTableModel() )
	{
		Multi multi = new Multi();
		
		for( int i = 0; i < getSubBusTableModel().getRowCount(); i++ )
		{
			SubBus sub = getSubBusTableModel().getRowAt(i);
			
			//only send the command if the state of the sub != our wanted command
			if( !sub.getWaiveControlFlag().booleanValue() )
				multi.getVector().add( 
					new CBCCommand(
						CBCCommand.WAIVE_SUB,
						sub.getCcId().intValue()) );
		}

		if( multi.getVector().size() > 0 )
			getConnectionWrapper().write( multi );
	}	
	
}

/**
 * Comment
 */
public void execute_UnwaiveSubs(java.awt.event.ActionEvent actionEvent) 
{
	String filter = getSubBusTableModel().getFilter();
   
	if( getSubBusTableModel().getRowCount() <= 0 )
		return;

	int confirm = javax.swing.JOptionPane.showConfirmDialog( this, 
			"Are you sure you want to Unwaive control " +
			"for '" + getSubBusTableModel().getFilter() +"' ?",
			"Confirm Unwaive", 
			javax.swing.JOptionPane.YES_OPTION);
   
	if( confirm != javax.swing.JOptionPane.YES_OPTION )
		return;
		
	//lock down our table model
	synchronized( getSubBusTableModel() )
	{
		Multi multi = new Multi();
		
		for( int i = 0; i < getSubBusTableModel().getRowCount(); i++ )
		{
			SubBus sub = getSubBusTableModel().getRowAt(i);
			
			//only send the command if the state of the sub != our wanted command
			if( sub.getWaiveControlFlag().booleanValue() )
				multi.getVector().add( 
					new CBCCommand(
						CBCCommand.UNWAIVE_SUB,
						sub.getCcId().intValue()) );
		}

		if( multi.getVector().size() > 0 )
			getConnectionWrapper().write( multi );
	}	
	
}

/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 12:44:52 PM)
 * @param e javax.swing.event.PopupMenuEvent
 */
private void feederPopUpMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
{
	com.cannontech.cbc.data.Feeder feeder = getFeederTableModel().getRowAt( getFeederTable().getSelectedRow() );

	if( feeder != null )
	{
		getFeederPopUp().setFeeder( feeder );

		//getFeederPopUp().setObservedCapBankRow( getCapBankTableModel().getObservableRow() );

		/*if( capBank.getDisableFlag().intValue() == com.cannontech.cbc.data.CapBankDevice.TRUE ||
			 ((capBank.getCurrentState().equalsIgnoreCase(CapBankTableModel.getStateNames()[CapBankTableModel.CLOSE_PENDING]) ||
				capBank.getCurrentState().equalsIgnoreCase(CapBankTableModel.getStateNames()[CapBankTableModel.OPEN_PENDING]) ) && 
				StrategyTableModel.isStrategyPending( getStrategyTableModel().getRow(getCapBankTableModel().getStrategyRowSelected())) ) )
		{
			getCapBankPopupMenu().setManualEntryEnabled(false);
		}
		else
			getCapBankPopupMenu().setManualEntryEnabled(true);
			*/
	}
}
/**
 * Return the JPopupMenu property value.
 * @return javax.swing.JPopupMenu
 */
private CapBankDevicePopUp getCapBankPopupMenu() 
{
	if (capBankPopupMenu == null) 
	{
		capBankPopupMenu = new CapBankDevicePopUp( getConnectionWrapper() );
		capBankPopupMenu.setName("CapBankTablePopupMenu");
	}
	
	return capBankPopupMenu;
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/00 3:56:44 PM)
 * @return javax.swing.JTable
 */
public javax.swing.JTable getCapBankTable() 
{
	if( capBankTable == null )
	{
		// Create the JTable
		capBankTable = new JTable()
		{
			public void clearSelection()
			{
				if( ReceiverMainPanel.this != null )
					prevSelectedCapBnkRow = NO_ROW_SELECTED;
				
				super.clearSelection();
			}
		};

		capBankTable.setName("CBC CapBank Table");		
		capBankTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
		capBankTable.setGridColor( capBankTable.getTableHeader().getBackground() );

		final SortTableModelWrapper s = new SortTableModelWrapper(getCapBankTableModel());

		java.awt.event.MouseAdapter m = new java.awt.event.MouseAdapter() 
		{
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				if( e.getClickCount() == 2 )
				{
					int vc = getCapBankTable().getColumnModel().getColumnIndexAtX( e.getX() );
					int mc = getCapBankTable().convertColumnIndexToModel( vc );

					java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame( getCapBankTable() );
					java.awt.Cursor original = null;

					if( owner != null )
					{	
						original = owner.getCursor();	
						owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
					}
		
					try
					{
						s.sort( mc );				
						getCapBankTable().repaint();
					}
					finally
					{
						if( owner != null )
							owner.setCursor( original );
					}
					
				}
				
			}};
	
		s.setTableHeaderListener( m, capBankTable.getTableHeader() );
		
		capBankTable.setModel( s );
		
		capBankTable.setDefaultRenderer( Object.class, new CapControlTableCellRenderer() );
		capBankTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	}
	
	return capBankTable;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 9:51:19 AM)
 * @return com.cannontech.cbc.gui.CapBankTableModel
 */
private CapBankTableModel getCapBankTableModel() 
{
	if( cabBankTableModel == null )
	{
		// Set up the schedule table
		cabBankTableModel = new CapBankTableModel();
		//getConnectionWrapper().addObserver(cabBankTableModel);
	}
	
	return cabBankTableModel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 9:51:19 AM)
 * @return com.cannontech.cbc.gui.CapBankTableModel
 */
private javax.swing.JScrollPane getCapBankTableScrollPane() 
{
	if( capBankScrollPane == null )
	{
		capBankScrollPane = new javax.swing.JScrollPane();
		capBankScrollPane.setName("CapBankTableScrollPane");
		capBankScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		capBankScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		capBankScrollPane.setBackground(java.awt.Color.white);
		capBankScrollPane.setForeground(java.awt.Color.lightGray);
		capBankScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));

		capBankScrollPane.setPreferredSize(new java.awt.Dimension(30, 30));
		
		getCapBankTableScrollPane().setViewportView(getCapBankTable());
	}
	
	return capBankScrollPane;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 9:43:45 AM)
 * @return com.cannontech.cbc.gui.CompositeJDeskTopPanel
 */
private CompositeJDeskTopPanel getCompJDeskTopPanel() 
{
	if( compJDeskTopPanel == null )
	{
		compJDeskTopPanel = new CompositeJDeskTopPanel(
								getSubBusScrollPane(), 
								getFeederTableScrollPane(), 
								getCapBankTableScrollPane() );

	}
	
	return compJDeskTopPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 9:29:43 AM)
 * @return javax.swing.JSplitPane
 */
private CompositeJSplitPane getCompSplitPane()
{
	if( compSplitPane == null )
	{
		compSplitPane = new CompositeJSplitPane( 
								getSubBusScrollPane(), 
								getFeederTableScrollPane(), 
								getCapBankTableScrollPane() );
	}
	
	return compSplitPane;
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getConfirmAreaButton() 
{
	if( confirmAreaButton == null )
	{
		confirmAreaButton = new JButton("Confirm Area");
		confirmAreaButton.setPreferredSize( new java.awt.Dimension(180,23) );
		confirmAreaButton.setEnabled(true);
		confirmAreaButton.setMnemonic('a');
		confirmAreaButton.setToolTipText("Confirm all Capbanks under the Sub with the selected area name");
	}

	return confirmAreaButton;
}
/**
 * Insert the method's description here.
 * Creation date: (8/22/00 2:26:19 PM)
 * @return com.cannontech.cbc.CBCClientConnection
 */
private com.cannontech.cbc.data.CBCClientConnection getConnectionWrapper() {
	return connectionWrapper;
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getCurrentViewButton() 
{
	if( currentViewButton == null )
	{
		currentViewButton = new JButton("Split Pane View");
		currentViewButton.setPreferredSize( new java.awt.Dimension(180,23) );
		currentViewButton.setEnabled(true);
		currentViewButton.setMnemonic('v');
		currentViewButton.setToolTipText("Change the way the tables are displayed");
	}

	return currentViewButton;
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getDisableAreaButton() 
{
	if( disableAreaButton == null )
	{
		disableAreaButton = new JButton("Disable Area");
		disableAreaButton.setActionCommand("Disable");
		disableAreaButton.setMnemonic('d');
		disableAreaButton.setPreferredSize( new java.awt.Dimension(80,23) );
		disableAreaButton.setToolTipText("Disable the SubBus's within the current selected area");
	}

	return disableAreaButton;
}

/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getWaiveAreaButton() 
{
	if( waiveAreaButton == null )
	{
		waiveAreaButton = new JButton("Waive Area");
		waiveAreaButton.setActionCommand("waiveAreaButton");
		waiveAreaButton.setMnemonic('w');
		waiveAreaButton.setPreferredSize( new java.awt.Dimension(80,23) );
		waiveAreaButton.setToolTipText("Waive control for the SubBus's within the current selected area");
	}

	return waiveAreaButton;
}

/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getUnwaiveAreaButton() 
{
	if( unwaiveAreaButton == null )
	{
		unwaiveAreaButton = new JButton("Unwaive Area");
		unwaiveAreaButton.setActionCommand("unwaiveAreaButton");
		unwaiveAreaButton.setMnemonic('u');
		unwaiveAreaButton.setPreferredSize( new java.awt.Dimension(80,23) );
		unwaiveAreaButton.setToolTipText("Unwaive control for the SubBus's within the current selected area");
	}

	return unwaiveAreaButton;
}

/**
 * Insert the method's description here.
 * Creation date: (8/4/00 9:01:12 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getEnableAreaButton() 
{
	if( enableAreaButton == null )
	{
		enableAreaButton = new JButton("Enable Area");
		enableAreaButton.setActionCommand("Enable");
		enableAreaButton.setMnemonic('e');
		enableAreaButton.setPreferredSize( new java.awt.Dimension(80,23) );
		enableAreaButton.setToolTipText("Enable the SubBus's within the current selected area");
	}

	return enableAreaButton;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 10:14:31 AM)
 * @return com.cannontech.cbc.popupmenu.FeederPopupMenu
 */
private com.cannontech.cbc.popupmenu.FeederPopUp getFeederPopUp() 
{
	if( feederPopupMenu == null )
	{
		feederPopupMenu = new com.cannontech.cbc.popupmenu.FeederPopUp( getConnectionWrapper() );
		feederPopupMenu.setName("FeederPopUpMenu");
	}
	
	return feederPopupMenu;
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/00 3:56:44 PM)
 * @return javax.swing.JTable
 */
public javax.swing.JTable getFeederTable() 
{
	if( feederTable == null )
	{
		// Create the JTable
		feederTable = new JTable()
		{
			public void clearSelection()
			{
				if( ReceiverMainPanel.this != null )
				{
					prevSelectedFeederRow = NO_ROW_SELECTED;
					prevSelectedCapBnkRow = NO_ROW_SELECTED;
				}

				super.clearSelection();
			}
		};
		
		feederTable.setName("FeederTable");		
		feederTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
		feederTable.setGridColor( feederTable.getTableHeader().getBackground() );

		final SortTableModelWrapper s = new SortTableModelWrapper(getFeederTableModel());

		java.awt.event.MouseAdapter m = new java.awt.event.MouseAdapter() 
		{
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				if( e.getClickCount() == 2 )
				{
					getFeederTable().clearSelection();

					int vc = getFeederTable().getColumnModel().getColumnIndexAtX( e.getX() );
					int mc = getFeederTable().convertColumnIndexToModel( vc );

					java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame( getFeederTable() );
					java.awt.Cursor original = null;

					if( owner != null )
					{	
						original = owner.getCursor();	
						owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
					}
		
					try
					{
						s.sort( mc );				
						getFeederTable().repaint();
					}
					finally
					{
						if( owner != null )
							owner.setCursor( original );
					}
					
				}
				
			}};
	
		s.setTableHeaderListener( m, feederTable.getTableHeader() );		
		feederTable.setModel( s );

		feederTable.setDefaultRenderer( Object.class, new CapControlTableCellRenderer() );
		feederTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		//feederTable.setAutoCreateColumnsFromModel( true );
		//feederTable.createDefaultColumnsFromModel();
	}
	
	return feederTable;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 9:51:19 AM)
 * @return com.cannontech.cbc.gui.FeederTableModel
 */
private FeederTableModel getFeederTableModel() 
{
	if( feederTableModel == null )
	{
		feederTableModel = new FeederTableModel();
	}
	
	return feederTableModel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 9:51:19 AM)
 * @return com.cannontech.cbc.gui.CapBankTableModel
 */
private javax.swing.JScrollPane getFeederTableScrollPane() 
{
	if( feederScrollPane == null )
	{
		feederScrollPane = new javax.swing.JScrollPane();
		feederScrollPane.setName("FeederScrollPane");
		feederScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		feederScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		feederScrollPane.setBackground(java.awt.Color.white);
		feederScrollPane.setForeground(java.awt.Color.lightGray);
		feederScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));
		
		feederScrollPane.setPreferredSize(new java.awt.Dimension(30, 30));
		
		getFeederTableScrollPane().setViewportView( getFeederTable() );
	}
	
	return feederScrollPane;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 11:31:11 AM)
 * @return javax.swing.border.TitledBorder
 */
private javax.swing.border.TitledBorder getMainTitleBorder() 
{
	return (javax.swing.border.TitledBorder)getBorder();
}
/**
 * This method was created in VisualAge.
 * @return SubBus
 */
protected SubBus getSelectedSubBus() 
{
	ListSelectionModel lsm = getSubBusTable().getSelectionModel();
	
	//only one should be selected
	int selectedRow = lsm.getMinSelectionIndex();
	
	if( selectedRow < 0 )
		return null;
		
	SubBus selected = getSubBusTableModel().getRowAt(selectedRow);

	return selected;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 10:14:31 AM)
 * @return com.cannontech.cbc.popupmenu.subBusPopupMenu
 */
private com.cannontech.cbc.popupmenu.SubBusPopUp getSubBusPopUp() 
{
	if( subBusPopupMenu == null )
	{
		subBusPopupMenu = new com.cannontech.cbc.popupmenu.SubBusPopUp( getConnectionWrapper() );
		subBusPopupMenu.setName("SubBusPopUpMenu");
	}
	
	return subBusPopupMenu;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 9:51:19 AM)
 */
private javax.swing.JScrollPane getSubBusScrollPane() 
{
	if( subBusScrollPane == null )
	{
		subBusScrollPane = new javax.swing.JScrollPane();
		subBusScrollPane.setName("SubBusTableScrollPane");
		subBusScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		subBusScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		subBusScrollPane.setBackground(java.awt.Color.white);
		subBusScrollPane.setForeground(java.awt.Color.lightGray);
		subBusScrollPane.setMinimumSize(new java.awt.Dimension(0, 0));

		subBusScrollPane.setPreferredSize(new java.awt.Dimension(200, 200));

		getSubBusScrollPane().setViewportView( getSubBusTable() );
	}
	
	return subBusScrollPane;
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/00 3:56:44 PM)
 * @return javax.swing.JTable
 */
public javax.swing.JTable getSubBusTable() 
{
	if( subBusTable == null )
	{
		// Create the JTable
		subBusTable = new JTable()
		{
			public void clearSelection()
			{
				if( ReceiverMainPanel.this != null )
				{
					ReceiverMainPanel.this.prevSelectedFeederRow = NO_ROW_SELECTED;
					ReceiverMainPanel.this.prevSelectedCapBnkRow = NO_ROW_SELECTED;
				}

				super.clearSelection();
			}
		};
		
		subBusTable.setName("CBC SubBus Table");
		subBusTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
		subBusTable.setGridColor( subBusTable.getTableHeader().getBackground() );

		final SortTableModelWrapper s = new SortTableModelWrapper(getSubBusTableModel());

		java.awt.event.MouseAdapter m = new java.awt.event.MouseAdapter() 
		{
			public void mouseClicked(java.awt.event.MouseEvent e)
			{
				if( e.getClickCount() == 2 )
				{
					int vc = getSubBusTable().getColumnModel().getColumnIndexAtX( e.getX() );
					int mc = getSubBusTable().convertColumnIndexToModel( vc );

					java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame( getSubBusTable() );
					java.awt.Cursor original = null;

					if( owner != null )
					{	
						original = owner.getCursor();	
						owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
					}
		
					try
					{
						//store the last selected rows id!
						SubBus sub = getSubBusTableModel().getRowAt(prevSelectedSubRow);

                  s.sort( mc );                  

						//Set the last selected row selected
						if( sub != null )
						{
							int i = 0;
							for( i = 0 ; i < getSubBusTableModel().getRowCount(); i++ )
								if( getSubBusTableModel().getRowAt(i).equals(sub) )
								{
									getSubBusTable().getSelectionModel().setSelectionInterval( i, i );
									prevSelectedSubRow = i;
									break;
								}

							//if i is the last row, we need to add some more space for it
							i = (i == getSubBusTableModel().getRowCount()-1 ? i+1: i);
							getSubBusTable().scrollRectToVisible( 
									new Rectangle( 
										0, 
										(getSubBusTable().getRowHeight() * i),
										0, 0 ) );								
						}
						
						getSubBusTable().repaint();
					}
					finally
					{
						if( owner != null )
							owner.setCursor( original );
					}
					
				}
				
			}};
	
		s.setTableHeaderListener( m, subBusTable.getTableHeader() );
		subBusTable.setModel( s );
		
		subBusTable.setDefaultRenderer( Object.class, new CapControlTableCellRenderer() );
		subBusTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
	}
	
	return subBusTable;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 9:41:48 AM)
 * @return com.cannontech.cbc.gui.SubBusTableModel
 */
public SubBusTableModel getSubBusTableModel() 
{
	if( subBusTableModel == null )
	{
		// Set up the subbus table
		subBusTableModel = new SubBusTableModel();
		subBusTableModel.setConnection( getConnectionWrapper() );
		getConnectionWrapper().addMessageListener( subBusTableModel );		
	}

	return subBusTableModel;
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 9:34:22 AM)
 * @return int
 */
private int getViewMode() {
	return viewMode;
}
/**
 * Insert the method's description here.
 * Creation date: (12/6/2001 4:16:15 PM)
 * @param event javax.swing.event.ListSelectionEvent
 */
private void handleFeederTableSelection( ListSelectionEvent event )
{
	ListSelectionModel lsm = (ListSelectionModel) event.getSource();

		
	//only one row should be selected
	int selectedRow = lsm.getMinSelectionIndex();

	if( lsm.isSelectionEmpty() )
	{
		handleSubBusTableSelection( event );
		return;
	}
	else
	{
		SubBus selected = getSelectedSubBus();
		prevSelectedFeederRow = selectedRow;

		getCapBankTableModel().setFeederRowSelected( selectedRow );

		getCapBankTableModel().setCapBankDevices( 
			((com.cannontech.cbc.data.Feeder)selected.getCcFeeders().get(selectedRow)).getCcCapBanks() );
		
		getCapBankTable().revalidate();
		getCapBankTable().repaint();
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (12/6/2001 4:16:15 PM)
 * @param event javax.swing.event.ListSelectionEvent
 */
private void handleSubBusTableSelection(ListSelectionEvent event)
{
	//ListSelectionModel lsm = (ListSelectionModel) event.getSource();
	getFeederTable().clearSelection();
	getCapBankTable().clearSelection();

	getCapBankTableModel().setFeederRowSelected( prevSelectedFeederRow );
		
	//only one should be selected
	int selectedRow = getSubBusTable().getSelectedRow();

	if( selectedRow < 0 )
		return;
	else
	{
		SubBus selected = getSelectedSubBus();
		prevSelectedSubRow = selectedRow;
		//lastSelectedRow = selectedRow;

		//set all the banks in the model to the selected SubBuses banks	
		java.util.Vector banks = new java.util.Vector(50);
		for( int i = 0; i < selected.getCcFeeders().size(); i++ )
			banks.addAll( ((com.cannontech.cbc.data.Feeder)selected.getCcFeeders().get(i)).getCcCapBanks() );

		getCapBankTableModel().setCapBankDevices( banks );
		getCapBankTableModel().setSubBusRowSelected(selectedRow);
		
		//set all the feeders in the model to the selected SubBuses feeders
		getFeederTableModel().setCurrentSubBus( selected );
		getFeederTableModel().setSubBusRowSelected(selectedRow);

		
		getCapBankTable().revalidate();
		getCapBankTable().repaint();
		
		if( selected != null )
			synchTableAndButtons( selected );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 9:33:17 AM)
 */
private void initConnections() 
{
	// init the popup box connections for the CapBankTable
	java.awt.event.MouseListener cbList = new com.cannontech.clientutils.popup.PopUpMenuShower( getCapBankPopupMenu() );
	java.awt.event.MouseListener feedList = new com.cannontech.clientutils.popup.PopUpMenuShower( getFeederPopUp() );
	getCapBankTable().addMouseListener(cbList);
	getFeederTable().addMouseListener(feedList);
	getCapBankPopupMenu().addPopupMenuListener( this );
	getFeederPopUp().addPopupMenuListener( this );

	getSubBusTableModel().addTableModelListener( getCapBankPopupMenu() );

	// init the popup box connections for the SubBusTable
	java.awt.event.MouseListener subListener = new com.cannontech.clientutils.popup.PopUpMenuShower( getSubBusPopUp() );
	getSubBusTable().addMouseListener( subListener );
	getSubBusPopUp().addPopupMenuListener( this );


	// Add our listeners to our button in the ToolBar
	getCurrentViewButton().addActionListener(this);
	getEnableAreaButton().addActionListener(this);
	getDisableAreaButton().addActionListener(this);
	getConfirmAreaButton().addActionListener(this);
	getWaiveAreaButton().addActionListener(this);
	getUnwaiveAreaButton().addActionListener(this);
	

	// add listeners to the CapBankTable & FeederTable
	getCapBankTable().addMouseListener(this);
	getFeederTable().addMouseListener(this);

	// Allow the CapBankTableModel & FeederTableModel & this to listen 
	//    to the StrategyTableModel
	//getFeederTableModel().addTableModelListener( getCapBankTableModel() );
	getSubBusTableModel().addTableModelListener( getFeederTableModel() );
	getSubBusTableModel().addTableModelListener( getCapBankTableModel() );
	getSubBusTableModel().addTableModelListener( this );
	
	//Listen for list selection events from the table so we can maintain 
	//the last selected schedule between updates
	getSubBusTable().getSelectionModel().addListSelectionListener(this);
	getCapBankTable().getSelectionModel().addListSelectionListener(this);
	getFeederTable().getSelectionModel().addListSelectionListener(this);
	//ListSelectionModel lsModel = getStrategyTable().getSelectionModel();
	//lsModel.addListSelectionListener( this );

	//Listen for mouse click events from the table
	getSubBusTable().addMouseListener( this );
}
/**
 * Insert the method's description here.
 * Creation date: (8/24/00 3:07:26 PM)
 */
public void initDividerPosition() 
{
	//if( getViewMode() == VIEW_SPLIT_PANE )
		//getCompSplitPane().initDividerPosition();
}
/**
 * This method was created in VisualAge.
 */
private void initialize()
{
	initConnections();
	setLayout( new java.awt.BorderLayout() );

	createCenterComponent();

	// 'Tool bar' creation
	createButtonToolBar();

	createMessagePanel();	

	//Observer connection state changes
	getConnectionWrapper().addObserver(this);
	update( getConnectionWrapper(), getConnectionWrapper() );
	
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseClicked(MouseEvent event)
{
	//If there was a double click scroll the bottom table up
	if (event.getClickCount() == 2)
	{
		if( getViewMode() == VIEW_SPLIT_PANE )
		{
			JTable table = null;
			javax.swing.JSplitPane split = null;
			if( event.getSource() == getSubBusTable() )
			{
				split = getCompSplitPane();
				table = getSubBusTable();
			}
			else if( event.getSource() == getFeederTable() )
			{
				split = getCompSplitPane().getJSplitPaneInner();
				table = getFeederTable();
			}

			if( split != null && table != null )
			{	
				java.awt.Point pt = new java.awt.Point( 0, table.getRowHeight() * table.getSelectedRow() );
				int rowHeight = table.getRowHeight()-1;
				int loc = rowHeight + table.getTableHeader().getHeight() + split.getDividerSize();
				split.setDividerLocation( loc );


				if( event.getSource() == getSubBusTable() )
					getSubBusScrollPane().getViewport().setViewPosition( pt );
				else
					getFeederTableScrollPane().getViewport().setViewPosition( pt );				
			}
		}
	
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
	if( event.getSource() == ReceiverMainPanel.this.getCapBankTable() )
	{
		int rowLocation = getCapBankTable().rowAtPoint( event.getPoint() );
		
		getCapBankTable().getSelectionModel().setSelectionInterval(
				 		rowLocation, rowLocation );
	}
	else if( event.getSource() == ReceiverMainPanel.this.getFeederTable() )
	{
		int rowLocation = getFeederTable().rowAtPoint( event.getPoint() );

		if( rowLocation == prevSelectedFeederRow
			 && event.isControlDown() )
		{
			getFeederTable().clearSelection();	
			getCapBankTableModel().setFeederRowSelected( prevSelectedFeederRow );
			getCapBankTable().clearSelection();
		}
		else
			getFeederTable().getSelectionModel().setSelectionInterval(
				 		rowLocation, rowLocation );

	}
	else if( event.getSource() == ReceiverMainPanel.this.getSubBusTable() )
	{
		int rowLocation = getSubBusTable().rowAtPoint( event.getPoint() );
		
		getSubBusTable().getSelectionModel().setSelectionInterval(
				 		rowLocation, rowLocation );
		
		if( getMainTitleBorder() != null )
		{
			getMainTitleBorder().setTitle("Selected SubBus: " 
					+ getSubBusTableModel().getRowAt(rowLocation) );

			repaint();
		}

	}
	
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseReleased(MouseEvent e)
{
	if( e.getSource() == ReceiverMainPanel.this.getCapBankTable() 
		 || e.getSource() == ReceiverMainPanel.this.getFeederTable() )
	{
		JTable table = (JTable)e.getSource();
		
		if( e.isPopupTrigger() )
		{
			int rowLocation = table.rowAtPoint( e.getPoint() );
			
			table.getSelectionModel().setSelectionInterval(
					 		rowLocation, rowLocation );

			//show the correct PopUp menu based on the current table
			if( e.getSource() == ReceiverMainPanel.this.getCapBankTable() )
			{
				getCapBankPopupMenu().show( e.getComponent(), e.getX(), e.getY() );
			}
			else if( e.getSource() == ReceiverMainPanel.this.getFeederTable() )
			{
				getFeederPopUp().show( e.getComponent(), e.getX(), e.getY() );
			}

		}
		
	}
	
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
	
	if( e.getSource() == ReceiverMainPanel.this.getCapBankPopupMenu() )
		capBankPopUpMenuWillBecomeVisible( e );
	
	if( e.getSource() == ReceiverMainPanel.this.getFeederPopUp() )
		feederPopUpMenuWillBecomeVisible( e );

	if( e.getSource() == ReceiverMainPanel.this.getSubBusPopUp() )
		subBusPopUpMenuWillBecomeVisible( e );
		
		
}
/**
 * Insert the method's description here.
 * Creation date: (2/1/2001 12:59:02 PM)
 * @param soundToggle boolean
 */
public void setCapBankTableModelSound( boolean muted  ) 
{
	getCapBankTableModel().setMuted( muted );
}

/**
 * Insert the method's description here.
 * Creation date: (2/1/2001 12:59:02 PM)
 * @param soundToggle boolean
 */
public void silenceAlarms() 
{
	getCapBankTableModel().silenceAlarms();
}

/**
 * Insert the method's description here.
 * Creation date: (8/22/00 2:26:19 PM)
 * @param newConnectionWrapper com.cannontech.cbc.CBCClientConnection
 */
public void setConnectionWrapper(com.cannontech.cbc.data.CBCClientConnection newConnectionWrapper) 
{
	connectionWrapper = newConnectionWrapper;
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 12:44:52 PM)
 * @param e javax.swing.event.PopupMenuEvent
 */
private void subBusPopUpMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
{
	getSubBusPopUp().setSubBus(
			getSubBusTableModel().getRowAt(getSubBusTable().getSelectedRow()) );

}
/**
 * This method was created in VisualAge.
 * @param selected SubBus
 */
protected void synchTableAndButtons(SubBus selected) 
{	   
   String filter = getSubBusTableModel().getFilter();

/*
   getConfirmAreaButton().setEnabled( !filter.equalsIgnoreCase(SubBusTableModel.ALL_FILTER) );
   getEnableAreaButton().setEnabled( !filter.equalsIgnoreCase(SubBusTableModel.ALL_FILTER) );
   getDisableAreaButton().setEnabled( !filter.equalsIgnoreCase(SubBusTableModel.ALL_FILTER) );
	getWaiveAreaButton().setEnabled( !filter.equalsIgnoreCase(SubBusTableModel.ALL_FILTER) );
	getUnwaiveAreaButton().setEnabled( !filter.equalsIgnoreCase(SubBusTableModel.ALL_FILTER) );
*/

}

/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(TableModelEvent event ) 
{
	if( event instanceof CBCGenericTableModelEvent 
		 && ((CBCGenericTableModelEvent)event).getChangeid() 
		 		== CBCGenericTableModelEvent.SUBBUS_FILTER_CHANGE )	
	{
		getSubBusTable().clearSelection();
		//prevSelectedSubRow = NO_ROW_SELECTED;

		getCapBankTable().clearSelection();
		getFeederTable().clearSelection();
	}
	else
	{
		//Attempt to re-selected the last thing that was selected
		if( prevSelectedSubRow >= 0 
			 && prevSelectedSubRow < getSubBusTableModel().getRowCount() )
		{
			getSubBusTable().getSelectionModel().setSelectionInterval( 
				prevSelectedSubRow, prevSelectedSubRow );

			getFeederTable().getSelectionModel().setSelectionInterval(
				prevSelectedFeederRow, prevSelectedFeederRow);
			
			getCapBankTable().getSelectionModel().setSelectionInterval(
				prevSelectedCapBnkRow, prevSelectedCapBnkRow);
		}
		else
		{
			prevSelectedSubRow = NO_ROW_SELECTED;
			getSubBusTable().clearSelection();
		}

	}


	SubBus selected = getSelectedSubBus();
	synchTableAndButtons( selected );

	//revalidate();
	getSubBusTable().repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:36:12 AM)
 * @param o java.util.Observable
 * @param val java.lang.Object
 */
public void update(java.util.Observable o, Object val)
{
	//Should be an instance of com.cannontech.cbc.data.CBCClientConnection
	//notifying us of a change in the connections state
	com.cannontech.cbc.data.CBCClientConnection conn = (com.cannontech.cbc.data.CBCClientConnection)o;

	boolean validConn = conn.isValid();
	
	//Clear the list table of schedules if the connection isn't good
	if ( !validConn && (lastConnectionStatus || startingUp) )
	{
		getSubBusTableModel().clear();		
		messagePanel.messageEvent(new com.cannontech.common.util.MessageEvent(this, 
			"No connection to CBC server", com.cannontech.common.util.MessageEvent.ERROR_MESSAGE));
	}
	else if( validConn && (!lastConnectionStatus || startingUp) )
	{
		messagePanel.messageEvent(new com.cannontech.common.util.MessageEvent(this, 
			"Connection to CBC server established", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE));
	}	
	
	final java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	
	if (f != null)
	{
		String title = TITLE;
		
		if (validConn)
			title += "   [Connected to CBCServer@" + conn.getHost() + ":" + conn.getPort() + "]";
		else
			title += "   [Not Connected to CBCServer]";
		
		f.setTitle( title );
	}

	lastConnectionStatus = validConn;
	startingUp = false;
}
/**
 * This method handles ListSelectionEvents generated by the strategy
 * JTable and the CapBankDeviceTable.
 * @param event ListSelectionEvent
 */
public void valueChanged(ListSelectionEvent event) 
{
	if( !event.getValueIsAdjusting() )  // make sure we have the last event in a
	{												// sequence of events.		
		if( event.getSource() == getSubBusTable().getSelectionModel() )
		{
			handleSubBusTableSelection( event );
		}
		else if ( event.getSource() == getFeederTable().getSelectionModel() )
		{
			//prevSelectedFeederRow = getFeederTable().getSelectedRow();
			handleFeederTableSelection( event );
		}
		else if ( event.getSource() == getCapBankTable().getSelectionModel() )
		{
			prevSelectedCapBnkRow = getCapBankTable().getSelectedRow();
		}
			
	}
	
}
}
