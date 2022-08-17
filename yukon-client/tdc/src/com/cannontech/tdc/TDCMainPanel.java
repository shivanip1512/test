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
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
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
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.TagDao;
import com.cannontech.core.image.dao.YukonImageDao;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.state.YukonImage;
import com.cannontech.debug.gui.ObjectInfoDialog;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.message.util.Command;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.tags.Tag;
import com.cannontech.tdc.commandevents.AckAlarm;
import com.cannontech.tdc.data.ColumnData;
import com.cannontech.tdc.data.Display;
import com.cannontech.tdc.data.DisplayData;
import com.cannontech.tdc.filter.ITDCFilter;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.roweditor.AltScanRatePanel;
import com.cannontech.tdc.roweditor.AnalogControlEntryPanel;
import com.cannontech.tdc.roweditor.AnalogManualEntryPanel;
import com.cannontech.tdc.roweditor.EditorDialogData;
import com.cannontech.tdc.roweditor.ManualEntryJPanel;
import com.cannontech.tdc.roweditor.RowEditorDialog;
import com.cannontech.tdc.roweditor.SendData;
import com.cannontech.tdc.roweditor.StatusControlEntryPanel;
import com.cannontech.tdc.roweditor.StatusManualEntryPanel;
import com.cannontech.tdc.roweditor.TagWizardPanel;
import com.cannontech.tdc.roweditor.TagsEditorPanel;
import com.cannontech.tdc.toolbar.AlarmToolBar;
import com.cannontech.tdc.utils.DataBaseInteraction;
import com.cannontech.tdc.utils.TDCDefines;

public class TDCMainPanel extends javax.swing.JPanel 
implements com.cannontech.tdc.bookmark.BookMarkSelectionListener, 
java.awt.event.ActionListener, 
java.awt.event.ItemListener, 
java.awt.event.MouseListener, 
javax.swing.event.PopupMenuListener
{
	//an int read in as a CParm used to turn on/off features
   public static final String PROP_BOOKMARK = "con.cannontech.BookMark";

	private boolean needColDataUpdate = true;

	private TDCClient tdcClient = null;  // copy of the tdcClient from the mainFrame
	// stores the last historical query, needed for when the Arrow buttons are pushed
	private int pageNumber = 1;
	private int totalPages = 1;
	private SpecialTDCChild currentSpecialChild = null;
	private AlarmToolBar frameAlarmToolBar = null;
	private SortTableModelWrapper sorterModelWrapper = null;

	/* START -Display atrributes */
	private Display[] lastDisplays = new Display[Display.DISPLAY_TYPES.length];  // holds a last display for EVERY display type that exists
	private Display currentDisplay = null;
	private Display[] allDisplays = null;
	/* END -Display atrributes */
	
    private int currentTemplateNum = 0;
	
	private boolean refreshPressed = false;
	private boolean initialStart = true;
	private Date previousDate = null;
	
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

	private final Map<String, SpecialTDCChild> specialChildMap = new HashMap<String, SpecialTDCChild>();

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
@Override
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
    TDCMainFrame parentFrame = ((TDCMainFrame) SwingUtil.getParentFrame(this));

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
		Frame owner = SwingUtil.getParentFrame( this );
		
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
		String ptTypeStr = tableModel.getPointType( selectedRow );
		PointType pointType = PointType.getForString(ptTypeStr);
		PointValues pt = tableModel.getPointValue( selectedRow );
		Object currentValue = tableModel.getPointDynamicValue( selectedRow );

		EditorDialogData data = new EditorDialogData( pt, pt.getAllText() );

			
        if(pointType == PointType.Analog
            && ( TagUtils.isControllablePoint(data.getTags()) && TagUtils.isControlEnabled(data.getTags()) )
            && source != getJMenuItemPopUpManualEntry() )
        {
            return new AnalogControlEntryPanel( data, currentValue );
        }
        else if(pointType == PointType.Analog ||
                pointType  == PointType.PulseAccumulator ||
                pointType  == PointType.DemandAccumulator ||
                pointType == PointType.CalcAnalog)
		{
			return new AnalogManualEntryPanel( data, currentValue );
		}
		else if(pointType == PointType.Status
					&& ( TagUtils.isControllablePoint(data.getTags()) && TagUtils.isControlEnabled(data.getTags()) )
					&& source != getJMenuItemPopUpManualEntry() )
		{			
			return new StatusControlEntryPanel( data, currentValue );
		}
		else if(pointType == PointType.Status
				 || pointType == PointType.CalcStatus)
		{			
			return new StatusManualEntryPanel( data, currentValue );
		}
		else
			CTILogger.info("** Unhandled POINTTYPE (" + pointType +") for a Manual Entry Panel **");
	}
	catch( NullPointerException ex )
	{   // one of the values we have is null, lets not create the panel
		handleException( ex );
	}
		
	return null;
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
	Frame owner = SwingUtil.getParentFrame(this);
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

		@Override
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
@Override
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
				setCurrentDisplay( getAllDisplays()[getDisplayIndexByName(currentDisplayTitle)] );
				
				// we must have a none static client display
				initClientDisplays();
			}


			//!Display.needsNoIniting(getCurrentDisplay().getType()) )
			if( getCurrentSpecialChild() == null
				 || (getCurrentSpecialChild() != null && getCurrentSpecialChild().needsComboIniting()) )
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
			TDCMainFrame parentFrame = ((TDCMainFrame) SwingUtil.getParentFrame(this));

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
	    SwingUtil.getParentFrame(this).setCursor(original);
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
	if( getCurrentSpecialChild() != null )
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
            Frame owner = SwingUtil.getParentFrame(this);
            
            owner.setCursor( original );
			
		}
	}
	
	//set our display with the last format
	try {
		formatDisplayColumns();
	}
	catch( Exception ex )
	{
		CTILogger.warn( "Unable to format table columns from last values", ex );
	}

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
	java.util.List alarmStates = YukonSpringHook.getBean(AlarmCatDao.class).getAlarmCategories();
	Object[][] data = null;
	
	// We want do disclude the EVENT alarmStateId which is 1
	data = new Object[alarmStates.size()-1][5];
	long displayNumber = Display.GLOBAL_ALARM_DISPLAY + 1;  // always one after the global display alarm display number
	int indx = 0;

	for( int i = 0; i < alarmStates.size(); i++ )
	{
		LiteAlarmCategory liteAlarm = (LiteAlarmCategory)alarmStates.get(i);
		if( liteAlarm.getAlarmCategoryId() == Signal.EVENT_SIGNAL )
			continue;

		data[indx][0] = liteAlarm.getCategoryName();
		data[indx][1] = String.valueOf( displayNumber++ );
		data[indx][2] = liteAlarm.getCategoryName();
		data[indx][3] = Display.DISPLAY_TYPES[Display.ALARMS_AND_EVENTS_TYPE_INDEX];
		data[indx++][4] = liteAlarm.getCategoryName();
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
public SpecialTDCChild getCurrentSpecialChild() {
	return currentSpecialChild;
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
/**
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

private javax.swing.JLabel getJLabelDisplayName() {
	if (ivjJLabelDisplayName == null) {
		try {
			ivjJLabelDisplayName = new javax.swing.JLabel();
			ivjJLabelDisplayName.setName("JLabelDisplayName");
			ivjJLabelDisplayName.setText("View");
		} catch (java.lang.Throwable ivjExc) {
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
public void getHistoryDisplayData( Date date ) 
{
	Frame owner = SwingUtil.getParentFrame(this);
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
                    @Override
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
	
	try {
	    boolean initSpecialChild = false;
	    final String className = getCurrentDisplay().getDescription();
	    
	    SpecialTDCChild specialChild = specialChildMap.get(className);
	    if (specialChild == null) {
	        initSpecialChild = true;
	        Class<?> clazz = Class.forName(className);
	        specialChild = (SpecialTDCChild) clazz.newInstance();
	        specialChildMap.put(className, specialChild);
	    }
	    
		setCurrentSpecialChild(specialChild);

		synchronized (getCurrentSpecialChild()) {
		    
		    if (initSpecialChild) {
		        // init the new special child right away!
		        getCurrentSpecialChild().initChild();
		    } else {
		        getCurrentSpecialChild().getMainJPanel().setVisible(true); 
		    }

		    JPanel p = getCurrentSpecialChild().getMainJPanel();

		    add(p, getScrollPaneGridBagConstraints() );
		    frameAlarmToolBar.setCurrentComponents( getCurrentSpecialChild().getJButtons() );
		    getCurrentSpecialChild().setTableFont( getDisplayTable().getFont() );
		    getCurrentSpecialChild().setGridLines( getDisplayTable().getShowHorizontalLines(), 
		                                           getDisplayTable().getShowVerticalLines() );

		    getCurrentSpecialChild().setInitialTitle();
		    getJLabelDisplayName().setText( getCurrentSpecialChild().getJComboLabel() );

		    getCurrentSpecialChild().setAlarmMute( getTableDataModel().isMuted() );


		    // add the new ActionListener to our combo box
		    getCurrentSpecialChild().addActionListenerToJComponent( getJComboCurrentDisplay() );

		    getJLabelDisplayTitle().setText(getCurrentDisplay().getName());			
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

			getCurrentSpecialChild().setRowColors( rowColors, bgColor );
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
		"select name, displaynum, type, description from display " +
 		"order by name, type");

	Object[][] values = DataBaseInteraction.queryResults( query, null );
	
	Object[][] alarmValues = getAlarmStatesCache();
	
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
        @Override
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
	
	display.setType( query[index][2].toString() );
	display.setDescription( query[index][3].toString() );
	
		
			
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
	TDCMainFrame parentFrame = ((TDCMainFrame) SwingUtil.getParentFrame(this));

	java.awt.Font newFont = null;
	ParametersFile pf = new ParametersFile( CtiUtilities.OUTPUT_FILE_NAME );

	try
	{
		
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

		currentTemplateNum = Integer.parseInt(pf.getParameterValue("TemplateNum", "1"));
		setStartUpDisplay( pf, parentFrame );
		
		//TDCMainFrame.messageLog.addMessage("Parameters file found and parsed successfully", MessageBoxFrame.INFORMATION_MSG );
	}
	catch( Exception e ) 
	{
		// init file not found
		if( TDCMainFrame.startingDisplayName != null )
		{
			if( TDCMainFrame.startingViewType != null ) {
                parentFrame.setSelectedViewType( TDCMainFrame.startingViewType );
            }
			
			getJComboCurrentDisplay().setSelectedIndex( getDisplayIndexByJComboValue( TDCMainFrame.startingDisplayName ) );
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
			Display currentDisp = getAllDisplays()[i];
            if( currentDisp.getDisplayNumber() <= Display.BEGINNING_CLIENT_DISPLAY_NUMBER )
			{
				boolean enabled = true;
            
				if( currentDisp.getType().equalsIgnoreCase(Display.DISPLAY_TYPES[Display.LOAD_CONTROL_CLIENT_TYPE_INDEX]) )
					enabled = !TDCDefines.isHiddenLoadControl(TDCDefines.USER_RIGHTS);
				else if( currentDisp.getType().equalsIgnoreCase(Display.DISPLAY_TYPES[Display.SCHEDULER_CLIENT_TYPE_INDEX]) )
					enabled = !TDCDefines.isHiddenMACS(TDCDefines.USER_RIGHTS);

				addClientRadioButtons( currentDisp.getName(), i, enabled );
			}
 
        
			if( getCurrentDisplay() == null 
				 && Display.isReadOnlyDisplay(currentDisp.getDisplayNumber()) )
			{
				// we must not have a currentDisplay set yet, set it to the EVENT VIEWER
                
                setCurrentDisplay( currentDisp );
				getJComboCurrentDisplay().addItem( currentDisp );
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
		 		&& getCurrentSpecialChild() != null );
}

/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
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
public void jMenuItemPageBack_ActionPerformed(ActionEvent actionEvent) 
{
	if( totalPages > 1 )
	{
		JRadioButtonMenuItem previousButton = null;
		Enumeration elementEnum = getButtonGroupPage().getElements();
		ArrayList buttonList = new ArrayList(10);

		while( elementEnum.hasMoreElements() )
			buttonList.add( elementEnum.nextElement() );
	
		for( int i = 0; i < buttonList.size(); i++ )
		{
			if( ((javax.swing.JRadioButtonMenuItem)buttonList.get(i)).isSelected() )
			{
				if( i == 0 )
					previousButton = (JRadioButtonMenuItem)buttonList.get( buttonList.size()-1 );
				else
					previousButton = (JRadioButtonMenuItem)buttonList.get( i-1 );

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
	freeChartPanel.setDomainZoomable(true);
	freeChartPanel.setRangeZoomable(true);
	
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
	
	jd.setIconImage(SwingUtil.getParentFrame(this).getIconImage());
	jd.setResizable( true );
	jd.setSize( 600, 430 );
	jd.setLocation( this.getLocationOnScreen() );
	
	jd.setContentPane( freeChartPanel );

	jd.show();
}

public void jMenuItemAltScanRate_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	RowEditorDialog d = new RowEditorDialog(SwingUtil.getParentFrame(this)); 

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
public void jMenuItemPageForward_ActionPerformed(ActionEvent actionEvent) 
{
	if( totalPages > 1 )
	{
		JRadioButtonMenuItem nextButton = null;
		Enumeration elementEnum = getButtonGroupPage().getElements();

		while( elementEnum.hasMoreElements() )
		{
			if( ((JRadioButtonMenuItem)elementEnum.nextElement()).isSelected() )
			{
				if( elementEnum.hasMoreElements() )
					nextButton = (JRadioButtonMenuItem)elementEnum.nextElement();
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
	
	sig.setDescription("Point control change occurred from TDC on point: " + 
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
		@Override
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
				LitePoint lPoint = YukonSpringHook.getBean(PointDao.class).getLitePoint( pv.getPointID() );
				
				//if there is more than one alarm, add the all alarms ack to the top
				if( i == 1 )
				{
					JMenuItem it = createAckAlarmJMenuItem("(ACK ALL)", null, pv );
					getJMenuPopUpAckAlarm().add( it, 0 );					
				}

				JMenuItem it = createAckAlarmJMenuItem(
					"Condition: " +
					AlarmUtils.getAlarmConditionText( 
						sigs[i].getCondition(), lPoint.getPointTypeEnum(), lPoint.getPointID() ) +
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
			&& DeviceTypesFuncs.hasDeviceScanRate(PaoType.getForDbString(pv.getDeviceType()) ) );		


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
	RowEditorDialog d = new RowEditorDialog(SwingUtil.getParentFrame(this)); 

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
		LiteTag liteTag = YukonSpringHook.getBean(TagDao.class).getLiteTag( aTag.getTagID() );

		JMenuItem mi = new JMenuItem(
				liteTag.getTagName() + " (Level: " + liteTag.getTagLevel() + ")" );

		//tells us what row we have selected
		mi.putClientProperty( "tdc_tag_row", new Integer(i++) );

		//mi.setForeground( Colors.getColor(liteTag.getColorID()) );
		if( liteTag.getImageId() != YukonImage.NONE_IMAGE_ID )
			mi.setIcon( 
				new ImageIcon(java.awt.Toolkit.getDefaultToolkit().createImage(
					YukonSpringHook.getBean(YukonImageDao.class).getLiteYukonImage(liteTag.getImageId()).getImageValue()) ) );

		//be sure any click on a specific tag takes the user to the
		// tag editor
		mi.addActionListener( new ActionListener()
		{
			@Override
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
@Deprecated
public void jRadioButtonMenuItemAllowPt_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int selectedRow = getDisplayTable().getSelectedRow();
	int pointID = getTableDataModel().getPointValue(selectedRow).getPointID();

	// build up our opArgList for our command	message
    List<Integer> data = new ArrayList<Integer>(4);
	data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);  // this is the ClientRegistrationToken	
	data.add(Command.ABLEMENT_POINT_IDTYPE);
	data.add(pointID);
	data.add(Command.ABLEMENT_ENABLE);

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
        List<Integer> data = new ArrayList<Integer>(4);
		data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);  // this is the ClientRegistrationToken	
		data.add(Command.ABLEMENT_DEVICE_IDTYPE);
		data.add(ptVal.getDeviceID());
		data.add(Command.ABLEMENT_DISABLE);
	
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
	int pointID = getTableDataModel().getPointValue(selectedRow).getPointID();

	// build up our opArgList for our command	message
    List<Integer> data = new ArrayList<Integer>(4); 
	data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);  // this is the ClientRegistrationToken	
	data.add(Command.ABLEMENT_POINT_IDTYPE);
	data.add(pointID);
	data.add(Command.ABLEMENT_DISABLE);

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
    List<Integer> data = new ArrayList<Integer>(4);
	data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);  // this is the ClientRegistrationToken	
	data.add(Command.ABLEMENT_DEVICE_IDTYPE);
	data.add(deviceID);
	data.add(Command.ABLEMENT_ENABLE); 

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
	int pointID = getTableDataModel().getPointValue(selectedRow).getPointID();

	// build up our opArgList for our command	message
    List<Integer> data = new ArrayList<Integer>(4);
	data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);  // this is the ClientRegistrationToken	
	data.add(Command.ABLEMENT_POINT_IDTYPE);
	data.add(pointID);
	data.add(Command.ABLEMENT_ENABLE);

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
        List<Integer> data = new ArrayList<Integer>(4);        
		data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);  // this is the ClientRegistrationToken
		data.add(Command.ABLEMENT_DEVICE_IDTYPE);
		data.add(deviceID);
		data.add(cmdInhib);

	
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
	RowEditorDialog d = new RowEditorDialog(SwingUtil.getParentFrame(this)); 

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
@Deprecated
public void jRadioButtonMenuItemInhibitPt_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int selectedRow = getDisplayTable().getSelectedRow();
	int pointID = getTableDataModel().getPointValue(selectedRow).getPointID();

	// build up our opArgList for our command	message
    List<Integer> data = new ArrayList<Integer>(4);
	data.add(Command.DEFAULT_CLIENT_REGISTRATION_TOKEN);  // this is the ClientRegistrationToken	
	data.add(Command.ABLEMENT_POINT_IDTYPE);
	data.add(pointID);
	data.add(Command.ABLEMENT_DISABLE);

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
    Frame owner = SwingUtil.getParentFrame(this);       
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
			@Override
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
@Override
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
@Override
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
@Override
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
@Override
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
@Override
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
@Override
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
@Override
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
@Override
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
		
		if( getCurrentSpecialChild() != null )
		{
			getCurrentSpecialChild().executeRefreshButton();
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
	java.io.File file = null;

	try
	{
		file = new java.io.File( TDCDefines.DISPLAY_OUT_FILE_NAME );

		if( file.exists() )
		{	
			java.io.ObjectInputStream in = new java.io.ObjectInputStream(
								new java.io.FileInputStream( file ) );

			Object o = in.readObject();
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
			
			in.close();
		}

	}
	catch ( Exception e )
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
	if(getCurrentSpecialChild() != null)
	{
		getCurrentSpecialChild().removeActionListenerFromJComponent( getJComboCurrentDisplay() );		
		getCurrentSpecialChild().getMainJPanel().setVisible( false );
		frameAlarmToolBar.setOriginalButtons();
		getJLabelDisplayName().setText("View");
	}
	
	setCurrentSpecialChild( null );
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
 * @param newCurrentSpecialChild com.cannontech.tdc.SpecialTDCChild
 */
protected void setCurrentSpecialChild(SpecialTDCChild newCurrentSpecialChild) {
	currentSpecialChild = newCurrentSpecialChild;
}
/**
 * Insert the method's description here.
 * Creation date: (1/30/2002 10:01:30 AM)
 */
public Cursor setCursorToWait() 
{
	Frame owner = SwingUtil.getParentFrame(this);
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
	      		CommonUtils.formatMonthString( calendar.get( GregorianCalendar.MONTH ) ) + " " +
	            calendar.get( GregorianCalendar.DAY_OF_MONTH ) + ", " +
	            calendar.get( GregorianCalendar.YEAR ) + ")";
		}
	}


	if( totalPages > 1
		 && Display.isHistoryDisplay(getCurrentDisplay().getDisplayNumber()) )
	{
		title += " (" + pageNumber + " of " + totalPages + ")";		
	}
	
	

		
	final String titleString = new String( title );	
	javax.swing.SwingUtilities.invokeLater( new Runnable()
		{	@Override
        public void run()
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
 * Creation date: (4/11/00 10:56:22 AM)
 * Version: <version>
 * @param parameters com.cannontech.tdc.ParametersData
 */
private void setStartUpDisplay( ParametersFile pf, TDCMainFrame parentFrame ) 
{
	if( getJComboCurrentDisplay().getItemCount() > 0 )
	{
		if( TDCMainFrame.startingDisplayName != null )
		{
			if( TDCMainFrame.startingViewType != null )
				parentFrame.setSelectedViewType( TDCMainFrame.startingViewType );

			getJComboCurrentDisplay().setSelectedIndex( getDisplayIndexByJComboValue( TDCMainFrame.startingDisplayName ) );
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
	if( getCurrentSpecialChild() != null )
		getCurrentSpecialChild().setTableFont( getDisplayTable().getFont() );
		
	getDisplayTable().revalidate();
	getDisplayTable().repaint();
}
/**
 * Insert the method's description here.
 * Creation date: (6/9/00 10:11:11 AM)
 */
private void setTableHeaderListener() 
{
	javax.swing.table.JTableHeader hdr = (getDisplayTable().getTableHeader());
	hdr.setToolTipText("Dbl Click on a column header to sort");

	// The actual listener is defined here
	hdr.addMouseListener( new java.awt.event.MouseAdapter() 
	{
		@Override
        public void mouseClicked(java.awt.event.MouseEvent e)
		{
			if( e.getClickCount() == 2 )
			{
				int vc = getDisplayTable().getColumnModel().getColumnIndexAtX( e.getX() );
				int mc = getDisplayTable().convertColumnIndexToModel( vc );

				Frame owner = SwingUtil.getParentFrame(TDCMainPanel.this);
				
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
		Frame owner = SwingUtil.getParentFrame(this);
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
				int displayNum = getCurrentDisplay().getDisplayNumber();

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
				setDisplayTitle( getCurrentDisplay().getName(), null );
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
	ObjectInfoDialog d = new ObjectInfoDialog(SwingUtil.getParentFrame(this)); 

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
	RowEditorDialog d = new RowEditorDialog(SwingUtil.getParentFrame(this)); 

	ManualEntryJPanel panel = null;	
	int selectedRow = getDisplayTable().getSelectedRow();
	
	if( getTableDataModel().getPointValue( selectedRow ).getPointQuality()
		== PointQuality.Constant)
	{
		JOptionPane.showMessageDialog( this, 
			"Manual changes of points with a quality of CONSTANT is not allowed",
			"Manual Entry Denied", JOptionPane.WARNING_MESSAGE );
	}
	else
		panel = createManualEditorPanel( selectedRow, source );


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
		
		if( panel instanceof StatusManualEntryPanel )
		{
			d.addRowEditorDialogListener( (StatusManualEntryPanel)panel );
		}
        else if( panel instanceof AnalogManualEntryPanel )
        {
            d.addRowEditorDialogListener( (AnalogManualEntryPanel)panel );
        }
        else if( panel instanceof AnalogControlEntryPanel )
        {
            d.addRowEditorDialogListener( (AnalogControlEntryPanel)panel );
        }
		else if( panel instanceof StatusControlEntryPanel )
		{
			d.addRowEditorDialogListener( (StatusControlEntryPanel)panel );
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
	javax.swing.JTable[] tables = ( isClientDisplay() 
				? getCurrentSpecialChild().getJTables()
				: new javax.swing.JTable[] {getDisplayTable()} );

	//we do not want to update the display column data if we
	//  do not have a display
	if( getCurrentDisplay() != null )
	{
		ColumnData[][] columnData = new ColumnData[tables.length][0];
		for( int i = 0; i < tables.length; i++ )
		{
			javax.swing.JTable table = tables[i];
			//ArrayList tmpList = new ArrayList(16);
			ColumnData[] cols = new ColumnData[table.getColumnCount()];
	
			for( int j = 0; j < table.getColumnCount(); j++ )
			{
				TableColumn col = table.getColumnModel().getColumn(j);
				//tmpList.add(
				cols[j] =
					new ColumnData(
						col.getHeaderValue().toString(),
						j,
						col.getWidth());
			}
				
			columnData[i] = cols;
				//(ColumnData[])tmpList.toArray(new ColumnData[tmpList.size()]);
		}

		DisplayData dd = new DisplayData(
				getCurrentDisplay().getDisplayNumber(),
				getCurrentDisplay().getTdcFilter().getFilterID(),
				columnData );



		//set any display properties needed for TDC clients
		if( getCurrentSpecialChild() != null )
		{
			JPanel p = getCurrentSpecialChild().getMainJPanel();
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
private void formatDisplayColumns() throws Exception
{
	Display currentDisp = getCurrentDisplay();
    DisplayData displayData = currentDisp.getDisplayData();
    if( currentDisp == null
		 || displayData == null )
		return;


    refreshColumnData();

	javax.swing.JTable[] tables = ( isClientDisplay() 
				? getCurrentSpecialChild().getJTables()
				: new javax.swing.JTable[]{getDisplayTable()} );

	for( int k = 0; k < tables.length; k++ )
	{
		javax.swing.JTable table = tables[k];

        ColumnData[][] columnData = displayData.getColumnData();
        if (columnData.length != tables.length) 
        {
            refreshColumnData();
        }
        else
        {
            for( int i = 0; i < columnData[k].length; i++ )
        		{
        			ColumnData singlCol = columnData[k][i];
        
        			for( int j = 0; j < table.getColumnCount(); j++ )
        			{
        				if( table.getColumnName(j).equalsIgnoreCase(singlCol.getColumnName()) )
        				{
        					//found the column, lets reformat its appearance
        					TableColumn tc = table.getColumnModel().getColumn(j);
        	
        					tc.setWidth( singlCol.getWidth() );
        					tc.setPreferredWidth( singlCol.getWidth() );	
        	
        					//make sure the table has the column index!!
        					table.moveColumn( j, 
        					   (singlCol.getOrdering() >= table.getColumnCount()
        						 ? table.getColumnCount() - 1
        						 : singlCol.getOrdering() ) );
        	             
        					break;
        				}
        				
        			}
        		}
        }
	}

/*	
	for( int i = 0; i < getCurrentDisplay().getDisplayData().getColumnData().length; i++ )
	{	
		ColumnData[] cd = getCurrentDisplay().getDisplayData().getColumnData()[i];



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
	}	
*/
	
	//set any display properties needed for TDC clients
	if( getCurrentSpecialChild() != null )
	{
		JPanel p = getCurrentSpecialChild().getMainJPanel();
		for( int i = 0; i < p.getComponentCount(); i++ )
		{
			if( p.getComponent(i) instanceof CompositeJSplitPane )
			{
				if( displayData.getProp0() >= 0 )
					((CompositeJSplitPane)p.getComponent(i)).setDividerLocation(
							displayData.getProp0() );
							
				if( displayData.getProp1() >= 0 )
					((CompositeJSplitPane)p.getComponent(i)).getJSplitPaneInner().setDividerLocation(
							displayData.getProp1() );
			}
		}
	}	


}

private void refreshColumnData() 
{
    CTILogger.info("refreshColumnData called");
    executeRefresh_Pressed();
}

/**
 * Insert the method's description here.
 * Creation date: (3/6/00 1:51:30 PM)
 */
public void writeAllDisplayColumnData() 
{
	if( allDisplays == null )
		return;

	HashMap map = new HashMap( getAllDisplays().length );

	try
	{
		java.io.FileOutputStream fileOut = new java.io.FileOutputStream( 
				new java.io.File( TDCDefines.DISPLAY_OUT_FILE_NAME ) );
		
		java.io.ObjectOutputStream o = new java.io.ObjectOutputStream(fileOut);

		for( int i = 0; i < getAllDisplays().length; i++ )
		{
			DisplayData displayData = getAllDisplays()[i].getDisplayData();
            map.put(
				new Integer(getAllDisplays()[i].getDisplayNumber()),
				displayData );
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
		cal.set( GregorianCalendar.HOUR_OF_DAY, 0 );
		cal.set( GregorianCalendar.MINUTE, 0 );
		cal.set( GregorianCalendar.SECOND, 0 );
		cal.set( GregorianCalendar.MILLISECOND, 0 );

		previousDate = cal.getTime();
	}

	return previousDate;
}

public int getCurrentTemplateNum() {
    return currentTemplateNum;
}
public void setCurrentTemplateNum(int currentTemplateNum) {
    this.currentTemplateNum = currentTemplateNum;
}

public void getHistoryDisplayDataForEventViewer( Date currentDate, int i) {
    ViewCreator vc = new ViewCreator( getTableDataModel() );
    if( getCurrentDisplay().getDisplayNumber() == Display.EVENT_VIEWER_DISPLAY_NUMBER )
    {       
        vc.createRowsForHistoricalView( currentDate, i );
    }
}
public int getPageNumber() {
    return pageNumber;
}
public int getTotalPages() {
    return totalPages;
}

}
