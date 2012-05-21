package com.cannontech.i18n;

import org.springframework.context.MessageSourceResolvable;

/* Extension of YukonMessageSourceResolvable which adds web-specific 
 * formatting information.  This is currently used to add a class name to flash scope messages
 */
public class WebMessageSourceResolvable extends YukonMessageSourceResolvable{

	private static final long serialVersionUID = 1L;

	private String className = "";

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
	public WebMessageSourceResolvable(MessageSourceResolvable resolvable) {
		super(resolvable);
	}

	public WebMessageSourceResolvable(String code) {
		super(code);
	}

	public WebMessageSourceResolvable(String[] codes, Object[] arguments, String defaultMessage) {
		super(codes, arguments, defaultMessage);
	}

	public WebMessageSourceResolvable(String[] codes, Object[] arguments) {
		super(codes, arguments);
	}

	public WebMessageSourceResolvable(String[] codes, String defaultMessage) {
		super(codes, defaultMessage);
	}

	public WebMessageSourceResolvable(String[] codes) {
		super(codes);
	}
	
	public WebMessageSourceResolvable(String code, Object... args) {
		super(new String[] { code }, args);
	}
}
