package com.cannontech.common.gui.panel;

import javax.swing.table.TableModel;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IMultiSelectModel extends TableModel 
{
	MultiSelectRow getRowAt( int row );
	
	void addRow( MultiSelectRow obj );

	void clear();

	void fireTableDataChanged();
	
	int getCheckBoxCol();
	
	void setAllGearNumbers(Integer val);
}
