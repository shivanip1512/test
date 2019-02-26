package com.cannontech.common.bulk.iterator;

import java.io.IOException;

import com.opencsv.CSVReader;

/**
 * Implementation of Iterator which iterates through an InputStream line by line.
 * 
 * This class is not trivial to use. Care must be taken to close the InputStream
 * after the Iterator has been used. A close method on the CloseableIterator
 * has been provided for this purpose.
 */
public class CsvReaderIterator extends ReadAheadIterator<String[]> {

    private final CSVReader csvReader;

    public CsvReaderIterator(CSVReader csvReader) throws IOException {
        this.csvReader = csvReader;
    }

    protected String[] doNext() {
        try {
            String[] currentLine = csvReader.readNext();
            return currentLine;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void close() throws IOException {
        this.csvReader.close();
    }
}
