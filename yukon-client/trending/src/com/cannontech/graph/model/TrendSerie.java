package com.cannontech.graph.model;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jfree.data.time.TimeSeriesDataItem;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.graph.GDSTypes;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.database.db.graph.GraphRenderers;

/**
 * @author snebben
 *
 * TrendSerie is a container for information about a data object that may be trended.
 * TrendModel uses TrendSerie(s) as the infromation of a data object to actually draw.
 */
public class TrendSerie
{
	//Contains values of org.jfree.data.time.TimeSeriesDataItem values.
	//TimeSeriesDataItem has a value and timestamp item.
	private HashMap dataItemsMap = null;
//	private TimeSeriesDataItem[] dataItemArray = null;
//	private AbstractDataset dataset = null;
	private Object dataSeries = null;	//THIS SHOULD BE SERIES or DEFAULTCATEGORYDATASET
	
	private Integer pointId;
	private String label = null;
	private Color color = Color.blue;
	private String deviceName = null;
	private Double multiplier = new Double(1.0);	//This is different then the point multiplier, this is a GDS
	private Character axis = new Character('L');
	public int typeMask = GDSTypes.BASIC_GRAPH_TYPE;
	public String moreData = CtiUtilities.STRING_NONE;
	public int renderer = GraphRenderers.LINE;
	// Flag for using graph multiplier
	public boolean useMultiplier = false;
	public long resolution = 1;

	private TimeSeriesDataItem minimumTSDataItem = null;
	private TimeSeriesDataItem maximumTSDataItem = null;

	// Load factor values computed for each point in the model, stored in an array that
	//  is in accordance to the order of the pointIds.
	private double areaOfSet = 0.0; //Load Factor, area under the curve
	private double maxArea = 0.0;  //Load Factor, total area (using max point value)

	// Number of decimal places each point has (from pointUnit table)
	private int decimalPlaces = 3;

	//contains dataItemArray.value(s).  (Readings)
	private double[] valuesArray = null;
	//contains dataItemArray.period(s). (Timestamps in millis).
	private long[] periodsArray = null;
	
	/**
	 * FreeChartModel constructor comment.
	 */
	public TrendSerie()
	{
		super();
	}
	/**
	 * @param gds
	 */
	public TrendSerie(GraphDataSeries gds)
	{
		super();
		setPointId(gds.getPointID());
		setColor(Colors.getColor(gds.getColor().intValue()));
		setLabel(gds.getLabel().toString());
		setAxis(gds.getAxis());
		setTypeMask(gds.getType().intValue());
		setMultiplier(gds.getMultiplier());
		setMoreData(gds.getMoreData());
	}

	/**
	 * @return
	 */
	public double getAreaOfSet()
	{
		if( areaOfSet == 0)
		{
			Iterator iter = getDataItemsMap().entrySet().iterator();
			while( iter.hasNext())
			{
				Map.Entry entry = (Map.Entry)iter.next();
				TimeSeriesDataItem dataItem = (TimeSeriesDataItem)entry.getValue();
				areaOfSet += dataItem.getValue().doubleValue();
			}
		}
		return areaOfSet;
	}

	/**
	 * Returns the axis value.
	 * @return java.lang.Character
	 */
	public Character getAxis()
	{
		return axis;
	}

	/**
	 * Returns the color value.
	 * @return java.awt.Color
	 */
	public Color getColor()
	{
		return color;
	}

	/**
	 * Returns the dataItemArray value.
	 * @return org.jfree.data.time.TimeSeriesDataItem[]
	 */
	/*public TimeSeriesDataItem[] getDataItemArray()
	{
		return dataItemArray;
	}*/
	
	public HashMap getDataItemsMap()
	{
		return dataItemsMap;
	}

	/**
	 * Returns the dataItemArray[item] value.
	 * @return org.jfree.data.time.TimeSeriesDataItem
	 */
	/*public TimeSeriesDataItem getDataItemArray(int item)
	{
		if( getUseMultiplier())
		{
			RegularTimePeriod tp = dataItemArray[item].getPeriod();
			Number val = new Double(dataItemArray[item].getValue().doubleValue() * getMultiplier().doubleValue());
			TimeSeriesDataItem multDP = new TimeSeriesDataItem(tp, val);
			return (multDP);
		}
		return dataItemArray[item];
	}*/
	
	/**
	 * Returns the decimalPlaces value.
	 * @return int
	 */
	public int getDecimalPlaces()
	{
		return decimalPlaces;
	}

	/**
	 * Returns the deviceName value.
	 * @return java.lang.String
	 */
	public String getDeviceName()
	{
		return deviceName;
	}
	/**
	 * Returns the label value.
	 * @return java.lang.String
	 */
	public String getLabel()
	{
		return label;
	}
	/**
	 * Returns the load factor value.
	 * Load factor computed by totalAreaOfSet / maxArea.  -1 when no data.
	 * @return double
	 */
	public double getLoadFactor()
	{
//		if(getDataItemArray() != null)
		if( getDataItemsMap() != null)
		{
			if (getMaxArea() != 0.0 )
				return getAreaOfSet() / getMaxArea();
		}

		return -1;
	}
	
	/**
	 * Returns the maxArea value.
	 * Multiplies the maximumValue by the dataItemArray length.
	 * Area value at the maximumValue curve.
	 * @return double
	 */
	public double getMaxArea()
	{
		if( maxArea == 0)
		{
			if( getMaximumValue() != null)
				maxArea = getMaximumValue().doubleValue() * getDataItemsMap().size();//getDataItemArray().length;
		}
		return maxArea;
	}
	/**
	 * Returns the maximumTSDataItem value.
	 * Searches through the entire dataItemArray for the MAX value and respective timeStamp.
	 * @return org.jfree.data.time.TimeSeriesDataItem
	 */
	public TimeSeriesDataItem getMaximumTSDataItem()
	{
		if( maximumTSDataItem == null)
		{
			double max = Double.MIN_VALUE;
			if( getDataItemsMap() != null)
			{
				Iterator iter = getDataItemsMap().entrySet().iterator();
				while( iter.hasNext())
				{
					Map.Entry entry = (Map.Entry)iter.next();
					TimeSeriesDataItem dataItem = (TimeSeriesDataItem)entry.getValue();
					if( dataItem.getValue().doubleValue() > max)
					{
						max = dataItem.getValue().doubleValue();
						maximumTSDataItem = dataItem;
					}
				}
			}
		}
		return maximumTSDataItem;
	}
	/**
	 * Returns the minimumTSDataItem value.
	 * Searches through the entire dataItemArray for the MIN value and respective timeStamp.
	 * @return org.jfree.data.time.TimeSeriesDataItem
	 */
	public TimeSeriesDataItem getMinimumTSDataItem()
	{
		if( minimumTSDataItem == null)
		{
			double min = Double.MAX_VALUE;
			if( getDataItemsMap() != null )
			{
				Iterator iter = getDataItemsMap().entrySet().iterator();
				while( iter.hasNext())
				{
					Map.Entry entry = (Map.Entry)iter.next();
					TimeSeriesDataItem dataItem = (TimeSeriesDataItem)entry.getValue();
					if( dataItem.getValue().doubleValue() < min)
					{
						min = dataItem.getValue().doubleValue();					
						minimumTSDataItem = dataItem;
					}
				}
			}
		}
		return minimumTSDataItem;
	}

	/**
	 * Returns the value from maximumTSDataItem.
	 * @return java.lang.Double
	 */
	public Double getMaximumValue()
	{
		if( getMaximumTSDataItem() != null)
			return (Double)getMaximumTSDataItem().getValue();
		else 
			return null;
	}
	/**
	 * Returns the value from minimumTSDataItem.
	 * @return java.lang.Double
	 */
	public Double getMinimumValue()
	{
		if( getMinimumTSDataItem() != null)
			return (Double)getMinimumTSDataItem().getValue();
		else
			return null;
	}

	/**
	 * Returns the multiplier value.
	 * @return java.lang.Double
	 */
	public Double getMultiplier()
	{
		return multiplier;
	}

	/**
	 * Returns the useMultiplier boolean.
	 * @return boolean
	 */
	public boolean getUseMultiplier()
	{
		return useMultiplier;
	}
	/**
	 * Returns the pointIDvalue.
	 * @return java.lang.Integer
	 */
	public Integer getPointId()
	{
		return pointId;
	}

	/**
	 * Returns the typeMask value.
	 * @return int
	 */
	public int getTypeMask()
	{
		return typeMask;
	}
	/**
	 * Returns the valuesArray value.
	 * An array of only TimeSeriesDataItem.value(s) is created from dataItemArray.
	 * * Actually populates both the values and periods array if values array is null.
	 *   We are already iterating through the entrySet, we might as well store everything we can. 
	 * @return double[]
	 */
	public double[] getValuesArray()
	{
		if( valuesArray == null)
		{
			if( getDataItemsMap() == null )
				return null;

			valuesArray = new double[getDataItemsMap().size()];
			periodsArray = new long[getDataItemsMap().size()];
			Iterator iter = getDataItemsMap().entrySet().iterator();
			int index = 0;
			while( iter.hasNext())
			{
				Map.Entry entry = (Map.Entry)iter.next();
				TimeSeriesDataItem dataItem = (TimeSeriesDataItem)entry.getValue();
				valuesArray[index] = dataItem.getValue().doubleValue();
				long time = dataItem.getPeriod().getStart().getTime();
				long round = time % resolution;
				time = time - round;
				periodsArray[index] = time;
				index++;
			}
		}
		return valuesArray;
	}
	/**
	 * Returns the periodsArray value.
	 * An array of only TimeSeriesDataItem.period(s) is created from dataItemArray.
	 * * Actually populates both the values and periods array if periods array is null.
	 *   We are already iterating through the entrySet, we might as well store everything we can. 
	 * @return long[]
	 */	
	public long[] getPeriodsArray()
	{
//		long resolution = this.resolution;
		if( periodsArray == null)
		{
			if (getDataItemsMap() == null )
				return null;

			valuesArray = new double[getDataItemsMap().size()];
			periodsArray = new long[getDataItemsMap().size()];
			Iterator iter = getDataItemsMap().entrySet().iterator();
			int index = 0;
			while( iter.hasNext())
			{
				Map.Entry entry = (Map.Entry)iter.next();
				TimeSeriesDataItem dataItem = (TimeSeriesDataItem)entry.getValue();
				valuesArray[index] = dataItem.getValue().doubleValue();
				long time = dataItem.getPeriod().getStart().getTime();
				long round = time % resolution;
				time = time - round;
				periodsArray[index] = time;
				index++;
			}
		}
		return periodsArray;
	}	
	/**
	 * Sets the axis value.
	 * Valid options are 'L'[eft] 'R'[ight].
	 * @param newAxis java.lang.Character
	 */
	protected void setAxis(Character newAxis)
	{
		axis = newAxis;
	}
	/**
	 * Sets the color value.
	 * @param newColor java.awt.Color
	 */
	protected void setColor(Color newColor)
	{
		color = newColor;
	}
	/**
	 * Sets the dataItemArray value.
	 * @param newDataItemArray org.jfree.data.time.TimeSeriesDataItem []
	 */
//	protected void setDataItemArray(TimeSeriesDataItem[] newDataItemArray)
//	{
//		dataItemArray = newDataItemArray;
//	}
	protected void setDataItemsMap(HashMap newDataItemsMap)
	{
		dataItemsMap = newDataItemsMap;
	}
	/**
	 * Sets the decimalPlaces value.
	 * @param newDecimalPlaces int
	 */
	protected void setDecimalPlaces(int newDecimalPlaces)
	{
		decimalPlaces = newDecimalPlaces;
	}
	/**
	 * Sets the deviceName value
	 * @param newDeviceName java.lang.String
	 */
	protected void setDeviceName(String newDeviceName)
	{
		deviceName = newDeviceName;
	}
	/**
	 * Sets the label value
	 * @param newLabel java.lang.String
	 */
	protected void setLabel(String newLabel)
	{
		label = newLabel;
	}
	/**
	 * Sets the multiplier value.
	 * @param newMultiplier java.lang.Double
	 */
	protected void setMultiplier(Double newMultiplier)
	{
		multiplier = newMultiplier;
	}
	/**
	 * Sets the useMultipier value
	 * @param selected boolean
	 */
	public void setUseMultiplier(boolean selected)
	{
		useMultiplier = selected;
	}	
	/**
	 * Sets the pointID value.
	 * @param newPointId java.lang.Integer
	 */
	protected void setPointId(Integer newPointId)
	{
		pointId = newPointId;
	}
	/**
	 * Sets the typeMask value
	 * @param newTypeMask int
	 */
	protected void setTypeMask(int newTypeMask)
	{
		typeMask = newTypeMask;
	}
	/**
	 * @param l
	 */
	public void setResolution(long l)
	{
		resolution = l;
	}

	/**
	 * @return
	 */
	public String getMoreData()
	{
		return moreData;
	}

	/**
	 * @param double1
	 */
	public void setMoreData(String string)
	{
		moreData = string;
	}

	/**
	 * @return
	 */
/*	public int getRenderer()
	{
		return renderer;
	}
*/
	/**
	 * @param i
	 */
/*	public void setRenderer(int i)
	{
		renderer = i;
	}
*/
	/**
	 * @return
	 */
	public Object getDataSeries()
	{
		return dataSeries;
	}
	
	/**
	 * @param series
	 */
	public void setDataSeries(Object series)
	{
		dataSeries = series;
	}

}
