package com.cannontech.simulators.message.request;

import com.cannontech.common.rfn.simulation.SimulatedCertificateReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedFirmwareVersionReplySettings;
import com.cannontech.common.rfn.simulation.SimulatedGatewayDataSettings;
import com.cannontech.common.rfn.simulation.SimulatedUpdateReplySettings;
import com.cannontech.simulators.SimulatorType;

/**
 * Request to modify the settings of the gateway simulator in the gateway simulator service. This includes stopping and
 * starting simulator threads.
 * 
 * To start a simulator thread, set the related settings object.
 */
public class ModifyGatewaySimulatorRequest implements SimulatorRequest {
    private static final long serialVersionUID = 1L;
    private boolean stopDataReply;
    private boolean stopUpdateReply;
    private boolean stopCertificateReply;
    private boolean stopFirmwareReply;
    private boolean stopFirmwareVersionReply;
    
    private SimulatedGatewayDataSettings dataSettings;
    private SimulatedUpdateReplySettings updateSettings;
    private SimulatedCertificateReplySettings certificateSettings;
    private SimulatedFirmwareReplySettings firmwareSettings;
    private SimulatedFirmwareVersionReplySettings firmwareVersionSettings;
    
    @Override
    public SimulatorType getRequestType() {
        return SimulatorType.GATEWAY;
    }
    
    public boolean isStopDataReply() {
        return stopDataReply;
    }
    
    public void setStopDataReply(boolean stopDataReply) {
        this.stopDataReply = stopDataReply;
    }
    
    public boolean isStopUpdateReply() {
        return stopUpdateReply;
    }
    
    public void setStopUpdateReply(boolean stopUpdateReply) {
        this.stopUpdateReply = stopUpdateReply;
    }
    
    public boolean isStopCertificateReply() {
        return stopCertificateReply;
    }
    
    public void setStopCertificateReply(boolean stopCertificateReply) {
        this.stopCertificateReply = stopCertificateReply;
    }
    
    public boolean isStopFirmwareReply() {
        return stopFirmwareReply;
    }
    
    public void setStopFirmwareReply(boolean stopFirmwareReply) {
        this.stopFirmwareReply = stopFirmwareReply;
    }
    
    public boolean isStopFirmwareVersionReply() {
        return stopFirmwareVersionReply;
    }
    
    public void setStopFirmwareVersionReply(boolean stopFirmwareVersionReply) {
        this.stopFirmwareVersionReply = stopFirmwareVersionReply;
    }
    
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
    
    /**
     * Sets this object to stop all gateway simulation.
     */
    public void setAllStop() {
        stopCertificateReply = true;
        stopDataReply = true;
        stopFirmwareReply = true;
        stopFirmwareVersionReply = true;
        stopUpdateReply = true;
    }

    @Override
    public String toString() {
        return "ModifyGatewaySimulatorRequest [stopDataReply=" + stopDataReply + ", stopUpdateReply=" + stopUpdateReply
               + ", stopCertificateReply=" + stopCertificateReply + ", stopFirmwareReply=" + stopFirmwareReply
               + ", stopFirmwareVersionReply=" + stopFirmwareVersionReply + ", dataSettings=" + dataSettings
               + ", updateSettings=" + updateSettings + ", certificateSettings=" + certificateSettings
               + ", firmwareSettings=" + firmwareSettings + ", firmwareVersionSettings=" + firmwareVersionSettings
               + "]";
    }
    
}
