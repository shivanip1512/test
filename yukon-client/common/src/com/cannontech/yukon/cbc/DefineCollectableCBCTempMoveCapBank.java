package com.cannontech.yukon.cbc;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;

public class DefineCollectableCBCTempMoveCapBank extends DefineCollectableCBCMessage 
{
	//RogueWave classId
	public static final int CTI_TEMPMOVEBANK_ID = 513;
	/**
	 * DefineCollectableScheduleCommand constructor comment.
	 */
	public DefineCollectableCBCTempMoveCapBank() 
	{
		super();
	}
	/**
	 * create method comment.
	 */
	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException 
	{
		return new CBCTempMoveCapBank();
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
		return CTI_TEMPMOVEBANK_ID;
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
		return CBCTempMoveCapBank.class;
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
	
		CBCTempMoveCapBank tmpMove = (CBCTempMoveCapBank)obj;
	
		vstr.insertUnsignedInt( (long) (tmpMove.getPermanentMove() ? 1 : 0) );
		vstr.insertUnsignedInt( (long) tmpMove.getOldFeedID() );
		vstr.insertUnsignedInt( (long) tmpMove.getCapBankID() );
		vstr.insertUnsignedInt( (long) tmpMove.getNewFeedID() );
		vstr.insertUnsignedInt( (long) tmpMove.getOrder() );
	}
}
