package com.cannontech.tdc.filter;

import java.util.BitSet;

import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public abstract class TDCFilterBase implements ITDCFilter
{
	private int id = CtiUtilities.NONE_ZERO_ID;
	private BitSet conditions = new BitSet();
	private Object[] values = null;
	
	public int getFilterID()
	{
		return id;
	}

	void setFilterID( int id_ )
	{
		id = id_;
	}

	public BitSet getConditions()
	{
		return conditions;
	}
	
	public void setConditions( BitSet conditions_ )
	{
		conditions = conditions_;
	}
	
	public Object[] getValues()
	{
		return values;
	}

	public String toString()
	{
		return getName();
	}
	
	public boolean equals( Object o )	
	{
		return
			(o != null)
			&& (o instanceof ITDCFilter)
			&& ( ((ITDCFilter)o).getFilterID() == getFilterID() );
	
	}
	
	public int hashCode()
	{
		return getFilterID();
	}

}
