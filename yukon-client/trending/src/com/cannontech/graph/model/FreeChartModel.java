package com.cannontech.graph.model;

/**
 * Insert the type's description here.
 * Creation date: (6/18/2002 2:30:08 PM)
 * @author: 
 */
public class FreeChartModel
{
	private long pointId;
	private com.jrefinery.data.TimeSeriesDataPair [] dataPairArray = null;
/**
 * FreeChartModel constructor comment.
 */
public FreeChartModel() {
	super();
}
public com.jrefinery.data.TimeSeriesDataPair[] getDataPairArray()
{
	return dataPairArray;
}
public void setDataPairArray( com.jrefinery.data.TimeSeriesDataPair [] newDataPairArray)
{
	dataPairArray = newDataPairArray;
}
public void setPointId(long newPointId)
{
	pointId = newPointId;
}
}
