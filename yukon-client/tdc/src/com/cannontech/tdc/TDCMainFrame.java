package com.cannontech.tdc;
/**
 * Insert the type's description here.
 * Creation date: (1/20/00 11:51:54 AM)
 * @author: 
 */
import com.cannontech.common.gui.util.CTIKeyEventDispatcher;
import com.cannontech.common.gui.util.SplashWindow;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.clientutils.AlarmFileWatchDog;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commandlineparameters.CommandLineParser;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.util.Message;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.message.util.MessageUtils;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.tdc.removedisplay.RemoveDisplayDialog;
import com.cannontech.tdc.removedisplay.RemoveDisplayPanel;
import com.cannontech.tdc.roweditor.SendData;
import com.cannontech.tdc.spawn.SpawnTDCMainFrameEvent;
import com.cannontech.tdc.bookmark.BookMarkBase;

import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import com.cannontech.tdc.filter.ITDCFilter;
import com.cannontech.tdc.fonteditor.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import com.klg.jclass.page.awt.*;
import com.klg.jclass.page.*;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.createdisplay.ColumnEditorDialog;
import com.cannontech.tdc.createdisplay.RemoveTemplateDialog;
import com.cannontech.tdc.utils.DateTimeUserQuery;
import com.cannontech.tdc.utils.TDCDefines;
import com.cannontech.clientutils.commonutils.ModifiedDate;
import com.cannontech.clientutils.parametersfile.ParameterNotFoundException;
import com.cannontech.clientutils.parametersfile.ParametersFile;
import com.cannontech.debug.gui.AboutDialog;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.tdc.editdisplay.EditDisplayDialog;
import com.cannontech.tdc.exportdata.ExportCreatedDisplay;
import com.cannontech.tdc.commandevents.AckAlarm;
import com.cannontech.tdc.spawn.TDCMainFrameSpawnListener;
import com.cannontech.tdc.data.Display;

public class TDCMainFrame extends javax.swing.JFrame implements com.cannontech.tdc.spawn.TDCMainFrameSpawnListener, TDCMainPanelListener, com.cannontech.tdc.toolbar.AlarmToolBarListener, java.awt.event.ActionListener, java.awt.event.ItemListener, java.util.Observer, MessageListener {
	private Clock ticker = null;
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
	private javax.swing.JMenu ivjJMenuAlarms = null;

   private SignalAlarmHandler alarmHandler = null;
   
	private javax.swing.JMenuItem jMenuItemResetCntrlHrs = null;

    public static final URL TDC_GIF = TDCMainFrame.class.getResource("/tdcIcon.gif");


/**
 * TDCFrame constructor comment.
 */
public TDCMainFrame() {
	super();
	//initialize();
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
	
	//initialize();
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
	
	if( e.getSource() == AlarmFileWatchDog.getInstance() )
	{
		//the Mute option could have changed, let use check
		ParametersFile pf = new ParametersFile( CtiUtilities.OUTPUT_FILE_NAME );

		boolean muted = Boolean.valueOf(pf.getParameterValue("Mute", "false")).booleanValue();
//		boolean silenced = Boolean.valueOf(pf.getParameterValue("Silence", "false")).booleanValue();
		
		if( muted )
			getAlarmToolBar().getJToolBarButtonMuteAlarms().setText("UnMute");
		else
			getAlarmToolBar().getJToolBarButtonMuteAlarms().setText("Mute");

		//the Silence option could have changed, let use check
//		if( silenced )
//			getAlarmToolBar().getJToolBarButtonSilenceAlarms().setText("UnSilence");
//		else
//			getAlarmToolBar().getJToolBarButtonSilenceAlarms().setText("Silence");

		
		//fire this for any possible alarm change
		executeAlarm_ActionPerformed( muted );
	}
	
	if( e.getSource() == getJMenuItemResetCntrlHrs() )
		jMenuItemResetCntrlHrs_ActionPerformed( e );


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

   //this is the value that tells the bookmark where to go
   bookMark.putClientProperty( TDCMainPanel.PROP_BOOKMARK, bookMark.getText() );

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
		for( int i = 0; i < rowNumbers.size(); i++ )
		{
			int rowNum = Integer.parseInt( rowNumbers.get(i).toString() );

			PointValues pv = getMainPanel().getTableDataModel().getPointValue( rowNum );

			if( getMainPanel().getTableDataModel().isRowInAlarmVector(rowNum) )
			{
				AckAlarm.sendAckAll( pv.getPointID() );
			
				TDCMainFrame.messageLog.addMessage(
						"An ACK ALARM message was sent for ALL ALARMS on pointid " + pv.getPointID(), MessageBoxFrame.INFORMATION_MSG );
			}

		}

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
 * Commen
 *
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

		if( Display.isAlarmDisplay(getMainPanel().getCurrentDisplayNumber()) )
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
*/

/**
 * Comment
 */
public void alarmToolBar_JToolBarButtonRefreshAction_actionPerformed(java.util.EventObject newEvent) 
{
	getMainPanel().executeRefresh_Pressed();

	return;
}

private void executeAlarm_ActionPerformed( boolean muted )
{

	if( muted )
	{
		getAlarmToolBar().getJToolBarButtonMuteAlarms().setText("UnMute");
	}
	else
	{
		getAlarmToolBar().getJToolBarButtonMuteAlarms().setText("Mute");
	}


	if( getMainPanel().isClientDisplay() )
		getMainPanel().getCurrentSpecailChild().setAlarmMute( muted );
	
	//Always set the MainTableModel sound toggle flag	
	getMainPanel().getTableDataModel().setAlarmMute( muted );

	
	getAlarmToolBar().getJToolBarButtonMuteAlarms().repaint();
}



/**
 * Comment
 */
public void alarmToolBar_JToolBarButtonMuteAlarmsAction_actionPerformed(java.util.EventObject newEvent) 
{
	try
	{			
		ParametersFile pf = new ParametersFile( CtiUtilities.OUTPUT_FILE_NAME );
		pf.updateValues( 
			new String[] {"Mute"}, 
			new String[] { String.valueOf(
				getAlarmToolBar().getJToolBarButtonMuteAlarms().getText().equalsIgnoreCase("Mute"))} );
	}
	catch( ParameterNotFoundException ex )
	{}			
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
 * Creation date: (10/13/00 1:49:44 PM)
 */
public void destroyClockThread() 
{
	if( ticker != null )
		ticker.interruptClockThread();
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
 * connEtoC27:  (AlarmToolBar.alarmToolBar.JToolBarButtonMuteAlarmsAction_actionPerformed(java.util.EventObject) --> TDCMainFrame.alarmToolBar_JToolBarButtonMuteAlarmsAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC27(java.util.EventObject arg1) {
	try {
		// user code begin {1}		
		// user code end
		this.alarmToolBar_JToolBarButtonMuteAlarmsAction_actionPerformed(arg1);
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
	destroyClockThread();
	
}


/**
 * Insert the method's description here.
 * Creation date: (3/7/00 9:44:46 AM)
 * @return boolean
 * @param dialog javax.swing.JDialog
 */
private boolean compCanceled(Component comp_) 
{
	//canceled comps are NOT displayable
	return !comp_.isDisplayable();
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


private SignalAlarmHandler getAlarmHandler()
{
   if( alarmHandler == null )
      alarmHandler = new SignalAlarmHandler( getJMenuAlarms(), getMainPanel() );

   return alarmHandler;
}


/**
 * Return the AlarmToolBar property value.
 * @return com.cannontech.tdc.toolbar.AlarmToolBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
protected com.cannontech.tdc.toolbar.AlarmToolBar getAlarmToolBar() {
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
	D0CB838494G88G88GA70ACFADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E165FD8FDCD4D55ABF15153B316F3A9B355652E6E5C5C59BED6EE69BED3451C6E5E5C53BECD1AF3D31E5E59BED34EB4556D54141C445C4C553C4C5A5A5C445A4C5652F72E7D41481D1D1991891C60719F198959375771CF36E735CBBF7AEB2A03E3F76737A79BCB267FB1EF34E73BC67B9FF6EB9671EABE5AC9C16B2FEC206A40DCF1164FF6B47CBD25C7271521E95612FA2165ADCED10D47F96814D10828E
	A4435FCC08CB3F324610AED51EC905F0AE04073BEA8CF36077CC29AC4D1E8E9976D0CEB9447D6AFF1E1EFD7EF2EEAC46F28CBC7CF0D59A7C1D86148F1889E89813C17ACBD5260CF7C1F81A7C1B51788912D4ECE6BCCF3BB3D5F83C4C1781F1B9C019AD0C679FD573657841008D830A869AE3963C9270CC6652DD3E21D066EB7A59B0A971496593263232D479A3B537083403643FD952BD5C9093A5C7B953312876E85B66D6E7C7DE2B5961F0BABC565676BC0F25F54386D3F959D63739438ABC09DC368F5C29BC1D
	A405C3B82015E1F9F94C9E05703B96283D9565FBEEEA11DCFE5051E1D25CAD73FA14AF32D548B526D511AFAABCF562D08B4B7F4B6AB4B9DD8E041B01046CBFBA38349759371BED8EEF79DB2BD17ED88B4A3FFDAF4BBF43DAED4885AA37A21E310F61CD9076G85DAD12FCD4BD12F465377C8DD5191A953FB50EB38D568B34A2A68D5F4CF6A44395C0FD616B2DFC8BD8C65829583D90086818D877A127B6F9B45AB60EFC90B274C595161F41438DC25F60BFB3B53E235436F8D9BA0C646F6DBDB1DEE0BA4899F9D67
	4E551187A97C3037FFB2923A6D115027C2F75FA635DF76C8CA0A0EEEA3DE7CFA42E8B90F78430AEEF1ED4CEE4F64B33BA543EF164E86A470A92B991E5D2E6022FE8B387F3030E42BB6986DE86746E9A80BB179FAA9FD35F102C1C71621FFAC19B054A9E488F3AA32889B2714B39B4782BE8EA85D856D8C2892285D05B64EAFD95B1B0D2B402F5C8E333D6EF80B4B6C76149E3759ADD637EC6BC5556733359E09F42B7A1CCE58A5E42D3A0EE26F2B5135CBECC87E04C1AEE10FE017E297D1F6FB0FE5073BFCFDE39A
	5561A33C2D0CF609BA1CC878D39C0FD36122EE6BB84E428990E7BAD6ED907C217B99BE7218605FC4793C4071A237020BFC66713648429190976A29B6087A3D25027515C3CFD49BC6G0D820A87CABED9ED4882AAB809F5FC3AE4151F6D28D6D53FA2EDC1A99BA02CDD9E37394A5169B1BBDA2D8DE6374D5CE237F6CAB24F26D2E657962FF4253CFD37C2995B2C8E2B5B6C31B99D10BBF4C760CC1D022726F48663F1DBF92C4956E10582BADC9266DBD7BA937E5A4D1D1EFA17456C31AA9CA2FEFFA9730993668648
	91B2005F03CEB2FBCD6FE47D1C89FE3BCE222DC6EFE5D894046348AEFFAE7D827E5600F3DB6D9BB6145BDA1998E677A97451687C5170B7917807F4010D018ACFC1BF8FB496E878775016CE435884B4383B5A50F886F0206433550679C005C035C06DC0E967205E00AC8190F7C935A18248F5E935A163B2788D54BE88643E3C5A908954FD8564FBE535A189A8F4B07479BF027A7CB10C03C0032E02B401D0A6D074CFC0267F0232FF8A7CC3E07C7C996485547CF31061EAC88B54988465G85828D870A88427E656F
	0D580E4A275EAA653ED82D5B0E98458409B6119024EDC7B3D6B37BE4DE833A82B582F5818D859A899483B48EA873DA0887EA846A828A860AG0A87CA7D85747FC0C6A0BBD0A0D0A850A82031C0E9C3A19E4888E4878A848A859A85B496A86DBA0887B28259018201C201C6810D85CA3B9E62010CC0F620C020D020D1C0E30152FED9ED4887B281F581857F9267B99B720A2161327AE275406A08694E6C4C6A1F7DE676E7FF19EF707E68EA7D3E03460DAED1172CCE91E33E5393BF731BC6591F3C70C122DE795C69
	B261B75A344CA718BF785B37C684754E4B7C17799173393E745BC8AEBF4A68ABA56B581C35BB56BED8DB9C2ED3A6EB37DA0C352B1E4AE06D2ED77BDD2270307E02355F737133B64E5AB4EB2B2CBDF37BDCAA7E4EBF6B1B16759F05277AE73FDA1D7CA2F5FC26AF447A3F79324E2C6FA3BB7CCC74099896EDE96AAA56163E065FDB00564AF175C0D3645FBB653FA9725FC33D144F72AD83AAD1E1B34F433F8408BD93FD2941578119655F2B01566B242D13584C50FF5B587C60D98E4466653B7AE97F568B283B1E68
	008E3690E8BB909BC35BF462276BE0D567A9E31B9FF2FC237A4D66CE1BFA615F88D481349F28C646D649FF976870336726497D3451A29D6C3B8B34BBFBAEDCA97F1E254277CA2C6DA8E1B10F59DC49660291B0CE4582A57D9267B2B34A32A4BE47ABB5F7DAF1D637538D93B22BC51959885EE9652C5E2CA72C8ECF3955E5F5D860C727BC271AD54EECA34F778EBBA51C0BFD49F1B67FAF0389D9A7610B4AE7889C72D04F9EB17EEB9EEF76F85C3616639EEB1D556331B95A14742B39AC1DDE0F961D322E45FC2DA1
	A39846F02022E0547577652C2D633C4D68F49EBD6EB2319929BC7FBB471FF3EB013FBD984773477732F50CC08ABF4743CA7C5E3D0D7D28BB161FBFFC228CF73F4A1868A71FA863B77B3BE47FE6B6DAAE7B9473AF567732BE709B5917D85BE67DA2EB8F9BE5BFE67DB06B27D91BE36D3DC476C356E7333E1275852C6FB9A9093E1235B756FFDEA5093ECC4870D7AE43B030CD94D082D0668D700C8F548EB484E8A4D09CD06A2FE07E85548894G9482B49AE89CD0560DB016G39000682C5G0D81CA9F86E3A1D0B3
	50E020B02098A069A6F83E3D896BB3EBDF5CF8DF9B69357B19320D6648F6D177FD53D4B6DAAE5BE625645DC721ED564BB65CA85BEF0B64BDBEA15548F65CA979767FFBB5E133247475FA7D2F1ACE7A69A7D7754387FF28A46A7A173AF52D57EED2E49F18AEF94F97305FC59FE079323EFE0A645D87E35D337AE6E38B6B0F18FF30BEFC39CEF94CA758D844FCC9BB0EFD2D89B35FD9AD7FDE5A0B5EEBFB09C75A7883282301A251775FDD517358B522C2FF6C7AFD057E5895D1F17E31A92AC2EFEC92F14FD6B0DFF2
	1EF428463A688A361E506AF4B82CFC4025A412D8337D3B0969E1F358BCFBEC16B62B274E76BDCC823BEEE2E356B23EE6D18B3FADB761B37A49A5780C1E7D7DCDD2442125937456A3075EDABEA17CA6B18E0D3EC9F9C697B23D38150D21D63B35838CD566BC6E70887CE53E9EA2631D93B996F2E888CB1BD33D5D6AB8DE6531F614D8AC39E62ECA1E78D3EBF59FF63ABB2C161CA3669366BC73C9CF9E2F0A3C929EDD41FEFF1D754DB71B33548B5039C7D4999EAA5164E46CB92B0DDFB7FC3DDE4876628656C62BF7
	1BADB6E769F10F476940BCD7CB222D4F143AAC47057CFADC0520C9AB686BB6FB1CEE9D91F553003014C6885A1557F9921CA42F46FA2A4E63F69E35B299D7ABB2EC597CB913D3A51C40DB267F545B36DB875632DB1D8ECFDFCD7B1DAC5BE5DBC205ECE56D565623254EAE4C34E11B5BE6B15A9C564EDC9B847358FFF59E3347DA56EEF634794A4A5274A8E94F199F2A52643B5AC7F24A191B7BBBA137F54FD0EF1BD6D8EC1EF233478CAD79076237D35784794AB83042319C355279DAD0A3DE41EAA15FAB732E7E7F
	A1DF6F62493E39733BC739FCA5F6333B4364F45ACB4D6EDC33AA10D7AD7F90ED372475E8095DAEF2BB24AB7FF10F4D1E478B5A59F284BAEA9D5155799F6AC37EC65D8238BE8532BE1F34DC510BBEFD92274CEEB53BF9DEFD97A99F7B26146FA7E31F3555E637D2DFE64ECEC33C5C5669321BCF897C216F26A1DE8A8FDF9DE67751CECC93E125B4BBDDCC94F9ECF9311C4A28345ADDD4C67E02EB3D7D2F0CED87D8875683C51EFD76C114F1533E1FEB7AEE68ECFF885D37AC5F8DAB6E7AE1755F3DF55F325C6D25BD
	48DD794314BBDF16FB3D6B9A4DBC2945F9FC20C79C16E5DFBDE20DB28FD9D7F40B370C35EE1B43D36B361E30D9CF8E2C286A1C7BAA71DA6C67A33E39C6C75E0BA0E8DFA5D44D6D5EBEF823370C3B2D9D4E93D639EF6C9CD8E1C55EFD7600B558A72C7A254E5C6822C85A1F71FDB54A79DA2BC64E6D662356B2276B54404A083976D94EF5321CD95D37EA6C5965F23AAF4EEC53AB6BBECB3C1E7CF5DF7B9D7A7DEBDD3B73245159B6B0D27710F91F65AEA439F3EA9F62F233C2EA4D8E2BBDD7AE1187722A4B1C9DAD
	4E3263EE3666AC3B7485CE78BAB073C3FD483C6729551A6FC8174995376B4DEFCC568E4879624CF3B06FBE779D05324C0F2C3F45376F38B8922BF36E73580173084B33EE576B17AF0E445EF9775946B807784B4AFBFBEE1B74DCF0315AA7BECC745562796457DFE46B5A1CBD8FEC0767010BE1F34C3B2FD22F43B9C6D746CDBE6BDB78GB3606B8518F11FED3C06ECDC7855250A3C8DB03B6A4CBD6CECBD5E19477F5F86457A8A3815C51DD7BE1E51212D2AAC3492A9F9F00168B9E8C5F9548F6DD91CA943F67E7A
	71C0AEF49A5E78CA1FF4628F5CD4AA636B3CF88A7ADF58F94622F564CF034EBC57A7DD6BECF62B237522EB69CFB167516FDB526F7E0C477A245FEE6BE13735337D62E9F67E82FA56E92DB2973AFF5BFDFA73CEB152DB64894A4518FEFA17506735939C1B8F35BD527776F511DBD41F6BE31DB21FFBE01266F9254EE5BE69006287368EB057BE4FAF564BF2D69D542E6BF68D783AEED75F1F7B5622FC73977EC2E3C7281856768136A24F334F3E0B73604CB6CDDDEF35B9ACBB2CDD833D76A7675AD7B93F45B6765B
	791AF5893642EAF23AEC2D837C242F645BE71B623CB7E6A12D833339BF352B40233426CC6BF398374BC0FBEA2CB1931E5CC9E9F0BECEF17606A998A7869679CCD1A7FF1FA66126EAC3724D550679B7631922BD5BD8BC1EA9128FCE15B96DF631158BF1C1B733FD5A632BB3A4E56F3560E6718ED4G7C95717F740A0F156327D17A45EB7452275D0269EFD9F37E744F6A264F22748DD5FA7185947F975D78CA0AEF2C560BEF22783F6A463BA8FEDF0DDE7CA00AFFD9B7FEA8440BF7441A0C6AF744C2EF91717170F7
	9C713C605063C99FDEED489E0EBC67AA74F80AA03E16F836E76A7134C3FCB771EC29576389027810612853AF3A75F40A20784CB3FA715194FF1DEE3CD2670F69460F2174DB4F6A45A7D17C633A716937E2FC653973176F510D4F26749F7612FE112497DFC869EF560DD752BF239BEF2474BB476B45B7D37CE85DF8A57F24DE62F3A61CBF5E54CBFCC0AF71213D44476846FBC83F3D934F1F7E72DE626F6DA57E15DE62A7A71FBFFEDDAF7166DE62FF52CB7C833D447F2D177899A9FA7103EF45BE6067A9FAFDC0B0
	4407914FEBA5FABCD1909FCBBC03966B71A4C2FC6AED48D3B7D70FA78B628B086709C6BD9EA344B75F0663D9F9E5BA7C6D349EBB4E1E2D5873E30D75943EF7D57905A62E41ECBF2E3C4FD5D74946CE3B6ADC2D4037D6691FA5B2D6313334C7A97F5A2AC992FBAFCB64A94EBE0519D91EAE372D436CBE458E8A4B6F84DFDB2D56258B64E76F8F87435F305B70DD57BBB77177396457F183EFE76916B761FB42D1409787A45DAE52965FAE6AD6703DC3FCB190CEE0D808602B8DD1735DF3G7906829E86B4DA66CB3B
	C34D67BE08FC96403B6F6809EF59A1648B86FC84D0749D02AF63CEB55F5B44578CF8571D8A5FD0AF3EDFB5A35F9840253B943E2C3B54FC3B4D48E787FC100AEF38975F249664CBGBCB5D4614B89D5737D1E78DC008F868A8995FCE33C784E36A2DF415D600BC0763B85DF685DEA3ED28B72C93F2EB6E482957EDA70F57D5ACBBEAB720D82BC9EA8759E41E739C74D779C71C5G9E83B4CE66AB8CD373DDF3987982819F9E2670C5FA71D535A1DF463D55065CFB95BE633DEA3EFF935FB040C782455CAB78B2FF23
	66FB241D6C8778E020105F487673623B5C06FCC577C1FB3ECF61735C27667B6E8872C5829E8BA40D90FCCDA354FC49C749AEA384DF0C4CD77CDBB55F7344977ADB419779DB598FFE27663B51CE767B1D609B7EBB41A75D2F66BB52C17E8CF89750507B85DF0C97DF1E837972C70A7CAAC74AFEBAD24D779171C98F00ED018A9F107D6581B55FC3CE328B6031C0C9B2DF516F55FC173810EF906041C0613F97FCC9DEFC9547102F72C168D701829E94FC918F2A79A63B11AFAD9C66C4C0656102EFD0387C1EE0B863
	1372705D7BD5044D6158C278EFE98A3E5F3CBDDDFD2F4148482409FAE7BE99E5BEA4724FFDC87B5EF7C48D4B5788F853C3B89EBCD925FEE72224335359EA632F5D4AFD7B29EA75F9D0011D2DE64FDD07ED6ECECF1543E26D4271E1D0B513153D17AB43F29FFF57CAF646D97B1EB1FB4CF33A71151432137C195064ACF3DA2C15B0441451F1D93650A961DD85212B51DE2E0770BC6C192D6ABB9542272F19B025875B0448B609D259A67A572993051CEF4DDE5C2B1CD52D6C21CF4832F78D4A127A87982FB9B6E9AD
	E245047D248A31AE4226511D8BE1047D1430B1042557A01675B0E2BFA32C1130AF0C088DF99847473D1F33FB9268AE88DBCBC75E71939DF96696F8988725B6EC10B790BA0F1B6DFC6815758A62F9B19BC6BD0C77BEE4B95898AFC5C0DB834A0F40F227B81137B282F9C715B35EG888F858AA35E7BAB98AFBB939FC53C574FE13C390FC07BGEAFA84F9AF4F44FCBD0FA0EF20097106C2B892A816F8CF56A3EFA271461F66797E917285EA7AA372BE530DF6729036738CE22104A59196CF581933084DFF9431CF89
	B391F657B9448289EB14900BA4ACE5BCE2C904DDC1D8C1A4E253A7A2E6A72CB285316104BD1902760D0D24FBC8CA189D0A9F2BB6B482399EC35E39250C17351F410FA16F3BEE7E8E9904E3009208B7FCB1661B7AA7649D1649F86D909E84947CA7643DEAAE564588621D5D48F847C2B87971EA437C4711F77CDE464B4E5297BC0E3C3F5A48F807C2B88CA86AF174650F66E522AF2B0EDB038B73391F48CF2A413263A83F2A5DAC3F4CA870D920CAA0F1F7C2EDD31A5CBFB5B3AC8A1F196C3354F37E51000F831A7F
	C435C1DEF7EFE27BF0FDDFBD433E70150A4D707742CEA06218F0734CE28967C257CBC567E6CE503BF3E570748593721F907DDD6393CA3F171A7471C4E363E479BE190C83E80F6140937B845AA3740C569E99CF42D87BA45A6374018B3347E71B0B54766847F9C7A1FF8C49EF845986BD0972273955720783BE96A87DA914FF4BC1B66E7671B421ACFBED83FB27F5408E92CAF82F47AF8F22AE59A0E753D328CBD31B1EAEC1CF21CF6FBC0469C2G0B23F4774D5556E166A8187F0CC29B7C6E50055561AF2AB7296B
	30BFC794C977194D28C3AD48B7F8946A30FD37DE3B0C98057D032B28DCAD43851CBD94F2BCCEF2C4C399E9CF239C9DF5FAF214BF0DF2AC34EE7211233F36B8EDC6999AA17FA1A4C36EDE2D8CD190B716E458334D2B0F695709C114E1D58B4AA0BD836D7E996A97CA75E4F0BD03B22CEE74EA577DB88BA84AFF1D4A9F84F907D379AFB9754ACFFC86FD3A3A7542FCFA443CE2357C7DBCA7A8F4383D95F5C8FD961E331E25B5F3131EAFF5BD0BBACC33DC188EBBEC95DEF5F081478749A70FDAD017C010B312F4B934
	D95BB731F1A209F4F97E82F5F925EB1BDEDF5B3FC305685BD654A5FDB44C8DC663BA5A4186755AD3G60A300E200460EC6FD2E356E10785A53G1EBFC4DD4B26313C87766CA1565F4724F372F3B086BC0775F7E0125697BD90B774B95457FC78426AEFED79D65FF9CFBFCF4B21AEFFB80C3A04021C7124CB7BE9BDDD66BF0FFDD33D43EB2C38003306C28EF79B4A118FE534BF0FF2BC753D1E9C43C80E6EE3DEB639209304E813E56DA84B88A8E7AC49F2EB0BDE7B4CFE816B77A76D97D63FFFDAD853D37B6C5F21
	C2D91F37C91FC2105575826AF33FCF5F9982F123C81FDD368B53E74BB7BAF5534F1306D8B73F3621AEF1A0E7E6B46AE2582427CBEDB47A49370B4A3C66D77DBEC4A84B51F2846530C0994151586F056DD177FB5151225FCBFF917C64C5346B93C7D85FB430077B50668FEFEB5064BDF0877B308E3EB90A3A9701CE9E52AB68687E736BF521877CE49D9FDAFA707CBA8E4059BE54759352B594740CA7DDDB6C756757357F877CA42C4B53250D67577302CE77897D226C285F7CBFD79BCCFFC67DD6F56CBC3FFE7DB9
	60A76B55BDE37779756A6759BE2C332BBBE8FE857AC47E997B087F6A567691C9901775975479630E8B6B6FFEDEE4741DABDE50D9BF214F9E876AD3G325AC95EBFB8367B515E7A67FBA5DBEB7DE8E37D733945CEDAA781DDE27F0275F3096EF3E1C68C6A7BA36705554F4872E2F57D74739C2050618D52A18764EB0EC19DBE50FD469ECABAECF7DD188EDFEA1E897BFECED048FF270B7AB510AD1E645F227BDCBC7F2FA87F0CE397A67F013DDE4F557DBAC5086D5CF10C660CA0DF7BDFD107F33ABA8CA39DDE3CC0
	9DFED36165C77DBCE3A8F4D8632639A648B716F488F26B2DB7E53F04BADC673EB09D62CC25EA9D7AFD7A106EF7DE235EDB0B08DB2DFB1F8A234217447A2971A56D5E5A706DEC6D399D706E17F0EFEDCE8D3BC7867756F6C3B7E2F634717768C57CE3B54C9EC72D27647093B52CDCFEBEC242BB0C6EDD5D177B10073C04775A3E36035541F088C7G45GA5G253F0C32559AFB3D5F39226BBC77BB9FDC53973972DF467B37262C6DCB3AF2CA77732A3E24EB24F4B37BF41F341D528575A9DDG251BD55317F4C114
	6E5ABE258B21F4F30CFDC997CE692E73CAB77AE5795EF278BB8EA8AD966696315EF758093B280378F8DE90AB6E229ECF788D9CAFD661727328836F29B7C2DCD8AC161F6ED4171F710AA8AB877E96815582D9DE51AB3F38026557750A367CAA0E8FFAC5DB7E7365D87E9008CBFC854B8F2F505E395778AA4CCF0082DE154F003D2AFF07DF08868F576193B2CD63FBE423DF554ABA0763312FEAE59DB687E59D83F1467FC6D9FF1C2936D558FF0B72A261EF8C50D8A0A9CE4FD6D2BD3FA7B1CEDB7EE40EE745E94B7F
	39894B4F0538A1F1D8FEF73D3A7CC4392C5457E0EC854A87AAFFCD2F7C46EE16DF63EB5A724D9C373C26AD7F6D53D83E8B6222DF4372DF686E0B0F977E8F26B31D69CBBAA3259BFF36AF691AA95D25FDCA6721F4934F75A55DE0CA975627F441146E005417F4E1146E7371FDC997C9692E6AD33A98CA17B131AF6946D23A6DA9FDC9A73D0E691ECD51768999901783D4743A706F5A57757B840B866F5261933EFFB35F0F8FFCDD5BA66E6278505735ED2220845B44F008CB0DC7D93FAA5553518E7181C0C1C0A171
	FAED32E2B14BAFBCDEDB7E8E0EC745EB4B7F400D65C7C3DC615F307CC796EBED958CF1E1C011FF9365467CCD5FD6E3B53874C6CF364A184B7B2FB73432AE60F8569BDAD9EF4B247E8B62063C01328E192B35D5A244253E897D97D0BED0791BFA367AB1BF37507826367C2BB9EEF9D3DB7E7CC66A3FA0AE7ACDACFF124F7D2E05E320FF9F63DDE67398DFBBF8G9B2C4203655FE1AACC4874C591ABBBF20CD656AFB9BEFA0CD656619BD156D808ABFE8BE5CD61731061909E89B46AAD0DDDB4614437705B958F71F3
	9629EFEB3F017138B8FF7136769B98BBF763B7B0F2A0AE70EDAC3F21C9DBD789EFFB1719760EF7D8243378247BCA934E5724934F92D1A9ECBE2CF32E841FAE647348498754EB231510179DE8885070F770BC4BAD33701CCC78BBB88FDFDF4B4E81E60D05BE84488464828A828A830A9E0B73719B0DFAE7643C6763BD9D10D36ED6963618F720FFB64C71C92735E162D86FF442B637B56859A66DDD18C33E0B36F97D8C1E4DA9FC976B7A55FE4AB936D7B9DD6F6A5575235317685735F657C2564B5A2D550B6D198C
	063C03C92FB2B71DF7A23DEEBA58BF3D6E71C92755EB2CC6AFA14355EDA8C3725FD1067AC37D132155A71DD606223F6B79C04CD9BD9FB081EF7B5F51D6EF4F457691C0F24EED6E1F1C4BFD52E9650C7F3B1E2D866D46F3EFC9A443537D146165DEE5A8F8CFCF0661F5D8DF4677D006CB4C7D136127BE6934B28CFACF2F3ECE2F542B2FE0608DFB8F6B2BF5AF1D83A5B9573574CF4EB21FF4DAB92584BDDB3DDE0AB2E4A6208CEF75D306FF75AAC32D2E8CB3DCA8C3BB49F0F7EB7FE4F850A71DD606E13AB2245399
	5711A443B1CB7FE4B86713CEABC3022E8C57EEC699D25FA7FF3076539FFC52E9E5A8FEDF2F8F5FBD5B1F6F5B71F393927A7A273B553E5E783E387F3F9B7E8EF99FFD7D5A86BCCF9B72BE1E9D7DF7831B2344C1F89CD07ABFD0E769F07FF41E6613CE2BF34EBF745A6A1AC95A364A76FE0A0157748F14BFE89276EBF612B3321FF2BE5F2B1C437F21679FB71EC69942C9062E367E49F005CFBA1F39082E8C93C806348FD0065C767E493041A71DD606728F74E408ED41F652C4B2A474D306CFFB15A16883BD1F291F
	25573F0782EF448768B3FFB7212DE2C84E91367E4979C71FF4DAB9539375EC35F9934A101308B2349F691F8CA7FC52E9E5E8CE546BD3BEB4323B7BFB68D372BCCE273D456C06A938EAC311649F33A7D54E27AB115EE9604F01E1900E820AA33D569E6D1FDEA5BE6934FA0DCB54CBB75E5E3F72267B2473294B8F7552BD534F72E2FBADAFC7373C6BBA7AD75E6DBE69FC461E8F755A5413963DB655883C768F31CDBD560DEDFF5007345E66681F1CC7FD52E9658C5735CB0E33FF65FD6B13CEDBDE7407FAEDF8A44D
	E747124EE37BA943473D4A107DCFBD99D654E1BFD274CFDA9FF575CF06BF7824534A60511561844D1783C9864F317E49F029CFBA2D8C113AB27C0EE408A5998AFD3EFB6B1F8CDBFD52E9E54878171E8CCF3971B9AF77DF388EF2F2974306C0F8583FF04DA34948F8F0FF3456E9F330FBDFC45C9D5B593B62CE372D4D66C06C579CEBF53A7947B544B71D22787B00E11067A84A772D9DBDFF8BA4E1077E37C07E39C37FDBA0C9BB4E7F9D2B641DBDFDABC448F7FC8D4A9787A43045EB91CBA46C0EAA44D247A13622
	9A3179045DC5D8BEE139B5081593F6B7E10D046599913393964631G888FA1AC2D09E143A1BC0230E39C0B00F08CE1330A98B69642499FA176BC47F2A0DC7C917A5F75D00368D33728FE67BA7A735DABFFC914D1F1D14B90BAAF676FC01AC0DF8B59615E71E843AE423E1800B69CC458DB4DE84321045DE6C69B06138D37D6A8F65B297A9DD3FE71ED787EC5ADC3687CAAFFB7F4B4689BCFF6D83E95EDB80E3041DB5106499FA3D6B08D6D15C9D8C39D7AF5AEE1CF4DC6DB97FFCCFBCC190AAD9728FE5FB667625B
	75610BDA06507926CC3403056CB042C53EC9582CE3641B04951DA65FA46C2353641BE443C9750A5D262BFE8FB5DDFC9B5EFBD14B90BADFDD0F6DFBB4596105CC34E13C6207F351068904251A280F7C843147CCE8431CCF5006873B953B3529FEA71C3E78B61C5CFD71ED78F6B70EBDC5E40723E7902BA5AC73ACE296422EA32C0B30F967908BA46CF14206916611909BC158227108C591F6B3E131042D1C08D882E1169444121390FBBF857B2ECC4256717518DC889791F67DA4740FCA42727620AFB491B66E8C36
	D997E1070A510F86A5214F0414A8FEF22F6A7706120B6FB3F597358C590625E8439032433525E8439104ED21FEBD0A302459E85731E42F1A450A0DCC2A5F9F39AF3E3D3E683C7876FAFDB17A2174EF9AC317208DB3889B3C84ED18C55810F3E82F8242EA76209FD692765C9C34F5533F512E7366AA36DC227A9D1AF9716D7A58C5ADC368FC7BDC34EBG59611D39E857A0420C73502E43885B3F896DB502304C4DE86B98326110C645EE5729FEAFEE3C78B65CF8D14B90BA6770675231202F74A9BDD3F0AC834259
	047D19E37990B69136E29F7AE13B02EDE0D8B70403886B5E00368EA16C3B0D8C9B8561311F222DB30B947BAED27D3EF3634537752397358C2173FC7E6CA7FD86F67D0C46C10E95C2381630F97C592F99429E42820CE843G42EE62E71B07FFA6563BE4BDF6333B5DC6FEC6FB28BC4C5E9713771FF77726A7CBFBE136F474DA063F5FC8562385BC7D933359DA024B5C59E995D75F3377276538B7BD6C5B13BE5F755CE16ED03E1576132649B2CF0547D9466EDC97EB4E136730EF4A76F06EC4AA17EA406E4FEE4775
	469830D582D0A66039C0EC7FCA2C35949D20B5A740457A73398CFE96884231C009C072FCE11656DEE963EB8CD9C085C0ACDCA97F55D2130C3BFA08E7A456C3CAEAFBDEFB292C55DFFB59D14B5601BA1D475D2DD66D7ACAFDEDA645A9A7DE58A18DE6E641F3102777185DEDD61F7599ABCFAF62FACA6F286DE98DC7FE177440672ABADC1D0F6BDC2FB650DA6FF8347D7B7CECEBB8042301460EC75B0F71B90B1ABC21461089148BD48CE41A60EDDBD1762C03EA7FE1770D0B32EF6073E916F77B84AC7BD67EBE74B0
	880F849ABD814B2E74D9479B83F1C9C06993EB8C59C00593754A0EB9682577EAFA2E73591BB3C27AE6A08F50E020E05D7C828F29F5D9C33A241FC3BB06CDC4DDDE616F470C0130148CF6CAC6DD5E1B2B7561FC08AB87EACC96E55A1375FDB4C04603FA08D7642CEDD64B792D04FA7FE837D66F9048AB9CE8341CE7FC321E5E49E6AFBB2E417C6E285366B78E522725C01B84AAG2ACC514BEF24D9AD5FFA3263FDABE95FB1856D7896BFBF9A8461D020489434E37BDEED59319017G14BC896C8D14BBC92FECFB0B
	17AEEBD117B1BE67D10BA13D89289D289BE808EEFED92DDE79FD0B794D76D9039F8E69C7820D820A83CA544DAF5EBB3FC24CEFD63DB63FD4C30DE1BED0BED0B9D023C1AF3F900BD7FE5F513745B7EB7333C37AG20A0209020F05D7C6CD6F5FEEB72F1BC78606B9E6E3762C799C47DFECBFB9C238DD83F3773337C292960B3C00529D83F3FECB85F99685AD45FB6A04EEB9E5B434F36C23C77B9526FB96ED261629C6744BDF80E349B62620064EFA19ED67B68EA7251A3F4CE21FCB26AB0BC0565518D61A0205049
	2843F51334BAC4C2DC8C505849C2FE69F3BD9BFFF2582B4E56E31D8DBF2D4DAF835267G954979546A66F7CF1BD71DD1BFE870494F82693B008265FC066966575A2E368DFB77CC64771A4F79249110BE8AA896A881A8F90ADEFE4BEDDE7A96202DFFB20B4ECECDC1DB6F606701EBA15C4E30A9E86B8FCC5A3207C2DCA8D004DCE634EE596FF8153D067A0332CDBEE38E24CF82CACF9379E4276965F763912F7C8AB03FF777307C8A53747BEE238EAE3F033317695B1C267567B99C3727E97DF9981D616E023858B4
	7467A3C73D647A163E45E47742575337F3BB3CF07AA66BE8AFFCAD4DA5A6D8312E0A27E2DD1D666D228B42C300C226E2DDBD6973BECC8444C5830D81CA82CA1F26E75BA00717CC05089FF22A7D31007C71A11F71A99B72AD84B282B583F9F44BD962D267371672DB65135FE0C89F8C94A66793291B5F1BAEAF7FDE07791D769943E2A07DD8A0E9FA0DA183A8E73ADEFEF79D7332830D478F7A64D78469EB01ACC0DDC0013A79B95DDEF5CA7D43E81FF525E110FE84D0141CCF6CF4FDFFCE107164745E665B5D3B38
	2FD2B80007CFC9B82ED8CB982F187FD6B8AC72A62B38E7763F795E693F0131A29931C03EDFF989E0D70FC76CBB3EDFF9BFE033A6A0964177AB57CD12A49B7115703D49BD40F3AB6577C70E9D876CEF9391AB634F22FB81FB1330C80E5DBADEBCF7892C02BF0B7E77841127307563C6761CF9586970185259B352AB1CA793FEE727E3DBDD5244F8BC7020B194B0C95A4157D3C45C60A6D6D7AD665623EDEE67F107C512C4BE43787EE6AC7C9E83A4ECF76FC9569E8F3B4D9DB09457392C2D12E586634DAD42673274
	99F2DFA67FAD1F01B23CD5446F48F53A2D5E650C676BC12140B712F8B7BB3DF5B2717D44F808CFG92BADC3B1769A062B772FD442CAFE00E86A4E4124CA813698BA1CB3B7CF7B07C95691E657B0AA1909E7185167DA15FF76BF0D8BB1C8EDB2B60CB63FBEC6C1BD9F1DFE0798F6CD34A3F1747A7C0DC9265FD006F41E54F047954CC444E7179CA93045BE7221C7FB39B659CBAD34897AA7F9DC5691E64FBABE3A1BCEEA6167F7EFE257C4DFC2FAAA78366489918EF5DE94C37A9C3FED6157F8E4940FC970BBD8888
	0793964E77F646C0B8B1834BDAF4C0A96BGDF674B1E85FA4D4232961A302C46D972B3077CB7F09666AB4E5605C2F8A410706771FCDD7FDFC09B4947AF6558C3605F5F90360A6FF75C83CE380E5AD198476E866CA3EAB32B79F353B54053C6FC2FF26C96885FC458D5FC8FE49324FD1630C23E5FB187723AA1853187B876A0709CA32C146F013C8B6D7CBDEA7B2779FE474B00BDB4916DB40F4F6B1EE2FEBB8BFD4BB8493B9D169FD46C7AA867CF84DEE9B65AEC0F588B01F081E10B385E4D90364FC67B1FA8C67B
	874F96F68F137F0E1E0DF5F87BA1252C07791EC18244A5D19E8D640759F3C45AC279EF639CACFBA9DF6B6E0270A04252785A79DB2C4CB9D856134D2A36A15656A1EE9425F90057C9A20425CC44BCDC57DC8897E622CCE36620CC6D19C216EE79EFB0259BBD974B8F4F4472A734A8659F67EB5023A0AE0672F5EF42FC2539A23F8C79EF41DC4C37042FC91BA05CBC976BEF4BDE5F3E2101E3AFB3DFA73E41FB3D6BF9E92B525F3E3BF7B653476936FD8F7D1B59EE822EDAE727CDFCBCC964194A57F7D91F9BCC7254
	F1AC8A4251D4CE498656271A6D36B6873B0D3C91536F656B3E49736039F59E6AFC75865439F21E5035C97E5BBD8F732B5DB853CB36924CD94E7748C6120B522C2DE1EBFE966BE173F13B274C6E6C346EF4D95DE679DBD0A25D8EBE1631F1B80A52DDD54366C4B618A4D4F5185B587AE07A170C77169AE63B64AF11EF3511156731F978BD91421E87AD4C1EC22FDF50F710CC10269DE8B0D088D09465B1122F57721544D233DBE0AB4B599A1738AD390C1DEEE547199C3C8CBE6F3DFB874EFB47C0BED973F18C3C74
	3BF9D20F6B147CA02C183FFE3883ED55B89F672FCB799D4BC3A09C829481A46E5B68DA067735075EF63DE48C2819B0D5723DEFA34E34E6C24CFCD1E7C972DFF65FC66DBD299365F331DE6F0195AE00F9A9D0B310E701BC76AD701EBF496FC44EE2F285AF90F37F1904FF4371D1AADC5866F23E1E4A42C990B7742B9AF9DD7CC97EFCD5303046D0399031E9FC5F318942F6421C9C6B02709042BE6D60EB6A908EA76CDB1E5FE8884791564E4711C48827E5617C7FF52FE77B790067831583B5E64963D1161E5E56B3
	CC0F00AC215FE4425D9C8FD661C26FFF7135CC969E89F1739721CC1F097DE9888791D6467B6990880FA4EC78D914BD1230FF1FC5DBC49316D70F36983388F53CD9771D7FA408CFDFAC4F39966B69B8175F45D838D86812C2F8B6470DAADC68F8975DA1598CF1110BD126C3EE54B1E789E23738D147A24212BBD147CA4202B6210E0D04A5ECC29D6D045D5306E5849076C19B1691C418299D4B98C6585D6DD8C698E17366E19911CB500E333C6E880881FCAC10148D4FC75942FEB959FAF65C455F7BAE4A565A3109
	633559DABB3ECE7799D9A0AEAA9BE51A41E54ADDDAE3A8DE0AD8BD47CC903690F678946A63A26C0F27D01FC104AD7C9E5B48D042E6EEC17B0492B6ECB7169BCE581D75D86EE84226B4E03931CAFE39D8EE82E191F518DF72B22AFB936617C918550479E5937690DF17AD04F0ADE133290C66E5D8D7A3D72A6B4A8378E020E02030E5727371B23D3AB217B25B47AC93F5B215F0974713D438282B77781AB08BE7AD07E75A65A853393D2863B8429E5D0BBA2665A0163D8F6D1DC95829FD28F78EE177C828E391E11F
	15E2993504151511BF90B63C8C4BF091766FB2ACA300305019D8C690E15F38300C90420E39300CF0421EF7E199D1042DBC06E54490B6EFBC16B116305CFAACC37A9A316364DF99043DD40F79E59166A0FFA8A06C6915D8C6B9E13F61F332C6885B897B04E381909EC2D8B379FFB0E18FEC41F2C389ABD805E5C4FC0DBE3751EBCDA69A70B1C0C9DF8BBFCBDF51533A5A177C5D386C955A7EE199478BD7E87B073B6B31FFB0C2DC588A521137BF365F1C3192E5BA39877E48EBA8812A5F1377DC7C7D79457F8BE5B8
	FD56350BC1F793D07BCAF90E3AD22F1DE7CE627329154236864233B99E224265FB72787AA18BC7C0DCF6AE5ABC0CBFFB8D06F0B0E19FF1AC8C4291049DB4208F0DA26C0DBA7421B8429EBC0D79A5927659E94CAF759B7253EE4CAF0330A24AAF07302FA83FA242DAA83FDA429EBD0D79B59136309B73F391B60E3F0FB78842C389ABE9654F67908EA76C36D66A8F887B2C157A834276DB28BF7886FD76673354F5AA65010EC0B9C0C5F9725EC21EDE1D7E112F47DB7234F57A8C473B7334F5EAAA42BA8D0238F1F9
	A853673C1FE9DA85FA2FC2EC874786C1B80830FB28BF9AC618988319BEA3885B3789730BA26CD71BB03FD842BE23394E98429E1B0B79A591165C483074FC30C9BEE2BB9B316D9765239DB376AA6DFD016A77ED7F8B67209E7EDFA84360755EF3BB685D8DB484E8F83E700F1179FAFE729EEF4B2372357D6D479C0F4B5776371724E0FF1B88F10D2B51662F7176918E615104096F9F45C3B8113091E730ADC8EB901BCAF377F442ECE7312E33890B66ED3A9042C6429670F9D1B304DD04DDCAF325EE42BEDE01E363
	90426AE8EC9CC65856BAFC769AC1580F6B7059AB0A30F76A68F90330EDA6FA5EA06C67FC1E1384617435083D4C31EC889790D6CCF31FE2422E2339CFADE10F505C47C25873BC3FAE88879276A5CFBB8C42E10485D09961047D154A98C5583A7D549793768F7E0C128861548244EE4DE258FC8867927679A24C2F10B0698C66E7A46C6BFA1CEFB793360D37EF8F04838BE84D7488EB2BE22DC1EAE207EB9D5613D9F47DA67B2ECF4EE20B4DECF736AD4E4D4A4F4DDA3D66EB3CFFF358C9757D1B051F2F6F717E4D60
	82F93DCF7E9BF96592B90FE7772B73C83FFE510471BD64B1C6CE1BD8202CA9889D6A6C0A8ED30A583DBDA4FF5D29CE765582275D694EEB85DD5CCEF9CFFEF7912E75642C839B2DE3EBB481C56A7BCF593A8B3B1BB80F79BF0F7F0DD7BC53754DE491AFF4F9EAA92E0FA4BD95A259FF7B1D6E7A882310F5725A397C3731E10954FE596409C21FCC07224FC4C7D6AF7A18E87DAE5A2D243BF53C3FF5A952EE70A8E97F3B59BF9BAEB3238D133FC59B1A4CDA9B5EB4D1ED43AB4CDA9B7ED5362110E358F1C50EFB4A7D
	51DD5EBB5A4A7EAEEFD9AE67B36924124F0AE93D69A352583B14B4B5F57E5BBD71FBA55D43337D353B30E16A9C34E19159705BB9DA9B5E6FE54376B9DA9B221F8AF97EF4C611A74455B7BF38771C12F64DE97F7C6054E95441C3BA5CFE7A7CBA7C7534D6074B462BF5A89A2F4861B479DF97C3A7AA699E4F6C1B6E1FA6ABE9BF2D73CF778A936A9ECA3AF71A343A2F1F20567DD613D67705A9EA5DAD13D4BA1C763F8D2C1E445B00955B401386A51F4F9A7CEB83791315B46F1E715F6E4327A8696A0A7B5686020B
	510671E443A70B4F5F97A795EBED7811178DD7CDD564D9B75B3F3A6C1E0DF24CDF0FF25CB9DBAB472CE4359C313335F2AC1D2816631AE90A9C235DFD684F16703A74E0DDFEB2DD49670AF37E5525F9061226F80F7FF579C026126E31B9FD2B4BE919E8C3935930A8D3EB43F36355B6F4E4EAEDD8A35725FC0EE42E37AD564FD3645B35493F3A9D3A89658AA4397E34C9ABD70917DC63B6E96532CDD2576D8DDFAAF2FC6417FD85EF47DE2ECF47F2B96D42FD8B653472B6CDDE9D7B3F4C696874189D1E4EBCE345D6
	13DC66C78B14B23F5A60EF5DAC1444BE883301789737C564615A30C4ADF719334365F430AB574BAAF618AAF69FAAB1D6ED5B319DFE4B757B408634E3A459714D8DBE7D1DD7DB1D3BD1EB477BA7296B77120D7E68AFF82DB52BB43CFBEC8E0B732442DC3E73500E1D264A2A9D5B8E6D5CF1284C38332EC2CEBB4748B497F335CDD6753CB37F74788993A57D39DA52FAB1C7CBABD4662E42968F99E96CAEC4DB3CE3D46B4A72FC4D2B6FDF30DDEB2BD3DE365A69954FE46D746AEFAE592E3565C6B93DF8CE3DEF4779
	4E0F767CFC7BC8BF53FD5CE3BA0FEC2F4105FA4F453F65FB0C41056279F78661FF62F8148A974F45ABB94E4209909770DD0DFCBF4542052C33CA5BD0E34881AA86EA846A828A8292E72BEE3564CB3D5C51E9F23A0C5693D63B6A2A88714C54485F5388013CC6EE406728A599788CBB0AB0FB86BE43469176DC86BE2B0DA56C1FF6FCD613B6625A4922D95970B7834279C0A6A0BB509020D020C820F820A462DF4B7963EE2BB6986F2EB6E496013EF7D69B243B2A8D05701BFDD33D917E32EF268B7E2BB206E8FDFA
	23D2A779F2F999B2B6E4236F5AB123789EF0B5C54ABC21329E3D7B4FEFFCE41B29716F491272BE50149E721A61073FCE177FCE154BBE9F2F68AFB6707A73BEC771815F6BFD1C55EB916E5BDF1661FD0E6239AC0972485929FFEEEDA81FEB8C06BC02A81F287D7AE74F16BB785A8F70C5916F76D95EE74FDA39DC0990AFEDC21EF3A6E6AFF3EB2B554E0EBAB85D725905DF18D85A7CCD34F65300BEBE14302BB2F10FB3143068CC5C630CA0EC57DCDA5FA06C3FE81DA6EE937A6C51B369CCBE8827EF2EB1648255EE
	4638AE9E17F03B78AEEE662F2B8DA36FC55F7DF73F5EF1E765E8B1AC13156F6D03334F74278C3DF7548C323F267878524D193E3E144357605819202E4DD8E76FE4FA7BD262A266CB225E1EBE4B6286EF4633B64B7879248488A79136072F7BE4EF013EE48B6DA9713543A6885B891BF7866B2D0B304334DF9638856BE6A15F579E8661A842D6F02C7B0EEAC3D88856533D07AFE48D1665BDCDAE27673E62498BAA836B2427F78B057D8673F5383B19DD37609A591CA5CA9DFC53499F920B515E8F71F9E5BB04BD04
	4D65FBFD41908EA1AC065B6CE7C05145186F06ECA55FB36DAC5FF8627FB3BFA7E0AC01712C8431B23E7F9EG6140925A4FBC076BFCA3896B20FD54D1045DCB7B28F104FDC1FB26E389F39C237129946BFB8F37C986048B88EB61D8F6F835E1449DF43F6BB1F57DB0FE7CDDD05A7B58519B09B2A6F81D4DD657E54A85662F345532FA5F363A015B7EB2205AD2EC2B01755EED75D2F645296CC7BFBD05FE51DC0A75774EA9740B4104D572F391A3A09C81A4FC21E88B4BB701F81E60E7A2DE81EAACC35F39F519D2D6
	816F3F5BA14ED306E90A78B905E08807907696DFD3FF0179A0659335DC49C7745B719017D006BADE3ED2FF8CF9E3A513B1339C735E424B8B00F0E0B97A5233ABE96EC458C4FEEF52A8884791E6EC467EA91130FB1B51B7258A5AA7D80C3E19D101FE58D64A66FFB990B682590106G05GC5924F894E139ADAED988CB48866C97190977C7BEAC37303683B9F16B42B7CA0D24E2BB7FF995283CF0886C779C4D969256A327C190FB01A5C832E1D93CD145B022817903EF771D720642DD8676F6430BA977538FD9F2B
	470C2DD80FB7737D8ABB043B880B614F0D43A09CCAD88EBF5371A320984AF75A8AA55F274BD83EE308FFD9B9632F5DD6E3E85A06580B7C8C48A0888FA12C0177D39FG05EF43FCD72FD4727D4445729DC57CA65EDF95D4425C3F127AC9BE0E35C358C358BE5E26DFE3FAD4E23E7C55A959CE93F99B8A033870CAEC07B7F09F0F27BC7E497D59D885FDF295360FA7B67B7695F19C3B0E69C6FCDFEC716EABCEFE236874A39E97D405657C8FBF8312846154EABA274F4F0064C338289A5B43EE5E1E5BA19CC0183823
	BC8842A104AD613A0F02F0BCE1624C8B5B439DD7CD4FA15C8673EB201F2FC1AC17E371B0A70B3D8F5B4AFD755AF11825D1078BEA7A5F5F0BB2C67914219D37AF243FEF2E617DEAC356F9F80C328E42DED7CC42FEAB2486ED7864A46C374289DBC9E7AF2288FB68A076DB31E4572F78590F8488E798EB8C05C61ADBF1BC661EEA437030EAC3AE4D034378B7EE92AE402E2C9CF53850281D87BF414B38103B9734F5226E5764756CC93E6D65D50EDD49ECE9447612EE70EEAFFF48E36DC570DF49EDEC875EAEA3360F
	69FC6C9A8661D04262793E44D54C1EC6EC732F2DD27279911F970F0138C4CAE3656DE77E769AC34EF6443671FC5EE56936E3BE492B15FC461E46315EC2E9AA3BF12C8F5C0EBACDEF7555FD994758BBBBA3092F3155DB77656C15FB392FF81647C5D1B9B996ACBBFF87E203ACD8F6638E7441BBE661D8BC083031E25FFD9568C458BAEEC7F6D6A414306BE93FFFA4E11FACC01F9E3583FDB70A1FF1090330343346103D9371E80E1B207F987C1BEAC391617B0A746673AC1DBA4C7273577F241DDA1F6E54ADC3BB5E
	764567FD1F6DA61AFC6BF5AD6FDFEFE5765DC96F55183C6B35FA2D62C3B175D80F761DD80FE2DE476AB108304BB661BCB4E2A77AE2C70112CF8E9FFBC6C3DCAC2579BBFFC6CF2E055F35A84B170DECBEF1586EB4FB58872BA97D5AC6DA5384DE51CF9EEE4CBA0FBDEAEB557D64D7D674B3CBAD0DBFED58F7F691565606FDE7A0E1D1ED68E7C38913684CD6E8AD7A4D077CBD2688884792A671BE4473DC35216BFE6CB32F747AAED8EC2DFFFE9461A71FA8E318D7997E0EC97E7A1B282BF15D3E7EB51DBF3F3F8E14
	3A8B6B747099EF7F9A422E2D117B0DD7F8DC66AE7409EF4F223F8590767333745CCAF90AF3B85EE5CF607E7E9ED0B47195E8FC7B3EEF951F3CCBBCD7GEF42AE1CDBAD914F8F3B316CEEBE378A00F0A0E1CEDE767BC0A3F7232F7FF93D12EF0689F50822B4AE936A10309BE53BC367FD28083D78CCB53F8E797E21F99F6A23C245FEB6BECF4E2D43F27E486749C1909EC6585BFC1E9C8561E84A73AD1D32A51E76E92E88722D5967DDF64186256CBBF8DC3A894B19309F756EA26C60FE54FB9865B93953376C2CCE
	DCF39AC5FC61D95EE56F592858F8ADBFEF948B3CE3282C2F396F255757986657A3361B77DF15ACDC0F799E1C2B7F6CF8E2AEABEFD0BD2E61A77333B8B190CE2C47B57B43C758243F41FCC4DE2D2F3058A0833EDC8FE02D5B69323ABD27845A403F3CAB727A0C5F8BA67288BD323427BCCADABC556693E67111F013390D5A731E223E6E03087D24C78CF876E7446FEF16C6DE7151F8263B5EFED2C6037C8E1F7CB741340463C27E464D6CD95582F181C043014201C6830DE9C05B8C65776E33AF0B01C2A59E0F3935
	1D3D7D56091AB5582CA795183DGADF4DB69734DA17F7A3829F37A176E2C4FBD9B7E252B763953423FF4B79577AF5DA5337B17EEE6E67F525D60F34706FF697E30212F6904CF1D664F17C964BFC5DF31FD631E5A967BCEDD1D47EDF33451B7C27A742DE2C6E26C1E3193477374BD342EC3E79F337760587DE0D3967CAD04F0ADD0BBD0GD0B0508820D1C07144BB0A730E133F2F1E84F8488B627BEE6C5BBA6CFB65D90D483B3AA9C01627DD4E076507B2566E69F91C8FBECFDCCF942FCA93A07FAEA439CDA48B8B
	333D28292A34671BB7743467F5BED26F07613A916B13C57D67AC6C792E37D58B756FFADB37D07F1E7D0D8BFB3A03CD44177132BA3DBEC50EE5D533E3CF1273A47FACG1E75BA603D2FF5B95FFFAD07BA2DED4431E6975F177B8933F3A3BD5795C3D09E33BE6671C3A0AE1852AC646FEDC6C2F8F4A30ECFB7ECE0325197A34B5CD63347AA4B71623C596A380AAE17534D6F90848D69FC54E4D6B755F5D6500D8DEF85B29E3BF116BAEDAD34A5CF2D5BFA823AE552F3DD72B49F9E0CB31F714EF71D0C7FBBED06BACD
	1D4BFC52E1AAAF23BC472D70CE379A4F71AD1B6A2D0B0D9D768C3A193547C53B5567891240B62CEDCD23787A3A7347BF5EE3FAD176673379D917D6BC3B332DD42238448E7F4EB1BDD62624D9B9AB2B17B44AB9261AEDCA3AA6133FE7E5847F5D267D1277B9139A6BA968961DC72DF9EDD68F85F2147CF803E2EC7C3E493C061AC3E539CA2CG4BCC3B0D0736175455FCAD7B6A92FE1FC076DEF8FE5FCB778E703D429B011A7722FF97D5A97E7DBDFF1EF3C1DCB72539DF6C39C2B8ECAF4D4917F855BB53176C91F8
	147CEF1B5BE6B15A9C56CE0CFB00FF034A676B1AF56D4E13C6E71B603909571DAE0F7C29A579AE0233F8F6CB04DFBFBBGF6B51D4F2E42DEB7D7ABF51FDD6F1F1F259A15B4155D7E781960EDED761EBF2F5C2E54D5991FAB47C21D0C213A7AA9DF1FCE5F877367FDD8D7AF1D7CD24F1E15CA65883E64393AFC8DDA3E9B397D17B7205D3F5FB4GF6EF685D6EBF5B2158700DDE4F6609B47F5A2924A95D6C3F5D5F633E383C92F53C3AFDGF42C6CDD47833B94F953677927634B3B15B46F9D73C7C761AB75FCFF37
	927C44348F7D67DA976EA58F5A077D4327F50A4FEDBC064F07C1146697477079B01CFC6E6115B3557DC325556EB2B9DD36D674218F0EF80DD1A5AD4E63B4C66D1A47EDDF02369FF92AB7BB883E0C6F7D6F3F77B5A8B6B3EC69DB7F3DFB4BG78C24979FCC114F3DBBD370383473718BD0A4C13F37D730D4DFB15B435FE3DF7A0F8A593AF5B06E58F5F2764F35FCA7F6AA3E965G58292A77B6332AC9112D50AF9D055F0E636BC2E340E79349EF67B57B2EEB2C66983B47A8FBBF36095D2A7B20DE62718590D73C9F
	5B44D3FCBF399D421E7D1877471FFB4D5D5823D1392D53E5B71FD26E95DE1A6EB58F5BEEBEEAADF33ACED1FC745AB96A785DA06789AB65A21FF9DD64BDD77B9677C79A3C6624F5D6333B351D5E6971AAF72B4DE159810612F0AEB6F32FF6AE461E1114395843625C319D5BEC6B3E2A8B2F7B728AE30529821A9FBB1FAF6CBE7220D25737CA7E35012F1A15B47FEC743F8DECA963BA15537C22ECG7C393CF7FF9EE4D6643DF926FFBA3E5D2224F948693F0E9DAE2E63A954F146318150F1EF6FBA56DA94F9FF5C6B
	3BC0A24D6F2CCA1A69FB7D57711868C7DD58179534A979D87C6C4B6E9B083E2C36F73BDC5F2E48163C5BFF9D7F35196BE844FA3CF1A046A8E36F72CA2AF7BCD72C722F9E4F9ED5528CEE71DFC7DB1D7EDDDF1770357CE1504F0E203E7641BA762293F40AFCDEA16E4EFF3E8E6F490A873EA4206C269AC3A1907BC601C837215E2B0C3DD806D8032E85DE8BD0D09343826B555F7E54127CDC3D0F2DED283FBFAD6F9B707BD6541F0F1567358DCC6EB2A81AADDC08755F13D6DC7F1DFF57AF25798BBF64FD355E7AEF
	EC13D89BCABD203C47BA0D4629FBA6617AFC51819C475E606F374A67DD785E3791629A8F60B87686BFDF90G614083F4AEE5255798A456B6CC568E9803E80DA3EF3B4FD8C79CE803EF0F680CE51AFCBE946F1D15E15BBD66D0FCA736577768C41A3FB815B4B5FDF8676D13533C6C8AACFB53B1A51FB5FE16FD0B6A3D59DF7BB5BF1577B7C459A730BD4F6A762F1FBA5607E74BCD2D3C0C96AC63EF2D8350E71476BE2F5DE571CF173BAD7E68A27C74F9FE86E1A478E8A4796EBC931EFFC8A4FF7F4623787B23BCBE
	79A07CBE08E9323FE2D8A1044B8F223FAFF4E6287D3444E2A9F5BA0FF6185DC7C57C32AE9DBFE6AC5B01C556F951BA6FB9599AF9DE75FD03F6DE45561C157E23DA0CDD3B310E46777A2E2560BB50603F9FDC3F891F37040D2ED63DA77D60DC7F7C7C2393CA1A6B7DFA27D6562F112D539EB65BBB15FE61B20E6DF03ABD6DD4D75B785E3F8B6A241B6A4A58067DD968C12C5F02AE25FE5F6771619097C5E93E66FB7E8990CE227ACD6E24E7D16CFDE83D6A37AB96C8BEEB352247334853EE25EFB9436DF88C6B6A03
	B383501EF6755E1E8E1D754FA79EBC6BFF3D4C954FFFA751A72EBF2554EF428A7F4A7BEEA066CCFB7C18031CD6E43B338FF326AB6A380EDDD8DF1BCC7E69F5F0257FE57CE5AFBE430B7039FD83E0133A5EEDF2D537E2137B7DFCCEFADF75EEFFCB9F6657F376F19D8F230E8F8F040EDBFB777B157B7D2B2FAB777B2F4B7ACE2E8B3D8F3CC7F5E741BFFB9D03C51A07A4A54DD7757E14AD1FC51E28245BB837EFEBD201F3AB8640662B65E7ED3107B63908750B347FA60EC248F1697C3E921B436681F9EC5F734BEE
	454E2E58979E1F4C62F38E41B88A548E548DB464906E931F5D4474ABE951BBE3D0E1E93362BB37D81EF9F34F7B0A6D1B59FE1A3C2F48D235022CEE9BBBFB5609690FED666F4AB9BD9EE7874B9F71131BD98762F13AB8A85F1F3F194D3B5D3636F60FE01571CB7979B73B75B00785F6EE8B1B2FF74AFB5AE20CA95E09EB242107F05C0923F3B55107F05CC979FEB20EADDBEDF6E57C0BD9C8B81BB793EE4ECEC3DC9EFA64677D6FE87C4209890DDF91D6CA335345AA29D31E0399BE27B18F1EBF087F45F2AA1B2D59
	D2597606A908730716CE29584C4F046FD7BF83D53EA33E871784BA26B75798844F59ADFABCB990DF540C63765FD477D24C49681B6F37E770B63B955B2C78541D089BEB772F4DDE73392A6F4B70274D0A3B10FF66E1796B5C05CCB3BD117FE4551D91677C3EBBC4700F4E647A6DC47D0226A9F98D6AF54C92E9BED65D71906D57B302BC977CC2C957B45E4F3960CCA54D60437E1735E621124E6C775DB2723302D847227D9A4BB17F4606883F76CB04FC7F35C8116FDF077AA6DFE5B3170F6ECFC9DB2264953058BF
	3B3A3315B423CA7C115B77EE86CCBF34EE11BA3DF2B7833B8FC1F5B3833EA37FF19D5E85EE02B69B580CF7013F1177255A7FD9E69BB614BA5D962B5BA896DF44FD8625F417CDE4B35E856EB2713BE8A11CE12EB1648275FC17042DFE093E3CBEF7C908B513A3C755EBA6EC9D24277B324A4D7237B14C5EEBA6E29C3BF9A933F38844C5820D8112DAC0DE20DC20E22046969C537EF8143D7C545359A7E194BE2E394DB607BC5F7853A46F7946CF16ABF5F45FC17F7CE24A57CA1AABD37C77673615CA3A383A3E7973
	EA932E9D083C3E7DC6496B7D857E499D1C2724D9DD643F5C637215F4F35A7AA6F7EB1B373D8F2ED1727AE72BFFF2C72ED5529C744B5E72FCAA3967B9CCFAF26FF31839492CDDD8E16EE1BA6512679FDFA5330153F978F0271566CACB1253892B6A44AFAB88F9EF77F34EBC39493F3BEFE2793C06FFEB33855BF57DD77CBDA388A7G253656485FA57D9DFF770C356F2CD6649D340B718E02F0B05008D6BAF35F4FF31A537A19EEDB1F5309BBC562F99FB62AD57BAD31F7B99E532A7D1658899376237190D7E941F3
	4A379730B9D2A80423004682A5DBEB8C19C079D66CDF9E48DEFB1E7EE5375349BF5F403E64G7D0B3A2B117D626E25BD7B5F08253D7B5FC3CB7567500FAE751DC3C7AD551FC3BF3354F78E7D021C2F1BBE14AB703FAEE5FEA55670371B9D66B6FE16CF5457EB7B356F5E14031D9A016CD6510F87405FA02B6FD94B909DCC3FEE1F60FE9CEE555E2F9A4D7151D66D7D2A7B3F427BD563A12E72B01DC15D2215B59462A200228F8BD946405F24433EF225375598325B943CD07EEDD4E1C226F13C9DB537E97DF07CEE
	7ECD3BB62D9F7EF897FD538E62E25BD056375910AF5B7883556FEE53453F87771A0BDA06702930021E5B407D853D370108827DB67027825FB670D401FE9BF82EC05D8664F757793746583DBF456DF427AA47583DBFA642C21A705CF3B3E137503D329E050F3FEBB598424389CB676F0B307B9C425A313E1F656B5D1190CEA06CA50EE55AEA8C05B6444CDFDDE85D307CD9B9BD577F7797DC0618136A47899B3C424720C6504BEEC33BAC63F78486C0F888E1D7F1AC9842A388F373F74A23A09CC7581D7C3BEC0990
	4EBC025827CEFCCFAD1730E779394AE288B79236085F7BE007F0GE18BF7B1AC904221C750761F72B3E16237E13772FB482E0B5FA66F3828E5083511DC6E536CFD7F08A3389E5028FA476BB99EBF9A62E20F60FA4AA86E7349C76177D1BAF35648468BB37F1AB73B0E4B4C2EB91267F927D56FF6FD475BC191242B2434472731E70F6A5DE60B4DD9FA9CDA370336FB58F287DFA24373B5055F4C5370169D6F047EC0B94CA376F652962A77F63A20AC76CEC1A87C957147E66A45C710CC09DBFC4FA2CD61FE493E35
	95CDFC1B0A3D4F087E0A3FDBA662EEAE563FA36A77BC1FF848A301727951CC7DBB22EE62FE1FE90731480E3C0FB8FB7F06D7ACEF872CCD565A315E167373055D909ECCF93DB1D7FFDF7FD35EE6C6825FA862AD1C47663B9DE6F71B4D01F57B48F70A3E5F6EBA7F773BEAF721CCC914E7D993FFA6A847E702AD9B157C2E73F36D7C56A2A54D8DFE1DB1926916EDD1522D7F4A5FE799514F3D7A8D3B28C5E76DC9470B718E4D3205CCFF3616175301737290DE3FEC2CEA6EC01BACAE66B669C01B8496ABF2367BF9A6
	699FA5CA1A9B1D7E58C45EB7F37AFFE7650EF2250CC77DDEB36FF91DE156BCFF579984FF5BAEFFE495FDE965C2760CB134C39983C361F784D0340C0D69709E9FC53A3BE7EA1FCD1200AF5D816D51A13F8B6470CEA74612C33C2E0D90576C20F98147BC909ECC587B9C8B06F0A8E18DD974DEBEE11EB2FAAF1F30C86EBBF190CEA4ECAAFFC7A65589E322136E57217B8A3389FBA38373ABF4225F1D6032F4DA6D56D60F55A25A6CCFB846620D447770C25F3E714F9CE3F7A9B491DF2A667D53E3EC1BC46E8B4EF19D
	6D405B65447E69862E63B08807D29EAF4F74AD6B9F9CFB87A8067832B5F775DDD72914F5DF862E7F0F2132264F42F35369AE4CE378C21E077B38D5045FDD485B6189EC07F3D46F9E9CA9732F9D1E29D15218E67A5F8EA70BF5756338269A1B615F5AC121DF6F514C613C37B79C0201A474DDA945A3A533DD900FE41047D10F140A5B99G4A3B97404576A5608FFB7A75C8D3584B8956AC7EFA02C7CA61FB999EE9B26D6343EF31DF31A1AB5FA3E5184CAD47A118455E364AE20D5489F3FB37B4552B43F86C26F5EF
	0CFA3521F322B429CA1A4B5E972B333A79B617554292B6D85D1DA04BD6E9320BFDBA398E70AAA939137FC969741C32DB0DD246615D56E34721FC2B2584B750C018D9C6D6522A639E1BFDD52539331DDF103051A8A5BB603957A3FDA1226DE6C75BAA311B36310B6D410CAF615D2F441E59A410F7A617176512C7394CFCF07A52C55571A3A7CA9336CA1F5B1DE64BD6188AB95DD552E4CF3B5B5A596E34DB3ADCDBC4C629A7F198188859FDC96AB3E573D86D1050C73BEE881CBAE66A568950FA5D52AA4E49DCBC0F
	B8ACE56DE6B794E5F543C04337BC32A96E122FBC4B1E5EB627E902B4DED67218F4D61A5A45AA10998C8A1F4E33E4AA67EDE5D7C63C746E93B73FDC14B09612EC15D2CE30CDC7A8B8DD615AF13C23456A3EA2741A85D302622EE2F23918F9BAD94EB3BC522443668E1B9D3CE7D2B96F76911F8C0FF50ECE185DF4C021D99A0DB10A844F3B7A097CEB6E78F89F97DCC96A11960AAA02242B5A2C4E0ED5A5078F036FD186D0DF107D824A1E7164E9F83E687A592C6A97BFEB817FAA17A6F66467737FD7737F57307FD7
	F3E4B5C7D62F01CACB104E7BCF9B5F5545F41E578512E468B5434DE71BBDF9FF38FA302853E92A518EA2475FBA6732741F2EBDBEC1CA5D4A0CE9F7351B2B640AB0CAD3DA7873ACDBDD32FB24F44598BCADF87204D6F073AF953C1561A2364EE5EEE54EBE5982CD1A6740444C5EAFCDBE6CEEEB6185F824897B376E07E2F908C5AFB3CA1FFB5A4577C93B20414FD1322EE5EBDBF204E8A76C5F8C977F472BFD310CFD2142267834A706CE77ED988C1E49A3E7B1F676A3133C0A053E64118B787FD97D2B23EF3A3C64
	6CCDF6FF79FED0BA2E6F7A61493429EFF67FC149DE71FF40E7F67E9F50F16FFF308F1A3BE0C09547A0737272E0FA4666CD7C57CC189DF9EC2DE6FB961B196144ECD6A5BD11FB61A97C45BF98C878CB6AD9721B6CBC23D48E711F134B61F9GCBE273BE710B3D98A5624531CC110C3DA61CE5F436E98424E4134579DC7EBB0DB34B8770E17AA52232F05AA9F8CAAC16ACE5A27A39A8AB2BEE1791F25D818F1B2672B2B69D6DE253D183FBF1CA9443FEE56D60E35694B6E54D926FBF4A535C7F5C2A3F34CB190D7B2B
	0577BCFD201B367CCC7043B3554F863C97131FFBAECA67787F01CF7D24B670F95363237FC94483647FD7337D20BC7837BF40395B037F416366237F8766B5CF7FGFD66057F83F6FF65BF38DD7C4DEF59556B034AAFE5110FD6145802C61523536A690CBB31F6434457EF19B8C1CA2E12A6093D7BAAA919ED40D7C9A9FCBD23CA1A68F13A789A4284B57D67DAB471BF589B12E00AB9C354E81699E651E27755A3CD977B2CD938516A2F067219502F4F293F4BB56E724C9E3FA9EB3AD2BE93FA25F2A6D41C537A2938
	EF9B7008AB71DC5663875861471E6E022C6BB05B6D705CF33444E1298647ACBD45DFAF134F02849DE479F7A20F9CA962FEF91049FDC415667F81D0CB8788B44EAC2AB0CEGG88A881GD0CB818294G94G88G88GA70ACFADB44EAC2AB0CEGG88A881G8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGEACEGGGG
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
	return getMainPanel().getCurrentDisplay().getDisplayNumber();
}


/**
 * Insert the method's description here.
 * Creation date: (3/21/00 2:26:52 PM)
 */
private void getExternalResources() 
{
	noCreationAllowed = ClientSession.getInstance().getRolePropertyValue(
				TDCRole.TDC_EXPRESS, "no_soup" );

	TDCDefines.MAX_ROWS = Integer.parseInt(
				ClientSession.getInstance().getRolePropertyValue( TDCRole.TDC_MAX_ROWS, "500" ) ); 
		
		
	//java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
	//noCreationAllowed = bundle.getString("tdc_express");
	//TDCDefines.MAX_ROWS = Integer.parseInt( bundle.getString("tdc_max_rows") );

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
			ivjJCheckBoxMenuItemShowToolBar.setMnemonic('r');
			ivjJCheckBoxMenuItemShowToolBar.setText("Show ToolBar");
			ivjJCheckBoxMenuItemShowToolBar.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK));
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
 * Return the JMenuAlarms property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getJMenuAlarms() {
	if (ivjJMenuAlarms == null) {
		try {
			ivjJMenuAlarms = new javax.swing.JMenu();
			ivjJMenuAlarms.setName("JMenuAlarms");
			ivjJMenuAlarms.setMnemonic('m');
			ivjJMenuAlarms.setText("Unacked Alarms: 0");
			ivjJMenuAlarms.setBackground(java.awt.SystemColor.control);
			ivjJMenuAlarms.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuAlarms.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJMenuAlarms.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuAlarms.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjJMenuAlarms.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJMenuAlarms.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuAlarms;
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
			ivjJMenuBookmarks.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuBookmarks.setMnemonic('b');
			ivjJMenuBookmarks.setText("Bookmark");
			ivjJMenuBookmarks.setBackground(java.awt.SystemColor.control);
			ivjJMenuBookmarks.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuBookmarks.add(getJMenuItemAddBookmark());
			ivjJMenuBookmarks.add(getJMenuItemRemoveBookMark());
			ivjJMenuBookmarks.add(getJSeparator2());
			// user code begin {1}


         ivjJMenuBookmarks.addMenuListener( new javax.swing.event.MenuListener()
         {
            public void menuCanceled( javax.swing.event.MenuEvent e ) {};
            public void menuDeselected( javax.swing.event.MenuEvent e ) {};

            public void menuSelected( javax.swing.event.MenuEvent e )            
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
            }
         });


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
			ivjJMenuEdit.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuEdit.setMnemonic('e');
			ivjJMenuEdit.setText("Edit");
			ivjJMenuEdit.setBackground(java.awt.SystemColor.control);
			ivjJMenuEdit.setForeground(java.awt.SystemColor.controlText);
			
			ivjJMenuEdit.add(getJMenuItemCreate());
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
			ivjJMenuFile.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuFile.setMnemonic('F');
			ivjJMenuFile.setText("File");
			ivjJMenuFile.setBackground(java.awt.SystemColor.control);
			ivjJMenuFile.setForeground(java.awt.SystemColor.controlText);
			
			ivjJMenuFile.add(getJMenuItemExportDataSet());
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
			ivjJMenuGridLines.setText("Grid Lines");
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
			ivjJMenuItemAbout.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_B, java.awt.Event.CTRL_MASK));
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
			ivjJMenuItemCreate.setMnemonic('n');
			ivjJMenuItemCreate.setText("New...");
			ivjJMenuItemCreate.setBackground(java.awt.SystemColor.control);
			ivjJMenuItemCreate.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuItemCreate.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuItemCreate.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_N, java.awt.Event.CTRL_MASK));
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
			ivjJMenuItemMakeCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_C, java.awt.Event.CTRL_MASK));
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
 * Return the JMenuItemMakeCopy property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemResetCntrlHrs() 
{
	if( jMenuItemResetCntrlHrs == null )
	{
		try {
			jMenuItemResetCntrlHrs = new javax.swing.JMenuItem();
			jMenuItemResetCntrlHrs.setName("jMenuItemResetCntrlHrs");
			jMenuItemResetCntrlHrs.setMnemonic('e');
			jMenuItemResetCntrlHrs.setText("Reset Season Cntrl Hrs");
			jMenuItemResetCntrlHrs.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_E, java.awt.Event.CTRL_MASK));
			jMenuItemResetCntrlHrs.setBackground(java.awt.SystemColor.control);
			jMenuItemResetCntrlHrs.setForeground(java.awt.SystemColor.controlText);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return jMenuItemResetCntrlHrs;
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
			ivjJMenuItemPrint.setText("Print Date Range...");
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
			ivjJMenuItemRemoveDisplays.setAccelerator(javax.swing.KeyStroke.getKeyStroke( java.awt.event.KeyEvent.VK_DELETE, java.awt.Event.CTRL_MASK));
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
			ivjJMenuOptions.setFont(new java.awt.Font("dialog", 0, 12));
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
			ivjJMenuTools.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJMenuTools.setMnemonic('t');
			ivjJMenuTools.setText("Tools");
			ivjJMenuTools.setBackground(java.awt.SystemColor.control);
			ivjJMenuTools.setForeground(java.awt.SystemColor.controlText);
			ivjJMenuTools.add(getJMenuTemplates());
			ivjJMenuTools.add(getJMenuItemExportCreatedDisplay());
			// user code begin {1}
			
			ivjJMenuTools.add( getJMenuItemResetCntrlHrs() );
			
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
protected TDCMainPanel getMainPanel() {
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
		tdcClient = new TDCClient( 
                        getMainPanel(), 
                        getMainPanel().getTableDataModel().getAllPointIDs(), 
                        this );

		tdcClient.addMessageListener( this );
		
		if( getMainPanel() != null )
			getMainPanel().setTdcClient( tdcClient );

		//start trying to connect immediately
		tdcClient.startConnection();
		
		
		//force our out bound TDC connection to connect immediately
		SendData.getInstance();
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
			ivjTDCFrameJMenuBar.add(getJMenuAlarms());
			// user code begin {1}


			ivjTDCFrameJMenuBar.remove( getJMenuAlarms() );			
			ivjTDCFrameJMenuBar.add( javax.swing.Box.createHorizontalGlue() );
			ivjTDCFrameJMenuBar.add( getJMenuAlarms() );

			
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
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION TDCMainFrame() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

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

	AlarmFileWatchDog.getInstance().addActionListener( this );

	getJRadioButtonMenuItemAlarmEvents().addActionListener(
		new com.cannontech.tdc.bookmark.SelectionHandler(this.getMainPanel()) );

	getJRadioButtonCustomDisplays().addActionListener(
		new com.cannontech.tdc.bookmark.SelectionHandler(this.getMainPanel()) );
	
	getJMenuItemResetCntrlHrs().addActionListener(this);
	
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
public void initialize() {
	try {
		// user code begin {1}
		initAccelerators();
		getExternalResources();
		
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

	ticker = new Clock( this );	

	// user code end
}


private void initAccelerators()
{
	/* 
	 * This way to handle accelerators was changed to work with JRE 1.4. The accelerator
	 * event would always get consumed by the component focus was in. This ensures that
	 * accelerator fires the correct event ONLY (that is why true is returned on after
	 * each click). We keep the above accelerators set for display purposes.
	 */
	KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
		new CTIKeyEventDispatcher()
		{
			public boolean handleKeyEvent(KeyEvent e)
			{
				//do the checks of the keystrokes here
				if( e.getKeyCode() == KeyEvent.VK_N && e.isControlDown() )
				{
					getJMenuItemCreate().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_P && e.isControlDown() )
				{
					getJMenuItemExportCreatedDisplay().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_I && e.isControlDown() )
				{
					getJMenuItemPrint().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_W && e.isControlDown() )
				{
					getJMenuItemSpawnTDC().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_D && e.isControlDown() )
				{
					getJMenuItemEditDisplays().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_C && e.isControlDown() )
				{
					getJMenuItemMakeCopy().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_DELETE && e.isControlDown() )
				{
					getJMenuItemRemoveDisplays().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_S && e.isControlDown() )
				{
					getJMenuItemSearch().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_F && e.isControlDown() )
				{
					getJMenuItemFindNext().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_T && e.isControlDown() )
				{
					getJMenuItemFont().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_L && e.isControlDown() )
				{
					getJCheckBoxMenuItemShowLog().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_R && e.isControlDown() )
				{
					getJCheckBoxMenuItemShowToolBar().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_Q && e.isControlDown() )
				{
					getJMenuItemExportDataSet().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_F1 )
				{
					getJMenuItemHelpTopics().doClick();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_B && e.isControlDown() )
				{
					getJMenuItemAbout().doClick();
					return true;
				}
				
				//its this the last handling of the KeyEvent in this KeyboardFocusManager?
				return false;
			}
		});	
}


/**
 * Insert the method's description here.
 * Creation date: (8/7/00 3:21:40 PM)
 * @return boolean
 */
private boolean isUserViewActionPermittable() 
{
	// make sure we are displaying a customized(user created) view
	if( Display.isUserDefinedType(getMainPanel().getCurrentDisplay().getType()) )
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
public void jMenuItemAbout_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	ArrayList list = new ArrayList(16);
	list.add("Max Rows     : " + TDCDefines.MAX_ROWS);
	list.add("Sound File   : " + CtiUtilities.ALARM_AU);
	list.add("Temp File    : " + CtiUtilities.OUTPUT_FILE_NAME);
				
	AboutDialog aboutDialog = new AboutDialog( this, "About TDC", true );

	aboutDialog.setLocationRelativeTo( this );
	aboutDialog.setValue( list );
	aboutDialog.show();
	
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
								
		String previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString();

		setCursor( original );
		createDisplay.setModal(true);
		createDisplay.setSize(680, 650);
		createDisplay.setLocationRelativeTo( this );
		createDisplay.show();

		if( !createDisplay.getDisplayName().equalsIgnoreCase("") )
			previousItem = createDisplay.getDisplayName();
	
		if( !compCanceled(createDisplay) )
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

public void jMenuItemResetCntrlHrs_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int res = JOptionPane.showConfirmDialog(
		this,
		"Are you sure you want to reset all the Seasonal Control Hours in the system?",
		"Reset Season Control Hours",
		JOptionPane.OK_CANCEL_OPTION );

	if( res == JOptionPane.OK_OPTION )
	{
		// build up our opArgList for our command	message
		Vector data = new Vector(1);
		data.addElement( new Integer(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN) );  // this is the ClientRegistrationToken	

		// create our command message
		Command cmd = new Command();
		cmd.setOperation( Command.RESET_CNTRL_HOURS );
		cmd.setOpArgList( data );
		cmd.setTimeStamp( new Date() );

		// write the command message to the server
		SendData.getInstance().sendCommandMsg( cmd );		
	}
	
}

/**
 * Comment
 */
public void jMenuItemCreateTemplate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if ( !checkDataBaseConnection() )
		return;

	ColumnEditorDialog editor = 
			new ColumnEditorDialog( 
				this, "Create Template",
				ColumnEditorDialog.DISPLAY_NAME_ONLY,
				getCurrentDisplayNumber() );
	
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
		String previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString();

		EditDisplayDialog display = 
					new EditDisplayDialog( this, previousItem );

		setCursor( original );
		display.setModal(true);
		display.setSize(680, 650);
		display.setLocationRelativeTo( this );	
		display.show();

		if( !compCanceled(display) && originalDisplayNumber >= (Display.BEGINNING_USER_DISPLAY_NUMBER-1) )
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
	ColumnEditorDialog editor = 
			new ColumnEditorDialog(
					owner, "Edit Template",
					ColumnEditorDialog.DISPLAY_COMBO_ONLY,
					getCurrentDisplayNumber() );
	
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
			getCurrentDisplayNumber(),
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

	if ( getCurrentDisplayNumber() <= 0 )
		return;
	
	Object previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString();
		
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
	catch( ClassCastException e )
	{
		//should not get here ever!!!!
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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

		if( display.isDisplayable() ) //was OK pressed?
		{
			getMainPanel().setTableFont( display.getSelectedFont() );

			if( getMainPanel().isClientDisplay() )
				getMainPanel().getCurrentSpecailChild().setTableFont( display.getSelectedFont() );
		}
		
		
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
		String previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString();
		
		EditDisplayDialog display = 
				new EditDisplayDialog( this, previousItem.toString() );

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
	//print the gui component?? Nah, dont change this now!!
	//new JTablePrinter( getMainPanel().getDisplayTable() ).printIt();

	if( getMainPanel().getJComboCurrentDisplay().getItemCount() <= 0 ||
		getMainPanel().getDisplayTable().getColumnCount() <= 0 )
		return;
		
	// printing does not work on JDK 1.2
	if( System.getProperty("java.version").equalsIgnoreCase("1.2") || 
		 System.getProperty("java.version").equalsIgnoreCase("1.2.0") ||
		 System.getProperty("java.version").equalsIgnoreCase("1.2.1") )
		 
	{
		closeBox.showMessageDialog(this, "Printing is not supported for JDK 1.2",
										"Message Box", closeBox.WARNING_MESSAGE);
		return;
 	}
	 
	 
	
	DateTimeUserQuery dq = new DateTimeUserQuery( 
					this, 
					this );
				 
	if( !dq.queryUser(getMainPanel().getPreviousDate(), getMainPanel().getPreviousDate()) )
		return;



	//copy all the valid row references into the temp vector 
	final Vector rows = new Vector(10);
	for( int i = 0; i < getMainPanel().getDisplayTable().getModel().getRowCount(); i++ ) {
		Vector v = new Vector(10);

		boolean validDate = false;
		for( int j = 0; j < getMainPanel().getDisplayTable().getModel().getColumnCount(); j++ ) {
			
			Object val = getMainPanel().getDisplayTable().getModel().getValueAt( i ,j );
			if( val instanceof Date
				 && ((Date)val).before(dq.getStopDate())
				 && ((Date)val).after(dq.getStartDate()) )
			{
				validDate = true;
			}
			
			v.add( val );
		}
		
		if( validDate )
			rows.add( v );
	}
			


	//did someone say "hack it"??!!
	TableModel tempModel = new AbstractTableModel() {

        public int getColumnCount() { 
        		return getMainPanel().getDisplayTable().getModel().getColumnCount(); }
        		
        public int getRowCount() { 
        		return rows.size(); 
        }

		  //special care here
        public Object getValueAt(int row, int col) {
        	
        		Vector v = (Vector)rows.get( row );
       		return v.get( col );
		  }

        public String getColumnName(int column) {
         	return getMainPanel().getDisplayTable().getModel().getColumnName(column);}
	};
	

	Cursor original = getCursor();
	setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
	
	int columnCount = getMainPanel().getDisplayTable().getColumnCount();
	JCPageTable table = null;
	java.awt.Font oldFont = null;
	JCPrinter printer = null;

	try
	{
		printer = new JCAWTPrinter(false);
	}
	catch (JCAWTPrinter.PrinterJobCancelledException e) 
	{
		com.cannontech.clientutils.CTILogger.info("Print Job Cancelled by user");
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

	table.setRowColumnDominance( JCPageTable.COLUMN_DOMINANCE );

	convertTable.populateTable( 
				table,
				tempModel );




	flow.setCurrentTextStyle( new JCTextStyle("TextStyle") );	
	oldFont = flow.getCurrentTextStyle().getFont();

		
	flow.getCurrentTextStyle().setFont( new java.awt.Font(oldFont.getName(), java.awt.Font.PLAIN, 21 ) );
	flow.print( getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString() );		
	flow.newLine();
	flow.getCurrentTextStyle().setFont( oldFont );
	flow.print( "Date Printed : " + getMainPanel().getJLabelDate().getText() + " " +
					getMainPanel().getJLabelTime().getText());

	//Print the range on the paper
	flow.newLine();
	flow.print( "Date Range : " + 
		new ModifiedDate(dq.getStartDate().getTime()).toString() + "  -  " + 
		new ModifiedDate(dq.getStopDate().getTime()).toString() );

	
	flow.getCurrentTextStyle().setFont( oldFont );
	flow.getCurrentTextStyle().setAlignment( JCTextStyle.ALIGNMENT_LEFT );	
	flow.newLine();


	flow.getCurrentTextStyle().setFont( new java.awt.Font(oldFont.getName(), java.awt.Font.PLAIN, 12) );
	flow.print( table );


	document.print();
	setCursor( original );
	
   
   repaint();

	return;
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
		flow.setCurrentTextStyle( new JCTextStyle("TextStyle") );

		if( getMainPanel().isClientDisplay() )
		{
			printClientDisplay( flow, document );			
		}
		else
		{
			printDisplay( flow, document );
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
	 	String previousItem = getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString();

	 	RemoveDisplayDialog display = 
							new RemoveDisplayDialog( this, previousItem );
							
		setCursor( original );
		display.setModal(true);
		display.setLocationRelativeTo(this);
		display.setTitle("Delete Display");
		display.show();
		
		
		if( !compCanceled(display) && originalDisplayNumber >= Display.BEGINNING_USER_DISPLAY_NUMBER )
		{
			setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

			getMainPanel().initComboCurrentDisplay();
			
			// just in case its also a bookmark
			for( int i = 0; i < display.getDisplayPanel().getRemovedDisplays().length; i++ )
				getBookmarks().removeBookmark( display.getDisplayPanel().getRemovedDisplays()[i]  );


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
				
			RemoveDisplayPanel display =
				new RemoveDisplayPanel( 
						displayToRemove, 
						String.valueOf(getCurrentDisplayNumber()) );			
			
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
	 	RemoveTemplateDialog display = 
				new RemoveTemplateDialog( this );
							
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
		javax.swing.JTable[] tables = null;
		
		if( getMainPanel().isClientDisplay() )
			tables = getMainPanel().getCurrentSpecailChild().getJTables();
		else
			tables = new javax.swing.JTable[] { getMainPanel().getDisplayTable() };

		((com.cannontech.tdc.search.TextSearchJPanel)
				getTextSearchDialog().getContentPane()).setTablesToSearch( tables );

		String[] columnNames = new String[ tables[0].getColumnModel().getColumnCount() ];
		for( int i = 0; i < tables[0].getColumnModel().getColumnCount(); i++ )
			columnNames[i] = tables[0].getColumnName(i);
			
		((com.cannontech.tdc.search.TextSearchJPanel)
				getTextSearchDialog().getContentPane()).setColumnNames( columnNames );

		getTextSearchDialog().show();
	}
	catch( ClassCastException e )
	{
		//should not get here ever!!!!
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
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
public void JToolBarButtonMuteAlarmsAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAlarmToolBar()) 
		connEtoC27(newEvent);
	// user code begin {2}
	// user code end
}

/**
 * Method to handle events for the AlarmToolBarListener interface.
 * @param newEvent java.util.EventObject
 */
public void JToolBarButtonSilenceAlarmsAction_actionPerformed(java.util.EventObject newEvent) 
{
	if( newEvent.getSource() == getAlarmToolBar() )
	{
		if( getMainPanel().isClientDisplay() )
			getMainPanel().getCurrentSpecailChild().silenceAlarms();
	
		//Always set the MainTableModel sound toggle flag	
		getMainPanel().getTableDataModel().silenceAlarms();

	}
		//alarmToolBar_JToolBarButtonSilenceAlarmsAction_actionPerformed( newEvent );
}


/**
 * Method to handle events for the AlarmToolBarListener interface.
 * @param newEvent java.util.EventObject
 */
public void JToolBarFilter_actionPerformed(java.util.EventObject newEvent) 
{
	if( newEvent.getSource() == getAlarmToolBar().getJComboBoxFilter() )
	{
		ITDCFilter filter = getAlarmToolBar().getSelectedFilter();
		
		getMainPanel().getCurrentDisplay().setTdcFilter( filter );

		//refresh the display we are looking at
		alarmToolBar_JToolBarButtonRefreshAction_actionPerformed( newEvent );			
//		getMainPanel().fireJComboCurrentDisplayAction_actionPerformed( 
//				new java.util.EventObject(this) );
	}

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
public static void main(final java.lang.String[] args)
{
	try
	{
		System.setProperty("cti.app.name", "TDC");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		SplashWindow splash = new SplashWindow(
			null,
			CtiUtilities.CTISMALL_GIF,
			"Loading " + System.getProperty("cti.app.name") + "...",
			new Font("dialog", Font.BOLD, 14 ), Color.black, Color.blue, 2 );
		
		
		CTILogger.info("Syntax for optional parameters is as follows:");
		CTILogger.info("   TDCMainFrame view=<value> display=<value>");

      
		ClientSession session = ClientSession.getInstance(); 

		if(!session.establishSession(null))
		{
			System.exit(-1);			
		}
	  	
		if(session == null) 
		{
			System.exit(-1);
		}
			 
		if(!session.checkRole(TDCRole.ROLEID)) 
		{
		  JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.", "Access Denied", JOptionPane.WARNING_MESSAGE);
		  System.exit(-1);				
		}
		
		javax.swing.ToolTipManager.sharedInstance().setDismissDelay(2000);

		final CommandLineParser parser;
		final TDCMainFrame aTDCFrame;
		final com.cannontech.tdc.spawn.TDCOverSeeer overSeer = new com.cannontech.tdc.spawn.TDCOverSeeer();

		if( args.length > 1 )  // the user tried to enter some params
		{
			com.cannontech.clientutils.CTILogger.info("Remember when entering parameters be sure to surrond parameters with spaces with double quotes.");
			com.cannontech.clientutils.CTILogger.info("Example:  TDCMainFrame view=Core \"display=Event Viewer\" ");
			
			parser = new CommandLineParser( TDCMainFrame.COMMAND_LINE_PARAM_NAMES );
						
			aTDCFrame = overSeer.createTDCMainFrame( parser.parseArgs( args ) );

		}
		else
		{
			aTDCFrame = overSeer.createTDCMainFrame();
		}		
		
		splash.setVisible( false );
		splash.dispose();

		aTDCFrame.visibilityInitialization();
	}
	catch (Throwable exception)
	{
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace( System.err );
		System.exit(-1);
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
		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMP_INDX_CLEAR,
			Display.isHistoryDisplay(source.getCurrentDisplay().getDisplayNumber()) );
			
		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMP_INDX_ACKALL,
			(Display.isAlarmDisplay(source.getCurrentDisplay().getDisplayNumber())
			&& !source.getCurrentDisplay().getTdcFilter().getConditions().get(ITDCFilter.COND_HISTORY)) 
			|| Display.isUserDefinedType(source.getCurrentDisplay().getType()) );
		
		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMP_INDX_ACTIVCEALARMS,
			Display.isAlarmDisplay(source.getCurrentDisplay().getDisplayNumber()) );


		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMP_INDX_DATELABEL,
			Display.isHistoryDisplay(source.getCurrentDisplay().getDisplayNumber())
			|| source.getCurrentDisplay().getTdcFilter().getConditions().get(ITDCFilter.COND_HISTORY) );

		getAlarmToolBar().setJComponentEnabled( getAlarmToolBar().COMP_INDX_DATE,
			Display.isHistoryDisplay(source.getCurrentDisplay().getDisplayNumber())
			|| source.getCurrentDisplay().getTdcFilter().getConditions().get(ITDCFilter.COND_HISTORY) );

		
		setTitleFromDisplay();				
		refreshTDCClient();
		
		
		//we may need to update the filter we have selected
		if( source.getCurrentDisplay().getDisplayData() != null )
		{
			//we only need one filterID
			getAlarmToolBar().setSelectedFilter(
					source.getCurrentDisplay().getDisplayData().getFilterID() );
		}
		else
			getAlarmToolBar().setSelectedFilter( CtiUtilities.NONE_ZERO_ID );

		TDCMainFrame.messageLog.addMessage( 
				"Current Filter is " + source.getCurrentDisplay().getTdcFilter(), MessageBoxFrame.INFORMATION_MSG );		
	}


		
	//JMenuItems disabling needs to go here
	getJMenuItemPrint().setEnabled(
			Display.isHistoryDisplay(source.getCurrentDisplay().getDisplayNumber())  );

	getJMenuItemEditDisplays().setEnabled(
			Display.isUserDefinedType(source.getCurrentDisplay().getType())  );

	getJMenuItemMakeCopy().setEnabled(
			Display.isUserDefinedType(source.getCurrentDisplay().getType())  );

	getJMenuItemRemoveDisplays().setEnabled(
			Display.isUserDefinedType(source.getCurrentDisplay().getType())  );


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


private void printClientDisplay( JCFlow flow, JCDocument document ) 
{
	if( getMainPanel().getCurrentSpecailChild().getJTables().length <= 0 )
		return;

	for( int i = 0; i < getMainPanel().getCurrentSpecailChild().getJTables().length; i++ )
	{
		JCPageTable table = new JCPageTable( document, getMainPanel().getCurrentSpecailChild().getJTables()[i].getModel().getColumnCount() );
		JCPageTableFromJTable convertTable = new JCPageTableFromJTable();
		flow.print( getMainPanel().getCurrentSpecailChild().getJTables()[i].getName() );
		convertTable.populateTable( table, getMainPanel().getCurrentSpecailChild().getJTables()[i].getModel() );
		java.awt.Font oldFont = flow.getCurrentTextStyle().getFont();
		
		// Set the font
		flow.getCurrentTextStyle().setFont( new java.awt.Font(oldFont.getName(), java.awt.Font.PLAIN, 21 ) );

		table.fitToFrame(flow.getCurrentFrame(),
						 flow.getCurrentTextStyle());
		table.setRowColumnDominance( JCPageTable.COLUMN_DOMINANCE );

		
			
		flow.newLine();
		flow.getCurrentTextStyle().setFont( oldFont );
		flow.print( getMainPanel().getJLabelDate().getText() + " " +
					getMainPanel().getJLabelTime().getText());

		flow.getCurrentTextStyle().setFont( oldFont );
		flow.getCurrentTextStyle().setAlignment( JCTextStyle.ALIGNMENT_LEFT );	
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


private void printDisplay( JCFlow flow, JCDocument document )
{
	if( getMainPanel().getDisplayTable().getColumnCount() <= 0 )
		return;

	JCPageTableFromJTable convertTable = new JCPageTableFromJTable();
	java.awt.Font oldFont = flow.getCurrentTextStyle().getFont();


	JCPageTable table = new JCPageTable( 
				document, 
				getMainPanel().getDisplayTable().getColumnCount() );


	
	// Set the font
	flow.getCurrentTextStyle().setFont( new java.awt.Font(oldFont.getName(), java.awt.Font.PLAIN, 21 ) );
	flow.print( getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString() );

	convertTable.populateTable( 
			table, 
			getMainPanel().getDisplayTable().getModel() );
	

	table.fitToFrame(
				flow.getCurrentFrame(),
				flow.getCurrentTextStyle() );

	table.setRowColumnDominance( JCPageTable.COLUMN_DOMINANCE );

	
		
	flow.newLine();
	flow.getCurrentTextStyle().setFont( oldFont );
	flow.print( getMainPanel().getJLabelDate().getText() + " " +
				getMainPanel().getJLabelTime().getText());

	flow.getCurrentTextStyle().setFont( oldFont );
	flow.getCurrentTextStyle().setAlignment( JCTextStyle.ALIGNMENT_LEFT );	
	flow.newLine();
	
	flow.print( table );


   JCAWTPreviewer previewer = new JCAWTPreviewer(
							"Print Preview", 
		    				this,
							document );
   		
	previewer.setLocationRelativeTo( this );
	previewer.setSize( (int)(this.getWidth() * .75), this.getHeight() );
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
		if( getMainPanel().getJComboCurrentDisplay().getItemAt(i).toString().equals( value ) )
		{
			getMainPanel().getJComboCurrentDisplay().setSelectedIndex(i);
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

		
	if( Display.isUserDefinedType(getMainPanel().getCurrentDisplay().getType()) )
	{
		setTitle( getMainPanel().getCurrentDisplay().getName() + connected );
	}
	else if( Display.isCoreType(getMainPanel().getCurrentDisplay().getType()) )
	{
		setTitle( getMainPanel().getCurrentDisplay().getTitle() + connected );
	}

}

public void resetFilter()
{
	if( getAlarmToolBar().getJComboBoxFilter().getItemCount() > 0 )
		getAlarmToolBar().getJComboBoxFilter().setSelectedIndex( 0 );
}

/**
 * Sets up our frame with new display data. Will fire a DBChange if needed!
 * 
 */
private void setUpMainFrame( String previousItem )
{
	//fire a DBChange to allow other TDC's to refresh their display data from the DB
	DBChangeMsg dbChange =
			new DBChangeMsg(
				0,
				DBChangeMsg.CHANGE_TDC_DB,
				"ALL",
				"ALL",
				DBChangeMsg.CHANGE_TYPE_UPDATE );

            
	//tell our tree we may need to change the display	
	getTdcClient().write( dbChange );
	
	
	getMainPanel().initComboCurrentDisplay();

	if( previousItem != null )
	{
		for( int i = 0; i < getMainPanel().getJComboCurrentDisplay().getItemCount(); i++ )
			if( getMainPanel().getJComboCurrentDisplay().getItemAt(i).toString().equalsIgnoreCase(previousItem) )
			{
				getMainPanel().getJComboCurrentDisplay().setSelectedIndex(i);
				break;
			}
	}

	alarmToolBar_JToolBarButtonRefreshAction_actionPerformed( null );

	repaint();
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

public void messageReceived( MessageEvent e )
{
	Message in = e.getMessage();

	if( in instanceof com.cannontech.message.dispatch.message.Signal )
	{
		com.cannontech.message.dispatch.message.Signal sig =
			(com.cannontech.message.dispatch.message.Signal)in;

		getAlarmHandler().handleSignal( sig );

	}
	
	if( in instanceof Command
		 && ((Command)in).getOperation() == Command.ARE_YOU_THERE )
	{
		String retStr = null;
		if( (retStr = MessageUtils.getVersionComp((Command)in)) != null )
		{
			//we have a version mismatch, alert the user
			JOptionPane.showMessageDialog(
				this,
				"The client you are running is of a different version than the server. Please update your client software." + System.getProperty("line.separator") +
				retStr,
				"Client/Server Version Mismatch",
				JOptionPane.OK_OPTION );
		}
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
		ParametersFile pf = new ParametersFile( CtiUtilities.OUTPUT_FILE_NAME );

		// get the current names for the params
		final String[] paramNames =
		{
			"DisplayName",
			"ViewType",
			"FrameX",
			"FrameY",
			"FrameWidth",
			"FrameHeight",
			"FontName",
			"FontSize",
			"HGridLine",
			"VGridLine",
			"MessageLog",
			"ToolBox",
			"Mute"
		};

		// get the current values for the params
		final String[] paramValues =
		{
			(getMainPanel().getJComboCurrentDisplay().getSelectedItem() == null
			 	? "" : getMainPanel().getJComboCurrentDisplay().getSelectedItem().toString()),

			getSelectedViewType(),
			String.valueOf(getX()),
			String.valueOf(getY()),
			String.valueOf(getWidth()),
			String.valueOf(getHeight()),
			getMainPanel().getDisplayTable().getFont().getName(),
			String.valueOf(getMainPanel().getDisplayTable().getFont().getSize()),
			String.valueOf(getJCheckBoxMenuItemHGridLines().getState()),
			String.valueOf(getJCheckBoxMenuItemVGridLines().getState()),
			String.valueOf(getJCheckBoxMenuItemShowLog().getState()),
			String.valueOf(getJCheckBoxMenuItemShowToolBar().getState()),

			String.valueOf( getAlarmToolBar().getJToolBarButtonMuteAlarms().getText().equalsIgnoreCase("UnMute") )
		};

		pf.updateValues( paramNames, paramValues );
	}
	catch ( Exception e )
	{
		handleException( e );
	}
	
}
}