package com.cannontech.web.search.lucene;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class PrefixTokenizerTest extends TestCase {
    private YukonObjectAnalyzer analyzer;

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
        analyzer = new YukonObjectAnalyzer();
        TokenStream stream = analyzer.tokenStream(null, new StringReader(testInput));
        stream.reset();
        while (stream.incrementToken()) {
            CharTermAttribute termAttribute = stream.addAttribute(CharTermAttribute.class);
            tokeTextList.add(termAttribute.toString());
        }
        stream.end();
        stream.close();

        assertEquals("Lists don't match", expectedList, tokeTextList);
    }

    /**
     * Test to be sure that {@link PrefixTokenizer#TOKEN_DELIMITER_PATTERN} matches the same characters as
     * {@link PrefixTokenizer#isTokenChar(int)} since they have slightly different code for performance reasons.
     */
    public void testIsTokenChar() {
        Pattern tokenDelimiterPattern = Pattern.compile(PrefixTokenizer.TOKEN_DELIMITER_PATTERN);
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (tokenDelimiterPattern.matcher(String.valueOf(c)).matches()) {
                if (PrefixTokenizer.isTokenChar(c)) {
                    fail("character " + c + " (0x" + Integer.toString((c), 16)
                        + ") reported as a token character but the RE disagrees");
                }
            } else {
                if (!PrefixTokenizer.isTokenChar(c)) {
                    fail("character " + c + " (0x" + Integer.toString((c), 16)
                        + ") reported as a non-token character but the RE disagrees");
                }
            }
        }
    }
}
