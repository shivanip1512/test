package com.cannontech.dr.rfn.model;

import com.cannontech.common.pao.YukonPao;

public class UnknownDevice {
    
    private YukonPao pao;
    private UnknownStatus unknownStatus;
    
    public YukonPao getPao() {
        return pao;
    }
    
    public void setPao(YukonPao pao) {
        this.pao = pao;
    }
    
    public UnknownStatus getUnknownStatus() {
        return unknownStatus;
    }
    
    public void setUnknownStatus(UnknownStatus unknownStatus) {
        this.unknownStatus = unknownStatus;
    }
}