package com.cannontech.loadcontrol.datamodels;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface IProgramTableModel extends TableModel, TableModelListener, ISelectableLMTableModel
{
	void clear();

	LMProgramBase getProgramAt(int rowIndex);

	void setCurrentControlArea( LMControlArea newCurrentControlArea );
	
	TableModelListener[] getTableModelListeners();
	
	//tells us if we should display a waiting messaging while updating the model
	boolean showWaiting( LMControlArea newCntrlArea );
	
	Object getRowAt(int row);
}
