package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (5/16/2001 5:21:13 PM)
 * @author: 
 */

import java.awt.event.WindowEvent;
import java.util.Date;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.DBPersistentFuncs;
import com.cannontech.database.data.graph.GraphDefinition;
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
public class GraphClient extends javax.swing.JPanel implements com.cannontech.database.cache.DBChangeListener, GraphDefines, java.awt.event.ActionListener, java.awt.event.WindowListener, javax.swing.event.ChangeListener, javax.swing.event.TreeSelectionListener
{
	//This is a somewhat temporary field user
	private int savedViewType = TrendModelType.LINE_VIEW;
	private static Graph graphClass = null;
	private static javax.swing.JFrame graphClientFrame = null;
	private String directory = null;
	private static boolean isGraphDefinitionEditable = true;
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
	
	private boolean timeToUpdate = true;	//true, update button was pushed, initial is ture also
		
	private CreateGraphPanel createPanel =  null;
	//Menu Bar and Items
	private javax.swing.JMenuBar menuBar = null;
	private FileMenu fileMenu = null;
	private TrendMenu trendMenu = null;
	private ViewMenu viewMenu = null;
	private OptionsMenu optionsMenu = null;
	private HelpMenu helpMenu = null;

	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
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
	private javax.swing.JEditorPane ivjTabularEditorPane = null;
	private javax.swing.JSlider ivjTabularSlider = null;
	private javax.swing.JPanel ivjTabularTabPanel = null;
	private javax.swing.JPanel ivjSliderPanel = null;
	private javax.swing.JScrollPane ivjTabularTabScrollPane = null;
	private javax.swing.JSplitPane ivjLeftRightSplitPane = null;
	private javax.swing.JTabbedPane ivjTabbedPane = null;
	private javax.swing.JPanel ivjTrendDisplayPanel = null;
	private javax.swing.JPanel ivjTrendSetupPanel = null;
	private javax.swing.JEditorPane ivjSummaryEditorPane = null;
	private javax.swing.JScrollPane ivjSummaryTabScrollPane = null;

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
									GraphClient.this.getGraph().setStartDate(getStartDateComboBox().getSelectedDate());
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
		actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
	}
	else if (event.getSource() == getStartDateComboBox())
	{
		// Need to make sure the date has changed otherwise we are doing a billion updates on the one stateChange.
		if( getStartDate().compareTo((Object)ivjStartDateComboBox.getSelectedDate()) != 0 )
		{
			getGraph().setStartDate(ivjStartDateComboBox.getSelectedDate());
			actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
		}
	}
	else if( event.getSource() == getViewMenu().getLineGraphRadioButtonItem() )
	{
		actionPerformed_GetRefreshButton(TrendModelType.LINE_VIEW);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getStepGraphRadioButtonItem() )
	{
		actionPerformed_GetRefreshButton(TrendModelType.STEP_VIEW);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getShapeLineGraphRadioButtonItem() )
	{
		actionPerformed_GetRefreshButton(TrendModelType.SHAPES_LINE_VIEW);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(true);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getBarGraphRadioButtonItem())
	{
		actionPerformed_GetRefreshButton(TrendModelType.BAR_VIEW);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if ( event.getSource() == getViewMenu().getBarGraph3DRadioButtonItem())
	{
		actionPerformed_GetRefreshButton(TrendModelType.BAR_3D_VIEW);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}	
	else if( event.getSource() == getOptionsMenu().getLoadDurationMenuItem())
	{
		boolean isMasked = getOptionsMenu().getLoadDurationMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.LOAD_DURATION_MASK, isMasked);
		actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
	}
	else if( event.getSource() == getOptionsMenu().getNoneResMenuItem())
	{
		com.cannontech.graph.model.TrendProperties.setResolutionInMillis(1L);
	}
	else if( event.getSource() == getOptionsMenu().getSecondResMenuItem())
	{
		com.cannontech.graph.model.TrendProperties.setResolutionInMillis(1000L);	
	}
	else if( event.getSource() == getOptionsMenu().getMinuteResMenuItem())
	{
		com.cannontech.graph.model.TrendProperties.setResolutionInMillis(1000L * 60L);	
	}
	/*
	else if ( event.getSource() == getViewMenu().getLoadDurationRadioButtonItem())
	{
		actionPerformed_GetRefreshButton(LOAD_DURATION_LINE_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getOptionsMenu().getPlotYesterdayMenuItem().setSelected(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getLoadDuration3DRadioButtonItem() )
	{
		actionPerformed_GetRefreshButton(LOAD_DURATION_STEP_MODEL);
		getOptionsMenu().getPlotYesterdayMenuItem().setEnabled(false);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
*/
	else if( event.getSource() == getOptionsMenu().getPlotYesterdayMenuItem())
	{
		com.cannontech.clientutils.CTILogger.info("yesterday change");
		boolean isMasked = getOptionsMenu().getPlotYesterdayMenuItem().isSelected();
		getGraph().setUpdateTrend(true);
		actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
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
		actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
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
		actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
	}

	else if( event.getSource() == getOptionsMenu().getShowLoadFactorMenuItem())
	{
		boolean isMasked = getOptionsMenu().getShowLoadFactorMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.LEGEND_LOAD_FACTOR_MASK, isMasked);
		actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
	}
	else if( event.getSource() == getOptionsMenu().getShowMinMaxMenuItem())
	{
		boolean isMasked = getOptionsMenu().getShowMinMaxMenuItem().isSelected();
		getGraph().setOptionsMaskHolder(TrendModelType.LEGEND_MIN_MAX_MASK, isMasked);
		actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
	}

	
	else if( event.getSource() == getTimePeriodComboBox())
	{
		actionPerformed_GetTimePeriodComboBox( );
		actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
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
	GraphDefinition gDef = createPanel.showCreateGraphPanelDialog( getGraphParentFrame());
	
	if( gDef != null )
	{
		DBPersistentFuncs.add(gDef, connToDispatch);
		getTreeViewPanel().refresh();
		getTreeViewPanel().selectObject(gDef);
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

	if (selected != null && selected instanceof com.cannontech.database.data.lite.LiteBase)
	{
		int option = javax.swing.JOptionPane.showConfirmDialog( getGraphParentFrame(),
	        		"Are you sure you want to permanently delete '" + ((com.cannontech.database.data.lite.LiteGraphDefinition)selected).getName() + "'?",
	        		"Confirm Delete", javax.swing.JOptionPane.YES_NO_OPTION);
		if (option == javax.swing.JOptionPane.YES_OPTION)
		{
			com.cannontech.database.cache.functions.DBPersistentFuncs.delete((com.cannontech.database.data.lite.LiteBase)selected, connToDispatch);
			getTreeViewPanel().refresh();
			getFreeChart().getPlot().setDataset(null);
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
		
		if (selected instanceof com.cannontech.database.data.lite.LiteBase)
		{
			GraphDefinition gDef = (GraphDefinition)DBPersistentFuncs.retrieve((com.cannontech.database.data.lite.LiteBase)selected);

			createPanel.setValue(gDef);
			gDef = createPanel.showCreateGraphPanelDialog(getGraphParentFrame());
			
			if (gDef != null)	// 'OK' out of dialog to continue on.
			{
				DBPersistentFuncs.update(gDef, connToDispatch);
				getGraph().setGraphDefinition(gDef);
			
				getTreeViewPanel().refresh();
				getTreeViewPanel().selectObject(gDef);			
				updateCurrentPane();
			}
			//else (gDef == null)	//'CANCEL' out of dialog.
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
	
	switch(  getGraph().getViewType()  )
	{
		case TrendModelType.TABULAR_VIEW:
			chooser = new com.cannontech.graph.exportdata.SaveAsJFileChooser(
				CtiUtilities.getExportDirPath(), getGraph().getViewType(), 
				getGraph().getHtmlString(), getGraph().getTrendModel().getChartName().toString(), getGraph().getTrendModel());
				break;
				
		case TrendModelType.SUMMARY_VIEW:
			chooser = new com.cannontech.graph.exportdata.SaveAsJFileChooser(
				CtiUtilities.getExportDirPath(), getGraph().getViewType(), 
				getGraph().getHtmlString(), getGraph().getTrendModel().getChartName().toString());
				break;

		default:
			chooser = new com.cannontech.graph.exportdata.SaveAsJFileChooser(
				CtiUtilities.getExportDirPath(), getGraph().getViewType(), 
				getFreeChart(),	getGraph().getTrendModel().getChartName().toString(), getGraph().getTrendModel());
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
	{
		getGraph().setViewType( refreshModelType );
		savedViewType = refreshModelType;
	}
	
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
			if ((pf.getImageableWidth() / getTabbedPane().getSelectedComponent().getWidth()) < (pf.getImageableHeight() / getTabbedPane().getSelectedComponent().getHeight()))
				scalePercent = pf.getImageableWidth() / getTabbedPane().getSelectedComponent().getWidth();
			else
				scalePercent = pf.getImageableHeight() / getTabbedPane().getSelectedComponent().getHeight();

			pj.setPrintable(getFreeChartPanel(), pf);
//			pj.setPrintable((PrintableChart) getChart(), pf);
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
 * Update the pane.
 *  Calls the html code and the usage code
 * Creation date: (11/15/00 4:11:14 PM)
 */
private String buildHTMLBuffer( HTMLBuffer htmlBuffer)
{
	StringBuffer returnBuffer = null;

	try
	{
		returnBuffer = new StringBuffer("<HTML><CENTER>");

		TrendModel tModel = getGraph().getTrendModel();
		{
			htmlBuffer.setModel( tModel);

			if ( htmlBuffer instanceof TabularHtml)
			{
				((TabularHtml) htmlBuffer).setTabularStartDate(tModel.getStartDate());
				((TabularHtml) htmlBuffer).setTabularEndDate(tModel.getStopDate());

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
 * Update the tabular pane.
 * Creation date: (11/15/00 4:11:14 PM)
 * Modified: (7/18/01 by SN) - eliminated a second call to hitDatabase()
 */
//private int formatDateRangeSlider()
private int formatDateRangeSlider(TrendModel model, TabularHtml htmlData)
{
	String timePeriod = getGraph().getPeriod();
	
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	int valueSelected = Integer.MIN_VALUE;	//some number not -1 or greater
	long DAY = 86400000;

	setSliderKeysAndValues(model.getStartDate(), model.getStopDate());
	int valuesCount = sliderValuesArray.length;	//number of days in time period

	
	if( ! (timePeriod.equalsIgnoreCase(ServletUtil.ONEDAY) || 
			timePeriod.equalsIgnoreCase(ServletUtil.TODAY) ))  //1 day
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

	if( timePeriod.equalsIgnoreCase( ServletUtil.ONEDAY) ||
		timePeriod.equalsIgnoreCase(ServletUtil.TODAY) )  //1 day
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
	D0CB838494G88G88GC1F611ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8FDCD4D556FC3FF4BF3F56025D58DD36BF7B312B35EC4B37EB2D3B6B96A5A996A9ED14D8D4D8E3E1D1EACB0546FED2BD7E69282322125AA7952519A892AAAAA9357EC1C59CE0E4C685C1E5E0C6989CE64619C702E9FD675E7B4EFDEF5E7C817D62435F713DFB6E39771CFB6E39771CFB5EFBA394FE9C191F1DD3A8885979027437BBDB90B6D984E14E70048644394605EA85455FA7GAB0472EF7360
	9A82F5316B423485425FDC04A89C4AD9EB4234EB60FE15B06ADC6BB938894027826A3E5E1BBAA3B81F59B548E7A1ADE7D768603A8CA082F0B1G5A3F05827F8FAA56CAF88D6016C9778432F3C0AECAB337F52D822F7F9B238B112E4577911A0A0AA229BE857004A683C0FB1F5C67A2AA5D70BD15D23975A711C246649DB9CB89AFC57F88DDD25B115235D8381FAAA2D77834EBA3DC2B8C97BA3F6EF07614F5189D8E27C334F6F415F53A4D2E2EBDFB0E104BC13B4D6A9085A10B4A376E0C16369504B8A8B3D91ED8
	DAA5718A3F90A9E8C6ED8CA84B3E7B198C167BE5D9F465073405B1246FB3D437245DE828CF81E0BC6F2CC01ECDC017CCE94BDA89ED985CC781444620ACDFFD0E32B4FD7BC7A1337DE44E72G3294BD40E428FEC016A5EFF86A028F68BC775512398A013A51G63GD2G32G0A9E8C552E2376356368E6386EEF978FBAFBFA1C0E7DAE57813B45DD6734D86DF03FE78F54C838C6EB07538DF64DEC2878CC299FF990AA9E74369FF94ECF89B867510DF78922284F4977B3363153F66654C8FD34BEA80F0D0D2BD0CF
	46F5857091GE3G628152G3463F1DCD70F6E98E8DCC741CE5C8E335D585B6EB23B45833DB63B456A164657D191ECFC7E00353BF84D6D18DE22F6A875127227D3B98B7D68A5A52AAAE743F8260F2A7132DE986F3B8372EE9D6FBB9FF45D0EA7F6789BEA07AE384F16EC146123A9BECE01E7B6DEC171241C84F51531E837CD4BF08C7A3CDB0502EDCDFE4790B121AE6776D8A64F58D8755C4E2FA3FB078670D9G058F05EACB8174GDDGD79E42790DBF7A45A06D562018DB56765FEAD743555AA73A4DC79D9E51
	6C68301EB43BED66F63B55A3C8B4B75692595B3D5A0552755481BC0ED89DD637D934B99D503B39478A0664E1B49135AB880D5BCAEBCD369EABB068F18958EFE46DAA385A4D9E71044BE2962DB2852B0F2AA53A7C46FC92FA048E60BE61A122735DF44F3A9D6E4781B0DDEDEAA0380289215ACD93D0AF6B0F2D046BB1B0EC2BFD4F1EEADB8791436C6EC77E9F6A49DEE27D863643EA2B4B6A30400DC72A5B20DFA46B653C134B3C1962499AB98802FBB83EDC3F0261218F2516313E12561BC551EDEB6F952DC62BA8
	5A9C1DF27BFDD4960F57727388ECEC9F9B080DD54138CCG96BE3EC7752B946353BB1D97FADDA6B2F3121E9E227BEC9F5007CDC0FF9755CA7C5F5889E8272FB7211DD6AF3DD3A81DF6522F1D92C818406CB3F502FA2D5DCB7B14F96CE9ED3A46F5BFD8984BF998070C87D94BCC6FBFBDCE742E1B88B685D0B391753E3C2E182D3783E60F95D7D8039BE64CEA112D0C5166578736B1DD3DFF9BDBD96F5F46565687372162FAFFB6446ABE2FA73E45F949213049527A15F48DBB9CD6AAB4EFA230F862F59351074DE1
	93CF59AC1DD651E83BEC9504919309ED5553B558867762C43425CB6B5116B6DC7E3590FBEE1BDFFF96F127A1A7E1A233A14DC4591618CCDB9B084E2DF6EB8FA86A203357A1CA3E192EEF89AF6DAF1F997C53EEB190FB3CE06D17683EB0AC9048BEE26F3572310DB1AFA1BAF65BFAE02A480215E23A648AE5CCA74405EAC97C37812E15G2C6DFABDE92B8C26183DCCE9A47298004E82F0A58E6D6577068F946BD4B21403CE3B1D291D4FC7447182B2FF560B3DD6580649C2BE4647701F47D72A6ACECA63E1753FBD
	CEE432AB760070B8B29EAB0F6F080FE038AB947785A8451A0BBE372EC099FFDABCC77D4E9D0164B8B6E73363547ECAB011FEF513A0261A04E37F34D139C76D77F81C9DB66A8E24F9F82B04681B462C5D66EF4CFDE51ECBE053FB76549A4C5002450B87F612FE493AAF1F047162F327082E3B209C82904179BD5168EBA7CFB5AE066BF91B5B83F10E455A07BACBE9A4E3A77EC7C2E3DC36DD290BD84DD781F5B1E69253C15CA435AE8E1FA0F2E8G1F41E5BBE5DC215085441060EEBBE126D01EFCD3AE33DB293C48
	140FF6ABE0CCF5E427BA2E51BEAC47B3C5F0DF7E30F7FC4362177CE6222F6A0755F14DF20AB7BD2C0EEBE61C42384682F5718F231F2DBAC946D57AC828366A919C572FCCCB9563B2B8ED64F8A10D61588932761CEEDB274D01B88345C184CDB7A3662B4E52B956C31FEDG8C37F7B7418D03726D9C57DAC9F031D0CEFE84FD4112E6325EE773727B342C0B4772AB27543E02507ED07E0271901A54BCF2FF207E17F1DD6EBFC966280F178F51F23F44EB15503BF3813567FEE7AFCC406D71C4DFA3F762FED68E659A
	G974030F8969330B5F84FA532F71C07608D161E51E56D907A9EA5ED9BF7E35BD4204F84D88B78CA00A6G4653D3C95AC2C3DBCFEF8FF59099346D0F59BC41FD8227DD5CCB76B03D753C58E86B6C920DAE3BCDA44B9C4F53071B15FBEF65E4A6E39BDCC5GE617E3F76219F2A46062A66398363511D8196FA3E566CBA2B96F5AAC874C1DFA2A913617E4D1BBA36D33A663DE32F9976933E28A4456G16A9684F7AB66259B37A2EDB85FD48A93F67E00D696B1CABD318BDC5CE11770174E1298B984F3E5D48B3856A
	8B93886E2E5D05AADBD98113D7C6569F2DCF762AA77BC609202CDFDC292CA772DE55AA6B2B3C6A73956D595826FC06E34B1A92A5587FFCA860993FB5C14AEBC8573610F5C246FFF5640ECBA079153F1C91387D0DBFC56640E13DD40CD3D0D68DA13D03846F1B4ACB0B4BCB986F48CB4A7D35F27141007BEB16443360B1F6CD38E71D64B7EEAB731AEF59EFF02BE23A7F2E99FDC74DE3B85FFF6DA7739D82658800B10F61BEF11F4F5EBA38359FFF1D6D32F7DDEBBB36E7E7D1FB0DFF0C6D59599C1F3F1B601394F8
	36E64AF7611A51C0DD755FF1AF571CA56BAD8A4AF1GA9G99G050F038FF99C777649A74B05814E16A627CB8F812EDDF16CE1633B3A7B7A4E7F8FADC45B9CFB5FEF04F1BF4A4FD99240BE2A9F1772CF52B5733BF5944F4667E9A3638B073AB1GC9G338104C4584792F1FCABCF1532719D3258447D22E86E682211838E6E244DFAC9C6134D114978F80B52068BEE5D1A139DC04652C4A6DBE522BAC6F859CCEC538E7891DC1E79E73640752439DB52B4914B692652G5260F63A2CEE311FE1E984A859528D4DE4
	9CD1097E7DDA0C8A1F88E5E6B3DBAE935BC8CDE4B6134FEDE99745E7AA704C169E20EB0714F5CF00AFF98257CE4EEE015B7592457D2EDDC3F5CE1357D1431072E073726A5940673874337E4FF1734E7ABF47E51D8DFE0E4BBB97789C37641C729C476CA82F036C75C9B09769G05CF42F995206AC93429DF1E7B1459389F7BBE84873B03CE1A7991117F153640E33D21ED60FC5148B692EF37BBC55159C37AC77CCF5B48FC09CE97C5B25CAF5A080E5CA49EE024927E0EB63AB6A0D6A0E8B64ECFAC822F5FEA7EAC
	50BA01D15502CBE8E86F06885BE46E64EDE61917A9754E160FD477261968101CF5EDE63B6DB2BD229801505149624EDB299FE9821D3A1E44D8B435996353E19CF73A1F6012201C7EA42E0DED1789AEEFEA2836F8AA622AA8EE9F14AD9CB731DFED47244D355AEBF100B60CC70A8F0F6B09D373032E95DDC7E07BD955B1307D94F550B39C4C3B295FE5E538CFBA68B9627CF90F155B69460E820EBB6AB1B8BDB6B2EBAC96392719788C0B6D9B9BAC9EC5DC7AA81DB3E2F3A3C0CF1A2984B73F39DA12BF420F6E5E13
	7EF2A5DDC583CDAC407BD2B9E92A6CEF996F0B7D4BE55EFAE787BB6FB27EB7D27B3803BC97CB62B1F4AE093DCCEE2B15F89BFA7065FD7D463A0255B9AC55B60F8B02FB2CE7B1336332D74C8CFD12F3CF9FDCC782B01ABB356A1C8621998D7563388C1AE6A2FF05004F05CACFEB25F9CE841A0FA98D1CB5857CCB7F17BF9C0B89E711E5AC797299BA7465C53F3FCD28FEBABFE73D605FCFEA27B15DB7CD137DE445CDB397B079FE5DCF2E5B4BF7C971DF6EF75B857F4F3ABAE820D25659EBAB6B95EDF0A0D5A9160D
	6F2E4BB83E30E998973E391046D5D056GE4CE4367D55335C8DB7094520E5EC2E8EB205C86507794DF6B2715B1CF9860C7BF95FC8D333D2D38135860B820CD824884A8F8BAD43B81208A40GE0816883888308820881C884C88548G50A607EA0B12F14FAFBFCF627DC0F104F4B0229B3F5B4C4E6BCC7E5FDF8E963315C37F55497E728B6735C49FCD496A1854C97116E4F5CC1A3990E3D29754A5A5237EC2CA547CAB1F1179691F7155A5633336147457760CBA0ED9CF7162B36AB8666EAD98470C003A594F607E
	DDC36D5E70AC23E738BA0A6B12F0CC3F951D0177383D1D836FF1551D7EFDE4ED272F0FEC68746FA3CFF4AAFDA4431D6EA4E36A0479ED62F467B82E1963AC34CF02BBE53308DD9C5FCD65A2789AABE108F897156B9245B5C938DE852ED942DD6EA47BB48C394B6946365FF712FC4CF91B5DAE459014466600ED1B6BE384155166B2AB643E09631A79DCCC384CFD6E33B8E77FFD99F3C2E31EC5BB3253ED095D77AA6E5FDDB87471689A6DD072E0E37EC58999D49C0CB7196BE1759654CD965743C72584376939D06D
	3E6790D7C2F12DD08E6138B1DB86121974912CFEDF10FA46635181F98C940B840ABB30AED7B0EF5489BC0E23264D70C5FDCBE5BC1A36B076B92237E7AE978769F734B4AE663F57FFCBF41785B8ED8A7A4EBF1478665DA615204F2DC8E1F9B7BDDC5BD2D83C4778EE5D02FC7BD2FCF5B7D242B19E8FACA36B52E4EEEF37DAD84E0C6163DB48BEC46383B8C7743A1813E3FE3F25D46977A3A55E89F05524E0EC70EC3DBAB6A0B419D02F1B0E631BF7192CDF324FB01F5EDD207469A97DEBF2C80C624F27EF184E46E0
	18AE7BF4F2E5F10241A67071CDDB781190B0F734333C34F8DBF9896E5DF3CA50F70C00FEE327231F5E399973EC195351CFF7ECA634654F439CG34BD0F34A73620CF6FFB9E47F95396D2F6CB36416CE60B76C341EF1EC9B995D2CCF1CBA976B966F964FFC7BD611F86E56105D06D5A97107F4A867ECC6385247D330BBE5300F288C0448B18275959CD6CE48C1493G5281349A7075G95GB5GED9A747B9FF5854B9F7064184273333579160F5F9D5CDE173D797A5A6D3966F64C1FBFCD67730AC69DE7E8A8FE04
	C69DE758B6232D04C3DD2686773B53DB147CDBE74039EEC6707D0D350BA88DA677B068A3E2063F78E885FD2E99BD0349371063BF2478B8851E49BD0A5A8DA927C05D3E97D16EB58DEA7E23DF14790D037B04977D717F72A269CF73A263331C630FD23C76A5994F785FCFED11144B21AE7AA5342FFD5D01631EA35D8347BD06EE7FF14F49EE5F3827255BFF5CE36E760DFB4E53FE5D56CE58AF6A4DBD3CDFFBB75977595EDCE7F618BBE95E0469F2BAF95DCB3AFFD9F16F5CBC74B14208A1654162010BDBB0169D35
	GF15FEC41D8761DFC447D31946306CD2998CB2C2F43182AAFD536BDE293A3219C85904F716BA9BE8D4A3A19215A7219087FDD3DFA0C249D39462786DEEBA30354796AF1028F0FFCC5FD2036034DF9B0DD14B56033C953CC54CF1481F55647F16597D1E771DC877F3AC81ED324C0B993E06D4BF01E83E882688388FF99699AA85DD8A8A781E4G943E827BBB0089C0FC85699EF2397C48C97AA357F029FFF23F76650175C8E48AD4C7E46E137AE03CB5FEF9AF112EE4DFD8B4885D2B67A9507CAC156ED90E6661D2
	4C51043F62ABB366DE902200A66695B9C713780A6C97D89C2727761D827834D7507FAFF5901E743D8E706E5EF918611F127CA020FB6DE6B7DC0C9DEE275DCEE8D87DA79F13FAE3EF8FF91F442B1E45D9A79B54F196093F8B53E04D25E1AC66ECD4526CCBE3B4F63806F01A7CE37EE8A2609A8D40E24FD31CD7DC9A0BBDD3601A1E4662FD264F7186542776D57F7AD46A3488E84ADF1575D97D2ADA1F4D54768D00EFFD9575D9381A683351FA5EED75F49D6805AD5F41757989FD1F65E02F1B3CB057E836581CC840
	6A430B882F9A1B872297DB07596EC5A2515CD2C1E3DF88938DD6374DE9511B5BE146D85D664FD7FA559DF47634BB8FB87B306D9D85E41CC6516C962B4D2215B5156A9EE9D9212C131B8AF12F12317E682272596A08D719DE4740155527FA556BDEE375152FE17DFB5D7E6A753C7E55334A7AB629FE982F37B4A96BA3247AD8DEFFE3133F76C93C5E65D51FA655EF1A0531695BBC0E2D1AF52D393F6997C8095D8BBD821F2B107AE2A1E0FC3F3D1C8478BBF616B07EB77358D81C0531F18CFD97AF9A4AF1GA93370
	39FF6721G71393CG850C4FFF5B08634A607D7E4AC87AAD1D9D2A2D86E81D0DBA786A98520A331136C2CFE500F29CC04AECFCA7617B860CB1B3GD7BA877BF824814FA316B95847DCCAFBBB144782A4F05AF48372CB1DB3304EEF7DA5797B1D2473758ED967ADCED967912E0F8750B928DCBA67ACB7FE60DF0FFC4DB1B99F206B1C2C6B225771BD092B07155F8B44AC7FBA60735A10572577A4DE174F037B0627AEE0B22CBE7011FF993C963F745D029B65503C0EBA5CF581F37B557FC07BF86BFC00774B680AE7
	6D0A4F213DDD616DBAE92E4ACFBB3E9B303637B5E15B317FC0B99E6238593C3F9BEA3789034CD72B769C49F61B50BEB624E3AC1065F596E9853CABFDB0E718DBFC4E9043946DA2D4FDC427633362F27AFEE3EC3A7A6C75A545A7242B4FDE930E633AC8063A2AB7D06E6846E0E72848B73C790FFD83792FB4517C7C9B6AF7580AA8BE798D75BBEC230D788EDB9A54695FC47E870E8563BF66CDEF7EF1EFA27FA83A7E135FD4733F1762535ED4735F23C77E19D0577A96729F6E237F01A0503C8D2EDD42B537E36332
	5177EBE35FD24F371B62935ED24F77FC3E8FA6C3DDD5860EF72EC149BF9270E3B386E32793AE2AE37BF8E817A235CD9F208FA5644D05589B20F42E5C0665D6EEF391DF388F702DGA240C8006839F85E9D659C705B19C3FDC13E1DA9F67BBB67A54C8DAC3BA6C0DD86603573C235C5G657314F1795E8B7E6637DA22E9022B9DA0E41E5CAFEB7759B9FF6D42A53AA838464C731605350BECA26D92677917B3957019FE5BBD5C640F1F6EED4997403562EDFF6D227D36C32801B62D2AF66CBC36EAAB1EFFFF4CF1EB
	376279F78EBF7F5E4D4FF72E3771AC3761B81E6F42B92E1A624611725BE85B3B9A85AE673F9477714707BE07B0E3C8F930B1CFECC4BDA4F3BDECB4529CB8148BB2D18FEBCDA4C7DC444B1F52F2A5AF5FEDC4D9894D5068E3A26771C33EFB6B2D0F66E3280F56CC54C78C4505C0B91C0F778FFAB27EC8DE9EC74B313C3CCF2F1E3B489FD0AF0CC7338F0F9FD2AFD22E4CA0707BBE45FD36E1686D7FFF0614871BDB5DF15CC3A6719C5A0A633EB934D49A97EBE08EB338DD145298D57BCF70BD7FC45CCFA8EE83142B
	B8EE4DD534A983474D70E0AEA91263B652E7CFE3219C7FCF6E1FDC017331F73B7C3F17B646657F3D343F3802FF437620AB50FBEBCC3E83FC3CC9DC66D1942701F286474DF663F8357F0DB86BA59CEF93475DEC46715AB96ED61A7B6D03F28447B5DEC03E519C77278B48E39C479DB0A38F6DFC44ED6BC29EC59CD7F58E7B5B44F1F71C437EAAB82E259D7B9B46F1FF25F1FA841447F05CC77CBD31980EF373363AAC44CDEFC23E459C77B5EFDB4AF1777136D7B8EEE2935205F15CA8CE2749425C50D41E9B4AG1C
	6E9D988FC08DG495F363DC3E8769ED7661854404E16ADF48D0866F613CD34DB5959331C180A407AF8166732225E715D57D89FDF99FD7B3091F3421C41A11A5F61B9B76533CD2349671D261920DF6015788E0E4345F3E5E981E518FA4CD7061122AC43708DB4974472FA6CE11A14D310F2B5FAE5AEA56B9D167F5E74AE66406653FD2147E10560515641F2E5AB5D18972862F4875D44BF0830D8198C955F48B2C43969B79666BE55B79631341F9A684374AE66754A2ED68B0167CE1D578B01F6616F323C9EB9DBC6
	3D2B4E6BAD96697AGFCAC17752BEF97D0F95C741B9EA66F29CBC4DEF6FF39CF167DBEAA2347CA3EF533DAD81FCFD09C291F4D654E758413BBD7A5F716A477BAC96E027754F25FCC7713A240EF82E0F25D51AF4BF5CFB74925F4B95D364BF08EB75B775BED1D8E72E62C54FECAB76692CC3CFD5E3724BD13F77205E072662B648DF90F493BDE2A8B771177825D1BA2819F4D79FDFEC556E96DD5197758F348BB79BDDF3BDE28629D2E5255EC9F5E4368BE118978BCA1CC1A633FB7911B931DCE3B4966B201D9CAF2
	74FCA74B3133B9188E962964A8073E15BA280632379C37D1B98C00EF85E07C423E17791D884AEF0960FF5C48EF048F3F17A83FF040C782B09E4FB6A10F844029F9ACD57150GCD86G2E9FED361AC78265D18878E2G75B9D98F38E1GACC774F219D74E09FD43F440C93E97E51FB0C9392003865CBFE220AD4BABDE24E7728CA86BF242349BGD8EEAE7E0C72BB1F62AD85FE3F7349BC33B727AA070D37CD3A125CDC4DCDB325EF1884A3661F42211E61FEECC2DCC28E4AD1F21A4811179B26AD822848C55A1F73
	FC2ABE97473BCD97A0F76B354FCAB93ABAECBFAC97ED3444AD2F0FCCBADA0657DD8D72AE496E1D25455BF7168802EF0ED563A72F0D7D2F4B116F3F4E15F92D7BB6882F8A6445E853589E6F6E357A634D7AB66749FC46644BFCEEB08761330370594E793CC85F97D128344CC87E47577CBDE50DC70F5418824B103CD016E13ED616E1E357E0E4E073B5739C4ED7CCAE2E65778E497DFEFAC46E77C8D330FE4923018A3F73054F9C8E55487DF69D157B6D8956AFF927E87B0E92EC17D8AB777156B1398F6320E4E3E3
	DE524C1FAF401887775C84F92E2E136F3F2C17792FE89D4C98704CF5FD79416557592E661A5B317C61CC3AD715660529720A2FD3FC25824F764BCB865CAF2B21AE8A00656ED651D8D6179F265D84208730G7481048130B3CB52771B04406FC331EDD5FE990A0DAB5F289C57E868EBDC3E2FAF7055C718C92D0F84C5BB0D1FBE58386F20FBE6C6FE186A5D65A80A57AE88D33D3B7C45E9FCF739886AC2G987F5B3C725A29004FDCB09839BF2B8BB60F3A055EFD94AFC4FE2FF9658FABGDF33F0B07C8A1A3D1EDB
	C09B57205A4DD26497B29477D77F9FF28DBF9FD29E52F7AF8D014F726B9B86FE372A34417F3BD55B9BFC5F2D5A5D607F5D2A2FE8AE945F2DE26748A2A366C026729C48A7C64C0118F98E6437B49E9886F39981A065ACCE7233AF371BD174FDFA8D1485AD627EC0F1EB21DC49F1BBCF875339E6G1BD1826163FFDE6B03723816FC193F77D9188E6EBD067A3B1F6B6F2F47D0FF47387E2A6AF8EED10B7A6B2C2739C5A8C7F0DCF69B76A7FF2330208D7B13F11FB77B5215B6AB69180FF950647B7E72B4FE3E9E8DFC
	C91EA0862E09E4FEB2C3356C77813EBA494F57004F82A8DA94262D82E8DD04B1DA5FC57A7D92BB43FB45F52C3E0D466DC6BB79D40A1D6F5939DAF3DAFD2EA6FC2FC03F6C7CBF33C5FD7EA775E1D0BF9A0049E72CC31A9840A582248394AC8E5316AFC679EEE8A17CBB4833D807C8BEB713FCF0B45DDF58FE7B0E897D0F815A0DD80CB16D2193980747AE467857F302FEB78B6502A5601716A0ED47C90C8D76810EFD23EFEED67F86GD9CBE582FE43FFCB337AB7G88AF39FED2337AB7G26FA7D06405B4D6A5FG
	4076CC26B5ADA8D39F177343D6FCF6BDE6890E6958917AFC934A02AECC3BD607347DBCA6AF5761186E29D30F69185718DE2A8BBE26776B55E33A5FEBCCC76A830D092D675C9357974B2C384EF635575C0E7976D7281DF46954B1D0BA450BBAF58C54FF82EDB0846AD2F538C716F979521A253076168E4617963584135B256AE358D26477E92B3ADD04C49B8D57D8BF3C593846D39BCADA9A26FAF67EA84527AA70EC3C470F6033738C28EBDA067C3F730AD5E2G1F38ECB0631DF3C260777394775F1F987A58A1E2
	C8F930FD7D905F6B9F63BE26166F75AD5C476CE8C19F133A8C7D093D85FDCC41F244FD590A3E3C08637EC4FFB7EA9F141BB86E0143581FFDB94E5124A364909B414B0F5372D8DEBEF5E4B07AA06D2FDF672EC171385E67E1EC6CBF2B73756B3756A97DFA5AF272DB3635AD45C17ACA1246497C4E634709EDB29F5C4EFFDF363CGE2EBG9740308296A3B17FB5798C0D794C9D97BA5D4EDE078573CEAF325FC8845AA8GA6C752E114A32940D737E9920E753BEB857DA61979ED661E854CF98BB5DE3ED912E7438A
	142933054493872D64B7C571BB60CBD665B7C3C59FE7877CE628F285134332C24EC311AB5B97EEEC45FDB0826A93D7E0AEE947F62F7757D86E6C305DF9C94A1C214FFB3295FD36F6A57AB7618C69F39F141BGDCABB1DFF11FD1607A3877304F3BF4871DF627FB53761DBBCBA5A811F8E49DC63FB8E2A56A246288A957BB5D74DBCB691B34AF833CF347754D7A5BDD2395182F0CDD09E75F3BCDE4AEB72C8A536E8330GDC81888308820881C8DA05E760D50D414E40AC4128BE8307796C7303DBF73F3A4EF6CFDF
	F3BB66BFC4BAF7692B54FE74AA45E72DD27B5185E750FE3405E1DAFBA16EC959C7147C93811FDAB8983FD2D5C37619CCBF343A77A57B05EB85C00DD4EEFDDF26F5A96E073D6F5B871B3FBF1B821F8BE3CC831F8BA7197C1F8B2718FC4F0509A67F674227CC4AF3A143BDA775CB6CC7794D4D8CD360EFEEAE535F0BE277B7A86EF5E7065E8FFFB624BC24770B0E787A618F0F20CF6864F4E7691A0E00B99F73BE7A52289A742FF19CB7191E3313211C4EF105F4AE72D603EFDE0D3839C7892E9C4A559C77F5AD41B5
	C1D964387F203891D00E643829356843472E467D2A37067EBEA65990681B329E492F5CD4034F6E1201D6FA1EC7F1E9244C5B4FB0AE4676A6DB47858F7A25FF9A31FD719A245DC26D683C53ED7576E18769B84B012E0A536AEA153F09A8FD13DB0BFB3A05531D28A53F8D52E36EF63A89FF234B5C81FBAB23F754228CA2270F2CA56375B84CAE1313894CE847F15AE1C04B1EAB3FD6BB70737151B5186BE73E64E7A785DEB75EB898DF42E88BCC5E7D4CBFA577B36B68877E7BA9AF05FFA51CEECFAD51CDB9AF8F27
	65ED3C7CF8AD72E03F331F5DBF8EAA226788799E373052B76F3C7F66E71E0EBED7DD18A3649C9596F4510F290F8A7917485756FD6493A17A87ED177336C769477E1AEFF66C49FDE9D4EE0E10878D5906F4D448A33B8A34F733FEF2E1C3223D64A941F3D120CF9E85E2AA5BE9A9C7D07DF11AFF87089F0006BC960C9705FC7A390FA8AC14C29D38139E76431D74698A6F0900CE029C2817BEDF712A67A08A0B654FD78211684897AA456C9315BD223052C89F14971B0BA99E2C56A18ACB6817A845643570BDDCFC7A
	0B323C57FCA5F851EC7522A97421A15EE37DG7D78527876C3AC7F23817AD963B5F57F47FEC4E191F9DB620BAE31472E3C8733DB663D9C326FDCF3C3C1680E5E9CC1FBD8D8F05EECF7F519415C689E22971634D33BA3919074D320A7ED3F0036DF5036FB75C2CE07C7945649788E02E735E4D7318249A20B59FD0176C0C47C3CC5D8F45E5D59CE9908C2CE4B6196E0CBCB243AC4AFAC963B489758C4E6D1D8A3F7EDA05B25D431172DA4724BC3AE7AC7073FB1A08E0061D83CDD723D727769B207EF88789B0BF9C5
	AC6ED8DBA4475BAC467C7C8A597BCC00F795E1ACF9695B2DC2605F5DB276186DF651EC3F305FE1A13F41F520DF344A6F1715DEA17DFB10C62A14FE5F798A113BDB5166FF81D0CB878878BC46606BA0GGD0E4GGD0CB818294G94G88G88GC1F611AD78BC46606BA0GGD0E4GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA5A1GGGG
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
 * Insert the method's description here.
 * Creation date: (7/6/2001 1:32:01 PM)
 * @return javax.swing.ButtonGroup
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
 * Insert the method's description here.
 * Creation date: (10/25/2001 9:55:03 AM)
 * @return java.lang.String
 */
public String getDirectory()
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
private Date getStartDate()
{
	return getGraph().getStartDate();
}
private FileMenu getFileMenu()
{
	if (fileMenu == null)
	{
		fileMenu = new FileMenu();
		addMenuItemActionListeners(fileMenu);
	}
	return fileMenu;
}
public com.jrefinery.chart.JFreeChart getFreeChart()
{
	return getGraph().getFreeChart();
}
private com.cannontech.jfreechart.chart.YukonChartPanel getFreeChartPanel()
{
	if( freeChartPanel == null)
	{
		freeChartPanel = new com.cannontech.jfreechart.chart.YukonChartPanel(getFreeChart());
		freeChartPanel.setVisible(true);
		freeChartPanel.setPopupMenu(null);	//DISABLE popup properties menu
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
 * Return the JSplitPane1 property value.
 * @return javax.swing.JSplitPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSplitPane getLeftRightSplitPane() {
	if (ivjLeftRightSplitPane == null) {
		try {
			ivjLeftRightSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
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
 * Insert the method's description here.
 * Creation date: (5/17/2001 10:57:58 AM)
 * @return javax.swing.JButton
 */
private javax.swing.JMenuBar getMenuBar()
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
			getGraph().setStartDate(ivjStartDateComboBox.getSelectedDate());
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
 * Return the SummaryTabEditorPane property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getSummaryEditorPane() {
	if (ivjSummaryEditorPane == null) {
		try {
			ivjSummaryEditorPane = new javax.swing.JEditorPane();
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
			// user code begin {1}
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
			constraintsRefreshButton.gridx = 0; constraintsRefreshButton.gridy = 0;
			constraintsRefreshButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRefreshButton.weightx = 0.5;
			constraintsRefreshButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getTrendSetupPanel().add(getRefreshButton(), constraintsRefreshButton);

			java.awt.GridBagConstraints constraintsCurrentRadioButton = new java.awt.GridBagConstraints();
			constraintsCurrentRadioButton.gridx = 1; constraintsCurrentRadioButton.gridy = 0;
			constraintsCurrentRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCurrentRadioButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsCurrentRadioButton.weightx = 0.5;
			constraintsCurrentRadioButton.insets = new java.awt.Insets(5, 20, 5, 5);
			getTrendSetupPanel().add(getCurrentRadioButton(), constraintsCurrentRadioButton);

			java.awt.GridBagConstraints constraintsHistoricalRadioButton = new java.awt.GridBagConstraints();
			constraintsHistoricalRadioButton.gridx = 2; constraintsHistoricalRadioButton.gridy = 0;
			constraintsHistoricalRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsHistoricalRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsHistoricalRadioButton.weightx = 0.5;
			constraintsHistoricalRadioButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getTrendSetupPanel().add(getHistoricalRadioButton(), constraintsHistoricalRadioButton);

			java.awt.GridBagConstraints constraintsTimePeriodLabel = new java.awt.GridBagConstraints();
			constraintsTimePeriodLabel.gridx = 3; constraintsTimePeriodLabel.gridy = 0;
			constraintsTimePeriodLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTimePeriodLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsTimePeriodLabel.insets = new java.awt.Insets(5, 20, 5, 5);
			getTrendSetupPanel().add(getTimePeriodLabel(), constraintsTimePeriodLabel);

			java.awt.GridBagConstraints constraintsTimePeriodComboBox = new java.awt.GridBagConstraints();
			constraintsTimePeriodComboBox.gridx = 4; constraintsTimePeriodComboBox.gridy = 0;
			constraintsTimePeriodComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTimePeriodComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTimePeriodComboBox.weightx = 1.0;
			constraintsTimePeriodComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getTrendSetupPanel().add(getTimePeriodComboBox(), constraintsTimePeriodComboBox);

			java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
			constraintsStartDateLabel.gridx = 5; constraintsStartDateLabel.gridy = 0;
			constraintsStartDateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartDateLabel.insets = new java.awt.Insets(5, 20, 5, 5);
			getTrendSetupPanel().add(getStartDateLabel(), constraintsStartDateLabel);

			java.awt.GridBagConstraints constraintsStartDateComboBox = new java.awt.GridBagConstraints();
			constraintsStartDateComboBox.gridx = 6; constraintsStartDateComboBox.gridy = 0;
			constraintsStartDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateComboBox.weightx = 1.0;
			constraintsStartDateComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getTrendSetupPanel().add(getStartDateComboBox(), constraintsStartDateComboBox);
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
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 1:51:05 PM)
 * @return java.lang.String
 */
public static String getVersion()
{
	return (com.cannontech.common.version.VersionTools.getYUKON_VERSION());
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

	getGraphParentFrame().addKeyListener(new java.awt.event.KeyAdapter()
	{
		public void keyPressed( java.awt.event.KeyEvent event)
		{
			if( event.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER )
			{
				actionPerformed_GetRefreshButton(TrendModelType.DONT_CHANGE_VIEW);
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
				getGraph().getPeriod().equalsIgnoreCase(ServletUtil.FOURWEEKS ) )
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
	java.awt.Cursor savedCursor = null;
	try
	{
		// set the cursor to a waiting cursor
		savedCursor = this.getCursor();
		this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

		if( getTabbedPane().getSelectedComponent() == getGraphTabPanel())
		{
//				com.cannontech.clientutils.CTILogger.info("GRAPH TAB");
			getGraph().setViewType(savedViewType);
			if( getTreeViewPanel().getSelectedNode().getParent() == null)	//has no parent, therefore is the root.
			{
				showPopupMessage("Please Select a Trend From the list", javax.swing.JOptionPane.WARNING_MESSAGE);
			}
			getGraph().update();
		}
			
		else if( getTabbedPane().getSelectedComponent() == getTabularTabScrollPane())
		{
//				com.cannontech.clientutils.CTILogger.info("TABULAR TAB");
			getGraph().setViewType(TrendModelType.TABULAR_VIEW);
			if( getTreeViewPanel().getSelectedNode().getParent() == null)
			{
				getTabularEditorPane().setText("<CENTER>Please Select a Trend from the list");
				getSliderPanel().setVisible(false);
				return;
			}
			if (!getTabularSlider().getModel().getValueIsAdjusting())
			{
				getGraph().update();
				StringBuffer buf = new StringBuffer();
				buf.append( buildHTMLBuffer(new TabularHtml()));
				getTabularEditorPane().setText( buf.toString() );
				getTabularEditorPane().setCaretPosition(0);
				getGraph().setHtmlString(buf);
			}
		}
		else if( getTabbedPane().getSelectedComponent() == getSummaryTabScrollPane())
		{
//				com.cannontech.clientutils.CTILogger.info("SUMMARY TAB");
			getGraph().setViewType(TrendModelType.SUMMARY_VIEW);
			
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
			getGraph().setViewType(TrendModelType.TABULAR_VIEW);
			getGraph().update();
			StringBuffer buf =  new StringBuffer();
			buf.append(buildHTMLBuffer(new TabularHtml()));

			buf.append("</CENTER></HTML>");
			getTabularEditorPane().setText( buf.toString() );
			getTabularEditorPane().setCaretPosition(0);
			getGraph().setHtmlString(buf);
		}
		else if( getTabbedPane().getSelectedComponent() == getSummaryTabScrollPane())
		{
			StringBuffer buf = new StringBuffer();

			getGraph().setViewType(TrendModelType.SUMMARY_VIEW);
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

		//  *Note:  An update will only occur when event is null.
		//			Null events are passed from the mouse Listener (in displayGraph..)
		//			and only occur after a double click has happened on a tree node.
		if( event == null)
		{
			
			// Signal the trend to be rebuilt through a database hit.
			getGraph().setUpdateTrend(true);
			
			//Current cursor set to waiting during the update.
			savedCursor = this.getCursor();
			this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
	
			//Find the selected graph definition and display it
			Object item = getTreeViewPanel().getSelectedItem();
			if( item == null || !( item instanceof com.cannontech.database.data.lite.LiteBase) )
				return;
			
			// Item is an instance of LiteBase...(from previous statement)	
	//		com.cannontech.database.db.DBPersistent object = DBPersistentFuncs.retrieve((com.cannontech.database.data.lite.LiteBase)item);
			getGraph().setGraphDefinition( ((com.cannontech.database.data.lite.LiteBase)item).getLiteID());

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
