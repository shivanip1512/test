/*
 * Created on Jul 18, 2005
 */
package com.cannontech.analysis.pentahoreport;

/**
 * @author stacey
 * 
 * Yukon override of getValue() method in super.
 */
public class ItemColumnQuotientExpression
        extends org.pentaho.reporting.engine.classic.core.function.ItemColumnQuotientExpression {

    /*
     * (non-Javadoc)
     * 
     * @see org.pentaho.reporting.engine.classic.core.function.ItemColumnQuotientExpression#getValue()
     */
    public Object getValue() {
        // If the divisor is null, this data is returned as null, the super returns it as Double.NAN, which we don't want.
        Object fieldValue = getDataRow().get(getDivisor());
        if (fieldValue == null)
            return null;
        else
            return super.getValue();
    }
}
