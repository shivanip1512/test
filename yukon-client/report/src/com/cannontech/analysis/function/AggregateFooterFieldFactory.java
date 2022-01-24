package com.cannontech.analysis.function;

import org.pentaho.reporting.engine.classic.core.elementfactory.TextElementFactory;

public interface AggregateFooterFieldFactory extends ExpressionFieldFactory {
    public TextElementFactory createElementFactory();
}
