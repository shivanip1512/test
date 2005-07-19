/*
 * Created on Jul 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.analysis.jfreereport;

/**
 * @author stacey
 * Yukon override of getValue() method in super.
 * We want different formatting!
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ItemColumnQuotientExpression extends org.jfree.report.function.ItemColumnQuotientExpression
{

    /* (non-Javadoc)
     * @see org.jfree.report.function.ItemColumnQuotientExpression#getValue()
     */
    public Object getValue()
    {
        //If the divisor is null, this data is returned as null, the super returns it as Double.NAN, which we don't want.
        Object fieldValue = getDataRow().get(getDivisor());
        if (fieldValue == null)
            return null;
        else
            return super.getValue();
    }
}
