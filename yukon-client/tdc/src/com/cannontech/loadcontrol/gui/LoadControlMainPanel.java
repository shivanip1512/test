package com.cannontech.loadcontrol.gui;

import java.awt.Frame;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.cannontech.common.gui.panel.CompositeJSplitPane;
import com.cannontech.common.gui.panel.ManualChangeJPanel;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.debug.gui.ObjectInfoDialog;
import com.cannontech.loadcontrol.LCUtils;
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
import com.cannontech.loadcontrol.displays.ControlAreaActionListener;
import com.cannontech.loadcontrol.displays.IControlAreaListener;
import com.cannontech.loadcontrol.displays.LCDisplayItem;
import com.cannontech.loadcontrol.events.LCChangeEvent;
import com.cannontech.loadcontrol.gui.manualentry.ConstraintResponsePanel;
import com.cannontech.loadcontrol.gui.manualentry.DirectControlJPanel;
import com.cannontech.loadcontrol.gui.manualentry.MultiSelectProg;
import com.cannontech.loadcontrol.gui.manualentry.NoViolationResponsePanel;
import com.cannontech.loadcontrol.gui.manualentry.ResponseProg;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.loadcontrol.messages.LMManualControlRequest;
import com.cannontech.loadcontrol.popup.ControlAreaPopUpMenu;
import com.cannontech.loadcontrol.popup.GroupPopUpMenu;
import com.cannontech.loadcontrol.popup.ProgramPopUpMenu;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tdc.TDCMainPanel;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.observe.ObservableJPopupMenu;

public class LoadControlMainPanel extends javax.swing.JPanel implements ButtonBarPanelListener, 
		com.cannontech.tdc.SpecialTDCChild, java.awt.event.MouseListener, java.util.Observer, 
		javax.swing.event.ListSelectionListener, javax.swing.event.PopupMenuListener, javax.swing.event.TableModelListener,
		IControlAreaListener 
{
	
	//All the possible TableModels the BottomTable can have
	private GroupTableModel groupTableModel = null;
	
	private javax.swing.JComboBox viewComboBox = null;
	private ControlAreaActionListener controlAreaActionListener = null;
	private ControlAreaPopUpMenu controlAreaPopUpMenu = null;
	private GroupPopUpMenu groupPopUpMenu = null;
	private ProgramPopUpMenu programPopUpMenu = null;
	private com.cannontech.common.gui.util.MessagePanel ivjMessagePanel = null;
	private CompositeJSplitPane compSplitPane = null;
	private ButtonBarPanel ivjButtonBarPanel = null;

    
    private enum ConnectionStatus {
        UNDEFINED,
        CONNECTED,
        DISCONNECTED
    }

    // Flag for connection to load management server
    private ConnectionStatus lastConnectionStatus = ConnectionStatus.UNDEFINED;

	private javax.swing.JTable ivjJTableControlArea = null;
	private javax.swing.JTable ivjJTableProgram = null;
	private javax.swing.JTable ivjJTableGroup = null;
	

	public final static String LOAD_MANAGEMENT_NAME = "Load Management";
	public final static String LOADCONTROL_VERSION = 
			com.cannontech.common.version.VersionTools.getYUKON_VERSION();

	private javax.swing.JScrollPane ivjJScrollPaneControlArea = null;
	private javax.swing.JScrollPane ivjJScrollPaneProgramTable = null;
	private javax.swing.JScrollPane ivjJScrollPaneGroupTable = null;

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
    
    lmDisplayRefresh();
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
public void addActionListenerToJComponent( javax.swing.JComponent component )
{
	if( component instanceof javax.swing.JComboBox )
	{
		viewComboBox = (javax.swing.JComboBox)component;
		getViewComboBox().removeAllItems();

		// ALL getter for TableModels return the initial table model
				
		//ALL Display
		LCDisplayItem d1 = 
			new LCDisplayItem( ControlAreaActionListener.SEL_ALL_CONTROL_AREAS);
		d1.setLocalTableModels(
			new TableModel[] { 
			        new FilteredControlAreaTableModel(
			                                          new int[] {LMControlArea.STATE_PARTIALLY_ACTIVE,
			                                                  LMControlArea.STATE_FULLY_ACTIVE,
			                                                  LMControlArea.STATE_MANUAL_ACTIVE,
			                                                  LMControlArea.STATE_INACTIVE,
			                                                  LMControlArea.STATE_FULLY_SCHEDULED,
			                                                  LMControlArea.STATE_PARTIALLY_SCHEDULED,
			                                                  LMControlArea.STATE_CNTRL_ATTEMPT},
			                                                  getControlAreaTableModel().getTableModelListeners()),
					getProgramTableModel(),
					getGroupTableModel() } );

		LCDisplayItem d2 = 
			new LCDisplayItem( ControlAreaActionListener.SEL_ACTIVE_AREAS );
		d2.setLocalTableModels(
			new TableModel[] { 
					new FilteredControlAreaTableModel(
						new int[] {LMControlArea.STATE_PARTIALLY_ACTIVE,
										LMControlArea.STATE_FULLY_ACTIVE,
										LMControlArea.STATE_MANUAL_ACTIVE,
										LMControlArea.STATE_FULLY_SCHEDULED},
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

		getViewComboBox().addItem( d1 );		
		getViewComboBox().addItem( d2 );
		getViewComboBox().addItem( d3 );
		getViewComboBox().addItem( d4 );

		getViewComboBox().addActionListener( getControlAreaActionListener() );
		setCurrentDisplay((LCDisplayItem)getViewComboBox().getSelectedItem());
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
				getLoadControlClientConnection().write(
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
				getLoadControlClientConnection().write(
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
public void buttonBarPanel_JButtonStartScenarioAction_actionPerformed(java.util.EventObject newEvent) 
{
	showContScenWindow( DirectControlJPanel.MODE_START_STOP );

}

/**
 * Comment
 */
public void buttonBarPanel_JButtonStopScenarioAction_actionPerformed(java.util.EventObject newEvent) 
{
	showContScenWindow( DirectControlJPanel.MODE_STOP );		
}


/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 5:05:29 PM)
 */
private void showContScenWindow( final int panelMode ) 
{
	Hashtable allProgs = new Hashtable( getControlAreaTableModel().getRowCount() * 3 ); //just a guess

	for( int i = 0; i < getControlAreaTableModel().getRowCount(); i++ )
		for( int j = 0; j < getControlAreaTableModel().getRowAt(i).getLmProgramVector().size(); j++ )
		{
			LMProgramBase prog = 
				(LMProgramBase)
				getControlAreaTableModel().getRowAt(i).getLmProgramVector().get(j);

			allProgs.put( prog.getYukonID(), prog );
		}
	
	final JDialog d = new JDialog(SwingUtil.getParentFrame(this));
	DirectControlJPanel panel = new DirectControlJPanel( allProgs )
	{
		public void exit()
		{
			d.dispose();
		}
	};


	d.setTitle(
		panelMode == DirectControlJPanel.MODE_START_STOP 
		? "Start Control Scenario"
		: "Stop Control Scenario" );
		
	panel.setMode( panelMode );
	
	
	d.setModal(true);
	d.setContentPane(panel);
	d.pack();
	d.setSize( 900, 400 );
	d.setLocationRelativeTo(this);

	if( allProgs.values().size() > 0 )
	{
		//init our list with the first scenario in the ComboBox
		//if( getJComboBoxScenario().getItemCount() > 0 )
		d.show();
	
		//destroy the JDialog
		d.dispose();

		if( panel.getChoice() == ManualChangeJPanel.OK_CHOICE )
		{
			MultiSelectProg[] selected = panel.getMultiSelectObject();
	
			if( selected != null )
			{
				ResponseProg[] programResp =
					new ResponseProg[ selected.length ];

				for( int i = 0; i < selected.length; i++ )
				{
					programResp[i] = new ResponseProg(
							panel.createScenarioMessage(selected[i]),
							selected[i].getBaseProgram() );
				}

				boolean success = LCUtils.executeSyncMessage( programResp );
                
				if( !success )
				{
					final ConstraintResponsePanel constrPanel = new ConstraintResponsePanel();
					OkCancelDialog diag = new OkCancelDialog(SwingUtil.getParentFrame(this),
					    "Results of Constraint Check", true, constrPanel);

					//set our responses
					constrPanel.setValue( programResp );
					
					diag.setOkButtonText( "Resubmit" );
					diag.setResizable( true );
					diag.setSize( 800, 350 );
					diag.setLocationRelativeTo( this );

					diag.show();

					ResponseProg[] respArr = 
						(ResponseProg[])constrPanel.getValue( null );
						
					if( diag.getButtonPressed() == OkCancelDialog.OK_PRESSED
						&& respArr.length > 0 )
					{
						LCUtils.executeSyncMessage( respArr );
					}

					diag.dispose();
				}
                else  {
                    /*Both an Observe and a Check with no constraints violated will return a success of true
                     *Let's check to see if it was a Check that didn't run into any constraints. 
                     */
                    boolean checkedButNoConstraintsViolated = false;
                    for(ResponseProg response : programResp) {
                        if(response.getViolations().size() == 0 
                                && response.getLmRequest().getConstraintFlag() == LMManualControlRequest.CONSTRAINTS_FLAG_CHECK) {
                            checkedButNoConstraintsViolated = response.getLmRequest().getCommand() == LMManualControlRequest.SCHEDULED_START || response.getLmRequest().getCommand() == LMManualControlRequest.START_NOW;
                            break;
                        }
                    }
                    
                    if(checkedButNoConstraintsViolated) {
                        final NoViolationResponsePanel noViolationsPanel = new NoViolationResponsePanel();
                        OkCancelDialog diag = new OkCancelDialog(SwingUtil.getParentFrame(this),
                            "No Program Constraints Currently Violated", true, noViolationsPanel);
    
                        //set our responses
                        noViolationsPanel.setValue( programResp );
                        diag.setOkButtonText( "Resubmit" );
                        diag.setResizable( true );
                        diag.setSize( 800, 350 );
                        diag.setLocationRelativeTo( this );
                        diag.show();
    
                        ResponseProg[] respArr = 
                            (ResponseProg[])noViolationsPanel.getValue( null );
                            
                        if( diag.getButtonPressed() == OkCancelDialog.OK_PRESSED
                            && respArr.length > 0 ) {
                            LCUtils.executeSyncMessage( respArr );
                        }
    
                        diag.dispose();
                    }
                }
			}
		}
	}
	else
	{
		JOptionPane.showMessageDialog(
			this,
			"There are no programs in the system, unable to use Control Scenarios",
			"Control Scenario unavailable",
			JOptionPane.WARNING_MESSAGE );
	}
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
		    tableData[ j++ ] = getProgramTableModel().getValueAt( i, k ).toString();
		}
	}
	
	return tableData;
}

/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 10:18:56 AM)
 */

private void retrieveAllControlAreasFromServer() {
    LMCommand command = new LMCommand();
    command.setCommand(LMCommand.RETRIEVE_ALL_CONTROL_AREAS);
    getLoadControlClientConnection().queue(command);
}

public void lmDisplayRefresh() {
    LMControlArea[] currentAreas = getLoadControlClientConnection().getAllLMControlAreas();
    if(currentAreas != null && currentAreas.length > 0) {
        getGroupTableModel().clear();
        getProgramTableModel().clear();
        getControlAreaTableModel().clear();
        for(LMControlArea controlArea : currentAreas) {
            updateControlAreaTableModel( controlArea, getControlAreaTableModel());
            getLoadControlClientConnection().notifyObservers( new LCChangeEvent( 
                                 getLoadControlClientConnection(), 
                                 LCChangeEvent.UPDATE, 
                                 controlArea ) );
        }
    }
}

public void executeRefreshButton() {
    lmDisplayRefresh();
}

/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:41:18 PM)
 */
// This method is used to export a set of data to a file
public void exportDataSet() {
	Frame f = SwingUtil.getParentFrame(this);

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
public javax.swing.JComboBox getViewComboBox() {
	return viewComboBox;
}
/**
 * Insert the method's description here.
 * Creation date: (3/2/2001 9:55:18 AM)
 */
public String getConnectionState() {
    StringBuffer title = new StringBuffer(LOAD_MANAGEMENT_NAME);
    LoadControlClientConnection conn = getLoadControlClientConnection();

    if( conn.isValid() ) {
        title.append("   [Connected to " + LOAD_MANAGEMENT_NAME + "@" + conn.getConnectionUri().getRawAuthority() + "]");

        if (lastConnectionStatus != ConnectionStatus.CONNECTED) {
            getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, 
                    "Connection to " + LOAD_MANAGEMENT_NAME + " server established. " + conn.getConnectionUri().getRawAuthority(), 
                    com.cannontech.common.util.MessageEvent.INFORMATION_MESSAGE));

            lastConnectionStatus = ConnectionStatus.CONNECTED;
        }
    }
    else {
        title.append("   [Not Connected to " + LOAD_MANAGEMENT_NAME + "]");

        if (lastConnectionStatus == ConnectionStatus.CONNECTED) { // not connected and status has change from connected
            getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, 
                    "Lost connection to LoadControl server. " + conn.getConnectionUri().getRawAuthority() + ". Reconnecting.", 
                    com.cannontech.common.util.MessageEvent.ERROR_MESSAGE));

            lastConnectionStatus = ConnectionStatus.DISCONNECTED;
        }
        else if (lastConnectionStatus == ConnectionStatus.UNDEFINED && conn.isConnectionFailed()) { // connection has fail on first attempt
            getMessagePanel().messageEvent(new com.cannontech.common.util.MessageEvent(this, 
                    "Unable to connect to LoadControl server. " + conn.getConnectionUri().getRawAuthority() + ". Reconnecting.", 
                    com.cannontech.common.util.MessageEvent.ERROR_MESSAGE));

            lastConnectionStatus = ConnectionStatus.DISCONNECTED;
        }
    }

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

private IControlAreaTableModel getControlAreaTableModel() 
{
	return (IControlAreaTableModel)getJTableControlArea().getModel();
}

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

	getMessagePanel().messageEvent( new com.cannontech.common.util.MessageEvent(this, "The following exception occurred : " + exception.getMessage(), com.cannontech.common.util.MessageEvent.ERROR_MESSAGE) );
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception 
{
	//Observe the Connection to the Client
	getLoadControlClientConnection().addObserver( this );
    
    //Make sure we request all control areas from the server
    retrieveAllControlAreasFromServer();

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
			}
		}
	};

	getJTableGroup().addMouseListener( groupListener );
	getGroupPopUpMenu().addPopupMenuListener( this );

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
public void JButtonStartScenarioAction_actionPerformed(java.util.EventObject newEvent)
{

	if (newEvent.getSource() == getButtonBarPanel()) 
		buttonBarPanel_JButtonStartScenarioAction_actionPerformed(newEvent);
}
/**
 * Method to handle events for the ButtonBarPanelListener interface.
 * @param newEvent java.util.EventObject
 */
public void JButtonStopScenarioAction_actionPerformed(java.util.EventObject newEvent)
{

	if (newEvent.getSource() == getButtonBarPanel()) 
		buttonBarPanel_JButtonStopScenarioAction_actionPerformed(newEvent);
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
		getViewComboBox().removeActionListener( getControlAreaActionListener()  );
		viewComboBox = null;
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
	LoadControlClientConnection clientConnection = YukonSpringHook.getBean("loadControlClientConnection", LoadControlClientConnection.class);
	update(clientConnection, clientConnection);
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


private void showDebugInfo(Object value) {
	ObjectInfoDialog d = new ObjectInfoDialog(SwingUtil.getParentFrame(this) ); 

	d.setLocation( this.getLocationOnScreen() );
	d.setModal( true );
	d.showDialog( value );
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
		if( getParent() instanceof TDCMainPanel)
		{
			//Do a bunch of work to find out if the displayed display is this display
			TDCMainPanel tdcMainPanel= (TDCMainPanel)getParent();
			Display display = tdcMainPanel.getCurrentDisplay();
			int displayTypeIndex = Display.getDisplayTypeIndexByType(display.getType());
			if (displayTypeIndex == Display.LOAD_CONTROL_CLIENT_TYPE_INDEX) {
				// set the frames Title to a connected/not connected text
				final String connectedString = getConnectionState();
	
				Frame f = SwingUtil.getParentFrame(LoadControlMainPanel.this);
				if( f != null ) {
					f.setTitle(connectedString);
				}
			}
		}
	
		if( val instanceof LCChangeEvent ) {
			/*so replace with different message types and use the ids to grab from maps
            to fill the control areas.*/
            LCChangeEvent msg = (LCChangeEvent)val;
            handleChange(msg);
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
	//set all the LMProgram values from the Control Area
	if( event.getSource() == getJTableControlArea().getSelectionModel()
		 || event.getSource() == getJTableProgram().getSelectionModel() )
	{
		updateProgramTableModel();
	}

	//set all the LMGroup values from the Control Area
	getGroupTableModel().setCurrentData( 
			getSelectedControlArea(), getSelectedProgram() );
}

private void updateProgramTableModel()
{
	getProgramTableModel().setCurrentControlArea( getSelectedControlArea() );
}

private int getInsertionIndx( LMControlArea area, IControlAreaTableModel cntrlAreaTbModel )
{
    int i = 0;
    
    //always keep our main list in order by the AreaName
    for( i = 0; i < cntrlAreaTbModel.getRowCount(); i++ )
    {
        LMControlArea areaRow = cntrlAreaTbModel.getRowAt(i);

        int cmpVal = area.getYukonName().compareToIgnoreCase(areaRow.getYukonName());

        // < means the new area is before row area
        if( cmpVal < 0 )
            return i;
    }

    return i;
}

private synchronized void updateControlAreaTableModel(LMControlArea changedArea, IControlAreaTableModel cntrlAreaTbModel) {
    boolean found = false;
    for( int i = 0; i < cntrlAreaTbModel.getRowCount(); i++ ) {
        LMControlArea areaRow = cntrlAreaTbModel.getRowAt(i);
            
        if( areaRow.equals(changedArea) ) {
            //update all the the control area's
            cntrlAreaTbModel.setControlAreaAt(
                    changedArea, i );

            found = true;
            break;
        }
    }

    if( !found )
        cntrlAreaTbModel.addControlAreaAt( 
            changedArea,
            getInsertionIndx(changedArea, cntrlAreaTbModel) );
}

private synchronized void handleChange( LCChangeEvent msg ) {
    
    if(msg.arg instanceof LMControlArea) {
        LMControlArea lmControlArea = (LMControlArea)msg.arg;
        
        if( msg.id == LCChangeEvent.INSERT ) {
            LMControlArea newArea = getLoadControlClientConnection().getControlArea(lmControlArea.getYukonID());
            getControlAreaTableModel().addControlAreaAt(newArea, getInsertionIndx(newArea, getControlAreaTableModel()));
        }
        else if( msg.id == LCChangeEvent.UPDATE ) {
            boolean found = false;
            LMControlArea newArea = getLoadControlClientConnection().getControlArea(lmControlArea.getYukonID());
            for( int i = 0; i < getControlAreaTableModel().getRowCount(); i++ ) {
                LMControlArea areaRow = getControlAreaTableModel().getRowAt(i);
                    
                if( areaRow.equals(newArea) ) {
                    //update all the the control area's
                    getControlAreaTableModel().setControlAreaAt(newArea, i);
                    found = true;
                    break;
                }
            }
            if( !found ) {
                getControlAreaTableModel().addControlAreaAt(newArea, getInsertionIndx(newArea, getControlAreaTableModel()) );
            }
        }
        else if( msg.id == LCChangeEvent.DELETE ) {
            getControlAreaTableModel().removeControlArea(lmControlArea);
        }
        else if( msg.id == LCChangeEvent.DELETE_ALL ) {
            getControlAreaTableModel().clear();               
        }
    }else {
        /*Must have just been a program, group, or trigger change by itself*/
        updateProgramTableModel();
        getGroupTableModel().setCurrentData( getSelectedControlArea(), getSelectedProgram() );
    }
}

private LoadControlClientConnection getLoadControlClientConnection() {
	return YukonSpringHook.getBean("loadControlClientConnection", LoadControlClientConnection.class);
}

}
