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
/**
 * A quick and dirty implementation.
 */
public class YukonDataSetFactory 
{
    private static java.text.SimpleDateFormat LEGEND_FORMAT = new java.text.SimpleDateFormat("MMM dd");
    private static java.text.SimpleDateFormat CATEGORY_FORMAT = new java.text.SimpleDateFormat(" MMM dd, HH:mm ");
    
	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:46:02 AM)
	 * @param gModel com.cannontech.graph.model.GraphModel
	 */
	public static java.util.TreeMap buildTreeMap(TrendSerie [] tSeries, int length)
	{
		java.util.TreeMap tree = new java.util.TreeMap();

		int validIndex = 0;
		for( int i = 0; i < tSeries.length; i++ )
		{
			TrendSerie serie = tSeries[i];			
			if(GraphDataSeries.isValidIntervalType( serie.getTypeMask()))
			{
				if( serie.getDataPairArray() != null)
				{
			 		long[] timeStamp = new long[serie.getDataPairArray().length];
			 		double[] values = new double[serie.getDataPairArray().length];
					for (int x = 0; x < serie.getDataPairArray().length; x++)
					{
						com.jrefinery.data.TimeSeriesDataPair dp = serie.getDataPairArray(x);						
						timeStamp[x] = dp.getPeriod().getStart();
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
		return tree;
	}

	public static com.jrefinery.data.TimeSeriesCollection createBasicDataSet(TrendSerie [] tSeries ) 
	{
		if( tSeries == null)
			return null;

		com.jrefinery.data.TimeSeriesCollection dSet = new com.jrefinery.data.TimeSeriesCollection();

		for ( int i = 0; i < tSeries.length; i++)
		{
			TrendSerie serie = tSeries[i];
			if( serie != null)
			{
				if(GraphDataSeries.isValidIntervalType( serie.getTypeMask()))
				{
					com.jrefinery.data.BasicTimeSeries series = 
						new com.jrefinery.data.BasicTimeSeries(serie.getLabel(), com.jrefinery.data.Second.class);
								
					if( serie.getDataPairArray() != null)
					{
						for (int j = 0; j < serie.getDataPairArray().length; j++)
						{
							com.jrefinery.data.TimeSeriesDataPair dp = (com.jrefinery.data.TimeSeriesDataPair)serie.getDataPairArray(j);
							try
							{
								series.add(dp);
							}
							catch(com.jrefinery.data.SeriesException se)
							{
								com.cannontech.clientutils.CTILogger.info("Series ["+i+"] Exception:  PERIOD = " + new java.util.Date(dp.getPeriod().getStart()) + " VALUE = " + dp.getValue().doubleValue());
							}
						}
					}
					dSet.addSeries(series);
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
	public static com.jrefinery.data.XYSeriesCollection createLoadDurationDataSet(TrendSerie[] tSeries, Integer peakPointId)
	{
		if( tSeries == null)
			return null;

		//Valid series are those that have type = graph.  tSeries has all types of series,
		//  therefore we need to weed out those we don't want in the graph.
		int peakPtIndex = -1;//getPeakPointIndex(tSeries, peakPointId);
			
		int validSeriesLength = 0;//getValidSeriesLength(tSeries);
		for( int i = 0; i < tSeries.length; i++)
		{
			if(GraphDataSeries.isValidIntervalType(tSeries[i].getTypeMask()))
			{
				if( tSeries[i].getPointId().equals(peakPointId))
				{	//find the 'graph' point representing the peak point, if it exists!
					peakPtIndex = validSeriesLength;
				}
				validSeriesLength++;
			}
		}

		java.util.TreeMap tree = buildTreeMap(tSeries, validSeriesLength);
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
		Double[][] datasetValues = new Double[validSeriesLength][];

		//This index holder is needed parrallel to i.
		//When there is a null tSeries[i].getDataPairArray(), we have to ignore the i values interval 
		//of the tree.get(keyArray[j]).  AKA...the i value can't be incremented, But because i is the 
		//for loop index of the series, we need another representation of it, hence notNullValuesIndex.
		int notNullValuesIndex = 0;
		int allIndex = 0;
		for (int i = 0; i < tSeries.length; i++)
		{
			if(GraphDataSeries.isValidIntervalType(tSeries[i].getTypeMask()))
			{
				datasetValues[allIndex] = new Double[keyArray.length];
				if( tSeries[i].getDataPairArray() != null)
				{
					for (int j = 0; j < keyArray.length; j++)
					{
						Double[] values = (Double[])tree.get(keyArray[j]);
						datasetValues[allIndex][j] = values[notNullValuesIndex];
					}
					notNullValuesIndex++;
					allIndex++;
				}
				else
				{
					if( tSeries[i].getPointId().equals(peakPointId))
					{
						// We take away the fact there is a peak point so that when we sort
						//  the values, we are able to still show load duration.
						peakPtIndex = -1;
					}

					for (int j = 0; j < keyArray.length; j++)
					{
						datasetValues[allIndex][j]= null;
					}
					allIndex++;
				}
			}
		}

		//Sort values based on peak point if it exists.		
		sortValuesDescending(datasetValues, peakPtIndex);
		
		//Create a collection of series as the dataset.
		com.jrefinery.data.XYSeriesCollection collection = new com.jrefinery.data.XYSeriesCollection();
		for ( int i = 0; i < datasetValues.length; i++)
		{
			com.jrefinery.data.XYSeries xySeries = new com.jrefinery.data.XYSeries(tSeries[i].getLabel());			
			for (int j = 0; j < datasetValues[i].length; j++)
			{
				if( datasetValues[i][j] != null)
					xySeries.add(categories[j], datasetValues[i][j].doubleValue());
			}
			collection.addSeries(xySeries);			
		}
		return collection;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:46:02 AM)
	 * @param cModels FreeChartModel []
	 */
	
	public static com.jrefinery.data.DefaultCategoryDataset createVerticalCategoryDataSet(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;

		java.util.Vector tNamesVector = new java.util.Vector(tSeries.length);//capacity is best guess right now.
		int validSeriesLength = 0;
		
		for( int i = 0; i < tSeries.length; i++)
		{
			if(GraphDataSeries.isValidIntervalType(tSeries[i].getTypeMask()))
			{
				tNamesVector.add(tSeries[i].getLabel().toString());
				validSeriesLength++;
			}
		}

		//set the series names up, excluding any not included in the buildTreeMap return.
		String[] seriesNames = new String[tNamesVector.size()];		
		tNamesVector.toArray(seriesNames);
	
		java.util.TreeMap tree = buildTreeMap(tSeries, validSeriesLength);
		if( tree == null)
			return null;

		// Get the keySet (timestamps) in a useable array structure.
		java.util.Set keySet = tree.keySet();
		Long[] keyArray = new Long[keySet.size()];
		keySet.toArray(keyArray);
		
		// Create the category list (of timestamps).
		String[] categoryList = new String[keyArray.length];
		for (int i = 0; i < keyArray.length; i++)
		{
			Long ts = keyArray[i];
			categoryList[i] = CATEGORY_FORMAT.format(new java.util.Date(ts.longValue()));
		}
		
		// Set series names for each point.
		Double[][] datasetValues = new Double[validSeriesLength ][];
		
		//This index holder is needed parrallel to i.
		//When there is a null tSeries[i].getDataPairArray(), we have to ignore the i values interval 
		//of the tree.get(keyArray[j]).  AKA...the i value can't be incremented, But because i is the 
		//for loop index of the series, we need another representation of it, hence notNullValuesIndex.
		int notNullValuesIndex = 0;
		int allIndex = 0;
		for (int i = 0; i < tSeries.length; i++)
		{
			if(GraphDataSeries.isValidIntervalType(tSeries[i].getTypeMask()))
			{
				datasetValues[allIndex] = new Double[keyArray.length];
				if( tSeries[i].getDataPairArray() != null)
				{
					for (int j = 0; j < keyArray.length; j++)
					{
						Double[] values = (Double[])tree.get(keyArray[j]);
						datasetValues[allIndex][j] = values[notNullValuesIndex];
					}
					notNullValuesIndex++;
					allIndex++;
				}
				else
				{
					for (int j = 0; j < keyArray.length; j++)
					{
						datasetValues[allIndex][j]= null;
					}
					allIndex++;
				}
			}
		}

//		com.jrefinery.data.DefaultCategoryDataset dataset = new com.jrefinery.data.DefaultCategoryDataset(getSeriesNames, getCategoryList(), datasetValues);	
		return (new com.jrefinery.data.DefaultCategoryDataset(seriesNames, categoryList, datasetValues));
	}


	public static com.jrefinery.data.DefaultCategoryDataset createVerticalCategoryDataSet_LD(TrendSerie[] tSeries, Integer peakPointId)
	{
		if( tSeries == null)
			return null;

		java.util.Vector tNamesVector = new java.util.Vector(tSeries.length);//capacity is best guess right now.
		int validSeriesLength = 0;
		int peakPtIndex = -1;
		
		for( int i = 0; i < tSeries.length; i++)
		{
			if(GraphDataSeries.isValidIntervalType(tSeries[i].getTypeMask()))
			{
				if( tSeries[i].getPointId().equals(peakPointId))
				{	//find the 'graph' point representing the peak point, if it exists!
					peakPtIndex = validSeriesLength;
				}

				tNamesVector.add(tSeries[i].getLabel().toString());
				validSeriesLength++;
			}
		}

		//set the series names up, excluding any not included in the buildTreeMap return.		
		String[] seriesNames = new String[tNamesVector.size()];		
		tNamesVector.toArray(seriesNames);
	
		java.util.TreeMap tree = buildTreeMap(tSeries, validSeriesLength);
		if( tree == null)
			return null;

		// Get the keySet (timestamps) in a useable array structure.
		java.util.Set keySet = tree.keySet();
		Long[] keyArray = new Long[keySet.size()];
		keySet.toArray(keyArray);
		
		// Create the category list (of timestamps).
		String[] categoryList = new String[keyArray.length];
		for (int i = 0; i < keyArray.length; i++)
		{
			Long ts = keyArray[i];
			categoryList[i] = CATEGORY_FORMAT.format(new java.util.Date(ts.longValue()));
		}
		
		// Set series names for each point.
		Double[][] datasetValues = new Double[validSeriesLength ][];
		
		//This index holder is needed parrallel to i.
		//When there is a null tSeries[i].getDataPairArray(), we have to ignore the i values interval 
		//of the tree.get(keyArray[j]).  AKA...the i value can't be incremented, But because i is the 
		//for loop index of the series, we need another representation of it, hence notNullValuesIndex.
		int notNullValuesIndex = 0;
		int allIndex = 0;
		for (int i = 0; i < tSeries.length; i++)
		{
			if(GraphDataSeries.isValidIntervalType(tSeries[i].getTypeMask()))
			{
				datasetValues[allIndex] = new Double[keyArray.length];
				if( tSeries[i].getDataPairArray() != null)
				{
					for (int j = 0; j < keyArray.length; j++)
					{
						Double[] values = (Double[])tree.get(keyArray[j]);
						datasetValues[allIndex][j] = values[notNullValuesIndex];
					}
					notNullValuesIndex++;
					allIndex++;
				}
				else
				{
					if( tSeries[i].getPointId().equals(peakPointId))
					{
						// We take away the fact there is a peak point so that when we sort
						//  the values, we are able to still show load duration.
						peakPtIndex = -1;
					}
						
					for (int j = 0; j < keyArray.length; j++)
					{
						datasetValues[allIndex][j]= null;
					}
					allIndex++;
				}
			}
		}

		sortValuesDescending(datasetValues, peakPtIndex);
		
		return (new com.jrefinery.data.DefaultCategoryDataset(seriesNames, categoryList, datasetValues));
	}
		
	/**
	 * Insert the method's description here.
	 * Creation date: (6/22/2001 4:25:53 PM)
	 * @param a java.util.ArrayList
	 * @param first int
	 * @param length int
	 */
	private static int findMaxIndex(Double[] values, int firstIndex)
	{
		int maxIndex = firstIndex;
		
		for(int x = firstIndex + 1; x < values.length; x++)
		{
			if( values[x] != null && values[maxIndex] != null)
			{
				if( values[x].doubleValue() > values[maxIndex].doubleValue())
					maxIndex = x;
			}
		}
		return maxIndex;	
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (7/2/2001 11:10:25 AM)
	 * @param yVals java.util.ArrayList
	 * @param xHrs java.util.ArrayList
	 * @param yQual java.util.ArrayList
	 */
	private static Double[][] sortValuesDescending(Double[][] dataSetValues, int peakPtIndex)
	{
		int maxIndex = 0;
		if( peakPtIndex < 0 )	//No peak point!
		{
			// Sort the values according to their value readings (descending)
			for (int i = 0; i < dataSetValues.length; i++)
			{
				if( dataSetValues[i] != null)
				{
					for (int j = 0; j < dataSetValues[i].length; j++)
					{
						maxIndex = findMaxIndex(dataSetValues[i], j);
						if( maxIndex != j)
						{
							Double tempDataSetValue = dataSetValues[i][j];
							dataSetValues[i][j] = dataSetValues[i][maxIndex];
							dataSetValues[i][maxIndex] = tempDataSetValue;
						}
					}
				}
			}
			
		}			
		else
		{
			// Have a peak point and need to sort peak values only!  Rest are coincidental on Peak Point!
			if( dataSetValues[peakPtIndex] != null)
			{
				for (int i = 0; i < dataSetValues[peakPtIndex].length; i++)
				{
					maxIndex = findMaxIndex(dataSetValues[peakPtIndex], i);
					if( maxIndex != i)
					{
						Double tempDataSetValue = dataSetValues[peakPtIndex][i];
						dataSetValues[peakPtIndex][i] = dataSetValues[peakPtIndex][maxIndex];
						dataSetValues[peakPtIndex][maxIndex] = tempDataSetValue;
						
						for ( int x = 0; x < dataSetValues.length; x++)
						{
							// For all other points, sort according to the peak values' sorting.
							if (x != peakPtIndex)
							{
								tempDataSetValue = dataSetValues[x][i];
								dataSetValues[x][i] = dataSetValues[x][maxIndex];
								dataSetValues[x][maxIndex] = tempDataSetValue;
							}
						}						
					}
				}
			}
		}
		return dataSetValues;
	}
}