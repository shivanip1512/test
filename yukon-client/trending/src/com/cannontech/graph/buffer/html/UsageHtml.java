package com.cannontech.graph.buffer.html;

/**
 * Generates html to display usage data in a GraphModel
 * Creation date: (1/31/2001 1:35:39 PM)
 * @author: 
 */
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.graph.model.TrendSerie;

public class UsageHtml extends HTMLBuffer
{
/**
 * Writes html into the given buffer.
 * Creation date: (1/31/2001 1:38:58 PM)
 * @return java.lang.StringBuffer
 */
public StringBuffer getHtml(StringBuffer buf)
{
	if( model.getTrendSeries() == null)
		return buf;

	/* The usage will be determined by taking the first available
	   USAGE_SERIES value and subtracting it from the last.
	   In the case that there is only one reading available
	   we cannot determine a total and will output text to
	   indicate this.
	 */
	 
	double[] values;
	long[] times;
			
	java.util.ArrayList usageLabels = new java.util.ArrayList(4);
	java.util.ArrayList startValues = new java.util.ArrayList(4);
	java.util.ArrayList endValues = new java.util.ArrayList(4);
	java.util.ArrayList totalValues = new java.util.ArrayList(4);
	java.util.ArrayList endNotes = new java.util.ArrayList(4);
	
	java.util.GregorianCalendar endCompare = new java.util.GregorianCalendar();
	int usageCount = 0;

	try
	{
		
		for( int i = 0; i < model.getTrendSeries().length; i++ )
		{
			TrendSerie serie = model.getTrendSeries()[i];
			if( GDSTypesFuncs.isUsageType(serie.getTypeMask()))
			{
				// initialize start,end to min_value which indicates no values found			
				Double startValue = null;
				Double endValue = null;
	
				Long startTime = null;
				Long endTime = null;
			

				values = serie.getValuesArray();
				times = serie.getPeriodsArray();

				String label = serie.getLabel();
				usageLabels.add( label );
			
				if( values != null )
				{
					if( times.length > 0 && values.length > 0 )
					{
						boolean addNote = true;
				
						//Make sure the first xSeries timestamp if on the
						//FIRST day of the interval
						endCompare.setTime(model.getStartDate());
						endCompare.set( java.util.Calendar.DAY_OF_YEAR, endCompare.get(java.util.Calendar.DAY_OF_YEAR) + 1 );
						if( endCompare.getTime().getTime() < times[0] )
						{					
							addNote = true;
						}
						
						startValue = new Double(values[0]);
						startTime = new Long(times[0]);
						
						if( times.length > 1 && values.length > 1 )
						{
							//Make sure that last xSeries timestamp is
							//on the LAST day of the interval
							endCompare.setTime(model.getStopDate());
							endCompare.set( java.util.Calendar.DAY_OF_YEAR, endCompare.get(java.util.Calendar.DAY_OF_YEAR) - 1 );
							if( endCompare.getTime().getTime() > times[ times.length-1 ])
							{
								addNote = true;
	
							}
							
							endValue = new Double(values[ values.length-1 ]);
							endTime = new Long(times[ times.length-1] );
						}	
	
						if( addNote && startTime != null && endTime != null)
						{
							// Didn't find an start or end timestamp on the day we expected,
							// so note when we did
							endNotes.add("* Usage for " + label + 
								" calculated from readings on " +
								extendedDateTimeformat.format(new java.util.Date( startTime.longValue())) + 
								" and " +
								extendedDateTimeformat.format( new java.util.Date( endTime.longValue() ) ));
						}
					}				
				}

				// Format the valueFormat for the number of decimals for each point
				int decimals = model.getTrendSeries()[i].getDecimalPlaces();
				setFractionDigits( decimals );
				
				if( startValue != null )			
					startValues.add( valueFormat.format( startValue.doubleValue() ) );
				else
					startValues.add( "N/A" );
	
				if( endValue != null )
					endValues.add( valueFormat.format( endValue.doubleValue() ) );
				else
					endValues.add( "N/A" );
			
				if( startValue != null && endValue != null )
				{
					totalValues.add( valueFormat.format( endValue.doubleValue() - startValue.doubleValue() ) );
				}
				else
				{
					totalValues.add( "N/A" );
				}	
			}
		}
	
	
		usageCount = usageLabels.size();
		
		//table label description
		buf.append("<p bgcolor=\""+TITLE_HEADER_BGCOLOR+"\" align=\"center\" class=\"titleheader\"><b><font size=\"-1\" face=\"arial\"><span class=\"titleheader\">Current Usage Table</span></font></b>\n");
			

		buf.append("<table valign=\"middle\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n");
		buf.append("<tr>\n");
		if( usageCount == 0)
		{
			buf.append("<td align=\"center\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><font size=\"-1\" face=\"arial\"><span class=\"headercell\">");
			buf.append("&nbsp;No Usage Information to Report&nbsp;\n");
			buf.append("</span></font></td></tr>\r\n");
			buf.append("</table>\r\n");
			return buf;
		}

		buf.append("<td align=\"center\" width=\"50\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\"></span></font></b></td>\n");

		for( int i = 0; i < usageCount; i++ )	
		{
			buf.append("<td align=\"center\" width=\"80\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">");
			buf.append(usageLabels.get(i));
			buf.append("</span></font></b></td>\n");
		}
		
		buf.append("</tr><tr>\n");
		buf.append("<td align=\"left\" width=\"50\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">&nbsp;Start:</span></font></b></td>\n");
		for( int i = 0; i < usageCount; i++ )
		{
			buf.append("<td align =\"center\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\"><font size=\"-1\" face=\"arial\"><span class=\"tablecell\">");
			buf.append( startValues.get(i));
			buf.append("</span></font></td>\n");
		}
		
		buf.append("</tr><tr>\n");
		buf.append("<td align=\"left\" width=\"50\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">&nbsp;End:</span></font></b></td>\n");		
		for( int i = 0; i < usageCount; i++ )
		{
			buf.append("<td align =\"center\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\"><font size=\"-1\" face=\"arial\"><span class=\"tablecell\">");
			buf.append( endValues.get(i));
			buf.append("</span></font></td>\n");
		}
		
		buf.append("</tr><tr>\n");
		buf.append("<td align=\"left\" width=\"50\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">&nbsp;Total:</span></font></b></td>\n");
		for( int i = 0; i < usageCount; i++ )
		{
			buf.append("<td align =\"center\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\"><font size=\"-1\" face=\"arial\"><span class=\"tablecell\">");
			buf.append( totalValues.get(i));
			buf.append("</span></font></td>\n");
		}

		buf.append("</tr></table>\n");

		if( endNotes.size() > 0 )
		{
			for( int i = 0; i < endNotes.size(); i++ )
			{
				buf.append("<p bgcolor=\""+TITLE_HEADER_BGCOLOR+"\" align=\"center\" class=\"subtext\"><font size=\"-1\" face=\"arial\"><span class=\"subtext\">");				
				buf.append(endNotes.get(i));
				buf.append("</span></font>\n");
			}
		}
	
	return buf;
	}//end try block
	catch( Exception e)
	{
		buf.append("<BR>No!! Usage points defined\r\n");
		CTILogger.info(" Exception in UsageHtml.getHtml");
		e.printStackTrace();
		return buf;
	}	
}
}
