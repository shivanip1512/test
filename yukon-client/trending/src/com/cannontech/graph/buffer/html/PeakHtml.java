package com.cannontech.graph.buffer.html;

import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.graph.model.TrendSerie;

/**
 * Generates html to display the peaks in a GraphModel
 * Creation date: (1/31/2001 1:35:28 PM)
 * @author: 
 */

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
	
		int primaryPointIndex = -1;
		// Find the primary point
		try
		{
			for( int i = 0; i < model.getTrendSeries().length; i++ )
			{
				if(GDSTypesFuncs.isPrimaryType( model.getTrendSeries()[i].getTypeMask()))
				{
					primaryPointIndex = i;
					break;
				}	
			}
	
			buf.append("<center>\n");
			buf.append("<p bgcolor=\""+TITLE_HEADER_BGCOLOR+"\" align=\"center\" class=\"titleheader\">&nbsp;<b><font face=\"arial\"><span class=\"titleheader\">\n");
			buf.append( model.getChartName());
			buf.append("<br>\n");
			
			if( TimeUtil.differenceInDays( model.getStartDate(), model.getStopDate() ) == 1 )
			{
				buf.append( dateFormat.format( model.getStartDate()) );
			}
			else
			{
				buf.append( dateFormat.format( model.getStartDate()) );
		 		buf.append( " - " );
	 			buf.append( dateFormat.format( model.getStopDate() ) );
			}
	 		buf.append("</span></font></b></p>\n");
		 	buf.append("<p bgcolor=\""+TITLE_HEADER_BGCOLOR+"\" align=\"center\" class=\"titleheader\"><b><font size=\"-1\" face=\"arial\"><span class=\"titleheader\">Current Peaks Table</span></font></b>\n");
				
			buf.append("<table valign=\"middle\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n");
			buf.append("<tr>\n");
	
			// Only continue if a peak point exists, otherwise, draw an imcomplete data table.
			if( primaryPointIndex == -1 )
			{
				buf.append("<td align=\"center\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><font size=\"-1\" face=\"arial\"><span class=\"headercell\">");
				buf.append("&nbsp;No Primary Point Defined&nbsp;\n");
				buf.append("</span></font></td></tr>\r\n");
				buf.append("</table><br>\r\n");
				return buf;
			}
	
			buf.append("<td align=\"center\" width=\"130\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">Time</span></font></b></td>\n");
			buf.append("<td align=\"center\" width=\"80\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">");
	
			buf.append("* " +  model.getTrendSeries()[primaryPointIndex].getLabel());
			buf.append("</span></font></b></td>\n");	 
		
			//List all graph points
			for( int i = 0; i < model.getTrendSeries().length; i++ )
			{
				TrendSerie serie = model.getTrendSeries()[i];
				
				if(GDSTypesFuncs.isGraphType(serie.getTypeMask()))
				{
					if( !(GDSTypesFuncs.isPrimaryType(serie.getTypeMask())))
					{
						buf.append("<td align=\"center\" width=\"80\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">");
						buf.append(serie.getLabel());
						buf.append("</span></font></b></td>\n");
					}
				}
			}
	
			buf.append("</tr>\r\n");
		
			// Set the number of decimal places that will display for each point.
			int decimals = model.getTrendSeries()[primaryPointIndex].getDecimalPlaces();
			setFractionDigits( decimals );
			
			// Find the 6 peak values for the primary point
			double[] peakData = model.getTrendSeries()[primaryPointIndex].getValuesArray();
			long[] peakTimeStamps = model.getTrendSeries()[primaryPointIndex].getPeriodsArray();
	
			// Using a sorted tree map to find the 6 peak values and timestamps
			// THIS IS DIFFERENT THAN MOST OUR TREE MAPS, KEY IS VALUE, NOT TIMESTAMP
			java.util.TreeMap peakMap = new java.util.TreeMap();
	
			for( int i = 0; i < peakData.length; i++ )
			{
				if( peakMap.size() < 6 || peakData[i] > ((Double)(peakMap.firstKey())).doubleValue() )
				{
					if( peakMap.size() == 6 )
						peakMap.remove( peakMap.firstKey() );
				
					peakMap.put( new Double( peakData[i]), new Long( peakTimeStamps[i] ));
				}
			}	
			java.util.Set keySet = peakMap.keySet();
			Double[] keyArray = new Double[keySet.size()];
			keySet.toArray(keyArray);
		
			for( int i = keyArray.length-1; i >= 0; i-- )
			{
				buf.append("<tr valign=\"middle\">\r\n");
				buf.append("<td valign=\"middle\" align=\"center\" width=\"130\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\">&nbsp;<font size=\"-1\" face=\"arial\"><span class=\"tablecell\">");
				buf.append( dateTimeformat.format(new java.util.Date(((Long)peakMap.get(keyArray[i])).longValue())));
				buf.append("</span></font></td>\r\n");
		
				buf.append("<td valign=\"middle\" align=\"center\" width=\"80\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\">&nbsp;<font size=\"-1\" face=\"arial\"><span class=\"tablecell\">");
				buf.append( valueFormat.format(( (Double)keyArray[i]).doubleValue()));
				buf.append("</span></font></td>\r\n");
				
				for( int j = 0; j < model.getTrendSeries().length; j++ )
				{
					TrendSerie serie = model.getTrendSeries()[j];					
	
					if(GDSTypesFuncs.isGraphType(serie.getTypeMask()))
					{
						if( !(GDSTypesFuncs.isPrimaryType(serie.getTypeMask())))
						{
							// Set the number of decimal places that are displayed for each point (series).
							decimals = serie.getDecimalPlaces();
							setFractionDigits( decimals );
		
							double[] vals = serie.getValuesArray();
							long[] times = serie.getPeriodsArray();
		
							Long ts = (Long) peakMap.get(keyArray[i]);
							int index = -1;
						
							if( ts != null  && times != null)
								index = java.util.Arrays.binarySearch( times, ts.longValue());
		
							if( index >= 0 )
							{	
								buf.append("<td align=\"center\" width=\"80\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\">&nbsp;<font size=\"-1\" face=\"arial\"><span class=\"tablecell\">");			
								buf.append( valueFormat.format( vals[index] ));
								buf.append("</span></font></td>\n");
							}
							else
							{
								buf.append("<td align=\"center\" width=\"80\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\">&nbsp;<font size=\"-1\" face=\"arial\"><span class=\"tablecell\">");			
								buf.append("</span></font></td>\n");
							}
						}
					}
				}
				buf.append("</tr>\n");
			}
	
			buf.append("</table>\n");
			return buf;
		}
		catch( Exception e)
		{
			buf.append("<tr><td align=\"center\" width=\"130\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\"><font size=\"-1\" face=\"arial\"><span class=\"tablecell\">");
			buf.append("No Data Obtained</span></font></td>\n");
			buf.append("<td align=\"center\" width=\"80\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\"><font size=\"-1\" face=\"arial\"><span class=\"tablecell\">N/A</span></font></td>\n");
			for( int i = 0; i < model.getTrendSeries().length; i++ )
			{
				TrendSerie serie = model.getTrendSeries()[i];
				if(GDSTypesFuncs.isGraphType(serie.getTypeMask()))
				{
					if( !(GDSTypesFuncs.isPrimaryType(serie.getTypeMask())))
					{
						buf.append("<td align=\"center\" width=\"80\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\"><font size=\"-1\" face=\"arial\"><span class=\"tablecell\">N/A</span></font></td>\r\n");
					}
				}
			}
			buf.append("</tr></table>\n");
			return buf;
		}
	}
}
