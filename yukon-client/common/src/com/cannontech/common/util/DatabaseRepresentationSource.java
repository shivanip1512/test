package com.cannontech.common.util;

public interface DatabaseRepresentationSource {
    /**
     * Should return an object that can be used as an argument to a JdbcTemplate
     * style call as a replacement for a '?' value.
     * 
     * For example, many of our Enums use name() as their representation, but
     * others, for historical reason, use something else. Those enums should
     * implement this interface to declare what String or Integer is used
     * for their DB representation.
     * @return
     */
    public Object getDatabaseRepresentation();
}
