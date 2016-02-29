package com.cannontech.web.search.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.util.CharTokenizer;

/**
 * Compatible with PointDeviceAnalyzer, but doesn't emit prefix tokens for search
 * terms which would be just crazy.
 */
public class YukonObjectSearchAnalyzer extends Analyzer {

    public YukonObjectSearchAnalyzer() {
        super();
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {

        Tokenizer tokenizer = new CharTokenizer() {
            @Override
            protected boolean isTokenChar(int c) {
                return PrefixTokenizer.isTokenChar(c);
            }
        };

        TokenStream stream = new LowerCaseFilter(tokenizer);
        TokenStreamComponents tokenStream = new TokenStreamComponents(tokenizer, stream);
        return tokenStream;
    }

}