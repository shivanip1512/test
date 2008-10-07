package com.cannontech.database.data.multi;

import java.util.Vector;

import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;

public class MultiDBPersistent extends CommonMulti implements CTIDbChange {

    /**
     * DeviceBase constructor comment.
     */
    public MultiDBPersistent() {
        super();
    }
    
    // raise visibility to public
    public Vector<DBPersistent> getDBPersistentVector() {
        return super.getDBPersistentVector();
    }

    /**
     * Supposedly, MultiDBPersistents should not be used for this.
     */
    public void retrieve() throws java.sql.SQLException {
        throw new UnsupportedOperationException("Not implemented");
    }

}
