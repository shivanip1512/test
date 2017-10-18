package com.cannontech.core.users.model;

public enum PreferenceType {

    EDITABLE, // Preference displayed on profile page,
              // editable and persisted in DB

    EDITABLE_SPECIAL, // Editable and persisted in the DB, but uses a special UI
    
    NONEDITABLE, // Preference neither displayed on UI nor editable,
                 // persisted in DB periodically/on server shutdown

    TEMPORARY // Preference is short-lived in cache, until server is up.
    ;
    
    public boolean isEditable() {
        if(this == EDITABLE || this == EDITABLE_SPECIAL) {
            return true;
        }
        return false;
    }
}