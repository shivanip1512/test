/*
 * Created on May 13, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.common.gui.table;

import java.awt.Color;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface ICTITableRenderer
{

	Color getCellBackgroundColor(int row, int col);

	Color getCellForegroundColor(int row, int col);

}
