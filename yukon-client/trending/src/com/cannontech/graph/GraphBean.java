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
import com.cannontech.graph.model.TrendModelType;
import com.cannontech.util.ServletUtil;

public class GraphBean implements GraphDefines
{
	private Graph graphClass = null;

	private String period = ServletUtil.historicalPeriods[0];
	private String tab = GRAPH_PANE_STRING;
	private int gdefid = -1;
	private Date start = null;
	private Date stop = null;
	private int viewType = TrendModelType.LINE_VIEW;
	private int options = 0x000;
	private String format = "png";
	
	private int page = 1;

	/**
	 * GraphClient constructor comment.
	 */
	public GraphBean() {
		super();
		initialize();
	}
	
	/**
	 * Returns an html String based on the current TrendModel selected.
	 * The HTMLBuffer instanceof determines which type of buffer is generated.
	 * @param htmlBuffer com.cannontech.graph.buffer.html.HTMLBuffer
	 * @return String
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
					((TabularHtml) htmlBuffer).setTabularStartDate(new Date(tModel.getStartDate().getTime() + (86400000 * (new Integer(page -1).longValue()) )) );
					((TabularHtml) htmlBuffer).setTabularEndDate(new Date(tModel.getStartDate().getTime() + (86400000 * (new Integer(page).longValue()) )) );
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
	 * Method getFreeChart.
	 * @return JFreeChart
	 */
	private com.jrefinery.chart.JFreeChart getFreeChart()
	{
		return getGraph().getFreeChart();
	}
	/**
	 * Method getGraph.
	 * @return Graph
	 */
	public Graph getGraph()
	{
		if( graphClass == null)
			graphClass = new Graph();
		return graphClass;
	}
	
	/**
	 * Method getFormat.
	 * @return String
	 */
	public String getFormat()
	{
		return format;
	}
	/**
	 * Method getOption.
	 * @return int
	 */
	public int getOption()
	{
		return getGraph().getOptionsMaskHolder();
	}
	/**
	 * Method setOption.
	 * @param newOption int
	 */
	public void setOption(int newOption)
	{
		getGraph().setOptionsMaskHolder(newOption);
	}
	/**
	 * Method getViewType.
	 * @return int
	 */
	public int getViewType()
	{
		return getGraph().getViewType();
	}
	/**
	 * Method setViewType.
	 * @param newViewType int
	 */
	public void setViewType(int newViewType)
	{
		getGraph().setViewType(newViewType);
	}
	
	/**
	 * Method getPeriod.
	 * @return String
	 */
	public String getPeriod()
	{
		if(period == null)
		{
			setPeriod(ServletUtil.historicalPeriods[0]);
		}
		return period;
	}
	/**
	 * Method setPeriod.
	 * @param newPeriod java.lang.String
	 */
	public void setPeriod(String newPeriod)
	{
		if(!period.equalsIgnoreCase(newPeriod))
		{
			period = newPeriod;
			getGraph().setUpdateTrend(true);
		}
	}
	/**
	 * Method getTab.
	 * @return String
	 */
	public String getTab()
	{
		return tab;
	}
	/**
	 * Method getWidth.
	 * @return int
	 */
	public int getWidth()
	{
		return getGraph().getWidth();
	}
	/**
	 * Method getHeight.
	 * @return int
	 */
	public int getHeight()
	{
		return getGraph().getHeight();
	}
		
	/**
	 * Method setTab.
	 * @param newTab java.lang.String
	 */
	public void setTab(String newTab)
	{
		tab = newTab;
	}
	/**
	 * Method setGdefid.
	 * @param newGdefid int
	 */
	public void setGdefid(int newGdefid)
	{
		if( newGdefid != gdefid)
		{
			gdefid = newGdefid;
			getGraph().setCurrentGraphDefinition( retrieveGdef(gdefid));
			setGraphDefinitionDates ( null, null );	
			getGraph().setUpdateTrend(true);		
		}
	}
	/**
	 * Method getGdefid.
	 * @return int
	 */
	public int getGdefid()
	{
		return gdefid;
	}
	
	/**
	 * Method retrieveGdef.
	 * @param gdefid int
	 * @return GraphDefinition
	 */
	private com.cannontech.database.data.graph.GraphDefinition retrieveGdef(int gdefid)
	{
		com.cannontech.database.data.graph.GraphDefinition gDef = new com.cannontech.database.data.graph.GraphDefinition();
		gDef.getGraphDefinition().setGraphDefinitionID(new Integer(gdefid));
		
		if (gDef != null)
		{			
			java.sql.Connection conn = null;
			try
			{
				conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
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
		}
		return gDef;
	}
	/**
	 * Method getStop.
	 * @return Date
	 */
	public Date getStop()
	{
		stop = com.cannontech.util.ServletUtil.getEndingDateOfInterval( getStart(), getPeriod());
		return stop;
	}
	/**
	 * Method getStart.
	 * @return Date
	 */
	public java.util.Date getStart()
	{
		if( start == null)
		{
			start = ServletUtil.getToday();
		}
		return start;
	}
	/**
	 * Method setStart.
	 * @param newStart String
	 */
	public void setStart(String newStart)
	{
		setStart(ServletUtil.parseDateStringLiberally(newStart));
	}
	
	/**
	 * Method setStart.
	 * @param newStart java.util.Date
	 */
	private void setStart(Date newStart)
	{
		if(start == null || start.compareTo((Object)newStart) != 0 )	//date changed
		{
			com.cannontech.clientutils.CTILogger.info("Changing Date!");
			start = newStart;
			getGraph().setUpdateTrend(true);
		}
	}
	
	/**
	 * Method setSize.
	 * @param width int
	 * @param height int
	 */
	public void setSize( int width, int height)
	{
		getGraph().setSize(width, height);
	}  
	/**
	 * Method initialize.
	 */
	private void initialize()
	{
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
	 * Method setGraphDefinitionDates.
	 * @param newStart java.util.Date
	 * @param newStop java.util.Date
	 */
	/**
	 * Set the current GraphDefinition's start Date and end Dates.
	 *  Check for null allows this function to compute the start and end dates.
	 *  If they are not null, then we just set the current graphDefinition dates to those
	 * 	 passed in through the function call.
	 * Creation date: (6/7/2001 12:27:23 PM)
	 * @param newStart java.util.Date
	 * @param newStop java.util.Date
	 */
	public void setGraphDefinitionDates(Date newStart, Date newStop)
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
	 * Method setFormat.
	 * @param newFormat java.lang.String
	 */
	public void setFormat(String newFormat)
	{
		format = newFormat;
	}
	
	/**
	 * Method getPage.
	 * @return int
	 */
	public int getPage()
	{
		return page;
	}
	/**
	 * Method setPage.
	 * @param newPage int
	 */
	public void setPage(int newPage)
	{
		page = newPage;
	}
	/**
	 * Method getNumDays.
	 * @return int
	 */
	public int getNumDays()
	{
		int numDays = com.cannontech.common.util.TimeUtil.differenceInDays(getStart(), getStop());
		return numDays;
	}
	/**
	 * Method updateCurrentPane.
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
	/**
	 * Method getHtmlString.
	 * @return StringBuffer
	 */
	public StringBuffer getHtmlString()
	{
		return getGraph().getHtmlString();
	}
	/**
	 * Method encode.
	 * @param out java.io.OutputStream
	 * @throws IOException
	 */
	public void encode(java.io.OutputStream out) throws IOException
	{
		if( getFormat().equalsIgnoreCase("gif") )								
			getGraph().encodeGif(out);
		else if( getFormat().equalsIgnoreCase("png") )
			getGraph().encodePng(out);
		else if( getFormat().equalsIgnoreCase("jpg") )
			;//getGraph().encodeJPG(out);
		else if( getFormat().equalsIgnoreCase("svg") )
			getGraph().encodeSVG(out);
	}
}
