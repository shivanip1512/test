package com.cannontech.esub.editor;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author alauinger
 *
 * Stores various preferences for the esubstation editor application.
 * i.e. remember the current working directory between invocations
 */
public class EditorPrefs {

	// Keys for settigs/getting preferences
	public static final String DEFAULT_WORKING_DIR = "Default Working";
	public static final String DEFAULT_DRAWING_WIDTH = "Default Drawing Width";
	public static final String DEFAULT_DRAWING_HEIGHT = "Default Drawing Height";
	
	// Singleton instance
	private static EditorPrefs instance;
	
	// instance variables
	private Preferences prefs;
		
	public static synchronized EditorPrefs getPreferences() {
		if( instance == null ) {
			instance = new EditorPrefs();
		}
		
		return instance;
	}
	
	private EditorPrefs() {
 		prefs = Preferences.userNodeForPackage(EditorPrefs.class);
	}
	
	private String get(String key, String def) {
		return prefs.get(key, def);
	}
	
	private void put(String key, String value) {
		try {
			prefs.put(key, value);
			prefs.flush();				
		}
		catch(BackingStoreException e) {
			e.printStackTrace();
		}
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
	
