package com.cannontech.tdc.data;

import java.io.IOException;

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
	// [0][x] - First table columnData
	// [1][x] - Second table columnData
	// [2][x] - Third table columnData ....etc.
	private ColumnData[][] columnData = new ColumnData[0][0];


	private static final String AS = String.valueOf( (char)0x1C );  //application seperator delimitor
	private static final String TS = String.valueOf( (char)0x1D );  //table seperator delimitor
	private static final String CS = String.valueOf( (char)0x1E );  //column seperator delimitor


	/**
	 * Keep this guy public for reflection sake!
	 */
	public DisplayData()
	{
		super();
	}


	public DisplayData( long displayNumber_, int filterID_, ColumnData[][] colData_ )
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
	public void readExternal(java.io.ObjectInput oIn) throws IOException
	{
		int length = oIn.readInt();
		byte[] data = new byte[length];

		oIn.read( data );
		String dataStr = new String(data);

		String[] mainTokenizer = dataStr.split( AS );

		setDisplayNumber( Long.parseLong(mainTokenizer[0]) );
		setFilterID( Integer.parseInt(mainTokenizer[1]) );
		setProp0( Integer.parseInt(mainTokenizer[2]) );
		setProp1( Integer.parseInt(mainTokenizer[3]) );
		setProp2( Integer.parseInt(mainTokenizer[4]) );
		setProp3( Integer.parseInt(mainTokenizer[5]) );
		setProp4( Integer.parseInt(mainTokenizer[6]) );
		setProp5( Integer.parseInt(mainTokenizer[7]) );


		// name1CS ord1CS wid1CS name2CS ord2CS wid2CS TS tb2name1CS tb2ordCS...CS TS
		if( mainTokenizer.length > 8 )
		{
			String[] tableToks = mainTokenizer[8].split(TS);
					
			columnData = new ColumnData[tableToks.length][0];
	
			for( int i = 0; i < tableToks.length; i++ )
			{
				String singleTable = tableToks[i];
				if( singleTable.length() <= 0 )
					continue;
	
				String[] realColsTok = singleTable.split( CS );
				ColumnData[] colsData = new ColumnData[ realColsTok.length / 3 ];
	
				for( int j = 0, k = 0; j < colsData.length; j++ )
				{
					colsData[j] =
						new ColumnData(
							realColsTok[k++],
							Integer.parseInt(realColsTok[k++]),
							Integer.parseInt(realColsTok[k++]));
				}
				
				columnData[i] = colsData;
			}
		}
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (1/30/2002 3:30:46 PM)
	 * @param oo java.io.ObjectOutput
	 */
	public void writeExternal(java.io.ObjectOutput oOut) throws IOException
	{
		StringBuffer buf = new StringBuffer();
		buf.append( getDisplayNumber() + AS );
		buf.append( getFilterID() + AS );

		buf.append( getProp0() + AS );
		buf.append( getProp1() + AS );
		buf.append( getProp2() + AS );
		buf.append( getProp3() + AS );
		buf.append( getProp4() + AS );
		buf.append( getProp5() + AS );
		
		for( int i = 0; getColumnData() != null && i < getColumnData().length; i++ )
		{
			ColumnData[] colData = getColumnData()[i];
			for( int j = 0; colData != null && j < colData.length; j++ )
			{
				buf.append( colData[j].getColumnName() + CS );
				buf.append( colData[j].getOrdering() + CS );
				buf.append( colData[j].getWidth() + CS );
			}

			buf.append( TS );
		}

		oOut.writeInt( buf.length() );
		oOut.writeBytes( buf.toString() );
	}
	
	/**
	 * @return
	 */
	public ColumnData[][] getColumnData()
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
	public void setColumnData(ColumnData[][] datas)
	{
		//never let this guy be null
		if( datas == null )
			columnData = new ColumnData[0][0];
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
