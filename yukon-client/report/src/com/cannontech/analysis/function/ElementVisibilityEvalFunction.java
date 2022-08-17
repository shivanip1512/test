/*
 * Created on Aug 10, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.function;

import org.jfree.report.event.ReportEvent;
import org.jfree.report.function.ElementVisibilitySwitchFunction;

/**
 * @author stacey
 *
 * Hides itemBand based on flag hideItem.
 * Using the evalColIndex for the column's whose value you wish to compare
 * and evalString for the value to compare against.
 */
public class ElementVisibilityEvalFunction extends ElementVisibilitySwitchFunction
{
	boolean hideItemBand = false;
	int evalColIndex = 0;
	String evalString = "";
	boolean firstShowInGroup = false;
	
	public ElementVisibilityEvalFunction(int evalColIndex_, String evalString_, boolean hideItem_)
	{
		super();
		evalColIndex = evalColIndex_;
		evalString = evalString_;
		hideItemBand = hideItem_;
	}
	public void groupStarted(final ReportEvent event)
	{
		firstShowInGroup = true;
	}
	public void itemsAdvanced(final ReportEvent event)
	{
		updateVisibleState(event);
	}
	private void updateVisibleState(final ReportEvent event)
	{
		if( hideItemBand)
		{
			//Don't show (negate the actual boolean returned, if the strings are equal.
			boolean show = !((String)getDataRow().get(evalColIndex)).equalsIgnoreCase(evalString);
			event.getReport().getItemBand().setVisible(show);

			if( show && firstShowInGroup )
			{
				for (int i = 0; i < event.getReport().getItemBand().getElementCount(); i++)
					event.getReport().getItemBand().getElement(i).setVisible(show);
				firstShowInGroup = false;
			}
		}
	}
	
}
