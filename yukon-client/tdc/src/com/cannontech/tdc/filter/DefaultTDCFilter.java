package com.cannontech.tdc.filter;

import java.util.BitSet;

/**
 * @author rneuharth
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultTDCFilter extends TDCFilterBase
{
	public DefaultTDCFilter()
	{
		super();
		
		//this filter shows every ting..
		BitSet allBitSet = new BitSet();
		allBitSet.clear();  //make them all NOT set!!

		setConditions( allBitSet );
	}


	/* (non-Javadoc)
	 * @see com.cannontech.tdc.filter.ITDCFilter#getName()
	 */
	public String getName()
	{
		return "(No Filter)";
	}

	/* (non-Javadoc)
	 * @see com.cannontech.tdc.filter.ITDCFilter#getDescription()
	 */
	public String getDescription()
	{
		return "(No Filter)";
	}

}
