package com.cannontech.graph.model;

import java.awt.Color;

import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeriesDataItem;

import com.cannontech.database.db.graph.GraphDataSeries;

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
	private TimeSeriesDataItem[] dataItemArray = null;

	private Integer pointId;
	private String label = null;
	private Color color = Color.blue;
	private String deviceName = null;
	private Double multiplier = new Double(1.0);	//This is different then the point multiplier, this is a GDS
	private Character axis = new Character('L');
	public int typeMask = GraphDataSeries.BASIC_GRAPH_TYPE;
	
	// Flag for using graph multiplier
	public boolean useMultiplier = false;
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
	 * @return
	 */
	public double getAreaOfSet()
	{
		if( areaOfSet == 0)
		{
			for (int i = 0; i < getDataItemArray().length; i++)
			{
				areaOfSet += getDataItemArray(i).getValue().doubleValue();
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
	public TimeSeriesDataItem[] getDataItemArray()
	{
		return dataItemArray;
	}

	/**
	 * Returns the dataItemArray[item] value.
	 * @return org.jfree.data.time.TimeSeriesDataItem
	 */
	public TimeSeriesDataItem getDataItemArray(int item)
	{
		if( getUseMultiplier())
		{
			RegularTimePeriod tp = dataItemArray[item].getPeriod();
			Number val = new Double(dataItemArray[item].getValue().doubleValue() * getMultiplier().doubleValue());
			TimeSeriesDataItem multDP = new TimeSeriesDataItem(tp, val);
			return (multDP);
		}
		return dataItemArray[item];
	}
	
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
		if(getDataItemArray() != null)
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
				maxArea = getMaximumValue().doubleValue() * getDataItemArray().length;
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
			if( getDataItemArray() != null)
			{
				for (int i = 0; i < getDataItemArray().length; i++)
				{
					if( getDataItemArray(i).getValue().doubleValue() > max)
					{
						max = getDataItemArray(i).getValue().doubleValue();
						maximumTSDataItem = getDataItemArray(i);
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
			if( getDataItemArray() != null )
			{
				for (int i = 0; i < getDataItemArray().length; i++)
				{
					if( getDataItemArray(i).getValue().doubleValue() < min)
					{
						min = getDataItemArray(i).getValue().doubleValue();					
						minimumTSDataItem = getDataItemArray(i);
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
	 * @return double[]
	 */
	public double[] getValuesArray()
	{
		if( valuesArray == null)
		{
			if( getDataItemArray() == null)
				return null;
			valuesArray = new double[getDataItemArray().length];
			for (int i = 0; i < getDataItemArray().length; i++)
			{
				valuesArray[i] = getDataItemArray(i).getValue().doubleValue();
			}
		}
		return valuesArray;
	}
	/**
	 * Returns the periodsArray value.
	 * An array of only TimeSeriesDataItem.period(s) is created from dataItemArray.
	 * @return long[]
	 */	
	public long[] getPeriodsArray()
	{
		long resolution = TrendProperties.getResolutionInMillis();
		if( periodsArray == null)
		{
			if( getDataItemArray() == null)
				return null;
			java.util.Date now = new java.util.Date();

			periodsArray = new long[getDataItemArray().length];
			for (int i = 0; i < getDataItemArray().length; i++)
			{
				//Apply the resolution to sync timeperiods.
				long time = getDataItemArray(i).getPeriod().getStart().getTime();
				long round = time % resolution;
				time = time - round;
				periodsArray[i] = time;
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
	protected void setDataItemArray(TimeSeriesDataItem[] newDataItemArray)
	{
		dataItemArray = newDataItemArray;
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
}
