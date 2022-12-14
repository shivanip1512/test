package com.cannontech.graph.buffer.html;

/**
 * Insert the type's description here.
 * Creation date: (11/5/2001 10:47:56 AM)
 * @author: 
 */
import com.cannontech.graph.model.TrendModel;
//import com.cannontech.graph.GraphDataFormats;

public abstract class HTMLBuffer implements com.cannontech.graph.GraphDefines
{
	public TrendModel model = null;

/**
 * HTMLBuffer constructor comment.
 */
public HTMLBuffer()
{
	super();
}
public abstract StringBuffer getHtml(StringBuffer buf);
/**
 * Insert the method's description here.
 * Creation date: (1/31/2001 1:37:50 PM)
 * @return com.cannontech.graph.model.GraphModel
 */
public TrendModel getModel() {
	return model;
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 11:15:11 AM)
 */
public void setFractionDigits(int decimalPlaces)
{
	valueFormat.setMaximumFractionDigits(decimalPlaces);
	valueFormat.setMinimumFractionDigits(decimalPlaces);
}
/**
 * Insert the method's description here.
 * Creation date: (1/31/2001 1:37:50 PM)
 * @param newModel com.cannontech.graph.model.GraphModel
 */
public void setModel( TrendModel newModel)
{
	model = newModel;
}
}
