package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (5/16/2001 5:21:13 PM)
 * @author: 
 */

import java.util.Date;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.model.GraphDefinitionTreeModel;
import com.cannontech.graph.buffer.html.HTMLBuffer;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.menu.FileMenu;
import com.cannontech.graph.menu.HelpMenu;
import com.cannontech.graph.menu.OptionsMenu;
import com.cannontech.graph.menu.TrendMenu;
import com.cannontech.graph.menu.ViewMenu;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.graph.model.TrendModelType;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.util.ServletUtil;

public class GraphClient extends javax.swing.JPanel implements com.cannontech.database.cache.DBChangeListener, GraphDataFormats, GraphDefines, TrendModelType, java.awt.event.ActionListener, javax.swing.event.ChangeListener, javax.swing.event.TreeSelectionListener 
{
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
			
			if( GraphClient.this.connToDispatch.isValid() )
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
							java.awt.Frame frame = GraphClient.this.getGraphParentFrame();
							java.awt.Cursor savedCursor = GraphClient.this.getCursor();

							if (getGraph().getTrendModel() != null)
							{
								frame.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
								// Set to currentDate - always want this date to be TODAY!
								GraphClient.this.getStartDateComboBox().setSelectedDate(com.cannontech.util.ServletUtil.getToday());
								GraphClient.this.setGraphDefinitionDates( null, null );
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
	private static Graph graphClass = null;
	private static javax.swing.JFrame graphClientFrame = null;
	private final java.lang.String DB_ALIAS = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
	private String directory = null;
	private static boolean isGraphDefinitionEditable = true;
//	public static final String TREE_PARENT_LABEL = "Trends";
	public static double scalePercent = 0;
//	private static final int GRAPH_PANE = 0;
//	private static final int TABULAR_PANE = 1;
//	private static final int SUMMARY_PANE = 2;

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
	private Date currentStartDate = null;
	
	private int histWeek = 0;
	private int currentWeek = NO_WEEK;	//used when more than one week is selected.
	private boolean timeToUpdate = true;	//true, update button was pushed, initial is ture also
//	private Object currentGraphPeak = null;	//flag to only update once something new has changed
	//private final javax.swing.JFileChooser pdfFileChooser = new javax.swing.JFileChooser();
	//public static boolean showDateRangeSlider = false;
	private java.util.Date[] sliderValuesArray = null;
	private CreateGraphPanel createPanel =  null;
	private javax.swing.ButtonGroup dataViewButtonGroup;
	//Menu Bar and Items
	private javax.swing.JMenuBar menuBar = null;
	private FileMenu fileMenu = null;
	private TrendMenu trendMenu = null;
	private ViewMenu viewMenu = null;
	private OptionsMenu optionsMenu = null;
	private HelpMenu helpMenu = null;
	
	//private static boolean removeMultiplier = false;
	private static boolean showPointLabels = true;
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	private TrendDataAutoUpdater trendDataAutoUpdater;
	private javax.swing.JRadioButton ivjCurrentRadioButton = null;
	private javax.swing.JPanel ivjGraphSetupPanel = null;
	private javax.swing.JPanel ivjGraphTabPanel = null;
	private javax.swing.JRadioButton ivjHistoricalRadioButton = null;
	private javax.swing.JButton ivjRefreshButton = null;
	private javax.swing.JLabel ivjStartDateLabel = null;
	private javax.swing.JEditorPane ivjSummaryTabEditorPane = null;
	private javax.swing.JComboBox ivjTimePeriodComboBox = null;
	private javax.swing.JLabel ivjTimePeriodLabel = null;
	private javax.swing.JSplitPane ivjTopBottomSplitPane = null;
	private com.cannontech.common.gui.util.TreeViewPanel ivjTreeViewPanel = null;
	private javax.swing.JTabbedPane ivjTrendingTabbedPane = null;
	private javax.swing.JEditorPane ivjTabularEditorPane = null;
	private javax.swing.JSlider ivjTabularSlider = null;
	private javax.swing.JPanel ivjTabularTabPanel = null;
	private javax.swing.JPanel ivjSliderPanel = null;
	private javax.swing.JScrollPane ivjTabularTabScrollPane = null;
	private javax.swing.JSplitPane ivjLeftRightSplitPane = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStartDateComboBox = null;
	
	private com.jrefinery.chart.ChartPanel freeChartPanel = null;
	
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
		getGraph().setUpdateTrend(true);	
		
		com.cannontech.clientutils.CTILogger.info(" don't change model");
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);		
	}
	else if (event.getSource() == getStartDateComboBox())
	{
		// Need to make sure the date has changed otherwise we are doing a billion updates on the one stateChange.
		if( currentStartDate.compareTo((Object)ivjStartDateComboBox.getSelectedDate()) != 0 )
		{
			com.cannontech.clientutils.CTILogger.info("Changing Date!");
			actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
			currentStartDate = ivjStartDateComboBox.getSelectedDate();
		}
	}
	else if( event.getSource() == getViewMenu().getLineGraphRadioButtonItem() )
	{
		actionPerformed_GetRefreshButton(LINE_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getStepGraphRadioButtonItem() )
	{
		actionPerformed_GetRefreshButton(STEP_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getShapeLineGraphRadioButtonItem() )
	{
		actionPerformed_GetRefreshButton(SHAPES_LINE_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getBarGraphRadioButtonItem())
	{
		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);		
		actionPerformed_GetRefreshButton(BAR_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if ( event.getSource() == getViewMenu().getBarGraph3DRadioButtonItem())
	{
		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);
		actionPerformed_GetRefreshButton(BAR_3D_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}	
	else if ( event.getSource() == getViewMenu().getLoadDurationRadioButtonItem())
	{
		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);		
		actionPerformed_GetRefreshButton(LOAD_DURATION_LINE_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getLoadDuration3DRadioButtonItem() )
	{
		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);
		actionPerformed_GetRefreshButton(LOAD_DURATION_STEP_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}

	else if( event.getSource() == getOptionsMenu().getPlotYesterdayMenuItem())
	{
		com.cannontech.clientutils.CTILogger.info("yesterday change");
		boolean isMasked = getOptionsMenu().getPlotYesterdayMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, isMasked);
		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_MULTIPLE_DAY_MASK, isMasked);
		getGraph().setUpdateTrend(true);
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
	}
//	else if( event.getSource() == getOptionsMenu().getSetupMultipleDaysMenuItem())
//	{
//		MultipleDaysSetupPanel setupPanel = new MultipleDaysSetupPanel();
////		setupPanel.setPanelsEnabled(getFileFormatComboBox().getSelectedIndex());
//		setupPanel.showAdvancedOptions( getGraphParentFrame() );
//	}

	else if( event.getSource() == getOptionsMenu().getMultiplierMenuItem())
	{
		boolean isMasked = getOptionsMenu().getMultiplierMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.MULTIPLIER_MASK, isMasked);
	}
	else if( event.getSource() == getOptionsMenu().getDwellMenuItem())
	{
		boolean isMasked = getOptionsMenu().getDwellMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.DWELL_LABELS_MASK, isMasked);
	}
	else if( event.getSource() == getOptionsMenu().getPlotMinMaxValuesMenuItem())
	{
		boolean isMasked = getOptionsMenu().getPlotMinMaxValuesMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_MIN_MAX_MASK, isMasked);
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
	}

	else if( event.getSource() == getOptionsMenu().getShowLoadFactorMenuItem())
	{
		boolean isMasked = getOptionsMenu().getShowLoadFactorMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.SHOW_LOAD_FACTOR_LEGEND_MASK, isMasked);
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
	}
	else if( event.getSource() == getOptionsMenu().getShowMinMaxMenuItem())
	{
		boolean isMasked = getOptionsMenu().getShowMinMaxMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.SHOW_MIN_MAX_LEGEND_MASK, isMasked);
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
	}

	
	else if( event.getSource() == getTimePeriodComboBox())
	{
		actionPerformed_GetTimePeriodComboBox( );
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
	}
	else if( event.getSource() == getTrendMenu().getCreateMenuItem())
	{		
		actionPerformed_CreateMenuItem( );
	}
	else if( event.getSource() == getTrendMenu().getEditMenuItem())
	{
		actionPerformed_EditMenuItem( );
	}
	else if( event.getSource() == getCurrentRadioButton()
			|| event.getSource() == getHistoricalRadioButton())
	{
		actionPerformed_GetToggleButton();
	}
	else if( event.getSource() == getFileMenu().getExportMenuitem())
	{
		actionPerformed_ExportMenuItem( );
	}
	else if( event.getSource() == getFileMenu().getPrintMenuItem())
	{
		actionPerformed_PrintMenuItem( );
	}
	else if( event.getSource() == getTrendMenu().getDeleteMenuItem())
	{
		actionPerformed_DeleteMenuItem( );
	}
	else if( event.getSource() == getHelpMenu().getHelpTopicsMenuItem())
	{
		com.cannontech.common.util.CtiUtilities.showHelp( HELP_FILE );
	}

	else if(event.getSource() == getHelpMenu().getAboutMenuItem())
	{
		actionPerformed_AboutMenuItem( );
	}
	//else if( event.getSource() == graphOptionsMenu.showPointLabelsItem )
	//{
		//actionPerformed_ShowPointLabelsItem();
	//}	
	else if( event.getSource() == getFileMenu().getExitMenuItem() )
	{
		exit();
	}
	else
	{
		com.cannontech.clientutils.CTILogger.info(" other action");
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_AboutMenuItem( ) 
{
	AboutTrending aboutDialog = new AboutTrending(getGraphParentFrame(), "About Trending", true );

	aboutDialog.setLocationRelativeTo( getGraphParentFrame() );
	aboutDialog.setValue(null);
	aboutDialog.show();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_CreateMenuItem( )
{

	createPanel =  new CreateGraphPanel();
	com.cannontech.database.data.graph.GraphDefinition gDef = createPanel.showCreateGraphPanelDialog( getGraphParentFrame());

	if( gDef != null )
	{
		addGraphDefinition(gDef);
		getTreeViewPanel().selectByString(gDef.getGraphDefinition().getName() );
	}
	createPanel = null;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_DeleteMenuItem( )
{
	Object selected = getTreeViewPanel().getSelectedItem();

	if (selected != null && selected instanceof com.cannontech.database.data.lite.LiteGraphDefinition)
	{
		com.cannontech.database.data.graph.GraphDefinition gDef =
			retrieveGraphDefinition((com.cannontech.database.data.lite.LiteGraphDefinition) selected);

		int option = javax.swing.JOptionPane.showConfirmDialog( getGraphParentFrame(),
	        		"Are you sure you want to permanently delete '" + gDef.getGraphDefinition().getName() + "'?",
	        		"Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
		if (option == javax.swing.JOptionPane.YES_OPTION)
		{
			deleteGraphDefinition(gDef);
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_EditMenuItem( )
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
		
		if (selected instanceof com.cannontech.database.data.lite.LiteGraphDefinition)
		{
			com.cannontech.database.data.graph.GraphDefinition gDef =
				retrieveGraphDefinition((com.cannontech.database.data.lite.LiteGraphDefinition) selected);

			//CreateGraphPanel panel = new CreateGraphPanel();
			createPanel.setValue(gDef);
			gDef = createPanel.showCreateGraphPanelDialog(getGraphParentFrame());

			if (gDef == null) //cancelled out of dialog will give a null value
				return;

			updateGraphDefinition(gDef);

			getTreeViewPanel().selectByString(gDef.getGraphDefinition().getName());

			// set the start and end date...null's let them be computed in the function too.
			setGraphDefinitionDates( null, null );
			updateCurrentPane();

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
 * Insert the method's description here.
 * Creation date: (9/25/2001 11:12:24 AM)
 */
public void actionPerformed_ExitMenuItem()
{
	try
	{
		if ( connToDispatch!= null && connToDispatch.isValid() )  // free up Dispatchs resources
		{
			com.cannontech.message.dispatch.message.Command command = new com.cannontech.message.dispatch.message.Command();
			command.setPriority(15);
			
			command.setOperation( com.cannontech.message.dispatch.message.Command.CLIENT_APP_SHUTDOWN );

			connToDispatch.write( command );

			connToDispatch.disconnect();
		}
	}
	catch ( java.io.IOException e )
	{
		e.printStackTrace();
	}

	System.exit(0);

}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_ExportMenuItem()
{
	com.cannontech.graph.exportdata.SaveAsJFileChooser chooser = null;
	switch(  getTrendingTabbedPane().getSelectedIndex()  )
	{
		case GRAPH_PANE:
			chooser = new com.cannontech.graph.exportdata.SaveAsJFileChooser(
				CtiUtilities.getExportDirPath(), GRAPH_PANE, 
				getFreeChart(),	getGraph().getTrendModel().getChartName().toString(), getGraph().getTrendModel());
				break;
				
		case TABULAR_PANE:
			chooser = new com.cannontech.graph.exportdata.SaveAsJFileChooser(
				CtiUtilities.getExportDirPath(), TABULAR_PANE, 
				getGraph().getHTMLString(), getGraph().getTrendModel().getChartName().toString(), getGraph().getTrendModel());
				break;
				
		case SUMMARY_PANE:
			chooser = new com.cannontech.graph.exportdata.SaveAsJFileChooser(
				CtiUtilities.getExportDirPath(), SUMMARY_PANE, 
				getGraph().getHTMLString(), getGraph().getTrendModel().getChartName().toString());
				break;
	}		

}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_GetRefreshButton( int refreshModelType )
{
	if( refreshModelType >= 0 )
		getGraph().setModelType( refreshModelType );
	
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

		// set the start and end date...null's let them be computed in the function too.
		setGraphDefinitionDates( null, null );

		timeToUpdate = true; //flags that update button was selected
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
		timeToUpdate = false; //reset flag
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_GetTimePeriodComboBox( )
{
	getGraph().setCurrentTimePeriod( ServletUtil.getIntValue( getTimePeriodComboBox().getSelectedItem().toString() ) );
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
		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);
	}
	else
	{
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
	}
	getGraph().setUpdateTrend(true);
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_GetToggleButton( )
{
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
		getStartDateComboBox().setSelectedDate(com.cannontech.util.ServletUtil.getToday()); //set to currentDate
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
			getStartDateComboBox().setSelectedDate(com.cannontech.util.ServletUtil.parseDateStringLiberally( (dateFormat.format( histDate)).toString() )); //set to saved histDate
		else
			com.cannontech.clientutils.CTILogger.info(" %%% hist date null!!! ");
	}

	// -- Put the action listener back on the timePeriodComboBox	
	getTimePeriodComboBox().addActionListener(this);
	getStartDateComboBox().addActionListener(this);
	actionPerformed_GetTimePeriodComboBox();
	
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_PrintMenuItem( )
{
	java.awt.print.PrinterJob pj = java.awt.print.PrinterJob.getPrinterJob();
	if (pj.printDialog())
	{
		java.awt.print.PageFormat pf = new java.awt.print.PageFormat();

		try
		{
			// set the orientation to landscape
			pf.setOrientation(java.awt.print.PageFormat.LANDSCAPE);
		}
		catch (IllegalArgumentException ex)
		{
			ex.printStackTrace(System.out);
		}

		try
		{
			// get the actual width and height of the tabbed pane in order to print all
			// Calculate what percent the actual size is vs the standard page size
			// Use the smaller percentage to make sure scaled values fit on one page
			if ((pf.getImageableWidth() / getTrendingTabbedPane().getSelectedComponent().getWidth()) < (pf.getImageableHeight() / getTrendingTabbedPane().getSelectedComponent().getHeight()))
				scalePercent = pf.getImageableWidth() / getTrendingTabbedPane().getSelectedComponent().getWidth();
			else
				scalePercent = pf.getImageableHeight() / getTrendingTabbedPane().getSelectedComponent().getHeight();

			pj.setPrintable(getFreeChartPanel(), pf);
//				pj.setPrintable((PrintableChart) getChart(), pf);
			pj.print();
		}
		catch (java.awt.print.PrinterException ex)
		{
			ex.printStackTrace();
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/27/00 12:57:26 PM)
 * @param gDef com.cannontech.database.data.graph.GraphDefinition
 */
private void addGraphDefinition(com.cannontech.database.data.graph.GraphDefinition gDef) 
{
	java.sql.Connection conn = null;

	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection( DB_ALIAS);	
		gDef.setDbConnection(conn);
		gDef.add();
		gDef.setDbConnection(null);
		conn.commit();
	}
	catch( java.sql.SQLException e )
	{
		try
		{
			if( conn != null )
				conn.rollback();
			e.printStackTrace();
		}
		catch( java.sql.SQLException e2 )
		{
			//hopeless
			e2.printStackTrace();
		}
	}
	finally
	{
		try
		{
			if( conn != null )
				conn.close();
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}

	com.cannontech.message.dispatch.message.DBChangeMsg dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg(
		gDef.getGraphDefinition().getGraphDefinitionID().intValue(),
		DBChangeMsg.CHANGE_GRAPH_DB,
		DBChangeMsg.CAT_GRAPH,
		DBChangeMsg.CAT_GRAPH,
		DBChangeMsg.CHANGE_TYPE_ADD
		);

	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange);

	//Now handled with the Base Message Class.
	//// Try not to update on the "echo"
	//synchronized (com.cannontech.graph.GraphClient.class)
	//{
		//dbChangeListener.ignoreNext = true;
	//}	
	connToDispatch.write(dbChange);
	
	getTreeViewPanel().refresh();
}
/**
 * Insert the method's description here.
 * Creation date: (5/9/2001 9:55:28 AM)
 * @param menu javax.swing.JMenu
 * Add action listeners to each JMenuItem in menu.
 */
public void addMenuItemActionListeners(javax.swing.JMenu menu)
{
	javax.swing.JMenuItem item;

	for (int i = 0; i < menu.getItemCount(); i++)
	{
		item = menu.getItem(i);

		if (item != null)
		{
			menu.getItem(i).addActionListener(this);
			if( item instanceof javax.swing.JMenu)
			{
				for (int j = 0; j < ((javax.swing.JMenu)item).getItemCount(); j++)
				{
					((javax.swing.JMenu)item).getItem(j).addActionListener(this);
				}
			}
		}
				
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/00 1:53:46 PM)
 */
private void deleteGraphDefinition(com.cannontech.database.data.graph.GraphDefinition gDef) 
{
	java.sql.Connection conn = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		gDef.setDbConnection(conn);
		gDef.delete();
		
		// Lose the reference to the connection
		gDef.setDbConnection(null);
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();		 
	}
	finally
	{   //make sure to close the connection
		try { if( conn != null ) conn.close(); } catch( java.sql.SQLException e2 ) { e2.printStackTrace(); };
	}

	com.cannontech.message.dispatch.message.DBChangeMsg dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg(
		gDef.getGraphDefinition().getGraphDefinitionID().intValue(),
		DBChangeMsg.CHANGE_GRAPH_DB,
		DBChangeMsg.CAT_GRAPH,
		DBChangeMsg.CAT_GRAPH,
		DBChangeMsg.CHANGE_TYPE_DELETE
		);
	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange);

	// Don't update on the echo
	//synchronized (com.cannontech.graph.GraphClient.class)
	//{
		//dbChangeListener.ignoreNext = true;
	//}
	connToDispatch.write(dbChange);
	getTreeViewPanel().refresh();

	getFreeChart().getXYPlot().setDataset(null);
//	getChart().setVisible(false);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 11:12:24 AM)
 */
public void exit()
{
	try
	{
		if ( connToDispatch!= null && connToDispatch.isValid() )  // free up Dispatchs resources
		{
			com.cannontech.message.dispatch.message.Command command = new com.cannontech.message.dispatch.message.Command();
			command.setPriority(15);
			
			command.setOperation( com.cannontech.message.dispatch.message.Command.CLIENT_APP_SHUTDOWN );

			connToDispatch.write( command );

			connToDispatch.disconnect();
		}
	}
	catch ( java.io.IOException e )
	{
		e.printStackTrace();
	}

	System.exit(0);

}
/**
 * Update the tabular pane.
 * Creation date: (11/15/00 4:11:14 PM)
 * Modified: (7/18/01 by SN) - eliminated a second call to hitDatabase()
 */
private int formatDateRangeSlider(TrendModel model, TabularHtml htmlData)
{
	int timePeriod = getGraph().getCurrentTimePeriod();
	
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	int valueSelected = Integer.MIN_VALUE;	//some number not -1 or greater
	long DAY = 86400000;

	setSliderKeysAndValues(model.getStartDate(), model.getStopDate());
	int valuesCount = sliderValuesArray.length;	//number of days in time period

	
	if( timePeriod != ServletUtil.getIntValue( ServletUtil.ONEDAY) &&
		timePeriod != ServletUtil.getIntValue( ServletUtil.TODAY) )  //1 day
	{
		if(timeToUpdate == true)	//update button pushed ("new start date selected")
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
				
			timeToUpdate = false;
		}

		// Set up the Slider labels and size, this method also calls setSliderLabels
		valueSelected = setLabelData (getTabularSlider().getMinimum(), getTabularSlider().getMaximum(), getTabularSlider().getValue());
		getTabularSlider().setValue(valueSelected);
		
		// If a date is indicated on the slider, just do that day....
		if( valueSelected > Integer.MIN_VALUE)
		{
			//// temporarily set the end date for only one day's data
			cal.setTime( new java.util.Date(((java.util.Date)sliderValuesArray[valueSelected]).getTime() + DAY));
			htmlData.setTabularEndDate(cal.getTime());

			//// temporarily set the start date for only one day's data
			cal.setTime( ((java.util.Date)sliderValuesArray[valueSelected]) );
			htmlData.setTabularStartDate(cal.getTime());

		}
		
	}

	if( timePeriod == ServletUtil.getIntValue( ServletUtil.ONEDAY) ||
		timePeriod == ServletUtil.getIntValue( ServletUtil.TODAY) )  //1 day
	{
		getSliderPanel().setVisible(false);
	}
	else
		getSliderPanel().setVisible(true);

	return valueSelected;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GCDF763ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DF8D45531D8E30B2F34459A7B7114760B95EB5EABED31092FF9FDBC3FD829524AEB2386089AA4E2D45028D19023825EEC928849A6EC128DAC64074DAFC1C2FE88E8C0D4A401822EB610B40687D6B659CD323059DDF7EFA4E840B7F34E1D33B77BC7609978663BFB664C19B3E74E1CB3B3671E9BC93FB5A2ABDD23172474ACC97977CE3AA44D6C16249DB555EF90AE722FD1B9126ADF85C001F4F6E7
	A6BCE320EE34A6BAC7A77D78F4B61467C1B92DB2BAE7937CAE122EA9B6F6420FA07D241D10A4EB5C4FDE8B5DCFD39175330E151B6B35704C8708835C8A0083F1D1507F51FA0302B7C0B9DF790D102E8139980DA94B204207A9F4B14A3367C124315417AA75FAA8A3CDBDBC8FBC6865391EC9F75D3ED625FC7A0688E9451FB7EA7230AF95FF0288256D9C65E914FE469411A1D58E5642F3FF473901777B1CC38DFDE6074369102DFD038D83EE33EBF05F3E3F61234DEE33BAE4C95A7D904A674E4AE6EDA5E9957063
	32BC1837DF69AB7CDC0414F45B7A20320CBC44E5187E10D716584487B2CC8FA36F9FB05DE23BC42857830E77B93F1E7A1C0AF471083BAE93E967C2B989A06DE112653DEDA4CB4F573F1152FA77EAB6841125AB1E4BB09A6F1525346A152C4DEC1EE76BF12EE220AE91A095C0BF9F64816882A8E176F53E68ADF8FE58AB37B907061C0E8FDD2E03F60B7BE32745EA075F7B76C10D02BBE16DF33AAD1244ED68FCD6FDGF9886C73475A0FF74EBF15E84EE71E7805A44B0D1A2CGE30B1EEF50CCD2F8044F770E0D0F
	2BE7230151C4654481AC851096C46598818EAC20F15DD354F239F19D85BBF1BB4C764E61DE1759AD9F9C3659ADD637B23E9B6BC30DAF9070F637DEF1BB2E17489635DE922376E9568550CBE264D64D6985DC9F72822FDEF85F0F866DBBFC017FFC30F53BG6D70B5E6071170BBDD31D10E4F48C27CAA951E5B787799BD16358B23F2DC8B49EEFB72E98C264C1BA5DD733B8147B06D8FCD1A7805DC1E14053EF33B5B00FB07A1816C944084E081888308G08CE2079556A9B47E9379D2A3965ED7F285F88CF6B086C
	B69FF5F8E433234F5AEDF65B4C3DF62BC7D2E87E22C759FB4734FB182D27BE6863EFD60755ED16EDCE87F0B78FD94110BC1CA6D1DF00B4EEAB2B6D328DD92103A117C4FC1375C538BE4C9E799FAE0BD936FAA9F8FD0A9EF57925399BB88283781D79886A7C07EC4F0A07712782F0DD19B7A36E88541FF90474B2D0DC884F0F40302D76FD7B5AEDFDA80659FD017AB7EF443D447AA5EC076DD61755E1019F9E25EEF063FA2FDE7A1DC2E68743639AE98341BD82AFEFAC60F8602156B2550F32FA33AC3BED3D433235
	53AA4BB6470037FD981345B3E679F9A4E50DEFC19B13E1DCD3G260B7165EDAAD20D4F64F41E9BF6F5614CA9FAFA096D333360775CC7485F3D19097EAF6591325397FA48CE5B73EE176A676F8DE82788190FF27BACFD54F72D2D4FC21E5EBEEE486C3942F5BFDEC891FD7C7B047501EB196B7D019254FBBB0C778C4024C7C96FC7B69979FABBE876D8E9057D5D8DB3E635F82D0C533EEF88EEE3078C01ED6CA3C3E09BBBEE88EDC3A78D01EC08571DB120EFF11EF728EC52EAA8E4EB586130B221C593094793AFF4
	21BEEC8E1B7C294DB2E015BBEDDFD9A5A976D1342D5B599A1C863FE7BECA36F43E0AEC297A2BDB25584F75817D59345BDBB4190FF19BB2BC6635A5AED37FE6543955EE9D82C535B9079DB2475B597AD6704A7E724FAD01E9ADDB509E4FD9AFA8F403DB56CA380F58072DE2EC334C3928E337ED88268A972C9253E56FD447F42DA09F46FF2761698260ED2BCC58D69DCCF1FB49ACC3F9A6835DF400684748DE1237ECD62DD345D05A1CF6BBD73B180F87CBF4B8FF56AF062D308D63C27EC80CA1212450272EDB998F
	2FCFAAC1196C2ABDE05EE3B81E59A23EC39F41F18FB35C0BB0E9F72DA51F7B31CE9DFFDABC7581678EC11748672CBA5157DF6DADC73E6D006FCA24311FAAD56FD19FFABC4EBE9BF3874ABCAC5F0E7AE6B16BD973176611864FF93069FD7B0EF518210592236EC43EC8BF18C8716270AE54F58414E3G62C4FFB9E57EF632218C9301FE1B5B83F10E45BAC2BAABAE4331237FD15094176DD26BA2B64998D497CB959D2C725345B7DBD18E43A208779711ECD34B8AD43A0098925C6DG4C944973FEF98637DB25FC30
	BC0B6CD6923166CE5F386640A2EFBC533548BF3661714BB2262F41C53EF14DF2069FD9649B57FC3D0B621A49D01732087C6C3091475571B86C090F53389616672946556134E1FAC1FEEDAB2EBD275BB6E0F3906EFA0603881AEDC60ACFE7F36C821EE1G9CF73099F111D00EFD1C767D97F7615ACE966557D8B9F3B1152F59656B9710765B728D3C0F1B7D7A48781678678B3DFDE54479989165CB2CFCC1692BC83AFD67DAE63B971C43206C7045EC2D0B3D2B8E4A87G8681C697F37F4F575B1D67F11F6807C08D
	16D9274B5AA73912306D49E6EA1B887429488F70CDG9DG3C4F1CE15C03CC56FE7904EDE0D06EF459EDB2AED34A075FEAD16F1DB1C93C5FA4F82EG60F6153C13F2C26D93D1B96DCF10DC4DE7B056957BC003793C0C792A4DF250BCE0E22364FBC1FDC33652FE74895A8B1A1B10E7B4146781A4BFC17EE82416F247193F38D9B2CD5A97B00FCD6A2A502CFAC2D93FCB3C6B3889F2C75E67B74D5467E9288FDF0238DFB56BFD663FGA624811E3CFE7118FADC775BA5F5FD41AEF5BD4AFBB1DBDD5FB626BECB551E
	0F6D019A9A5B2A87A2A57BDDF5C1F374C4E5CC52134A58B617C8D3CB66E5F03D5F52B0C66F5E7DD74CAE57C1EFCB36E27F2DCF125EDF48C53D3B20BC99A062C9DA034F7A6DD163DBD7E9D7592E3961CA5BA9B9DDB34A9F73A45F7B5285DE4B70F3D4F8EE3B6FB4116D46C15D6EE45A931BAA516E27C1B99AE09EC0B2C066D31007BFC5FBE4C6C513F4199C2D4B69B2C120E8D7258FFCFCE14DD717C75F330EECA47A77BF17E23E3BB4BDB7080D6CFE0A5BC617720C59D44270FCFC77D661784220AE82E08EC0A2C0
	AAC07653B43E0FAA6A79788ED9EC720732EC6E9BE49E188657ED331E7722F16361E39BE841F625CF875E231BFC706DD066F3542B453968F91A4FD116183BA106372B70FC6ED6B0DB4572A428CBFE1AECF5FD33A46C28D0757B3D26094AAF3CF65BBD21FD70F959DB99BC7FB8D099B87FB8DC99B87FB0D5064EBF3AAA03679F27AA557987372B75B538479DF9A6AA4782B099E0BA402CE7482616D5D6F21B8AE0CF07A0A1E9F332938B197A1FD795FC2C8FD7DD7E1CA3318A6344DE27ACBB0710BF6117D461FC49CE
	97C3F25CB3D528A3B77AC1CE2A601F2F42FD528EBE9251BC0E3944764EF9B02E64E7A83639E6BF45B6E982F7C3AEBB43D88A7AD8CA76B928C55C881443854E43F051D0CE903857F3FDED895B506F70257F3F980877D1248D9577E4FDAB76DAD093FC8E8DB5171F430A9A967F036E3BAE382C9CD7DB43E2507EFE0FD5584A0E9A1D409D75F4B8BDB6CCCAF81CF3D38B6E13965B17B6B0E01674EA17B11F5FC2F140AA5067B4061B363FDD113FE3193F0E5FD47EE5A83A32G4D88C031D21E324C6BE3F85F11399B3C
	FD1B1CFDBCD7627D5F436C63DEA0CEDAC6B119A7837DFE175BEA459D16A5CD0257A59B46EB60D68EB213760B57EE572A6335D46009F1F45333901781F01A15F93EB19D521C06FA5733A443457DA8BF7AB76EF7A24E2B7327561C5A20795364E71577874AF34A3192653D441F76D34E978FF5DBD9BF1214AD513F548F8261344F61BB1F6F20CBD1E4F9C2CBBE30741737C86D8B1726D785112563B9AEC344F35E98B03260F0A69FE3C2AEBEF76D5A23642D9917F6C9015F3574B1875FB0B0ECEB9816ED109079CC8E
	9F5FA2AD6565894FD1BC551607BECA179215D387F0A0055E17B464E7AB6369C9A15AA7F5C8BB954A11G31A9940BFC49722884A82FC821FD746E1A9D706CB61FD5228EF451CEB75BC9815961F63A2CEE79824732D385E53D7FE53FBA6E50BF8FF2BD9FFA9F61BA7F6BA9757C6BEE56E94846FD6977AB7C0EBC6F1BBF3F5A006BEA947053GE6834483AC8510DE084A518354816C8768823083CC82888708840881188710748269607E7A6DA1F420A49A4C21384DBCFF65BAD8228D95FB2DF8A1D83E7DBBB61759AF
	7A4616318C2FFF51B736BC1AC73125916A42DE24FD3DA59F7BDF8A65D5AF7A7725CB9D0B2BCE257868538264571A6A9B9F75B23CA955B7BEDA262378688C544DC9A53DFD3D93798A1F4F4DC359C33F33937DB71E8F5A4CF65BD7EC2B6A84C247G77E7CF31B5DC77926F0B631EE538838A0E4F75E738F5A8BC339BEA024D93F87DE310227C3D772C35CF6EB28F08B6390D41FDC6E16365FDC6C9E3E03FDF51686F77EB9B837B7D7AC6355F673866C65467G58D60F20FBDB60CE8A5C3B0CA762BE35D964C1017F10
	490578A3D66C1070FFE3F21DE7389E0A4DD43813F4DE5C08FE8F06BC68F4D35B6EC6BCAF6937596D3C7C3F0C4666GB7A87471B9135166B22B6436885CC9B1172FE9A906B97D92596BFF23BBD2564EFD2A5FFF4F1B78F8FAF0C27B60E3BE46766249B05E99AFD15CF6934345C0B9CE60966713EE12856E128EF13A17E16FFA197475D9816276C339C7608699EE844A53856EC55DF847C63C2EDCFF5985636D837D561546FB5CEF1AEB351258B31839F3FC69C81E97CF66AE453D0CFA536599C3700DD5464A7D34B9
	BFF87C74951B93BCC3B3AC2758E5E6FE60D8EFCEBE45FAF5C04B65D8199FCA0E5665EAB9EE52A1DF279BDF8F28E578150E781A048C7AB3A8B3FB1FGB955300B878A3C4E485EE1C2886130403E5AE56E6D35DA54B15C4D853E6FABB0A718843C4317D38CF7CD091AE666F2CE13G4F94A1434EBC5C97F07F62BE7C2CCE9DB7A5DEF8BD3DC08A72DEF0859FF7538A2F8F776A6106FC8DBC5FEE2272E37994AB7272859D4E3FDB19EF0E1B23C39F20BA0EEA701EC6B5F49FBA51F5346DC353671D9D2623DD9245D38F
	9508F784ABA8C6FA25083DA300F28CC05C8ABAEB5C11DDA685BC73D22BD7617B1FA59427AD95FC7BCA112F7115281CD6000ED7A8763A2F996DE3904AE1G11G31G71GA9G524A289C43CA722B0B1BC245E4E2382A08042F5105853E71486E155E35FEE4E530F864029EE5BE3D127B5D8D82FFBD43072970BC9EF803698E4BB3212E6ED55A076EAE89958F4DF8B5D87F8B194EE2DE657D2C9378A799BECE05677D5B989E4BCB214E14C6FAFB30B9382FCFE83E3C2FFF22B9302FFF26595F5727B4877675AFB77B
	7B7AD799DF37F5G167AA766A141F7F5B36EEB7C1C7EE33343BC404EBF38BFF821005EF94CCAA39FB1D8C4FEA3BE0DF47E0D9E4FE8255722F2CA8176831C819885188E90739A517D0851A5C0B98D4070BA58A7C08F408840345709AEDD6F8AB0F7480F4D17421F791257AE6FC7CAC350C80A0CDE9FE38C58F72E724439DDAFF9FD52F8FCCAA89F15A729FD777C9252ED0C5045A2E6E749D0165E084AB13EC178E58CFFG4A960049826FAC757511586EF2F2A27FE0F5C6D5FB5E47E43FBE4663EBC33D03D26B123F
	AB5AD4C8BEA1728D7E2EA8961E71G98F7F0FF77CD11D121C989B0C6E9155A5F69B7222ECFD87B5DD64F6041E1D8DE8ED1D7F19CFDD85B309BAF471CB0DBECCEA260756125F89EF24466019D5056E7360FA1D1E8EE5C0D3601B704BA2CEE1B53E2B2778A9F79563642B1F5ED4E21DE67C16788357D298E656C144DEE395DACDBF9D3256E7ED3856ABAEFD349348AF5F01FDE7D9EC6370A6B27951E3C7E1CD3DD9F2E54470A7A385D016A63C57D7DEF296B17AA755A55D43F608BF53DF1B52FBFA26A0B6B8335BFAD
	6AC7AF2A6BDDCA7BD92BE90ED3CAE80E672C761F6304556AB94EF3609C33B885BC655833266BAA319FC08F5B4DEEF8F476391DF6BB52707A5FEC407A4E61A13CEF81755C5FE1BD0FD1CA4BFC63980CFDD320FFE98D45B1FB0CEA9A439ACE538E4F1EB5B45E77B3460CD721193E065EBFFED0C8F976AC40ADDDC3E7AC5FAB24384C78A679793B98ED8F14DDG135FA45ABB0AA85608F813622745C578847F2144399BBAB1A68890E3883B663CD66B09D7421B745E7262E1757D5B599BAAB4415E5B6824E89EFBA9CF
	0C3D5AFF782572EEEF63417240B20CD9E0DC0E1FBB697D1E8BF8F15C3ABDA4DBA460147B9A7DC16EEB30D5455B2DF90B5AA509F683AC5F8C50CE2CB85EB64FCDED8D69A407291EF09D69446F5AE34D52B84F5FFC56B56799F511623A49229FFDA975938BB8BE2F26D24C0FF93CC0ED3B4AC8D7E9C047ED65BBDB51D61AB451B9C7G4EE844388F8509B53D8BC448B4E4247EC7B44457D201FCE7C1F9AEC012605B2C8D42573B30845F618CA1AF3467BE66D061553DEB6D3C4AF6D32E389D0F8F3733B834A9A35A67
	9CED97436FD761797A7EAF313E0FC05D4C8C9A6F4CA2F57F3A4C681C6A4C6810FE1B37539487123BD5E9EB8A400367825FEF41FD688C540F8204834C8418874063429BF7DD76CE76211190F7326FF022DCA9BE7DCBD93C2C4F72170B37536E89B41EBA05FEFF50F699BB82356BD068ADC15B953A315DC800FA04A9C15B9D8FF8FFBBC2210F8E5AAE66E2A8BB1B9B345D66D2F53BC420CB8DA27358F6A74BFC7B4BDC6BEDE7D8BB16C7BD14397D2EE47B4501353EF67DA6431B567A5A75642DE457BDD0B7FBAD5935
	5B98EA3C46F5E37B5F3D0E7ACFE07B4A11F551BE774116B0FC0F8A4F7BBFDBC177418621EE5EBA6A7F2B8C6C3FA9BB302E5AFD70BD59542E91DF5DA8BAECD57D3E27F0624F899F1D50BEF82EF1EF911DFD972FA55C5CA2BA7BFEAF0BF01B0BD9AE89FA9985D04E890BA9CF191CAD760B1660F96F4DAD015F63C73484FE0F7F0B1650F755FF5D926CBDBF176FEE96ABA400ECA9C26699F69A07341EF0D9C3B48E2D40D56DA13A5382973607688685EE4B8E220B91382FF7905D9C015B4E6285FDCEF4CEFD8E613A07
	092ED560866B095FE4017BD3BD51058B5CDFAF0AF3DB01EBAA25393CCD4C651ED21A4BEB44DC3A4AA81F5F1DCBED1FADA7FEBD8217436E060EC2F9EAAE2D07D5DB3DEBA04B687D7D432D933F9E6E1850BE78182FB7129E268BBD24B0DCAC1463059E9E2F40F348A4D1DE4A4A2BC459D5C132A24D446863FA514737F92FF32CBEEEE0B119D19B1D532AA5FD3C1CC1BAEA973842CC3215C1AD4524CD4528939996BCFF5FED5C0376426FA35CD45C9E22EF75DD83162FGCF844C3FB12E1C2C0D76390FB01F2DF50F95
	6F2ADBAD7C8C7BFABB454E61C04FE5E8FB5BABC369D0A899B20248D02248B0C3A583676DF9474B7B16BD21F8E78561DD25544D72637DD82B17770FF7906F595A40F1C2A839637CF4F7400DF91B6CF45A3BEC2EAE18AE254FB5FB3DFD8E8D079A4FFA1FBE93FD46136AB71E0AF73D3C136AC3714E15C26BEA150AB7677774C562171D672F1FBC9FFE25C0531467352D76BCAFBFFE86752FA2676F003AC10029GB3G705CEDF69EFE871C3E5590E28C4A79925BCFE5F3EF17CD36DB399ECEE28AADF19E91625CA0B3
	5FDFEE4EA323421F4768FB12281FF4081DBF09738DFE7A414FA5B6D77A1EDD601921817A2A4F27B5FBEDAD6A3B8FF3D4070CD71314793A6BFD2F1C6BB3C30D1553A7FC00747EF7DF287E74912F4C91F636CF38071571FCF354CB376DA37C4D7343FBF7D05E591ECF6B7918A9F0BFF447664E0F3D3C96FF6265356992719A85DE633B37C3FCB49D781B471B7714D13C9B8DFCD272A96FFD3AE14CB9823FA7FE50095F5470EB620A9C6B4B69BC27FA8365526F1433F7A0D0168126G707316391F296FD29BF7ACCF8F
	F417BA6D33F24D0C8D5CFE66EE701E374CAEB81C497B3CCF646F2BB650194271CA426D56119C5BEB589DD5A80781CC5711EF1BBBA6470A867CDC5DF8F21052CBE3F297E813BA2EF6777B65AE63730F732F32DDFE79551D817C124DDBE601EF2E944D703A825FDC693DF2321DD2281B8440F3720A63383E1221AC95C27E8F309F2083E03010626C439DF5D27077F25C443CAF6578389E9813030D822F29056351FFF7252FBEE6285A45846041477DD3E6BF738A23FD6E76C4B2FC128A4F7551D8C3F7FBD6C0DDCF91
	45960BF7785F4FC95E213E1F43F1AD177C69DAAF296978F86E4463C665776FD53F5B4AA7BE06BDBD21FDF05BF86BF8709C2C7978655FBD6EBB9E785D6301637E6F9E8F9D8F7C6E7123637E770B1798E9FE9E967949B3C61A9F3348CF0658DEB49B66BFCE58C0CA95451CAB0AE89F3A0D5D21B568A1B655936E578C5781E517406D298925F3ECBFDE7D36862065FDFC92320FAB094F833DC363FEB22C527FDB2ADB04CF8F577358E4A6BCE381B097615FCCE5D493CD8260D3G3445607381CC456477C73E40DCDA09
	9B461C4D727AB3AC8E6B346375939ED3707798C9B53E6FB130DFBB7065B147D3353EB18756CF027AF06C1F2D7BFBABE99F1C893864E272C939D5649BCBB712CF5ADB0534DDD0368304EDA45A6D55643F26830EFF1BE42E767D7689ED2CC122EF17EE2C767D76897B72567F315A775B270747FCBB75EA356F37CF54DE09B1EA455DE0A1E759B6BA6356EE22B1BD154E3E5F0672E9GF993596D1F7C765D716DB18B2F321D2E6A6A7C5E2F581CCC59646B777E036127ED7275FB9FD451FC4F003AEC830D3721DA5D3F
	9D70138C630AABEAC3499D6E43A3D274D7394D375DEC05B68E1E49817A6663BA29C179538C3EE712FD8C1F3D5977CC7239F4BA13B4C05D68E66ABF8AD398E58C33D53F8FD5CD3C9F7A67047641776CC355345F7F0F586F0FD553FEFFCA6C77AD353437A7ED267D5EDECB678E5AAD045B3B0D7C02C160226A583DC4A8F7895C2769621CEE8B697AF38D66986122ECE365D9227CD27AF874016D2FDE67B91A7174F135E7B949DBB0B7BBD6EB8CC19327484F73185F1FC01B63FE24F79B6D5375A551B96DGF600C9A5
	5C0FF29F7067BA9697187B4E8D381D438E0B245C95DC4266E09A504EG60F2B852C90E3892FF1DA5ABB84E776D8276ED8877BD5C45C8F42F61E60DFA0FCDD364B11612CC833568935B2C78F70168DE7FF92B7ACEE069566420F782F717F2B94E14FA73A4FC72F57BAF5BE81F8A077AF925145B356C9A734E1C67F6076D4E73CAE6A76E7AEDA37F15D9C6FB3DD4475E87C2398B40DEC6F96CCF7EA189FD1852B177F4D84F9B09FDC31B536EF4576D5A3327DE016DCA9FBFC9A79F93D6C6BA798B7BBBDB1FB85D6C0E
	36F2EFF5EF10777CC25F1C5F031AEC097B4259E5140FFC5D0DF3E9AC07BC84608C408840940099GB1GF16514172CB991AAAF618930EFDEB255EF9F9E5FFA7A59D536DBF8456D787EAE3339CBA97775F39799BE2D5C574F2D2DA37B495C8A3159D65AEBEEC9D777BF8F70C9DB47636796307DE3C5G5AECA3471542338920DDA977983D34F6556FC9C6FF9EFC7E3E5B9DBCF778C17765F3071FF4874E9D26F77B678E37F6874E9D6E68763F37B8D3610B76233E37785B6EC0779615F7FEE80A4A6FEBD53F35F593
	6FDFEBA6348F6E67FE1B6E6FDFFF17CE7EF5C0502531B59D8EF3BE53C8BE72E086794DD8017BB17B1B8571D0CE9138C40613AA4067D6906E2723082B07728101FB1A613A206C9238BF30FE4320BCDD6034625D412C8A5A2FB6E4303FF1039B823B1D63D17C4A368CBA3F0F87DA0EBB47F049D0CE956D7F2DA1075AF7597A4EF948AF5D53C06DCBAB09F6B9236DF73A2DE3FDD8AE7BAE269E68DA85ED74287A6F1AF03A87C6E9CFBFA368D60F62B71AC36633CEB7765F69B277415E4A696F50108CAEC17F883BA765
	F118DDDDCEAEB027FDC950CE82DAFE56DA15F179F36577B3461E1F762369A9F55FEB980FAFD13E9DEC984BE75EC9AF1F9BC7B7876653D48F7F378B3AA523E36F4D5F4D3E29EB9665ECA1AB7FDB195F5FF1DE83F95F73D21647ADED903C0F328FFD123EEC59177164ED999AA973283416EFAAC725CC5C990ECAD9EC63B8AAE54026B20217F5B355A0CB6B14CB1E70CB3952A9CBB95E4B1B3224451B18C6FE95F31FAC95F63217C7C6331161E1969D32144FEEDC42EC9A71F696D0E531EB0540D389D7E4E9BD3E60EA
	9C14070065BA656220678B69926A0EB9DF3D724C9A87B6EB82E8FD74621597B41EBFF6B653EF5FF42D6EC7AD439AA97B3034365FECF78D1AC153EC8918245CDE26F2F460B0D61D895BB6C25BC65676DD1324697348D209975F07F8DE0B0B428AA46BADE677B9468145ABBEA52D6FF78F74328EE4C9F36A70A968161530FA13C94A118771638214D90632E05D012BDD29F817CDBF7B0859457E31216B0362F0B132FFCA3C387D9B75F720E9572D8B7A9DF0EC8D771B71B55EF8D1793BFEEC2F5155C6675457D2ACF4
	674E1DD270EF84BB074CF63BEC361F7B50E1416F858FDE102D5E3B8F7D8D485FC3B4CAA52F9BEAC0394F2A5A7C9FD0CB878838A99EB96C9CGG54D6GGD0CB818294G94G88G88GCDF763AC38A99EB96C9CGG54D6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA69DGGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:48:47 PM)
 * @return com.klg.jclass.chart.JCChart
 */
/*
public JCChart getChart()
{
	return getGraph().getChart();
}
*/
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 5:14:03 PM)
 * @return com.cannontech.message.util.ClientConnection
 */
public com.cannontech.message.util.ClientConnection getClientConnection() {
	return connToDispatch;
}
/**
 * Return the CurrentRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCurrentRadioButton() {
	if (ivjCurrentRadioButton == null) {
		try {
			ivjCurrentRadioButton = new javax.swing.JRadioButton();
			ivjCurrentRadioButton.setName("CurrentRadioButton");
			ivjCurrentRadioButton.setSelected(true);
			ivjCurrentRadioButton.setText("Current");
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
 * Insert the method's description here.
 * Creation date: (7/6/2001 1:32:01 PM)
 * @return javax.swing.ButtonGroup
 */
public javax.swing.ButtonGroup getDataViewButtonGroup()
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
 * Insert the method's description here.
 * Creation date: (10/25/2001 9:55:03 AM)
 * @return java.lang.String
 */
private String getDirectory()
{
	if( directory == null )
	{
		try
		{
			java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
			directory = bundle.getString("yc_common_commands_dir");	//using the YC one so not another one needs to exist.
			//com.cannontech.clientutils.CTILogger.info("[" + new java.util.Date() + "]  Directory found in config.properties is " + directory);
			int index = directory.lastIndexOf("/");
			directory = directory.substring(0, index+1);
			directory = directory + "export/";
		}
		catch( Exception e)
		{
			directory= "C:/yukon/client/export/";
			//com.cannontech.clientutils.CTILogger.info("[" + new java.util.Date() + "]  Directory was NOT found in config.properties, defaulted to " + directory);
			com.cannontech.clientutils.CTILogger.info("[" + new java.util.Date() + "]  Add row named 'yc_common_commands_dir' to config.properties with the home directory.");
		}

		java.io.File file = new java.io.File( directory );
		file.mkdirs();
	}
	return directory;
}
	//private OptionsMenu optionsMenu = null;

private FileMenu getFileMenu()
{
	if (fileMenu == null)
	{
		fileMenu = new FileMenu();
		addMenuItemActionListeners(fileMenu);
	}
	return fileMenu;
}
private com.jrefinery.chart.JFreeChart getFreeChart()
{
	return getGraph().getFreeChart();
}
private com.jrefinery.chart.ChartPanel getFreeChartPanel()
{
	if( freeChartPanel == null)
	{
		freeChartPanel = new com.jrefinery.chart.ChartPanel(getFreeChart());
		freeChartPanel.setVisible(true);
		freeChartPanel.setHorizontalZoom(true);
		freeChartPanel.setVerticalZoom(true);
	}
	return freeChartPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/30/2001 9:55:19 AM)
 * @return com.cannontech.graph.Graph
 */
public Graph getGraph()
{
	if( graphClass == null)
		graphClass = new Graph();
	return graphClass;
}
private java.awt.Frame getGraphParentFrame ()
{
	return graphClientFrame;
}
/**
 * Return the GraphSetupPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGraphSetupPanel() {
	if (ivjGraphSetupPanel == null) {
		try {
			ivjGraphSetupPanel = new javax.swing.JPanel();
			ivjGraphSetupPanel.setName("GraphSetupPanel");
			ivjGraphSetupPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRefreshButton = new java.awt.GridBagConstraints();
			constraintsRefreshButton.gridx = 0; constraintsRefreshButton.gridy = 0;
			constraintsRefreshButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRefreshButton.insets = new java.awt.Insets(5, 20, 5, 5);
			getGraphSetupPanel().add(getRefreshButton(), constraintsRefreshButton);

			java.awt.GridBagConstraints constraintsCurrentRadioButton = new java.awt.GridBagConstraints();
			constraintsCurrentRadioButton.gridx = 1; constraintsCurrentRadioButton.gridy = 0;
			constraintsCurrentRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCurrentRadioButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsCurrentRadioButton.insets = new java.awt.Insets(5, 20, 5, 5);
			getGraphSetupPanel().add(getCurrentRadioButton(), constraintsCurrentRadioButton);

			java.awt.GridBagConstraints constraintsHistoricalRadioButton = new java.awt.GridBagConstraints();
			constraintsHistoricalRadioButton.gridx = 2; constraintsHistoricalRadioButton.gridy = 0;
			constraintsHistoricalRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsHistoricalRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsHistoricalRadioButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getGraphSetupPanel().add(getHistoricalRadioButton(), constraintsHistoricalRadioButton);

			java.awt.GridBagConstraints constraintsTimePeriodLabel = new java.awt.GridBagConstraints();
			constraintsTimePeriodLabel.gridx = 3; constraintsTimePeriodLabel.gridy = 0;
			constraintsTimePeriodLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTimePeriodLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsTimePeriodLabel.insets = new java.awt.Insets(5, 20, 5, 5);
			getGraphSetupPanel().add(getTimePeriodLabel(), constraintsTimePeriodLabel);

			java.awt.GridBagConstraints constraintsTimePeriodComboBox = new java.awt.GridBagConstraints();
			constraintsTimePeriodComboBox.gridx = 4; constraintsTimePeriodComboBox.gridy = 0;
			constraintsTimePeriodComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTimePeriodComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTimePeriodComboBox.weightx = 1.0;
			constraintsTimePeriodComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getGraphSetupPanel().add(getTimePeriodComboBox(), constraintsTimePeriodComboBox);

			java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
			constraintsStartDateLabel.gridx = 5; constraintsStartDateLabel.gridy = 0;
			constraintsStartDateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartDateLabel.insets = new java.awt.Insets(5, 20, 5, 5);
			getGraphSetupPanel().add(getStartDateLabel(), constraintsStartDateLabel);

			java.awt.GridBagConstraints constraintsStartDateComboBox = new java.awt.GridBagConstraints();
			constraintsStartDateComboBox.gridx = 6; constraintsStartDateComboBox.gridy = 0;
			constraintsStartDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateComboBox.weightx = 1.0;
			constraintsStartDateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getGraphSetupPanel().add(getStartDateComboBox(), constraintsStartDateComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGraphSetupPanel;
}
/**
 * Return the GraphTabPanel property value.
 * @return javax.swing.JPanel
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
 * Return the HistoricalRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getHistoricalRadioButton() {
	if (ivjHistoricalRadioButton == null) {
		try {
			ivjHistoricalRadioButton = new javax.swing.JRadioButton();
			ivjHistoricalRadioButton.setName("HistoricalRadioButton");
			ivjHistoricalRadioButton.setText("Historical");
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
 * Update the Summary pane.
 *  Calls the peaks html code and the usage code
 * Creation date: (11/15/00 4:11:14 PM)
 */
private String buildHTMLBuffer( String seriesType)
{
	HTMLBuffer htmlBuffer = null;
	StringBuffer returnBuffer = null;

	int sliderValueSelected = 0;
	try
	{
		if ( seriesType.equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES) )
		{
			htmlBuffer = new TabularHtml();
			getTabularSlider().getModel().setValueIsAdjusting(true);
		}
		else if ( seriesType.equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES) )
		{
			htmlBuffer = new PeakHtml();
		}
		else if (seriesType.equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES) )
		{
			htmlBuffer = new UsageHtml();
		}
		else
		{
			htmlBuffer = new TabularHtml();	//default on error(?) to graph_series
		}
	
		returnBuffer = new StringBuffer("<HTML><CENTER>");

		TrendModel tModel = getGraph().getTrendModel();
		{
			htmlBuffer.setModel( tModel);

			if ( htmlBuffer instanceof TabularHtml)
			{
				((TabularHtml) htmlBuffer).setTabularStartDate(tModel.getStartDate());
				((TabularHtml) htmlBuffer).setTabularEndDate(tModel.getStopDate());

				//if( showDateRangeSlider )
				//{
					sliderValueSelected = formatDateRangeSlider(tModel, (TabularHtml)htmlBuffer);
				//}
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
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getLeftRightSplitPane() {
	if (ivjLeftRightSplitPane == null) {
		try {
			ivjLeftRightSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjLeftRightSplitPane.setName("LeftRightSplitPane");
			ivjLeftRightSplitPane.setDividerSize(0);
			ivjLeftRightSplitPane.setDividerLocation(225);
			getLeftRightSplitPane().add(getTreeViewPanel(), "left");
			getLeftRightSplitPane().add(getTopBottomSplitPane(), "right");
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
 * Insert the method's description here.
 * Creation date: (5/17/2001 10:57:58 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JMenuBar getMenuBar()
{
	if (menuBar == null)
	{
		try
		{
			menuBar = new javax.swing.JMenuBar();
			menuBar.add(getFileMenu());
			menuBar.add(getTrendMenu());
			menuBar.add(getViewMenu());
			menuBar.add(getOptionsMenu());
			menuBar.add(getHelpMenu());	
		}
		catch (java.lang.Throwable ivjExc) 
		{
			com.cannontech.clientutils.CTILogger.info(" Throwable Exception in getMenuBar()");
			ivjExc.printStackTrace();
		}
	}
	return menuBar;
}
private OptionsMenu getOptionsMenu()
{
	if (optionsMenu == null)
	{
		optionsMenu = new OptionsMenu();
		addMenuItemActionListeners(optionsMenu);
	}
	return optionsMenu;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 10:57:58 AM)
 * @return javax.swing.JButton
 */
public javax.swing.JButton getRefreshButton() {
	if (ivjRefreshButton == null) {
		try {
			ivjRefreshButton = new javax.swing.JButton();
			ivjRefreshButton.setName("RefreshButton");
			ivjRefreshButton.setText("Refresh");
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
public javax.swing.JPanel getSliderPanel() {
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
			// user code begin {1}
			currentStartDate = ivjStartDateComboBox.getSelectedDate();
			com.cannontech.clientutils.CTILogger.info(" DAY -> "+ivjStartDateComboBox.getSelectedDate() + " and  " + currentStartDate);
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
			ivjStartDateLabel.setText("Starting Date:");
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
 * Return the SummaryTabEditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getSummaryTabEditorPane() {
	if (ivjSummaryTabEditorPane == null) {
		try {
			ivjSummaryTabEditorPane = new javax.swing.JEditorPane();
			ivjSummaryTabEditorPane.setName("SummaryTabEditorPane");
			ivjSummaryTabEditorPane.setContentType("text/html");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSummaryTabEditorPane;
}
/**
 * Return the TabularTabEditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getTabularEditorPane() {
	if (ivjTabularEditorPane == null) {
		try {
			ivjTabularEditorPane = new javax.swing.JEditorPane();
			ivjTabularEditorPane.setName("TabularEditorPane");
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
public javax.swing.JComboBox getTimePeriodComboBox() {
	if (ivjTimePeriodComboBox == null) {
		try {
			ivjTimePeriodComboBox = new javax.swing.JComboBox();
			ivjTimePeriodComboBox.setName("TimePeriodComboBox");
			ivjTimePeriodComboBox.setToolTipText("Select a Time Span");
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
public javax.swing.JLabel getTimePeriodLabel() {
	if (ivjTimePeriodLabel == null) {
		try {
			ivjTimePeriodLabel = new javax.swing.JLabel();
			ivjTimePeriodLabel.setName("TimePeriodLabel");
			ivjTimePeriodLabel.setText("Time Period:");
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
 * Return the TopBottomSplitPane property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getTopBottomSplitPane() {
	if (ivjTopBottomSplitPane == null) {
		try {
			ivjTopBottomSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
			ivjTopBottomSplitPane.setName("TopBottomSplitPane");
			ivjTopBottomSplitPane.setDividerLocation(60);
			ivjTopBottomSplitPane.setDividerSize(0);
			getTopBottomSplitPane().add(getGraphSetupPanel(), "top");
			getTopBottomSplitPane().add(getTrendingTabbedPane(), "bottom");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTopBottomSplitPane;
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 10:57:58 AM)
 * @return javax.swing.JButton
 */
public com.cannontech.common.gui.util.TreeViewPanel getTreeViewPanel() {
	if (ivjTreeViewPanel == null) {
		try {
			ivjTreeViewPanel = new com.cannontech.common.gui.util.TreeViewPanel();
			ivjTreeViewPanel.setName("TreeViewPanel");
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
 * Return the GraphTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getTrendingTabbedPane() {
	if (ivjTrendingTabbedPane == null) {
		try {
			ivjTrendingTabbedPane = new javax.swing.JTabbedPane();
			ivjTrendingTabbedPane.setName("TrendingTabbedPane");
			ivjTrendingTabbedPane.insertTab("Graph", null, getGraphTabPanel(), null, 0);
			ivjTrendingTabbedPane.insertTab("Tabular", null, getTabularTabScrollPane(), null, 1);
			ivjTrendingTabbedPane.insertTab("Summary", null, getSummaryTabEditorPane(), null, 2);
			// user code begin {1}
			ivjTrendingTabbedPane.addChangeListener(this);			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTrendingTabbedPane;
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
 * Insert the method's description here.
 * Creation date: (10/3/2001 1:51:05 PM)
 * @return java.lang.String
 */
public static String getVersion()
{
	return (com.cannontech.common.version.VersionTools.getYUKON_VERSION() + VERSION);
}
private ViewMenu getViewMenu()
{
	if (viewMenu == null)
	{
		viewMenu = new ViewMenu();
		addMenuItemActionListeners(viewMenu);
	}
	return viewMenu;
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 5:12:47 PM)
 * @param msg com.cannontech.message.dispatch.message.DBChangeMsg
 */
public void handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase treeObject)
{
	if (!((DBChangeMsg)msg).getSource().equals(com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE))
	{
		com.cannontech.clientutils.CTILogger.info(" ## DBChangeMsg ##\n" + msg);
		/*
		if( msg.getDatabase() == msg.CHANGE_POINT_DB )
		{
			
			com.cannontech.database.data.point.PointBase obj = (com.cannontech.database.data.point.PointBase)userObject;
			if( obj.getPoint().getPointType().equalsIgnoreCase(msg.getObjectType())
				 && obj.getPoint().getPointID().intValue() == msg.getId() )
			{
				
				txtMsg.append( ". Editing of '"+
					com.cannontech.database.cache.functions.PAOFuncs.getYukonPAOName(obj.getPoint().getPaoID().intValue()) + "/" +
					obj.getPoint().getPointName() + "' was canceled." );

				current.fireCancelButtonPressed();
			}
		}
*/

		// Refreshes the device trees in the createGraphPanel if that's
		// the panel that is open panel.
		if( createPanel != null)
		{
			((com.cannontech.database.model.DeviceTree_CustomPointsModel) createPanel.getTreeViewPanel().getTree().getModel()).update();
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
 * Set up a connection to dispatch, for database changes.
 * In the future we could also get point changes and dynamically
 * update a graph.
 * Creation date: (10/31/00 12:18:31 PM)
 */
private void initDispatchConnection() 
{
	String host = null;
	int port;
	try
	{
		java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");
		host = bundle.getString("dispatch_machine");
		port = (new Integer(bundle.getString("dispatch_port"))).intValue();
	}
	catch ( java.util.MissingResourceException mre )
	{
		mre.printStackTrace();
		host = "127.0.0.1";
		port = 1510;
	}
	catch ( NumberFormatException nfe )
	{
		nfe.printStackTrace();
		port = 1510;
	}

	connToDispatch = new com.cannontech.message.dispatch.ClientConnection();

	com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
	reg.setAppName("Yukon Trending");
	reg.setAppIsUnique(0);
	reg.setAppKnownPort(0);
	reg.setAppExpirationDelay( 1000000 );
	
	connToDispatch.setHost(host);
	connToDispatch.setPort(port);
	connToDispatch.setAutoReconnect(true);
	connToDispatch.setRegistrationMsg(reg);

	try
	{
		connToDispatch.connectWithoutWait();
	}
	catch ( Exception e )
	{
		e.printStackTrace();
	}

	trendDataAutoUpdater = new TrendDataAutoUpdater();
	trendDataAutoUpdater.start();

	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);	
	//dbChangeListener = new DBChangeMessageListener();
	//dbChangeListener.start();
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 11:51:33 AM)
 */
private void initialize() {
	try {
		// user code begin {1}
		histDate = com.cannontech.util.ServletUtil.getToday();
		currDate = com.cannontech.util.ServletUtil.getToday();
		// user code end
		setName("GraphClient");
		setLayout(new java.awt.GridBagLayout());
		setSize(1086, 776);

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

	getGraphParentFrame().addKeyListener(new java.awt.event.KeyAdapter()
	{
		public void keyPressed( java.awt.event.KeyEvent event)
		{
			if( event.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER )
			{
				actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
			}
		}
	});
	
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

	javax.swing.text.html.HTMLEditorKit editorKit = new javax.swing.text.html.HTMLEditorKit();
	//javax.swing.text.html.HTMLDocument doc = (javax.swing.text.html.HTMLDocument) (editorKit.createDefaultDocument());
	javax.swing.text.html.StyleSheet styleSheet = editorKit.getStyleSheet();
	
	try
	{
		java.io.FileReader reader = new java.io.FileReader("c:/yukon/client/bin/CannonStyle.css");
		styleSheet.loadRules(reader, new java.net.URL("file:c:/yukon/client/bin/CannonStyle.css"));
		//styleSheet.addRule("Main {  font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #000000; background-color: #FFFFCC; font-weight: bold}");
		//styleSheet.addRule("LeftCell {  font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #FFFFFF; background-color: #666699}");
		//styleSheet.addRule("HeaderCell {  font-family: Arial, Helvetica, sans-serif; font-size: 9pt; font-weight: bold; background-color: blue; color: blue}");
		//styleSheet.addRule("TableCell {  font-family: Arial, Helvetica, sans-serif; font-size: 12pt; color: green; background-color: green; font-weight: normal}");
		//styleSheet.loadRules(reader, new java.net.URL("file:d:/yukon/client/bin/CannonStyle.css"));
	}
	catch(java.io.IOException e){com.cannontech.clientutils.CTILogger.info(e);}

	editorKit.setStyleSheet(styleSheet);
	//tabularPanel.setEditorKit(editorKit);

	initDispatchConnection();
	getDirectory();	//setup the directory for the exports
}

/**
 * Insert the method's description here.
 * Creation date: (6/6/2001 11:39:04 AM)
 * @return boolean
 */
public static boolean isShowPointLabels() {
	return showPointLabels;
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
        javax.swing.UIManager.setLookAndFeel(
            javax.swing.UIManager.getSystemLookAndFeelClassName());

        javax.swing.JFrame mainFrame = new javax.swing.JFrame();
        mainFrame.setIconImage(
            java.awt.Toolkit.getDefaultToolkit().getImage("GraphIcon.gif"));
        mainFrame.setTitle("Yukon Trending");

        // Add the Window Closing Listener.
        mainFrame.addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent e)
            {
                java.awt.Window win = e.getWindow();
                win.setVisible(false);
                win.dispose();
                System.exit(0);
            }
        });

        java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize((int) (d.width * .85), (int) (d.height * .85));
        mainFrame.setLocation((int) (d.width * .05), (int) (d.height * .05));

        GraphClient gc = new GraphClient(mainFrame);
        mainFrame.setContentPane(gc);
        mainFrame.setJMenuBar(gc.getMenuBar());
        mainFrame.setVisible(true);

        //gc.initialize();
        //gc.initializeSwingComponents();

        java.util.ResourceBundle res = null;
        try
            {
            res = java.util.ResourceBundle.getBundle("config");
        }
        catch (java.util.MissingResourceException mse)
            {
            System.err.println(mse.getMessage());
            mse.printStackTrace();
        }
        catch (NumberFormatException nfe)
            {
            nfe.printStackTrace();
        }
        /*
        try
        {
        	graphEdit = res.getString("graph_edit_graphdefinition").toLowerCase();
        }
        catch( java.util.MissingResourceException mse )
        {
        	System.err.println(mse.getMessage());
        }
        
        
        if( graphEdit.equals("false") )
        {
        	isGraphDefinitionEditable = false;
        	gc.graphTrendMenu.createMenuItem.setEnabled(false);
        	gc.graphTrendMenu.editMenuItem.setEnabled(false);
        }
        */

    }
    catch (Exception e)
        {
        e.printStackTrace();
    }

}
/**
 * Insert the method's description here.
 * Creation date: (10/31/00 1:53:46 PM)
 */
private com.cannontech.database.data.graph.GraphDefinition retrieveGraphDefinition(com.cannontech.database.data.lite.LiteGraphDefinition lGDef) 
{
	com.cannontech.database.data.graph.GraphDefinition gDef = (com.cannontech.database.data.graph.GraphDefinition) com.cannontech.database.data.lite.LiteFactory.createDBPersistent(lGDef);

	java.sql.Connection conn = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		gDef.setDbConnection(conn);
		gDef.retrieve();
		
		// Lose the reference to the connection
		gDef.setDbConnection(null);
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();		 
	}
	finally
	{   //make sure to close the connection
		try { if( conn != null ) conn.close(); } catch( java.sql.SQLException e2 ) { e2.printStackTrace(); };
	}

	return gDef;
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
 * Set the current GraphDefinition's start Date and end Dates.
 *  Check for null allows this function to compute the start and end dates.
 *  If they are not null, then we just set the current graphDefinition dates to those
 * 	 passed in through the function call.
 * Creation date: (6/7/2001 12:27:23 PM)
 * @param newStart java.util.Date
 * @param newStop java.util.Date
 */
public void setGraphDefinitionDates(java.util.Date newStart, java.util.Date newStop)
{
	if (newStart == null )
	{
		newStart = getStartDateComboBox().getSelectedDate();
	}

	if (newStop == null)
	{
		newStop = com.cannontech.util.ServletUtil.getEndingDateOfInterval( newStart, getTimePeriodComboBox().getSelectedItem().toString() );
	}

	newStart = com.cannontech.util.ServletUtil.getStartingDateOfInterval( newStart, getTimePeriodComboBox().getSelectedItem().toString() );

	getGraph().getCurrentGraphDefinition().getGraphDefinition().setStartDate(newStart);
	getGraph().getCurrentGraphDefinition().getGraphDefinition().setStopDate(newStop);
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

	if( getCurrentRadioButton().isSelected() )
	{
		setSliderLabels(minIndex, maxIndex);
		currentWeek = NO_WEEK;
	}

	else if (getGraph().getCurrentTimePeriod() < ServletUtil.getIntValue(ServletUtil.FOURWEEKS))	// 2 - 7 days (index of Array, not a value)
	{
		setSliderLabels(minIndex, maxIndex);
		currentWeek = NO_WEEK;
	}

	else	//8 or more days
	{
		if(	value == next )	//week "NEXT"
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
private void setSeriesType(String newSeriesType)
{
	getGraph().setSeriesType(newSeriesType);
}
/**
 * Insert the method's description here.
 * Creation date: (6/29/2001 11:24:02 AM)
 * @param b boolean
 */
public void setShowPointLabels(boolean b)
{
	showPointLabels = b;
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2002 4:53:16 PM)
 * @param end java.util.Date
 * @param start java.util.Date
 */
public void setSliderKeysAndValues(java.util.Date start, java.util.Date end)
{
	long DAY = 86400000;
	
	java.util.GregorianCalendar tempCal = new java.util.GregorianCalendar();
	int numDays = new Long( (end.getTime() - start.getTime()) / DAY).intValue();
	sliderValuesArray = new java.util.Date[numDays];

	for (int i = 0; i < sliderValuesArray.length; i++)
	{
		java.util.Date nextDate = new java.util.Date(start.getTime() + (DAY * i));
		sliderValuesArray[i] = nextDate;
	}
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
				getGraph().getCurrentTimePeriod() == ServletUtil.getIntValue( ServletUtil.FOURWEEKS ) )
			|| currentWeek == FIFTH_WEEK)	//farthest we can go on slider, No next
	{
		maxIndex = minIndex + 6;
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
	popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("GraphIcon.gif"));
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
	if( event.getSource() == getTrendingTabbedPane())
	{
		if( getTrendingTabbedPane().getSelectedComponent() == getGraphTabPanel())
		{
			com.cannontech.clientutils.CTILogger.info("GRAPH TAB");
			getGraph().setSeriesMask(GRAPH_MASK, true);
			getGraph().setSeriesMask(PEAK_MASK, false);
			getGraph().setSeriesMask(USAGE_MASK, false);
		}
		else if( getTrendingTabbedPane().getSelectedComponent() == getTabularTabScrollPane())
		{
			com.cannontech.clientutils.CTILogger.info("TABULAR TAB");
			getGraph().setSeriesMask(GRAPH_MASK, true);
			getGraph().setSeriesMask(PEAK_MASK, false);
			getGraph().setSeriesMask(USAGE_MASK, false);
		}
		else if( getTrendingTabbedPane().getSelectedComponent() == getSummaryTabEditorPane())
		{
			com.cannontech.clientutils.CTILogger.info("SUMMARY TAB");
			getGraph().setSeriesMask(GRAPH_MASK, true);
			getGraph().setSeriesMask(PEAK_MASK, true);
			getGraph().setSeriesMask(USAGE_MASK, true);
		}
	}			

	java.awt.Cursor savedCursor = null;
	try
	{
		// set the cursor to a waiting cursor
		savedCursor = this.getCursor();
		this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

		if( getTrendingTabbedPane().getSelectedIndex() == GRAPH_PANE )
		{
			if( getTreeViewPanel().getSelectedNode().getParent() == null)	//has no parent, therefore is the root.
			{
				showPopupMessage("Please Select a Trend From the list", javax.swing.JOptionPane.WARNING_MESSAGE);
			}
			getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);
			getGraph().update();
		}
			
		else if( getTrendingTabbedPane().getSelectedIndex()  == TABULAR_PANE )
		{
			if( getTreeViewPanel().getSelectedNode().getParent() == null)
			{
				getTabularEditorPane().setText("<CENTER>Please Select a Trend from the list");
				getSliderPanel().setVisible(false);
				return;
			}
			if (!getTabularSlider().getModel().getValueIsAdjusting())
			{
				getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);
				getGraph().update();
				StringBuffer buf = new StringBuffer();
				buf.append (buildHTMLBuffer( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES ));
				buf.append("</CENTER></HTML>");
				getTabularEditorPane().setText( buf.toString() );
				getTabularEditorPane().setCaretPosition(0);
				getGraph().setHtmlString(buf);
			}
		}
	
		else if( getTrendingTabbedPane().getSelectedIndex()  == SUMMARY_PANE )
		{
			if( getTreeViewPanel().getSelectedNode().getParent() == null)
				getSummaryTabEditorPane().setText("<CENTER>Please Select a Trend from the list");
			else
			{
				getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES);			
				getGraph().update();
				StringBuffer buf = new StringBuffer();
				buf.append( buildHTMLBuffer( com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES) );

				getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES);
				getGraph().update();
				buf.append( buildHTMLBuffer(com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES) );
	
				buf.append("</CENTER></HTML>");
				getSummaryTabEditorPane().setText(buf.toString());
				getSummaryTabEditorPane().setCaretPosition(0);
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
		if( getTrendingTabbedPane().getSelectedIndex()  == GRAPH_PANE )
		{
			setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);					
			getGraph().update();
			getFreeChartPanel().setChart(getFreeChart());
		}
		
		else if( getTrendingTabbedPane().getSelectedIndex()  == TABULAR_PANE )
		{
			setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);							
			getGraph().update();
			StringBuffer buf =  new StringBuffer();
			buf.append(buildHTMLBuffer(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES));

			buf.append("</CENTER></HTML>");
			getTabularEditorPane().setText( buf.toString() );
			getTabularEditorPane().setCaretPosition(0);
			getGraph().setHtmlString(buf);
			
		}
	
		else if( getTrendingTabbedPane().getSelectedIndex()  == SUMMARY_PANE )
		{
			StringBuffer buf = new StringBuffer();
			if( getGraph().getHasPeakSeries() )
			{
				setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES);			
				getGraph().update();
				buf.append( buildHTMLBuffer(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES) );
			}

			setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES);
//			getGraph().update();
			buf.append( buildHTMLBuffer(com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES) );
			
			buf.append("</CENTER></HTML>");
			getSummaryTabEditorPane().setText(buf.toString());
			getGraph().setHtmlString(buf);
			getSummaryTabEditorPane().setCaretPosition(0);
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
 * Insert the method's description here.
 * Creation date: (10/31/00 1:59:14 PM)
 * @param gDef com.cannontech.database.data.graph.GraphDefinition
 */
private com.cannontech.database.data.graph.GraphDefinition updateGraphDefinition(com.cannontech.database.data.graph.GraphDefinition gDef) 
{
	java.sql.Connection conn = null;
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		gDef.setDbConnection(conn);
		gDef.update();
		
		// Lose the reference to the connection
		gDef.setDbConnection(null);
	}
	catch( java.sql.SQLException e )
	{
		e.printStackTrace();		 
	}
	finally
	{   //make sure to close the connection
		try { if( conn != null ) conn.close(); } catch( java.sql.SQLException e2 ) { e2.printStackTrace(); };
	}

	com.cannontech.message.dispatch.message.DBChangeMsg dbChange = new com.cannontech.message.dispatch.message.DBChangeMsg(
		gDef.getGraphDefinition().getGraphDefinitionID().intValue(),
		DBChangeMsg.CHANGE_GRAPH_DB,
		DBChangeMsg.CAT_GRAPH,
		DBChangeMsg.CAT_GRAPH,
		DBChangeMsg.CHANGE_TYPE_UPDATE
		);

	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange);
	
	// Don't update on the echo
	//synchronized (com.cannontech.graph.GraphClient.class)
	//{
		//dbChangeListener.ignoreNext = true;
	//}
	connToDispatch.write(dbChange);
	getTreeViewPanel().refresh();
	
	return gDef;
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
		// Signal the trend to be rebuilt through a database hit.
		getGraph().setUpdateTrend(true);
		
		//Current cursor set to waiting during the update.
		savedCursor = this.getCursor();
		this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

		//Find the selected graph definition and display it
		Object item = getTreeViewPanel().getSelectedItem();
		if( item == null || !( item instanceof com.cannontech.database.data.lite.LiteGraphDefinition) )
			return;

		// Item is an instance of LiteGraphDefinition...(from previous statement)	
		com.cannontech.database.data.graph.GraphDefinition selection = 
					retrieveGraphDefinition( (com.cannontech.database.data.lite.LiteGraphDefinition) item );
		if( selection == null )
			return;

		getGraph().setCurrentGraphDefinition( selection );

		for (int i = 0; i < selection.getGraphDataSeries().size(); i++)
		{
			com.cannontech.database.db.graph.GraphDataSeries gds = (com.cannontech.database.db.graph.GraphDataSeries) getGraph().getCurrentGraphDefinition().getGraphDataSeries().get(i);

			if ( getGraph().isPeakSeries( gds.getType()) )
			{
				getGraph().setHasPeakSeries( true );
				break;
			}
		}
		// set the start and end date...null's let them be computed in the method too.
		setGraphDefinitionDates ( null, null );	

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
}
