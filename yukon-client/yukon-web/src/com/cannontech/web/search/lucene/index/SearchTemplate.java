package com.cannontech.web.search.lucene.index;

import java.io.IOException;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

import com.cannontech.web.search.lucene.TopDocsCallbackHandler;

public interface SearchTemplate {
    /**
     * Execute the specified Lucene query against the index, returning up to maxResults documents.
     */
    <R> R doCallBackSearch(Query query, TopDocsCallbackHandler<R> handler, int maxResults) throws IOException;

    /**
     * Execute the specified Lucene query against the index with no limit on the number of documents returned.
     */
    <R> R doCallBackSearch(Query query, TopDocsCallbackHandler<R> handler) throws IOException;
    
    <R> R doCallBackSearch(Query query, TopDocsCallbackHandler<R> handler, int maxResults, Sort sort) throws IOException;
}
