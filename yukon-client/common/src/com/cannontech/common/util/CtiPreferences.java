/*
 * Created on Jun 24, 2003
 */
package com.cannontech.common.util;

import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * @author aaron
 */
public abstract class CtiPreferences {
	private Preferences prefs;
	
	protected CtiPreferences() {
		prefs = Preferences.userNodeForPackage(getClass());
	}
	
	protected String get(String key, String def) {
		return prefs.get(key, def);
	}
	
	protected int getInt(String key, int def) {
		return prefs.getInt(key, def);
	}
	
	protected boolean getBoolean(String key, boolean def) {
		return prefs.getBoolean(key, def);
	}
	
	protected String[] getStringArr(String key, String[] def) {
		String s = prefs.get(key, null);
		if(s == null) {
			return new String[] { };	
		}
		
		StringTokenizer tok = new StringTokenizer(s, ",");
		String[] retVal = new String[tok.countTokens()];
		int i = 0;
		while(tok.hasMoreTokens()) {
			retVal[i++] = tok.nextToken();
		}
		return retVal;
	}
	
	protected void put(String key, String value) {
		try {
			prefs.put(key, value);
			prefs.flush();				
		}
		catch(BackingStoreException e) {
			e.printStackTrace();
		}
	}

	protected void put(String key, int value) {
		try {
			prefs.putInt(key, value);
			prefs.flush();				
		}
		catch(BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	protected void put(String key, boolean value) {
		try {
			prefs.putBoolean(key,value);
			prefs.flush();
		}
		catch(BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	protected void put(String key, String[] value) {
		try {
			StringBuffer sbuf = new StringBuffer();
			for(int i = 0; i < value.length; i++) {
				sbuf.append(value[i]);
				sbuf.append(",");	
			}
			prefs.put(key, sbuf.toString());
			prefs.flush();
		}
		catch(BackingStoreException e) {
			e.printStackTrace();
		}
	}
}
