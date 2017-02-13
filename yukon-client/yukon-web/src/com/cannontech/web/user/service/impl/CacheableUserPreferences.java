package com.cannontech.web.user.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.core.users.model.UserPreference;

/**
 * CacheableUserPreferences maintains cache of User preferences, mainly of 2 types -
 * Temporary cache which will not be persisted in DB and will exist until server is up
 * Persisted cache will be persisted in DB periodically/at shutdown
 */
public class CacheableUserPreferences {

    private Map<String, UserPreference> temporaryPreferences;
    private Map<String, UserPreference> persistedPreferences;
    
    public CacheableUserPreferences(Map<String, UserPreference> temporaryPreferences,
            Map<String, UserPreference> persistedPreferences) {
        super();
        this.temporaryPreferences = temporaryPreferences;
        this.persistedPreferences = persistedPreferences;
    }

    public CacheableUserPreferences() {
    }

    public Map<String, UserPreference> getTemporaryPreferences() {
        return temporaryPreferences;
    }

    public void setTemporaryPreferences(Map<String, UserPreference> temporaryPreferences) {
        this.temporaryPreferences = temporaryPreferences;
    }

    public Map<String, UserPreference> getPersistedPreferences() {
        return persistedPreferences;
    }

    public void setPersistedPreferences(Map<String, UserPreference> persistedPreferences) {
        this.persistedPreferences = persistedPreferences;
    }
    
	public Map<String, UserPreference> getMergedPreferences() {
		Map<String, UserPreference> mergedPreferences = new HashMap<>();
		if (temporaryPreferences != null) {
			mergedPreferences.putAll(temporaryPreferences);
		}
		if (persistedPreferences != null) {
			mergedPreferences.putAll(persistedPreferences);
		}
		return mergedPreferences;
	}
}
