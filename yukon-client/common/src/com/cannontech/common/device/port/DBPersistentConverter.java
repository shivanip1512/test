package com.cannontech.common.device.port;

import com.cannontech.database.data.lite.LiteYukonPAObject;
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

    /**
     * Build model from liteYukonPAObject.
     * Model object populated by this method is partially populated model object
     * it contains id,Name,Type and Enable i.e Status.
     * Populated model object is not fully populated object
     */
    public default void buildModel(LiteYukonPAObject liteYukonPAObject) {
    }
}
