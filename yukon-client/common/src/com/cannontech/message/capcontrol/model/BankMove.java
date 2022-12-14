package com.cannontech.message.capcontrol.model;


public class BankMove extends ItemCommand {
	private int oldFeederId = -1;
	private boolean permanentMove = false;
	private int newFeederId = -1;
	private double displayOrder = -1;
    private double closeOrder = -1;
    private double tripOrder = -1;
    
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

    public double getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(double displayOrder) {
        this.displayOrder = displayOrder;
    }

    public double getCloseOrder() {
        return closeOrder;
    }

    public void setCloseOrder(double closeOrder) {
        this.closeOrder = closeOrder;
    }

    public double getTripOrder() {
        return tripOrder;
    }

    public void setTripOrder(double tripOrder) {
        this.tripOrder = tripOrder;
    }

}