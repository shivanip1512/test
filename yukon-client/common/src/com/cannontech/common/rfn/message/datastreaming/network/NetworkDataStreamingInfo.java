package com.cannontech.common.rfn.message.datastreaming.network;

import java.io.Serializable;

/**
 * Describes system configuration and network parameters for data streaming.
 * NM sends this information to Yukon in the following ways:
 * 1. Each time NM is started;
 * 2. When NM receives a RfnArchiveStartupNotification message, which indicates Yukon restarts;
 * 3. When NM receives a NetworkDataStreamingInfoRequest from Yukon.
 */
public class NetworkDataStreamingInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private int unitReportingInterval; // to calculate the basic unit of DS capacity.  (e.x., 30 minutes)
    
    private int minReportingInterval;  // Define the minimum reporting interval. (e.x.,  1 minute)
    private int maxReportingInterval;  // Define the maximum reporting interval. (e.x., 30 minute)
    
    private int maxNumberOfReportingIntervals; // Define the max number of reporting intervals you can configure for a device.
                                               // For the initial phase, only one reporting interval is supported.
       
    private int maxNumberOfMetricsPerReport; // Define the max number of DS metrics fitted in a report. For example, if the value
                                             // is 8 and you already enabled one DS metric, the current firmware implementation
                                             // supports you to add 7 more metrics without inputting additional loading.
    
    private int maxNumberOfReportsPerDevice; // Define the max number of DS reports a device can send at a reporting time.
                                             // So the max number of metrics you can configure for a device is the product of
                                             // this value and the max number of DS metrics fitted in a report.
                                             // For the initial phase, only one DS report per device is supported.
                                             // So if maxNumberOfMetricsPerReport is 8, only 8 * 1 DS metrics
                                             // can be configured for a device.

    public int getUnitReportingInterval() {
        return unitReportingInterval;
    }

    public void setUnitReportingInterval(int unitReportingInterval) {
        this.unitReportingInterval = unitReportingInterval;
    }

    public int getMinReportingInterval() {
        return minReportingInterval;
    }

    public void setMinReportingInterval(int minReportingInterval) {
        this.minReportingInterval = minReportingInterval;
    }

    public int getMaxReportingInterval() {
        return maxReportingInterval;
    }

    public void setMaxReportingInterval(int maxReportingInterval) {
        this.maxReportingInterval = maxReportingInterval;
    }

    public int getMaxNumberOfReportingIntervals() {
        return maxNumberOfReportingIntervals;
    }

    public void setMaxNumberOfReportingIntervals(int maxNumberOfReportingIntervals) {
        this.maxNumberOfReportingIntervals = maxNumberOfReportingIntervals;
    }

    public int getMaxNumberOfMetricsPerReport() {
        return maxNumberOfMetricsPerReport;
    }

    public void setMaxNumberOfMetricsPerReport(int maxNumberOfMetricsPerReport) {
        this.maxNumberOfMetricsPerReport = maxNumberOfMetricsPerReport;
    }

    public int getMaxNumberOfReportsPerDevice() {
        return maxNumberOfReportsPerDevice;
    }

    public void setMaxNumberOfReportsPerDevice(int maxNumberOfReportsPerDevice) {
        this.maxNumberOfReportsPerDevice = maxNumberOfReportsPerDevice;
    }

    @Override
    public int hashCode() {
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NetworkDataStreamingInfo other = (NetworkDataStreamingInfo) obj;
        if (maxNumberOfMetricsPerReport != other.maxNumberOfMetricsPerReport) {
            return false;
        }
        if (maxNumberOfReportingIntervals != other.maxNumberOfReportingIntervals) {
            return false;
        }
        if (maxNumberOfReportsPerDevice != other.maxNumberOfReportsPerDevice) {
            return false;
        }
        if (maxReportingInterval != other.maxReportingInterval) {
            return false;
        }
        if (minReportingInterval != other.minReportingInterval) {
            return false;
        }
        if (unitReportingInterval != other.unitReportingInterval) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NetworkDataStreamingInfo [unitReportingInterval=" + unitReportingInterval + ", minReportingInterval="
               + minReportingInterval + ", maxReportingInterval=" + maxReportingInterval
               + ", maxNumberOfReportingIntervals=" + maxNumberOfReportingIntervals + ", maxNumberOfMetricsPerReport="
               + maxNumberOfMetricsPerReport + ", maxNumberOfReportsPerDevice=" + maxNumberOfReportsPerDevice + "]";
    }
    
}
