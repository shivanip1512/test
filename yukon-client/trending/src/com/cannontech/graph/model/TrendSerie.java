package com.cannontech.graph.model;

import java.awt.Color;
//
//import org.exolab.castor.jdo.Database;
/**
 * Insert the type's description here.
 * Creation date: (6/18/2002 2:30:08 PM)
 * @author: 
 */
public class TrendSerie
{
	private com.jrefinery.data.TimeSeriesDataPair[] dataPairArray = null;

	private Integer pointId;
	private String label = null;
	private Color color = null;
	private String deviceName = null;
	private Double multiplier = null;	//This is different then the point multiplier, this is a GDS
	private Character axis = null;
	private String type = com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES;		
	public int typeMask = com.cannontech.database.db.graph.GraphDataSeries.GRAPH_MASK;
	
	// Flag for using graph multiplier
	public boolean useMultiplier = false;
	private com.jrefinery.data.TimeSeriesDataPair minimumTSDataPair = null;
	private com.jrefinery.data.TimeSeriesDataPair maximumTSDataPair = null;

	// Load factor values computed for each point in the model, stored in an array that
	//  is in accordance to the order of the pointIds.
	private double areaOfSet = 0.0; //Load Factor, area under the curve
	private double maxArea = 0.0;  //Load Factor, total area (using max point value)

	// Number of decimal places each point has (from pointUnit table)
	private int decimalPlaces = 3;

	private double[] valuesArray = null;
	private long[] periodsArray = null;
	
	/**
	 * FreeChartModel constructor comment.
	 */
	public TrendSerie()
	{
		super();
	}
	public double getAreaOfSet()
	{
		if( areaOfSet == 0)
		{
			for (int i = 0; i < getDataPairArray().length; i++)
			{
				areaOfSet += getDataPairArray(i).getValue().doubleValue();
			}
		}
		return areaOfSet;
	}
	public Color getColor()
	{
		return color;
	}

	public com.jrefinery.data.TimeSeriesDataPair[] getDataPairArray()
	{
		return dataPairArray;
	}

	public com.jrefinery.data.TimeSeriesDataPair getDataPairArray(int serie)
	{
//		if( getMultiplier() != null)
		if( getUseMultiplier())
		{
			com.jrefinery.data.TimePeriod tp = dataPairArray[serie].getPeriod();
			Number val = new Double(dataPairArray[serie].getValue().doubleValue() * getMultiplier().doubleValue());
			com.jrefinery.data.TimeSeriesDataPair multDP = new com.jrefinery.data.TimeSeriesDataPair(tp, val);
			return (multDP);
		}
		return dataPairArray[serie];
	}
	
	public int getDecimalPlaces()
	{
		return decimalPlaces;
	}
	public String getDeviceName()
	{
		return deviceName;
	}
	public String getLabel()
	{
		return label;
	}
	
	public double getLoadFactor()
	{
		if(getDataPairArray() != null)
		{
			if (getMaxArea() != 0.0 )
				return getAreaOfSet() / getMaxArea();
		}

		return -1;
	}
	
	public double getMaxArea()
	{
		if( maxArea == 0)
		{
			if( getMaximumValue() != null)
				maxArea = getMaximumValue().doubleValue() * getDataPairArray().length;
		}
		return maxArea;
	}

	public com.jrefinery.data.TimeSeriesDataPair getMaximumTSDataPair()
	{
		if( maximumTSDataPair == null)
		{
			double max = Double.MIN_VALUE;
			if( getDataPairArray() != null)
			{
				for (int i = 0; i < getDataPairArray().length; i++)
				{
					if( getDataPairArray(i).getValue().doubleValue() > max)
					{
						max = getDataPairArray(i).getValue().doubleValue();
						maximumTSDataPair = getDataPairArray(i);
					}
				}
			}
		}
		return maximumTSDataPair;
	}
	public com.jrefinery.data.TimeSeriesDataPair getMinimumTSDataPair()
	{
		if( minimumTSDataPair == null)
		{
			double min = Double.MAX_VALUE;
			if( getDataPairArray() != null )
			{
				for (int i = 0; i < getDataPairArray().length; i++)
				{
					if( getDataPairArray(i).getValue().doubleValue() < min)
					{
						min = getDataPairArray(i).getValue().doubleValue();					
						minimumTSDataPair = getDataPairArray(i);
					}
				}
			}
		}
		return minimumTSDataPair;
	}

	public Double getMaximumValue()
	{
		if( getMaximumTSDataPair() != null)
			return (Double)getMaximumTSDataPair().getValue();
		else 
			return null;
	}
	/*
	public long getMaximumTimestamp()
	{
		return getMaximumTSDataPair().getPeriod().getStart();
	}
	*/
	public Double getMinimumValue()
	{
		if( getMinimumTSDataPair() != null)
			return (Double)getMinimumTSDataPair().getValue();
		else
			return null;
	}
	/*
	public long getMinimumTimestamp()
	{
		return getMinimumTSDataPair().getPeriod().getStart();
	}
	*/
	public Double getMultiplier()
	{
		return multiplier;
	}
	public boolean getUseMultiplier()
	{
		return useMultiplier;
	}
	public Integer getPointId()
	{
		return pointId;
	}
	
	public String getType()
	{
		return type;
	}
	public int getTypeMask()
	{
		return typeMask;
	}
	public double[] getValuesArray()
	{
		if( valuesArray == null)
		{
			if( getDataPairArray() == null)
				return null;
			valuesArray = new double[getDataPairArray().length];
			for (int i = 0; i < getDataPairArray().length; i++)
			{
				valuesArray[i] = getDataPairArray(i).getValue().doubleValue();
			}
		}
		return valuesArray;
	}
	
	public long[] getPeriodsArray()
	{
		if( periodsArray == null)
		{
			if( getDataPairArray() == null)
				return null;
				
			periodsArray = new long[getDataPairArray().length];
			for (int i = 0; i < getDataPairArray().length; i++)
			{
				periodsArray[i] = getDataPairArray(i).getPeriod().getStart();
			}
		}
		return periodsArray;
	}	
	
	protected void setColor(Color newColor)
	{
		color = newColor;
	}
	protected void setDataPairArray(com.jrefinery.data.TimeSeriesDataPair[] newDataPairArray)
	{
		dataPairArray = newDataPairArray;
	}
	protected void setDecimalPlaces(int newDecimalPlaces)
	{
		decimalPlaces = newDecimalPlaces;
	}
	protected void setDeviceName(String newDeviceName)
	{
		deviceName = newDeviceName;
	}
	protected void setLabel(String newLabel)
	{
		label = newLabel;
	}
	protected void setMultiplier(Double newMultiplier)
	{
		multiplier = newMultiplier;
	}
	public void setUseMultiplier(boolean selected)
	{
		useMultiplier = selected;
	}	
	protected void setPointId(Integer newPointId)
	{
		pointId = newPointId;
	}
	protected void setType(String newType)
	{
		type = newType;
	}
	protected void setTypeMask(int newTypeMask)
	{
		typeMask = newTypeMask;
	}
}
