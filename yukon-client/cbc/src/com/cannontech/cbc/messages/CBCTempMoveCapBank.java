package com.cannontech.cbc.messages;

/**
 */
public class CBCTempMoveCapBank extends com.cannontech.cbc.messages.CBCMessage 
{	
	private int oldFeedID = -1;
	private int newFeedID = -1;
	private int capBankID = -1;
	private int order = -1;

	/**
	 * comment.
	 */
	public CBCTempMoveCapBank() 
	{
		super();
	}

	public CBCTempMoveCapBank( int oldFeedID_, int newFeedID_, int capBankID_, int order_ ) 
	{
		super();

		oldFeedID = oldFeedID_;
		newFeedID = newFeedID_;
		capBankID = capBankID_;
		order = order_; 
	}


	/**
	 * Returns the capBankID.
	 * @return int
	 */
	public int getCapBankID() {
		return capBankID;
	}

	/**
	 * Returns the newFeedID.
	 * @return int
	 */
	public int getNewFeedID() {
		return newFeedID;
	}

	/**
	 * Returns the oldFeedID.
	 * @return int
	 */
	public int getOldFeedID() {
		return oldFeedID;
	}

	/**
	 * Returns the order.
	 * @return int
	 */
	public int getOrder() {
		return order;
	}

}
