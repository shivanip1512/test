package com.cannontech.message.capcontrol.model;


public class BankMove extends ItemCommand {
	private int oldFeederId = -1;
	private boolean permanentMove = false;
	private int newFeederId = -1;
	private float displayOrder = -1;
    private float closeOrder = -1;
    private float tripOrder = -1;
    
	public BankMove() {
		super();
		setCommandId(CommandType.MOVE_BANK.getCommandId());
	}

    public int getOldFeederId() {
        return oldFeederId;
    }

    public void setOldFeederId(int oldFeederId) {
        this.oldFeederId = oldFeederId;
    }

    public boolean isPermanentMove() {
        return permanentMove;
    }

    public void setPermanentMove(boolean permanentMove) {
        this.permanentMove = permanentMove;
    }

    public int getNewFeederId() {
        return newFeederId;
    }

    public void setNewFeederId(int newFeederId) {
        this.newFeederId = newFeederId;
    }

    public float getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(float displayOrder) {
        this.displayOrder = displayOrder;
    }

    public float getCloseOrder() {
        return closeOrder;
    }

    public void setCloseOrder(float closeOrder) {
        this.closeOrder = closeOrder;
    }

    public float getTripOrder() {
        return tripOrder;
    }

    public void setTripOrder(float tripOrder) {
        this.tripOrder = tripOrder;
    }

}