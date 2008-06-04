package com.cannontech.common.util.predicate;

public class NullPredicate<T> implements Predicate<T> {

    @Override
    public boolean evaluate(T object) {
        return true;
    }

}
