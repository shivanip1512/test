/*
 * Created on Jul 19, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.dbeditor.editor.device;

import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteYukonPAObject;

import com.cannontech.dbeditor.editor.device.DeviceVerificationAssignmentPanel;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DeviceVerificationAssignmentTableModel extends AbstractTableModel 
{
	public final static int DEVICELITEPAO_COLUMN = 0;
	public final static int RESENDONFAIL_COLUMN = 1;
	
	private String[] COLUMN_NAMES = 
	{
		"Device", 
		"Resend on Fail"
	};
	
	private Class[] COLUMN_CLASSES = {LiteYukonPAObject.class, Boolean.class};
	
	private Vector rows = null;
	
	private class RowValue
	{
		private LiteYukonPAObject deviceLitePAO = null;
		private boolean resendOnFail = false;
		
		public RowValue(LiteYukonPAObject deviceLitePAO, boolean resendOnFail )
		{
			super();
			this.deviceLitePAO = deviceLitePAO;
			this.resendOnFail = resendOnFail;
		}

		// All getters
		public LiteYukonPAObject getDeviceLitePAO()
			{ return deviceLitePAO; }

		public boolean getResendOnFail()
			{ return resendOnFail; }
		
		// All setters
		public void setDeviceLitePAO(LiteYukonPAObject val)
			{ deviceLitePAO = val; }

		public void setResendOnFail(boolean val)
			{ resendOnFail = val; }
		}
		
	public DeviceVerificationAssignmentTableModel()
	{
		super();
	}
	
	public void addRowValue(LiteYukonPAObject deviceLitePAO, boolean resendOnFail) 
	{
		getRows().addElement( new RowValue(deviceLitePAO, resendOnFail) );
	}
	
	public LiteYukonPAObject getDeviceLitePAOAt(int row ) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getDeviceLitePAO();		
		}
		else
			return null;
	}
	
	public Class getColumnClass(int index) {
		return COLUMN_CLASSES[index];
	}
	/**
	 * getColumnCount method comment.
	 */
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 * @param index int
	 */
	public String getColumnName(int index) {
		return COLUMN_NAMES[index];
	}
	/**
	 * getValueAt method comment.
	 */
	public boolean getResendOnFailAt(int row ) 
	{
		if( getRows() == null )
			return false;

		if( row <= getRows().size() )
		{
			return ((RowValue)getRows().elementAt(row)).getResendOnFail();
		}
		else
			return false;
	}
	
	/**
	 * getRowCount method comment.
	 */
	public int getRowCount() {
		return getRows().size();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/9/00 5:07:51 PM)
	 * @return java.util.Vector
	 */
	private java.util.Vector getRows() 
	{
		if( rows == null )
			rows = new Vector();
		
		return rows;
	}	

	public Object getValueAt(int row, int col) 
	{
		if( getRows() == null )
			return null;

		if( row <= getRows().size() )
		{
			RowValue rowVal = ((RowValue)getRows().elementAt(row));
		
			switch( col )
			{
				case DEVICELITEPAO_COLUMN:
					return rowVal.getDeviceLitePAO();

				case RESENDONFAIL_COLUMN:
					return new Boolean(rowVal.getResendOnFail());
					
				default:
					return null;
			}
				
		}
		else
			return null;
	}
	
	public boolean isCellEditable(int row, int column)
	{
		if (column == DEVICELITEPAO_COLUMN)
			return false;
		else
			return true;
	}
	
	public void removeRowValue(int rowNumber )
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().removeElementAt( rowNumber );
		}
	}
	
	public void setRowValue(int rowNumber, LiteYukonPAObject name, boolean resend) 
	{
		if( rowNumber >=0 && rowNumber < getRowCount() )
		{
			getRows().setElementAt( new RowValue(name, resend), rowNumber );
		}
	}
	
	public void setValueAt(Object value, int row, int col) 
	{
		if( row <= getRows().size() && col < getColumnCount() )
		{

			switch( col )
			{
				case DEVICELITEPAO_COLUMN:
					((RowValue)getRows().elementAt(row)).setDeviceLitePAO((LiteYukonPAObject)value);
					break;
				
				case RESENDONFAIL_COLUMN:
					if(value.toString().compareTo("") != 0)
						((RowValue)getRows().elementAt(row)).setResendOnFail(((Boolean)value).booleanValue());
					break;
					
				default:
					com.cannontech.clientutils.CTILogger.info(this.getClass() + " tried to set value for an invalid column, column number " + col );
			}
		}
	}
	
}
