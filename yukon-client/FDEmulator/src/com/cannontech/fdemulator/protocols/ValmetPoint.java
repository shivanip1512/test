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
public class ValmetPoint
{

	private String pointType;
	private String pointName;
	private int interval;
	private int port;
	private String function;
	private double min;
	private double max;
	private double delta;
	private boolean maxstart;
	private double currentValue;

	public ValmetPoint(String pointTypevar, String pointNamevar, String portvar, String intervalvar, 
	                   String functionvar, String minvar, String maxvar, String deltavar, String maxstartvar)
	{
		pointType = pointTypevar;
		pointName = pointNamevar;
		function = functionvar;
		Integer newint = new Integer(intervalvar);
		interval = newint.intValue();
		Integer newport = new Integer(portvar);
		port = newport;
		
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
//			min = 0.0;
//			max = 0.0;
//			delta = 0.0;
//			maxstart = false;
//			currentValue = min;
//		}
	}

	public String getPointType()
	{
		return pointType;
	}

	public String getPointName()
	{
		return pointName;
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

	public void setPointName(String pointNamevar)
	{
		pointName = pointNamevar;
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}