package com.cannontech.web.common.flashScope;

public enum FlashScopeMessageType {

	SUCCESS("success"),
	WARNING("warning"),
	ERROR("error");
	
	private String className;
	private FlashScopeMessageType(String className) {
	    this.className = className;
	}
	
	public String getClassName() {
        return className;
    }
	
}