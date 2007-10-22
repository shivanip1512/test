package com.cannontech.common.bulk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonPAObject;

public class BulkDataContainer{
    List<LiteYukonPAObject> yukonPAObjects = new ArrayList<LiteYukonPAObject>();
    Map<String, List<String>> fails = new HashMap<String, List<String>>();

    public Map<String, List<String>> getFails() {
        return this.fails;
    }
    public void setFails(Map<String, List<String>> fails) {
        this.fails = fails;
    }

    public List<LiteYukonPAObject> getYukonPAObjects() {
        return yukonPAObjects;
    }
    public void setYukonPAObjects(List<LiteYukonPAObject> yukonPAObjects) {
        this.yukonPAObjects = yukonPAObjects;
    }
}