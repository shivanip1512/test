package com.cannontech.web.search.lucene.index;

import java.io.IOException;

import org.apache.lucene.search.Query;

import com.cannontech.web.search.lucene.TopDocsCallbackHandler;

public interface SearchTemplate {

    public <R> R doCallBackSearch(final Query query,
            final TopDocsCallbackHandler<R> handler) throws IOException;

}
