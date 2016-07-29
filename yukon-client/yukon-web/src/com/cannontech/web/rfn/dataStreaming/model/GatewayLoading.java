package com.cannontech.web.rfn.dataStreaming.model;

public class GatewayLoading {
    
    private double currentPercent;
    private double proposedPercent;
    private String gatewayName;
    private String detail;
    
    
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
    public String getDetail() {
        String change = proposedPercent > currentPercent ? "increase" : "decrease";
        detail = "Gateway " + gatewayName + " loading will " + change + " from " + currentPercent + "% to " + proposedPercent + "% after making these changes.";
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    
    

}
