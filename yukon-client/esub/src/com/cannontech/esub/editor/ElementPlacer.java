package com.cannontech.esub.editor;

/**
 * Creation date: (12/13/2001 12:26:52 PM)
 * @author: Aaron Lauinger
 */

import com.loox.jloox.LxElement;

class ElementPlacer {

	private boolean placing = false;
	
	private LxElement element;
	private double x;
	private double y;

	ElementPlacer() {
	}
	LxElement getElement() {
		return element;
	}
	double getXPosition() {
		return x;
	}
	double getYPosition() {
		return y;
	}
	boolean isPlacing() {
		return placing;
	}
	void setElement(LxElement elem) {
		element = elem;
	}
	void setIsPlacing(boolean b) {
		placing = b;
		
		if( !placing )	{
			element = null;
			x = 0.0;
			y = 0.0;
		}
	}
	void setXPosition(double x) {
		this.x = x;
	}
	void setYPosition(double y) {
		this.y = y;
	}
}
