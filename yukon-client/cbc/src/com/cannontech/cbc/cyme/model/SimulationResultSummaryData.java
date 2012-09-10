package com.cannontech.cbc.cyme.model;

public final class SimulationResultSummaryData {

    private String networkId;
    private String simulationId;
    private String reportId;
    
    public SimulationResultSummaryData(String networkId, String reportId, String simulationId) {
        this.networkId = networkId;
        this.reportId = reportId;
        this.simulationId = simulationId;
    }
    
    public String getNetworkId() {
        return networkId;
    }
    public String getSimulationId() {
        return simulationId;
    }
    public String getReportId() {
        return reportId;
    }
}
