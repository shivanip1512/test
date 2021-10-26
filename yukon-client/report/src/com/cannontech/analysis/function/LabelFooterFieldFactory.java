package com.cannontech.analysis.function;

import org.pentaho.reporting.engine.classic.core.elementfactory.LabelElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextElementFactory;
import org.pentaho.reporting.engine.classic.core.function.Expression;

import com.cannontech.analysis.report.ColumnLayoutData;

public class LabelFooterFieldFactory implements AggregateFooterFieldFactory {

    private final ColumnLayoutData layoutData;
    private final String label;

    public LabelFooterFieldFactory(ColumnLayoutData layoutData, String label) {
        this.layoutData = layoutData;
        this.label = label;
    }
    
    public TextElementFactory createElementFactory() {
        LabelElementFactory factory = new LabelElementFactory();
        factory.setText(label);
        return factory;
    }

    public Expression createExpression() {
        return null;
    }

    public ColumnLayoutData getSourceColumn() {
        return layoutData;
    }

}
