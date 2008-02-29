package com.cannontech.common.util;

import java.util.AbstractList;
import java.util.List;

public class ReverseList<E> extends AbstractList<E> {
    List<? extends E> delegate;

    public ReverseList(List<? extends E> delegate) {
        super();
        this.delegate = delegate;
    }

    @Override
    public E get(int index) {
        return delegate.get(delegate.size() - index - 1);
    }

    @Override
    public int size() {
        return delegate.size();
    }



}
