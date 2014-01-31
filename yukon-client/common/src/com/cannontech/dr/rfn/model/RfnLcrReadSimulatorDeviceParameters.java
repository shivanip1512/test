package com.cannontech.dr.rfn.model;

import com.cannontech.common.rfn.message.RfnIdentifier;

public final class RfnLcrReadSimulatorDeviceParameters {

    private final RfnIdentifier rfnIdentifier;
    private final int majorVersion;
    private final int minorVersion;
    private final int revision;
    private final int recordingInterval;
    private final int reportingInterval;

    public RfnLcrReadSimulatorDeviceParameters(RfnIdentifier rfnIdentifier, int majorVersion, int minorVersion,
            int revision, int recordingInterval, int reportingInterval) {
        super();
        this.rfnIdentifier = rfnIdentifier;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.revision = revision;
        this.recordingInterval = recordingInterval;
        this.reportingInterval = reportingInterval;
    }

    public RfnIdentifier getRfnIdentifier() {
        return rfnIdentifier;
    }

    public int getMajorVersion() {
        return majorVersion;
    }

    public int getMinorVersion() {
        return minorVersion;
    }

    public int getRevision() {
        return revision;
    }

    public int getRecordingInterval() {
        return recordingInterval;
    }

    public int getReportingInterval() {
        return reportingInterval;
    }
}
