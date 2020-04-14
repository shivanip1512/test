package com.cannontech.common.device.port;

import com.cannontech.database.db.DBPersistent;

public interface DBPersistentConverter<T extends DBPersistent> {
    /**
     * Build model object from the passed db persistent port.
     */
    public void buildModel(T port);

    /**
     * Builds db persistent object for a port.
     */
    public void buildDBPersistent(T port);
}


