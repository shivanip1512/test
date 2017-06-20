package com.cannontech.common.rfn.simulation;

import java.io.Serializable;

public class SimulatedGatewayDataSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean returnGwy800Model;
    private double currentDataStreamingLoading;
    private Integer numberOfReadyNodes;
    private Integer numberOfNotReadyNodes;

    public boolean isReturnGwy800Model() {
        return returnGwy800Model;
    }

    public void setReturnGwy800Model(boolean returnGwy800Model) {
        this.returnGwy800Model = returnGwy800Model;
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
}
