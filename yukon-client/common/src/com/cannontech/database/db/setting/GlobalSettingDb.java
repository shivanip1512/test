package com.cannontech.database.db.setting;

import org.joda.time.Instant;

public class GlobalSettingDb {
    private Integer globalSettingId;
    private String name;
    private Object value;
    private String comment;
    private Instant lastChangedDate;

    public GlobalSettingDb(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    /**
     * A null value in this case does not indicate a null value in the database.
     * Here it indicates it is not in the database, but stored in the cache with null
     * 
     * @return globalSettingId if found in database, or null if not
     */
    public Integer getGlobalSettingId() { 
        return globalSettingId; 
    }

    public void setGlobalSettingId(int globalSettingId) {
        this.globalSettingId = globalSettingId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }
    
    public void setValue(Object value) {
        this.value = value;
    }
    
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public Instant getLastChangedDate() {
        return lastChangedDate;
    }
    
    public void setLastChangedDate(Instant lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }
}
