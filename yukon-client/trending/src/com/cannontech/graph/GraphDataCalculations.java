package com.cannontech.graph;

/**
 * Insert the type's description here.
 * Creation date: (7/18/2001 10:10:16 AM)
 * @author: 
 */
public class GraphDataCalculations
{
/**
 * Insert the method's description here.
 * Creation date: (7/18/2001 9:59:33 AM)
 * @return double
 * @param maxValue double
 * @param totalValuesRead int
 */
public static double calculateLoadFactor(double areaOfSet, double totalMaxArea)
{
	if (totalMaxArea != 0.0)
		return areaOfSet / totalMaxArea;
	else
		return 0.0;
}
}
