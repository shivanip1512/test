/*
 * Created on Sep 21, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cannontech.yukon.server.cache.bypass;

/*
 * 
 * @author jdayton
 *
 * This class exists to provide an alternative to an extra Map to Map relationship
 * when presented with multiple key values that must be combined to uniquely store
 * an object in a primary map.
 */
public class MapKeyInts 
{
	int primaryID;
	int secondaryID;
	
	public MapKeyInts(int primID, int secID)
	{
		primaryID = primID;
		secondaryID = secID;
	}
	
	public int getPrimaryID()
	{
		return primaryID;
	}
	
	public int getSecondaryID()
	{
		return secondaryID;
	}
	/**
	 * keep this consistent with .equals() pleez
	 * o1.equals(o2) => o1.hashCode() == o2.hashCode()
	 * 
	 * unique most of the time! No real way to form a unique hash 
	 * with 2 32bit ints and return them in a 32bit value
	 * 
	 * "That is a manly hash code!" 
	 * 			--MFisher
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() 
	{
		// primaryID will be less than 2^16 = 65536
		return primaryID << 16 | secondaryID;
	}
	
	public boolean equals(Object o)
	{
		if( o == null )
			return false;
		else if(o instanceof MapKeyInts)
		{
			return this.getPrimaryID() == ((MapKeyInts)o).getPrimaryID() 
				&& this.getSecondaryID() == ((MapKeyInts)o).getSecondaryID() ;
		}
		return false;
	} 
}
