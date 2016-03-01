package com.cannontech.web.search.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

public class YukonObjectAnalyzer extends Analyzer {

    public YukonObjectAnalyzer() {
        super();
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new PrefixTokenizer();
        TokenStreamComponents tokenStream = new TokenStreamComponents(tokenizer);
        return tokenStream;
    }

}
