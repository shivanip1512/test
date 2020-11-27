package com.eaton.builders.assets.commchannel;

import com.eaton.builders.assets.commchannel.CommChannelTypes.SharedPortType;

public class CommChannelSharedPort {
	private SharedPortType sharedPortType;
    private Integer sharedSocketNumber;
    
    public CommChannelSharedPort(SharedPortType sharedPortType, Integer sharedSocketNumber) {
    	this.sharedPortType = sharedPortType;
    	this.sharedSocketNumber = sharedSocketNumber;
    }
    
    public CommChannelSharedPort() {
    	this(SharedPortType.NONE, 1025);
    }
    
    public SharedPortType getSharedPortType() {
    	return sharedPortType;
    }
    
    public void setSharedPortType(SharedPortType sharedPortType) {
    	this.sharedPortType = sharedPortType;
    }
    
    public Integer getSharedSocketNumber() {
    	return sharedSocketNumber;
    }
    
    public void setSharedSocketNumber(Integer sharedSocketNumber) {
    	this.sharedSocketNumber = sharedSocketNumber;
    }
}