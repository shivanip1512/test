package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (5/16/2001 5:21:13 PM)
 * @author: 
 */

import java.io.IOException;
import java.util.Date;

import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.buffer.html.HTMLBuffer;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.util.ServletUtil;
import org.jfree.chart.JFreeChart;

public class GraphBean implements GraphDefines
{
	private Graph graphClass = null;
	private String start = "";
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
	private JFreeChart getFreeChart()
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
		return getGraph().getOptionsMaskSettings();
	}
	/**
	 * Method setOption.
	 * @param newOption int
	 */
	public void setOption(int newOption)
	{
		getGraph().getTrendProperties().setOptionsMaskSettings(newOption);
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
		return getGraph().getPeriod();
	}
	/**
	 * Method setPeriod.
	 * @param newPeriod java.lang.String
	 */
	public void setPeriod(String newPeriod)
	{
		if( !newPeriod.equalsIgnoreCase(getGraph().getPeriod()))
		{
			getGraph().setPeriod(newPeriod);
			setPage(1);
		}
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
	 * Method setGdefid.
	 * @param newGdefid int
	 */
	public void setGdefid(int newGdefid)
	{
		if( newGdefid != getGdefid())
			getGraph().setGraphDefinition(newGdefid);			
	}
	/**
	 * Method getGdefid.
	 * @return int
	 */
	public int getGdefid()
	{
		if( getGraph().getGraphDefinition() != null)
			return getGraph().getGraphDefinition().getGraphDefinition().getGraphDefinitionID().intValue();
		else
			return -1;
	}
	
	/**
	 * Method getStop.
	 * @return Date
	 */
	private Date getStopDate()
	{
		return getGraph().getStopDate();
	}
	/**
	 * Method getStart.
	 * @return Date
	 */
	public java.util.Date getStartDate()
	{
		return getGraph().getStartDate();
	}
	/**
	 * Method setStart.
	 * @param newStart String
	 */
	public void setStart(String newStart)
	{
		start = newStart;
		setStartDate(ServletUtil.parseDateStringLiberally(start));
	}
	
	/**
	 * Method setStart.
	 * @param newStart java.util.Date
	 */
	private void setStartDate(Date newStartDate)
	{
		getGraph().setStartDate(newStartDate);
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
		//Don't attempt to load any preperties from a file.
		getGraph().setTrendProperties(new com.cannontech.graph.model.TrendProperties(false));
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
	 * Method setFormat.
	 * @param newFormat java.lang.String
	 */
	public void setFormat(String newFormat)
	{
		if( !newFormat.equalsIgnoreCase(format))
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
		if( page != newPage)
		{
			getGraph().setUpdateTrend(true);
			page = newPage;
		}
	}
	/**
	 * Method getNumDays.
	 * @return int
	 */
	public int getNumDays()
	{
		int numDays = com.cannontech.common.util.TimeUtil.differenceInDays(getStartDate(), getStopDate());
		return numDays;
	}
	/**
	 * Method updateCurrentPane.
	 */
	public void updateCurrentPane()
	{
		if( getViewType() == GraphRenderers.TABULAR)
		{
			getGraph().update();
			
			StringBuffer buf =  new StringBuffer();
			buf.append(buildHTMLBuffer(new TabularHtml()));
	
			buf.append("</CENTER></HTML>");
			getGraph().setHtmlString(buf);
		}
		else if( getViewType() == GraphRenderers.SUMMARY)
		{			
			StringBuffer buf = new StringBuffer();
	
			getGraph().update();
			buf.append(buildHTMLBuffer(new PeakHtml()));
			buf.append(buildHTMLBuffer(new UsageHtml()));
			buf.append("</CENTER></HTML>");
			
			getGraph().setHtmlString(buf);
		}
		else
		{
			getGraph().update();
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
	 * This method will encode the graph in the outputStream.
	 * @param out java.io.OutputStream
	 * @throws IOException
	 */
	public void encode(java.io.OutputStream out) throws IOException
	{
		encode (out, getFormat());
	}
	
	public void encode(java.io.OutputStream out, String format) throws IOException
	{
		if( format.equalsIgnoreCase("gif") )								
			getGraph().encodeGif(out);
		else if( format.equalsIgnoreCase("png") )
			getGraph().encodePng(out);
		else if( format.equalsIgnoreCase("jpg") )
			getGraph().encodeJpeg(out);
		else if( format.equalsIgnoreCase("svg") )
			getGraph().encodeSVG(out);
//		else if( format.equalsIgnoreCase("csv") )
//			getGraph().encodeCSV(out);
		else	//default to png
			getGraph().encodePng(out);
	}

	public void getDataNow()
	{
		com.cannontech.clientutils.CTILogger.info("GET DATA NOW - for all meters!!!");
		getGraph().getDataNow(null);
	}
}
