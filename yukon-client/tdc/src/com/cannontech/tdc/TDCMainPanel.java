package com.cannontech.tdc;

/**
 * Insert the type's description here.
 * Creation date: (1/20/00 11:43:56 AM)
 * @author: 
 */
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.table.TableColumn;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.parametersfile.ParametersFile;
import com.cannontech.clientutils.popup.PopUpMenuShower;
import com.cannontech.clientutils.tags.AlarmUtils;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.gui.panel.CompositeJSplitPane;
import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.gui.util.SortTableModelWrapper;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.cache.functions.TagFuncs;
import com.cannontech.database.cache.functions.YukonImageFuncs;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.tags.Tag;
import com.cannontech.tdc.commandevents.AckAlarm;
import com.cannontech.tdc.data.ColumnData;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.data.DisplayData;
import com.cannontech.tdc.filter.ITDCFilter;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.roweditor.AltScanRatePanel;
import com.cannontech.tdc.roweditor.AnalogPanel;
import com.cannontech.tdc.roweditor.EditorDialogData;
import com.cannontech.tdc.roweditor.ManualEntryJPanel;
import com.cannontech.tdc.roweditor.RowEditorDialog;
import com.cannontech.tdc.roweditor.SendData;
import com.cannontech.tdc.roweditor.StatusPanelControlEntry;
import com.cannontech.tdc.roweditor.StatusPanelManualEntry;
import com.cannontech.tdc.roweditor.TagWizardPanel;
import com.cannontech.tdc.roweditor.TagsEditorPanel;
import com.cannontech.tdc.toolbar.AlarmToolBar;
import com.cannontech.tdc.utils.DataBaseInteraction;
import com.cannontech.tdc.utils.TDCDefines;

public class TDCMainPanel extends javax.swing.JPanel implements com.cannontech.tdc.bookmark.BookMarkSelectionListener, java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.MouseListener, javax.swing.event.PopupMenuListener 
{
	//an int read in as a CParm used to turn on/off features
   public static final String PROP_BOOKMARK = "con.cannontech.BookMark";

	private boolean needColDataUpdate = true;

	private TDCClient tdcClient = null;  // copy of the tdcClient from the mainFrame
	// stores the last historical query, needed for when the Arrow buttons are pushed
	private int pageNumber = 1;
	private int totalPages = 1;
	private SpecialTDCChild currentSpecailChild = null;
	private AlarmToolBar frameAlarmToolBar = null;
	private SortTableModelWrapper sorterModelWrapper = null;

	/* START -Display atrributes */
	private Display[] lastDisplays = new Display[Display.DISPLAY_TYPES.length];  // holds a last display for EVERY display type that exists
	private Display currentDisplay = null;
	private Display[] allDisplays = null;
	/* END -Display atrributes */
	
	
	private boolean refreshPressed = false;
	private boolean initialStart = true;
	private Date previousDate = null;
	
	private javax.swing.JOptionPane closeBox = null;
	private Display2WayDataAdapter dbAdaptor = null;
	public boolean connectedToDB = false;
	private javax.swing.JComboBox ivjJComboCurrentDisplay = null;
	private javax.swing.JLabel ivjJLabelDate = null;
	private javax.swing.JLabel ivjJLabelDisplayName = null;
	private javax.swing.JLabel ivjJLabelTime = null;
	private javax.swing.JTable ivjDisplayTable = null;
	private javax.swing.JLabel ivjJLabelDisplayTitle = null;
	private javax.swing.JScrollPane ivjScrollPaneDisplayTable = null;
	protected transient com.cannontech.tdc.TDCMainPanelListener fieldTDCMainPanelListenerEventMulticaster = null;
	private javax.swing.JMenu ivjJMenuPopUpAckAlarm = null;
	private javax.swing.JMenuItem ivjJMenuItemPopUpClear = null;
	private javax.swing.JMenuItem ivjJMenuItemPopUpManualEntry = null;
	private javax.swing.JRadioButtonMenuItem ivjJRadioButtonMenuItemDisableDev = null;
	private javax.swing.JRadioButtonMenuItem ivjJRadioButtonMenuItemDisablePt = null;
	private javax.swing.JRadioButtonMenuItem ivjJRadioButtonMenuItemEnableDev = null;
	private javax.swing.JRadioButtonMenuItem ivjJRadioButtonMenuItemEnbablePt = null;
	private javax.swing.JMenu jMenuAbleDis = null;
	private javax.swing.JMenuItem ivjJMenuItemPageBack = null;
	private javax.swing.JMenuItem ivjJMenuItemPageForward = null;
    
    private BasicComboPopup comboPopup = null;
    private JComboBox jComboBoxPopUp = null;
    
	private javax.swing.JPopupMenu ivjJPopupMenuManual = null;
	private javax.swing.JRadioButtonMenuItem ivjJRadioButtonPage1 = null;
	private javax.swing.ButtonGroup buttonGroupPage = null;
	private javax.swing.JMenuItem ivjJMenuItemPopUpManualControl = null;
	private javax.swing.JMenu ivjJMenuControl = null;
	private TDCDBChangeHandler dbChangeHandler = null;
	
	
	private javax.swing.JMenuItem jMenuItemInhibitPoint = null;	
	private javax.swing.JMenuItem jMenuItemInhibitDevice = null;

	private javax.swing.JMenuItem jMenuItemGraph = null;
	private javax.swing.JMenuItem jMenuItemCreateTag = null;
	private javax.swing.JMenuItem jMenuItemAltScanRate = null;



/**
 * TDC constructor comment.
 */
public TDCMainPanel() {
	super();
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
	if (e.getSource() == getJComboCurrentDisplay()) 
		connEtoC2(e);
	if (e.getSource() == getJMenuItemPopUpManualEntry()) 
		connEtoC9(e);
	if (e.getSource() == getJMenuItemPageForward()) 
		connEtoC7(e);
	if (e.getSource() == getJMenuItemPageBack()) 
		connEtoC8(e);
	if (e.getSource() == getJMenuItemPopUpManualControl()) 
		connEtoC11(e);
	if( e.getSource() == getJMenuItemGraph() ) 
		jMenuItemGraph_ActionPerformed( e );

	if( e.getSource() == getJMenuItemAltScanRate() ) 
		jMenuItemAltScanRate_ActionPerformed( e );

	if( e.getSource() == getJMenuItemInhibitDevice() ) 
		jMenuItemInhibitDev_ActionPerformed(e);

	if( e.getSource() == getJMenuItemCreateTag() ) 
		jMenuItemCreateTag_ActionPerformed( e );

	if( e.getSource() == getJComboPopup() ) 
		jComboPopupPage_ActionPerformed();
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 12:58:49 PM)
 * Version: <version>
 * @param value java.lang.Object
 */
private void addClientRadioButtons(Object value, int index, boolean enabled ) 
{	
	TDCMainFrame parentFrame = 
			((TDCMainFrame)CtiUtilities.getParentFrame( this ));

	parentFrame.addClientRadioButtons( value, index, enabled );
}
/**
 * 
 * @param newListener com.cannontech.tdc.TDCMainPanelListener
 */
public void addTDCMainPanelListener(com.cannontech.tdc.TDCMainPanelListener newListener) {
	fieldTDCMainPanelListenerEventMulticaster = com.cannontech.tdc.TDCMainPanelListenerEventMulticaster.add(fieldTDCMainPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/00 11:46:00 AM)
 */
public void checkForMissingPoints() 
{
	if( getTableDataModel().getPointsInLimbo() != null )
	{
		java.awt.Frame owner = CtiUtilities.getParentFrame( this );
		
		MissingPointsDialog missing = new MissingPointsDialog( owner, getTableDataModel().getPointsInLimbo() );
		missing.setLocationRelativeTo( owner );
		missing.show();
		
		getTableDataModel().setLimboPointsValue( null );
		
	}

}
/**
 * connEtoC1:  (ScrollPaneDisplayTable.mouse.mousePressed(java.awt.event.MouseEvent) --> TDCMainPanel.scrollPaneDisplayTable_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.displayTable_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (JLabelDisplayTitle.mouse.mousePressed(java.awt.event.MouseEvent) --> TDCMainPanel.jLabelDisplayTitle_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jLabelDisplayTitle_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC11:  (JMenuItemPopUpManualControl.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainPanel.jMenuItemPopUpManualControl_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPopUpManualControl_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC12:  (JRadioButtonMenuItemDisableDev.item.itemStateChanged(java.awt.event.ItemEvent) --> TDCMainPanel.jRadioButtonMenuItemDisableDev_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jRadioButtonMenuItemDisableDev_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC13:  (JRadioButtonMenuItemEnableDev.item.itemStateChanged(java.awt.event.ItemEvent) --> TDCMainPanel.jRadioButtonMenuItemEnableDev_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jRadioButtonMenuItemEnableDev_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (JRadioButtonMenuItemDisablePt.item.itemStateChanged(java.awt.event.ItemEvent) --> TDCMainPanel.jRadioButtonMenuItemDisablePt_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jRadioButtonMenuItemDisablePt_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC15:  (JRadioButtonMenuItemEnbablePt.item.itemStateChanged(java.awt.event.ItemEvent) --> TDCMainPanel.jRadioButtonMenuItemEnbablePt_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jRadioButtonMenuItemEnbablePt_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JComboCurrentDisplay.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainPanel.fireJComboCurrentDisplayAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJComboCurrentDisplayAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (DisplayTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> TDCMainPanel.displayTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.displayTable_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JPopupMenu.popupMenu.popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent) --> TDCMainPanel.jPopupMenu_PopupMenuWillBecomeVisible(Ljavax.swing.event.PopupMenuEvent;)V)
 * @param arg1 javax.swing.event.PopupMenuEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.PopupMenuEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jPopupMenu_PopupMenuWillBecomeVisible(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPageForward_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPageBack_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (JMenuItemPopUpChangeValue.action.actionPerformed(java.awt.event.ActionEvent) --> TDCMainPanel.jMenuItemPopUpChangeValue_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jMenuItemPopUpManualEntry_ActionPerformed(arg1);
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
 * Creation date: (3/8/00 4:31:03 PM)
 * @return javax.swing.JPanel
 * @param selectedRow int
 */
private ManualEntryJPanel createManualEditorPanel(int selectedRow, Object source ) 
{
	//JPanel retValue = null;
	Display2WayDataAdapter tableModel = getTableDataModel();

	try
	{
		String ptType = tableModel.getPointType( selectedRow );
		PointValues pt = tableModel.getPointValue( selectedRow );
		Object currentValue = tableModel.getPointDynamicValue( selectedRow );

		EditorDialogData data = new EditorDialogData( pt, pt.getAllText() );

			
		if( PointTypes.getType(ptType) == PointTypes.ANALOG_POINT ||
			 PointTypes.getType(ptType) == PointTypes.PULSE_ACCUMULATOR_POINT ||
			 PointTypes.getType(ptType) == PointTypes.DEMAND_ACCUMULATOR_POINT ||
			 PointTypes.getType(ptType) == PointTypes.CALCULATED_POINT )
		{
			return new AnalogPanel( data, currentValue );
		}
		else if( PointTypes.getType(ptType) == PointTypes.STATUS_POINT
					&& ( TagUtils.isControllablePoint(data.getTags()) && TagUtils.isControlEnabled(data.getTags()) )
					&& source != getJMenuItemPopUpManualEntry() )
		{			
			return new StatusPanelControlEntry( data, currentValue );
		}
		else if( PointTypes.getType(ptType) == PointTypes.STATUS_POINT )
		{			
			return new StatusPanelManualEntry( data, currentValue );
		}
		else
			CTILogger.info("** Unhandled POINTTYPE (" + ptType +") for a Manual Entry Panel **");
	}
	catch( NullPointerException ex )
	{   // one of the values we have is null, lets not create the panel
		handleException( ex );
	}
		
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/10/00 1:49:00 PM)
 * @param currentDisplayNumber int
 */
private void deleteBlankColumnsFromDB(int currentDisplayNumber) 
{
	
	String query = new String
		("delete from display2waydata where DisplayNum = ? " +
		 " and pointid = ?" );

	Object[] objs = new Object[2];
	objs[0] = new Long(currentDisplayNumber);
	objs[1] = new Long(TDCDefines.ROW_BREAK_ID);
	DataBaseInteraction.updateDataBase( query, objs );
}
/**
 * Comment
 */
public void displayTable_MouseClicked(java.awt.event.MouseEvent mouseEvent) 
{
	if( mouseEvent.getClickCount() == 2 && getDisplayTable().getSelectedRow() >= 0 
		 && getTdcClient().connected() )
	{
		if( mouseEvent.isShiftDown() )
		{
			showDebugInfo();
		}
		else if( Display.isUserDefinedType(getCurrentDisplay().getType()) )
		{
			showRowEditor( mouseEvent.getSource() );
		}
	}
		
	return;
}
/**
 * Comment
 */
public void displayTable_MousePressed(java.awt.event.MouseEvent mouseEvent) 
{
	int rowLocation = getDisplayTable().rowAtPoint( mouseEvent.getPoint() );
	
	getDisplayTable().getSelectionModel().setSelectionInterval(
			 		rowLocation, rowLocation );
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (7/5/2001 10:10:20 AM)
 */
public void executeDateChange( Date newDate ) 
{
	getTableDataModel().setCurrentDate( newDate );
	java.awt.Frame owner = CtiUtilities.getParentFrame(this);
	Cursor original = owner.getCursor();
	owner.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

	try
	{
		if( Display.isTodaysDate(newDate) )
			 //&& !Display.isAlarmDisplay(getCurrentDisplay().getDisplayNumber()) )
		{
			//we are looking at today
			setDisplayTitle( getCurrentDisplay().getName(), null );
		}
		else
		{
			setDisplayTitle( getCurrentDisplay().getName(), newDate );
		}
	
		
		//only accept the change if we are looking at the event viewer
		//  and there is at least a 1 minute difference between the new date and the old date
		if( (Display.isHistoryDisplay(getCurrentDisplay().getDisplayNumber())
			 || getCurrentDisplay().getTdcFilter().getConditions().get(ITDCFilter.COND_HISTORY) )
			 && (int)(newDate.getTime() / 600000) != (int)(getPreviousDate().getTime() / 600000) )
		{		
			getTableDataModel().clearSystemViewerDisplay( true );
			resetPagingProperties();

			//getHistoryDisplayData( new Date(newDate.getTime()) );
		}
	}		
	finally
	{
		previousDate = newDate;

		owner.setCursor( original );
		executeRefresh_Pressed();
	}		
	
}

/**
 * Insert the method's description here.
 * Creation date: (12/11/2001 3:58:32 PM)
 * @param bookMark java.lang.String
 */
/* This method will wait at most waitSecs in a separate Thread for the item 
choosen in the bookmark to "arrive" in the JComboBox.  If the time elapses, 
then we do not attempt to select it and just return */
private void fire_SelectClientBookMark(final String bookMark) 
{
	final int waitSecs = 5;
	
	new Thread( new Runnable()
	{

		public void run()
		{
			for( int o = 0; o < waitSecs ; o++)
			{	
				for( int i = 0; i < getJComboCurrentDisplay().getItemCount(); i++ )
					if( getJComboCurrentDisplay().getItemAt(i).toString().equals(bookMark) )
					{
						getJComboCurrentDisplay().setSelectedIndex(i);
						return;
						//break;
					}
					
				try
				{ Thread.sleep(1000); }
				catch(InterruptedException e ){}
			}
		}
	}).start();
	
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:57:02 PM)
 * @param source Object
 */
public void fireBookMarkSelected( Object source )
{
	//set our display column data for the display we are looking at
	updateDisplayColumnData();
	needColDataUpdate = false;

	Cursor original = setCursorToWait();
	resetMainPanel();
	
	try
	{
		// set our last display to the last one we were looking at
		if( getCurrentDisplay() != null )
			setLastDisplays( Display.getDisplayTypeIndexByType(getCurrentDisplay().getType()), getCurrentDisplay() );

		// see if one of the radio buttons were fired
		if( source instanceof javax.swing.JRadioButtonMenuItem )
		{
			javax.swing.JRadioButtonMenuItem radioButton = (javax.swing.JRadioButtonMenuItem)source;
			String currentDisplayTitle = radioButton.getText();
					
			if( Display.getDisplayTypeIndexByTitle(currentDisplayTitle) 
				 == Display.ALARMS_AND_EVENTS_TYPE_INDEX )
			{
				setCurrentDisplay( 
                  getAllDisplays()
                     [getDisplayIndexOfFirstType(
                        Display.getDisplayType(Display.ALARMS_AND_EVENTS_TYPE_INDEX))] );
			}
			else if( Display.getDisplayTypeIndexByTitle(currentDisplayTitle) 
				 		== Display.CUSTOM_DISPLAYS_TYPE_INDEX )
			{
				setCurrentDisplay( getAllDisplays()[ getDisplayIndexOfFirstType(Display.getDisplayType(Display.CUSTOM_DISPLAYS_TYPE_INDEX)) ] );
			}
			else
			{
				// set the current display to the newly selected one
				setCurrentDisplay( getAllDisplays()[getDisplayIndexByTitle(currentDisplayTitle)] );
				
				// we must have a none static client display
				initClientDisplays();
			}


			//!Display.needsNoIniting(getCurrentDisplay().getType()) )
			if( getCurrentSpecailChild() == null
				 || (getCurrentSpecailChild() != null && getCurrentSpecailChild().needsComboIniting()) )
			{			
				initComboCurrentDisplay();				
			}

			
			try
			{
				if( !initialStart )
				{
					if( getLastDisplays()[ Display.getDisplayTitle(currentDisplayTitle) ] != null )
					{
						getJComboCurrentDisplay().setSelectedItem( getLastDisplays()[Display.getDisplayTitle(currentDisplayTitle)] );
					}
					else
					{
						if( getJComboCurrentDisplay().getItemCount() > 0 )
							getJComboCurrentDisplay().setSelectedIndex(0);
						else
							getJComboCurrentDisplay().setSelectedIndex(-1);
					}
				}
			}
			catch( IllegalArgumentException e )
			{
				CTILogger.error( e.getMessage(), e );
				CTILogger.info("*****************************************************");
				CTILogger.info("  Most likely cause is an invalid database.");
				CTILogger.info("*****************************************************");
			}

		}
		else if( source instanceof javax.swing.JMenuItem )
		{
			// see if the source is just a regular user bookmark
			javax.swing.JMenuItem bookMarkButton = (javax.swing.JMenuItem)source;
			         
			java.util.StringTokenizer tokenizer = new java.util.StringTokenizer( 
               //bookMarkButton.getText(), 
               bookMarkButton.getClientProperty(PROP_BOOKMARK).toString(),
               com.cannontech.tdc.bookmark.BookMarkBase.BOOKMARK_TOKEN );
               
			final String bookMarkType = tokenizer.nextToken();
			final String bookMark = tokenizer.nextToken();

			// we must have the View Type clicked on here
			TDCMainFrame parentFrame = 
					((TDCMainFrame)CtiUtilities.getParentFrame( this ));

			parentFrame.setSelectedViewType( bookMarkType );

			// if the desired bookmark exists in the Combo Box, set it selected
			if( isClientDisplay() )
			{
				fire_SelectClientBookMark( bookMark );
			}
			else
			{
				for( int i = 0; i < getJComboCurrentDisplay().getItemCount(); i++ )
					if( getJComboCurrentDisplay().getItemAt(i).toString().equals(bookMark) )
					{						
						getJComboCurrentDisplay().setSelectedIndex(i);
						
						//set the date to today for all Alarm bookmarks and to the active alarms display
						if( Display.isAlarmDisplay(getCurrentDisplay().getDisplayNumber()) )
						{
							parentFrame.resetFilter();
						}
						
						
						break;
					}
			}
	
		}
		else
		{
			throw new IllegalArgumentException("A JMenuItem or JRadioButtonMenuItem was not found to be the cause of a bookmark event");
		}

	}
	finally
	{
		CtiUtilities.getParentFrame(this).setCursor( original );
		needColDataUpdate = true;
	}
	
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
private void fireJComboCurrentDisplayAction_actionPerformed(java.util.EventObject newEvent)
{
	//only save the column data IF we have not done so already. Will happen when coming from
	// fireBookMarkSelected()
	if( needColDataUpdate )
	{
		updateDisplayColumnData();
	}

	// only let events go through that are not special client related
	if( getCurrentSpecailChild() != null )
	{
		if( getJComboCurrentDisplay().getSelectedItem() != null )
			getCurrentDisplay().setName( getJComboCurrentDisplay().getSelectedItem().toString() );

	}
	else
	{
		Cursor original = setCursorToWait();
		try
		{
			if( getJComboCurrentDisplay().getItemCount() > 0 && 
				 getJComboCurrentDisplay().getSelectedIndex() >= 0 )
			{
				String selectedDisplayName = getJComboCurrentDisplay().getSelectedItem().toString();
	
	
				// set the current display to the one selected
				if( getDisplayIndexByName(selectedDisplayName) >= 0 )
					setCurrentDisplay( getAllDisplays()[ getDisplayIndexByName(selectedDisplayName) ] );
				
				// set our last display to the last one we were looking at
				if( getCurrentDisplay() != null )
					setLastDisplays( Display.getDisplayTypeIndexByType(getCurrentDisplay().getType()), getCurrentDisplay() );
		

				//new display, reset any page numbers
				resetPagingProperties();


				// check if we are a client display
				if( isClientDisplay() )
				{
					// FOR NOW, DO NOTHING, LATER WE WILL WANT THIS
					// TO DO A LITTLE MORE SINCE WE COULD HAVE DIFFERENT DISPLAYS FOR
					// EACH CLIENT DISPLAY!!!				
				}
				else
				{
					// we must have a CORE or a USER CREATED display
					setUpTable();

					if( this.isDisplayable() )
						checkForMissingPoints();
						
					if( Display.isCoreType(getCurrentDisplay().getType()) ) 
						initCoreDisplays();
				}

			}
		}
		finally
		{
			CtiUtilities.getParentFrame(this).setCursor( original );
			
		}
	}
	
	//set our display with the last format
	formatDisplayColumns();

	// tell the MAIN FRAME we just changed displays
	if( !(newEvent.getSource() instanceof TDCMainFrame)
		 && fieldTDCMainPanelListenerEventMulticaster != null)
	{
		fieldTDCMainPanelListenerEventMulticaster.JComboCurrentDisplayAction_actionPerformed(newEvent);
	}

		
	return;
}


/**
 * Insert the method's description here.
 * Creation date: (4/27/00 3:06:28 PM)
 * Version: <version>
 */
private Object[][] getAlarmStatesCache()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.List alarmStates = cache.getAllAlarmCategories();
	Object[][] data = null;
	
	synchronized(cache)
	{
		// We want do disclude the EVENT alarmStateId which is 1
		data = new Object[alarmStates.size()-1][5];
		long displayNumber = Display.GLOBAL_ALARM_DISPLAY + 1;  // always one after the global display alarm display number
		int indx = 0;

		for( int i = 0; i < alarmStates.size(); i++ )
		{
			LiteAlarmCategory liteAlarm = (LiteAlarmCategory)alarmStates.get(i);
			if( liteAlarm.getAlarmStateID() == Signal.EVENT_SIGNAL )
				continue;

			data[indx][0] = liteAlarm.getCategoryName();
			data[indx][1] = String.valueOf( displayNumber++ );
			data[indx][2] = liteAlarm.getCategoryName();
			data[indx][3] = Display.DISPLAY_TYPES[Display.ALARMS_AND_EVENTS_TYPE_INDEX];
			data[indx++][4] = liteAlarm.getCategoryName();
		}
	}

	if( alarmStates.size() <= 1 )
		CTILogger.info("*** The AlarmState Table has 1 or less entries in it, not good");

	return data;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 3:28:51 PM)
 * @return Display[]
 */
private Display[] getAllDisplays() 
{
	return allDisplays;
}
/**
 * Insert the method's description here.
 * Creation date: (7/3/2001 9:58:40 AM)
 * @return javax.swing.ButtonGroup
 */
public javax.swing.ButtonGroup getButtonGroupPage() 
{
	if( buttonGroupPage == null )
		buttonGroupPage = new javax.swing.ButtonGroup();

	return buttonGroupPage;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:57:02 PM)
 * @return com.cannontech.tdc.Display
 */
public Display getCurrentDisplay() 
{
	return currentDisplay;
}
/**
 * Insert the method's description here.
 * Creation date: (2/28/00 4:52:53 PM)
 * @return int
 */
//public long getCurrentDisplayNumber() 
//{
//	return getTableDataModel().getCurrentDisplayNumber();
//}
/**
 * Insert the method's description here.
 * Creation date: (12/12/00 11:19:33 AM)
 * @return com.cannontech.tdc.SpecialTDCChild
 */
public SpecialTDCChild getCurrentSpecailChild() {
	return currentSpecailChild;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 4:07:13 PM)
 * @return int
 * @param displayName java.lang.String
 */ 
private int getDisplayIndexByJComboValue(String name) 
{	
	for( int i = 0; i < getJComboCurrentDisplay().getItemCount(); i++ )
	{
		String comboName = getJComboCurrentDisplay().getItemAt( i ).toString();

		if( name.equalsIgnoreCase( comboName ) )
			return i;
	}
	
	return 0;
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 4:07:13 PM)
 * @return int
 * @param displayName java.lang.String
 */
private int getDisplayIndexByName(String displayName) 
{
	for( int i = 0; i < getAllDisplays().length; i++ )
		if( getAllDisplays()[i].getName().equalsIgnoreCase(displayName) )
			return i;
			
	return -1;
}
private int getDisplayIndexByTitle(String displayTitle)
{
	for( int i = 0; i < getAllDisplays().length; i++ )
		if( getAllDisplays()[i].getTitle().equalsIgnoreCase(displayTitle) )
			return i;

	throw new RuntimeException("Display title '" + displayTitle + "' was not found.");
	//return -1;
}/**
 * Insert the method's description here.
 * Creation date: (3/20/2001 2:34:03 PM)
 * @return int
 * @param displayType java.lang.String
 */
private int getDisplayIndexOfFirstType(String displayType) 
{
	for( int i = 0; i < getAllDisplays().length; i++ )
		if( getAllDisplays()[i].getType().equalsIgnoreCase(displayType) )
			return i;
			
	throw new RuntimeException("Display type '" + displayType + "' was not found.");
	//return -1;
}/**
 * Return the JMenuItemPageBack property value.
 * @return javax.swing.JMenuItem
 */
///**
/* Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JTable getDisplayTable() {
	if (ivjDisplayTable == null) {
		try {
			ivjDisplayTable = new javax.swing.JTable();
			ivjDisplayTable.setName("DisplayTable");
			getScrollPaneDisplayTable().setColumnHeaderView(ivjDisplayTable.getTableHeader());
			//getScrollPaneDisplayTable().getViewport().setBackingStoreEnabled(true);
			ivjDisplayTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjDisplayTable.setOpaque(false);
			ivjDisplayTable.setShowVerticalLines(false);
			ivjDisplayTable.setShowHorizontalLines(false);
			ivjDisplayTable.setRowMargin(0);
			ivjDisplayTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
			ivjDisplayTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
				
			sorterModelWrapper = new SortTableModelWrapper(getTableDataModel());			
			ivjDisplayTable.setModel(sorterModelWrapper);
			
			ivjDisplayTable.setGridColor( getDisplayTable().getTableHeader().getBackground() );
			
			ivjDisplayTable.createDefaultColumnsFromModel();
			ivjDisplayTable.setDefaultRenderer(Object.class, new DisplayTableCellRenderer());

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisplayTable;
}
/**
 * Insert the method's description here.
 * Creation date: (1/21/00 11:46:00 AM)
 */
public Object[] getDisplayTablesBlankRows() 
{
	return getTableDataModel().getBlankRows();
}
/**
 * Return the JComboCurrentDisplay property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JComboBox getJComboCurrentDisplay() {
	if (ivjJComboCurrentDisplay == null) {
		try {
			ivjJComboCurrentDisplay = new javax.swing.JComboBox();
			ivjJComboCurrentDisplay.setName("JComboCurrentDisplay");
			ivjJComboCurrentDisplay.setPreferredSize(new java.awt.Dimension(160, 20));
			ivjJComboCurrentDisplay.setBackground(java.awt.Color.white);
			ivjJComboCurrentDisplay.setMaximumSize(new java.awt.Dimension(160, 20));
			ivjJComboCurrentDisplay.setMinimumSize(new java.awt.Dimension(160, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboCurrentDisplay;
}
/**
 * Return the JLabelDate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JLabel getJLabelDate() {
	if (ivjJLabelDate == null) {
		try {
			ivjJLabelDate = new javax.swing.JLabel();
			ivjJLabelDate.setName("JLabelDate");
			ivjJLabelDate.setText("Date");
			ivjJLabelDate.setMaximumSize(new java.awt.Dimension(39, 24));
			ivjJLabelDate.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			ivjJLabelDate.setPreferredSize(new java.awt.Dimension(39, 24));
			ivjJLabelDate.setFont(new java.awt.Font("serif.bold", 1, 18));
			ivjJLabelDate.setMinimumSize(new java.awt.Dimension(39, 24));
			ivjJLabelDate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDate;
}
/**
 * Return the JLabelDisplayName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDisplayName() {
	if (ivjJLabelDisplayName == null) {
		try {
			ivjJLabelDisplayName = new javax.swing.JLabel();
			ivjJLabelDisplayName.setName("JLabelDisplayName");
			ivjJLabelDisplayName.setText("Display");
			// user code begin {1}
			
			ivjJLabelDisplayName.setText("View");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDisplayName;
}
/**
 * Return the JLabelDisplayTItle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDisplayTitle() {
	if (ivjJLabelDisplayTitle == null) {
		try {
			ivjJLabelDisplayTitle = new javax.swing.JLabel();
			ivjJLabelDisplayTitle.setName("JLabelDisplayTitle");
			ivjJLabelDisplayTitle.setBorder(new com.cannontech.common.gui.util.TitleBorder());
			ivjJLabelDisplayTitle.setText("No Title");
			ivjJLabelDisplayTitle.setMaximumSize(new java.awt.Dimension(84, 24));
			ivjJLabelDisplayTitle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			ivjJLabelDisplayTitle.setPreferredSize(new java.awt.Dimension(84, 24));
			ivjJLabelDisplayTitle.setFont(new java.awt.Font("dialog.bold", 1, 18));
			ivjJLabelDisplayTitle.setMinimumSize(new java.awt.Dimension(84, 24));
			ivjJLabelDisplayTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}

			ivjJLabelDisplayTitle.setForeground( java.awt.Color.black );
			ivjJLabelDisplayTitle.setBackground( java.awt.Color.black );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDisplayTitle;
}
/**
 * Return the JLabelTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JLabel getJLabelTime() {
	if (ivjJLabelTime == null) {
		try {
			ivjJLabelTime = new javax.swing.JLabel();
			ivjJLabelTime.setName("JLabelTime");
			ivjJLabelTime.setFont(new java.awt.Font("serif.bold", 1, 18));
			ivjJLabelTime.setText("Time");
			ivjJLabelTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			ivjJLabelTime.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTime;
}
/**
 * Return the JMenuControl property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getJMenuTags() 
{
	if( ivjJMenuControl == null )
	{
		try 
		{
			ivjJMenuControl = new javax.swing.JMenu();
			ivjJMenuControl.setName("JMenuControl");
			ivjJMenuControl.setMnemonic('r');
			ivjJMenuControl.setText("Control");
			// user code begin {1}

			ivjJMenuControl.add( getJMenuItemInhibitPoint() );
			ivjJMenuControl.add( getJMenuItemInhibitDevice() );
			
//			separatorViews = new javax.swing.JSeparator();
//			separatorViews.setName("JSeparatorViews");
//			separatorViews.setOpaque(false);
//			separatorViews.setVisible(true);
//			separatorViews.setEnabled(true);
//			separatorViews.setMinimumSize(new java.awt.Dimension(0, 2));

			ivjJMenuControl.add( new javax.swing.JSeparator() );
			ivjJMenuControl.add( getJMenuItemCreateTag() );
			//remove all item
						
			//must be our last item in the list!!
			getJMenuTags().add( new javax.swing.JSeparator() );

			
			ivjJMenuControl.setMnemonic('t');
			ivjJMenuControl.setText("Tags");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuControl;
}
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPageBack() {
	if (ivjJMenuItemPageBack == null) {
		try {
			ivjJMenuItemPageBack = new javax.swing.JMenuItem();
			ivjJMenuItemPageBack.setName("JMenuItemPageBack");
			ivjJMenuItemPageBack.setMnemonic('b');
			ivjJMenuItemPageBack.setText("Page Back");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPageBack;
}
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPageForward() {
	if (ivjJMenuItemPageForward == null) {
		try {
			ivjJMenuItemPageForward = new javax.swing.JMenuItem();
			ivjJMenuItemPageForward.setName("JMenuItemPageForward");
			ivjJMenuItemPageForward.setMnemonic('f');
			ivjJMenuItemPageForward.setText("Page Forward");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPageForward;
}
/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenu getJMenuPopUpAckAlarm() {
	if (ivjJMenuPopUpAckAlarm == null) {
		try {
			ivjJMenuPopUpAckAlarm = new javax.swing.JMenu();
			ivjJMenuPopUpAckAlarm.setName("JMenuPopUpAckAlarm");
			ivjJMenuPopUpAckAlarm.setMnemonic('A');
			ivjJMenuPopUpAckAlarm.setText("Ack Alarm");

		} catch (java.lang.Throwable ivjExc) {

			handleException(ivjExc);
		}
	}
	return ivjJMenuPopUpAckAlarm;
}
/**
 * Return the JMenuItemPopUpManualControl property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPopUpManualControl() {
	if (ivjJMenuItemPopUpManualControl == null) {
		try {
			ivjJMenuItemPopUpManualControl = new javax.swing.JMenuItem();
			ivjJMenuItemPopUpManualControl.setName("JMenuItemPopUpManualControl");
			ivjJMenuItemPopUpManualControl.setMnemonic('c');
			ivjJMenuItemPopUpManualControl.setText("Manual Control...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPopUpManualControl;
}

/**
 * Return the JMenuItemCreateTag property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemCreateTag() 
{
	if( jMenuItemCreateTag == null )
	{
		try 
		{
			jMenuItemCreateTag = new javax.swing.JMenuItem();
			jMenuItemCreateTag.setName("jMenuItemCreateTag");
			jMenuItemCreateTag.setMnemonic('n');
			jMenuItemCreateTag.setText("New Tag...");
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
	}

	return jMenuItemCreateTag;
}


/**
 * Return the JMenuItemPopUpChangeValue property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getJMenuItemPopUpManualEntry() {
	if (ivjJMenuItemPopUpManualEntry == null) {
		try {
			ivjJMenuItemPopUpManualEntry = new javax.swing.JMenuItem();
			ivjJMenuItemPopUpManualEntry.setName("JMenuItemPopUpManualEntry");
			ivjJMenuItemPopUpManualEntry.setMnemonic('M');
			ivjJMenuItemPopUpManualEntry.setText("Manual Entry...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJMenuItemPopUpManualEntry;
}

/**
 * Return the JMenuItemGraph property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemGraph() 
{
	if( jMenuItemGraph == null ) 
	{
		try 
		{
			jMenuItemGraph = new javax.swing.JMenuItem();
			jMenuItemGraph.setName("JMenuItemGraph");
			jMenuItemGraph.setMnemonic('T');
			jMenuItemGraph.setText("30 Day Trend...");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	return jMenuItemGraph;
}
 
/**
 * Return the jMenuItemAltScanRate property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemAltScanRate() 
{
	if( jMenuItemAltScanRate == null ) 
	{
		try 
		{
			jMenuItemAltScanRate = new javax.swing.JMenuItem();
			jMenuItemAltScanRate.setName("jMenuItemAltScanRate");
			jMenuItemAltScanRate.setMnemonic('f');
			jMenuItemAltScanRate.setText("Force Alt Scan...");
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	return jMenuItemAltScanRate;
}


/**
 * Return the JMenuAblement property value.
 * @return javax.swing.JMenu
 */
private javax.swing.JMenu getJMenuAbleDis() 
{
	if (jMenuAbleDis == null) {
		try {
			jMenuAbleDis = new javax.swing.JMenu();
			jMenuAbleDis.setName("jMenuAbleDis");
			jMenuAbleDis.setMnemonic('b');
			jMenuAbleDis.setText("Enable/Disable");
			jMenuAbleDis.add(getJRadioButtonMenuItemDisableDev());
			jMenuAbleDis.add(getJRadioButtonMenuItemEnableDev());
			jMenuAbleDis.add(getJRadioButtonMenuItemDisablePt());
			jMenuAbleDis.add(getJRadioButtonMenuItemEnbablePt());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	return jMenuAbleDis;
}
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPopupMenu getJPopupMenuManual() {
	if (ivjJPopupMenuManual == null) {
		try {
			ivjJPopupMenuManual = new javax.swing.JPopupMenu();
			ivjJPopupMenuManual.setName("JPopupMenuManual");
			ivjJPopupMenuManual.add(getJMenuPopUpAckAlarm());
			ivjJPopupMenuManual.add(getJMenuItemPopUpManualEntry());
			ivjJPopupMenuManual.add(getJMenuItemPopUpManualControl());
			ivjJPopupMenuManual.add(getJMenuTags());
			ivjJPopupMenuManual.add(getJMenuAbleDis());
			// user code begin {1}

			ivjJPopupMenuManual.add( getJMenuItemGraph() );
			ivjJPopupMenuManual.add( getJMenuItemAltScanRate() );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPopupMenuManual;
}

private JComboBox getJComboPopup()
{
    if( jComboBoxPopUp == null )
    {
        jComboBoxPopUp = new JComboBox();        
        jComboBoxPopUp.setBackground( getBackground() );
    }
        
    return jComboBoxPopUp;
}

private BasicComboPopup getComboPopUp() 
{
    if( comboPopup == null )
    {
        comboPopup = new BasicComboPopup( getJComboPopup() );
        comboPopup.setPopupSize( 100, 300 );
    }
    
    return comboPopup;
}

/**
 * Return the JRadioButtonMenuItemDisableDev property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getJRadioButtonMenuItemDisableDev() {
	if (ivjJRadioButtonMenuItemDisableDev == null) {
		try {
			ivjJRadioButtonMenuItemDisableDev = new javax.swing.JRadioButtonMenuItem();
			ivjJRadioButtonMenuItemDisableDev.setName("JRadioButtonMenuItemDisableDev");
			ivjJRadioButtonMenuItemDisableDev.setMnemonic('D');
			ivjJRadioButtonMenuItemDisableDev.setText("Disabled Entire Device");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonMenuItemDisableDev;
}
/**
 * Return the JRadioButtonMenuItemDisablePt property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getJRadioButtonMenuItemDisablePt() {
	if (ivjJRadioButtonMenuItemDisablePt == null) {
		try {
			ivjJRadioButtonMenuItemDisablePt = new javax.swing.JRadioButtonMenuItem();
			ivjJRadioButtonMenuItemDisablePt.setName("JRadioButtonMenuItemDisablePt");
			ivjJRadioButtonMenuItemDisablePt.setMnemonic('P');
			ivjJRadioButtonMenuItemDisablePt.setText("Disabled Point");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonMenuItemDisablePt;
}
/**
 * Return the JRadioButtonMenuItemEnableDev property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getJRadioButtonMenuItemEnableDev() {
	if (ivjJRadioButtonMenuItemEnableDev == null) {
		try {
			ivjJRadioButtonMenuItemEnableDev = new javax.swing.JRadioButtonMenuItem();
			ivjJRadioButtonMenuItemEnableDev.setName("JRadioButtonMenuItemEnableDev");
			ivjJRadioButtonMenuItemEnableDev.setMnemonic('E');
			ivjJRadioButtonMenuItemEnableDev.setText("Enabled Entire Device");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonMenuItemEnableDev;
}
/**
 * Return the JRadioButtonMenuItemEnbablePt property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getJRadioButtonMenuItemEnbablePt() {
	if (ivjJRadioButtonMenuItemEnbablePt == null) {
		try {
			ivjJRadioButtonMenuItemEnbablePt = new javax.swing.JRadioButtonMenuItem();
			ivjJRadioButtonMenuItemEnbablePt.setName("JRadioButtonMenuItemEnbablePt");
			ivjJRadioButtonMenuItemEnbablePt.setMnemonic('t');
			ivjJRadioButtonMenuItemEnbablePt.setText("Enabled Point");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonMenuItemEnbablePt;
}
/**
 * Return the JRadioButtonMenuItemInhibitDev property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemInhibitPoint() 
{
	if( jMenuItemInhibitPoint == null )
	{
		try 
		{
			jMenuItemInhibitPoint = new javax.swing.JMenuItem();
			jMenuItemInhibitPoint.setName("jMenuItemInhibitPoint");
			jMenuItemInhibitPoint.setMnemonic('i');
			jMenuItemInhibitPoint.setText("Control on Point: NO");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}

	return jMenuItemInhibitPoint;
}

/**
 * Return the jMenuItemInhibitDevice property value.
 * @return javax.swing.JMenuItem
 */
private javax.swing.JMenuItem getJMenuItemInhibitDevice() 
{
	if( jMenuItemInhibitDevice == null) 
	{
		try 
		{
			jMenuItemInhibitDevice= new javax.swing.JMenuItem();
			jMenuItemInhibitDevice.setName("jMenuItemInhibitDevice");
			jMenuItemInhibitDevice.setMnemonic('l');
			jMenuItemInhibitDevice.setText("Control on Device: NO");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return jMenuItemInhibitDevice;
}


/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButtonMenuItem getJRadioButtonPage1() {
	if (ivjJRadioButtonPage1 == null) {
		try {
			ivjJRadioButtonPage1 = new javax.swing.JRadioButtonMenuItem();
			ivjJRadioButtonPage1.setName("JRadioButtonPage1");
			ivjJRadioButtonPage1.setSelected(false);
			ivjJRadioButtonPage1.setText("1");
			ivjJRadioButtonPage1.setActionCommand("1");
			// user code begin {1}

			getButtonGroupPage().add( ivjJRadioButtonPage1 );
			ivjJRadioButtonPage1.setSelected(true);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonPage1;
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 1:27:51 PM)
 * @return com.cannontech.tdc.Display
 */
private Display[] getLastDisplays() {
	return lastDisplays;
}
/**
 * Insert the method's description here.
 * Creation date: (3/24/00 1:11:30 PM)
 */
private void getHistoryDisplayData( Date date ) 
{
	java.awt.Frame owner = CtiUtilities.getParentFrame(this);
	java.awt.Cursor original = owner.getCursor();	
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	try
	{
		ViewCreator vc = new ViewCreator( getTableDataModel() );
		

		if( getCurrentDisplay().getDisplayNumber() == Display.RAW_POINT_HISTORY_VIEWER_DISPLAY_NUMBER ) 
		{
			totalPages = vc.createRowsForRawPointHistoryView( date, pageNumber );
		}
		else if( getCurrentDisplay().getDisplayNumber() == Display.EVENT_VIEWER_DISPLAY_NUMBER )
		{		
			totalPages = vc.createRowsForHistoricalView( date, pageNumber );
		}
		else if( getCurrentDisplay().getDisplayNumber() == Display.SOE_LOG_DISPLAY_NUMBER )
		{		
			totalPages = vc.createRowsForSOELogView( date, pageNumber );
		}
		else if( getCurrentDisplay().getDisplayNumber() == Display.TAG_LOG_DISPLAY_NUMBER )
		{		
			totalPages = vc.createRowsForTAGLogView( date, pageNumber );
		}
		else if( Display.isAlarmDisplay(getCurrentDisplay().getDisplayNumber()) )
		{		
			totalPages = vc.createRowsForHistoricalAlarmView( 
						date,
						pageNumber,
						getCurrentDisplay().getDisplayNumber() );
		}
				

		//see if we need to add paging capability here
		if( Display.isHistoryDisplay(getCurrentDisplay().getDisplayNumber()) )
		{
			//add all the page number JRadioButtons
			for( int i = 1; i <= totalPages; i++ )
			{
				//javax.swing.JRadioButtonMenuItem jRadioButton = new javax.swing.JRadioButtonMenuItem();
                JLabel pgeLabel = new JLabel()
                {
                    public String toString()
                    {
                        return getText();
                    }
                };
                
                pgeLabel.setName("JRadioButtonPage" + i);
                pgeLabel.setText( "Page " + i );
                pgeLabel.putClientProperty( "TDCPage", new Integer(i) );

				getJComboPopup().addItem( pgeLabel );
			}
		}

	}
	finally
	{
		owner.setCursor( original );
	}
}
/**
 * Return the DisplayTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getScrollPaneDisplayTable() {
	if (ivjScrollPaneDisplayTable == null) {
		try {
			ivjScrollPaneDisplayTable = new javax.swing.JScrollPane();
			ivjScrollPaneDisplayTable.setName("ScrollPaneDisplayTable");
			ivjScrollPaneDisplayTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjScrollPaneDisplayTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjScrollPaneDisplayTable.setBackground(java.awt.Color.white);
			ivjScrollPaneDisplayTable.setForeground(java.awt.Color.lightGray);
			getScrollPaneDisplayTable().setViewportView(getDisplayTable());
			// user code begin {1}			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScrollPaneDisplayTable;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 4:59:38 PM)
 * @return java.awt.GridBagConstraints
 */
// These constraints should match the ScrollPaneConstraints in the
//   initialize() method
private java.awt.GridBagConstraints getScrollPaneGridBagConstraints() 
{
	java.awt.GridBagConstraints constraintsScrollPaneDisplayTable = new java.awt.GridBagConstraints();	
	constraintsScrollPaneDisplayTable.gridx = 1; constraintsScrollPaneDisplayTable.gridy = 3;
	constraintsScrollPaneDisplayTable.gridwidth = 4;
	constraintsScrollPaneDisplayTable.fill = java.awt.GridBagConstraints.BOTH;
	constraintsScrollPaneDisplayTable.weightx = 1.0;
	constraintsScrollPaneDisplayTable.weighty = 1.0;
	constraintsScrollPaneDisplayTable.ipadx = 562;
	constraintsScrollPaneDisplayTable.ipady = 300;
	constraintsScrollPaneDisplayTable.insets = new java.awt.Insets(3, 4, 4, 4);
		
	return constraintsScrollPaneDisplayTable;
}
/**
 * Return the ScrollPaneTable property value.
 * @return com.cannontech.tdc.DisplayTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public Display2WayDataAdapter getTableDataModel() {
	if (dbAdaptor == null) {
		try {
			dbAdaptor= new com.cannontech.tdc.Display2WayDataAdapter();
	
			// user code begin {1}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	};
	return dbAdaptor;
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 11:06:44 AM)
 * @return com.cannontech.tdc.TDCClient
 */
public TDCClient getTdcClient() {
	return tdcClient;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 10:56:12 AM)
 * Version: <version>
 * @param rowNumber int
 */
public java.util.ArrayList getViewableRowNumbers() 
{
	int rowHeight = getDisplayTable().getRowHeight();
	double startY = getDisplayTable().getVisibleRect().getMinY();
	double distanceY = getDisplayTable().getVisibleRect().getMaxY() - 
					   getDisplayTable().getVisibleRect().getMinY();

	int rowsPresent = (int)distanceY / rowHeight;
	java.util.ArrayList rows = new java.util.ArrayList(rowsPresent);

	for( int i = 0; i < rowsPresent; i++, startY += rowHeight )
	{
		java.awt.Point loc = new java.awt.Point(0, (int)startY );
		rows.add( new Integer(getDisplayTable().rowAtPoint( loc )) );
	}	

	// returns all the pointids in the ViewPort of the JTable
	return rows;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION in TDCMainPanel() ---------");
	CTILogger.error( exception.getMessage(), exception );;
	
	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Insert the method's description here.
 * Creation date: (4/27/00 3:06:28 PM)
 * Version: <version>
 * @return boolean
 */
public boolean hasUserDefinedViews() 
{
	if( getAllDisplays() == null || getAllDisplays().length <= 0 )
		return false;

	for( int i = 0; i < getAllDisplays().length; i++ )
		if( getAllDisplays()[i].getDisplayNumber() >= Display.PRECANNED_USER_DISPLAY_NUMBER )
			return true;
	
	return false; 
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/00 11:48:39 AM)
 */
private void initClientDisplays() 
{
	setMiddlePanelVisible( false );

	// make sure the last non-special display is registered for NOTHING
	if( getTdcClient() != null )
		getTdcClient().reRegisterForNothing();
	
	// just in case we are switching from one special display to another
	resetMainPanel();
	
	Class theClass = null;

	try
	{
		theClass = Class.forName( getCurrentDisplay().getDescription() );
		setCurrentSpecailChild( (SpecialTDCChild)theClass.newInstance() );

		synchronized( getCurrentSpecailChild() )
		{
			//init the new special child right away!
			getCurrentSpecailChild().initChild();
			
			JPanel p = getCurrentSpecailChild().getMainJPanel();
			
			add(p, getScrollPaneGridBagConstraints() );
			frameAlarmToolBar.setCurrentComponents( getCurrentSpecailChild().getJButtons() );
			getCurrentSpecailChild().setTableFont( getDisplayTable().getFont() );
			getCurrentSpecailChild().setGridLines( getDisplayTable().getShowHorizontalLines(), 
											  getDisplayTable().getShowVerticalLines() );
			
			getCurrentSpecailChild().setInitialTitle();
			getJLabelDisplayName().setText( getCurrentSpecailChild().getJComboLabel() );

			getCurrentSpecailChild().setAlarmMute( getTableDataModel().isMuted() );
			

			// add the new ActionListener to our combo box
			getCurrentSpecailChild().addActionListenerToJComponent( getJComboCurrentDisplay() );
			
			getJLabelDisplayTitle().setText(getCurrentDisplay().getTitle());			
		}

		
		/************** Set the JTable row colors here ********************************/
		String query2 = new String
				("select s.foregroundcolor, s.backgroundcolor from state s, stategroup st "  +
				" where st.name = ? and s.rawstate>= 0 " +
				" and st.stategroupid=s.stategroupid order by s.rawstate");
		Object[] objs = new Object[1];
		objs[0] = getCurrentDisplay().getName();
		Object[][] st2 = DataBaseInteraction.queryResults( query2, objs );

		if( st2.length > 0 )
		{
			java.awt.Color[] rowColors = new java.awt.Color[ st2.length ];
			java.awt.Color bgColor = null;
			for( int i = 0; i < st2.length; i++ )
			{
				bgColor = Colors.getColor( Integer.parseInt(st2[i][1].toString()) );
				rowColors[i] = Colors.getColor( Integer.parseInt(st2[i][0].toString()) );
			}

			getCurrentSpecailChild().setRowColors( rowColors, bgColor );
		}
		/************** End of row colors setting *******************************************/
	}
	catch( Exception excep )
	{  // many exceptions can occur here, handle them all
		handleException( excep );
	}

}
/**
 * This method was created in VisualAge.
 */
public boolean initComboCurrentDisplay() 
{
	writeAllDisplayColumnData();


	// clear all the displays from memory
	allDisplays = null;

   //reinit our display number
	getTableDataModel().setCurrentDisplay( Display.UNKNOWN_DISPLAY );

	
	// get the data for the majority of our displays
	String query = new String(
		"select name, displaynum, title, type, description from display " +
 		"order by name, type");

	Object[][] values = DataBaseInteraction.queryResults( query, null );
	
	Object[][] alarmValues = getAlarmStatesCache();
	java.util.ArrayList clientList = new java.util.ArrayList(10);
	
	if ( values.length > 0 )
	{

		// allocate some space for the displays to be stored in
		allDisplays = new Display[values.length];

		getJComboCurrentDisplay().removeActionListener(this);
		if( getJComboCurrentDisplay().getItemCount() > 0 )
			getJComboCurrentDisplay().removeAllItems();
		      
		// add all the displays in the database
		int alarmIndex = 0;
		for( int i = 0; i < values.length; i++ )
		{
			long displayNumber = Long.parseLong(values[i][1].toString());
			
			// substitute the data in the alarmState table in for the priority alarms for or displays
			if( displayNumber > Display.GLOBAL_ALARM_DISPLAY && displayNumber <= Display.LAST_ALARM_DISPLAY )
				values[i] = alarmValues[alarmIndex++];   // this is a HACK because the original data in the Display table is not respected.
																		  // I did things this way because the DisplayColumns table has a MANDATORY PARENT constraint with the Display tables.
																		  // Thus, we couldn't have DisplayCoulmns that did not belong to a display.
			initDisplays( values, i );


			if( getCurrentDisplay() != null
				 && getCurrentDisplay().getType().equalsIgnoreCase(getAllDisplays()[i].getType()) )
			{
				getJComboCurrentDisplay().addItem( getAllDisplays()[i] );
			}
		}

		readAllDisplayColumnData(); //get all the display column data for each display

		getJComboCurrentDisplay().addActionListener(this);
		getJComboCurrentDisplay().revalidate();
		getJComboCurrentDisplay().repaint();
		
		return true;
	}

	return true; // no displays were found in the display table
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getJRadioButtonPage1().addActionListener( this );
	MouseListener listener = new PopUpMenuShower( getJPopupMenuManual() );
	getDisplayTable().addMouseListener( listener );


	getJLabelDisplayTitle().addMouseListener(new MouseAdapter()
    {
        public void mouseReleased(MouseEvent e)
        {
            //if( e.isPopupTrigger() )
            getComboPopUp().show( getJLabelDisplayTitle(), e.getX(), e.getY());
        }
    });
    
    getJComboPopup().addActionListener( this );

	setTableHeaderListener();
	
	getJMenuItemInhibitDevice().addActionListener( this );	
	getJMenuItemGraph().addActionListener( this );
	getJMenuItemAltScanRate().addActionListener( this );
	getJMenuItemCreateTag().addActionListener(this);

	// user code end
	getDisplayTable().addMouseListener(this);
	getJComboCurrentDisplay().addActionListener(this);
	getJPopupMenuManual().addPopupMenuListener(this);
	getJMenuItemPopUpManualEntry().addActionListener(this);
	getJLabelDisplayTitle().addMouseListener(this);
	getJRadioButtonMenuItemDisableDev().addItemListener(this);
	getJRadioButtonMenuItemEnableDev().addItemListener(this);
	getJRadioButtonMenuItemDisablePt().addItemListener(this);
	getJRadioButtonMenuItemEnbablePt().addItemListener(this);
	getJMenuItemPageForward().addActionListener(this);
	getJMenuItemPageBack().addActionListener(this);
	getJMenuItemPopUpManualControl().addActionListener(this);
}

/**
 * Insert the method's description here.
 * Creation date: (5/31/00 2:29:27 PM)
 * Version: <version>
 */
private void initDisplays( Object[][] query, int index ) 
{
	Display display = new Display();
	
	display.setName( query[index][0].toString() );
	display.setDisplayNumber( Integer.parseInt(query[index][1].toString()) );
	
	if ( query[index][2] == null )
		display.setTitle(" ");
	else
		display.setTitle( query[index][2].toString() );
		
	display.setType( query[index][3].toString() );
	display.setDescription( query[index][4].toString() );
	
		
			
	getAllDisplays()[index] = display;

}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
      
      		
		// user code end
		setName("TDCMainPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(592, 389);

		java.awt.GridBagConstraints constraintsJLabelDisplayName = new java.awt.GridBagConstraints();
		constraintsJLabelDisplayName.gridx = 1; constraintsJLabelDisplayName.gridy = 1;
		constraintsJLabelDisplayName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelDisplayName.ipadx = 4;
		constraintsJLabelDisplayName.ipady = 3;
		constraintsJLabelDisplayName.insets = new java.awt.Insets(7, 5, 5, 1);
		add(getJLabelDisplayName(), constraintsJLabelDisplayName);

		java.awt.GridBagConstraints constraintsJLabelDate = new java.awt.GridBagConstraints();
		constraintsJLabelDate.gridx = 4; constraintsJLabelDate.gridy = 1;
		constraintsJLabelDate.anchor = java.awt.GridBagConstraints.NORTHEAST;
		constraintsJLabelDate.ipadx = 60;
		constraintsJLabelDate.insets = new java.awt.Insets(5, 39, 0, 4);
		add(getJLabelDate(), constraintsJLabelDate);

		java.awt.GridBagConstraints constraintsJLabelTime = new java.awt.GridBagConstraints();
		constraintsJLabelTime.gridx = 4; constraintsJLabelTime.gridy = 2;
		constraintsJLabelTime.anchor = java.awt.GridBagConstraints.NORTHEAST;
		constraintsJLabelTime.ipadx = 17;
		constraintsJLabelTime.insets = new java.awt.Insets(1, 82, 5, 4);
		add(getJLabelTime(), constraintsJLabelTime);

		java.awt.GridBagConstraints constraintsJComboCurrentDisplay = new java.awt.GridBagConstraints();
		constraintsJComboCurrentDisplay.gridx = 2; constraintsJComboCurrentDisplay.gridy = 1;
		constraintsJComboCurrentDisplay.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboCurrentDisplay.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboCurrentDisplay.ipadx = 19;
		constraintsJComboCurrentDisplay.insets = new java.awt.Insets(5, 1, 4, 3);
		add(getJComboCurrentDisplay(), constraintsJComboCurrentDisplay);

		java.awt.GridBagConstraints constraintsScrollPaneDisplayTable = new java.awt.GridBagConstraints();
		constraintsScrollPaneDisplayTable.gridx = 1; constraintsScrollPaneDisplayTable.gridy = 3;
		constraintsScrollPaneDisplayTable.gridwidth = 4;
		constraintsScrollPaneDisplayTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsScrollPaneDisplayTable.weightx = 1.0;
		constraintsScrollPaneDisplayTable.weighty = 1.0;
		constraintsScrollPaneDisplayTable.ipadx = 562;
		constraintsScrollPaneDisplayTable.ipady = 300;
		constraintsScrollPaneDisplayTable.insets = new java.awt.Insets(3, 4, 4, 4);
		add(getScrollPaneDisplayTable(), constraintsScrollPaneDisplayTable);

		java.awt.GridBagConstraints constraintsJLabelDisplayTitle = new java.awt.GridBagConstraints();
		constraintsJLabelDisplayTitle.gridx = 3; constraintsJLabelDisplayTitle.gridy = 2;
		constraintsJLabelDisplayTitle.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelDisplayTitle.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsJLabelDisplayTitle.weightx = 1.0;
		constraintsJLabelDisplayTitle.ipadx = 91;
		constraintsJLabelDisplayTitle.insets = new java.awt.Insets(1, 3, 2, 38);
		add(getJLabelDisplayTitle(), constraintsJLabelDisplayTitle);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	dbChangeHandler = new TDCDBChangeHandler( this );
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/00 4:22:10 PM)
 */
private void initializeParameters()
{
	TDCMainFrame parentFrame = 
			((TDCMainFrame)CtiUtilities.getParentFrame( this ));
			
	java.awt.Font newFont = null;
	ParametersFile pf = new ParametersFile( CtiUtilities.OUTPUT_FILE_NAME );

	try
	{
		parentFrame.setBounds( 
							Integer.parseInt(pf.getParameterValue("FrameX", "150")),
							Integer.parseInt(pf.getParameterValue("FrameY", "200")),
							Integer.parseInt(pf.getParameterValue("FrameWidth", "650")),
							Integer.parseInt(pf.getParameterValue("FrameHeight", "400")) );
		
		newFont = new java.awt.Font( pf.getParameterValue("FontName", "Arial"),
									 java.awt.Font.PLAIN,
									 Integer.parseInt(pf.getParameterValue("FontSize", "14")) );

		parentFrame.getJCheckBoxMenuItemHGridLines().setState(
				Boolean.valueOf(pf.getParameterValue("HGridLine", "false")).booleanValue() );
		
		parentFrame.getJCheckBoxMenuItemVGridLines().setState(
				Boolean.valueOf(pf.getParameterValue("VGridLine", "false")).booleanValue() );
				
		parentFrame.getJCheckBoxMenuItemShowLog().setState(
				Boolean.valueOf(pf.getParameterValue("MessageLog", "false")).booleanValue() );
		
		parentFrame.getJCheckBoxMenuItemShowToolBar().setState(
				Boolean.valueOf(pf.getParameterValue("ToolBox", "true")).booleanValue() );


		//should we Mute the alarms?
		if( Boolean.valueOf(pf.getParameterValue("Mute", "false")).booleanValue() )
			parentFrame.alarmToolBar_JToolBarButtonMuteAlarmsAction_actionPerformed( null );


		setStartUpDisplay( pf, parentFrame );
		
		//TDCMainFrame.messageLog.addMessage("Parameters file found and parsed successfully", MessageBoxFrame.INFORMATION_MSG );
	}
	catch( Exception e ) 
	{
		// init file not found
		if( parentFrame.startingDisplayName != null )
		{
			if( parentFrame.startingViewType != null )
				parentFrame.setSelectedViewType( parentFrame.startingViewType );
			
			getJComboCurrentDisplay().setSelectedIndex( getDisplayIndexByJComboValue( parentFrame.startingDisplayName ) );
			setUpTable();
		}
		else
		{
			try
			{
				getJComboCurrentDisplay().setSelectedIndex(0); // just set the DisplayCombo to the first display				
				setUpTable();
			}
			catch( Exception ex )
			{
				CTILogger.error( ex.getMessage(), ex );
				CTILogger.info("*****************************************************");
				CTILogger.info("*** Most likely cause is an invalid database.  ******");
				CTILogger.info("*****************************************************");
			}
		}

		TDCMainFrame.messageLog.addMessage("Parameters file " + CtiUtilities.OUTPUT_FILE_NAME + " not found or corrupt", MessageBoxFrame.INFORMATION_MSG );
	}
		

	if( newFont != null )
		setTableFont( newFont );
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/20/00 4:22:10 PM)
 */
public void initializeTable() 
{
	// check to see if any rows are in the display table
	if ( (connectedToDB = initComboCurrentDisplay()) == true )
	{		
		Display.setDISPLAY_TITLES( getAllDisplays() );

		for( int i = 0; i < getAllDisplays().length; i++ )
		{
			// add the radio buttons for the display types to the mainframe
			if( getAllDisplays()[i].getDisplayNumber() <= Display.BEGINNING_CLIENT_DISPLAY_NUMBER )
			{
				boolean enabled = true;
            
				if( getAllDisplays()[i].getType().equalsIgnoreCase(Display.DISPLAY_TYPES[Display.CAP_CONTROL_CLIENT_TYPE_INDEX]) )
					enabled = !TDCDefines.isHiddenCapControl(TDCDefines.USER_RIGHTS);
				else if( getAllDisplays()[i].getType().equalsIgnoreCase(Display.DISPLAY_TYPES[Display.LOAD_CONTROL_CLIENT_TYPE_INDEX]) )
					enabled = !TDCDefines.isHiddenLoadControl(TDCDefines.USER_RIGHTS);
				else if( getAllDisplays()[i].getType().equalsIgnoreCase(Display.DISPLAY_TYPES[Display.SCHEDULER_CLIENT_TYPE_INDEX]) )
					enabled = !TDCDefines.isHiddenMACS(TDCDefines.USER_RIGHTS);
				else if( getAllDisplays()[i].getType().equalsIgnoreCase(Display.DISPLAY_TYPES[Display.STATIC_CLIENT_TYPE_INDEX]) )
					enabled = TDCDefines.isClientEnabled(TDCDefines.USER_RIGHTS);

				addClientRadioButtons( getAllDisplays()[i].getTitle(), i, enabled );
			}
 
        
			if( getCurrentDisplay() == null 
				 && Display.isReadOnlyDisplay(getAllDisplays()[i].getDisplayNumber()) )
			{
				// we must not have a currentDisplay set yet, set it to the EVENT VIEWER
				setCurrentDisplay( getAllDisplays()[i] );
				getJComboCurrentDisplay().addItem( getAllDisplays()[i] );
			}
			
		}

		initializeParameters();
		
		initialStart = false;
	}

}
/**
 * Insert the method's description here.
 * Creation date: (3/23/00 5:33:39 PM)
 */
private void initCoreDisplays() 
{
	// add in todays data
	Date newDate = getPreviousDate();

	if( Display.isHistoryDisplay(getCurrentDisplay().getDisplayNumber()) 
		 || getCurrentDisplay().getTdcFilter().getConditions().get(ITDCFilter.COND_HISTORY) )
	{			
		getHistoryDisplayData( newDate );
	}

	if( Display.isTodaysDate(newDate) )
	{
		//we are looking at today
		setDisplayTitle( getCurrentDisplay().getName(), null );
	}
	else
	{
		setDisplayTitle( getCurrentDisplay().getName(), newDate );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/8/00 12:44:36 PM)
 * @return boolean
 */
public boolean isClientDisplay() 
{

	return ( !Display.isCoreType(getCurrentDisplay().getType()) 
		 		&& !Display.isUserDefinedType(getCurrentDisplay().getType()) 
		 		&& getCurrentDisplay() != null 
		 		&& getCurrentSpecailChild() != null );
}

/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJRadioButtonMenuItemDisableDev()) 
		connEtoC12(e);
	if (e.getSource() == getJRadioButtonMenuItemEnableDev()) 
		connEtoC13(e);
	if (e.getSource() == getJRadioButtonMenuItemDisablePt()) 
		connEtoC14(e);
	if (e.getSource() == getJRadioButtonMenuItemEnbablePt()) 
		connEtoC15(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jLabelDisplayTitle_MousePressed(java.awt.event.MouseEvent mouseEvent) 
{
	// JUST SOMETHING TO UNSELECT ROWS
	// DOESNT WORK?????????????
	getDisplayTable().clearSelection();
	getDisplayTable().repaint();

	if( getDisplayTable().getSelectedRow() >= 0 )
	{
		PointValues point = getTableDataModel().getPointValue( getDisplayTable().getSelectedRow() );
		CTILogger.info("CLCIKSD for ptID = "+ point.getPointID() + " Time = "+ point.getTimeStamp() + " tags = " + Long.toHexString(point.getTags()) );
	}
	
/*  // a test to force a point into an alarming state
	com.cannontech.message.dispatch.message.Signal s = new com.cannontech.message.dispatch.message.Signal();
	s.setId(3);
	s.setPriority( 4 );
	
	getTableDataModel().setRowAlarmed( s );
*/
	return;
}
/**
 * Comment
 */
public void jMenuItemPageBack_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( totalPages > 1 )
	{
		javax.swing.JRadioButtonMenuItem previousButton = null;
		java.util.Enumeration enum = getButtonGroupPage().getElements();
		java.util.ArrayList buttonList = new java.util.ArrayList(10);

		while( enum.hasMoreElements() )
			buttonList.add( enum.nextElement() );
	
		for( int i = 0; i < buttonList.size(); i++ )
		{
			if( ((javax.swing.JRadioButtonMenuItem)buttonList.get(i)).isSelected() )
			{
				if( i == 0 )
					previousButton = (javax.swing.JRadioButtonMenuItem)buttonList.get( buttonList.size()-1 );
				else
					previousButton = (javax.swing.JRadioButtonMenuItem)buttonList.get( i-1 );

				break;
			}

		}
		

		previousButton.doClick();
	}


	
/*	java.awt.Frame owner = CtiUtilities.getParentFrame(this);		
	Cursor savedCursor = owner.getCursor();
	owner.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );

	try
	{
		getTableDataModel().clearSystemViewerDisplay( false );
		
		if( pageNumber <= 1 )
			pageNumber = pagesRemaining + 1;

		if( getCurrentDisplayNumber() == Display.HISTORY_EVENT_VIEWER_DISPLAY_NUMBER )
			pagesRemaining = getTableDataModel().createRowsForHistoricalView( 
							previousDate, 
							--pageNumber );
		else if( getCurrentDisplayNumber() == Display.RAW_POINT_HISTORY_VIEWER_DISPLAY_NUMBER )
			pagesRemaining = getTableDataModel().createRowsForRawPointHistoryView( 
							previousDate, 
							--pageNumber );
		
		
		setDisplayTitle(getCurrentDisplay().getName(), true, true);
	}
	finally
	{
		owner.setCursor( savedCursor );
	}
	
	return;*/
}


public void jMenuItemGraph_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	PointValues pv = getTableDataModel().getPointValue( getDisplayTable().getSelectedRow() );
	
	GregorianCalendar prev30 = new GregorianCalendar();
	prev30.setTime( new Date() );
	prev30.set( GregorianCalendar.DAY_OF_YEAR, prev30.get(GregorianCalendar.DAY_OF_YEAR) - 30 );
	
	GregorianCalendar currDayPls1 = new GregorianCalendar();
	currDayPls1.setTime( new Date() );
	currDayPls1.set( GregorianCalendar.DAY_OF_YEAR, currDayPls1.get(GregorianCalendar.DAY_OF_YEAR) + 1 );
	
	//Create a default freechart to use.  It will be refreshed with our data in a moment.
	org.jfree.chart.JFreeChart freeChart = org.jfree.chart.ChartFactory.createTimeSeriesChart(
				"TDC Trending", "Date/Time", "Value", new org.jfree.data.time.TimeSeriesCollection(), true, true, true);
				
	freeChart.setBackgroundPaint(java.awt.Color.white);

	com.cannontech.jfreechart.chart.YukonChartPanel
		freeChartPanel = new com.cannontech.jfreechart.chart.YukonChartPanel(freeChart);
	
	//disable right click popup menu (it is not fully functionall as of jfreechart-0.9.12.jar)
	freeChartPanel.setPopupMenu(null);
	freeChartPanel.setVisible(true);
	// turn all zoom on
	freeChartPanel.setHorizontalZoom(true);
	freeChartPanel.setVerticalZoom(true);
	
	TrendModel model = new TrendModel(
			prev30.getTime(),
			currDayPls1.getTime(),
			"30 Day Trend Snapshot for " + pv.getPointName(),
			new int[] { pv.getPointID() },
			new String[] { pv.getPointName() }) ;
	
	model.setTrendProps( new com.cannontech.graph.model.TrendProperties(false));//do not use saved TrendProperties from .dat file.
	freeChartPanel.setChart( model.refresh() );
	
	JFrame jd = new JFrame(
		pv.getPointName() + " - 30 Day Trend Snapshot");
	
	jd.setIconImage( CtiUtilities.getParentFrame(this).getIconImage() );
	jd.setResizable( true );
	jd.setSize( 600, 430 );
	jd.setLocation( this.getLocationOnScreen() );
	
	jd.setContentPane( freeChartPanel );

	jd.show();
}

public void jMenuItemAltScanRate_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	RowEditorDialog d = new RowEditorDialog( 
		CtiUtilities.getParentFrame(this) ); 

	int selectedRow = getDisplayTable().getSelectedRow();
	PointValues pt = getTableDataModel().getPointValue( selectedRow );

	AltScanRatePanel panel =
		new AltScanRatePanel(
			new EditorDialogData(pt, pt.getAllText()) );


	// should be put in its own method
	java.awt.GridBagConstraints constraintsPanel = new java.awt.GridBagConstraints();
	constraintsPanel.gridx = 0; constraintsPanel.gridy = 0;
	constraintsPanel.fill = java.awt.GridBagConstraints.BOTH;
	constraintsPanel.anchor = java.awt.GridBagConstraints.NORTH;
	constraintsPanel.ipadx = 0; constraintsPanel.ipady = 0;
	constraintsPanel.insets = new java.awt.Insets(0, 0, 0, 0);
	constraintsPanel.weightx = 1.0; constraintsPanel.weighty = 1.0;

	d.getContentPane().add(panel, constraintsPanel);
	d.setResizable( false );
	d.pack();
	d.setTitle( panel.getPanelTitle() );
	
	d.addRowEditorDialogListener( panel );
	
	d.setLocationRelativeTo( this );		
	d.setModal( true );		
	d.show();
}

/**
 * Comment
 */
public void jMenuItemPageForward_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( totalPages > 1 )
	{
		javax.swing.JRadioButtonMenuItem nextButton = null;
		java.util.Enumeration enum = getButtonGroupPage().getElements();

		while( enum.hasMoreElements() )
		{
			if( ((javax.swing.JRadioButtonMenuItem)enum.nextElement()).isSelected() )
			{
				if( enum.hasMoreElements() )
					nextButton = (javax.swing.JRadioButtonMenuItem)enum.nextElement();
				else
					nextButton = getJRadioButtonPage1();

				break;
			}	
		}
		

		nextButton.doClick();
	}
	
/*	java.awt.Frame owner = CtiUtilities.getParentFrame(this);		
	Cursor savedCursor = owner.getCursor();
	owner.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );

	try
	{
		getTableDataModel().clearSystemViewerDisplay( false );
			
		if( pageNumber >= pagesRemaining )
			pageNumber = 0;
			
		if( getCurrentDisplayNumber() == Display.HISTORY_EVENT_VIEWER_DISPLAY_NUMBER )
			pagesRemaining = getTableDataModel().createRowsForHistoricalView( 
							previousDate, 
							++pageNumber );
		else if( getCurrentDisplayNumber() == Display.RAW_POINT_HISTORY_VIEWER_DISPLAY_NUMBER )
			pagesRemaining = getTableDataModel().createRowsForRawPointHistoryView( 
							previousDate, 
							++pageNumber );

		setDisplayTitle(getCurrentDisplay().getName(), true, true);
	}
	finally
	{
		owner.setCursor( savedCursor );
	}

	return;*/
} 
/**
 * Comment
 */
public void jMenuItemPopUpAckAlarm_ActionPerformed(java.awt.event.ActionEvent ae ) 
{
	if( ae.getSource() instanceof JMenuItem )
	{
		JMenuItem item = (JMenuItem)ae.getSource();

		Object obj = item.getClientProperty( "com.cannontech.tdc.AlarmValues" );
		if( obj instanceof PointValues )
		{
			PointValues ptValues = (PointValues)obj;
			
			AckAlarm.sendAckAll( ptValues.getPointID() );
			
			TDCMainFrame.messageLog.addMessage(
					"An ACK ALARM message was sent for ALL ALARMS on pointid " + ptValues.getPointID(), MessageBoxFrame.INFORMATION_MSG );
		}
		else if( obj instanceof Signal )
		{
			Signal sig = (Signal)obj;
			TDCMainFrame.messageLog.addMessage(
					"An ACK ALARM message was sent for pointid " + sig.getPointID(), MessageBoxFrame.INFORMATION_MSG );
	
			AckAlarm.send( sig.getPointID(), sig.getCondition() );
		}

	}

	return;
}
/**
 * Comment
 *
public void jMenuItemPopUpClear_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int pointid = (int)getTableDataModel().getPointID( getDisplayTable().getSelectedRow() );

	if( pointid >= 0 )
	{
		ClearAlarm.send( pointid );
	}
		
	return;
}*/
/**
 * Comment
 */
public void jMenuItemPopUpDisable_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	// Create a signal here to send
	com.cannontech.message.dispatch.message.Signal sig = new com.cannontech.message.dispatch.message.Signal();	
	int selectedRow = getDisplayTable().getSelectedRow();
	PointValues ptValue = getTableDataModel().getPointValue(selectedRow);
	String msg = new String();

	// see if we need to disable the points service
	if( TagUtils.isPointOutOfService(ptValue.getTags()) )
	{
		msg = "ENABLE";
		sig.setTags( ptValue.getTags() & ~com.cannontech.message.dispatch.message.Signal.TAG_DISABLE_POINT_BY_POINT );
	}
	else
	{
		msg = "DISABLE";
		sig.setTags( ptValue.getTags() | com.cannontech.message.dispatch.message.Signal.TAG_DISABLE_POINT_BY_POINT );
	}
						
	sig.setPointID( ptValue.getPointID() );
	
	sig.setDescription("Point control change occured from TDC on point: " + 
		ptValue.getDeviceName().toString() + " / " + ptValue.getPointName()); //who
	
	sig.setAction("A " + msg + " control point event was executed");
	sig.setCategoryID( com.cannontech.message.dispatch.message.Signal.EVENT_SIGNAL );
	sig.setUserName(CtiUtilities.getUserName());
	sig.setTimeStamp( new Date() );
	
	SendData.getInstance().sendSignal( sig );
	
	return;
}
/**
 * Comment
 */
public void jMenuItemPopUpManualControl_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getTdcClient().connected() )
	{
		showRowEditor( actionEvent.getSource() );
	}
	else
		javax.swing.JOptionPane.showMessageDialog( 
					getDisplayTable(),
					"Manual point controls are only allowed when connected to the server.",
					"Not Connected",
					javax.swing.JOptionPane.WARNING_MESSAGE );

	return;
}
/**
 * Comment
 */
public void jMenuItemPopUpManualEntry_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getTdcClient().connected() )
	{
		showRowEditor( actionEvent.getSource() );
	}
	else
		javax.swing.JOptionPane.showMessageDialog( 
					getDisplayTable(),
					"Manual changes are only allowed when connected to the server.",
					"Not Connected",
					javax.swing.JOptionPane.WARNING_MESSAGE );
	
	return;
}

private JMenuItem createAckAlarmJMenuItem( String text, Color bg, Object alarmValue_ )
{
	JMenuItem it = new JMenuItem( text );
	
	if( bg != null )
		it.setBackground( bg );

	//store the PointValues object in the JMenuItem so we can access the alarms if the
	// user ACKs an alarm
	it.putClientProperty( "com.cannontech.tdc.AlarmValues", alarmValue_ );

	it.addActionListener( new ActionListener()
	{
		public void actionPerformed( ActionEvent ae )
		{
			jMenuItemPopUpAckAlarm_ActionPerformed( ae );
		}
	});
	
	return it;
}

/**
 * Comment
 */
public void jPopupMenu_PopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent menuEvent)
{
	if( getDisplayTable().getSelectedRow() >= 0 
		 && getTableDataModel().getPointID(getDisplayTable().getSelectedRow()) != TDCDefines.ROW_BREAK_ID
		 && !Display.isReadOnlyDisplay(getCurrentDisplay().getDisplayNumber()) )
	{
		int selectedRow = getDisplayTable().getSelectedRow();
			
		//init our ACK ALARM JMenu
		getJMenuPopUpAckAlarm().removeAll();
		PointValues pv = getTableDataModel().getPointValue( selectedRow );
		
		Signal[] sigs = pv.getAllSignals();
		for( int i = 0; i < sigs.length; i++ )
		{
			if( TagUtils.isAlarmUnacked(sigs[i].getTags()) )
			{
				LitePoint lPoint = PointFuncs.getLitePoint( pv.getPointID() );
				
				//if there is more than one alarm, add the all alarms ack to the top
				if( i == 1 )
				{
					JMenuItem it = createAckAlarmJMenuItem("(ACK ALL)", null, pv );
					getJMenuPopUpAckAlarm().add( it, 0 );					
				}

				JMenuItem it = createAckAlarmJMenuItem(
					"Condition: " +
					AlarmUtils.getAlarmConditionText( 
						sigs[i].getCondition(), lPoint.getPointType(), lPoint.getPointID() ) +
					" (" + sigs[i].getDescription() + ")",
					Colors.getColor(getTableDataModel().getAlarmColor((int)sigs[i].getCategoryID()) ),
					sigs[i] );
								
	
				getJMenuPopUpAckAlarm().add( it );
			}
		}
		
		getJMenuPopUpAckAlarm().setEnabled( getJMenuPopUpAckAlarm().getItemCount() > 0 );
				

		setAblementPopUpItems( selectedRow );
		
		addTagMenuItems( selectedRow );

		getJMenuItemAltScanRate().setEnabled( 
			!Display.isCoreType(getCurrentDisplay().getType())
			&& DeviceTypesFuncs.hasDeviceScanRate(
				PAOGroups.getDeviceType(pv.getDeviceType()) ) );		


		// check to see if the point can be controlled AND its control is NOT disabled
		getJMenuItemPopUpManualControl().setEnabled(
				!Display.isCoreType(getCurrentDisplay().getType()) 
				&& 
				(TagUtils.isControllablePoint(
					getTableDataModel().getPointValue(selectedRow).getTags())
				&&
				TagUtils.isControlEnabled(
					getTableDataModel().getPointValue(selectedRow).getTags())) );

		getJMenuTags().setEnabled( !Display.isCoreType(getCurrentDisplay().getType()) );
		getJMenuItemPopUpManualEntry().setEnabled( !Display.isCoreType(getCurrentDisplay().getType()) );
		getJMenuItemGraph().setEnabled( !Display.isCoreType(getCurrentDisplay().getType()) );
		getJMenuAbleDis().setEnabled( !Display.isCoreType(getCurrentDisplay().getType()) );

	}
	else
	{
		// set all the popupmenuItems disabled
		for( int i = 0; i < getJPopupMenuManual().getSubElements().length; i++ )
			getJPopupMenuManual().getSubElements()[i].getComponent().setEnabled( false );
	}
	
	return;
}

private void jMenutItemTag_Modify( ActionEvent e )
{
	RowEditorDialog d = new RowEditorDialog( 
		CtiUtilities.getParentFrame(this) ); 

	int selectedRow = getDisplayTable().getSelectedRow();
	PointValues pt = getTableDataModel().getPointValue( selectedRow );

	//tells us what row we have selected
	Integer rowNum =(Integer)
		((JComponent)e.getSource()).getClientProperty( "tdc_tag_row" );


	TagsEditorPanel panel =
		new TagsEditorPanel( 
			new EditorDialogData(pt, pt.getAllText() ),
			rowNum );

	d.setSize(680, 450);
	panel.setPreferredSize( new Dimension(
			(int)(d.getWidth()  * .95),
			(int)(d.getHeight() * .9) ) );

	// should be put in its own method
	java.awt.GridBagConstraints cPanel = new java.awt.GridBagConstraints();
	cPanel.gridx = 0; cPanel.gridy = 0;
	cPanel.fill = java.awt.GridBagConstraints.BOTH;
	cPanel.anchor = java.awt.GridBagConstraints.CENTER;
	cPanel.ipadx = 0; cPanel.ipady = 0;
	cPanel.weightx = 1.0; cPanel.weighty = 1.0;
	cPanel.insets = new java.awt.Insets(0, 0, 0, 0);
	d.getContentPane().add(panel, cPanel);
	

	d.pack();
	d.setResizable( true );

	d.setTitle( panel.getPanelTitle() );
	d.setUpdateButtonVisible( false );
	d.setCancelButtonText("Close");
	
	d.addRowEditorDialogListener( panel );
	
	d.setLocationRelativeTo( this );		
	d.setModal( true );

	getTdcClient().addMessageListener( panel );
	d.show();
	getTdcClient().removeMessageListener( panel );
}


private void addTagMenuItems( int selRow )
{
	//remove everything up to the first seperator
	Component[] comps = getJMenuTags().getMenuComponents();
	for( int i = (comps.length-1); i >=0; i-- )
		if( comps[i] instanceof javax.swing.JSeparator )
			break;
		else
			getJMenuTags().remove( comps[i] );
	
	
	Set tags = SendData.getInstance().getTagMgr().getTags(
			(int)getTableDataModel().getPointID(selRow) );
	
	int i = 0;
	Iterator it = tags.iterator();
	while( it.hasNext() )
	{
		Tag aTag = (Tag)it.next();
		LiteTag liteTag = TagFuncs.getLiteTag( aTag.getTagID() );

		JMenuItem mi = new JMenuItem(
				liteTag.getTagName() + " (Level: " + liteTag.getTagLevel() + ")" );

		//tells us what row we have selected
		mi.putClientProperty( "tdc_tag_row", new Integer(i++) );

		//mi.setForeground( Colors.getColor(liteTag.getColorID()) );
		if( liteTag.getImageID() > CtiUtilities.NONE_ID )
			mi.setIcon( 
				new ImageIcon(java.awt.Toolkit.getDefaultToolkit().createImage(
					YukonImageFuncs.getLiteYukonImage(liteTag.getImageID()).getImageValue()) ) );

		//be sure any click on a specific tag takes the user to the
		// tag editor
		mi.addActionListener( new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				jMenutItemTag_Modify( e );
			}
			
			
		});		
		
		getJMenuTags().add( mi );
	}

}

/**
 * Comment
 * 
 * @deprecated use new tagging system
 */
public void jRadioButtonMenuItemAllowPt_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int selectedRow = getDisplayTable().getSelectedRow();
	int pointID = (int)getTableDataModel().getPointValue(selectedRow).getPointID();

	// build up our opArgList for our command	message
	Vector data = new Vector(4);
	data.addElement( new Integer(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN) );  // this is the ClientRegistrationToken	
	data.addElement( new Integer(Command.ABLEMENT_POINT_IDTYPE) );
	data.addElement( new Integer(pointID) );
	data.addElement( new Integer(Command.ABLEMENT_ENABLE) );

	// create our command message
	Command cmd = new Command();
	cmd.setOperation( Command.CONTROL_ABLEMENT );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new Date() );

	// write the command message to the server
	SendData.getInstance().sendCommandMsg( cmd );

	return;
}
/**
 * Comment
 */
public void jRadioButtonMenuItemDisableDev_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{
	PointValues ptVal = getTableDataModel().getPointValue(
										getDisplayTable().getSelectedRow() );
	
	if( ptVal != null )
	{
		// build up our opArgList for our command	message
		Vector data = new Vector(4);
		data.addElement( new Integer(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN) );  // this is the ClientRegistrationToken	
		data.addElement( new Integer(Command.ABLEMENT_DEVICE_IDTYPE) );
		data.addElement( new Integer(ptVal.getDeviceID()) );
		data.addElement( new Integer(Command.ABLEMENT_DISABLE) );
	
		// create our command message
		Command cmd = new Command();
		cmd.setOperation( Command.ABLEMENT_TOGGLE );
		cmd.setOpArgList( data );
		cmd.setTimeStamp( new Date() );
	
		// write the command message to the server
		SendData.getInstance().sendCommandMsg( cmd );
	}
	else
	{
		//opps, for some reason the selectedRow does not have a PointValues for it??
		CTILogger.error( "The selected row number (" + getDisplayTable().getSelectedRow() +
			") does not have a valid point row value for it, please try operation again");
	}
		
	return;
}
/**
 * Comment
 */
public void jRadioButtonMenuItemDisablePt_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{
	int selectedRow = getDisplayTable().getSelectedRow();
	int pointID = (int)getTableDataModel().getPointValue(selectedRow).getPointID();

	// build up our opArgList for our command	message
	Vector data = new Vector(4);
	data.addElement( new Integer(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN) );  // this is the ClientRegistrationToken	
	data.addElement( new Integer(Command.ABLEMENT_POINT_IDTYPE) );
	data.addElement( new Integer(pointID) );
	data.addElement( new Integer(Command.ABLEMENT_DISABLE) );

	// create our command message
	Command cmd = new Command();
	cmd.setOperation( Command.ABLEMENT_TOGGLE );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new Date() );

	// write the command message to the server
	SendData.getInstance().sendCommandMsg( cmd );
	
	return;
}
/**
 * Comment
 */
public void jRadioButtonMenuItemEnableDev_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{
	int selectedRow = getDisplayTable().getSelectedRow();
	int deviceID = getTableDataModel().getPointValue(selectedRow).getDeviceID();

	// build up our opArgList for our command	message
	Vector data = new Vector(4);
	data.addElement( new Integer(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN) );  // this is the ClientRegistrationToken	
	data.addElement( new Integer(Command.ABLEMENT_DEVICE_IDTYPE) );
	data.addElement( new Integer(deviceID) );
	data.addElement( new Integer(Command.ABLEMENT_ENABLE) );

	// create our command message
	Command cmd = new Command();
	cmd.setOperation( Command.ABLEMENT_TOGGLE );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new Date() );

	// write the command message to the server
	SendData.getInstance().sendCommandMsg( cmd );

	return;
}
/**
 * Comment
 */
public void jRadioButtonMenuItemEnbablePt_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{
	int selectedRow = getDisplayTable().getSelectedRow();
	int pointID = (int)getTableDataModel().getPointValue(selectedRow).getPointID();

	// build up our opArgList for our command	message
	Vector data = new Vector(4);
	data.addElement( new Integer(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN) );  // this is the ClientRegistrationToken	
	data.addElement( new Integer(Command.ABLEMENT_POINT_IDTYPE) );
	data.addElement( new Integer(pointID) );
	data.addElement( new Integer(Command.ABLEMENT_ENABLE) );

	// create our command message
	Command cmd = new Command();
	cmd.setOperation( Command.ABLEMENT_TOGGLE );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new Date() );

	// write the command message to the server
	SendData.getInstance().sendCommandMsg( cmd );
	
	return;
}
/**
 * Comment
 * 
 * */
public void jMenuItemInhibitDev_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int selectedRow = getDisplayTable().getSelectedRow();
	int deviceID = getTableDataModel().getPointValue(selectedRow).getDeviceID();

	//assumes inhibiting
	String cntrlStr = "INHIBIT";
	int cmdInhib = Command.ABLEMENT_DISABLE;
	
	if( ((javax.swing.JMenuItem)
			actionEvent.getSource()).getText().equals("Control on Device: NO") )
	{
		cntrlStr = "ALLOW";
		cmdInhib = Command.ABLEMENT_ENABLE;
	}

	int res = javax.swing.JOptionPane.showConfirmDialog(
					this,
					"Are you sure you want to " + cntrlStr +
					" control for all points attached to '" +
						getTableDataModel().getPointValue(selectedRow).getDeviceName() + "'?",
					cntrlStr + " Control on Device",
					javax.swing.JOptionPane.OK_CANCEL_OPTION );

	if( res == javax.swing.JOptionPane.OK_OPTION )
	{	
		// build up our opArgList for our command	message
		Vector data = new Vector(4);
		data.addElement( new Integer(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN) );  // this is the ClientRegistrationToken
		data.addElement( new Integer(Command.ABLEMENT_DEVICE_IDTYPE) );
		data.addElement( new Integer(deviceID) );
		data.addElement( new Integer(cmdInhib) );

	
		// create our command message
		Command cmd = new Command();
		cmd.setOperation( Command.CONTROL_ABLEMENT );
		cmd.setOpArgList( data );
		cmd.setTimeStamp( new Date() );
	
		// write the command message to the server
		SendData.getInstance().sendCommandMsg( cmd );
	}

}

/**
 * Comment
 */
public void jMenuItemCreateTag_ActionPerformed(java.awt.event.ActionEvent source) 
{
	RowEditorDialog d = new RowEditorDialog( 
		CtiUtilities.getParentFrame(this) ); 

	int selectedRow = getDisplayTable().getSelectedRow();
	PointValues pt = getTableDataModel().getPointValue( selectedRow );

	TagWizardPanel panel =
		new TagWizardPanel(
			new EditorDialogData(pt, pt.getAllText()),
			false );


	// should be put in its own method
	java.awt.GridBagConstraints constraintsPanel = new java.awt.GridBagConstraints();
	constraintsPanel.gridx = 0; constraintsPanel.gridy = 0;
	constraintsPanel.fill = java.awt.GridBagConstraints.BOTH;
	constraintsPanel.anchor = java.awt.GridBagConstraints.NORTH;
	constraintsPanel.ipadx = 0; constraintsPanel.ipady = 0;
	constraintsPanel.insets = new java.awt.Insets(0, 0, 0, 0);
	constraintsPanel.weightx = 1.0; constraintsPanel.weighty = 1.0;

	d.getContentPane().add(panel, constraintsPanel);
	
	d.pack();
	d.setResizable( true );
	d.setTitle( panel.getPanelTitle() );
	d.setUpdateButtonText( "Create" );
	
	d.addRowEditorDialogListener( panel );
	
	d.setLocationRelativeTo( this );		
	d.setModal( true );		
	d.show();
}

/**
 * Comment
 * 
 * @deprecated use new tagging system
 */
public void jRadioButtonMenuItemInhibitPt_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int selectedRow = getDisplayTable().getSelectedRow();
	int pointID = (int)getTableDataModel().getPointValue(selectedRow).getPointID();

	// build up our opArgList for our command	message
	Vector data = new Vector(4);
	data.addElement( new Integer(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN) );  // this is the ClientRegistrationToken	
	data.addElement( new Integer(Command.ABLEMENT_POINT_IDTYPE) );
	data.addElement( new Integer(pointID) );
	data.addElement( new Integer(Command.ABLEMENT_DISABLE) );

	// create our command message
	Command cmd = new Command();
	cmd.setOperation( Command.CONTROL_ABLEMENT );
	cmd.setOpArgList( data );
	cmd.setTimeStamp( new Date() );

	// write the command message to the server
	SendData.getInstance().sendCommandMsg( cmd );

	return;
}
/**
 * COMMENT
 *
 **/
public void jComboPopupPage_ActionPerformed()
{
    java.awt.Frame owner = CtiUtilities.getParentFrame(this);       
    Cursor savedCursor = owner.getCursor();

	try
	{
        if( !(getJComboPopup().getSelectedItem() instanceof JLabel)
            || ((JLabel)getJComboPopup().getSelectedItem()).getClientProperty("TDCPage") == null )
            return;
            
        owner.setCursor( new Cursor( Cursor.WAIT_CURSOR ) );
        
		if( Display.isHistoryDisplay(getCurrentDisplay().getDisplayNumber()) )
		{
			getTableDataModel().clearSystemViewerDisplay( false );

			pageNumber = ((Integer)((JLabel)getJComboPopup().getSelectedItem()).getClientProperty("TDCPage")).intValue();
			
			if( pageNumber <= 1 )
				pageNumber = totalPages + 1;

			if( pageNumber > totalPages )
				pageNumber = 1;

			//Always add 1 milleseconds less than 1 day (86399999L) to see the current day
			final int MILLI_OFFSET = 86399000;
			
			
			//use to do the view creations
			ViewCreator vc = new ViewCreator( getTableDataModel() );

			if( getCurrentDisplay().getDisplayNumber() == Display.EVENT_VIEWER_DISPLAY_NUMBER )
			{
				totalPages = vc.createRowsForHistoricalView( 
								new Date(getPreviousDate().getTime() + MILLI_OFFSET),
								pageNumber );
			}
			else if( getCurrentDisplay().getDisplayNumber() == Display.RAW_POINT_HISTORY_VIEWER_DISPLAY_NUMBER )
			{
				totalPages = vc.createRowsForRawPointHistoryView( 
								new Date(getPreviousDate().getTime() + MILLI_OFFSET),
								pageNumber );
			}
			else if( getCurrentDisplay().getDisplayNumber() == Display.SOE_LOG_DISPLAY_NUMBER )
			{		
				totalPages = vc.createRowsForSOELogView( 
						new Date(getPreviousDate().getTime() + MILLI_OFFSET),
						pageNumber );
			}			
			else if( getCurrentDisplay().getDisplayNumber() == Display.TAG_LOG_DISPLAY_NUMBER )
			{		
				totalPages = vc.createRowsForTAGLogView( 
						new Date(getPreviousDate().getTime() + MILLI_OFFSET),
						pageNumber );
			}
			else if( Display.isAlarmDisplay(getCurrentDisplay().getDisplayNumber()) )
			{
				totalPages = vc.createRowsForHistoricalAlarmView( 
								new Date(getPreviousDate().getTime() + MILLI_OFFSET),
								pageNumber,
								getCurrentDisplay().getDisplayNumber() );
			}
			
			
			setDisplayTitle(getCurrentDisplay().getName(), getPreviousDate() );
		}
	}
	catch( Exception e)
	{
		CTILogger.info("*** Exception caught in : jComboPopupPage_ActionPerformed(ActionEvent) in class : " + this.getClass().getName() );
      CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		owner.setCursor( savedCursor );
        getComboPopUp().hide();
	}

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TDCMainPanel aTDCMainPanel;
		aTDCMainPanel = new TDCMainPanel();
		frame.setContentPane(aTDCMainPanel);
		frame.setSize(aTDCMainPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getDisplayTable()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getDisplayTable()) 
		connEtoC1(e);
	if (e.getSource() == getJLabelDisplayTitle()) 
		connEtoC10(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the PopupMenuListener interface.
 * @param e javax.swing.event.PopupMenuEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the PopupMenuListener interface.
 * @param e javax.swing.event.PopupMenuEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the PopupMenuListener interface.
 * @param e javax.swing.event.PopupMenuEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJPopupMenuManual()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}

public void executeRefresh_Pressed()
{
	// set refresh to true so it doesnt ask for a date on certain displays
	refreshPressed( true );

	try
	{
		TDCMainFrame.messageLog.addMessage("Refresh pressed for the display named " + 
						getCurrentDisplay().getName(), MessageBoxFrame.INFORMATION_MSG);
		
		if( getCurrentSpecailChild() != null )
		{
			getCurrentSpecailChild().executeRefreshButton();
		}		
		else
		{
			//setUpMainFrame( getJComboCurrentDisplay().getSelectedItem() );
			Object previousItem = getJComboCurrentDisplay().getSelectedItem();
			//initComboCurrentDisplay();
		
			//select the last item if we have one
			if( previousItem != null )
			{
				getJComboCurrentDisplay().setSelectedItem( previousItem );
			}

		}
		
			
	}
	finally
	{
		// must do this no matter what
		refreshPressed( false );
	}
	
}

/**
 * Insert the method's description here.
 * Creation date: (1/21/00 11:46:00 AM)
 */
protected void processDBChangeMsg( DBChangeMsg msg )
{		
	try
	{
		dbChangeHandler.processDBChangeMsg( msg );
	}
	catch( Exception e )
	{
		CTILogger.error( "Problem with handling a DBChange message, refreshing entire display", e );
		fireJComboCurrentDisplayAction_actionPerformed( 
					new java.util.EventObject( this ) );		
	}
	
	//refresh the ALL models data
	getTableDataModel().fireTableRowsUpdated( 
				0,
				getTableDataModel().getRowCount() );
}

/**
 * Insert the method's description here.
 * Creation date: (3/6/00 1:51:30 PM)
 * Before this method is executed, we should have all of our
 *  displays read in.
 */
private HashMap readAllDisplayColumnData() 
{
	HashMap map = null;

	try
	{
		java.io.File file = new java.io.File( TDCDefines.DISPLAY_OUT_FILE_NAME );

		if( file.exists() )
		{	
			java.io.ObjectInputStream in = new java.io.ObjectInputStream(
								new java.io.FileInputStream( file ) );
			

			Object o = null;
			try
			{
				while( (o = in.readObject()) != null )
				{
					if( o instanceof HashMap )
					{
						map = (HashMap)o;
												

						for( int j = 0; j < getAllDisplays().length; j++ )
						{
							DisplayData dd = (DisplayData)map.get(
								new Integer(getAllDisplays()[j].getDisplayNumber()) );

							if( dd != null )
							{
								getAllDisplays()[j].setDisplayData( dd );
							}
						}

							
					}
				}
			}
			catch( java.io.EOFException eof )
			{  /*this will not be an error*/ }
			
			in.close();
		}

	}
	catch ( java.io.IOException e )
	{
		handleException( e );
	}
	catch ( ClassNotFoundException e )
	{
		handleException( e );
	}
	
	return map;
}

/**
 * Insert the method's description here.
 * Creation date: (5/4/00 3:00:51 PM)
 * Version: <version>
 */
public void refreshPressed( boolean value ) 
{
	refreshPressed = value;	
}
/**
 * 
 * @param newListener com.cannontech.tdc.TDCMainPanelListener
 */
public void removeTDCMainPanelListener(com.cannontech.tdc.TDCMainPanelListener newListener) {
	fieldTDCMainPanelListenerEventMulticaster = com.cannontech.tdc.TDCMainPanelListenerEventMulticaster.remove(fieldTDCMainPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (9/8/00 4:55:09 PM)
 */
protected void resetMainPanel() 
{
	// reset the unfound points list
	getTableDataModel().setLimboPointsValue(null);

	// We need to set the original middle panel to visible since we
	// are not looking at a Special Display
	if(getCurrentSpecailChild() != null)
	{
		getCurrentSpecailChild().removeActionListenerFromJComponent( getJComboCurrentDisplay() );		
		getCurrentSpecailChild().getMainJPanel().setVisible( false );
		frameAlarmToolBar.setOriginalButtons();
		getJLabelDisplayName().setText("View");


		getCurrentSpecailChild().destroy();
	}
	
	setCurrentSpecailChild( null );
}
/**
 * Insert the method's description here.
 * Creation date: (8/11/00 12:03:48 PM)
 */
private void resetPagingProperties() 
{
	totalPages = 1;
	pageNumber = 1;
	
    getJComboPopup().removeAllItems();
}

/**
 * Insert the method's description here.
 * Creation date: (1/21/00 11:46:00 AM)
 */
public void resetTable() 
{
	getTableDataModel().reset();	
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 2:21:50 PM)
 */
public void resizeTable() 
{
	getScrollPaneDisplayTable().setSize( getScrollPaneDisplayTable().getWidth() -1, getScrollPaneDisplayTable().getHeight() );
	getScrollPaneDisplayTable().invalidate();
	getScrollPaneDisplayTable().validate();
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2001 9:26:55 AM)
 * @param rowNumber int
 */
public void scrollTableToRow(int rowNumber) 
{
	//scroll to a location that makes the new selected row appear
	getDisplayTable().scrollRectToVisible( new java.awt.Rectangle(
		0,
		getDisplayTable().getRowHeight() * (rowNumber+1),  //just an estimated that works!!
		100,
		100) );	
}
/**
 * Insert the method's description here.
 */
 
private void setAblementPopUpItems( int selectedRow ) 
{
	// remove the item listeners so we can init the values
	// without activating the associated events
	getJRadioButtonMenuItemDisableDev().removeItemListener(this);
	getJRadioButtonMenuItemEnableDev().removeItemListener(this);
	getJRadioButtonMenuItemDisablePt().removeItemListener(this);
	getJRadioButtonMenuItemEnbablePt().removeItemListener(this);

	
	long tags = getTableDataModel().getPointValue( selectedRow ).getTags();
	
	boolean isPointOutOfService = TagUtils.isPointOutOfService(tags);
	getJRadioButtonMenuItemDisablePt().setSelected( isPointOutOfService );
	getJRadioButtonMenuItemEnbablePt().setSelected( !isPointOutOfService );
	
	boolean isDeviceOutOfService = TagUtils.isDeviceOutOfService(tags);
	getJRadioButtonMenuItemDisableDev().setSelected( isDeviceOutOfService );
	getJRadioButtonMenuItemEnableDev().setSelected( !isDeviceOutOfService );


	boolean isDeviceControlInhibited = TagUtils.isDeviceControlInhibited(tags);
	getJMenuItemInhibitDevice().setText( 
		(isDeviceControlInhibited ? "Control on Device: NO" : "Control on Device: YES") );
	
	boolean isPointControlInhibited = TagUtils.isPointControlInhibited(tags);
	getJMenuItemInhibitPoint().setText(
		(isPointControlInhibited ? "Control on Point: NO" : "Control on Point: YES") );



	//if the point is not controllable, then disable the control options for that point
	boolean isControllablePoint = TagUtils.isControllablePoint(tags);
	if( !isControllablePoint )
		getJMenuItemInhibitPoint().setText("Point not controllable");

//	getJRadioButtonMenuItemInhibitPt().setEnabled( isControllablePoint );
//	getJRadioButtonMenuItemAllowPt().setEnabled( isControllablePoint );

	
	// add the item listeners back
	getJRadioButtonMenuItemDisableDev().addItemListener(this);
	getJRadioButtonMenuItemEnableDev().addItemListener(this);
	getJRadioButtonMenuItemDisablePt().addItemListener(this);
	getJRadioButtonMenuItemEnbablePt().addItemListener(this);
}

/**
 * Insert the method's description here.
 * Creation date: (1/21/00 11:46:00 AM)
 */
private void setColumnWidths() 
{

/*	for( int i = 0; i < getDisplayTable().getColumnCount(); i++ )
	{
		getDisplayTable().getColumnModel().getColumn( i )
			.setWidth( getTableDataModel().columnWidth[ i ].intValue() );

		getDisplayTable().getColumnModel().getColumn( i )
			.setPreferredWidth( getTableDataModel().columnWidth[ i ].intValue() );
	}
	
	getDisplayTable().revalidate();
	getDisplayTable().repaint();
*/
}
/**
 * Insert the method's description here.
 * Creation date: (10/3/00 2:57:02 PM)
 * @param newCurrentDisplay com.cannontech.tdc.Display
 */
public void setCurrentDisplay(Display newCurrentDisplay) {
	currentDisplay = newCurrentDisplay;
}
/**
 * Insert the method's description here.
 * Creation date: (12/12/00 11:19:33 AM)
 * @param newCurrentSpecailChild com.cannontech.tdc.SpecialTDCChild
 */
protected void setCurrentSpecailChild(SpecialTDCChild newCurrentSpecailChild) {
	currentSpecailChild = newCurrentSpecailChild;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 10:01:30 AM)
 */
public Cursor setCursorToWait() 
{
	java.awt.Frame owner = CtiUtilities.getParentFrame(this);
	Cursor original = owner.getCursor();	
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	return original;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/00 1:45:11 PM)
 * @param title java.lang.String
 */
private void setDisplayTitle(String title, Date date ) 
{
	if( date != null )
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime( date );

		if( (getCurrentDisplay().getTdcFilter().getConditions().isEmpty() 
		      && !Display.isAlarmDisplay(getCurrentDisplay().getDisplayNumber()) )
			 || getCurrentDisplay().getTdcFilter().getConditions().get(ITDCFilter.COND_HISTORY) )
		{
	      title += " (" + 
	      		CommonUtils.formatMonthString( calendar.get( calendar.MONTH ) ) + " " +
	            calendar.get( calendar.DAY_OF_MONTH ) + ", " +
	            calendar.get( calendar.YEAR ) + ")";
		}
	}


	if( totalPages > 1
		 && Display.isHistoryDisplay(getCurrentDisplay().getDisplayNumber()) )
	{
		title += " (" + pageNumber + " of " + totalPages + ")";		
	}
	
	

		
	final String titleString = new String( title );	
	javax.swing.SwingUtilities.invokeLater( new Runnable()
		{	public void run()
			{
				getJLabelDisplayTitle().setText( titleString );
			}
		}			
	);	
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 1:27:51 PM)
 * @param newLastDisplay com.cannontech.tdc.Display
 */
private void setLastDisplays( int index, Display newLastDisplay) 
{
	if( index >= 0 && index < Display.DISPLAY_TYPES.length )
		lastDisplays[index] = newLastDisplay;
}
/**
 * Insert the method's description here.
 * Creation date: (8/3/00 3:16:35 PM)
 */
private void setMiddlePanelVisible( boolean toggle )
{
	getDisplayTable().setVisible( toggle );
	getScrollPaneDisplayTable().setVisible( toggle );

}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 10:56:12 AM)
 * Version: <version>
 * @param rowNumber int
 */
private void setRowFocus(int rowNumber) 
{
	if( !getViewableRowNumbers().contains( new Integer(rowNumber) ) )	
		getScrollPaneDisplayTable().getViewport().setViewPosition( 
			new java.awt.Point( 0, getDisplayTable().getRowHeight() * rowNumber ) );
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/00 10:56:22 AM)
 * Version: <version>
 * @param parameters com.cannontech.tdc.ParametersData
 */
private void setStartUpDisplay( ParametersFile pf, TDCMainFrame parentFrame ) 
{
	if( getJComboCurrentDisplay().getItemCount() > 0 )
	{
		if( parentFrame.startingDisplayName != null )
		{
			if( parentFrame.startingViewType != null )
				parentFrame.setSelectedViewType( parentFrame.startingViewType );

			getJComboCurrentDisplay().setSelectedIndex( getDisplayIndexByJComboValue( parentFrame.startingDisplayName ) );
			TDCMainFrame.messageLog.addMessage("Starting display name on start up found", MessageBoxFrame.INFORMATION_MSG );
		}
		else
		{
			parentFrame.setSelectedViewType( 
					pf.getParameterValue("ViewType", Display.DISPLAY_TYPES[0]) );

			String dispName = pf.getParameterValue("DisplayName", "");
			for( int i = 0; i < getJComboCurrentDisplay().getItemCount(); i++ )
				if( dispName.equalsIgnoreCase(getJComboCurrentDisplay().getItemAt(i).toString()) )
				{
					//be sure we do NOT update the column data!!!
					needColDataUpdate = false;
					getJComboCurrentDisplay().setSelectedIndex(i);					
					needColDataUpdate = true;
					break;
				}
		}

		// manually fire the event (no longer needed in JRE 1.4)
//		if( getJComboCurrentDisplay().getSelectedIndex() == 0 )
//			fireJComboCurrentDisplayAction_actionPerformed( new java.util.EventObject( this ) );

		getParent().invalidate();
		getParent().repaint();

	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/2/00 2:21:50 PM)
 */
public void setTableFont( java.awt.Font newFont ) 
{
	
	getDisplayTable().setFont( newFont );
	getDisplayTable().setRowHeight( newFont.getSize() + 2 );

	// set the table headers font
	getDisplayTable().getTableHeader().setFont( newFont );


	//set any clients font to the initialized font, this is important
	// to do here if start up begins with a client display.
	if( getCurrentSpecailChild() != null )
		getCurrentSpecailChild().setTableFont( getDisplayTable().getFont() );
		
	getDisplayTable().revalidate();
	getDisplayTable().repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (6/9/00 10:11:11 AM)
 */
private void setTableHeaderListener() 
{
	javax.swing.table.JTableHeader hdr = ((javax.swing.table.JTableHeader)getDisplayTable().getTableHeader());
	hdr.setToolTipText("Dbl Click on a column header to sort");

	// The actual listener is defined here
	hdr.addMouseListener( new java.awt.event.MouseAdapter() 
	{
		public void mouseClicked(java.awt.event.MouseEvent e)
		{
			if( e.getClickCount() == 2 )
			{
				int vc = getDisplayTable().getColumnModel().getColumnIndexAtX( e.getX() );
				int mc = getDisplayTable().convertColumnIndexToModel( vc );

				java.awt.Frame owner = CtiUtilities.getParentFrame( TDCMainPanel.this );
				
				Cursor original = owner.getCursor();	
				owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
				
				try
				{
					sorterModelWrapper.sort( mc );				
					getDisplayTable().repaint();
				}
				finally
				{
					owner.setCursor( original );
				}
				
			}
			
		};
		
	});	
	
}
/**
 * Insert the method's description here.
 * Creation date: (1/18/2001 11:06:44 AM)
 * @param newTdcClient com.cannontech.tdc.TDCClient
 */
public void setTdcClient(TDCClient newTdcClient) {
	tdcClient = newTdcClient;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/00 5:56:37 PM)
 * @param toolBar javax.swing.JToolBar
 */
public void setToolBar(javax.swing.JToolBar toolBar) 
{
	if( toolBar instanceof AlarmToolBar )
		frameAlarmToolBar = (AlarmToolBar)toolBar;
	
}
/**
 * This method was created in VisualAge.
 */
public void setUpTable()
{
	if (getJComboCurrentDisplay().getSelectedItem() != null)
	{
		java.awt.Frame owner = CtiUtilities.getParentFrame(this);
		Cursor original = owner.getCursor();
		owner.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
		
		try
		{
			setMiddlePanelVisible( true );

			resetMainPanel();
			
			if( Display.isCoreType(getCurrentDisplay().getType()) )
			{
				// Set up the column names and Display Title
				String displayName = getJComboCurrentDisplay().getSelectedItem().toString();
				int displayNum = (int)getCurrentDisplay().getDisplayNumber();

				if( displayNum == Display.UNKNOWN_DISPLAY_NUMBER )
				{
					TDCMainFrame.messageLog.addMessage("Unknown Core Display Number for '" + displayName + "' found.", MessageBoxFrame.ERROR_MSG );
					getJLabelDisplayTitle().setText("UNKNOWN CORE DISPLAY");
				}
				else
					setDisplayTitle( displayName, null );

				getTableDataModel().setCurrentDisplay( getCurrentDisplay() );
				getTableDataModel().makeTable();				
				setColumnWidths();
			}
			else
			{
				// Set up the column names and Display Title
				setDisplayTitle( getCurrentDisplay().getTitle(), null );
				getTableDataModel().setCurrentDisplay( getCurrentDisplay() );
				getTableDataModel().makeTable();
				setColumnWidths();				
			}
			
			resizeTable();
		}
		finally
		{
			owner.setCursor(original);
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
	com.cannontech.debug.gui.ObjectInfoDialog d = new com.cannontech.debug.gui.ObjectInfoDialog(
		CtiUtilities.getParentFrame(this) ); 

	d.setLocation( this.getLocationOnScreen() );
	d.showDialog( 
		getTableDataModel().getPointValue( getDisplayTable().getSelectedRow() ) );
}
/**
 * Insert the method's description here.
 * Creation date: (4/5/00 4:29:59 PM)
 * Version: <version>
 * @param selectedRow int
 *
 * called by a ManualEntry, ManualControl or a Dbl click on the JTable
 */
private void showRowEditor( Object source ) 
{
	
	RowEditorDialog d = new RowEditorDialog( 
		CtiUtilities.getParentFrame(this) ); 
	
	int selectedRow = getDisplayTable().getSelectedRow();
	
	ManualEntryJPanel panel = createManualEditorPanel( selectedRow, source );
		
	if( panel != null )
	{
		// should be put in its own method
		java.awt.GridBagConstraints constraintsPanel = new java.awt.GridBagConstraints();
		constraintsPanel.gridx = 0; constraintsPanel.gridy = 0;
		constraintsPanel.anchor = java.awt.GridBagConstraints.NORTH;
		constraintsPanel.ipadx = 0; constraintsPanel.ipady = 0;
		constraintsPanel.insets = new java.awt.Insets(0, 0, 0, 0);
		constraintsPanel.weightx = 1.0; constraintsPanel.weighty = 1.0;
		
		d.getContentPane().add(panel, constraintsPanel);
		d.pack();
		d.setTitle( panel.getPanelTitle() );
		
		if( panel instanceof StatusPanelManualEntry )
		{
			d.addRowEditorDialogListener( (StatusPanelManualEntry)panel );
		}
		else if( panel instanceof AnalogPanel )
		{
			d.addRowEditorDialogListener( (AnalogPanel)panel );
		}
		else if( panel instanceof StatusPanelControlEntry )
		{
			d.addRowEditorDialogListener( (StatusPanelControlEntry)panel );
			d.setUpdateButtonVisible( false );
		}
		
		d.setLocationRelativeTo( this );		
		d.setModal( true );
		d.setResizable( false );
		d.show();
	}
	
}

/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:45:40 AM)
 */
public void updateDisplayColumnData() 
{
	javax.swing.JTable table = ( isClientDisplay() 
				? getCurrentSpecailChild().getJTables()[0]
				: getDisplayTable() );

	//we do not want to update the display column data if we
	//  do not have a display
	if( getCurrentDisplay() != null )
	{
		ColumnData[] cols = new ColumnData[table.getColumnCount()];

		for( int i = 0; i < cols.length; i++ )
		{	
			TableColumn col = table.getColumnModel().getColumn(i);
				
			ColumnData cd = new ColumnData(
					col.getHeaderValue().toString(),
					i,
					col.getWidth() );

			cols[i] = cd;
		}

		DisplayData dd = new DisplayData(
				getCurrentDisplay().getDisplayNumber(),
				getCurrentDisplay().getTdcFilter().getFilterID(),
				cols );



		//set any display properties needed for TDC clients
		if( getCurrentSpecailChild() != null )
		{
			JPanel p = getCurrentSpecailChild().getMainJPanel();
			for( int i = 0; i < p.getComponentCount(); i++ )
			{
				if( p.getComponent(i) instanceof CompositeJSplitPane )
				{
					dd.setProp0(
						((CompositeJSplitPane)p.getComponent(i)).getDividerLocation() );

					dd.setProp1(
						((CompositeJSplitPane)p.getComponent(i)).getJSplitPaneInner().getDividerLocation() );
				}
			}
		}

		//update the current display and the display in allDisplays[]
		getCurrentDisplay().setDisplayData( dd );
		for( int i = 0; i < getAllDisplays().length; i++ )
			if( dd.getDisplayNumber() == getAllDisplays()[i].getDisplayNumber() )
			{
				getAllDisplays()[i].setDisplayData( dd );
				break;
			}
	}
	

}
/**
 * Insert the method's description here.
 * Creation date: (1/24/2002 11:45:40 AM)
 */
private void formatDisplayColumns()
{
	if( getCurrentDisplay() == null
		 || getCurrentDisplay().getDisplayData() == null )
		return;



	for( int i = 0; i < getCurrentDisplay().getDisplayData().getColumnData().length; i++ )
	{	
		ColumnData cd = getCurrentDisplay().getDisplayData().getColumnData()[i];

		javax.swing.JTable table = ( isClientDisplay() 
					? getCurrentSpecailChild().getJTables()[0]
					: getDisplayTable() );
			
		for( int j = 0; j < table.getColumnCount(); j++ )
		{
			if( table.getColumnName(j).equalsIgnoreCase(cd.getColumnName()) )
			{
				//found the column, lets reformat its appearance
				TableColumn tc = table.getColumnModel().getColumn(j);

				tc.setWidth( cd.getWidth() );
				tc.setPreferredWidth( cd.getWidth() );	

            //make sure the table has the column index!!
				table.moveColumn( j, 
               ( cd.getOrdering() >= table.getColumnCount()
                 ? table.getColumnCount() - 1
                 : cd.getOrdering() ) );
             
				break;
			}
			
		}
			
	}
	
	
	//set any display properties needed for TDC clients
	if( getCurrentSpecailChild() != null )
	{
		JPanel p = getCurrentSpecailChild().getMainJPanel();
		for( int i = 0; i < p.getComponentCount(); i++ )
		{
			if( p.getComponent(i) instanceof CompositeJSplitPane )
			{
				if( getCurrentDisplay().getDisplayData().getProp0() >= 0 )
					((CompositeJSplitPane)p.getComponent(i)).setDividerLocation(
							getCurrentDisplay().getDisplayData().getProp0() );
							
				if( getCurrentDisplay().getDisplayData().getProp1() >= 0 )
					((CompositeJSplitPane)p.getComponent(i)).getJSplitPaneInner().setDividerLocation(
							getCurrentDisplay().getDisplayData().getProp1() );
			}
		}
	}	


}

/**
 * Insert the method's description here.
 * Creation date: (3/6/00 1:51:30 PM)
 */
public void writeAllDisplayColumnData() 
{
	if( allDisplays == null )
		return;

	HashMap map = null;

	try
	{
		java.io.FileOutputStream fileOut = new java.io.FileOutputStream( 
				new java.io.File( TDCDefines.DISPLAY_OUT_FILE_NAME ) );
		
		java.io.ObjectOutputStream o = new java.io.ObjectOutputStream(fileOut);

		if( map == null )
			map = new HashMap( getAllDisplays().length );

		for( int i = 0; i < getAllDisplays().length; i++ )
		{
			map.put(
				new Integer(getAllDisplays()[i].getDisplayNumber()),
				getAllDisplays()[i].getDisplayData() );
		}
		
		o.writeObject( map );
		
		o.flush();
		o.close();
	}
	catch ( java.io.IOException e )
	{
		handleException( e );
	}
	
}

public Date getPreviousDate() 
{
	if( previousDate == null )
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime( new Date() );
		cal.set( cal.HOUR_OF_DAY, 0 );
		cal.set( cal.MINUTE, 0 );
		cal.set( cal.SECOND, 0 );
		cal.set( cal.MILLISECOND, 0 );

		previousDate = cal.getTime();
	}

	return previousDate;
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G7CF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8BF8D45535B0CA0420D403469A91B58AEA3846C2AD36DC459AA3AAAAD65ACBEBEC31173634CD6F253D74CAABDA6E75A40490B4C0A088D1F9C49E728AF910C01EC01E10C000108C98B0B619A4A74910494CE4668C19BC14FF6D475A674C19B3C9083F5F777FFC2E1C3357DEFB6D3557DEFB6D3D575EE714B6EE8FDD99933BD112E2D6CA7CDFEE0CA44DF0C95203012F9EC75CA8B9B2DE527C7B94E03D
	74F1CD9CBC03A02FA1AAB2BEC93AB3A38152D3A0DDBFAFB2FEB33CA7CB93B3422E400B1FFAC6C1DE4C544A7B872FA771BC56330A26737292613996A08CF06439D4A675576425F07CDC0EC708091524B9ED0426B3EF8B4765C13A86A0GA0240D51CF0767EADA73CD85F91C2EE1C228347465BD31EB889F8DCF0424F6D6B607BFD325E9B18B5AC9DD41D4CED2468A69A9G2471AB245ACD3B61D9D45359FE2C455E155EE236596CB6C5EE69C8D7DADB8A8ACC65E5E74C96DB0D59A6DB012E3E0370DA1017C04B82FF
	C8AF22388DB5047FA4F81F81B0D7606DC5843F579299DF8CD0EF41B69D5DD344E52E6F1FA6ADFF5D36E21D1FB6B9AC2CADA19635CD416EF7D7FDC4757FF9AD5173FC48DB8610F8A5B2BE932086408170896D77FF1E5F8F4F12E6254C5E55E53715B89C2556D66799FB2BEC05770282486138DA39456EEC15A456379373528C64998CD839306BAE47F492160D7D9CDE7B3034608727E3D79968E4C1D8E6EC6095260B50AB2ACED85D3F71DBF74495EFBB742E7BF3C9DB7762B17695AB8D6A1E7173FD3173B91FE8CD
	5D291D242F47D09BC80077982E8B063F1D62DBB5F8E613A994CF528310373093ED23FEAD4AD29DB7D1CA4A2AB654C3C8E4C9EC39154950E0554BB27AA8619DE8E5F56E91325CCF718BB5F8A60B1B5AA9C967F5C546AB3648F8E6DFCDA6B2C6275B4126819681AC875862080C4F8328F6200DBDF2BEE318B6D6E3F6AA25EE0B35D5F6CA2CECD36DA6F84A9E45E92E323994332DC53EE8F6DA4C4DD699FC9F233154929DB4FB1533D15BEF01BACE4AB659E9D6ACF69829CEF3172C48CE97236929DDCFE81CB24DB5D9
	3AE428204BA1A15FEB354970341ADD4A85C72BD911D58A16BF36167449D573C560888C601D6965BB97083E2C507EG00E0210758BA426F346C8407D1D0D0ED37F73A9DA65292E9110368F999759DE1709E61C09D0F3BC8F0FB3B41C7F4A31F7C3A8D9ABE651696A23E59590BE3ECBC359D9350DB81823B51F67ED20F36D33E66A1A96D67278CED074014EEE6B3333A75E3A80472A672AD023C84A77A7AEDB5751A7224DED33771189A8C827D14E1F564FA5541FC747571C7DEC6F8266F37BFA76DCB03F6D582F4B8
	D167CADDAA3361D233CBC62BBD67840313DB559EE0F20EBBDA4D65A3BC8F72BAC99FFCC07389BFC9A273EE8937352DG97G48B8498258AE31B9F5A740EE517E07A85FD1A0D39840BC00E5G29AE1095408190829081908D102440B884E8828884188E9085A039A36377825438316F7A0BB2E33C752A159771A877DAAE7BC79A597572EA75FAD0532E417A25E4187DF7E184FD7EEDG535D4BDE3ADB21515B9A8D6DBA2E3F35DC07ABB5BA546AED0FC19D285F41E479F018B2FF727F005ED00EE4690A0968C53EAA
	5B14F259A15BDA614545733ACDC40F5C6F3759C5199E0AF740BCD1860E59A570DF1908FE4D0A6234B43B9539CED6940B2DDD4D3F095665F2B7BB54A9866B9AEFA27DE16F01F9C250CFB0919F5BE23759E4BA38C5911EFF87757132D56E82214B6CEE1B4270F7DFA0EB60CE39174FB177DEA08B652BE62B1B4CD74CE7CDB7937BF0B8ADDD60B24814A74DF1937F3FAEBFC523A38F60487AF892BC273B5107776CC49F3E2B6F81A922F1F72C51DAA464217258242B4C176E3A2A7AF0A6532D970DE58F26F38D473BD6
	D05CAFBE24F309E6294B704FEDC90267299A3B436DB8A35B5CE74CB63719673DFDE82DD7DE0D39DDC67F1AF90970AB87196A819CD7513FA6FDCE72F1CE630E354CEE35B275C39E53511BB5DA9D85DEE5BAA26F53611D51AC4975C7B3C75477BF1F9379DDF237DB06A5851904CF0BBE0B2B5F204B3B487B0F65A756137E33EA666FC9BD245E682338E7D98875AC835852436A2E6FE175B33AC70FA1DDB424A58F78F08F23EB02678CGC6777763C8B7474378AD64F4DBFA357C7E3598691AFA593C6C69E5F4F33D68
	EEACC13ADDFD1171C5G8DFD5C467A34F4A7CA11EE8960937AD53A3D7DDA3AF7851D8770C19A3A69DEF45FABC33A3881109D20E80051F98634F45D65C8B79B708BG16F33A3C2F34F4B995C8978478302FD43AB9DEF45102AE75EB607135CAD77F3516EE5289241B8E78B99A3AE8AF3A1A13C8D7FC8DF8GF82EB13A195734F49BAB11AEE194746DA8152EF81416EE0E209B8F78A9G33C7B13A389B34F4632B102E81708AC0708D0CEE3E97DD4DA9244B3C915684B72AF49DB7EA6962CEA3DD846023B4F44B3C68
	E68A3A1A5140E334CA97B81A51B1FF7656BEB25E68BE790A792A59136E6A815FDBD0F00A7A8B36AF6E4DCF60748BC7637E77A13A2FC88900FDA9C0E5G7A381F2E417DEFF0E72834F07247FE7774CD81CC0E00G4D9EE7B46EFF7BAE13BAE7C35E7CG9C77E36B35EB7092174B5EE2214BFFBE360FDC527AC006AB3C74BEBC5BACCE17D2851314877DC049A5A2AF599FF0B457C7FC4A96BF7AC00F485A7F7EF9527EE810CD3AA9B2BE65A6544BD69A0F4804F4A5C093G5B072DBF16486B70GEE7ACD686F96F94D
	9B5100CF9C83BA85A887E0BE59F30C2C433A6CEE171CCE7F16D9ADAD1DF26BDEA2E13A39C7C92773F07A9912D7C1DE770976D832F26159EAF1B92C66DE6A709A4FE8D9D47B65D1386FA25FC3BF18DC4C651B7B3D09D2513564D82318C6603A2D318DE3F81F0ED17BF4607178950B7F790FD5CC8F73059E6681CD425848780131847F34FB2306D771D8360F6A980BBAA8BCFE881EE6BA212433C70D6CEC33BB3BFC75D0C233D191CC8FC77FD9496CC0BE85BB59E6FB195B69A4EB972698D622D14F17A9472DD82C69
	145B3966ABB02501E2583868BB0E712259E3311F3797E37B1683AEA790659FD32C6B439A276CF28D2F8F4B720D7B90D98C5E07CC2E5F8A392CA05394A1D7D54989F8BA503853455B67962B35D4EE31F74997ADAE8B5439D7B39EF8C5E2CCF86B5A7D45A9F8DE91390DB57E19D68F4515B40137615192EC438210FF63B8341DD74FEBED27FA9C1BC715F158468D255F4CF646AB9524BDC424AAC56E8279AEB8CADABACB2CE6E7D7E309214D98B224CD61ED3117E2DB82C14E8851160535C6EDD9B68E631A7F71D129
	0FACE5D6596C3CEEC1189CBB4BD00E441B41675C0CF27CF41F119C83B7239C53CED77A4841561195B64559FB1D52B0D9DE973284C1BDF304AC5B7A3432C837B05F306B966C5FE365A39FDB355B0E12F6D41B1BE5ABF786A60BF2FDA38CF5B9319C654F83599C37207CAF9C544A9F867885G52F814BF26026C31AD2046F47227CEB1ABF2D90759566E5B8422EB5E82A67F3AE492D738D2EBEE3558CB5D0AE237E117C0EB082BA8172FB6D669396A5BA3580A3E7832825B3291642C9E0FED1917EE54160071E8973F
	6EB96DC71E8A5B0844613ADDFF826589013A6689F9FE19E1A4CF42F7D01E54777D494375D3230CC81E6705BCDB20AE53F7D01EC519C6720C9772FC36463FFE1A3F01BC3DA7D11ED028ABCA48730BBC7DB8A6F13F24DB5176961E7CE63EF14D26B2AFBFG5B4593F6E70F5959FA3D8E093563E8A536E3974858F0AB3663CF79C66D883E95755A74F531DE0ED2F3CB67087C50DF058CE140FF2110E151FEA31F18F29B6A323E6A1B69B22568149F1FDAE68727EA370E5047BFD1056DC983D9DBEF93EB2EEC3D2DCE02
	3C88511EAD273ED9FB6ACDE77C58FA152D4352ECD108B398617C59FB8A5BB49F64CD8C42B67D6108D11B4A0350CE36FCFA4A0FCCA5D62B3D87C65F48F4FC70B44AD38FF58589F9969CB612E70E106706437E7CB75751F5CB4464790D10E7815435F1824A73E20E11BC5593D01E13C77D79A72A1F9174980B35FE1D2FBF3FEB1A201E97A893FC4F8E583956D6F65EB5011DEB2D9178B40A8F5760799A116ECBC9FA9E64955D0EE7B12B9B48BEAB8C52338196GAC8558F8872C516F407D65CB173225A14E3A2AEC8A
	ECBF4D56BA2FE0A41EB9DDA66326D36E6569511709CEE97CD048FDA35DB7565C21EF4F840AD76E5037A769BC36A784726202A363B9BD5DE7BA0275E71277D2FCE0307ECCF2E79E1EC906C2DE5C1DF8FE3079183EDFBA6E746E0BGDD1A155B6DD3EE1BAE9D26AB476C6BCA3F1FF8835D5730BD7162EC6D1EB862CE365EDB8CCF693B1171EC6F3525006C3D48BEB4653B38776E6CA5ED9C00F4B0C078F7316F5B3FA4FB7A5910DE88308CE063DD1171F9GA6G4FDD587FEB3EB8BAD47FD32BB6594BGD7E937F716
	69E34CC2BF3F9C21DE23FD4A256B52A1F79969355D7A0931DE69EE37546E61F79C163A095EC88CF47ADD9833796A04B6E6B3EB5D21D812EF94330966F5A704287B7BB4773B2B186C570E9359ABC306FFF648ED29F8E43A4A76A92737410690C39D0F303E3F8FD91FE2D85F77CBC6D65FB3BE6574750D8FB13205B9AB7D58823AF2EE8BF13598571B92027E40DDBAB2D9C77B14534B3AC0A7AB13E1F39D4A30D448302FECE4B2647A14534B10F637118C897BD106723BD106B7C7A843FF8EA943405DC6FD76D61B9F
	3FC8F726CC4E8BFDC4CE92838D96F206160FCC4E6F7914534B394CD04EDF9F7C4CD8CE0395831E7B3CFDC06B4F13A632F5F38EBCABA722BF7F1F035887CD9331EDAD95A3EBDB37CFB9FD5BC2A69A59412A8C14E13A10E17309914E0FBE6574B2C49B4A30BE93E5106EC199DE99218CEF8CA9C35EBDC6B2FC1205B25488992E1D9C198C5F71A92717A150D006B5624CA1D448D0D6B9B2994E7B14534B90E5A8C3EA814A30C448707688E5D8D9B5148CFBA7997645811421F8924A70148F2F6149705210B2F8F4B230
	717B17AC23719B8434D3A66178DD1A0DF24E92F2DEBBB5C21B71A92717F329212E3666228C0977228C5927C7A6C309CFB93D8C65779A4910FC84E5E890B27CF104B27CEDC899028DE5785F9C14A19C72597D34BB69F8DA8469E58267ACA438AD7701AF3E8FE55DF91474F5BD24BDGC1GE1GF3G968B1AC314A6717E4878B4006A7B59B9B8B937A26743FC4DD3542413D55854FD2A4C9E4E57080EC0984FA3F2549B50313A667A2DCBFB870A68E30D9FBA822BB5BC0D6E2631BD4206867F77DEBEEAB03E77323D
	41785E4B6E06758C8FBC3437A7B17FE0436077E28EB7985D0BE1BA89BCCF7A488AFD9384B09DA00A74D3E8E4FCCAA87651DD1426085F93A0F7G48B976829E93E0B41B4E7B532D9EC89DFA9C298FE5A075C69950287D18B56C3A0C6E81FE407B8D7BF975A0E5216D6C1C96E42A81F0881DCC26F8F2BF001C7D87897C3EBC6D7DC5D2E6B8F27A03CCDE5E411F2C0E52BC7D9D49EFD2875AB06AEAAB453D34E99D35B917CBF6514DEC39D9B1733CBFAA64AE1A4F3D293366AE1973CB164E9EDB4DE9AA94FBD936A476
	1F950407413A302E45E937DA49ED82897D532E9C31878D453DFBC6BEBDCF073474G584A832877CF8B08CEB2A1DD7E00314E6B81EF55650D52240379FB18C1F996B379F79AB71875G0B194488FCB4454F5160D9AC253B973FB9188FF9F98FE27C207BCB7F3E41732531EF787ACBE22BAE3B5B59A26B477FCDDF2608BCB552C0D65624CB78FDB0DADEB1BB5BE59F7FF1A7AD4F727C153F6FCBFFBE0675777A635A7EBEA4EC759DB74ECD139E74566BF4C84F860882D82C49E3311802C692FF14A643DC811083D08D
	E01D0CBAAC7D6710B1388A0FE18C0E4DE95F25E77263015F24499847EEA85636A1CB34EDFB09961FA37051DE7843224D2BEB7573F1B85491B1593FAFE07C42CB357C0E88FEDB6A747C66832F68A179B515E979658BFEEB766B79AD87DEC9D3066237317CFD2DFEB2115F65BE2DBF5A45796471E7B5FFB66978AB108E5461A6C1FABA40EC8D1E557D3AF75D07501F2C5ECC6E587A09E1F0BF1211066B1C28A968C7BEB8C846E652C31171FB810A9FC2BF921B2E57CDBD64D99F9ACAB793AB3C644BC65DA4E668790D
	82DE41C372BBFB420BDF8E725B14296797863CE68D49AF7624973F4348AFB5CB4FEF9E70DAB4A43F9F1E545AD62E302D8DBE717BE540AB7161217875D5EA796589FE3B8B747CD201D74E107C522B3C5A1BA55AFBC04F2F92F8B58C496F3F3D799D41B96A33C3E8DB0E075136E23309ED05C1BA82A06AE1342DF73275F5AF013C38C7065475A92F3AF331ADBBF2757CD200D75A107C2E1C766217077CD20E68799583AF53107C36D5FB714BC7FEEB8F7B6CE7005740107C427349FDEA2B39576ED640B7BDC2F47A1C
	387F968469B000F9G0B9EE1B1FF668B3E5FC3568AEDCEF3973800BA075CA2C5051132638A31ECB9246B81BCGA1GB3435077475A497CF6CAFB3F43EBBD133E6DFDB55F2C4808BF31D94513EF07903FB2154CDFC6F748D83E5BC43EF9D297BD651ADBDB48E3556E8DFA19681D93C9E2E7F49DC70D6E3B24CE45B33C2CF2237C9C11FF0FE414DFB995F5720BF6A263A9437BCCA8E37F6EE49F9AFA4F0867EFD97911CF3E667E0F506FCFD7FD62070E1FE9F33AFABA27D1BA13D9337EF8F2F5A262F98106CFDD3751
	0B2FFA6F00654FB52F5767137BG026F02AA1A2F09DA929232B6E0BAFC2BC0FFCECA6E9BB4CC957B019C9C838E4085CCD5EDBF845E4381A226FA0F8736261CCC5739732732B5658A01FF0C6297E970EC2DF91862C9FAA964557F8B6E5D4BE8ECEA96242384AE046296C3DAFA94F13F25380D104E91388C3A6F2F04F407408DD05CG2403856E850A8B0374EC01CB77E04CA06AD154456FC9F7F35F70974DFBCF2FF67D70FF9BD89DC15FEA9D2C4DC9D7C91B17C3FBD342D18FFF2CA638B4C8178BDCC1AF6A26DE60
	EA68DEA78C529182B7011E414D077462F0316EED743FF728EAB45EBB1CEBB40EABDCE89CBC2E70E56360EB7E470E136FE0E05B101576987551B4ADBD06722752F3A3872483844EC2F1A1108E93386CBA241BA1F0BDF5C8B7C760F651731779100E9638AE0ADB8E69446F899B25679FA910CE9338FF7493DC8D241B842EE900602C108E90B8970D8D06C0FA16404D2138F910DEACF0EFD17B1626417BB4443DD10E7D3ACB60FECA6994C80F973870F384978A6908E9B8B6F252FC56781097BD4D7FDC497C41B53A0E
	DDBE0D79035582BF1E62D3262BF8FEBF0E62C9BA8772C22623CC9F25D3BDC2BA697B087B1262F6C1BAC760F6E6A0DD314035E7A01DC9608EE6A2DD1340F5E7A21DA2F039D9C8B7CA607A3310AED860765138E4A824E7881C136266C0BACA600A8A10EE11400DAAC03AE5EA3DB4DE9E77B80C5547859D4525C13A68F16C178307FC560B10577078D07DF2152E819D0F6B7BE5B4450FFFDC5FAF895958AFE11017788314A9097A85872483844EC4F1A1108E93387CDC241BA1F0DF65A25D9C014BB802F473854EF584
	692285EE77E124DBAAF01643C817B0836F317FE7BE46B8B6822EFA862E45321A3C3E0FA057FE8A8ACEBAAD2D256676EA3A32E36B67EC5AB724FC408CDCBF1B8BE81C85527381964F407B871E4F707EC1784393256AD1278D3F99D9E8BA949B77846B1F3DCF28778F425C6F2EE2F5CEAB44BADBA1FF52938477E621F69EA77E6D03956CCE854BDFD7244DA7FE718BC91BDF6215CF64FDA0CE1B6F724AA7BABA4C4B33363D329B5B367C15B0497AC43E5F6FE19671B6A57C10BDEBAE6E14F65D7C8F5E367B0F0AF320
	9F6219CB8775CBC4E60F407DCC42585A82015BCF75C26CFE630F304F0B292DD5C35AA1F00D542E82A09DAEF0CF6662BC97A1F09B73509F4E973867FB68581E89E3ECA60E312979E45E2891699F50B479960E258B7307B3F71372A3DF9F1806D547C83EC547F24952658272EEF6AB76DA59E56913C9348AFC55CCEA9F85780DC847CCDCFFCFABA07BA03B435C6D96F4AF087132C8503DD3C07A5655E16F39A8BB95CB0B59DAED3111CF99F919E4E16F4BC4199305493CCC255DE96903A5B316E2E51AC41944FF45B2
	F79492DBF55AFB4E401CEF31897E0F8A5AD4C17BE6A1797652C22E413548D6EB1D435C82EB87C67FF7CA1F8934C5GEC0FD66141BDDAAB60BCGA1GB3C93E603967AA99938E275CA643062A358ED4A8E4A826EB2FE5C09B77A4527FAF5D17B643F23E5DE9F75BDA11F65399DABF50958184BDC9F067FB4B8769BF7C9D820486A863G584D738260FD8F57812B3BBD070C2DAE334752656E2212323A2F51719485740B041CE1EECAEB3169E8A3687AAF69294878ADCFA16D9B4749F762B639A755701B233DCA0745
	350F7B14233276F7F3B27BE3E2D7C846BE2572A9B6F66A79F356581D3C9D39F43D56E5133B6CB6CB8B136BAC450D868894F20D2AA5E3D60145E748D364F7035EEB13C45FB450355CABG8BA00F659F6C10042E8A777B56D1CBF177GAC96F5846C772EC33628BC6E68A3E325596E248BD896B37817BE342965C03FF1964CCFGCD335879A37F66E7401BE7609515E74F00F19F5EC6570D9E6093B08B795C1CCB764A9DE2AC1900E30D5DE5E19FB633F28F6562B819AE4A7DFB2EFF1BDEC66997826DE2C17FFEAF11
	378D6AE0B429F4AF1172B42CB91FC6DBDED2BE18AD2F5259F37D534C16B7F33C9552BF723265E80F31FDCA34EE8F5087BC0D72BDF7AE59CB8FA5D6CB3B0DFCE7494B3CF9CE6860E91C93CE27619AF021408527619AF06953B88F5CF41078946947302E8728878878B1665DCE7348774464DB61BC7E0DAF3F1BF31078F6D2E6A8BFDD4FE988DFE998746A7C11C96B984ED99C1E332D1606BF1FB05DB627790E13D23AC69FC77479E36C03C7533CEDFA35D59D0B55F42DFE8BC0440FF1AC26F5297997699A7D8100F9
	A27F1CCD4DEF22EB73BF913B9479BF322BE326B85FD7464FA96ED500A5C246F1795EB23E6BD06B10699C77BCC1BC03F5ECF52AF51CBD60DB07CCF1AF82A4BD03F55CF5403B0E0BE4CA64FB7552C3383ECDFD86ED6E16C3383E4DF986EDE7A01B58D5B1242DG41G91A2EF9C4DA35FEA136F2623787758BC7619BD1C7584613716735767C568F0D6CD1A5531E7D8F5203D99591A7E6CDBEB3FCC07178E796ABA1F4EE17F8690ADF47D60A1EFDD77AAEAFFB6D3BF768640B2511F135CEAFE7D916C6F0488E1F3BDEA
	7ED93A6F78AF0094116FA41FE2F3BD7C55674CE1F8E3763D91164B73B913988A585EAF3B106CEB76C6684F0F0BA8BEA7C2FFFE5CDE00FB2BE2481B92017754AF7412F9AD61D97079GC5GA6G85A068D90C933D5518A98DF1BEEA32BB2A652B32D5937AE16D1BD1F83D6DE36B0DE7D7619EE646138FCAB3473C93636FFE7814E719AD4F664FE853CE0AE76D0B267376D248CB0A043188D08D50846009447695B6253176D534DA1492C5B137F4502F7931F197ADF20F0AA607812CEDD30E9A35EDFCA413A3B4525F
	5D212EF22331B603534F614F853A72CBA09D771CB7AE8552E9G454F6947736B12D6B6934F6F604F817E8C026714673477C1B2E8FF4D5455B3D717DE28C933B17BF98E19732F72BD83BB0BA8B5BBEB6CD6CBCBAF9FFBCEBA5649F730CB1F4331FEB5177413BA476BCB62383D1D0EFB722DC44E6C48F8067BDDAE2E3DBD33F13DF2A0EF30754A8AC97D2DAA729E82654281BE646988F8174179DC0A95B17B9E7ADB8EF6276C3D6EDFCA771EF301BEEAB63665D7B46E22BDFD616B3982721B095A0F4E5917A769A597
	CC679AEBAB6A2AAED7B41E2D28B7B5161D2B3EF066AC0E01509E6D1ABB6F7DDC435F6EA130FCB66B0F246775EB6EB78BC819B63355251EBF6DAF7445759860A28BC9DDE46CB3BF197815A472F6F9C8CCC66C9D4817D86596989E64076D76E62625E66EE3F100CE8F4690AA1F477848E40FBEBEB2A1C69B5F7839C79F9FA1F228792BFA7571916F72793D7A788816E76D4878DAED47026A6D7E5B11F584DAF2A8F39FAFB7D1D24B9D22F508B2E5F62B5D195E53E1D1646D923BAFCFE33A7C1F910E717C93910D7777
	EF925167EDF9749E297C75F7E2F67A696FD13C1F67BD2F4624222FACDF45F8562DD0659C639EFE7BEE08D34B3D16BD1CF24C47262FA48593DEC0FFBA2AE987BCAF1A2F70190238D53B13FAD3C0126F186A94A78C832CCB2EA54FBDA5FB389EDE7BC2EB77C993BF0D0D712307ADAF70585C8B2A9E98CF97D5429EF98F2F43321F264F6C6173614F7A700C2B814A863F003146BFEEA57E4968B7DC5B5D963AC5AEA820872725FCFB484F6EBF6E473D485C97B076D859CF6A08FB917608GB9AF62FA6377712A1EBBF2
	377957F3DE8E28B9E79F524E4A7DD47276A3F564EF191D7E502562CAAF2BB8EB2A28953615102056936D99CEBD2C9C8D0D70F2F34B77684B1DG056C5D51EAB1DB6D6D694DF6EB6B0E3454AC78AFEF1F011DAFB94EF8BE36D63579F96BD47E8F1C9BFE7B171E9BEE7B190F6ECD437DC535507D7975EA5D5BBBC95DC6DF11283FE3A07A4935558F2D780D8141773C620FD6FC7FAFE8AB127D73ED5651DECA27B6DC0EB65D12C15395186E4A24699ACC77E4513402E9DBBECD37E1FA2000261BC57AGCD376C617AF3
	50757A6F4998917AD3B6287A6B79581F7E54EF2D1176594CADFEE8719BDF3E67CB75278F71ED32287F4644CF866389140C2EA607364B026D6C4D25E9AB266DC73C75A09F26691EBDFCCD14DFC85A4C6F4BEA5E3B8A3E7D3366B15FEA9D7CB71DFB7D1F834B3D4667401D3D4667405D3D035F9F6F6D757F3B0A23FA353F2B48661672E4B24FDA417E82814281A2G663F08F34D5ABEB25710DF8476DD37D734364B689C307E3F7679EF6B323E21FFBBB8261F46016DE0EDDD04BF62D775137ED26C8E0A643F7B57CF
	E35C16768E051162FDFEBAD7D865B60AE671022768D94B92E897ECGF88C619747704CA3D160EEABC73A8601DBC2F19D109EA5F0FBF790DCB024E78A5CG454D05F4344049B20D17C1BA71A5443D560D75268A5CAB6C7C9D520182F7047A79C9101EA5F0FF11105FBC01DBD406B2AF92388AF13E3DC460BEF4A33F621745393A9B5BE1923893B4665F816981018B2238A0C8078A5C5FA8AE9C52F3DE9631229CF26756EBAD2EDE1CCA3FD8D1EB2AAAAB29EE2CAB2BBDD7DDDDDAD25BD8D287CB730A720AF2D6FEEA
	6E11C14AD71E2B2D3AFC6E2C4918837A2D443C612F9D179C77BF273ECE2FB533772344F471EEAD3D5F2663EFA5E6F852E96EE5636ABFB6517B8F2017A5AF63D81A5CFFC0723FEE2BF15A9D303D6BE5D87A8B175CC7B9BC5A3DE652AB11713BDE99BC5E49E448CAA1FB35BC20B581F8G02G26G4C8218873088E099C0525C48783DGC5GB5G2DG8AC0A0402439A8FFFA6C3EC16467874654A1B84D96629278F8BC3412AC48DCFCCD4AE43B0BBCF87E93ABF77B638BDE669468199D18184CD01C6BA42AEF2838
	477439C65FDF7DE148F27A6F31D8396D7DA30BA34D22EB5439F375F12467A9BEEA2EBE0EF4369F63C80BA02F72D59CD7C1E95EEB0D3107E85A39076B30BA593F5F2DCB9E5A6FB6A493FB1541DF1AFA9D7CBB82F3B2BD53ECEBF349423F37A5A789DC158BCFCD18CCBFBB6A9DD7FC7BE35247ACEE7067E3C3458D3460501C5991DD043F4A6E952C92637163AD6AF8DCD5E1FC76333D0234E9A6141DAD74F8FC4750E7BF17299FA775AD91656E58617F6CA71C52EF79890C211FA07D006CFD76F3BB1D87CC105F7493
	0C257C21FBB01D2C7623130F38CE0201CF2F24BD7B79ED2D71594F3BF4EE89857AF0A15F99E970331FF689F59075930C87ED0E2177F0DE037757F03D9A39D56D6306C14F32882F150334279CF862FE1C55F74C0DB2543C06B20C9338015730ADDF52F948A5135FA616DB991DA24E3943845DA3055E76797BED2A8DA596117EE5BF08C44230E6920A42FDDEAA21E3767FDF1BC8DB447A0B39E91E7777CDE442G75AB30F73374511FB1E5BBF1B6DF0E25311AD9A08F0927B05CF80A1B4FF10CCFC50A7F31DC1DB274
	D83E10E23C067AA245F78D5514E23C06EACB512E21781AB40574DFBB7879FAC1679038CB826726BC896EF3CB2B52A1707DD4AE022F14C905083F014A55C3F1759CB7C6033B44F1372490FF84CD86334532C1A9F41CD92CD67E1B591446E28377A474F1B7155162B0EB643ECF60085C6CDB390864B5342C55E2134B587F29C2C27F9F59AF0977D7B46F267EEFFF0F52752D5641EC7C88597A7377CBBB54F131F547A071036CFC2F78C1778EB2D086099F54D61D2CB431BA86F62AF5DC1607D387AB773B5DEA39486E
	4F7465D86C44A5BBADED3A5089AF3FFE0F5A4E1C3DAA2F8352705B7935B47CF6866DD76BF83D6C7A657D4181357C8577F06AE5342157C1FB0BD7EC1A79E873E7091270E5F498735FDA732CD171B80CA5767F2C7AD5755F40E0437FAC2DD42455A5AD1D29746B83C57A00FEE40029F56CD301D47ACDC1FAFA3AA2A51C352752181FA2ED62911CD612EDF14A296572D5CB8BE4FC4842C0BE783522C00D9D561C0A348689F9BA11FC83104A3FA3G39E812FCB620C86B39A47CA384AACBAA8F03A0B255EE53D73909C6
	BF34843C2A940332BCEB1BCF99AF26AE273441576ECAEEFD63757046720D31D2EC15945FC115DDA52D6CA13D6121E770641F2B9B8C12CC98E4728985CEEB3DF7A6B18FED9E1DF4DB36BBD6CAB8A145C3AFBBBA4C40057E04D93574C1B3757D3293E895A9097E6EDB86144D20E58B2B25589617A2FD2262DB881E65120BE2B2102CEEB5BBBBA987A2CA7297526AB6E7FBB32DC011E23FB87185D4CBD3A47B43EA69FD2503448D48C42CC81BD556B5E45740B38AD9835F03FF8E7A8F9A36CEB42C0A7A7705D7338BD6
	7CEE720AD8A98E7443663BAAA90ECCDA50C0A7D3578A186FA8AFB2CF60DAE925CEE946A9324BB369D2CEE90D5760FD7E0143FF187B6B0B2E95D2FC157491D96F5541E0A6533F5CCABE43667FAF229352EA87318432AA8391DD7431522574DA218FB63655127F37018B96AFA53830G25FF48B4C3E2629915E6D707C26C9A549DE7A37F57A4E9834B361AED6D99EC1541F496D392C46D028492E3C05EE475721960127C543A5D1F1EDAB4F30594FBC2FA5FEAB7371E00250F5DD9A52DD6BA1C322B83DC1147F13C18
	B2CA68416B27F1406E9351FC5258F432FE96C2F7FA6E9D3FA6FA4D09D8C972B8258CCAC9BAB3DDD03416F5181DD0156C2CD2487F854A6E5CA572EE5826FC766A494D8D31D28CEFA4D1FD2A472190336D32D8FB61251C0645E9276C02A4DBA0885BA04AC8BF816DD1FE7967179E7CD5511245406C84B7F690A9C925BA6B6EEA161DE3426F5C7AC17042DB08440EE29CCD40F9B5AC30ECAE72EB0840BCD557E64C9222275F71D2661DD35F3DCCC5D70BAA52F6553A5BE5FBD7C6C9DB9BACAE848368B1E03FD530A7B4
	69BA1A8D1E899F1E7A79FB4DE0E165520A2E4CCC7AB70B7EBDC47EE6D1CC9645E49D02EEDBA28D7ACF1F6F41B6D3332409834406BC64EDAF4BA5469F4B874FE754303C068514A5DFF8A952AA6E61C421B88D2C20D89A60FEAFA2A2020DEBA843CFF4018979DA0F703290830F49A2A0B82D14543346D5DE556FB1C8ED739989843B435724897AB3C5DAC906ED9BC96CA3FF8E12BF6934CCA67D1BFD5DDAA5258E6B34D929EA137C5B439533727F1B8654F83C7A23D6D27DAABBDB5CF7CDFB36386C26943FE76A21D1
	ACBEB3A3CABDDBE431217567E9BC937009EFE06C49DCC3FE8F5CDF0C282E4BEC3542AE36334456CA62C5253D0A2C4644B71DA77CDDC843B3D95E5673C46EAB1AB27F87D0CB87885EGC8FDE1A6GG5CF3GGD0CB818294G94G88G88G7CF854AC5EGC8FDE1A6GG5CF3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1BA6GGGG
**end of data**/
}
}
