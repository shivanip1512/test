/*
 * Created on Dec 6, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.analysis.jfreereport;

import org.jfree.report.Band;
import org.jfree.report.Element;
import org.jfree.report.ItemBand;
import org.jfree.report.function.AbstractElementFormatFunction;
import org.jfree.report.function.FunctionUtilities;
import org.jfree.report.style.ElementStyleSheet;

import com.cannontech.analysis.tablemodel.HECO_LMEventSummaryModel;

/**
 * @author stacey
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class BoldFormatFunction extends AbstractElementFormatFunction
{
	private int col = HECO_LMEventSummaryModel.START_TIME_COLUMN;	//the first customer column
	public BoldFormatFunction (int col_)
	{
		col = col_;
	}
	/* (non-Javadoc)
	 * @see org.jfree.report.function.AbstractElementFormatFunction#processRootBand(org.jfree.report.Band)
	 */
	protected void processRootBand(Band band)
	{ 
		if (band instanceof ItemBand == false) return; 
		Element e = FunctionUtilities.findElement(band, getElement()); 
		if (e == null) return; 
      
		String originalValue = (String)getDataRow().get(col); 
		if (originalValue == null) return; 

		Boolean bold = Boolean.FALSE; 
		if (originalValue.equalsIgnoreCase(HECO_LMEventSummaryModel.MAX_KW_STRING) ||
			originalValue.equalsIgnoreCase(HECO_LMEventSummaryModel.PENALTY_DOLLARS_STRING) ||
			originalValue.equalsIgnoreCase(HECO_LMEventSummaryModel.NUMBER_VIOLATIONS_STRING)) 
		{ 
			bold = Boolean.TRUE; 
		} 
		else 
		{ 
			bold = Boolean.FALSE; 
		}
		e.getStyle().setStyleProperty(ElementStyleSheet.BOLD, bold); 
		e.getStyle().setStyleProperty(ElementStyleSheet.ITALIC, bold);
		e.getStyle().setStyleProperty(ElementStyleSheet.UNDERLINED, bold);
	} 
	/* (non-Javadoc)
	 * @see org.jfree.report.function.Expression#getValue()
	 */
	public Object getValue()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
