package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (5/16/2001 5:21:13 PM)
 * @author: 
 */

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.clientutils.ActivityLogger;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.activity.ActivityLogActions;
import com.cannontech.database.data.activity.ActivityLogSummary;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.buffer.html.HTMLBuffer;
import com.cannontech.graph.buffer.html.PeakHtml;
import com.cannontech.graph.buffer.html.TabularHtml;
import com.cannontech.graph.buffer.html.UsageHtml;
import com.cannontech.graph.model.TrendModel;
import com.cannontech.roles.cicustomer.CommercialMeteringRole;
import com.cannontech.util.ServletUtil;
import com.cannontech.util.SessionAttribute;

public class GraphBean extends Graph
{
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
	
			TrendModel tModel = getTrendModel();
			{
				htmlBuffer.setModel( tModel);
	
	
				if ( htmlBuffer instanceof TabularHtml)
				{
					GregorianCalendar tempCal = new GregorianCalendar();
					tempCal.setTime((Date)tModel.getStartDate().clone());
					tempCal.add(Calendar.DATE, (page - 1));
					((TabularHtml) htmlBuffer).setTabularStartDate(tempCal.getTime());

					tempCal.add(Calendar.DATE, 1);	//incr date by one
					((TabularHtml) htmlBuffer).setTabularEndDate(tempCal.getTime());
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
	 * Method getFormat.
	 * @return String
	 */
	public String getFormat()
	{
		return format;
	}
	
	/**
	 * Method setOption.
	 * @param newOption int
	 */
	public void setOption(int newOption)
	{
		getTrendProperties().setOptionsMaskSettings(newOption);
	}
	/**
	 * Method setPeriod.
	 * @param newPeriod java.lang.String
	 */
	public void setPeriod(String newPeriod)
	{
		//Actually compares the periods in super and this so we 
		// know if the page must be set in this.
		if( !newPeriod.equalsIgnoreCase(getPeriod()))
		{
			super.setPeriod(newPeriod);
			setPage(1);
		}
	}
		
	/**
	 * Method setGdefid.
	 * @param newGdefid int
	 */
	public void setGdefid(int newGdefid)
	{
		if( newGdefid != getGdefid())
			setGraphDefinition(newGdefid);			
	}
	/**
	 * Method getGdefid.
	 * @return int
	 */
	public int getGdefid()
	{
		if( getGraphDefinition() != null)
			return getGraphDefinition().getGraphDefinition().getGraphDefinitionID().intValue();
		else
			return -1;
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
	 * Method initialize.
	 */
	private void initialize()
	{
		//Don't attempt to load any properties from a file.
		setTrendProperties(new com.cannontech.graph.model.TrendProperties(false));
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
			setUpdateTrend(true);
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
			update();
			
			StringBuffer buf =  new StringBuffer();
			buf.append(buildHTMLBuffer(new TabularHtml()));
	
			buf.append("</CENTER></HTML>");
			setHtmlString(buf);
		}
		else if( getViewType() == GraphRenderers.SUMMARY)
		{			
			StringBuffer buf = new StringBuffer();
	
			update();
			buf.append(buildHTMLBuffer(new PeakHtml()));
			buf.append(buildHTMLBuffer(new UsageHtml()));
			buf.append("</CENTER></HTML>");
			
			setHtmlString(buf);
		}
		else
		{
			update();
		}
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
			encodeGif(out);
		else if( format.equalsIgnoreCase("png") )
			encodePng(out);
		else if( format.equalsIgnoreCase("jpg") )
			encodeJpeg(out);
		else if( format.equalsIgnoreCase("svg") )
			encodeSVG(out);
//		else if( format.equalsIgnoreCase("csv") )
//			encodeCSV(out);
		else	//default to png
			encodePng(out);
	}
	

	/**
	 * Performs the error checking for getDataNow functionality based on properties.
	 * @param liteYukonUser LiteYukonUser
	 * @param custDevices String[] of ids
	 * @return SessionAttribute (success/error message)
	 */
	public SessionAttribute getDataNow(LiteYukonUser liteYukonUser, String[] custDevices)
	{
		LiteEnergyCompany liteEC = EnergyCompanyFuncs.getEnergyCompany(liteYukonUser);
		int ecId = -1;
		if (liteEC != null)
			ecId = liteEC.getEnergyCompanyID();
			
		ActivityLogSummary actLogSummary = new ActivityLogSummary(liteYukonUser.getUserID(), ecId);
		actLogSummary.retrieve();
		
		for (int i= 0; i < actLogSummary.getLogSummaryVector().size(); i++)
		{
			ActivityLogSummary.LogSummary logSummary = (ActivityLogSummary.LogSummary)actLogSummary.getLogSummaryVector().get(i); 
			if( logSummary.action.equals(ActivityLogActions.SCAN_DATA_NOW_ACTION))
			{
				if( logSummary.count >= Integer.valueOf(AuthFuncs.getRolePropertyValue(liteYukonUser, CommercialMeteringRole.MAXIMUM_DAILY_SCANS, "2")).intValue())
				{
					return new SessionAttribute( ServletUtil.ATT_ERROR_MESSAGE, "Maximum Scans allowed for today exceeded" );
				}
							
				Date now = new Date();
				long sinceLast = now.getTime() - logSummary.maxTimeStamp.getTime();
				long duration = Long.valueOf(AuthFuncs.getRolePropertyValue(liteYukonUser, CommercialMeteringRole.MINIMUM_SCAN_FREQUENCY, "15")).longValue() * 36000;
				if (sinceLast <= duration)
				{
					long waitTime = duration - sinceLast;
					return new SessionAttribute( ServletUtil.ATT_ERROR_MESSAGE, "Last Scan Time: "+ logSummary.maxTimeStamp +". Please wait " + waitTime/36000 + " minutes.");
				}
			}
		}

		java.util.List paObjects = new Vector(3);
		String logDesc = "Forced alternate scan of deviceID(s): ";
		for (int i = 0; i < custDevices.length; i++)
		{
			LiteYukonPAObject litePAO = PAOFuncs.getLiteYukonPAO(Integer.valueOf(custDevices[i]).intValue());
			paObjects.add(litePAO);
			logDesc += litePAO.getYukonID() + ", ";
		}
		ActivityLogger.logEvent(liteYukonUser.getUserID(), ActivityLogActions.SCAN_DATA_NOW_ACTION, logDesc);
		getDataNow(paObjects);
		return new SessionAttribute( ServletUtil.ATT_CONFIRM_MESSAGE, "Alternate Scans of Selected Meters Started." );
	}				
}
