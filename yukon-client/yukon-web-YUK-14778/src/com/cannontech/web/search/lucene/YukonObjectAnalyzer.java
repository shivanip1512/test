package com.cannontech.web.search.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

public class YukonObjectAnalyzer extends Analyzer {

    public YukonObjectAnalyzer() {
        super();
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream stream = new PrefixTokenizer(reader);
        return stream;
    }


}
