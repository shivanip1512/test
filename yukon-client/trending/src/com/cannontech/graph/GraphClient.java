package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (5/16/2001 5:21:13 PM)
 * @author: 
 */

import java.awt.event.WindowEvent;
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

public class GraphClient extends javax.swing.JPanel implements com.cannontech.database.cache.DBChangeListener, GraphDataFormats, GraphDefines, com.cannontech.graph.model.TrendModelType, java.awt.event.ActionListener, java.awt.event.WindowListener, javax.swing.event.ChangeListener, javax.swing.event.TreeSelectionListener {

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
	private com.cannontech.jfreechart.chart.YukonChartPanel freeChartPanel = null;
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
//		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);		
		actionPerformed_GetRefreshButton(BAR_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if ( event.getSource() == getViewMenu().getBarGraph3DRadioButtonItem())
	{
//		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);
		actionPerformed_GetRefreshButton(BAR_3D_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}	
	else if ( event.getSource() == getViewMenu().getLoadDurationRadioButtonItem())
	{
//		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);		
		actionPerformed_GetRefreshButton(LOAD_DURATION_LINE_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getLoadDuration3DRadioButtonItem() )
	{
//		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);
		actionPerformed_GetRefreshButton(LOAD_DURATION_STEP_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}

	else if( event.getSource() == getOptionsMenu().getPlotYesterdayMenuItem())
	{
		com.cannontech.clientutils.CTILogger.info("yesterday change");
		boolean isMasked = getOptionsMenu().getPlotYesterdayMenuItem().isSelected();
//		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, isMasked);
//		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_MULTIPLE_DAY_MASK, isMasked);
		getGraph().setUpdateTrend(true);
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
	}
	/*
	else if( event.getSource() == getOptionsMenu().getSetupMultipleDaysMenuItem())
	{
		MultipleDaysSetupPanel setupPanel = new MultipleDaysSetupPanel();
//		setupPanel.setPanelsEnabled(getFileFormatComboBox().getSelectedIndex());
		setupPanel.showAdvancedOptions( getGraphParentFrame() );
	}
*/

	else if( event.getSource() == getOptionsMenu().getMultiplierMenuItem())
	{
		boolean isMasked = getOptionsMenu().getMultiplierMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.GRAPH_MULTIPLIER, isMasked);
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);		
	}
	/*
	else if( event.getSource() == getOptionsMenu().getDwellMenuItem())
	{
		boolean isMasked = getOptionsMenu().getDwellMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.DWELL_LABELS_MASK, isMasked);
	}
	*/
	else if( event.getSource() == getOptionsMenu().getPlotMinMaxValuesMenuItem())
	{
		boolean isMasked = getOptionsMenu().getPlotMinMaxValuesMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_MIN_MAX_MASK, isMasked);
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
	}

	else if( event.getSource() == getOptionsMenu().getShowLoadFactorMenuItem())
	{
		boolean isMasked = getOptionsMenu().getShowLoadFactorMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.LEGEND_LOAD_FACTOR_MASK, isMasked);
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
	}
	else if( event.getSource() == getOptionsMenu().getShowMinMaxMenuItem())
	{
		boolean isMasked = getOptionsMenu().getShowMinMaxMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.LEGEND_MIN_MAX_MASK, isMasked);
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
//		getGraph().setOptionsMaskHolder(TrendModelType.PLOT_YESTERDAY_MASK, false);
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
	D0CB838494G88G88GFB0AD0ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8BF4D4C5326C0AFBB84A3EE51FF18F3B623E2081A306B5AAE85438C4792A8123C408EB945420A8D1D051076F316FDD728782849314DD329095A1019082040F089AC82210CCE0C8424785A1B7B3B71311494CB8F3E386C477D5F55F6A393973A170CCF26A5C5B55555D555555DDD55DFDC7AADA97193DA823C81296E5CB5A5F6EC51294F3DC125E197D47B442AD788FB5C3527DFDG30D232EF4E02E7
	DE241A1178161AD1A07D2AB08F524510BE78261A71BE3C97CA579616B443CB08F696B4C95252339B8F07EF67B76B291DDC169E3AB19F1E4B81CAGD78BB0A8925B3FF763EA8D5F8169655ABB42A22835F8A8527CE0DA2D43A7EBF4F9C379B31A51A4EFAC56722D10C61A0B709CB454DF67E2465D55FB77E869533F0D14129FF9A5E79936252B1FE01ED63668A67E14A4CF8E8AA2D37A30E383BC77194F59BE3738BAABAC325369F22A0A25234266115D9DFB77FE050F9A07DDF12A12547FA664EF28A90F1515A493
	24B9AF0FAF5B273595F1AED2CA387919103CCC52F8C8577192D7121EB564E62C7B2FCC36D82E9C52D6G5EE6DDA935B993F02DD1089B5500342B613D9260E094717259C66265648F37CBA9ED4BF2D70460A5AA0A7310986567A57968D799FFE3633CE6830ED55EB0B5239C209A408A509FA08AE08D532FE96BB741F3FF1BDA636A6CF4B9773B5D879CD6CF234B2AB860FD6FDE4851F0CD0A456531CA925721612672A07C90448F6B29BF7EB1BFA1511847B48D17DA544C5C6CA0FD0BF8B2BDA7C92BA3ED183FEF3C
	DF77B0BD4D9A2EE614G6C81B0833881868D27FE15FC3C63D27DBA84FA62F14A0E662EB6376CD18FF4599DD6452375AF33B4DC7F0281AF3772324BF139C46F504BA57E2E653939C164929F7DE6CE4CF0AE0FF1437DF261ED5707ECBBEDF860F830F9FB8B6AE1298975F08150AC52F4146337B33C6996BF1E6B78FDCCEFB1FD9A7292EEA13DBD391C7AE04A7A03B42FF2C550BE789E4A4ADD9F4D795997ED9C5B6B4AF06D68GFCFF00A1G23G12816681E45DCA63FB6E632D3D54DB33EEECF959759B5E032762D3
	BD72A127D7151D9665386C314BED8E45ABE9B4DBB6A06FEDBD4AEDE7734982EDFC25B8950F2C5ADDCE28DD6ED4C0113C1C66538DAB114623305C96FB27828DF43AA52A37E6C3A1BC9D32D7BD66364A2A622760798D9BD0165F4B4721C628G5EDBEEC519BF40562C7570DE8D40E5153089F103A19DAB64B2E1433B70ECG45D69CFB77565AAD48066CB9CF6DA7EC4435C4799E1643DA452DB82D70625572A6EEDC6C17CB3BCB70BC1961F10E54G63DE011F3AF1A547C39DFAA9D37E53ACDFD6D50F3D2DCBD51A95
	D535BBED7E72AFB0DE3CBD261FD7526678E65431F1502FD400F451BFC7E9212EFFA6176BDC173B85C7CE13134C56D9E9041A31FA84593B8A935A3FFDA3C8CFDFBBC9FADA3BEC18147F643220FA0A50B2026B27E304F12E6DE1F57A5BC8E8B8F919733E3730CF3471D41F3501F3194BFD539614FB8434998B10B40264FECBE9891FEF87E42FC2B36C0887C6CC317A350C535ED896DA47224A026B58083260BA96DB96DE07624A0269904F9BD3063645556D5469643832F7599CF6BA954634A8A2F1FF62359614075D
	E9D7CF582DB6C5ED36DFD0A42960B6665730B9B89BE432D068D277C724CB6BAF8C15A24F4E4E89E64FFC43F2F3DBEE63BA54F11BDF17B8CF63B7214C95075289022AF1F5B9D50EFF144DEF8D2F2DAF8FEC8ECEBB1679126714739A5D444DB9922EA30EAEC574ADD6DE0AB27658BBE128F042EABE1D2FD46F538DG7E507F0B01E782GAF7B11894B6A1DA92EAF168A64A78568528132E2C8DFEA4AB11F662926A8B5AE07030BDD0CC76516829CBF653BAE8516E11C488D228F3B373CEB48BB2E750767FF3185F9F2
	68560062986C4F9C615F210D60380CC6444D05C19B1DC3B63731C06FFF56FA25203E88C2EB8C9FB3B546E82FEE5E0A75C6GBECA747D3EAD7AB5EA3F576B325819B95046614DB214B773D93F153F17FD955EEE50693DFB6B4DB214607E6273EC9DC37A389872971717232C67C1BAEFA478BEA329BDFBC5201EB8AB1640335D6E71021FE3D5FCA4B335827B0E76C7C313DF36DDAF0B481973036AB1421E11DC862611C6D93CDC09FCF4G7E2260EDEE45CA1DAC400784F3EB0311A2FE2237E6F23D5552B1DB33C9EF
	A57229DE8A70EB860D747BB3D1A383FD9B6E3F7C1249ABEE24512F99487013C69A7D1A22F272EBD2A0EF5F1F484E4E58067D0A04F46C1F28DF7BAB17697AE5F659B13C507A70DAA54EBD1747EE33BB8977A64301874D96A3EE2B7E070DF18254198C40F1CF33B93FG528537533ABF209C67F615C87FBBCB3708745F4B0DF681E9FFAE5B405B589150C6664FD47FF2A13734EDB89EBE111E4D526735368A25D5E5B9CCF74F3B3AC058E937233C4A4BE86D72C1FA90C09CC0626D5C7E7379F6C7B72E936D602841B4
	EBF6AB96A9A1964B3E31094A16C33A9A2095209FC0A4GEFF3C9972EC1A625DDED325BBA54E637432E62B425F8F8EC39FE6D4C3B0337DB894FDAG2ED7ADE594931E86DC449D445736D674F545BAD0A1F72B982F5A2D87E41B0975122F85C34A72347209F750DA70882BB36BCEB52398206ACE32C73E8D94BB468C7F03D45EEFF9EE30B8B621652D9C531DDCC75CF77A67F182440E3C4D551B294D9848CF3B93F1DFEEAEB20C7FCA98108AF872FCDF0FFC1C77E512BEFFC40FFC647747BCFDFEEA0F7CECDDF95E37
	C7BF263EADFCB4DAB20DDE98B2C6AF3F0B77492CBD63B2B764D01D19528D95BD646EDFFF4568F2997CF7F91E267F836EA239CF28C739A7C0BA85E05EDDB4877FD9F6E571E24795169BFA5965785AB704799AF923785A37C86007B1FC118E4FF5B7C968EE8964759FC5EB629FF6205E4F06F456E850C100AA0096G77E8DAA3BB37D7CA170851DADCEE93B80A8EDD78407B770F4DD796C7BF18CBBAB26A3E1B24413F0A4FD99AC2C77A0F663A9125BD4D99DCC7F87F366C447EA5C35EBC00223B550CF200EA0013F7
	D37FC6EFAF677D2B335A557D2AAADBBA1885264E9D37AB5DFEB4AEBC3CEF5296AC6738BB789A5D4F000F00B49F233DAC6E0E3E1B0FD136983BBA060F57617958A5B2DD45F49264D55DC33AFA93EE79E8FAF41B6EFDDAD9DF4597FE3D1D5B27ED70F139F9C76878E3440E6071C76C0E6071475DBB42479FFF5E91BA7E983FC39FFFF03D726E42B5EEB00C45A8009400F4GA90EF42A396AC32ED3C174298E82129A975B31D0297DDDBBC37775331D175E6778F2A77A09EDAED5F5F5E27D04B76D4471D2DDEE066438
	23BBD1C69E34031CD4431F58096B2483ECA422399F737E9636178D7D2A0AA35F666F3B4837B9A8F076FA36078169D1F1241F558D081B8469B401EBE0382CFB550C52FB8977C43DD117308C3D27457D7FFCA05E461C00B674FECF764F222FBF6C8ABD063F5CF569B13C1649D5C1591FF7AB9CB7E89773C15B5B3D0A50156BF795885CA12F596535E3D0427D1C8A96CBD96D5F5BC101D950FB7ADEE67337D09CE20274EC065B362BD663BF725EC0997F2F76172949EA9450CC82D82525D36F755B985EF63F7A957E36
	CDAE8B0F15F87B3FE77AB19608AB6FA31F4C1B09F63F4523A838423220C95475139D7DB5B0AB8718CA8778EBBFB6687D35EA28937D68FE700CG60B4D3CCC61F8EE9E2A0BFC170705EAE64FF3BC4F6A732DB9FBF2DDE72DFA16327147B3473836D39FE67069C6EEF346DA2D92F3FDF4DD8475A0176A57A1B77C5B05C697B714CA77DB3C97072DCBE5940625BEE1056CF0F4F79A884AF1177F39E665D6F7781C947E1CD2C4767766DBBB53FA973A76CEF3033968BB3709536AEFBC517EA070048B0B83CFF239BA8AE
	AF0DA7FFEA8D5BE3EE05348FE0D0BC1D176430F89D7B93ADE86FBF0234B3A13DG20608172C5E6FE0E3425102EFD005651525D1B61F9DC7ED673BA50C43BBCECA585245963F2AB9E75BC4732DD85ED3E5BF6697D8EAB5467FBA07CBA42E57E58A97D784F7BFDDA8E693811FE20D65F60870C7133DC0D73AA91703381727F2CE62C875883E086308278G8682C4814483A4812481ACG489B837A8BD0B906E4306D7332B0B250828DE6D0BCB20FDF398C6EEB8867FB550E8995EF7F060D6549B1C65FF2B043DB4798
	FD4B8FCC64DB3AA1AFF98C2D6BCB9B317DBD89EA06A9A1302DD683CECDA07FE82B996B9B10E0740F76B27C108423FF746091720FE2A12F68C1125B2C4F31DEE17339FAE8EBE87A67E83FF1FF50AEBB6C9758D2558C04CE9B37E7BF31B96CFB103745F1D7B15CA08D4747FA62FE9CF9DE27670B0FC30D93D87DFA88D10E34FD2BD8549659A64AF4D5073699972BAFEDB32E2E8EEE772F2D8E347B032A035B7D5FD56B6DBE475DD00D723401EE1D94F411827735408DE3F5A26E045D2AF6887CED0CAF449FD430C142
	5F41786AE63813641B69F0DFEB387B2B516EC117BBDC9EF166D10D7BA56DF607C35B83E6B4F6A718C1A10F07990FF6372C63FB0A40FDAD4672718672E1E29EA4FD3D5EA4093973C75D7BC6D35F7B5355FD5A866F7307ECADCE017E4EFF107C36CE064BFBC84DA8F908F077B412EC2A846E78914435C25A779049EB2799F183A19DADF0558CB789526982B771C8EF7BC6F5DD3E7CE6763A8D34DB176B6FF13BA9EF4817441A41541D630BFD4B7CF8D2F729E4AC4AEDF6C3C918FA8B46723EF2BB7DC9E3E87F29090D
	896E21F50CA55F25FFE3F0DFEFC8A36D7978001673B125B19C9F83FA7051F9846BF5F970F8C04F4355C7285EA1020722D664191DA7C0CC5565660E824FABD945FCB88F2CEA382E4AEDED0AD56F43F94C46738A0C8912206E34316443DDF5C6CF33F0AC27A99D27E66C9BC7BCE433B3BCDC3F388D7F36C06FB7451F1F1233D28AF1AEB80E773B5F38E0FE133D91AF616C2A2474DD0DD8C72548BFFA84475F230DB7478DB902B6C0379DD5615F0D2AB8DE5754F228E63F69EC3359F428C5A2FFEA04D91C910CA39FE9
	F293BBA300F45EF8984371345738B96F9FD250BDAF3DF835FA6FBFC3FE5A1E71D42FF2966BF5C3FAGC064F8723DAAEBD0BF62A01D8C30G20E08278ADG7BG4CG9D93482E9EBA904EA7935D55F9A4FC0E5EEEB67AA37DA778677A6089217C11234D48F34C84EEF7D7883C4C7049BABC778726B259E1FAA16479A60A335DB3617C217993C3357F9413D95EA45ECE2E404FE278929D1E37FF0E61B13D877206CCA239FDDA935A569F2839342DEF2889EE6B0F56845A7A63B541ED7D691AC0DBAF33FABD0A8D267A
	E139D354EB2B41F50D6F53B74ACE594676BF38BD18E026B38F69E1329155CDE4B756BFCCB2BF510CFB34E6C8BBG86824482A482248364BDC2F4B6C6D78A6903G9DG83G2281A6814C96F4CFB539030C9D5647466B91DEBF33A58FDF5A0EB8425018B59E7DB6E6C1503617EACF9C5B45125FA67546260433D14BA43D6D1EFE06E41B77A849E2865333AAC81B815C82BF076187C1FA94C00A40FFF756E8A3315C25782C8AC363566571B62E89E823B736B65C9914DE167C2CE82E309B8B9E65E7C5850920F3G68
	F7F0FBF72229C423591798483FB9D1EF6F0A5EC3D9B7A96D9E455BF1208B2617D364FDF094EDD8CD1787AF47B449D63B0B88F8FEC4B16E079C34FBE18534DBE4C78F920D663AAA548D3CA1E4D6BCF61755A437899B39E9633BBD72EADC1DED2E83AE9F153D31G79ECD6E50FDAAB2B8AAF2A65CDBC35D21F67AFAA8DC9C4997C31D9FF8E532A49E7GBCF97E2C96FDFE1A16DFB01972CFD4874BDFAF722FDA2B4F5FB319671F9679835B7479EEADFF30485F5E9C2CFC0C48CF285567A7E87952949A63574E509897
	CD899C6352A97AB1DE6644B1E6FE8AD84A1EFBCDD7FF086DG3A4BA1FB6051EC71389C8E246179372F407C662ECE3CEF81795C5EE1BE77D156369A7D9874FD77C17B66A964476C3668E9BA26F01A88F8C6CF217EF65677682FC613BE054E9F5FBFCAF13674181A3167B15AE3B9FB147CB277E3E467FF4DE823A11DG10A2E87F55C43E463C4748FF3A27891FE0BFB4BFF7C5B37A84C1FC8C21573C2EA71B292E52A4BA377C71CB7D7D5BA1AB5E8AF9EE531A2479DEC9FE5F2B3469AB4D4FF96F405A60BC7418E01C
	0F62E3F43E1710C47D7C37EA62EDC19279CEEF3607382F41E695AFF75FDAAAD779B8153331F8B3C8B9B163F85931EDD43663F16243AEE494A96A3B2AFE1B544B7DB7433C66F57EE7AD79F5A9221D774ED2BB85CF50389EBE0B71B17797286C71D6125541A7C8D77A37212E740374E00058A7C43F6BC2781A7E89A2F8F2D9287DC9225EF6AB56ABCDD5B3D683D4CE95676579A16A75CFACD1EFD43D60F7AA5931FF9C3D3233564DD7D84EFA596538FF380879217D269A775116B07C402946FD346B447C9E8CF98BC5
	FF7BB7695BEFCDD6B3546470F61B177BCBF3B03E87E8E5078429034782EF31F5B79672938152G968294BFC9FE6CDFEBAEF9A73B4E9766CE768747102FFDCF76EC5F2C252DCF8672454BDDD79D2CBFBE0DFEE048F2E35686AB9729510F8AD9EED29B161B94A49FA1B5E4B9E7D0394F5368332605AA3730B61C1E2D8ED9EE4DD9FD39F2202B8EC29BD86ED42B313D96DD398EC39D9727517E6C8236DE8C1AE6546BC59636BFBB4D2857573411DEC7C3DE7EF452EB55922E3F6E69BD5B6FBF1D5A7F8BDBD786CFB75E
	87CBE3786869467BE08EAB5D870B033C6294EA7F56FAEC3FDFCAF0D9C598705122DCEE13A4E4D820FB7F7D513E5FA73C33CF5B6031468DCD34773DAA07F011CD34777DD9B661DEE736AD9E641218A27689DBA8CEC9C9916BC5ED6838B72BAE78B97E123A60677885F5616F2A2F2E8BF54E4F795B43FC05522740BFFB0AF87E45B16A07D9601EBFC67DB8ADF0A32A09AEC660166EA73AB8011B361668727EC2385D6B082EC860E6B0FF418A690B82175FCEF483846EEAA1679401FB31196852842E30CE6C5BBECD38
	6DE7E9ACEF96E3397BAC0D65AF44D8FADAA91E6FAF4A264BD4DF344065B31E93A1BD73E91A8F7F53661F8339967F7B20363E1F8F3776E99B3C4F57DAC88E69C28E2FB1DCC1AA44C329A40719D65C072C9469D7D85AA4529EAB710AB4FDA30FEBC59BBF673D4E1E72C8E0BE199B7AB0A015647101B8CF0990387D8D24ABF1296413663520CCD418707C3C7B7AAFD1DF78FD044E165AB0ED6B6F9AA00C03BA91B07EC63FB2A555F89F61881B43DE856F2AABD63E075DFE0CFC67B42067BC44FD6567611DE361F8488C
	41439A0D0779BA9EF85D338F7A6B7EF0FF383A33C3547D1116A7BDE32C5BDC6F2F7B70BA2ABB7F1960FEC2B83ECB1EB14A6E6DB60C5BD4174B51E2F73740F0E9EDFA9B7CEDFE549E2EBF0B8DED16BF5333BF55817D791D49DF77DF1B43553DD48AAFAB132EEEDE5F26BA2A6FE49079ACB35467G1AFE4F78F5ABC2D79F5F033AC1447C11109787B093E0BEG6E3B65BF0BB419ED2B4374014B79A3361E2AF2DB0BDDF5A8DC8ED39AD18E3C0E1B443EC14B3301FCEB679F5640BADEBFAC097CFEF5EC7FC96CEF705D
	8F3EAF31C6B16EDD601EE18734F571D91A33F3FCA8EF8B4628CE952FA6E96335496C67F3CFC3383EF27A4623C89FF87705725FB966677947E3EC1D70F4E97DA9BE61279B07CB1C969F7EEB9D451D914F52FCFE696B606D509D1B43277CF5FDF75ADF5729BA2AAB71595E5E5B21FAA23E41F76EEF0EEDA5FFB7EB867899B3A86EFD312247BE823FA7FE40055F5470EB629A9FCBE55A4FD1E7D0AC7D094C4EC0A0BD8EA0F5866D37CC7AC6FF17DA5ABCB168DD6A14EF5E4819BF036B4F6A19BD4F3AF81B13C47CEE1A
	C9FB8A4D8A617A4FA4BEB6F730BB2A10CE86C81FA9E254F5FADFBE6BB908791F6BCD8C52DA57A3F601B2553DAA77F0C06C52BB7BB87D8A4B9548D73687B0120DDB4BF346D869EE06EFFD4E98ABFDA1136EB8A0AF69B9BA1F3B0B1D03D6BD8FBEAA008AB090A092A06EF97233D33EA915C21F4BF195739F4A71FECD6E9103A5C2DDB31F6F0D7CCFAAC6F94C57154BFBA154D9DA368F7BD77C0271EECF8143D73EE03C5B332D036E7654C2DE748B64DB5C30AE70FECE64BA7D7D9C0E6B2E8B243BD82727637D39CB16
	44FB3C6E3DCE6EFB9F76CC1F3641F5A37ECC6898EC6C19CB1FBDA61E89FE7678441940334769E7021FBDBEF3A670FEF11A0546E72A08CFDE36507848A2BEF13135A8BFCD4DA8C993BE380815EA53441DB39F7BF68D5283846EBD060B04F402405D589DCE66D83E37729D90041637F1F758B6AE47BF8FF606466D642F1440EF29EE94B6BDAD0D7BA68B61D9B0CB4D40D804FFB315EDA31AD2406F83B88DF091E048AC327B3E6FB016567C069EFB33BC3F1579E14D8E3CFE42FD8AFE0EF150E7BC4740F663E751D9C8
	63F9234F0179C9101FB60B4EBA46AB348EAE84DC550BE413163513EDF43CC8B66953F6240D02F4BCC032205DECA37B157EA2FD7BE431993FFDC29D2B10685B256BED46EF1F30ADFF7EC41B715B2729BD3E1DFA47E67C76094AEB7E0ACF5C8DFE09783C7ABC6DF11FFE097A14DA483E5F06F48C403817C8EF9F8DD8F7FB3746BCF505655EED3FB23BF7BB9B1354170CF6AF0E61E73FE434FB7B5BE93C67C35E4959545FCA1B3E7DF840A74D6E0DFD6A6F8B47F71A210E85223DEB4E9B4B653F4CE9CB60D975F2A83B
	E6AEC27E8F3EEC5C137CA7431FFC5938A779FCA16DC9F6C0DE624B547E68F6C974614F3A772F5A7B5E8E3576E99BFC4D7E4AC66B7DE4315E1BEC345E1F926B7D8DBEDA5BABDF2175BE59C77B8E2785EE68F9328B9D82376687F6AF9152D182F738D06C533DC232BEDE04B1C61AC8373234B40752338BFBA38FACFF65B2DFD4541BB62EF4AF27EA8E767DCEDFC9981A12B91CFF9E4718DAD1845C0EBCA256690BC09381908F10B4075BD1EE83367F407C8259F24E66F1F5B92D12F6D7301E0D41EC201D8F4079D08A
	090F1274C019D5256B4F6BF72DE45F06F05B43CD0CC4779AFEDB24DFE38F26F3FE5C694453E3BE3409B58A7EAE905D6B6FD674F782972E0D8BF9A7303F46C7ECFA4FB8094F5BB74E53BA158679452FD2EC37E3FB0FB3F39E5BFD69F0F5EB119D2D7FFBAF107DEAF915567A0E8B6CBC9052D1G712FD29CDB4A76D2F91FEA8AB176F4AA5DA5D4FDC50D4B617214EE5F3933DC03B22D0DDF97120DC9FE15E4B23E905307DD9EF6C7DB3B377AC908F3FEA1EFADB6AC4A1338AD4CFF0D6211E5D69CCBB72487824482CC
	82C885188F10B7977C343994178CEA8D971770G589817AC88D807FBB71FF2AEDCD939AFAE3B9CDF5FD359586D1BEB34F333987E60DC231DF3DEA07DE9013C38396237G8A756D974F03F5EFDEEF6C5CE436FE548621BD29619C706C8790212523F534713A7724A0F5E8E7AA56503143BA6B25E307D2EB705821429A98BB6C30860F9DBE31865EDB7CDC2B9775C7FFEF31569A6C5EA217577397A471BEC7772EDE68FB7B7A6BBEED035B39DB8B836D6B1F8A493E5A845DDCB62753E04C974EA39B7949AA321B852F
	93AE157DAE4AFAC86F93388D6C77C14C106E9038696CDE69C5C88F923892F6CFA58A528982777AA144A5C3BADD60BE2DA75BAC3DC16BD54EAA769BB738A0305BB9DE4D2E2CDDC57B776B0116635A982E8A5255227C1BDB17D079963B651C176C5252CAAA6F90341FD5325FD8F1F9141EB66C98233D88F483846DEBB57A5FB561F459353426478A3A5D35780DE6277C2D4B036DB73BE58B2C2D1CFE5AA162A1C1506755E3FF3DCE595D62628CF35A12FA22CDFA0376151B6AAF3D2F3CF3D54F7D5385482E16777856
	5E5892CE7BE1E54FFA4E587C75CC2E79DB70FAAA4B613FCC509557763CB77F6AA1CCEF93695D7554867F2D4CEB4A3A212518D725EC2FC7DAA16ABE44BE7419797D0E3D19AF5C1C19A1E59D12F27822F2C84A42156110144D960EC3D2A6ACAABE3C2C1B2587D54A55AEF9421BF625D3151678AFEF2AD2BE5E44AC61D7B1772A523B4D6C7028C4AEE1F898C527AAADE7B7AEE1B4CB70F696D0E533EB05D027662E2852E2BC605A5A21F6C215395A45C16FF752CFA8BBE6FC0B34E7F6AF3059FDC0EB100B1FDF107872
	1E2339E8587BD7957CE6C7D7061477251453AEBB5C9DB2C81ACD811334340D099C8DB874354004E537C2592D2C6C27A6A94362D525B5FE3C8571BC97A70582A40B2D32679C2B8159DBF5CADA5C6E31353186D4A96354172720D916426C77CD52923583BFAEC01ED5C80B2A4DB85B350CCF5970330F185D6C0FF53DA8A48E978B7627790BE57F52FF871AF27553A13F83AEF8DB7B7E77ED63F72067586F0E34827E625B648B45EE5EA2057EC63039D3F6B8D459F1EE3F530A5F8B9EB82FAA7E3B8F6EF22C5FCBB4DA
	A64F6BAEC73E3F551579BFD0CB878871C50A72499CGG54D6GGD0CB818294G94G88G88GFB0AD0AD71C50A72499CGG54D6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG839DGGGG
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
private com.cannontech.jfreechart.chart.YukonChartPanel getFreeChartPanel()
{
	if( freeChartPanel == null)
	{
		freeChartPanel = new com.cannontech.jfreechart.chart.YukonChartPanel(getFreeChart());
		freeChartPanel.setVisible(true);
	}

	// turn all zoom on
	freeChartPanel.setHorizontalZoom(true);
	freeChartPanel.setVerticalZoom(true);
	
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
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getLeftRightSplitPane() {
	if (ivjLeftRightSplitPane == null) {
		try {
			ivjLeftRightSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjLeftRightSplitPane.setName("LeftRightSplitPane");
			ivjLeftRightSplitPane.setDividerSize(2);
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
		//styleSheet.loadRules(reader, new java.net.URL("file:c:/yukon/client/bin/CannonStyle.css"));
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

        java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        mainFrame.setSize((int) (d.width * .85), (int) (d.height * .85));
        mainFrame.setLocation((int) (d.width * .05), (int) (d.height * .05));

        GraphClient gc = new GraphClient(mainFrame);
        mainFrame.setContentPane(gc);
        mainFrame.setJMenuBar(gc.getMenuBar());
        mainFrame.setVisible(true);

        // Add the Window Closing Listener.
        mainFrame.addWindowListener(gc);
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
