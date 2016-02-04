package com.cannontech.simulators.message.response;

import com.cannontech.common.rfn.simulation.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareVersionReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.simulation.SimulatedUpdateReplySettings;

/**
 * Response to the GatewaySimulatorStatusRequest. Contains settings for any simulator thread that is currently running.
 * For any simulator thread that is not running, the settings will be null.
 */
public class GatewaySimulatorStatusResponse extends SimulatorResponseBase {
    private static final long serialVersionUID = 1L;
    private SimulatedGatewayDataSettings dataSettings;
    private SimulatedUpdateReplySettings updateSettings;
    private SimulatedCertificateReplySettings certificateSettings;
    private SimulatedFirmwareReplySettings firmwareSettings;
    private SimulatedFirmwareVersionReplySettings firmwareVersionSettings;
    
    public SimulatedGatewayDataSettings getDataSettings() {
        return dataSettings;
    }
    
    public void setDataSettings(SimulatedGatewayDataSettings dataSettings) {
        this.dataSettings = dataSettings;
    }
    
    public SimulatedUpdateReplySettings getUpdateSettings() {
        return updateSettings;
    }
    
    public void setUpdateSettings(SimulatedUpdateReplySettings updateSettings) {
        this.updateSettings = updateSettings;
    }
    
    public SimulatedCertificateReplySettings getCertificateSettings() {
        return certificateSettings;
    }
    
    public void setCertificateSettings(SimulatedCertificateReplySettings certificateSettings) {
        this.certificateSettings = certificateSettings;
    }
    
    public SimulatedFirmwareReplySettings getFirmwareSettings() {
        return firmwareSettings;
    }
    
    public void setFirmwareSettings(SimulatedFirmwareReplySettings firmwareSettings) {
        this.firmwareSettings = firmwareSettings;
    }
    
    public SimulatedFirmwareVersionReplySettings getFirmwareVersionSettings() {
        return firmwareVersionSettings;
    }
    
    public void setFirmwareVersionSettings(SimulatedFirmwareVersionReplySettings firmwareVersionSettings) {
        this.firmwareVersionSettings = firmwareVersionSettings;
    }
    
    public boolean isDataReplyActive() {
        return dataSettings != null;
    }
    
    public boolean isUpdateReplyActive() {
        return updateSettings != null;
    }
    
    public boolean isCertificateReplyActive() {
        return certificateSettings != null;
    }
    
    public boolean isFirmwareReplyActive() {
        return firmwareSettings != null;
    }
    
    public boolean isFirmwareVersionReplyActive() {
        return firmwareVersionSettings != null;
    }
    
    public int getNumberOfSimulatorsRunning() {
        int simulatorsRunning = 0;
        
        if (isDataReplyActive()) {
            simulatorsRunning++;
        }
        if (isUpdateReplyActive()) {
            simulatorsRunning++;
        }
        if (isCertificateReplyActive()) {
            simulatorsRunning++;
        }
        if (isFirmwareReplyActive()) {
            simulatorsRunning++;
        }
        if (isFirmwareVersionReplyActive()) {
            simulatorsRunning++;
        }
        
        return simulatorsRunning;
    }

    @Override
    public String toString() {
        return "GatewaySimulatorStatusResponse [dataSettings=" + dataSettings + ", updateSettings=" + updateSettings
               + ", certificateSettings=" + certificateSettings + ", firmwareSettings=" + firmwareSettings
               + ", firmwareVersionSettings=" + firmwareVersionSettings + ", success=" + success + ", exception="
               + exception + "]";
    }
    
}
