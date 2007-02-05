package com.cannontech.analysis.function;

import org.jfree.report.elementfactory.TextElementFactory;
import org.jfree.report.function.Expression;

import com.cannontech.analysis.report.ColumnLayoutData;

public interface AggregateFooterFieldFactory {
    public TextElementFactory createElementFactory();
    public Expression createExpression(String string);
    public ColumnLayoutData getSourceColumn();
}
