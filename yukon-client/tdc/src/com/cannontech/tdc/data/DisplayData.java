package com.cannontech.tdc.data;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DisplayData implements java.io.Externalizable
{
	private long displayNumber = Display.UNKNOWN_DISPLAY_NUMBER;
	private int filterID = CtiUtilities.NONE_ZERO_ID;

	// a list a properties that has different meanings for each display
	private int prop0 = -1;
	private int prop1 = -1;
	private int prop2 = -1;
	private int prop3 = -1;
	private int prop4 = -1;
	private int prop5 = -1;


	//never let this guy be null
	private ColumnData[] columnData = new ColumnData[0];


	private static final String FD = String.valueOf( (char)16 );

	/**
	 * Keep this guy public for reflection sake!
	 */
	public DisplayData()
	{
		super();
	}


	public DisplayData( long displayNumber_, int filterID_, ColumnData[] colData_ )
	{
		this();
		setDisplayNumber( displayNumber_ );
		setFilterID( filterID_ );
		setColumnData( colData_ );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/30/2002 3:31:06 PM)
	 * @param oi java.io.ObjectInput
	 */
	public void readExternal(java.io.ObjectInput oIn)
	{
		try
		{
			int length = oIn.readInt();
			byte[] data = new byte[length];

			oIn.read( data );

			StringTokenizer statTokenizer = new StringTokenizer( new String(data), FD );

			setDisplayNumber( Long.parseLong(statTokenizer.nextToken()) );
			setFilterID( Integer.parseInt(statTokenizer.nextToken()) );
			

			setProp0( Integer.parseInt(statTokenizer.nextToken()) );
			setProp1( Integer.parseInt(statTokenizer.nextToken()) );
			setProp2( Integer.parseInt(statTokenizer.nextToken()) );
			setProp3( Integer.parseInt(statTokenizer.nextToken()) );
			setProp4( Integer.parseInt(statTokenizer.nextToken()) );
			setProp5( Integer.parseInt(statTokenizer.nextToken()) );

			ArrayList tmpList = new ArrayList(16);			
			while( statTokenizer.hasMoreTokens() )
			{
				tmpList.add( 
					new ColumnData(
						statTokenizer.nextToken(),
						Integer.parseInt(statTokenizer.nextToken()),
						Integer.parseInt(statTokenizer.nextToken())) );
			}
			
			columnData = new ColumnData[tmpList.size()];			
			tmpList.toArray(columnData);
		}
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}

	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/30/2002 3:30:46 PM)
	 * @param oo java.io.ObjectOutput
	 */
	public void writeExternal(java.io.ObjectOutput oOut)
	{
		try
		{
			StringBuffer buf = new StringBuffer();
			buf.append( getDisplayNumber() + FD );
			buf.append( getFilterID() + FD );

			buf.append( getProp0() + FD );
			buf.append( getProp1() + FD );
			buf.append( getProp2() + FD );
			buf.append( getProp3() + FD );
			buf.append( getProp4() + FD );
			buf.append( getProp5() + FD );
			
			for( int i = 0; getColumnData() != null && i < getColumnData().length; i++ )
			{
				buf.append( getColumnData()[i].getColumnName() + FD );
				buf.append( getColumnData()[i].getOrdering() + FD );
				buf.append( getColumnData()[i].getWidth() + FD );
			}

			oOut.writeInt( buf.length() );
			oOut.writeBytes( buf.toString() );
		}
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}

	}
	
	/**
	 * @return
	 */
	public ColumnData[] getColumnData()
	{
		return columnData;
	}

	/**
	 * @return
	 */
	public long getDisplayNumber()
	{
		return displayNumber;
	}

	/**
	 * @return
	 */
	public int getFilterID()
	{
		return filterID;
	}

	/**
	 * @param datas
	 */
	public void setColumnData(ColumnData[] datas)
	{
		//never let this guy be null
		if( datas == null )
			columnData = new ColumnData[0];
		else
			columnData = datas;
	}

	/**
	 * @param l
	 */
	public void setDisplayNumber(long l)
	{
		displayNumber = l;
	}

	/**
	 * @param i
	 */
	public void setFilterID(int i)
	{
		filterID = i;
	}

	/**
	 * @return
	 */
	public int getProp0()
	{
		return prop0;
	}

	/**
	 * @return
	 */
	public int getProp1()
	{
		return prop1;
	}

	/**
	 * @return
	 */
	public int getProp2()
	{
		return prop2;
	}

	/**
	 * @return
	 */
	public int getProp3()
	{
		return prop3;
	}

	/**
	 * @return
	 */
	public int getProp4()
	{
		return prop4;
	}

	/**
	 * @return
	 */
	public int getProp5()
	{
		return prop5;
	}

	/**
	 * @param i
	 */
	public void setProp0(int i)
	{
		prop0 = i;
	}

	/**
	 * @param i
	 */
	public void setProp1(int i)
	{
		prop1 = i;
	}

	/**
	 * @param i
	 */
	public void setProp2(int i)
	{
		prop2 = i;
	}

	/**
	 * @param i
	 */
	public void setProp3(int i)
	{
		prop3 = i;
	}

	/**
	 * @param i
	 */
	public void setProp4(int i)
	{
		prop4 = i;
	}

	/**
	 * @param i
	 */
	public void setProp5(int i)
	{
		prop5 = i;
	}

}
