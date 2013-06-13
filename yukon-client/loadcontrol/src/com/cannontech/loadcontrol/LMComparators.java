package com.cannontech.loadcontrol;

import java.util.Comparator;

import com.cannontech.messaging.message.loadcontrol.data.Data;

/**
 * Created for defining comparators for all types in LM
 * 
 */
public final class LMComparators 
{
	/* Used to compare names for ILMData */
    public static Comparator<Data> lmDataNameComp = new Comparator<Data>()
	{
		public int compare(Data o1, Data o2)
		{
			String thisVal = null, anotherVal = null;
			
			if(o1 instanceof Data && o2 instanceof Data)
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
