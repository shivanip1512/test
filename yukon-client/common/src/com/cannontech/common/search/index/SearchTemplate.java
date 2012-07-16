package com.cannontech.common.search.index;

import java.io.IOException;

import org.apache.lucene.search.Query;

import com.cannontech.common.search.TopDocsCallbackHandler;

public interface SearchTemplate {

    public <R> R doCallBackSearch(final Query query,
            final TopDocsCallbackHandler<R> handler) throws IOException;

}
