package com.cannontech.web.validate;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
/**
 * Insert the type's description here.
 * Creation date: (6/12/2001 12:36:46 PM)
 * @author: 
 */
public class PageBean implements AutoBean, Validator {
	// implementing Validator
	private Map patterns = new Hashtable();
	private Map messages = new Hashtable();
	private Map errors = new Hashtable();
	private static Map namedMessages = new Hashtable();
	
	private static java.text.SimpleDateFormat[] timePatterns = {
		new java.text.SimpleDateFormat("HH:mm"),
		new java.text.SimpleDateFormat("HH:mm:ss")
	};
	private static java.text.SimpleDateFormat[] datePatterns = {
		new java.text.SimpleDateFormat("MM/dd/yy"),
		new java.text.SimpleDateFormat("MM-dd-yy"),
		new java.text.SimpleDateFormat("MM.dd.yy"),
		new java.text.SimpleDateFormat("MM/dd/yyyy"),
		new java.text.SimpleDateFormat("MM-dd-yyyy"),
		new java.text.SimpleDateFormat("MM.dd.yyyy"),
	};
	static {
		namedMessages.put("@time", "Time must be in the form of HH:mm or HH:mm:ss");
		namedMessages.put("@date", "Date must be in the form of MM/dd/yy or MM/dd/yyyy");
		namedMessages.put("@int", "Number must be an integer");
		namedMessages.put("@long", "Number must be a long integer");
		namedMessages.put("@real", "Number must be a real number");
		namedMessages.put("@not-empty", "This field cannot be empty");
	};

	// implementing AutoBean
	private Map properties = new Hashtable();
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 10:47:09 AM)
 */
public void clear()
{
	errors.clear();
	patterns.clear();
	messages.clear();
	properties.clear();
}
/**
 * get method comment.
 */
public String get(String field) {
	Object property = properties.get(field);
	if (property == null)
		return null;
		
	return property.toString();
}
/**
 * getError method comment.
 */
public String getError(String field) {
	String result = (String)errors.get(field);
	if (result == null)
		return "";
		
	return ("<FONT COLOR=RED>*" + result + "</FONT>");
}
/**
 * getObject method comment.
 */
public Object getObject(String property) {
	return properties.get(property);
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 4:20:20 PM)
 * @return java.util.Date
 * @param dateStr java.lang.String
 */
public static Date parseDate(String dateStr) {
	java.util.Date dateVal = null;
	
	for (int i = 0; i < datePatterns.length; i++) {
		try {
			dateVal = datePatterns[i].parse(dateStr);
			break;
		} catch (java.text.ParseException pe) {}
	}
	
	return dateVal;
}
/**
 * Insert the method's description here.
 * Creation date: (6/27/2001 4:27:10 PM)
 * @return java.util.Date
 * @param timeStr java.lang.String
 */
public static Date parseTime(String timeStr) {
	java.util.Date timeVal = null;
	
	for (int i = 0; i < timePatterns.length; i++) {
		try {
			timeVal = timePatterns[i].parse(timeStr);
			break;
		} catch (java.text.ParseException pe) {}
	}
	
	return timeVal;
}
/**
 * Insert the method's description here.
 * Creation date: (6/13/2001 2:49:19 PM)
 * @param field java.lang.String
 */
public void removePattern(String field)
{
	if (field == null)
		return;
	patterns.remove(field);
}
/**
 * set method comment.
 */
public void set(String field, Object value) {
	properties.put(field, value);
}
/**
 * Insert the method's description here.
 * Creation date: (6/29/2001 6:43:58 PM)
 * @param field java.lang.String
 * @param error java.lang.String
 */
public void setError(String field, String error) {
	if (field == null || field.equals("") )
		return;

	errors.put(field, error);
}
/**
 * setErrorMessage method comment.
 */
public void setErrorMessage(String field, String message) {
	if (message == null || message.equals(""))
		return;

	messages.put(field, message);
}
/**
 * setPattern method comment.
 */
public void setPattern(String field, String pattern) {
	if (pattern == null || pattern.equals(""))
		return;

	if (pattern.charAt(0) == '@') {
		String message = (String)namedMessages.get(pattern);
		if (message != null)
			messages.put(field, message);
	}
	
	patterns.put(field, pattern);
}
/**
 * validate method comment.
 */
public boolean validate() {
	errors.clear();
	Enumeration e = ((Hashtable) properties).keys();
	while (e.hasMoreElements()) {
		String field = (String)e.nextElement();
		Object value = properties.get(field);
		if (value instanceof String)	// not indexed property
			validateField(field, (String)value);
	}
	return errors.isEmpty();
}
/**
 * validate method comment.
 */
public boolean validate(javax.servlet.http.HttpServletRequest request) {
	errors.clear();
	Enumeration e = request.getParameterNames();
	while (e.hasMoreElements()) {
		String field = (String)e.nextElement();
		validateField(field, request.getParameter(field));
	}
	return errors.isEmpty();
}
/**
 * Insert the method's description here.
 * Creation date: (6/12/2001 2:09:27 PM)
 * @return boolean
 * @param field java.lang.String
 * @param value java.lang.String
 */
private boolean validateField(String field, String value) {
	if (value == null)
		return true;
		
	String pattern = (String)patterns.get(field);
	if (pattern == null)
		return true;

	boolean valid = false;

	// check for time format
	if (pattern.equalsIgnoreCase("@time")) {
		java.util.Date retVal = null;
		for (int i = 0; i < timePatterns.length; i++) {
			try {
				retVal = timePatterns[i].parse(value);
				valid = true;
				break;
			} catch (java.text.ParseException pe) {
			}
		}
	}

	// check for date format
	if (pattern.equalsIgnoreCase("@date")) {
		java.util.Date retVal = null;
		for (int i = 0; i < datePatterns.length; i++) {
			try {
				retVal = datePatterns[i].parse(value);
				valid = true;
				break;
			} catch (java.text.ParseException pe) {
			}
		}
	}

	// check for integer
	if (pattern.equalsIgnoreCase("@int")) {
		Integer retVal = null;
		try {
			retVal = Integer.valueOf(value);
			valid = true;
		} catch (NumberFormatException ne) {
		}
	}

	// check for long
	if (pattern.equalsIgnoreCase("@long")) {
		Long retVal = null;
		try {
			retVal = Long.valueOf(value);
			valid = true;
		} catch (NumberFormatException ne) {
		}
	}

	// check for real number
	if (pattern.equalsIgnoreCase("@real")) {
		Double retVal = null;
		try {
			retVal = Double.valueOf(value);
			valid = true;
		} catch (NumberFormatException ne) {
		}
	}

	// check for not-empty field
	if (pattern.equalsIgnoreCase("@not-empty")) {
		if (!value.equals(""))
			valid = true;
	}

	if (!valid)
		errors.put(field, messages.get(field));
	return valid;
}
}
