package com.cannontech.loadcontrol.datamodels;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;
import com.cannontech.messaging.message.loadcontrol.data.Program;

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

	Program getProgramAt(int rowIndex);

	void setCurrentControlArea( ControlAreaItem newCurrentControlArea );
	
	TableModelListener[] getTableModelListeners();
	
	//tells us if we should display a waiting messaging while updating the model
	boolean showWaiting( ControlAreaItem newCntrlArea );
	
	Object getRowAt(int row);
}
