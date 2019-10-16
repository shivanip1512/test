package com.cannontech.simulators.message.request;

import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.message.status.type.MeterDisconnectStatus;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.simulators.SimulatorType;

/**
 * Request to send a meter info update.
 */
public class MeterInfoStatusArchiveSimulatorRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    
    private Set<RfnIdentifier> rfnIdentifiers;
    private String meterConfigurationId;
    private MeterDisconnectStatus disconnectStatus;
    private Instant timestamp = new Instant();

    public MeterInfoStatusArchiveSimulatorRequest(Set<RfnIdentifier> rfnIdentifiers, Instant timestamp, 
            String meterConfigurationId, MeterDisconnectStatus disconnectStatus) {
        this.rfnIdentifiers = rfnIdentifiers;
        this.timestamp = timestamp;
        this.meterConfigurationId = meterConfigurationId;
        this.disconnectStatus = disconnectStatus;
    }

    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.STATUS_ARCHIVE;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Set<RfnIdentifier> getRfnIdentifiers() {
        return rfnIdentifiers;
    }

    public String getMeterConfigurationId() {
        return meterConfigurationId;
    }

    public MeterDisconnectStatus getDisconnectStatus() {
        return disconnectStatus;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
