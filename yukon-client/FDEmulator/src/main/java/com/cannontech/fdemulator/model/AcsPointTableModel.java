/*
 * Created on Jan 4, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.fdemulator.model;

import java.awt.Color;
import java.io.*;
import java.util.*;

import com.cannontech.fdemulator.protocols.ACSPoint;
import com.cannontech.fdemulator.fileio.*;

public class AcsPointTableModel extends javax.swing.table.AbstractTableModel
{
	/* ROW DATA */
	private java.util.Vector allAcsPoints = null;
	private java.util.List currentAcsPoints = null;
	/* END - ROW DATA */

	//The columns and their column index	
	public static final int TYPE_COLUMN = 0;
	public static final int REMOTE_COLUMN = 1;
	public static final int POINT_COLUMN = 2;
	public static final int CATEGORY_COLUMN = 3;
	public static final int INTERVAL_COLUMN = 4;
	public static final int FUNCTION_COLUMN = 5;
	public static final int MIN_COLUMN = 6;
	public static final int MAX_COLUMN = 7;
	public static final int DELTA_COLUMN = 8;
	public static final int MAXSTART_COLUMN = 9;
	public static final String acsFile = "resource/acs_points.cfg";
	private static final String LF = System.getProperty("line.separator");
	private BufferedReader file;
	private AcsFileIO acsFileIO = null;

	//The column names based on their column index
	private static final String[] COLUMN_NAMES = { "Type", "Remote", "Point", "Category", "Interval", "Function", "Min", "Max", "Delta", "MaxStart" };

	//The color schemes - based on the schedule status
	private static Color[] cellColors = {
		//Enabled subbus
		Color.green,
		//Disabled subbus
		Color.red,
		//Pending subbus
		Color.yellow };

	public AcsPointTableModel()
	{
		super();
	}

	public void clear()
	{
		getAllAcsPoints().removeAllElements();

		currentAcsPoints = getAllAcsPoints();

		fireTableDataChanged();
	}

	public void forcePaintTableRowUpdated(int minLocation, int maxLocation)
	{
		fireTableRowsUpdated(minLocation, maxLocation);
	}

	public void addRow(ACSPoint pointRow)
	{
		getAllAcsPoints().add(pointRow);
		fireTableDataChanged();
	}

	private java.util.Vector getAllAcsPoints()
	{
		if (allAcsPoints == null)
			allAcsPoints = new java.util.Vector(20);
		return allAcsPoints;
	}

	public java.awt.Color getCellColor(ACSPoint pointRow)
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

	public synchronized ACSPoint getRowAt(int rowIndex)
	{
		if (rowIndex >= 0 && rowIndex < getRowCount())
			return (ACSPoint) getCurrentAcsPoints().get(rowIndex);
		else
			return null;
	}

	private java.util.List getCurrentAcsPoints()
	{
		if (currentAcsPoints == null)
			currentAcsPoints = getAllAcsPoints();

		return currentAcsPoints;
	}

	public int getRowCount()
	{

		return getCurrentAcsPoints().size();
	}

	public Object getValueAt(int row, int col)
	{
		ACSPoint pointRow = getRowAt(row);

		switch (col)
		{
			case AcsPointTableModel.TYPE_COLUMN :
				{
					return pointRow.getPointType();
				}

			case AcsPointTableModel.REMOTE_COLUMN :
				{
					Integer newint = new Integer(pointRow.getPointRemote());
					return newint.toString();
				}

			case AcsPointTableModel.POINT_COLUMN :
				{
					Integer newint = new Integer(pointRow.getPointNumber());
					return newint.toString();
				}

			case AcsPointTableModel.CATEGORY_COLUMN :
				{
					return pointRow.getPointCategory();
				}

			case AcsPointTableModel.INTERVAL_COLUMN :
				{
					Integer newint = new Integer(pointRow.getPointInterval());
					return newint.toString();
				}

			case AcsPointTableModel.FUNCTION_COLUMN :
				{
					return pointRow.getPointFunction();
				}

			case AcsPointTableModel.MIN_COLUMN :
				{
					Double newdouble = new Double(pointRow.getPointMin());
					return newdouble.toString();
				}

			case AcsPointTableModel.MAX_COLUMN :
				{
					Double newdouble = new Double(pointRow.getPointMax());
					return newdouble.toString();
				}

			case AcsPointTableModel.DELTA_COLUMN :
				{
					Double newdouble = new Double(pointRow.getPointDelta());
					return newdouble.toString();
				}

			case AcsPointTableModel.MAXSTART_COLUMN :
				{
					Boolean newbool = new Boolean(pointRow.getPointMaxStart());
					return newbool.toString();
				}

			default :
				return null;
		}

	}

	public AcsFileIO getAcsFileIO()
	{
		if(acsFileIO == null)
		{
			acsFileIO = new AcsFileIO(acsFile)
			{
			};
		}
		return acsFileIO;
	}
	
	public void setValueAt(Object value, int row, int col)
	{
		
		// update the file with new value
		getAcsFileIO().writeACSFileUpdate(acsFileIO.readFile(new File(acsFile)), new File(acsFile), row, col, value);
		// update table cell
		Vector vec = getAllAcsPoints();
		ACSPoint point = (ACSPoint) vec.elementAt(row);
		if(col == 0){
			point.setPointType(value.toString());
		}else if(col == 1){
			Integer newint = new Integer(value.toString());
			point.setPointRemote(newint.intValue());
		}else if(col == 2){
			Integer newint = new Integer(value.toString());
			point.setPointNumber(newint.intValue());
		}else if(col == 3){
			point.setPointCategory(value.toString());
		}else if(col == 4){
			Integer newint = new Integer(value.toString());
			point.setPointInterval(newint.intValue());
		}else if(col == 5){
			point.setPointFunction(value.toString());
		}else if(col == 6){
			Double newdouble = new Double(value.toString());
			point.setPointMin(newdouble.doubleValue());
		}else if(col == 7){
			Double newdouble = new Double(value.toString());
			point.setPointMax(newdouble.doubleValue());
		}else if(col == 8){
			Double newdouble = new Double(value.toString());
			point.setPointDelta(newdouble.doubleValue());
		}else if(col == 9){
			Boolean bool = new Boolean(value.toString());
			point.setPointMaxStart(bool.booleanValue());
		}
		vec.setElementAt(point,row);
		// fire a repaint of the cell
		fireTableCellUpdated(row, col);
		
	}

	public boolean isCellEditable(int row, int column)
	{
		//return false;
		return true;
		
	}
}