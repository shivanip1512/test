package com.cannontech.common.util;

public interface ObjectMapper<J,K> {
    public K map(J from);
}
