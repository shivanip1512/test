package com.cannontech.tdc.observe;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class ObservableJTableRow extends java.util.Observable
{

	private int rowNumber = -1;
	private Object data = null;

	/**
	 * Constructor for ObservableJTableRow.
	 */
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
