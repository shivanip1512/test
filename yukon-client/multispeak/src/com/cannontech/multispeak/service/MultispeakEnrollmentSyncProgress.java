package com.cannontech.multispeak.service;

import java.util.concurrent.atomic.AtomicInteger;

public class MultispeakEnrollmentSyncProgress extends MultispeakSyncProcessBase {

    private AtomicInteger enrollmentSentCount = new AtomicInteger(0);

    public MultispeakEnrollmentSyncProgress(MultispeakSyncType type) {
        super(type);
    }

    public void incrementEnrollmentSentCount() {
        enrollmentSentCount.incrementAndGet();
    }

    public int getEnrollmentSentCount() {
        return enrollmentSentCount.get();
    }
}
