package com.cannontech.loadcontrol.displays;

import java.util.EventListener;


/**
 * Insert the type's description here.
 * Creation date: (7/31/2001 12:06:36 PM)
 * @author: 
 */
public interface IControlAreaListener extends EventListener
{

	/**
	 * Insert the method's description here.
	 * Creation date: (8/6/2001 3:02:44 PM)
	 * @param viewName java.lang.String
	 */
	void setCurrentDisplay( LCDisplayItem display_ );
}
