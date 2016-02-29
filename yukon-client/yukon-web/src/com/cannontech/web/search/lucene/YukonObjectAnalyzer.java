package com.cannontech.web.search.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

public class YukonObjectAnalyzer extends Analyzer {

    public YukonObjectAnalyzer() {
        super();
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer stream = new PrefixTokenizer();
        TokenStreamComponents tokenStream = new TokenStreamComponents(stream);
        return tokenStream;
    }

}
