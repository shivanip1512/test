package com.cannontech.common.search.index;

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import com.cannontech.common.search.HitsCallbackHandler;

public interface SearchTemplate {

    public <R> R doCallBackSearch(final Query query, final Sort sort,
            final HitsCallbackHandler<R> handler) throws IOException;

}
