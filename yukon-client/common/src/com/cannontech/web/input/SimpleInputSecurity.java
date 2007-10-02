package com.cannontech.web.input;

import com.cannontech.database.data.lite.LiteYukonUser;

/**
 * InputSecutrity implementation that determines security settings based on a
 * settable boolean
 */
public class SimpleInputSecurity implements InputSecurity {

    private boolean editable = true;

    public boolean isEditable(LiteYukonUser user) {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

}
