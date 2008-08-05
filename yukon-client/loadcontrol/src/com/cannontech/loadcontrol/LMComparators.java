package com.cannontech.loadcontrol;

import java.util.Comparator;

import com.cannontech.loadcontrol.data.ILMData;

/**
 * Created for defining comparators for all types in LM
 * 
 */
public final class LMComparators 
{
	public static Comparator<ILMData> lmDataYukonID = new Comparator<ILMData>()
	{
		public int compare(ILMData o1, ILMData o2)
		{
			int thisVal = o1.getYukonID().intValue();
			int anotherVal = o2.getYukonID().intValue();
			return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
		}
		public boolean equals(Object obj)
		{
			return false;
		}
	};
	
	/* Used to compare names for ILMData */
    public static Comparator<ILMData> lmDataNameComp = new Comparator<ILMData>()
	{
		public int compare(ILMData o1, ILMData o2)
		{
			String thisVal = null, anotherVal = null;
			
			if(o1 instanceof ILMData && o2 instanceof ILMData)
			{
				thisVal = o1.getYukonName();
				anotherVal = o2.getYukonName();
			}
			else
			{	// unknown type
				throw new IllegalArgumentException("Unhandled types or the 2 objects being compared are not the same object types in comparator of : " + this.getClass().toString() );
			}

			
			return ( thisVal.compareToIgnoreCase(anotherVal) );
		}
		
		public boolean equals(Object obj)
		{
			return false;
		}
	};

}
