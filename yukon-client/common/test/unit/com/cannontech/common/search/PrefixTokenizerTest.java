package com.cannontech.common.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Token;

import junit.framework.TestCase;

public class PrefixTokenizerTest extends TestCase {

    /*
     * Test method for 'com.cannontech.common.search.PrefixTokenizer.next()'
     */
    
    public void testNext() throws Exception {
        String testInput = "Hello_my -name (is) /65412";
        List<String> expectedList = Arrays.asList(new String[] {
            "h",
            "he",
            "hel",
            "hell",
            "hello",
            "m",
            "my",
            "n",
            "na",
            "nam",
            "name",
            "i",
            "is",
            "6",
            "65",
            "654",
            "6541",
            "65412",
        });
        testNextHelper(testInput, expectedList);
        String testInput2 = " 65412 my    name is . ";
        List<String> expectedList2 = Arrays.asList(new String[] {
            "6",
            "65",
            "654",
            "6541",
            "65412",
            "m",
            "my",
            "n",
            "na",
            "nam",
            "name",
            "i",
            "is",
            ".",
        });
        testNextHelper(testInput2, expectedList2);
    }

    private void testNextHelper(String testInput, List<String> expectedList) throws IOException {
        List<String> tokeTextList = new ArrayList<String>();
        PrefixTokenizer tokenizer = new PrefixTokenizer(new StringReader(testInput));
        Token tok;
        while ((tok = tokenizer.next()) != null) {
            String tokeText = tok.termText();
            tokeTextList.add(tokeText);
        }
        
        
        assertEquals("Lists don't match", expectedList, tokeTextList);
    }

}
