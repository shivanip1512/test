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
	public static java.util.TreeMap buildCategoryTreeMap(TrendSerie [] tSeries, int validSeriesSize)
	{
		java.util.TreeMap tree = new java.util.TreeMap();

		int validIndex = 0;
		for( int i = 0; i < tSeries.length; i++ )
		{
			if( tSeries[i].getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
			{
		   		com.jrefinery.data.TimeSeriesDataPair[] dp = tSeries[i].getDataPairArray();
		
				if( dp != null)
				{
			 		long[] timeStamp = new long[dp.length];
			 		double[] values = new double[dp.length];
					for (int x = 0; x < dp.length; x++)
					{
						timeStamp[x] = dp[x].getPeriod().getStart();
						values[x] = dp[x].getValue().doubleValue();
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
				if( serie.getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
				{
					com.jrefinery.data.BasicTimeSeries series = 
						new com.jrefinery.data.BasicTimeSeries(serie.getLabel(), com.jrefinery.data.Second.class);
								
					if( serie.getDataPairArray() != null)
					{
						for (int j = 0; j < serie.getDataPairArray().length; j++)
						{
							com.jrefinery.data.TimeSeriesDataPair dp = (com.jrefinery.data.TimeSeriesDataPair)serie.getDataPairArray()[j];
							try
							{
								series.add(dp);
							}
							catch(com.jrefinery.data.SeriesException se)
							{
								System.out.println("Series Exception:  PERIOD = " + new java.util.Date(dp.getPeriod().getStart()));
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
	public static com.jrefinery.data.DefaultCategoryDataset createLoadDurationDataSet(TrendSerie[] tSeries)
//	public static com.jrefinery.data.DefaultIntervalCategoryDataset createLoadDurationDataSet(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;

		//Valid series are those that have type = graph.  tSeries has all types of series,
		//  therefore we need to weed out those we don't want in the graph.
				
		java.util.Vector tNamesVector = new java.util.Vector(tSeries.length);//capacity is best guess right now.
		int validSeriesLength = 0;
		for( int i = 0; i < tSeries.length; i++)
		{
			if( tSeries[i].getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
			{
				tNamesVector.add(tSeries[i].getLabel().toString());
				validSeriesLength++;
			}
		}
		
		//set the series names up, excluding any not included in the buildTreeMap return.		
		String[] seriesNames = new String[tNamesVector.size()];		
		tNamesVector.toArray(seriesNames);
		
		java.util.TreeMap tree = buildCategoryTreeMap(tSeries, validSeriesLength);
		if( tree == null)
			return null;

		// Get the keySet (timestamps) in a useable array structure.
		java.util.Set keySet = tree.keySet();
		Long[] keyArray = new Long[keySet.size()];
		keySet.toArray(keyArray);
		
		// Create the category list (of timestamps).
		String[] categoryList = new String[keyArray.length];
		float categoryCount = keyArray.length -1;// scale is from 0 start point not 1
		for (int i = 0; i < keyArray.length; i++)
		{
			categoryList[i] = percentFormat.format((i/categoryCount));
			System.out.println(" Category "  + i + " = " + categoryList[i]);
		}
		
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
		sortValuesDescending(datasetValues, categoryList);
		return (new com.jrefinery.data.DefaultCategoryDataset(seriesNames,  categoryList, datasetValues));
	}

	public static com.jrefinery.data.TimeSeriesCollection createMinMaxDataSetSeries(TrendSerie [] tSeries) 
	{
		if( tSeries == null)
			return null;

		com.jrefinery.data.TimeSeriesCollection dSet = new com.jrefinery.data.TimeSeriesCollection();

		for ( int i = 0; i < tSeries.length; i++)
		{
			TrendSerie serie = tSeries[i];
			if( serie != null)
			{
				if( serie.getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
				{
					com.jrefinery.data.BasicTimeSeries series = 
						new com.jrefinery.data.BasicTimeSeries(("Min/Max " + serie.getLabel()), com.jrefinery.data.Second.class);
								
					if( serie.getDataPairArray() != null)
					{
						series.add(serie.getMaximumTSDataPair());
						series.add(serie.getMinimumTSDataPair());
					}
					dSet.addSeries(series);
				}
			}
		}
		return dSet;
	}
	




	public static com.jrefinery.data.TimeSeriesCollection createMultipleDaysDataSet(TrendSerie[] tSeries,  java.util.Date startDate, java.util.Date compareStart)
	{
		if( tSeries == null)
			return null;
		com.jrefinery.data.TimeSeriesCollection dSet = new com.jrefinery.data.TimeSeriesCollection();
		
		long TRANSLATE_TIME = 86400000;
		TRANSLATE_TIME = (startDate.getTime() - compareStart.getTime());
		
		for ( int i = 0; i < tSeries.length; i++)
		{
			TrendSerie serie = tSeries[i];
			if( serie != null)
			{
				boolean firstOne = true;				
				if( serie.getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
				{
					com.jrefinery.data.BasicTimeSeries series = null;// new com.jrefinery.data.BasicTimeSeries(serie.getLabel()+ " - " + LEGEND_FORMAT.format(new java.util.Date(startDate.getTime() -1000)) + " -No Data", com.jrefinery.data.Second.class);

					if( serie.getDataPairArray() != null)
					{
						java.util.Date compareToDate = startDate;
						Number[] valArray =  new Number[serie.getDataPairArray().length];

						for (int j = 0; j < serie.getDataPairArray().length; j++)
						{
							com.jrefinery.data.TimeSeriesDataPair dp = (com.jrefinery.data.TimeSeriesDataPair)serie.getDataPairArray()[j];
							java.util.Date dpDate = new java.util.Date(dp.getPeriod().getStart());
							
							try
							{
								if( dp.getPeriod().getStart() <= (compareToDate.getTime()))
								{
									com.jrefinery.data.TimePeriod tp = new com.jrefinery.data.Second(new java.util.Date(dp.getPeriod().getStart() + TRANSLATE_TIME));
									com.jrefinery.data.TimeSeriesDataPair tempDP = new com.jrefinery.data.TimeSeriesDataPair(tp, dp.getValue());
									if( series == null)
									{
										series = new com.jrefinery.data.BasicTimeSeries(serie.getLabel()+ " - " + LEGEND_FORMAT.format(new java.util.Date(compareStart.getTime())), com.jrefinery.data.Second.class);
									}		
									series.add(tempDP);
								}
								else		
								{
									if( firstOne)
									{
										firstOne = false;

										if( series == null)	//sets up a series if there is no yesterday available.
										{
											//subtract an extra 1000 (sec) so we can hit yesterday, otherwise we get the first second of today.
											series = new com.jrefinery.data.BasicTimeSeries(serie.getLabel() + " - " + LEGEND_FORMAT.format(new java.util.Date(compareStart.getTime()))+ " -No Data", com.jrefinery.data.Second.class);
										}

										dSet.addSeries(series);
										series = new com.jrefinery.data.BasicTimeSeries(serie.getLabel() + " - " + LEGEND_FORMAT.format(new java.util.Date(startDate.getTime() )), com.jrefinery.data.Second.class);
									}
									series.add(dp);
								}
							}
							catch(com.jrefinery.data.SeriesException se)
							{
								System.out.println(" PERIOD = " + new java.util.Date(dp.getPeriod().getStart()));
							}
							valArray[j] = dp.getValue();
						}
					}

					if( series == null)
					{
						series = new com.jrefinery.data.BasicTimeSeries(serie.getLabel()+ " - " + LEGEND_FORMAT.format(new java.util.Date(startDate.getTime())), com.jrefinery.data.Second.class);
					}		

					dSet.addSeries(series);
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
			if( tSeries[i].getType().equalsIgnoreCase("graph"))
			{
				tNamesVector.add(tSeries[i].getLabel().toString());
				validSeriesLength++;
			}
		}

		//set the series names up, excluding any not included in the buildTreeMap return.		
		String[] seriesNames = new String[tNamesVector.size()];		
		tNamesVector.toArray(seriesNames);
	
		java.util.TreeMap tree = buildCategoryTreeMap(tSeries, validSeriesLength);
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
	private static Double[][] sortValuesDescending(Double[][] dataSetValues, String[] categoryList)
	{
		// Sort the values according to the readings (descending)
		int maxIndex = 0;
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
		return dataSetValues;
	}
	}