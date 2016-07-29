package com.cannontech.web.rfn.dataStreaming.model;

import com.cannontech.common.i18n.MessageSourceAccessor;

public class GatewayLoading {
    
    private static final String nameKey= "yukon.web.modules.tools.bulk.dataStreaming.verification.gatewayLoading";
    
    private double currentPercent;
    private double proposedPercent;
    private String gatewayName;
    private String detail;
    private MessageSourceAccessor accessor;
    
    
    public double getCurrentPercent() {
        return currentPercent;
    }
    public void setCurrentPercent(double currentPercent) {
        this.currentPercent = currentPercent;
    }
    public double getProposedPercent() {
        return proposedPercent;
    }
    public void setProposedPercent(double proposedPercent) {
        this.proposedPercent = proposedPercent;
    }
    public String getGatewayName() {
        return gatewayName;
    }
    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }
    
    public void setAccessor(MessageSourceAccessor accessor) {
        this.accessor = accessor;
    }
    
    public String getDetail() {
        String key = nameKey;
        if (currentPercent < proposedPercent) {
            key += "Increase";
        } else if (currentPercent > proposedPercent) {
            key += "Decrease";
        } else {
            key += "Same";
        }
        detail = accessor.getMessage(key, gatewayName, currentPercent, proposedPercent);
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    

}
