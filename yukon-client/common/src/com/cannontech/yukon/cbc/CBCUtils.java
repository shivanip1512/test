package com.cannontech.yukon.cbc;

import java.util.Comparator;

import com.cannontech.clientutils.CTILogger;

/**
 * @author ryan
 *
 * Comment
 */
public final class CBCUtils
{

	public static final Comparator SUB_AREA_COMPARATOR = new Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			try
			{
				String thisArea = ((SubBus)o1).getCcArea();
				String anotherArea = ((SubBus)o2).getCcArea();
				
				if( !thisArea.equalsIgnoreCase(anotherArea) )
					return( thisArea.compareToIgnoreCase(anotherArea) );
				
				//if the Area Names	are equal, we need to sort by SubName
				String thisName = ((SubBus)o1).getCcName();
				String anotherName = ((SubBus)o2).getCcName();
				
				return( thisName.compareToIgnoreCase(anotherName) );				
			}
			catch( Exception e )
			{
				CTILogger.error( "Something went wrong with sorting, ignoring sorting rules", e );
				return 0; 
			}
			
		}
	};

}
