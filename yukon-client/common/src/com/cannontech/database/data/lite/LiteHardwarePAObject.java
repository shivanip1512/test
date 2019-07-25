package com.cannontech.database.data.lite;

public class LiteHardwarePAObject {
    
    private Integer inventoryId;
    private LiteYukonPAObject pao;

    public LiteHardwarePAObject(LiteYukonPAObject pao, Integer inventoryId) {
        this.setPao(pao);
        this.inventoryId = inventoryId;
    }

    public Integer getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(Integer inventoryId) {
        this.inventoryId = inventoryId;
    }

    public LiteYukonPAObject getPao() {
        return pao;
    }

    public void setPao(LiteYukonPAObject pao) {
        this.pao = pao;
    }

}
