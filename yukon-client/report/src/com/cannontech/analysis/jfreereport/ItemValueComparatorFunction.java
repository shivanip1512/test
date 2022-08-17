/*
 * Created on Jul 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.analysis.jfreereport;

import java.io.Serializable;

import org.jfree.report.function.AbstractExpression;
import org.jfree.report.function.Expression;

import com.cannontech.clientutils.CTILogger;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ItemValueComparatorFunction extends AbstractExpression implements Serializable
{
	private String comparator = "==";
    private String xValue;
    private String yValue;

	/**
	 * Constructs a new function. <P> Initially the function has no name...be sure to assign
	 * one before using the function.
	 */
	public ItemValueComparatorFunction ()
	{
	}
	
	/**
	 * Returns a clone of the function. <P> Be aware, this does not create a deep copy. If
	 * you have complex strucures contained in objects, you have to overwrite this
	 * function.
	 *
	 * @return A clone of the function.
	 *
	 * @throws CloneNotSupportedException this should never happen.
	 */
	public Object clone ()
	    throws CloneNotSupportedException
	{
		return super.clone();
	}

	/**
	 * Return the current function value. <P> The value is calculated as the quotient of two
	 * columns: the dividend column and the divisor column.  If the divisor is zero, the
	 * return value is "n/a";
	 *
	 * @return The quotient
	 */
	public Object getValue ()
	{
	    double xVal = 0;
	    double yVal = 0;
	    if( getXValue() != null && getYValue() != null)
	    {
            Object fieldValue = getDataRow().get(getXValue());
            // do not add when field is null
            if (fieldValue != null)
            {
                try
                {
                    final Number n = (Number) fieldValue;
                    xVal = n.doubleValue();
                }
                catch (Exception e)
                {
                    CTILogger.error("ItemColumnQuotientExpression(): problem with dividend value");
                }
            }

            fieldValue = getDataRow().get(getYValue());
            // do not add when field is null
            if (fieldValue != null)
            {
                try
                {
                    final Number n = (Number) fieldValue;
                    yVal = n.doubleValue();
                }
                catch (Exception e)
                {
                    CTILogger.error("ItemColumnQuotientExpression(): problem with dividend value");
                }
			}
	    }
	
	    double result = 0;
	    if( comparator.equalsIgnoreCase("=="))
	        return (xVal == yVal? "":"*");	//if they are equal, the result will be 0
	    
	    return null;
	}
	
	/**
	 * Return a completely separated copy of this function. The copy does no longer share
	 * any changeable objects with the original function.
	 *
	 * @return a copy of this function.
	*/
	public Expression getInstance ()
	{
	    final ItemValueComparatorFunction function = (ItemValueComparatorFunction) super.getInstance();
	    return function;
	}
   
	public String getXValue()
    {
        return xValue;
    }
    
    /**
     * Sets the object to be used as the x value for the function. <P> This value is treated as a fieldname
     * when the isUseXValueAsObject flag is false.  The field name corresponds to a column name in the report's TableModel.
     * @param value
     */
    public void setXValue(String value)
    {
        xValue = value;
    }
    public String getYValue()
    {
        return yValue;
    }
    /**
     * Sets the object to be used as the y value for the function. <P> This value is treated as a fieldname
     * when the isUseYValueAsObject flag is false.  The field name corresponds to a column name in the report's TableModel.
     * @param value
     */
    public void setYValue(String value)
    {
        yValue = value;
    }
}
