package com.cannontech.graph.model;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */

/**
 * A quick and dirty implementation.
 */
public class YukonDataSetFactory implements com.cannontech.graph.GraphDataFormats, TrendModelType
{
    private static java.text.SimpleDateFormat LEGEND_FORMAT = new java.text.SimpleDateFormat("MMM dd");
    private static java.text.SimpleDateFormat CATEGORY_FORMAT = new java.text.SimpleDateFormat(" MMM dd, HH:mm ");
    

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:46:02 AM)
	 * @param gModel com.cannontech.graph.model.GraphModel
	 */
	public static java.util.TreeMap buildTreeMap(TrendSerie [] tSeries, int validSeriesSize)
	{
		java.util.TreeMap tree = new java.util.TreeMap();

		int validIndex = 0;
		for( int i = 0; i < tSeries.length; i++ )
		{
			TrendSerie serie = tSeries[i];			
			if(( serie.getTypeMask() & com.cannontech.database.db.graph.GraphDataSeries.VALID_INTERVAL_MASK) == serie.getTypeMask())
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
					 		objectValues = new Double[ validSeriesSize ];
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
				if(( serie.getTypeMask() & com.cannontech.database.db.graph.GraphDataSeries.VALID_INTERVAL_MASK) == serie.getTypeMask())
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
								com.cannontech.clientutils.CTILogger.info("Series Exception:  PERIOD = " + new java.util.Date(dp.getPeriod().getStart()));
							}
						}
					}
//					series.setName(serie.getLabel() + "          Load Factor: " + serie.getLoadFactor()+"/n          Min: " + serie.getMinimumValue() + "\n          Max: " + serie.getMaximumValue());
					dSet.addSeries(series);
				}
			}
		}
		return dSet;
	}
	
	/**
     * Creates a horizontally combined chart.
     */
/*    public JFreeChart createHorizontallyCombinedChart() {

        // create a default chart based on some sample data...
        String title = this.resources.getString("combined.horizontal.title");
        String subtitleStr = this.resources.getString("combined.horizontal.subtitle");
        String[] domains = this.resources.getStringArray("combined.horizontal.domains");
        String range = this.resources.getString("combined.horizontal.range");

        // calculate Time Series and Moving Average Dataset
        MovingAveragePlotFitAlgorithm mavg = new MovingAveragePlotFitAlgorithm();
        mavg.setPeriod(30);
        PlotFit pf = new PlotFit(DemoDatasetFactory.createTimeSeriesCollection2(), mavg);
        XYDataset tempDataset = pf.getFit();

        // create master dataset
        CombinedDataset data = new CombinedDataset();
        data.add(tempDataset);                // time series + MA

        // test SubSeriesDataset and CombinedDataset operations

        // decompose data into its two dataset series
        XYDataset series0 = new SubSeriesDataset(data, 0);
        XYDataset series1 = new SubSeriesDataset(data, 1);

        JFreeChart chart = null;

        // make a common vertical axis for all the sub-plots
        NumberAxis valueAxis = new VerticalNumberAxis(range);
        valueAxis.setAutoRangeIncludesZero(false);  // override default
        valueAxis.setCrosshairVisible(false);

        // make a horizontally combined plot
        CombinedXYPlot multiPlot = new CombinedXYPlot(valueAxis, CombinedXYPlot.HORIZONTAL);

        int[] weight = { 1, 1, 1 }; // control horizontal space assigned to each subplot

        // add subplot 1...
        XYPlot subplot1 = new XYPlot(series0, new HorizontalDateAxis("Date"), null);
        multiPlot.add(subplot1, weight[0]);

        // add subplot 2...
        XYPlot subplot2 = new XYPlot(data, new HorizontalDateAxis("Date"), null);
        multiPlot.add(subplot2, weight[1]);

        // add subplot 3...
        XYPlot subplot3 = new XYPlot(series0, new HorizontalDateAxis("Date"), null, new VerticalXYBarRenderer(0.20));
        //chartToCombine = ChartFactory.createCombinableVerticalXYBarChart(timeAxis[2], valueAxis, series0);
        multiPlot.add(subplot3, weight[2]);

        // call this method after all sub-plots have been added
        //combinedPlot.adjustPlots();

        // now make tht top level JFreeChart
        chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, multiPlot, true);

        // then customise it a little...
        TextTitle subtitle = new TextTitle(subtitleStr, new Font("SansSerif", Font.BOLD, 12));
        chart.addTitle(subtitle);
        chart.setBackgroundPaint(new GradientPaint(0, 0, Color.white,0, 1000, Color.blue));
        return chart;

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
		int peakPtIndex = -1;
			
		int validSeriesLength = 0;
		for( int i = 0; i < tSeries.length; i++)
		{
			if(( tSeries[i].getTypeMask() & com.cannontech.database.db.graph.GraphDataSeries.VALID_INTERVAL_MASK) == tSeries[i].getTypeMask())			
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
		for (int i = 0; i < validSeriesLength; i++)
		{
			datasetValues[i] = new Double[keyArray.length];
			for (int j = 0; j < keyArray.length; j++)
			{
				Double[] values = (Double[])tree.get(keyArray[j]);
				datasetValues[i][j] = values[i];
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



	public static com.jrefinery.data.TimeSeriesCollection createMultipleDaysDataSet(TrendSerie[] tSeries,  java.util.Date startDate, java.util.Date compareStart)
	{
		if( tSeries == null)
			return null;
		com.jrefinery.data.TimeSeriesCollection dSet = new com.jrefinery.data.TimeSeriesCollection();
		
		long TRANSLATE_TIME = 86400000;
		TRANSLATE_TIME = (startDate.getTime() - compareStart.getTime());
		System.out.println("StartDate = " + startDate + "  CompareStart = " + compareStart);	
		for ( int i = 0; i < tSeries.length; i++)
		{
			TrendSerie serie = tSeries[i];
			if( serie != null)
			{
				boolean firstOne = true;				
				if(( serie.getTypeMask() & com.cannontech.database.db.graph.GraphDataSeries.VALID_INTERVAL_MASK) == serie.getTypeMask())
				{
					com.jrefinery.data.BasicTimeSeries timeSeries = null;// new com.jrefinery.data.BasicTimeSeries(serie.getLabel()+ " - " + LEGEND_FORMAT.format(new java.util.Date(startDate.getTime() -1000)) + " -No Data", com.jrefinery.data.Second.class);

					if( serie.getDataPairArray() != null)
					{
						java.util.Date compareToDate = startDate;
						Number[] valArray =  new Number[serie.getDataPairArray().length];

						for (int j = 0; j < serie.getDataPairArray().length; j++)
						{
							com.jrefinery.data.TimeSeriesDataPair dp = (com.jrefinery.data.TimeSeriesDataPair)serie.getDataPairArray(j);
							java.util.Date dpDate = new java.util.Date(dp.getPeriod().getStart());
							
							try
							{
								if( dp.getPeriod().getStart() <= (compareToDate.getTime()))
								{
									com.jrefinery.data.TimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(dp.getPeriod().getStart() + TRANSLATE_TIME));
									com.jrefinery.data.TimeSeriesDataPair tempDP = new com.jrefinery.data.TimeSeriesDataPair(tp, dp.getValue());
									if( timeSeries == null)
									{
										timeSeries = new com.jrefinery.data.BasicTimeSeries(serie.getLabel()+ " - " + LEGEND_FORMAT.format(new java.util.Date(compareStart.getTime())), com.jrefinery.data.Second.class);
									}		
									timeSeries.add(tempDP);
								}
								else		
								{
									if( firstOne)
									{
										firstOne = false;

										if( timeSeries == null)	//sets up a series if there is no yesterday available.
										{
											//subtract an extra 1000 (sec) so we can hit yesterday, otherwise we get the first second of today.
											timeSeries = new com.jrefinery.data.BasicTimeSeries(serie.getLabel() + " - " + LEGEND_FORMAT.format(new java.util.Date(compareStart.getTime()))+ " -No Data", com.jrefinery.data.Second.class);
										}

										dSet.addSeries(timeSeries);
										timeSeries = new com.jrefinery.data.BasicTimeSeries(serie.getLabel() + " - " + LEGEND_FORMAT.format(new java.util.Date(startDate.getTime() )), com.jrefinery.data.Second.class);
									}
									timeSeries.add(dp);
								}
							}
							catch(com.jrefinery.data.SeriesException se)
							{
								com.cannontech.clientutils.CTILogger.info(" PERIOD = " + new java.util.Date(dp.getPeriod().getStart()));
							}
							valArray[j] = dp.getValue();
						}
					}

					if( timeSeries == null)
					{
						timeSeries = new com.jrefinery.data.BasicTimeSeries(serie.getLabel()+ " - " + LEGEND_FORMAT.format(new java.util.Date(startDate.getTime())), com.jrefinery.data.Second.class);
					}		

					dSet.addSeries(timeSeries);
					if( firstOne )	
					{
						//Only had data for one day, therefore, we have null data on the other days
						// and need to make sure we have (2) data sets for each serie.
						dSet.addSeries( new com.jrefinery.data.BasicTimeSeries(serie.getLabel() + " - " + LEGEND_FORMAT.format(new java.util.Date(startDate.getTime() )) + " -No Data" , com.jrefinery.data.Second.class));
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
	
	public static com.jrefinery.data.DefaultCategoryDataset createVerticalCategoryDataSet(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;

		java.util.Vector tNamesVector = new java.util.Vector(tSeries.length);//capacity is best guess right now.
		int validSeriesLength = 0;
		for( int i = 0; i < tSeries.length; i++)
		{
			if(( tSeries[i].getTypeMask() & com.cannontech.database.db.graph.GraphDataSeries.VALID_INTERVAL_MASK) == tSeries[i].getTypeMask())
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
		for (int i = 0; i < validSeriesLength; i++)
		{
			datasetValues[i] = new Double[keyArray.length];
			for (int j = 0; j < keyArray.length; j++)
			{
				Double[] values = (Double[])tree.get(keyArray[j]);
				datasetValues[i][j] = values[i];
			}
		}

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
				for (int j = 0; j < dataSetValues[peakPtIndex].length; j++)
				{
					maxIndex = findMaxIndex(dataSetValues[peakPtIndex], j);
					if( maxIndex != j)
					{
						Double tempDataSetValue = dataSetValues[peakPtIndex][j];
						dataSetValues[peakPtIndex][j] = dataSetValues[peakPtIndex][maxIndex];
						dataSetValues[peakPtIndex][maxIndex] = tempDataSetValue;
						
						for ( int x = 0; x < dataSetValues.length; x++)
						{
							// For all other points, sort according to the peak values' sorting.
							if (x != peakPtIndex)
							{
								tempDataSetValue = dataSetValues[x][j];
								dataSetValues[x][j] = dataSetValues[x][maxIndex];
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