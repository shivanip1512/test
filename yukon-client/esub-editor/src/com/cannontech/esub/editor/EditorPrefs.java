package com.cannontech.esub.editor;

import com.cannontech.common.util.CtiPreferences;

/**
 * @author alauinger
 *
 * Stores various preferences for the esubstation editor application.
 * i.e. remember the current working directory between invocations
 */
public class EditorPrefs extends CtiPreferences {

	// Keys for settigs/getting preferences
	public static final String DEFAULT_WORKING_DIR = "Default Working";
	public static final String DEFAULT_DRAWING_WIDTH = "Default Drawing Width";
	public static final String DEFAULT_DRAWING_HEIGHT = "Default Drawing Height";
	
	public static final String DEFAULT_WINDOW_WIDTH = "Default Window Width";
	public static final String DEFAULT_WINDOW_HEIGHT = "Default Window Height";
	 
	// Singleton instance
	private static EditorPrefs instance;
	
	public static synchronized EditorPrefs getPreferences() {
		if( instance == null ) {
			instance = new EditorPrefs();
		}
		
		return instance;
	}
	
	private EditorPrefs() {
 		super();
	}
		
	public int getDefaultDrawingWidth() {
		String widthStr = get(DEFAULT_DRAWING_WIDTH, "1024");
		return Integer.parseInt(widthStr);
	}
	
	public void setDefaultDrawingWidth(int width) {
		put(DEFAULT_DRAWING_WIDTH, Integer.toString(width));
	}
	
	public int getDefaultDrawingHeight() {
		String heightStr = get(DEFAULT_DRAWING_HEIGHT, "768");
		return Integer.parseInt(heightStr);
	}
	
	public void setDefaultDrawingHeight(int height) {
		put(DEFAULT_DRAWING_HEIGHT, Integer.toString(height));
	}
			
	/**
	 * Returns the workingDir.
	 * @return String
	 */
	public String getWorkingDir() {
		return get(DEFAULT_WORKING_DIR, System.getProperty("user.home"));	
	}

	/**
	 * Sets the workingDir.
	 * @param workingDir The workingDir to set
	 */
	public void setWorkingDir(String workingDir) {
		put(DEFAULT_WORKING_DIR, workingDir);
	}

}
	
