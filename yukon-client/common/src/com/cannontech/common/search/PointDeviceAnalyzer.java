package com.cannontech.common.search;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

public class PointDeviceAnalyzer extends Analyzer {

    public PointDeviceAnalyzer() {
        super();
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        TokenStream stream = new PrefixTokenizer(reader);
        return stream;
    }


}
