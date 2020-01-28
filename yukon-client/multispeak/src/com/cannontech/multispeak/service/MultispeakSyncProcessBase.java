package com.cannontech.multispeak.service;

public class MultispeakSyncProcessBase {

    protected MultispeakSyncType type;
    protected MultispeakSyncProgressStatus status;
    protected Exception exception = null;

    public MultispeakSyncProcessBase(MultispeakSyncType type) {
        this.type = type;
        this.status = MultispeakSyncProgressStatus.RUNNING;
    }

    public MultispeakSyncType getType() {
        return type;
    }

    public MultispeakSyncProgressStatus getStatus() {
        return status;
    }

    public boolean isRunning() {
        return this.status == MultispeakSyncProgressStatus.RUNNING;
    }

    public void finish() {
        this.status = MultispeakSyncProgressStatus.FINISHED;
    }

    public boolean isFinished() {
        return this.status == MultispeakSyncProgressStatus.FINISHED;
    }

    public void cancel() {
        this.status = MultispeakSyncProgressStatus.CANCELED;
    }

    public boolean isCanceled() {
        return this.status == MultispeakSyncProgressStatus.CANCELED;
    }

    public void setException(Exception exception) {
        if (exception != null) {
            this.exception = exception;
            this.status = MultispeakSyncProgressStatus.FAILED;
        }
    }

    public Exception getException() {
        return this.exception;
    }

    public boolean isHasException() {
        return this.exception != null;
    }

}