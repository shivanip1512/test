package com.cannontech.analysis.function;

import org.jfree.report.elementfactory.NumberFieldElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.Expression;
import org.jfree.report.function.TotalGroupSumFunction;

import com.cannontech.analysis.report.ColumnLayoutData;

public class TotalSumFooterFieldFactory implements AggregateFooterFieldFactory {
    private static final String FIELD_TOTAL_SUFFIX = " Group Total";
    private final ColumnLayoutData sourceColumn;

    public TotalSumFooterFieldFactory(ColumnLayoutData sourceColumn) {
        this.sourceColumn = sourceColumn;
    }
    
    public TextFieldElementFactory createElementFactory() {
        NumberFieldElementFactory factory = new NumberFieldElementFactory();
        factory.setFormatString(sourceColumn.getFormat());
        factory.setName(sourceColumn.getFieldName());
        factory.setFieldname(sourceColumn.getFieldName() + FIELD_TOTAL_SUFFIX);
        return factory;
    }

    public Expression createExpression() {
        TotalGroupSumFunction function = new TotalGroupSumFunction();
        function.setField(sourceColumn.getFieldName());
        function.setName(sourceColumn.getFieldName() + FIELD_TOTAL_SUFFIX);
        return function;
    }

    public ColumnLayoutData getSourceColumn() {
        return sourceColumn;
    }
}