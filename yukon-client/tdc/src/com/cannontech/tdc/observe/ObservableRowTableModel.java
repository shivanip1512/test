package com.cannontech.tdc.observe;

/**
 * Insert the type's description here.
 * Creation date: (1/9/2002 9:58:51 AM)
 * @author: 
 */
public abstract class ObservableRowTableModel extends javax.swing.table.AbstractTableModel 
{
	private ObservableJTableRow observedRow = null;


	//the observable row for this table model
	private class ObservableJTableRow extends java.util.Observable
	{
		private int rowNumber = -1;
		private Object data = null;


		public ObservableJTableRow( int rowNumber_ )
		{
			super();
			
			setRowNumber( rowNumber_ );
		}

		public java.lang.Object getData() 
		{
			return data;
		}

		public int getRowNumber() 
		{
			return rowNumber;
		}

		public void setChanged() 
		{
			setChanged();
		}

		public void setData(java.lang.Object newData) 
		{
			data = newData;
		}

		public void setRowNumber(int newRowNumber) 
		{
			rowNumber = newRowNumber;
		}		
	}

/**
 * ObservableRowTableModel constructor comment.
 */
public ObservableRowTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 10:21:56 AM)
 */
protected void fireObservedRowChanged( Object value ) 
{
	if( getObservedRow() != null
		 && getObservedRow().countObservers() > 0 )
	{		
		getObservedRow().setChanged();
		getObservedRow().notifyObservers( value );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 10:02:14 AM)
 * @return ObservableJTableRow
 */
public ObservableJTableRow getObservedRow()
{
	return observedRow;
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2002 10:02:14 AM)
 * @param int rowNumber
 */
public void setObservedRow( int rowNumber ) 
{
	observedRow = new ObservableJTableRow( rowNumber );
}
}
