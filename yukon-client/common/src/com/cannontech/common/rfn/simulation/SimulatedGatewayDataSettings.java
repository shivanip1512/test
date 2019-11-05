package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

import com.cannontech.common.rfn.message.gateway.ConnectionStatus;

public class SimulatedGatewayDataSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean returnGwy800Model;
    private boolean returnVirtualGatewayModel;
    private double currentDataStreamingLoading;
    private Integer numberOfReadyNodes;
    private Integer numberOfNotReadyNodes;
    private boolean isFailsafeMode;
    private ConnectionStatus connectionStatus;
    
    public boolean isReturnGwy800Model() {
        return returnGwy800Model;
    }

    public void setReturnGwy800Model(boolean returnGwy800Model) {
        this.returnGwy800Model = returnGwy800Model;
    }
    
    public boolean isReturnVirtualGatewayModel() {
        return returnVirtualGatewayModel;
    }

    public void setReturnVirtualGatewayModel(boolean returnVirtualGatewayModel) {
        this.returnVirtualGatewayModel = returnVirtualGatewayModel;
    }

    public double getCurrentDataStreamingLoading() {
        return currentDataStreamingLoading;
    }

    public void setCurrentDataStreamingLoading(double currentDataStreamingLoading) {
        this.currentDataStreamingLoading = currentDataStreamingLoading;
    }

    public Integer getNumberOfReadyNodes() {
        return numberOfReadyNodes;
    }

    public void setNumberOfReadyNodes(Integer numberOfReadyNodes) {
        this.numberOfReadyNodes = numberOfReadyNodes;
    }

    public Integer getNumberOfNotReadyNodes() {
        return numberOfNotReadyNodes;
    }

    public void setNumberOfNotReadyNodes(Integer numberOfNotReadyNodes) {
        this.numberOfNotReadyNodes = numberOfNotReadyNodes;
    }

    public boolean isFailsafeMode() {
        return isFailsafeMode;
    }

    public void setFailsafeMode(boolean isFailsafeMode) {
        this.isFailsafeMode = isFailsafeMode;
    }
    
    public ConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(ConnectionStatus connectionStatus) {
        this.connectionStatus = connectionStatus;
    }
}
