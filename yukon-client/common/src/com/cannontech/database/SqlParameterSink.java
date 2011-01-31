package com.cannontech.database;

import java.util.List;

public interface SqlParameterSink {
    public static class ChildPair<T> {
        SimpleTableAccessTemplate<? super T> template;
        List<? extends T> children;
    }
    
    public <T> void addChildren(SimpleTableAccessTemplate<? super T> template,
                                List<? extends T> children);

    public void addValue(String paramName, Object value);

}
