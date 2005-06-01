/*
 * Created on May 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.cannontech.analysis.data.device;

/**
 * @author stacey
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class LPMeterDetailsData
{
    private MeterAndPointData meterAndPointData = null;
    private String lastIntervalDemand = null;
    private String lastIntervalVoltage = null;
    private String demandRate = null;
    private String voltageDemandRate = null;
    private Character missingDataFlag = null;
    
    /**
     * @param paobjectID
     * @param lastIntervalDemand
     * @param lastIntervalVoltage
     * @param demandRate
     * @param voltageDemandRate
     */
    public LPMeterDetailsData(MeterAndPointData meterAndPoint, String lastIntervalDemand, String lastIntervalVoltage, String demandRate, String voltageDemandRate) {
        super();
        this.meterAndPointData = meterAndPoint;
        this.lastIntervalDemand = lastIntervalDemand;
        this.lastIntervalVoltage = lastIntervalVoltage;
        this.demandRate = demandRate;
        this.voltageDemandRate = voltageDemandRate;
    }
    /**
     * @return Returns the demandRate.
     */
    public String getDemandRate()
    {
        return demandRate;
    }
    /**
     * @return Returns the lastIntervalDemand.
     */
    public String getLastIntervalDemand()
    {
        return lastIntervalDemand;
    }
    /**
     * @return Returns the lastIntervalVoltage.
     */
    public String getLastIntervalVoltage()
    {
        return lastIntervalVoltage;
    }
    /**
     * @return Returns the voltageDemandRate.
     */
    public String getVoltageDemandRate()
    {
        return voltageDemandRate;
    }
    /**
     * @return Returns the meterAndPointData.
     */
    public MeterAndPointData getMeterAndPointData()
    {
        return meterAndPointData;
    }
}
