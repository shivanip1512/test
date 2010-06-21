package com.cannontech.multispeak.service;

import java.util.concurrent.atomic.AtomicInteger;

public class MultispeakDeviceGroupSyncProgress {

	private MultispeakDeviceGroupSyncType type;
	private MultispeakDeviceGroupSyncStatus status;
	private AtomicInteger metersProcessed = new AtomicInteger(0);
	private AtomicInteger substationChangeCount = new AtomicInteger(0);
	private AtomicInteger substationNoChangeCount = new AtomicInteger(0);
	private AtomicInteger billingCycleChangeCount = new AtomicInteger(0);
	private AtomicInteger billingCycleNoChangeCount = new AtomicInteger(0);
	private Exception exception = null;
	
	// type
	public MultispeakDeviceGroupSyncProgress(MultispeakDeviceGroupSyncType type) {
		this.type = type;
		this.status = MultispeakDeviceGroupSyncStatus.IN_PROGRESS;
	}
	
	public MultispeakDeviceGroupSyncType getType() {
		return type;
	}
	
	public MultispeakDeviceGroupSyncStatus getStatus() {
		return status;
	}
	
	// meters processed
	public void incrementMetersProcessedCount() {
		metersProcessed.incrementAndGet();
	}
	public int getMetersProcessedCount() {
		return metersProcessed.get();
	}
	
	// substation change counts
	public void incrementSubstationChangeCount() {
		substationChangeCount.incrementAndGet();
	}
	public void incrementSubstationNoChangeCount() {
		substationNoChangeCount.incrementAndGet();
	}
	public int getSubstationChangeCount() {
		return substationChangeCount.get();
	}
	public int getSubstationNoChangeCount() {
		return substationNoChangeCount.get();
	}
	
	// billing cycle change counts
	public void incrementBillingCycleChangeCount() {
		billingCycleChangeCount.incrementAndGet();
	}
	public void incrementBillingCycleNoChangeCount() {
		billingCycleNoChangeCount.incrementAndGet();
	}
	public int getBillingCycleChangeCount() {
		return billingCycleChangeCount.get();
	}
	public int getBillingCycleNoChangeCount() {
		return billingCycleNoChangeCount.get();
	}
	
	// status
	public boolean isRunning() {
		return this.status == MultispeakDeviceGroupSyncStatus.IN_PROGRESS;
	}
	
	public void finish() {
		this.status = MultispeakDeviceGroupSyncStatus.IN_PROGRESS_FINISHED;
	}
	public boolean isFinished() {
		return this.status == MultispeakDeviceGroupSyncStatus.IN_PROGRESS_FINISHED;
	}
	
	public void cancel() {
		this.status = MultispeakDeviceGroupSyncStatus.IN_PROGRESS_CANCELED;
	}
	public boolean isCanceled() {
		return this.status == MultispeakDeviceGroupSyncStatus.IN_PROGRESS_CANCELED;
	}
	
	// exception
	public void setException(Exception exception) {
		
		if (exception != null) {
			this.exception = exception;
			this.status = MultispeakDeviceGroupSyncStatus.IN_PROGRESS_FAILED;
		}
	}
	public Exception getException() {
		return this.exception;
	}
	public boolean isHasException() {
		return this.exception != null;
	}
}
