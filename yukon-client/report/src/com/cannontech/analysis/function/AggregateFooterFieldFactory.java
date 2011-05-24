package com.cannontech.analysis.function;

import org.jfree.report.elementfactory.TextElementFactory;

public interface AggregateFooterFieldFactory extends ExpressionFieldFactory {
    public TextElementFactory createElementFactory();
}
