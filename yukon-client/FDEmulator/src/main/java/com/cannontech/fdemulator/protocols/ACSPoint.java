/*
 * Created on Dec 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.fdemulator.protocols;

/**
 * @author Aaron Solberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ACSPoint
{

	private String pointType;
	private int remote;
	private int point;
	private int interval;
	private String category;
	private String function;
	private double min;
	private double max;
	private double delta;
	private boolean maxstart;
	private double currentValue;

	public ACSPoint(String pointTypevar, String remotevar, String pointvar, String categoryvar, String intervalvar, String functionvar, String minvar, String maxvar, String deltavar, String maxstartvar)
	{
		pointType = pointTypevar;
		Integer remoteInt = new Integer(remotevar);
		remote = remoteInt.intValue();
		Integer pointInt = new Integer(pointvar);
		point = pointInt.intValue();
		category = categoryvar;
		Integer newint = new Integer(intervalvar);
		interval = newint.intValue();
		
		function = functionvar;
		Double newmin = new Double(minvar);
		min = newmin.doubleValue();
		Double newmax = new Double(maxvar);
		max = newmax.doubleValue();
		try{
			Double newdelta = new Double(deltavar);
			delta = newdelta.doubleValue();
		}catch(Exception e){
			if("java.lang.NumberFormatException: empty String".equals(e.toString())){
				delta = 0.0;
			}
		}
		Boolean newmaxStart = new Boolean(maxstartvar);
		maxstart = newmaxStart.booleanValue();
		if (maxstart)
		{
			currentValue = max;
		} else
			currentValue = min;

//		if ("Value".equalsIgnoreCase(pointTypevar))
//		{
//
//			function = functionvar;
//
//			if ("RANDOM".equalsIgnoreCase(functionvar))
//			{
//				Double newmin = new Double(minvar);
//				min = newmin.doubleValue();
//				Double newmax = new Double(maxvar);
//				max = newmax.doubleValue();
//				delta = 0.0;
//				maxstart = false;
//				currentValue = 0.0;
//			} else if ("PYRAMID".equalsIgnoreCase(functionvar))
//			{
//				Double newmin = new Double(minvar);
//				min = newmin.doubleValue();
//				Double newmax = new Double(maxvar);
//				max = newmax.doubleValue();
//				Double newdelta = new Double(deltavar);
//				delta = newdelta.doubleValue();
//				maxstart = false;
//				currentValue = min;
//			} else if ("DROPOFF".equalsIgnoreCase(function))
//			{
//				Double newmin = new Double(minvar);
//				min = newmin.doubleValue();
//				Double newmax = new Double(maxvar);
//				max = newmax.doubleValue();
//				Double newdelta = new Double(deltavar);
//				delta = newdelta.doubleValue();
//				Boolean newmaxStart = new Boolean(maxstartvar);
//				maxstart = newmaxStart.booleanValue();
//				if (maxstart)
//				{
//					currentValue = max;
//				} else
//					currentValue = min;
//			}
//		} else
//		{
//			function = functionvar;
//			min = 0;
//			max = 0;
//			delta = 0.0;
//			maxstart = false;
//			currentValue = 0.0;
//		}
	}

	public String getPointType()
	{
		return pointType;
	}

	public int getPointRemote()
	{
		return remote;
	}

	public int getPointNumber()
	{
		return point;
	}

	public String getPointCategory()
	{
		return category;
	}

	public int getPointInterval()
	{
		return interval;
	}

	public String getPointFunction()
	{
		return function;
	}

	public double getPointMin()
	{
		return min;
	}

	public double getPointMax()
	{
		return max;
	}

	public double getPointDelta()
	{
		return delta;
	}

	public boolean getPointMaxStart()
	{
		return maxstart;
	}

	public double getPointCurrentValue()
	{
		return currentValue;
	}

	public void setPointType(String pointTypevar)
	{
		pointType = pointTypevar;
	}

	public void setPointRemote(int remotevar)
	{
		remote = remotevar;
	}

	public void setPointNumber(int pointvar)
	{
		point = pointvar;
	}

	public void setPointCategory(String categoryvar)
	{
		category = categoryvar;
	}

	public void setPointInterval(int intervalvar)
	{
		interval = intervalvar;
	}

	public void setPointFunction(String functionvar)
	{
		function = functionvar;
	}

	public void setPointMin(double minvar)
	{
		min = minvar;
	}

	public void setPointMax(double maxvar)
	{
		max = maxvar;
	}

	public void setPointDelta(double deltavar)
	{
		delta = deltavar;
	}

	public void setPointMaxStart(boolean maxstartvar)
	{
		maxstart = maxstartvar;
	}

	public void setPointCurrentValue(double currentValuevar)
	{
		currentValue = currentValuevar;
	}

}