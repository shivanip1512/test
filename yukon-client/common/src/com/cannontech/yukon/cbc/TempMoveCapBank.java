package com.cannontech.yukon.cbc;

/**
 */
public class TempMoveCapBank extends com.cannontech.yukon.cbc.CapControlMessage 
{	
	private int oldFeedID = -1;
	private boolean permanentMove = false;
	private int newFeedID = -1;
	private int capBankID = -1;
	private float displayOrder = -1;
    private float closeOrder = -1;
    private float tripOrder = -1;

    /**
	 * comment.
	 */
	public TempMoveCapBank() 
	{
		super();
	}

	public TempMoveCapBank( int oldFeedID_, int newFeedID_, int capBankID_, float order_, float cO, float tO, boolean permMove_ ) 
	{
		super();

		oldFeedID = oldFeedID_;
		newFeedID = newFeedID_;
		capBankID = capBankID_;
		displayOrder = order_; 
		permanentMove = permMove_;
        closeOrder = cO;
        tripOrder = tO;
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
	 * Returns the permanentMove.
	 * @return int
	 */
	public boolean getPermanentMove() {
		return permanentMove;
	}

	/**
	 * Returns the order.
	 * @return int
	 */
	public float getOrder() {
		return displayOrder;
	}
    
    public float getCloseOrder() {
        return closeOrder;
    }

    public float getTripOrder() {
        return tripOrder;
    }
}
