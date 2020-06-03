package com.cannontech.common.device.port;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;

public interface DBPersistentConverter<T extends DBPersistent> {
    /**
     * Build model object from the passed db persistent .
     */
    public void buildModel(T object);

    /**
     * Builds db persistent object for a object.
     */
    public void buildDBPersistent(T object);

    /**
     * Build model from liteYukonPAObject.
     * Model object populated by this method is partially populated model object
     * it contains id,Name,Type and Enable i.e Status.
     * Populated model object is not fully populated object
     */
    public default void buildModel(LiteYukonPAObject liteYukonPAObject) {
    }
}
