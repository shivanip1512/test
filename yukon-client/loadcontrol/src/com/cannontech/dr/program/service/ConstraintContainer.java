package com.cannontech.dr.program.service;

import java.util.Map;

import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.loadcontrol.messages.ConstraintViolation;
import com.cannontech.loadcontrol.messages.ConstraintError;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;

public class ConstraintContainer {
	
	private ConstraintViolation constraintViolation;
	private final String keyPrefix = "yukon.web.modules.dr.constrainterror.";
	
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
		TemplateProcessorFactory processorFactory = YukonSpringHook.getBean(TemplateProcessorFactory.class);
		YukonUserContext userContext = ClientSession.getUserContext();
		
		return processorFactory.processResolvableTemplate(this.getConstraintTemplate(), userContext);
		//return getErrorCode().toString() + " " + getViolationParameters();
	}
}