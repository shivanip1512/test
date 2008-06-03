package com.cannontech.common.bulk.iterator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Implementation of Iterator which iterates through an InputStream line by line.
 * 
 * This class is not trivial to use. Care must be taken to close the InputStream
 * after the Iterator has been used. A close method on the CloseableIterator
 * has been provided for this purpose.
 */
public class InputStreamIterator extends ReadAheadIterator<String> {

    private BufferedReader reader = null;

    public InputStreamIterator(InputStream inputStream) throws IOException {
        reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    protected String doNext() {
        try {
            String currentLine = reader.readLine();
            if (currentLine == null) {
                // we must be done reading
                reader.close();
                reader = null;
            }
            return currentLine;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void close() throws IOException {
        reader.close();
    }
}
