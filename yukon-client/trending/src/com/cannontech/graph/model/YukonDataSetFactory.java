package com.cannontech.graph.model;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import com.cannontech.database.db.graph.GraphDataSeries;
import com.jrefinery.data.AbstractSeriesDataset;
import com.jrefinery.data.AbstractDataset;
/**
 * A quick and dirty implementation.
 */
public class YukonDataSetFactory 
{
    private static java.text.SimpleDateFormat LEGEND_FORMAT = new java.text.SimpleDateFormat("MMM dd");
    private static java.text.SimpleDateFormat CATEGORY_FORMAT = new java.text.SimpleDateFormat(" MMM dd, HH:mm ");

	private static Character [] axisChars = new Character[]{new Character('L'), new Character('R')};
    private static java.text.DecimalFormat MIN_MAX_FORMAT = new java.text.DecimalFormat("0.000");
    private static java.text.DecimalFormat LF_FORMAT = new java.text.DecimalFormat("###.000%");
	private static int options = 0x0000;
	
	public static AbstractDataset [] createDataset(TrendSerie [] trendSeries, int options_, int type_ )
	{
		options = options_;
		
		if( type_ == TrendModelType.LINE_VIEW|| type_ == TrendModelType.SHAPES_LINE_VIEW || type_ == TrendModelType.STEP_VIEW)
		{
			if( (options_ & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)
				return YukonDataSetFactory.createLoadDurationDataSet(trendSeries);
			else
				return YukonDataSetFactory.createBasicDataSet(trendSeries);
			
		}/*
		else if( type_ == TrendModelType.BAR_VIEW )
		{
			if( (options_ & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)			
				return YukonDataSetFactory.createLoadDurationDataSet(trendSeries);
			else
				return YukonDataSetFactory.createBasicDataSet(trendSeries);
		}*/
		
		else if( type_ == TrendModelType.BAR_VIEW || type_ == TrendModelType.BAR_3D_VIEW)
		{
			if( (options_ & TrendModelType.LOAD_DURATION_MASK) == TrendModelType.LOAD_DURATION_MASK)			
				return YukonDataSetFactory.createVerticalCategoryDataSet_LD(trendSeries);
			else
				return YukonDataSetFactory.createVerticalCategoryDataSet(trendSeries);
		}
		return null;
	}
	
    private static String updateSeriesNames(TrendSerie serie)
    {
		String stat = "";					
		if(GraphDataSeries.isGraphType(serie.getTypeMask()))
		{
			if ((options & TrendModelType.LEGEND_LOAD_FACTOR_MASK) == TrendModelType.LEGEND_LOAD_FACTOR_MASK)
			{
				double lf = serie.getLoadFactor();
				if( lf < 0)
					stat += "     Load Factor: n/a";
				else
					stat += "     Load Factor: " + LF_FORMAT.format(lf);
			}

			if( (options & TrendModelType.LEGEND_MIN_MAX_MASK) == TrendModelType.LEGEND_MIN_MAX_MASK)
			{
				if( serie.getAxis().equals(new Character('L')))
				{					
					if( serie.getMinimumValue() == null ||	serie.getMinimumValue().doubleValue() == Double.MAX_VALUE)
						stat += "    Min:  n/a";
					else
						stat += "    Min: " + MIN_MAX_FORMAT.format(serie.getMinimumValue());

					if( serie.getMaximumValue() == null ||	serie.getMaximumValue().doubleValue() == Double.MIN_VALUE)
						stat += "    Max:  n/a";
					else
						stat += "    Max: " + MIN_MAX_FORMAT.format(serie.getMaximumValue());
				}
				else if( serie.getAxis().equals(new Character('R')))
				{					
					if( serie.getMinimumValue() == null || serie.getMinimumValue().doubleValue() == Double.MAX_VALUE)
						stat += "    Min:  n/a";
					else
						stat += "    Min: " + MIN_MAX_FORMAT.format(serie.getMinimumValue());
					if( serie.getMaximumValue() == null || serie.getMaximumValue().doubleValue() == Double.MIN_VALUE)
						stat += "    Max:  n/a";
					else
						stat += "    Max: " + MIN_MAX_FORMAT.format(serie.getMaximumValue());
				}
			}
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
			if(GraphDataSeries.isGraphType( serie.getTypeMask()))
			{
				if( serie.getAxis().equals(axisChars[datasetIndex]))
				{
					if( serie.getDataPairArray() != null)
					{
				 		long[] timeStamp = new long[serie.getDataPairArray().length];
				 		double[] values = new double[serie.getDataPairArray().length];
						for (int x = 0; x < serie.getDataPairArray().length; x++)
						{
							com.jrefinery.data.TimeSeriesDataPair dp = serie.getDataPairArray(x);						
							timeStamp[x] = dp.getPeriod().getStart().getTime();
							values[x] = dp.getValue().doubleValue();
						}
						
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



	public static com.jrefinery.data.TimeSeriesCollection [] createBasicDataSet(TrendSerie [] tSeries ) 
	{
		if( tSeries == null)
			return null;


		com.jrefinery.data.TimeSeriesCollection [] dSet = new com.jrefinery.data.TimeSeriesCollection[2];
		for ( int datasetIndex = 0; datasetIndex < 2; datasetIndex++)
		{
			dSet[datasetIndex] = new com.jrefinery.data.TimeSeriesCollection();
			for ( int i = 0; i < tSeries.length; i++)
			{
				Double prevValue = null;
				TrendSerie serie = tSeries[i];
				if( serie != null)
				{
					if(GraphDataSeries.isGraphType( serie.getTypeMask()))
					{
						if( serie.getAxis().equals(axisChars[datasetIndex]))
						{	
							com.jrefinery.data.TimeSeries series = new com.jrefinery.data.TimeSeries(serie.getLabel(), com.jrefinery.data.Second.class);

							if( serie.getDataPairArray() != null)
							{
								for (int j = 0; j < serie.getDataPairArray().length; j++)
								{
									com.jrefinery.data.TimeSeriesDataPair dp = (com.jrefinery.data.TimeSeriesDataPair)serie.getDataPairArray(j);
									try
									{
										if( GraphDataSeries.isUsageType(serie.getTypeMask()))
										{
											if( prevValue == null)
											{
												prevValue = (Double)dp.getValue();
											}
											else
											{
												Double currentValue = (Double)dp.getValue();
												if( currentValue != null && prevValue != null)
												{
													com.jrefinery.data.TimeSeriesDataPair tempDP = new com.jrefinery.data.TimeSeriesDataPair(dp.getPeriod(), new Double(currentValue.doubleValue() - prevValue.doubleValue()));
													prevValue = currentValue;
													series.add(tempDP);
												}
											}
										}
										else
										{
											series.add(dp);
										}
									}
									catch(com.jrefinery.data.SeriesException se)
									{
										com.cannontech.clientutils.CTILogger.info("Series ["+i+"] Exception:  PERIOD = " + dp.getPeriod().getStart() + " VALUE = " + dp.getValue().doubleValue());
									}
								}
							}
							//Update the serie's name for legend display.
			//YOU ARE HERE TRYING TO FIND A BETTER WAY TO UPDATE THE NAME!!! (FOR LEGEND PURPOSES)
							series.setName(series.getName() + updateSeriesNames(serie));
							dSet[datasetIndex].addSeries(series);
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
	public static com.jrefinery.data.XYSeriesCollection [] createLoadDurationDataSet(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;

		com.jrefinery.data.XYSeriesCollection [] dataset = new com.jrefinery.data.XYSeriesCollection[2];
		int primaryIndex = -1;
		int primaryDset = -1;
		//Valid series are those that have type = graph.  tSeries has all types of series,
		//  therefore we need to weed out those we don't want in the graph.
		for( int datasetIndex = 0; datasetIndex < 2; datasetIndex++)
		{
			dataset[datasetIndex] = new com.jrefinery.data.XYSeriesCollection();			
		
			int count = 0;
			for( int i = 0; i < tSeries.length; i++)
			{
				if(GraphDataSeries.isGraphType(tSeries[i].getTypeMask()))
				{
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{
						if( GraphDataSeries.isPrimaryType(tSeries[i].getTypeMask()))
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
	
			// Create the dataset of values for each point.
			Double[][] datasetValues = new Double[count][];
	
			//This index holder is needed parrallel to i.
			//When there is a null tSeries[i].getDataPairArray(), we have to ignore the i values interval 
			//of the tree.get(keyArray[j]).  AKA...the i value can't be incremented, But because i is the 
			//for loop index of the series, we need another representation of it, hence notNullValuesIndex.
			int notNullValuesIndex = 0;
			int allIndex = 0;
			for (int i = 0; i < tSeries.length; i++)
			{
				if(GraphDataSeries.isGraphType(tSeries[i].getTypeMask()))
				{
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{
						datasetValues[allIndex] = new Double[keyArray.length];
						Double prevValue = null;				
						if( tSeries[i].getDataPairArray() != null)
						{
							for (int j = 0; j < keyArray.length; j++)
							{
								Double[] values = (Double[])tree.get(keyArray[j]);
								if( GraphDataSeries.isUsageType(tSeries[i].getTypeMask()))
								{
									if( prevValue == null)
									{
										prevValue = values[notNullValuesIndex];	//dp.getValue().doubleValue();
										datasetValues[allIndex][j]= null;
									}
									else
									{
										Double currentValue = values[notNullValuesIndex];	//dp.getValue().doubleValue();
										if( currentValue != null && prevValue != null)
										{
											datasetValues[allIndex][j] = new Double(currentValue.doubleValue() - prevValue.doubleValue());
											prevValue = currentValue;
										}
									}
								}						
								else
								{
									datasetValues[allIndex][j] = values[notNullValuesIndex];
								}
							}
							notNullValuesIndex++;
							allIndex++;
						}
						else
						{
							if( GraphDataSeries.isPrimaryType(tSeries[i].getTypeMask()))
							{
								// We take away the fact there is a primary gds so that when we sort
								//  the values, we are able to still show load duration.
								primaryIndex = -1;
								primaryDset = -1;
							}
		
							for (int j = 0; j < keyArray.length; j++)
							{
								datasetValues[allIndex][j]= null;
							}
							allIndex++;
						}
					}
				}
			}
			//Create a collection of series as the dataset.
//			com.jrefinery.data.XYSeriesCollection collection = new com.jrefinery.data.XYSeriesCollection();
			for ( int i = 0; i < datasetValues.length; i++)
			{
				com.jrefinery.data.XYSeries xySeries = new com.jrefinery.data.XYSeries(tSeries[i].getLabel());			
				for (int j = 0; j < datasetValues[i].length; j++)
				{
					if( datasetValues[i][j] != null)
						xySeries.add(categories[j], datasetValues[i][j].doubleValue());
				}
//				xySeries.setName(xySeries.getName() + updateSeriesNames(serie));
				dataset[datasetIndex].addSeries(xySeries);
//				collection.addSeries(xySeries);			
			}
		}

		//Sort values based on primary gds, if it exists.		
		sortValuesDescending(dataset, primaryDset, primaryIndex);

		return dataset;
//		return collection;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:46:02 AM)
	 * @param cModels FreeChartModel []
	 */
	
	public static com.jrefinery.data.DefaultCategoryDataset [] createVerticalCategoryDataSet(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;

		com.jrefinery.data.DefaultCategoryDataset[] dataset = new com.jrefinery.data.DefaultCategoryDataset[2];
		
		for( int datasetIndex = 0; datasetIndex < 2; datasetIndex++)
		{
			int count = 0;				
			for( int i = 0; i < tSeries.length; i++)
			{
				if(GraphDataSeries.isGraphType(tSeries[i].getTypeMask()))
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
			//When there is a null tSeries[i].getDataPairArray(), we have to ignore the i values interval of the tree.get(keyArray[j]).
			int notNullValuesIndex = 0;

			com.jrefinery.data.DefaultCategoryDataset tempDataset = new com.jrefinery.data.DefaultCategoryDataset();
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
				
				com.jrefinery.data.TimePeriod prevTimePeriod = null;
				if(GraphDataSeries.isGraphType(tSeries[i].getTypeMask()))
				{
					String serieKey = tSeries[i].getLabel().toString();
					//UNCOMMENT WITH MULTIPLE AXIS SUPPORT					
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{
						if( tSeries[i].getDataPairArray() != null)
						{
							for (int j = 0; j < keyArray.length; j++)
							{
								if( GraphDataSeries.isUsageType(tSeries[i].getTypeMask()))
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


	public static com.jrefinery.data.DefaultCategoryDataset [] createVerticalCategoryDataSet_LD(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;

		com.jrefinery.data.DefaultCategoryDataset[] dataset = new com.jrefinery.data.DefaultCategoryDataset[2];
		int primaryIndex = -1;
		int primaryDset = -1;
		for( int datasetIndex = 0; datasetIndex < 2; datasetIndex++)
		{
			int count = 0;
			
			for( int i = 0; i < tSeries.length; i++)
			{
				if(GraphDataSeries.isGraphType(tSeries[i].getTypeMask()))
				{
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{				
						if( GraphDataSeries.isPrimaryType(tSeries[i].getTypeMask()))
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
			//When there is a null tSeries[i].getDataPairArray(), we have to ignore the i values interval of the tree.get(keyArray[j]).
			int notNullValuesIndex = 0;

			com.jrefinery.data.DefaultCategoryDataset tempDataset = new com.jrefinery.data.DefaultCategoryDataset();
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
			
				com.jrefinery.data.TimePeriod prevTimePeriod = null;
				if(GraphDataSeries.isGraphType(tSeries[i].getTypeMask()))
				{
					String serieKey = tSeries[i].getLabel().toString();
					if( tSeries[i].getAxis().equals(axisChars[datasetIndex]))
					{
						if( tSeries[i].getDataPairArray() != null)
						{
							for (int j = 0; j < keyArray.length; j++)
							{
								if( GraphDataSeries.isUsageType(tSeries[i].getTypeMask()))
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
							if( GraphDataSeries.isPrimaryType(tSeries[i].getTypeMask()))
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
								tempDataset.addValue(value, serieKey, categoryVector.get(j).toString());
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
	private static void sortValuesDescending(com.jrefinery.data.DefaultCategoryDataset[] dSet, int primaryDset, int primaryIndex)
	{
		int maxIndex = 0;

		if( primaryIndex < 0 )	//No primary gds point!
		{
			for(int i = 0; i < 2; i++)
			{
//				com.jrefinery.data.DefaultCategoryDataset dSet = dataSet[i];
				
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
							Double nextVal = (Double)dSet[i].getValue(j, y+1);
							if( currentVal != null && nextVal != null)
							{
								if( currentVal.doubleValue() < nextVal.doubleValue())
								{
									//swap the values
									dSet[i].setValue(currentVal, rowKey, dSet[i].getColumnKey(y+1));
									dSet[i].setValue(nextVal, rowKey , dSet[i].getColumnKey(y));
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
						double currentVal = dSet[primaryDset].getValue(primaryIndex, y).doubleValue();
						double nextVal = dSet[primaryDset].getValue(primaryIndex, y+1).doubleValue();
						if( currentVal < nextVal)
						{
							dSet[primaryDset].setValue(currentVal, rowKey, dSet[primaryDset].getColumnKey(y+1));
							dSet[primaryDset].setValue(nextVal, rowKey, dSet[primaryDset].getColumnKey(y));

							//need to do all other series here.
							for(int a = 0; a < 2; a++)
							{
								for(int b = 0; b < dSet[a].getRowCount(); b++)
								{
									if( !(a==primaryDset && b == primaryIndex))	//not primary dSet/serie
									{
										Comparable rowKey2 = dSet[a].getRowKey(b);
										currentVal = dSet[a].getValue(b, y).doubleValue();
										nextVal = dSet[a].getValue(b, y+1).doubleValue();
										
										dSet[a].setValue(currentVal, rowKey2, dSet[a].getColumnKey(y+1));
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
	
	private static void sortValuesDescending(com.jrefinery.data.XYSeriesCollection[] dSet, int primaryDset, int primaryIndex)
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
								double currentVal = dSet[i].getSeries(j).getDataPair(y).getY().doubleValue();
								double nextVal = dSet[i].getSeries(j).getDataPair(y+1).getY().doubleValue();
								if( currentVal < nextVal)
								{
									//swap the values
									dSet[i].getSeries(j).getDataPair(y+1).setY(new Double(currentVal));
									dSet[i].getSeries(j).getDataPair(y).setY(new Double(nextVal));
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
						double currentVal = dSet[primaryDset].getSeries(primaryIndex).getDataPair(y).getY().doubleValue();
						double nextVal = dSet[primaryDset].getSeries(primaryIndex).getDataPair(y+1).getY().doubleValue();
						if( currentVal < nextVal)
						{
							dSet[primaryDset].getSeries(primaryIndex).getDataPair(y+1).setY(new Double(currentVal));
							dSet[primaryDset].getSeries(primaryIndex).getDataPair(y).setY(new Double(nextVal));

							//SORT ALL OTHER SERIES BASE ON PRIMARY
							for(int a = 0; a < 2; a++)
							{
								for(int b = 0; b < dSet[a].getSeriesCount(); b++)
								{
									if( !(a==primaryDset && b == primaryIndex))	//not primary dSet/serie
									{
										currentVal = dSet[a].getSeries(b).getDataPair(y).getY().doubleValue();
										nextVal = dSet[a].getSeries(b).getDataPair(y+1).getY().doubleValue();
										
										dSet[a].getSeries(b).getDataPair(y+1).setY(new Double(currentVal));
										dSet[a].getSeries(b).getDataPair(y).setY(new Double(nextVal));
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