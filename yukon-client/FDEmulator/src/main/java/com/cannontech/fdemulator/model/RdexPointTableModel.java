package com.cannontech.fdemulator.model;

import java.awt.Color;
import java.io.*;
import java.util.*;
import com.cannontech.fdemulator.protocols.RdexPoint;
import com.cannontech.fdemulator.fileio.*;

public class RdexPointTableModel extends javax.swing.table.AbstractTableModel
{
	/* ROW DATA */
	private java.util.Vector allRdexPoints = null;
	private java.util.List currentRdexPoints = null;
	/* END - ROW DATA */

	//The columns and their column index	
	public static final int TYPE_COLUMN = 0;
	public static final int NAME_COLUMN = 1;
	public static final int INTERVAL_COLUMN = 2;
	public static final int FUNCTION_COLUMN = 3;
	public static final int MIN_COLUMN = 4;
	public static final int MAX_COLUMN = 5;
	public static final int DELTA_COLUMN = 6;
	public static final int MAXSTART_COLUMN = 7;
	public static final String rdexFile = "src/main/resources/rdex_points.cfg";
	private static final String LF = System.getProperty("line.separator");
	private BufferedReader file;
	private RdexFileIO rdexFileIO = null;

	//The column names based on their column index
	private static final String[] COLUMN_NAMES = { "Type", "Name", "Interval", "Function", "Min", "Max", "Delta", "MaxStart" };

	//The color schemes - based on the schedule status
	private static Color[] cellColors = {
		//Enabled subbus
		Color.green,
		//Disabled subbus
		Color.red,
		//Pending subbus
		Color.yellow };

	public RdexPointTableModel()
	{
		super();
	}

	public void clear()
	{
		getAllRdexPoints().removeAllElements();

		currentRdexPoints = getAllRdexPoints();

		fireTableDataChanged();
	}

	public void forcePaintTableRowUpdated(int minLocation, int maxLocation)
	{
		fireTableRowsUpdated(minLocation, maxLocation);
	}

	public void addRow(RdexPoint pointRow)
	{
		getAllRdexPoints().add(pointRow);
		fireTableDataChanged();
	}

	private java.util.Vector getAllRdexPoints()
	{
		if (allRdexPoints == null)
			allRdexPoints = new java.util.Vector(20);
		return allRdexPoints;
	}

	public java.awt.Color getCellColor(RdexPoint pointRow)
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

	public synchronized RdexPoint getRowAt(int rowIndex)
	{
		if (rowIndex >= 0 && rowIndex < getRowCount())
			return (RdexPoint) getCurrentRdexPoints().get(rowIndex);
		else
			return null;
	}

	private java.util.List getCurrentRdexPoints()
	{
		if (currentRdexPoints == null)
			currentRdexPoints = getAllRdexPoints();

		return currentRdexPoints;
	}

	public int getRowCount()
	{

		return getCurrentRdexPoints().size();
	}

	public Object getValueAt(int row, int col)
	{

		RdexPoint pointRow = getRowAt(row);

		switch (col)
		{
			case RdexPointTableModel.TYPE_COLUMN :
				{
					return pointRow.getPointType();
				}

			case RdexPointTableModel.NAME_COLUMN :
				{
					return pointRow.getPointName();
				}

			case RdexPointTableModel.INTERVAL_COLUMN :
				{
					Integer newint = new Integer(pointRow.getPointInterval());
					return newint.toString();
				}

			case RdexPointTableModel.FUNCTION_COLUMN :
				{
					return pointRow.getPointFunction();
				}

			case RdexPointTableModel.MIN_COLUMN :
				{
					Double newdouble = new Double(pointRow.getPointMin());
					return newdouble.toString();
				}

			case RdexPointTableModel.MAX_COLUMN :
				{
					Double newdouble = new Double(pointRow.getPointMax());
					return newdouble.toString();
				}

			case RdexPointTableModel.DELTA_COLUMN :
				{
					Double newdouble = new Double(pointRow.getPointDelta());
					return newdouble.toString();
				}

			case RdexPointTableModel.MAXSTART_COLUMN :
				{
					Boolean newbool = new Boolean(pointRow.getPointMaxStart());
					return newbool.toString();
				}

			default :
				return null;
		}

	}

	public RdexFileIO getRdexFileIO()
	{
		if(rdexFileIO == null)
		{
			rdexFileIO = new RdexFileIO(rdexFile)
			{
			};
		}
		return rdexFileIO;
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		
		// update the file with new value
		getRdexFileIO().writeRdexFileUpdate(rdexFileIO.readFile(new File(rdexFile)), new File(rdexFile), row, col, value);
		// update table cell
		Vector vec = getAllRdexPoints();
		RdexPoint point = (RdexPoint) vec.elementAt(row);
		if(col == 0){
			point.setPointType(value.toString());
		}else if(col == 1){
			point.setPointName(value.toString());
		}else if(col == 2){
			Integer newint = new Integer(value.toString());
			point.setPointInterval(newint.intValue());
		}else if(col == 3){
			point.setPointFunction(value.toString());
		}else if(col == 4){
			Double newdouble = new Double(value.toString());
			point.setPointMin(newdouble.doubleValue());
		}else if(col == 5){
			Double newdouble = new Double(value.toString());
			point.setPointMax(newdouble.doubleValue());
		}else if(col == 6){
			Double newdouble = new Double(value.toString());
			point.setPointDelta(newdouble.doubleValue());
		}else if(col == 7){
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