package com.cannontech.common.util;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class OneLessSet<E> extends AbstractSet<E> {
    
    private final Set<E> setToDecorate;
    private final E elementToExclude;

    public OneLessSet(Set<E> setToDecorate, E elementToExclude) {
        if (setToDecorate.isEmpty()) {
            throw new IllegalArgumentException("Can't decorate empty set");
        }
        if (!setToDecorate.contains(elementToExclude)) {
            throw new IllegalArgumentException("Set to decorate must include element to exclude");
        }
        this.setToDecorate = setToDecorate;
        this.elementToExclude = elementToExclude;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            Iterator<E> delegate;
            E nextElement;
            {
                delegate = setToDecorate.iterator();
                getNext();
            }
            public boolean hasNext() {
                return nextElement != null;
            }

            public E next() {
                E result = nextElement;
                if (result == null) {
                    throw new NoSuchElementException();
                }
                getNext();
                return result;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
            
            private void getNext() {
                if (!delegate.hasNext()) {
                    nextElement = null;
                } else {
                    nextElement = delegate.next();
                    if (nextElement.equals(elementToExclude)) {
                        getNext();
                    }
                }
            }
            
        };
    }
    
    @Override
    public boolean contains(Object o) {
        // this isn't required, but is easy to provid
        if (elementToExclude.equals(o)) {
            return false;
        } else {
            return setToDecorate.contains(o);
        }
    }

    @Override
    public int size() {
        return setToDecorate.size() - 1;
    }

}
