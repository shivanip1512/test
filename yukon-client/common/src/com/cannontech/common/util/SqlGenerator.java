package com.cannontech.common.util;

import java.util.List;

public interface SqlGenerator<R> {

    public String generate(List<R> subList);
    
}
