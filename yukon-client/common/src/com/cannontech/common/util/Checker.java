package com.cannontech.common.util;

/**
 * Interface used to check an object to see if it matches certain critieria
 */
public interface Checker<T> {

    public boolean check(T object);

}