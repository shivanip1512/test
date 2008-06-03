package com.cannontech.common.bulk.iterator;

import java.io.Closeable;
import java.io.IOException;
import java.util.Iterator;

public class CloseableIteratorWrapper<T> implements CloseableIterator<T> {
    Iterator<T> delegate;

    public CloseableIteratorWrapper(Iterator<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public T next() {
        return delegate.next();
    }

    @Override
    public void remove() {
        delegate.remove();
    }

    @Override
    public void close() throws IOException {
        if (delegate instanceof Closeable) {
            Closeable closeable = (Closeable) delegate;
            closeable.close();
        }
    }
    
    public static <T> CloseableIterator<T> getCloseableIterator(Iterator<T> iterator) { 
        if (iterator instanceof CloseableIterator) {
            return (CloseableIterator<T>) iterator;
        } else {
            return new CloseableIteratorWrapper<T>(iterator);
        }
    }

}
