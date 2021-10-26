package com.cannontech.analysis.function;

import org.pentaho.reporting.engine.classic.core.function.Expression;

import com.cannontech.analysis.report.ColumnLayoutData;

public interface ExpressionFieldFactory {
    public Expression createExpression();
    public ColumnLayoutData getSourceColumn();
}
