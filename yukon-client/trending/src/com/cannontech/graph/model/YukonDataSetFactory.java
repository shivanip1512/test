package com.cannontech.graph.model;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import java.util.Iterator;
import java.util.Map;

import org.jfree.data.AbstractDataset;
import org.jfree.data.DefaultCategoryDataset;
import org.jfree.data.SeriesException;
import org.jfree.data.XYSeries;
import org.jfree.data.XYSeriesCollection;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;

import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphRenderers;
/**
 * A quick and dirty implementation.
 */
public class YukonDataSetFactory implements com.cannontech.graph.GraphDefines
{
	private static int options = 0x0000;
	
	public static AbstractDataset [] createDataset(TrendSerie [] trendSeries, int options_, int type_ )
	{
		options = options_;
		if( type_ == GraphRenderers.BAR || type_ == GraphRenderers.BAR_3D)
		{
			if( (options_ & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK)			
				return YukonDataSetFactory.createVerticalCategoryDataSet_LD(trendSeries);
			else
				return YukonDataSetFactory.createVerticalCategoryDataSet(trendSeries);
		}
		else //if( type_ == GraphRenderers.LINE|| type_ == GraphRenderers.LINE_SHAPES || type_ == GraphRenderers.STEP)
		{
			if( (options_ & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK)
				return YukonDataSetFactory.createLoadDurationDataSet(trendSeries);
			else
				return YukonDataSetFactory.createBasicDataSet(trendSeries);
	
		}		
	}
	
    private static String updateSeriesNames(TrendSerie serie)
    {
		String stat = "";					
		if(GDSTypesFuncs.isGraphType(serie.getTypeMask()))
		{
			if ((options & GraphRenderers.LEGEND_LOAD_FACTOR_MASK) == GraphRenderers.LEGEND_LOAD_FACTOR_MASK)
			{
				double lf = serie.getLoadFactor();
				if( lf < 0)
					stat += "     LF: n/a";
				else
					stat += "     LF: " + LF_FORMAT.format(lf);
			}

			if( (options & GraphRenderers.LEGEND_MIN_MAX_MASK) == GraphRenderers.LEGEND_MIN_MAX_MASK)
			{
				if( serie.getAxis().equals(new Character('L')))
				{					
					if( serie.getMinimumValue() == null ||	serie.getMinimumValue().doubleValue() == Double.MAX_VALUE)
						stat += "   Min:  n/a";
					else
						stat += "   Min: " + MIN_MAX_FORMAT.format(serie.getMinimumValue());

					if( serie.getMaximumValue() == null ||	serie.getMaximumValue().doubleValue() == Double.MIN_VALUE)
						stat += "   Max:  n/a";
					else
						stat += "   Max: " + MIN_MAX_FORMAT.format(serie.getMaximumValue());
				}
				else if( serie.getAxis().equals(new Character('R')))
				{					
					if( serie.getMinimumValue() == null || serie.getMinimumValue().doubleValue() == Double.MAX_VALUE)
						stat += "   Min:  n/a";
					else
						stat += "   Min: " + MIN_MAX_FORMAT.format(serie.getMinimumValue());
					if( serie.getMaximumValue() == null || serie.getMaximumValue().doubleValue() == Double.MIN_VALUE)
						stat += "   Max:  n/a";
					else
						stat += "   Max: " + MIN_MAX_FORMAT.format(serie.getMaximumValue());
				}
			}
			stat += "     " + serie.getAxis();
		}
		return stat;
    }    	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:46:02 AM)
	 * @param gModel com.cannontech.graph.model.GraphModel
	 */
	public static java.util.TreeMap buildTreeMap(TrendSerie [] tSeries, int length, int datasetIndex)
	{
		java.util.TreeMap tree = new java.util.TreeMap();

		int validIndex = 0;
		for( int i = 0; i < tSeries.length; i++ )
		{
			TrendSerie serie = tSeries[i];			
			if(GDSTypesFuncs.isGraphType( serie.getTypeMask()))
			{
				if( serie.getAxis().equals(axisChars[datasetIndex]))
				{
					if( serie.getDataItemsMap() != null)
					{
				 		long[] timeStamp = serie.getPeriodsArray();
				 		double[] values = serie.getValuesArray();
				 		for( int j = 0; timeStamp != null && values != null &&  j < timeStamp.length && j < values.length; j++ )
				 		{
					 		Long d = new Long(timeStamp[j]);
					 		Double[] objectValues = (Double[]) tree.get(d);
					 		if( objectValues == null )
					 		{	
						 		//objectValues is not in the key already
						 		objectValues = new Double[ length ];
						 		tree.put(d,objectValues);
					 		}
					 		objectValues[validIndex] = new Double(values[j]);
						}
						validIndex++;
					}
				}
			}
		}
		return tree;
	}



	public static TimeSeriesCollection [] createBasicDataSet(TrendSerie [] tSeries ) 
	{
		if( tSeries == null)
			return null;


		TimeSeriesCollection [] dSet = new TimeSeriesCollection[2];
		for ( int datasetIndex = 0; datasetIndex < 2; datasetIndex++)
		{
			dSet[datasetIndex] = new TimeSeriesCollection();
			for ( int i = 0; i < tSeries.length; i++)
			{
				Double prevValue = null;
				TrendSerie serie = tSeries[i];
				if( serie != null)
				{
					if(GDSTypesFuncs.isGraphType( serie.getTypeMask()))
					{
						if( serie.getAxis().equals(axisChars[datasetIndex]))
						{	
							TimeSeries timeSeries = new TimeSeries(serie.getLabel(), Minute.class);

							if( serie.getDataItemsMap() != null)
							{

								Iterator iter = serie.getDataItemsMap().entrySet().iterator();
								while( iter.hasNext())
								{
									TimeSeriesDataItem dataItem = serie.getDataItem((Map.Entry)iter.next());
									try
									{
										if( GDSTypesFuncs.isUsageType(serie.getTypeMask()))
										{
											if( prevValue == null)
											{
												prevValue = (Double)dataItem.getValue();
											}
											else
											{
												Double currentValue = (Double)dataItem.getValue();
												if( currentValue != null && prevValue != null)
												{
													TimeSeriesDataItem tempDP = new TimeSeriesDataItem(dataItem.getPeriod(), new Double(currentValue.doubleValue() - prevValue.doubleValue()));
													prevValue = currentValue;
													timeSeries.add(tempDP);
												}
											}
										}
										else
										{
											timeSeries.add(dataItem);
										}
									}
									catch(SeriesException se)
									{
										com.cannontech.clientutils.CTILogger.info("Serie ["+serie.getLabel()+"] Exception:  PERIOD = " + dataItem.getPeriod().getStart() + " VALUE = " + dataItem.getValue().doubleValue());
									}
								}								
							}
							//Update the serie's name for legend display.
							//FIND A BETTER WAY TO UPDATE THE NAME!!! (FOR LEGEND PURPOSES)
							timeSeries.setName(timeSeries.getName() + updateSeriesNames(serie));
							dSet[datasetIndex].addSeries(timeSeries);
						}
					}
				}
			}
		}
		return dSet;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:46:02 AM)
	 * @param cModels FreeChartModel []
	 */
	public static XYSeriesCollection [] createLoadDurationDataSet(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;

		XYSeriesCollection [] dataset = new XYSeriesCollection[2];
		int primaryIndex = -1;
		int primaryDset = -1;
		//Valid series are those that have type = graph.  tSeries has all types of series,
		//  therefore we need to weed out those we don't want in the graph.
		for( int datasetIndex = 0; datasetIndex < 2; datasetIndex++)
		{
			dataset[datasetIndex] = new XYSeriesCollection();			
		
			int count = 0;
			for( int i = 0; i < tSeries.length; i++)
			{
				if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
				{
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{
						if( GDSTypesFuncs.isPrimaryType(tSeries[i].getTypeMask()))
						{	//find the primary gds, if it exists!
							primaryIndex = count;
							primaryDset = datasetIndex;
						}
						count++;
					}
				}
			}
	
			java.util.TreeMap tree = buildTreeMap(tSeries, count, datasetIndex);
			if( tree == null)
				return null;
	
			// Get the keySet (timestamps) in a useable array structure.
			java.util.Set keySet = tree.keySet();
			Long[] keyArray = new Long[keySet.size()];
			keySet.toArray(keyArray);
	
			// Create the category list (of timestamps).
			// Categories only serve a purpose to create a graph with a percentage axis.
			double[] categories = new double[keyArray.length];
			double categoryCount = keyArray.length -1;// scale is from 0 start point not 1
			for (int i = 0; i < keyArray.length; i++)
				categories[i] = ( i / categoryCount ) * 100;
	
			//This index holder is needed parrallel to i.
			//When there is a null tSeries[i].getDataItemArray(), we have to ignore the i values interval 
			//of the tree.get(keyArray[j]).  AKA...the i value can't be incremented, But because i is the 
			//for loop index of the series, we need another representation of it, hence notNullValuesIndex.
			int notNullValuesIndex = 0;
			int allIndex = 0;
			for (int i = 0; i < tSeries.length; i++)
			{
				TrendSerie serie = tSeries[i];
				if(GDSTypesFuncs.isGraphType(serie.getTypeMask()))
				{
					if( serie.getAxis().equals(axisChars[datasetIndex]))
					{
						XYSeries xySeries = new XYSeries(serie.getLabel());

						Double value = null;
						Double prevValue = null;				
						if( serie.getDataItemsMap() != null)
						{
							for (int j = 0; j < keyArray.length; j++)
							{
								Double[] values = (Double[])tree.get(keyArray[j]);
								if( GDSTypesFuncs.isUsageType(serie.getTypeMask()))
								{
									if( prevValue == null)
									{
										prevValue = values[notNullValuesIndex];	//dp.getValue().doubleValue();
									}
									else
									{
										Double currentValue = values[notNullValuesIndex];	//dp.getValue().doubleValue();
										if( currentValue != null && prevValue != null)
										{
											value = new Double(currentValue.doubleValue() - prevValue.doubleValue());
											prevValue = currentValue;
										}
									}
								}						
								else
								{
									value = values[notNullValuesIndex];
								}
//								if( value != null || !(primaryDset==datasetIndex && primaryIndex==allIndex))
									xySeries.add(categories[j], value);
							}
							notNullValuesIndex++;
							allIndex++;
						}
						else
						{
							if( GDSTypesFuncs.isPrimaryType(serie.getTypeMask()))
							{
								// We take away the fact there is a primary gds so that when we sort
								//  the values, we are able to still show load duration.
								primaryIndex = -1;
								primaryDset = -1;
							}
		
							for (int j = 0; j < keyArray.length; j++)
							{
//								if(!(primaryDset==datasetIndex && primaryIndex==allIndex))								
									xySeries.add(categories[j],null);
							}
							allIndex++;
						}
						xySeries.setName(xySeries.getName() + updateSeriesNames(serie));
						dataset[datasetIndex].addSeries(xySeries);
					}
				}
			}
		}

		//Sort values based on primary gds, if it exists.		
		sortValuesDescending(dataset, primaryDset, primaryIndex);
		return dataset;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:46:02 AM)
	 * @param cModels FreeChartModel []
	 */
	
	public static DefaultCategoryDataset [] createVerticalCategoryDataSet(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;

		DefaultCategoryDataset[] dataset = new DefaultCategoryDataset[2];
		
		for( int datasetIndex = 0; datasetIndex < 2; datasetIndex++)
		{
			int count = 0;				
			for( int i = 0; i < tSeries.length; i++)
			{
				if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
				{
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{
						count++;
					}
				}
			}
	
		
			java.util.TreeMap tree = buildTreeMap(tSeries, count, datasetIndex);
			if( tree == null)
				return null;		
	
			// Get the keySet (timestamps) in a useable array structure.
			java.util.Set keySet = tree.keySet();
			Long[] keyArray = new Long[keySet.size()];
			keySet.toArray(keyArray);
			
			//This index holder is needed parrallel to i.
			//When there is a null tSeries[i].getDataItemArray(), we have to ignore the i values interval of the tree.get(keyArray[j]).
			int notNullValuesIndex = 0;

			DefaultCategoryDataset tempDataset = new DefaultCategoryDataset();
			java.util.Vector categoryVector = new java.util.Vector();
			for (int j = 0; j < keyArray.length; j++)
			{
				Long ts = keyArray[j];
				categoryVector.add(CATEGORY_FORMAT.format(new java.util.Date(ts.longValue())));
			}

			
			for (int i = 0; i < tSeries.length; i++)
			{
				Double prevValue = null;
				Double value = null;
				
				TimeSeriesDataItem prevTimePeriod = null;
				if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
				{
					String serieKey = tSeries[i].getLabel().toString() + updateSeriesNames(tSeries[i]);
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{
						if( tSeries[i].getDataItemsMap() != null)
						{

							for (int j = 0; j < keyArray.length; j++)
							{
								if( GDSTypesFuncs.isUsageType(tSeries[i].getTypeMask()))
								{
									Double[] values = (Double[])tree.get(keyArray[j]);							
									if( prevValue == null)
									{
										prevValue = values[notNullValuesIndex];	//dp.getValue().doubleValue();
										value = null;
									}
									else
									{
										Double currentValue = values[notNullValuesIndex];	//dp.getValue().doubleValue();
										if( currentValue != null && prevValue != null)
										{
											value = new Double(currentValue.doubleValue() - prevValue.doubleValue());
											prevValue = currentValue;
										}
									}
								}						
								else
								{
									Double[] values = (Double[])tree.get(keyArray[j]);
									value = values[notNullValuesIndex];
								}
								tempDataset.addValue(value, serieKey, categoryVector.get(j).toString());
							}
							notNullValuesIndex++;
						}
						else
						{
							if( keyArray.length <= 0)
								tempDataset.addValue(value, serieKey, "");
							else
							{
								for (int j = 0; j < keyArray.length; j++)
								{
									tempDataset.addValue(value, serieKey, categoryVector.get(j).toString());
								}
							}
						}
					}
				}
			}
			dataset[datasetIndex] = tempDataset;
		}
		return dataset;
	}

	
	public static DefaultCategoryDataset [] createVerticalCategoryDataSet_LD(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;

		DefaultCategoryDataset[] dataset = new DefaultCategoryDataset[2];
		int primaryIndex = -1;
		int primaryDset = -1;
		for( int datasetIndex = 0; datasetIndex < 2; datasetIndex++)
		{
			int count = 0;
			
			for( int i = 0; i < tSeries.length; i++)
			{
				if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
				{
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{				
						if( GDSTypesFuncs.isPrimaryType(tSeries[i].getTypeMask()))
						{	//find the primary gds, if it exists!
							primaryIndex = count;
							primaryDset = datasetIndex;
						}
						count++;
					}
				}
			}

			java.util.TreeMap tree = buildTreeMap(tSeries, count, datasetIndex);
			if( tree == null)
				return null;		
			
			// Get the keySet (timestamps) in a useable array structure.
			java.util.Set keySet = tree.keySet();
			Long[] keyArray = new Long[keySet.size()];
			keySet.toArray(keyArray);
			
			//This index holder is needed parrallel to i.
			//When there is a null tSeries[i].getDataItemArray(), we have to ignore the i values interval of the tree.get(keyArray[j]).
			int notNullValuesIndex = 0;

			DefaultCategoryDataset tempDataset = new DefaultCategoryDataset();
			double[] categories = new double[keyArray.length];
			double categoryCount = keyArray.length -1;// scale is from 0 start point not 1
			for (int i = 0; i < keyArray.length; i++)
				categories[i] = ( i / categoryCount ) * 100;

			for (int i = 0; i < tSeries.length; i++)
			{
				Double prevValue = null;
				Double value = null;
			
				TimeSeriesDataItem prevTimePeriod = null;
				if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
				{
					String serieKey = tSeries[i].getLabel().toString() + updateSeriesNames(tSeries[i]);
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{
						if( tSeries[i].getDataItemsMap() != null)
						{

							for (int j = 0; j < keyArray.length; j++)
							{
								if( GDSTypesFuncs.isUsageType(tSeries[i].getTypeMask()))
								{
									Double[] values = (Double[])tree.get(keyArray[j]);							
									if( prevValue == null)
									{
										prevValue = values[notNullValuesIndex];	//dp.getValue().doubleValue();
										value = null;
									}
									else
									{
										Double currentValue = values[notNullValuesIndex];	//dp.getValue().doubleValue();
										if( currentValue != null && prevValue != null)
										{
											value = new Double(currentValue.doubleValue() - prevValue.doubleValue());
											prevValue = currentValue;
										}
									}
								}						
								else
								{
									Double[] values = (Double[])tree.get(keyArray[j]);
									value = values[notNullValuesIndex];
								}
								
								tempDataset.addValue(value, serieKey, new Integer(new Double(categories[j]).intValue()));
							}
							notNullValuesIndex++;
						}
						else
						{
							if( GDSTypesFuncs.isPrimaryType(tSeries[i].getTypeMask()))
							{
								// We take away the fact there is a primary gds so that when we sort
								//  the values, we are able to still show load duration.
								primaryIndex = -1;
								primaryDset = -1;
							}
							
							if( keyArray.length <= 0)
								tempDataset.addValue(value, serieKey, "");
							for (int j = 0; j < keyArray.length; j++)
							{
								tempDataset.addValue(value, serieKey, new Integer(new Double(categories[j]).intValue()));
							}
						}
					}
				}
			}
			dataset[datasetIndex] = tempDataset;
//			sortValuesDescending(tempDataset, primaryDset, primaryIndex);
		}			
		sortValuesDescending(dataset, primaryDset, primaryIndex);
		return dataset;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (7/2/2001 11:10:25 AM)
	 * @param yVals java.util.ArrayList
	 * @param xHrs java.util.ArrayList
	 * @param yQual java.util.ArrayList
	 */
	private static void sortValuesDescending(DefaultCategoryDataset[] dSet, int primaryDset, int primaryIndex)
	{
		int maxIndex = 0;

		if( primaryIndex < 0 )	//No primary gds point!
		{
			for(int i = 0; i < 2; i++)
			{
				// Sort the values according to their value readings (descending)
				for (int j = 0; j < dSet[i].getRowCount(); j++)
				{
					Comparable rowKey = dSet[i].getRowKey(j);
					//SORT Start for serie = j
					for (int x = dSet[i].getColumnCount() -1; x >=0; x--)
					{
						for (int y = 0; y < x; y++)
						{
							Double currentVal = (Double)dSet[i].getValue(j, y);
							if( currentVal != null)
							{
								int tempy = y+1;
								Double nextVal = (Double)dSet[i].getValue(j, y+1);
								while(nextVal == null && tempy < x )
								{
									tempy++;
									nextVal = (Double)dSet[i].getValue(j, tempy);
								}
								if( nextVal!= null)
								{
									if( currentVal.doubleValue() < nextVal.doubleValue())
									{
										//swap the values
										dSet[i].setValue(currentVal, rowKey, dSet[i].getColumnKey(tempy));
										dSet[i].setValue(nextVal, rowKey , dSet[i].getColumnKey(y));
									}
								}
							}
						}
					}
				}
			}
		}
		else
		{
			// Have a primary gds and need to sort these values only!  Rest are coincidental on Primary GDS!
			Comparable rowKey = dSet[primaryDset].getRowKey(primaryIndex);
			if( dSet[primaryDset].getColumnKey(primaryIndex) != null)
			{
				//SORT
				for( int x = dSet[primaryDset].getColumnCount() - 1; x >=0; x--)
				{
					for( int y = 0; y < x; y++)
					{
						Double currentVal = (Double)dSet[primaryDset].getValue(primaryIndex, y);
						if( currentVal != null)
						{
							int tempy = y+1;
							Double nextVal = (Double)dSet[primaryDset].getValue(primaryIndex, y+1);
							while(nextVal == null && tempy < x )
							{
								tempy++;
								nextVal = (Double)dSet[primaryDset].getValue(primaryIndex, tempy);
							}
							if( nextVal!= null)
							{
								if( currentVal.doubleValue() < nextVal.doubleValue())	//sort values descending
								{
									dSet[primaryDset].setValue(currentVal.doubleValue(), rowKey, dSet[primaryDset].getColumnKey(tempy));
									dSet[primaryDset].setValue(nextVal.doubleValue(), rowKey, dSet[primaryDset].getColumnKey(y));
		
									//need to do all other series here.
									for(int a = 0; a < 2; a++)
									{
										for(int b = 0; b < dSet[a].getRowCount(); b++)
										{
											if( !(a==primaryDset && b == primaryIndex))	//not primary dSet/serie
											{
												currentVal = (Double)dSet[a].getValue(b, y);
												nextVal = (Double)dSet[a].getValue(b, tempy);
	
												Comparable rowKey2 = dSet[a].getRowKey(b);
												dSet[a].setValue(currentVal, rowKey2, dSet[a].getColumnKey(tempy));
												dSet[a].setValue(nextVal, rowKey2, dSet[a].getColumnKey(y));
											}
										}
									}
								}
							}							
						}
					}
				}
			}
		}
	}
	
	private static void sortValuesDescending(XYSeriesCollection[] dSet, int primaryDset, int primaryIndex)
	{
		int maxIndex = 0;

		if( primaryIndex < 0 )	//No primary gds point!
		{
			for(int i = 0; i < 2; i++)
			{
				// SORT EACH SERIES BASED ON ITS OWN VALUES (descending)
				for (int j = 0; j < dSet[i].getSeriesCount();j++)
				{
					if( dSet[i].getSeries(j) != null)
					{
						//SORT Start for serie = j
						for(int x  = dSet[i].getSeries(j).getItemCount() -1; x >=0; x--)
						{
							for (int y = 0; y < x; y++)
							{
								Double currentVal = (Double)dSet[i].getSeries(j).getDataItem(y).getY();
								if( currentVal != null)
								{
									int tempy = y+1;
									Double nextVal = (Double)dSet[i].getSeries(j).getDataItem(y+1).getY();
									while(nextVal == null && tempy < x )
									{
										tempy++;
										nextVal = (Double)dSet[i].getSeries(j).getDataItem(tempy).getY();
									}
									if( nextVal!= null)
									{
										if( currentVal.doubleValue() < nextVal.doubleValue())
										{
											//swap the values
											dSet[i].getSeries(j).getDataItem(tempy).setY(currentVal);
											dSet[i].getSeries(j).getDataItem(y).setY(nextVal);
										}
									}
								}
							}
						}
	
					}
				}
			}
		}
		else
		{
			// Have a primary gds and need to sort these values only!  Rest are coincidental on Primary GDS!
			if( dSet[primaryDset].getSeries(primaryIndex) != null)
			{
				//SORT
				for( int x = dSet[primaryDset].getItemCount(primaryIndex) - 1; x >=0; x--)
				{
					for( int y = 0; y < x; y++)
					{
						Double currentVal_ = (Double)dSet[primaryDset].getSeries(primaryIndex).getDataItem(y).getY();
						if( currentVal_ != null)
						{
							int tempy = y+1;
							Double nextVal_ = (Double)dSet[primaryDset].getSeries(primaryIndex).getDataItem(y+1).getY();
							while(nextVal_ == null && tempy < x )
							{
								tempy++;
								nextVal_ = (Double)dSet[primaryDset].getSeries(primaryIndex).getDataItem(tempy).getY();
							}
							if( nextVal_!= null)
							{
								if( currentVal_.doubleValue() < nextVal_.doubleValue())
								{
									dSet[primaryDset].getSeries(primaryIndex).getDataItem(tempy).setY((Double)currentVal_);
									dSet[primaryDset].getSeries(primaryIndex).getDataItem(y).setY((Double)nextVal_);
	
									//SORT ALL OTHER SERIES BASE ON PRIMARY
									for(int a = 0; a < 2; a++)
									{
										for(int b = 0; b < dSet[a].getSeriesCount(); b++)
										{
											if( !(a==primaryDset && b==primaryIndex))	//not primary dSet/serie
											{
												if( dSet[a].getSeries(b).getItemCount() > 0 && dSet[a].getSeries(b).getItemCount() > tempy)
												{
													currentVal_ = (Double)dSet[a].getSeries(b).getDataItem(y).getY();
													nextVal_ = (Double)dSet[a].getSeries(b).getDataItem(tempy).getY();
													
													dSet[a].getSeries(b).getDataItem(tempy).setY((Double)currentVal_);
													dSet[a].getSeries(b).getDataItem(y).setY((Double)nextVal_);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		//Remove all null values, line graphs will not print continuous with null data.
		for(int a = 0; a < 2; a++)
		{
			for(int b = 0; b < dSet[a].getSeriesCount(); b++)
			{
				for (int c = 0; c < dSet[a].getSeries(b).getItemCount()-1; c++)
				{
					if(dSet[a].getSeries(b).getDataItem(c).getY() == null)
					{
						dSet[a].getSeries(b).delete(c, c);
						c--;
					}
				}
			}
		}

	}
}