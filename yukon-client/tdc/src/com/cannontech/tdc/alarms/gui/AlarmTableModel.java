package com.cannontech.tdc.alarms.gui;

import com.cannontech.common.gui.table.ICTITableRenderer;

/**
 * Insert the type's description here.
 * Creation date: (1/12/2001 12:54:18 PM)
 * @author: 
 */
public interface AlarmTableModel extends ICTITableRenderer
{
	/**
	 * Insert the method's description here.
	 * Creation date: (1/12/2001 1:11:16 PM)
	 * @param minLocation int
	 * @param maxLocation int
	 */
	/* This method will meake the table model repaint itself for the choosen rowNumbers */
	void forcePaintTableRowUpdated(int minLocation, int maxLocation);

	boolean isPlayingSound();
	/**
	 * Insert the method's description here.
	 * Creation date: (1/12/2001 1:04:48 PM)
	 * @return boolean
	 * @param rowNumber int
	 * @param color java.awt.Color
	 */
	/* Returns true if the alarm is alarming and the color was set successfully, false otherwise */
	boolean setBGRowColor(int rowNumber, int color);
}
