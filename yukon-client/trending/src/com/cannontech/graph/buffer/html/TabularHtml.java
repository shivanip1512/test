package com.cannontech.graph.buffer.html;

/**
 * Generates html to display the data in a GraphModel in a tabular format.
 * Creation date: (1/31/2001 1:35:09 PM)
 * @author: 
 */
import com.cannontech.graph.model.TrendSerie;
//import com.cannontech.graph.GraphDataFormats;
public class TabularHtml extends HTMLBuffer
{
	// In order for the tabular display to only show one date...
	//  we are setting a "tabular" start/end date criteria.
	public java.util.Date tabularStartDate = null; 
	public java.util.Date tabularEndDate = null;
/**
 * Writes html into the given buffer.
 * Creation date: (1/31/2001 1:38:46 PM)
 * @return java.lang.StringBuffer
 */
public StringBuffer getHtml(StringBuffer buf)
{
	if( model.getTrendSeries() == null)
		return buf;

//	com.cannontech.clientutils.CTILogger.info("Tabular HTML getHtml()");
//	long timer = System.currentTimeMillis();

	long tabStDt = model.getStartDate().getTime();
	long tabEndDt = model.getStopDate().getTime();
	
	java.util.Date headerDateDisplay = model.getStartDate();
		
	if( getTabularStartDate() != null && getTabularEndDate() != null)
	{
		tabStDt = getTabularStartDate().getTime();
		tabEndDt = getTabularEndDate().getTime();
		headerDateDisplay = getTabularStartDate();
	}
//	buf.append("<link rel=\"stylesheet\" href=\"c:/yukon/client/bin/CannonStyle.css\" type=\"text/css\">");
	buf.append("<CENTER><CENTER><TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\"><TR>\n");
	buf.append("<TD BGCOLOR=\"#ffffff\" class=\"Main\"><CENTER>&nbsp;<B><FONT FACE=\"Arial\">\n");
	buf.append( model.getChartName());
	buf.append("</FONT></B><BR><B><FONT FACE=\"Arial\">\n");

	//if( com.cannontech.common.util.TimeUtil.differenceInDays( model.getStartDate(), model.getEndDate() ) == 1 )
	//{
		buf.append( dateFormat.format( headerDateDisplay ));
	//}
	//else
	//{
		//buf.append( dateFormat.format( model.getStartDate()) );
 		//buf.append( " - " );
 		//buf.append( dateFormat.format( model.getEndDate() ) );
	//}
	buf.append("</FONT></B><TABLE BORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\"></CENTER>\n");
 	buf.append("<TR>\n");
	buf.append("<TD BGCOLOR=\"#999966\" class=\"HeaderCell\" WIDTH=\"90\">\n");
	buf.append("<P ALIGN=CENTER><B><FONT SIZE=\"-1\" FACE=\"Arial\">Time</FONT></B></TD>\n");

	int validSeriesCount = 0;
	java.util.Vector validDecimalPlaces = new java.util.Vector(model.getTrendSeries().length);
	for (int i = 0; i < model.getTrendSeries().length; i++)
	{
		TrendSerie serie = model.getTrendSeries()[i];
		if( com.cannontech.database.db.graph.GraphDataSeries.isGraphType(serie.getTypeMask()))
		{
			buf.append("<TD BGCOLOR=\"#999966\" class=\"HeaderCell\" WIDTH=\"130\">\n");
			buf.append("<P ALIGN=CENTER><B><FONT SIZE=\"-1\" FACE=\"Arial\">\n");
			buf.append( serie.getLabel());
			buf.append("</FONT></B></TD>\n");

			validDecimalPlaces.add(new Integer(serie.getDecimalPlaces()));
			validSeriesCount++;
		}
	}			
	buf.append("</TR><TR>\n");
	buf.append("<TD VALIGN=\"TOP\" BGCOLOR=\"#CCCC99\" class=\"TableCell\" WIDTH=\"90\">\n");
	buf.append("<P ALIGN=CENTER><FONT SIZE=\"-1\" FACE=\"Arial\">\n");


	Integer [] decimals = new Integer[validDecimalPlaces.size()];
	validDecimalPlaces.toArray(decimals);
 	// Gets ALL possible timestamps for all of the points.
 	// Set up a tree map of the model
	java.util.TreeMap tree = new java.util.TreeMap();
	
	int validIndex = 0;
	for( int i = 0; i < model.getTrendSeries().length; i++ )
	{
		TrendSerie serie = model.getTrendSeries()[i];
		if(com.cannontech.database.db.graph.GraphDataSeries.isGraphType( serie.getTypeMask()))
		{
	 		long[] timeStamp = serie.getPeriodsArray();
			double[] values = serie.getValuesArray();

	 		for( int j = 0; timeStamp != null && values != null &&  j < timeStamp.length; j++ )
 			{
				Long tsKey = new Long(timeStamp[j]);
 				Double[] objectValues = (Double[]) tree.get(tsKey);
		 		if( objectValues == null )
 				{
					if (tsKey.longValue() > tabStDt && tsKey.longValue() <= tabEndDt)
					{
						//objectValues is not in the key already
				 		objectValues = new Double[ validSeriesCount];
			 			tree.put(tsKey,objectValues);
	 				}
		 		}
				if( objectValues != null)	//MAY NEED THIS AGAIN
					objectValues[validIndex] = new Double(values[j]);
 			}
 			validIndex++;
		}
	}
	
	
	//time values
	java.util.Set keySet = tree.keySet();

	Long[] keyArray = new Long[keySet.size()];
	keySet.toArray(keyArray);
		
	//Output html for the timestamps
	for( int x = 0; x < keyArray.length; x++ )
	{
		Long ts1 = keyArray[x];
		buf.append(timeFormat.format(new java.util.Date(ts1.longValue())));
		buf.append("<BR>\n");
	}
	buf.append("</FONT></TD>\n");

	//Output html for each valid serie's readings.
	validIndex = 0;
	for (int i = 0; i < model.getTrendSeries().length; i++)
	{
		com.jrefinery.data.TimePeriod prevTimePeriod = null;
		if(com.cannontech.database.db.graph.GraphDataSeries.isGraphType(model.getTrendSeries()[i].getTypeMask()))
		{
			setFractionDigits(3);
			buf.append("<TD VALIGN=\"TOP\" BGCOLOR=\"#CCCC99\" class=\"TableCell\" WIDTH=\"130\">\n");
			buf.append("<P ALIGN=CENTER><FONT SIZE=\"-1\" FACE=\"Arial\">\n");
			
			for (int j = 0; j < keyArray.length; j++)
			{
				Double[] values = (Double[])tree.get(keyArray[j]);												
				Double val = values[validIndex];
				if( val != null )
					buf.append(valueFormat.format(val));
				buf.append("<BR>");
			}
			buf.append("</FONT></TD>\n");
				
			validIndex++;
		}
	}

	buf.append("</FONT></TD></TR></TABLE></CENTER></TD></TR></TABLE></CENTER>\n");

//	com.cannontech.clientutils.CTILogger.info(" @TABULAR HTML - Took " + (System.currentTimeMillis() - timer) +" millis to build html buffer.");
	return buf;
}
/**
 * Returns the end date for the data.
 * Creation date: (10/3/00 5:52:24 PM)
 * @return java.util.Date
 */
public java.util.Date getTabularEndDate()
{
	return tabularEndDate;
} 
/**
 * Returns the starting date for the data.
 * Creation date: (10/3/00 5:51:55 PM)
 * @return java.util.Date
 */
public java.util.Date getTabularStartDate()
{
	return tabularStartDate;
}
/**
 * Sets the ending date for the data.
 * Creation date: (10/3/00 5:51:29 PM)
 * @param end java.util.Date
 */
public void setTabularEndDate(java.util.Date end) 
{
	tabularEndDate = end;
}
/**
 * Sets the starting date for the data.
 * Creation date: (10/3/00 5:50:50 PM)
 * @param start java.util.Date
 */
public void setTabularStartDate(java.util.Date start) 
{
	tabularStartDate = start;
}
}
