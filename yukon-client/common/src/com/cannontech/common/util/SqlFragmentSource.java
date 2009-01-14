package com.cannontech.common.util;

import java.util.List;

public interface SqlFragmentSource {
    public String getSql();
    public List<Object> getArgumentList();
    public Object[] getArguments();
}
