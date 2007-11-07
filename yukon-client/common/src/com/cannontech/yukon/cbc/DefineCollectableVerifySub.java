package com.cannontech.yukon.cbc;

import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;

public class DefineCollectableVerifySub extends DefineCollectableCBCMessage {

	public static final int CTI_VERIFYSUB_ID = 514;
	public DefineCollectableVerifySub() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * create method comment.
	 */
	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException 
	{
		return new CBCVerifySubBus();
	}

	/**
	 * getComparator method comment.
	 */
	public com.roguewave.tools.v2_0.Comparator getComparator() 
	{
		return new Comparator() {
		  public int compare(Object x, Object y) {
				if( x == y )
					return 0;
				else
					return -1;
		  }
		};
	}
	/**
	 * getCxxClassId method comment.
	 */
	public int getCxxClassId() 
	{
		return CTI_VERIFYSUB_ID;
	}
	/**
	 * getCxxStringId method comment.
	 */
	public String getCxxStringId() 
	{
		return DefineCollectable.NO_STRINGID;
	}
	/**
	 * getJavaClass method comment.
	 */
	public Class getJavaClass() 
	{
		return CBCVerifySubBus.class;
	}
	/**
	 * restoreGuts method comment.
	 */
	public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{		
		//Should never be called
		super.restoreGuts( obj, vstr, polystr );
		throw new java.lang.Error( this.getClass().getName() + " should never be receieved by the client" );
	}

	/**
	 * saveGuts method comment.
	 */
	public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
	{
		super.saveGuts( obj, vstr, polystr );
	
		CBCVerifySubBus verifySub = (CBCVerifySubBus)obj;
	
		vstr.insertUnsignedInt( (int) (verifySub.getAction()));
		vstr.insertUnsignedInt( (int) verifySub.getSubId());
		vstr.insertUnsignedInt( (int) verifySub.getStrategy());
		vstr.insertUnsignedInt( (long) verifySub.getCbInactivityTime());

	}
}
