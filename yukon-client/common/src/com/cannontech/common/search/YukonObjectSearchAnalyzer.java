package com.cannontech.common.search;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;

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
        
        TokenStream stream =
            new CharTokenizer(Version.LUCENE_34, reader) {
                @Override
                protected boolean isTokenChar(int c) {
                    return PrefixTokenizer.isTokenChar(c);
                }
            };
            
        stream = new LowerCaseFilter(Version.LUCENE_34, stream);
        return stream;
    }
}