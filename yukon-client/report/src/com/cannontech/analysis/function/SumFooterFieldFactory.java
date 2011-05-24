package com.cannontech.analysis.function;

import org.jfree.report.elementfactory.NumberFieldElementFactory;
import org.jfree.report.elementfactory.TextFieldElementFactory;
import org.jfree.report.function.Expression;
import org.jfree.report.function.ItemSumFunction;

import com.cannontech.analysis.ReportFactory;
import com.cannontech.analysis.report.ColumnLayoutData;

public class SumFooterFieldFactory implements AggregateFooterFieldFactory {
    private static final String FIELD_TOTAL_SUFFIX = " Total";
    private final String groupFieldName;
    private final ColumnLayoutData sourceColumn;

    public SumFooterFieldFactory(ColumnLayoutData sourceColumn, String groupFieldName) {
        this.sourceColumn = sourceColumn;
        this.groupFieldName = groupFieldName;
    }

    public SumFooterFieldFactory(ColumnLayoutData sourceColumn) {
        this.sourceColumn = sourceColumn;
        this.groupFieldName = "Column Heading";
    }
    
    public TextFieldElementFactory createElementFactory() {
        NumberFieldElementFactory factory = new NumberFieldElementFactory();
        factory.setFormatString(sourceColumn.getFormat());

        factory.setName(sourceColumn.getFieldName());
        factory.setFieldname(sourceColumn.getFieldName() + FIELD_TOTAL_SUFFIX);
        return factory;
    }

    public Expression createExpression() {
        ItemSumFunction function = new ItemSumFunction();
        function.setField(sourceColumn.getFieldName());
        function.setGroup(groupFieldName  + ReportFactory.NAME_GROUP);
        function.setName(sourceColumn.getFieldName() + FIELD_TOTAL_SUFFIX);
        return function;
    }

    public ColumnLayoutData getSourceColumn() {
        return sourceColumn;
    }
}
