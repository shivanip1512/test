package com.cannontech.web.tools.mapping.model;

import com.google.common.base.Function;

public class Group {

    private int id; // state group id
    private int state; // raw state
    
    public final static Function<Group, Integer> ID_FUNCTION = new Function<Group, Integer>() {
        @Override
        public Integer apply(Group group) {
            return group.getId();
        }
    };
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
    }
    
}