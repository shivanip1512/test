package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (1/20/00 11:51:54 AM)
 * @author: 
 */
import com.cannontech.clientutils.commandlineparameters.CommandLineParser;
import com.cannontech.tdc.spawn.SpawnTDCMainFrameEvent;
import com.cannontech.tdc.bookmark.BookMarkBase;
import javax.swing.UIManager;
import com.cannontech.tdc.fonteditor.*;
import com.cannontech.clientutils.CommonUtils;
import java.awt.Cursor;
import java.io.StringReader;
import com.klg.jclass.page.awt.*;
import com.klg.jclass.page.*;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.createdisplay.ColumnEditorDialog;
import com.cannontech.tdc.utils.TDCDefines;

import java.util.GregorianCalendar;
import java.util.Observer;
import com.cannontech.tdc.exportdata.ExportCreatedDisplay;
import com.cannontech.tdc.commandevents.AckAlarm;
import com.cannontech.tdc.commandevents.ClearAlarm;
import com.cannontech.tdc.aboutbox.AboutBoxDialog;
import com.cannontech.tdc.spawn.TDCMainFrameSpawnListener;
import com.cannontech.tdc.data.Display;

public class TDCMainFrame extends javax.swing.JFrame implements com.cannontech.tdc.spawn.TDCMainFrameSpawnListener, TDCMainPanelListener, com.cannontech.tdc.toolbar.AlarmToolBarListener, java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.util.Observer {
	private transient javax.swing.JDialog textSearchDialog = null;
	protected transient TDCMainFrameSpawnListener spawnTDCEventMulticaster = null;
	private javax.swing.ButtonGroup viewTypeButtonGroup = new javax.swing.ButtonGroup();
	private javax.swing.JSeparator separatorViews = null;
	private static String noCreationAllowed = "cant_create";
	private TDCClient tdcClient = null;
	public static MessageBoxFrame messageLog = MessageBoxFrame.getInstance();
	public static String startingDisplayName = null;
	public static String startingViewType = null;
	private javax.swing.JOptionPane closeBox = null;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JMenu ivjJMenuFile = null;
	private javax.swing.JMenuItem ivjJMenuItemAbout = null;
	private javax.swing.JMenuItem ivjJMenuItemCreate = null;
	private javax.swing.JMenuItem ivjJMenuItemExit = null;
	private javax.swing.JMenuBar ivjTDCFrameJMenuBar = null;
	private TDCMainPanel ivjMainPanel = null;
	private javax.swing.JMenuItem ivjJMenuItemFont = null;
	private FontEditorFrame fontFrame = null;
	private javax.swing.JMenu ivjJMenuOptions = null;
	private javax.swing.JCheckBoxMenuItem ivjJCheckBoxMenuItemHGridLines = null;
	private javax.swing.JCheckBoxMenuItem ivjJCheckBoxMenuItemVGridLines = null;
	private javax.swing.JMenuItem ivjJMenuItemPrint = null;
	private javax.swing.JMenuItem ivjJMenuItemPrintPreview = null;
	private javax.swing.JMenuItem ivjJMenuItemEditDisplays = null;
	private javax.swing.JMenuItem ivjJMenuItemRemoveDisplays = null;
	private javax.swing.JMenu ivjJMenuHelp = null;
	private javax.swing.JMenuItem ivjJMenuItemMakeCopy = null;
	private javax.swing.JMenuItem ivjJMenuItemExportDataSet = null;
	private javax.swing.JCheckBoxMenuItem ivjJCheckBoxMenuItemShowLog = null;
	private javax.swing.JMenu ivjJMenuDisplay = null;
	private javax.swing.JMenuItem ivjJMenuItemCreateTemplate = null;
	private javax.swing.JMenuItem ivjJMenuItemEditTemplate = null;
	private javax.swing.JMenuItem ivjJMenuItemRemoveTemplate = null;
	private com.cannontech.tdc.toolbar.AlarmToolBar ivjAlarmToolBar = null;
	private javax.swing.JCheckBoxMenuItem ivjJCheckBoxMenuItemShowToolBar = null;
	private javax.swing.JMenuItem ivjJMenuItemAddBookmark = null;
	private javax.swing.JMenuItem ivjJMenuItemRemoveBookMark = null;
	private static final String LUDICROUS_SPEED = "ludicrous_speed";
	private javax.swing.JMenuItem ivjJMenuItemExportCreatedDisplay = null;
	private AboutBoxDialog aboutBox = null;
	protected transient com.cannontech.tdc.TDCMainFrameListener fieldTDCMainFrameListenerEventMulticaster = null;
	private javax.swing.JSeparator ivjJSeparator1 = null;
	private javax.swing.JSeparator ivjJSeparator2 = null;
	private javax.swing.JMenuItem ivjJMenuItemSpawnTDC = null;
	// Names of parameters that can be accept from the command line
	public final static String[] COMMAND_LINE_PARAM_NAMES = 
	{
		"display",   // beginning display name
		"view" // beginning display type
	};
	private javax.swing.JRadioButtonMenuItem ivjJRadioButtonMenuItemAlarmEvents = null;
	private javax.swing.JRadioButtonMenuItem ivjJRadioButtonCustomDisplays = null;
	private javax.swing.JMenuItem ivjJMenuItemSearch = null;
	private javax.swing.JMenuItem ivjJMenuItemFindNext = null;
	private javax.swing.JMenuItem ivjJMenuItemHelpTopics = null;
	private javax.swing.JMenu ivjJMenuEdit = null;
	private javax.swing.JSeparator ivjJSeparator4 = null;
	private javax.swing.JSeparator ivjJSeparator5 = null;
	private javax.swing.JSeparator ivjJSeparator6 = null;
	private javax.swing.JMenu ivjJMenuBookmarks = null;
	private javax.swing.JMenu ivjJMenuGridLines = null;
	private javax.swing.JMenu ivjJMenuTemplates = null;
	private javax.swing.JMenu ivjJMenuTools = null;
/**
 * TDCFrame constructor comment.
 */
public TDCMainFrame() {
	super();
	initialize();
}
/**
 * TDCFrame constructor comment.
 * @param title java.lang.String
 */
public TDCMainFrame(String[] parameters) 
{
	super();

	if( parameters != null )
	{		
		startingDisplayName = parameters[0];
		startingViewType = parameters[1];
	}
	
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJMenuItemCreate()) 
		connEtoC5(e);
	if (e.getSource() == getJMenuItemFont()) 
		connEtoC10(e);
	if (e.getSource() == getJMenuItemAbout()) 
		connEtoC2(e);
	if (e.getSource() == getJMenuItemPrintPreview()) 
		connEtoC6(e);
	if (e.getSource() == getJMenuItemPrint()) 
		connEtoC12(e);
	if (e.getSource() == getJMenuItemRemoveDisplays()) 
		connEtoC7(e);
	if (e.getSource() == getJMenuItemEditDisplays()) 
		connEtoC13(e);
	if (e.getSource() == getJMenuItemMakeCopy()) 
		connEtoC16(e);
	if (e.getSource() == getJMenuItemExportDataSet()) 
		connEtoC18(e);
	if (e.getSource() == getJCheckBoxMenuItemShowLog()) 
		connEtoC19(e);
	if (e.getSource() == getJMenuItemCreateTemplate()) 
		connEtoC1(e);
	if (e.getSource() == getJMenuItemEditTemplate()) 
		connEtoC17(e);
	if (e.getSource() == getJMenuItemRemoveTemplate()) 
		connEtoC21(e);
	if (e.getSource() == getJCheckBoxMenuItemShowToolBar()) 
		connEtoC8(e);
	if (e.getSource() == getJMenuItemRemoveBookMark()) 
		connEtoC14(e);
	if (e.getSource() == getJMenuItemAddBookmark()) 
		connEtoC23(e);
	if (e.getSource() == getJMenuItemExportCreatedDisplay()) 
		connEtoC30(e);
	if (e.getSource() == getJMenuItemSpawnTDC()) 
		connEtoC4(e);
	if (e.getSource() == getJMenuItemExit()) 
		connEtoC3(e);
	if (e.getSource() == getJMenuItemSearch()) 
		connEtoC15(e);
	if (e.getSource() == getJMenuItemFindNext()) 
		connEtoC22(e);
	if (e.getSource() == getJMenuItemHelpTopics()) 
		connEtoC31(e);
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 1:05:50 PM)
 * Version: <version>
 * @param value java.lang.Object
 */
public void addClientRadioButtons(Object value, int index, boolean enabled )
{
	javax.swing.JRadioButtonMenuItem menuItem = new javax.swing.JRadioButtonMenuItem( value.toString() );
	menuItem.setBackground(java.awt.SystemColor.control);
	menuItem.setForeground(java.awt.SystemColor.controlText);
	
	menuItem.setEnabled( enabled );

	getJMenuDisplay().insert( menuItem, index+2 );  // Core and User Created Radio needs to stay at the top
	viewTypeButtonGroup.add( menuItem );


	// add a listener for the MainPanel & this MainFrame
	menuItem.addActionListener(
		new com.cannontech.tdc.bookmark.SelectionHandler( this.getMainPanel() ) );
}
/**
 * 
 * @param newListener com.cannontech.tdc.TDCMainFrameListener
 */
public void addTDCMainFrameListener(com.cannontech.tdc.TDCMainFrameListener newListener) {
	fieldTDCMainFrameListenerEventMulticaster = com.cannontech.tdc.TDCMainFrameListenerEventMulticaster.add(fieldTDCMainFrameListenerEventMulticaster, newListener);
	return;
}
/**
 * 
 * @param newListener com.cannontech.tdc.TDCMainFrameListener
 */
public void addTDCMainFrameSpawnListener(TDCMainFrameSpawnListener newListener) 
{
	spawnTDCEventMulticaster = com.cannontech.tdc.spawn.TDCMainFrameSpawnListenerEventMulticaster.add(spawnTDCEventMulticaster, newListener);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 2:51:18 PM)
 * Version: <version>
 * @param newValue java.lang.Object
 */
private void addUserBookmark(Object newValue) 
{
	if( newValue == null )
		return;

	javax.swing.JMenuItem bookMark = new javax.swing.JMenuItem( newValue.toString() );
	bookMark.setBackground(java.awt.SystemColor.control);
	bookMark.setForeground(java.awt.SystemColor.controlText);	
	bookMark.setName("UserBookMarkMenuItem" + newValue.toString());
	bookMark.setFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 12));

	getJMenuBookmarks().add( bookMark );

	bookMark.addActionListener( 
		new com.cannontech.tdc.bookmark.SelectionHandler( getMainPanel() ) );
}
/**
 * Comment
 */
public void alarmToolBar_JToolBarButtonAckViewableAction_actionPerformed(java.util.EventObject newEvent) 
{	
	java.util.ArrayList rowNumbers = getMainPanel().getViewableRowNumbers();

	if( rowNumbers.size() > 0 )
	{
		com.cannontech.common.util.NativeIntVector ptIDs = new com.cannontech.common.util.NativeIntVector(rowNumbers.size());
		
		for( int i = 0; i < rowNumbers.size(); i++ )
		{
			long pointID = getMainPanel().getTableDataModel().getPointID( Integer.parseInt(rowNumbers.get(i).toString()) );
			
			if( getMainPanel().getTableDataModel().isPointAlarmed(pointID) )
				ptIDs.addElement( (int)pointID);
		}

		if( ptIDs != null && ptIDs.size() > 0 )
			AckAlarm.send( ptIDs.toArray() );
	}
		
	return;
}
/**
 * Comment
 */
public void alarmToolBar_JToolBarButtonClearAction_actionPerformed(java.util.EventObject newEvent) 
{
	
	getMainPanel().getTableDataModel().clearSystemViewerDisplay( true );
	
	return;
}
/**
 * Comment
 */
public void alarmToolBar_JToolBarButtonClearViewableAlarmsAction_actionPerformed(java.util.EventObject newEvent) 
{
	java.util.ArrayList rowNumbers = getMainPanel().getViewableRowNumbers();
	
	if( rowNumbers.size() > 0 )
	{
		com.cannontech.common.util.NativeIntVector ptIDs = new com.cannontech.common.util.NativeIntVector(rowNumbers.size());
		java.util.ArrayList blankRows = new java.util.ArrayList(5);
	
		for( int i = 0; i < rowNumbers.size(); i++ )
		{
			int pointID = (int)getMainPanel().getTableDataModel().getPointID( Integer.parseInt(rowNumbers.get(i).toString()) );
			
			if( pointID == TDCDefines.ROW_BREAK_ID )
				blankRows.add( rowNumbers.get(i) );

			if( getMainPanel().getTableDataModel().isRowAlarmUnCleared(Integer.parseInt(rowNumbers.get(i).toString())) )
				ptIDs.addElement( pointID );
		}

		if( getMainPanel().getTableDataModel().isAlarmDisplay() )
		{
			// remove all the blank rows starting with the bottom one and working our way up
			for( int i = (blankRows.size()-1); i >= 0; i-- )
				getMainPanel().getTableDataModel().removeRow( Integer.parseInt(blankRows.get(i).toString()), false );
		}

		if( ptIDs != null && ptIDs.size() > 0 )
			ClearAlarm.send( ptIDs.toArray() );
	}
		
	return;
}
/**
 * Comment
 */
public void alarmToolBar_JToolBarButtonRefreshAction_actionPerformed(java.util.EventObject newEvent) 
{
	// set refresh to true so it doesnt ask for a date on certain displays
	getMainPanel().refreshPressed( true );

	try
	{
		messageLog.addMessage("Refresh pressed for the display names " + getMainPanel().getCurrentDisplay().getName(), MessageBoxFrame.INFORMATION_MSG);
		
		if( getMainPanel().getCurrentSpecailChild() != null )
		{
			getMainPanel().getCurrentSpecailChild().executeRefreshButton();
		}		
		else if( getMainPanel().getTableDataModel().isAlarmDisplay() )
				 // || getMainPanel().isUserDefinedDisplay() )
		{
			// do not relayout alarm models
			//This will only reRegister the client, no screen setup is involved
			refreshTDCClient();			
		}
		else //This will setup the screen AND reRegister the client
		{
			//be sure we capture any changes made to the table by the user
			getMainPanel().updateDisplayColumnData();
			
			setUpMainFrame( getMainPanel().getJComboCurrentDisplay().getSelectedItem() );
		}
		
			
	}
	finally
	{
		// must do this no matter what
		getMainPanel().refreshPressed( false );
	}
	
	return;
}
/**
 * Comment
 */
public void alarmToolBar_JToolBarButtonSilenceAlarmsAction_actionPerformed(java.util.EventObject newEvent) 
{

	boolean soundToggle = true;
	
	if( getAlarmToolBar().getJToolBarButtonSilenceAlarms().getText().equals("Silence") )
	{
		getAlarmToolBar().getJToolBarButtonSilenceAlarms().setText("UnSilence");
		soundToggle = false;
	}
	else
	{
		getAlarmToolBar().getJToolBarButtonSilenceAlarms().setText("Silence");
		soundToggle = true;
	}


	if( getMainPanel().isClientDisplay() )
		getMainPanel().getCurrentSpecailChild().setSound( soundToggle );
	else
		getMainPanel().getTableDataModel().setSound( soundToggle );

	
	getAlarmToolBar().getJToolBarButtonSilenceAlarms().repaint();
		
	return;
}
/**
 * Comment
 */
public void alarmToolBar_JToolBarJCDateChange_actionPerformed(java.beans.PropertyChangeEvent event)
{

   if( event.getNewValue() instanceof java.util.Date )
   {
   	//be sure the new time has 0's for hour, min and sec
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime( (java.util.Date)event.getNewValue() );
		cal.set( cal.HOUR_OF_DAY, 0 );
		cal.set( cal.SECOND, 0 );
		cal.set( cal.MINUTE, 0 );
   	
	  getMainPanel().executeDateChange( cal.getTime() );
   }
   

}
/**
 * Insert the method's description here.
 * Creation date: (2/28/00 4:00:59 PM)
 * @return boolean
 */
private boolean checkDataBaseConnection() 
{
	if ( getMainPanel().connectedToDB == false )
	{
		closeBox.showMessageDialog(this, "TDC is not connected to the DataBase, restart TDC to attempt connection.",
										"Message Box", closeBox.WARNING_MESSAGE);
		this.repaint();		
		return false;
	}
	else
		return true;
}
/**
 * connEtoC1:  (JMenuItemCreateTemplate.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemCreateTemplate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemCreateTemplate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (JMenuItemFont.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemFont_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemFont_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (JCheckBoxMenuItemHGridLines.item.itemStateChanged(java.awt.event.ItemEvent) --> TDCMainFrame.jCheckBoxMenuItemVGridLines_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxMenuItemVGridLines_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (JMenuItemPrint.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemPrint_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPrint_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (JMenuItemEditDataSets.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemEdit_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemEdit_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (JMenuItemRemoveBookMark.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemRemoveBookMark_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemRemoveBookMark_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC15:  (JMenuItemSearch.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemSearch_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemSearch_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC16:  (JMenuItemMakeCopy.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemMakeCopy_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemMakeCopy_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC17:  (JMenuItemEditTemplate.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemEditTemplate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC17(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemEditTemplate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC18:  (JMenuItemExportDataSet.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemExportDataSet_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC18(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}		
		// user code end
		this.jMenuItemExportDataSet_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC19:  (JCheckBoxMenuItemShowLog.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jCheckBoxMenuItemShowLog_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC19(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxMenuItemShowLog_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JMenuItemAbout.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemAbout_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemAbout_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC20:  (MainPanel.TDCMainPanel.JComboCurrentDisplayAction_actionPerformed(java.util.EventObject) --> TDCMainFrame.mainPanel_JComboCurrentDisplayAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC20(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.mainPanel_JComboCurrentDisplayAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC21:  (JMenuItemRemoveTemplate.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemRemoveTemplate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC21(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemRemoveTemplate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC22:  (JMenuItemFindNext.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemFindNext_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC22(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemFindNext_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC23:  (JMenuItemAddBookmark.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemAddBookmark_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC23(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemAddBookmark_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC24:  (AlarmToolBar.alarmToolBar.JToolBarButtonAckAllAction_actionPerformed(java.util.EventObject) --> TDCMainFrame.alarmToolBar_JToolBarButtonAckAllAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC24(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.alarmToolBar_JToolBarButtonAckViewableAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC25:  (JMenuViews.focus.focusGained(java.awt.event.FocusEvent) --> TDCMainFrame.jMenuViews_FocusGained(Ljava.awt.event.FocusEvent;)V)
 * @param arg1 java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC25(java.awt.event.FocusEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuBookmarks_FocusGained(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC26:  (AlarmToolBar.alarmToolBar.JToolBarButtonClearViewableAlarmsAction_actionPerformed(java.util.EventObject) --> TDCMainFrame.alarmToolBar_JToolBarButtonClearViewableAlarmsAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC26(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.alarmToolBar_JToolBarButtonClearViewableAlarmsAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC27:  (AlarmToolBar.alarmToolBar.JToolBarButtonSilenceAlarmsAction_actionPerformed(java.util.EventObject) --> TDCMainFrame.alarmToolBar_JToolBarButtonSilenceAlarmsAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC27(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.alarmToolBar_JToolBarButtonSilenceAlarmsAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC28:  (AlarmToolBar.alarmToolBar.JToolBarButtonClearAction_actionPerformed(java.util.EventObject) --> TDCMainFrame.alarmToolBar_JToolBarButtonClearAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC28(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.alarmToolBar_JToolBarButtonClearAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC29:  (AlarmToolBar.alarmToolBar.JToolBarButtonRefreshAction_actionPerformed(java.util.EventObject) --> TDCMainFrame.alarmToolBar_JToolBarButtonRefreshAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC29(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.alarmToolBar_JToolBarButtonRefreshAction_actionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JMenuItemExit.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jButtonClose_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}		
		// user code end
		this.jMenuItemExit_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC30:  (JMenuItemExportCreatedDisplay.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemExportCreatedDisplay_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC30(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemExportCreatedDisplay_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC31:  (JMenuItemHelpTopics.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemHelpTopics_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC31(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemHelpTopics_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JMenuItemSpawnTDC.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemSpawnTDC_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemSpawnTDC_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JMenuItemCreate.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemCreate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemCreate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JMenuItemPrintPreview.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemPrintPreview_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPrintPreview_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JMenuItemRemoveDisplays.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jMenuItemRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemRemove_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (JCheckBoxMenuItemShowToolBar.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainFrame.jCheckBoxMenuItemShowToolBar_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxMenuItemShowToolBar_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (JCheckBoxMenuItemVGridLines.item.itemStateChanged(java.awt.event.ItemEvent) --> TDCMainFrame.jCheckBoxMenuItemVGridLines_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxMenuItemVGridLines_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/14/00 4:21:41 PM)
 * @return java.lang.String[]
 */
private String[] createPrintableText() 
{
	int columnCount = getMainPanel().getDisplayTable().getColumnCount();
	int rowCount = getMainPanel().getDisplayTable().getRowCount();
	Display2WayDataAdapter tableModel = getMainPanel().getTableDataModel();
																
	int cellCount = ( rowCount * columnCount + columnCount);// - ( tableModel.getBlankRowCount() * columnCount );
	
	String[] tableData = new String[ cellCount ];

	int j = 0;
	for( j = 0; j < columnCount; j++ )
	{
		tableData[ j ] = tableModel.getColumnName( j );
	}
	
	for( int i = 0; i < rowCount; i++ )
	{
		for( int k = 0; k < columnCount; k++ )
		{
			if( tableModel.getCellData( i, k ).equals("") )
				tableData[ j++ ] = " ";  // blank row
			else
				tableData[ j++ ] = tableModel.getCellData( i, k );
		}
	}
	
	return tableData;
}
/**
 * Insert the method's description here.
 * Creation date: (1/3/01 1:06:50 PM)
 */
// This method is used to eliminate any sigle/static instances that exist
//  ( ie: clientConnections, clients etc )
public void destroySingularities()
{
	// this will destroy any client sender connection the main panel is using
	// SHOULD ONLY BE CALLED ONCE SINCE THE SENDER CAN ONLY EXIST ONCE
	if( com.cannontech.tdc.roweditor.SendData.classExists() )
		com.cannontech.tdc.roweditor.SendData.getInstance().destroyConnection();

	// nuke any special clients that may be opened
	if( getMainPanel().getCurrentSpecailChild() != null )
		getMainPanel().getCurrentSpecailChild().destroy();
}/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 10:11:44 AM)
 */
/**
 * Insert the method's description here.
 * Creation date: (10/13/00 1:06:50 PM)
 */
public void destroySpawn() 
{
	// this will destroy any client connections in the main panel
	// if we are a specialClient display.
	getMainPanel().resetMainPanel();

	getTdcClient().stop();

	// destroy all other independent threads below
	getMainPanel().destroyClockThread();
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/7/00 9:44:46 AM)
 * @return boolean
 * @param dialog javax.swing.JDialog
 */
private boolean dialogCanceled(javax.swing.JDialog dialog) 
{
	if( dialog.isDisplayable() )
		return false;
	else
		return true;
}
/**
 * This method was created by Cannon Technologies Inc.
 */
private boolean exitConfirm()
{
	int confirm = closeBox.showConfirmDialog( this, 
			"About To Exit Tabular Data Console, Proceed?",
			"Exiting Tabular Display Console", 
			closeBox.YES_NO_OPTION);
	
	if (confirm == closeBox.YES_OPTION)
		return true;
	else
		return false;
}
/**
 * Insert the method's description here.
 * Creation date: (3/28/00 3:43:14 PM)
 */
protected void finalize() throws Throwable
{
	super.finalize();  // NEVER FORGET THIS!!
	
/*	if( getMainPanel().tdcClient != null )
		getMainPanel().tdcClient.stop();
*/
	getTdcClient().stop();
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireOtherTDCMainFrame_actionPerformed(com.cannontech.tdc.spawn.SpawnTDCMainFrameEvent newEvent) 
{
/*	Cursor original = getCursor();	
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
	
	try
	{*/
		// tell the other TDCMainFrames something happened
		if ( spawnTDCEventMulticaster != null)
			spawnTDCEventMulticaster.otherTDCMainFrameActionPerformed( newEvent );
/*	}
	finally
	{
		setCursor( original );
	}
*/	
	return;
}
/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void focusGained(java.awt.event.FocusEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJMenuBookmarks()) 
		connEtoC25(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the FocusListener interface.
 * @param e java.awt.event.FocusEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void focusLost(java.awt.event.FocusEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/16/00 4:48:21 PM)
 * @return com.cannontech.tdc.aboutbox.AboutBoxDialog
 */
private AboutBoxDialog getAboutBox() 
{
	if( aboutBox == null )
	{
		aboutBox = new AboutBoxDialog( this );		
	}
	
	return aboutBox;
}
/**
 * Return the AlarmToolBar property value.
 * @return com.cannontech.tdc.toolbar.AlarmToolBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.tdc.toolbar.AlarmToolBar getAlarmToolBar() {
	if (ivjAlarmToolBar == null) {
		try {
			ivjAlarmToolBar = new com.cannontech.tdc.toolbar.AlarmToolBar();
			ivjAlarmToolBar.setName("AlarmToolBar");
			ivjAlarmToolBar.setFloatable(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmToolBar;
}
/**
 * Insert the method's description here.
 * Creation date: (4/13/00 10:23:54 AM)
 * Version: <version>
 * @return com.cannontech.tdc.bookmark.BookMarkBase
 */
private BookMarkBase getBookmarks() 
{
	return BookMarkBase.getInstance();			
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G53D44DACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DFD8DDCD4D57AB0EDD43452BF3725F20BB6AAADEA292C28E833220DF6ED23DDF7570AF62DE8F3B75B2CF5F72DE54BED2F48A8AAAAAA66C71028282808282828A8A89F0222121262C749408CB0B24C0C43A003223E4FB967BE4F3DF367A2B3886F3B6F7BFB7D7D9E07731C67BCDF67B95F675EAB25AD8EB3CCCCCC13240986C97E37E522A495A6CF144EADCF7DB16242C624D3BE7B3784E036D4111D843F
	C3A1AFA9AE4310AA8D191A8C6998C8473D1CE1188FFF4F15FEE0CC7E8C7E68C6CE7C271234751B1C611717F35DD414B30527D3BAD260F7A640F0400D85A878CDBA484F6AD8A063D3A1BDD37E1B4144C4C90A63B46B92972870DDB2DD847CC6838C7DAD23A96AC81773B3A1DDGE08588782D209D843FD339F657ECAB1069CE7EA0CC9A7F4B5909B318AC95FF048439EC7A6F44EF2674A36E08C9122D1459D8D85D523433415E1A5BE034596CB6173921B957E5EA58362D2634245CE9ECB583CD706F18EE5FA4A673
	F212D4836985A3996E15C4660F91109E83A00DC47DFEB323D0169F5292A6455F13542DFEA7C78A3D82C6AA7A450E8AC84AFB1571EF667EE66522A1BF81C068FE7DF74BFB50FD3F51E27354FFE8876A6FFA9575FF3B09710F00F48C40A8425F5B4C7063A11D72DA3AA16BB534EB47CA342B767C43D2C2FDFE622CEE6CAAFCCD58E3FACD312B785781C98BF99C4D1D4EE2A18C72E2G62GA6G243F1EEEA8847812476F2F262E065FDD752E92FBEB2B5D364B6158EDB5B9775BCDE6AB7C3DED9B644838C366863B53
	A4C9A2C60FA766686803D0733AE7FCB290369D15B026A28E5DAB39DC2B938DBA36C53EBAB731D96651753AE21BAB0E794DE8E0FE8B85BCAB5744FC4D71F7F0FCD41C0297757BBB9EBFACBD700DF4C36CA874F36DCC5425AA69B6A9F57D6644A9BA3A8C7A591A4464F8D907F8C59761638FD3180F2BG5F8C90GB088A08AA0AE9EFD3CE05A461EFCDC89F16534992D075B6B9DC627EBF73B45EAB2BBE5DF5BBAAE67EBBD90653A7CAEA77C923ED1DDC7239E5B246B17D1610B92474B7EC8F9C7710B107DF83732F3
	5E710E0D19D40707F91BA8FEC75461A442FF4B7155AA3C285BBF71364552F5109783A068771EE6786CBF8A7AA26263647821AA3C6093417933F4413B69067CBF259BC47D8E18497A4A3C77528DD5GA600AE00D000A8005877300E9B262D73319DD52B6AD714CD1F7E857C1A5DAE2731525666B25A9A4CC70CCE0B315EEAEE13E41A655319DF6BBD4A2D666D3B81E46CB15B4CCE234BE23781F7680EA0185A844D466933990D534CF3EBAC2DE690506A10106F7669F36157EAECF3FD63B099DDE605C26417CCE7B1
	F156F884B8828378BBFD8C7357995ECF0F857B1347202F22E6B1DC9D245DE350AF4FCE1F833F7BA0384D56ED5BCAAD8DCC8D233393E33449908B3F41C01F7DBE7898E084409800403FC0BF75C13AA19EA0E0EC3AA17343F443B800400F407FG11GA3G46GC8FFCDB78C86A89D87FD9CC0645F528D35FF875D7E11EE08G289D8FB49F279B4281CEFE12EEC87BA7708588C900B27F02BE9FA06AD39887BF037EF18214818878B724BF871D7E8332258CC3BC40500999065A448CC382C068248CC3E9D206E19CC0
	32A1431085D088A05A50C7355806CAE78C11F2DE4D57EDC33C0F07B26C57A17FAAED689E6E1BC1000F820883C8GD8B0B943D0GD08B608698849085908710G10BEA543D08CE082888208G0885988B10121CE14881288230828483C4G44820C85C8198A79GD5GD600E0008800D80031GA953A09F208A408A908C9081908BB096A0E5BA6483D481D881028106820C84C8G90F63D1118878D16551356972C0E7C692F90D8CC30BAD463D87D30FA6C29AC0B151EE8D8FC31B842B40B83D677AC7654F4ACFE70
	EF962B5D71CB166B1AC13ACAC7964FACD6F9DCA4AAB4D9B2BDEBA7175313359BBE1E3D6FCD4762D72F8CEB8F78B7EBEF2C5D703FE5192CDDE0BEEBAF78B7EBCB257F6ADDFD798A2C1D33FEC00DCBC85437C38B2C4F504333F61FA97B0275AD6C37D4A796F9ECFC54BB7BD89F467A9E56F7303E86712C0FE37D8A26B36598E17D8C45CD1227FD2CDFE33F226D04CFE2EDE7150E4CCD3D74B16B3BCBB438EF817679D0F60D8FB44BG562B52C781CEC9E26D23265B237AFB7EE578B5287E2ED47D3D98E0237C77B195
	5EAC7FCE6AC14F69726F941D3CEF3AA933D575B71B232F157FEE12FF4BD479C52A3F37G6C6AE5FD798AEC7C1D21411D70316C5CEE70BBG8E4BFF4F16FFD7F6C31B51CB3D59784966D2DF83ECD76159BAE13FAABDCF7EBD284299B53C164B3FE2FE32E2B61B632527407889D08BA066A87F1A1EA971395BEEE31B99E7EB871DB051B21B14990B207DA4055533792C5966AAB5BB4CB6937C51A64F157E1D42F41767F10DF68967D813E6B0BC1B571740C42B0D70D3E74C96F860211E95E27EEC1EEFF4391C167AF6
	177930596532581A14728BE7B0DD5ABC168CED322D59F3182D8E30B188E09059FA778CD637B89F2B325BDB5A9DB5EC26A94F6BE2797AB58268E3D2F03CBE564C76A746D03A0127157C8F1AEAFBD14F0C1FAFF4C2C6D2F3EFE4745466BDE5D41F6E1263EF2E9C57AC46D89F475AAE6B13564A31447A1AE5F2FC31985DAA47A16B17D99BE16D1F35EF567ED9DF447A25CDF27C327E2BC392FD40BEB9E6874871AE74F0F3BDA600FF32E640DC8A4085908A9083B096A0EDA64CB581CCG41G11G23G12E6417C8B20
	86208BE0B0C0AC40F80074D41833GD881C2G06810C86C8191DE14887B889908890B19B6B755166F193755B27DEFFB1CF7651C295FE317C0B3EDAA97BE80D247415B3D57E59A27BF23B6C47A2C969A755FD69BE591787E53F29F5B926C99BA5E54CE812AE9F879DBE464B00DE44627FADA06AFE3DC52F6E755A936B17D9CC4C12BC478D6C1FB1A698DFB666CF171431E3269C876B643AE7E3866B27D83C30318B479675FC0745871BCF3058D20F298C34739196C7B8A7D8AEDD5EEEDF67CFBB7E8B6A283FC00C91
	B74F6CFE0C8B1D29BF465DBDD3FF0C8B1FF979B1EC68CC3DB1CC64BDB91345163D43269A93074DE47B898DF61B4D4C87E6AAA209BD5B0FEA189D961B45F554E2EAB23B8EDB4E4184F042EC36FF9F4477ACC6425FE3E663BA3DE3992E53334E5DA5C51F1A253B9FB9E8C8EEE2719CB1DE554CD15669C227E7E6313156ECB5370223CA6C6DB617403F4077C3E4FC5BA4610FD383D9596C3D7B4D3676CA1739F5174914E36426640A1FEA3333516EECB51B32CF9B4F9AF30D9D2EDCDE95393BF8F6997BFBD5665A35BB
	B3559B50B927D58CCF6D52F02A6A1E5576D5C7D6ED913A3D3A0D35593D070CA60BFDF73B4BE537A14F7512E87BF3A53729DD682FC7D5G16B4003DCE234B6E54D1D1378CA8CBE504226E5C368E88125CFD664E43AE273D454CF4DC2F68D034F39A53D3251C40574F3A51533765FD6B59F23B4D652FEB374A3ADDDD94A1F4ABE9B6B7346C363B116911BDCE0B294AE2B33765D8A0194B7EBB6CB23A4CA54DC6DB13372E2CCC371AF64F7CD425066FFAAF4D09B3F777D621377968A0CF1F1619AC2ED2234B88AD793F
	A5EEE7EC88715631EF15E39C355AF9FBD023DE7EFA215F1B0BFE78FFC23F1E5513E37360569738FE3B2CC6E7EB0D5DEE5DEDF46698D5095C3D729F226D6EEAE859E5358AEE27F475EFF7D92C39DC50417A5350D16B282E66FF4A8F7ED53A8238BD7932BD1F55DF5B03BDFE29D3E2B59B1D1C177FAA65E15FE4B8B7957B2CF20B55CCFD19B1AB85712516B6075558A9704FEC1D09785D30C8EBB5BADB5A30CC34194A9CF4B0D564316555D212D1E136BAC8C6DE46AD1E71D7420E834CFD9B0102275FB108BA6EB8FE
	13266F064E763F217B16753BE375CF7E3B7A6F1E3AEFD96F665D5D68DD715F29F71E2C7796474D1AF9D23D3D3D2FC79C46525F0858204CC3B6955E6329E33553E2F3D5BB4DE7AD660E3ED5D54D59DF0DB7E2BF9F3D76E69DFD7BC1D1FFB5D44D6D7E725D1D1EBA9EB2375A4F1A653E312DEF15953C7D0E008D58A72C3BDDE7EE54AF1A76E6FCDF0FFA3E532051F33F3145DCE2F7F4762D0E4855EFBDB749FAE6F68D5178536D30BB7BE73669415AEF0D37D03C9EEF3EDF3FFFBD5CEC6F2832B7750D565DB077DB6F
	8252BB3B7A992EB793D2ED34192DB932C41E485DDBE2EF2D371734BB595E349C52D7B861EBC566277CE05E7D54EA43D632E5EA59FDFA731B9AF3ABF06E1FF98E72763B6FA810F5FEFE4BBD5EFDC77FE82C666C7758017308EBB26F536B177BC7E3CF5EFE7B9867902F2FF9247B36C96B027EEA1F3818705763F99457F332F4FD4E56837BE1BD509FBEC75E7EEA3D8967986E343B3C7637F08153677B854858EF9FEFA09F97FC75BDC55FA3B03BEA4BE934B7343765727F7700D8EF854BD956E575630CCE152BD8E8
	B5D2F8F005E89D343AF478FF5BDA1C9943CE88FB31A097B68D2EFD53AF1B78021B24B23A367EB350F7E117990BB6D1BC85DE780DDF369E36D84D3606FE375297B11731EFB35977E45A0BFE59F7485C68B437B5771FE51797503DCD9B15395013FB9E531BF70A115EA4CFD07AE37A69A9416F3D939C1BCFB5BD6FFF7B6A679665F7FDECD266F3CFCD562CD78EBB0C9DB6905F37F500DC7D1EDFEC11752C7CCE3B2F6B6E73FDDD377F6B3E0D28DF7A62DBB5FE040AE9E86EE3AFF21EFE47AE4E0397B4E96A3A5CE2B3
	9DB03B7BFA6FCF666A2F1E1B310DBD1E2E5917E0BB2CB5F60725210FD77A8ADF3FFD0A735E3845348F4C667E542E02DBE8CF197667B06F6BA9F426469AB361135A288C4E475B70BED563DC368F6D1A13E18882881B03F70C0AD2D9BE5EB112AFD215582DD6F1E48BF98B66327358491353A4650CB58A4A33E71D12A0CF64DF6711DFB7D7644F24720F69169FC8657327DC3EFC5D943D7203297CBB3A79D114DF1F2C179FCB796F6A6647D33EE92ADE7EF84A7F13EEFEF29A66B7CE534BCF07FC71AC58FB53544F02
	6525097CE6786DA29A578CBD1A90788DA71A9529FAB45170BB12E89E495623998B3F52BC245936C60FE68164674CC31B462E5533291472DBF37572EBA97FA35DFC254E972D534B37D2795BF57383A8BFBDCFAFBF04726F504DD7643F241B9FCE65772E3FFC79333A7943287CBFFBA83FEC03DE7E88AABFD8B7FFB465AF5FF8F97EDF7710FF21077C7BFB48FF35077C0979174FDF2B1B1FC076BD54C3795F7710BFF9536573B774107FDD8F795F6FA17F099E72BF6CA1FF61664B67177510FFC6B7BF658B6C837E3E
	D92F8F48047CFC2231EE552329027CBA22F9E63B9E0D9B720309E64BCEBD1A41109F75850ED5B933D361374DFC261D2D0F589AF01F39931F1D1ABDC713F744E8EDD71E495ABE1B5DE5332A6E508AFC6E9C7D7BC079F3583D599662DFB0E732441E2D92BC457D2521C646536134349A1D1D6CD2307CDC6F3B9DEADBC6007E6C996071701BBCBFC3FEDE75019D6C3E91BED2BBFABE2B0FDF1C26E7FDA1DDGD0BBDF149DB4DF5CE392F483DA10AEEFC106218220F901208BDF2026BBE8C53AF100CFDE18E148DEA868
	CC8B55F4F3DA11EE9860C7F6CB77F3229B8F7814C5B096AC92F456C5EA3A6F5B10AE8A70A3D4F463BC68F65A112E78CB3075CB054E7D251A6E6FC4978378D1AA3A899EF477BA102EA29D62AFDD218BC8D7539DBF03F443819F8FA0E5883A628CB55DD7CE248B867CE0009819AE69ABB55D9F092E96708E0001DF893A919EF47736A1DD56628CC3A1C05DE2C1B7EC311A6E0C8B6996E4E69872G2AB3855D40CCB55D26F6249B8D7889AA3A74A5EA3A77094E8A7840A58A5DE08F3ABB4EA2DD8260D317026C25024E
	31D4CDE724E7520783BE9EC0DAA67BEF191AEE399B69C2G9F31CC210B7520FB0368523320EF81284D126D4DD2535D5E09F4A381BF1651AC1763EF391A2E719C520DD8AE684648F455AB54F47967A96ED7883A11AB84DD41CAB55D44AE248BDDA968A2D78A3A54ECB5DDAC51B9G9F84901EAD6846F8508D3CC0F13FCA702BDDA568A2D72969CEDCC43A545550A681AAD68B3A2055EA3A451748AF009F8B1032C650D52DD1533DCBF4A1000FG081569D2F254F477C9944F00F7G8C4C116359032EE5A2555BDA28
	B7001A3502AEE42DCC4752521D741E00F504FB02633643FFADC978AC737ED475BB8CCCEDABF56FF7B2484E957C8BF3157B1DE21DF3FB9A63FB927056DC9CB79E1D23FE0EE2D7DB1B3D41429F311547G6BDC755DCF01BBB3172D319AAD4EB6D7254DE4F663B852B117694A1E41155172D87013B56C2E317619E2362433BB71B115128E3E7E2B311758CD668A980ACA68EAAC5B5414703D84BBA6233F82F2716E6B05F2757B93064DCACF1C5E0DEF22E41F4429FC93772B00A421677BDFAC6DD14F4A863670933A7C
	05F4D9308E46EB0E3BF58A622A8937A099F1C1047B9161228937E8AA629288F7BB6172729017BE8DF14D043B03F0E1F9B80E9E1B465E09C06FFD30543766361FED4DB55643429B0C5A36CDBEACE8EBB7DA7990AD5B35F8BA6345FC9B873C44BB9E1ECFE1F30154755056G0A57235C08990C16793BF6BD523671FB4483A19D8E90CD345F25A2ED9C51A670E719D2B7GDF00628DC87BFEB66AD03B81E92FDB4D79C2BA9CA01AE81DEB08AF51EEDD4B79EE843EG459B11F6D9AE7A2916F00389B710F0DF2FC35CF0
	427D16F0D2BE628E67A12E00F052FA44B98877A0614289F7FC83626289778742AD5804B8D3BE45946142B7D3CC91EE759674C374A67443D3854C8F8910CE5D8C3E58CCB6EFC57F96EFC65AE95B992D9B528381420976636D4417E883F7F03E10CE5D82FC37A02DFDA75296EFC15A4F0BB8DFC88F84885F0271792FC58BB0BED55725A1AC793CCF705B6FA659446F8E4EAF81522985A09BC03C7BE09CFFA78263DD8CB8F7812E056E72D88B8F85FC9C4084G31B7F434051D23793F7B057D5B1BE5BB61774AEE90E2
	BFFF775CE2D94F5807EE138AAF7DFB225EBBD302E6CDC9CC5DAA7A30022DCADF967F691969F97F38CAFE9FCCE48B7AA390E806EDC5FFE497EA7DB11E65EDC3FFFCE93DB2FF7CE7E7215A9F3D382FA874F7DBD17F9C504D348D755F6021FFA860E38146117EEFDB59D86A67EDC0D9776AA36CD95CBE3B88A861FBB9363722ADC95BE1FE3B9DEDD924EB0BEBBB4674DF29DC906022295CDFEA35F518GF969056803A336AB2B43DB776ED057E1EF2E98126D0F595006BC502F3990EDE8B92B57AE438A31FFF094162A
	F538023B03C20FA6BB6A9189B246129E535DFAFAE46DC0BD961BF7F86951DBDFFC69C09D8A003FE3876A7062E22D8E611097CBBA9C5D635147746A469F6A70EB52A11E495F09BA384F6B69D03D93F5D8DF6B512EFBF117CF483F7A8C4AB7816FD012EFDE2AA7FF64CE0C693F1C3932180EDCD42C563F17777C048DFB1CE8439850AF3388ED58F4D1AF16EA0B5006071CD7E64381CB19C79DDC4175BF0A4969EDE80B95749CCC3654D4EA7BA6B6CE0CA2DB1CEDD7B8EE3A776875353D3B940831CD360C83BD330BF1
	8FED1CC93D6FD487789000A80058E234E7336B0044771D7A707EA05ADAB213716E5B3B03D8FF3739506651E0CB7AAE2CBFDB39B696EBA04F3D8B6D1D58FEE5753731345CFB5E534B5BEEE84BF76DE8CBB06899C3361C5335E542AE6C1B3E31F90C95D7F0D7D068B167AC6A113ABB43D0319B7518292BC740EE54236B0C07CF2E6886A07A646724CB884809A5DDBEAC53EB1FC9A5D83FEBBB2E2CFEFF3EF8DFF76D33F71782E5FB3E5F0176ACGDD2BCB501EC70FE8FD4B763B02481E3FDD21BDC5EBF56A2617B785
	31EE8E385116D050F3A459723ED14F1614D20C134DCBCABC66D73D3E84A86B115809FAE4020C5AD26C776EF6287B3D01000FE4F9GE3CB512F16CE760EBBFF2E8B8A9B96A496208D74EC176F1785055EB124F7D2994C0B4BD06F5D0DEA3DDDE5223F9E8A3FB1E528771AF32CCF6D5BCB0598AB4F6DB922615DF7978AB1F6EEBC07360F821B524A512ECF4E1D383CDDD7FA31D0367119655FDD5E46BE38D30836FEFB9EED2D82BB83495618735FDC5E565EDFAC1430AE4F6F2E3D3C1DD7F42BD058373A8B6D9B8636
	0DA77BFE50F57072767566E221ECD7576CC317372B17F78A314EBEA61BF276407AFB8F76ED3F59236D5BC2A1AFFA8F5AFC7242157553B795D6F94FF12F680E213067298BE84FC850B52582754D3C385F0776563B585BD5DE6DC39B6BDD4C85DEC4FBCA41162E8A2C1FFBBA7456A011E46F87972F2CFE22CA0B5575534B7B0742060ACBE843F050CF2A24BDAA5DB5F9C1A55A707025AB3361CB4DDA567F7B09C27F94097AB550AD1074EFDB2E37169DC67A37C9D7267F3747BC7683FAF5FB915B798832E18468173C
	97EDD82FEBC371DE34E14B44AB336151B20FB86A655DC6E1C348C4342186748BA69BDEAF525BA70BA19BBE3BC29B62EBF62BED68752DC7FA2F748675B9DFF47C175D1E73456D957B3EE377EA4F79AEFD41764C137741DAF09F1E737DFDAEFBFF8D1E739D02EE44E8EB624F6F0B7C30B4660F96F3271C9E1C466472BB9D923ECBE977E4FF5E431C873205BE436633BAA805F49D009BE0A040D052ED4D3C9E5FABDD663E4CFB257F6A17DEA37661FB41C22778D3EEB4154BC87627DC82157B31DF65D22A305C62297E
	144B24F2E1FE154B27F2CB2679D32E024A5D6DD73913D4AEEB3ADEB9876487828C82082872FC471EF8877626992C4DC7C39EEB9F93893F1363E3D5F851C77CA2855F0F9F87F9057BD17EA3B3547243768BD91170BB9CE09440387DFA725F63671CC987347247F1FC6A812D7C4ED9A8BF9D72828E50FB88D335760F063C840014039906CC007C03FA721D590CDF45C12D7C739CDFF3D0AB7F7359A83F8E72E28E227C0F32757C1FDD8DBE822886B0D56B497F0C1F473A2B35721356B0FCE035D67E8D2BD17EC0489B
	DF0D72BB56286557FEADE4D961B7GA084A07CEBBD798F72B35EE1DFEB65BF4171433F564AAFDA0B72C7C2DE41A1141F1D6BCF5C06D2397B7DAAB7144A9DD967CF3998AA773ADF656228DCED1EBF6546D2B943FAFF4AC90769FD0BFE15CB23F227B678D3AE1B4A457BD52E104AD9727DA9D7CD659E5C6CCFB99315DB37C5DB4E8DF941GE1G11G438F6B45795A2DACEEC79D564679E60E9FF3D89B674F95E01C0F073C5A9A0A572DFAFA8F037C91G238192EA7464DF647782D23E514A3F0E6397FC23151F3A9D
	65E7C1DE50B7A87F536DDA79E3A1CFBA12E1C8834886A8BC22A7BF015FB528BE2215BFF1A7431FBC22157F7D9DA83F9972E20F20FC57CE2D7C3C239906D200DAGABC040D15DFE3618718BB96A554FF2FC58D12D7C24E2149F81F92935A87F101B49F7C1BA88A03456D356D0CDBA8652C24EC1A36397D71B21791644B10E9F23428B7977946137A492A06F64B1147F49E92D7D5147BCE50E522465F75636E84BFD25C9CB47BD4B093971F0BE3F53395F0133E579CEF075E63DBD4A8540B367B85EAB0963F7EF583B
	B4CB0F633CF24EC2F647AE9C52B1G23G92G52CFC05F8EF072844EAF4F4D533BFF66B93F6C6E721972AEE3610B1FFA7942B79F8E6F5107DDA7BC4B895F5CD52B671B90208DBF013EB9D5C8F7E4CEE0DDDF3476CE4FE02FF2DABD93CE6855758B33166957357674C036EBDF2156AEF6F611FAB24310F5926D3210DD05A7512EDD2D3D336B20D7B92DDD2E131E6564FBE024C3B06970F1AFF518E46BC9071813FAB170C925DE8C0C825AF1642BA54730FDA4FF0BFABE65A54FB7BDFF51231E56EF75FCF577D93CD3
	96C8BA345AFB2743F92FF2DA9D06696A301B6E0B0DA49D16B8FA2743BA2FF2DA9DD23E532B2F37A66B55D7965096FC0775B5E1B16AD97D9D6A79EAAF75FC37C7BD033E5373554367D107B052617AB33D5361D62FF2DA9D62F4F588DD0ABA0CA39D76BBFB2743892FF2DA9D32CF6969E03DC8F796CF218E335BFA2743E22FF2DA9D5C3ABA8C28C49D86128E2F76D207F7FB54E178A93DBE7C50973EFCA70E5FDF10B056879A55319EFFCA3CEFBF298E466FBA0C75ECB35DE12E43BB1CB772F7BFB6C3BAGE0D09D6D
	D938FAE773C3DE6534B68F2D53EB2BB9655A364A4EB206836DA852FFC8B976EB63C9CFD3FB6F74F4FA15536A19E3540B0FC7C807D2A36A3060EC6FF4D861D54EEBAE222BC3996990C2BA4476D2075F7728C32C2E8ED96558CEC6138E833D3E51661B8EBF76AA2755A13DDECF07770F209F726BD1079AF76FF42877AA67B5566A6A10D9CFE3AD69105C59BB9D3E70AA67B556566B7597D36733F762F7B33F170A9B78F3466731FD0D849EE38152G1F5DA07A89E14B6F9DE8CBD083768FF9FCED938D69B800F18DE8
	63AFFBE9E3DC0FB6A61B74EC7C38EA2D54ED1F186B325B2D75C6A7ACA1D487FCD4C7679A13E5BE19A634AB07FF672F96528E00A0935AF573395E59F50FD7B92DDDE1A63DF28D67FBA76F0CD7B92D3CA8DDF9F9DD3D1337532B1CD65E88DDF91F75D25E548BBD499B2311A74611B995FAF3BE498C71EE46B8CF2E403EAB470C757E1C17BC5F74FC25C7BDCDE63DF25F3F58BBF9033C4AF90D99E63DFE6A779DE8F38859FC60D26FF4B869D54EEB4C505561149B7B175124439C29F7BAE4FA15739AB39A75F4F8E0B9
	7AA13F91F5F82D17BA0C6ED1872B2E8E4D24C3A069F05B445E69906ED54EEB4C5055A1208E5729A39BF19F675BC58CD75014E128E842BD1B5767B19ABC2F2C36DBEC6C7DAFA2AF708B763C395DE9E9325890F7BD47B5581D7CA39B629BD0477873F9354053C5FCE366F77FCD1017676BFF9364B773753F89727A7C4BFF776A6D795DFDB3C468B7F98A6A9784A0F00F91AE14F07949089BCA38478997C3384DD39197C738A789B716F0855390A7B5A36EA9422591EEBBFF8EB09B5285048B6338D2C81FA45CCB9C67
	00F490612ECD615F5F02F4CCB3465AF6D625F27C142A7EFEA525B75F447295040CBF772B8CE173F07EACE49C58BB067C70379968438904BBB493FD18ECC15C79196843F442954CC29F96DB50079F4ED27C76096A6FCB337A5F07037AD50630799CFFC634866CEDA6BF5C130ABE6CA2DCECAA7AB008F09F27228FC3897768EC74E1B479501D2D786DAA555FC9F37A5F07997DAAC358ECD8057E9AC9FEE8DC057E0AA75C0D2B50DF6389D7359A7D3560B47AEB629A45C753D4FF7FF0F57F7BAB22DFE5881B7FE78D36
	5B1C5354EF2EC19F9693EEC18E7A300AF03BF25007F5047BBC87FD98C8BEFCEC2D6237E12A3F4B56763F8F3F6BD799426622DC9CBF86119F228997C13856F5080BA65CC7049BC9B8FB9E624690AEFDBD62A6906EA5422536A06E6C8644E5916E1F04ABA0DCC0BE622A88973399F1F504AB5F02FD120BF0F60E8B0274A0428D5A0271B118F04FEF417808A25C45820C0FD1AD989F9B37AAB1D1287A7B45027E0F0F377AD5069C8BDB51DF63488F6FEFC5FFCDA05C1CED682FD4AB62FE3D8D7D15EFC5FF85EED7FCF4
	036A6FAF36773F3F56752B8CE1736CC20C398A724336C27461C942B594228F1B8997DC08BE6CA2DC658E74E1987970739D0A5F266CD47E9E38237FFDF8FF3F4A90B68F5809BE0CA4BFC46EC49F8EA75C1BBB5107A389B73988FDB806F0AF94218FD3DAE9AED12C786DAA555F530A7B5F074B7AD506305942574E19E0EFE9AB75E99CD78BE987611ABA99AEG52A104FB2593FD98C6380F4EB1DCCC2B589F91F6FCE3E46F930BA31F1E62E9763C0F9CABC5BD59494ADE19AFFF56238CDF3F412B87993C7CCBDF3089
	3B831610E6719494FBEED74EFB4F453ED96875BD488346D6659BDBEBDB264AB4E5AEFB89FBD737D8D387ED467D0D04D65CE73CBF176DB36659207FG283321AF5FDC2735538DF941GE1B6795E23CD5F1743E57C28EE729908F5EF7A426E57584B966A2F31D7ADE48B42B6FB3B33412CDDC767ADDCC0F94A4D8CF6190039C750EC6565DDC6E71359EB9DDE4C4B0B3C6E4AD7AE6CEE2DAEBFFB58B2CD657B75F9687BB7EA517763EC687BD6FE7FA95B8EF3C700DABB1D9995EA77CC9C109788B088A082A05A6E69DB
	791D3DD6F53DEF25FA3F1A77730C77C8BB4AEE90F7359CB07781A8F420EC1B176C9A48EB8668829888B05821A75B5D6AE177FAECAFC73C78C5C179D800F80071G49E7747865585436ECA0DBF277625ED47A9934A5135FE32A013495A0708C5A326218B606C3A1EFA8C00CACB36E0CFE0C0E157112337B9896FA0E7350F33304F65F67F5EE15863C32818AE51E55CEBD3B875BBD7C3881796D71BA7FB1C1F9B7C0B0C098C024AE3FDA07DA3FAD6447AF26E0BC8EF7229F9F674F99CAED108648EB23F5DD26D6F685
	641D84F0G84828CEA53131DF24643160DE8CB245759C084140F869889B086E002AE3FE8CFFE1B115FDDCB357CD2DD9057G85GD5GF5AEBDFE01CE8FFE85F4F66135076D0272C1G21GC381E2F479D534F9705BCA7324CAAD3FB8A8BF96C0EA4FB02481E4376B7A4F256637A18F4F1A7E312A1B77A171A3F7D13F430DD83F056D742EAB932BDF972403818637E37D2EB5DF6E1E6A30F66FB6A06E95CEEAE47CC634EB6FBB26F07C28F66DFD471B4DF85FF18C64D51D45E7EBDEF729E3F4BD4528094E11071E25
	98AD63F799A1ADF5G2E83EDF8205C6B4EA664158254F6487744BB74FCFCFB3BC71DED41BAFB420BDFG148F818817798C5365F7702CC71DD1BF38570B5F88A8BF9AA0C16613625663B729C36D9B764C0F60374A0BDFA6144F8728GB889605065770B8E8FFB73115FC7C7347C82217CA0008819CF34AE3F81EE8FFB290DAF2F57729B896547GCC107924F66A71AB6A74603789E3675373F4D7984A1D6C4467633F37F511FEDB60CF1B33C3D6793ED7971E3106F4E2ACDD5A44F846C2FAB44004CEFCCF20FB853E
	A7B0625E5B242A00CD09B3A46FE72B62EB5293534E899B8A64DF76ECD54D2F8312048F5E763AEF65823AA000D019FE68B9CF9F0876B3181FE9471C936DE78A6107F27CA895DE78E6B3473374F8482BBDCF6F6DB86761CB2A6BCB0DCC2FE16775473691BAF8A1BF0C1FC90FBE2FED6F77F37C38735A763E21896BEC826455F521DE311EFAED463A3F6D3C87FE8B621B3ABC70740D528D9754780DB4575ADB017DC6CC9756F5BB6FB7A6C0BA6DC206A16782769BB3AA34F5D48AF935GD600G00108BFAF17A0907EC
	76B01E7C1D330B6A761ACF6D75ED2FBBA46140F79840880051G893AF22E392466371178193C6683A997B38C19G7997851F0A0BFA7CAAA50F763F8979BD3CDC4B6FA414F7G84828C82085065B7E322079FE83E526A45AF9A4A0F849883B081A075129E3F5FF8705BC87D67B5F55A39DC9614AFG28127954DD520FE7170C8F6AA61F01185B57AFE2316A26F4A3CFF7CAB86E1AF7F1FEFCFDD0E6B349070D62DD2C8EFE0678EFA0ACCBC2DC861FE3DD85389FCEC45CAFB96EC9404DCBC45CBEFEC637E932A4D908
	AE16630E824D9062371F1F5135836ECF139077AB0EBB86387788F7101F518D19A8561382B702635ECE94BC052F8FCDE76BE797AC32C241B7125438C422FAE867ED2E5E5850526434375BCCB0FF8DE0FC4E73FD4AF8209FFB897B60C7BA58FC3C51E9EC052E7730435CA0C5F05A7869F85721B6C09E03655F60G6C8B16EE626F0835BB4D1EF24AB7B1B96361EF69AA247DBCB109535ADC02A6021FD367C0FE81003061FA76EAD61947557C5D204D10678290BA5DB493F58A3DCA6E03655F9170AB4A5DDB4C4A0D03
	748412DD496B2D55E6EE355BAC8D02AE6B8C43316FC3E5FF8F65C7ACD3643F42738BA02F74FBA8BFE5B64A37FECF488D10FF433E0772EF1A444AC5C3BA16780E4ED2784672BB5C891017F4B5723DF795724D39DA70AB16FF6B2EC63E2FAD61FBAF108EA6DC9BDFCFC5C1BA66EA141536C211F5116FF90D07BCA990E55566202C6CC0A123D07EBD9908FCFF4F4BF5C1BA88C04460913E4F7FCF006D94172FF35CB39013EB89974877799F86CDB6D16C9B78BE7F03007B974579359CF7B350B4915DC63E4FFF8F246F
	A2DCBC476D00323FA65C8DFC1FFFBE703A430038199B996EE920B9C338DB795E7F0750B67FCA6D758FFC9F658F00FBE6927A290E47474BG110198B3B7CD71ECDFDBD6AAFE1D4A5BF78C500EA03F46EDC13FA6487ECC39C67C66DC03FE7D9B3FA3DB85695AEB303EEE4BD6782E627B370110B7709A643BEA9B720D1279454A3FE3096FCBBCB6D22E057172DA445D4847517781CA2FC5D94F2CD2E465733B48B510D7F7AD4A3A071E57883AD6488815FF07915FCCFEE6B68A52E388F7FFB94ACA3B8EE5FD3CDA11F5
	E08F4B4F023C3C6BD0D6C69175AF57497D0B7C9BFC9D725D4CE5C5C0BA6ABA2C17GB75BC3B3199B0D6DD6D709555EE6BE68B0BB0D7237FAC4395B795EA56B7FC7D339B8B79B0BADB0B8D5369A1B58FEDBCE90239D67E6751C9904F497BBD93F68323878F3734216E5ECB8147B45511D2837834A84828C86088198CDBC8C7C4EBD4EE265779815323DA771565B92F63B10DDA731A94F5D5F60753CE15275D01FGB9574BFD4275FA734D32F5FC7F64FAB1FF1BCB78239C9F28420BF95D43FC4F1025A3A12F7A7B38
	E77B9F5EEE47C2DA9A0038925EC653A01DC53841F9380F1EC738E573F04F3C14F06FA562F9EBAD61E2AEB01C95520183F08E77D62D566E90488B8798B6C06E5B8768597D8E5F839BBDC058B715709FF13C942C60055DED9C4F5269109790CCF7B2787E5AE8C8A7906EB90ECB398152B7A0AEF3876A1ECD384E9D680B8242BD3884FDD1F5835A9864350F467BF648F75DA04FEFEE503371F52EF3688D429683617F4871C3D5F8E1230563D9BA8672327F87F53A06EC8CA65CCBE4E39861D6130D91043B1AEC9CC638
	BFD5200DA38857CAB2C6936E911211C038B9A4A369C64435100CB442FD1209B232EF2473533DEABF96823E9A4084603ED16EBBEE5473E3885FE38C3BD16B47BBB9BE72C62D9FD794219F07C3DE4EC05429020F5D83A1BD18F0419C97856961047B70981DB5912E72B85AB306F0779E47B6B201F019C5681F549FA06E5933A8B713F0099DA8B71FF08D9DA83718F0FF1E0BF2EB88771D9B79B5936EEEB7726BA25C24CE6497C438B766B0DCA824A3893701E4447CG6B6ACB2F77EE44C15ED8G69A6D1C7E9B76955
	55DA3ED710FD1328139904AF6478AA95DE545553FC5F13259D10B76AA63A471109B6D67C90F1FFDC0EB61EA4DC498A74F7B361862DC03B5D049BC367024104FB79BC4A88A35C2CAE1491C9B8C7974A08A65CCBDDA8E38461AE56220C51040BDA0AB29288B7E1A94AC889C15C374BD0468242BD318CE5E4936EE7F4C6DCC838200BA8231AF03F3908B2CC045BFC8979B988773759988F01042B1E03B2C288F79F1FEF04C3FA986146F15C88C847936E1B3DE847D842BD349765CEA05CB3EBD0C66A4D98F32F9855B1
	17857882002A1B6539744D5D6D0DBCA876D6EF56768FD19C9FF43336FFA8B2E17F908AF952AD28D329994FC2CD3720CED39B61C79E6367287E3E5D5C7FE747117F9BE4D83C76B287026D038122EE11672B37683573D5654C077137E87749B6F1FC42AD5AFD32274BF01FAC75D698B7EF25F3EABE770C0374D842CD6373CDE9904446A03A4B3387E3A81DF0CFD18C65916E9562D7CA3859442F16F0CE62E7A25CB16267A61C11788593CEBCEB46780591EEB2710BA0DC7D9E6497CD38E2DEF6A42447906EFA0E1BG
	69149F5199FCB97587045BC3B23289F78F49A8A41C61A843D5C35AC438CA0EF3C3BA08F08F9CC5FEA1041BCCFDD8B8618A6B115FB0428554A33F9104FB279E790DA25CFB67105F389FE11B123C1E7B70ADD6E776325C1EEE4BA55DA6E2FB41EDFAB1BEE9B30B591C5BC4AC4FA27CDC0E2FD161C50CDF437734D93A8B7292EE433B73135772336D5BE1DD88908E908D908FB061F63C737E4122BC2907779F54589DD56633E62B6A9A3BBC57F2E07DA877F10B4EE07DA877F1871F2179G61AC13A8C6696E6D83FCCF
	BBB5945A95C0CDA8621F60F8769DAC764DACAB618B0DFA7D94AB27CE2706FA775F5DC11E0A966F1F68491822C97BF37F43207ADB7862967EACD8B0489E9CCAB15D08E3C29459FBEF13C4FABD2C7A3B20297F7B6B2F7FB748B86835AFBF9AECCFGC839C3BEEB39C32F3D3C4F7B787CBB346370F80E2F38C3BB8E1FAF43F178A464C55F01BE4F626B132C9F43787FE3DA1FF0DC9524CF926E3795B477A35C1ACA1A7B916E3ACA1A7B916E93BE578E03F4A4610A789CFAB8246388B7580D7CC6932E0E66508904FB0A
	66CC49F7525A540DEB72F442BD6746B5F99E61B2E91EDEC8382ECE64D7CD381F73792F8952EE424D62F35DE0C807924EC5F362F0423DC2F362A8423DC9F362D84285F29971109E2F14653864B0980B42E84DC0B23288B700E464936E3D95A82302F07771670BCEC25AC138F19C9788699042FDE3C4FEE1041BC6735FC8423D12096B30610493F371D1109E9F06E70B3FBE21FEEFE36AED5393714C4CEBDC38CBBEA73DCBB9DB94FBC917D8B7AD6F534F5D445EEDC32F0CBC5C59465E30EF375A1D398DF61B4BE937
	8A39E19DEA39855316F47B3E48D2D9DE2D7C5B7549A279DCEDF28A6E098D043C61F771BD409475FBB3597E945BA04CE5B14C739BBD72194C7712C43E506B6565F856BA6165F0497A78B25D33D66E073B05BE7972EF6A0B0B240A455FCB92BE611F18406FFE67E67660139A5A13FD22C0A9770A059D9E584C9D19D416FD13A8FB2949E2345A1B1666E46665E42E5F308A7774D6EED34A369577D48F4207D1B35107563B5107FF1E297561DD1354BEDCB0CB6B43B7E49F8ABD443DE621C76AF2DFEC97345B4F305F15
	75ABE5BE7FA8D4781C1A641BBD9BE623BD03491E3A59DAFB1E7430672659DAFBB0E6049E7C1535985FC65F6B7239A2255CBDCBFC53BFF6956ABF0A747FF45565754FD92D557F6A09EA7D176FD274285E6E3B7E41A5CA3941BB7C0B453F17AAE56F5B691B6D97F3507634FB5076089C2D6DDB9255363F152335FD31C1EDFBED39224724A25FE331E38A0FC5B3466233FB94BE4F1AFD336773ADE8CF955913D3F0797E693B822DBD7F72302724D251E350B95F6B727E3DCA39DBFC0C45035BD07FC0527F42B62D7E73
	12547ABF3CCD2B7F72C9EA7D5DD50A9E65BE4522203D370257078B6B632D7D8A1F8FD77BE60F589FE6768CA3FB7ED928356752C435BD2BF6E86D5967D19F258795BDEA4BFC37A73A5C531E872AD5F13A46B7FBCA0A501E71E44F19A22DBD3BBC6C3927C8EB0FE5325A1E54C32A7872431EC75D6BE4DA794AD36ED10B4DE46FA8315B5ADCC61B2BAD377460298387EBAAAA8F6CB9F5704029122A03074BE4B9A6F625C19EFFF7185573023C739FCC1CA4690F3FB90365335E415AF94134F3B4268DC63F941CD35B4D
	F83E63510F1CBE2775DB27C73B8C7548E73A36F9447D2F4EE97D3ADDAEAF4F19B7633CF770E05AD35F0C735EA8429D22E731146714B3B651DEB4BD133CEF9DBB000F01F271GC9C30096209860A4008BA084A0E28852FF4B6959774759B72F078F915F70E65F679EB3C4FC07B879DE711DE1C17FF86EC00D3F99AF7C1B49E472E2E45F978F51DF4706F40377D8A749B4CC6F249E6805EE233CF41B2B717DD4C9D977CE6F06576C9E74E2B0CB7E1DA1F92F2B35A0E279E9DE271E77B232797962AFG326F45735424
	AD1EE771E78F3368176FE9F07A8220AD3D974F0D2B78F9249552EE422571FD0A41109ECAFC7FF00159EBECE8B0DB5991325DA91F897F63821397FFAF46544735F836D5F19F62F69E43332D13043B074EFD1467EF1573C065D95B77362742EFB314CBB6E4988261B77CBE4C9B4F7358B76D59B7A259375459F748C5DEE72F1EC9E3F234B8144964FB464BCD3D122137EFB2C50EAD0306EE2E14F24C3B5E877371065DA198F19F564FB747BC6B7DAB769999390EAAF770BA3A8F6B77EE3E2E2B884FB05404FB5E1969
	02F4D0B856471D74FC1D720C22C4670F4AB30AE1D4EF4A7308EF72EF64C6822FD161089B43F1A39702AF97A24EFEC54F8EB25EB3E5B95D376B20AB1201F5525DFE17705FC35CD78F82A45D0F77BAF69DD16A60B02F03347B515F915C0F6C3ED19761E6723D93F617E8B061EE61FCFF406A1A781A0EAAFC370B3AA57A90D1378FC05DBE00384522EEA19D74G56595E3D380ED71E719E3A834F36A209EE7C8EBC5B9ACE38EF6879C065396FC7F6E28CA84FF85F466791A3215C788790B7046358775FD396A1AE4763
	D9B3C60FFF0FFF4077FD4C4B776DE59EB254F5E938C27ECADB3D7322F7DBBD435B6F55G29BF4136BA6622E7DB3D0929A647519747B1AEB2FF027567BC0EF1D1C138B57C4C428569GG391D97B13EC30966BDFE967EA64002880C1D470FA93202793EDCB66465C7E019D9E9FC3F88524D043B0E6F237D8EA00478449DD77864707D334110B7B482EDCC1E23BFDEA44E654FB6926F937CBC3C78C1082587E93F0B5FB173FC3E7C7C946C0B14674B6327E09CAA4F178FB7E39C2A1EA5676FDCF3837FD000E1G71G
	89GE98FA14DEECED3178EF9G23E07E128FF991EBE0DC49C11A674F55295B7DC302D7CF31915F8DCD02860F7A49F3918FD93E4C9398CC6D862F1D2BCC1263DE5441AE3E2777867379C3D83F1F31CF2E49F5E6DD416A4C7490565973FC1FF198240793EE3638CB8A6989049B4F4F7C2FE7B176B0727D6ADB056F0F3A985F0207117E6F7C1CBF8C5291043B0F4FA7C6C2BA1EF06F70B37DFFG248C253E76BB056F354B985F7421C87FB6BF5B8F0474C042FDA07AB5C847906E895EFE5FE1F690DF53A9A54ECF71B3
	FD6991683B9E41B617B907494AFF84F9F40A7DEAC807BF02ED6159CA6FFE61D50E7B91330D68266C756C972E2BD3EC3A4873C6131C57F97FDD75A84C359FC55C1F7919EAG24039F4576B01A6F6BC6C1FA8461B67333435110CEA05C638D8C177E98447DE3081BE24273180A47683D9D9CE705F4A0611E65384245B04F4FC2DC19C9BB6632B26AF460E33D6F5B6577F8F849500E51D752378FFD0C77CF664C4B50044AB64867346558EFA94F574714E33F25BCDF3F1C4E1615676BE750F93872AC7DBAF13E4F6AA3
	9256E011745EA80E4F4B0436869038047A2732DA193E37B6B3B96AF4F124F64E7BFBAE63CA1E7D5656093ADF136F1F16FB37173FF25CF5G26C8ECAFCB76F83617870C2C3D887AC15C47AE208D08447671C76E63F0C8C7926E934EF7GC0FCA43679D76A95BE37F3BE63A0EF8215799E671375F806A16FF144FD1F73799060646348E7DC0342E75D9E9C572DD4E6409E9C57C39EC71B0E6A58EE63B8F67FBE1A68424ABDED1F4B2E474AFD45FC6E1791A447C1328B1EC0DCAC492EFB82F94EB863ADBB175FDB9882
	90CCF4C74EF84A5E5C2858793B23A8AB14E42D2BC5D9B104B357633CE85C93682FE3CD8A1FE93CBF121E047670A416C96563C9BE240B1F247BF9F4B74CCA3869342F9170A45DAD5A4C763EC2A0BDAE894697780D7CA85D90833FD9302E9BC5F48BB9DD1C7CED3771CF0A6F5432F7713377A033EF25893A811B9CBE46BB134564E0FA142CC3F774CC2F19322EEABC53D33EB36033ECC62F9E7BE76934BFB5607CC15B3E597CC35C1D1835367BF799FC31D67FDD8669EB755F9738F4EDF76F9890792B392CB60FCF1C
	212C75EB199D768E7EFAC3395FD8ECE7FBE5FBF53EA1AC72D738672B73BD3FF7AC73D8320979EB6FE1B348E453568D1278EEDEFB2DFA9FEEC114088B254FFA30115958E8359BDD6C63309447AFB6621EE1B11491E3CAC2E366E56A20A6CABD26B4D66018223C7766018A9CD314775ECC2733F8653DB77F24F717A96F3879EB999F2BA1161A2368BBB49C473E5B9D140D382B4A54E3EBF314EFE3CA2141B7BAF9FD68A14357315A5731C7BE7B2B706E538EF15C3BGA1D1582785D6F876E9DD4D10147B534FF85E60
	A86C07EAAA310FCBF98AF1C3E96D1E7F947527EEEF59D99C77D700BA22BB627614FD27C52278F90553BB00366BA91CF3DE4F671C03C9769F791CB36527990685BFC51C38CB71B79693BF457E75E727953E151DE8C38D15995209B6F47D94F5FBF339378D6317633AB21C68D62C7034E1DC0B623FBBF8DE944979042F9FB21F86FB1EC65CE43EFE2801F45D534873F69D590FF25CABG41C4B7FA39E6ACECD5E41F6032C3C9CE648A34BBE9980DABAB516E1CE1B4E72F53193357613EDBAD51CDB1EA46C21B62630B
	FC6E5D8C34AE1235112FBBC2A19DCE3809FCFFE6A42463096F4D357A6B6787EB193C54E7703E5BC7DC1E68931FDF4B3ED7F044F8DA3E51D6E632C0C13C523657F856A8BE18DBE3E4832DA89FE7E732CD40AF6099FAA7EC5E1A6E78G325AE9F7181D2ECE01BD423F08A95A737494FF6F8C0A33196727605D08481F5EADC5DD7B6EC4E63B5E59CC58B372B37A72EF788B0BB8DE58327B8CDB430F033C14E7E1EE86D081F0924075AC5AB61DFF0B0DFD6188545F65F2999B1ADB599006DEE2DF77D6506CA1CFE15BB3
	B3FBF79FF24B6C5E157B4D2A5E153BBA27F765A695742EDC4D365E157B156EF3BEBD172BA872371C088D3313351BA00A034307583D036EE21CFD776AB0CCCBECCDA8F71657FB65FB825136FF4B4FEDEEG88FD967B61175994C79E1F6E584666B8B4A791DF751673DEDD7405BACFFC771B3DA38BE6CAF4C6BD55F33E12AF63C7ED1F27AEDB8D36382A1D6633905EA4FBD352CCAF9A4CB3DE70643BC9467FBBE5363A8CFE6B1AF8A62C76AC379E5FE130E20627AD96767EEAB137AABA2B1DDB317977CC4A6F6838FC
	7E04EE7365F7ED6471B37186BC939F562E78FF613C1E4E44C519259DCA19D10919BD14D16EC7DCFDC1A997F541573BAE02FE6A459312675977BEF3A704253D451C5BE4F6D1A2BBD373A9E548616FD34BBD326FD4C90E12AB10A5B5072ACE6D5FF5F85FAAB9C61771BD6ABB81A2A8C66F3D28681D3F4C236E184E147754BC5667FAFD556CF0333D234A5EA4E86EEACD6F0EC67EC404FC46D908F797643BD9BB7A407E1A4B59AFEC65CFC17AFC1FCA1439B0D1A913DB68CBBC885A5FF7FC29670B0ABDCE0B294AE2B3
	3749BC13966A529D51523DA8FCF684FDE6698B1F9D391C4F643EE9C7CFFE92F4DBAAFD774D0342168A34656B3E302522677AFFB7D1294B31193E55FFE512D2E61ECF75AF5A5A9BBB70BCEF9835B52BC161755C1A396A3626FCBFDD4E7F47E90FBEFBD73D3D1D7A6C20E35CFF3B50FFC547FD2B235B0F7B5E1F8D1F22681AD664DFFFF62A288F6AF3D74F317979D96E879B7677DB272A7A6E393E556F1D5314B29FF5789E43BFF6F3599614EDC8D1787CFA0EEF7531E0EE9F7829326738EF192168F65C8F9BBF1D66
	B137E0D327D2CB1B43EA6CD45E8F39BC55E31E305F58E2AE31BBBAA9FF64C61D3D9862A2E82EDA62B917580C6F76BA62B1E7BAECB6BA9B1A653CA193BD6416DBEC2683203804F305BD4B35F385364F274C95EAB3F97DD9318DFDEFC5651557C5E9D9D5D9CD99B487F6A7D3781EEF4549B25EAA72ADA69BD315B22B7D387FBC3D0B5BD40AB6BD5C558771D55AF3FC454ED674F5557AE6630639CA190A253E5B1830145B5809B606AE6B839B0F75EC6320B4C55F289E47C4D16673F9CA1970453E5B784AC5EE23837B
	967BFC054FFB33FD6BDBB64F69833FD4776C17D797A83AADBD6B3B0DE3AB390DD5D80FA1FB7BC05F2A1E755D3EC85177319E6F490AB2F7FD29149961471D564F0CECF0B1DAADCDB63649D00BEB45397CCCA4966685714F62BE507EFA769D9F689479B8AF3EA1E72A47772A24BE17E148862886B08130F7C40BF2318D9EB20E210C517CDC258BE88782C4BF47F0DF19545F6A52023C6EBB4E86D17577AE857E7EDDEC00D3FF6ECD605DE626F78908E69B74E21F27430C7BBC69BF39DDCADF7CB66F2B7576F9122365
	E7E02215E70294DDDEDA63B1F608B5F30D3995468EDABB676E779A23088279ECBE2DB386E978CC5E436331845B5C518C254E9F76B1CE1E79CAA973ED396FF1B2C348AEC3597CF19F194FC19FE507285EF576B7BFE274AEF10F7CAC36435ABD3E75AF11FBFC17F10D10D10FB2566E6903363E3B6779610FFC3465E3BFECA9BB4AEDB90436CCB86A1B0C5ADA5FE5F455637CDE546D27CB153AFD731CEF71D0172514397908AF32C53FF1C3976E1BB7C7633E7536CD089B98CD6FF3BAB7955BD439452A34B5E3D68A62
	65D91E40C71B89FF50415ECC55A62FAFCB098FDB83C978DD41A3AB753EAB988572E32371BDFDBFF0B1DF683CAB15E61272DA201151B79A2DED0A1E731A986E005D69EAA65C632BB3A42FFDB8516B18E4E30856A21EA7B903F1B03B328FE27A6B1EE37AC225EF7176069F6B716DE2AD53017176728AA5F60E7838163933AF669BC7FB9E3F57E7AB3A3D6D47DA66E9E1239B6B4B59691BDD4FCF76DD467495389E156797AB7A40A707FB76492D2B941F7C4E47B5463FD7ABE5527CD8E33C341C5B5808B61E698B9B4B
	FB0E7BB7D778D6DFDBD77AEE4B58BAEE8BBDC355102378C472F10EFFEB2ED266E7193E481657862AE71E1D357E6D2F3CD0DB5687BEDFAF2FD345B9491BF6564F529D8DF15CA7670D6677ADACB60B8B74311C63AF969427A6624C65D76BD97EE868172567B38CD9G85GD54F6399CC330359372BDE6F9C2E4C54E446E73CD05E3EB35D5FBDB9F4061D1548F7CFD829865055E9E1F7B15A307C897EDE7DFA3B4BE5EFE57C91EFBC435F29E3F7F0247CCE5CB3EC7CF1DA1A1ADD02D446DB799DA92B391123854E62E4
	F35DB6793CC854457B1BF98C15E3FB58A676668EE2FA5C8E1E2ED8A9AD96F1A5717BCE72BFF90DDF3FC49DB7A576D6075D462A292A2C3C66542E2A4ABD8776179D2811709D8F4175789E6AE670F15873B89E7EB977CB75F84818ED5B365B6EB4191DD5E2E1A11EEF9BD90FF7D5621E4777C9A7B67077137C8C5699G45G5E7A4697496B1A06E57A7A160016E507D49A0B7540699675FA004D713BFBA63E76E77233F8BF73DC8F08B833EDE46DEB98644581CCGC8FB817485A8862881303E00B1B76ECBB6D0F4F7
	6EA71C42634E89CB9D39BF1858633B8784DDC30FF4424F7F436F9BB0DB03DEC0BFFFE0E1FE9E8B6924188CC3FA8C6A7B5A7A6EE37E4D75BD477C1F79BBE14D902BB51D8EB9DEBFD84F3F975B5858E62636B5EEFDAA61AA5B2A6DED96561A65671C3C5EF16F5BD9EDD8AF4B3D61F7B9716E1333BCD673E3346FED3F447145B15A7736A7B7E0FB29023C21B1F8276235FCB647CCFBB1431087D083E08588G9874A2564BB56C93G5D4651A13B1D3F32143D3D9462C89DD2323FBBB7F6DF2F819BFB2E576BB76A77E5
	B7EE746E4BC2B66A77E537ED746E4B6E1479BA69E3CB82BFE4A30BD7318F315FE8B3B671FBF7223EFE38D2FB8FBB827C948DB072C551DE47406F04973D6F06257E5C0767B8F99D1EBA4D6AAA6B675A775D1C6678021FEB5FF773B78B3E6F268A72067E9C6F35F5B5C82477B52A3FE7B6747F3BFED6762B8C799B4B795D47551F73FB0E2B3F666B475547795EF175E93EFEDCC91B54F1A5BFDF407BBC761CF25AAF70FE623FB80EBD271CCD386359F80FD179967005E23C47D8CC38AF79BD30FCA8D7CDE52F66B87C
	5E30703979B4FF1E89E802FE01B887478D057488427D53F225F54378B3B95D57FF6A69AB15A146F37DBC31A6FFEBA5BEF3B36E97F83EE8E71FEB42FB984BD9BBB672AFFE31571F99592B6865F96ECDDB943A7619EC1C5FFB48E83258F737C3B4586848116D757169A21E53942CDD2421ADE9EF0378D18E0544594B5D2755E7AF52F079FEB57C0A7C57ECFA799543F1EF616315F80EA17C7056DA36A055D9676BE8CD5FA763F54F4E15CE82DF011B416F4031D88E99CE6FDABE4D462058612AE75319BEG49AF4977
	F3DF722CEB793D15F62F6F73GDDA9C02DCCEF5514131F1B66FA85C0DE48CB745CF48BFFDE81524388F79D470D00F4BC614636E01B99CB38230958E624DFA2EE9537B38D525904F3F2DCA1242B88773A9D795592EE238379D9FF097511426536192D66861759A45FA561B8169FC0F45F34F85F25343660739283096E47AD1EF7A99F676FAC9274732C7C59B0200D78A56EBF85F09BC7C2BA1EF8945B3DE59D65388FGD2DEC63A6B6D1E325E5D2A48129C382F157EB24ABA6940BB222544E31B505759EE96692BDB
	F04FD43EA320FADF517EC45F560D8F2B5ECD7414AD3307B24A1A55E667325BF1FD314A615B1C759A07AFB266F35A7FAC4B00C1B562C349601212586E1ECB1AA237F117140CF76D81A96F6481955B3B039FB6BBF2C953596595F3A63F3E62128C6C3CGFE27620EBB7C1DD6E32CEF077EA5135DF24BE413A6BB0CCDCEA9CDCF411D976BDC394F7EB0A8D1125CCEE926EA5D811993074C3FBA75460D6D09D2F239B43951E8F5B49BABA543D936FE2D12265773D10B4D4B2CAEA9350A15DD87E55771325B2BE059835A
	FC29609B98DE649EF6989B4CC0B2558446F28EE7240BD24A89E9EA2333291E8BF0C909A74ACF00D81EE259B32B24E92EE671B6FBB738E23E423A1A4D8A650C6DEE3653497E4DF670FFED2C70B499B7C746B925999E9B82AF5E35694F31EF9DE91BA4CD2E1496326F069CB6BB79125CECE20EBCE2F636C1FD14CBD39D6CD3AE0781DFA9A535719FC31B2B538A9EC9EBBCE4BE538EF5E0B66D4245BE38E01E50335DE5312E2BB036B573079B4078A49B4C8DDD529C11EDB55A1A5609153F30E0622EE22E31784E1ED3
	1A4B75E5DCF2094B5C27E7ADDF32F7F454A4A931DC1AE6359BCD65B0AC591D1552D4D733535C56EC371A5C0EA241A839833B6DC9406ECBB21F991B4BA21414EEF15F91BCE34C0CF291956BB8A5F3EDAED118CA1A0DCE90E5F6424040370333A86F2A2FDCABDE59B37FE422B4D1B612D5C01A9B02F8AACC1BECED0DF6E7ABE8102951G3308D16E8FDF4A3B797ECF0FF3C6CAD11734D82979A6333DF55D2E46C6E88F44G7C876CB308BD234955504CF17FE05E5ED77FD38F75DDAACDEA4D4B637F2F677FEFE07F2F
	6718759C33FE83B8F11CF459FF5AFCB78F3B85AE91A4204FAC2E8F2B665CF276084B6FBFFC696EBF940E9B8B56154B0D8A1C1D2AD09DE8EF2DB7BB2F0D38B9E3FAC87CGD6978E969248979AE42B458A3D4664D23E1565E678EC37C734DF2E8574875D9C2F38E558AF3C83102197714CF47E7FE23F5D4BCAAD61E517B1016C0F951E3E487FBF6B0BBE240306A30ECAF2F3F3E1E8E07D957FEBAE74CAAECB03511A49C6859C9466D5581D16F3EC556C09B7700BEE50D573CB4A1972CDE64EA81923781FD3CBE1AE02
	125818A37EE2970FC43EB83A91455875534CAAFB13C6C1AAB6D51C87733FD3B831FC408D5D1E484A44A1CF506CB219B215C1F01A1015F9786BAA60FA408C45CBCB58D068E6C36194F6B1C908E1FFE59E602342F4B6DCE60A7BFE72907BFFEF881FF2ABB3013E8FAABE33786FB33A657F19F67B7F697EDF9BD386385573F5652F59FCC55F16E904FEC82C6BDD52AC31024F44A53C2FD6CBF857E755A5757B76922EC96C76BD3C95F172FB5A62143DFD318FDB445FD9BA887011F1386FFAE3B67BFEDAF74F389DEEB5
	DA2DB0F6346C32195873EE3BBBDDE665F96252D50CFF9B524819725952AA2677E9D5197F85D0CB8788AE40167693C7GGE48881GD0CB818294G94G88G88G53D44DACAE40167693C7GGE48881G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCDC7GGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (4/1/00 5:37:53 PM)
 * Version: <version>
 * @return int
 */
public long getCurrentDisplayNumber() 
{
	return getMainPanel().getCurrentDisplayNumber();
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/00 2:26:52 PM)
 */
private void getExternalResources() 
{
	try
	{
		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
		noCreationAllowed = bundle.getString("tdc_express");
		TDCDefines.MAX_ROWS = Integer.parseInt( bundle.getString("tdc_max_rows") );
	}
	catch( java.util.MissingResourceException mre)
	{
		//no resource found, let program execution continue
		/*handleException( mre );*/
		
	}
}
/**
 * Return the JCheckBoxMenuItemHGridLines property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getJCheckBoxMenuItemHGridLines() {
	if (ivjJCheckBoxMenuItemHGridLines == null) {
		try {
			ivjJCheckBoxMenuItemHGridLines = new javax.swing.JCheckBoxMenuItem();
			ivjJCheckBoxMenuItemHGridLines.setName("JCheckBoxMenuItemHGridLines");
			ivjJCheckBoxMenuItemHGridLines.setMnemonic('H');
			ivjJCheckBoxMenuItemHGridLines.setText("Horizontal Grid Lines");
			ivjJCheckBoxMenuItemHGridLines.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_H, java.awt.Event.CTRL_MASK));
			ivjJCheckBoxMenuItemHGridLines.setBackground(java.awt.SystemColor.control);
			ivjJCheckBoxMenuItemHGridLines.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMenuItemHGridLines;
}
/**
 * Return the JCheckBoxMenuItemShowLog property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getJCheckBoxMenuItemShowLog() {
	if (ivjJCheckBoxMenuItemShowLog == null) {
		try {
			ivjJCheckBoxMenuItemShowLog = new javax.swing.JCheckBoxMenuItem();
			ivjJCheckBoxMenuItemShowLog.setName("JCheckBoxMenuItemShowLog");
			ivjJCheckBoxMenuItemShowLog.setMnemonic('L');
			ivjJCheckBoxMenuItemShowLog.setText("Show Log");
			ivjJCheckBoxMenuItemShowLog.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_L, java.awt.Event.CTRL_MASK));
			ivjJCheckBoxMenuItemShowLog.setBackground(java.awt.SystemColor.control);
			ivjJCheckBoxMenuItemShowLog.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMenuItemShowLog;
}
/**
 * Return the JCheckBoxMenuItemShowToolBar property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getJCheckBoxMenuItemShowToolBar() {
	if (ivjJCheckBoxMenuItemShowToolBar == null) {
		try {
			ivjJCheckBoxMenuItemShowToolBar = new javax.swing.JCheckBoxMenuItem();
			ivjJCheckBoxMenuItemShowToolBar.setName("JCheckBoxMenuItemShowToolBar");
			ivjJCheckBoxMenuItemShowToolBar.setMnemonic('b');
			ivjJCheckBoxMenuItemShowToolBar.setText("Show ToolBar");
			ivjJCheckBoxMenuItemShowToolBar.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_B, java.awt.Event.CTRL_MASK));
			ivjJCheckBoxMenuItemShowToolBar.setBackground(java.awt.SystemColor.control);
			ivjJCheckBoxMenuItemShowToolBar.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}

			//always have the tool bar start out showing
			ivjJCheckBoxMenuItemShowToolBar.setSelected( true );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMenuItemShowToolBar;
}
/**
 * Return the JCheckBoxMenuItemVGridLines property value.
 * @return javax.swing.JCheckBoxMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JCheckBoxMenuItem getJCheckBoxMenuItemVGridLines() {
	if (ivjJCheckBoxMenuItemVGridLines == null) {
		try {
			ivjJCheckBoxMenuItemVGridLines = new javax.swing.JCheckBoxMenuItem();
			ivjJCheckBoxMenuItemVGridLines.setName("JCheckBoxMenuItemVGridLines");
			ivjJCheckBoxMenuItemVGridLines.setMnemonic('V');
			ivjJCheckBoxMenuItemVGridLines.setText("Vertical Grid Lines");
			ivjJCheckBoxMenuItemVGridLines.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK));
			ivjJCheckBoxMenuItemVGridLines.setBackground(java.awt.SystemColor.control);
			ivjJCheckBoxMenuItemVGridLines.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMenuItemVGridLines;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
			ivjJFrameContentPane.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			getJFrameContentPane().add(getMainPanel(), "Center");
			getJFrameContentPane().add(getAlarmToolBar(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the JMenuViews property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenu getJMenuBookmarks() {
	if (ivjJMenuBookmarks == null) {
		try {
			ivjJMenuBookmarks = new javax.swing.JMenu();
			ivjJMenuBookmarks.setName("JMenuBookmarks");
			ivjJMenuBookmarks.setMnemonic('b');
			ivjJMenuBookmarks.setText("Bookmark");
			ivjJMenuBookmarks.setBackground(java.awt.SystemColor.control);
			ivjJMenuBookmarks.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuBookmarks.add(getJMenuItemAddBookmark());
			ivjJMenuBookmarks.add(getJMenuItemRemoveBookMark());
			ivjJMenuBookmarks.add(getJSeparator2());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuBookmarks;
}
/**
 * Return the Display property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuDisplay() {
	if (ivjJMenuDisplay == null) {
		try {
			ivjJMenuDisplay = new javax.swing.JMenu();
			ivjJMenuDisplay.setName("JMenuDisplay");
			ivjJMenuDisplay.setMnemonic('D');
			ivjJMenuDisplay.setText("Display");
			ivjJMenuDisplay.setBackground(java.awt.SystemColor.control);
			ivjJMenuDisplay.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuDisplay.setActionCommand("Display");
			ivjJMenuDisplay.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuDisplay.add(getJRadioButtonMenuItemAlarmEvents());
			ivjJMenuDisplay.add(getJRadioButtonCustomDisplays());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuDisplay;
}
/**
 * Return the JMenuEdit property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuEdit() {
	if (ivjJMenuEdit == null) {
		try {
			ivjJMenuEdit = new javax.swing.JMenu();
			ivjJMenuEdit.setName("JMenuEdit");
			ivjJMenuEdit.setMnemonic('e');
			ivjJMenuEdit.setText("Edit");
			ivjJMenuEdit.setBackground(java.awt.SystemColor.control);
			ivjJMenuEdit.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuEdit.add(getJMenuItemEditDisplays());
			ivjJMenuEdit.add(getJMenuItemMakeCopy());
			ivjJMenuEdit.add(getJMenuItemRemoveDisplays());
			ivjJMenuEdit.add(getJSeparator6());
			ivjJMenuEdit.add(getJMenuItemSearch());
			ivjJMenuEdit.add(getJMenuItemFindNext());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuEdit;
}
/**
 * Return the JMenuFile property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuFile() {
	if (ivjJMenuFile == null) {
		try {
			ivjJMenuFile = new javax.swing.JMenu();
			ivjJMenuFile.setName("JMenuFile");
			ivjJMenuFile.setMnemonic('F');
			ivjJMenuFile.setText("File");
			ivjJMenuFile.setBackground(java.awt.SystemColor.control);
			ivjJMenuFile.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuFile.add(getJMenuItemCreate());
			ivjJMenuFile.add(getJMenuItemExportDataSet());
			ivjJMenuFile.add(getJSeparator4());
			ivjJMenuFile.add(getJMenuItemPrintPreview());
			ivjJMenuFile.add(getJMenuItemPrint());
			ivjJMenuFile.add(getJSeparator5());
			ivjJMenuFile.add(getJMenuItemSpawnTDC());
			ivjJMenuFile.add(getJSeparator1());
			ivjJMenuFile.add(getJMenuItemExit());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuFile;
}
/**
 * Return the JMenuGridLines property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuGridLines() {
	if (ivjJMenuGridLines == null) {
		try {
			ivjJMenuGridLines = new javax.swing.JMenu();
			ivjJMenuGridLines.setName("JMenuGridLines");
			ivjJMenuGridLines.setText("Gride Lines");
			ivjJMenuGridLines.setBackground(java.awt.SystemColor.control);
			ivjJMenuGridLines.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuGridLines.add(getJCheckBoxMenuItemHGridLines());
			ivjJMenuGridLines.add(getJCheckBoxMenuItemVGridLines());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuGridLines;
}
/**
 * Return the JMenuHelp property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuHelp() {
	if (ivjJMenuHelp == null) {
		try {
			ivjJMenuHelp = new javax.swing.JMenu();
			ivjJMenuHelp.setName("JMenuHelp");
			ivjJMenuHelp.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuHelp.setMnemonic('H');
			ivjJMenuHelp.setText("Help");
			ivjJMenuHelp.setBackground(java.awt.SystemColor.control);
			ivjJMenuHelp.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuHelp.add(getJMenuItemHelpTopics());
			ivjJMenuHelp.add(getJMenuItemAbout());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuHelp;
}
/**
 * Return the JMenuItemAbout property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemAbout() {
	if (ivjJMenuItemAbout == null) {
		try {
			ivjJMenuItemAbout = new javax.swing.JMenuItem();
			ivjJMenuItemAbout.setName("JMenuItemAbout");
			ivjJMenuItemAbout.setMnemonic('A');
			ivjJMenuItemAbout.setText("About");
			ivjJMenuItemAbout.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemAbout.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuItemAbout.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuItemAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_A, java.awt.Event.CTRL_MASK));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAbout;
}
/**
 * Return the JMenuItemAddBookmark property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemAddBookmark() {
	if (ivjJMenuItemAddBookmark == null) {
		try {
			ivjJMenuItemAddBookmark = new javax.swing.JMenuItem();
			ivjJMenuItemAddBookmark.setName("JMenuItemAddBookmark");
			ivjJMenuItemAddBookmark.setMnemonic('A');
			ivjJMenuItemAddBookmark.setText("Add Bookmark");
			ivjJMenuItemAddBookmark.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemAddBookmark.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemAddBookmark;
}
/**
 * Return the JMenuItemCreate property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCreate() {
	if (ivjJMenuItemCreate == null) {
		try {
			ivjJMenuItemCreate = new javax.swing.JMenuItem();
			ivjJMenuItemCreate.setName("JMenuItemCreate");
			ivjJMenuItemCreate.setMnemonic('c');
			ivjJMenuItemCreate.setText("Create...");
			ivjJMenuItemCreate.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemCreate.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuItemCreate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuItemCreate.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK));
			// user code begin {1}

			// if the config file is not set for creation of displays,
			// then disable the create menu item
			if( !noCreationAllowed.equalsIgnoreCase( LUDICROUS_SPEED ) )
				ivjJMenuItemCreate.setEnabled( false );
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCreate;
}
/**
 * Return the JMenuItemCreateTemplate property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemCreateTemplate() {
	if (ivjJMenuItemCreateTemplate == null) {
		try {
			ivjJMenuItemCreateTemplate = new javax.swing.JMenuItem();
			ivjJMenuItemCreateTemplate.setName("JMenuItemCreateTemplate");
			ivjJMenuItemCreateTemplate.setMnemonic('C');
			ivjJMenuItemCreateTemplate.setText("Create...");
			ivjJMenuItemCreateTemplate.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemCreateTemplate.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemCreateTemplate;
}
/**
 * Return the JMenuItemEditDisplays property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemEditDisplays() {
	if (ivjJMenuItemEditDisplays == null) {
		try {
			ivjJMenuItemEditDisplays = new javax.swing.JMenuItem();
			ivjJMenuItemEditDisplays.setName("JMenuItemEditDisplays");
			ivjJMenuItemEditDisplays.setMnemonic('D');
			ivjJMenuItemEditDisplays.setText("Edit...");
			ivjJMenuItemEditDisplays.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_D, java.awt.Event.CTRL_MASK));
			ivjJMenuItemEditDisplays.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemEditDisplays.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemEditDisplays;
}
/**
 * Return the JMenuItemEditTemplate property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemEditTemplate() {
	if (ivjJMenuItemEditTemplate == null) {
		try {
			ivjJMenuItemEditTemplate = new javax.swing.JMenuItem();
			ivjJMenuItemEditTemplate.setName("JMenuItemEditTemplate");
			ivjJMenuItemEditTemplate.setMnemonic('E');
			ivjJMenuItemEditTemplate.setText("Edit...");
			ivjJMenuItemEditTemplate.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemEditTemplate.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemEditTemplate;
}
/**
 * Return the JMenuItemExit property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemExit() {
	if (ivjJMenuItemExit == null) {
		try {
			ivjJMenuItemExit = new javax.swing.JMenuItem();
			ivjJMenuItemExit.setName("JMenuItemExit");
			ivjJMenuItemExit.setMnemonic('x');
			ivjJMenuItemExit.setText("Exit");
			ivjJMenuItemExit.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemExit.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemExit;
}
/**
 * Return the JMenuItemExportCreatedDisplay property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemExportCreatedDisplay() {
	if (ivjJMenuItemExportCreatedDisplay == null) {
		try {
			ivjJMenuItemExportCreatedDisplay = new javax.swing.JMenuItem();
			ivjJMenuItemExportCreatedDisplay.setName("JMenuItemExportCreatedDisplay");
			ivjJMenuItemExportCreatedDisplay.setMnemonic('q');
			ivjJMenuItemExportCreatedDisplay.setText("Export SQL");
			ivjJMenuItemExportCreatedDisplay.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));
			ivjJMenuItemExportCreatedDisplay.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemExportCreatedDisplay.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemExportCreatedDisplay;
}
/**
 * Return the JMenuItemExportDataSet property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemExportDataSet() {
	if (ivjJMenuItemExportDataSet == null) {
		try {
			ivjJMenuItemExportDataSet = new javax.swing.JMenuItem();
			ivjJMenuItemExportDataSet.setName("JMenuItemExportDataSet");
			ivjJMenuItemExportDataSet.setMnemonic('p');
			ivjJMenuItemExportDataSet.setText("Export...");
			ivjJMenuItemExportDataSet.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemExportDataSet.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_P, java.awt.Event.CTRL_MASK));
			ivjJMenuItemExportDataSet.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemExportDataSet;
}
/**
 * Return the JMenuItemFindNext property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemFindNext() {
	if (ivjJMenuItemFindNext == null) {
		try {
			ivjJMenuItemFindNext = new javax.swing.JMenuItem();
			ivjJMenuItemFindNext.setName("JMenuItemFindNext");
			ivjJMenuItemFindNext.setMnemonic('f');
			ivjJMenuItemFindNext.setText("Find Next");
			ivjJMenuItemFindNext.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F, java.awt.Event.CTRL_MASK));
			ivjJMenuItemFindNext.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemFindNext.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemFindNext;
}
/**
 * Return the JMenuItemFont property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemFont() {
	if (ivjJMenuItemFont == null) {
		try {
			ivjJMenuItemFont = new javax.swing.JMenuItem();
			ivjJMenuItemFont.setName("JMenuItemFont");
			ivjJMenuItemFont.setMnemonic('t');
			ivjJMenuItemFont.setText("Font...");
			ivjJMenuItemFont.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_T, java.awt.Event.CTRL_MASK));
			ivjJMenuItemFont.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemFont.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemFont;
}
/**
 * Return the JMenuItemHelpTopics property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemHelpTopics() {
	if (ivjJMenuItemHelpTopics == null) {
		try {
			ivjJMenuItemHelpTopics = new javax.swing.JMenuItem();
			ivjJMenuItemHelpTopics.setName("JMenuItemHelpTopics");
			ivjJMenuItemHelpTopics.setMnemonic('t');
			ivjJMenuItemHelpTopics.setText("Help Topics");
			ivjJMenuItemHelpTopics.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemHelpTopics.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuItemHelpTopics.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuItemHelpTopics.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_F1, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemHelpTopics;
}
/**
 * Return the JMenuItemMakeCopy property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemMakeCopy() {
	if (ivjJMenuItemMakeCopy == null) {
		try {
			ivjJMenuItemMakeCopy = new javax.swing.JMenuItem();
			ivjJMenuItemMakeCopy.setName("JMenuItemMakeCopy");
			ivjJMenuItemMakeCopy.setMnemonic('y');
			ivjJMenuItemMakeCopy.setText("Copy...");
			ivjJMenuItemMakeCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_Y, java.awt.Event.CTRL_MASK));
			ivjJMenuItemMakeCopy.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemMakeCopy.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemMakeCopy;
}
/**
 * Return the JMenuItemPrint property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPrint() {
	if (ivjJMenuItemPrint == null) {
		try {
			ivjJMenuItemPrint = new javax.swing.JMenuItem();
			ivjJMenuItemPrint.setName("JMenuItemPrint");
			ivjJMenuItemPrint.setMnemonic('i');
			ivjJMenuItemPrint.setText("Print...");
			ivjJMenuItemPrint.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemPrint.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_I, java.awt.Event.CTRL_MASK));
			ivjJMenuItemPrint.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPrint;
}
/**
 * Return the JMenuItemPrintPreview property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPrintPreview() {
	if (ivjJMenuItemPrintPreview == null) {
		try {
			ivjJMenuItemPrintPreview = new javax.swing.JMenuItem();
			ivjJMenuItemPrintPreview.setName("JMenuItemPrintPreview");
			ivjJMenuItemPrintPreview.setMnemonic('n');
			ivjJMenuItemPrintPreview.setText("Print Preview...");
			ivjJMenuItemPrintPreview.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemPrintPreview.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPrintPreview;
}
/**
 * Return the JMenuItemRemoveBookMark property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemRemoveBookMark() {
	if (ivjJMenuItemRemoveBookMark == null) {
		try {
			ivjJMenuItemRemoveBookMark = new javax.swing.JMenuItem();
			ivjJMenuItemRemoveBookMark.setName("JMenuItemRemoveBookMark");
			ivjJMenuItemRemoveBookMark.setMnemonic('R');
			ivjJMenuItemRemoveBookMark.setText("Remove Bookmark...");
			ivjJMenuItemRemoveBookMark.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemRemoveBookMark.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemRemoveBookMark;
}
/**
 * Return the JMenuItemRemoveDisplays property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemRemoveDisplays() {
	if (ivjJMenuItemRemoveDisplays == null) {
		try {
			ivjJMenuItemRemoveDisplays = new javax.swing.JMenuItem();
			ivjJMenuItemRemoveDisplays.setName("JMenuItemRemoveDisplays");
			ivjJMenuItemRemoveDisplays.setMnemonic('l');
			ivjJMenuItemRemoveDisplays.setText("Delete...");
			ivjJMenuItemRemoveDisplays.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_DELETE, 0));
			ivjJMenuItemRemoveDisplays.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemRemoveDisplays.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemRemoveDisplays;
}
/**
 * Return the JMenuItemRemoveTemplate property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemRemoveTemplate() {
	if (ivjJMenuItemRemoveTemplate == null) {
		try {
			ivjJMenuItemRemoveTemplate = new javax.swing.JMenuItem();
			ivjJMenuItemRemoveTemplate.setName("JMenuItemRemoveTemplate");
			ivjJMenuItemRemoveTemplate.setMnemonic('v');
			ivjJMenuItemRemoveTemplate.setText("Remove...");
			ivjJMenuItemRemoveTemplate.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemRemoveTemplate.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemRemoveTemplate;
}
/**
 * Return the JMenuItemSearch property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemSearch() {
	if (ivjJMenuItemSearch == null) {
		try {
			ivjJMenuItemSearch = new javax.swing.JMenuItem();
			ivjJMenuItemSearch.setName("JMenuItemSearch");
			ivjJMenuItemSearch.setMnemonic('n');
			ivjJMenuItemSearch.setText("Find...");
			ivjJMenuItemSearch.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
			ivjJMenuItemSearch.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemSearch.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemSearch;
}
/**
 * Return the JMenuItemSpawnTDC property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemSpawnTDC() {
	if (ivjJMenuItemSpawnTDC == null) {
		try {
			ivjJMenuItemSpawnTDC = new javax.swing.JMenuItem();
			ivjJMenuItemSpawnTDC.setName("JMenuItemSpawnTDC");
			ivjJMenuItemSpawnTDC.setMnemonic('w');
			ivjJMenuItemSpawnTDC.setText("New TDC");
			ivjJMenuItemSpawnTDC.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemSpawnTDC.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_W, java.awt.Event.CTRL_MASK));
			ivjJMenuItemSpawnTDC.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemSpawnTDC;
}
/**
 * Return the JMenuOptions property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuOptions() {
	if (ivjJMenuOptions == null) {
		try {
			ivjJMenuOptions = new javax.swing.JMenu();
			ivjJMenuOptions.setName("JMenuOptions");
			ivjJMenuOptions.setMnemonic('O');
			ivjJMenuOptions.setText("Options");
			ivjJMenuOptions.setBackground(java.awt.SystemColor.control);
			ivjJMenuOptions.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuOptions.add(getJMenuItemFont());
			ivjJMenuOptions.add(getJMenuGridLines());
			ivjJMenuOptions.add(getJCheckBoxMenuItemShowLog());
			ivjJMenuOptions.add(getJCheckBoxMenuItemShowToolBar());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuOptions;
}
/**
 * Return the JMenuTemplates property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuTemplates() {
	if (ivjJMenuTemplates == null) {
		try {
			ivjJMenuTemplates = new javax.swing.JMenu();
			ivjJMenuTemplates.setName("JMenuTemplates");
			ivjJMenuTemplates.setMnemonic('t');
			ivjJMenuTemplates.setText("Templates");
			ivjJMenuTemplates.setBackground(java.awt.SystemColor.control);
			ivjJMenuTemplates.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuTemplates.add(getJMenuItemCreateTemplate());
			ivjJMenuTemplates.add(getJMenuItemEditTemplate());
			ivjJMenuTemplates.add(getJMenuItemRemoveTemplate());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuTemplates;
}
/**
 * Return the JMenuTools property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuTools() {
	if (ivjJMenuTools == null) {
		try {
			ivjJMenuTools = new javax.swing.JMenu();
			ivjJMenuTools.setName("JMenuTools");
			ivjJMenuTools.setMnemonic('t');
			ivjJMenuTools.setText("Tools");
			ivjJMenuTools.setBackground(java.awt.SystemColor.control);
			ivjJMenuTools.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuTools.add(getJMenuTemplates());
			ivjJMenuTools.add(getJMenuItemExportCreatedDisplay());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuTools;
}
/**
 * Return the JRadioButtonUserCreated property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getJRadioButtonCustomDisplays() {
	if (ivjJRadioButtonCustomDisplays == null) {
		try {
			ivjJRadioButtonCustomDisplays = new javax.swing.JRadioButtonMenuItem();
			ivjJRadioButtonCustomDisplays.setName("JRadioButtonCustomDisplays");
			ivjJRadioButtonCustomDisplays.setMnemonic('u');
			ivjJRadioButtonCustomDisplays.setText("Custom Displays");
			ivjJRadioButtonCustomDisplays.setBackground(java.awt.SystemColor.control);
			ivjJRadioButtonCustomDisplays.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}

			viewTypeButtonGroup.add( ivjJRadioButtonCustomDisplays );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonCustomDisplays;
}
/**
 * Return the JRadioButtonMenuItemCore property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getJRadioButtonMenuItemAlarmEvents() {
	if (ivjJRadioButtonMenuItemAlarmEvents == null) {
		try {
			ivjJRadioButtonMenuItemAlarmEvents = new javax.swing.JRadioButtonMenuItem();
			ivjJRadioButtonMenuItemAlarmEvents.setName("JRadioButtonMenuItemAlarmEvents");
			ivjJRadioButtonMenuItemAlarmEvents.setSelected(true);
			ivjJRadioButtonMenuItemAlarmEvents.setMnemonic('v');
			ivjJRadioButtonMenuItemAlarmEvents.setText("Alarms and Events");
			ivjJRadioButtonMenuItemAlarmEvents.setBackground(java.awt.SystemColor.control);
			ivjJRadioButtonMenuItemAlarmEvents.setForeground(java.awt.SystemColor.controlText);
			// user code begin {1}

			viewTypeButtonGroup.add( ivjJRadioButtonMenuItemAlarmEvents );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonMenuItemAlarmEvents;
}
/**
 * Return the JSeparator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator1() {
	if (ivjJSeparator1 == null) {
		try {
			ivjJSeparator1 = new javax.swing.JSeparator();
			ivjJSeparator1.setName("JSeparator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator1;
}
/**
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator2() {
	if (ivjJSeparator2 == null) {
		try {
			ivjJSeparator2 = new javax.swing.JSeparator();
			ivjJSeparator2.setName("JSeparator2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator2;
}
/**
 * Return the JSeparator4 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator4() {
	if (ivjJSeparator4 == null) {
		try {
			ivjJSeparator4 = new javax.swing.JSeparator();
			ivjJSeparator4.setName("JSeparator4");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator4;
}
/**
 * Return the JSeparator5 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator5() {
	if (ivjJSeparator5 == null) {
		try {
			ivjJSeparator5 = new javax.swing.JSeparator();
			ivjJSeparator5.setName("JSeparator5");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator5;
}
/**
 * Return the JSeparator6 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getJSeparator6() {
	if (ivjJSeparator6 == null) {
		try {
			ivjJSeparator6 = new javax.swing.JSeparator();
			ivjJSeparator6.setName("JSeparator6");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJSeparator6;
}
/**
 * Return the MainPanel property value.
 * @return com.cannontech.tdc.TDCMainPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private TDCMainPanel getMainPanel() {
	if (ivjMainPanel == null) {
		try {
			ivjMainPanel = new com.cannontech.tdc.TDCMainPanel();
			ivjMainPanel.setName("MainPanel");
			// user code begin {1}
			ivjMainPanel.setToolBar( getAlarmToolBar() );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMainPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (10/17/00 10:37:46 AM)
 * @return java.lang.String
 */
private String getSelectedViewType() 
{
	for( int i = 0; i < getJMenuDisplay().getItemCount(); i++ )
	{
		if( getJMenuDisplay().getItem(i) instanceof javax.swing.JRadioButtonMenuItem )
		{
			if( getJMenuDisplay().getItem(i).isSelected() )
			{				
				return getJMenuDisplay().getItem(i).getText();
			}
		}
	}

	// should never happen
	return null;
}
/**
 * Return the JSeparator2 property value.
 * @return javax.swing.JSeparator
 */
private javax.swing.JSeparator getSeparatorViews()
{
	if (separatorViews == null) 
	{
		try 
		{
			separatorViews = new javax.swing.JSeparator();
			separatorViews.setName("JSeparatorViews");
			separatorViews.setOpaque(false);
			separatorViews.setVisible(true);
			separatorViews.setEnabled(true);
			separatorViews.setMinimumSize(new java.awt.Dimension(0, 2));
			
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	
	return separatorViews;
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 10:41:27 AM)
 * @return com.cannontech.tdc.TDCClient
 */
public TDCClient getTdcClient()
{
	// Connect If we need to only
	if ( tdcClient == null )  // if no connection exists
	{
		// client is null and there is at least 1 column
		tdcClient = new TDCClient( getMainPanel(), getMainPanel().getTableDataModel().getAllPointIDs(), this );

		if( getMainPanel() != null )
			getMainPanel().setTdcClient( tdcClient );

		//start trying to connect immediately
		tdcClient.startConnection();
	}

	return tdcClient;
}
/**
 * Return the TDCFrameJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getTDCFrameJMenuBar() {
	if (ivjTDCFrameJMenuBar == null) {
		try {
			ivjTDCFrameJMenuBar = new javax.swing.JMenuBar();
			ivjTDCFrameJMenuBar.setName("TDCFrameJMenuBar");
			ivjTDCFrameJMenuBar.setBackground(java.awt.SystemColor.control);
			ivjTDCFrameJMenuBar.setForeground(java.awt.SystemColor.control);
			ivjTDCFrameJMenuBar.add(getJMenuFile());
			ivjTDCFrameJMenuBar.add(getJMenuEdit());
			ivjTDCFrameJMenuBar.add(getJMenuDisplay());
			ivjTDCFrameJMenuBar.add(getJMenuBookmarks());
			ivjTDCFrameJMenuBar.add(getJMenuOptions());
			ivjTDCFrameJMenuBar.add(getJMenuTools());
			ivjTDCFrameJMenuBar.add(getJMenuHelp());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTDCFrameJMenuBar;
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2001 11:07:20 AM)
 * @return javax.swing.JDialog
 */
public javax.swing.JDialog getTextSearchDialog() 
{
	if( textSearchDialog == null )
	{
		textSearchDialog = new javax.swing.JDialog(this);
		com.cannontech.tdc.search.TextSearchJPanel searchPanel = new com.cannontech.tdc.search.TextSearchJPanel()
		{
			public void executeCancelButtonPressed()
			{
				getTextSearchDialog().setVisible(false);
			}
			
		};

		//searchPanel.setColumnNames( getMainPanel().getDisplayTable().getColumnModel().get)
		textSearchDialog.setContentPane( searchPanel );
		
		textSearchDialog.setTitle("Search Table");
		textSearchDialog.setLocation( this.getX(), this.getY() );
		textSearchDialog.setSize( 400, 280 );
	}
	
	return textSearchDialog;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION TDCMainFrame() ---------");
	exception.printStackTrace(System.out);

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 11:34:36 AM)
 * Version: <version>
 */
private void initAppearance() 
{
	Cursor original = this.getCursor();
	try
	{
		messageLog.setCallerItem( getJCheckBoxMenuItemShowLog() );
		messageLog.setLocation( this.getLocation() );
		messageLog.setIconImage( this.getIconImage() );
		
		getMainPanel().initializeTable();

		messageLog.setVisible( getJCheckBoxMenuItemShowLog().getState() );		
			
		getAlarmToolBar().setVisible( getJCheckBoxMenuItemShowToolBar().getState() );
		
	}
	finally
	{
		this.setCursor( original );
	}
	
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getJRadioButtonMenuItemAlarmEvents().addActionListener(
		new com.cannontech.tdc.bookmark.SelectionHandler(this.getMainPanel()) );

	getJRadioButtonCustomDisplays().addActionListener(
		new com.cannontech.tdc.bookmark.SelectionHandler(this.getMainPanel()) );
	
	// user code end
	getJMenuItemCreate().addActionListener(this);
	getJMenuItemFont().addActionListener(this);
	getJCheckBoxMenuItemVGridLines().addItemListener(this);
	getJCheckBoxMenuItemHGridLines().addItemListener(this);
	getJMenuItemAbout().addActionListener(this);
	getJMenuItemPrintPreview().addActionListener(this);
	getJMenuItemPrint().addActionListener(this);
	getJMenuItemRemoveDisplays().addActionListener(this);
	getJMenuItemEditDisplays().addActionListener(this);
	getJMenuItemMakeCopy().addActionListener(this);
	getJMenuItemExportDataSet().addActionListener(this);
	getJCheckBoxMenuItemShowLog().addActionListener(this);
	getMainPanel().addTDCMainPanelListener(this);
	getJMenuItemCreateTemplate().addActionListener(this);
	getJMenuItemEditTemplate().addActionListener(this);
	getJMenuItemRemoveTemplate().addActionListener(this);
	getJCheckBoxMenuItemShowToolBar().addActionListener(this);
	getJMenuItemRemoveBookMark().addActionListener(this);
	getJMenuItemAddBookmark().addActionListener(this);
	getJMenuBookmarks().addFocusListener(this);
	getAlarmToolBar().addAlarmToolBarListener(this);
	getJMenuItemExportCreatedDisplay().addActionListener(this);
	getJMenuItemSpawnTDC().addActionListener(this);
	getJMenuItemExit().addActionListener(this);
	getJMenuItemSearch().addActionListener(this);
	getJMenuItemFindNext().addActionListener(this);
	getJMenuItemHelpTopics().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}

		getExternalResources();
		getAboutBox();  // init our about box
		
		// user code end
		setName("TDCFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("Tabular Data Console");
		setSize(569, 368);
		setJMenuBar(getTDCFrameJMenuBar());
		setContentPane(getJFrameContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	setIconImage( com.cannontech.tdc.utils.TDCDefines.ICON_TDC );
	
	initAppearance();
	TDCMainFrame.messageLog.addMessage("TDC started with MAX_ROWS = " + TDCDefines.MAX_ROWS, MessageBoxFrame.INFORMATION_MSG );	
	
	// connect and register with Dispatch
	getTdcClient();

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:21:40 PM)
 * @return boolean
 */
private boolean isUserViewActionPermittable() 
{
	// make sure we are displaying a customized(user created) view
	if( getMainPanel().isUserDefinedDisplay() )
		return true;
	else
	{
		closeBox.showMessageDialog(this, "User created displays are the only displays allowed to perform that function.",
										"Message Box", closeBox.WARNING_MESSAGE);
		this.repaint();
		return false;
	}	
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJCheckBoxMenuItemVGridLines()) 
		connEtoC9(e);
	if (e.getSource() == getJCheckBoxMenuItemHGridLines()) 
		connEtoC11(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jCheckBoxMenuItemShowLog_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{

	messageLog.setVisible( getJCheckBoxMenuItemShowLog().getState() );

	fireOtherTDCMainFrame_actionPerformed( new SpawnTDCMainFrameEvent(this, com.cannontech.tdc.spawn.SpawnTDCMainFrameEvent.LOG_TOGGLE) );
	this.repaint();
	
	return;
}
/**
 * Comment
 */
public void jCheckBoxMenuItemShowToolBar_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getAlarmToolBar().setVisible( getJCheckBoxMenuItemShowToolBar().getState() );
	this.repaint();
	
	return;
}
/**
 * Comment
 */
public void jCheckBoxMenuItemVGridLines_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{
	int vLines = ((getJCheckBoxMenuItemVGridLines().getState() == true) ? 1 : 0);
	int hLines = ((getJCheckBoxMenuItemHGridLines().getState() == true) ? 1 : 0);

	
	getMainPanel().getDisplayTable().setIntercellSpacing(new java.awt.Dimension(vLines, hLines));
	getMainPanel().getDisplayTable().setShowVerticalLines( getJCheckBoxMenuItemVGridLines().getState() );
	getMainPanel().getDisplayTable().setShowHorizontalLines( getJCheckBoxMenuItemHGridLines().getState() );

	if( getMainPanel().isClientDisplay() )
	{
		getMainPanel().getCurrentSpecailChild().setGridLines( getJCheckBoxMenuItemHGridLines().getState(), 
													   getJCheckBoxMenuItemVGridLines().getState() );
	}

	getMainPanel().getDisplayTable().revalidate();
	getMainPanel().getDisplayTable().repaint();
	return;
}
/**
 * Method to handle events for the TDCMainPanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JComboCurrentDisplayAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getMainPanel()) 
		connEtoC20(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jMenuBookmarks_FocusGained(java.awt.event.FocusEvent focusEvent) 
{	
	Object[] values = getBookmarks().getList();
	int itemCount = getJMenuBookmarks().getItemCount() - 1;


	try
	{
		for( int i = itemCount; i >= 0; i-- )
		{
			if( getJMenuBookmarks().getItem(i) != null )
			{
				if( getJMenuBookmarks().getItem(i).getName().equalsIgnoreCase("JMenuItemRemoveBookMark") )
				{
					break; // this is the first non bookmark, lets get out
				}
				else
				{
					getJMenuBookmarks().remove( i );
				}
			}
		}			
	}
	catch( NullPointerException npe )
	{
		throw new Error("We were unable to delete a bookmark when the JMenuBookmark received FOCUS, the getName() probably failed on the item!!");
	}

	// since we are adding bookmarks, put a seperator above them
	getJMenuBookmarks().add( getSeparatorViews() );

	for( int i = 0; i < values.length; i++ )
		addUserBookmark( values[i] );
	

	return;
}
/**
 * Comment
 */
public void jMenuItemAbout_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
/*	com.cannontech.tdc.aboutbox.AboutBox About = 
			new com.cannontech.tdc.aboutbox.AboutBox( this, TDCDefines.VERSION );

	About.setModal( true );
	About.setLocationRelativeTo( this );
	About.show();
*/
	getAboutBox().setModal( true );
	getAboutBox().setLocationRelativeTo( this );
	getAboutBox().show();

	this.repaint();
	
	return;
}
/**
 * Comment
 */
public void jMenuItemAddBookmark_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	// only add user defined views
	if( getMainPanel().getJComboCurrentDisplay().getSelectedIndex() >= 0 )
	{
		if( getMainPanel().getCurrentDisplay().getDisplayNumber() <= Display.BEGINNING_CLIENT_DISPLAY_NUMBER )
			getBookmarks().addBookmark( getMainPanel().getCurrentDisplay().getTitle() + BookMarkBase.BOOKMARK_TOKEN + getMainPanel().getCurrentDisplay().getName() );
		else
			getBookmarks().addBookmark( getMainPanel().getCurrentDisplay().getType() + BookMarkBase.BOOKMARK_TOKEN + getMainPanel().getCurrentDisplay().getName() );
	}
	
		
	return;
}
/**
 * Comment
 */
public void jMenuItemCreate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if ( !checkDataBaseConnection() )
		return;
	
	Cursor original = getCursor();
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
	
	try
	{
		com.cannontech.tdc.createdisplay.CreateDisplayDialog createDisplay = 
								new com.cannontech.tdc.createdisplay.CreateDisplayDialog( this );
								
		Object previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem();

		setCursor( original );
		createDisplay.setModal(true);
		createDisplay.setLocationRelativeTo( this );
		createDisplay.show();

		if( !createDisplay.getDisplayName().equalsIgnoreCase("") )
			previousItem = createDisplay.getDisplayName();
	
		if( !dialogCanceled(createDisplay) )
		{
			if( !getJRadioButtonCustomDisplays().isSelected() )
				getJRadioButtonCustomDisplays().doClick();
				
			setUpMainFrame( previousItem ); // return to the main Frame
		}

	}
	finally
	{
		setCursor( original );
		this.repaint();	
	}
	
	return;
}
/**
 * Comment
 */
public void jMenuItemCreateTemplate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if ( !checkDataBaseConnection() )
		return;

	ColumnEditorDialog editor = new ColumnEditorDialog( this, "Create Template", ColumnEditorDialog.DISPLAY_NAME_ONLY, getMainPanel().getCurrentDisplayNumber() );
	
	editor.setModal( true );
	editor.setLocationRelativeTo( this );
	editor.show();

	this.repaint();
	
	return;
}
/**
 * Comment
 */
public void jMenuItemEdit_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	
	if ( !checkDataBaseConnection() || !getMainPanel().hasUserDefinedViews() )
		return;


	Cursor original = getCursor();	
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
	long originalDisplayNumber = getCurrentDisplayNumber();
	
	try
	{
		Object previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem();

		com.cannontech.tdc.editdisplay.EditDisplayDialog display = 
					new com.cannontech.tdc.editdisplay.EditDisplayDialog( this, previousItem.toString() );

		setCursor( original );							
		display.setModal(true);
		display.setLocationRelativeTo( this );	
		display.show();

		if( !dialogCanceled(display) && originalDisplayNumber >= (Display.BEGINNING_USER_DISPLAY_NUMBER-1) )
		{
			if( !getJRadioButtonCustomDisplays().isSelected() )
				getJRadioButtonCustomDisplays().doClick();
				
			setUpMainFrame( previousItem ); // return to the main Frame
		}
	}
	finally
	{	
		setCursor( original );
		this.repaint();
	}

	return;
}
/**
 * Comment
 */
public void jMenuItemEditTemplate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if ( !checkDataBaseConnection() )
		return;

	java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame( this );
	ColumnEditorDialog editor = new ColumnEditorDialog( owner, "Edit Template", ColumnEditorDialog.DISPLAY_COMBO_ONLY, getMainPanel().getCurrentDisplayNumber() );
	
	editor.setModal( true );
	editor.setLocationRelativeTo( this );
	editor.show();

	this.repaint();
	
	return;
}
/**
 * Comment
 */
public void jMenuItemExit_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	this.setVisible( false );
	
	writeParameters();
	getMainPanel().updateDisplayColumnData();
	getMainPanel().writeAllDisplayColumnData();
	
	fireOtherTDCMainFrame_actionPerformed( new SpawnTDCMainFrameEvent( this, SpawnTDCMainFrameEvent.EXIT_TDC ) );
	return;
}
/**
 * Comment
 */
public void jMenuItemExportCreatedDisplay_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	// make sure we are displaying a customized view
	if( !isUserViewActionPermittable() )
		return;
		
	ExportCreatedDisplay export = new ExportCreatedDisplay( 
			getMainPanel().getCurrentDisplayNumber(),
			this,
			getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString() );

	return;
}
/**
 * Comment
 */
public void jMenuItemExportDataSet_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getMainPanel().isClientDisplay() )
	{		
		getMainPanel().getCurrentSpecailChild().exportDataSet();
		return;
	}

	if ( getMainPanel().getCurrentDisplayNumber() <= 0 )
		return;
	
	Object previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem();
		
	com.cannontech.tdc.exportdata.ExportOptionDialog exportDisplay = 
				new com.cannontech.tdc.exportdata.ExportOptionDialog(
						this,
						getMainPanel().getDisplayTable().getColumnCount(),
						previousItem.toString(),
						getMainPanel().getJLabelDate().getText(),
						getMainPanel().getJLabelTime().getText(),
						createPrintableText() );							

	exportDisplay.setModal(true);
	exportDisplay.setLocationRelativeTo( this );
	exportDisplay.show();

	this.repaint();

	return;
}
/**
 * Comment
 */
public void jMenuItemFindNext_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	try
	{
		((com.cannontech.tdc.search.TextSearchJPanel)getTextSearchDialog().getContentPane()).findNextOccurence();
	}
	catch( ClassCastException c )
	{
		//should not get here ever!!!!
		c.printStackTrace(System.out);
	}

	return;
}
/**
 * Comment
 */
public void jMenuItemFont_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	Cursor original = getCursor();
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	java.awt.Font prevFont = null;

	if( getMainPanel().isClientDisplay() )
		prevFont = getMainPanel().getCurrentSpecailChild().getFont();
	else
		prevFont = getMainPanel().getDisplayTable().getFont();
	
	
	try
	{
		FontEditorFrame display = new FontEditorFrame( this );

		display.setSelectedFont( prevFont );
		
		display.setModal(true);
		display.setLocationRelativeTo( this );
		display.show();

		if( getMainPanel().isClientDisplay() )
			getMainPanel().getCurrentSpecailChild().setFont( display.getSelectedFont() );
			
		if( display.isDisplayable() )
			getMainPanel().setTableFont( display.getSelectedFont() );

		display.dispose();
	}
	finally
	{
		setCursor( original );
		this.repaint();
	}
		
	return;
}
/**
 * Comment
 */
public void jMenuItemHelpTopics_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	Process p = com.cannontech.common.util.CtiUtilities.showHelp( TDCDefines.HELP_FILE );

	return;
}
/**
 * Comment
 */
public void jMenuItemMakeCopy_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if ( !checkDataBaseConnection() || !isUserViewActionPermittable() )
		return;

	Cursor original = getCursor();	
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	try
	{
		Object previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem();
		
		com.cannontech.tdc.editdisplay.EditDisplayDialog display = 
								new com.cannontech.tdc.editdisplay.EditDisplayDialog( this, previousItem.toString() );
								
		setCursor( original );							
		display.setModal(true);
		display.setLocationRelativeTo( this );
		
		previousItem = display.createCopy();	
		
		// return to the main Frame
		if( previousItem != null )
			setUpMainFrame( previousItem );

	}
	finally
	{
		setCursor( original );
		this.repaint();
	}
	
	return;
}
/**
 * Comment
 */
public void jMenuItemPrint_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	closeBox.showMessageDialog(this, "This menu Option is not supported as of 2/18/2000, Use the Print Preview Menu Item",
									"Message Box", closeBox.WARNING_MESSAGE);

	return;
	
/*	if( getMainPanel().getJComboCurrentDisplay().getItemCount() <= 0 ||
		getMainPanel().getDisplayTable().getColumnCount() <= 0 )
		return;
		
	// printing does not work on JDK 1.2
	if( System.getProperty("java.version").equalsIgnoreCase("1.2" ) || 
		 System.getProperty("java.version").equalsIgnoreCase("1.2.0" ) )
	{
		closeBox.showMessageDialog(this, "Printing is not supported for JDK 1.2",
										"Message Box", closeBox.WARNING_MESSAGE);
		return;
	}
	
	Cursor original = getCursor();
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
	
	int columnCount = getMainPanel().getDisplayTable().getColumnCount();
	JCPageTable table = null;
	java.awt.Font oldFont = null;
	JCPrinter printer = null;

	try
	{
		printer = new JCAWTPrinter();
	}
	catch (JCAWTPrinter.PrinterJobCancelledException e) 
	{
		System.out.println("Print Job Cancelled by user");
		return;
	}
		
	JCDocument document = new JCDocument(printer, JCDocument.BLANK_8p5X11);
		
	// instantiate a flow object on the document
	JCFlow flow = new JCFlow(document);

	JCPageTableFromJTable convertTable = new JCPageTableFromJTable();

	table = new JCPageTable( document, columnCount );

	JCPageTable table_header = table.createHeaders();		
	table.fitToFrame(flow.getCurrentFrame(),
					 flow.getCurrentTextStyle());
	
	convertTable.populateTable( table, getMainPanel().getDisplayTable().getModel() );

	JCTextStyle textStyle = new JCTextStyle("TextStyle");

	flow.setCurrentTextStyle( textStyle );
	
	oldFont = textStyle.getFont();
		
	textStyle.setFont( new java.awt.Font(oldFont.getName(), java.awt.Font.PLAIN, 21 ) );
	flow.print( getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString() );		
	flow.newLine();
	textStyle.setFont( oldFont );
	flow.print( getMainPanel().getJLabelDate().getText() + " " +
					getMainPanel().getJLabelTime().getText());

	textStyle.setFont( oldFont );
	textStyle.setAlignment( JCTextStyle.ALIGNMENT_LEFT );	
	flow.newLine();
	flow.print( table );


	document.print();
	setCursor( original );
	
   
   this.repaint();

	return;
*/
}
/**
 * Comment
 */
public void jMenuItemPrintPreview_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{		
	// printing does not work on JDK 1.2 or 1.2.1
	if( System.getProperty("java.version").equalsIgnoreCase("1.2" ) || 
		 System.getProperty("java.version").equalsIgnoreCase("1.2.0" ) || 
		 System.getProperty("java.version").equalsIgnoreCase("1.2.1" ) )
	{
		closeBox.showMessageDialog(this, "Printing is not supported for JDK 1.2 and JDK 1.2.1",
 										 "Message Box", closeBox.WARNING_MESSAGE);
		return;
	}
	
	Cursor original = getCursor();
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	try
	{
		JCPrinter printer = new JCAWTScreenPrinter();
		JCDocument document = new JCDocument(printer, JCDocument.BLANK_8p5X11);
		JCFlow flow = new JCFlow(document); // instantiate a flow object on the document
		JCTextStyle textStyle = new JCTextStyle("TextStyle");
		flow.setCurrentTextStyle( textStyle );

		if( getMainPanel().isClientDisplay() )
		{
			printClientDisplay( textStyle, flow, document );			
		}
		else
		{
			printDisplay( textStyle, flow, document );			
		}

		setCursor( original );
	}
	finally
	{
		setCursor( original );
		this.repaint();
	}
	 
	return;
}
/**
 * Comment
 */
public void jMenuItemRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if ( !checkDataBaseConnection() )
		return;
	
	Cursor original = getCursor();
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
	long originalDisplayNumber = getCurrentDisplayNumber();
	
	try
	{
	 	Object previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem();

	 	com.cannontech.tdc.removedisplay.RemoveDisplayDialog display = 
							new com.cannontech.tdc.removedisplay.RemoveDisplayDialog( this, previousItem.toString() );
							
		setCursor( original );
		display.setModal(true);
		display.setLocationRelativeTo(this);
		display.setTitle("Delete Display");
		display.show();
		
		
		if( !dialogCanceled(display) && originalDisplayNumber >= Display.BEGINNING_USER_DISPLAY_NUMBER )
		{
			setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

			getMainPanel().initComboCurrentDisplay();
			
			// just in case its also a bookmark
			for( int i = 0; i < display.getDisplayPanel().getRemovedDisplays().length; i++ )
				getBookmarks().removeBookmark( display.getDisplayPanel().getRemovedDisplays()[i]  );

			getMainPanel().getJComboCurrentDisplay().setSelectedItem( previousItem );
			
			if ( getMainPanel().getJComboCurrentDisplay().getItemCount() > 0 )
				getMainPanel().setUpTable();
			else
				getMainPanel().resetTable();
		}
	}
	finally
	{
		setCursor( original );
		this.repaint();
	}
	
	return;	

}
/**
 * Comment
 */
public void jMenuItemRemoveBookMark_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
 	com.cannontech.tdc.bookmark.RemoveBookMarkDialog display = 
						new com.cannontech.tdc.bookmark.RemoveBookMarkDialog( this );
						
	display.setModal(true);
	display.setLocationRelativeTo(this);
	display.setBookMarkBase( getBookmarks() );
	display.show();		
	
	this.repaint();
	
	return;
}
/**
 * Comment
 */
public void jMenuItemRemoveSingleDisplay_ActionPerformed1(java.awt.event.ActionEvent actionEvent) 
{
	if ( !checkDataBaseConnection() || !isUserViewActionPermittable() )
		return;
		
	Cursor original = getCursor();	
 	String displayToRemove = getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString();

	int returnValue = closeBox.showConfirmDialog(this, 
									"Are You Sure You Want To Remove \n      " + displayToRemove,
									"Removing " + displayToRemove, 
									closeBox.YES_NO_OPTION, 
									closeBox.WARNING_MESSAGE);

	if( returnValue == closeBox.YES_OPTION )
	{		
		setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

		try
		{	
			// just in case its also a bookmark
			getBookmarks().removeBookmark( displayToRemove  );
				
			com.cannontech.tdc.removedisplay.RemoveDisplayPanel display =
				new com.cannontech.tdc.removedisplay.RemoveDisplayPanel( displayToRemove, String.valueOf( getMainPanel().getCurrentDisplayNumber() ) );			
			
			getMainPanel().initComboCurrentDisplay();
			getMainPanel().setUpTable();
		}
		finally
		{		
			setCursor( original );
		}
	}	
	
	this.repaint();
	
	return;
}
/**
 * Comment
 */
public void jMenuItemRemoveTemplate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if ( !checkDataBaseConnection() )
		return;
	
	Cursor original = getCursor();
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	try
	{
	 	Object previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem();

	 	com.cannontech.tdc.createdisplay.RemoveTemplateDialog display = 
							new com.cannontech.tdc.createdisplay.RemoveTemplateDialog( this );
							
		setCursor( original );
		display.setModal(true);
		display.setLocationRelativeTo( this );	
		display.show();
	}
	finally
	{
		setCursor( original );
	}

	this.repaint();
	
	return;	
}
/**
 * Comment
 */
public void jMenuItemSearch_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	try
	{
		javax.swing.JTable table = null;
		
		if( getMainPanel().isClientDisplay() )
			table = getMainPanel().getCurrentSpecailChild().getJTables()[0];
		else
			table = getMainPanel().getDisplayTable();
			
		((com.cannontech.tdc.search.TextSearchJPanel)getTextSearchDialog().getContentPane()).setTableToSearch( table );

		String[] columnNames = new String[ table.getColumnModel().getColumnCount() ];
		for( int i = 0; i < table.getColumnModel().getColumnCount(); i++ )
			columnNames[i] = table.getColumnName(i);
			
		((com.cannontech.tdc.search.TextSearchJPanel)getTextSearchDialog().getContentPane()).setColumnNames( columnNames );
		getTextSearchDialog().show();
	}
	catch( ClassCastException c )
	{
		//should not get here ever!!!!
		c.printStackTrace(System.out);
	}
	
	return;
}
/**
 * Comment
 */
public void jMenuItemSpawnTDC_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	System.gc();
	long totalMemory = Runtime.getRuntime().totalMemory()/1000;
	long prevMemory = totalMemory - (Runtime.getRuntime().freeMemory()/1000);
	
	Cursor original = getCursor();	
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	try
	{
		fireOtherTDCMainFrame_actionPerformed( new com.cannontech.tdc.spawn.SpawnTDCMainFrameEvent(this, com.cannontech.tdc.spawn.SpawnTDCMainFrameEvent.CREATE_TDCMAINFRAME) );
	}
	finally
	{
		setCursor( original );
	}

	TDCMainFrame.messageLog.addMessage("New SPAWN of TDC has been created (Available Memory Before/After = " + (totalMemory - (Runtime.getRuntime().freeMemory()/1000)) + "k / " + prevMemory + "k", MessageBoxFrame.INFORMATION_MSG );
	return;
}
/**
 * Method to handle events for the AlarmToolBarListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JToolBarButtonAckAllAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAlarmToolBar()) 
		connEtoC24(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AlarmToolBarListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JToolBarButtonClearAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAlarmToolBar()) 
		connEtoC28(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AlarmToolBarListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JToolBarButtonClearAlarmAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AlarmToolBarListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JToolBarButtonClearViewableAlarmsAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAlarmToolBar()) 
		connEtoC26(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AlarmToolBarListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JToolBarButtonRefreshAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAlarmToolBar()) 
		connEtoC29(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AlarmToolBarListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void JToolBarButtonSilenceAlarmsAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAlarmToolBar()) 
		connEtoC27(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * 
 * @param newEvent java.beans.PropertyChangeEvent
 */
public void JToolBarJCDateChange_actionPerformed(java.beans.PropertyChangeEvent event) {
	// user code begin {1}
	if (event.getSource() == getAlarmToolBar()) 
		alarmToolBar_JToolBarJCDateChange_actionPerformed(event);
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args)
{
	try
	{
		System.out.println("Syntax for optional parameters is as follows:");
		System.out.println("   TDCMainFrame view=<value> display=<value>");
		System.out.println();

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(2000);
		
		CommandLineParser parser = null;
		TDCMainFrame aTDCFrame = null;
		com.cannontech.tdc.spawn.TDCOverSeeer overSeer = new com.cannontech.tdc.spawn.TDCOverSeeer();

		if( args.length > 1 )  // the user tried to enter some params
		{
			System.out.println("Remember when entering parameters be sure to surrond parameters with spaces with double quotes.");
			System.out.println("Example:  TDCMainFrame view=Core \"display=Event Viewer\" ");
			
			parser = new CommandLineParser( TDCMainFrame.COMMAND_LINE_PARAM_NAMES );						
			aTDCFrame = overSeer.createTDCMainFrame( parser.parseArgs( args ) );
		}
		else
			aTDCFrame = overSeer.createTDCMainFrame();


		aTDCFrame.visibilityInitialization();
	}
	catch (Throwable exception)
	{
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void mainPanel_JComboCurrentDisplayAction_actionPerformed(java.util.EventObject newEvent) 
{
	if( !(newEvent.getSource() instanceof TDCMainPanel) )
		return;

	TDCMainPanel source = (TDCMainPanel)newEvent.getSource();
		
	if( source.isClientDisplay() )
	{
		// do nothing for now if we are viewing a special display
	}
	else
	{

		// enable/disable the correct corresponding buttons for the the current view
		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMPONENT_INDEX_CLEAR,
			(source.getTableDataModel().getCurrentDisplayNumber() == Display.EVENT_VIEWER_DISPLAY_NUMBER) );
		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMPONENT_INDEX_ACKALL, true );
		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMPONENT_INDEX_CLEARVIEWABLE, true );
		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMPONENT_INDEX_DATELABEL,
			(source.getTableDataModel().getCurrentDisplayNumber() == Display.EVENT_VIEWER_DISPLAY_NUMBER) );
		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMPONENT_INDEX_DATE,
			(source.getTableDataModel().getCurrentDisplayNumber() == Display.EVENT_VIEWER_DISPLAY_NUMBER) );

		
		setTitleFromDisplay();				
		refreshTDCClient();
	}
		
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 4:11:03 PM)
 * @param e com.cannontech.tdc.spawn.TDCMainFrameEvent
 */
public void otherTDCMainFrameActionPerformed(SpawnTDCMainFrameEvent e)
{
	if( !(e.getSource() instanceof TDCMainFrame) )
		throw new Error("In otherTDCMainFrameActionPerformed(SpawnTDCMainFrameEvent e), e was an event NOT caused by a TDCMainFrame");

	TDCMainFrame tdcMainFrameSource = (TDCMainFrame)e.getSource();

	if( e.getId() == SpawnTDCMainFrameEvent.LOG_TOGGLE )
	{
		getJCheckBoxMenuItemShowLog().setState( tdcMainFrameSource.getJCheckBoxMenuItemShowLog().getState() );
	}
}
private void printClientDisplay( JCTextStyle textStyle, JCFlow flow, JCDocument document ) 
{
	if( getMainPanel().getCurrentSpecailChild().getJTables().length <= 0 )
		return;

	for( int i = 0; i < getMainPanel().getCurrentSpecailChild().getJTables().length; i++ )
	{
		JCPageTable table = new JCPageTable( document, getMainPanel().getCurrentSpecailChild().getJTables()[i].getModel().getColumnCount() );
		JCPageTableFromJTable convertTable = new JCPageTableFromJTable();
		flow.print( getMainPanel().getCurrentSpecailChild().getJTables()[i].getName() );
		convertTable.populateTable( table, getMainPanel().getCurrentSpecailChild().getJTables()[i].getModel() );
		java.awt.Font oldFont = textStyle.getFont();
		
		// Set the font
		textStyle.setFont( new java.awt.Font(oldFont.getName(), java.awt.Font.PLAIN, 21 ) );

		table.fitToFrame(flow.getCurrentFrame(),
						 flow.getCurrentTextStyle());
		table.setRowColumnDominance( JCPageTable.COLUMN_DOMINANCE );

		
			
		flow.newLine();
		textStyle.setFont( oldFont );
		flow.print( getMainPanel().getJLabelDate().getText() + " " +
					getMainPanel().getJLabelTime().getText());

		textStyle.setFont( oldFont );
		textStyle.setAlignment( JCTextStyle.ALIGNMENT_LEFT );	
		flow.newLine();
		flow.print( table );
	}

	
   JCAWTPreviewer previewer = new JCAWTPreviewer(
							"Print Preview", 
		    				this,
							document );
   		
	previewer.setLocationRelativeTo( this );
	previewer.setSize( (int)(this.getWidth() * .60), this.getHeight() );
	previewer.setVisible(true);
	
}/**
 * Insert the method's description here.
 * Creation date: (1/25/2001 10:12:00 AM)
 */
private void printDisplay( JCTextStyle textStyle, JCFlow flow, JCDocument document )
{
	if( getMainPanel().getDisplayTable().getColumnCount() <= 0 )
		return;

	JCPageTable table = new JCPageTable( document, getMainPanel().getDisplayTable().getColumnCount() );
	JCPageTableFromJTable convertTable = new JCPageTableFromJTable();
	flow.print( getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString() );
	convertTable.populateTable( table, getMainPanel().getDisplayTable().getModel() );
	java.awt.Font oldFont = textStyle.getFont();
	
	// Set the font
	textStyle.setFont( new java.awt.Font(oldFont.getName(), java.awt.Font.PLAIN, 21 ) );

	table.fitToFrame(flow.getCurrentFrame(),
					 flow.getCurrentTextStyle());
	table.setRowColumnDominance( JCPageTable.COLUMN_DOMINANCE );

	
		
	flow.newLine();
	textStyle.setFont( oldFont );
	flow.print( getMainPanel().getJLabelDate().getText() + " " +
				getMainPanel().getJLabelTime().getText());

	textStyle.setFont( oldFont );
	textStyle.setAlignment( JCTextStyle.ALIGNMENT_LEFT );	
	flow.newLine();
	flow.print( table );

   JCAWTPreviewer previewer = new JCAWTPreviewer(
							"Print Preview", 
		    				this,
							document );
   		
	previewer.setLocationRelativeTo( this );
	previewer.setSize( (int)(this.getWidth() * .60), this.getHeight() );
	previewer.setVisible(true);
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/00 11:02:58 AM)
 */
protected void processWindowEvent( java.awt.event.WindowEvent e ) 
{	
	if(e.getID() == e.WINDOW_CLOSING) 
	{
		this.setVisible( false );
	
		writeParameters();
		getMainPanel().updateDisplayColumnData();
		getMainPanel().writeAllDisplayColumnData();

		fireOtherTDCMainFrame_actionPerformed( new SpawnTDCMainFrameEvent(
						this, 
						com.cannontech.tdc.spawn.SpawnTDCMainFrameEvent.DISPOSE_TDCMAINFRAME) );
	}
	
	super.processWindowEvent( e );
}
/**
 * Insert the method's description here.
 * Creation date: (5/25/00 2:39:49 PM)
 * Version: <version>
 */
private void refreshTDCClient() 
{
	// create a new registration message
	getTdcClient().reRegister( getMainPanel().getTableDataModel().getAllPointIDs() );
}
/**
 * 
 * @param newListener com.cannontech.tdc.TDCMainFrameListener
 */
public void removeTDCMainFrameListener(com.cannontech.tdc.TDCMainFrameListener newListener) {
	fieldTDCMainFrameListenerEventMulticaster = com.cannontech.tdc.TDCMainFrameListenerEventMulticaster.remove(fieldTDCMainFrameListenerEventMulticaster, newListener);
	return;
}
/**
 * 
 * @param newListener com.cannontech.tdc.TDCMainFrameListener
 */
public void removeTDCMainFrameSpawnListener(TDCMainFrameSpawnListener newListener) 
{
	spawnTDCEventMulticaster = com.cannontech.tdc.spawn.TDCMainFrameSpawnListenerEventMulticaster.remove(spawnTDCEventMulticaster, newListener);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/00 12:35:09 PM)
 */
private void setAllMenuItemsEnabled() 
{
	for( int i = 0; i < getJMenuDisplay().getItemCount(); i++ )
	{
		// dont enable seperators
		if( getJMenuDisplay().getItem( i ) instanceof javax.swing.JMenuItem )
			getJMenuDisplay().getItem( i ).setEnabled( true );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (3/22/00 12:35:09 PM)
 */
private void setEditingMenuItemsDisabled() 
{
	for( int i = 1; i <= 4; i++ )
		getJMenuDisplay().getItem( i ).setEnabled( false );
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 1:59:19 PM)
 * Version: <version>
 * @param value java.lang.Object
 */
public void setMainPanelCombo(Object value) 
{
	for( int i = 0; i < getMainPanel().getJComboCurrentDisplay().getItemCount(); i++)
	{
		if( getMainPanel().getJComboCurrentDisplay().getItemAt(i).equals( value ) )
		{
			getMainPanel().getJComboCurrentDisplay().setSelectedItem( value );
			return;
		}
	}
	
	// we didnt find the item we wanted, it must be a system view
		
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/00 10:16:52 AM)
 * @param buttonText java.lang.String
 */
public void setSelectedViewType(String buttonText) 
{
	for( int i = 0; i < getJMenuDisplay().getItemCount(); i++ )
	{
		if( getJMenuDisplay().getItem(i) instanceof javax.swing.JRadioButtonMenuItem )
		{
			if( getJMenuDisplay().getItem(i).getText().equalsIgnoreCase(buttonText) )
			{
				getJMenuDisplay().getItem(i).doClick();
				return;
			}
		}
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/00 3:43:57 PM)
 */
private void setTitleFromDisplay() 
{
	// dont change the title if we are looking at a specialTDCChild
	if( getMainPanel().isClientDisplay() )
		return;

	final String connected;

	if( getTdcClient().connected() )
	{
		connected = "   [Connected to Dispatch@" + getTdcClient().HOST + ":" + getTdcClient().PORT + "]";
	}
	else
	{
		connected = "   [Not Connected to Dispatch]";
	}

		
	if( getMainPanel().isUserDefinedDisplay() )
	{
		setTitle( getMainPanel().getCurrentDisplay().getName() + connected );
	}
	else if( getMainPanel().isCoreDisplay() )
	{
		setTitle( getMainPanel().getCurrentDisplay().getTitle() + connected );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 11:47:44 AM)
 */
private void setUpMainFrame( Object previousItem ) 
{
	getMainPanel().initComboCurrentDisplay();

	if( previousItem != null )
		getMainPanel().getJComboCurrentDisplay().setSelectedItem( previousItem );

	// doesnt fire the getMainPanel().jComboCurrentDisplay_ActionPerformed() if
	// the new previousItem is at index 0, so I do it manually
	if( getMainPanel().getJComboCurrentDisplay().getSelectedIndex() == 0 )
		getMainPanel().fireJComboCurrentDisplayAction_actionPerformed( new java.util.EventObject( getMainPanel() ) );

		
	this.repaint();	
}
/**
 * Insert the method's description here.
 * Creation date: (5/25/00 12:26:42 PM)
 * Version: <version>
 * @param observ java.util.Observable
 * @param obj java.lang.Object
 */
public void update(java.util.Observable observ, Object obj) 
{
	//Should be an instance of com.cannontech.message.dispatch.ClientConnection
	//notifying us of a change in the connections state
	if( obj instanceof com.cannontech.message.dispatch.ClientConnection )
	{
			
		if (this != null)
			setTitleFromDisplay();

		//if we are not a client display and have connected, lets register for our points
		if( getTdcClient().connected() && !getMainPanel().isClientDisplay() )
			getTdcClient().reRegister( getMainPanel().getTableDataModel().getAllPointIDs() );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (10/13/00 12:18:55 PM)
 */
public void visibilityInitialization() 
{
	setVisible( true );
	getMainPanel().checkForMissingPoints();
}
/**
 * Insert the method's description here.
 * Creation date: (3/6/00 1:51:30 PM)
 */
private void writeParameters() 
{
	try
	{
		java.io.FileWriter writer = new java.io.FileWriter( TDCDefines.OUTPUT_FILE_NAME );
		java.io.File outPutFile = new java.io.File( TDCDefines.OUTPUT_FILE_NAME );

		// write the current display name
		if( getMainPanel().getJComboCurrentDisplay().getSelectedItem() == null )
			writer.write("\r\n");
		else
	 		writer.write( getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString() +"\r\n");

		// write the current display type
		writer.write( getSelectedViewType() + "\r\n" );

		// write the frames X coordinate
		writer.write( String.valueOf( getX() ) + "\r\n" );
			
		// write the frames Y coordinate
		writer.write( String.valueOf( getY() ) + "\r\n" );
			
		// write the frames width
		writer.write( String.valueOf( getWidth() ) + "\r\n" );

		// write the frames height
		writer.write( String.valueOf( getHeight() ) + "\r\n" );

		// current Font of the table
		writer.write( getMainPanel().getDisplayTable().getFont().getName() + "\r\n" );
		writer.write( getMainPanel().getDisplayTable().getFont().getSize() + "\r\n" );

		// current grid line states
		writer.write( getJCheckBoxMenuItemHGridLines().getState() + "\r\n" );
		writer.write( getJCheckBoxMenuItemVGridLines().getState() + "\r\n" );

		// current message box state
		writer.write( getJCheckBoxMenuItemShowLog().getState() + "\r\n" );

		// current toolbox state
		writer.write( getJCheckBoxMenuItemShowToolBar().getState() + "\r\n" );
			
		writer.close();
	}
	catch ( java.io.IOException e )
	{
		handleException( e );
	}
	
}
}
