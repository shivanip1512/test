package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (5/16/2001 5:21:13 PM)
 * @author: 
 */

import com.klg.jclass.chart.JCChart;
import javax.swing.event.*;
import com.cannontech.message.dispatch.message.*;
import com.cannontech.graph.model.*;
import com.cannontech.graph.menu.*;
import com.cannontech.graph.buffer.html.*;
import com.cannontech.common.util.*;
import com.cannontech.util.*;
public class GraphClient extends javax.swing.JPanel implements com.cannontech.database.cache.DBChangeListener, GraphDataFormats, GraphDefines, GraphModelType, com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, ChangeListener, TreeSelectionListener {
private class PointDataUpdater extends Thread
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
						System.out.println("** ignoreAutoUpdate **\n");
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

							if (getGraph().getCurrentModels() != null)
							{
								frame.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
								// Set to currentDate - always want this date to be TODAY!
								GraphClient.this.getStartDatePopupField().getValueModel().setValue(com.cannontech.util.ServletUtil.getToday());
								GraphClient.this.setGraphDefinitionDates( null, null );
								updateCurrentPane();
								long timer = (System.currentTimeMillis());
								frame.setTitle("Yukon Trending - ( Updated " + extendedDateTimeformat.format(new java.util.Date(timer)) + " )");
								System.out.println("[" + extendedDateTimeformat.format(new java.util.Date(timer)) + "] - Yukon Trending - Auto Updating Point Data");
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
	public static final String TREE_PARENT_LABEL = "Trends";
	public static double scalePercent = 0;
	private static final int GRAPH_PANE = 0;
	private static final int TABULAR_PANE = 1;
	private static final int SUMMARY_PANE = 2;
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
	private boolean timeToUpdate = true;	//true, update button was pushed, initial is ture also
	private Object currentGraphPeak = null;	//flag to only update once something new has changed
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
	private HelpMenu helpMenu = null;
	//private static boolean removeMultiplier = false;
	private static boolean showPointLabels = true;
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	private PointDataUpdater pointDataUpdater;
	private javax.swing.JRadioButton ivjCurrentRadioButton = null;
	private javax.swing.JPanel ivjGraphSetupPanel = null;
	private javax.swing.JPanel ivjGraphTabPanel = null;
	private javax.swing.JRadioButton ivjHistoricalRadioButton = null;
	private javax.swing.JButton ivjRefreshButton = null;
	private javax.swing.JLabel ivjStartDateLabel = null;
	private com.klg.jclass.field.JCPopupField ivjStartDatePopupField = null;
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
		System.out.println(" don't change model");
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);		
	}

	else if ( event.getSource() == getViewMenu().getLoadDurationRadioButtonItem())
	{
		System.out.println(" load duration curve model");
		actionPerformed_GetRefreshButton(LOAD_DURATION_CURVE_MODEL);
		getFileMenu().getExportMenuitem().setEnabled(false);
	}
	else if( event.getSource() == getViewMenu().getLineGraphRadioButtonItem() )
	{
		System.out.println(" data view model");
		actionPerformed_GetRefreshButton(DATA_VIEW_MODEL);
		getFileMenu().getExportMenuitem().setEnabled(true);
	}
	else if( event.getSource() == getViewMenu().getBarGraphRadioButtonItem())
	{
		System.out.println(" bar graph model");
		actionPerformed_GetRefreshButton(BAR_GRAPH_MODEL);		
		getFileMenu().getExportMenuitem().setEnabled(true);
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
	try
	{
		// Do this big step all just to get the number of columns
		GraphModel [][] currentModels = getGraph().getCurrentModels();
		int numColumns = 2;	//extras for date and time
		for (int i = 0; currentModels != null && i < currentModels.length; i++)
			for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
		{
			numColumns = numColumns + currentModels[i][j].getNumSeries();
		}
		

		GraphModel dvm = getGraph().getCurrentModels()[0][0];

		//Set up the export data in the arrays.
		// ** the export array is now set up in update();
		//setExportArray(); //setup array used for exporting all "selected" data (not just one day)

		com.cannontech.graph.exportdata.ExportOptionDialog exportDisplay =
			new com.cannontech.graph.exportdata.ExportOptionDialog(getGraphParentFrame(), dvm.getName(),
				numColumns, dvm.getStartDate().toString(), (String[]) Graph.exportArray, getChart(), directory);

		exportDisplay.setModal(true);
		exportDisplay.setLocationRelativeTo(getGraphParentFrame());
		exportDisplay.show();
	}
	catch (NullPointerException npe)
	{
		System.out.println(" ++ NullPtrExcep ++ in actionPerformed::Graph  No data to export.");
		npe.printStackTrace(System.out);
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

		//set flag to update tabular and peaks only when newly updated
		currentGraphPeak = null;

		timeToUpdate = true; //flags that update button was selected
		updateCurrentPane();

	}
	catch (Exception e)
	{
		System.out.println("-> Null pointer Exception - GraphClient.ActionPerformed on UpdateButton");
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


	// Set the model type again, we do this when there is a change in the time
	//  period where load duration may now be a line instead of a bar graph.
	//getGraph().setModelType(getGraph().getModelType() );
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
	getStartDatePopupField().removeValueListener(this);
	
	if( getCurrentRadioButton().isSelected() )
	{
		Object date = getStartDatePopupField().getValueModel().getValue();
		if( date instanceof java.util.Date )
			histDate = (java.util.Date) date;
		else
		if( date instanceof java.util.Calendar )
			histDate= ((java.util.Calendar) date).getTime();
			
		histIndex = getTimePeriodComboBox().getSelectedIndex(); //save current period
		histWeek = currentWeek;
		
		getStartDateLabel().setEnabled(false);
		getStartDatePopupField().setEnabled(false);
		getTimePeriodComboBox().removeAllItems();

		for (int i = 0; i < ServletUtil.currentPeriods.length; i++)
			getTimePeriodComboBox().addItem(ServletUtil.currentPeriods[i]);

		getTimePeriodComboBox().setSelectedIndex(currIndex); //set to saved currentPeriod
		getStartDatePopupField().getValueModel().setValue(com.cannontech.util.ServletUtil.getToday()); //set to currentDate
		currentWeek = NO_WEEK;
	}
	else if ( getHistoricalRadioButton().isSelected() )
	{
		currIndex = getTimePeriodComboBox().getSelectedIndex(); //save current period
		currentWeek = histWeek;
		getStartDateLabel().setEnabled(true);
		getStartDatePopupField().setEnabled(true);
		getStartDatePopupField().setEditable(true);
		getTimePeriodComboBox().removeAllItems();

		// -- Fill combo box with historical time periods
		for (int i = 0; i < ServletUtil.historicalPeriods.length; i++)
			getTimePeriodComboBox().addItem(ServletUtil.historicalPeriods[i]);

		getTimePeriodComboBox().setSelectedIndex(histIndex); //set to saved histPeriod
		if( histDate != null)
			getStartDatePopupField().getValueModel().setValue( com.cannontech.util.ServletUtil.parseDateStringLiberally( (dateFormat.format( histDate)).toString() )); //set to saved histDate
		else
			System.out.println(" %%% hist date null!!! ");
	}

	// -- Put the action listener back on the timePeriodComboBox	
	getTimePeriodComboBox().addActionListener(this);
	getStartDatePopupField().addValueListener(this);
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

			pj.setPrintable((PrintableChart) getChart(), pf);
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
			menu.getItem(i).addActionListener(this);
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

	getChart().setVisible(false);
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
private int formatDateRangeSlider(GraphModel model, TabularHtml htmlData)
{
	int timePeriod = getGraph().getCurrentTimePeriod();
	
	java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
	int valueSelected = Integer.MIN_VALUE;	//some number not -1 or greater
	long DAY = 86400000;

	setSliderKeysAndValues(model.getStartDate(), model.getEndDate());
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
	D0CB838494G88G88GD3054DACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8BF494471178CECEE49F97ABB96E4245A4D1922CD3E2A546E712A38ECEF0C29262609C09C9C2EC7202ED6CD37C30C3E26CF0BE459E907A8BD8C07C02EC84167998AC8492980444EFA53142828B6DA244579AE9C752C8231D556E8889C740D5F5CF750EE6BF8872A4DE3D592E2E6E2A2E2E2E2E6A1EC1AA58181831E8F101A4AD4A104C3F7797C952C437A4ED1ABA3009F073F31272A44B9FD62C10EA
	3CCB30101B14B77DE2D21EC3FA31AE9B0A85D0F61CCF4ADB8B3FD7C9FF2A1B7FA1A2A3731986F52EB10B0AE273F93C1178E43172A71C7970DC86308EF015G7148D37A02F31D09EF0172B273B7422245A0C29E520CB93F4E021FEE52C9F97C1948E83E6DACB46BAFC299E98CF84667057A4CE1525DD9311F68BE1DA84DFCECDE66D264E5691FA055EC1B1FEF72130CCCD4C43A3439F38BBCAB9B3ABB8E3569BDA5ED324F277B8C252D3324A3A07BBBABAA0E634329290A4F102441FC146F6173592C2DA4D5C3194B
	72732515A62FB15D0952142F7CAE2AACD3CC9966DAE419DC5E1DBFE6A9767D8C53AD36AB0672C5G4E735DEA62B98BF04D4B907778B924F5406FEDGC71611ACD5DBC916663FBDA04DEADD1E35BC0AAC6316F19926AE8B49127F6109256B59BC1F7360DCC94B13720A817683DC8498849887300159D7017BDDF89EEDB51CFACF0F6EBB6A779F533C01133AD75160F7C585541838C625CD8FF8A5095B50774E6F0CA08F414465C36DA7B467E7A51A731446A449E3E4E4E5C4985B18DF3E14B94D6CE3767250587838
	DEB90363CA837CBAC7D2DEA9008B2093A05EC1633A6CAE3B59386A40CE82BED9F377357A6500F12CCF553CCA409CDF2AB35678A281EF776AAD3763FAC9AE336AE572C30E2C2C88FA191C7CCA66F8875747E4C7C8AF1C772E6AE83CE7BB42670335DD01F6F874BC5A61FC20D9E45AA847FF407055ABC2F8EE63CFB03B4532876AA62DA03BEDDEC6E328DF72B9E9FE690A08E39878FEE6D661CAAECF79CA7B5C7E7CAC7A0E96408F820C81C881188E108A10361266772B6EDD43345B864B5C72362D1EB570D4860C00
	DC678B9A322FCDE912832A5C2AA9C149246976206C2DC35A7959FAEA839E47951F92108DD577C16FF20F82069464B4D7BDAB10A62030DA0F5A23G039E3FC47D5E61D985CFCD8E9AE77CDE59D0C2943C7EAE8F6A720A5C84BDC287284BD52873DFB11FD5883F7783F0DD3D5E0C3884A8A72FA23DACB933921E9F00E1ABDAC5C5355A06E248012B447F75B368CB14AB608E2B953F6273420F20D91751149352CB3BAEE44EE3F8DCA3CE90BCA870AB1AD6F0BC74E155B2552FE77532E1845456BEC3F1AB06217ABAC2
	6DB7B521AC41A14BAFA819EB1C5958E49857CC0039E2FCDFEBDAE599DF3D2EF77779BDB8F3261EEA191FDD083F8BE83FBBFE9E773F72823253171A49CE2B175EA7657FF2F9C4BBE5F6DC406D53DBE0DFEB27D91FA19E3FB957FC0B6BFE38D0AEF87CE944F860DA66FA6F3C00FA9F8DBC13812695105E1FEBAA626B6D189CD4E805FD980099D33CA1AB6334CF1D0DEEE34F1C0DECE329E7A35B58CBE7E35B502BE7A359902F7B4BD95CDB74FE1F45A697B72FE4EB5867D31850220944630917BC288F55279AE7D5EF
	07E238550F95C94ADE4D62B336866700CE9688DB6AFF1BEC297863AFC9099F3D1099E9BF9B38AFA72BFEB537211655A1DB62B225B3DBD7B425879465547BFC8647671C4B8E61CD7F72FFD1E897B1FB6CD62E1AF49967B3A574A3DA1FA246B6C14660509FD0FBE02AF0411AB15D25EAEBCCB78A644378EFBCBCA7817036EF57E3DBEBB0456DA565A24AB383686682242DA6FB39637CFA4BBAB58D4529EB9AD73B188F5F85874E1F525B27009B46057C0198C35F0515363AA6F3BC3C7E5A8514C933780002B5B81EA5
	97A85642BD0263B6B05C9FE0527E18C9FB6EC907B57E2C8EAE0E980BA0B42F61F3262D316FD7CFDF42FEC783FE5C9A9A7B428BD69FF5B498545BD4369D18737072F654B70BD93B64AB72C0C9309FEC3A2262C4038CADF83C78E3664710FE429A0A97DFBE0E3ACE05323496E21F35446FBFAF065B49FFDD4C05E73B9A88C21C63D586C8E70FDC4431637EE322A9AE5BE355C5621CD7A35AB1C269DA2E036A35F6DDEC7D8865E8813CA1E45BFFE905C59790C342F65B81B3C572BCFFA91D5B2DD9FE61D286592DC4B1
	55743038A6FEEDA81E9937B6BC366171CB078B75B5E1AD0FDFB284BE4070D3ADF89E577CAF53AF16E7C1DD65BA5AE75FEC41F1A5C2F942BA9A570D4BCBAD63EA50D5CCAF4CB16C7C88571E9ED0BBD49F61F6B39CC4504C99713D6AD9B647D3204F19G9CB725166DDFD0F62CA73F5F63C2DCB9149B84EE00617CD09EA3F0EF9C376F8F5806FEB72C0F9C838E97B80FF5AEBB0FF44B6F0C3F237FE5C2FF1BDBF0DE86C4F98BABDFB5F92D12DE2B4EE4B6FCD56F8325977F9575362206FC58A498678C0005G79FF65
	F1GDFF78F76233FE807008D161B5B2F348183EC7B6BDAEA2B817DA800C400A9GF3GB84FE0BA4E37A720A8CD2A524F962A10673A0AFE4A237B0F69062177387D1AEAA085654CDFF3D97D2BEB8317E9G1E2381B80D7F0C15E63CC9B39D1EF3G38FD9E28215CF2A160CA8BE9DC3BDBB0E6967E24C46EB7B06FD53D47640EFA26A56ED36E2849B65BF79612CFF90075198865C9GB38AE9DF9B58C2B9E8CA5267241D239CD9117261A91EBFE726961A39791BA1FFD084B9A8677917DA6269027AD1EFA2EE477182
	1B7D2C0089AD01A72F3FB4249E7D47F649DAFF4F10FA1477DA36357E51A175991676FCECBFFD0746167653E4297E1B2FC74D752719E31AE7BEA725EF4B1CF33DBB1F6BFDC1EB31E4BDFBB054120EBE35244FD041A58CB1952E0BE7DC34AF953FC57AFF44037A3F88E583E074DBB42777168C1953509EA12C0B7719FCA65B9C57F85167772EE11F33213CGA0FFA3790743B53717531E3C4DF61F3865F65CBF5FD10B7297EF647EF911405F4D702596BCDF975F946B22926A92B7125F1EA5631ADAD21414D784309F
	2081408FB03A087C787DAD2552CD72C8D85875904CEA1694070FEF71715B4B759F4DA27B1B78701725311F78EEE6DE947BCBAC62F6B745FCB6AC5E1609CFBE3E6EB69C5FFC284B5F847EG4085F0914058C463FB39E5A79FDF0DD7B50E9A065C564922849A9C1AE8880DCE110F6DFFDC3146963FA95A1E3B7B8C4E4538CD76392BE07864CD76397B010766EEA2542D5BCC367A75B348FF904A891B43F90D3761A6C11973A93B0E7DCD5FEC5F738FB17C1C4D76BD7F0757E84FDFGF54DEF937FCF56CA420E93AC3F
	3FD3B3D2B9D8E85D4C9CD19E5CAE1E162367E829F2649C6DA5B9F20E36C00E1D23254951F33474D6EB0E466D7AC1AF7A6F49B09733G52G9C45C9F94545E4531FEE5D4CEDBA02BD57C052665459290EC17C0D3668E37DDB5B4D4F02625AB016EEE51BBB76CF783B59DAB1F4BFC3F25CBDED28A3887DBA8DCEEA627F358D7D342634B3B40F75DEF7D12CF724186C6E7DB3A1DBBBE2797D934F485B5DB3A34A030FF9AA5B2F92E03CA9459473AEEFE2F1B014E7895C39A6524DBC01BB0F5B3CD479CED21E679D5257
	016B086B04F21C40D5B35CF8A8CF93381FDE9B6E58282FDB575F5CE173409860D6E3E5EEC75F76C63765A95E1B5B72CF3CACD7849B74DC75AB9C77B3AF4BD75A5B030AD8B3CFF89D82D797EC5003AAA630C5DBF09ED657D54710137CFB3A8973C0179234FB5B54D34116509E40636727581924D73D2202E328575BF82EBC40F8ADE0760231FF67960A4537B465056859818D27BD54C43166E4A8F3D97DCD31E4CD3749BA9D5A4C81D8ED169704495AFB8DF9C3EE87FA374AF957B562ADEDA5B98BDAF0DC6C4C84FC
	E21F1FE78D3C2EE8B54B9982B00360ABBDF2EB2B6235668CD72E5B4FE4B04FAB073EDDDBA9E7E8D42DB4ADDBB94DE8F8A682ECE4FCA48965223FF05C7C431336621D61CB5DD86271495379941F945EFF2FD47C6B6FE63EADC50ECF523672F5D23FAD947B13DE715CF8B1BC7715D23F097D56B306F539E9D14F98624C7E461A4F623D5BB2F91FE71A103EB44AD8220F6F0BBD1210AB715ABB70340455A521283A24292651D367BCDA7F113B213E4EA3511D61B72E53F94C0CED94E7FFB187E3076C6DE0EBG7B37D3
	6C3EA37BCDA9E26CEE1DE6335F3BD54A899A36D33F65DD58EFBC149381A6ED271C6C3FAFD875B77F5F1F4FA45B346BEF46F6736EEC3B7D0C6697BEDC8F0EF71372F682B4GF4828C820C81C886188CB083A095C05A813686308D2092209E20854058C1FBF1190639D5135CE5C64E98E66A81369B8352CCC259069C1079998917E33F9F479D8F7D0C97FDFD2C6D0851D7C3C077AB8163AA473213C0536FBEF45D9E2BCE5D91725F33F6C40BD55F4EC5BD4F5B415778F201AFE378021DA1BC0F956FE3730D65CA281B
	34137616FB54D831F279FB5178A733F9F63D4779E4897CC406EF3160B97F8A064772G544DFE0F721C32DEEC4AF578EC4FBB51F488515189B0478FDB3B14B643A3F708B64F7B226FA9AF7AEE3E273C6A0B9C9F2579426323C5FA6478A8CB3746C79C172F63FA6AG9BEA96F4AB846E1C402DE1FDA26E2C6AB5BA853ED04FB571AE85990A73A59D656AE738E693375502BBC767F8BA6E0BB064CEBDA04E71F4BCE7EFD7B50D1777B29A55875B2450C78513D1754B96398F8BDCC86EAABF5A02081B39C91BF5557E82
	095FC3283226FE4C369AB7907ABAF8AC7458F5BABBBCD2C271D1CA9645CC5E92324D6D39F80E10G65940069G738172F7417A87F06D92F194236B04F2BCC0B240B400D4006CD2D87F25C4F7FF0EBF020DE3FF749B7B4767741258B19472CE09C103B26FB47B653C9F0B48BB4F72BBC78A45E331F8233D470A3F509FAF152CF1EB12CA3AF589DDFC032DC7834A89G93847EE1061F816585G6BF6937EC817BDBE44F6B70B7B0C98B493ACF51C4739B09E4309B353E354D9F5494FDADF9631497E5D7C2C35811EDE
	G0C3993F7333CBE2B4824995C9DAEFF0209B36324B5286BC625BD2084BB0F75019B71093ACD27B1CEF27685701239D1762ABA91707AB105982B3954A0F8B935CD56061018B47FD20E730DB76D8DCAC0553D75F22B08435E5D3AF2C81DD36FE9550F69835476738E1453EDC066DAAD9B8AEFEA567D7E3DD5563A865D5F672FD5954DAB956FE131E72E759CB255540F836AF87DDBCEEB3D3607570F9575DBCAA25527087A2FDE3156CFB16B670A7A15172D758B4D7A22B22ADF23C5EADFAE6A6F2E33563B4AF87DE0
	994D71A3AA4DF1C2D904B31AB26B9CAF75619C33D8982220216767F7EEC6BE006E5364GBC5CED81DD53100657BF309C6B5DFDBDF8EF8975FCCF43FA9E873F58E50F15B16F1B847C2717D12C1C6933524CAD63B40EF298EBB90DD7F2DB47DBD94EE9BA4B697CFD438DBA2B9A845C44F2FA372B6D86457E734AA9067BB423AD5E8BF19300E7AF511E4E21B8D25BCBF942DDB97804FD520C133B9C5678FB72559919AB2448F15E783DDC47B3768E0D3FF99C3A5C0D71C104B8D42C8FAE53FF6412CC597BA8563CD6EB
	FD9FEE5C72BFC7BD23BC324F3C575E9712217E752716F299569CFBAB328CC396AA17630CB35B74B7097BC8DF17CAC836D97BA83EFE39BD4A7DA9DB1D3C5D1CAB544E71BE356BE0FBF604F6E2517336F517292D6BFD126324C638D122BFD559AAD94E7F3B350E122EB6BC99ABE9C7AF009DDBFC826F6751BA9A63A45177F3AA75BD7FFD3209B73BB02E60B19569671DEEEA3BEDBF59193B07450AD09EGC858AF46DA93A58789ADAEB17F07FC44BFD9743BD7E76725D0DE8860B8A06E1A7223749BDA14225F775DC2
	5E833487EE3AF1FBF7893BEE33DD67AD3763B1F8064474FA40FE0E5D1D05F86D007D9C7B3342B78CC05DECB15E269CAB7FA395608BAAE26F79BC26CF8D94413393E863G128126G4C841887A09D043862A065CD5F6C0D15B70954561205F159D2F2A369267CA0174FF5B0DCCEAE1BEC201F3C887583G89G49G5304BCEF745E745D4B1A01986FDE1EE76FD0265A782719E5C7E538DC3CDDEBC92471941B747B2336FB6ECA24F675A6FDCB54F627AEE3BBA3C2BDC2FC54F60DDAA4FE63CC7A89D15B7D288E5BCD
	0D42EF966067596AF83B97066427D2956C09D531ED1037FB375BAE67B6CB3BCADB9F8DD5F48773E366133CD57635B303617DD57635F32EC76C2BD0B7270A560E5397EB6DD69F9A4A3F7990717FAA73DD5AA17B7B2C8FB17C60A17B7B2C87F5FA1FF5B4544DBFC47CAB5D485FF3B8322EBAED784143540E3F024FFF8FE605FEFF7E46481F61FFEBC4F970DC280745B089B05E5519048BB0DCB2602AB2A807FDB9907DDC6135404D4F955203114F95F20261678A4B02114F958A0261678A6BD93F812583F26AD3F20F
	68F7E390E3D47EDE49C959A7F730BBA9BE3E9F3098E9AA0CEF66E14A373765D1CEB867B079BE5508BEE63F9179DE6D0A9179DEED50087DFEED1C916D5E0D4B574B622952A3C9F955C7C866071DB40EE6015BD0AD4E8D846EEA8951CD9438A7CB08EE0A4025DDA1BA47D142DD6AA73AC201D3D8CC25C1B9CE6026DFA63A8401D3B56AEF0E40BD26093BA0017BED9D75D7F90CF007CE089CDD60DE6DC45CD8A827885C222EF07B4D6CA27BBD18C138235DF446B2C5347D0E10E52E40BD45F059CE982F13FC4063BD21
	F57FAB4B6F0FFAC65E878C0CA88F73AE4EC7FA5866A4BDBC42F075D0EE91FAF8001D17E9227CEDD60E976523BA490AB4A3230F7302C72CF30E3FCF9FEE966B264098268AFDD41CA39D4D9038736748D696B8A9261637607EE31E7531A3CC0EAF9CD89A4253912619B3BF190BFAB44019707B0DD77B5046781D57613C1B5D4F25DBC02A0676557C4E8B7996D45B6F3C62194F88AA786E2E626577DDDFF4523DC59150F399367407E458D3FD33FB37C8B2ECB0E5A83548407B7E54C0286FBF16446ABBA3CA5FEF1BF5
	15E1FDBFF8B5543751CFFD3B2AA347E131643E98263B414B988D983A2EF9D43F87264B64B97363904FE7AF479ACF0E0DE727EDBC03E163499F8C757D34962B6F3C1B68AABE2C6F4EEB213E4F1C085577D2DB5FE3ECFDA70769EA290A7AD5FC9884F84DFB55E2154ED6A682BDBFFF6C6A26331CE9001B8310DD833E97G4FDC4BEB1066975A3A9832F1FEFFE2FE5B10DBBD2A21A9FCFCDF3A0E6363FD740AB323411AF0DB60FD4C740577B17506A46AC7553033C7F13645CF3E7819548B3A7D5C8A4F0B930057789A
	BA877098B8C7ED78CD234F40ABFBF39EFE38A7A46769F331464A69874A11BE521DB02F3FF7EFC866EFB919EF89741963796E3E905DBBF885E966777775533941649A72812E83117950BDECF7C5282FCF9D0CCD7BE0E5C82603AA72E91735A046F6BCEE7FCCB7658D8B007F4EDA72274F178CB967616F439E53719B847EBA2CA97F135DE4DB5EDABA774856587BEBD01E88B03D164E4326DD323EB3AA6D78C944F7C6E7DDDA10B937165BCA767121E7F21C67E745D9CB65F1BA7F596CA71CF11C64F8AD00F224C0F9
	AA406C6334DFFE2D5F1A93ADGFC36EBB8395C5830DCEEF8FBE752ED367B5DAD3763B95AA053FF214B1EBB5E49705BDC765CF119C6B6D08EF5E3DDF4A7BA230AFDA38A65F5F590A382F8GBA81C655D12C7ECA65B6A97A3989B7155021899F5738A1B9699868AB39EEB87A5F6A376BE332255D0C88FD70F17B02B83E39F57677992FB27C42BA7B7B0CD2105EE74CBF111467BFC1F147237D9473A2BD477D281FE25EC5C24EFF40E3F97377BFD9FE97E8A39F373EB722BC781C7F26AAFAFEB6276A66B9696F2BA267
	24FF288A4FC997D4C54EC9DF2B8AFF9770A19F4D4F93A2A71964237911C5CEF208790A19B02F29E2EE9F6925B8B37F837293864BFBCB215CA0F0B7982E934A718217990C25F3ECBFDC7DB6C42065BCB64664F1ABB1F9243BCC733DA0FF78B7A11A1F7645148FF81CB0951E33F07C90FB73EFBFE676924DFC40AF29079883209A40DBCFFB70C0AF6659669EBE641C1B573730B84A2D616BE8FCFF6777C98F866D77C948B72E1E6E243EE55877FF2C9F8B75A975F467B446CF7EEDAA60526AE94F186D273D2B7CA46D
	99393D6C5C8B4AF1G63CE926DEB3D343FCCBCC95FE06C89583F41C09BAB11689B8AEF407E8D86728A557FDB407E8D4693C33E61189630FF03C16D39CC29C112E91E1073A5036E8B0ACF5118463A597791D0B6GC61FA23B7DDC18BF9D1E0FC93E4DF6CF5FF2BB6E272F33B999FF4A3E1FFD1261A71C326FE70EDE1A6F49D0D7742178362B574ABF8E70E3BF9C4E3E73E2B0165CA936BE2688FEAF9B76F6B3CD5AD4F826C5604D47154F5E67F7B4584FDE57B0FCD1037D6C758BEEBAFBAD053AB18D44BF0E3D5344
	FF7F3365771A5E115F07F60DA88F6E335784485FBFAE7C7D06G797B8B425FAFB14837AFECA0FFDFE550D9C371E9426576513ED0AAF0F16CEC4E83E5ADC467267EC61DA6DD2F74E03C1FA24A052CBCCD14473B07238FECFF7BBAFFE4D8BCEE777CA66DB4532FD19403A655141F679E6AE134B93E0F1475111F2EEECC4A3B889087B036116F23FC8F38778A0B8B64366E0E005E6773CAE67E7A901B0389C0BB99004B3149CDF224B6066BAC2D517A5E443E956C3BC33E7770AD8665E27B4BC90F5547BA5CDC1E7DEE
	12E93F01FB22D3417F67045E416DD72C6F96243D75F054F78B6A5DDC0E817750BC062F5B8BFD6427D220FE2E1BF22F32BDC35EB96039D72D26771B1997797F2F74517ED568A1DF7FF35627874A9AC01C07F24A06E31250479B0D18AF7A147EA26A3E4429EBFAE05B1E3DFBF71A301D6E715D34470C7310CE6EF0E379149EE06F891A67EC87223CA7A1744D7B0B73E4CBFCAF1C6921BCE32F9367F27F9948AFG86G92G4683CC86188110FA06720D2F9F0E15EF7084551EEF48FD3737DF693759AE79165BF17FDE
	F38567EEC913FD1FBB45700EA67BBE77EBE1BF05D0B72A09FC4DA937157FDC40AFEC9A4EBEF7977BF6A57FECB8ED1109AB07278B6022D976DBE863AC3F47C668034F5F91E7745C6138736639C303B3F26E50648C4F9DAEB8A3678E32B37CBE2B5D6C976D47FA1F25B9A35DE7F1FDFDF0C5923FBD165F3F6D9B797D75559165E15EB739437757AEB76D2F9D026EFE3626D3E04E271E25BD72958F6D1B3384EEAD5BF367C1F9C9B361E406DB8765D2011BFF9AF155D0EE9638830DEC4E209CAFF0F18CB78E4A93856E
	E70D34B7CFEBA6FF75FD8F7BBFBB50A130371B02663E7224074E5F6781AD473D453E15C903F276B9EADFE264D2FB0F5A569D24FD295620766502F60051366B81E5689E1640E82B012EC150E6B5DA7F1F8613FFA3797481C15750085F917548DDFAG793B7DF29B78D6CE2FB6128C71027E5EC69CEF50A77BBDBA97585CC7856D58F3F4467BDC634D4FF87F6899FA163933C692F57946F07692CEFB49985A4FE7EAC37DBCFFFAFD64FECAF7423F6D02EEEFA3FD3BE21E8D3372EED1FE2C11F8707F7B2F39750145E0
	1BAFCA99410034DC74DD47DEB61FF3252CA27D592F24AF161654C9195C2954C9CB50B354C9994CF154C96960D486702569A1FF41DE693A9D87FCE1CE79AB38704BFC615610F2C32F569A905C2BBDCA91FFD1364210D63A5915D011DC4470B037BEC3DA465E0705B9AE42F7E201EA2320C2C4D1FBC06FA9B2BA15A2272C018B457F05A6033D968ADC4DB04710F270E2EBD7275183CC334C97BF05546C124AFCE68C831BB102342C46AAAF4C543221D630683E357F6838272CEF3114DDABE5364B1A3FD30699E2CB27
	DE4AEBE5D3059BBF0C55D10FEDF7C15BDD2C6D41FAE9F1DB5010B60470ED086735381894A04981ADF6339ED03C3597241C76C0C7ABE3E0C80BAF54DEG362C04556B6B25DCD03F93EF9486E00257063AEEC0AFE1D69C64E673867C79599F9B7A1A28B8F4B26C4F0CB3375F30FE2BB46B4E5FC67D564BF1497C466B127DDB25CA8557FC8B6087AFD18CB55A7B1E947D7B96F70F2CE9062CF59F75F971DB17E3D78DA574BE45D1857B8F920DD9496BDC8A4A5DE5E9737FD0CB8788D0F53C7F649BGG5CD3GGD0CB
	818294G94G88G88GD3054DACD0F53C7F649BGG5CD3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9E9CGGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:48:47 PM)
 * @return com.klg.jclass.chart.JCChart
 */
public JCChart getChart()
{
	return getGraph().getChart();
}
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
			//System.out.println("[" + new java.util.Date() + "]  Directory found in config.properties is " + directory);
			int index = directory.lastIndexOf("/");
			directory = directory.substring(0, index+1);
			directory = directory + "export/";
		}
		catch( Exception e)
		{
			directory= "C:/yukon/client/export/";
			//System.out.println("[" + new java.util.Date() + "]  Directory was NOT found in config.properties, defaulted to " + directory);
			System.out.println("[" + new java.util.Date() + "]  Add row named 'yc_common_commands_dir' to config.properties with the home directory.");
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
			constraintsRefreshButton.gridx = 0; constraintsRefreshButton.gridy = 1;
			constraintsRefreshButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRefreshButton.insets = new java.awt.Insets(5, 20, 5, 5);
			getGraphSetupPanel().add(getRefreshButton(), constraintsRefreshButton);

			java.awt.GridBagConstraints constraintsCurrentRadioButton = new java.awt.GridBagConstraints();
			constraintsCurrentRadioButton.gridx = 1; constraintsCurrentRadioButton.gridy = 1;
			constraintsCurrentRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCurrentRadioButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsCurrentRadioButton.insets = new java.awt.Insets(5, 20, 5, 5);
			getGraphSetupPanel().add(getCurrentRadioButton(), constraintsCurrentRadioButton);

			java.awt.GridBagConstraints constraintsHistoricalRadioButton = new java.awt.GridBagConstraints();
			constraintsHistoricalRadioButton.gridx = 2; constraintsHistoricalRadioButton.gridy = 1;
			constraintsHistoricalRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsHistoricalRadioButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsHistoricalRadioButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getGraphSetupPanel().add(getHistoricalRadioButton(), constraintsHistoricalRadioButton);

			java.awt.GridBagConstraints constraintsTimePeriodLabel = new java.awt.GridBagConstraints();
			constraintsTimePeriodLabel.gridx = 3; constraintsTimePeriodLabel.gridy = 1;
			constraintsTimePeriodLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTimePeriodLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsTimePeriodLabel.insets = new java.awt.Insets(5, 20, 5, 5);
			getGraphSetupPanel().add(getTimePeriodLabel(), constraintsTimePeriodLabel);

			java.awt.GridBagConstraints constraintsTimePeriodComboBox = new java.awt.GridBagConstraints();
			constraintsTimePeriodComboBox.gridx = 4; constraintsTimePeriodComboBox.gridy = 1;
			constraintsTimePeriodComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTimePeriodComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTimePeriodComboBox.weightx = 1.0;
			constraintsTimePeriodComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getGraphSetupPanel().add(getTimePeriodComboBox(), constraintsTimePeriodComboBox);

			java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
			constraintsStartDateLabel.gridx = 5; constraintsStartDateLabel.gridy = 1;
			constraintsStartDateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartDateLabel.insets = new java.awt.Insets(5, 20, 5, 5);
			getGraphSetupPanel().add(getStartDateLabel(), constraintsStartDateLabel);

			java.awt.GridBagConstraints constraintsStartDatePopupField = new java.awt.GridBagConstraints();
			constraintsStartDatePopupField.gridx = 6; constraintsStartDatePopupField.gridy = 1;
			constraintsStartDatePopupField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDatePopupField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartDatePopupField.weightx = 1.0;
			constraintsStartDatePopupField.insets = new java.awt.Insets(5, 5, 5, 20);
			getGraphSetupPanel().add(getStartDatePopupField(), constraintsStartDatePopupField);
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
			com.jrefinery.chart.ChartPanel cPanel = new com.jrefinery.chart.ChartPanel(getFreeChart());
			cPanel.isVisible();
			ivjGraphTabPanel.add(cPanel);
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
private StringBuffer getHTMLBuffer( String seriesType)
{
	HTMLBuffer htmlData = null;
	StringBuffer buf = null;
	
	int sliderValueSelected = 0;
	try
	{
		if ( seriesType.equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES) )
		{
			htmlData = new TabularHtml();
			getTabularSlider().getModel().setValueIsAdjusting(true);
		}
		else if ( seriesType.equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES) )
		{
			htmlData = new PeakHtml();
		}
		else if (seriesType.equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES) )
		{
			htmlData = new UsageHtml();
		}
		else
		{
			htmlData = new TabularHtml();	//default on error(?) to graph_series
		}
	
		buf = new StringBuffer("<HTML><CENTER>");
		GraphModel [][] currentModels = getGraph().getCurrentModels();
			
		for (int i = 0; currentModels != null && i < currentModels.length; i++)
			for (int j = 0; currentModels[i] != null && j < currentModels[i].length; j++)
			{
				htmlData.setModel( currentModels[i][j] );

				if ( htmlData instanceof TabularHtml)
				{
					((TabularHtml) htmlData).setTabularStartDate(currentModels[i][j].getStartDate());
					((TabularHtml) htmlData).setTabularEndDate(currentModels[i][j].getEndDate());

					//if( showDateRangeSlider )
					//{
						sliderValueSelected = formatDateRangeSlider(currentModels[i][j], (TabularHtml)htmlData);
					//}
				}
				htmlData.getHtml( buf );
			}
	}
	catch(Throwable t )
	{
		t.printStackTrace();
	}
	return buf;
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
			menuBar.add(getHelpMenu());	
		}
		catch (java.lang.Throwable ivjExc) 
		{
			System.out.println(" Throwable Exception in getMenuBar()");
			ivjExc.printStackTrace();
		}
	}
	return menuBar;
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
 * Return the StartDatePopupField property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCPopupField getStartDatePopupField() {
	if (ivjStartDatePopupField == null) {
		try {
			ivjStartDatePopupField = new com.klg.jclass.field.JCPopupField();
			ivjStartDatePopupField.setName("StartDatePopupField");
			ivjStartDatePopupField.setToolTipText("Select a Date from the Calendar");
			ivjStartDatePopupField.setEnabled(false);
			// user code begin {1}
			com.klg.jclass.util.value.DateValueModel dateModel = new com.klg.jclass.util.value.DateValueModel();
			dateModel.setValue( com.cannontech.util.ServletUtil.getToday() );
			ivjStartDatePopupField.setValueModel(dateModel);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDatePopupField;
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
			ivjTrendingTabbedPane.getModel().addChangeListener(this);			
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
		System.out.println(" ## DBChangeMsg ##\n" + msg);

		// Refreshes the device trees in the createGraphPanel if that's
		// the panel that is open panel.
		if( createPanel != null)
		{
			((com.cannontech.database.model.DeviceTree_CustomPointsModel) createPanel.getTreeViewPanel().getModel()).update();
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
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
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

	pointDataUpdater = new PointDataUpdater();
	pointDataUpdater.start();

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

		java.awt.GridBagConstraints constraintsTreeViewPanel = new java.awt.GridBagConstraints();
		constraintsTreeViewPanel.gridx = 0; constraintsTreeViewPanel.gridy = 0;
		constraintsTreeViewPanel.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsTreeViewPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsTreeViewPanel.weighty = 1.0;
		add(getTreeViewPanel(), constraintsTreeViewPanel);

		java.awt.GridBagConstraints constraintsTopBottomSplitPane = new java.awt.GridBagConstraints();
		constraintsTopBottomSplitPane.gridx = 1; constraintsTopBottomSplitPane.gridy = 0;
		constraintsTopBottomSplitPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTopBottomSplitPane.weightx = 1.0;
		constraintsTopBottomSplitPane.weighty = 1.0;
		add(getTopBottomSplitPane(), constraintsTopBottomSplitPane);
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
				valueChanged((TreeSelectionEvent) null);
			}
				
		}	
	});

	javax.swing.text.html.HTMLEditorKit editorKit = new javax.swing.text.html.HTMLEditorKit();
	//javax.swing.text.html.HTMLDocument doc = (javax.swing.text.html.HTMLDocument) (editorKit.createDefaultDocument());
	javax.swing.text.html.StyleSheet styleSheet = editorKit.getStyleSheet();
	
	try
	{
		java.io.FileReader reader = new java.io.FileReader("d:/yukon/client/bin/CannonStyle.css");
		styleSheet.loadRules(reader, new java.net.URL("file:D:/yukon/client/bin/CannonStyle.css"));
		//styleSheet.addRule("Main {  font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #000000; background-color: #FFFFCC; font-weight: bold}");
		//styleSheet.addRule("LeftCell {  font-family: Arial, Helvetica, sans-serif; font-size: 12px; color: #FFFFFF; background-color: #666699}");
		//styleSheet.addRule("HeaderCell {  font-family: Arial, Helvetica, sans-serif; font-size: 9pt; font-weight: bold; background-color: blue; color: blue}");
		//styleSheet.addRule("TableCell {  font-family: Arial, Helvetica, sans-serif; font-size: 12pt; color: green; background-color: green; font-weight: normal}");
		//styleSheet.loadRules(reader, new java.net.URL("file:d:/yukon/client/bin/CannonStyle.css"));
	}
	catch(java.io.IOException e){System.out.println(e);}

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
		Object date = getStartDatePopupField().getValueModel().getValue();
		
		if( date instanceof java.util.Date )
			newStart = (java.util.Date) date;
		else
		if( date instanceof java.util.Calendar )
			newStart = ((java.util.Calendar) date).getTime();
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
	java.awt.Cursor savedCursor = null;

	boolean updated = false;

	try
	{
		// set the cursor to a waiting cursor
		savedCursor = this.getCursor();
		this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

		Object selectedItem = getTreeViewPanel().getSelectedItem();

		if( getTrendingTabbedPane().getSelectedIndex() == GRAPH_PANE )
		{
			if( selectedItem == null || selectedItem.toString() == TREE_PARENT_LABEL)
			{
				showPopupMessage("Please Select a Trend From the list", javax.swing.JOptionPane.WARNING_MESSAGE);
			}
			getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);
			getGraph().update();
			getFreeChart();
			//getGraph().updateChart();
			updated = true;
		}
			
		else if( getTrendingTabbedPane().getSelectedIndex()  == TABULAR_PANE )
		{
			if( selectedItem == null || selectedItem.toString() == TREE_PARENT_LABEL)
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
				buf.append (getHTMLBuffer( com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES ));

				buf.append("</CENTER></HTML>");
				getTabularEditorPane().setText( buf.toString() );
				updated = true;		
			}
		}
	
		else if( getTrendingTabbedPane().getSelectedIndex()  == SUMMARY_PANE )
		{
			if( selectedItem == null || selectedItem.toString() == TREE_PARENT_LABEL)
				getSummaryTabEditorPane().setText("<CENTER>Please Select a Trend from the list");
			else if( selectedItem == currentGraphPeak)
				return;	//no need to update when the item hasn't changed
			else
			{
				StringBuffer buf = new StringBuffer();
				currentGraphPeak = selectedItem;
	
				getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES);			
				getGraph().update();
				buf.append( getHTMLBuffer( com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES) );

				getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES);
				getGraph().update();
				buf.append( getHTMLBuffer(com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES) );
				
				buf.append("</CENTER></HTML>");
				getSummaryTabEditorPane().setText(buf.toString());
				getSummaryTabEditorPane().setCaretPosition(0);

				updated = true;
			}
		}
		if (updated)
			getGraph().setExportArray();
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
		pointDataUpdater.ignoreAutoUpdate = true;
	}	

	
	Object selectedItem = getTreeViewPanel().getSelectedItem();
	boolean updated = false;
	
	if( getTrendingTabbedPane().getSelectedIndex()  == GRAPH_PANE )
	{
		if( selectedItem != null)
		{
			getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);					
			getGraph().update();
			getFreeChart();
			//getGraph().updateChart();
			updated = true;
		}
	}
	
	else if( getTrendingTabbedPane().getSelectedIndex()  == TABULAR_PANE )
	{
		if( selectedItem != null)
		{
			getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES);							
			getGraph().update();
			StringBuffer buf = new StringBuffer();
			buf.append (getHTMLBuffer(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES));

			buf.append("</CENTER></HTML>");
			getTabularEditorPane().setText( buf.toString() );
			updated = true;			
		}
		else
			showPopupMessage("Please select a Trend from the List", javax.swing.JOptionPane.WARNING_MESSAGE);
	}

	else if( getTrendingTabbedPane().getSelectedIndex()  == SUMMARY_PANE )
	{
		if( selectedItem != null)
		{
			StringBuffer buf = new StringBuffer();
			
			if( getGraph().getHasPeakSeries() )
			{
				getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES);			
				getGraph().update();
				buf.append( getHTMLBuffer(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES) );
			}

			getGraph().setSeriesType(com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES);
			getGraph().update();
			buf.append( getHTMLBuffer(com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES) );
			
			buf.append("</CENTER></HTML>");
			getSummaryTabEditorPane().setText(buf.toString());
			getSummaryTabEditorPane().setCaretPosition(0);
			updated = true;
		}
		else
			showPopupMessage("Please select a Trend from the List", javax.swing.JOptionPane.WARNING_MESSAGE);
	}

	//if( updated)
		//getGraph().setExportArray();
	
	synchronized (com.cannontech.graph.GraphClient.class)
	{
		pointDataUpdater.ignoreAutoUpdate = false;
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
 * This listener for DatePopupComboBox.  Listens for value changes (date changed) and
 *  refreshes after a changed date is found.  SN
 * Insert the method's description here.
 * Creation date: (7/19/2001 12:45:54 PM)
 * @param event com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent event)
{
	if( event.getNewValue() != event.getOldValue())
	{
		actionPerformed_GetRefreshButton(DONT_CHANGE_MODEL);
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
 * This method needs to be implemented for the abstract class JCValueListener.
 *  JCValueListener is the DatePopupComboBox's listener.  This particular method is
 *  not needed for catching value changes.  ValueChanged(JCValueEvent) is used for that.
 * Insert the method's description here.  SN 
 * Creation date: (7/19/2001 12:45:10 PM)
 * @param event com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent event)
{
}
}
