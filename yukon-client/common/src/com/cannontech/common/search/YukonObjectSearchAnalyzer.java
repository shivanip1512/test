package com.cannontech.common.search;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceTokenizer;

/**
 * Compatible with PointDeviceAnalyzer, but doesn't emit prefix tokens for search
 * terms which would be just crazy.
 */
public class YukonObjectSearchAnalyzer extends Analyzer {

    public YukonObjectSearchAnalyzer() {
        super();
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream stream = new WhitespaceTokenizer(reader){
            @Override
            protected boolean isTokenChar(char c) {
                return PrefixTokenizer.isTokenChar(c);
            }
        };
        stream = new LowerCaseFilter(stream);
        return stream;
    }

}
