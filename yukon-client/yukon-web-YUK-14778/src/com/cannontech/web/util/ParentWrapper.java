package com.cannontech.web.util;

import java.util.Collection;

public class ParentWrapper<S,C> {
    Collection<C> children;
    S self;

    public ParentWrapper(S self, Collection<C> children) {
        this.children = children;
        this.self = self;
    }
    
    public S getSelf() {
        return self;
    }
    
    public Collection<C> getChildren() {
        return children;
    }

}
