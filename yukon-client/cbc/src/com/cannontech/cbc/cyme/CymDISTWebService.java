package com.cannontech.cbc.cyme;


public interface CymDISTWebService {

    /**
     * Takes an XML formatted string which represents changes to make to a CYME model 
     * and will run a load simulation on the model with the changes.
     * 
     * @param xmlData
     * @return simulationId for subsequent calls to get the reports.
     */
    public String runSimulation(String xmlData);

    /**
     * Returns simulation status from CYME for the simulation with simulationId.
     * 
     * @param simulationId
     */
    public boolean getSimulationReportStatus(String simulationId);

    /**
     * Returns the XML formatted report for the simulation with simulationId.
     * Returns null if the report is not ready yet.
     * 
     * @param simulationId
     * @return
     */
    public String getSimulationReport(String simulationId);
}
