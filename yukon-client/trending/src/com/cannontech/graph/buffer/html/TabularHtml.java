package com.cannontech.graph.buffer.html;

/**
 * Generates html to display the data in a GraphModel in a tabular format.
 * Creation date: (1/31/2001 1:35:09 PM)
 * @author: 
 */
import com.cannontech.graph.model.*;
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
System.out.println(" 55555 ");	
	long timer = System.currentTimeMillis();

	double tabStDt = new Long(model.getStartDate().getTime() / 1000).doubleValue();
	double tabEndDt = new Long(model.getEndDate().getTime() / 1000).doubleValue();
System.out.println(" AAAAAA ");
	
	java.util.Date headerDateDisplay = model.getStartDate();
		
	if( getTabularStartDate() != null && getTabularEndDate() != null)
	{
		tabStDt = (new Long(getTabularStartDate().getTime() / 1000)).doubleValue();
		tabEndDt = (new Long(getTabularEndDate().getTime() / 1000)).doubleValue();
		//System.out.println("START = "+ tabStDt + "   END: " + tabEndDt);
		headerDateDisplay = getTabularStartDate();
	}
System.out.println(" BBBBBB ");
//	buf.append("<link rel=\"stylesheet\" href=\"d:/yukon/client/bin/CannonStyle.css\" type=\"text/css\">");
	buf.append("<CENTER><CENTER><TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\"><TR>\n");
	buf.append("<TD BGCOLOR=\"#ffffff\" class=\"Main\"><CENTER>&nbsp;<B><FONT FACE=\"Arial\">\n");
	buf.append( model.getName());
	buf.append("</FONT></B><BR><B><FONT FACE=\"Arial\">\n");
System.out.println(" CCCCCC ");	
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
System.out.println(" DDDDDDDDDD ");
	buf.append("</FONT></B><TABLE BORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\"></CENTER>\n");
 	buf.append("<TR>\n");
	buf.append("<TD BGCOLOR=\"#999966\" class=\"HeaderCell\" WIDTH=\"90\">\n");
	buf.append("<P ALIGN=CENTER><B><FONT SIZE=\"-1\" FACE=\"Arial\">Time</FONT></B></TD>\n");

	for( int z = 0; z < model.getNumSeries(); z++ )
	{
		buf.append("<TD BGCOLOR=\"#999966\" class=\"HeaderCell\" WIDTH=\"130\">\n");
		buf.append("<P ALIGN=CENTER><B><FONT SIZE=\"-1\" FACE=\"Arial\">\n");
		//buf.append( model.getSeriesDevices(z)+"<BR>" + model.getSeriesNames(z));
		buf.append( model.getSeriesNames(z));
		buf.append("</FONT></B></TD>\n");
	}
			
	buf.append("</TR><TR>\n");
	buf.append("<TD VALIGN=\"TOP\" BGCOLOR=\"#CCCC99\" class=\"TableCell\" WIDTH=\"90\">\n");
	buf.append("<P ALIGN=CENTER><FONT SIZE=\"-1\" FACE=\"Arial\">\n");
System.out.println(" EEEEEEE ");
 	// Gets ALL possible timestamps for all of the points.
 	// Set up a tree map of the model
	java.util.TreeMap tree = new java.util.TreeMap();
	for( int k = 0; k < model.getNumSeries(); k++ )
	{
		double[] values = model.getYSeries(k);
 		double[] timeStamp = null;
		if ( model instanceof DataViewModel )
			timeStamp = model.getXSeries(k);
		else if (model instanceof LoadDurationCurveModel)
			timeStamp = ((LoadDurationCurveModel)model).getXHours(k);
			
 		for( int l = 0; timeStamp != null && values != null &&  l < timeStamp.length; l++ )
 		{
	 		Double d = new Double(timeStamp[l]);
	 		Double[] objectValues = (Double[]) tree.get(d);
	 		if( objectValues == null )
	 		{
				if (d.doubleValue() > tabStDt && d.doubleValue() <= tabEndDt)
				{
					//System.out.println("match");
					//objectValues is not in the key already
			 		objectValues = new Double[ model.getPointIDs().length];
		 			tree.put(d,objectValues);
		 		}
	 		}
			if( objectValues != null)
				objectValues[k] = new Double(values[l]);	 		
		}
	}
System.out.println(" FFFFFFFFF");
	//time values
	java.util.Set keySet = tree.keySet();
	Double[] keyArray = new Double[keySet.size()];
	String date = dateFormat.format( model.getStartDate());

	int csvRowCount = keySet.size();
	int csvColCount = model.getPointIDs().length + 2;	// +2 cols -> 1 for date, 1 for time

	keySet.toArray(keyArray);
		
	//Output html for the timestamps
	for( int x = 0; x < keyArray.length; x++ )
	{
		Double ts1 = keyArray[x];
		buf.append(timeFormat.format(new java.util.Date(ts1.longValue() *1000)));
		buf.append("<BR>\n");
	}
//	System.out.println(" $$ Collecting timestamps; constructing html for timestamps $$" );
	buf.append("</FONT></TD>\n");

	//Go through all the points one by one and output their values as html
	for( int z = 0; z < model.getPointIDs().length; z++ )
	{
		// Set the number decimal places for each point.
		int decimals = model.getDecimalPlaces( z );
		setFractionDigits( decimals );
		
		buf.append("<TD VALIGN=\"TOP\" BGCOLOR=\"#CCCC99\" class=\"TableCell\" WIDTH=\"130\">\n");
		buf.append("<P ALIGN=CENTER><FONT SIZE=\"-1\" FACE=\"Arial\">\n");
		for( int x = 0; x < keyArray.length;x++ )
		{
			Double[] values = (Double[])tree.get(keyArray[x]);
			Double val = values[z];
			
			if( val != null )
			{
				//// Divide the multiplier out of the value if the user chose to removeMultiplier
				//if ( GraphClient.isRemoveMultiplier() )
					//val = new Double (val.doubleValue() / model.getMultiplier( z ));
				buf.append(valueFormat.format(val));
			}
							
			buf.append("<BR>");
		}
		buf.append("</FONT></TD>\n");
	}			
	
	buf.append("</FONT></TD></TR></TABLE></CENTER></TD></TR></TABLE></CENTER>\n");

//	System.out.println(" @TABULAR HTML - Took " + (System.currentTimeMillis() - timer) +" millis to build html buffer.");
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
