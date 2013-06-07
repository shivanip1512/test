package com.cannontech.dr.program.service;

import java.util.Map;

import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.messaging.message.loadcontrol.data.ConstraintError;
import com.cannontech.messaging.message.loadcontrol.data.ConstraintViolation;

public class ConstraintContainer {
	
	private ConstraintViolation constraintViolation;
	private final String keyPrefix = "yukon.common.constrainterror.";
	
	public ConstraintContainer(ConstraintViolation constraintViolation) {
		super();
		this.constraintViolation = constraintViolation;
	}

	public ConstraintError getErrorCode() {
		return constraintViolation.getErrorCode();
	}

	public Map<String, Object> getViolationParameters() {
		return constraintViolation.getViolationParameters();
	}
	
	public ResolvableTemplate getConstraintTemplate() {
		ResolvableTemplate resolvableTemplate = new ResolvableTemplate(keyPrefix + getErrorCode());
		resolvableTemplate.setData(getViolationParameters());
		return resolvableTemplate;
	}
	
	@Override
	public String toString() {
		return getErrorCode().toString() + " " + getViolationParameters();
	}
}