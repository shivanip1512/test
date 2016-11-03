package com.cannontech.common.rfn.message.datastreaming.network;

import java.io.Serializable;

/**
 * Describes system configuration and network parameters for data streaming.
 * NM sends this information to Yukon in the following ways:
 * 1. Each time NM starts;
 * 2. When NM receives a RfnArchiveStartupNotification message, which Yukon sends to NM each time Yukon starts;
 * 3. When NM receives a NetworkDataStreamingInfoRequest from Yukon.
 */
public class NetworkDataStreamingInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private short unitReportingInterval; // to calculate the basic unit of DS capacity.  (e.x., 30 minutes)
    private short minReportingInterval;  // Define the minimum reporting interval. (e.x.,  1 minute)
    private short maxReportingInterval;  // Define the maximum reporting interval. (e.x., 30 minute)
    
    private short maxNumberOfReportingIntervals;
        // Define the max number of reporting intervals you can configure for a device.
        // For the initial phase, only one reporting interval is supported.
       
    private short maxNumberOfMetricsPerReport;
        // Define the max number of DS metrics fitted in a report.
        // Note load is calculated by reports.
        // For example, if max metrics is set to 8 and you already enabled one DS metric,
        // the current firmware implementation supports you to add 7 more metrics without inputting additional load.
    
    private short maxNumberOfReportsPerDevice;
        // Define the max number of reports a device can send at a reporting time.
        // Thus the max metrics you can enable for a device equals to
        // the max metrics per report times max reports per device. For the initial phase,
        // since only one report per device is supported, if the max metrics per report is 8,
        // up to (8 * 1 = 8) metrics can be enabled for a device.

    public short getUnitReportingInterval()
    {
        return unitReportingInterval;
    }

    public void setUnitReportingInterval(short unitReportingInterval)
    {
        this.unitReportingInterval = unitReportingInterval;
    }

    public short getMinReportingInterval()
    {
        return minReportingInterval;
    }

    public void setMinReportingInterval(short minReportingInterval)
    {
        this.minReportingInterval = minReportingInterval;
    }

    public short getMaxReportingInterval()
    {
        return maxReportingInterval;
    }

    public void setMaxReportingInterval(short maxReportingInterval)
    {
        this.maxReportingInterval = maxReportingInterval;
    }

    public short getMaxNumberOfReportingIntervals()
    {
        return maxNumberOfReportingIntervals;
    }

    public void setMaxNumberOfReportingIntervals(short maxNumberOfReportingIntervals)
    {
        this.maxNumberOfReportingIntervals = maxNumberOfReportingIntervals;
    }

    public short getMaxNumberOfMetricsPerReport()
    {
        return maxNumberOfMetricsPerReport;
    }

    public void setMaxNumberOfMetricsPerReport(short maxNumberOfMetricsPerReport)
    {
        this.maxNumberOfMetricsPerReport = maxNumberOfMetricsPerReport;
    }

    public short getMaxNumberOfReportsPerDevice()
    {
        return maxNumberOfReportsPerDevice;
    }

    public void setMaxNumberOfReportsPerDevice(short maxNumberOfReportsPerDevice)
    {
        this.maxNumberOfReportsPerDevice = maxNumberOfReportsPerDevice;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + maxNumberOfMetricsPerReport;
        result = prime * result + maxNumberOfReportingIntervals;
        result = prime * result + maxNumberOfReportsPerDevice;
        result = prime * result + maxReportingInterval;
        result = prime * result + minReportingInterval;
        result = prime * result + unitReportingInterval;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NetworkDataStreamingInfo other = (NetworkDataStreamingInfo) obj;
        if (maxNumberOfMetricsPerReport != other.maxNumberOfMetricsPerReport)
            return false;
        if (maxNumberOfReportingIntervals != other.maxNumberOfReportingIntervals)
            return false;
        if (maxNumberOfReportsPerDevice != other.maxNumberOfReportsPerDevice)
            return false;
        if (maxReportingInterval != other.maxReportingInterval)
            return false;
        if (minReportingInterval != other.minReportingInterval)
            return false;
        if (unitReportingInterval != other.unitReportingInterval)
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("NetworkDataStreamingInfo [unitReportingInterval=");
        builder.append(unitReportingInterval);
        builder.append(", minReportingInterval=");
        builder.append(minReportingInterval);
        builder.append(", maxReportingInterval=");
        builder.append(maxReportingInterval);
        builder.append(", maxNumberOfReportingIntervals=");
        builder.append(maxNumberOfReportingIntervals);
        builder.append(", maxNumberOfMetricsPerReport=");
        builder.append(maxNumberOfMetricsPerReport);
        builder.append(", maxNumberOfReportsPerDevice=");
        builder.append(maxNumberOfReportsPerDevice);
        builder.append("]");
        return builder.toString();
    }
}
