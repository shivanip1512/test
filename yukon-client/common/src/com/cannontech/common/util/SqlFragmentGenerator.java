package com.cannontech.common.util;

import java.util.List;

public interface SqlFragmentGenerator<R> {

    public SqlFragmentSource generate(List<R> subList);
    
}
