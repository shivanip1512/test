package com.cannontech.graph.buffer.html;

/**
 * Generates html to display the peaks in a GraphModel
 * Creation date: (1/31/2001 1:35:28 PM)
 * @author: 
 */

//import com.cannontech.graph.GraphDataFormats;

public class PeakHtml extends HTMLBuffer
{
	
/**
 * Insert the method's description here.
 * Creation date: (11/5/2001 11:25:32 AM)
 */
public PeakHtml()
{
	super();
}
/**
 * Writes html into the given buffer.
 * Creation date: (1/31/2001 1:38:34 PM)
 * @return java.lang.StringBuffer
 */
public StringBuffer getHtml(StringBuffer buf)
{
	if( model.getTrendSeries() == null)
		return buf;

//	com.cannontech.clientutils.CTILogger.info("Usage HTML getHtml()");
//	long timer = System.currentTimeMillis();
	long peakPointIndex = -1;
	// Find the peak point
	try
	{
		for( int i = 0; i < model.getTrendSeries().length; i++ )
		{
			if( model.getTrendSeries()[i].getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES) )
			{
				peakPointIndex = i;
				break;
			}	
		}

		//if( peakPointIndex == -1 )
		//{
			//buf.append("No peak point defined\r\n");
			//return buf;
		//}
	
		buf.append("<CENTER><TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\">\n");
		buf.append("<TR><TD BGCOLOR=\"#ffffff\" class=\"Main\"><CENTER>&nbsp;<B><FONT FACE=\"Arial\">");
		buf.append( model.getChartName());
		buf.append("</FONT></B>\n");
	
		buf.append("	<BR><B><FONT FACE=\"Arial\">");
		if( com.cannontech.common.util.TimeUtil.differenceInDays( model.getStartDate(), model.getStopDate() ) == 1 )
		{
			buf.append( dateFormat.format( model.getStartDate()) );
		}
		else
		{
			buf.append( dateFormat.format( model.getStartDate()) );
	 		buf.append( " - " );
 			buf.append( dateFormat.format( model.getStopDate() ) );
		}
 		buf.append("</FONT></B>\n");
	 	buf.append("	<BR><BR><B><FONT SIZE=\"-1\" FACE=\"ARIAL\">Current Peaks Table</FONT></B>\n");
		buf.append("</CENTER></TD></TR></TABLE></CENTER>\n");
	
		buf.append("<CENTER><TABLE BORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\">\r\n");
		buf.append("  <TR>\r\n");

		// Only continue if a peak point exists, otherwise, draw an imcomplete data table.
		if( peakPointIndex == -1 )
		{
			buf.append("	<TD ALIGN=CENTER BGCOLOR=\"#999966\" class=\"HeaderCell\"><FONT SIZE=\"-1\" FACE=\"Arial\">");
			buf.append("No Peak Point Defined\r\n");
			buf.append("</FONT></CENTER></TD></TR>\r\n");
			buf.append("</CENTER></TABLE>\r\n");
			return buf;
		}

		buf.append("    <TD ALIGN=CENTER WIDTH=\"120\" BGCOLOR=\"#999966\" class=\"HeaderCell\"><CENTER><B><FONT SIZE=\"-1\" FACE=\"Arial\">Time</FONT></B></CENTER></TD>\r\n");
		buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#999966\" class=\"HeaderCell\"><CENTER><B><FONT SIZE=\"-1\" FACE=\"Arial\">");


		buf.append("PEAK " +  model.getTrendSeries()[(int) peakPointIndex].getLabel());
		buf.append("</FONT></B></CENTER></TD>\r\n");	 
	
		//List all graph points
		for( int i = 0; i < model.getTrendSeries().length; i++ )
		{
			if( model.getTrendSeries()[i].getPointId().intValue() != model.getTrendSeries()[(int)peakPointIndex].getPointId().intValue() && 
				model.getTrendSeries()[i].getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES) )
			{
				buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#999966\" class=\"HeaderCell\"><CENTER><B><FONT SIZE=\"-1\" FACE=\"Arial\">");
				buf.append(model.getTrendSeries()[i].getLabel());
				buf.append("</FONT></B></CENTER></TD>\r\n");
			}
		}

		buf.append("  </TR>\r\n");
	
		// Set the number of decimal places that will display for each point.
		int decimals = model.getTrendSeries()[(int) peakPointIndex].getDecimalPlaces();
		setFractionDigits( decimals );
		
		// Find the 6 peaks for the peak point
		double[] peakData = model.getTrendSeries()[(int) peakPointIndex].getValuesArray();
		long[] peakTimeStamps = model.getTrendSeries()[(int) peakPointIndex].getPeriodsArray();


		// Using a sorted tree map to find the 6 peak values and timestamps
		java.util.TreeMap peakMap = new java.util.TreeMap();
	
		for( int i = 0; i < peakData.length; i++ )
		{
			if( peakMap.size() < 6 || peakData[i] > ((Long) peakMap.firstKey()).doubleValue() )
			{
				if( peakMap.size() == 6 )
					peakMap.remove( peakMap.firstKey() );
			
				peakMap.put( new Long( peakTimeStamps[i] ), new Double( peakData[i]));
			}
		}	
		java.util.Set keySet = peakMap.keySet();
		Long[] keyArray = new Long[keySet.size()];
		keySet.toArray(keyArray);
	
		for( int i = keyArray.length-1; i >= 0; i-- )
		{
			buf.append("  <TR>\r\n");
			buf.append("    <TD ALIGN=CENTER WIDTH=\"120\" BGCOLOR=\"#CCCC99\" class=\"TableCell\">&nbsp;<FONT SIZE=\"-1\" FACE=\"Arial\">");
			buf.append( dateTimeformat.format(new java.util.Date(((Long)keyArray[i]).longValue())));
			buf.append("</FONT></TD>\r\n");
	
			buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#CCCC99\" class=\"TableCell\">&nbsp;<FONT SIZE=\"-1\" FACE=\"Arial\">");
			buf.append( valueFormat.format(( (Double)peakMap.get(keyArray[i])).doubleValue()));
			buf.append("</FONT></TD>\r\n");
			
			for( int j = 0; j < model.getTrendSeries().length; j++ )
			{
					
				if( model.getTrendSeries()[j].getPointId().intValue() != model.getTrendSeries()[(int)peakPointIndex].getPointId().intValue() && 
					model.getTrendSeries()[j].getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
				{
					// Set the number of decimal places that are displayed for each point (series).
					decimals = model.getTrendSeries()[j].getDecimalPlaces();
//					setFractionDigits( decimals );
					setFractionDigits( 3);

					double[] vals = model.getTrendSeries()[j].getValuesArray();
					long[] times = model.getTrendSeries()[j].getPeriodsArray();
					
					Long ts = (Long) keyArray[i];
					int index = -1;
				
					if( ts != null  && times != null)
						index = java.util.Arrays.binarySearch( times, ts.longValue());

					if( index >= 0 )
					{	
						buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#CCCC99\" class=\"TableCell\">&nbsp;<FONT SIZE=\"-1\" FACE=\"Arial\">");			
						buf.append( valueFormat.format( vals[index] ));
						buf.append("</FONT></TD>\r\n");
					}
					else
					{
						buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#CCCC99\" class=\"TableCell\">&nbsp;<FONT SIZE=\"-1\" FACE=\"Arial\">");			
						buf.append("</FONT></TD>\r\n");
					}					
				}
			}
	
			buf.append("  </TR>\r\n");
		}

		buf.append("</CENTER></TABLE>\r\n");
		return buf;
	}
	catch( Exception e)
	{
		buf.append("    <TR><TD ALIGN=CENTER WIDTH=\"120\" BGCOLOR=\"#CCCC99\" class=\"TableCell\"><CENTER><FONT SIZE=\"-1\" FACE=\"Arial\">");
		buf.append("No Data Obtained</FONT></TD>\r\n");
		buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#CCCC99\" class=\"TableCell\"><CENTER><FONT SIZE=\"-1\" FACE=\"Arial\">N/A</FONT></CENTER></TD>\r\n");
		for( int i = 0; i < model.getTrendSeries().length; i++ )
		{
			if( model.getTrendSeries()[i].getPointId().intValue() != model.getTrendSeries()[(int)peakPointIndex].getPointId().intValue() && 
				model.getTrendSeries()[i].getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES) )
			{
		buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#CCCC99\" class=\"TableCell\"><CENTER><FONT SIZE=\"-1\" FACE=\"Arial\">N/A</FONT></CENTER></TD>\r\n");
			}
		}
		buf.append("</TR></CENTER></TABLE>\r\n");		
		return buf;
	}
//	finally
//	{
//		com.cannontech.clientutils.CTILogger.info(" @PEAK HTML - Took " + (System.currentTimeMillis() - timer) +" millis to build html buffer.");
//	}

}
}
