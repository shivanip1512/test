package com.cannontech.dr.rfn.model;

import org.joda.time.Instant;

import com.cannontech.dr.assetavailability.AssetAvailabilityStatus;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;

public final class PerformanceVerificationEventMessageDeviceStatus {

    private final int deviceId;
    private final PerformanceVerificationEventMessage message;
    private final AssetAvailabilityStatus assetAvailabilityStatus;
    private final PerformanceVerificationMessageStatus performanceVerificationStatus;

    public PerformanceVerificationEventMessageDeviceStatus(int deviceId, long messageId,
            Instant timeMessageSent, PerformanceVerificationMessageStatus performanceVerificationStatus) {
        this(deviceId, messageId, timeMessageSent, performanceVerificationStatus, null);
    }

    public PerformanceVerificationEventMessageDeviceStatus(int deviceId, long messageId,
            Instant timeMessageSent, AssetAvailabilityStatus assetAvailabilityStatus) {
        this(deviceId, messageId, timeMessageSent, PerformanceVerificationMessageStatus.UNKNOWN, assetAvailabilityStatus);
    }

    private PerformanceVerificationEventMessageDeviceStatus(int deviceId, long messageId, Instant timeMessageSent,
                                                    PerformanceVerificationMessageStatus performanceVerificationStatus,
                                                    AssetAvailabilityStatus assetAvailabilityStatus) {
        this.deviceId = deviceId;
        this.performanceVerificationStatus = performanceVerificationStatus;
        this.assetAvailabilityStatus = assetAvailabilityStatus;
        this.message = new PerformanceVerificationEventMessage(messageId, timeMessageSent);
    }
    
    public int getDeviceId() {
        return deviceId;
    }
    
    public long getMessageId() {
        return message.getMessageId();
    }

    public Instant getTimeMessageSent() {
        return message.getTimeMessageSent();
    }

    public AssetAvailabilityStatus getAssetAvailabilityStatus() {
        return assetAvailabilityStatus;
    }

    public PerformanceVerificationMessageStatus getPerformanceVerificationStatus() {
        return performanceVerificationStatus;
    }
    
    public PerformanceVerificationEventMessageDeviceStatus
            withAssetAvailabilityStatus(AssetAvailabilityStatus newAssetAvailabilityStatus) {
        return new PerformanceVerificationEventMessageDeviceStatus(deviceId, getMessageId(), 
                                                                   getTimeMessageSent(), newAssetAvailabilityStatus);
    }
}
