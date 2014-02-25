package com.cannontech.stars.dr.program.service;


public final class HardwareEnrollmentInfo {

    private final int inventoryId;
    private final int accountId;
    private final int loadGroupId;
    private final int relayNumber;
    private final int programId;

    public HardwareEnrollmentInfo(int inventoryId, int accountId, int loadGroupId, int relayNumber,
                                  int programId) {
        this.inventoryId = inventoryId;
        this.accountId = accountId;
        this.loadGroupId = loadGroupId;
        this.relayNumber = relayNumber;
        this.programId = programId;
    }

    public int getInventoryId() {
        return inventoryId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getLoadGroupId() {
        return loadGroupId;
    }

    public int getRelayNumber() {
        return relayNumber;
    }

    public int getProgramId() {
        return programId;
    }
}