package com.cannontech.esub.element;

import com.cannontech.esub.Drawing;

/**
 * Provides a common interface for elements that
 * be added to drawing
 * Creation date: (1/23/2002 2:27:38 PM)
 * @author:  Aaron Lauinger 
 */
public interface DrawingElement {
	
	public String getElementID();
	
	public int getVersion();
    public void setVersion(int newVer);
    
	/**
	 * @return true if this element is copyable (cut 'N pasteable)
	 */
	public boolean isCopyable();
	
	public Drawing getDrawing();
	public void setDrawing(Drawing d);
	
	public String getLinkTo();
	public void setLinkTo(String linkTo);
}
