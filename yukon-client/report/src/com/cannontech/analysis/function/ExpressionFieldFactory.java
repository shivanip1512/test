package com.cannontech.analysis.function;

import org.jfree.report.function.Expression;

import com.cannontech.analysis.report.ColumnLayoutData;

public interface ExpressionFieldFactory {
    public Expression createExpression();
    public ColumnLayoutData getSourceColumn();
}
