package com.cannontech.database;

import java.util.List;

public interface SqlParameterSink {
    public <T> void addChildren(SimpleTableAccessTemplate<? super T> template,
                                List<? extends T> children);

    public void addValue(String paramName, Object value);

}
