package com.cannontech.analysis.function;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ItemHideFunction;

import com.cannontech.analysis.report.ColumnLayoutData;

public class HideDuplicateFieldFactory implements ExpressionFieldFactory {

    private final ColumnLayoutData layoutData;

    public HideDuplicateFieldFactory(ColumnLayoutData layoutData) {
        this.layoutData = layoutData;
    }
    
    public Expression createExpression() {
        ItemHideFunction hideItem = new ItemHideFunction();
        hideItem.setName("hide" + layoutData.getFieldName());
        hideItem.setField(layoutData.getFieldName());
        hideItem.setElement(layoutData.getColumnName());
        return hideItem;
    }

    public ColumnLayoutData getSourceColumn() {
        return layoutData;
    }

}
