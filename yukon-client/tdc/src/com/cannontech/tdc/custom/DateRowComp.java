package com.cannontech.tdc.custom;

import java.util.Date;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;

/**
 * @author rneuharth
 *
 * A comparator class to find the index of the first Date class
 * and compare its value to another Date class
 */
public class DateRowComp
{

	public DateRowComp()
	{
		super();
	}

	public int compare(Object o1, Object o2)
	{
		try
		{
			Vector rowA = (Vector)o1;
			Vector rowB = (Vector)o2;
			
			Date timeA = findDateItem( rowA );
			Date timeB = findDateItem( rowB );
			
			
			//null is considered to be of least value
			if( timeA == null )
				return -1;
			else if( timeB == null )
				return 1;
			
			//the Date class implements Comparable......excellent Smithers!			
			return( timeA.compareTo(timeB) );				
		}
		catch( Exception e )
		{
			CTILogger.error( "Something went wrong with sorting of our row, ignoring sorting rules", e );
			return 0; 
		}
		
	}


	/**
	 * Finds the first instance of a Date class and returnes it
	 * @param row
	 * @return
	 */
	private Date findDateItem( Vector row )
	{
		if( row == null )
			return null;
			
		for( int i = 0; i < row.size(); i++ )
			if( row.get(i).getClass().getSuperclass() == Date.class )
				return (Date)row.get(i);

		return null;
	}
}
