package com.cannontech.graph.buffer.html;

/**
 * Generates html to display the data in a GraphModel in a tabular format.
 * Creation date: (1/31/2001 1:35:09 PM)
 * @author: 
 */
import java.util.TreeMap;

import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

import com.cannontech.common.util.TimeUtil;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.model.TrendSerie;

public class TabularHtml extends HTMLBuffer
{
	// In order for the tabular display to only show one date...
	//  we are setting a "tabular" start/end date criteria.
	public java.util.Date tabularStartDate = null; 
	public java.util.Date tabularEndDate = null;
	private java.text.SimpleDateFormat tabularTimeFormat = timeFormat_HH_mm;
	private long resolution = 1;

	/**
	 * Writes html into the given buffer.
	 * Creation date: (1/31/2001 1:38:46 PM)
	 * @return java.lang.StringBuffer
	 */
	public StringBuffer getHtml(StringBuffer buf)
	{
		if( model.getTrendSeries() == null)
			return buf;
		
		//Find the primary point index, if one exists.
		int primaryIndex = -1;
		int validSeriesCount = 0;
		for( int i = 0; i < model.getTrendSeries().length; i++ )
		{
			TrendSerie serie = model.getTrendSeries()[i];
			if(GDSTypesFuncs.isGraphType( serie.getTypeMask()))
			{
				if( GDSTypesFuncs.isPrimaryType(serie.getTypeMask()))
					primaryIndex = validSeriesCount;
				
				validSeriesCount++;
			}
		}
	
		long tabStDt = model.getStartDate().getTime();
		long tabEndDt = model.getStopDate().getTime();
		
		if( getTabularStartDate() != null && getTabularEndDate() != null )
		{
			if(! ((model.getOptionsMaskSettings() & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK))
			{
				tabStDt = getTabularStartDate().getTime();
				tabEndDt = getTabularEndDate().getTime();
			}
		}
		buf.append("<center>\n");
		buf.append("<p bgcolor=\""+TITLE_HEADER_BGCOLOR+"\" align=\"center\">&nbsp;<b><font face=\"arial\"><span class=\"titleheader\">\n");
		buf.append( model.getChartName());
		buf.append("<br>\n");
	
		if( TimeUtil.differenceInDays( new java.util.Date(tabStDt), new java.util.Date(tabEndDt)) == 1 )
		{
			buf.append( dateFormat.format( new java.util.Date(tabStDt)) );
		}
		else
		{
			buf.append( dateFormat.format( new java.util.Date(tabStDt)) );
			buf.append( " - " );
			buf.append( dateFormat.format( new java.util.Date(tabEndDt)));
		}
		buf.append("</span></font></b>");
	
		buf.append("<table><tr valign=\"top\">");
					
		boolean loadDurationSort = false;
		if( (model.getOptionsMaskSettings() & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK)
		{
			tabularTimeFormat = multipleDaystimedateFormat;
	
			if( primaryIndex < 0 )	//no primary point.
			{
				buf.append( getLDHTML( ));
				buf.append("</tr></table></center>\n");
				return buf;
			}
			else
			{
				//set flag to sort values based on primary point later...like after we collect them in the treeMap.
				loadDurationSort = true;
			}
		}
		
		buf.append("<td>");
		buf.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n");
		buf.append("<tr valign=\"middle\" align=\"center\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\">\n");
		buf.append("<td height=\"40\" width=\"70\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">Time</span></b></font></td>\n");
	
		java.util.Vector validDecimalPlaces = new java.util.Vector(model.getTrendSeries().length);
		for (int i = 0; i < model.getTrendSeries().length; i++)
		{
			TrendSerie serie = model.getTrendSeries()[i];
			if( GDSTypesFuncs.isGraphType(serie.getTypeMask()))
			{
				buf.append("<td height=\"40\" width=\"130\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">");
				buf.append( serie.getLabel());
				buf.append("</span></font></b></td>\n");
	
				validDecimalPlaces.add(new Integer(serie.getDecimalPlaces()));
			}
		}			
		Integer [] decimals = new Integer[validDecimalPlaces.size()];
		validDecimalPlaces.toArray(decimals);
	
		// Gets ALL possible timestamps for all of the points.
		// Set up a tree map of the model
		java.util.TreeMap tree = buildTreeMap(validSeriesCount, tabStDt, tabEndDt);  //time values
		java.util.Set keySet = tree.keySet();
		Long[] keyArray = new Long[keySet.size()];
		keySet.toArray(keyArray);
		
		Double[][] valuesArray = new Double[keySet.size()][validSeriesCount];
		for (int j = 0; j < keyArray.length; j++)
		{
			Double[] values = (Double[])tree.get(keyArray[j]);
			valuesArray[j]= values;
		}
			
		if (loadDurationSort)
		{
			//Sort values based on primary point.
			for (int a = valuesArray.length -1; a >=0; a--)
			{
				for (int b = 0; b < a; b++)
				{
					Double currentVal = (Double)valuesArray[b][primaryIndex];
					if( currentVal != null)
					{
						int tempB = b+1;
						Double nextVal = (Double)valuesArray[b+1][primaryIndex];
						while(nextVal == null && tempB < a )
						{
							tempB++;
							nextVal =  (Double)valuesArray[tempB][primaryIndex];
						}
						if( nextVal!= null)
						{
							if( currentVal.doubleValue() < nextVal.doubleValue())
							{
								valuesArray[tempB][primaryIndex] = currentVal;
								valuesArray[b][primaryIndex] = nextVal;
									
								Long currentTS = keyArray[b];
								Long nextTS = keyArray[tempB];
									
								keyArray[tempB] = currentTS;
								keyArray[b] = nextTS;
								for( int c = 0; c < valuesArray[b].length; c++)
								{
									if( c != primaryIndex)
									{
										currentVal = (Double)valuesArray[b][c];
										nextVal = (Double)valuesArray[tempB][c];
										valuesArray[tempB][c] = currentVal;
										valuesArray[b][c] = nextVal;
									}
								}
							}
						}
					}
				}
			}
		}
	
		buf.append("</tr>\n");
		
		buf.append("<tr align=\"center\" valign=\"top\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\">\n");
		//Output html for the timestamps.
		buf.append("<td class=\"tablecell\" width=\"80\"><font size=\"-1\" face=\"arial\">\n");
		for( int x = 0; x < keyArray.length; x++ )
		{
			Long ts1 = keyArray[x];
			buf.append(tabularTimeFormat.format(new java.util.Date(ts1.longValue())));
			buf.append("<br>");
		}
		buf.append("</font></td>\n");
		
		//Output html for each valid serie's readings.
		int validIndex = 0;
		for (int i = 0; i < model.getTrendSeries().length; i++)
		{
			TimePeriod prevTimePeriod = null;
			if(GDSTypesFuncs.isGraphType(model.getTrendSeries()[i].getTypeMask()))
			{
				setFractionDigits(3);
				buf.append("<td width=\"130\"><font size=\"-1\" face=\"arial\"><span class=\"tablecell\">\n");
				for (int j = 0; j < keyArray.length; j++)
				{
					Double[] values = valuesArray[j];												
					Double val = values[validIndex];
					if( val != null )
						buf.append(valueFormat.format(val));
					buf.append("<br>");
				}
				buf.append("</span></font></td>\n");
					
				validIndex++;
			}
		}
		buf.append("</tr></table></center>\n");	
	
		return buf;
	}
	/**
	 * @return
	 */
	private TreeMap buildTreeMap(int validSeriesCount, long startDate, long endDate)
	{
		TreeMap tree = new java.util.TreeMap();
		
		int validIndex = 0;
		for( int i = 0; i < model.getTrendSeries().length; i++ )
		{
			TrendSerie serie = model.getTrendSeries()[i];
			if(GDSTypesFuncs.isGraphType( serie.getTypeMask()))
			{
				long[] timeStamp = serie.getPeriodsArray();
				double[] values = serie.getValuesArray();
	
				for( int j = 0; timeStamp != null && values != null &&  j < timeStamp.length; j++ )
				{
					Long tsKey = new Long(timeStamp[j]);
					Double[] objectValues = (Double[]) tree.get(tsKey);
					if( objectValues == null )
					{
						if (tsKey.longValue() > startDate && tsKey.longValue() <= endDate)
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
		return tree;
	}
	
	
	private StringBuffer getLDHTML()
	{
		StringBuffer buf = new StringBuffer();
		
		java.util.Vector validDecimalPlaces = new java.util.Vector(model.getTrendSeries().length);
		for (int i = 0; i < model.getTrendSeries().length; i++)
		{
			buf.append("<td><table border=\"1\" cellspacing=\"0\" cellpadding=\"0\">\n");
			buf.append("<tr valign=\"middle\" align=\"center\" bgcolor=\""+HEADER_CELL_BGCOLOR+"\" class=\"headercell\">\n");
	
			TrendSerie serie = model.getTrendSeries()[i];
			if( GDSTypesFuncs.isGraphType(serie.getTypeMask()))
			{
				buf.append("<td height=\"40\" width=\"80\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">Time</span></font></b></td>\n");
				buf.append("<td height=\"40\" width=\"130\"><b><font size=\"-1\" face=\"arial\"><span class=\"headercell\">");
				buf.append( serie.getLabel());
				buf.append("</span></font></b></td>\n");
	
				buf.append("</tr>\n");
				buf.append("<tr valign=\"middle\" align=\"center\" bgcolor=\""+TABLE_CELL_BGCOLOR+"\" class=\"tablecell\">\n");
	
				validDecimalPlaces.add(new Integer(serie.getDecimalPlaces()));
				
				//Sort each dataItemArray. (no primary point coincidentals)
				String timeString = "";
				String valueString = "";
				if (serie.getDataItemsMap() != null )
				{
					Object [] dataItemsArray = serie.getDataItemsMap().values().toArray();
				
					java.util.Arrays.sort(dataItemsArray, timeSeriesDataItemValueComparator);
		
					for (int j = 0; j < dataItemsArray.length; j++)
					{
						TimeSeriesDataItem item = (TimeSeriesDataItem)dataItemsArray[j];
						long ts = item.getPeriod().getStart().getTime();
						Double value = (Double)item.getValue();
						timeString += tabularTimeFormat.format(new java.util.Date(ts)) + "<br>";
						valueString += valueFormat.format(value) + "<br>";
					}
				}
				buf.append("<td width=\"80\"><font size=\"-1\" face=\"arial\"><span class=\"tablecell\">\n");
				buf.append(timeString.toString());
				buf.append("</span></font></td>\n");
				
				buf.append("<td width=\"130\"><font size=\"-1\" face=\"arial\"><span class=\"tablecell\">\n");
				buf.append(valueString.toString());
				buf.append("</span></font></td>\n");
			}
			buf.append("</tr></table></td>\n");
		}			
	
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
    /**
     * @return Returns the resolution.
     */
    public long getResolution()
    {
        return resolution;
    }
    /**
     * @param resolution The resolution to set.
     */
    public void setResolution(long resolution)
    {
        this.resolution = resolution;
        if( resolution == 1)
            tabularTimeFormat = timeFormat_HH_mm_ss_SSS;
        else if (resolution == 1000)
            tabularTimeFormat = timeFormat_HH_mm_ss;
        else if (resolution > 1000)
            tabularTimeFormat = timeFormat_HH_mm;
    }
}
