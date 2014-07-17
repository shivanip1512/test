/*
 * Created on Jul 17, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.calchist;

import java.io.Serializable;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class HoursAndValues implements Serializable
{
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
	public Double getValue(int hourOfDay)
	{
		for (int i = 0; i < getHours().length; i++)
		{
			if( getHours()[i].intValue() == hourOfDay)
				return getValues()[i];
		}
		return null;
	}
}