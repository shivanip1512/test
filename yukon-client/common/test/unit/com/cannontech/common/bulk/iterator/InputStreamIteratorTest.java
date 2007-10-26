package com.cannontech.common.bulk.iterator;

import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;

import junit.framework.TestCase;

/**
 * Test class for InputStreamIterator
 */
public class InputStreamIteratorTest extends TestCase {

    private InputStreamIterator iterator = null;
    private String[] testStrings = new String[] { "123", "abc", "test" };

    @Override
    protected void setUp() throws Exception {

        // Create an input stream for the test
        InputStream inputStream = new InputStream() {

            private int readCount = 0;

            private char[] charStream = new char[] { '1', '2', '3', Character.LINE_SEPARATOR, 'a',
                    'b', 'c', Character.LINE_SEPARATOR, 't', 'e', 's', 't',
                    Character.LINE_SEPARATOR };

            @Override
            public int read() throws IOException {

                if (readCount < charStream.length) {
                    char c = charStream[readCount++];
                    return c;
                } else if (readCount == charStream.length) {
                    return -1;
                }
                throw new IOException("Read failed.");
            }
        };

        iterator = new InputStreamIterator(inputStream);

    }

    public void testHasNext() throws Exception {

        assertTrue("The iterator has items in it", iterator.hasNext());
        iterator.next();
        assertTrue("The iterator has more than one item in it", iterator.hasNext());
        iterator.next();
        assertTrue("The iterator has more than two items in it", iterator.hasNext());
        iterator.next();
        assertFalse("The iterator only has three items in it", iterator.hasNext());

    }

    public void testNext() throws Exception {

        // Test next method with valid input stream
        String actual = iterator.next();
        assertEquals("", testStrings[0], actual);

        actual = iterator.next();
        assertEquals("", testStrings[1], actual);

        actual = iterator.next();
        assertEquals("", testStrings[2], actual);

        try {
            iterator.next();
            fail("Iterated past the end of the iterator");
        } catch (NoSuchElementException e) {
            // do nothing - went over the end of the iterator
        }

        // Test next method with input stream that fails immediately
        InputStream iStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Read failed.");
            }
        };

        InputStreamIterator invalidIterator = null;
        try {
            invalidIterator = new InputStreamIterator(iStream);
        } catch (IOException e) {
            // do nothing - expected exception
        }

        // Test next method with input stream that fails part way through
        iStream = new InputStream() {

            private int count = 0;

            @Override
            public int read() throws IOException {
                if (count++ > 1) {
                    throw new IOException("Read failed.");
                }
                return Character.LINE_SEPARATOR;
            }
        };

        invalidIterator = new InputStreamIterator(iStream);

        // Valid read
        invalidIterator.next();
        try {
            // Invalid read
            invalidIterator.next();
        } catch (RuntimeException e) {
            // 2nd read should fail
        }

    }

    public void testRemove() throws Exception {
        try {
            iterator.remove();
            fail("Remove is not supported");
        } catch (UnsupportedOperationException e) {
            // do nothing - went over the end of the iterator
        }
    }
}
