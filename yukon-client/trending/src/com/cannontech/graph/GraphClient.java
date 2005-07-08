package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (5/16/2001 5:21:13 PM)
 * @author: 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;

import org.jfree.chart.JFreeChart;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.CTIKeyEventDispatcher;
import com.cannontech.common.gui.util.SplashWindow;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.GraphFuncs;
import com.cannontech.database.data.graph.GraphDefinition;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.database.model.DeviceTree_CustomPointsModel;
import com.cannontech.database.model.GraphDefinitionTreeModel;
import com.cannontech.debug.gui.AboutDialog;
import com.cannontech.graph.buffer.html.HTMLBuffer;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.exportdata.SaveAsJFileChooser;
import com.cannontech.graph.menu.FileMenu;
import com.cannontech.graph.menu.HelpMenu;
import com.cannontech.graph.menu.OptionsMenu;
import com.cannontech.graph.menu.TrendMenu;
import com.cannontech.graph.menu.ViewMenu;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.graph.model.TrendProperties;
import com.cannontech.jfreechart.chart.YukonChartPanel;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.application.TrendingRole;
import com.cannontech.util.ServletUtil;
public class GraphClient extends javax.swing.JPanel implements com.cannontech.database.cache.DBChangeListener, GraphDefines, java.awt.event.ActionListener, java.awt.event.WindowListener, javax.swing.event.ChangeListener, javax.swing.event.TreeSelectionListener
{
    public static final URL GRAPH_GIF = GraphClient.class.getResource("/GraphIcon.gif");

	private class TrendDataAutoUpdater extends Thread
	{
		public boolean ignoreAutoUpdate = false;
		public void run()
		{
			while (true)
			{
				try
				{
					//interval rate is in seconds (* 1000 for millis)
					Thread.sleep((long) (GraphClient.this.getGraph().getMinIntervalRate() * 1000));
				}
				catch (InterruptedException ie)
				{
					return;
				}
				
//				if( GraphClient.this.getClientConnection().isValid() )
				{
					synchronized (com.cannontech.graph.GraphClient.class)
					{
						if( ignoreAutoUpdate )
						{
							com.cannontech.clientutils.CTILogger.info("** ignoreAutoUpdate **\n");
							ignoreAutoUpdate = false;
							continue;
						}
					}					
				
					if (GraphClient.this.getCurrentRadioButton().isSelected())
					{
						javax.swing.SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								javax.swing.JFrame frame = GraphClient.this.getGraphParentFrame();
								java.awt.Cursor savedCursor = GraphClient.this.getCursor();
	
								if (getTrendModel() != null)
								{
									frame.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
									// Set to currentDate - always want this date to be TODAY!
									GraphClient.this.getStartDateComboBox().setSelectedDate(com.cannontech.util.ServletUtil.getToday());
									GraphClient.this.setStartDate(getStartDateComboBox().getSelectedDate());
									GraphClient.this.getGraph().setUpdateTrend(true);
									updateCurrentPane();
									long timer = (System.currentTimeMillis());
									frame.setTitle("Yukon Trending - ( Updated " + extendedDateTimeformat.format(new java.util.Date(timer)) + " )");
									com.cannontech.clientutils.CTILogger.info("[" + extendedDateTimeformat.format(new java.util.Date(timer)) + "] - Yukon Trending - Auto Updating Point Data");
									frame.setCursor(savedCursor);
	
								}
							}
						});
					}
				}
			}
		}
	}
	//This is a somewhat temporary field user
	private int savedViewType = GraphRenderers.LINE;
	private static Graph graphClass = null;
	private static javax.swing.JFrame graphClientFrame = null;
	public static double scalePercent = 0;
	private static final int NO_WEEK = 0;	// one week or less
	private static final int FIRST_WEEK = 1;//exactly the first week
	private static final int SECOND_WEEK = 2;
	private static final int THIRD_WEEK = 3;
	private static final int FOURTH_WEEK = 4;
	private static final int FIFTH_WEEK = 5;	//exactly the last week
	//Components for controlling the view toggle (historical or current data)
	private int currIndex = 0;	//Current timeperiodComboBox index 
	private int histIndex = 0;	//Historical timeperiodComboBox index
	private Object currDate = null;
	private Object histDate = null;
	private int histWeek = 0;
	private int currentWeek = NO_WEEK;	//used when more than one week is selected.
	private java.util.Date[] sliderValuesArray = null;
	private javax.swing.ButtonGroup dataViewButtonGroup;
	private boolean timeToUpdateSlider = true;	//true, update button was pushed, initial is ture also
	private CreateGraphPanel createPanel =  null;
	//Menu Bar and Items
	private javax.swing.JMenuBar menuBar = null;
	private FileMenu fileMenu = null;
	private TrendMenu trendMenu = null;
	private ViewMenu viewMenu = null;
	private OptionsMenu optionsMenu = null;
	private HelpMenu helpMenu = null;
	private com.cannontech.common.gui.util.TreeViewPanel ivjTreeViewPanel = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStartDateComboBox = null;
	private com.cannontech.jfreechart.chart.YukonChartPanel freeChartPanel = null;
	private TrendDataAutoUpdater trendDataAutoUpdater;
	private javax.swing.JRadioButton ivjCurrentRadioButton = null;
	private javax.swing.JPanel ivjGraphTabPanel = null;
	private javax.swing.JRadioButton ivjHistoricalRadioButton = null;
	private javax.swing.JButton ivjRefreshButton = null;
	private javax.swing.JLabel ivjStartDateLabel = null;
	private javax.swing.JComboBox ivjTimePeriodComboBox = null;
	private javax.swing.JLabel ivjTimePeriodLabel = null;
	private com.cannontech.common.gui.util.JEditorPanePrintable ivjTabularEditorPane = null;
	private javax.swing.JSlider ivjTabularSlider = null;
	private javax.swing.JPanel ivjTabularTabPanel = null;
	private javax.swing.JPanel ivjSliderPanel = null;
	private javax.swing.JScrollPane ivjTabularTabScrollPane = null;
	private javax.swing.JSplitPane ivjLeftRightSplitPane = null;
	private javax.swing.JTabbedPane ivjTabbedPane = null;
	private javax.swing.JPanel ivjTrendDisplayPanel = null;
	private javax.swing.JPanel ivjTrendSetupPanel = null;
	private com.cannontech.common.gui.util.JEditorPanePrintable ivjSummaryEditorPane = null;
	private javax.swing.JScrollPane ivjSummaryTabScrollPane = null;
	private javax.swing.JSlider ivjStartTimeJSlider = null;
	private javax.swing.JTextField ivjStartTimeTestField = null;
	private AdvancedOptionsPanel advOptsPanel = null;
/**
 * This method needs to be implemented for the abstract class JCValueListener.
 *  JCValueListener is the DatePopupComboBox's listener.  This particular method is
 *  not needed for catching value changes.  ValueChanged(JCValueEvent) is used for that.
 * Insert the method's description here.  SN 
 * Creation date: (7/19/2001 12:45:10 PM)
 * @param event com.klg.jclass.util.value.JCValueEvent
 */
/*
public void valueChanging(com.klg.jclass.util.value.JCValueEvent event)
{
}*/
/**
 * GraphClient constructor comment.
 */
public GraphClient() {
	super();
	initialize();
}
/**
 * GraphClient constructor comment.
 */
public GraphClient(javax.swing.JFrame rootFrame)
{
	super();
	setGraphClientFrame(rootFrame);
	initialize();
	initializeSwingComponents();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event) 
{
	if( event.getSource() == getRefreshButton() 
		|| event.getSource() == getViewMenu().getRefreshMenuItem())
	{
		//Don't force a refresh unless some option or other field has been updated.
		//The updateTrend flag should be set at the other field's level
		//getGraph().setUpdateTrend(true);	
		com.cannontech.clientutils.CTILogger.info(" don't change model");
		refresh();
	}
	else if (event.getSource() == getStartDateComboBox())
	{
		// Need to make sure the date has changed otherwise we are doing a billion updates on the one stateChange.
		if( getStartDate().compareTo((Object)getStartDateComboBox().getSelectedDate()) != 0 )
		{
			setStartDate(getStartDateComboBox().getSelectedDate());
			if( currentWeek != NO_WEEK)
				currentWeek = FIRST_WEEK;
			refresh();
		}
	}
	else if( event.getSource() == getViewMenu().getDefaultRadioButtonItem() )
	{	
		getGraph().setViewType( GraphRenderers.DEFAULT);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getLineRadioButtonItem() )
	{	
		getGraph().setViewType( GraphRenderers.LINE);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getLineAreaRadioButtonItem() )
	{
		getGraph().setViewType( GraphRenderers.LINE_AREA);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}

	else if( event.getSource() == getViewMenu().getLineShapesRadioButtonItem() )
	{
		getGraph().setViewType( GraphRenderers.LINE_SHAPES);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getLineAreaShapesRadioButtonItem() )
	{
		getGraph().setViewType( GraphRenderers.LINE_AREA_SHAPES);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}

	else if( event.getSource() == getViewMenu().getStepRadioButtonItem() )
	{	
		getGraph().setViewType( GraphRenderers.STEP);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getStepAreaRadioButtonItem() )
	{
		getGraph().setViewType( GraphRenderers.STEP_AREA);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}

	else if( event.getSource() == getViewMenu().getStepShapesRadioButtonItem() )
	{
		getGraph().setViewType( GraphRenderers.STEP_SHAPES);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getStepAreaShapesRadioButtonItem() )
	{
		getGraph().setViewType( GraphRenderers.STEP_AREA_SHAPES);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}

	else if( event.getSource() == getViewMenu().getStepRadioButtonItem() )
	{
		getGraph().setViewType( GraphRenderers.STEP);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}

	else if( event.getSource() == getViewMenu().getBarRadioButtonItem())
	{
		getGraph().setViewType( GraphRenderers.BAR);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if ( event.getSource() == getViewMenu().getBar3DRadioButtonItem())
	{
		getGraph().setViewType( GraphRenderers.BAR_3D);
		savedViewType = getGraph().getViewType();
		refresh();
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}	
	else if( event.getSource() == getOptionsMenu().getResetPeaksAllPointsMenuItem())
	{
		ResetPeaksPanel peaksPanel = new ResetPeaksPanel();
		boolean hasChanged = peaksPanel.showResetDialog(getGraphParentFrame());
		getGraph().setUpdateTrend(hasChanged);
	}
	else if( event.getSource() == getOptionsMenu().getResetPeakSelectedTrendMenuItem())
	{
		/*TODO Need to update the selected GDEF correctly to cause a reload.*/
		Object selected = getTreeViewPanel().getSelectedItem();

		if (selected == null)
			showPopupMessage("Please select a Trend to Edit from the List", javax.swing.JOptionPane.WARNING_MESSAGE);
		else
		{
			if (selected instanceof LiteGraphDefinition)
			{
				ResetPeaksPanel peaksPanel = new ResetPeaksPanel(GraphDataSeries.getAllGraphDataSeries(new Integer(((LiteGraphDefinition)selected).getGraphDefinitionID())));
				boolean hasChanged = peaksPanel.showResetDialog(getGraphParentFrame());
				getGraph().setUpdateTrend(hasChanged);
			}
		}
	}
	else if( event.getSource() == getOptionsMenu().getLoadDurationMenuItem())
	{
		boolean isMasked = getOptionsMenu().getLoadDurationMenuItem().isSelected();
		getTrendProperties().updateOptionsMaskSettings(GraphRenderers.LOAD_DURATION_MASK, isMasked);
		refresh();
	}
	else if( event.getSource() == getOptionsMenu().getNoneResMenuItem())
	{
		getTrendProperties().setResolutionInMillis(1L);
	}
	else if( event.getSource() == getOptionsMenu().getSecondResMenuItem())
	{
		getTrendProperties().setResolutionInMillis(1000L);	
	}
	else if( event.getSource() == getOptionsMenu().getMinuteResMenuItem())
	{
		getTrendProperties().setResolutionInMillis(1000L * 60L);	
	}
	else if( event.getSource() == getOptionsMenu().getPlotYesterdayMenuItem())
	{
		com.cannontech.clientutils.CTILogger.info("yesterday change");
		boolean isMasked = getOptionsMenu().getPlotYesterdayMenuItem().isSelected();
		getGraph().setUpdateTrend(true);
		refresh();
	}
	else if( event.getSource() == getOptionsMenu().getMultiplierMenuItem())
	{
		boolean isMasked = getOptionsMenu().getMultiplierMenuItem().isSelected();
		getTrendProperties().updateOptionsMaskSettings(GraphRenderers.GRAPH_MULTIPLIER_MASK, isMasked);
		refresh();
	}
	else if( event.getSource() == getOptionsMenu().getPlotMinMaxValuesMenuItem())
	{
		boolean isMasked = getOptionsMenu().getPlotMinMaxValuesMenuItem().isSelected();
		getTrendProperties().updateOptionsMaskSettings(GraphRenderers.PLOT_MIN_MAX_MASK, isMasked);
		refresh();
	}
	else if( event.getSource() == getOptionsMenu().getShowLoadFactorMenuItem())
	{
		boolean isMasked = getOptionsMenu().getShowLoadFactorMenuItem().isSelected();
		getTrendProperties().updateOptionsMaskSettings(GraphRenderers.LEGEND_LOAD_FACTOR_MASK, isMasked);
		refresh();
	}
	else if( event.getSource() == getOptionsMenu().getShowMinMaxMenuItem())
	{
		boolean isMasked = getOptionsMenu().getShowMinMaxMenuItem().isSelected();
		getTrendProperties().updateOptionsMaskSettings(GraphRenderers.LEGEND_MIN_MAX_MASK, isMasked);
		refresh();
	}
	else if( event.getSource() == getOptionsMenu().getAdvancedOptionsMenuItem())
	{
		TrendProperties props = getAdvOptsPanel().showAdvancedOptions(getGraphParentFrame());
		if (props != null)
		{
//			getCommandLogPanel().setVisible( defaults.getShowMessageLog() );			
//			ycClass.setYCDefaults(defaults);
		}
		advOptsPanel = null;
	}
	
	else if( event.getSource() == getTimePeriodComboBox())
	{
		updateTimePeriod( );
		refresh();
	}
	else if( event.getSource() == getTrendMenu().getCreateMenuItem())
	{		
		create( );
	}
	else if( event.getSource() == getTrendMenu().getEditMenuItem())
	{
		edit( );
	}
	else if( event.getSource() == getCurrentRadioButton()
			|| event.getSource() == getHistoricalRadioButton())
	{
		toggleTimePeriod();
	}
	else if( event.getSource() == getFileMenu().getExportMenuitem())
	{
		export( );
	}
	else if( event.getSource() == getFileMenu().getPrintMenuItem())
	{
		print( );
	}
	else if( event.getSource() == getTrendMenu().getDeleteMenuItem())
	{
		delete( );
	}
	else if( event.getSource() == getHelpMenu().getHelpTopicsMenuItem())
	{
		com.cannontech.common.util.CtiUtilities.showHelp( HELP_FILE );
	}
	else if(event.getSource() == getHelpMenu().getAboutMenuItem())
	{
		about( );
	}
	else if( event.getSource() == getFileMenu().getExitMenuItem() )
	{
		exit();
	}
	else if( event.getSource() == getTrendMenu().getGetDataNowAllMetersMenuItem())
	{
		getGraph().getDataNow(null);
	}
	else if( event.getSource() == getTrendMenu().getGetDataNowSelectedTrendMenuItem())
	{
		java.util.List trendPaobjects = GraphFuncs.getLiteYukonPaobjects(getGraph().getGraphDefinition().getGraphDefinition().getGraphDefinitionID().intValue());
		getGraph().getDataNow(trendPaobjects);
	}
	/*else if( event.getSource() == getOptionsMenu().getStatCarrierCommReportMenuItem())
	{
		com.cannontech.analysis.data.statistic.StatisticCarrierCommData data = new com.cannontech.analysis.data.statistic.StatisticCarrierCommData("DEVICE", "CARRIER", "Monthly");
		runReport(data);
	}
	else if( event.getSource() == getOptionsMenu().getStatCommChannelReportMenuItem())
	{
		com.cannontech.analysis.data.statistic.StatisticCommChannelData data = new com.cannontech.analysis.data.statistic.StatisticCommChannelData("PORT", "PORT", "Monthly");
		runReport(data);
	}
	else if( event.getSource() == getOptionsMenu().getStatDeviceCommReportMenuItem())
	{
		com.cannontech.analysis.data.statistic.StatisticDeviceCommData data = new com.cannontech.analysis.data.statistic.StatisticDeviceCommData("DEVICE", "Monthly");
		runReport(data);
	}
	else if( event.getSource() == getOptionsMenu().getStatTransmitterCommReportMenuItem())
	{
		com.cannontech.analysis.data.statistic.StatisticTransmitterCommData data = new com.cannontech.analysis.data.statistic.StatisticTransmitterCommData("DEVICE", "TRANSMITTER", "Monthly");
		runReport(data);
	}*/
	else
	{
		com.cannontech.clientutils.CTILogger.info(" other action");
	}
}

/**
 * Display the dialog with about information.
 */
public void about( ) 
{
	AboutDialog aboutDialog = new AboutDialog(getGraphParentFrame(), "About Trending", true );

	aboutDialog.setLocationRelativeTo( getGraphParentFrame() );
	aboutDialog.setValue(null);
	aboutDialog.show();
}
/**
 * Display a Create Graph Panel.  Set gDef based on the value returned from the create panel.
 */
public void create( )
{
	createPanel =  new CreateGraphPanel();
	GraphDefinition gDef = createPanel.showCreateGraphPanelDialog( getGraphParentFrame());

	if( gDef != null)
	{
		getGraph().create(gDef);
		getTreeViewPanel().refresh();
		getTreeViewPanel().selectObject(gDef);
	}
	createPanel = null;
}
/**
 * Delete the gDef selected in the tree panel.
 */
public void delete( )
{
	Object selected = getTreeViewPanel().getSelectedItem();

	if (selected != null && selected instanceof LiteBase)
	{
		int option = javax.swing.JOptionPane.showConfirmDialog( getGraphParentFrame(),
	        		"Are you sure you want to permanently delete '" + ((LiteGraphDefinition)selected).getName() + "'?",
	        		"Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
		if (option == javax.swing.JOptionPane.YES_OPTION)
		{
			DBPersistent dbPersistent = LiteFactory.createDBPersistent((LiteBase)selected);
			getGraph().delete(dbPersistent);
			getTreeViewPanel().refresh();
		}
	}
}
/**
 * Displays the Create Graph Panel for the gDef selected in the treeViewPanel.
 * Sets gDef based on the returned updated value.
 */
public void edit( )
{
	java.awt.Cursor savedCursor = null;
	createPanel =  new CreateGraphPanel();
	try
	{
		Object selected = getTreeViewPanel().getSelectedItem();

		if (selected == null)
			showPopupMessage("Please select a Trend to Edit from the List", javax.swing.JOptionPane.WARNING_MESSAGE);
			
		//Set cursor to show waiting for update.
		savedCursor = this.getCursor();
		this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
		
		if (selected instanceof LiteBase)
		{
			GraphDefinition gDef = (GraphDefinition)LiteFactory.createDBPersistent((LiteBase)selected);
			Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, gDef);
			gDef = (GraphDefinition)t.execute();			

			createPanel.setValue(gDef);
			gDef = createPanel.showCreateGraphPanelDialog(getGraphParentFrame());
			
			if (gDef != null)	// 'OK' out of dialog to continue on.
			{
				getGraph().update(gDef);
				getTreeViewPanel().refresh();
				getTreeViewPanel().selectObject(gDef);	//inits a valueChanged event.			
				updateCurrentPane();
			}
		}
	}
	catch (Exception e)
	{
		e.printStackTrace();
	}
	finally
	{
		this.setCursor(savedCursor);
		createPanel = null;
	}

}
/**
 * Displays SaveAsJFileChooser based on the currently selected tab from trendProperties.
 */
public void export()
{
	SaveAsJFileChooser chooser = null;
	
	switch( getTrendProperties().getViewType() )
	{
		case GraphRenderers.TABULAR:
			chooser = new SaveAsJFileChooser(
				CtiUtilities.getExportDirPath(), getTrendProperties().getViewType(), 
				getGraph().getHtmlString(), getTrendModel().getChartName().toString(), getTrendModel());
				break;
				
		case GraphRenderers.SUMMARY:
			chooser = new SaveAsJFileChooser(
				CtiUtilities.getExportDirPath(), getTrendProperties().getViewType(), 
				getGraph().getHtmlString(), getTrendModel().getChartName().toString());
				break;

		default:
			chooser = new SaveAsJFileChooser(
				CtiUtilities.getExportDirPath(), getTrendProperties().getViewType(), 
				getFreeChart(),	getTrendModel().getChartName().toString(), getTrendModel());
				break;
				
	}		
}
/**
 * Update the selected gDef from treeViewPanel.  Calls updateCurrentPane() to actually update the display.
 */
public void refresh( )
{
	java.awt.Cursor savedCursor = null;

	try
	{
		//Set cursor to show waiting for update.
		savedCursor = this.getCursor();
		this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

		// Check that a trend from the tree has been selected.
		if ( getTreeViewPanel().getSelectedItem() == null)
		{
			showPopupMessage("Please select a Trend from the list", javax.swing.JOptionPane.WARNING_MESSAGE);
			return;
		}

		timeToUpdateSlider = true; //flags that update button was selected
		updateCurrentPane();

	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.info("-> Null pointer Exception - GraphClient.ActionPerformed on UpdateButton");
		e.printStackTrace();
	}
	finally
	{
		getGraphParentFrame().setTitle("Yukon Trending - ( Updated " + extendedDateTimeformat.format(new java.util.Date( System.currentTimeMillis() ) )+ " )" );
		this.setCursor(savedCursor);
//		timeToUpdate = false; //reset flag
	}
}
/**
 * Updates currentWeek and period values based on TimePeriod selected. 
 */
public void updateTimePeriod( )
{
	getGraph().setPeriod( getTimePeriodComboBox().getSelectedItem().toString() );
	if (getCurrentRadioButton().isSelected()
		|| getTimePeriodComboBox().getSelectedItem().toString().equalsIgnoreCase(com.cannontech.util.ServletUtil.ONEDAY.toString())
		|| getTimePeriodComboBox().getSelectedItem().toString().equalsIgnoreCase(com.cannontech.util.ServletUtil.THREEDAYS.toString())
		|| getTimePeriodComboBox().getSelectedItem().toString().equalsIgnoreCase(com.cannontech.util.ServletUtil.ONEWEEK.toString()))
		currentWeek = NO_WEEK;
	else
		currentWeek = FIRST_WEEK;
		
	if( ! getTimePeriodComboBox().getSelectedItem().toString().equalsIgnoreCase(ServletUtil.TODAY.toString()) &&
		!getTimePeriodComboBox().getSelectedItem().toString().equalsIgnoreCase(ServletUtil.ONEDAY.toString()) )
	{
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
	}
	else
	{
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
	}
	getGraph().setUpdateTrend(true);
}
/**
 * Updates the editable items and start date display when period selection changes between current and historical.
 */
public void toggleTimePeriod( )
{
	//ADD CODE FOR WHEN CURRENT/HISTORICAL IS SELECTED 2 TIMES IN A ROW!!!
	//Put action events from getTimePeriodComboBox on hold until the method ends
	getTimePeriodComboBox().removeActionListener(this);
	getStartDateComboBox().removeActionListener(this);
	
	if( getCurrentRadioButton().isSelected() )
	{
		histDate = getStartDateComboBox().getSelectedDate()			;
		histIndex = getTimePeriodComboBox().getSelectedIndex(); //save current period
		histWeek = currentWeek;
		
		getStartDateLabel().setEnabled(false);
		getStartDateComboBox().setEnabled(false);
		getTimePeriodComboBox().removeAllItems();

		for (int i = 0; i < ServletUtil.currentPeriods.length; i++)
			getTimePeriodComboBox().addItem(ServletUtil.currentPeriods[i]);

		getTimePeriodComboBox().setSelectedIndex(currIndex); //set to saved currentPeriod
		getStartDateComboBox().setSelectedDate(ServletUtil.getToday()); //set to currentDate
		setStartDate(getStartDateComboBox().getSelectedDate());
		currentWeek = NO_WEEK;
	}
	else if ( getHistoricalRadioButton().isSelected() )
	{
		currIndex = getTimePeriodComboBox().getSelectedIndex(); //save current period
		currentWeek = histWeek;
		getStartDateLabel().setEnabled(true);
		getStartDateComboBox().setEnabled(true);
		getStartDateComboBox().setEditable(true);
		getTimePeriodComboBox().removeAllItems();

		// -- Fill combo box with historical time periods
		for (int i = 0; i < ServletUtil.historicalPeriods.length; i++)
			getTimePeriodComboBox().addItem(ServletUtil.historicalPeriods[i]);

		getTimePeriodComboBox().setSelectedIndex(histIndex); //set to saved histPeriod
		if( histDate != null)
		{
			getStartDateComboBox().setSelectedDate(ServletUtil.parseDateStringLiberally( (dateFormat.format( histDate)).toString() )); //set to saved histDate
			setStartDate(getStartDateComboBox().getSelectedDate());
		}
		else
			com.cannontech.clientutils.CTILogger.debug("Historical date it null, what should we do???");
	}

	// -- Put the action listener back on the timePeriodComboBox	
	getTimePeriodComboBox().addActionListener(this);
	getStartDateComboBox().addActionListener(this);
	updateTimePeriod();
}

/**
 * Display a printer job dialog, prints the currently viewed tab.
 */
public void print( )
{
	PrinterJob pj = PrinterJob.getPrinterJob();
	if (pj.printDialog())
	{
		PageFormat pf = new PageFormat();
		
		try
		{
			Paper paper = new Paper();
			if( getTabbedPane().getSelectedComponent() == getGraphTabPanel())
			{
				pf.setOrientation(PageFormat.LANDSCAPE);
				paper.setImageableArea(30, 40, 552, 712);
				pf.setPaper(paper);
				pj.setPrintable(getFreeChartPanel(), pf);
			}
			else if( getTabbedPane().getSelectedComponent() == getTabularTabScrollPane())
			{
				pf.setOrientation(PageFormat.PORTRAIT);
				paper.setImageableArea(72, 36, 468, 720);
				pf.setPaper(paper);
				pj.setPrintable(getTabularEditorPane(), pf);
			}
			else if( getTabbedPane().getSelectedComponent() == getSummaryTabScrollPane())
			{
				pf.setOrientation(PageFormat.PORTRAIT);
				paper.setImageableArea(40, 40, 542, 712);
				pf.setPaper(paper);
				pj.setPrintable(getSummaryEditorPane(), pf);
			}
			pj.print();
		}
		catch (IllegalArgumentException ex)
		{
			ex.printStackTrace(System.out);
		}
		catch (java.awt.print.PrinterException ex)
		{
			ex.printStackTrace();
		}
	}
	// FIX to keep the GraphClient frame on top after calling the printDialog.
	// JDK1.4 should have fixed the issue but I(SN) have still seen inconsistencies with focus.
	getGraphParentFrame().toFront();//keeps the main frame in front focus
}

/**
 * Add action listeners to each JMenuItem in menu.
 * @param menu javax.swing.JMenu
 */
public void addMenuItemActionListeners(JMenu menu)
{
	JMenuItem item;

	for (int i = 0; i < menu.getItemCount(); i++)
	{
		item = menu.getItem(i);

		if (item != null)
		{
			menu.getItem(i).addActionListener(this);
			if( item instanceof javax.swing.JMenu)
			{
				for (int j = 0; j < ((JMenu)item).getItemCount(); j++)
				{
					if( ((JMenu)item).getItem(j) != null)
						((JMenu)item).getItem(j).addActionListener(this);
				}
			}
		}
				
	}
}

/**
 * Returns HTML string buffer for summary, usage or tabular displays.
 * @param htmlBuffer HTMLBuffer
 * @return StringBuffer.toString()
 */
private String buildHTMLBuffer( HTMLBuffer htmlBuffer)
{
	StringBuffer returnBuffer = null;

	try
	{
		returnBuffer = new StringBuffer("<html><center>");
		TrendModel tModel = getTrendModel();
		{
			htmlBuffer.setModel( tModel);

			if ( htmlBuffer instanceof TabularHtml)
			{
				((TabularHtml) htmlBuffer).setTabularStartDate(tModel.getStartDate());
				((TabularHtml) htmlBuffer).setTabularEndDate(tModel.getStopDate());
				((TabularHtml) htmlBuffer).setResolution(getTrendProperties().getResolutionInMillis());

				formatDateRangeSlider(tModel, (TabularHtml)htmlBuffer);
			}
			htmlBuffer.getHtml( returnBuffer );
		}
	}
	catch(Throwable t )
	{
		t.printStackTrace();
	}
	return returnBuffer.toString();
}

/**
 * Writes the current application state to a file for convenient default startup display.
 * Sends CLIENT_APP_SHUTDOWN message to dispatch before exitting.
 * Creation date: (9/25/2001 11:12:24 AM)
 */
public void exit()
{
	getTrendProperties().writeDefaultsFile();
	try
	{
		if ( getClientConnection() != null && getClientConnection().isValid() )  // free up Dispatches resources
		{
			Command command = new Command();
			command.setPriority(15);
			command.setOperation( Command.CLIENT_APP_SHUTDOWN );
			getClientConnection().write( command );
			getClientConnection().disconnect();
		}
	}
	catch ( java.io.IOException e )
	{
		e.printStackTrace();
	}

	System.exit(0);

}

/**
 * Format the tabular view Slider.
 * Returns the currently selected value from the slider.
 * @param model
 * @param htmlData
 * @return valueSelected
 */
private int formatDateRangeSlider(TrendModel model, TabularHtml htmlData)
{
	String timePeriod = getGraph().getPeriod();
	int valueSelected = Integer.MIN_VALUE;	//some number not -1 or greater

	setSliderKeysAndValues(model.getStartDate(), model.getStopDate());
	int valuesCount = sliderValuesArray.length;	//number of days in time period

	if( timePeriod.equalsIgnoreCase( ServletUtil.ONEDAY) ||
		timePeriod.equalsIgnoreCase(ServletUtil.TODAY) || 
		(getTrendProperties().getOptionsMaskSettings() & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK)  //1 day
	{
		//With load duration, we show all values at the same time.
		getSliderPanel().setVisible(false);
	}
	else
	{
		if(timeToUpdateSlider == true)	//update button pushed ("new start date selected")
		{
			// SET SLIDER LABELS
			if( valuesCount > 7)	//More than one week.
				setSliderLabels(0, 7);
			else 	//One week or less.
				setSliderLabels(0, (valuesCount - 1));	//always start with the min day, max is length - 1

			// SET CURRENT SLIDER VALUE Selected.
			if( getCurrentRadioButton().isSelected() )	//today and backwards
			{
				getTabularSlider().getModel().setValue(valuesCount - 1);
			}
			else if (getHistoricalRadioButton().isSelected()) // some day and forward
			{
				getTabularSlider().getModel().setValue( 0 );
			}
				
			timeToUpdateSlider = false;
		}

		// Set up the Slider labels and size, this method also calls setSliderLabels
		valueSelected = setLabelData (getTabularSlider().getMinimum(), getTabularSlider().getMaximum(), getTabularSlider().getValue());
		getTabularSlider().setValue(valueSelected);
		
		GregorianCalendar cal = new GregorianCalendar();
		// If a date is indicated on the slider, just do that day....
		if( valueSelected > Integer.MIN_VALUE)
		{
			// temporarily set the end date for only one day's data
			cal.setTime((Date)(sliderValuesArray[valueSelected]).clone());
			cal.add(Calendar.DATE, 1);

			htmlData.setTabularEndDate(cal.getTime());

			// temporarily set the start date for only one day's data
			cal.setTime( ((java.util.Date)sliderValuesArray[valueSelected]) );
			htmlData.setTabularStartDate(cal.getTime());

		}

		getSliderPanel().setVisible(true);	
	}

	return valueSelected;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G8BDBF2AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BD8FDCD5C556FE1BED6EAEDB6C0AAD1A3524D8242CD1D2CB9BA5EDD824D4D434D1222111D119D2726D924B6EDE01072828E8140FF4837FD43831860AC6CAAB7FAAACECC15EAA05FFF870AE7064715E73F111E79F355F1919FB665EF75F9F50DF506FF46F1C19B3674C19B3B3E74E1DF98A0545C13A55D9050230DAA748FF9F2D96043CBEC1D87471955FA0AE6F1A8C1ECF7EF6GEC96DE7FA8871EFA48
	2BFABDC3D7A0945B72A0DD866974348C5DD6F8FFC378695BD1A23CF8619388F9F9BF2FFB4FB71F1A5548E78DCD5FF1AE9F1E9B814A81E7G883A0670FF781CDE46BBA13DD1FEA730BA8B6472A7E53EAD522B7049F2B93DBFFBC650B2A967364B799224C999BFF88679ABF52E2552DDF928CACE1F7AC51010B67F3D2C8D04172AFE8461970CF6377CAC91E6D3C5E48BBBFBC9632B1B7BFA7E55E56B5F5BE534DAEDD6C96C6A5D5B63B05AFB8F9D7A1CBC6AADE651AA89C240AF08FCC1C5F91416704D50B1D99E5BD0
	AD738A688B92122697F915A5C116A1D3A5CB58027559213FA4F5EF6E4B17692AA1AD81B01EDF77A14FD44059A7925C9CBDA9DB8A6FB5GED93D116CF5EC7D95A7E6FD6A13D73E856A6AF3204CFE4B2A4CED4E471FBFCD2F6916D6758AC52D77A008CDDB5C0B3008490G908E300D5A57D52B7F814F5ACE295E565FEF33565A6DF5961363184DA4DA607D50A14811F14745AE1B43A4884C06E69415F91087A1B64055FE14BEBFA9E01F079E3FC91024A3D9BA8FED8BFF32B4ABC92EA3ADC0E99BEBD7EF8EE9D76124
	8CDDB9C0A3C08740B80020C9582E47D66F9F29DDC741CE9CD62325F53053EEF4C8F503E60BC9F4486D5B5A672BFD1E00513DFB49F4CCAFA17B55FAC91C73C5569A8FFAC98C79B0ABF29253C77CA4C5AF0C77992F3C53A63977879D375792BBFC3F0858E19E3C2F16ED1461ABA87E148A4FEC7CAEEA37A4ED013C38EB51EE5BB6E29B1AF226888595DFFAECC3606FEB32AAFF4564E97A1536EF9BF2495C6184FCGC0A8C0B4C0B200901821AB8C447E5D1E7561A86D36D955370C76492C3760A9BAA5077128F5C0B2
	DA3B4493C6075958E9918784394CE2AAFB278B5DF3F4BCF5810F4FC52B68B0CAE61B95EAB7760BE0C8832C4C722C4D240CC3243986F33F888C7A6D82561B167586511BF1C07A0F5DE414C425844B4F4CA23A3CE0BC81B5C28570EE8AA4BA0F24F3D6A53CB783B0DD1DDFC3F0A1100E62FA994A5E824FAF4130C54B21C38D66AEA20651F191791F4FA1F309F88126438651AEDACD70B2A067B9F356AAFA6936F1193F27F8B2C66AC170810E9F1F331961218E3516B15F0F669BA549E16E9C1444D6D1124C569E05BE
	004AB260B27C8684F90C67919B0B07F6AD8748646D7B3B6E8DD57B1AEC363EC13B01741C2C27C1BA4F96CC4E5015CE46756E1FC5E47DEB1A0CF63A228D6D34E1438C216C49239E6D1400E9B2334F61495A3176912DD361B1D75FF60963FE345044F9A40C998FB216195E972EA5FA8F861ED1GC913D16FED3A92B65E6A0C83A20E30FFBB2047C413E2E52C6CD7395EED6CBF391EED2CBD57330D99F3FD5B10B957138D31BCFBAED9DBECC3D615CD8E66EE21E358EA9529501CC4E07E448A83510759EA16CE1ACDBD
	2254EA7E96FC2C12A944361E20E3B09DF412BF85EDE9E8975A526EEFEF94224E6E76381E854E284FB2CDE1B6641C2258921369429A22F351A27603226AED03D609610769781671727CE24D73DCD64AA37658A7DE144BB973F285B20FD886C55E36B063FA22E307399F3A0A8CD85927736BD37BF401A09F717FA261998740E8F7B591DA35B3456C25F0BD11A7854AE58294DE07761216D7249A273221545BAC9626F65E9FCB5696107E934F8F0AB08D13017CA5EFC34A5AAD1A3C93F2FBD8FE6ADAA213C5B58714DD
	C75A7392776F489A41F02B5FA638F6A0B81A0BEB6E3182357FE99A287658F7846C5731BE9BFF1DF63DBA1ACF6A8D86FCB8EFFB5FBA759CD5BBB0E06BB25365C06E07D5FB083E294FFA4EF841685CBBB084B6FD6850974DC620E07E629F69BCC646FD4CF568AF0E5FC8F4ADCC4D5069814A27A23F37573B5BC9717AF5706CB6BB86404FB109CE54593B6BC95B497AA32351AF5B2756C5D4D239D7DDB4CEE5BAB8B5D52B0BEBB690B91C00776332852D5F2C52857810305C76D8C45E7FDF65E7B33B15534773F5E837
	827AD4FFF673EB0226AA7ECCB83C47CCF575EF187FB2FB9351D762D42DDF93C1714B27EA7D1A1FEEC43FA69372BA2E47F5764A0D24DD51100E3F9E5B75C27E86D53B1AEDE6323D40F5ED8399FBB6073947ECC55CE40A838F1ACEC6EC2D5AE2A175A6C31D69G8C37321F60365F10212B3C81F185F4DF5886E97B8D389604EFA263FDBCCF5FCB5341BC6D3FD13BD61032BF54FA41F85C6246A37B872AFFA35765CFB712BEF2727455B4FDD1667506304712CB6D7922ED90BA207257C4DFB5961C4FE2204DC9GF900
	5F7EEB669330B1F85B9019BB3A41F9032157EA973B0402A0C21BEBC55AE1A86F8F90869087108A40F83EEFA334C0E86E9F6C278B049F25BD427A895ECDC1D8F65DA019431A44EE69383927D7EA35DB4C929966381FDE39C9BD774685B199536119FFA3783F54AEFBAD38275C8D38D3B7E29BAABA082F4C67113D46A1096CF74D26BAE3CF9355881BCB42281D917A009BF1AED9CC6B0C05F4B2C0660D381EB95F433DE768CDD30426F1DFF85C87A799AA328A27B1FB2A1A264C83CE58FBB21EE5D664A9C1FE68B402
	7B46DA2831154D50F9FB89AF1A6F6712CF660DBD02BA7FDE17FCA26FF7F96A7CD417FC1D0A1E35ED413B58364C85A102650EAA2FFB7CB439CD7A696C59D83DCD88173ED95BBC1D44D77E7626406D2FF7A369832BB8D402DD303781DCFAABF15ECB4B4BCA4A77B05EC1C36A7935EA5DC12F73EB004CB3C4FE0677EF13570D6B763A743732EEF02BE23AFF3FG570E2469585F2F59C8FF6B03E15DGE88C46F942EA393CFD6E70E5525DF149F4EC4E8E6BA772F78433B9FBB5475FC57196951E0D19E4BEE61C101798
	0CF3791E2DE43C554C00F184A08178810482444C40397D0DCD654288FBCB034D5E848E2EC5356DE16D2B30DE5E7E7F3EB5E81B613F1BA6C47CB8FD75FAAF7611B8C30EBF494F24C35BA81E35AFD2CF5AD7F69374B10085E0BCC0A0C058CD583E76CDE52CFD1F1A4CD22DA4993BFA2967000DBBE1960794B4199C190C0F362BED38E0CAC956EAAFB2C65F44E40B3BC96BA36C26FED84E4D993A121BD11E0637BE00678963B9D953C4AC1B03CA8348E6074DAEBA240B8CCBBDC059164A8AC8BBEAEE763C2E99B478DE
	C8B31B6936935B983E19590C0E5B52F90A77D76119AD255331C3524110179702E3674EFE015B75DC557BCB16315A27A963A83BFFACF930FE5937557BBE6E632D1E77F1C736FA5E47FD3A5577BE6E58D66F7BB843D675BE0E59D15DB6B257DB20AFA6G0482C481440720CDAD5D3A135938877B7E94B6F675B69A7911107FBC3D7736466AC70E97BD29A77EF627CD12EC7D24FE44AF54137E12ECF60AE438A4BD51110378832C280CDF26A7E343823E82C133F61EA5D33E1CFF2168DDEF6384DA7585AC897F6EBC87
	9E36415843E9763C3DD12DF7B6FC643C7237098E49DE57EC34183F25DB14D6A8E86DE1FEE7BDDDC7D2C127F93720AF7A738274CF37F35CA46A77D8A0BD61969C9B96BB41CD05F484478DD1DC8224B3B96E2FB62D9D931ACB355788AFB4F2AC577EC37829BA1FE3650BED5E6D27E55B4876734DB63A0703FEB7DC340B8CF7F69B5DC7F4F78F085CCECD5B8AB86E68C033ED40CCFA0D79A25F9410B543E43EE006412372CB9D344F084D154C043D7DCC028BAEE81065AF1F692E3B3F49FF59322E9A20CC9B401BF25A
	B2D3D9EF996FD05BA605F7132D0B6DF7997FF954BE2E0742D1B351AF9E48A63E1741A10AE435219BDFDE57B4F1B34B331A9A4C83F6F06EB11F794C3758DDFCE628136CFB8AE6E568F68330B247BE5146B4C819EA48EF1E05B27C240048DFA960F721A7291EC8BF77C219E2DAC690880E7D259E7104E3BE611EF784815772A5793816EF1FF51D5070D47A6ADD0267F5B2E89653FD6AACE51DCC3F6A5AECA6DF220DBC77159F147D3F6C6F77891E3FF5F5D1C7E5EF4F20F96F20E4068D29C6312CFDCB6C583E52D074
	8B27D4D33F8A52CEG7FD07CDEF565A7D8B60417AD2CA5E513A01D8ED0709B9C434F9428FD1ED240D77F467798E6F35B19E2E2034DD05682608F90829089908F10821089D0B09B76C4GB5G8600DE00E1GBF00A0007059B867DB5FA17E3EB7BFC25E9851095FE1E47BF5A67FB23BAF1FAD866ACF1C6DA93E307193220F5459DA1FF4AB45E74E567A24C1556813665D8AED3E9575177AAFAD7F38DB95FE49373A6B127119C07BA37DD62D9FB31162734334FE4C5B35684714C05E78B01C3FDF25F61FA217E738BF
	D21CF09B43B17DBA0A3D4FF1970BC71E63FED46CF90D1CD06C3EC67E3C58739AB931D83DC6B2DCE0B1E9D38F74EF9BAFF7BD47FD4DF153E81D84F752EC12FAB9BE044AC5700DA2E1087859D42EA10AEB13F1F3D4382FE5DCC4B1192721493DB687525ED3CC62B15DE60B05256F23E54CD6183639BE9E20B21A6DC6155C8BB86EEB5E97FF3173B577B66C338367B1A654F89B5AD161A78237693F2B5EEF2A9EFBFFF46E1872E0EDDEF30434799434574E7570639A54CDG5743553584978569840E1BCCF1E910565F
	0E38125A11E4A6F5784ACF70114FF8FCBCA20F11FC91EFFE8766E58B467772856E47D153E6786D4E8D8A9ECDDBE83A1D4E6567CBFC54DBFDBBEB97DB3F4FD0DB8BGDC506D38F63EF344BD6EF660882E3931373338DBB2BC53EFE77E9E63FBFD8D72AD18632E3B5DB20E7138E7A3991786E3E727E8E2B1B3060FE9A77390758FE09FB1E8E70B9CDB7797542A577D2AB90C37891E4EB9689B3CA5EAFD83D2469F720367E07BAA4E1371CB669936261FABD02F69099757516F3C1E5674C8398DA9F314B51DBC191FF0
	B5B912A337633BEA62B4F8099D9DA8AFAB29A85F03F37769A338F6140403AF960E6B740CA398E7738F47F57A89DAB68652C9G693C6C3C9ADC538B6E40F67E2506249D32EDB03B79A06F6F0247B8133AABE41FA23B8E6BEC3C8379FFA7917E63A09D8890F687727FCA645FB4F859EAB37D26F1A70CCB0072BBB1CEB3301358C9A324CDG93G02G4281E28112G526F44F57FD4092F78818F0E29D6FEB6B63B0EDCDE8C6B7CE5524D3CE4BA361EFFDEC3F4D478DB2D1F51CC71A53F557A99CFF3DBA903BC7F5F62
	FCB73FC64DBF8D70F9913E67B7C617D767CB6E6DD0C7F904A77F68D5EA878D91CC3EB59C7F970ABF2542B3392F20F86A8BC0DEC2844A5DAAEA79D75F2570EB06F753DD1E78A7D05BF25E45783C4171AFD1FC088A4F7857D1BCC9A7C01E74BB342F6FF7FA77FB2E5CB9325FF375CE4FFECF40CEF73FE772CE4FFE4F2FF73A7BBD4134DE0758837345D746FEDE6F4C1DE45EE7F373B1235558C363A6CC1747EA847EFEC2757E6C11317791524714877387165420AFBBBD97F14BEA50177D0B8EF145F568B7945C0D3E
	4417DB50276A389BED6FCF54A6A5C87B8304F1FC76A04147C1BA8DC09F09781FC85AB692BA728C3B5B7BD813FC64396BF11A9B8F1DAA5F9B6DE8E39ECC9767C47CB6D99D097AC96EC61DF5F0DC5EBE54D9985741E273EEF8C6C3BA89C038877665GB5G9DG43GD381A26F617B9FDABE815269G7AFBB3F4D5GEDGCE00C000880004FB31FC23595F0B6C48D30BA77CC6EAB711CF106553666D16F1111A3C8ED51A49E673AADB366059175BBC0ABED9AF573F49C35EDA797906863FC1754EE2BE876BB06613FE
	2FFB9BB1160310BFB7C3D7B2D70979D44ED556996697CAF43C54G3EE9AE7A939B2C04AFBDA7825E02EBDC674A1DA45E8868C10B51810F56AE074DE2A1E5D87E0EE2125FBA58CF4E273864B33F6D602056EFA37E3C93F87B4DC55F6E5FCEF51960392CCC9CBC13F919DA2327B21970AC084A50B1DFF6A27791CB2318AFDB83CFC3945BBFB0FD7E399B75E909722CCF35CE47C11900A8C51FA1D1DAFDD65031948E7828A854E761DBC41F4745EE07B850DBB788CB08156BF3C78B7936DCBF68A087700E9BCDE69B96
	E079815B892FC673GF8C366AE23452508DCE6E2A57525416DEC969DE61B29495889BD46727E717E96173CFADBFF272D4E66C45A6B8BC8BBDBA523C3EAB0CAA2A315739EEC5F2C4ED3C8657C2BEA37E0BEE1E09087244F4C22450479AB1C856A7CAFDAADA430D8FA9F51531EF36A6F3C4B65BEA904BC16EFF649375F477227727CA9BBBC6507717C1BCF2A7323657C941E3F2AD91D1FA9676F7EBD669FEE71C4DF45730BDC721BFE4F727DF87E5FDC6A8F1273E3F87EB1977AC4B9BF67FE7433FB38CF2E3F7FD263
	185B48D4AC6FD57EC5BEB7487D51A11588DE77AA7B4A49E6E57F01BD0C3FF5907D7C667B514F1FBAC4564B89108E8208389F4FB074FC6AE52F218CFE81779AD7B931DDF13C5EFBCE53334B51B0D781D4C5238EAE9A31ECF3B41635F7D299A09D8490910D67AB6E9651DF0E83DC5EBC2CA33B9B77D6B573300E0AEE6AD3C25A9FA00417AD6BC1FED173C656791449646F96D9674677941DCFFEDF51F93139F8841D034A65BD5B2656A24FFA64631D4959F10E70A7F3CA7ABCBC73715DE76A3B8F111BAA323CFDFBBE
	7500FC9E7681E5EF9B737824ECA643DBF56FF81641E562116FE0F0B9A29FC09DBE3603FF27F8906DE3D5371733F2F436E1F40BCF223D35F13A9E9AF773C047E7A2C67B0F962475FF9065F0F0DC8C2F6F0AAFAA04D1465EB5739D2B3339856DC30841BA5FAD75F21E43G7B89BA9BB25A03141674C995275D7E31177EE693A567BB3E85F5EA0F615F66DC76F37100CF09994DBEF0E0D03B8F4BD45195BC64DA476E0770FBFB01130C192A07347B57A20AEFF8C83BFF1DB60463319972429FC2397D1C3E76217A792E
	7C4B66A37F0AB3243E1A795AF30007A93EF93E769C607D27719CE08764C54FC77E5F9BFD71AFDD604A3FFA8172CF2673CE7382AD7F95945F31C04B7FC2A77237C3DE7C8264FF1F5BBEF8A4E0F40FDEA6DDDE776545ABE650B932AAD65B5F33A93EA1D65B5F9FF5737E063C70D8ECEFC50F1A3F7EE1705F9E9E0D1D0A67347B239A20B348343DA3542106E1A86B8F902C22E1712927CB499A9C8178F80065GB90F008F7088468C163CBF627D23CF1DBE6E9F4DF0EB830106C73C4B5E66A54F8278F1G81GA1F299
	46638FBBBC75EF24DCA68E1E49G690FE877B84F1D74C4177F283CA6403372D1D7D9985D07AD042E71D14FF21E823C5DA35DD00BA7FE63653201708C75C8779F0FF488D1C0936F116EB027CBD165E74A6F85F1504FGD5F1DA3D7C2465F2629BCD2AFABA64F7891E9362DCE5E37B6DAB6AB03E71B30E1BD007710DD7F8FC63270338FF1F9A07FB751CA15C3FC7F15CEE0ACB06F4FA9C0E3B374832A53729D8759EB2B476B1227B461487EB73F4A76AA167B154432727892E9452550F219EBEBAC33E81B472F4ADCD
	1BF87A2153A8ABA9B3B67AD84AF97C10E72BDD75B149087A18407511CEF121100E626DFD291334BF162753E8FAB9CF8FF7EA7BAE7687548B63B1490D478F299746E3D637405F43D56F1FF40F3D7D1F98D39E2CEF9BFBF08E1947E32447FA5CE324D7D19FBD7FF158DBBD0EF671A845D5C23A0163AA69DD15E6C877F25C357B512606B9EE3A85E305319C774B8A02CB02F45A63FC6DAC759EEFCFA975FC6EF0C52967F307E9253E6FA8E616FABB174864FB1037D7F882E5AE256D4D07F489471D6F6377B6B86EC4A5
	36F7BC47B5FD036D8D643883F45F938C69E80EFBE58772CD60382FF6A20FE40E4B7E86F9D446A36E16F710C7A347E51C447A8C9C575E4663399C77081E47F3B96EB33A070806F4A2477D2685E9D3B86ED5CEDB75A4622EEEC13ECD9C37125336F1DCB4278D61B88BAF9749F159C5D8AE7F8F08BBCE4B15C03A1A632E2675B5C33A0363D66931BE8B474D213411100E6738C64AA3855242D3081B45F994F25C0FE8FD25102E6638851CC7B347C537E0FDC1CFE1FCF18E0FE50683AE96A08DA0AFA1C3C73EA914A410
	B25B0654F1CAAD309841E13AFECB46CE03D93208AC06E0A5D77A85D6476B27300E5E8477391855F16A34FB9DB713CF2AF27E38CFE90C1047ED555F5B9DE75CE232A07FB07072CF40F60472F8EB08D799B6F53A4BB03FCC11614A5DB41E48E2436C832F9C9BE2B131F73B54B131488476CDA6A5816328B750F15CEF954199B7F731F86B9D67B03E13494BADBBC756B489A698A603E32FA24317FD745E0F5129397753C547FE5E53E09BCFE3EC78B77B9B846FFD270D3557G1D61E9969B26678F1F564606E3695CA9
	81FE5C53A86BAD9539D49E873DE746641DC42EBC4A6F91650A6CC7280C83A239FFA91AD81DA778EFD204F239A7747912BBDB8311325C5BE439635C647EAF1D8393811F824064DAD821485534034444FAED8E7337B62BE43454DA4CBDD6F2DADB266F66B12142E7103E0EEC2D64EF9A62CEDF726A34FAFE06493BCB4EB3BC2315370C4E279D0037F0FEE22522538987945E0EB6649D780C3BDD2F5170B6E8F4956A467BCFF46E08GFC74B3584727DB084DC9B61B45E036133810AC472C030A9C51AD3EF430D6A3C7
	2AC687B98B35F23CCD673F8240EFDF08BAF864A305DF6EF1DF7C56FBE9B772EBF463778AED3781709D8B518E2F60ED72DB682E5B8D9A9EC1D0A6FC21B2FEE25CF81451B64583BEE9A16A565CC26A31BB44EE516190CD6C2A37DC7EC7F44E4ECF04F9A4914B4F503B4E8D2FD6A9BAD9D70472B6A43A4B0B71D414378D4AD89295F947A5EA4FD34FEF71BC57AC25F279C179C0AE5759A24F779107E81B2321DC9CG4345D37ACCC867ADC27A49740E024CCB268D23FDB29720F2917649D2BDEF63A24FFD625246C52E
	ED34ACF26BF7AA0B93706397E1ACFE61A0460242G173288631D4B763A44F359DD47BA9B39D74E2EBA4A714EDD97F0CEAFD90CB17B168B244EB61286183098635EB127557781CBBE4876F89FB07D745E2C20452C3D510B1538F77E04EB653B0E4527B1361B3E98E348B34FA02E7CD9142351C464680574B80040E7316C637CDBC558337CEEE73E176FA2AEEB9F23BF550574A94FE21F461EA3ADE4F67A75A1015B554F767BB8F37651013212FD877688027B770BA48F5F0C307E61434A7B2F2B95DED7D57860D509
	3CD8597EBEBA369C0322A75E2C6E951FA8FCB69FD17854FE63034FFE42E79F67735D0E92F82AD43A37157C9F2F838D6CBDFE7473C603F7993E2BD1E4882DD5E438611D5148407AEBFA9B76D7C9924E2F0F56A9753ED62F547B07E6DF75124FEE159E7B8B3F673D7929D26FF71FA9753E53B2425943FD7B77A0DD5067CA9D77B7AAF5E40ECAB65666F07E7D23ADE9345FA4116732234A7B5AAF957ECBFD715734212CC9213BC26F4BAE89DDB9177BF0914A6DFF49F2DFF3CCF93F7DAB057FA7C5BE789FC47E0C6E49
	FFABF4653E7AEC9F6A1B5125B7ABF4C3FA9F5F91AB4A41B8AB8E6E416F767AACF8FEFCG5B7F86EFFF0C1776B3FE6F9DD75AFA34D561FD2DAFDD53B32E40DA0EFDACF17B3EB43A18494A4B24AB3DF0F95FB7665175C4D8227D3E71884567AF51FE5FB8F98157B4BD648DAF41B32E3B8CE4DF148469CC00122530EF86E882B0AD45D848010FCB856FE7DB5952279CECE56D5AFDDA5DAEA7546537F4B45FCDEEBD6376BB9BAA3AF08FF530F6C752F5ADE629769E4A03941F38D4FB8F65AB935EC3C905BC43F398BFFC
	4A657BDA8460E31F9B0D5C46AEDF7D182429A30D738BE9D35365A5033F1BBC9AFEF739FCD7A8871A06D15155387CE6DE9B50D8C6C527F7219B87B4812322DBF0C1606F712A77539746BE0EF9E1CCF93031D1E6709EA75CEF9879DCEE354173395CBA037B395C4F8D1E4F659EB3284F6532F84F615398DFFD0247D7EBCEE3FC5548632B8FD09FB1847AB2B219FF07684078D1D2B25A696FCD841773FC06EE777308FB18622AA1FD0A638CA6DFBAA77423556FEE8FE5990FFE1FBCAEA5966F69ACA453418AA36A6FEE
	2E3FD5C6545F17DCFF4EAE54553867F90C196AA59852119CB7378D6BD36E37C537E1FD8AAE2E453D5C13AD6E65CE963917BBD564DE6EC38F65AADD4A31B5F25199773BB4E9BC2E9673BC0B8FA641B30D3469B148277B27D39DD8A6FF992C9FGB5G9DG631661BE40F91E5E25E531BB173D03FC2E0B6ECD5921919657134F20193471B442B7E8991E214C6956467DC8FEB86447ACC3792E6942B2C900CB83A8FCA1C3D789E0F881651BA6927EDD64AC0DD5A2D71FE59FA272BFC4CDEC3D58FE9657CF8968C2DF40
	FD1369AC6E7512DF40BD56AFE9596DA9408720A9854B7E28837D98D38A7E9E4DB81718A111070C4D3D827E1E4C6DAE79F85FC749CFF649A7734393AE3FE773160B8EF5AAFA79DC0F08B2CD65F2D6F76359237894EC5321BA6AD73C88639F207AC5ACFB875F7799DE44B63D625626AFDD5AD4EC725D26961336CDF73B34698A2FEDE273434E3317670B15DFA61D79126918EF125BC1F46A772256075BC47101AFEAFD38C0EE0341101777924E3947DCFC8F8B60473DB41A3579304FB3B9811ABAC2B83FDA37B3B911
	F259B8F8A6FB60AD1F3D21B6147E125BEF21D1FC5E4B5AB3C8B5F5F886C98FF943AFA37F5F3934B78970E9AF0F263D799D82FFFFD375FE43593177C5FEBB26BC583CEE6A42397EE13EE6193BF02EEF67EB56F191572C3C6538BE7DCC44B52B12635A3A51B72861384769B7DA932407B9AE34966B73DF4EFF9B2E0E44D2A2F97ADE1ACE60694F6AC6238FC2FF79BABFBBAA9E177B6D1E357D31AEF7BF618FDDEABFA17F955A8FE2090F3A5265F632F5675993041C2D417F4357B88354E38188G88F90579DCEC7DFA
	31077A10462E3E9E07ED50EA4258666ADEA2C794140D85E0F25CDF0BF2243F622E5B7C956A3B2F87B7535F47E06BB6BF5349566615F55A735FC41E1295A8530FC562CF540B64772D71B7A906C4757D556D45E92B3D5DDF2D146568D861FAFF154D8B377033FA81109F3B8263157B77391CC7E571594FAC36A1B9BA0BEB5E2B7CCCF54ECADC5F0AFBC81D5510B6GD8D6E2CC6C9A7A7B792CCDB7563A45B06AED961B23F45F0183E5B26C11F93CDC0B6B6278152813DDF5A47D154DC16F7D33F26F556789EC8D0CDC
	09FBF2291D74D1C9AA2C33G9DGCEGBF00E000880038D45C1B6FBA69EBEF4E02535A3D792D17FC2612515DF319F4FF3C4CB31C93E91FACCF552E0FD3A9BEBDD53BBE3E5B03F61173EA062E77D5FE372BD64DBF9670C92F0EE63D78201E4C9FE99E4A663D46F05B61D98E50A0275BDED34ADAD46F63DEF32F03755FFF5B3D6F9FFF5CBE727E711AF64F7B47C96D6E7B47A96D1E770FC16D6A7DA3434D106BA576233E57B92B5D7B3D4E4964A71F70FE206A7D031E31DFDF9B4614077C5B8FF56E6BEBBEBF775843
	4B9D23E3BAG7ABC74B5DCA3FFD50F6BE69447BDCAFF37B79E524BB92E087EFE2D30AAC327DF05388A3AEFA803F48D47FDCBF186C85BB9EE9E450D0774D40E5B444F55052D42F948D4CFFF0319CC88748643003CDE8C5763377878D5782DAE0A6212A11D4A6917652FC3FA03392BEFG571B3F66A37D76B4ACDB3E017E86304DA13A2ECD0D341DE5D02E0A17ED6CA073A37E6EAEABF73683676A8EDE6E979D64DED63F711C4DC178375A0DDDE6EB8FABBF3383E5307372099D243D83D6235DE0E3823332E93C6C38
	B4BCA7721EF16473AE936B71BB91DBA34E1E96F8DEF47EE85688D6764D8D2E754CBB235473FEC71167FA4A4B603FBD3C5C058E221BF21E3E0F26ABF8FA3D91F930FF4BE55601FD0088FDC5508DB804AD6E71705A2B1FFEAA74ECC3E11610F5D4486D25BF58F1D4508D11DF74F012FFC2007E816DA6CEFB147E20CC52057D07321F1B1E1DA564GA11B100E8AB9E4D681FA872BA79BA6A4DACB0E9A864E8B5F53EA09296C2329ACC173474BDC8590B3C2997219BFC692F4748A28A42C11DD98F8138F6F401BFC1D11
	57C4A0DF06AC48172FB43A64F31004F54A15C6EFC57249254192F6ED7110A4ECE92587DFCA0CA5948FD6EB15047574F6E189392E83C5F43D3D077A7BF9B3682F17735AF5EAF0A973CF17B205EEE548AA32EB04FA5C4B3857C3C640BBA3543355258BFF08FA84390E5291EAA518EA979E05BCC5668B73889CC919FD1230161C2D7830D76A37285F4160B73A8E4455B336DED1F04D7E41ACA16FB3A1375BE831779A4150696C55A42C6F2496CFFCAF2827200950FE88349FD25A43CDC2D65700A4ECD370DD844FF249
	FCA6C21135A623230F56C0C47C30DDD85B6D6869248CA4A12B7D33F6E0CBD3A43B22C9D8A775125F97A1B2CB42D6256AE6B2D14B99075998A63F2BE727FF3479FB3D62G988EFD78BD7FD57F7EEA7A15DB3D7E0670706B4C6771FFDD7B7B2B29D5E45E0D85FC4A6B684756D47DD3707E3B122D7DC60BC5B2DA7AEA2DA6729B13F597A5D1B9DF7B3FD5247E81ACA3E74AE7EB2B085C67D4B47F8FD0CB878834F5C65C50A2GGB0EBGGD0CB818294G94G88G88G8BDBF2AE34F5C65C50A2GGB0EBGG8CG
	GGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8AA3GGGG
**end of data**/
}

/**
 * Returns the getGraph().connection to dispatch.
 * @return com.cannontech.message.util.ClientConnection
 */
public com.cannontech.message.util.ClientConnection getClientConnection() {
	return getGraph().getClientConnection();
}

/**
 * A radio button for selected the current time frame.
 * @return javax.swing.JRadioButton ivjCurrentRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCurrentRadioButton() {
	if (ivjCurrentRadioButton == null) {
		try {
			ivjCurrentRadioButton = new javax.swing.JRadioButton();
			ivjCurrentRadioButton.setName("CurrentRadioButton");
			ivjCurrentRadioButton.setText("Current");
			ivjCurrentRadioButton.setSelected(true);
			ivjCurrentRadioButton.setMinimumSize(new java.awt.Dimension(35, 22));
			ivjCurrentRadioButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjCurrentRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			ivjCurrentRadioButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurrentRadioButton;
}
/**
 * A button group for current and historical radio button exclusive selection.
  * @return javax.swing.ButtonGroup dataViewButtonGroup
 */
private javax.swing.ButtonGroup getDataViewButtonGroup()
{
	if( dataViewButtonGroup == null )
	{
		dataViewButtonGroup = new javax.swing.ButtonGroup();
		dataViewButtonGroup.add( getCurrentRadioButton());
		dataViewButtonGroup.add( getHistoricalRadioButton());
	}
	return dataViewButtonGroup;
}

/**
 * A menu listing FILE type functions.
 * @return FileMenu fileMenu
 */
private FileMenu getFileMenu()
{
	if (fileMenu == null)
	{
		fileMenu = new FileMenu();
		addMenuItemActionListeners(fileMenu);
	}
	return fileMenu;
}

/**
 * A jFreeChart for containing the graph image.
 * @return JFreeChart getGraph().getFreeChart()
 */
public JFreeChart getFreeChart()
{
	return getGraph().getFreeChart();
}

/**
 * A special panel for containing a jfreeChart.
 * @return YukonChartPanel freeChartPanel
 */
private YukonChartPanel getFreeChartPanel()
{
	if( freeChartPanel == null)
	{
		freeChartPanel = new YukonChartPanel(getFreeChart());
		freeChartPanel.setVisible(true);
		freeChartPanel.setPopupMenu(null);	//DISABLE popup properties menu
	}

	// turn all zoom on
	freeChartPanel.setDomainZoomable(true);
	freeChartPanel.setRangeZoomable(true);
	
	return freeChartPanel;
}

/**
 * An instance of the Graph class.
 * @return com.cannontech.graph.Graph graphClass
 */
public Graph getGraph()
{
	if( graphClass == null)
		graphClass = new Graph();
	return graphClass;
}

/**
 * A defined parent frame.
 * @return JFrame graphClientFrame
 */
private javax.swing.JFrame getGraphParentFrame()
{
	return graphClientFrame;
}

/**
 * A panel for containing the freeChart panel.
 * This panel is used as a tabbed pane.
 * Return the GraphTabPanel property value.
 * @return javax.swing.JPanel ivjGraphTabPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGraphTabPanel() {
	if (ivjGraphTabPanel == null) {
		try {
			ivjGraphTabPanel = new javax.swing.JPanel();
			ivjGraphTabPanel.setName("GraphTabPanel");
			ivjGraphTabPanel.setLayout(new java.awt.BorderLayout());
			// user code begin {1}
			ivjGraphTabPanel.add(getFreeChartPanel());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphTabPanel;
}

/**
 * A menu listing HELP type functions.
 * @return HelpMenu helpMenu
 */
private HelpMenu getHelpMenu()
{
	if (helpMenu == null)
	{
		helpMenu = new HelpMenu();
		addMenuItemActionListeners(helpMenu);
	}
	return helpMenu;
}

/**
 * A radio button for selected the historical time frame.
 * Return the HistoricalRadioButton property value.
 * @return javax.swing.JRadioButton ivjHistoricalRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JRadioButton getHistoricalRadioButton() {
	if (ivjHistoricalRadioButton == null) {
		try {
			ivjHistoricalRadioButton = new JRadioButton();
			ivjHistoricalRadioButton.setName("HistoricalRadioButton");
			ivjHistoricalRadioButton.setText("Historical");
			ivjHistoricalRadioButton.setMinimumSize(new java.awt.Dimension(35, 22));
			ivjHistoricalRadioButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
			// user code begin {1}
			ivjHistoricalRadioButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHistoricalRadioButton;
}

/**
 * A split pane to separate the treeviewPanel and the tabbed pane/trend setup information.
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane ivjLeftRightSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JSplitPane getLeftRightSplitPane() {
	if (ivjLeftRightSplitPane == null) {
		try {
			ivjLeftRightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			ivjLeftRightSplitPane.setName("LeftRightSplitPane");
			ivjLeftRightSplitPane.setDividerSize(4);
			ivjLeftRightSplitPane.setDividerLocation(206);
			getLeftRightSplitPane().add(getTreeViewPanel(), "left");
			getLeftRightSplitPane().add(getTrendDisplayPanel(), "right");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftRightSplitPane;
}

/**
 * A menu bar for containing the menu lists.
 * @return javax.swing.JMenuBar menuBar
 */
private JMenuBar getMenuBar()
{
	if (menuBar == null)
	{
		try
		{
			menuBar = new JMenuBar();
			menuBar.add(getFileMenu());
			menuBar.add(getTrendMenu());
			menuBar.add(getViewMenu());
			menuBar.add(getOptionsMenu());
			menuBar.add(getHelpMenu());	
		}
		catch (java.lang.Throwable ivjExc) 
		{
			CTILogger.info(" Throwable Exception in getMenuBar()");
			ivjExc.printStackTrace();
		}
	}
	return menuBar;
}
/**
 * A menu listing OPTIONAS type functions.
 * @return OptionsMenu optionsMenu
 */
private OptionsMenu getOptionsMenu()
{
	if (optionsMenu == null)
	{
		optionsMenu = new OptionsMenu(getTrendProperties().getOptionsMaskSettings(), getTrendProperties().getResolutionInMillis());
		addMenuItemActionListeners(optionsMenu);
	}
	return optionsMenu;
}

/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 10:57:58 AM)
 * @return javax.swing.JButton
 */
private javax.swing.JButton getRefreshButton() {
	if (ivjRefreshButton == null) {
		try {
			ivjRefreshButton = new javax.swing.JButton();
			ivjRefreshButton.setName("RefreshButton");
			ivjRefreshButton.setText("Refresh");
			ivjRefreshButton.setMaximumSize(new java.awt.Dimension(75, 25));
			ivjRefreshButton.setMinimumSize(new java.awt.Dimension(45, 25));
			ivjRefreshButton.setMargin(new java.awt.Insets(2, 12, 2, 12));
			// user code begin {1}
			ivjRefreshButton.addActionListener( this );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshButton;
}
/**
 * Insert the method's description here.
 * Creation date: (3/27/2001 12:06:15 PM)
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getSliderPanel() {
	if (ivjSliderPanel == null) {
		try {
			ivjSliderPanel = new javax.swing.JPanel();
			ivjSliderPanel.setName("SliderPanel");
			ivjSliderPanel.setLayout(new java.awt.FlowLayout());
			ivjSliderPanel.setBackground(new java.awt.Color(255,255,255));
			getSliderPanel().add(getTabularSlider(), getTabularSlider().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSliderPanel;
}
private Date getStartDate()
{
	return getGraph().getStartDate();
}
private void setStartDate(Date newStartDate)
{
	getGraph().setStartDate(newStartDate );
}
/**
 * Return the DateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.DateComboBox getStartDateComboBox() {
	if (ivjStartDateComboBox == null) {
		try {
			ivjStartDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjStartDateComboBox.setName("StartDateComboBox");
			ivjStartDateComboBox.setMinimumSize(new java.awt.Dimension(50, 23));
			// user code begin {1}
			setStartDate(ivjStartDateComboBox.getSelectedDate());
			com.cannontech.clientutils.CTILogger.info(" DAY -> "+ivjStartDateComboBox.getSelectedDate() + " and  " + getStartDate());
			ivjStartDateComboBox.addActionListener(this);
			ivjStartDateComboBox.setEnabled(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDateComboBox;
}
/**
 * Return the StartDateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartDateLabel() {
	if (ivjStartDateLabel == null) {
		try {
			ivjStartDateLabel = new javax.swing.JLabel();
			ivjStartDateLabel.setName("StartDateLabel");
			ivjStartDateLabel.setText("Start Date:");
			ivjStartDateLabel.setMinimumSize(new java.awt.Dimension(30, 14));
			// user code begin {1}
			ivjStartDateLabel.setEnabled(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDateLabel;
}
/**
 * Return the StartTimeJSlider property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getStartTimeJSlider() {
	if (ivjStartTimeJSlider == null) {
		try {
			ivjStartTimeJSlider = new javax.swing.JSlider();
			ivjStartTimeJSlider.setName("StartTimeJSlider");
			ivjStartTimeJSlider.setPreferredSize(new java.awt.Dimension(105, 16));
			ivjStartTimeJSlider.setMaximum(95);
			ivjStartTimeJSlider.setMinimumSize(new java.awt.Dimension(50, 16));
			// user code begin {1}
			java.util.Hashtable labels = new java.util.Hashtable(96);
			int value = 0;
			
			for (int i = 1; i < 25; i++)
			{
				for (int j = 1; j < 5; j++)
				{
//					String key = String.valueOf(i) + ":" + String.valueOf(j * 15);
					labels.put( new Integer(value), new Integer(value));
					value += 900000;
				}
			}
//			ivjStartTimeJSlider.setPaintLabels(true);
//			ivjStartTimeJSlider.setBackground(new java.awt.Color(255,255,255));
//			ivjStartTimeJSlider.setPaintTicks(true);
//			ivjStartTimeJSlider.setForeground(new java.awt.Color(0,0,0));
			ivjStartTimeJSlider.setValue(0);
			ivjStartTimeJSlider.setMajorTickSpacing(4);
			ivjStartTimeJSlider.setMinorTickSpacing(1);
//			ivjStartTimeJSlider.setSnapToTicks(true);

			ivjStartTimeJSlider.setLabelTable(labels);
			ivjStartTimeJSlider.addChangeListener(this);			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartTimeJSlider;
}
/**
 * Return the StartTimeTestField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStartTimeTestField() {
	if (ivjStartTimeTestField == null) {
		try {
			ivjStartTimeTestField = new javax.swing.JTextField();
			ivjStartTimeTestField.setName("StartTimeTestField");
			ivjStartTimeTestField.setPreferredSize(new java.awt.Dimension(40, 20));
			ivjStartTimeTestField.setText("hh:mm");
			ivjStartTimeTestField.setMaximumSize(new java.awt.Dimension(60, 20));
			ivjStartTimeTestField.setMinimumSize(new java.awt.Dimension(20, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartTimeTestField;
}
/**
 * Return the SummaryTabEditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JEditorPanePrintable getSummaryEditorPane() {
	if (ivjSummaryEditorPane == null) {
		try {
			ivjSummaryEditorPane = new com.cannontech.common.gui.util.JEditorPanePrintable();
			ivjSummaryEditorPane.setName("SummaryEditorPane");
			ivjSummaryEditorPane.setBounds(0, 0, 861, 677);
			ivjSummaryEditorPane.setContentType("text/html");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSummaryEditorPane;
}
/**
 * Return the SummaryTabScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getSummaryTabScrollPane() {
	if (ivjSummaryTabScrollPane == null) {
		try {
			ivjSummaryTabScrollPane = new javax.swing.JScrollPane();
			ivjSummaryTabScrollPane.setName("SummaryTabScrollPane");
			getSummaryTabScrollPane().setViewportView(getSummaryEditorPane());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSummaryTabScrollPane;
}
/**
 * Return the GraphTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getTabbedPane() {
	if (ivjTabbedPane == null) {
		try {
			ivjTabbedPane = new javax.swing.JTabbedPane();
			ivjTabbedPane.setName("TabbedPane");
			ivjTabbedPane.setMinimumSize(new java.awt.Dimension(200, 135));
			ivjTabbedPane.insertTab("Graph", null, getGraphTabPanel(), null, 0);
			ivjTabbedPane.insertTab("Tabular", null, getTabularTabScrollPane(), null, 1);
			ivjTabbedPane.insertTab("Summary", null, getSummaryTabScrollPane(), null, 2);
			
			/*StatisticModel data = new StatisticModel("Monthly", StatisticData.DEVICE_COMM_DATA);
			com.cannontech.analysis.report.StatisticReport report = new com.cannontech.analysis.report.StatisticReport();
			ivjTabbedPane.insertTab("Reports", null, report.getPreviewFrame(data), null, 2);
			*/
			// user code begin {1}
			
			if( getTrendProperties().getViewType() == GraphRenderers.TABULAR)
				ivjTabbedPane.setSelectedIndex(1);
			else if( getTrendProperties().getViewType() == GraphRenderers.SUMMARY)
				ivjTabbedPane.setSelectedIndex(2);
			else
				ivjTabbedPane.setSelectedIndex(0);
				
			ivjTabbedPane.addChangeListener(this);			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTabbedPane;
}
/**
 * Return the TabularTabEditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JEditorPanePrintable getTabularEditorPane() {
	if (ivjTabularEditorPane == null) {
		try {
			ivjTabularEditorPane = new com.cannontech.common.gui.util.JEditorPanePrintable();
			ivjTabularEditorPane.setName("TabularEditorPane");
			ivjTabularEditorPane.setEditable(false);
			ivjTabularEditorPane.setContentType("text/html");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTabularEditorPane;
}
/**
 * Return the TabularSlider property value.
 * @return javax.swing.JSlider
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSlider getTabularSlider() {
	if (ivjTabularSlider == null) {
		try {
			ivjTabularSlider = new javax.swing.JSlider();
			ivjTabularSlider.setName("TabularSlider");
			ivjTabularSlider.setPaintLabels(true);
			ivjTabularSlider.setBackground(new java.awt.Color(255,255,255));
			ivjTabularSlider.setPaintTicks(true);
			ivjTabularSlider.setForeground(new java.awt.Color(0,0,0));
			ivjTabularSlider.setValue(0);
			ivjTabularSlider.setMajorTickSpacing(1);
			ivjTabularSlider.setSnapToTicks(true);
			// user code begin {1}
			setSliderSize( 0, 0, 1);	//default initialize
			ivjTabularSlider.addChangeListener( this );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTabularSlider;
}
/**
 * Return the TabularTabPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTabularTabPanel() {
	if (ivjTabularTabPanel == null) {
		try {
			ivjTabularTabPanel = new javax.swing.JPanel();
			ivjTabularTabPanel.setName("TabularTabPanel");
			ivjTabularTabPanel.setLayout(new java.awt.BorderLayout());
			ivjTabularTabPanel.setBounds(0, 0, 873, 688);
			getTabularTabPanel().add(getTabularEditorPane(), "Center");
			getTabularTabPanel().add(getSliderPanel(), "North");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTabularTabPanel;
}
/**
 * Return the TabularTabScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getTabularTabScrollPane() {
	if (ivjTabularTabScrollPane == null) {
		try {
			ivjTabularTabScrollPane = new javax.swing.JScrollPane();
			ivjTabularTabScrollPane.setName("TabularTabScrollPane");
			getTabularTabScrollPane().setViewportView(getTabularTabPanel());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTabularTabScrollPane;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 10:57:58 AM)
 * @return javax.swing.JButton
 */
private javax.swing.JComboBox getTimePeriodComboBox() {
	if (ivjTimePeriodComboBox == null) {
		try {
			ivjTimePeriodComboBox = new javax.swing.JComboBox();
			ivjTimePeriodComboBox.setName("TimePeriodComboBox");
			ivjTimePeriodComboBox.setToolTipText("Select a Time Span");
			ivjTimePeriodComboBox.setMinimumSize(new java.awt.Dimension(50, 23));
			// user code begin {1}
			for (int i = 0; i < ServletUtil.currentPeriods.length; i++)
			{
				ivjTimePeriodComboBox.addItem(ServletUtil.currentPeriods[i]);
			}
			ivjTimePeriodComboBox.addActionListener( this );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimePeriodComboBox;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 10:57:58 AM)
 * @return javax.swing.JButton
 */
private javax.swing.JLabel getTimePeriodLabel() {
	if (ivjTimePeriodLabel == null) {
		try {
			ivjTimePeriodLabel = new javax.swing.JLabel();
			ivjTimePeriodLabel.setName("TimePeriodLabel");
			ivjTimePeriodLabel.setText("Time Period:");
			ivjTimePeriodLabel.setMinimumSize(new java.awt.Dimension(30, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimePeriodLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 10:57:58 AM)
 * @return javax.swing.JButton
 */
private com.cannontech.common.gui.util.TreeViewPanel getTreeViewPanel() {
	if (ivjTreeViewPanel == null) {
		try {
			ivjTreeViewPanel = new com.cannontech.common.gui.util.TreeViewPanel();
			ivjTreeViewPanel.setName("TreeViewPanel");
			ivjTreeViewPanel.setMinimumSize(new java.awt.Dimension(50, 10));
			// user code begin {1}
			ivjTreeViewPanel.setTreeModels( new com.cannontech.database.model.LiteBaseTreeModel[] { new GraphDefinitionTreeModel() } );
						ivjTreeViewPanel.addTreeSelectionListener( this );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTreeViewPanel;
}
/**
 * Return the TrendDisplayPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTrendDisplayPanel() {
	if (ivjTrendDisplayPanel == null) {
		try {
			ivjTrendDisplayPanel = new javax.swing.JPanel();
			ivjTrendDisplayPanel.setName("TrendDisplayPanel");
			ivjTrendDisplayPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTabbedPane = new java.awt.GridBagConstraints();
			constraintsTabbedPane.gridx = 0; constraintsTabbedPane.gridy = 1;
			constraintsTabbedPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTabbedPane.weightx = 1.0;
			constraintsTabbedPane.weighty = 1.0;
			getTrendDisplayPanel().add(getTabbedPane(), constraintsTabbedPane);

			java.awt.GridBagConstraints constraintsTrendSetupPanel = new java.awt.GridBagConstraints();
			constraintsTrendSetupPanel.gridx = 0; constraintsTrendSetupPanel.gridy = 0;
			constraintsTrendSetupPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTrendSetupPanel.weightx = 1.0;
			constraintsTrendSetupPanel.weighty = 0.05;
			getTrendDisplayPanel().add(getTrendSetupPanel(), constraintsTrendSetupPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTrendDisplayPanel;
}
private TrendMenu getTrendMenu()
{
	if (trendMenu == null)
	{
		trendMenu = new TrendMenu();
		addMenuItemActionListeners(trendMenu);
	}
	return trendMenu;
}
/**
 * Return the GraphSetupPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTrendSetupPanel() {
	if (ivjTrendSetupPanel == null) {
		try {
			ivjTrendSetupPanel = new javax.swing.JPanel();
			ivjTrendSetupPanel.setName("TrendSetupPanel");
			ivjTrendSetupPanel.setLayout(new java.awt.GridBagLayout());
			ivjTrendSetupPanel.setMinimumSize(new java.awt.Dimension(200, 35));

			java.awt.GridBagConstraints constraintsRefreshButton = new java.awt.GridBagConstraints();
			constraintsRefreshButton.gridx = 0; constraintsRefreshButton.gridy = 1;
			constraintsRefreshButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRefreshButton.weightx = 0.5;
			constraintsRefreshButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getTrendSetupPanel().add(getRefreshButton(), constraintsRefreshButton);

			java.awt.GridBagConstraints constraintsCurrentRadioButton = new java.awt.GridBagConstraints();
			constraintsCurrentRadioButton.gridx = 1; constraintsCurrentRadioButton.gridy = 1;
			constraintsCurrentRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCurrentRadioButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsCurrentRadioButton.weightx = 0.5;
			constraintsCurrentRadioButton.insets = new java.awt.Insets(5, 20, 5, 5);
			getTrendSetupPanel().add(getCurrentRadioButton(), constraintsCurrentRadioButton);

			java.awt.GridBagConstraints constraintsHistoricalRadioButton = new java.awt.GridBagConstraints();
			constraintsHistoricalRadioButton.gridx = 2; constraintsHistoricalRadioButton.gridy = 1;
			constraintsHistoricalRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsHistoricalRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsHistoricalRadioButton.weightx = 0.5;
			constraintsHistoricalRadioButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getTrendSetupPanel().add(getHistoricalRadioButton(), constraintsHistoricalRadioButton);

			java.awt.GridBagConstraints constraintsTimePeriodLabel = new java.awt.GridBagConstraints();
			constraintsTimePeriodLabel.gridx = 3; constraintsTimePeriodLabel.gridy = 1;
			constraintsTimePeriodLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTimePeriodLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsTimePeriodLabel.insets = new java.awt.Insets(5, 20, 5, 5);
			getTrendSetupPanel().add(getTimePeriodLabel(), constraintsTimePeriodLabel);

			java.awt.GridBagConstraints constraintsTimePeriodComboBox = new java.awt.GridBagConstraints();
			constraintsTimePeriodComboBox.gridx = 4; constraintsTimePeriodComboBox.gridy = 1;
			constraintsTimePeriodComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTimePeriodComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTimePeriodComboBox.weightx = 1.0;
			constraintsTimePeriodComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getTrendSetupPanel().add(getTimePeriodComboBox(), constraintsTimePeriodComboBox);

			java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
			constraintsStartDateLabel.gridx = 5; constraintsStartDateLabel.gridy = 1;
			constraintsStartDateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartDateLabel.insets = new java.awt.Insets(5, 20, 5, 5);
			getTrendSetupPanel().add(getStartDateLabel(), constraintsStartDateLabel);

			java.awt.GridBagConstraints constraintsStartDateComboBox = new java.awt.GridBagConstraints();
			constraintsStartDateComboBox.gridx = 6; constraintsStartDateComboBox.gridy = 1;
			constraintsStartDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateComboBox.weightx = 1.0;
			constraintsStartDateComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getTrendSetupPanel().add(getStartDateComboBox(), constraintsStartDateComboBox);

/*			java.awt.GridBagConstraints constraintsStartTimeTestField = new java.awt.GridBagConstraints();
			constraintsStartTimeTestField.gridx = 5; constraintsStartTimeTestField.gridy = 0;
			constraintsStartTimeTestField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartTimeTestField.weightx = 1.0;
			constraintsStartTimeTestField.insets = new java.awt.Insets(5, 20, 5, 5);
			getTrendSetupPanel().add(getStartTimeTestField(), constraintsStartTimeTestField);

			java.awt.GridBagConstraints constraintsStartTimeJSlider = new java.awt.GridBagConstraints();
			constraintsStartTimeJSlider.gridx = 6; constraintsStartTimeJSlider.gridy = 0;
			constraintsStartTimeJSlider.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartTimeJSlider.weightx = 1.0;
			constraintsStartTimeJSlider.insets = new java.awt.Insets(5, 5, 5, 5);
			getTrendSetupPanel().add(getStartTimeJSlider(), constraintsStartTimeJSlider);
			*/
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTrendSetupPanel;
}

private ViewMenu getViewMenu()
{
	if (viewMenu == null)
	{
		viewMenu = new ViewMenu(getTrendProperties().getViewType());
		addMenuItemActionListeners(viewMenu);
	}
	return viewMenu;
}

public com.cannontech.graph.model.TrendProperties getTrendProperties()
{
	return getGraph().getTrendProperties();
}
public TrendModel getTrendModel()
{
	return getGraph().getTrendModel();
}
/**
 * Returns the advOptsPanel.
 * @return AdvancedOptionsPanel
 */
public AdvancedOptionsPanel getAdvOptsPanel()
{
	if( advOptsPanel == null)
	{
		advOptsPanel  = new AdvancedOptionsPanel(getTrendProperties());
	}
	return advOptsPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 5:12:47 PM)
 * @param msg com.cannontech.message.dispatch.message.DBChangeMsg
 */
public void handleDBChangeMsg(DBChangeMsg msg, LiteBase treeObject)
{
	if (!((DBChangeMsg)msg).getSource().equals(CtiUtilities.DEFAULT_MSG_SOURCE))
	{
		CTILogger.info(" ## DBChangeMsg ##\n" + msg);

		// Refreshes the device trees in the createGraphPanel if that's the panel that is open panel.
		if( createPanel != null)
		{
			((DeviceTree_CustomPointsModel) createPanel.getTreeViewPanel().getTree().getModel()).update();
		}

		// Refreshes the device tree panel in the GraphClient. (Main Frame)	
		Object sel = getTreeViewPanel().getSelectedItem();
		getTreeViewPanel().refresh();

		if( sel != null )
			getTreeViewPanel().selectByString(sel.toString());
	}
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}

/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 11:51:33 AM)
 */
private void initialize()
{
	try
	{
		// Setup Role Property to create/edit trends.
		ClientSession session = ClientSession.getInstance();
		boolean graphEdit = Boolean.valueOf(AuthFuncs.getRolePropertyValue(session.getUser(), TrendingRole.GRAPH_EDIT_GRAPHDEFINITION)).booleanValue();
		if( !graphEdit )
		{
			getTrendMenu().getCreateMenuItem().setEnabled(false);
			getTrendMenu().getEditMenuItem().setEnabled(false);
		}
		// user code begin {1}
		histDate = com.cannontech.util.ServletUtil.getToday();
		currDate = com.cannontech.util.ServletUtil.getToday();
		// user code end
		setName("GraphClient");
		setLayout(new java.awt.GridBagLayout());
		setSize(1086, 776);
		setMinimumSize(new java.awt.Dimension(0, 0));

		java.awt.GridBagConstraints constraintsLeftRightSplitPane = new java.awt.GridBagConstraints();
		constraintsLeftRightSplitPane.gridx = -1; constraintsLeftRightSplitPane.gridy = -1;
		constraintsLeftRightSplitPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLeftRightSplitPane.weightx = 1.0;
		constraintsLeftRightSplitPane.weighty = 1.0;
		constraintsLeftRightSplitPane.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLeftRightSplitPane(), constraintsLeftRightSplitPane);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 11:51:33 AM)
 */
private void initializeSwingComponents()
{
	getDataViewButtonGroup();	// make sure that the radio buttons get grouped.

	KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(
		new CTIKeyEventDispatcher()
		{
			public boolean handleKeyEvent(KeyEvent e)
			{
				if( e.getKeyCode() == KeyEvent.VK_ENTER )
				{
					refresh();
					return true;
				}
				else if( e.getKeyCode() == KeyEvent.VK_F5)
				{
					//FORCE THE TREND TO UPDATE
					getGraph().setUpdateTrend(true);
					refresh();
				}
				
				//its this the last handling of the KeyEvent in this KeyboardFocusManager?
				return false;
			}
		});	
	
	boolean found = getTreeViewPanel().selectByString(getTrendProperties().getGdefName());
	//Construct a mouse listener that will allow double clicking selection in the tree
	getTreeViewPanel().getTree().addMouseListener(new java.awt.event.MouseAdapter()
	{
		public void mouseClicked( java.awt.event.MouseEvent event)
		{
			if( event.getClickCount() == 2)
			{
				valueChanged((javax.swing.event.TreeSelectionEvent) null);
			}
				
		}	
	});

	javax.swing.text.html.StyleSheet styleSheet = new javax.swing.text.html.StyleSheet();
	javax.swing.text.html.HTMLEditorKit kit = new javax.swing.text.html.HTMLEditorKit();
	javax.swing.text.html.HTMLDocument doc = (javax.swing.text.html.HTMLDocument)(kit.createDefaultDocument());

	getTabularEditorPane().setEditorKit(kit);
	getTabularEditorPane().setDocument(doc);
	try
	{
		java.io.FileReader reader = new java.io.FileReader("c:/yukon/client/config/CannonStyle.css");
		styleSheet.loadRules(reader, new java.net.URL("file:c:/yukon/client/config/CannonStyle.css"));
		//styleSheet.addRule("Main {  font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #000000; background-color: #FFFFCC; font-weight: bold}");
		//styleSheet.addRule("LeftCell {  font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #FFFFFF; background-color: #666699}");
		//styleSheet.addRule("HeaderCell {  font-family: Arial, Helvetica, sans-serif; font-size: 9pt; font-weight: bold; background-color: blue; color: blue}");
		//styleSheet.addRule("TableCell {  font-family: Arial, Helvetica, sans-serif; font-size: 12pt; color: green; background-color: green; font-weight: normal}");
		//styleSheet.loadRules(reader, new java.net.URL("file:c:/yukon/client/bin/CannonStyle.css"));
	}
	catch(java.io.IOException e){com.cannontech.clientutils.CTILogger.info(e);}

	kit.setStyleSheet(styleSheet);

	trendDataAutoUpdater = new TrendDataAutoUpdater();
	trendDataAutoUpdater.start();

	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);	

	if( getTrendProperties().getViewType() != GraphRenderers.TABULAR &&
		getTrendProperties().getViewType() != GraphRenderers.SUMMARY )	//not tabular or summary
		savedViewType = getTrendProperties().getViewType();
	
	if(found)	//found a gdefName to start with and display data
		refresh();
}
/**
 * Insert the method's description here.
 * Creation date: (6/9/00 1:19:04 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
    try
        {
		System.setProperty("cti.app.name", "Trending");
        javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

        javax.swing.JFrame mainFrame = new javax.swing.JFrame();
		mainFrame.setIconImage( java.awt.Toolkit.getDefaultToolkit().getImage(GRAPH_GIF));
		mainFrame.setTitle("Yukon Trending");
        
        SplashWindow splash = new SplashWindow( mainFrame, CtiUtilities.CTISMALL_GIF, "Loading " + System.getProperty("cti.app.name") + "...", new Font("dialog", Font.BOLD, 14 ), Color.black, Color.blue, 2 );
        
		ClientSession session = ClientSession.getInstance(); 
		if(!session.establishSession(mainFrame))
			System.exit(-1);			
	  	
		if(session == null) 		
			System.exit(-1);
				
		if(!session.checkRole(TrendingRole.ROLEID)) 
		{
		  JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.", "Access Denied", JOptionPane.WARNING_MESSAGE);
		  System.exit(-1);				
		}
			
        java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize((int) (d.width * .85), (int) (d.height * .85));
        mainFrame.setLocation((int) (d.width * .05), (int) (d.height * .05));

        GraphClient gc = new GraphClient(mainFrame);
        mainFrame.setContentPane(gc);
        mainFrame.setJMenuBar(gc.getMenuBar());
        mainFrame.setVisible(true);
        // Add the Window Closing Listener.
        mainFrame.addWindowListener(gc);
    }
    catch (Exception e)
    {
		e.printStackTrace( System.err );
		System.exit(-1);    	
    }
}
/**
 * Insert the method's description here.
 * Creation date: (6/10/2002 3:32:07 PM)
 * @param frame javax.swing.JFrame
 */
public void setGraphClientFrame(javax.swing.JFrame frame)
{
	graphClientFrame = frame;
}
/**
 *** Client *** 
 *Creation date: (3/29/2001 2:49:45 PM)
 * @param minDay int
 * @param maxDay int
 * @param cal GregorianCalendar
 * This method is used in setting up which max and min time period will be used in
 *	setting up the slider.
 */
private int setLabelData(int minIndex, int maxIndex, int value)
{
	int next = currentWeek * 7;
	int prev = ((currentWeek - 2) * 7);

	String graphPeriod = getGraph().getPeriod();
	
	if( getCurrentRadioButton().isSelected() )
	{
		setSliderLabels(minIndex, maxIndex);
		currentWeek = NO_WEEK;
	}

	else if (graphPeriod.equalsIgnoreCase(ServletUtil.ONEDAY) ||
				graphPeriod.equalsIgnoreCase(ServletUtil.THREEDAYS) ||
				graphPeriod.equalsIgnoreCase(ServletUtil.FIVEDAYS) ||
				graphPeriod.equalsIgnoreCase(ServletUtil.ONEWEEK))	// 2 - 7 days (index of Array, not a value)
	{
		setSliderLabels(minIndex, maxIndex);
		currentWeek = NO_WEEK;
	}

	else	//8 or more days
	{
		if( value == next )	//week "NEXT"
		{
			currentWeek++;
			setSliderLabels( next, maxIndex);
		}
		else if( value == prev + 6)	//week "PREV"
		{
			currentWeek--;
			setSliderLabels( prev, maxIndex);
		}
	}  //end else 8 or more days in the period

	return value;

}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2002 4:53:16 PM)
 * @param end java.util.Date
 * @param start java.util.Date
 */
public void setSliderKeysAndValues(java.util.Date start, java.util.Date end)
{
	Vector temp = new Vector();
	GregorianCalendar tempCal = new GregorianCalendar();
	tempCal.setTime(start);
	while (tempCal.getTime().compareTo(end) < 0)
	{
		temp.add(new Date(tempCal.getTimeInMillis()));
		tempCal.add(Calendar.DATE, 1);
	}
	sliderValuesArray = new Date[temp.size()];
	temp.toArray(sliderValuesArray);
}
/**
 * Passed in the min, max time period lengths and a current calendar
 * Create a hashtable of values/labels for the date Slider.
 * Depending on length and current position of the time period, different labels are created
 * Creation date: (3/23/2001 12:40:46 PM)
 */
public void setSliderLabels(int minIndex, int maxIndex)
{
	java.util.Hashtable sliderLabelTable = new java.util.Hashtable();

	final int weekNormal = 7;	
	final int weekPrevOrNext = 8;
	final int weekPrevAndNext = 9;
		
	if(currentWeek == NO_WEEK)	//at most 1, 2, 3 or 7 days
	{
		setSliderSize(minIndex, maxIndex, (maxIndex - minIndex));
	}	

	else if( currentWeek == FIRST_WEEK)
	{
		maxIndex = minIndex + 6;
		setSliderSize( minIndex, maxIndex + 1, weekPrevOrNext);
		sliderLabelTable.put( new Integer( maxIndex + 1), new javax.swing.JLabel("Next"));
	}

	else if ( (currentWeek == FOURTH_WEEK && 
				getGraph().getPeriod().equalsIgnoreCase(ServletUtil.FOURWEEKS ) )
			|| currentWeek == FIFTH_WEEK)	//farthest we can go on slider, No next
	{
		if( (minIndex + 6 ) < sliderValuesArray.length)
			maxIndex = minIndex + 6;
		else
			maxIndex = sliderValuesArray.length -1;
		setSliderSize( minIndex - 1, maxIndex, weekPrevOrNext);
		sliderLabelTable.put( new Integer( minIndex -1 ), new javax.swing.JLabel("Prev"));
	}

	else  //not the first nor last week, we can go prev or next from here
	{
		maxIndex = minIndex + 6;
		setSliderSize( minIndex - 1, maxIndex + 1, weekPrevAndNext );
		sliderLabelTable.put( new Integer( minIndex - 1 ), new javax.swing.JLabel("Prev"));
		sliderLabelTable.put( new Integer( maxIndex + 1 ), new javax.swing.JLabel("Next"));
	}

	for(int  i = minIndex; i <= maxIndex; i++)	// label up to 7 days
	{
		java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
		tempCal.setTime((java.util.Date) sliderValuesArray[i]);
		
		int month = tempCal.get(java.util.Calendar.MONTH) + 1;	//month count is 0 - 11
		int day = tempCal.get(java.util.Calendar.DAY_OF_MONTH);
		sliderLabelTable.put( new Integer(i), new javax.swing.JLabel((month + "/" + day)));
	}

	getTabularSlider().setLabelTable( sliderLabelTable );
	
}
/**
 *** Client **
 * Resizes the DateSlider using the minimun date selected and maximum.
 * Uses the number of ticks to tell it how many ticks it needs to contain
 *
 * Creation date: (3/28/2001 1:59:57 PM)
 */
public void setSliderSize(int min, int max, int numTicks)
{

	getTabularSlider().setPreferredSize(new java.awt.Dimension( ((numTicks) * 60),48));
	getTabularSlider().setMinimum( min );	
	getTabularSlider().setMaximum( max );

}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 9:14:02 AM)
 * @param message java.lang.String
 */
public void showPopupMessage(String message, int messageType )
{
	javax.swing.JFrame popupFrame = new javax.swing.JFrame();
	popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(GRAPH_GIF));
	javax.swing.JOptionPane.showMessageDialog(popupFrame,
	message, "Yukon Trending", messageType);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/00 4:06:37 PM)
 * @param event javax.swing.event.ChangeEvent
 */
public void stateChanged(javax.swing.event.ChangeEvent event) 
{
	/*if( event.getSource() == getStartTimeJSlider())
	{
		int val = 0;
		if( getStartTimeJSlider().getValueIsAdjusting())
		{
			java.text.DecimalFormat format = new java.text.DecimalFormat("00");
			
			val= getStartTimeJSlider().getValue();
			getStartTimeTestField().setText(format.format(val/4) + ":"+ format.format(val%4* 15));
		}
		System.out.println(" UPDATED STARTTIME " + String.valueOf(val));
	}*/
//	else
	{	
		java.awt.Cursor savedCursor = null;
		try
		{
			// set the cursor to a waiting cursor
			savedCursor = this.getCursor();
			this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
	
			if( getTabbedPane().getSelectedComponent() == getGraphTabPanel())
			{
				getGraph().setViewType(savedViewType);
				if( getTreeViewPanel().getSelectedNode().getParent() == null)	//has no parent, therefore is the root.
				{
					showPopupMessage("Please Select a Trend From the list", javax.swing.JOptionPane.WARNING_MESSAGE);
				}
				getGraph().update();
				getFreeChartPanel().setChart(getFreeChart());
			}
				
			else if( getTabbedPane().getSelectedComponent() == getTabularTabScrollPane())
			{
				getGraph().setViewType(GraphRenderers.TABULAR);
				if( getTreeViewPanel().getSelectedNode().getParent() == null)
				{
					getTabularEditorPane().setText("<CENTER>Please Select a Trend from the list");
					getSliderPanel().setVisible(false);
					return;
				}
				if (!getTabularSlider().getModel().getValueIsAdjusting())
				{
					getTabularSlider().removeChangeListener(this);
					getGraph().update();
					StringBuffer buf = new StringBuffer();
					buf.append( buildHTMLBuffer(new TabularHtml()));
					getTabularEditorPane().setText( buf.toString() );
					getTabularEditorPane().setCaretPosition(0);
					getGraph().setHtmlString(buf);
					getTabularSlider().addChangeListener(this);
				}				
			}
			else if( getTabbedPane().getSelectedComponent() == getSummaryTabScrollPane())
			{
				getGraph().setViewType(GraphRenderers.SUMMARY);
				
				if( getTreeViewPanel().getSelectedNode().getParent() == null)
					getSummaryEditorPane().setText("<CENTER>Please Select a Trend from the list");
				else
				{
					StringBuffer buf = new StringBuffer();
					getGraph().update();
					buf.append( buildHTMLBuffer( new PeakHtml()));
					buf.append( buildHTMLBuffer(new UsageHtml()));
					getSummaryEditorPane().setText(buf.toString());
					getSummaryEditorPane().setCaretPosition(0);
					getGraph().setHtmlString(buf);
				}
			}
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			this.setCursor( savedCursor );
		}	
	}
}
/**
 * Calls the appropriate update function according to the tab selected.
 * Creation date: (4/10/2001 11:36:57 AM)
 */
public void updateCurrentPane()
{
	synchronized (com.cannontech.graph.GraphClient.class)
	{
		trendDataAutoUpdater.ignoreAutoUpdate = true;
	}	
	
	Object selectedItem = getTreeViewPanel().getSelectedItem();
	if( selectedItem != null)
	{
		if( getTabbedPane().getSelectedComponent() == getGraphTabPanel())
		{
			getGraph().setViewType(savedViewType);
			getGraph().update();
			getFreeChartPanel().setChart(getFreeChart());
		}
		else if( getTabbedPane().getSelectedComponent() == getTabularTabScrollPane())
		{
			getTabularSlider().removeChangeListener(this);
			getGraph().setViewType(GraphRenderers.TABULAR);
			getGraph().update();
			StringBuffer buf =  new StringBuffer();
			buf.append(buildHTMLBuffer(new TabularHtml()));

			buf.append("</CENTER></HTML>");
			getTabularEditorPane().setText( buf.toString() );
			getTabularEditorPane().setCaretPosition(0);
			getGraph().setHtmlString(buf);
			getTabularSlider().addChangeListener(this);
		}
		else if( getTabbedPane().getSelectedComponent() == getSummaryTabScrollPane())
		{
			StringBuffer buf = new StringBuffer();

			getGraph().setViewType(GraphRenderers.SUMMARY);
			getGraph().update();
			buf.append(buildHTMLBuffer(new PeakHtml()));
			buf.append(buildHTMLBuffer(new UsageHtml()));
			buf.append("</CENTER></HTML>");
			
			getSummaryEditorPane().setText(buf.toString());
			getGraph().setHtmlString(buf);
			getSummaryEditorPane().setCaretPosition(0);
		}
	}
	else
		showPopupMessage("Please select a Trend from the List", javax.swing.JOptionPane.WARNING_MESSAGE);


	synchronized (com.cannontech.graph.GraphClient.class)
	{
		trendDataAutoUpdater.ignoreAutoUpdate = false;
	}	

}
/**
 * Selections on the left hand tree generate and event that appears here.
 * Creation date: (6/22/00 10:57:11 AM)
 * @param event javax.swing.event.TreeSelectionEvent
 */
public void valueChanged(javax.swing.event.TreeSelectionEvent event) 
{
	java.awt.Cursor savedCursor = null;

	if( getTreeViewPanel() == null )
		return; 

	try
	{
		//Current cursor set to waiting during the update.
		savedCursor = this.getCursor();
		this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
		
		//Find the selected graph definition and display it
		Object item = getTreeViewPanel().getSelectedItem();
		if( item == null || !( item instanceof LiteBase) )
			return;
		
		// Item is an instance of LiteBase...(from previous statement)	
		getGraph().setGraphDefinition( ((LiteBase)item).getLiteID());

		//  *Note:  An update will only occur when event is null.
		//			Null events are passed from the mouse Listener (in displayGraph..)
		//			and only occur after a double click has happened on a tree node.
		if( event == null)
		{
			updateCurrentPane();
			getGraphParentFrame().setTitle("Yukon Trending - ( Updated " + extendedDateTimeformat.format(new java.util.Date( System.currentTimeMillis() ) )+ " )" );
			getRefreshButton().requestFocus();
		}
	}
	catch( Exception e )
	{
		e.printStackTrace();
	}
	finally
	{
		this.setCursor( savedCursor);
	}
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowActivated(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowClosed(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowClosing(WindowEvent event) 
{
	exit();		
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowDeactivated(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowDeiconified(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowIconified(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowOpened(WindowEvent event) {
}
}
