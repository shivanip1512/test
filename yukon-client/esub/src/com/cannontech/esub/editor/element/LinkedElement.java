package com.cannontech.esub.editor.element;

/**
 * Provides a common interface for elements that
 * contain a link to another drawing.
 * Creation date: (1/23/2002 2:27:38 PM)
 * @author:  Aaron Lauinger 
 */
public interface LinkedElement {
	public String getLinkTo();
	public void setLinkTo(String linkTo);
}
