package com.cannontech.cbc.gui;

/**
 * Insert the type's description here.
 * Creation date: (12/12/2001 10:27:00 AM)
 * @author: 
 */
import java.awt.Color;
import com.cannontech.common.gui.util.Colors;

public interface CapControlTableModel
{
	public static final Color DEFAULT_ALARMCOLOR = Colors.getColor(Colors.WHITE_STR_ID);  // Only Alarm Color used - MAGENTA(will change later)
	public static final Color DEFUALT_BGCOLOR = Colors.getColor(Colors.BLACK_STR_ID);  // black as of 1-12-2001

/**
 * Insert the method's description here.
 * Creation date: (12/12/2001 10:27:54 AM)
 */
void clear();
/**
 * Insert the method's description here.
 * Creation date: (12/12/2001 1:45:17 PM)
 */
void toggleAlarms( boolean toggle );
}
