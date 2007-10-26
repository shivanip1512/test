package com.cannontech.common.bulk.iterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Implementation of Iterator which iterates through an InputStream line by line
 */
public class InputStreamIterator implements Iterator<String> {

    private BufferedReader reader = null;
    private String currentLine = null;

    public InputStreamIterator(InputStream inputStream) throws IOException {

        reader = new BufferedReader(new InputStreamReader(inputStream));

        // currentLine is always one step ahead of the iterator
        currentLine = reader.readLine();
    }

    public boolean hasNext() {
        // null means we're at the end of the input stream
        return currentLine != null;
    }

    public String next() {

        // We've run of the end of the input stream
        if (currentLine == null) {
            throw new NoSuchElementException();
        }

        String returnString = currentLine;
        try {
            // keep currentLine ahead of iterator
            currentLine = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return returnString;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
