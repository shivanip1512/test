package com.cannontech.graph.model;

/**
 * @author snebben
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.AbstractDataset;
import org.jfree.data.general.Series;
import org.jfree.data.general.SeriesException;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.cannontech.common.util.Pair;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.graph.ResetPeaksPanel;
/**
 * A quick and dirty implementation.
 */
public class YukonDataSetFactory implements com.cannontech.graph.GraphDefines
{
	private static int options = 0x0000;
	
	public static Object[] createDataset(TrendSerie [] trendSeries, int options_, boolean isCategory, int type_ )
	{
		options = options_;
		if(isCategory)// type_ == GraphRenderers.BAR_3D)	//type_ == GraphRenderers.BAR || 
		{
			if( (options_ & GraphRenderers.LOAD_DURATION_MASK) == GraphRenderers.LOAD_DURATION_MASK)			
				return YukonDataSetFactory.createVerticalCategoryDataSet_LD(trendSeries, type_);
			else
				return YukonDataSetFactory.createVerticalCategoryDataSet(trendSeries, type_);
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
	public static java.util.TreeMap buildTreeMap(TrendSerie [] tSeries)//, int length, int datasetIndex)
	{
		java.util.TreeMap tree = new java.util.TreeMap();

		int validIndex = 0;
		for( int i = 0; i < tSeries.length; i++ )
		{
			TrendSerie serie = tSeries[i];			
			if(GDSTypesFuncs.isGraphType( serie.getTypeMask()))
			{
				if( serie.getTimeAndValueVector() != null)
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
							objectValues = new Double[ tSeries.length];
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

	private static Class getResolutionClass(long resolution)
	{
		if( resolution < 1000 )
			return Millisecond.class;
		else if (resolution < 60000)
			return Second.class;
		else if( resolution < 300000)
			return Minute.class;
		else	//default?
			return Millisecond.class;
	}
	private static RegularTimePeriod getRegularTimePeriod(long resolution, long time)
	{
		if( resolution < 1000 )
			return new Millisecond(new Date( time));		
		else if (resolution < 60000)
			return new Second(new Date( time));
		else if( resolution < 300000)
			return new Minute(new Date( time));
		else	//default?
			return new Millisecond(new java.util.Date( time));
	}
	
	public static TimeSeries [] createBasicDataSet(TrendSerie [] tSeries ) 
	{
		if( tSeries == null)
			return null;

		TimeSeries [] timeSeriesArray = new TimeSeries[tSeries.length];
		for ( int i = 0; i < tSeries.length; i++)
		{
			Double prevValue = null;
			TrendSerie serie = tSeries[i];
			if( serie != null)
			{
				TimeSeries timeSeries = new TimeSeries(serie.getLabel(), getResolutionClass(serie.resolution));

				if( serie.getTimeAndValueVector() != null)
				{
					for (int j = 0; j < serie.getTimeAndValueVector().size(); j++)
					{
						Pair timeAndValue = (Pair)serie.getTimeAndValueVector().get(j);
						Double value = serie.getAdjustedValue((Double)timeAndValue.getSecond());//Adjusted value takes GraphMultiplier into account!!!
						try{
							if( GDSTypesFuncs.isUsageType(serie.getTypeMask()))
							{
								if( prevValue == null)
									prevValue = value;

								else
								{
									Double currentValue = value;
									if( currentValue != null && prevValue != null)
									{
										RegularTimePeriod rtp = getRegularTimePeriod(serie.resolution, ((Long)timeAndValue.getFirst()).longValue());
										TimeSeriesDataItem tempDP = new TimeSeriesDataItem(rtp, new Double(currentValue.doubleValue() - prevValue.doubleValue()));
										prevValue = currentValue;
										timeSeries.add(tempDP);
									}
								}
							}
							else
							{
								RegularTimePeriod rtp = getRegularTimePeriod(serie.resolution, ((Long)timeAndValue.getFirst()).longValue());
								TimeSeriesDataItem item = new TimeSeriesDataItem(rtp, value);
								timeSeries.add(item);
							}
						}
						catch(SeriesException se)
						{
							com.cannontech.clientutils.CTILogger.info("Serie ["+serie.getLabel()+"] Exception:  PERIOD = " + new Date( ((Long)timeAndValue.getFirst()).longValue())  + " VALUE = " + ((Double)timeAndValue.getSecond()).doubleValue());
						}
					}
				}
				//Update the serie's name for legend display.
				//FIND A BETTER WAY TO UPDATE THE NAME!!! (FOR LEGEND PURPOSES)
				timeSeries.setKey(timeSeries.getKey().toString() + updateSeriesNames(serie));
				timeSeriesArray[i] = timeSeries;
			}
		}
		return timeSeriesArray;
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:46:02 AM)
	 * @param cModels FreeChartModel []
	 */
	public static XYSeries [] createLoadDurationDataSet(TrendSerie[] tSeries)
	{
		if( tSeries == null)
			return null;
		XYSeries [] xySeriesArray = new XYSeries[tSeries.length];

		int primaryDset = -1;
		//Valid series are those that have type = graph.  tSeries has all types of series,
		//  therefore we need to weed out those we don't want in the graph.
		java.util.TreeMap tree = buildTreeMap(tSeries);
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
				if( GDSTypesFuncs.isPrimaryType(tSeries[i].getTypeMask()))
				{	//find the primary gds, if it exists!
					primaryDset = i;
				}
									
				XYSeries xySeries = new XYSeries(serie.getLabel());

				Double value = null;
				Double prevValue = null;				
				if( serie.getTimeAndValueVector() != null)
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
						primaryDset = -1;
					}

					for (int j = 0; j < keyArray.length; j++)
					{
//								if(!(primaryDset==datasetIndex && primaryIndex==allIndex))								
							xySeries.add(categories[j],null);
					}
					allIndex++;
				}
				xySeries.setKey(xySeries.getKey().toString() + updateSeriesNames(serie));
				xySeriesArray[i] = xySeries;
			}
		}

		//Sort values based on primary gds, if it exists.		
		sortValuesDescending(xySeriesArray, primaryDset);
		return xySeriesArray;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (8/2/2001 10:46:02 AM)
	 * @param cModels FreeChartModel []
	 */
	public static DefaultCategoryDataset [] createVerticalCategoryDataSet(TrendSerie[] tSeries, int viewType)
	{
		if( tSeries == null)
			return null;

		Vector datasetVector = new Vector(tSeries.length);
		
		SimpleDateFormat catFormat = CATEGORY_FORMAT_MMM_dd_HH_mm_ss_SSS;
		long maxResolution = 0;				
		for( int i = 0; i < tSeries.length; i++)
		{
			if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
			{
				if (tSeries[i].resolution > maxResolution)
					maxResolution = tSeries[i].resolution;
			}
		}
		if( maxResolution == 1000)
			catFormat = CATEGORY_FORMAT_MMM_dd_HH_mm_ss;
		else if (maxResolution > 1000)
			catFormat = CATEGORY_FORMAT_MMM_dd_HH_mm;
		else // < 1000
			catFormat = CATEGORY_FORMAT_MMM_dd_HH_mm_ss_SSS;

		java.util.TreeMap tree = buildTreeMap(tSeries);//, count, datasetIndex);
		if( tree == null)
			return null;		

		// Get the keySet (timestamps) in a useable array structure.
		java.util.Set keySet = tree.keySet();
		Long[] keyArray = new Long[keySet.size()];
		keySet.toArray(keyArray);
		
		//This index holder is needed parrallel to i.
		//When there is a null tSeries[i].getDataItemArray(), we have to ignore the i values interval of the tree.get(keyArray[j]).
		int notNullValuesIndex = 0;

		java.util.Vector categoryVector = new java.util.Vector();
		for (int j = 0; j < keyArray.length; j++)
		{
			Long ts = keyArray[j];
			categoryVector.add(catFormat.format(new java.util.Date(ts.longValue())));
		}

		// Load all serie with a "Bar" type renderer into the first array location of the dataset.
		DefaultCategoryDataset tempDataset = null;
		for (int i = 0; i < tSeries.length; i++)
		{
			if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
			{
			
				if( !(GDSTypesFuncs.isMarkerType(tSeries[i].getTypeMask()) && tSeries[i].getPointId().intValue() == PointTypes.SYS_PID_THRESHOLD ) &&
								 ( (viewType == GraphRenderers.DEFAULT && GraphRenderers.useCategoryPlot(tSeries[i].getRenderer()) )
									|| GraphRenderers.useCategoryPlot(viewType) ) )
				{
					if (tempDataset == null)
						tempDataset = new DefaultCategoryDataset();
					
					Double prevValue = null;
					Double value = null;
		
					TimeSeriesDataItem prevTimePeriod = null;
					String serieKey = tSeries[i].getLabel().toString() + updateSeriesNames(tSeries[i]);
					{
						if( tSeries[i].getTimeAndValueVector() != null)
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
				else
				{
					if( tSeries[i].getTimeAndValueVector() != null)
						notNullValuesIndex++;
				}
			}
		}
		if( tempDataset != null)
			datasetVector.add(tempDataset);

		notNullValuesIndex = 0;	//reset the not null values counter
		//load all non bar type series data
		for (int i = 0; i < tSeries.length; i++)
		{
			if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
			{
				if ( (GDSTypesFuncs.isMarkerType(tSeries[i].getTypeMask()) && tSeries[i].getPointId().intValue() == PointTypes.SYS_PID_THRESHOLD ) ||
					! ( (viewType == GraphRenderers.DEFAULT && GraphRenderers.useCategoryPlot(tSeries[i].getRenderer()) ) 
					|| GraphRenderers.useCategoryPlot(viewType)))
				{					
					Double prevValue = null;
					Double value = null;
				
					tempDataset = new DefaultCategoryDataset();

					TimeSeriesDataItem prevTimePeriod = null;
					
					String serieKey = tSeries[i].getLabel().toString() + updateSeriesNames(tSeries[i]);
					if( tSeries[i].getTimeAndValueVector() != null)
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
					datasetVector.add(tempDataset);				
				}
				else
				{
					if( tSeries[i].getTimeAndValueVector() != null)
						notNullValuesIndex++;
				}
			}

		}
		DefaultCategoryDataset[] datasetArray = new DefaultCategoryDataset[datasetVector.size()];
		datasetVector.toArray(datasetArray);
		return datasetArray;
	}

	
	public static DefaultCategoryDataset [] createVerticalCategoryDataSet_LD(TrendSerie[] tSeries, int viewType)
	{
		if( tSeries == null)
			return null;

		Vector datasetVector = new Vector(tSeries.length);
		int primaryIndex = -1;
		int primaryDset = -1;
		
		SimpleDateFormat catFormat = CATEGORY_FORMAT_MMM_dd_HH_mm_ss_SSS;
		long maxResolution = 0;				
		for( int i = 0; i < tSeries.length; i++)
		{
			if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
			{
				if (tSeries[i].resolution > maxResolution)
					maxResolution = tSeries[i].resolution;
			}
		}
		if( maxResolution == 1000)
			catFormat = CATEGORY_FORMAT_MMM_dd_HH_mm_ss;
		else if (maxResolution > 1000)
			catFormat = CATEGORY_FORMAT_MMM_dd_HH_mm;
		else // < 1000
			catFormat = CATEGORY_FORMAT_MMM_dd_HH_mm_ss_SSS;
		

		java.util.TreeMap tree = buildTreeMap(tSeries);//, count, datasetIndex);
		if( tree == null)
			return null;		
		
		// Get the keySet (timestamps) in a useable array structure.
		java.util.Set keySet = tree.keySet();
		Long[] keyArray = new Long[keySet.size()];
		keySet.toArray(keyArray);
		
		//This index holder is needed parrallel to i.
		//When there is a null tSeries[i].getDataItemArray(), we have to ignore the i values interval of the tree.get(keyArray[j]).
		int notNullValuesIndex = 0;

		double[] categories = new double[keyArray.length];
		double categoryCount = keyArray.length -1;// scale is from 0 start point not 1
		for (int i = 0; i < keyArray.length; i++)
			categories[i] = ( i / categoryCount ) * 100;

		// Load all serie with a "Bar" type renderer into the first array location of the dataset.
		DefaultCategoryDataset tempDataset = null;
		int barCount = 0;
		int datasetCount = 0;
		
		//Load all of the bar type renderer series
		for (int i = 0; i < tSeries.length; i++)
		{
			if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
			{
				if( !(GDSTypesFuncs.isMarkerType(tSeries[i].getTypeMask()) && tSeries[i].getPointId().intValue() == PointTypes.SYS_PID_THRESHOLD ) &&
					 ( (viewType == GraphRenderers.DEFAULT && GraphRenderers.useCategoryPlot(tSeries[i].getRenderer()) )
						|| GraphRenderers.useCategoryPlot(viewType) ) )
				{
					if (tempDataset == null)
						tempDataset = new DefaultCategoryDataset();

					if( GDSTypesFuncs.isPrimaryType(tSeries[i].getTypeMask()))
					{	//find the primary gds, if it exists!
						primaryIndex = barCount;
						primaryDset = datasetCount;
					}					
					
					Double prevValue = null;
					Double value = null;
		
					TimeSeriesDataItem prevTimePeriod = null;
					String serieKey = tSeries[i].getLabel().toString() + updateSeriesNames(tSeries[i]);
					{
						if( tSeries[i].getTimeAndValueVector() != null)
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
							if( keyArray.length <= 0)
								tempDataset.addValue(value, serieKey, "");
							else
							{
								for (int j = 0; j < keyArray.length; j++)
								{
									tempDataset.addValue(value, serieKey, new Integer(new Double(categories[j]).intValue()));
								}
							}
						}
					}
					barCount++;
				}
				else
				{
					if( tSeries[i].getTimeAndValueVector() != null)
						notNullValuesIndex++;
				}
			}
		}
		if( tempDataset != null)
		{
			datasetVector.add(tempDataset);
			datasetCount++;
		}

		notNullValuesIndex = 0;	//reset the not null values counter
		//Load all non bar type series data.
		for (int i = 0; i < tSeries.length; i++)
		{
			if(GDSTypesFuncs.isGraphType(tSeries[i].getTypeMask()))
			{
				if ( (GDSTypesFuncs.isMarkerType(tSeries[i].getTypeMask()) && tSeries[i].getPointId().intValue() == PointTypes.SYS_PID_THRESHOLD ) ||
					! ( (viewType == GraphRenderers.DEFAULT && GraphRenderers.useCategoryPlot(tSeries[i].getRenderer()) ) 
					|| GraphRenderers.useCategoryPlot(viewType)))
				{
					if( GDSTypesFuncs.isPrimaryType(tSeries[i].getTypeMask()))
					{	//find the primary gds, if it exists!
						primaryIndex = 0;
						primaryDset = datasetCount;
					}
					
					Double prevValue = null;
					Double value = null;
				
					tempDataset = new DefaultCategoryDataset();

					TimeSeriesDataItem prevTimePeriod = null;
					
					String serieKey = tSeries[i].getLabel().toString() + updateSeriesNames(tSeries[i]);
					if( tSeries[i].getTimeAndValueVector() != null)
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
						if( keyArray.length <= 0)
							tempDataset.addValue(value, serieKey, "");
						else
						{
							for (int j = 0; j < keyArray.length; j++)
							{
								tempDataset.addValue(value, serieKey, new Integer(new Double(categories[j]).intValue()));
							}
						}
					}
					datasetVector.add(tempDataset);
					datasetCount++;				
				}
				else
				{
					if( tSeries[i].getTimeAndValueVector() != null)
						notNullValuesIndex++;
				}
			}

		}
		DefaultCategoryDataset[] datasetArray = new DefaultCategoryDataset[datasetVector.size()];
		datasetVector.toArray(datasetArray);

		sortValuesDescending(datasetArray, primaryDset, primaryIndex);
		return datasetArray;
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
			for(int i = 0; i < dSet.length; i++)
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
									for(int a = 0; a < dSet.length; a++)
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
	
	private static void sortValuesDescending(XYSeries[] dSet, int primaryDset)
	{
		int maxIndex = 0;

		if( primaryDset < 0 )	//No primary gds point!
		{
//			for(int i = 0; i < 2; i++)
			{
				// SORT EACH SERIES BASED ON ITS OWN VALUES (descending)
				for (int j = 0; j < dSet.length;j++)
				{
					if( dSet[j] != null)
					{
						//SORT Start for serie = j
						for(int x  = dSet[j].getItemCount() -1; x >=0; x--)
						{
							for (int y = 0; y < x; y++)
							{
								Double currentVal = (Double)dSet[j].getDataItem(y).getY();
								if( currentVal != null)
								{
									int tempy = y+1;
									Double nextVal = (Double)dSet[j].getDataItem(y+1).getY();
									while(nextVal == null && tempy < x )
									{
										tempy++;
										nextVal = (Double)dSet[j].getDataItem(tempy).getY();
									}
									if( nextVal!= null)
									{
										if( currentVal.doubleValue() < nextVal.doubleValue())
										{
											//swap the values
											dSet[j].getDataItem(tempy).setY(currentVal);
											dSet[j].getDataItem(y).setY(nextVal);
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
			if( dSet[primaryDset] != null)
			{
				//SORT
				for( int x = dSet[primaryDset].getItemCount() - 1; x >=0; x--)
				{
					for( int y = 0; y < x; y++)
					{
						Double currentVal_ = (Double)dSet[primaryDset].getDataItem(y).getY();
						if( currentVal_ != null)
						{
							int tempy = y+1;
							Double nextVal_ = (Double)dSet[primaryDset].getDataItem(y+1).getY();
							while(nextVal_ == null && tempy < x )
							{
								tempy++;
								nextVal_ = (Double)dSet[primaryDset].getDataItem(tempy).getY();
							}
							if( nextVal_!= null)
							{
								if( currentVal_.doubleValue() < nextVal_.doubleValue())
								{
									dSet[primaryDset].getDataItem(tempy).setY((Double)currentVal_);
									dSet[primaryDset].getDataItem(y).setY((Double)nextVal_);
	
									//SORT ALL OTHER SERIES BASE ON PRIMARY
									for(int a = 0; a < dSet.length; a++)
									{
										if( !(a==primaryDset ))	//not primary dSet/serie
										{
											if( dSet[a] != null)
											{
												if( dSet[a].getItemCount() > 0 && dSet[a].getItemCount() > tempy)
												{
													currentVal_ = (Double)dSet[a].getDataItem(y).getY();
													nextVal_ = (Double)dSet[a].getDataItem(tempy).getY();
													
													dSet[a].getDataItem(tempy).setY((Double)currentVal_);
													dSet[a].getDataItem(y).setY((Double)nextVal_);
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
		for(int a = 0; a < dSet.length; a++)
		{
			if( dSet[a] != null)
			{
				for (int c = 0; c < dSet[a].getItemCount()-1; c++)
				{
					if(dSet[a].getDataItem(c).getY() == null)
					{
						dSet[a].delete(c, c);
						c--;
					}
				}
			}
		}

	}
}