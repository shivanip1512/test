/*
 * Created on Jul 17, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.calchist;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HoursAndValues{
	Double [] values = null;
	Integer [] hours = null;
		
	public HoursAndValues(int hoursSize, int valuesSize)
	{
		hours = new Integer[hoursSize];
		values = new Double[valuesSize];
	}
	public Double[] getValues()
	{
		return values;
	}
	public Integer[] getHours()
	{
		return hours;
	}
	public void setValues(Double [] values)
	{
		this.values = values;
	}
	public void setHours(Integer [] hours)
	{
		this.hours = hours;
	}
	public int length()
	{
		return (getValues().length == getHours().length ? getValues().length : 0);
	}
}