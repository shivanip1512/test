package com.cannontech.analysis.function;

import org.jfree.report.elementfactory.LabelElementFactory;
import org.jfree.report.elementfactory.TextElementFactory;
import org.jfree.report.function.Expression;

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
