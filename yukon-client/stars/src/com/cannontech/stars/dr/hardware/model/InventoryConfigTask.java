package com.cannontech.stars.dr.hardware.model;

import org.apache.commons.lang.StringUtils;

import com.cannontech.database.data.lite.LiteYukonUser;

public class InventoryConfigTask {
    private int inventoryConfigTaskId;
    private String taskName;
    private boolean sendInService;
    private int numberOfItems;
    private int numberOfItemsProcessed;
    private int energyCompanyId;
    private LiteYukonUser user;

    public int getInventoryConfigTaskId() {
        return inventoryConfigTaskId;
    }

    public void setInventoryConfigTaskId(int inventoryConfigTaskId) {
        this.inventoryConfigTaskId = inventoryConfigTaskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isSendInService() {
        return sendInService;
    }

    public void setSendInService(boolean sendInService) {
        this.sendInService = sendInService;
    }

    public int getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(int numberOfItems) {
        this.numberOfItems = numberOfItems;
    }

    public int getNumberOfItemsProcessed() {
        return numberOfItemsProcessed;
    }

    public void setNumberOfItemsProcessed(int numberOfItemsProcessed) {
        this.numberOfItemsProcessed = numberOfItemsProcessed;
    }

    public int getEnergyCompanyId() {
        return energyCompanyId;
    }

    public void setEnergyCompanyId(int energyCompanyId) {
        this.energyCompanyId = energyCompanyId;
    }

    public LiteYukonUser getUser() {
        return user;
    }

    public void setUser(LiteYukonUser user) {
        this.user = user;
    }

    public String getDisplayName() {
        return StringUtils.abbreviate(taskName, 35);
    }

    @Override
    public String toString() {
        return "InventoryConfigTask [inventoryConfigTaskId=" + inventoryConfigTaskId +
            ", numberOfItems=" + numberOfItems +
            ", numberOfItemsProcessed=" + numberOfItemsProcessed + ", taskName=" + taskName +
            ", sendInService=" + sendInService + ", energyCompanyId=" + energyCompanyId +
            ", userId=" + user.getUserID() + "]";
    }
}
