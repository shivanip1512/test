package com.cannontech.web.validate;

/**
 * Insert the type's description here.
 * Creation date: (6/12/2001 12:28:56 PM)
 * @author: 
 */
public interface Validator {
	public String getError(String field);
	public void setErrorMessage(String field, String message);
	public void setPattern(String field, String pattern);
	public boolean validate(javax.servlet.http.HttpServletRequest request);
}
