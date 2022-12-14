package com.cannontech.common.gui.util;

import java.util.Date;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

public class SortTableModelWrapper extends AbstractTableModel implements TableModelListener
{
	private  SortableTableModel realModel;

	private int swapsMade = 0;
	private int sortingColumn = -1;


public SortTableModelWrapper(SortableTableModel model) 
{
	if (model == null)
		throw new IllegalArgumentException("null models are not allowed");
		
	this.realModel = model;
	realModel.addTableModelListener(this);
}
public void addTableModelListener(TableModelListener l) 
{
	realModel.addTableModelListener(l);
}
private int compareRowsByColumn( int row1, int row2 )
{
	int res = 0;

	// Check for nulls
	Object o1 = realModel.getValueAt(row1, sortingColumn);
	Object o2 = realModel.getValueAt(row2, sortingColumn);


	// If both values are null return 0
	if (o1 == null && o2 == null)
		return 0;
	else if (o1 == null )
		return -1;  // Define null less than everything. 
	else if (o2 == null )
		return 1;



	Class type1 = o1.getClass();
	Class type2 = o2.getClass();
			
	// if the 2 row types are not the same
	if( type1 != type2 )
	{
		if (type1.getSuperclass() == java.lang.Number.class)
			res = 1;
		else if (type2.getSuperclass() == java.lang.Number.class)
			res = -1;
		else if (type1.getSuperclass() == String.class )
			res = 1;
		else if (type2.getSuperclass() == String.class )
			res = -1;
		else
			res = 1;
	}
	else if (type1.getSuperclass() == java.lang.Number.class)
	{
		/* We copy all returned values from the getValue call in case
		an optimised model is reusing one object to return many values.
		The Number subclasses in the JDK are immutable and so will not be used in 
		this way but other subclasses of Number might want to do this to save 
		space and avoid unnecessary heap allocation. 
		*/
		Number n1 = (Number)o1;
		double d1 = n1.doubleValue();
		Number n2 = (Number)o2;
		double d2 = n2.doubleValue();
		if (d1 < d2)
			res = 1;
		else if (d1 > d2)
			res = -1;
		else
			res = 0;
	}
	else if (type1.getSuperclass() == java.util.Date.class)
	{
		Date d1 = (Date)o1;
		long n1 = d1.getTime();
		Date d2 = (Date)o2;
		long n2 = d2.getTime();
		if (n1 < n2)
			res = -1; // we want the dates to be descending
		else if (n1 > n2)
			res = 1;
		else
			res = 0;
	}
	else if (type1 == String.class)
	{
		String s1 = (String)o1;
		String s2 = (String)o2;
      int result = s1.compareTo(s2);
      
      Double d1, d2;
      try
      { //if there is numbers in the string, use those values for a comparison
         d1 = Double.valueOf( java.text.DecimalFormat.getInstance().parse(s1).toString() );
         d2 = Double.valueOf( java.text.DecimalFormat.getInstance().parse(s2).toString() );
         result = d1.compareTo(d2);
      }
      catch( Exception e ) 
      {};  //no big thang, just use the Strings compare to
      
      
		if (result < 0)
			res =  1;
		else if (result > 0)
			res = -1;
		else
			res = 0;
	}
	else
	{
		String s1 = o1.toString();
		String s2 = o2.toString();
		int result = s1.compareTo(s2);
		
		if (result < 0)
			res = 1;
		else if (result > 0)
			res = -1;
		else
			res = 0;
	}

	//if we have equal values, lets try to sort by the Auxilary sorting column
	if( res == 0 )
	{
		res = findAuxSortColumn( row1, row2 );
	}


	return res;
}

public Class getColumnClass(int columnIndex)
{
	return realModel.getColumnClass(columnIndex);
}
public int getColumnCount()
{
	return realModel.getColumnCount();
}
public String getColumnName(int columnIndex)
{
	return realModel.getColumnName(columnIndex);
}
public TableModel getRealDataModel() 
{
	return realModel;
}
// TableModel pass-through methods follow ...

public int getRowCount()
{
	return realModel.getRowCount();
}
/* There is a problem here: this method is called from all sorts of
	threads( for painting purposes, update table purposes, etc..).
	The SortTableModelWrapper is not being informed
	of a change in the realModel. We will just wait for the change to propogate.
	For now just have this method wait for the realModel to get all the data */
		
public Object getValueAt(int row, int column)
{
	return realModel.getValueAt( row, column);
}
public boolean isCellEditable(int rowIndex, int columnIndex)
{
	return realModel.isCellEditable(rowIndex, columnIndex);
}
public void removeTableModelListener(TableModelListener l)
{
	realModel.removeTableModelListener(l);
}
/**
 * Insert the method's description here.
 * Creation date: (6/9/00 10:11:11 AM)
 */
public void setTableHeaderListener( java.awt.event.MouseAdapter listener,
	JTableHeader hdr )
{
	//if( table == null )
		//return;

	//javax.swing.table.JTableHeader hdr = (javax.swing.table.JTableHeader)table.getTableHeader();
	hdr.setToolTipText("Dbl Click on a column header to sort");
	hdr.addMouseListener( listener );	
}
public void setValueAt(Object aValue, int row, int column)
{
	realModel.setValueAt(aValue, row, column);
}
/* NOT USED YET */

// This is a home-grown implementation which we have not had time
// to research - it may perform poorly in some circumstances. It
// requires twice the space of an in-place algorithm and makes
// NlogN assigments shuttling the values between the two
// arrays. The number of compares appears to vary between N-1 and
// NlogN depending on the initial order but the main reason for
// using it here is that, unlike qsort, it is stable.
private void shuttlesort(int from[], int to[], int low, int high)
{	
	if (high - low < 2)
	{
		return;
	}
	int middle = (low + high) / 2;	
	shuttlesort(to, from, low, middle);
	shuttlesort(to, from, middle, high);
	int p = low;
	int q = middle;

	/* This is an optional short-cut; at each recursive call,
	check to see if the elements in this subset are already
	ordered.  If so, no further comparisons are needed; the
	sub-array can just be copied.  The array must be copied rather
	than assigned otherwise sister calls in the recursion might
	get out of sinc.  When the number of elements is three they
	are partitioned so that the first set, [low, mid), has one
	element and and the second, [mid, high), has two. We skip the
	optimisation when the number of elements is three or less as
	the first compare in the normal merge will produce the same
	sequence of steps. This optimisation seems to be worthwhile
	for partially ordered lists but some analysis is needed to
	find out how the performance drops to Nlog(N) as the initial
	order diminishes - it may drop very quickly.  */
	
	if (high - low >= 4 && compareRowsByColumn(from[middle - 1], from[middle]) <= 0)
	{
		for (int i = low; i < high; i++)
		{
			to[i] = from[i];			
		}
		return;
	}

	// A normal merge.
	for (int i = low; i < high; i++)
	{
		if (q >= high || (p < middle && compareRowsByColumn(from[p], from[q]) <= 0))
		{
			to[i] = from[p];
			p++;
		}
		else
		{
			to[i] = from[q];
			q++;
		}
	}
}
public void sort(int column )
{
	sortingColumn = column;
//  USE SHUTTLESORT WHEN PERFORMANCE BECOMES AN ISSUE
//	shuttlesort((int[])indexes.clone(), indexes, 0, indexes.length);

	int rowCount = getRowCount();

	for (int i = 0; i < rowCount; i++)
	{
		if( realModel.isRowSelectedBlank( i ) )
			continue;

		for (int j = i + 1; j < rowCount; j++)
		{
			if( realModel.isRowSelectedBlank( j ) )
				continue;

			int res = compareRowsByColumn(i, j);
			if( res < 0)
			{
				swapsMade++;

				/* DO THE TABLE MODELS OWN SWAPPING HERE */
				// CUSTOMIZED CODE
				realModel.rowDataSwap( i, j );
			}
		}
	}

	// if we are already in order, reverse the sorting routine
	if( swapsMade == 0 )
		sortDescending( column );
		
	swapsMade = 0;
	fireTableDataChanged();
}
private void sortDescending( int column )
{
	sortingColumn = column;
//  USE SHUTTLESORT WHEN PERFORMANCE BECOMES AN ISSUE
//	shuttlesort((int[])indexes.clone(), indexes, 0, indexes.length);

	int rowCount = getRowCount();

	for (int i = 0; i < rowCount; i++)
	{
		if( realModel.isRowSelectedBlank( i ) )
			continue;

		for (int j = i + 1; j < rowCount; j++)
		{
			if( realModel.isRowSelectedBlank( j ) )
				continue;

			if (compareRowsByColumn(i, j) > 0)
			{
				/* DO YOUR OWN SWAPPING HERE */				
				realModel.rowDataSwap( i, j );
			}
		}		
	}

}
/**
 * Sort the equal rows by using a secondary column (like Date)
 * @param row1
 * @param row2
 * @return
 */
private int findAuxSortColumn( int row1, int row2 )
{
	for( int i = 0; i < realModel.getColumnCount(); i++ )
	{
		// Check for nulls
		Object o1 = realModel.getValueAt(row1, i);
		Object o2 = realModel.getValueAt(row2, i);


		// If both values are null move to the next column
		if( o1 == null || o2 == null )
			continue;

		// If both values are not the same type move to the next column
		Class type1 = o1.getClass();
		Class type2 = o2.getClass();
		if( type1 != type2 )
			continue;


		if( type1.getSuperclass() == java.util.Date.class )
		{			
			Date d1 = (Date)o1;
			long n1 = d1.getTime();
			Date d2 = (Date)o2;
			long n2 = d2.getTime();
			
			if (n1 < n2)
				return -1; // we want the dates to be descending
			else if (n1 > n2)
				return 1;
			else
				return 0;
		}
	}
	
	//they are equal and we do not know what to do
	return 0;
}

public void tableChanged(TableModelEvent e)
{
	// do stuff here if needed on events from the real model
}
}
