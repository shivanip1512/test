/*
 * Created on Feb 17, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ImportProblem {
	
	public static final String NO_DEVICE_NAME = "No device name";
	public static final String DEVICE_NAME_NOT_FOUND = "Device name not found in Yukon";
	
	private String problem = null;
	
	public void setProblem(String s) {
		problem = s;
	}
	
	public void appendProblem(String s) {
		if (problem == null)
			problem = s;
		else
			problem += ", " + s;
	}
	
	public String getProblem() {
		return problem;
	}
	
}
