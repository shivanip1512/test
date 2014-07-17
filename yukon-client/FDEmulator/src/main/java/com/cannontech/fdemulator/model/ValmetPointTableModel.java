/*
 * Created on Jan 4, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.fdemulator.model;

import java.awt.Color;
import java.io.File;
import java.util.List;
import java.util.Vector;

import com.cannontech.fdemulator.fileio.ValmetFileIO;
import com.cannontech.fdemulator.protocols.ValmetPoint;

public class ValmetPointTableModel extends javax.swing.table.AbstractTableModel
{
	/* ROW DATA */
	private Vector<ValmetPoint> allValmetPoints = null;
	private List<ValmetPoint> currentValmetPoints = null;
	/* END - ROW DATA */

	//The columns and their column index	
	public static final int TYPE_COLUMN = 0;
	public static final int NAME_COLUMN = 1;
	public static final int PORT_COLUMN = 2;
	public static final int INTERVAL_COLUMN = 3;
	public static final int FUNCTION_COLUMN = 4;
	public static final int MIN_COLUMN = 5;
	public static final int MAX_COLUMN = 6;
	public static final int DELTA_COLUMN = 7;
	public static final int MAXSTART_COLUMN = 8;
	public static final String valmetFile = "resource/valmet_points.cfg";
	private ValmetFileIO valmetFileIO = null;

	//The column names based on their column index
	private static final String[] COLUMN_NAMES = { "Type", "Name", "Port", "Interval", "Function", "Min", "Max", "Delta", "MaxStart" };

	//The color schemes - based on the schedule status
	private static Color[] cellColors = {
		//Enabled subbus
		Color.green,
		//Disabled subbus
		Color.red,
		//Pending subbus
		Color.yellow };

	public ValmetPointTableModel()
	{
		super();
	}

	public void clear()
	{
		getAllValmetPoints().removeAllElements();

		currentValmetPoints = getAllValmetPoints();

		fireTableDataChanged();
	}

	public void forcePaintTableRowUpdated(int minLocation, int maxLocation)
	{
		fireTableRowsUpdated(minLocation, maxLocation);
	}

	public void addRow(ValmetPoint pointRow)
	{
		getAllValmetPoints().add(pointRow);
		fireTableDataChanged();
	}

	private Vector<ValmetPoint> getAllValmetPoints()
	{
		if (allValmetPoints == null)
			allValmetPoints = new Vector<ValmetPoint>(20);
		return allValmetPoints;
	}

	public java.awt.Color getCellColor(ValmetPoint pointRow)
	{
		//		if (pointRow.getCcDisableFlag().booleanValue()) {
		//			return cellColors[1]; //disabled color
		//		} else if (pointRow.getRecentlyControlledFlag().booleanValue()) {
		//			return cellColors[2]; //pending color
		//		} else {
		return cellColors[0];
		//		}
	}

	public int getColumnCount()
	{
		return COLUMN_NAMES.length;
	}

	public String getColumnName(int index)
	{
		return COLUMN_NAMES[index];
	}

	public synchronized ValmetPoint getRowAt(int rowIndex)
	{
		if (rowIndex >= 0 && rowIndex < getRowCount())
			return (ValmetPoint) getCurrentValmetPoints().get(rowIndex);
		else
			return null;
	}

	private List<ValmetPoint> getCurrentValmetPoints()
	{
		if (currentValmetPoints == null)
			currentValmetPoints = getAllValmetPoints();

		return currentValmetPoints;
	}

	public int getRowCount()
	{
		return getCurrentValmetPoints().size();
	}

	public Object getValueAt(int row, int col)
	{
		ValmetPoint pointRow = getRowAt(row);

		switch (col)
		{
			case ValmetPointTableModel.TYPE_COLUMN :
				{
					return pointRow.getPointType();
				}

			case ValmetPointTableModel.NAME_COLUMN :
				{
					return pointRow.getPointName();
				}
				
			case ValmetPointTableModel.PORT_COLUMN :
    			{
    			    Integer newint = new Integer(pointRow.getPort());
    			    return newint.toString();
    			}

			case ValmetPointTableModel.INTERVAL_COLUMN :
				{
					Integer newint = new Integer(pointRow.getPointInterval());
					return newint.toString();
				}

			case ValmetPointTableModel.FUNCTION_COLUMN :
				{
					return pointRow.getPointFunction();
				}

			case ValmetPointTableModel.MIN_COLUMN :
				{
					Double newdouble = new Double(pointRow.getPointMin());
					return newdouble.toString();
				}

			case ValmetPointTableModel.MAX_COLUMN :
				{
					Double newdouble = new Double(pointRow.getPointMax());
					return newdouble.toString();
				}

			case ValmetPointTableModel.DELTA_COLUMN :
				{
					Double newdouble = new Double(pointRow.getPointDelta());
					return newdouble.toString();
				}

			case ValmetPointTableModel.MAXSTART_COLUMN :
				{
					Boolean newbool = new Boolean(pointRow.getPointMaxStart());
					return newbool.toString();
				}

			default :
				return null;
		}
	}
	
	public ValmetFileIO getValmetFileIO()
	{
		if(valmetFileIO == null) {
			valmetFileIO = new ValmetFileIO(valmetFile);
		}
		return valmetFileIO;
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		// update the file with new value
		getValmetFileIO().writeValmetFileUpdate(valmetFileIO.readFile(new File(valmetFile)), new File(valmetFile), row, col, value);
		// update table cell
		Vector<ValmetPoint> vec = getAllValmetPoints();
		ValmetPoint point = (ValmetPoint) vec.elementAt(row);
		if(col == TYPE_COLUMN){
			point.setPointType(value.toString());
		}else if(col == NAME_COLUMN){
			point.setPointName(value.toString());
		}else if(col == PORT_COLUMN){
		    Integer newint = new Integer(value.toString());
		    point.setPort(newint.intValue());
		}else if(col == INTERVAL_COLUMN){
			Integer newint = new Integer(value.toString());
			point.setPointInterval(newint.intValue());
		}else if(col == FUNCTION_COLUMN){
			point.setPointFunction(value.toString());
		}else if(col == MIN_COLUMN){
			Double newdouble = new Double(value.toString());
			point.setPointMin(newdouble.doubleValue());
		}else if(col == MAX_COLUMN){
			Double newdouble = new Double(value.toString());
			point.setPointMax(newdouble.doubleValue());
		}else if(col == DELTA_COLUMN){
			Double newdouble = new Double(value.toString());
			point.setPointDelta(newdouble.doubleValue());
		}else if(col == MAXSTART_COLUMN){
			Boolean bool = new Boolean(value.toString());
			point.setPointMaxStart(bool.booleanValue());
		}
		vec.setElementAt(point,row);
		// fire a repaint of the cell
		fireTableCellUpdated(row, col);
	}

	public boolean isCellEditable(int row, int column)
	{
		return true;
	}
}