package com.cannontech.common.device.streaming.model;

import java.util.ArrayList;
import java.util.List;

public class Behavior {
    private int id;
    private BehaviorType type;
    private List<BehaviorValue> values = new ArrayList<>();

    public BehaviorType getType() {
        return type;
    }

    public void setType(BehaviorType type) {
        this.type = type;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<BehaviorValue> getValues() {
        return values;
    }

    public void setValues(List<BehaviorValue> values) {
        this.values = values;
    }
}
