/*
 * Created on Dec 6, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.pentahoreport;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

import com.cannontech.analysis.tablemodel.HECO_LMEventSummaryModel;

/**
 * @author stacey
 */
public class BoldFormatFunction extends AbstractElementFormatFunction {
    private int col = HECO_LMEventSummaryModel.START_TIME_COLUMN; // the first customer column

    public BoldFormatFunction(int col_) {
        col = col_;
    }

    private String column = HECO_LMEventSummaryModel.START_TIME_STRING;

    /*
     * (non-Javadoc)
     * 
     * @see org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction#processRootBand
     */
    protected void processRootBand(Band band) {
        if (band instanceof ItemBand == false)
            return;
        Element e = FunctionUtilities.findElement(band, getElement());
        if (e == null)
            return;

        String originalValue = (String) getDataRow().get(column);
        if (originalValue == null)
            return;

        Boolean bold = Boolean.FALSE;
        if (originalValue.equalsIgnoreCase(HECO_LMEventSummaryModel.MAX_KW_STRING) ||
                originalValue.equalsIgnoreCase(HECO_LMEventSummaryModel.PENALTY_DOLLARS_STRING) ||
                originalValue.equalsIgnoreCase(HECO_LMEventSummaryModel.NUMBER_VIOLATIONS_STRING)) {
            bold = Boolean.TRUE;
        } else {
            bold = Boolean.FALSE;
        }
        e.getStyle().setStyleProperty(TextStyleKeys.BOLD, bold);
        e.getStyle().setStyleProperty(TextStyleKeys.ITALIC, bold);
        e.getStyle().setStyleProperty(TextStyleKeys.UNDERLINED, bold);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.pentaho.reporting.engine.classic.core.function.Expression#getValue()
     */
    public Object getValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected boolean evaluateElement(ReportElement arg0) {
        // TODO Auto-generated method stub
        return false;
    }

}
