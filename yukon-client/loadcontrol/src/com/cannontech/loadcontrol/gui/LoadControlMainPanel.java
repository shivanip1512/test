package com.cannontech.loadcontrol.gui;

/**
 * Insert the type's description here.
 * Creation date: (9/19/00 10:13:05 AM)
 * @author: 
 */
import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.cannontech.common.gui.panel.CompositeJSplitPane;
import com.cannontech.common.gui.util.JDialogWait;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.loadcontrol.LoadControlClientConnection;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMGroupBase;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.datamodels.ControlAreaTableModel;
import com.cannontech.loadcontrol.datamodels.ControlHistoryTableModel;
import com.cannontech.loadcontrol.datamodels.FilteredControlAreaTableModel;
import com.cannontech.loadcontrol.datamodels.GroupTableModel;
import com.cannontech.loadcontrol.datamodels.IControlAreaTableModel;
import com.cannontech.loadcontrol.datamodels.IProgramTableModel;
import com.cannontech.loadcontrol.datamodels.ProgramTableModel;
import com.cannontech.loadcontrol.displays.*;
import com.cannontech.loadcontrol.events.LCChangeEvent;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.loadcontrol.popup.ControlAreaPopUpMenu;
import com.cannontech.loadcontrol.popup.GroupPopUpMenu;
import com.cannontech.loadcontrol.popup.ProgramPopUpMenu;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.tdc.observe.ObservableJPopupMenu;

public class LoadControlMainPanel extends javax.swing.JPanel implements ButtonBarPanelListener, 
		com.cannontech.tdc.SpecialTDCChild, java.awt.event.MouseListener, java.util.Observer, 
		javax.swing.event.ListSelectionListener, javax.swing.event.PopupMenuListener, javax.swing.event.TableModelListener,
		IControlAreaListener 
{
	//an int read in as a CParm used to turn on/off features
	private static int userRightsInt = 0;
	
	//All the possible TableModels the BottomTable can have
//	private ProgramTableModel programTableModel = null;
	private GroupTableModel groupTableModel = null;
	
	private javax.swing.JComboBox comboBox = null;
	private ControlAreaActionListener controlAreaActionListener = null;
	private ControlAreaPopUpMenu controlAreaPopUpMenu = null;
	private GroupPopUpMenu groupPopUpMenu = null;
	private ProgramPopUpMenu programPopUpMenu = null;
	//private CurtailPopUpMenu curtailCustomerPopUpMenu = null;
	private com.cannontech.common.gui.util.MessagePanel ivjMessagePanel = null;
	private CompositeJSplitPane compSplitPane = null;
	private ButtonBarPanel ivjButtonBarPanel = null;
	// Flag for connection to macs server
	private boolean lastConnectionStatus = false;
	
	private javax.swing.JTable ivjJTableControlArea = null;
	private javax.swing.JTable ivjJTableProgram = null;
	private javax.swing.JTable ivjJTableGroup = null;
	

	public final static String LOAD_MANAGEMENT_NAME = "Load Management";
	public final static String LOADCONTROL_VERSION = 
			com.cannontech.common.version.VersionTools.getYUKON_VERSION();

	private javax.swing.JScrollPane ivjJScrollPaneControlArea = null;
	private javax.swing.JScrollPane ivjJScrollPaneProgramTable = null;
	private javax.swing.JScrollPane ivjJScrollPaneGroupTable = null;

	//a class used to handle control area events
	private LCAreaEventHandler lcAreaEventHandler = null;

	private final EmptyTableModel EMPTY_TABLE_MODEL = 
			new EmptyTableModel();

	
	
	
	class EmptyTableModel implements TableModel
	{
		public int getRowCount() { return 0; }
		public int getColumnCount()  { return 0; }
		public String getColumnName(int columnIndex) { return null; }
		public Class getColumnClass(int columnIndex) { return Object.class; }
		public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }
		public Object getValueAt(int rowIndex, int columnIndex) { return null; }
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
		public void addTableModelListener(TableModelListener l) {}
		public void removeTableModelListener(TableModelListener l) {}		
	}



/**
 * LoadControlMainPanel constructor comment.
 */
public LoadControlMainPanel() {
	super();
	//initialize();
}

public void initChild()
{
	initialize();
}

private LCAreaEventHandler getLCAreaEventHandler()
{
	if( lcAreaEventHandler == null )
		lcAreaEventHandler = new LCAreaEventHandler();
	
	return lcAreaEventHandler;
}

public void setCurrentDisplay( LCDisplayItem display_ )
{
	if( display_ == null )
		return;

	//clear out the current model and selected control area
	getJTableControlArea().getSelectionModel().clearSelection();
	getControlAreaTableModel().clear();

	//decide which tables model we should change
	getJTableControlArea().setModel( display_.getLocalTableModels()[LCDisplayItem.TYPE_CONTROL_AREA] );
	getJTableProgram().setModel( display_.getLocalTableModels()[LCDisplayItem.TYPE_PROGRAM] );
	getJTableGroup().setModel( display_.getLocalTableModels()[LCDisplayItem.TYPE_GROUP] );


	getCompSplitPane().getJSplitPaneInner().getBottomComponent().setVisible(
			getJTableGroup().getModel() != EMPTY_TABLE_MODEL );


	//tell the server we need all the ControlAreas sent to us
	executeRefreshButton();
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
public void addActionListenerToJComponent( javax.swing.JComponent component )
{
	if( component instanceof javax.swing.JComboBox )
	{
		comboBox = (javax.swing.JComboBox)component;
		getComboBox().removeAllItems();

		// ALL getter for TableModels return the initial table model
				
		//ALL Display
		LCDisplayItem d1 = 
			new LCDisplayItem( ControlAreaActionListener.SEL_ALL_CONTROL_AREAS);
		d1.setLocalTableModels(
			new TableModel[] { 
					getControlAreaTableModel(),
					getProgramTableModel(),
					getGroupTableModel() } );

		LCDisplayItem d2 = 
			new LCDisplayItem( ControlAreaActionListener.SEL_ACTIVE_AREAS );
		d2.setLocalTableModels(
			new TableModel[] { 
					new FilteredControlAreaTableModel(
						new int[] {LMControlArea.STATE_ACTIVE,
										LMControlArea.STATE_FULLY_ACTIVE,
										LMControlArea.STATE_MANUAL_ACTIVE},
						getControlAreaTableModel().getTableModelListeners()),
					getProgramTableModel(),
					getGroupTableModel() } );


		LCDisplayItem d3 =
			new LCDisplayItem( ControlAreaActionListener.SEL_INACTIVE_AREAS );
		d3.setLocalTableModels(
			new TableModel[] { 
					new FilteredControlAreaTableModel( 
						new int[] {LMControlArea.STATE_INACTIVE},
						getControlAreaTableModel().getTableModelListeners()),
					getProgramTableModel(),
					getGroupTableModel() } );
			

		LCDisplayItem d4 =
			new LCDisplayItem( ControlAreaActionListener.SEL_CNTRL_PT_HISTORY );				
		d4.setLocalTableModels(
			new TableModel[] { 
					getControlAreaTableModel(),
					new ControlHistoryTableModel(),
					EMPTY_TABLE_MODEL } );


//NO SUCH THING AS A SCHEDULED AREA!!
/* 
		LCDisplayItem d5 = 
			new LCDisplayItem( ControlAreaActionListener.SEL_SCHEDULED_AREAS );
		d5.setLocalTableModels(
			new TableModel[] { 
					new FilteredControlAreaTableModel(
						new int[] {LMControlArea.STATE_SCHEDULED,
										LMControlArea.STATE_CNTRL_ATTEMPT},
						getControlAreaTableModel().getTableModelListeners()),
					getProgramTableModel(),
					getGroupTableModel() } );
*/


		getComboBox().addItem( d1 );		
		getComboBox().addItem( d2 );
		getComboBox().addItem( d3 );
		getComboBox().addItem( d4 );


		getComboBox().addActionListener( getControlAreaActionListener() );
	}
	
}

public boolean needsComboIniting()
{
	return false;
}

/**
 * Comment
 *	This action will do the DisableAll and ViewRevisions 
 *   based on the current view.
 */
public void buttonBarPanel_JButtonDisableAllAction_actionPerformed(java.util.EventObject newEvent) 
{
	
	int res = JOptionPane.showConfirmDialog( this, 
						"Are you sure you want to DISABLE ALL control areas?", 
						"Disable Confirmation", 
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );

	if( res == JOptionPane.OK_OPTION )
	{
		for(int i = 0; i < getControlAreaTableModel().getRowCount(); i++ )
		{
			if( !getControlAreaTableModel().getRowAt(i).getDisableFlag().booleanValue() )
			{
				LoadControlClientConnection.getInstance().write(
					new LMCommand( LMCommand.DISABLE_CONTROL_AREA,
						 				getControlAreaTableModel().getRowAt(i).getYukonID().intValue(),
						 				0, 0.0) );
			}
		}
	
		getMessagePanel().messageEvent( new com.cannontech.common.util.MessageEvent(this, 
			"All control areas " +
			"have been manually DISABLED.", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE) );
	}


}
/**
 * Comment
 *	This action will do the EnableAll and CreateOffer
 *   based on the current view.
 */
public void buttonBarPanel_JButtonEnableAllAction_actionPerformed(java.util.EventObject newEvent) 
{

	int res = JOptionPane.showConfirmDialog( this,
						"Are you sure you want to ENABLE ALL control areas?", 
						"Enable Confirmation", 
						JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );

	if( res == JOptionPane.OK_OPTION )
	{
		for(int i = 0; i < getControlAreaTableModel().getRowCount(); i++ )
		{
			if( getControlAreaTableModel().getRowAt(i).getDisableFlag().booleanValue() )
			{
				LoadControlClientConnection.getInstance().write(
					new LMCommand( LMCommand.ENABLE_CONTROL_AREA,
						 				getControlAreaTableModel().getRowAt(i).getYukonID().intValue(),
						 				0, 0.0) );
			}
		}
	
		getMessagePanel().messageEvent( new com.cannontech.common.util.MessageEvent(this, 
			"All control areas " + 
			"have been manually ENABLED.", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE) );
	}

}

/**
 * Comment
 */
public void buttonBarPanel_JButtonEnableControlAreaAction_actionPerformed(java.util.EventObject newEvent) 
{
	if( getSelectedControlArea() != null )
	{
		if( getSelectedControlArea().getDisableFlag().booleanValue() )
		{
			int res = JOptionPane.showConfirmDialog( this,
								"Are you sure you want to ENABLE the selected control area?", 
								"Enable Confirmation", 
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );

			if( res == JOptionPane.OK_OPTION )
			{
				LoadControlClientConnection.getInstance().write(
					new LMCommand( LMCommand.ENABLE_CONTROL_AREA,
						 				getSelectedControlArea().getYukonID().intValue(),
						 				0, 0.0) );
	
				getMessagePanel().messageEvent( new com.cannontech.common.util.MessageEvent(this, 
					"Control area '" + getSelectedControlArea().getYukonName() + 
					"' has been manually ENABLED.", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE) );		
			}
		}
		else
		{
			int res = JOptionPane.showConfirmDialog( this,
								"Are you sure you want to DISABLE the selected control area?", 
								"Disable Confirmation", 
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE );

			if( res == JOptionPane.OK_OPTION )
			{
				LoadControlClientConnection.getInstance().write(
					new LMCommand( LMCommand.DISABLE_CONTROL_AREA,
						 				getSelectedControlArea().getYukonID().intValue(),
						 				0, 0.0) );
	
				getMessagePanel().messageEvent( new com.cannontech.common.util.MessageEvent(this, 
					"Control area '" + getSelectedControlArea().getYukonName() + 
					"' has been manually DISABLED.", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE) );					 				
			}
		}
		
	}
	
	return;
}

/**
 * Comment
 */
public void jTableControlArea_MouseClicked(java.awt.event.MouseEvent event) 
{
	//If there was a double click open a new edit window
	if (event.getClickCount() == 2)
	{
		if( event.isShiftDown() )
			showDebugInfo( getControlAreaTableModel().getRowAt(getJTableControlArea().getSelectedRow()) );
		else
		{
			int rowHeight = getJTableControlArea().getRowHeight()-1;
			int loc = rowHeight + getJTableControlArea().getTableHeader().getHeight() + getCompSplitPane().getDividerSize();
			getCompSplitPane().setDividerLocation( loc );

			getJScrollPaneControlArea().getViewport().setViewPosition( 
				new java.awt.Point( 0, getJTableControlArea().getRowHeight() * getJTableControlArea().getSelectedRow() ) );
		}

	}
	
	return;
}

public void jTableGroup_MouseClicked(java.awt.event.MouseEvent event) 
{
	//If there was a double click open a new edit window
	if (event.getClickCount() == 2)
	{
		if( event.isShiftDown() )
			showDebugInfo( getGroupTableModel().getRowAt(getJTableGroup().getSelectedRow()) );
	}
}

/**
 * Comment
 */
public void jTableGroup_MousePressed(java.awt.event.MouseEvent event) 
{
	if( event.getSource() == LoadControlMainPanel.this.getJTableGroup() )
	{
		int rowLocation = getJTableGroup().rowAtPoint( event.getPoint() );
			
		getJTableGroup().getSelectionModel().setSelectionInterval(
				 		rowLocation, rowLocation );
	}

	return;
}
	
/**
 * Comment
 */
public void jTableControlArea_MousePressed(java.awt.event.MouseEvent event) 
{
	if( event.getSource() == LoadControlMainPanel.this.getJTableControlArea() )
	{
		int rowLocation = getJTableControlArea().rowAtPoint( event.getPoint() );
		
		getJTableControlArea().getSelectionModel().setSelectionInterval(
				 		rowLocation, rowLocation );
	}

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 4:21:41 PM)
 * @return java.lang.String[]
 */
private String[] createPrintableText() 
{
	int columnCount = getJTableControlArea().getColumnCount();
	int rowCount = getJTableControlArea().getRowCount();																
	int cellCount = ( rowCount * columnCount + columnCount);
	
	String[] tableData = new String[ cellCount ];

	int j = 0;
	for( j = 0; j < columnCount; j++ )
	{
		tableData[ j ] = getProgramTableModel().getColumnName( j );
	}
	
	for( int i = 0; i < rowCount; i++ )
	{
		for( int k = 0; k < columnCount; k++ )
		{
			/*if( getJTableBottom().getModel().getValueAt( i, k ).equals("") )
				break;  // blank row
			else*/
				tableData[ j++ ] = getProgramTableModel().getValueAt( i, k ).toString();
		}
	}
	
	return tableData;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
// This message should contain the functions to clean up
//   resources upon termination
public void destroy()
{
	ivjButtonBarPanel = null;

	//if( getComboBox() != null )
	//{
		//getComboBox().removeActionListener( getControlAreaActionListener() );
		//getComboBox().removeAllItems();
	//}
	
	LoadControlClientConnection.getInstance().removeClient(this);

	System.gc();	
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 10:18:56 AM)
 */
public void executeRefreshButton() 
{
	LMCommand cmd = new LMCommand();
	cmd.setCommand( LMCommand.RETRIEVE_ALL_CONTROL_AREAS );
	
	LoadControlClientConnection.getInstance().write( cmd );
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
// This method is used to export a set of data to a file
public void exportDataSet()
{
	java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame( this );

	com.cannontech.clientutils.commonutils.ModifiedDate date = new com.cannontech.clientutils.commonutils.ModifiedDate();
	
	if (f != null)
	{
		com.cannontech.tdc.exportdata.ExportOptionDialog exportDisplay = 
					new com.cannontech.tdc.exportdata.ExportOptionDialog(
							f,
							getJTableControlArea().getColumnCount(),
							getName(),
							date.getTimeString(),
							date.getDateString(),
							createPrintableText() );							

		exportDisplay.setModal(true);
		exportDisplay.setLocationRelativeTo( f );
		exportDisplay.show();

		f.repaint();
	}
	
}
/**
 * Return the ButtonBarPanel property value.
 * @return com.cannontech.loadcontrol.gui.ButtonBarPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ButtonBarPanel getButtonBarPanel() {
	if (ivjButtonBarPanel == null) {
		try {
			ivjButtonBarPanel = new com.cannontech.loadcontrol.gui.ButtonBarPanel();
			ivjButtonBarPanel.setName("ButtonBarPanel");
			// user code begin {1}

			//dont show this because since this Object is created inside TDC,
			// we will just add its buttons to TDC's tool bar
			ivjButtonBarPanel.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonBarPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 9:40:02 AM)
 * @return javax.swing.JComboBox
 */
public javax.swing.JComboBox getComboBox() {
	return comboBox;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 9:55:18 AM)
 */
public String getConnectionState() 
{
	StringBuffer title = new StringBuffer(LOAD_MANAGEMENT_NAME);
	boolean validConn = LoadControlClientConnection.getInstance().isValid();

	if( validConn && !lastConnectionStatus )
	{
		// connected and change
		title.append("   [Connected to " +
			LOAD_MANAGEMENT_NAME + "@" + LoadControlClientConnection.getInstance().getHost() + ":" + LoadControlClientConnection.getInstance().getPort() + "]");

		getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, 
				"Connection to " + LOAD_MANAGEMENT_NAME + " server established", com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE));
	
 	}
	else if( !validConn && lastConnectionStatus )
	{
		// not connected and change
		title.append("   [Not Connected to " + LOAD_MANAGEMENT_NAME + "]");
		//getComboBox().removeAllItems();
		getProgramTableModel().clear();
		getControlAreaTableModel().clear();
		getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, "No connection to LoadControl server", com.cannontech.common.util.MessageEvent.ERROR_MESSAGE));
	}
	else if( lastConnectionStatus )  // still connected
	{
		title.append("   [Connected to " + LOAD_MANAGEMENT_NAME + "@" + 
				LoadControlClientConnection.getInstance().getHost() + ":" + 
				LoadControlClientConnection.getInstance().getPort() + "]");
	}
	else // still disconnected
	{		
		title.append("   [Not Connected to " + LOAD_MANAGEMENT_NAME + "]");
		//getComboBox().removeAllItems();		
		getProgramTableModel().clear();
		getControlAreaTableModel().clear();
	}
			
	lastConnectionStatus = validConn;

	return title.toString();
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 9:42:02 AM)
 * @return com.cannontech.loadcontrol.gui.ControlAreaActionListener
 */
private ControlAreaActionListener getControlAreaActionListener() 
{
	if( controlAreaActionListener == null )
	{
		controlAreaActionListener = new ControlAreaActionListener();
	}

	return controlAreaActionListener;
}
/**
 * Insert the method's description here.
 * Creation date: (1/3/2002 12:32:26 PM)
 * @return com.cannontech.loadcontrol.popup.ControlAreaPopUpMenu
 */
private com.cannontech.loadcontrol.popup.ControlAreaPopUpMenu getControlAreaPopUpMenu() 
{
	if( controlAreaPopUpMenu == null )
	{
		controlAreaPopUpMenu = new ControlAreaPopUpMenu();		
	}

	return controlAreaPopUpMenu;
}
/**
 * Insert the method's description here.
 * Creation date: (9/29/00 1:46:27 PM)
 * @return ControlAreaTableModel
 *
private ControlAreaTableModel getControlAreaTableModel() 
{
	return (ControlAreaTableModel)getJTableControlArea().getModel();
}
*/

private IControlAreaTableModel getControlAreaTableModel() 
{
	return (IControlAreaTableModel)getJTableControlArea().getModel();
}

/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 12:56:35 PM)
 * @return com.cannontech.loadcontrol.popup.CurtailPopUpMenu
 *
private com.cannontech.loadcontrol.popup.CurtailPopUpMenu getCurtailCustomerPopUpMenu() 
{
	if( curtailCustomerPopUpMenu == null )
		curtailCustomerPopUpMenu = new CurtailPopUpMenu();

	return curtailCustomerPopUpMenu;
}
*/
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 */
public java.awt.Font getFont()
{
	return getJTableControlArea().getFont();
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 3:58:13 PM)
 * @return com.cannontech.loadcontrol.gui.GroupPopUpMenu
 */
private GroupPopUpMenu getGroupPopUpMenu() 
{
	if( groupPopUpMenu == null )
		groupPopUpMenu = new GroupPopUpMenu();

	return groupPopUpMenu;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/00 11:30:03 AM)
 * @return com.cannontech.loadcontrol.datamodels.GroupTableModel
 */
private GroupTableModel getGroupTableModel() 
{
	if( groupTableModel == null )
	{
		groupTableModel = new GroupTableModel();
	}

	return groupTableModel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 * @return javax.swing.JButton[]
 */

// this array of JButtons should represent the buttons that
//   get inserted into the toolbar of TDC
public javax.swing.JButton[] getJButtons()
{
	return getButtonBarPanel().getAllJButtons();
}
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 10:48:58 AM)
 * @return java.lang.String
 */
public String getJComboLabel() {
	return "View:";
}
/**
 * Return the GroupTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneProgramTable() {
	if (ivjJScrollPaneProgramTable == null) {
		try {
			ivjJScrollPaneProgramTable = new javax.swing.JScrollPane();
			ivjJScrollPaneProgramTable.setName("JScrollPaneProgramTable");
			ivjJScrollPaneProgramTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneProgramTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneProgramTable.setViewportView(getJTableProgram());
			// user code begin {1}
			
			ivjJScrollPaneProgramTable.setPreferredSize(new java.awt.Dimension(30, 30));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneProgramTable;
}

private javax.swing.JScrollPane getJScrollPaneGroupTable() {
	if (ivjJScrollPaneGroupTable == null) {
		try {
			ivjJScrollPaneGroupTable = new javax.swing.JScrollPane();
			ivjJScrollPaneGroupTable.setName("JScrollPaneGroupTable");
			ivjJScrollPaneGroupTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneGroupTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneGroupTable.setViewportView(getJTableGroup());
			// user code begin {1}

			ivjJScrollPaneGroupTable.setPreferredSize(new java.awt.Dimension(30, 30));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneGroupTable;
}


/**
 * Return the JScrollPaneControlArea property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneControlArea() {
	if (ivjJScrollPaneControlArea == null) {
		try {
			ivjJScrollPaneControlArea = new javax.swing.JScrollPane();
			ivjJScrollPaneControlArea.setName("JScrollPaneControlArea");
			ivjJScrollPaneControlArea.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneControlArea.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			getJScrollPaneControlArea().setViewportView(getJTableControlArea());
			// user code begin {1}		
			
			ivjJScrollPaneControlArea.setPreferredSize(new java.awt.Dimension(200, 200));
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneControlArea;
}

private CompositeJSplitPane getCompSplitPane()
{
	if( compSplitPane == null )
	{		
		compSplitPane = new CompositeJSplitPane( 
								getJScrollPaneControlArea(), 
								getJScrollPaneProgramTable(), 
								getJScrollPaneGroupTable() );
	}
	
	return compSplitPane;
}

/**
 * Return the JTableBottom property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getJTableProgram() {
	if (ivjJTableProgram == null) {
		try {
			ivjJTableProgram = new javax.swing.JTable();
			ivjJTableProgram.setName("Load Programs");
			getJScrollPaneProgramTable().setColumnHeaderView(ivjJTableProgram.getTableHeader());
			ivjJTableProgram.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableProgram.setModel( new ProgramTableModel() );

			ivjJTableProgram.setBackground( getJScrollPaneProgramTable().getBackground() );
			ivjJTableProgram.setAutoCreateColumnsFromModel( true );
			ivjJTableProgram.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableProgram.setGridColor( ivjJTableProgram.getTableHeader().getBackground()  );

			//ivjJTableProgram.setDefaultRenderer( Object.class, new MultiLineGearRenderer() );
			ivjJTableProgram.setDefaultRenderer( Object.class, new LoadControlCellRenderer() );

			ivjJTableProgram.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableProgram.createDefaultColumnsFromModel();
			
		} catch (java.lang.Throwable ivjExc) {

			handleException(ivjExc);
		}
	}
	return ivjJTableProgram;
}

/**
 * Return the JTableBottom property value.
 * @return javax.swing.JTable
 */
private javax.swing.JTable getJTableGroup() {
	if (ivjJTableGroup == null) {
		try {
			ivjJTableGroup = new javax.swing.JTable();
			ivjJTableGroup.setName("Load Groups");
			getJScrollPaneProgramTable().setColumnHeaderView(ivjJTableGroup.getTableHeader());
			ivjJTableGroup.setBounds(0, 0, 200, 200);
			// user code begin {1}
	
			ivjJTableGroup.setModel( getGroupTableModel() );

			ivjJTableGroup.setBackground( getJScrollPaneGroupTable().getBackground() );
			ivjJTableGroup.setAutoCreateColumnsFromModel( true );
			ivjJTableGroup.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableGroup.setGridColor( ivjJTableProgram.getTableHeader().getBackground()  );
			ivjJTableGroup.setDefaultRenderer( Object.class, new LoadControlCellRenderer() );
			ivjJTableGroup.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableGroup.createDefaultColumnsFromModel();

		} catch (java.lang.Throwable ivjExc) {

			handleException(ivjExc);
		}
	}
	return ivjJTableGroup;
}


/**
 * Return the JTableControlArea property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableControlArea() {
	if (ivjJTableControlArea == null) {
		try {
			ivjJTableControlArea = new javax.swing.JTable();
			ivjJTableControlArea.setName("Control Areas");
			getJScrollPaneControlArea().setColumnHeaderView(ivjJTableControlArea.getTableHeader());
			ivjJTableControlArea.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableControlArea.setModel( new ControlAreaTableModel() );
			ivjJTableControlArea.setBackground( getJScrollPaneControlArea().getBackground() );
			ivjJTableControlArea.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableControlArea.setGridColor( ivjJTableControlArea.getTableHeader().getBackground() );
			ivjJTableControlArea.setDefaultRenderer( Object.class, new MultiLineControlAreaRenderer() );
			ivjJTableControlArea.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);					
			ivjJTableControlArea.setAutoCreateColumnsFromModel( true );
			ivjJTableControlArea.createDefaultColumnsFromModel();

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableControlArea;
}
/**
 * Insert the method's description here.
 * Creation date: (8/14/00 9:51:45 AM)
 * @return javax.swing.JTable
 */
public javax.swing.JTable[] getJTables()
{
	javax.swing.JTable[] tables =
	{
		getJTableControlArea(),
		getJTableProgram(),
		getJTableGroup()
	};
		
	return tables;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 9:18:52 AM)
 * @return javax.swing.JPanel
 */
// This method must be called in order to see the
//   SpecailTDCChild(), the panel returned is the one
//   used to replace TDC's JTable
   
public javax.swing.JPanel getMainJPanel()
{

	//if( LoadControlClientConnection.getInstance().needInitConn() )
	initClientConnection();

	return this;
}
/**
 * Return the MessagePanel property value.
 * @return com.cannontech.common.gui.util.MessagePanel
 */
private com.cannontech.common.gui.util.MessagePanel getMessagePanel() {
	if (ivjMessagePanel == null) {
		try {
			ivjMessagePanel = new com.cannontech.common.gui.util.MessagePanel();
			ivjMessagePanel.setName("MessagePanel");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMessagePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/14/00 9:56:13 AM)
 * @return java.lang.String
 */

// Overide this method so TDC knows what the name of the special child is
public String getName()
{
	return LOAD_MANAGEMENT_NAME;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/2001 5:05:32 PM)
 * @return com.cannontech.loadcontrol.popup.ProgramPopUpMenu
 */
private com.cannontech.loadcontrol.popup.ProgramPopUpMenu getProgramPopUpMenu() 
{
	if( programPopUpMenu == null )
	{
		programPopUpMenu = new ProgramPopUpMenu();
	}

	return programPopUpMenu;
}

/**
 * Insert the method's description here.
 * Creation date: (9/29/00 1:46:27 PM)
 * @return com.cannontech.loadcontrol.datamodels.ProgramTableModel
 */
private IProgramTableModel getProgramTableModel() 
{
	return (IProgramTableModel)getJTableProgram().getModel();
}

/**
 * This method was created in VisualAge.
 * @return Object
 */
protected LMProgramBase getSelectedProgram() 
{
	javax.swing.ListSelectionModel lsm = getJTableProgram().getSelectionModel();
	
	//only one should be selected
	int selectedRow = lsm.getMinSelectionIndex();
	
	if( selectedRow < 0 )
		return null;
		
	return getProgramTableModel().getProgramAt( selectedRow );
}

/**
 * This method was created in VisualAge.
 * @return Object
 */
protected LMGroupBase getSelectedGroup() 
{
	javax.swing.ListSelectionModel lsm = getJTableGroup().getSelectionModel();
	
	//only one should be selected
	int selectedRow = lsm.getMinSelectionIndex();
	
	if( selectedRow < 0 )
		return null;
	
	Object row = getGroupTableModel().getRowAt( selectedRow );
	
	if( row instanceof LMGroupBase )	
		return (LMGroupBase)row;
	else
		return null;
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.loadcontrol.data.LMControlArea
 */
protected LMControlArea getSelectedControlArea() 
{
	javax.swing.ListSelectionModel lsm = getJTableControlArea().getSelectionModel();
	
	//only one should be selected
	int selectedRow = lsm.getMinSelectionIndex();
	
	if( selectedRow < 0 )
		return null;
		
	return getControlAreaTableModel().getRowAt( selectedRow );
}
/**
 * Insert the method's description here.
 * Creation date: (8/14/00 4:26:01 PM)
 * @return java.lang.String
 */
public String getVersion()
{
	return LOADCONTROL_VERSION;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	getMessagePanel().messageEvent( new com.cannontech.common.util.MessageEvent(this, "The following exception occured : " + exception.getMessage(), com.cannontech.common.util.MessageEvent.ERROR_MESSAGE) );
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 4:11:33 PM)
 */
private void initClientConnection()
{
	int port = 1920;

	//figure out where the LoadControl server is
	String host = RoleFuncs.getGlobalPropertyValue( SystemRole.LOADCONTROL_MACHINE );

	try
	{
		port = Integer.parseInt(
			RoleFuncs.getGlobalPropertyValue( SystemRole.LOADCONTROL_PORT ) );
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	//hex value representing the privelages of the user on this machine
	userRightsInt = Integer.parseInt( 
			ClientSession.getInstance().getRolePropertyValue(
				TDCRole.LOADCONTROL_EDIT, "0"), 16 );


   LoadControlClientConnection.getInstance().setHost(host);
   LoadControlClientConnection.getInstance().setPort(port);
   LoadControlClientConnection.getInstance().setAutoReconnect(true);
   LoadControlClientConnection.getInstance().setTimeToReconnect(5);

   try
   {
	   //lock the connection down and try to connect if we arent connected already
	   synchronized( LoadControlClientConnection.getInstance() )
	   {
		   if( !LoadControlClientConnection.getInstance().isMonitorThreadAlive() /*.isValid()*/ )
	  			LoadControlClientConnection.getInstance().connectWithoutWait();
	   }
	}
	catch( java.io.IOException i )
   {
	   com.cannontech.clientutils.CTILogger.error( i.getMessage(), i );
   }
   
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception 
{
	//Observe the Connection to the Client
	LoadControlClientConnection.getInstance().addObserver( this );

	//do this so we can tell the connection what Models to change to
	getControlAreaActionListener().addControlAreaListener( this );

	//Listen for any selections made on the JTables
	getJTableControlArea().getSelectionModel().addListSelectionListener( this );
	getJTableProgram().getSelectionModel().addListSelectionListener( this );

	//Listen for TableModelEvents
	// with the GroupTableModel AND the ProgramTableModel
	getJTableControlArea().getModel().addTableModelListener( this );
	getJTableControlArea().getModel().addTableModelListener( getProgramTableModel() );
	getJTableControlArea().getModel().addTableModelListener( getGroupTableModel() );


	// add the GroupPopUp menu listener AND the ProgramPopUp menu listener here
	//  Note: We have to overide: showIfPopupTrigger(java.awt.event.MouseEvent e)
	//   because we have a different PopUpBox for each subclass of com.cannontech.loadcontrol.data.LMGroupBase  --Tricky!
	java.awt.event.MouseListener groupListener = new com.cannontech.clientutils.popup.PopUpMenuShower( getGroupPopUpMenu() )
	{
		protected void showIfPopupTrigger(java.awt.event.MouseEvent e) 
		{
			if( e.isPopupTrigger() )
			{
				Object row = getGroupTableModel().getRowAt( getJTableGroup().rowAtPoint(e.getPoint()) );

				//determines what popupBox is shown
				if( row instanceof LMGroupBase )
					getGroupPopUpMenu().show( e.getComponent(), e.getX(), e.getY() );

//				else if( row instanceof LMProgramBase )
//					getProgramPopUpMenu().show( e.getComponent(), e.getX(), e.getY() );
//				else if( row instanceof OfferRowData )
//					getOfferPopUpMenu().show( e.getComponent(), e.getX(), e.getY() );
//				else if( row instanceof EExchangeRowData )
//					getCustomerReplyPopUpMenu().show( e.getComponent(), e.getX(), e.getY() );

			}
		}
	};

	getJTableGroup().addMouseListener( groupListener );
	getGroupPopUpMenu().addPopupMenuListener( this );
	//getCurtailCustomerPopUpMenu().addPopupMenuListener( this );


	//Add the ProgramPopUpMenu menu listeners
	getJTableProgram().addMouseListener( new com.cannontech.clientutils.popup.PopUpMenuShower(getProgramPopUpMenu()) );
	getProgramPopUpMenu().addPopupMenuListener( this );

	// add the ControlAreaPopUp menu listener here
	getJTableControlArea().addMouseListener( new com.cannontech.clientutils.popup.PopUpMenuShower(getControlAreaPopUpMenu()) );
	getControlAreaPopUpMenu().addPopupMenuListener( this );

	getButtonBarPanel().addButtonBarPanelListener(this);
	
	// Add some table mouse listeners
	getJTableControlArea().addMouseListener(this);
	getJTableProgram().addMouseListener(this);
	getJTableGroup().addMouseListener(this);
}

/**
 * Initialize the class.
 */
private void initialize() 
{
	
	try 
	{
		setName("LoadControlMainPanel");
		setLayout( new java.awt.BorderLayout() );

		add( getCompSplitPane(), java.awt.BorderLayout.CENTER );

		add( getButtonBarPanel(), java.awt.BorderLayout.NORTH );

		add( getMessagePanel(), java.awt.BorderLayout.SOUTH );
		
		initConnections();

	}
	catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}

}

/**
 * Method to handle events for the ButtonBarPanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void JButtonDisableAllAction_actionPerformed(java.util.EventObject newEvent) {
	if (newEvent.getSource() == getButtonBarPanel()) 
		buttonBarPanel_JButtonDisableAllAction_actionPerformed(newEvent);
}
/**
 * Method to handle events for the ButtonBarPanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void JButtonEnableAllAction_actionPerformed(java.util.EventObject newEvent) {
	if (newEvent.getSource() == getButtonBarPanel()) 
		buttonBarPanel_JButtonEnableAllAction_actionPerformed(newEvent);
}
/**
 * Method to handle events for the ButtonBarPanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void JButtonEnableControlAreaAction_actionPerformed(java.util.EventObject newEvent) {

	if (newEvent.getSource() == getButtonBarPanel()) 
		buttonBarPanel_JButtonEnableControlAreaAction_actionPerformed(newEvent);

}

/**
 * Comment
 */
public void jTableProgram_MouseClicked(java.awt.event.MouseEvent mouseEvent) 
{
	if( mouseEvent.getClickCount() == 2 )
	{
		if( mouseEvent.isShiftDown() )
		{
			int row = getJTableProgram().rowAtPoint( mouseEvent.getPoint() );
			showDebugInfo( getProgramTableModel().getRowAt(row) );  
		}

	}
	
	return;
}
/**
 * Comment
 */
public void jTableProgram_MousePressed(java.awt.event.MouseEvent event) 
{
	if( event.getSource() == LoadControlMainPanel.this.getJTableProgram() )
	{		
		int rowLocation = getJTableProgram().rowAtPoint( event.getPoint() );
	
		if( event.isControlDown() )
		{
			getJTableControlArea().getSelectionModel().setSelectionInterval(
				getJTableControlArea().getSelectionModel().getLeadSelectionIndex(),
				getJTableControlArea().getSelectionModel().getLeadSelectionIndex() );
		}
		else
			getJTableProgram().getSelectionModel().setSelectionInterval(
					 		rowLocation, rowLocation );
	}
	
	return;
}

/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseClicked(java.awt.event.MouseEvent e) 
{
	if (e.getSource() == getJTableControlArea()) 
		jTableControlArea_MouseClicked(e);

	if (e.getSource() == getJTableProgram()) 
		jTableProgram_MouseClicked(e);

	if (e.getSource() == getJTableGroup()) 
		jTableGroup_MouseClicked(e);
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseEntered(java.awt.event.MouseEvent e) {

}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mouseExited(java.awt.event.MouseEvent e) {
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
public void mousePressed(java.awt.event.MouseEvent e) 
{
	if (e.getSource() == getJTableControlArea()) 
		jTableControlArea_MousePressed(e);

	if (e.getSource() == getJTableProgram()) 
		jTableProgram_MousePressed(e);

	if (e.getSource() == getJTableGroup()) 
		jTableGroup_MousePressed(e);

}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */

public void mouseReleased(java.awt.event.MouseEvent e) {

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
	if( e.getSource() == LoadControlMainPanel.this.getGroupPopUpMenu() )
		getGroupPopUpMenu().setLoadControlGroup( getSelectedGroup() );

/*
	if( e.getSource() == LoadControlMainPanel.this.getCurtailCustomerPopUpMenu() )
		getCurtailCustomerPopUpMenu().setLoadControlGroup( 
			(com.cannontech.loadcontrol.data.LMGroupBase)getSelectedBottomRow() );
*/


	if( e.getSource() == LoadControlMainPanel.this.getProgramPopUpMenu() )
	{
		getProgramPopUpMenu().setLoadControlProgram( getSelectedProgram() );
	}


	if( e.getSource() == LoadControlMainPanel.this.getControlAreaPopUpMenu() )
	{
		//make our selected row observed
		getControlAreaTableModel().setObservedRow( getJTableControlArea().getSelectedRow() );

		//set the observed flag for our selected row
		getControlAreaPopUpMenu().setObservable( getControlAreaTableModel().getObservedRow() );
		getControlAreaPopUpMenu().addObserver( this );
		
		getControlAreaPopUpMenu().setLoadControlArea( getSelectedControlArea() );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
public void print()
{
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
public void printPreview()
{

}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
public void removeActionListenerFromJComponent( javax.swing.JComponent component )
{
	if( component instanceof javax.swing.JComboBox )
	{
		getComboBox().removeActionListener( getControlAreaActionListener()  );
		comboBox = null;
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/00 5:05:36 PM)
 * @param visible boolean
 */
public void setButtonBarPanelVisible(boolean visible) 
{
	getButtonBarPanel().setVisible( visible );
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 */
public void setTableFont( java.awt.Font font )
{
   for( int i = 0; i < getJTables().length; i++ )
   {
   	getJTables()[i].setFont( font );
   	getJTables()[i].setRowHeight( font.getSize() + 2 );

   	// set the table headers font
   	getJTables()[i].getTableHeader().setFont( font );
   
   	getJTables()[i].revalidate();
      getJTables()[i].revalidate();
   }

}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 */
// This method should handle the option of having
//   the vertical and horizontal grid lines on a JTable set or not,
//   if a JTable is present
public void setGridLines(boolean hGridLines, boolean vGridLines )
{
	int vLines = ((vGridLines == true) ? 1 : 0);
	int hLines = ((hGridLines == true) ? 1 : 0);

	// Set the ControlArea Table accordingly	
	getJTableControlArea().setIntercellSpacing(new java.awt.Dimension(vLines, hLines));
	getJTableControlArea().setShowHorizontalLines( hGridLines );
	getJTableControlArea().setShowVerticalLines( vGridLines );

	// Set the Program Table accordingly
	getJTableProgram().setIntercellSpacing(new java.awt.Dimension(vLines, hLines));
	getJTableProgram().setShowHorizontalLines( hGridLines );
	getJTableProgram().setShowVerticalLines( vGridLines );


	// Set the Group Table accordingly
	getJTableGroup().setIntercellSpacing(new java.awt.Dimension(vLines, hLines));
	getJTableGroup().setShowHorizontalLines( hGridLines );
	getJTableGroup().setShowVerticalLines( vGridLines );


		
	getJTableControlArea().repaint();
	getJTableProgram().repaint();
	getJTableGroup().repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
public void setInitialTitle()
{
	//NOT USED
}
/**
 * Insert the method's description here.
 * Creation date: (8/4/00 8:59:17 AM)
 * @return javax.swing.JButton[]
 */
// this method should be used to set the array of JButtons
// in the toolbar of TDC
public void setJButtons(javax.swing.JButton[] buttons)
{
	//DO NOTHING
	//buttonsArray = buttons;
}

/**
 * Insert the method's description here.
 * Creation date: (8/29/00 1:32:55 PM)
 * @param colors java.awt.Color[]
 * @param colors java.awt.Color 
 */
public void setRowColors(java.awt.Color[] foreGroundColors, java.awt.Color bgColor )
{
	//DO NOTHING
}

/**
 * Insert the method's description here.
 * Creation date: (2/1/2001 12:55:52 PM)
 * @param soundToggle boolean
 */
public void setAlarmMute( boolean muted ) {}
public void silenceAlarms() {}


/**
 * Insert the method's description here.
 * Creation date: (4/5/00 4:29:59 PM)
 * Version: <version>
 */
private void showDebugInfo( Object value ) 
{
	com.cannontech.debug.gui.ObjectInfoDialog d = new com.cannontech.debug.gui.ObjectInfoDialog(
		com.cannontech.common.util.CtiUtilities.getParentFrame(this) ); 

	d.setLocation( this.getLocationOnScreen() );
	d.setModal( true );
	d.showDialog( value );
}
/**
 * This method was created in VisualAge.
 * @param selected Schedule
 */
protected void synchControlAreaAndButtons()
{
	LMControlArea area = getSelectedControlArea();
		
	if( area != null )
	{
		if( area.getDisableFlag().booleanValue() ) 
		{
			getButtonBarPanel().getJButtonEnableControlArea().setText("Enable Area");
		}
		else 
		{
			getButtonBarPanel().getJButtonEnableControlArea().setText("Disable Area");
		}

		getButtonBarPanel().getJButtonEnableControlArea().setEnabled(true);
	}

}

/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(javax.swing.event.TableModelEvent event ) 
{
	if( getSelectedControlArea() != null )
	{
		//set all the LMProgram values
		updateProgramTableModel();
		//getProgramTableModel().setCurrentControlArea( getSelectedControlArea() );

		
		//set all the LMGroup values
		getGroupTableModel().setCurrentData( 
				getSelectedControlArea(), getSelectedProgram() );
	}

	

	if( event instanceof com.cannontech.loadcontrol.events.LCGenericTableModelEvent
		 && ( ((com.cannontech.loadcontrol.events.LCGenericTableModelEvent)event).getType() 
			   == com.cannontech.loadcontrol.events.LCGenericTableModelEvent.TYPE_CLEAR) )
	{
		getJTableControlArea().getSelectionModel().clearSelection();
		getJTableProgram().getSelectionModel().clearSelection();
		//setSelectionInterval( -1, -1 );

		getGroupTableModel().clear();
		getProgramTableModel().clear();
	}


	revalidate();
	synchControlAreaAndButtons();
}


/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:36:12 AM)
 * @param o java.util.Observable
 * @param val java.lang.Object
 */
public void update(java.util.Observable source, Object val)
{
	//Could be an instance of com.cannontech.loadcontrol.LoadControlClientConnection
	//notifying us of a change in the connections state
	if( source instanceof LoadControlClientConnection )
	{
		if( val != null && val instanceof LoadControlClientConnection )
		{
			LoadControlClientConnection conn = (LoadControlClientConnection)val;

			// set the frames Title to a connected/not connected text
			final String connectedString = getConnectionState();

			java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(LoadControlMainPanel.this);
			if( f != null )
				f.setTitle(connectedString);						
		}
	
		if( val instanceof LCChangeEvent )
		{
			getLCAreaEventHandler().handleChangeEvent( 
					(LCChangeEvent)val,
					getControlAreaTableModel() );

			//synchControlAreaAndButtons();
		}
	
	}
	else if( source instanceof ObservableJPopupMenu.ObservableJPopUp ) {
		
		getMessagePanel().messageEvent( new com.cannontech.common.util.MessageEvent(this,
			val.toString(),
			com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE) );		
	}
	
	
}
/**
 * This method handles ListSelectionEvents
 * @param event ListSelectionEvent
 */
public void valueChanged(javax.swing.event.ListSelectionEvent event) 
{
	javax.swing.ListSelectionModel lsm = (javax.swing.ListSelectionModel) event.getSource();

	//set all the LMProgram values from the Control Area
	if( event.getSource() == getJTableControlArea().getSelectionModel()
		 || event.getSource() == getJTableProgram().getSelectionModel() )
	{
		updateProgramTableModel();
	}

	//set all the LMGroup values from the Control Area
	getGroupTableModel().setCurrentData( 
			getSelectedControlArea(), getSelectedProgram() );

	synchControlAreaAndButtons();
}

private void updateProgramTableModel()
{
	final JDialogWait d = new JDialogWait( CtiUtilities.getParentFrame(this) );
	
	if( getProgramTableModel().showWaiting(getSelectedControlArea()) )
	{
		d.setLocationRelativeTo( this );
		d.show();
	}

	//this must be done in its own thread since it would block the GUI thread
	new Thread( new Runnable()
	{
		public void run()
		{ 
			getProgramTableModel().setCurrentControlArea( getSelectedControlArea() );

			if( d.isShowing() )
				d.dispose();

		}
	}, "CtrlHstThread").start();
	
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BF4D4C516C1FE99EDA438D1DAC0A7C0F0324A2C99E5C61CE59C0C1C95576C0E63B2AB6E62B9F10633E6F7F097F718B9D163596A4ED78D10C00068C48986951C188972B10668105F12040674A48DC187CD3FF43FF4BAF43A1B57AF64C3BEFB2B6A5D57AF2F3B1310A31BF3EE5E2BDB376AFE6A562DDBD52FC9617E38AC13391090D396D17EBEB59192F30A1043A77DC644C509C959C473F7G20007C
	68F8A6BCE3202EF2C5D2F6BE99FCAB974A71D07647A7E56F03775D64315235DF40CB84BEC628F32F78E97C44FC5EBE0DFCF2D8F9EBFE9EBCF7GA4G0EBE370914FFEEC11102CFD67088A6B3A11B3AA8CDE9C131022B043295408830320B532F03676B0C736C2ACA056E5202B8326D6FBFB4EF277DE87AC4B86862ED9B15E789395F3455C5F9D98A7214B6D1D0DE8DC0154FA01FE7FD864FEA6BE557978EDFEF39C370FAFDDED9F4F417FBFC0253816F124FD36E6AF3D7D5D9GD14791E7853757AAF8C58F3475F4
	537EE38BF2D9FF049C03F29A43ED31D31E09703E9EA0C545BFA8D0FC35BBA95B8660F1231E1FFFD82D68F1715A7DE455B35E0C1D91741C65667A2DF487750C6DFBA567ADB6A6DFB5D25B27C2DDDECFD2F689C0BD0093E096406F19AF8827BF02E7CD27DC676B6D75F9EB7C7EDA0FD3BA6BF30A9EF82F2A029A8557AABAFC121390BE5E2D85E5E1641988F8BB7BF53763B6C9B80AE33F32759E326941B3661CB0B65914D0E1DE5243ED312AA7E8934E7B4E083CB7740C774D713C5B0916F77C9CDFC6D6985E2BFEF1
	581C2A74132E61DDF6190E351D0DF5B13C1B947F60789E0677EB705CCF7F1B798CADC7C15D664B689B97F720AC164CC5A47F08A52C9D0CC9B566C68F17416E514BD257C97BB6F8B84F82D5969B432F566039AC8FB399E9F9B35479FB133239FFB91A693CCD07F211B7A93B822091209BE09600510BBEF66F69BFCE51472C02A4577639BDCED1A23C2D23F1AFBC4581D9921A3C81D970BA448B026496BABDE200A8B41EC6EA034EF16D2EB05FF7G0F5322D71484596D0319AA893D22ACCA81CEB354C8F5F2C8A22B
	3539FBC5E0506BA7586FCD0D3B693C968272F93FD31045A0852F3F39110E49D5618274889D40BB37CB4C99E6C7507FA90094558ED9CD343FB32284810306915F65BE3F0DEAC2AAFC544E09ACF6EC837ABC9F5A58E021B8994A869F76D35D34CB53CF3D5BC1459724C11CE3DD820D4DF1BE74197F3C08BED33FFD85A97BC5F3D81F21305647FDE523CFBFF77C2CCFAA97AF2759AFCEF17E4679424F2D49E3C501B0D59E9957597F8EF52CDEB9CB470A7813320B812A7DE8637E26926E3335C2C0C4AFED114041C4E7
	5062DC4E264E92C5E6EAEF9A2BCB81685A74A9616B6599D5272F982D8D788C834483A483EC237C2FC0FC8670DCC19B7FD4088AE31757157E36AB3CA6537300A24FD4EC72E9443178E508AC5466D9044705B7953A497A5F2948BF95D9360741713169ED267D08D7C52FDCAF7AC52F93DE82CA1D5CCC7BD762C517CFEDB34470FE08AFF5B02183AABE2A194AAF4832646E6C1345B6D1165DDED730BE1A718A74F57A032189F94DEF267A7B7AA13E2874314DF4EEC23E6095191328CD147ABBD8EC90BDE2AF88DD676B
	734A9C3F648C1DDF1745C1A5B6451D21C957D54153C7639C4FD19285EAE73F646E8557232112EC3EC2634663EA0EC5A196F0B457DACD1FD7B0867417E28CB8B834142C6DF85F9CEE8DB32E28B717C9FC3E1E1202B1004B344092DE7605ACC6A978G17357DA8A416A4DC2A44675D51965A179DF848G8689675D6E33349EE31BB2616AFC9E8FB727EAE7F2AE1F0E0BF825CF04A5018651B32A6DF24E6D5255DDD06C486BF71E23F674E862EFEC005A7136CE4CC363C11EB5GA99237E5E5005B0253DDF2A0DDCA00
	532585B81DCD5652FD68C43AC419532D17B95D21BEAD5DEFD4BAC39F270B6F63F4642A166E9191696C009FGD8F21553ED9CC7B7238B69CA7AB9DDE5BF27137B35F475AE24DB83788D7D417E5246512529F455831C8E7B338DF0BABE663FB9CC7D106550BD42D5E120BC508FF3292A2A190DBB4F0F0F6C4AD5680D831887FF3D077A42D3D0CE85A0036833BF5F0EF9F06C65B812327C6D0879FE51A01723FAB06833A5B3B18F9E31D21E43D0E798C4BF3B791CF6AD2E8984FC8EB7CB83949F792CC56B539C77C54B
	9B706CF2CB813989024EG7AD3FD8B151766898ADA491B7E1665D3D457B8603B91004F5767C73573B5FF082F77D6F8BA81B2D81F6FB9AC0466B4328CB1C41018854B1BEBD93941CB1D36466329E1F322C3E08F2BA8F57924DE51F9089A3F3CCFF6FB4A9BE80CEC696C0149F3180F116D5D1610FEBB26582FA5EC47A78FDFD07264E53BCFA9BAA57F4DA2D2BD365B9CEE5F923D739D7370909F27782160F88DBF101D31651BD7F3386DE6BB50F6DB016650B5344B96475908F629F78726E39855FF971D36C6344D24
	FDC7B48E57476AC4FD6C200BD15567A127ED12F1D662E78D242953986F3BCE36CFB25E13779FF95C39EE45A26A76946815BB0C3A4D94C3FD8DF5EB6B76751F167C812B648ADC5FE8F13D1EF83FAD22DE1377FDE4D21DDED0F5BA857A8C8C635CDDB1EE6EA682FE83C09AC05E886AFD2E6BE3F8767A7A82E2B97BDF67F1BBAE23C6C23FDC4EF2177233340EF18F7AE0E26EC94272879C9336C0F51C55F6E30958CFD07EFB3BD07E03A057A5D5368217CEB62BA48682D3132D78E2E459301B2948566BC259E2C12E64
	91346D53E63DEDB3C7E16E83D40F62B9CD13C8F3549E46395687235DFB7D36B9500D7CED502FC16D7B3BD2783E27269B4F13CF6C521FB12C9B8D6EFFB60E066E05785E7B67EC8D5CB24A775EB9AA7EB9064F9C8B62398E8E062765E2280B9A437DE2DEAB3561E6A813A8581783D4GD8817CD13876A5379CA5136C471B3C30CD768A1E36F109AF6E0B2D5417A0D7D54AF32C54FEACD7A538DFF834D3EF87C354F85D93F4E55E6EE7A16D5E551513F56D7858ED7AA355E9CA67F735630206F2EE7756981E5BED0E42
	3CA470B5BB33191114FD8C40B283ED5C53C673A1A71423G0CG2BGB682EC83A83A896D1C7F674FA733B3739E1B2F8EF00D100F5669778D2ADDEEF1CC4F1EF70634535B337226F06DAEB827474F9952CE4F4F9A165FFB627478D5043453736B8E4B6F45E972FBF5D2FED1B705734F1EEB91F2E396EDF87C7B6D10B67E99211FF5G29G69G5CCF1F9BC5BFAD1C09FEFA02D0BF35C05989B0BC934F8F923B262763A3A16D74BA464E8C57AE601A9E3F99A16D747CD66A78F1FB7DBB89E72FF5C03BF1A65A6B0D8C
	3457D655DEDFE6D2FB9D1A1514FD8A6062AC3457075D53137FF8C8BB3D7C7ED9635B71733E33ECAF948DF5CBGB86E23C20ADB8B650D2ADC4F30D81F8E654259C959E533917FAB0627BAB74E667BC60EF76F09F4EEC65B6FD07A5257154D1E5AB99D67F1D3C49ED94AB35C195CD44E08A870783927B572B9CFF1EB78F31E77DA431F739CEAAD60F868C3FB4A0C75952D931F83D53606BB87FA076979445E1D2CEFC89282AC20578B3220543DA06FA1E14E034E893D22E2235DA42D73F50526C176559DD3ED7C6E58
	C4F7A51EB06345F3066F333B032859BCE7B029789FB13CC1036739446057F8A7E6043A745938FE49ED116DFF2DAD3C7DE73451BDFA4057A7B9C43D0D235B0A543A60CA57A40B3D549C4A9993EBAF8B12CB8C99A3A3EB4F6BA235DF5A96E99C392D139CDADB9FD771731CDAFC250A7FD25462BFD571873B3478A384633C2B010EFB18B85F66008444C3D38E0271EB6FDEED7C324F6167G5170DCB28763574757F295FFD8B5876357569116ABC2B9F3EED2F671DC1C773F9C1D48DF0E4D0D64AF77327523F12E5EDF
	9EE4F85BDC3D3FD4937497BB542D1B0BFB0B9746596428EA2BFBDDDA7CA7846FCA8F18D037C351285BBBE6F6AE8865810018E854EDE746C43AA5C4C7526D9FD82CDF932D576D1F99FEFD34DE370ECC54ED8354D5FE87F57B26DB2B4389D5B767AEFACE6B9186FDFDB2A139B753BE2E6D42F1DD826DD781EC82587A9DBE3E5CCFFE50CF4FA03BA421975C234DAFBA48E6563626905BDA216C841885908F306EE61465FF7650F32866B63F47ADF3076278BF33B3063330D991DCA2CBEEB97EE8A76DCF1776AABE1817
	25754132DB380CCEF88E5C02EB5271B1DC13E2EE4171F850CE884629B51A77312F27FAE7B39D60BC0CB714879F1F4565B4478FB70F516A5C074F6D253621F1B265967461AF0668B8549BE05F8DA09B50E646E174E18360628C63FDF615A1128FBBC7192F9A74F747DE065FE8505F9D7FEE846F0ED3214EB28F477165D19C4744F9EA0EC802E37730663D165C78F17C4B7F830F8FCCE8738D20F36A3C71B6CE1F9749668BD89C4A3FD5EF73458CDFF22B5E6625E634F98554455E0AB67FA083ED1EF6AB5A7C1FB203
	F65E24F997B3EF3CBDC6EFA88FEE0B653BB006964FCF4A364D4718F744BEEECE5113142A2A53125BD9AB38ACAC0272B975EB169753760679B8275E5A4DF2DDA8EF8258B69F6F8886BE40BB021577ACA21628B3E16F88D2EC1F1873E278D8D744844F1C937ADE4961BCDD054853837571B194F7FBE121268FBA367360DFB9BCF97D63636AE95EC14F4A0275AF0F2B27723E16292D7F439EEDFD1626BD576D4977D13774A7930867071FC53C7F582268143F00BF2D97CA49411BDF4D61F6BFACFFC4421F0D387A5C6C
	8C342ACA3BEA607ADC1F05FB274685B88E65AC66C4C159881088103E00FDD7B5D6BF01DFE468E0BD345984703E2277D6F87F3D096B4D7D34EDAF75537A5BE0FFF65B647B920FC29325230D05F2028E37864AFCAEBF406274D3377139CC6560781FB07CE68D1E7BF57D901E9B26C15D253F42B9FE7FB01B63D05E2C62F2C7A8AE8D4AA53108F30CE0ACB8960B3120E71486G3BDA16D9195E5971F27AC82479CA5BFCBBB1E14FE8A49E53792EC28FBC2F499F43BCA38A640E85D8853081E01BEA1F4FCD21F66CB205
	5AF13E896DD8F8BB5AE921195A6D14DADE4A4A742E12178F1BD1C7CA73ED47BDE54DD4F9FC9BF68BEF474FB3500E97C18F8F0081E0A5C0322A7BDF071CC1CFCD7E072659EE6BF53763736C57AC26EF39DD1F1BEFE378345B7539797E5D382EE45E11145DFD871E478E1F27B6C906F2AAGD91814DD84D089E0DB08FB5996DB851964AC566673DBE0336B519C8FF07DBC05537BE671519C0C5F2BFE3C0C2C1E733269CD92BEFEF7AF64BE992DBCD3ED258C4F755B59CE75DB8DF59BG52G720DC95987812A0D285F
	C2DB99572F416916EBE4D9F0F4331BEBD46E02DB6C8F226906C379F6A9EBA25D2C46C87951B2368EB80D7A313B0F617DC67D589D9B45319B063A94007DCCEE58B3917C5B7A27F0387ABB6977472FFDCDD43BBE1F07F6AD3EEFB129FF66E5D3E9843BC65F29FC4FF6E7F04DCF6DC94F61F1240F656E790B12328FAD425852370F622A21FCC945BD4AF0FEA89BD4DCDE914545C1F90D0ADB40560127203CD545BDB70C31AAF3B1621E9D24B89B14BD0BF14E2E9E2238A8A8472B384798EE03F28F46F14DC35AD8CD69
	2FB70E44CF500673F00C63B11DD816B5C19D5F137E5DB07D967D2AA849EE07606107A3F4EF6A73389D03A4F7897BCE02590E66C46B97635EF77F881D935DBE49BD84B9CCE8CB2234BD4956DAFA7FE20172AA069B9AA9D1E408D992EA03F8854779FC3C0F66F37CF4DEF3C3CEB0B7BAB1065F3124AF41FC51BB264F9797183479DE5C18BEDF24BE9B2CFFFACC1FAF0EEF1FC97479A236677A5DE3C27D0E5D952ADF63DDDA7D56D465297A713BCE2E17A3837360CBF721DE8BB2A6566B310C09757AED4644FAFD1899
	C9AF6E0FAF5110203C3F22F9FFFF770D5FAFFDF1C3F97058BD7BFC64F3D6437970672C371D8FFF4EEDBCBF71B9F65C79485FB3FE6F3C76FBC63E7694F650310985DFC88458G108A10FE972EBB47AF3CC7583A93E64DE9F03A685C21DFED4B483F28BD322E7B5BA77F567BD0BBBD7B6AE49ECB7BC7FCF9BB9DAF5967E7C80EBB5ACEEDA439DD5DB2A745EF3B5A290FFB44AE0666B1591E05F13A68EE0C358923C1DF7B01663DAAE23E7C6D79DD5B8D654157511F7DC57BFB1A7CC5874C38E66B693B6F6631C33EBB
	380EF27FF848CE77FD8900DF87108AC03E8BF9B9C09940A9G9BC0B7C0940091E0A540DA008DG1B8152G7262501FEA3F21717E025023E4E8B41D71C94C23G299C96B0271284B7F5ABA51E9976525C4A693E6A8607EEF38F094ABA904F56F8BAA7CA62F08DB16C436F7A6BB93D4567605F7E71B67730757EB9008A354D46FDBB037D6B68DFD269CF297425C5F42F6E1744AED112C4A713885726EA16BBD8017612CA1FB0CC3FD975F9C51B2F4F515DB060973CCE76E92A52A6E998776251F138675EB6BC591EBB
	0B84775CC6E8379220D4A92F0577C7E113B4430CB65CBAC8ED88CBAB4CC92DFE39C338A6A42B726E6825F3D5F32BB1EE1164B4FB3AF5B4FC21611F5C625D57B03D6F529C3116EB684BAFB4345A1A6AEAAC9DEDF52DAD96CBEDCDEBC7CDDB473906067A06FA6E43F17DDA9F2EFC6353305FB5D3A0CB396F96AA4FB5F3CB9599DAC7CECCA0C3E3CBEB53D7AD67EC612540FCE0598866831625E823FF9921CF08C94AFC6B4957662D1B865FB497C410F516A2E3625260FC8B460613636E85D301A6FD6944F9A017719F
	CDA8E37EB214B15BCC6FAFF8BC457E3BA73CEB093C0E95F7C40E6D259D134776328E764DAD44E45B20DF394FBB52C16573F5F585C4F58DB951112F621A82D6DF40CD1D154F097E2C4966C40EE6CEB4029DAE819CD06C620777BF411C20F7D7BC3E5D57C9D47D56F750732A303189D63BE6709F7E9521CDF029ED1E34C73649537649ED722F76706B5D737650756E5F6C61573BADF66DFA47F1AF59293EAE082797D53A5F2938AFD55C2B2CCF0AEBF7BB65EE95EF925ED0700DA2E508789C014A554FF09795DC1E86
	772502AB906838024A104AE35B3D6CB7C6DDEE0F07175FE6B4EEAF8C33EA0F7D8215516D97B4F29FD4F1DF920CABD77651F9953C477D49BE7ABCF224C2A96796E1196FAF2DCE22524E99AE0127D76CAFD1BF782BF7C3E6C3FF64F568587163E5A51547AB8EF35AE707294C3254A7DE5FFEB6CA8C725BBFC47DEAFC7C41DBA6CD8834DA1AEC2ADFFE5C453EE987FFB5AE43357307429FA279A7A02D124F8F5B2DC10EE53F08D0667B77C63473FDB574173CEC62F842E5303B281DD200B69D20E4F9D2F6A500854089
	B0G90339CE5CB1FD0B6D5DD4D4A4E7332D6364FDA897D50BD834735B35CBA85275C291834BA24C05D3665936BF0FD63156BA62A3FFD52C55B2BB9B6B7A56616DD054CE7A1A4899E7790DBC65B00506B6232BFB2027A3C18097ABCB6027ABC2B72386C0A9CB7A45764F1631AABFC5C18618A0D9BF3DC61630641959AB79630FEA55185FA710FD81473A7974DF778BD78D941AB38589E02679F4287F9C11B30B022FCCFBC30BD0847B0225864BFCC21F67AAF935A698998F77EDB767FBD76E3CA77A2498AC8E427BA
	DF1BD850C839FA34AA63D74BB34CA43309E4F3E3B411CC2AD19349E28AB7118CB04600DF99EEDC98CC505B2E5059DFF36B334F2C6C28AFB493B3F458AD729E327A29CE340F0CD6E87AF8608AE13B85E29E57A3E2B3B5588C4C458F0FE973183459C591731861955C2FE3D668F3716F1F2076C986FC6A8A1CF72F9D7FB8523C03B52B2DD770F8E441F339466B246BD76D20AC86770A8F1C207D8710C62964F58F1F20F277E85A7C9FD0CB87884A7F6DD51695GG38C0GGD0CB818294G94G88G88GC6F954AC
	4A7F6DD51695GG38C0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5095GGGG
**end of data**/
}
}
