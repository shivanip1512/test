/*
 * Created on Jun 24, 2003
 */
package com.cannontech.common.util;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

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

    protected void put(String key, String value) {
        try {
            prefs.put(key, value);
            prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    protected void put(String key, boolean value) {
        try {
            prefs.putBoolean(key, value);
            prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }
}
