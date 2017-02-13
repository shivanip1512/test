package com.cannontech.core.users.model;

public enum PreferenceType {

    EDITABLE, // Preference displayed on profile page,
              // editable and persisted in DB

    NONEDITABLE, // Preference neither displayed on UI nor editable,
                 // persisted in DB periodically/on server shutdown

    TEMPORARY // Preference is short-lived in cache, until server is up.
}