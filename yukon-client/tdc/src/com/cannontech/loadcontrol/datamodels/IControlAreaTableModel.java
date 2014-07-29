package com.cannontech.loadcontrol.datamodels;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.tdc.observe.ObservableJTableRow;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IControlAreaTableModel extends TableModel
{
	void clear();
	
	LMControlArea getRowAt( int rowNum_ );
	
	void setObservedRow( int rowNum_ );
	
	ObservableJTableRow getObservedRow();


	void addControlAreaAt( LMControlArea area, int indx );
	void removeControlArea( LMControlArea area );
	void setControlAreaAt( LMControlArea area, int index );
	
	TableModelListener[] getTableModelListeners();
}
