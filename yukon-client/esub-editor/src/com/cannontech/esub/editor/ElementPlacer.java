package com.cannontech.esub.editor;

import com.loox.jloox.LxComponent;

/**
 * Creation date: (12/13/2001 12:26:52 PM)
 * @author: Aaron Lauinger
 */

class ElementPlacer {

	private boolean placing = false;
	
	private LxComponent element;
	private double x;
	private double y;

	ElementPlacer() {
	}
	LxComponent getElement() {
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
	void setElement(LxComponent elem) {
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
