package com.cannontech.database.data.lite;

import org.apache.commons.lang.builder.HashCodeBuilder;


/*
 */
public abstract class LiteBase implements java.io.Serializable, Comparable
{
	private int liteType;
	private int liteID;

	/**
	 * Insert the method's description here.
	 * Creation date: (3/23/00 3:15:58 PM)
	 * @return int
	 * @param val java.lang.Object
	 */
	public int compareTo(Object val) 
	{
		int thisVal = getLiteID();
		int anotherVal = ((LiteBase)val).getLiteID();
	
		return (thisVal<anotherVal ? -1 : (thisVal==anotherVal ? 0 : 1));
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (11/17/00 4:28:38 PM)
	 * @return boolean
	 */
	public boolean equals(Object o)
	{
		if( o == null )
			return false;
		else if( o instanceof LiteBase )
		{
			return ( ((LiteBase)o).getLiteID() == this.getLiteID()
						&& ((LiteBase)o).getLiteType() == this.getLiteType() );
		}
		else
			return false;
	}
	
	@Override
	public int hashCode() {
	    return new HashCodeBuilder(6709, 3511).append(liteType).append(liteID).toHashCode();
	}
		
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public int getLiteID() {
		return liteID;
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	public int getLiteType() {
		return liteType;
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	protected void setLiteID(int newValue) {
		this.liteID = newValue;
	}
	/**
	 * This method was created by Cannon Technologies Inc.
	 */
	protected void setLiteType(int newValue) {
		this.liteType = newValue;
	}

}
