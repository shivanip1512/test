package com.cannontech.graph.buffer.html;

/**
 * Generates html to display usage data in a GraphModel
 * Creation date: (1/31/2001 1:35:39 PM)
 * @author: 
 */

//import com.cannontech.graph.GraphDataFormats;
public class UsageHtml extends HTMLBuffer
{
/**
 * Writes html into the given buffer.
 * Creation date: (1/31/2001 1:38:58 PM)
 * @return java.lang.StringBuffer
 */
public StringBuffer getHtml(StringBuffer buf)
{
	System.out.println("Usage HTML getHtml()");
	/* The usage will be determined by taking the first available
	   USAGE_SERIES value and subtracting it from the last.
	   In the case that there is only one reading available
	   we cannot determine a total and will output text to
	   indicate this.
	 */
	 
	double[] xSeries;
	double[] ySeries;
			
	java.util.ArrayList usageLabels = new java.util.ArrayList(4);
	java.util.ArrayList startValues = new java.util.ArrayList(4);
	java.util.ArrayList endValues = new java.util.ArrayList(4);
	java.util.ArrayList totalValues = new java.util.ArrayList(4);
	java.util.ArrayList endNotes = new java.util.ArrayList(4);
	
	java.util.GregorianCalendar endCompare = new java.util.GregorianCalendar();
	int usageCount = 0;

	try
	{
		
		for( int i = 0; i < model.getNumSeries(); i++ )
		{
			if( model.getSeriesTypes(i).equalsIgnoreCase( com.cannontech.database.db.graph.GraphDataSeries.USAGE_SERIES) )
			{
				// initialize start,end to min_value which indicates no values found			
				Double start = null;
				Double end = null;
	
				Double startTime = null;
				Double endTime = null;
			
				xSeries = model.getXSeries(i);
				ySeries = model.getYSeries(i);

				String label = (model.getSeriesNames(i));
				usageLabels.add( label );
			
				if( xSeries != null )
				{
					if( xSeries.length > 0 && ySeries.length > 0 )
					{
						boolean addNote = false;
				
						//Make sure the first xSeries timestamp if on the
						//FIRST day of the interval
						endCompare.setTime(model.getStartDate());
						endCompare.set( java.util.Calendar.DAY_OF_YEAR, endCompare.get(java.util.Calendar.DAY_OF_YEAR) + 1 );
						if( endCompare.getTime().getTime() < xSeries[0]*1000 )					
						{					
							addNote = true;
						}
						
						start = new Double(ySeries[0]);
						startTime = new Double(xSeries[0]);
						
						if( xSeries.length > 1 && ySeries.length > 1 )
						{
							//Make sure that last xSeries timestamp is
							//on the LAST day of the interval
							endCompare.setTime(model.getEndDate());
							endCompare.set( java.util.Calendar.DAY_OF_YEAR, endCompare.get(java.util.Calendar.DAY_OF_YEAR) - 1 );
							if( endCompare.getTime().getTime() > xSeries[ xSeries.length-1 ]*1000 )													
							{
								addNote = true;
	
							}
							
							end = new Double(ySeries[ ySeries.length-1 ]);
							endTime = new Double(xSeries[ xSeries.length-1] );
						}	
	
						if( addNote && startTime != null && endTime != null)
						{
							// Didn't find an start or end timestamp on the day we expected,
							// so note when we did
							endNotes.add("* Usage for " + label + 
								" calculated from readings on " +
								extendedDateTimeformat.format(new java.util.Date( startTime.longValue()*1000 )) + 
								" and " +
								extendedDateTimeformat.format( new java.util.Date( endTime.longValue()*1000 ) ));
						}
					}				
				}

				// Format the valueFormat for the number of decimals for each point
				int decimals = model.getDecimalPlaces(i);
				setFractionDigits( decimals );
				
				if( start != null )			
					startValues.add( valueFormat.format( start.doubleValue() ) );
				else
					startValues.add( "N/A" );
	
				if( end != null )
					endValues.add( valueFormat.format( end.doubleValue() ) );
				else
					endValues.add( "N/A" );
			
				if( start != null && end != null )
				{
					totalValues.add( valueFormat.format( end.doubleValue() - start.doubleValue() ) );
				}
				else
				{
					totalValues.add( "N/A" );
				}	
			}
		}
	
	
		usageCount = usageLabels.size();
		if( usageCount == 0)
		{
			return buf;
		}
		
		//table label description
		buf.append("<CENTER><TABLE BORDER=\"0\" CELLSPACING=\"0\" CELLPADDING=\"0\">\n");
		buf.append("  <TR>\n");
		buf.append("	<TD BGCOLOR=\"#ffffff\" class=\"Main\"><P><CENTER>&nbsp;<B><FONT SIZE=\"-1\" FACE=\"Arial\">");
		buf.append("Current Usage Table</FONT></B></CENTER></TD></TR></TABLE></CENTER>\n");	

		//buf.append("\r\n<!-- Begin usage html generation -->\r\n");
		buf.append("<CENTER><TABLE BORDER=\"1\" CELLSPACING=\"0\" CELLPADDING=\"0\">\r\n");
		buf.append("  <TR>\n");

//		if( usageCount == 0 )
//		{	// put this exit thing in the middle here for a reason, this way we get the formatting for the
			//	  table that we need before we return out of this method.
//			buf.append("    <TD ALIGN=CENTER BGCOLOR=\"#999966\"><FONT SIZE=\"-1\" FACE=\"Arial\">");
//			buf.append("No Usage points defined\r\n");
//			buf.append("</FONT></CENTER></TD>\r\n");
//			buf.append("  </TR>\n</CENTER></TABLE>\n");
//			return buf;
//		}

		buf.append("    <TD ALIGN=LEFT WIDTH=\"50\" BGCOLOR=\"#999966\" class=\"HeaderCell\"><B><FONT SIZE=\"-1\" FACE=\"Arial\"></FONT></B></TD>\r\n");
		
		for( int i = 0; i < usageCount; i++ )	
		{		
			buf.append("    <TD ALIGN=CENTER BGCOLOR=\"#999966\" class=\"HeaderCell\"><B><FONT SIZE=\"-1\" FACE=\"Arial\">");
			buf.append(usageLabels.get(i));
			buf.append("</FONT></B></CENTER></TD>\r\n");		
		}
		
		buf.append("  </TR>\r\n  <TR>\r\n");
		buf.append("    <TD ALIGN=LEFT WIDTH=\"50\" BGCOLOR=\"#999966\" class=\"HeaderCell\"><B><FONT SIZE=\"-1\" FACE=\"Arial\">");
		buf.append("Start:");	
		buf.append("</FONT></B></CENTER></TD>\r\n");
			
		for( int i = 0; i < usageCount; i++ )
		{
			buf.append("    <TD ALIGN=CENTER BGCOLOR=\"#CCCC99\" class=\"TableCell\"><FONT SIZE=\"-1\" FACE=\"Arial\">");
			buf.append( startValues.get(i));
			buf.append("</FONT></CENTER></TD>\r\n");
		}
		
		buf.append("  </TR>\r\n  <TR>\r\n");
		buf.append("    <TD ALIGN=LEFT WIDTH=\"50\" BGCOLOR=\"#999966\" class=\"HeaderCell\"><B><FONT SIZE=\"-1\" FACE=\"Arial\">");
		buf.append("End:");	
		buf.append("</FONT></B></CENTER></TD>\r\n");
		
		for( int i = 0; i < usageCount; i++ )
		{
			buf.append("    <TD ALIGN=CENTER BGCOLOR=\"#CCCC99\" class=\"TableCell\"><FONT SIZE=\"-1\" FACE=\"Arial\">");
			buf.append( endValues.get(i));	
			buf.append("</FONT></CENTER></TD>\r\n");		
		}
		
		buf.append("  </TR>\r\n  <TR>\r\n");
	
		buf.append("    <TD ALIGN=LEFT WIDTH=\"50\" BGCOLOR=\"#999966\" class=\"HeaderCell\"><B><FONT SIZE=\"-1\" FACE=\"Arial\">");
		buf.append("Total:");	
		buf.append("</FONT></B></CENTER></TD>\r\n");
		for( int i = 0; i < usageCount; i++ )
		{
			buf.append("    <TD ALIGN=CENTER BGCOLOR=\"#CCCC99\" class=\"TableCell\"><FONT SIZE=\"-1\" FACE=\"Arial\">");
			buf.append( totalValues.get(i));	
			buf.append("</FONT></CENTER></TD>\r\n");		
		}

		buf.append("  </TR>\n</CENTER></TABLE>\n");

		if( endNotes.size() > 0 )
		{
			for( int i = 0; i < endNotes.size(); i++ )
			{
				buf.append("<BR><FONT ALIGN=LEFT SIZE=\"-1\" FACE=\"Arial\"> ");
				buf.append(endNotes.get(i));
				buf.append(" </FONT>\n");
			}		
		}
	
	//buf.append("<!-- End usage html generation -->\r\n");
	return buf;
	}//end try block
	catch( Exception e)
	{
		buf.append("<BR>No!! Usage points defined\r\n");
		System.out.println(" Exception in UsageHtml.getHtml");
		e.printStackTrace();
		return buf;
	}	
	finally
	{
		System.out.println("END USAGE HTML getHtml()");
	}
}
}
