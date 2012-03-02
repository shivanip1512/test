package com.cannontech.common.model;

/**
 * Adding Yukon addressing to the ZigbeeTextMessage to be used as a spring model.
 */
public class ZigbeeTextMessageDto extends YukonTextMessage {

    private int accountId;
    private int inventoryId;
    private int gatewayId;
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public int getInventoryId() {
        return inventoryId;
    }
    
    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(int gatewayId) {
        this.gatewayId = gatewayId;
    }
}