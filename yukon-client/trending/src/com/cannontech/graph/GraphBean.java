package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (5/16/2001 5:21:13 PM)
 * @author: 
 */

import java.io.IOException;
import java.util.Date;

import com.cannontech.graph.buffer.html.HTMLBuffer;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.util.ServletUtil;

public class GraphBean implements GraphDataFormats, GraphDefines
{
	private static Graph graphClass = null;
	private final java.lang.String DB_ALIAS = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
//	private String directory = null;

//	public static double scalePercent = 0;
	int count = 0;
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

	private java.util.Date[] sliderValuesArray = null;

	private String period = com.cannontech.util.ServletUtil.historicalPeriods[0];
	private String tab = null;
	private int gdefid = -1;
	private com.cannontech.database.data.graph.GraphDefinition gdef = null;
	private String startStr = null;
	private Date start = null;
	private Date stop = null;
	private int viewType = com.cannontech.graph.model.TrendModelType.LINE_VIEW;
	private int options = 0x000;
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
public GraphBean() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
/*
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
}*/
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
/*
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
*/
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 *//*
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

}*/
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
/*
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
*/
/**
 * Insert the method's description here.
 * Creation date: (6/22/00 2:07:26 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed_GetRefreshButton( int refreshViewType )
{
	if( refreshViewType >= 0 )
	{
		getGraph().setViewType( refreshViewType);
	}
	
	try
	{
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
		com.cannontech.clientutils.CTILogger.info(new String("Yukon Trending - ( Updated " + extendedDateTimeformat.format(new java.util.Date( System.currentTimeMillis() ) )+ " )" ));
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
	getGraph().setCurrentTimePeriod( ServletUtil.getIntValue( getPeriod().toString() ) );
	if (getPeriod().toString().equalsIgnoreCase(com.cannontech.util.ServletUtil.ONEDAY.toString())
		|| getPeriod().toString().equalsIgnoreCase(com.cannontech.util.ServletUtil.THREEDAYS.toString())
		|| getPeriod().toString().equalsIgnoreCase(com.cannontech.util.ServletUtil.ONEWEEK.toString()))
		currentWeek = NO_WEEK;
	else
		currentWeek = FIRST_WEEK;
		
	getGraph().setUpdateTrend(true);
}
/**
 * Insert the method's description here.
 * Creation date: (10/27/00 12:57:26 PM)
 * @param gDef com.cannontech.database.data.graph.GraphDefinition
 *** NOT USING ADD FUNCTIONALITY ON WEB SIDE YET - 11/11/02 SN
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
//	connToDispatch.write(dbChange);
	
//	getTreeViewPanel().refresh();
}
/**
 * Update the pane.
 *  Calls the html code and the usage code
 * Creation date: (11/15/00 4:11:14 PM)
 */
private String buildHTMLBuffer( HTMLBuffer htmlBuffer)
{
	StringBuffer returnBuffer = null;

	int sliderValueSelected = 0;
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

				sliderValueSelected = formatDateRangeSlider(tModel, (TabularHtml)htmlBuffer);
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
 *** NOT USING ADD FUNCTIONALITY ON WEB SIDE YET - 11/11/02 SN
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
//	connToDispatch.write(dbChange);
	getFreeChart().getXYPlot().setDataset(null);
//	getChart().setVisible(false);
	return;
}/**
 * Insert the method's description here.
 * Creation date: (9/25/2001 11:12:24 AM)
 */
/*
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

}*/
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
			//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD
//			if( getCurrentRadioButton().isSelected() )	//today and backwards
			{
	//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD				
//				getTabularSlider().getModel().setValue(valuesCount - 1);
			}
			
//			else if (getHistoricalRadioButton().isSelected()) // some day and forward
			{
	//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD
//				getTabularSlider().getModel().setValue( 0 );
			}
				
			timeToUpdate = false;
		}

	//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD
		// Set up the Slider labels and size, this method also calls setSliderLabels
//		valueSelected = setLabelData (getTabularSlider().getMinimum(), getTabularSlider().getMaximum(), getTabularSlider().getValue());
//		getTabularSlider().setValue(valueSelected);
		
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
	//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD		
//		getSliderPanel().setVisible(false);
	}
	//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD	
//	else
//		getSliderPanel().setVisible(true);

	return valueSelected;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/2001 9:55:03 AM)
 * @return java.lang.String
 */
/*
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
	private com.jrefinery.chart.JFreeChart getFreeChart()
{
	return getGraph().getFreeChart();
}*/
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
public int getOption()
{
	return getGraph().getOptionsMaskHolder();
}
public void setOption(int newOption)
{
	getGraph().setOptionsMaskHolder(newOption);
}
public int getViewType()
{
	return getGraph().getViewType();
}
public void setViewType(int newViewType)
{
	getGraph().setViewType(newViewType);
}

public String getPeriod()
{
	if(period == null)
	{
		setPeriod(ServletUtil.historicalPeriods[0]);
	}
	return period;
}
public void setPeriod(String newPeriod)
{
	if(!period.equalsIgnoreCase(newPeriod))
	{
		period = newPeriod;
		getGraph().setUpdateTrend(true);
	}
}
public String getTab()
{
	return tab;
}
public void setTab(String newTab)
{
	tab = newTab;
}
public void setGdefid(int newGdefid)
{
	if( newGdefid != gdefid)
	{
		gdefid = newGdefid;
		retrieveGdef();	//get all definition data for defID
//		setGraphDefinitionDates ( null, null );		
	}
}
public int getGdefid()
{
	return gdefid;
}

public void retrieveGdef()
{
	com.cannontech.database.data.graph.GraphDefinition gDef = new com.cannontech.database.data.graph.GraphDefinition();
	gDef.getGraphDefinition().setGraphDefinitionID(new Integer(getGdefid()));
	
	if (gDef != null)
	{			
		java.sql.Connection conn = null;
		try
		{
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(DB_ALIAS);
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
		
		setGdef(gDef);
		// Signal the trend to be rebuilt through a database hit.
		getGraph().setUpdateTrend(true);
		getGraph().setCurrentGraphDefinition( gDef);
		// set the start and end date...null's let them be computed in the method too.
		setGraphDefinitionDates ( null, null );	
	}
}
public com.cannontech.database.data.graph.GraphDefinition getGdef()
{
	return gdef;
}
private void setGdef(com.cannontech.database.data.graph.GraphDefinition newGdef)
{
	gdef = newGdef;
}


public Date getStop()
{
	stop = com.cannontech.util.ServletUtil.getEndingDateOfInterval( getStart(), getPeriod());
	return stop;
}
public java.util.Date getStart()
{
	if( start == null)
	{
		start = ServletUtil.getToday();
	}
	return start;
}
private void setStart(java.util.Date newStart)
{
	if(start == null || start.compareTo((Object)newStart) != 0 )	//date changed
	{
		com.cannontech.clientutils.CTILogger.info("Changing Date!");
		start = newStart;
		getGraph().setUpdateTrend(true);
	}
}

public void setStartStr(String newStartStr)
{
	startStr = newStartStr;
	try
	{
		setStart( dateFormat.parse(startStr));
	}
	catch (java.text.ParseException e)
	{
		e.printStackTrace();
	}
}
/**
 * Return the DateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
/*
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
}*/
/**
 * Insert the method's description here.
 * Creation date: (10/3/2001 1:51:05 PM)
 * @return java.lang.String
 */
public static String getVersion()
{
	return (com.cannontech.common.version.VersionTools.getYUKON_VERSION() + VERSION);
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/2001 11:51:33 AM)
 */
private void initialize()
{
	histDate = com.cannontech.util.ServletUtil.getToday();
	currDate = com.cannontech.util.ServletUtil.getToday();
}

/**
 * Insert the method's description here.
 * Creation date: (6/9/00 1:19:04 PM)
 * @param args java.lang.String[]
 */
public static void main(String[] args)
{
	System.setProperty("cti.app.name", "Trending");
	GraphBean gb = new GraphBean();
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
		newStart = getStart();
	}

	if (newStop == null)
	{
		newStop = com.cannontech.util.ServletUtil.getEndingDateOfInterval( newStart, getPeriod().toString() );
	}

	newStart = com.cannontech.util.ServletUtil.getStartingDateOfInterval( newStart, getPeriod().toString() );

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

	//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD
//	if( getCurrentRadioButton().isSelected() )
	{
		setSliderLabels(minIndex, maxIndex);
		currentWeek = NO_WEEK;
	}
	//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD
//	else 
	if (getGraph().getCurrentTimePeriod() < ServletUtil.getIntValue(ServletUtil.FOURWEEKS))	// 2 - 7 days (index of Array, not a value)
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

	//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD
//	getTabularSlider().setLabelTable( sliderLabelTable );
	
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
	//UNCOMMENT ME FOR SLIDER!!! JUST COMMENTED OUT SO i COULD BUILD
//	getTabularSlider().setPreferredSize(new java.awt.Dimension( ((numTicks) * 60),48));
//	getTabularSlider().setMinimum( min );	
//	getTabularSlider().setMaximum( max );

}
/**
 * Calls the appropriate update function according to the tab selected.
 * Creation date: (4/10/2001 11:36:57 AM)
 */
public void updateCurrentPane()
{
	setGraphDefinitionDates(null, null);
	if( getTab().equalsIgnoreCase(GRAPH_PANE_STRING) )
	{
		getGraph().update();
	}
	
	else if( getTab().equalsIgnoreCase(TABULAR_PANE_STRING) )
	{
		getGraph().update();
		StringBuffer buf =  new StringBuffer();
		buf.append(buildHTMLBuffer(new TabularHtml()));

		buf.append("</CENTER></HTML>");
		getGraph().setHtmlString(buf);
	}

	else if( getTab().equalsIgnoreCase(SUMMARY_PANE_STRING ))
	{
		StringBuffer buf = new StringBuffer();

		getGraph().update();
		buf.append(buildHTMLBuffer(new PeakHtml()));
		buf.append(buildHTMLBuffer(new UsageHtml()));
		buf.append("</CENTER></HTML>");
		
		getGraph().setHtmlString(buf);
	}
}
public StringBuffer getHtmlString()
{
	return getGraph().getHtmlString();
}
/**
 * Insert the method's description here.
 * Creation date: (10/31/00 1:59:14 PM)
 * @param gDef com.cannontech.database.data.graph.GraphDefinition
 */
/*
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
}*/
public void saySomething(String firstPart, String secondPart)
{
	//Debug function!!!
	System.out.println(firstPart + "  " + secondPart + " " + count++);
}
}
