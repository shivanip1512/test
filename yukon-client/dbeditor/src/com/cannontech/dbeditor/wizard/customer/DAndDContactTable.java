package com.cannontech.dbeditor.wizard.customer;

import com.cannontech.common.gui.dnd.DragAndDropListener;
import com.cannontech.common.gui.dnd.DragAndDropListenerMulticaster;
import com.cannontech.common.gui.dnd.DragAndDropTable;
import com.cannontech.common.gui.dnd.IDroppableTableModel;
import com.cannontech.database.data.lite.LiteContact;

/**
 * Used as a drag and drop JTable for Contacts
 */
public class DAndDContactTable extends DragAndDropTable implements java.awt.dnd.DropTargetListener, java.awt.dnd.DragGestureListener
{
	/**
	 * DragAndDropTree constructor comment.
	 */
	public DAndDContactTable() 
	{
		super();
	}
	
	/**
	 * Adds a d&d listener
	 */
	public void addDragAndDropListener(DragAndDropListener newListener) 
	{
		dndEventMulticaster = DragAndDropListenerMulticaster.add(dndEventMulticaster, newListener);
		return;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/31/00 2:08:42 PM)
	 * @param device java.lang.String
	 * @param pointid java.lang.String
	 * @param pointName java.lang.String
	 */
	public void addContact( final LiteContact liteContact )
	{
		IDroppableTableModel tableModel = (IDroppableTableModel)this.getModel();
	
		// just return if the point is already in the right tree
		if( tableModel.objectExists( liteContact ) )
			return;
	
		tableModel.insertNewRow( liteContact );
	}
}
