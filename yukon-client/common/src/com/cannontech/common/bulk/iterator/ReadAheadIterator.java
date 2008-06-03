package com.cannontech.common.bulk.iterator;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Implementation of Iterator which iterates through an InputStream line by line
 */
public abstract class ReadAheadIterator<T> implements CloseableIterator<T> {
    private boolean gotFirst = false;
    private T currentLine = null;

    public boolean hasNext() {
        if (!gotFirst) {
            currentLine = doNext();
            gotFirst = true;
        }
        return currentLine != null;
    }

    public T next() {
        if (!gotFirst) {
            currentLine = doNext();
            gotFirst = true;
        }

        // We've run of the end of the input stream
        if (currentLine == null) {
            throw new NoSuchElementException();
        }

        T returnString = currentLine;
        currentLine = doNext();

        return returnString;
    }

    protected abstract T doNext();

    @Override
    public void close() throws IOException {
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
