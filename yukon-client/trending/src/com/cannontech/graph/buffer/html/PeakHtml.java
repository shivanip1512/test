package com.cannontech.graph.buffer.html;

/**
 * Generates html to display the peaks in a GraphModel
 * Creation date: (1/31/2001 1:35:28 PM)
 * @author: 
 */
import com.cannontech.graph.model.DataViewModel;
import com.cannontech.graph.model.LoadDurationCurveModel;
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
public StringBuffer getHtml(StringBuffer buf) {

	long peakPointIndex = -1;
	
	// Find the peak point
	try
	{
		for( int i = 0; i < model.getNumSeries(); i++ )
		{
			if( model.getSeriesTypes(i).equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES) )
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
		buf.append( model.getName());
		buf.append("</FONT></B>\n");
	
		buf.append("	<BR><B><FONT FACE=\"Arial\">");
		if( com.cannontech.common.util.TimeUtil.differenceInDays( model.getStartDate(), model.getEndDate() ) == 1 )
		{
			buf.append( dateFormat.format( model.getStartDate()) );
		}
		else
		{
			buf.append( dateFormat.format( model.getStartDate()) );
	 		buf.append( " - " );
 			buf.append( dateFormat.format( model.getEndDate() ) );
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


		buf.append("PEAK " +  model.getSeriesNames((int) peakPointIndex) );
		buf.append("</FONT></B></CENTER></TD>\r\n");	 
	
		//List all graph points
		for( int i = 0; i < model.getNumSeries(); i++ )
		{
			if( model.getPointIDs()[i] != model.getPointIDs()[(int)peakPointIndex] && 
				model.getSeriesTypes(i).equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES) )
			{
				buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#999966\" class=\"HeaderCell\"><CENTER><B><FONT SIZE=\"-1\" FACE=\"Arial\">");
				buf.append(model.getSeriesNames(i));
				buf.append("</FONT></B></CENTER></TD>\r\n");
			}
		}

		buf.append("  </TR>\r\n");
	
		// Set the number of decimal places that will display for each point.
		int decimals = model.getDecimalPlaces( (int) peakPointIndex);
		setFractionDigits( decimals );
		
		// Find the 6 peaks for the peak point
		double[] peakData = model.getYSeries( (int) peakPointIndex);
		double[] peakTimeStamps = null;
		if ( model instanceof LoadDurationCurveModel )
			peakTimeStamps = ((LoadDurationCurveModel)model).getXHours((int) peakPointIndex);
		else if ( model instanceof DataViewModel )
			peakTimeStamps = model.getXSeries( (int) peakPointIndex);

		// Using a sorted tree map to find the 6 peak values and timestamps
		java.util.TreeMap peakMap = new java.util.TreeMap();
	
		for( int i = 0; i < peakData.length; i++ )
		{
			if( peakMap.size() < 6 || peakData[i] > ((Double) peakMap.firstKey()).doubleValue() )
			{
				if( peakMap.size() == 6 )
					peakMap.remove( peakMap.firstKey() );
			
				peakMap.put( new Double( peakData[i]), new Double( peakTimeStamps[i] ));
			}
		}	

		Object[] values = peakMap.keySet().toArray();
	
		for( int i = values.length-1; i >= 0; i-- )
		{
			buf.append("  <TR>\r\n");
			buf.append("    <TD ALIGN=CENTER WIDTH=\"120\" BGCOLOR=\"#CCCC99\" class=\"TableCell\">&nbsp;<FONT SIZE=\"-1\" FACE=\"Arial\">");
			buf.append( dateTimeformat.format(new java.util.Date( ((Double) peakMap.get( values[i])).longValue() * 1000 )) );
			buf.append("</FONT></TD>\r\n");
	
			buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#CCCC99\" class=\"TableCell\">&nbsp;<FONT SIZE=\"-1\" FACE=\"Arial\">");
			buf.append( valueFormat.format( ((Double) values[i])));
			buf.append("</FONT></TD>\r\n");
			
			for( int j = 0; j < model.getNumSeries(); j++ )
			{
					
				if( model.getPointIDs()[j] != model.getPointIDs()[(int)peakPointIndex] && 
					model.getSeriesTypes(j).equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
				{
					// Set the number of decimal places that are displayed for each point (series).
					decimals = model.getDecimalPlaces( j );
					setFractionDigits( decimals );

					double[] vals = model.getYSeries(j);
					double[] times = model.getXSeries(j);
					
					if ( model instanceof LoadDurationCurveModel )
						times = ((LoadDurationCurveModel)model).getXHours(j);
					else if ( model instanceof DataViewModel )
						times = model.getXSeries( j );
	
					Double v = (Double) peakMap.get( values[i] );
	
					int index = -1;
				
					if( v != null  && times != null)
						index = java.util.Arrays.binarySearch( times, v.doubleValue());

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
		for( int i = 0; i < model.getNumSeries(); i++ )
		{
			if( model.getPointIDs()[i] != model.getPointIDs()[(int)peakPointIndex] && 
				model.getSeriesTypes(i).equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES) )
			{
		buf.append("    <TD ALIGN=CENTER WIDTH=\"70\" BGCOLOR=\"#CCCC99\" class=\"TableCell\"><CENTER><FONT SIZE=\"-1\" FACE=\"Arial\">N/A</FONT></CENTER></TD>\r\n");
			}
		}
		buf.append("</TR></CENTER></TABLE>\r\n");		
		return buf;
	}

}
}
