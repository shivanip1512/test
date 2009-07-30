package com.cannontech.common.bulk.callbackResult;

import com.cannontech.common.device.model.SimpleDevice;

public abstract class BackgroundProcessBulkProcessorCallback<I> extends CollectingBulkProcessorCallback<I, SimpleDevice> {

	protected BackgroundProcessTypeEnum backgroundProcessType;
	protected String resultsId = "";
	protected int totalItems = 0;
    
    public BackgroundProcessBulkProcessorCallback(BackgroundProcessTypeEnum backgroundProcessType, String resultsId, int totalItems) {
    	
    	this.backgroundProcessType = backgroundProcessType;
    	this.resultsId = resultsId;
    	this.totalItems = totalItems;
    }
    
    public void setBackgroundProcessType(
			BackgroundProcessTypeEnum backgroundProcessType) {
		this.backgroundProcessType = backgroundProcessType;
	}
    public BackgroundProcessTypeEnum getBackgroundProcessType() {
		return backgroundProcessType;
	}
    
    public void setResultsId(String resultsId) {
        this.resultsId = resultsId;
    }
    public String getResultsId() {
        return resultsId;
    }
    
    public void setTotalItems(int totalItems) {
		this.totalItems = totalItems;
	}
    public int getTotalItems() {
		return totalItems;
	}
    
}
