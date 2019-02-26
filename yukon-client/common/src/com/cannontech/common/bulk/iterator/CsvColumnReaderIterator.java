package com.cannontech.common.bulk.iterator;

import java.io.IOException;

import com.opencsv.CSVReader;

/**
 * Implementation of Iterator which iterates through an InputStream line by line.
 * 
 * Each iteration returns only the data at the specified column index. If a row does
 * not contain a column for the index, or if the line is blank, a null value is returned
 * for that iteration.
 * 
 * This class is not trivial to use. Care must be taken to close the InputStream
 * after the Iterator has been used. A close method on the CloseableIterator
 * has been provided for this purpose.
 */
public class CsvColumnReaderIterator extends ReadAheadIterator<String> {

    private final CSVReader csvReader;
    private final int columnIdx;

    public CsvColumnReaderIterator(CSVReader csvReader, int columnIdx) throws IOException {
        this.csvReader = csvReader;
        this.columnIdx = columnIdx;
    }

    protected String doNext() {
        try {
            String[] currentLine = csvReader.readNext();
            
            if (currentLine != null && columnIdx <= currentLine.length - 1) {
                return currentLine[columnIdx];
            } else {
                return null;
            }
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void close() throws IOException {
        this.csvReader.close();
    }
}