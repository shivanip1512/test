package com.cannontech.database;

import java.util.List;

public interface SqlParameterChildSink extends SqlParameterSink {
    public <T> void addChildren(SimpleTableAccessTemplate<? super T> template,
                                List<? extends T> children);
}
