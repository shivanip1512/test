package com.cannontech.common.gui.dnd;

import javax.swing.table.TableModel;

/**
 * Insert the type's description here.
 * Creation date: (6/17/2002 3:16:17 PM)
 * @author: 
 */
public interface IDroppableTableModel extends TableModel
{
	/**
	 * Insert the method's description here.
	 * Creation date: (6/17/2002 3:16:41 PM)
	 * @param ptId java.lang.String
	 */
	void insertNewRow( Object litePoint_ );
	/**
	 * Insert the method's description here.
	 * Creation date: (6/17/2002 3:17:59 PM)
	 * @return boolean
	 * @param LitePoint litePoint_
	 */
	boolean objectExists( Object litePoint_ );


	Object getRowAt( int row );
	
	void insertRowAt( Object value, int row );
	
	void removeRow( int row );
}
